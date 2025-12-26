package net.minecraft.core.player.inventory.slot;

import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tool.ItemToolHoe;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import net.minecraft.core.item.tool.ItemToolSword;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public class SlotResult extends Slot {
   private final Container craftSlots;
   private Player thePlayer;

   public SlotResult(Player player, Container craftSlots, Container container, int index, int x, int y) {
      super(container, index, x, y);
      this.thePlayer = player;
      this.craftSlots = craftSlots;
   }

   @Override
   public boolean mayPlace(ItemStack itemstack) {
      return false;
   }

   @Override
   public void onTake(ItemStack itemStack) {
      itemStack.onCrafting(this.thePlayer.world, this.thePlayer);
      Item item = itemStack.getItem();
      if (item.id == Blocks.WORKBENCH.id()) {
         this.thePlayer.addStat(Achievements.BUILD_WORKBENCH, 1);
      }

      if (item.id == Blocks.FURNACE_STONE_IDLE.id()) {
         this.thePlayer.addStat(Achievements.BUILD_FURNACE, 1);
      }

      if (item.id == Items.FOOD_BREAD.id) {
         this.thePlayer.addStat(Achievements.MAKE_BREAD, 1);
      }

      if (item.id == Items.FOOD_CAKE.id) {
         this.thePlayer.addStat(Achievements.BAKE_CAKE, 1);
      }

      if (item.id == Items.BUCKET_ICECREAM.id) {
         this.thePlayer.addStat(Achievements.CRAFT_ICECREAM, 1);
      }

      if (item.id == Items.FOOD_PUMPKIN_PIE.id) {
         this.thePlayer.addStat(Achievements.CRAFT_PUMPKIN_PIE, 1);
      }

      if (item.id == Items.HANDCANNON_UNLOADED.id) {
         this.thePlayer.addStat(Achievements.CRAFT_HANDCANNON, 1);
      }

      if (item instanceof ItemToolHoe) {
         this.thePlayer.addStat(Achievements.BUILD_HOE, 1);
      }

      if (item instanceof ItemToolSword) {
         this.thePlayer.addStat(Achievements.BUILD_SWORD, 1);
      }

      if (item instanceof ItemToolPickaxe) {
         ItemToolPickaxe itemToolPickaxe = (ItemToolPickaxe)item;
         if (itemToolPickaxe.getMaterial().getMiningLevel() > 0) {
            this.thePlayer.addStat(Achievements.BUILD_BETTER_PICKAXE, 1);
         }

         this.thePlayer.addStat(Achievements.BUILD_PICKAXE, 1);
      }

      if (itemStack.itemID == Items.ARMOR_BOOTS_CHAINMAIL.id
         || itemStack.itemID == Items.ARMOR_HELMET_CHAINMAIL.id
         || itemStack.itemID == Items.ARMOR_CHESTPLATE_CHAINMAIL.id
         || itemStack.itemID == Items.ARMOR_LEGGINGS_CHAINMAIL.id) {
         this.thePlayer.addStat(Achievements.REPAIR_ARMOR, 1);
      }

      Registries.RECIPES.onCraftResult((ContainerCrafting)this.craftSlots);
   }

   @Override
   public boolean enableDragAndPickup() {
      return false;
   }

   @Override
   public boolean allowItemInteraction() {
      return false;
   }
}
