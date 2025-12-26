package net.minecraft.core.data.registry.recipe.entry;

import java.util.List;
import java.util.Objects;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.HasJsonAdapter;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.SearchQuery;
import net.minecraft.core.data.registry.recipe.adapter.RecipeBlastFurnaceJsonAdapter;
import net.minecraft.core.data.registry.recipe.adapter.RecipeJsonAdapter;
import net.minecraft.core.item.ItemStack;

public class RecipeEntryBlastFurnace extends RecipeEntryBase<RecipeSymbol, ItemStack, Void> implements HasJsonAdapter {
   public RecipeEntryBlastFurnace(RecipeSymbol input, ItemStack output) {
      super(input, output, null);
   }

   public RecipeEntryBlastFurnace() {
   }

   public Void getData() {
      return null;
   }

   public boolean containsData(Void data) {
      return false;
   }

   public boolean matches(ItemStack stack) {
      return this.getInput().matches(stack);
   }

   public boolean matchesQueryIgnoreExceptions(SearchQuery query) {
      try {
         return this.matchesQuery(query);
      } catch (IllegalArgumentException | NullPointerException var3) {
         return false;
      }
   }

   public boolean matchesQuery(SearchQuery query) {
      switch (query.mode) {
         case ALL:
            if ((this.matchesRecipe(query) || this.matchesUsage(query)) && this.matchesScope(query)) {
               return true;
            }
            break;
         case RECIPE:
            if (this.matchesRecipe(query) && this.matchesScope(query)) {
               return true;
            }
            break;
         case USAGE:
            if (this.matchesUsage(query) && this.matchesScope(query)) {
               return true;
            }
      }

      return false;
   }

   private boolean matchesRecipe(SearchQuery query) {
      if (query.query.getLeft() == SearchQuery.QueryType.NAME) {
         if (query.strict && this.getOutput().getDisplayName().equalsIgnoreCase(query.query.getRight())) {
            return true;
         }

         if (!query.strict && this.getOutput().getDisplayName().toLowerCase().contains(query.query.getRight().toLowerCase())) {
            return true;
         }
      } else if (query.query.getLeft() == SearchQuery.QueryType.GROUP && !Objects.equals(query.query.getRight(), "")) {
         List<ItemStack> groupStacks = new RecipeSymbol(query.query.getRight()).resolve();
         if (groupStacks == null) {
            return false;
         }

         return groupStacks.contains(this.getOutput());
      }

      return false;
   }

   private boolean matchesUsage(SearchQuery query) {
      for (ItemStack stack : this.getInput().resolve()) {
         if (stack != null) {
            if (query.query.getLeft() == SearchQuery.QueryType.NAME) {
               if (query.strict && stack.getDisplayName().equalsIgnoreCase(query.query.getRight())) {
                  return true;
               }

               if (!query.strict && stack.getDisplayName().toLowerCase().contains(query.query.getRight().toLowerCase())) {
                  return true;
               }
            } else if (query.query.getLeft() == SearchQuery.QueryType.GROUP && !Objects.equals(query.query.getRight(), "")) {
               List<ItemStack> groupStacks = new RecipeSymbol(query.query.getRight()).resolve();
               if (groupStacks == null) {
                  return false;
               }

               return groupStacks.contains(this.getOutput());
            }
         }
      }

      return false;
   }

   private boolean matchesScope(SearchQuery query) {
      if (query.scope.getLeft() == SearchQuery.SearchScope.NONE) {
         return true;
      } else {
         if (query.scope.getLeft() == SearchQuery.SearchScope.NAMESPACE) {
            RecipeNamespace namespace = Registries.RECIPES.getItem(query.scope.getRight());
            if (namespace == this.parent.getParent()) {
               return true;
            }
         } else if (query.scope.getLeft() == SearchQuery.SearchScope.NAMESPACE_GROUP) {
            RecipeGroup group;
            try {
               group = Registries.RECIPES.getGroupFromKey(query.scope.getRight());
            } catch (IllegalArgumentException var4) {
               group = null;
            }

            if (group == this.parent) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public RecipeJsonAdapter<?> getAdapter() {
      return new RecipeBlastFurnaceJsonAdapter();
   }
}
