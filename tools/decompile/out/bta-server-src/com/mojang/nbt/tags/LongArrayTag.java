package com.mojang.nbt.tags;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public class LongArrayTag extends Tag<long[]> {
   public LongArrayTag() {
      this(new long[0]);
   }

   public LongArrayTag(long[] array) {
      super(array);
   }

   @Override
   void write(@NotNull DataOutput dos) throws IOException {
      dos.writeInt(this.getValue().length);
      byte[] bytes = new byte[((long[])this.getValue()).length * 8];
      ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().put(this.getValue());
      dos.write(bytes);
   }

   @Override
   void read(@NotNull DataInput dis) throws IOException {
      int length = dis.readInt();
      this.setValue(new long[length]);
      byte[] bytes = new byte[length * 8];
      dis.readFully(bytes);
      ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().get(this.getValue());
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_LONG_ARRAY.getId();
   }

   @Override
   public void fromJson(@NotNull JsonElement element) throws JsonParseException {
      if (!element.isJsonArray()) {
         throw new JsonParseException("LongArrayTag json value must be a json array!");
      } else {
         JsonArray array = element.getAsJsonArray();
         long[] longs = new long[array.size()];

         for (int i = 0; i < longs.length; i++) {
            JsonElement e = array.get(i);
            if (!e.isJsonPrimitive()) {
               throw new JsonParseException("LongArrayTag json value must be a json array values must all be numbers!");
            }

            JsonPrimitive p = e.getAsJsonPrimitive();
            if (!p.isNumber()) {
               throw new JsonParseException("LongArrayTag json value must be a json array values must all be numbers!");
            }

            longs[i] = p.getAsLong();
         }

         this.setValue(longs);
      }
   }

   @NotNull
   @Override
   public JsonElement toJson() {
      long[] longs = this.getValue();
      JsonArray array = new JsonArray(longs.length);

      for (int i = 0; i < longs.length; i++) {
         array.add(longs[i]);
      }

      return array;
   }

   @Override
   public String toString() {
      if (this.getValue().length > 16) {
         return "\"" + this.getTagName() + "\": [ " + this.getValue().length + " longs ]";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("\"").append(this.getTagName()).append("\": [ ");

         for (long l : this.getValue()) {
            sb.append(l).append(", ");
         }

         sb.append("]");
         return sb.toString();
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof LongArrayTag)) {
         return false;
      } else {
         LongArrayTag otherTag = (LongArrayTag)obj;
         long[] thisData = this.getValue();
         long[] otherData = otherTag.getValue();
         return Arrays.equals(thisData, otherData);
      }
   }
}
