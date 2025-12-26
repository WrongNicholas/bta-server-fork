package net.minecraft.core.net.entity.entries;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityFishingBobber;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.IVehicleEntry;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetEntryBobber implements IVehicleEntry<EntityFishingBobber>, ITrackedEntry<EntityFishingBobber> {
   @NotNull
   @Override
   public Class<EntityFishingBobber> getAppliedClass() {
      return EntityFishingBobber.class;
   }

   @Override
   public int getTrackingDistance() {
      return 64;
   }

   @Override
   public int getPacketDelay() {
      return 5;
   }

   @Override
   public boolean sendMotionUpdates() {
      return true;
   }

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, EntityFishingBobber trackedObject) {
   }

   @Override
   public Entity getEntity(
      World world, double x, double y, double z, int metadata, boolean hasVelocity, double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag
   ) {
      EntityFishingBobber bobber = new EntityFishingBobber(world, x, y, z);
      if (owner != null) {
         bobber.owner = (Player)owner;
         ((Player)owner).bobberEntity = bobber;
      }

      return bobber;
   }

   public PacketAddEntity getSpawnPacket(EntityTrackerEntry tracker, EntityFishingBobber bobber) {
      return new PacketAddEntity(bobber, 0, bobber.owner == null ? -1 : bobber.owner.id);
   }
}
