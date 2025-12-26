package net.minecraft.core.net.command.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class Iterables {
   public static <T> T getLast(Iterable<? extends T> iterable) {
      if (iterable instanceof List) {
         List<? extends T> list = (List<? extends T>)iterable;
         if (list.isEmpty()) {
            throw new NoSuchElementException();
         } else {
            return (T)list.get(list.size() - 1);
         }
      } else {
         Iterator<? extends T> iterator = iterable.iterator();
         if (!iterator.hasNext()) {
            throw new NoSuchElementException();
         } else {
            T last = (T)iterator.next();

            while (iterator.hasNext()) {
               last = (T)iterator.next();
            }

            return last;
         }
      }
   }
}
