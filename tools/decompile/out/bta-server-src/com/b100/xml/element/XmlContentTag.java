package com.b100.xml.element;

import com.b100.utils.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class XmlContentTag extends XmlTag<List<XmlTag<?>>> {
   public XmlContentTag(String id) {
      super(id, new ArrayList<>());
   }

   @Override
   public StringWriter write(StringWriter writer) {
      writer.write("<" + this.name);
      this.attributes.write(writer);
      writer.write(">");
      if (this.content.size() > 0) {
         writer.writeln("");
      }

      writer.addTab();

      for (XmlTag<?> tag : this.content) {
         tag.write(writer);
         writer.writeln();
      }

      writer.removeTab();
      writer.write("</" + this.name + ">");
      return writer;
   }

   public void add(XmlTag<?> tag) {
      this.content.add(tag);
   }

   public XmlTag<?> get(String id) {
      for (XmlTag<?> tag : this.content) {
         if (tag.name.equals(id)) {
            return tag;
         }
      }

      return null;
   }
}
