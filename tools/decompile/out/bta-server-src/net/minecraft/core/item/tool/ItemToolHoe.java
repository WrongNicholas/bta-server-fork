package net.minecraft.core.item.tool;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemToolHoe extends ItemTool {
   public ItemToolHoe(String name, String namespaceId, int id, ToolMaterial enumtoolmaterial) {
      super(name, namespaceId, id, 3, enumtoolmaterial, BlockTags.MINEABLE_BY_HOE);
      this.maxStackSize = 1;
      this.setMaxDamage(enumtoolmaterial.getDurability());
   }

   @Override
   public boolean canHarvestBlock(Mob mob, ItemStack itemStack, Block<?> block) {
      return block.hasTag(BlockTags.MINEABLE_BY_HOE);
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      return this.till(itemstack, player, world, blockX, blockY, blockZ, side);
   }

   public boolean till(ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side) {
      int id = world.getBlockId(blockX, blockY, blockZ);
      int j1 = world.getBlockId(blockX, blockY + 1, blockZ);
      if (side != Side.BOTTOM
         && j1 == 0
         && (id == Blocks.GRASS.id() || id == Blocks.DIRT.id() || id == Blocks.PATH_DIRT.id() || id == Blocks.GRASS_RETRO.id() || id == Blocks.MUD.id())) {
         int meta = 0;
         if (id == Blocks.MUD.id()) {
            meta = 1;
         }

         world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, Blocks.DIRT, EnumBlockSoundEffectType.MINE);
         if (!world.isClientSide
            && (id == Blocks.GRASS.id() || id == Blocks.GRASS_RETRO.id() || id == Blocks.GRASS_SCORCHED.id())
            && world.rand.nextInt(5) == 0) {
            world.dropItem(blockX, blockY + 1, blockZ, new ItemStack(Items.SEEDS_WHEAT));
         }

         if (world.isClientSide) {
            return true;
         } else {
            world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, Blocks.FARMLAND_DIRT.id(), meta);
            itemstack.damageItem(1, player);
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onUseByActivator(
      ItemStack itemStack,
      TileEntityActivator activatorBlock,
      World world,
      Random random,
      int blockX,
      int blockY,
      int blockZ,
      double offX,
      double offY,
      double offZ,
      Direction direction
   ) {
      this.till(itemStack, null, world, blockX + direction.getOffsetX(), blockY + direction.getOffsetY(), blockZ + direction.getOffsetZ(), direction.getSide());
   }

   @Override
   public boolean onBlockDestroyed(World world, ItemStack itemstack, int id, int x, int y, int z, Side side, Mob mob) {
      if (mob != null && !mob.world.isClientSide && (id == Blocks.TALLGRASS.id() || id == Blocks.TALLGRASS_FERN.id())) {
         if (this.material.isSilkTouch()) {
            mob.world.dropItem(x, y, z, new ItemStack(Item.itemsList[id]));
            itemstack.damageItem(1, mob);
         } else if (mob.world.rand.nextInt(4) == 0) {
            mob.world.dropItem(x, y, z, new ItemStack(Items.SEEDS_WHEAT));
            itemstack.damageItem(1, mob);
         }
      }

      return super.onBlockDestroyed(world, itemstack, id, x, y, z, side, mob);
   }
}
