package net.minecraft.core.item.tool;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;

public class ItemToolPickaxe extends ItemTool {
   public static Map<Block<?>, Integer> miningLevels = new HashMap<>();

   public ItemToolPickaxe(String name, String namespaceId, int id, ToolMaterial enumtoolmaterial) {
      super(name, namespaceId, id, 2, enumtoolmaterial, BlockTags.MINEABLE_BY_PICKAXE);
   }

   @Override
   public boolean canHarvestBlock(Mob mob, ItemStack itemStack, Block<?> block) {
      Integer miningLevel = miningLevels.get(block);
      return miningLevel != null ? this.material.getMiningLevel() >= miningLevel : block.hasTag(BlockTags.MINEABLE_BY_PICKAXE);
   }

   static {
      miningLevels.put(Blocks.OBSIDIAN, 3);
      miningLevels.put(Blocks.BLOCK_DIAMOND, 2);
      miningLevels.put(Blocks.ORE_DIAMOND_STONE, 2);
      miningLevels.put(Blocks.ORE_DIAMOND_BASALT, 2);
      miningLevels.put(Blocks.ORE_DIAMOND_GRANITE, 2);
      miningLevels.put(Blocks.ORE_DIAMOND_LIMESTONE, 2);
      miningLevels.put(Blocks.BLOCK_GOLD, 2);
      miningLevels.put(Blocks.ORE_GOLD_STONE, 2);
      miningLevels.put(Blocks.ORE_GOLD_BASALT, 2);
      miningLevels.put(Blocks.ORE_GOLD_GRANITE, 2);
      miningLevels.put(Blocks.ORE_GOLD_LIMESTONE, 2);
      miningLevels.put(Blocks.BLOCK_IRON, 1);
      miningLevels.put(Blocks.ORE_IRON_STONE, 1);
      miningLevels.put(Blocks.ORE_IRON_BASALT, 1);
      miningLevels.put(Blocks.ORE_IRON_GRANITE, 1);
      miningLevels.put(Blocks.ORE_IRON_LIMESTONE, 1);
      miningLevels.put(Blocks.BLOCK_STEEL, 2);
      miningLevels.put(Blocks.ORE_NETHERCOAL_NETHERRACK, 2);
      miningLevels.put(Blocks.BLOCK_LAPIS, 1);
      miningLevels.put(Blocks.ORE_LAPIS_STONE, 1);
      miningLevels.put(Blocks.ORE_LAPIS_BASALT, 1);
      miningLevels.put(Blocks.ORE_LAPIS_GRANITE, 1);
      miningLevels.put(Blocks.ORE_LAPIS_LIMESTONE, 1);
      miningLevels.put(Blocks.BLOCK_REDSTONE, 2);
      miningLevels.put(Blocks.ORE_REDSTONE_STONE, 2);
      miningLevels.put(Blocks.ORE_REDSTONE_BASALT, 2);
      miningLevels.put(Blocks.ORE_REDSTONE_GRANITE, 2);
      miningLevels.put(Blocks.ORE_REDSTONE_LIMESTONE, 2);
      miningLevels.put(Blocks.ORE_REDSTONE_GLOWING_STONE, 2);
      miningLevels.put(Blocks.ORE_REDSTONE_GLOWING_BASALT, 2);
      miningLevels.put(Blocks.ORE_REDSTONE_GLOWING_GRANITE, 2);
      miningLevels.put(Blocks.ORE_REDSTONE_GLOWING_LIMESTONE, 2);
   }
}
