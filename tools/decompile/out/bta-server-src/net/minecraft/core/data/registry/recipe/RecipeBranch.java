package net.minecraft.core.data.registry.recipe;

public class RecipeBranch<T extends RecipeEntryBase<?, ?, ?>> {
   public final RecipeNamespace namespace;
   public final RecipeGroup group;
   public final T recipe;

   public RecipeBranch(RecipeNamespace namespace, RecipeGroup group, T recipe) {
      this.namespace = namespace;
      this.group = group;
      this.recipe = recipe;
   }
}
