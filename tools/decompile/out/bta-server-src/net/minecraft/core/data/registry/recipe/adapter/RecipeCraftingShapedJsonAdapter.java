package net.minecraft.core.data.registry.recipe.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeRegistry;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCraftingShaped;
import net.minecraft.core.item.ItemStack;

public class RecipeCraftingShapedJsonAdapter implements RecipeJsonAdapter<RecipeEntryCraftingShaped> {
public RecipeEntryCraftingShaped deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject obj = json.getAsJsonObject();

    List<String> pattern = obj.get("pattern")
        .getAsJsonArray()
        .asList()
        .stream()
        .map(JsonElement::getAsString)
        .collect(Collectors.toList());

    List<RecipeSymbol> symbols = obj.get("symbols")
        .getAsJsonArray()
        .asList()
        .stream()
        .map(e -> (RecipeSymbol) context.deserialize(e, RecipeSymbol.class))
        .collect(Collectors.toList());

    ItemStack result = context.deserialize(obj.get("result").getAsJsonObject(), ItemStack.class);
    boolean consumeContainers = obj.has("consumeContainers") && obj.get("consumeContainers").getAsBoolean();
    boolean mirror = !obj.has("mirror") || obj.get("mirror").getAsBoolean();

    return RecipeRegistry.parseRecipe(pattern, symbols, result, consumeContainers, mirror);
}

   public JsonElement serialize(RecipeEntryCraftingShaped src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.addProperty("name", src.toString());
      obj.addProperty("type", Registries.RECIPE_TYPES.getKey((Class<? extends RecipeEntryBase<?, ?, ?>>)src.getClass()));
      RecipeSymbol[] symbols = src.getInput();
      StringBuilder sb = new StringBuilder();

      for (RecipeSymbol symbol : symbols) {
         if (symbol == null) {
            sb.append(" ");
         } else if (symbol.getSymbol() == 0) {
            sb.append(" ");
         } else {
            sb.append(symbol.getSymbol());
         }
      }

      String s = sb.toString();
      String[] pattern = s.split("(?<=\\G.{" + src.recipeWidth + "})");
      JsonArray arr = new JsonArray();

      for (String string : pattern) {
         arr.add(string);
      }

      ArrayList<RecipeSymbol> list = new ArrayList<>();

      for (RecipeSymbol symbolx : symbols) {
         if (symbolx != null) {
            boolean found = false;

            for (RecipeSymbol recipeSymbol : list) {
               if (recipeSymbol.getSymbol() == symbolx.getSymbol()) {
                  found = true;
                  break;
               }
            }

            if (!found) {
               list.add(symbolx);
            }
         }
      }

      obj.add("pattern", arr);
      obj.add("symbols", context.serialize(list));
      obj.add("result", context.serialize(src.getOutput()));
      if (src.consumeContainerItem) {
         obj.addProperty("consumeContainers", src.consumeContainerItem);
      }

      if (!src.allowMirrored) {
         obj.addProperty("mirror", src.allowMirrored);
      }

      return obj;
   }
}
