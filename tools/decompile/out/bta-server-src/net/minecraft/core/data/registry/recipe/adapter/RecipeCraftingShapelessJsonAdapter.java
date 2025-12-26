package net.minecraft.core.data.registry.recipe.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShapeless;
import net.minecraft.core.item.ItemStack;

public class RecipeCraftingShapelessJsonAdapter implements RecipeJsonAdapter<RecipeEntryCraftingShapeless> {
   public RecipeEntryCraftingShapeless deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      List<RecipeSymbol> symbols = obj.get("inputs")
          .getAsJsonArray()
          .asList()
          .stream()
          .map(e -> (RecipeSymbol) context.deserialize(e, RecipeSymbol.class))
          .collect(Collectors.toList());
      ItemStack result = context.deserialize(obj.get("result").getAsJsonObject(), ItemStack.class);
      return new RecipeEntryCraftingShapeless(symbols, result);
   }

   public JsonElement serialize(RecipeEntryCraftingShapeless src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.addProperty("name", src.toString());
      obj.addProperty("type", Registries.RECIPE_TYPES.getKey((Class<? extends RecipeEntryBase<?, ?, ?>>)src.getClass()));
      List<RecipeSymbol> symbols = src.getInput();
      obj.add("inputs", context.serialize(symbols));
      obj.add("result", context.serialize(src.getOutput()));
      return obj;
   }
}
