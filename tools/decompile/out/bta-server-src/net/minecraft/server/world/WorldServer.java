package net.minecraft.server.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Global;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.packet.PacketBlockEvent;
import net.minecraft.core.net.packet.PacketChat;
import net.minecraft.core.net.packet.PacketCustomPayload;
import net.minecraft.core.net.packet.PacketEntityEvent;
import net.minecraft.core.net.packet.PacketEntityTagData;
import net.minecraft.core.net.packet.PacketExplosion;
import net.minecraft.core.net.packet.PacketWeatherEffect;
import net.minecraft.core.net.packet.PacketWeatherStatus;
import net.minecraft.core.util.collection.IntHashMap;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.ExplosionCannonball;
import net.minecraft.core.world.SpawnerMobs;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.IChunkLoader;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import net.minecraft.core.world.save.LevelStorage;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;

public class WorldServer extends World {
   public ChunkProviderServer chunkProviderServer;
   public boolean dontSave;
   public MinecraftServer mcServer;
   private final IntHashMap<Entity> entityIntHashMap;
   private long lastWeatherSend = System.currentTimeMillis();

   public WorldServer(MinecraftServer minecraftserver, LevelStorage isavehandler, String name, int dimensionId, WorldType worldType, long seed) {
      super(isavehandler, name, seed, Dimension.getDimensionList().get(dimensionId), worldType);
      this.entityIntHashMap = new IntHashMap<>();
      this.mcServer = minecraftserver;
   }

   @Override
   protected IChunkProvider createChunkProvider() {
      IChunkLoader ichunkloader = this.saveHandler.getChunkLoader(this.dimension);
      this.chunkProviderServer = (ChunkProviderServer)Global.accessor.createChunkProvider(this, ichunkloader);
      return this.chunkProviderServer;
   }

   public List<TileEntity> getBlockEntitiesWithinBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      ArrayList<TileEntity> arraylist = new ArrayList<>();

      for (int i = 0; i < this.loadedTileEntityList.size(); i++) {
         TileEntity tileEntity = this.loadedTileEntityList.get(i);
         if (tileEntity.x >= minX && tileEntity.y >= minY && tileEntity.z >= minZ && tileEntity.x < maxX && tileEntity.y < maxY && tileEntity.z < maxZ) {
            arraylist.add(tileEntity);
         }
      }

