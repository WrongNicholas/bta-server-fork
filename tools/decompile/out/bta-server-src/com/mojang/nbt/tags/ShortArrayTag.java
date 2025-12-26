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

public class ShortArrayTag extends Tag<short[]> {
   public ShortArrayTag() {
      this(new short[0]);
   }

   public ShortArrayTag(short[] array) {
      super(array);
   }

   @Override
   void write(@NotNull DataOutput dos) throws IOException {
      dos.writeInt(this.getValue().length);
      byte[] bytes = new byte[((short[])this.getValue()).length * 2];
      ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(this.getValue());
      dos.write(bytes);
   }

   @Override
   void read(@NotNull DataInput dis) throws IOException {
      int length = dis.readInt();
      this.setValue(new short[length]);
      byte[] bytes = new byte[length * 2];
      dis.readFully(bytes);
      ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(this.getValue());
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_SHORT_ARRAY.getId();
   }

   @Override
   public void fromJson(@NotNull JsonElement element) throws JsonParseException {
      if (!element.isJsonArray()) {
         throw new JsonParseException("ShortArrayTag json value must be a json array!");
      } else {
         JsonArray array = element.getAsJsonArray();
         short[] shorts = new short[array.size()];

         for (int i = 0; i < shorts.length; i++) {
            JsonElement e = array.get(i);
            if (!e.isJsonPrimitive()) {
               throw new JsonParseException("ShortArrayTag json value must be a json array values must all be numbers!");
            }

            JsonPrimitive p = e.getAsJsonPrimitive();
            if (!p.isNumber()) {
               throw new JsonParseException("ShortArrayTag json value must be a json array values must all be numbers!");
            }

            shorts[i] = p.getAsShort();
         }

         this.setValue(shorts);
      }
   }

   @NotNull
   @Override
   public JsonElement toJson() {
      short[] shorts = this.getValue();
      JsonArray array = new JsonArray(shorts.length);

      for (int i = 0; i < shorts.length; i++) {
         array.add(shorts[i]);
      }

      return array;
   }

   @Override
   public String toString() {
      if (this.getValue().length > 16) {
         return "\"" + this.getTagName() + "\": [ " + this.getValue().length + " shorts ]";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("\"").append(this.getTagName()).append("\": [ ");

         for (short s : this.getValue()) {
            sb.append((int)s).append(", ");
         }

         sb.append("]");
         return sb.toString();
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof ShortArrayTag)) {
         return false;
      } else {
         ShortArrayTag otherTag = (ShortArrayTag)obj;
         short[] thisData = this.getValue();
         short[] otherData = otherTag.getValue();
         return Arrays.equals(thisData, otherData);
      }
   }
}
