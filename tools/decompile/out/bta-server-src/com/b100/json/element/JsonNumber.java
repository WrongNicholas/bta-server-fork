package com.b100.json.element;

import com.b100.utils.InvalidCharacterException;
import com.b100.utils.StringReader;
import com.b100.utils.StringWriter;
import com.b100.utils.Utils;

public class JsonNumber implements JsonElement {
   public Number value;

   public JsonNumber(Number number) {
      this.value = Utils.requireNonNull(number);
   }

   public JsonNumber(int i) {
      this.value = i;
   }

   public JsonNumber(long l) {
      this.value = l;
   }

   public JsonNumber(double d) {
      this.value = d;
   }

   public JsonNumber(float f) {
      this.value = f;
   }

   public JsonNumber(StringReader reader) {
      StringWriter numberString = new StringWriter();
      boolean decimal = false;
      if (reader.get() == '-') {
         numberString.write(reader.get());
         reader.next();
      }

      this.readInteger(reader, numberString);
      if (reader.get() == '.') {
         this.readDecimal(reader, numberString);
         decimal = true;
      }

      if (reader.get() == 'e' || reader.get() == 'E') {
         this.readExponent(reader, numberString);
      }

      if (reader.get() != ',' && reader.get() != '}' && !reader.isWhitespace(reader.get())) {
         throw new InvalidCharacterException(reader);
      } else {
         String str = numberString.toString();
         if (decimal) {
            float floatVal = 0.0F;
            double doubleVal = 0.0;

            try {
               floatVal = Float.parseFloat(str);
            } catch (Exception var10) {
            }

            doubleVal = Double.parseDouble(str);
            if (floatVal == doubleVal) {
               this.value = floatVal;
            } else {
               this.value = doubleVal;
            }
         } else {
            int intVal = 0;
            long longVal = 0L;

            try {
               intVal = Integer.parseInt(str);
            } catch (Exception var9) {
            }

            longVal = Long.parseLong(str);
            if (intVal == longVal) {
               this.value = intVal;
            } else {
               this.value = longVal;
            }
         }
      }
   }

   private void readInteger(StringReader reader, StringWriter numberString) {
      if (reader.get() == '0') {
         numberString.write(reader.get());
         reader.next();
      } else if (reader.get() >= '1' && reader.get() <= '9') {
         numberString.write(reader.get());
         reader.next();

         while (reader.get() >= '0' && reader.get() <= '9') {
            numberString.write(reader.get());
            reader.next();
         }
      }
   }

   private void readDecimal(StringReader reader, StringWriter numberString) {
      reader.expectAndSkip('.');
      numberString.write('.');

      while (reader.get() >= '0' && reader.get() <= '9') {
         numberString.write(reader.get());
         reader.next();
      }
   }

   private void readExponent(StringReader reader, StringWriter numberString) {
      reader.expectOne("eE");
      numberString.write(reader.get());
      reader.next();
      if (reader.get() == '+' || reader.get() == '-') {
         numberString.write(reader.get());
         reader.next();
      }

      while (reader.get() >= '0' && reader.get() <= '9') {
         numberString.write(reader.get());
         reader.next();
      }
   }

   @Override
   public void write(StringWriter writer) {
      writer.write(this.value.toString());
   }

   public int getInteger() {
      return this.value.intValue();
   }

   public double getDouble() {
      return this.value.doubleValue();
   }

   public float getFloat() {
      return this.value.floatValue();
   }

   public long getLong() {
      return this.value.longValue();
   }

   public byte getByte() {
      return this.value.byteValue();
   }

   public short getShort() {
      return this.value.shortValue();
   }

   public void set(Number n) {
      this.value = Utils.requireNonNull(n);
   }

   public void set(int i) {
      this.value = i;
   }

   public void set(long l) {
      this.value = l;
   }

   public void set(float f) {
      this.value = f;
   }

   public void set(double d) {
      this.value = d;
   }

   public boolean isInteger() {
      return this.value instanceof Integer;
   }

   public boolean isLong() {
      return this.value instanceof Long;
   }

   public boolean isFloat() {
      return this.value instanceof Float;
   }

   public boolean isDouble() {
      return this.value instanceof Double;
   }
}
