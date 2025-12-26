package com.mojang.nbt.tags;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class IntTag extends Tag<Integer> {
   public IntTag() {
      this(0);
   }

   public IntTag(int value) {
      super(value);
   }

   @Override
   void write(@NotNull DataOutput dos) throws IOException {
      dos.writeInt(this.getValue());
   }

   @Override
   void read(@NotNull DataInput dis) throws IOException {
      this.setValue(dis.readInt());
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_INT.getId();
   }

   @Override
   public void fromJson(@NotNull JsonElement json) throws JsonParseException {
      if (!json.isJsonPrimitive()) {
         throw new JsonParseException("Json element '" + json + "' is not a valid intTag!");
      } else {
         JsonPrimitive primitive = json.getAsJsonPrimitive();
         if (!primitive.isNumber()) {
            throw new JsonParseException("Json element '" + json + "' is not a number!");
         } else {
            this.setValue(primitive.getAsInt());
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
