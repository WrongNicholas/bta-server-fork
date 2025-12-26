package net.minecraft.core.item.tool;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class ItemToolSword extends Item {
   private int weaponDamage;
   private ToolMaterial material;

   public ItemToolSword(String name, String namespaceId, int id, ToolMaterial enumtoolmaterial) {
      super(name, namespaceId, id);
      this.maxStackSize = 1;
      this.setMaxDamage(enumtoolmaterial.getDurability());
      this.weaponDamage = 4 + enumtoolmaterial.getDamage() * 2;
      this.material = enumtoolmaterial;
   }

   @Override
   public float getStrVsBlock(ItemStack itemstack, Block<?> block) {
      return block != Blocks.COBWEB ? 1.5F : 15.0F;
   }

   @Override
   public boolean hitEntity(ItemStack itemstack, Mob target, Mob attacker) {
      itemstack.damageItem(1, attacker);
      return true;
   }

   @Override
   public boolean onBlockDestroyed(World world, ItemStack itemstack, int i, int x, int y, int z, Side side, Mob mob) {
      Block<?> block = Blocks.blocksList[i];
      if (block != null && (block.getHardness() > 0.0F || this.isSilkTouch())) {
         itemstack.damageItem(2, mob);
      }

      return true;
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
      Block<?> b = world.getBlock(blockX, blockY, blockZ);
      if (b == Blocks.PUMPKIN) {
         world.setBlockAndMetadata(blockX, blockY, blockZ, Blocks.PUMPKIN_CARVED_IDLE.id(), direction.getOpposite().getId());
         itemStack.damageItem(1, null);
      }
   }

   @Override
   public int getDamageVsEntity(Entity entity, ItemStack is) {
      return this.weaponDamage;
   }

   @Override
   public boolean canHarvestBlock(Mob mob, ItemStack itemStack, Block<?> block) {
      return block.hasTag(BlockTags.MINEABLE_BY_SWORD);
   }

   @Override
   public boolean isSilkTouch() {
      return this.material.isSilkTouch();
   }
}
