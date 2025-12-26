package net.minecraft.core.net.entity.entries;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityPrimedTNT;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.IVehicleEntry;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetEntryPrimedTNT implements IVehicleEntry<EntityPrimedTNT>, ITrackedEntry<EntityPrimedTNT> {
   @NotNull
   @Override
   public Class<EntityPrimedTNT> getAppliedClass() {
      return EntityPrimedTNT.class;
   }

   @Override
   public int getTrackingDistance() {
      return 160;
   }

   @Override
   public int getPacketDelay() {
      return 10;
   }

   @Override
   public boolean sendMotionUpdates() {
      return true;
   }

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, EntityPrimedTNT trackedObject) {
   }

   @Override
   public Entity getEntity(
      World world, double x, double y, double z, int metadata, boolean hasVelocity, double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag
   ) {
      return new EntityPrimedTNT(world, x, y, z);
   }

   public PacketAddEntity getSpawnPacket(EntityTrackerEntry tracker, EntityPrimedTNT trackedObject) {
      return new PacketAddEntity(trackedObject);
   }
}
