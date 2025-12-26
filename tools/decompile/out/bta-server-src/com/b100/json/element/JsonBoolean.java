package com.b100.json.element;

import com.b100.utils.InvalidCharacterException;
import com.b100.utils.StringReader;
import com.b100.utils.StringWriter;

public class JsonBoolean implements JsonElement {
   public boolean value;

   public JsonBoolean(boolean value) {
      this.value = value;
   }

   public JsonBoolean(StringReader reader) {
      if (reader.isNext("false")) {
         this.value = false;
         reader.skip(5);
      } else {
         if (!reader.isNext("true")) {
            throw new InvalidCharacterException(reader);
         }

         this.value = true;
         reader.skip(4);
      }
   }

   @Override
   public String toString() {
      return "" + this.value;
   }

   @Override
   public void write(StringWriter writer) {
      writer.write("" + this.value);
   }
}
