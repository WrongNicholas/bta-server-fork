package com.mojang.nbt.tags;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class ShortTag extends Tag<Short> {
   public ShortTag() {
      this((short)0);
   }

   public ShortTag(short value) {
      super(value);
   }

   @Override
   void write(@NotNull DataOutput dos) throws IOException {
      dos.writeShort(this.getValue());
   }

   @Override
   void read(@NotNull DataInput dis) throws IOException {
      this.setValue(dis.readShort());
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_SHORT.getId();
   }

   @Override
   public void fromJson(@NotNull JsonElement json) throws JsonParseException {
      if (!json.isJsonPrimitive()) {
         throw new JsonParseException("Json element '" + json + "' is not a valid shortTag!");
      } else {
         JsonPrimitive primitive = json.getAsJsonPrimitive();
         if (!primitive.isNumber()) {
            throw new JsonParseException("Json element '" + json + "' is not a number!");
         } else {
            this.setValue(primitive.getAsShort());
         }
      }
   }

   @NotNull
   @Override
   public JsonElement toJson() {
      return new JsonPrimitive(this.getValue());
   }

   @Override
   public String toString() {
      return "\"" + this.getTagName() + "\": " + this.getValue();
   }
}
