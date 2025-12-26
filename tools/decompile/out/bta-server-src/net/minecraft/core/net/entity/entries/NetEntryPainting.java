package net.minecraft.core.net.entity.entries;

import net.minecraft.core.entity.EntityPainting;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.IPacketEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketAddPainting;
import org.jetbrains.annotations.NotNull;

public class NetEntryPainting implements IPacketEntry<EntityPainting>, ITrackedEntry<EntityPainting> {
   @NotNull
   @Override
   public Class<EntityPainting> getAppliedClass() {
      return EntityPainting.class;
   }

   @Override
   public int getTrackingDistance() {
      return 160;
   }

   @Override
   public int getPacketDelay() {
      return Integer.MAX_VALUE;
   }

   @Override
   public boolean sendMotionUpdates() {
      return false;
   }

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, EntityPainting trackedObject) {
   }

   public Packet getSpawnPacket(EntityTrackerEntry tracker, EntityPainting trackedObject) {
      return new PacketAddPainting(trackedObject);
   }
}
