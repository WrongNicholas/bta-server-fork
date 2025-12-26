package net.minecraft.core.net.entity.entries;

import net.minecraft.core.entity.Mob;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.IPacketEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketAddMob;
import org.jetbrains.annotations.NotNull;

public class NetEntryAnimal<T extends Mob> implements IPacketEntry<T>, ITrackedEntry<T> {
   @NotNull
   @Override
   public Class<T> getAppliedClass() {
      return (Class<T>)Mob.class;
   }

   @Override
   public int getTrackingDistance() {
      return 160;
   }

   @Override
   public int getPacketDelay() {
      return 3;
   }

   @Override
   public boolean sendMotionUpdates() {
      return false;
   }

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, Mob trackedObject) {
   }

   public Packet getSpawnPacket(EntityTrackerEntry tracker, Mob trackedObject) {
      return new PacketAddMob(trackedObject);
   }
}
