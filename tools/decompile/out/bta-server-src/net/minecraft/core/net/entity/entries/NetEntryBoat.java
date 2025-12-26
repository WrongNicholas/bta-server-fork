package net.minecraft.core.net.entity.entries;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.vehicle.EntityBoat;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.IVehicleEntry;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetEntryBoat implements IVehicleEntry<EntityBoat>, ITrackedEntry<EntityBoat> {
   @NotNull
   @Override
   public Class<EntityBoat> getAppliedClass() {
      return EntityBoat.class;
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

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, EntityBoat trackedObject) {
   }

   @Override
   public Entity getEntity(
      World world, double x, double y, double z, int metadata, boolean hasVelocity, double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag
   ) {
      return new EntityBoat(world, x, y, z);
   }

   public PacketAddEntity getSpawnPacket(EntityTrackerEntry tracker, EntityBoat trackedObject) {
      return new PacketAddEntity(trackedObject);
   }
}
