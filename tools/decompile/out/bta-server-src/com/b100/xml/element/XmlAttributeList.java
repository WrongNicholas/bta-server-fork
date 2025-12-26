package com.b100.xml.element;

import com.b100.utils.StringReader;
import com.b100.utils.StringWriter;
import java.util.ArrayList;

public class XmlAttributeList extends ArrayList<XmlAttribute> {
   private static final long serialVersionUID = 1L;

   public static XmlAttributeList read(StringReader reader) {
      XmlAttributeList list = new XmlAttributeList();
      reader.skipWhitespace();

      while (reader.get() != '>' && reader.get() != '?') {
         list.add(XmlAttribute.read(reader));
         reader.skipWhitespace();
      }

      return list;
   }

   public StringWriter write(StringWriter writer) {
      for (XmlAttribute attribute : this) {
         writer.write(" ");
         attribute.write(writer);
      }

      return writer;
   }
}
