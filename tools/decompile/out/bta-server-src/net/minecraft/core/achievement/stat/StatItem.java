package net.minecraft.core.achievement.stat;

import net.minecraft.core.item.Item;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.collection.NamespaceID;

public class StatItem extends Stat {
   private final int itemID;

   public StatItem(NamespaceID id, String key, int itemId) {
      super(id, key);
      this.itemID = itemId;
   }

   public int getItemID() {
      return this.itemID;
   }

   public Item getItem() {
      return Item.getItem(this.itemID);
   }

   @Override
   public String getStatName() {
      return I18n.getInstance().translateKeyAndFormat(this.statKey, Item.itemsList[this.itemID].getStatName());
   }
}
