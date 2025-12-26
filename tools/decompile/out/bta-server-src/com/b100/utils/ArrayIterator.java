package com.b100.utils;

import com.b100.utils.interfaces.Listener;
import java.util.Iterator;

public class ArrayIterator<E> implements Iterator<E>, Iterable<E> {
   private int pos;
   private E[] array;

   public ArrayIterator(E[] array) {
      this.array = array;
      this.pos = 0;
   }

   @Override
   public boolean hasNext() {
      return this.pos < this.array.length;
   }

   @Override
   public E next() {
      return this.array[this.pos++];
   }

   @Override
   public Iterator<E> iterator() {
      return this;
   }

   public static <E> void forEach(E[] arr, Listener<E> listener) {
      Utils.requireNonNull((E)arr);
      Utils.requireNonNull(listener);

      for (int i = 0; i < arr.length; i++) {
         listener.listen(arr[i]);
      }
   }
}
