package net.minecraft.core.data.registry.recipe.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryRepairable;
import net.minecraft.core.item.ItemStack;

public class RecipeEntryRepairableJsonAdapter implements RecipeJsonAdapter<RecipeEntryRepairable> {
   public RecipeEntryRepairable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      ItemStack inputItem = context.deserialize(obj.get("input"), ItemStack.class);
      RecipeSymbol repairMaterial = context.deserialize(obj.get("repairMaterial"), RecipeSymbol.class);
      return new RecipeEntryRepairable(inputItem, repairMaterial);
   }

   public JsonElement serialize(RecipeEntryRepairable src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject object = new JsonObject();
      object.addProperty("name", src.toString());
      object.addProperty("type", Registries.RECIPE_TYPES.getKey((Class<? extends RecipeEntryBase<?, ?, ?>>)src.getClass()));
      object.add("input", context.serialize(src.inItem));
      object.add("repairMaterial", context.serialize(src.repairMaterial));
      return object;
   }
}
