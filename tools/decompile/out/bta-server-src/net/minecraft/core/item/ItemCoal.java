package net.minecraft.core.item;

public class ItemCoal extends Item {
   public ItemCoal(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
   }

   @Override
   public String getLanguageKey(ItemStack itemstack) {
      return itemstack.getMetadata() == 1 ? "item.charcoal" : "item.coal";
   }
}
