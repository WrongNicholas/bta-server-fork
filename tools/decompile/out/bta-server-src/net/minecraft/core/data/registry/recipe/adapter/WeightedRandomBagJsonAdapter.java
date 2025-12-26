package net.minecraft.core.data.registry.recipe.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.util.collection.Pair;

public class WeightedRandomBagJsonAdapter
   implements JsonDeserializer<WeightedRandomBag<WeightedRandomLootObject>>,
   JsonSerializer<WeightedRandomBag<WeightedRandomLootObject>> {
   public WeightedRandomBag<WeightedRandomLootObject> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonArray array = (JsonArray)json;
      ArrayList<Pair<WeightedRandomLootObject, Double>> entries = new ArrayList<>();

      for (JsonElement jsonElement : array) {
         JsonObject obj = (JsonObject)jsonElement;
         WeightedRandomLootObject loot = context.deserialize(obj.get("loot").getAsJsonObject(), WeightedRandomLootObject.class);
         double weight = obj.get("weight").getAsDouble();
         entries.add(Pair.of(loot, weight));
      }

      WeightedRandomBag<WeightedRandomLootObject> bag = new WeightedRandomBag<>();

      for (Pair<WeightedRandomLootObject, Double> entry : entries) {
         bag.addEntry(entry.getLeft(), entry.getRight());
      }

      return bag;
   }

   public JsonElement serialize(WeightedRandomBag<WeightedRandomLootObject> src, Type typeOfSrc, JsonSerializationContext context) {
      JsonArray array = new JsonArray();

      for (WeightedRandomBag<WeightedRandomLootObject>.Entry entry : src.getEntriesWithWeights()) {
         JsonObject obj = new JsonObject();
         obj.addProperty("weight", entry.getWeight());
         obj.add("loot", context.serialize(entry.getObject()));
         array.add(obj);
      }

      return array;
   }
}
