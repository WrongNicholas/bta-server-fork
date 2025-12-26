package net.minecraft.core.block.motion;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.ICarriable;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CarriedBlock implements ICarriable {
   public int blockId;
   public int metadata;
   @Nullable
   public TileEntity entity;
   @NotNull
   public Entity holder;
   @NotNull
   public World world;

   public CarriedBlock(@NotNull Entity holder, @NotNull Block<?> block, int metadata, @Nullable TileEntity entity) {
      this(holder, block.id(), metadata, entity);
   }

   public CarriedBlock(@NotNull Entity holder, int blockId, int metadata, @Nullable TileEntity entity) {
      this.holder = holder;
      this.blockId = blockId;
      this.metadata = metadata;
      this.entity = entity;
      this.world = holder.world;

      assert this.world != null : "Carried block world ref must not be null!";
   }

   protected CarriedBlock(@NotNull Entity holder) {
      this.holder = holder;
      this.world = holder.world;

      assert this.world != null : "Carried block world ref must not be null!";
   }

   public Block<?> block() {
      return Blocks.getBlock(this.blockId);
   }

   @Override
   public void heldTick(World world, Entity holder) {
      if (this.entity != null) {
         this.entity.heldTick(world, holder);
      }
   }

   @Override
   public boolean tryPlace(World world, Entity holder, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced) {
      if (this.entity != null) {
         return this.entity.tryPlace(world, holder, blockX, blockY, blockZ, side, xPlaced, yPlaced);
      } else {
         int x = blockX + side.getOffsetX();
         int y = blockY + side.getOffsetY();
         int z = blockZ + side.getOffsetZ();
         Block<?> currentBlock = world.getBlock(x, y, z);
         if (currentBlock != null && !currentBlock.hasTag(BlockTags.PLACE_OVERWRITES)) {
            return false;
         } else {
            world.setBlockAndMetadataWithNotify(x, y, z, this.blockId, this.metadata);
            Block<?> b = world.getBlock(x, y, z);
            if (b != null && holder instanceof Mob) {
               b.onBlockPlacedByMob(world, x, y, z, side, (Mob)holder, xPlaced, yPlaced);
            }

            return true;
         }
      }
   }

   @Override
   public void drop(World world, Entity holder) {
      if (this.entity != null) {
         this.entity.drop(world, holder);
      } else {
         int holderX = MathHelper.floor(holder.x);
         int holderY = MathHelper.floor(holder.y);
         int holderZ = MathHelper.floor(holder.z);

         for (int _y = holderY - 1; _y <= holderY + 1; _y++) {
            for (int _x = holderX - 1; _x <= holderX + 1; _x++) {
               for (int _z = holderZ - 1; _z <= holderZ + 1; _z++) {
                  if (this.tryPlace(world, holder, _x, _y - 1, _z, Side.TOP, 0.0, 0.0)) {
                     return;
                  }
               }
            }
         }

         if (world.isClientSide) {
            return;
         }

         this.block().dropBlockWithCause(world, EnumDropCause.WORLD, holderX, holderY, holderZ, this.metadata, this.entity, null);
      }
   }

   @Override
   public boolean canBeCarried(World world, Entity potentialHolder) {
      return true;
   }

   @Override
   public ICarriable pickup(World world, Entity holder) {
      return this;
   }

   @Override
   public void writeToNBT(CompoundTag tag) {
      if (this.entity != null) {
         CompoundTag entityTag = new CompoundTag();
         this.entity.writeToNBT(entityTag);
         tag.put("entity", entityTag);
      }

      tag.putInt("block", this.blockId);
      tag.putShort("meta", (short)this.metadata);
      tag.putString("type", "block");
   }

   @Override
   public void readFromNBT(CompoundTag tag) {
      if (tag.containsKey("entity")) {
         this.entity = TileEntityDispatcher.createAndLoadEntity(tag.getCompound("entity"));
         if (this.entity != null) {
            this.entity.carriedBlock = this;
         }
      }

      this.blockId = tag.getInteger("block");
      this.metadata = tag.getShort("meta");
   }

   public static CarriedBlock createAndLoadCarriedBlock(@NotNull Entity holder, CompoundTag tag) {
      CarriedBlock carriedBlock = new CarriedBlock(holder);
      carriedBlock.readFromNBT(tag);
      return carriedBlock;
   }
}
