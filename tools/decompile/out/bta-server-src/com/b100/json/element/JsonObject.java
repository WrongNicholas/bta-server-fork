package com.b100.json.element;

import com.b100.utils.InvalidCharacterException;
import com.b100.utils.StringReader;
import com.b100.utils.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonObject implements JsonElement, Iterable<JsonEntry> {
   private List<JsonEntry> entries;
   private boolean compact = false;

   public JsonObject() {
      this.entries = new ArrayList<>();
   }

   public JsonObject(StringReader reader) {
      this();
      reader.skipWhitespace();
      reader.expectAndSkip('{');

      while (true) {
         reader.skipWhitespace();
         if (reader.get() == '"') {
            String id = (new JsonString(reader)).value;
            reader.skipWhitespace();
            reader.expectAndSkip(':');
            JsonElement element = JsonElement.readElement(reader);
            this.set(id, element);
            reader.skipWhitespace();
            if (reader.get() == ',') {
               reader.next();
               continue;
            }

            if (reader.get() != '}') {
               throw new InvalidCharacterException(reader);
            }

            reader.next();
            break;
         }

         if (reader.get() != '}') {
            throw new InvalidCharacterException(reader);
         }

         reader.next();
         break;
      }
   }

   @Override
   public void write(StringWriter writer) {
      writer.write("{");
      writer.addTab();
      int i = 0;

      for (JsonEntry entry : this.entries) {
         if (!this.isCompact()) {
            writer.write('\n');
         } else {
            writer.write(' ');
         }

         new JsonString(entry.name).write(writer);
         writer.write(": ");
         entry.value.write(writer);
         if (i < this.entries.size() - 1) {
            writer.write(',');
         }

         i++;
      }

      if (i > 0) {
         if (!this.isCompact()) {
            writer.write('\n');
         } else {
            writer.write(' ');
         }
      }

      writer.removeTab();
      writer.write("}");
   }

   @Override
   public String toString() {
      StringWriter writer = new StringWriter();
      this.write(writer);
      return writer.toString();
   }

   public JsonObject getOrCreateObject(String id) {
      JsonObject object = this.getObject(id);
      if (object == null) {
         object = new JsonObject();
         this.set(id, object);
      }

      return object;
   }

   public JsonEntry getOrCreateEntry(String string) {
      JsonEntry entry = this.getEntry(string);
      if (entry == null) {
         entry = new JsonEntry(string, null);
         this.entries.add(entry);
      }

      return entry;
   }

   public JsonEntry getEntry(String string) {
      for (JsonEntry e : this.entries) {
         if (e.equalsId(string)) {
            return e;
         }
      }

      return null;
   }

   public JsonElement get(String id) {
      JsonEntry entry = this.getEntry(id);
      return entry != null ? entry.value : null;
   }

   public JsonObject getObject(String id) {
      JsonElement element = this.get(id);
      return element != null ? element.getAsObject() : null;
   }

   public JsonArray getArray(String id) {
      JsonElement element = this.get(id);
      return element != null ? element.getAsArray() : null;
   }

   public JsonString getJsonString(String id) {
      return this.get(id).getAsString();
   }

   public JsonNumber getJsonNumber(String id) {
      return this.get(id).getAsNumber();
   }

   public JsonBoolean getJsonBoolean(String id) {
      return this.get(id).getAsBoolean();
   }

   public String getString(String id) {
      return this.get(id).getAsString().value;
   }

   public Number getNumber(String id) {
      return this.get(id).getAsNumber().value;
   }

   public int getInt(String id) {
      return this.getNumber(id).intValue();
   }

   public long getLong(String id) {
      return this.getNumber(id).longValue();
   }

   public double getDouble(String id) {
      return this.getNumber(id).doubleValue();
   }

   public float getFloat(String id) {
      return this.getNumber(id).floatValue();
   }

   public byte getByte(String id) {
      return this.getNumber(id).byteValue();
   }

   public short getShort(String id) {
      return this.getNumber(id).shortValue();
   }

   public boolean getBoolean(String id) {
      return this.get(id).getAsBoolean().value;
   }

   public int getInt(String id, int defaultValue) {
      JsonEntry entry = this.getOrCreateEntry(id);
      if (entry.value == null || !(entry.value instanceof JsonNumber)) {
         entry.value = new JsonNumber(defaultValue);
      }

      return entry.value.getAsNumber().getInteger();
   }

   public long getLong(String id, long defaultValue) {
      JsonEntry entry = this.getOrCreateEntry(id);
      if (entry.value == null || !(entry.value instanceof JsonNumber)) {
         entry.value = new JsonNumber(defaultValue);
      }

      return entry.value.getAsNumber().getLong();
   }

   public float getFloat(String id, float defaultValue) {
      JsonEntry entry = this.getOrCreateEntry(id);
      if (entry.value == null || !(entry.value instanceof JsonNumber)) {
         entry.value = new JsonNumber(defaultValue);
      }

      return entry.value.getAsNumber().getFloat();
   }

   public double getDouble(String id, double defaultValue) {
      JsonEntry entry = this.getOrCreateEntry(id);
      if (entry.value == null || !(entry.value instanceof JsonNumber)) {
         entry.value = new JsonNumber(defaultValue);
      }

      return entry.value.getAsNumber().getDouble();
   }

   public short getShort(String id, short defaultValue) {
      JsonEntry entry = this.getOrCreateEntry(id);
      if (entry.value == null || !(entry.value instanceof JsonNumber)) {
         entry.value = new JsonNumber((int)defaultValue);
      }

      return entry.value.getAsNumber().getShort();
   }

   public byte getByte(String id, byte defaultValue) {
      JsonEntry entry = this.getOrCreateEntry(id);
      if (entry.value == null || !(entry.value instanceof JsonNumber)) {
         entry.value = new JsonNumber((int)defaultValue);
      }

      return entry.value.getAsNumber().getByte();
   }

   public boolean getBoolean(String id, boolean defaultValue) {
      JsonEntry entry = this.getOrCreateEntry(id);
      if (entry.value == null || !(entry.value instanceof JsonBoolean)) {
         entry.value = new JsonBoolean(defaultValue);
      }

      return entry.value.getAsBoolean().value;
   }

   public JsonObject set(String id, JsonElement element) {
      this.getOrCreateEntry(id).value = element;
      return this;
   }

   public JsonObject set(String id, String s) {
      return this.set(id, new JsonString(s));
   }

   public JsonObject set(String id, int n) {
      return this.set(id, new JsonNumber(n));
   }

   public JsonObject set(String id, long n) {
      return this.set(id, new JsonNumber(n));
   }

   public JsonObject set(String id, float n) {
      return this.set(id, new JsonNumber(n));
   }

   public JsonObject set(String id, double n) {
      return this.set(id, new JsonNumber(n));
   }

   public JsonObject set(String id, short n) {
      return this.set(id, new JsonNumber((int)n));
   }

   public JsonObject set(String id, byte n) {
      return this.set(id, new JsonNumber((int)n));
   }

   public JsonObject set(String id, boolean b) {
      return this.set(id, new JsonBoolean(b));
   }

   public List<JsonElement> elementList() {
      List<JsonElement> elements = new ArrayList<>();

      for (JsonEntry entry : this.entries) {
         elements.add(entry.value);
      }

      return elements;
   }

   public List<String> idList() {
      List<String> elements = new ArrayList<>();

      for (JsonEntry entry : this.entries) {
         elements.add(entry.name);
      }

      return elements;
   }

   public List<JsonEntry> entryList() {
      return this.entries;
   }

   public boolean has(String id) {
      return this.getEntry(id) != null;
   }

   public boolean has(String name, JsonElement element) {
      JsonEntry entry = this.getEntry(name);
      return entry != null ? entry.value.equals(element) : false;
   }

   public boolean has(String name, String string) {
      JsonEntry entry = this.getEntry(name);
      return entry != null && entry.value.isString() ? entry.value.getAsString().value.equals(string) : false;
   }

   @Override
   public Iterator<JsonEntry> iterator() {
      return this.entries.iterator();
   }

   public JsonObject setCompact(boolean b) {
      this.compact = b;
      return this;
   }

   public boolean isCompact() {
      return this.compact;
   }
}
