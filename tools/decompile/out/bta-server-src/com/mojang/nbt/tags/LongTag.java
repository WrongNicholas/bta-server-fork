package com.mojang.nbt.tags;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class LongTag extends Tag<Long> {
   public LongTag() {
      this(0L);
   }

   public LongTag(long value) {
      super(value);
   }

   @Override
   void write(@NotNull DataOutput dos) throws IOException {
      dos.writeLong(this.getValue());
   }

   @Override
   void read(@NotNull DataInput dis) throws IOException {
      this.setValue(dis.readLong());
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_LONG.getId();
   }

   @Override
   public void fromJson(@NotNull JsonElement json) throws JsonParseException {
      if (!json.isJsonPrimitive()) {
         throw new JsonParseException("Json element '" + json + "' is not a valid longTag!");
      } else {
         JsonPrimitive primitive = json.getAsJsonPrimitive();
         if (!primitive.isNumber()) {
            throw new JsonParseException("Json element '" + json + "' is not a number!");
         } else {
            this.setValue(primitive.getAsLong());
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
