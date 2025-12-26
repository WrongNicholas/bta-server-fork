package net.minecraft.core.net.entity.entries;

import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.IPacketEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketAddItemEntity;
import org.jetbrains.annotations.NotNull;

public class NetEntryItem implements IPacketEntry<EntityItem>, ITrackedEntry<EntityItem> {
   @NotNull
   @Override
   public Class<EntityItem> getAppliedClass() {
      return EntityItem.class;
   }

   @Override
   public int getTrackingDistance() {
      return 64;
   }

   @Override
   public int getPacketDelay() {
      return 20;
   }

   @Override
   public boolean sendMotionUpdates() {
      return true;
   }

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, EntityItem trackedObject) {
   }

   public Packet getSpawnPacket(EntityTrackerEntry tracker, EntityItem trackedObject) {
      PacketAddItemEntity addItemEntityPacket21Pickupspawn = new PacketAddItemEntity(trackedObject);
      trackedObject.x = addItemEntityPacket21Pickupspawn.xPosition / 32.0;
      trackedObject.y = addItemEntityPacket21Pickupspawn.yPosition / 32.0;
      trackedObject.z = addItemEntityPacket21Pickupspawn.zPosition / 32.0;
      return addItemEntityPacket21Pickupspawn;
   }
}
