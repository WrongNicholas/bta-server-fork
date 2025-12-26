package net.minecraft.core.data.registry.recipe.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryScrap;
import net.minecraft.core.item.ItemStack;

public class RecipeEntryScrapJsonAdapter implements RecipeJsonAdapter<RecipeEntryScrap> {
   public RecipeEntryScrap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      ItemStack input = context.deserialize(obj.get("input").getAsJsonObject(), ItemStack.class);
      ItemStack scrapItem = context.deserialize(obj.get("output").getAsJsonObject(), ItemStack.class);
      int maxYield = obj.get("maxYield").getAsInt();
      return new RecipeEntryScrap(input.getItem(), scrapItem.getItem(), maxYield);
   }

   public JsonElement serialize(RecipeEntryScrap src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject object = new JsonObject();
      object.addProperty("name", src.toString());
      object.addProperty("type", Registries.RECIPE_TYPES.getKey((Class<? extends RecipeEntryBase<?, ?, ?>>)src.getClass()));
      object.add("input", context.serialize(new ItemStack(src.itemToScrap)));
      object.add("output", context.serialize(new ItemStack(src.scrapYield)));
      object.addProperty("maxYield", src.maxYield);
      return object;
   }
}
