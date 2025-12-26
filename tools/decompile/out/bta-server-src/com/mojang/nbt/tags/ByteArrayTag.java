package com.mojang.nbt.tags;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public class ByteArrayTag extends Tag<byte[]> {
   public ByteArrayTag() {
      this(new byte[0]);
   }

   public ByteArrayTag(byte[] array) {
      super(array);
   }

   @Override
   void write(@NotNull DataOutput dos) throws IOException {
      dos.writeInt(this.getValue().length);
      dos.write(this.getValue());
   }

   @Override
   public void fromJson(@NotNull JsonElement element) throws JsonParseException {
      if (!element.isJsonArray()) {
         throw new JsonParseException("ByteArrayTag json value must be a json array!");
      } else {
         JsonArray array = element.getAsJsonArray();
         byte[] bytes = new byte[array.size()];

         for (int i = 0; i < bytes.length; i++) {
            JsonElement e = array.get(i);
            if (!e.isJsonPrimitive()) {
               throw new JsonParseException("ByteArrayTag json value must be a json array values must all be numbers!");
            }

            JsonPrimitive p = e.getAsJsonPrimitive();
            if (!p.isNumber()) {
               throw new JsonParseException("ByteArrayTag json value must be a json array values must all be numbers!");
            }

            bytes[i] = p.getAsByte();
         }

         this.setValue(bytes);
      }
   }

   @NotNull
   @Override
   public JsonElement toJson() {
      byte[] bytes = this.getValue();
      JsonArray array = new JsonArray(bytes.length);

      for (int i = 0; i < bytes.length; i++) {
         array.add(bytes[i]);
      }

      return array;
   }

   @Override
   void read(@NotNull DataInput dis) throws IOException {
      int length = dis.readInt();
      this.setValue(new byte[length]);
      dis.readFully(this.getValue());
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_BYTE_ARRAY.getId();
   }

   @Override
   public String toString() {
      if (this.getValue().length > 16) {
         return "\"" + this.getTagName() + "\": [ " + this.getValue().length + " bytes ]";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("\"").append(this.getTagName()).append("\": [ ");

         for (byte b : this.getValue()) {
            sb.append((int)b).append(", ");
         }

         sb.append("]");
         return sb.toString();
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof ByteArrayTag)) {
         return false;
      } else {
         ByteArrayTag otherTag = (ByteArrayTag)obj;
         byte[] thisData = this.getValue();
         byte[] otherData = otherTag.getValue();
         return Arrays.equals(thisData, otherData);
      }
   }
}
