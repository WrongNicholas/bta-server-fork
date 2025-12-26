package net.minecraft.core.item.tool;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class ItemTool extends Item {
   private final Tag<Block<?>> tagEffectiveAgainst;
   private final int damageVsEntity;
   protected ToolMaterial material;

   protected ItemTool(String name, String namespaceId, int id, int damageDealt, ToolMaterial toolMaterial, Tag<Block<?>> tagEffectiveAgainst) {
      super(name, namespaceId, id);
      this.material = toolMaterial;
      this.tagEffectiveAgainst = tagEffectiveAgainst;
      this.withTags(ItemTags.PREVENT_LEFT_CLICK_INTERACTIONS);
      this.maxStackSize = 1;
      this.setMaxDamage(toolMaterial.getDurability());
      this.damageVsEntity = damageDealt + toolMaterial.getDamage();
   }

   @Override
   public float getStrVsBlock(ItemStack itemstack, Block<?> block) {
      return block.hasTag(this.tagEffectiveAgainst) ? this.material.getEfficiency(false) : 1.0F;
   }

   @Override
   public boolean hitEntity(ItemStack itemstack, Mob target, Mob attacker) {
      itemstack.damageItem(2, attacker);
      return true;
   }

   @Override
   public boolean onBlockDestroyed(World world, ItemStack itemstack, int i, int x, int y, int z, Side side, Mob mob) {
      Block<?> block = Blocks.blocksList[i];
      if (block != null && (block.getHardness() > 0.0F || this.isSilkTouch())) {
         itemstack.damageItem(1, mob);
      }

      return true;
   }

   @Override
   public int getDamageVsEntity(Entity entity, ItemStack is) {
      return this.damageVsEntity;
   }

   @Override
   public boolean isSilkTouch() {
      return this.material.isSilkTouch();
   }

   public ToolMaterial getMaterial() {
      return this.material;
   }
}
