package net.minecraft.core.net.command.util;

import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public class ConcatIterable<T> implements Iterable<T> {
   private final Iterable<T> first;
   private final Iterable<T> second;

   public ConcatIterable(Iterable<T> first, Iterable<T> second) {
      this.first = first;
      this.second = second;
   }

   @NotNull
   @Override
   public Iterator<T> iterator() {
      return new Iterator<T>() {
         private final Iterator<T> firstIterator = ConcatIterable.this.first.iterator();
         private final Iterator<T> secondIterator = ConcatIterable.this.second.iterator();
         private boolean firstIteratorActive = true;

         @Override
         public boolean hasNext() {
            if (this.firstIteratorActive) {
               if (this.firstIterator.hasNext()) {
                  return true;
               }

               this.firstIteratorActive = false;
            }

            return this.secondIterator.hasNext();
         }

         @Override
         public T next() {
            if (this.firstIteratorActive) {
               if (this.firstIterator.hasNext()) {
                  return this.firstIterator.next();
               }

               this.firstIteratorActive = false;
            }

            return this.secondIterator.next();
         }
      };
   }
}