      return arraylist;
   }

   @Override
   public boolean canMineBlock(Player player, int x, int y, int z) {
      if (this.mcServer.spawnProtectionRange > 0 && !this.mcServer.playerList.isOp(player.uuid)) {
         int dx = (int)MathHelper.abs(x - this.levelData.getSpawnX());
         int dz = (int)MathHelper.abs(z - this.levelData.getSpawnZ());
         return Math.max(dx, dz) > this.mcServer.spawnProtectionRange;
      } else {
         return super.canMineBlock(player, x, y, z);
      }
   }

   @Override
   protected void obtainEntitySkin(Entity entity) {
      super.obtainEntitySkin(entity);
      if (entity != null) {
         this.entityIntHashMap.put(entity.id, entity);
      }
   }

   @Override
   protected void releaseEntitySkin(Entity entity) {
      super.releaseEntitySkin(entity);
      if (entity != null) {
         this.entityIntHashMap.remove(entity.id);
      }
   }

   @Override
   protected void updateSleepingPlayers() {
      if (this.areEnoughPlayersFullyAsleep()) {
         boolean wasInterrupted = false;
         if (this.getSpawnerConfig().canHostileSpawn(this) && this.getPlayersRequiredToSkipNight() <= 1) {
            wasInterrupted = SpawnerMobs.performSleepSpawning(this, this.players);
         }

         if (!wasInterrupted) {
            if (MinecraftServer.getInstance() != null && this.players.size() > 1 && this.getPlayersRequiredToSkipNight() <= 1) {
               Player player = null;

               for (Player player2 : this.players) {
                  if (player2.isPlayerFullyAsleep()) {
                     player = player2;
                  }
               }

               if (player != null) {
                  MinecraftServer.getInstance()
                     .playerList
                     .sendPacketToAllPlayersInDimension(
                        new PacketChat(TextFormatting.YELLOW + player.getDisplayName() + TextFormatting.ORANGE + " went to sleep. Sweet dreams!"),
                        this.dimension.id
                     );
               }
            }

            long timePlusOneDay = this.levelData.getWorldTime() + 24000L;
            this.levelData.setWorldTime(timePlusOneDay - timePlusOneDay % 24000L + this.worldType.getSunriseTick(this) + 1000L);
            this.wakeUpAllPlayers();
         }
      }
   }

   @Override
   public void updateEnoughPlayersSleepingFlag(Player player) {
      this.enoughPlayersSleeping = false;
      if (this.players.size() > 0) {
         int playersSleeping = 0;
         int req = this.getPlayersRequiredToSkipNight();

         for (Player p : this.players) {
            if (p.isPlayerSleeping()) {
               playersSleeping++;
            }
         }

         if (playersSleeping >= req) {
            this.enoughPlayersSleeping = true;
         }

         if (MinecraftServer.getInstance() != null && player != null && this.players.size() > 1 && req > 1) {
            int moreReq = req - playersSleeping;
            if (player.isPlayerSleeping()) {
               MinecraftServer.getInstance()
                  .playerList
                  .sendPacketToAllPlayersInDimension(
                     new PacketChat(TextFormatting.YELLOW + player.username + TextFormatting.ORANGE + " went to bed."), this.dimension.id
                  );
            } else {
               MinecraftServer.getInstance()
                  .playerList
                  .sendPacketToAllPlayersInDimension(
                     new PacketChat(TextFormatting.YELLOW + player.username + TextFormatting.ORANGE + " has left their bed."), this.dimension.id
                  );
            }

            if (moreReq > 0) {
               if (moreReq > 1) {
                  MinecraftServer.getInstance()
                     .playerList
                     .sendPacketToAllPlayersInDimension(
                        new PacketChat(TextFormatting.LIGHT_GRAY + "" + moreReq + " more players are required to sleep to skip to daytime"), this.dimension.id
                     );
               } else {
                  MinecraftServer.getInstance()
                     .playerList
                     .sendPacketToAllPlayersInDimension(
                        new PacketChat(TextFormatting.LIGHT_GRAY + "1 more player is required to sleep to skip to daytime"), this.dimension.id
                     );
               }
            }
         }
      }
   }

   public Entity getEntityFromId(int i) {
      return this.entityIntHashMap.get(i);
   }

   @Override
   public boolean addWeatherEffect(Entity entity) {
      if (super.addWeatherEffect(entity)) {
         this.mcServer.playerList.sendPacketToPlayersAroundPoint(entity.x, entity.y, entity.z, 512.0, this.dimension.id, new PacketWeatherEffect(entity));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void sendTrackedEntityStatusUpdatePacket(Entity entityId, byte entityStatus) {
      PacketEntityEvent enitityEventPacket38Entitystatus = new PacketEntityEvent(entityId.id, entityStatus);
      this.mcServer.getEntityTracker(this.dimension.id).sendPacketToTrackedPlayersAndTrackedEntity(entityId, enitityEventPacket38Entitystatus);
   }

   @Override
   public void sendTrackedEntityStatusUpdatePacket(Entity entityId, byte entityStatus, float attackedAtYaw) {
      PacketEntityEvent enitityEventPacket38Entitystatus = new PacketEntityEvent(entityId.id, entityStatus, attackedAtYaw);
      this.mcServer.getEntityTracker(this.dimension.id).sendPacketToTrackedPlayersAndTrackedEntity(entityId, enitityEventPacket38Entitystatus);
   }

   @Override
   public void sendTrackedEntityDataPacket(Entity entity) {
      PacketEntityTagData entitytagdataNBT = new PacketEntityTagData(entity);
      this.mcServer.getEntityTracker(this.dimension.id).sendPacketToTrackedPlayersAndTrackedEntity(entity, entitytagdataNBT);
   }

   @Override
   public Explosion createExplosion(Entity entity, double x, double y, double z, float explosionSize) {
      return this.createExplosion(entity, x, y, z, explosionSize, false, false);
   }

   @Override
   public Explosion createExplosion(Entity entity, double x, double y, double z, float explosionSize, boolean flaming, boolean isCannonBall) {
      Explosion explosion;
      if (!isCannonBall) {
         explosion = new Explosion(this, entity, x, y, z, explosionSize);
      } else {
         explosion = new ExplosionCannonball(this, entity, x, y, z, explosionSize);
      }

      explosion.isFlaming = flaming;
      explosion.explode();
      explosion.addEffects(true);
      this.mcServer
         .playerList
         .sendPacketToPlayersAroundPoint(
            x, y, z, 64.0, this.dimension.id, new PacketExplosion(x, y, z, explosionSize, explosion.destroyedBlockPositions, isCannonBall)
         );
      return explosion;
   }

   @Override
   public void triggerEvent(int x, int y, int z, int index, int data) {
      super.triggerEvent(x, y, z, index, data);
      this.mcServer.playerList.sendPacketToPlayersAroundPoint(x, y, z, 64.0, this.dimension.id, new PacketBlockEvent(x, y, z, index, data));
   }

   public void checkLock() {
      this.saveHandler.checkSessionLock();
   }

   @Override
   public void tick() {
      super.tick();
      if (System.currentTimeMillis() - this.lastWeatherSend > 1000L) {
         this.mcServer
            .playerList
            .sendPacketToAllPlayers(
               new PacketWeatherStatus(
                  this.dimension.id,
                  this.getCurrentWeather() != null ? this.getCurrentWeather().weatherId : -1,
                  this.weatherManager.getNextWeather() != null ? this.weatherManager.getNextWeather().weatherId : -1,
                  this.weatherManager.getWeatherDuration(),
                  this.weatherManager.getWeatherIntensity(),
                  this.weatherManager.getWeatherPower()
               )
            );
         this.lastWeatherSend = System.currentTimeMillis();
      }
   }

   @Override
   public void sendGlobalMessage(String message) {
      this.mcServer.playerList.sendPacketToAllPlayers(new PacketChat(message));
   }

   @Override
   public void addRainbow(int rainbowTicks) {
      byte b0 = (byte)(rainbowTicks >> 24 & 0xFF);
      byte b1 = (byte)(rainbowTicks >> 16 & 0xFF);
      byte b2 = (byte)(rainbowTicks >> 8 & 0xFF);
      byte b3 = (byte)(rainbowTicks >> 0 & 0xFF);
      this.mcServer.playerList.sendPacketToAllPlayersInDimension(new PacketCustomPayload("BTA:RainbowStart", new byte[]{b0, b1, b2, b3}), this.dimension.id);
      super.addRainbow(rainbowTicks);
   }
}
