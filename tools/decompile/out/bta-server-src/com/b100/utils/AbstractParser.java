package com.b100.utils;

public abstract class AbstractParser<ReturnType, ReadType> {
   public final Class<ReadType> clazz;

   public AbstractParser(Class<ReadType> clazz) {
      this.clazz = Utils.requireNonNull(clazz);
   }

   public final ReturnType parse(Object object) {
      return this.parse2(this.clazz.cast(object));
   }

   public boolean canParse(Object object) {
      return this.clazz.isAssignableFrom(object.getClass()) && object.getClass().isAssignableFrom(this.clazz);
   }

   protected abstract ReturnType parse2(ReadType var1);
}
