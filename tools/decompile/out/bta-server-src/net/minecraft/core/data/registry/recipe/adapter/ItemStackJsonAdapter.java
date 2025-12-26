package net.minecraft.core.data.registry.recipe.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.logging.LogUtils;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import java.lang.reflect.Type;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;

public class ItemStackJsonAdapter implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public final boolean verbose;

   public ItemStackJsonAdapter() {
      this(false);
   }

   public ItemStackJsonAdapter(boolean verbose) {
      this.verbose = verbose;
   }

   public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      Item item;
      if (obj.has("key")) {
         String key = obj.get("key").getAsString();

         try {
            if (key.startsWith("tile.")) {
               item = Item.itemsList[Blocks.keyToIdMap.get(key)];
            } else {
               if (!key.startsWith("item.")) {
                  throw new IllegalArgumentException("Item keys must either start with 'tile' or 'item' provided key '" + key + "' does not!");
               }

               item = Item.itemsList[Item.nameToIdMap.get(key)];
            }
         } catch (Exception var11) {
            LOGGER.warn("Failed to find item for key '{}'!", key);
            throw new IllegalArgumentException("Failed to find item for key '" + key + "'!\n" + var11.getMessage());
         }
      } else {
         JsonElement e = obj.get("id");

         try {
            item = Item.itemsList[e.getAsInt()];
         } catch (NumberFormatException var10) {
            try {
               item = Item.itemsMap.get(NamespaceID.getTemp(e.getAsString()));
            } catch (HardIllegalArgumentException var9) {
               throw new JsonParseException(var9);
            }
         }
      }

      int amount = 1;
      int meta = 0;
      CompoundTag tag = null;
      if (obj.has("amount")) {
         amount = obj.get("amount").getAsInt();
      }

      if (obj.has("meta")) {
         meta = obj.get("meta").getAsInt();
      }

      if (obj.has("tag")) {
         tag = NbtIo.fromJson(obj.get("tag"));
      }

      if (item == null) {
         throw new JsonParseException("Could not parse stack for json:\n" + json);
      } else {
         return new ItemStack(item, amount, meta, tag);
      }
   }

   public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.addProperty("id", src.getItem().namespaceID.toString());
      if (this.verbose || src.stackSize != 1) {
         obj.addProperty("amount", src.stackSize);
      }

      if (this.verbose || src.getMetadata() != 0) {
         obj.addProperty("meta", src.getMetadata());
      }

      if (this.verbose || !src.getData().getValue().isEmpty()) {
         obj.add("tag", NbtIo.toJson(src.getData()));
      }

      return obj;
   }
}
