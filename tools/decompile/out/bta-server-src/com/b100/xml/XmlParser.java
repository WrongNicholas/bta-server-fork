package com.b100.xml;

import com.b100.utils.ObjectParser;
import com.b100.utils.Parser;
import com.b100.utils.ParserCollection;
import com.b100.utils.StringParser;
import com.b100.utils.StringReader;
import com.b100.utils.StringUtils;
import java.io.File;
import java.io.InputStream;

public class XmlParser implements StringParser<XmlFile> {
   public static final XmlParser instance = new XmlParser();
   private final ParserCollection<XmlFile> parsers = new ParserCollection<>();
   private final Parser<XmlFile, String> stringParser = string -> XmlFile.read(new StringReader(string));
   private final Parser<XmlFile, File> fileParser = file -> this.stringParser.parse(StringUtils.getFileContentAsString(file));
   private final Parser<XmlFile, InputStream> streamParser = stream -> this.stringParser.parse(StringUtils.readInputString(stream));

   public XmlParser() {
      this.parsers.add(new ObjectParser<>(String.class, this.stringParser));
      this.parsers.add(new ObjectParser<>(File.class, this.fileParser));
      this.parsers.add(new ObjectParser<>(InputStream.class, this.streamParser));
   }

   public XmlFile parse(Object object) {
      return this.parsers.parse(object);
   }

   public ParserCollection<XmlFile> getParsers() {
      return this.parsers;
   }

   public XmlFile parseString(String string) {
      return this.stringParser.parse(string);
   }
}
