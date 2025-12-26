package com.b100.json;

import com.b100.json.element.JsonObject;
import com.b100.utils.ObjectParser;
import com.b100.utils.Parser;
import com.b100.utils.ParserCollection;
import com.b100.utils.StringParser;
import com.b100.utils.StringReader;
import com.b100.utils.StringUtils;
import java.io.File;
import java.io.InputStream;

public class JsonParser implements StringParser<JsonObject> {
   public static final JsonParser instance = new JsonParser();
   private final ParserCollection<JsonObject> parsers = new ParserCollection<>();
   private final Parser<JsonObject, String> stringParser = string -> new JsonObject(new StringReader(string));
   private final Parser<JsonObject, File> fileParser = file -> this.stringParser.parse(StringUtils.getFileContentAsString(file));
   private final Parser<JsonObject, InputStream> streamParser = stream -> this.stringParser.parse(StringUtils.readInputString(stream));

   public JsonParser() {
      this.parsers.add(new ObjectParser<>(String.class, this.stringParser));
      this.parsers.add(new ObjectParser<>(File.class, this.fileParser));
      this.parsers.add(new ObjectParser<>(InputStream.class, this.streamParser));
   }

   public JsonObject parse(Object object) {
      return this.parsers.parse(object);
   }

   public ParserCollection<JsonObject> getParsers() {
      return this.parsers;
   }

   public JsonObject parseString(String string) {
      return this.stringParser.parse(string);
   }
}
