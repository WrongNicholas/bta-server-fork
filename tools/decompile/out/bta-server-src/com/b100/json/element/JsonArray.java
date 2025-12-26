package com.b100.json.element;

import com.b100.utils.ArrayIterator;
import com.b100.utils.InvalidCharacterException;
import com.b100.utils.StringReader;
import com.b100.utils.StringWriter;
import com.b100.utils.Utils;
import com.b100.utils.interfaces.Condition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonArray implements JsonElement, Iterable<JsonElement> {
   private JsonElement[] elements;
   private boolean compact = false;

   public JsonArray(int length) {
      this.elements = new JsonElement[length];
   }

   public JsonArray(JsonElement[] elements) {
      this.elements = elements;
   }

   public JsonArray(StringReader reader) {
      List<JsonElement> elementsList = new ArrayList<>();
      reader.skipWhitespace();
      reader.expectAndSkip('[');

      while (true) {
         reader.skipWhitespace();
         if (reader.get() == ']') {
            reader.next();
            break;
         }

         elementsList.add(JsonElement.readElement(reader));
         reader.skipWhitespace();
         if (reader.get() != ',') {
            if (reader.get() != ']') {
               throw new InvalidCharacterException(reader);
            }

            reader.next();
            break;
         }

         reader.next();
      }

      this.elements = Utils.toArray(JsonElement.class, elementsList);
   }

   public JsonElement query(Condition<JsonElement> condition) {
      for (JsonElement e : this.elements) {
         if (condition.isTrue(e)) {
            return e;
         }
      }

      return null;
   }

   @Override
   public String toString() {
      return "JsonArray: " + this.elements.length + " elements";
   }

   @Override
   public void write(StringWriter writer) {
      if (this.elements.length == 0) {
         writer.write("[]");
      } else {
         if (this.isCompact()) {
            writer.write("[ ");
         } else {
            writer.writeln("[");
         }

         writer.addTab();
         int i = 0;

         for (JsonElement element : this.elements) {
            element.write(writer);
            if (i < this.elements.length - 1) {
               writer.write(", ");
            }

            if (!this.isCompact()) {
               writer.write('\n');
            }

            i++;
         }

         writer.removeTab();
         if (this.isCompact()) {
            writer.write(" ]");
         } else {
            writer.write("]");
         }
      }
   }

   @Override
   public Iterator<JsonElement> iterator() {
      return new ArrayIterator<>(this.elements);
   }

   public int length() {
      return this.elements.length;
   }

   public JsonElement get(int i) {
      return this.elements[i];
   }

   public JsonArray set(int i, JsonElement element) {
      this.elements[i] = element;
      return this;
   }

   public JsonArray set(int i, Number number) {
      this.elements[i] = new JsonNumber(number);
      return this;
   }

   public JsonArray setCompact(boolean compact) {
      this.compact = compact;
      return this;
   }

   public boolean isCompact() {
      return this.compact;
   }
}
