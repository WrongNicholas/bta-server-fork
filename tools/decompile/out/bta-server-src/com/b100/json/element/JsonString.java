package com.b100.json.element;

import com.b100.utils.InvalidCharacterException;
import com.b100.utils.StringReader;
import com.b100.utils.StringWriter;

public class JsonString implements JsonElement {
   public String value;

   public JsonString(String value) {
      this.value = value;
   }

   public JsonString(StringReader reader) {
      StringBuilder builder = new StringBuilder();
      reader.expectAndSkip('"');

      while (reader.get() != '"') {
         if (reader.get() == '\\') {
            reader.next();
            char next = reader.get();
            if (next != 'n' && next != 'N') {
               if (next != '\\') {
                  throw new InvalidCharacterException(reader);
               }

               builder.append('\\');
            } else {
               builder.append('\n');
            }

            reader.next();
         } else {
            builder.append(reader.getAndSkip());
         }
      }

      reader.next();
      this.value = builder.toString();
   }

   public boolean equals(JsonString string2) {
      return this.value.equals(string2.value);
   }

   @Override
   public String toString() {
      return this.value;
   }

   @Override
   public void write(StringWriter writer) {
      writer.write("\"");

      for (int i = 0; i < this.value.length(); i++) {
         char c = this.value.charAt(i);
         if (c == '\n') {
            writer.write("\\n");
         } else if (c == '\t') {
            writer.write("\\t");
         } else if (c == '\\') {
            writer.write("\\\\");
         } else {
            writer.write(c);
         }
      }

      writer.write("\"");
   }
}
