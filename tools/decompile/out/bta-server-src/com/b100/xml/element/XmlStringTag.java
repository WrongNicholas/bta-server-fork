package com.b100.xml.element;

import com.b100.utils.StringWriter;

public class XmlStringTag extends XmlTag<String> {
   public XmlStringTag(String id, String value) {
      super(id, value);
   }

   @Override
   public StringWriter write(StringWriter writer) {
      writer.write("<" + this.name);
      this.attributes.write(writer);
      writer.write(">" + (this.content != null ? this.content : "") + "</" + this.name + ">");
      return writer;
   }

   public long getLong() {
      return Long.parseLong(this.content);
   }

   public int getInt() {
      return Integer.parseInt(this.content);
   }
}
