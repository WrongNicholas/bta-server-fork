package com.mojang.nbt.tags;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class EndTag extends Tag<Void> {
   @Override
   void read(@NotNull DataInput dis) throws IOException {
   }

   @Override
   void write(@NotNull DataOutput dos) throws IOException {
   }

   @Override
   public void fromJson(@NotNull JsonElement element) throws JsonParseException {
   }

   @NotNull
   @Override
   public JsonElement toJson() {
      return JsonNull.INSTANCE;
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_END.getId();
   }

   @Override
   public String toString() {
      return "\"" + this.getTagName() + "\": END";
   }
}
