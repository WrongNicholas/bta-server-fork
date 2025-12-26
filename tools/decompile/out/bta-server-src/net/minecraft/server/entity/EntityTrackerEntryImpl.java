package net.minecraft.server.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.NetEntityHandler;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketEntityNickname;
import net.minecraft.core.net.packet.PacketEntityTagData;
import net.minecraft.core.net.packet.PacketMoveEntity;
import net.minecraft.core.net.packet.PacketRemoveEntity;
import net.minecraft.core.net.packet.PacketSetEntityData;
import net.minecraft.core.net.packet.PacketSetEntityMotion;
import net.minecraft.core.net.packet.PacketSetEquippedItem;
import net.minecraft.core.net.packet.PacketSleep;
import net.minecraft.core.net.packet.PacketTeleportEntity;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.data.SynchedEntityData;
import net.minecraft.server.entity.player.PlayerServer;

public class EntityTrackerEntryImpl implements EntityTrackerEntry {
   public Entity trackedEntity;
   public int trackingDistance;
   public int packetDelay;
   public int encodedPosX;
   public int encodedPosY;
   public int encodedPosZ;
   public int encodedRotationYaw;
   public int encodedRotationPitch;
   public double lastTrackedEntityMotionX;
   public double lastTrackedEntityMotionY;
   public double lastTrackedEntityMotionZ;
   public int updateCounter = 0;
   private double lastTrackedEntityPosX;
   private double lastTrackedEntityPosY;
   private double lastTrackedEntityPosZ;
   private boolean firstUpdateDone;
   private final boolean shouldSendMotionUpdates;
   private int ticksSinceLastTeleport = 0;
   public boolean playerEntitiesUpdated;
   public Set<PlayerServer> trackedPlayers;

   public EntityTrackerEntryImpl(Entity entity, int trackingDistance, int packetDelay, boolean shouldSendMotionUpdates) {
      this.firstUpdateDone = false;
      this.playerEntitiesUpdated = false;
      this.trackedPlayers = new HashSet<>();
      this.trackedEntity = entity;
      this.trackingDistance = trackingDistance;
      this.packetDelay = packetDelay;
      this.shouldSendMotionUpdates = shouldSendMotionUpdates;
      this.encodedPosX = MathHelper.floor(entity.x * 32.0);
      this.encodedPosY = MathHelper.floor(entity.y * 32.0);
      this.encodedPosZ = MathHelper.floor(entity.z * 32.0);
      this.encodedRotationYaw = MathHelper.floor_float(entity.yRot * 256.0F / 360.0F);
      this.encodedRotationPitch = MathHelper.floor_float(entity.xRot * 256.0F / 360.0F);
   }

   @Override
   public boolean equals(Object obj) {
      return !(obj instanceof EntityTrackerEntryImpl) ? false : ((EntityTrackerEntryImpl)obj).trackedEntity.id == this.trackedEntity.id;
   }

   @Override
   public int hashCode() {
      return this.trackedEntity.id;
   }

