package com.b100.utils;

import java.util.ArrayList;
import java.util.List;

public class ParserCollection<E> {
   public final List<AbstractParser<E, ?>> parsers = new ArrayList<>();

   public E parse(Object object) {
      Utils.requireNonNull(object);

      for (AbstractParser<E, ?> parser : this.parsers) {
         if (parser.canParse(object)) {
            try {
               return parser.parse(object);
            } catch (Exception var5) {
               throw new RuntimeException("Error parsing Object: " + object.getClass().getName() + " '" + object + "'", var5);
            }
         }
      }

      throw new RuntimeException("No parser for class: " + object.getClass().getName());
   }

   public void add(AbstractParser<E, ?> parser) {
      this.parsers.add(parser);
   }
}
