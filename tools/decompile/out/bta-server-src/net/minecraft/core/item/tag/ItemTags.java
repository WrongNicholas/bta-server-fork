package net.minecraft.core.item.tag;

import com.mojang.logging.LogUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.item.Item;
import org.slf4j.Logger;

public abstract class ItemTags {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static Tag<Item> PREVENT_CREATIVE_MINING = Tag.of("prevent_creative_mining");
   public static Tag<Item> IS_SILK_TOUCH = Tag.of("is_silk_touch");
   public static Tag<Item> NOT_IN_CREATIVE_MENU = Tag.of("not_in_creative_menu");
   public static Tag<Item> PREVENT_LEFT_CLICK_INTERACTIONS = Tag.of("prevent_left_click_interactions");
   public static Tag<Item> CHICKENS_FAVOURITE_ITEM = Tag.of("chickens_favourite_item");
   public static Tag<Item> COWS_FAVOURITE_ITEM = Tag.of("cows_favourite_item");
   public static List<Tag<Item>> TAG_LIST = new ArrayList<>();

   static {
      for (Field field : ItemTags.class.getDeclaredFields()) {
         if (field.getType().equals(Tag.class)) {
            try {
               TAG_LIST.add((Tag<Item>)field.get(null));
            } catch (Exception var5) {
               LOGGER.error("Failed to add tag '{}'!", field.getName(), var5);
            }
         }
      }
   }
}
