package net.minecraft.core.net.entity.entries;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.IPacketEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketAddPlayer;
import org.jetbrains.annotations.NotNull;

public class NetEntryPlayerServer implements IPacketEntry<Player>, ITrackedEntry<Player> {
   @NotNull
   @Override
   public Class<Player> getAppliedClass() {
      return Player.class;
   }

   @Override
   public int getTrackingDistance() {
      return 512;
   }

   @Override
   public int getPacketDelay() {
      return 2;
   }

   @Override
   public boolean sendMotionUpdates() {
      return false;
   }

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, Player trackedObject) {
      for (EntityTrackerEntry entry : tracker.getTrackedEntries()) {
         if (entry.getTrackedEntity() != trackedObject) {
            entry.updatePlayerEntity(trackedObject);
         }
      }
   }

   public Packet getSpawnPacket(EntityTrackerEntry tracker, Player trackedObject) {
      return new PacketAddPlayer(trackedObject);
   }
}
