package net.minecraft.core.item.tool;

import java.util.List;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobSheep;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

public class ItemToolShears extends Item {
   public ItemToolShears(String name, String namespaceId, int id, ToolMaterial material) {
      super(name, namespaceId, id);
      this.setMaxStackSize(1);
      this.setMaxDamage(material.getDurability());
   }

   public void onBlockSheared(Mob entity, ItemStack itemStack) {
      itemStack.damageItem(1, entity);
   }

   @Override
   public boolean canHarvestBlock(Mob mob, ItemStack itemStack, Block<?> block) {
      return block.hasTag(BlockTags.MINEABLE_BY_SHEARS);
   }

   @Override
   public float getStrVsBlock(ItemStack itemstack, Block<?> block) {
      if (block == Blocks.COBWEB || Block.hasLogicClass(block, BlockLogicLeavesBase.class)) {
         return 15.0F;
      } else {
         return block.hasTag(BlockTags.MINEABLE_BY_SHEARS) ? 10.0F : super.getStrVsBlock(itemstack, block);
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
      blockX += direction.getOffsetX();
      blockY += direction.getOffsetY();
      blockZ += direction.getOffsetZ();
      AABB box = AABB.getTemporaryBB(blockX, blockY, blockZ, blockX + 1, blockY + 1, blockZ + 1);
      List<MobSheep> entities = world.getEntitiesWithinAABB(MobSheep.class, box);
      if (!entities.isEmpty()) {
         entities.get(0).onItemInteract(itemStack);
      }
   }
}
