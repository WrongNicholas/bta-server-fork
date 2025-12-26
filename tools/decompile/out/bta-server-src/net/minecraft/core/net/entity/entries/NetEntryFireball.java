package net.minecraft.core.net.entity.entries;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.projectile.ProjectileFireball;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.IVehicleEntry;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetEntryFireball implements IVehicleEntry<ProjectileFireball>, ITrackedEntry<ProjectileFireball> {
   @NotNull
   @Override
   public Class<ProjectileFireball> getAppliedClass() {
      return ProjectileFireball.class;
   }

   @Override
   public int getTrackingDistance() {
      return 128;
   }

   @Override
   public int getPacketDelay() {
      return 1;
   }

   @Override
   public boolean sendMotionUpdates() {
      return true;
   }

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, ProjectileFireball trackedObject) {
   }

   @Override
   public Entity getEntity(
      World world, double x, double y, double z, int metadata, boolean hasVelocity, double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag
   ) {
      return new ProjectileFireball(world, x, y, z, xd, yd, zd);
   }

   public PacketAddEntity getSpawnPacket(EntityTrackerEntry tracker, ProjectileFireball trackedObject) {
      return new PacketAddEntity(trackedObject, -1, -1, trackedObject.xd, trackedObject.yd, trackedObject.zd);
   }
}
