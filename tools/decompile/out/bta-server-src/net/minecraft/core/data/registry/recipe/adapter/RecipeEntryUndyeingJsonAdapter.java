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
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryUndyeing;
import net.minecraft.core.item.ItemStack;

public class RecipeEntryUndyeingJsonAdapter implements RecipeJsonAdapter<RecipeEntryUndyeing> {
   public RecipeEntryUndyeing deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      RecipeSymbol input = context.deserialize(obj.get("input"), RecipeSymbol.class);
      ItemStack output = context.deserialize(obj.get("output"), ItemStack.class);
      return new RecipeEntryUndyeing(input, output);
   }

   public JsonElement serialize(RecipeEntryUndyeing src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject object = new JsonObject();
      object.addProperty("name", src.toString());
      object.addProperty("type", Registries.RECIPE_TYPES.getKey((Class<? extends RecipeEntryBase<?, ?, ?>>)src.getClass()));
      object.add("input", context.serialize(src.inputSymbol));
      object.add("output", context.serialize(src.output));
      return object;
   }
}
