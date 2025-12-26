package net.minecraft.core.net.entity.entries;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.entity.projectile.ProjectileArrowGolden;
import net.minecraft.core.entity.projectile.ProjectileArrowPurple;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.IVehicleEntry;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetEntryArrow implements IVehicleEntry<ProjectileArrow>, ITrackedEntry<ProjectileArrow> {
   @NotNull
   @Override
   public Class<ProjectileArrow> getAppliedClass() {
      return ProjectileArrow.class;
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
      return false;
   }

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, ProjectileArrow trackedObject) {
   }

   @Override
   public Entity getEntity(
      World world, double x, double y, double z, int metadata, boolean hasVelocity, double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag
   ) {
      if (metadata == 2) {
         return new ProjectileArrowGolden(world, x, y, z);
      } else {
         return (Entity)(metadata == 1 ? new ProjectileArrowPurple(world, x, y, z) : new ProjectileArrow(world, x, y, z, metadata));
      }
   }

   public PacketAddEntity getSpawnPacket(EntityTrackerEntry tracker, ProjectileArrow trackedObject) {
      Mob entityliving = trackedObject.owner;
      return new PacketAddEntity(
         trackedObject, trackedObject.getArrowType(), entityliving == null ? -1 : entityliving.id, trackedObject.xd, trackedObject.yd, trackedObject.zd
      );
   }
}
