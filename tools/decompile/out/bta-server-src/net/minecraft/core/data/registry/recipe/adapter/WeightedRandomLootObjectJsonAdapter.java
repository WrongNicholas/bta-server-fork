package net.minecraft.core.data.registry.recipe.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.item.ItemStack;

public class WeightedRandomLootObjectJsonAdapter implements JsonSerializer<WeightedRandomLootObject>, JsonDeserializer<WeightedRandomLootObject> {
   public WeightedRandomLootObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = (JsonObject)json;
      boolean random = obj.get("random").getAsBoolean();
      WeightedRandomLootObject loot;
      if (random) {
         int maxYield = obj.get("yield").getAsJsonObject().get("max").getAsInt();
         int minYield = obj.get("yield").getAsJsonObject().get("min").getAsInt();
         ItemStack stack = context.deserialize(obj.get("stack").getAsJsonObject(), ItemStack.class);
         loot = new WeightedRandomLootObject(stack, minYield, maxYield);
      } else {
         int fixedYield = obj.get("yield").getAsJsonObject().get("fixed").getAsInt();
         ItemStack stack = context.deserialize(obj.get("stack").getAsJsonObject(), ItemStack.class);
         loot = new WeightedRandomLootObject(stack, fixedYield);
      }

      return loot;
   }

   public JsonElement serialize(WeightedRandomLootObject src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.addProperty("random", src.isRandomYield());
      if (src.isRandomYield()) {
         JsonObject yield = new JsonObject();
         yield.addProperty("min", src.getMinYield());
         yield.addProperty("max", src.getMaxYield());
         obj.add("yield", yield);
         obj.add("stack", context.serialize(src.getDefinedItemStack()));
      } else {
         JsonObject yield = new JsonObject();
         yield.addProperty("fixed", src.getFixedYield());
         obj.add("yield", yield);
         obj.add("stack", context.serialize(src.getDefinedItemStack()));
      }

      return obj;
   }
}
