package net.minecraft.core.player.inventory.menu;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.slot.SlotCreative;

public class MenuInventoryCreative extends MenuInventory {
   public int page = 0;
   public int maxPage;
   protected int creativeSlotsStart;
   protected List<ItemStack> searchedItems = new ArrayList<>();
   public String searchText = "";
   public static List<ItemStack> creativeItems = new ArrayList<>();
   public static int creativeItemsCount;

   public MenuInventoryCreative(ContainerInventory inventoryplayer) {
      this(inventoryplayer, true);
   }

   public MenuInventoryCreative(ContainerInventory inventoryplayer, boolean isNotClientSide) {
      super(inventoryplayer, isNotClientSide);
      this.creativeSlotsStart = this.slots.size();

      for (int i = 0; i < 36; i++) {
         int x = i % 6;
         int y = i / 6;
         this.addSlot(new SlotCreative(this.creativeSlotsStart + i, 184 + x * 18, 30 + y * 18, creativeItems.get(i)));
      }

      this.searchPage("");
   }

   public void setInventoryStatus(int page, String searchText) {
      if (this.page != page) {
         this.page = page;
         this.updatePage();
      }

      if (!this.searchText.equals(searchText)) {
         this.searchText = searchText;
         this.searchPage(searchText);
      }
   }

   public void lastPage() {
      if (this.page != 0) {
         this.page--;
         this.updatePage();
      }
   }

   public void nextPage() {
      if (this.page != this.maxPage) {
         this.page++;
         this.updatePage();
      }
   }

   public void searchPage(String search) {
      this.searchText = search;
      this.searchedItems.clear();
      this.page = 0;
      I18n t = I18n.getInstance();

      for (int i = 0; i < creativeItemsCount; i++) {
         if (t.translateNameKey(creativeItems.get(i).getItemKey()).toLowerCase().contains(search.toLowerCase())) {
            this.searchedItems.add(creativeItems.get(i));
         }
      }

      this.updatePage();
   }

   protected void updatePage() {
      this.maxPage = this.searchedItems.size() / 36;
      if (this.searchedItems.size() % 36 == 0) {
         this.maxPage--;
      }

      if (this.maxPage == -1) {
         this.maxPage = 0;
      }

      for (int i = 0; i < 36; i++) {
         if (i + this.page * 36 >= this.searchedItems.size()) {
            ((SlotCreative)this.slots.get(this.creativeSlotsStart + i)).item = null;
         } else {
            ((SlotCreative)this.slots.get(this.creativeSlotsStart + i)).item = this.searchedItems.get(i + this.page * 36);
         }
      }

      this.inventory.player.updateCreativeInventory(this.page, this.searchText);
   }

   public String getSearchText() {
      return this.searchText;
   }

   @Override
   public int getHotbarSlotId(int number) {
      return number + 8 + 27;
   }

   static {
      int count = 0;

      for (int id = 0; id < Blocks.blocksList.length; id++) {
         if (Blocks.blocksList[id] != null && !Blocks.blocksList[id].hasTag(BlockTags.NOT_IN_CREATIVE_MENU)) {
            creativeItems.add(new ItemStack(Blocks.blocksList[id]));
            count++;
            if (id != Blocks.WOOL.id()
               && id != Blocks.PLANKS_OAK_PAINTED.id()
               && id != Blocks.LAMP_IDLE.id()
               && id != Blocks.LAMP_INVERTED_ACTIVE.id()
               && id != Blocks.FENCE_PLANKS_OAK_PAINTED.id()) {
               if (id == Blocks.CHEST_PLANKS_OAK_PAINTED.id()
                  || id == Blocks.SLAB_PLANKS_PAINTED.id()
                  || id == Blocks.FENCE_GATE_PLANKS_OAK_PAINTED.id()
                  || id == Blocks.STAIRS_PLANKS_PAINTED.id()
                  || id == Blocks.DOOR_PLANKS_PAINTED_BOTTOM.id()
                  || id == Blocks.DOOR_PLANKS_PAINTED_TOP.id()
                  || id == Blocks.TRAPDOOR_PLANKS_PAINTED.id()
                  || id == Blocks.PRESSURE_PLATE_PLANKS_OAK_PAINTED.id()
                  || id == Blocks.BUTTON_PLANKS_PAINTED.id()) {
                  for (int i = 16; i < 256; i += 16) {
                     creativeItems.add(new ItemStack(Blocks.blocksList[id], 1, i));
                     count++;
                  }
               }
            } else {
               for (int i = 1; i < 16; i++) {
                  creativeItems.add(new ItemStack(Blocks.blocksList[id], 1, i));
                  count++;
               }
            }
         }
      }

      for (int i = Blocks.blocksList.length; i < Item.itemsList.length; i++) {
         if (Item.itemsList[i] != null && !Item.itemsList[i].hasTag(ItemTags.NOT_IN_CREATIVE_MENU)) {
            if (i == Items.PAINTBRUSH.id) {
               creativeItems.add(new ItemStack(Item.itemsList[i], 1, Items.PAINTBRUSH.getMaxDamage()));
            } else {
               creativeItems.add(new ItemStack(Item.itemsList[i]));
            }

            count++;
            if (i == Items.COAL.id) {
               for (int j = 1; j < 2; j++) {
                  creativeItems.add(new ItemStack(Item.itemsList[i], 1, j));
                  count++;
               }
            }

            if (i == Items.DYE.id || i == Items.DOOR_OAK_PAINTED.id || i == Items.SIGN_PAINTED.id) {
               for (int j = 1; j < 16; j++) {
                  creativeItems.add(new ItemStack(Item.itemsList[i], 1, j));
                  count++;
               }
            }
         }
      }

      creativeItemsCount = count;
   }
}
