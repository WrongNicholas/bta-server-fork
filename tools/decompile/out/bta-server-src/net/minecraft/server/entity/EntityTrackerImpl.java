package net.minecraft.server.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.NetEntityHandler;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.util.collection.IntHashMap;
import net.minecraft.core.world.Dimension;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;

public class EntityTrackerImpl implements EntityTracker {
   public final Set<EntityTrackerEntryImpl> trackedEntitySet = new HashSet<>();
   public final IntHashMap<EntityTrackerEntryImpl> trackedEntityHashTable = new IntHashMap<>();
   public final MinecraftServer mcServer;
   public final int maxTrackingDistanceThreshold;
   public final Dimension dimension;

   public EntityTrackerImpl(MinecraftServer minecraftserver, Dimension dimensions) {
      this.mcServer = minecraftserver;
      this.dimension = dimensions;
      this.maxTrackingDistanceThreshold = minecraftserver.playerList.getMaxTrackingDistance();
   }

   @Override
   public void trackEntity(Entity entity) {
      ITrackedEntry<Entity> trackedConfig = NetEntityHandler.getTrackedEntry(entity);
      if (trackedConfig != null) {
         EntityTrackerEntryImpl trackerEntry = this.trackEntity(
            entity, trackedConfig.getTrackingDistance(), trackedConfig.getPacketDelay(), trackedConfig.sendMotionUpdates()
         );
         trackedConfig.onEntityTracked(this, trackerEntry, entity);
      }
   }

   public EntityTrackerEntryImpl trackEntity(Entity entity, int trackingDistance, int packetDelay, boolean shouldSendMotionUpdates) {
      if (trackingDistance > this.maxTrackingDistanceThreshold) {
         trackingDistance = this.maxTrackingDistanceThreshold;
      }

      if (this.trackedEntityHashTable.containsValue(entity.id)) {
         throw new IllegalStateException("Entity is already tracked!");
      } else {
         EntityTrackerEntryImpl trackerEntry = new EntityTrackerEntryImpl(entity, trackingDistance, packetDelay, shouldSendMotionUpdates);
         this.trackedEntitySet.add(trackerEntry);
         this.trackedEntityHashTable.put(entity.id, trackerEntry);
         trackerEntry.updatePlayerEntities(this.mcServer.getDimensionWorld(this.dimension.id).players);
         return trackerEntry;
      }
   }

   @Override
   public void untrackEntity(Entity entity) {
      if (entity instanceof PlayerServer) {
         PlayerServer playerServer = (PlayerServer)entity;

         for (EntityTrackerEntryImpl entitytrackerentry1 : this.trackedEntitySet) {
            entitytrackerentry1.removeFromTrackedPlayers(playerServer);
         }
      }

      EntityTrackerEntryImpl entityTrackerEntry = this.trackedEntityHashTable.remove(entity.id);
      if (entityTrackerEntry != null) {
         this.trackedEntitySet.remove(entityTrackerEntry);
         entityTrackerEntry.sendDestroyEntityPacketToTrackedPlayers();
      }
   }

   @Override
   public void tick() {
      ArrayList<Entity> arraylist = new ArrayList<>();

      for (EntityTrackerEntryImpl entityTrackerEntry : this.trackedEntitySet) {
         entityTrackerEntry.tick(this.mcServer.getDimensionWorld(this.dimension.id).players);
         if (entityTrackerEntry.playerEntitiesUpdated && entityTrackerEntry.trackedEntity instanceof PlayerServer) {
            arraylist.add(entityTrackerEntry.trackedEntity);
         }
      }

      for (int i = 0; i < arraylist.size(); i++) {
         PlayerServer entityplayermp = (PlayerServer)arraylist.get(i);

         for (EntityTrackerEntryImpl entitytrackerentry1 : this.trackedEntitySet) {
            if (entitytrackerentry1.trackedEntity != entityplayermp) {
               entitytrackerentry1.updatePlayerEntity(entityplayermp);
            }
         }
      }
   }

   @Override
   public void sendPacketToTrackedPlayers(Entity entity, Packet packet) {
      EntityTrackerEntryImpl entityTrackerEntry = this.trackedEntityHashTable.get(entity.id);
      if (entityTrackerEntry != null) {
         entityTrackerEntry.sendPacketToTrackedPlayers(packet);
      }
   }

   @Override
   public void sendPacketToTrackedPlayersAndTrackedEntity(Entity entity, Packet packet) {
      EntityTrackerEntryImpl entityTrackerEntry = this.trackedEntityHashTable.get(entity.id);
      if (entityTrackerEntry != null) {
         entityTrackerEntry.sendPacketToTrackedPlayersAndTrackedEntity(packet);
      }
   }

   @Override
   public void removeTrackedPlayerSymmetric(Player player) {
      for (EntityTrackerEntryImpl entitytrackerentry : this.trackedEntitySet) {
         entitytrackerentry.removeTrackedPlayerSymmetric(player);
      }
   }

   @Override
   public Set<? extends EntityTrackerEntry> getTrackedEntries() {
      return this.trackedEntitySet;
   }
}
