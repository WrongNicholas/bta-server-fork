package net.minecraft.core.net.entity.entries;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.projectile.ProjectilePebble;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.IVehicleEntry;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetEntryPebble implements IVehicleEntry<ProjectilePebble>, ITrackedEntry<ProjectilePebble> {
   @NotNull
   @Override
   public Class<ProjectilePebble> getAppliedClass() {
      return ProjectilePebble.class;
   }

   @Override
   public int getTrackingDistance() {
      return 64;
   }

   @Override
   public int getPacketDelay() {
      return 10;
   }

   @Override
   public boolean sendMotionUpdates() {
      return true;
   }

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, ProjectilePebble trackedObject) {
   }

   @Override
   public Entity getEntity(
      World world, double x, double y, double z, int metadata, boolean hasVelocity, double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag
   ) {
      return new ProjectilePebble(world, x, y, z);
   }

   public PacketAddEntity getSpawnPacket(EntityTrackerEntry tracker, ProjectilePebble trackedObject) {
      return new PacketAddEntity(trackedObject);
   }
}
