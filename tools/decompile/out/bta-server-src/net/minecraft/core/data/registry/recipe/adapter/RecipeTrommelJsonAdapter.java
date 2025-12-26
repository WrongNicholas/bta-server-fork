package net.minecraft.core.data.registry.recipe.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryTrommel;

public class RecipeTrommelJsonAdapter implements RecipeJsonAdapter<RecipeEntryTrommel> {
   public RecipeEntryTrommel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      RecipeSymbol input = context.deserialize(obj.get("input").getAsJsonObject(), RecipeSymbol.class);
      Type type = (new TypeToken<WeightedRandomBag<WeightedRandomLootObject>>() {}).getType();
      WeightedRandomBag<WeightedRandomLootObject> bag = context.deserialize(obj.get("outputs").getAsJsonArray(), type);
      return new RecipeEntryTrommel(input, bag);
   }

   public JsonElement serialize(RecipeEntryTrommel src, Type typeOfSrc, JsonSerializationContext context) {
      Type type = (new TypeToken<WeightedRandomBag<WeightedRandomLootObject>>() {}).getType();
      JsonObject obj = new JsonObject();
      obj.addProperty("name", src.toString());
      obj.addProperty("type", Registries.RECIPE_TYPES.getKey((Class<? extends RecipeEntryBase<?, ?, ?>>)src.getClass()));
      obj.add("input", context.serialize(src.getInput()));
      obj.add("outputs", context.serialize(src.getOutput(), type));
      return obj;
   }
}
