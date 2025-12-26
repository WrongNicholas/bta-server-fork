package net.minecraft.core.net.command.util;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class BeautifulNbt {
   public static String toBeautifulNbt(@NotNull CompoundTag nbt) {
      StringBuilder builder = new StringBuilder("{");
      boolean comma = false;

      for (Tag<?> tag : nbt.getValue().values()) {
         if (comma) {
            builder.append(", ");
         }

         builder.append(tag.getTagName()).append(": ").append(element(tag instanceof CompoundTag ? tag : tag.getValue()));
         comma = true;
      }

      return builder.append("}").toString();
   }

   private static String element(Object o) {
      if (o instanceof byte[] || o instanceof short[] || o instanceof double[] || o instanceof List) {
         return collection(o);
      } else if (o instanceof Byte) {
         return o + "b";
      } else if (o instanceof Short) {
         return o + "s";
      } else if (o instanceof Long) {
         return o + "l";
      } else if (o instanceof Float) {
         return o + "f";
      } else if (o instanceof String) {
         return "\"" + o + "\"";
      } else {
         return o instanceof CompoundTag ? toBeautifulNbt((CompoundTag)o) : o.toString();
      }
   }

   private static String collection(Object o) {
      StringBuilder result = new StringBuilder("[");
      boolean comma = false;
      if (o instanceof byte[]) {
         result.append("B; ");

         for (byte element : (byte[])o) {
            if (comma) {
               result.append(", ");
            }

            result.append(element(element));
            comma = true;
         }
      } else if (o instanceof short[]) {
         result.append("S; ");

         for (short element : (short[])o) {
            if (comma) {
               result.append(", ");
            }

            result.append(element(element));
            comma = true;
         }
      } else if (o instanceof double[]) {
         result.append("D; ");

         for (double element : (double[])o) {
            if (comma) {
               result.append(", ");
            }

            result.append(element(element));
            comma = true;
         }
      } else if (o instanceof List) {
         for (Tag<?> element : (List)o) {
            if (comma) {
               result.append(", ");
            }

            result.append(element(element instanceof CompoundTag ? element : element.getValue()));
            comma = true;
         }
      }

      return result.append("]").toString();
   }
}
