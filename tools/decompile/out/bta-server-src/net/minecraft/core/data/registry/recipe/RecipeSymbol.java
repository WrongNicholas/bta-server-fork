package net.minecraft.core.data.registry.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.item.ItemStack;

public class RecipeSymbol {
   private char symbol;
   private ItemStack stack;
   private String itemGroup;
   private int amount = 1;
   private List<ItemStack> override;

   public RecipeSymbol(char symbol, ItemStack stack, String itemGroup) {
      if (stack != null || itemGroup != null && !Objects.equals(itemGroup, "")) {
         if (Registries.ITEM_GROUPS.getItem(itemGroup) == null) {
            throw new NullPointerException("Item group '" + itemGroup + "' not found!");
         } else {
            this.symbol = symbol;
            this.stack = stack;
            this.itemGroup = itemGroup;
         }
      } else {
         throw new NullPointerException("Null symbol!");
      }
   }

   public RecipeSymbol(char symbol, ItemStack stack) {
      if (stack == null) {
         throw new NullPointerException("Null symbol!");
      } else {
         this.symbol = symbol;
         this.stack = stack;
      }
   }

   public RecipeSymbol(char symbol, ItemStack stack, int amount) {
      if (stack == null) {
         throw new NullPointerException("Null symbol!");
      } else {
         this.symbol = symbol;
         this.stack = stack;
         this.amount = amount;
      }
   }

   public RecipeSymbol(char symbol, ItemStack stack, String itemGroup, int amount) {
      if (stack != null || itemGroup != null && !Objects.equals(itemGroup, "")) {
         if (itemGroup != null && !Objects.equals(itemGroup, "") && Registries.ITEM_GROUPS.getItem(itemGroup) == null) {
            throw new NullPointerException("Item group '" + itemGroup + "' not found!");
         } else {
            this.symbol = symbol;
            this.stack = stack;
            this.itemGroup = itemGroup;
            this.amount = amount;
         }
      } else {
         throw new NullPointerException("Null symbol!");
      }
   }

   public RecipeSymbol(ItemStack stack, String itemGroup) {
      if (stack != null || itemGroup != null && !Objects.equals(itemGroup, "")) {
         if (itemGroup != null && !Objects.equals(itemGroup, "") && Registries.ITEM_GROUPS.getItem(itemGroup) == null) {
            throw new NullPointerException("Item group '" + itemGroup + "' not found!");
         } else {
            this.stack = stack;
            this.itemGroup = itemGroup;
         }
      } else {
         throw new NullPointerException("Null symbol!");
      }
   }

   public RecipeSymbol(ItemStack stack, String itemGroup, int amount) {
      if (stack != null || itemGroup != null && !Objects.equals(itemGroup, "")) {
         if (itemGroup != null && !Objects.equals(itemGroup, "") && Registries.ITEM_GROUPS.getItem(itemGroup) == null) {
            throw new NullPointerException("Item group '" + itemGroup + "' not found!");
         } else {
            this.stack = stack;
            this.itemGroup = itemGroup;
            this.amount = amount;
         }
      } else {
         throw new NullPointerException("Null symbol!");
      }
   }

   public RecipeSymbol(ItemStack stack) {
      if (stack == null) {
         throw new NullPointerException("Null symbol!");
      } else {
         this.stack = stack;
      }
   }

   public RecipeSymbol(ItemStack stack, int amount) {
      if (stack == null) {
         throw new NullPointerException("Null symbol!");
      } else {
         this.stack = stack;
         this.amount = amount;
      }
   }

   public RecipeSymbol(String itemGroup) {
      if (itemGroup == null || Objects.equals(itemGroup, "")) {
         throw new NullPointerException("Null symbol!");
      } else if (Registries.ITEM_GROUPS.getItem(itemGroup) == null) {
         throw new NullPointerException("Item group '" + itemGroup + "' not found!");
      } else {
         this.itemGroup = itemGroup;
      }
   }

