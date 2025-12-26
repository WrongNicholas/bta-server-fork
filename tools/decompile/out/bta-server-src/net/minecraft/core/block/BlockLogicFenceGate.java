package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicFenceGate extends BlockLogic implements IPaintable {
   public static final int MASK_DIRECTION = 3;
   public static final int MASK_OPEN = 4;

   public BlockLogicFenceGate(Block<?> block) {
      super(block, Material.wood);
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      int l = world.getBlockMetadata(x, y, z);
      return isOpen(l) ? null : this.getAABBFromMeta(l).grow(0.0, 0.25, 0.0).move(x, y + 0.25F, z);
   }

   private AABB getAABBFromMeta(int meta) {
      return getDirection(meta) != 3 && getDirection(meta) != 1
         ? AABB.getTemporaryBB(0.0, 0.0, 0.375, 1.0, 1.0, 0.625)
         : AABB.getTemporaryBB(0.375, 0.0, 0.0, 0.625, 1.0, 1.0);
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      return this.getAABBFromMeta(world.getBlockMetadata(x, y, z));
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
      int direction = mob.getHorizontalPlacementDirection(side).getHorizontalIndex();
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockMetadataWithNotify(x, y, z, direction | meta & -4);
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      if (!side.isHorizontal()) {
         side = Side.NORTH;
      }

      int direction = side.getDirection().getHorizontalIndex();
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockMetadataWithNotify(x, y, z, direction | meta & -4);
   }

   @Override
   public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      if (!Item.hasTag(player.getCurrentEquippedItem(), ItemTags.PREVENT_LEFT_CLICK_INTERACTIONS)) {
         this.onBlockRightClicked(world, x, y, z, player, null, 0.0, 0.0);
      }
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, @Nullable Player player, Side side, double xPlaced, double yPlaced) {
      int l = world.getBlockMetadata(x, y, z);
      if (isOpen(l)) {
         world.setBlockMetadataWithNotify(x, y, z, l & -5);
      } else {
         int i1 = 0;
         if (player != null) {
            i1 = (MathHelper.floor(player.yRot * 4.0F / 360.0F + 0.5) & 3) % 4;
         }

         int j1 = getDirection(l);
         if (j1 == (i1 + 2) % 4) {
            l = l & 240 | i1;
         }

         world.setBlockMetadataWithNotify(x, y, z, l | 4);
      }

      if (Math.random() < 0.5) {
         world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, x, y, z, "random.door_open", 1.0F, 1.0F);
      } else {
         world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, x, y, z, "random.door_close", 1.0F, 1.0F);
      }

      return true;
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      this.onBlockRightClicked(world, x, y, z, null, direction.getSide(), 0.5, 0.5);
   }

   public static boolean isOpen(int meta) {
      return (meta & 4) != 0;
   }

   public static int getDirection(int meta) {
      return meta & 3;
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockAndMetadata(x, y, z, Blocks.FENCE_GATE_PLANKS_OAK_PAINTED.id(), meta);
      Blocks.FENCE_GATE_PLANKS_OAK_PAINTED.getLogic().setColor(world, x, y, z, color);
   }
}
