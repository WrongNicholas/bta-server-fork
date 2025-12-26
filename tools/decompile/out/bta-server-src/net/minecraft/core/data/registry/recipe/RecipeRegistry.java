package net.minecraft.core.data.registry.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.Registry;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShaped;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShapeless;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryFurnace;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryLabel;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryLabelDye;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryMapDuplication;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerCrafting;

public class RecipeRegistry extends Registry<RecipeNamespace> {
   public final RecipeNamespace MINECRAFT = new RecipeNamespace();
   public final RecipeGroup<RecipeEntryCrafting<?, ?>> WORKBENCH = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Blocks.WORKBENCH)));
   public final RecipeGroup<RecipeEntryFurnace> FURNACE = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Blocks.FURNACE_STONE_ACTIVE)));
   public final RecipeGroup<RecipeEntryBlastFurnace> BLAST_FURNACE = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Blocks.FURNACE_BLAST_ACTIVE)));
   public final RecipeGroup<RecipeEntryTrommel> TROMMEL = new RecipeGroup<>(new RecipeSymbol(new ItemStack(Blocks.TROMMEL_ACTIVE)));
   private ArrayList<RecipeEntryCrafting<?, ?>> recipeCache = null;

   public RecipeRegistry() {
      if (Registries.RECIPES == null) {
         Registries.RECIPES = this;
      }

      this.MINECRAFT.register("workbench", this.WORKBENCH);
      this.MINECRAFT.register("furnace", this.FURNACE);
      this.MINECRAFT.register("blast_furnace", this.BLAST_FURNACE);
      this.MINECRAFT.register("trommel", this.TROMMEL);
      this.register("minecraft", this.MINECRAFT);
      this.addProceduralRecipes();
   }

   public void addProceduralRecipes() {
      this.WORKBENCH.register("label", new RecipeEntryLabel());
      this.WORKBENCH.register("label_dye", new RecipeEntryLabelDye());
      this.addCustomRecipe("minecraft:workbench/map_duplication", new RecipeEntryMapDuplication());
   }

   public void register(String key, RecipeNamespace item) {
      if (!key.contains(":") && !key.contains("/")) {
         super.register(key, item);
      } else {
         throw new IllegalArgumentException("Keys cannot contain ':' or '/'!");
      }
   }

   public List<RecipeEntryBase<?, ?, ?>> getAllRecipes() {
      ArrayList<RecipeEntryBase<?, ?, ?>> recipes = new ArrayList<>();

      for (RecipeNamespace recipeNamespace : this) {
         for (RecipeGroup<? extends RecipeEntryBase<?, ?, ?>> recipeGroup : recipeNamespace) {
            for (RecipeEntryBase<?, ?, ?> recipeEntry : recipeGroup) {
               recipes.add(recipeEntry);
            }
         }
      }

      return Collections.unmodifiableList(recipes);
   }

   public List<RecipeEntryBase<?, ?, ?>> getAllSerializableRecipes() {
      ArrayList<RecipeEntryBase<?, ?, ?>> recipes = new ArrayList<>();

      for (RecipeNamespace recipeNamespace : this) {
         for (RecipeGroup<? extends RecipeEntryBase<?, ?, ?>> recipeGroup : recipeNamespace) {
            for (RecipeEntryBase<?, ?, ?> recipeEntry : recipeGroup) {
               if (recipeEntry instanceof HasJsonAdapter) {
                  recipes.add(recipeEntry);
               }
            }
         }
      }

      return Collections.unmodifiableList(recipes);
   }

   public List<RecipeGroup<?>> getAllGroups() {
      ArrayList<RecipeGroup<?>> recipes = new ArrayList<>();

      for (RecipeNamespace recipeNamespace : this) {
         for (RecipeGroup<?> recipeGroup : recipeNamespace) {
            recipes.add(recipeGroup);
         }
      }

      return Collections.unmodifiableList(recipes);
   }

   public List<RecipeEntryCrafting<?, ?>> getAllCraftingRecipes() {
      if (this.recipeCache != null) {
         return Collections.unmodifiableList(this.recipeCache);
      } else {
         ArrayList<RecipeEntryCrafting<?, ?>> recipes = new ArrayList<>();

         for (RecipeNamespace recipeNamespace : this) {
            for (RecipeGroup<? extends RecipeEntryBase<?, ?, ?>> recipeGroup : recipeNamespace) {
               for (RecipeEntryBase<?, ?, ?> recipeEntry : recipeGroup) {
                  if (recipeEntry instanceof RecipeEntryCrafting) {
                     recipes.add((RecipeEntryCrafting<?, ?>)recipeEntry);
                  }
               }
            }
         }

         this.recipeCache = recipes;
         return Collections.unmodifiableList(recipes);
      }
   }

   public void invalidateCaches() {
      this.recipeCache = null;
   }

   public List<RecipeEntryFurnace> getAllFurnaceRecipes() {
      ArrayList<RecipeEntryFurnace> recipes = new ArrayList<>();

      for (RecipeNamespace recipeNamespace : this) {
         for (RecipeGroup<? extends RecipeEntryBase<?, ?, ?>> recipeGroup : recipeNamespace) {
            for (RecipeEntryBase<?, ?, ?> recipeEntry : recipeGroup) {
               if (recipeEntry instanceof RecipeEntryFurnace) {
                  recipes.add((RecipeEntryFurnace)recipeEntry);
               }
            }
         }
      }

      return Collections.unmodifiableList(recipes);
   }

   public List<RecipeEntryBlastFurnace> getAllBlastFurnaceRecipes() {
      ArrayList<RecipeEntryBlastFurnace> recipes = new ArrayList<>();

      for (RecipeNamespace recipeNamespace : this) {
         for (RecipeGroup<? extends RecipeEntryBase<?, ?, ?>> recipeGroup : recipeNamespace) {
            for (RecipeEntryBase<?, ?, ?> recipeEntry : recipeGroup) {
               if (recipeEntry instanceof RecipeEntryBlastFurnace) {
                  recipes.add((RecipeEntryBlastFurnace)recipeEntry);
               }
            }
         }
      }

      return Collections.unmodifiableList(recipes);
   }

   public List<RecipeEntryTrommel> getAllTrommelRecipes() {
      ArrayList<RecipeEntryTrommel> recipes = new ArrayList<>();

      for (RecipeNamespace recipeNamespace : this) {
         for (RecipeGroup<? extends RecipeEntryBase<?, ?, ?>> recipeGroup : recipeNamespace) {
            for (RecipeEntryBase<?, ?, ?> recipeEntry : recipeGroup) {
               if (recipeEntry instanceof RecipeEntryTrommel) {
                  recipes.add((RecipeEntryTrommel)recipeEntry);
               }
            }
         }
      }

      return Collections.unmodifiableList(recipes);
   }

   public ItemStack findMatchingRecipe(ContainerCrafting inventorycrafting) {
      for (int i = 0; i < this.getAllCraftingRecipes().size(); i++) {
         RecipeEntryCrafting<?, ?> recipe = this.getAllCraftingRecipes().get(i);
         if (recipe.matches(inventorycrafting)) {
            return recipe.getCraftingResult(inventorycrafting);
         }
      }

      return null;
   }

   public ItemStack[] onCraftResult(ContainerCrafting inventorycrafting) {
      for (int i = 0; i < this.getAllCraftingRecipes().size(); i++) {
         RecipeEntryCrafting<?, ?> recipe = this.getAllCraftingRecipes().get(i);
         if (recipe.matches(inventorycrafting)) {
            return recipe.onCraftResult(inventorycrafting);
         }
      }

      return null;
   }

   public void addRecipe(String recipeKey, ItemStack itemstack, boolean consumeContainerItem, Object... aobj) {
      this.invalidateCaches();
      StringBuilder s = new StringBuilder();
      int i = 0;
      int j = 0;
      int k = 0;
      if (aobj[i] instanceof String[]) {
         String[] as = (String[])aobj[i++];

         for (String s2 : as) {
            k++;
            j = s2.length();
            s.append(s2);
         }
      } else {
         while (aobj[i] instanceof String) {
            String s1 = (String)aobj[i++];
            k++;
            j = s1.length();
            s = new StringBuilder(s + s1);
         }
      }

      HashMap<Character, RecipeSymbol> map;
      for (map = new HashMap<>(); i < aobj.length; i += 2) {
         Character character = (Character)aobj[i];
         RecipeSymbol recipeSymbol = null;
         if (aobj[i + 1] instanceof Item) {
            recipeSymbol = new RecipeSymbol(character, new ItemStack((Item)aobj[i + 1]), null);
         } else if (aobj[i + 1] instanceof Block) {
            recipeSymbol = new RecipeSymbol(character, new ItemStack((Block<?>)aobj[i + 1]), null);
         } else if (aobj[i + 1] instanceof ItemStack) {
            recipeSymbol = new RecipeSymbol(character, (ItemStack)aobj[i + 1], null);
         }

         if (aobj[i + 1] instanceof String) {
            recipeSymbol = new RecipeSymbol(character, null, (String)aobj[i + 1]);
         }

         if (aobj[i + 1] instanceof RecipeSymbol) {
            recipeSymbol = (RecipeSymbol)aobj[i + 1];
         }

         map.put(character, recipeSymbol);
      }

      RecipeSymbol[] symbols = new RecipeSymbol[j * k];

      for (int i1 = 0; i1 < j * k; i1++) {
         char c = s.charAt(i1);
         if (map.containsKey(c)) {
            symbols[i1] = map.get(c).copy();
         } else {
            symbols[i1] = null;
         }
      }

      RecipeEntryCraftingShaped recipe = new RecipeEntryCraftingShaped(j, k, symbols, itemstack, consumeContainerItem);
      RecipeGroup<RecipeEntryBase<?, ?, ?>> group = this.getGroupFromKey(recipeKey);
      String key = this.deconstructKey(recipeKey)[2];
      group.register(key, recipe);
   }

   public static RecipeEntryCraftingShaped parseRecipe(ItemStack itemstack, boolean consumeContainerItem, boolean allowMirror, Object... aobj) {
      StringBuilder s = new StringBuilder();
      int i = 0;
      int w = 0;
      int h = 0;
      if (aobj[i] instanceof String[]) {
         String[] as = (String[])aobj[i++];

         for (String s2 : as) {
            h++;
            w = s2.length();
            s.append(s2);
         }
      } else {
         while (aobj[i] instanceof String) {
            String s1 = (String)aobj[i++];
            h++;
            w = s1.length();
            s = new StringBuilder(s + s1);
         }
      }

      HashMap<Character, RecipeSymbol> map;
      for (map = new HashMap<>(); i < aobj.length; i += 2) {
         Character character = (Character)aobj[i];
         RecipeSymbol recipeSymbol = null;
         if (aobj[i + 1] instanceof Item) {
            recipeSymbol = new RecipeSymbol(character, new ItemStack((Item)aobj[i + 1]), null);
         } else if (aobj[i + 1] instanceof Block) {
            recipeSymbol = new RecipeSymbol(character, new ItemStack((Block<?>)aobj[i + 1]), null);
         } else if (aobj[i + 1] instanceof ItemStack) {
            recipeSymbol = new RecipeSymbol(character, (ItemStack)aobj[i + 1], null);
         }

         if (aobj[i + 1] instanceof String) {
            recipeSymbol = new RecipeSymbol(character, null, (String)aobj[i + 1]);
         }

         if (aobj[i + 1] instanceof RecipeSymbol) {
            recipeSymbol = (RecipeSymbol)aobj[i + 1];
         }

         map.put(character, recipeSymbol);
      }

      RecipeSymbol[] symbols = new RecipeSymbol[w * h];

      for (int i1 = 0; i1 < w * h; i1++) {
         char c = s.charAt(i1);
         if (map.containsKey(c)) {
            symbols[i1] = map.get(c).copy();
         } else {
            symbols[i1] = null;
         }
      }

      return new RecipeEntryCraftingShaped(w, h, symbols, itemstack, consumeContainerItem, allowMirror);
   }

   public static RecipeEntryCraftingShaped parseRecipe(
      List<String> pattern, List<RecipeSymbol> symbols, ItemStack result, boolean consumeContainerItem, boolean allowMirror
   ) {
      Set<Character> chars = new HashSet<>();

      for (String s : pattern) {
         char[] chars1 = s.toCharArray();

         for (char c : chars1) {
            chars.add(c);
         }
      }

      ArrayList<Object> objs = new ArrayList<>();

      for (Character c : chars) {
         for (RecipeSymbol symbol : symbols) {
            if (symbol.getSymbol() == c) {
               objs.add(c);
               objs.add(symbol);
               break;
            }
         }
      }

      objs.add(0, pattern.toArray(new String[0]));
      return parseRecipe(result, consumeContainerItem, allowMirror, objs.toArray());
   }

   public void addShapelessRecipe(String recipeKey, ItemStack result, Object... ingredients) {
      this.invalidateCaches();
      List<RecipeSymbol> list = new ArrayList<>();

      for (int i = 0; i < ingredients.length; i++) {
         Object obj = ingredients[i];
         if (obj instanceof ItemStack) {
            list.add(new RecipeSymbol('\u0000', (ItemStack)obj, null));
         } else if (obj instanceof Item) {
            list.add(new RecipeSymbol('\u0000', new ItemStack((Item)obj), null));
         } else if (obj instanceof Block) {
            list.add(new RecipeSymbol('\u0000', new ItemStack((Block<?>)obj), null));
         } else {
            if (!(obj instanceof String)) {
               throw new RuntimeException("Invalid object " + obj + " at index " + i);
            }

            list.add(new RecipeSymbol('\u0000', null, (String)obj));
         }
      }

      RecipeEntryCraftingShapeless recipe = new RecipeEntryCraftingShapeless(list, result);
      RecipeGroup<RecipeEntryBase<?, ?, ?>> group = this.getGroupFromKey(recipeKey);
      String key = this.deconstructKey(recipeKey)[2];
      group.register(key, recipe);
   }

   public void addFurnaceRecipe(String recipeKey, ItemStack result, Object input) {
      this.invalidateCaches();
      RecipeSymbol symbol;
      if (input instanceof ItemStack) {
         symbol = new RecipeSymbol('\u0000', (ItemStack)input, null);
      } else if (input instanceof Item) {
         symbol = new RecipeSymbol('\u0000', new ItemStack((Item)input), null);
      } else if (input instanceof Block) {
         symbol = new RecipeSymbol('\u0000', new ItemStack((Block<?>)input), null);
      } else {
         if (!(input instanceof String)) {
            throw new RuntimeException("Invalid object " + input + "!");
         }

         symbol = new RecipeSymbol('\u0000', null, (String)input);
      }

      RecipeEntryFurnace recipe = new RecipeEntryFurnace(symbol, result);
      RecipeGroup<RecipeEntryBase<?, ?, ?>> group = this.getGroupFromKey(recipeKey);
      String key = this.deconstructKey(recipeKey)[2];
      group.register(key, recipe);
   }

   public void addBlastFurnaceRecipe(String recipeKey, ItemStack result, Object input) {
      this.invalidateCaches();
      RecipeSymbol symbol;
      if (input instanceof ItemStack) {
         symbol = new RecipeSymbol('\u0000', (ItemStack)input, null);
      } else if (input instanceof Item) {
         symbol = new RecipeSymbol('\u0000', new ItemStack((Item)input), null);
      } else if (input instanceof Block) {
         symbol = new RecipeSymbol('\u0000', new ItemStack((Block<?>)input), null);
      } else {
         if (!(input instanceof String)) {
            throw new RuntimeException("Invalid object " + input + "!");
         }

         symbol = new RecipeSymbol('\u0000', null, (String)input);
      }

      RecipeEntryBlastFurnace recipe = new RecipeEntryBlastFurnace(symbol, result);
      RecipeGroup<RecipeEntryBase<?, ?, ?>> group = this.getGroupFromKey(recipeKey);
      String key = this.deconstructKey(recipeKey)[2];
      group.register(key, recipe);
   }

   public void addTrommelRecipe(String recipeKey, WeightedRandomBag<WeightedRandomLootObject> result, Object input) {
      this.invalidateCaches();
      RecipeSymbol symbol;
      if (input instanceof ItemStack) {
         symbol = new RecipeSymbol('\u0000', (ItemStack)input, null);
      } else if (input instanceof Item) {
         symbol = new RecipeSymbol('\u0000', new ItemStack((Item)input), null);
      } else if (input instanceof Block) {
         symbol = new RecipeSymbol('\u0000', new ItemStack((Block<?>)input), null);
      } else {
         if (!(input instanceof String)) {
            throw new RuntimeException("Invalid object " + input + "!");
         }

         symbol = new RecipeSymbol('\u0000', null, (String)input);
      }

      RecipeEntryTrommel recipe = new RecipeEntryTrommel(symbol, result);
      RecipeGroup<RecipeEntryBase<?, ?, ?>> group = this.getGroupFromKey(recipeKey);
      String key = this.deconstructKey(recipeKey)[2];
      group.register(key, recipe);
   }

   public void addCustomRecipe(String recipeKey, RecipeEntryBase<?, ?, ?> recipe) {
      this.invalidateCaches();
      RecipeGroup<RecipeEntryBase<?, ?, ?>> group = this.getGroupFromKey(recipeKey);
      String key = this.deconstructKey(recipeKey)[2];
      group.register(key, recipe);
   }

   public <T extends RecipeEntryBase<?, ?, ?>> RecipeBranch<T> getRecipeFromKey(String key) {
      if (key.contains(":") && key.contains("/")) {
         String[] keys = this.deconstructKey(key);
         String namespaceKey = keys[0];
         String groupKey = keys[1];
         String recipeKey = keys[2];
         RecipeNamespace namespace = this.getItem(namespaceKey);
         if (namespace == null) {
            throw new IllegalArgumentException(String.format("Namespace '%s' doesn't exist!", namespaceKey));
         } else {
            RecipeGroup<? extends RecipeEntryBase<?, ?, ?>> group = namespace.getItem(groupKey);
            if (group == null) {
               throw new IllegalArgumentException(String.format("Group '%s' in namespace '%s' doesn't exist!", groupKey, namespaceKey));
            } else {
               T recipe = (T)group.getItem(recipeKey);
               if (recipe == null) {
                  throw new IllegalArgumentException(
                     String.format("Recipe '%s' in group '%s' in namespace '%s' doesn't exist!", recipeKey, groupKey, namespaceKey)
                  );
               } else {
                  return new RecipeBranch<>(namespace, group, recipe);
               }
            }
         }
      } else {
         throw new IllegalArgumentException("Invalid or malformed key!");
      }
   }

   public String[] deconstructKey(String key) {
      String namespaceKey = key.split(":")[0];
      String groupKey = key.split(":")[1].split("/")[0];
      String recipeKey = "";
      if (key.contains("/")) {
         recipeKey = key.split(":")[1].split("/")[1];
      }

      return new String[]{namespaceKey, groupKey, recipeKey};
   }

   public <T extends RecipeEntryBase<?, ?, ?>> RecipeGroup<T> getGroupFromKey(String key) {
      if (!key.contains(":")) {
         throw new IllegalArgumentException("Invalid or malformed key!");
      } else {
         String[] keys;
         try {
            keys = this.deconstructKey(key);
         } catch (ArrayIndexOutOfBoundsException var7) {
            throw new IllegalArgumentException("Invalid or malformed key!", var7);
         }

         String namespaceKey = keys[0];
         String groupKey = keys[1];
         RecipeNamespace namespace = this.getItem(namespaceKey);
         if (namespace == null) {
            throw new IllegalArgumentException(String.format("Namespace '%s' doesn't exist!", namespaceKey));
         } else {
            RecipeGroup<T> group = (RecipeGroup<T>)namespace.getItem(groupKey);
            if (group == null) {
               throw new IllegalArgumentException(String.format("Group '%s' in namespace '%s' doesn't exist!", groupKey, namespaceKey));
            } else {
               return group;
            }
         }
      }
   }
}
