package com.b100.xml.element;

import com.b100.utils.InvalidCharacterException;
import com.b100.utils.StringReader;
import com.b100.utils.StringWriter;

public class XmlAttribute {
   private String id;
   private String value;

   public XmlAttribute(String id, String value) {
      this.id = id;
      this.value = value;
   }

   public static XmlAttribute read(StringReader reader) {
      String id = "";
      String value = "";

      while (reader.get() != '=') {
         if (reader.get() == ' ') {
            throw new InvalidCharacterException(reader);
         }

         id = id + reader.get();
         reader.next();
      }

      reader.next();
      reader.expectAndSkip("\"");

      while (reader.get() != '"') {
         value = value + reader.get();
         reader.next();
      }

      reader.next();
      return new XmlAttribute(id, value);
   }

   public StringWriter write(StringWriter writer) {
      writer.write(this.id + "=\"" + this.value + "\"");
      return writer;
   }
}
