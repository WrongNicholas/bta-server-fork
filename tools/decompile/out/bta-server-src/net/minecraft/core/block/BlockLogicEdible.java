package net.minecraft.core.block;

import java.util.function.Supplier;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class BlockLogicEdible extends BlockLogic {
   @NotNull
   private final Supplier<Item> itemSupplier;
   public int maxBites;
   public int healAmount;

   public BlockLogicEdible(Block<?> block, int maxBites, int healAmount, @NotNull Supplier<Item> dropItemSupplier) {
      super(block, Material.cake);
      this.maxBites = maxBites;
      this.healAmount = healAmount;
      this.itemSupplier = dropItemSupplier;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return dropCause != EnumDropCause.PICK_BLOCK && meta != 0 ? null : new ItemStack[]{new ItemStack(this.itemSupplier.get())};
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   private void eatSlice(World world, int x, int y, int z, Player entityplayer) {
      if (entityplayer.getHealth() < entityplayer.getMaxHealth()) {
         entityplayer.heal(this.healAmount);
         int data = world.getBlockMetadata(x, y, z) + 1;
         if (data >= this.maxBites) {
            world.setBlockWithNotify(x, y, z, 0);
         } else {
            world.setBlockMetadataWithNotify(x, y, z, data);
            world.markBlockDirty(x, y, z);
         }
      }
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      this.eatSlice(world, x, y, z, player);
      return true;
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return !super.canPlaceBlockAt(world, x, y, z) ? false : this.canBlockStay(world, x, y, z);
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!this.canBlockStay(world, x, y, z)) {
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public boolean canBlockStay(World world, int x, int y, int z) {
      return world.getBlockMaterial(x, y - 1, z).isSolid();
   }
}
