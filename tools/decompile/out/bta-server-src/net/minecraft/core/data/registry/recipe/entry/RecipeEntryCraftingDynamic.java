package net.minecraft.core.data.registry.recipe.entry;

public abstract class RecipeEntryCraftingDynamic extends RecipeEntryCrafting<Void, Void> {
   public RecipeEntryCraftingDynamic() {
      super(null, null);
   }

   public Void getInput() {
      return null;
   }

   public Void getOutput() {
      return null;
   }

   public boolean containsInput(Void input) {
      return false;
   }

   public boolean containsOutput(Void output) {
      return false;
   }
}
