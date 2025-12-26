package net.minecraft.core.net.entity.entries;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.vehicle.EntityMinecart;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.IVehicleEntry;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetEntryMinecart implements IVehicleEntry<EntityMinecart>, ITrackedEntry<EntityMinecart> {
   @NotNull
   @Override
   public Class<EntityMinecart> getAppliedClass() {
      return EntityMinecart.class;
   }

   @Override
   public int getTrackingDistance() {
      return 160;
   }

   @Override
   public int getPacketDelay() {
      return 1;
   }

   @Override
   public boolean sendMotionUpdates() {
      return true;
   }

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, EntityMinecart trackedObject) {
   }

   @Override
   public Entity getEntity(
      World world, double x, double y, double z, int metadata, boolean hasVelocity, double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag
   ) {
      return new EntityMinecart(world, x, y, z, metadata);
   }

   public PacketAddEntity getSpawnPacket(EntityTrackerEntry tracker, EntityMinecart trackedObject) {
      return new PacketAddEntity(trackedObject, trackedObject.getType());
   }
}
