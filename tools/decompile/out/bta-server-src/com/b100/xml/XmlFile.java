package com.b100.xml;

import com.b100.utils.StringReader;
import com.b100.utils.StringWriter;
import com.b100.xml.element.XmlAttributeList;
import com.b100.xml.element.XmlTag;

public class XmlFile {
   private XmlAttributeList meta = new XmlAttributeList();
   private XmlTag<?> rootElement;

   public static XmlFile read(StringReader reader) {
      XmlFile file = new XmlFile();
      reader.skipWhitespace();
      reader.expectAndSkip("<?xml");
      file.meta = XmlAttributeList.read(reader);
      reader.expectAndSkip("?>");
      file.rootElement = XmlTag.read(reader);
      return file;
   }

   public StringWriter write(StringWriter writer) {
      writer.write("<?xml");
      this.meta.write(writer);
      writer.writeln("?>");
      this.rootElement.write(writer);
      return writer;
   }

   @Override
   public String toString() {
      return this.write(new StringWriter()).toString();
   }

   public XmlTag<?> getRootElement() {
      return this.rootElement;
   }
}
