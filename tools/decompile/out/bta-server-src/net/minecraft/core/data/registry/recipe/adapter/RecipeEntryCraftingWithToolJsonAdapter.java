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
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingWithTool;
import net.minecraft.core.item.ItemStack;

public class RecipeEntryCraftingWithToolJsonAdapter implements RecipeJsonAdapter<RecipeEntryCraftingWithTool> {
   public RecipeEntryCraftingWithTool deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      RecipeSymbol input = context.deserialize(object.get("input"), RecipeSymbol.class);
      RecipeSymbol tool = context.deserialize(object.get("tool"), RecipeSymbol.class);
      ItemStack output = context.deserialize(object.get("output"), ItemStack.class);
      return new RecipeEntryCraftingWithTool(input, tool, output);
   }

   public JsonElement serialize(RecipeEntryCraftingWithTool src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject object = new JsonObject();
      object.addProperty("name", src.toString());
      object.addProperty("type", Registries.RECIPE_TYPES.getKey((Class<? extends RecipeEntryBase<?, ?, ?>>)src.getClass()));
      object.add("input", context.serialize(src.inputSymbol));
      object.add("tool", context.serialize(src.toolToUse));
      object.add("output", context.serialize(src.output));
      return object;
   }
}
