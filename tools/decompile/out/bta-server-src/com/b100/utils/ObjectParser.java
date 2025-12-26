package com.b100.utils;

public class ObjectParser<F, E> extends AbstractParser<F, E> {
   private final Parser<F, E> parser;

   public ObjectParser(Class<E> type, Parser<F, E> parser) {
      super(type);
      this.parser = parser;
   }

   @Override
   protected F parse2(E obj) {
      return this.parser.parse(obj);
   }
}
