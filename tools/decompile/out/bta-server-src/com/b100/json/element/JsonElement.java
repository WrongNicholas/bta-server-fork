package com.b100.json.element;

import com.b100.utils.InvalidCharacterException;
import com.b100.utils.StringReader;
import com.b100.utils.Writable;

public interface JsonElement extends Writable {
   static JsonElement readElement(StringReader reader) {
      reader.skipWhitespace();
      if (reader.get() == '"') {
         return new JsonString(reader);
      } else if (reader.get() == '{') {
         return new JsonObject(reader);
      } else if (reader.get() == '[') {
         return new JsonArray(reader);
      } else if ((reader.get() < '0' || reader.get() > '9') && reader.get() != '-') {
         if (!reader.isNext("true") && !reader.isNext("false")) {
            throw new InvalidCharacterException(reader);
         } else {
            return new JsonBoolean(reader);
         }
      } else {
         return new JsonNumber(reader);
      }
   }

   default <E extends JsonElement> E getAs(Class<E> clazz) {
      return clazz.cast(this);
   }

   default JsonObject getAsObject() {
      return this.getAs(JsonObject.class);
   }

   default JsonArray getAsArray() {
      return this.getAs(JsonArray.class);
   }

   default JsonNumber getAsNumber() {
      return this.getAs(JsonNumber.class);
   }

   default JsonString getAsString() {
      return this.getAs(JsonString.class);
   }

   default JsonBoolean getAsBoolean() {
      return this.getAs(JsonBoolean.class);
   }

   default boolean is(Class<? extends JsonElement> clazz) {
      return this.getClass().isAssignableFrom(clazz);
   }

   default boolean isObject() {
      return this.is(JsonObject.class);
   }

   default boolean isArray() {
      return this.is(JsonArray.class);
   }

   default boolean isNumber() {
      return this.is(JsonNumber.class);
   }

   default boolean isString() {
      return this.is(JsonString.class);
   }

   default boolean isBoolean() {
      return this.is(JsonBoolean.class);
   }
}
