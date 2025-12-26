package com.mojang.nbt;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.jetbrains.annotations.NotNull;

public class NbtIo {
   @NotNull
   public static CompoundTag fromJson(@NotNull JsonElement element) throws JsonParseException {
      if (!element.isJsonObject()) {
         throw new JsonParseException("Root NBT Json element must be a JsonObject!");
      } else {
         CompoundTag compoundTag = new CompoundTag();
         compoundTag.fromJson(element);
         return compoundTag;
      }
   }

   @NotNull
   public static JsonElement toJson(@NotNull CompoundTag tag) {
      return tag.toJson();
   }

   @NotNull
   public static CompoundTag readCompressed(InputStream inputStream) throws IOException {
      DataInputStream datainputstream = new DataInputStream(new GZIPInputStream(inputStream));

      CompoundTag var2;
      try {
         var2 = read(datainputstream);
      } catch (Throwable var5) {
         try {
            datainputstream.close();
         } catch (Throwable var4) {
            var5.addSuppressed(var4);
         }

         throw var5;
      }

      datainputstream.close();
      return var2;
   }

   public static void writeCompressed(@NotNull CompoundTag tag, OutputStream outputStream) throws IOException {
      DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(outputStream));

      try {
         write(tag, dataoutputstream);
      } catch (Throwable var6) {
         try {
            dataoutputstream.close();
         } catch (Throwable var5) {
            var6.addSuppressed(var5);
         }

         throw var6;
      }

      dataoutputstream.close();
   }

   @NotNull
   public static CompoundTag read(DataInput dataInput) throws IOException {
      Tag<?> tag = Tag.readNamedTag(dataInput);
      if (!(tag instanceof CompoundTag)) {
         throw new IOException("Root tag must be a named compound tag!");
      } else {
         return (CompoundTag)tag;
      }
   }

   public static void write(@NotNull CompoundTag tag, DataOutput dataOutput) throws IOException {
      Tag.writeNamedTag(tag, dataOutput);
   }
}
