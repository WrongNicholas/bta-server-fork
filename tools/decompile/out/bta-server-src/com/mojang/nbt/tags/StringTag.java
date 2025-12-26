package com.mojang.nbt.tags;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class StringTag extends Tag<String> {
   public StringTag() {
      this("");
   }

   public StringTag(@NotNull String value) {
      super(Objects.requireNonNull(value, "Null string not allowed!"));
   }

   @Override
   void write(@NotNull DataOutput dos) throws IOException {
      dos.writeUTF(this.getValue());
   }

   @Override
   void read(@NotNull DataInput dis) throws IOException {
      this.setValue(dis.readUTF());
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_STRING.getId();
   }

   @Override
   public void fromJson(@NotNull JsonElement json) throws JsonParseException {
      if (!json.isJsonPrimitive()) {
         throw new JsonParseException("Json element '" + json + "' is not a valid stringTag!");
      } else {
         JsonPrimitive primitive = json.getAsJsonPrimitive();
         if (!primitive.isString()) {
            throw new JsonParseException("Json element '" + json + "' is not a string!");
         } else {
            this.setValue(primitive.getAsString());
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
      return "\"" + this.getTagName() + "\": \"" + this.getValue() + "\"";
   }
}