   public RecipeSymbol(String itemGroup, int amount) {
      if (itemGroup == null || Objects.equals(itemGroup, "")) {
         throw new NullPointerException("Null symbol!");
      } else if (Registries.ITEM_GROUPS.getItem(itemGroup) == null) {
         throw new NullPointerException("Item group '" + itemGroup + "' not found!");
      } else {
         this.itemGroup = itemGroup;
         this.amount = amount;
      }
   }

   public RecipeSymbol(List<ItemStack> override) {
      if (override != null && !override.isEmpty()) {
         this.override = override;
         this.stack = override.get(0);
      } else {
         throw new NullPointerException("Null symbol!");
      }
   }

   public RecipeSymbol(List<ItemStack> override, int amount) {
      if (override != null && !override.isEmpty()) {
         this.override = override;
         this.stack = override.get(0);
         this.amount = amount;
      } else {
         throw new NullPointerException("Null symbol!");
      }
   }

   public List<ItemStack> resolve() {
      if (this.override != null) {
         return this.override.stream().peek(S -> {
            ItemStack var10000 = S.copy();
            var10000.stackSize = var10000.stackSize * this.amount;
         }).collect(Collectors.toList());
      } else if (this.stack != null && this.itemGroup == null) {
         ItemStack s = this.stack.copy();
         s.stackSize = s.stackSize * this.amount;
         return Collections.singletonList(s);
      } else if (this.itemGroup != null && this.stack == null) {
         return Registries.ITEM_GROUPS.getItem(this.itemGroup).stream().peek(S -> {
            ItemStack var10000 = S.copy();
            var10000.stackSize = var10000.stackSize * this.amount;
         }).collect(Collectors.toList());
      } else if (this.itemGroup != null) {
         List<ItemStack> list = Registries.ITEM_GROUPS.getItem(this.itemGroup).stream().peek(S -> {
            ItemStack var10000 = S.copy();
            var10000.stackSize = var10000.stackSize * this.amount;
         }).collect(Collectors.toList());
         ItemStack s = this.stack.copy();
         s.stackSize = s.stackSize * this.amount;
         list.add(s);
         return list;
      } else {
         return null;
      }
   }

   public boolean matches(ItemStack stack) {
      if (stack == null) {
         return false;
      } else {
         List<ItemStack> stacks = this.resolve();
         boolean found = false;

         for (ItemStack resolvedStack : stacks) {
            boolean foundId = resolvedStack.itemID == stack.itemID;
            boolean foundMeta = resolvedStack.getMetadata() == -1 || resolvedStack.getMetadata() == stack.getMetadata();
            if (foundId && foundMeta) {
               found = true;
               break;
            }
         }

         return found;
      }
   }

   public char getSymbol() {
      return this.symbol;
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public String getItemGroup() {
      return this.itemGroup;
   }

   public int getAmount() {
      return this.amount;
   }

   public RecipeSymbol copy() {
      return new RecipeSymbol(this.symbol, this.stack, this.itemGroup, this.amount);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         RecipeSymbol that = (RecipeSymbol)o;
         if (this.getSymbol() != that.getSymbol()) {
            return false;
         } else if (this.getAmount() != that.getAmount()) {
            return false;
         } else if (this.getStack() != null ? this.getStack().equals(that.getStack()) : that.getStack() == null) {
            return (this.getItemGroup() != null ? this.getItemGroup().equals(that.getItemGroup()) : that.getItemGroup() == null)
               ? Objects.equals(this.override, that.override)
               : false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      if (this.stack != null && this.itemGroup == null) {
         return "symbol: " + this.amount + "x " + this.stack;
      } else if (this.itemGroup != null && this.stack == null) {
         return "symbol: " + this.amount + "x " + this.itemGroup;
      } else {
         return this.itemGroup != null ? "symbol: " + this.amount + "x " + this.stack + " && " + this.itemGroup : "null symbol";
      }
   }
}
