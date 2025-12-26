package net.minecraft.core.data.tag;

import java.util.ArrayList;
import java.util.List;

public interface ITaggable<E extends ITaggable<E>> {
   boolean isIn(Tag<E> var1);

   default boolean isInAll(Tag<E>... tags) {
      List<Boolean> isIn = new ArrayList<>();

      for (Tag<E> tag : tags) {
         if (this.isIn(tag)) {
            isIn.add(true);
         } else {
            isIn.add(false);
         }
      }

      return !isIn.contains(false);
   }
}
