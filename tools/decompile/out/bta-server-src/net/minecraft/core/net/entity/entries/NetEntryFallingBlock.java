package net.minecraft.core.net.entity.entries;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityFallingBlock;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.IVehicleEntry;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetEntryFallingBlock implements IVehicleEntry<EntityFallingBlock>, ITrackedEntry<EntityFallingBlock> {
   @NotNull
   @Override
   public Class<EntityFallingBlock> getAppliedClass() {
      return EntityFallingBlock.class;
   }

   @Override
   public int getTrackingDistance() {
      return 160;
   }

   @Override
   public int getPacketDelay() {
      return 20;
   }

   @Override
   public boolean sendMotionUpdates() {
      return true;
   }

   public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, EntityFallingBlock trackedObject) {
   }

   @Override
   public Entity getEntity(
      World world, double x, double y, double z, int metadata, boolean hasVelocity, double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag
   ) {
      EntityFallingBlock fallingBlock = new EntityFallingBlock(world, x, y, z, MathHelper.clamp(metadata, 0, Blocks.blocksList.length), 0, null);
      if (tag != null) {
         fallingBlock.readAdditionalSaveData(tag);
      }

      return fallingBlock;
   }

   public PacketAddEntity getSpawnPacket(EntityTrackerEntry tracker, EntityFallingBlock trackedObject) {
      CompoundTag tag = new CompoundTag();
      trackedObject.addAdditionalSaveData(tag);
      return new PacketAddEntity(trackedObject, -1, -1, null, null, null, tag);
   }
}
