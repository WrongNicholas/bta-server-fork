package net.minecraft.core.data.registry.recipe.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.item.ItemStack;

public class RecipeSymbolJsonAdapter implements JsonDeserializer<RecipeSymbol>, JsonSerializer<RecipeSymbol> {
   public final boolean verbose;

   public RecipeSymbolJsonAdapter() {
      this(false);
   }

   public RecipeSymbolJsonAdapter(boolean verbose) {
      this.verbose = verbose;
   }

   public RecipeSymbol deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      char symbol = 0;
      if (obj.has("symbol")) {
         symbol = obj.get("symbol").getAsString().charAt(0);
      }

      ItemStack stack = null;
      String group = null;
      int amount = 1;
      if (obj.has("stack")) {
         stack = context.deserialize(obj.getAsJsonObject("stack"), ItemStack.class);
      }

      if (obj.has("group")) {
         group = obj.get("group").getAsString();
      }

      if (obj.has("amount")) {
         amount = obj.get("amount").getAsInt();
      }

      return new RecipeSymbol(symbol, stack, group, amount);
   }

   public JsonElement serialize(RecipeSymbol src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      if (this.verbose || src.getSymbol() != 0) {
         obj.addProperty("symbol", src.getSymbol());
      }

      if (this.verbose || src.getItemGroup() != null) {
         obj.addProperty("group", src.getItemGroup());
      }

      if (this.verbose || src.getAmount() != 1) {
         obj.addProperty("amount", src.getAmount());
      }

      if (this.verbose || src.getStack() != null) {
         obj.add("stack", context.serialize(src.getStack()));
      }

      return obj;
   }
}
