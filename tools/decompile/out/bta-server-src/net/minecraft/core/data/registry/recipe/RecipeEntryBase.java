package net.minecraft.core.data.registry.recipe;

import java.util.Objects;

public abstract class RecipeEntryBase<I, O, D> {
   private final I input;
   private final O output;
   private final D data;
   public RecipeGroup parent;

   public RecipeEntryBase(I input, O output, D data) {
      this.input = input;
      this.output = output;
      this.data = data;
   }

   public RecipeEntryBase() {
      this.input = null;
      this.output = null;
      this.data = null;
   }

   public I getInput() {
      return this.input;
   }

   public O getOutput() {
      return this.output;
   }

   public D getData() {
      return this.data;
   }

   public boolean containsInput(I input) {
      return input.equals(this.input);
   }

   public boolean containsOutput(O output) {
      return output.equals(this.output);
   }

   public boolean containsData(D data) {
      return data == this.data;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         RecipeEntryBase<?, ?, ?> that = (RecipeEntryBase<?, ?, ?>)o;
         if (this.getInput() != null ? this.getInput().equals(that.getInput()) : that.getInput() == null) {
            if (this.getOutput() != null ? this.getOutput().equals(that.getOutput()) : that.getOutput() == null) {
               return (this.getData() != null ? this.getData().equals(that.getData()) : that.getData() == null)
                  ? Objects.equals(this.parent, that.parent)
                  : false;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      RecipeNamespace namespace = (RecipeNamespace)this.parent.getParent();
      String recipeKey = this.parent.getKey(this);
      String groupKey = namespace.getKey(this.parent);
      String namespaceKey = ((RecipeRegistry)namespace.getParent()).getKey(namespace);
      return String.format("%s:%s/%s", namespaceKey, groupKey, recipeKey);
   }
}
