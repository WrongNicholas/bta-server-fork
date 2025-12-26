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
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryBlastFurnace;
import net.minecraft.core.item.ItemStack;

public class RecipeBlastFurnaceJsonAdapter implements RecipeJsonAdapter<RecipeEntryBlastFurnace> {
   public RecipeEntryBlastFurnace deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      RecipeSymbol input = context.deserialize(obj.get("input").getAsJsonObject(), RecipeSymbol.class);
      ItemStack output = context.deserialize(obj.get("output").getAsJsonObject(), ItemStack.class);
      return new RecipeEntryBlastFurnace(input, output);
   }

   public JsonElement serialize(RecipeEntryBlastFurnace src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.addProperty("name", src.toString());
      obj.addProperty("type", Registries.RECIPE_TYPES.getKey((Class<? extends RecipeEntryBase<?, ?, ?>>)src.getClass()));
      obj.add("input", context.serialize(src.getInput()));
      obj.add("output", context.serialize(src.getOutput()));
      return obj;
   }
}
