package com.b100.xml.element;

import com.b100.utils.InvalidCharacterException;
import com.b100.utils.StringReader;
import com.b100.utils.StringWriter;

public abstract class XmlTag<E> {
   protected String name;
   protected XmlAttributeList attributes;
   protected E content;

   public XmlTag(String id, E content) {
      if (id == null) {
         throw new NullPointerException();
      } else if (id.length() == 0) {
         throw new RuntimeException("Empty String");
      } else {
         this.name = id;
         this.content = content;
      }
   }

   public abstract StringWriter write(StringWriter var1);

   public E content() {
      return this.content;
   }

   public void setContent(E content) {
      this.content = content;
   }

   public XmlAttributeList getAttributes() {
      return this.attributes;
   }

   public void setAttributes(XmlAttributeList attributes) {
      this.attributes = attributes;
   }

   public String name() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public static XmlTag<?> read(StringReader reader) {
      reader.skipWhitespace();
      reader.expectAndSkip("<");
      String id = readId(reader);
      XmlAttributeList attributes = XmlAttributeList.read(reader);
      reader.expectAndSkip(">");
      String closeTag = "</" + id + ">";
      reader.skipWhitespace();
      XmlTag<?> tag;
      if (reader.isNext(closeTag)) {
         reader.expectAndSkip(closeTag);
         tag = new XmlStringTag(id, null);
      } else if (reader.get() == '<') {
         tag = readContentTag(id, reader);
      } else {
         tag = readStringTag(id, reader);
      }

      tag.setAttributes(attributes);
      return tag;
   }

   public static XmlContentTag readContentTag(String id, StringReader reader) {
      XmlContentTag tag = new XmlContentTag(id);
      String closeTag = "</" + id + ">";

      do {
         tag.add(read(reader));
         reader.skipWhitespace();
      } while (!reader.isNext("</"));

      reader.expectAndSkip(closeTag);
      return tag;
   }

   public static XmlStringTag readStringTag(String id, StringReader reader) {
      String closeTag = "</" + id + ">";
      String value = "";

      while (reader.get() != '<') {
         value = value + reader.getAndSkip();
      }

      reader.expectAndSkip(closeTag);
      return new XmlStringTag(id, value);
   }

   public static String readId(StringReader reader) {
      String id = "";

      while (reader.get() != '/') {
         if (reader.get() == ' ' || reader.get() == '>') {
            return id;
         }

         id = id + reader.getAndSkip();
      }

      throw new InvalidCharacterException(reader);
   }

   @Override
   public String toString() {
      return this.write(new StringWriter()).toString();
   }

   public XmlContentTag getAsContentTag() {
      return (XmlContentTag)this;
   }

   public XmlStringTag getAsStringTag() {
      return (XmlStringTag)this;
   }
}
