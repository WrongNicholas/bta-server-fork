package com.mojang.nbt.tags;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.nbt.UnknownTagException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class ListTag extends Tag<List<Tag<?>>> implements Iterable<Tag<?>> {
   private byte tagType;

   public ListTag() {
      this(new ArrayList<>());
   }

   public ListTag(List<Tag<?>> array) {
      super(array);
   }

   @Override
   void write(@NotNull DataOutput dos) throws IOException {
      if (!this.getValue().isEmpty()) {
         this.tagType = this.getValue().get(0).getId();
      } else {
         this.tagType = 1;
      }

      dos.writeByte(this.tagType);
      dos.writeInt(this.getValue().size());

      for (int i = 0; i < this.getValue().size(); i++) {
         this.getValue().get(i).write(dos);
      }
   }

   @Override
   public void fromJson(@NotNull JsonElement element) throws JsonParseException {
      if (!element.isJsonObject()) {
         throw new JsonParseException("ListTag JsonElement must be a json object!");
      } else {
         JsonObject asObject = element.getAsJsonObject();
         if (!asObject.has("type")) {
            throw new JsonParseException("ListTag must have a defined type!");
         } else {
            Tag.TagID type = Tag.TagID.fromIdString(asObject.get("type").getAsString());
            this.getValue().clear();
            if (asObject.has("list")) {
               for (JsonElement e : asObject.getAsJsonArray("list")) {
                  Tag<?> entry = type.getNewTag();
                  entry.fromJson(e);
                  this.addTag(entry);
               }
            }
         }
      }
   }

   @NotNull
   @Override
   public JsonElement toJson() {
      JsonObject output = new JsonObject();
      output.addProperty("type", Tag.TagID.fromId(this.tagType).getIdString());
      JsonArray list = new JsonArray(this.getValue().size());

      for (Tag<?> tag : this.getValue()) {
         list.add(tag.toJson());
      }

      output.add("list", list);
      return output;
   }

   @Override
   void read(@NotNull DataInput dis) throws IOException {
      this.tagType = dis.readByte();
      int length = dis.readInt();
      this.setValue(new ArrayList<>());

      for (int i = 0; i < length; i++) {
         try {
            Tag<?> tag = Tag.TagID.fromId(this.tagType).getNewTag();
            tag.read(dis);
            this.getValue().add(tag);
         } catch (IllegalArgumentException var5) {
            throw new UnknownTagException("Unknown tag type '" + this.tagType + "'!");
         }
      }
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_LIST.getId();
   }

   @Override
   public String toString() {
      if (this.getValue().size() > 16) {
         return "\"" + this.getTagName() + "\": [ " + this.getValue().size() + " elements ]";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("\"").append(this.getTagName()).append("\": [ ");

         for (Tag<?> d : this.getValue()) {
            sb.append(d).append(", ");
         }

         sb.append("]");
         return sb.toString();
      }
   }

   public void addTag(Tag<?> tag) {
      this.tagType = tag.getId();
      this.getValue().add(tag);
   }

   public Tag<?> tagAt(int index) {
      return this.getValue().get(index);
   }

   public int tagCount() {
      return this.getValue().size();
   }

   @NotNull
   @Override
   public Iterator<Tag<?>> iterator() {
      return this.getValue().iterator();
   }
}