   @Override
   public void tick(List<Player> list) {
      this.playerEntitiesUpdated = false;
      if (!this.firstUpdateDone || this.trackedEntity.distanceToSqr(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0) {
         this.lastTrackedEntityPosX = this.trackedEntity.x;
         this.lastTrackedEntityPosY = this.trackedEntity.y;
         this.lastTrackedEntityPosZ = this.trackedEntity.z;
         this.firstUpdateDone = true;
         this.playerEntitiesUpdated = true;
         this.updatePlayerEntities(list);
      }

      this.ticksSinceLastTeleport++;
      if (++this.updateCounter % this.packetDelay == 0) {
         int entityX = MathHelper.floor(this.trackedEntity.x * 32.0);
         int entityY = MathHelper.floor(this.trackedEntity.y * 32.0);
         int entityZ = MathHelper.floor(this.trackedEntity.z * 32.0);
         int entityYaw = MathHelper.floor_float(this.trackedEntity.yRot * 256.0F / 360.0F);
         int entityPitch = MathHelper.floor_float(this.trackedEntity.xRot * 256.0F / 360.0F);
         int dx = entityX - this.encodedPosX;
         int dy = entityY - this.encodedPosY;
         int dz = entityZ - this.encodedPosZ;
         Packet packet = null;
         boolean hasMoved = Math.abs(entityX) >= 8 || Math.abs(entityY) >= 8 || Math.abs(entityZ) >= 8;
         boolean hasLooked = Math.abs(entityYaw - this.encodedRotationYaw) >= 8 || Math.abs(entityPitch - this.encodedRotationPitch) >= 8;
         if (dx < -128 || dx >= 128 || dy < -128 || dy >= 128 || dz < -128 || dz >= 128 || this.ticksSinceLastTeleport > 400) {
            this.ticksSinceLastTeleport = 0;
            this.trackedEntity.x = entityX / 32.0;
            this.trackedEntity.y = entityY / 32.0;
            this.trackedEntity.z = entityZ / 32.0;
            packet = new PacketTeleportEntity(this.trackedEntity.id, entityX, entityY, entityZ, (byte)entityYaw, (byte)entityPitch);
         } else if (hasMoved && hasLooked) {
            packet = new PacketMoveEntity.PosRot(this.trackedEntity.id, (byte)dx, (byte)dy, (byte)dz, (byte)entityYaw, (byte)entityPitch);
         } else if (hasMoved) {
            packet = new PacketMoveEntity.Pos(this.trackedEntity.id, (byte)dx, (byte)dy, (byte)dz);
         } else if (hasLooked) {
            packet = new PacketMoveEntity.Rot(this.trackedEntity.id, (byte)entityYaw, (byte)entityPitch);
         }

         if (this.shouldSendMotionUpdates) {
            double d = this.trackedEntity.xd - this.lastTrackedEntityMotionX;
            double d1 = this.trackedEntity.yd - this.lastTrackedEntityMotionY;
            double d2 = this.trackedEntity.zd - this.lastTrackedEntityMotionZ;
            double d3 = 0.02;
            double d4 = d * d + d1 * d1 + d2 * d2;
            if (d4 > d3 * d3 || d4 > 0.0 && this.trackedEntity.xd == 0.0 && this.trackedEntity.yd == 0.0 && this.trackedEntity.zd == 0.0) {
               this.lastTrackedEntityMotionX = this.trackedEntity.xd;
               this.lastTrackedEntityMotionY = this.trackedEntity.yd;
               this.lastTrackedEntityMotionZ = this.trackedEntity.zd;
               this.sendPacketToTrackedPlayers(
                  new PacketSetEntityMotion(this.trackedEntity.id, this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.lastTrackedEntityMotionZ)
               );
            }
         }

         if (packet != null) {
            this.sendPacketToTrackedPlayers(packet);
         }

         SynchedEntityData synchedEntityData = this.trackedEntity.getEntityData();
         if (synchedEntityData.isDirty()) {
            this.sendPacketToTrackedPlayersAndTrackedEntity(new PacketSetEntityData(this.trackedEntity.id, synchedEntityData));
         }

         if (hasMoved) {
            this.encodedPosX = entityX;
            this.encodedPosY = entityY;
            this.encodedPosZ = entityZ;
         }

         if (hasLooked) {
            this.encodedRotationYaw = entityYaw;
            this.encodedRotationPitch = entityPitch;
         }
      }

      if (this.trackedEntity.hurtMarked) {
         this.sendPacketToTrackedPlayersAndTrackedEntity(new PacketSetEntityMotion(this.trackedEntity));
         this.trackedEntity.hurtMarked = false;
      }

      if (this.trackedEntity.hadNicknameSet) {
         this.sendPacketToTrackedPlayersAndTrackedEntity(
            new PacketEntityNickname(this.trackedEntity.id, ((Mob)this.trackedEntity).nickname, ((Mob)this.trackedEntity).chatColor)
         );
         this.trackedEntity.hadNicknameSet = false;
      }
   }

   @Override
   public void sendPacketToTrackedPlayers(Packet packet) {
      for (PlayerServer playerMP : this.trackedPlayers) {
         playerMP.playerNetServerHandler.sendPacket(packet);
      }
   }

   @Override
   public void sendPacketToTrackedPlayersAndTrackedEntity(Packet packet) {
      this.sendPacketToTrackedPlayers(packet);
      if (this.trackedEntity instanceof PlayerServer) {
         ((PlayerServer)this.trackedEntity).playerNetServerHandler.sendPacket(packet);
      }
   }

   @Override
   public void sendDestroyEntityPacketToTrackedPlayers() {
      this.sendPacketToTrackedPlayers(new PacketRemoveEntity(this.trackedEntity.id));
   }

   @Override
   public void removeFromTrackedPlayers(Player player) {
      this.trackedPlayers.remove(player);
   }

   @Override
   public void updatePlayerEntity(Player player) {
      assert player instanceof PlayerServer : "Player must be a instance of player server!";

      PlayerServer playerServer = (PlayerServer)player;
      if (playerServer != this.trackedEntity) {
         double dx = playerServer.x - this.encodedPosX / 32;
         double dz = playerServer.z - this.encodedPosZ / 32;
         if (dx >= -this.trackingDistance && dx <= this.trackingDistance && dz >= -this.trackingDistance && dz <= this.trackingDistance) {
            if (!this.trackedPlayers.contains(playerServer)) {
               this.trackedPlayers.add(playerServer);
               playerServer.playerNetServerHandler.sendPacket(NetEntityHandler.getSpawnPacket(this));
               if (this.trackedEntity.sendAdditionalData) {
                  playerServer.playerNetServerHandler.sendPacket(new PacketEntityTagData(this.trackedEntity));
               }

               if (this.shouldSendMotionUpdates) {
                  playerServer.playerNetServerHandler
                     .sendPacket(new PacketSetEntityMotion(this.trackedEntity.id, this.trackedEntity.xd, this.trackedEntity.yd, this.trackedEntity.zd));
               }

               ItemStack[] aitemstack = this.trackedEntity.getInventory();
               if (aitemstack != null) {
                  for (int i = 0; i < aitemstack.length; i++) {
                     playerServer.playerNetServerHandler.sendPacket(new PacketSetEquippedItem(this.trackedEntity.id, i, aitemstack[i]));
                  }
               }

               if (this.trackedEntity instanceof PlayerServer) {
                  PlayerServer trackedPlayerServer = (PlayerServer)this.trackedEntity;
                  if (trackedPlayerServer.isPlayerSleeping()) {
                     playerServer.playerNetServerHandler
                        .sendPacket(
                           new PacketSleep(
                              this.trackedEntity,
                              0,
                              MathHelper.floor(this.trackedEntity.x),
                              MathHelper.floor(this.trackedEntity.y),
                              MathHelper.floor(this.trackedEntity.z)
                           )
                        );
                  }
               }
            }
         } else if (this.trackedPlayers.contains(playerServer)) {
            this.trackedPlayers.remove(playerServer);
            playerServer.playerNetServerHandler.sendPacket(new PacketRemoveEntity(this.trackedEntity.id));
         }
      }
   }

   @Override
   public void updatePlayerEntities(List<Player> list) {
      for (Player player : list) {
         this.updatePlayerEntity((PlayerServer)player);
      }
   }

   @Override
   public void removeTrackedPlayerSymmetric(Player player) {
      if (player instanceof PlayerServer) {
         PlayerServer playerServer = (PlayerServer)player;
         if (this.trackedPlayers.contains(playerServer)) {
            this.trackedPlayers.remove(playerServer);
            playerServer.playerNetServerHandler.sendPacket(new PacketRemoveEntity(this.trackedEntity.id));
         }
      }
   }

   @Override
   public Entity getTrackedEntity() {
      return this.trackedEntity;
   }
}
