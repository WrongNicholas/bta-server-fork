package com.mojang.nbt.tags;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class DoubleTag extends Tag<Double> {
   public DoubleTag() {
      this(0.0);
   }

   public DoubleTag(double value) {
      super(value);
   }

   @Override
   void write(@NotNull DataOutput dos) throws IOException {
      dos.writeDouble(this.getValue());
   }

   @Override
   public void fromJson(@NotNull JsonElement json) throws JsonParseException {
      if (!json.isJsonPrimitive()) {
         throw new JsonParseException("Json element '" + json + "' is not a valid doubleTag!");
      } else {
         this.setValue(json.getAsDouble());
      }
   }

   @NotNull
   @Override
   public JsonElement toJson() {
      return new JsonPrimitive(this.getValue());
   }

   @Override
   void read(@NotNull DataInput dis) throws IOException {
      this.setValue(dis.readDouble());
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_DOUBLE.getId();
   }

   @Override
   public String toString() {
      return "\"" + this.getTagName() + "\": " + this.getValue();
   }
}
