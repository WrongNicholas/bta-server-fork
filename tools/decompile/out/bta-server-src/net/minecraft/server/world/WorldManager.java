package net.minecraft.server.world;

import com.mojang.logging.LogUtils;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.PacketPlaySoundEffect;
import net.minecraft.core.net.packet.PacketPlaySoundEffectDirect;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.sound.SoundTypes;
import net.minecraft.core.world.LevelListener;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class WorldManager implements LevelListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Set<String> unknownSoundTypes = new HashSet<>();
   private final MinecraftServer mcServer;
   private final WorldServer worldServer;

   public WorldManager(MinecraftServer minecraftserver, WorldServer worldserver) {
      this.mcServer = minecraftserver;
      this.worldServer = worldserver;
   }

   @Override
   public void addParticle(String particleKey, double x, double y, double z, double motionX, double motionY, double motionZ, int data, double maxDistance) {
   }

   @Override
   public void addParticle(String particleKey, double x, double y, double z, double motionX, double motionY, double motionZ, int data) {
   }

   @Override
   public void entityAdded(Entity entity) {
      if (entity instanceof EntityItem) {
         EntityItem entityItem = (EntityItem)entity;
         if (entityItem.item == null || entityItem.item.getItem() == null) {
            return;
         }
      }

      this.mcServer.getEntityTracker(this.worldServer.dimension.id).trackEntity(entity);
   }

   @Override
   public void entityRemoved(Entity entity) {
      this.mcServer.getEntityTracker(this.worldServer.dimension.id).untrackEntity(entity);
   }

   @Override
   public void playSound(Entity player, String soundPath, SoundCategory soundType, double x, double y, double z, float volume, float pitch) {
      if (soundPath != null) {
         int soundId = SoundTypes.getSoundId(soundPath);
         if (soundId == -1) {
            if (!unknownSoundTypes.contains(soundPath)) {
               LOGGER.warn("Unable to play unknown sound '{}'!", soundPath);
               unknownSoundTypes.add(soundPath);
            }
         } else {
            PacketPlaySoundEffectDirect packet = new PacketPlaySoundEffectDirect(soundId, soundType, x, y, z, volume, pitch);
            float range = 16.0F;
            if (volume > 1.0F) {
               range *= volume;
            }

            Player entityPlayer = null;
            if (player instanceof Player) {
               entityPlayer = (Player)player;
            }

            this.mcServer.playerList.sendPacketToOtherPlayersAroundPoint(entityPlayer, x, y, z, range, this.worldServer.dimension.id, packet);
         }
      }
   }

   @Override
   public void setBlocksDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
   }

   @Override
   public void allChanged(boolean lightChanged, boolean seasonChanged) {
   }

   @Override
   public void blockChanged(int x, int y, int z) {
      this.mcServer.playerList.markBlockNeedsUpdate(x, y, z, this.worldServer.dimension.id);
   }

   @Override
   public void playStreamingMusic(String soundPath, String Author, int x, int y, int z) {
   }

   @Override
   public void tileEntityChanged(int x, int y, int z, TileEntity tileEntity) {
      this.mcServer.playerList.sendTileEntityToPlayer(x, y, z, this.worldServer.dimension.id, tileEntity);
   }

   @Override
   public void levelEvent(@Nullable Player player, int id, int x, int y, int z, int data) {
      this.mcServer
         .playerList
         .sendPacketToOtherPlayersAroundPoint(player, x, y, z, 64.0, this.worldServer.dimension.id, new PacketPlaySoundEffect(id, x, y, z, data));
   }
}
