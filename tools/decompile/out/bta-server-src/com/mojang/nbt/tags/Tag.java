package com.mojang.nbt.tags;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.nbt.UnknownTagException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Tag<T> {
   @Nullable
   private String name = null;
   private T value;

   public Tag() {
   }

   public Tag(T value) {
      this.value = value;
   }

   abstract void read(@NotNull DataInput var1) throws IOException;

   abstract void write(@NotNull DataOutput var1) throws IOException;

   public abstract void fromJson(@NotNull JsonElement var1) throws JsonParseException;

   @NotNull
   public abstract JsonElement toJson();

   public abstract byte getId();

   @NotNull
   public String getTagName() {
      return this.name == null ? "" : this.name;
   }

   @NotNull
   public Tag<T> setName(String key) {
      this.name = key;
      return this;
   }

   public T getValue() {
      return this.value;
   }

   @NotNull
   public Tag<T> setValue(T value) {
      this.value = value;
      return this;
   }

   public static Tag<?> readNamedTag(@NotNull DataInput dis) throws IOException {
      byte typeId = dis.readByte();
      if (typeId == Tag.TagID.TAG_END.getId()) {
         return new EndTag();
      } else {
         try {
            Tag<?> tag = Tag.TagID.fromId(typeId).getNewTag();
            tag.name = dis.readUTF();
            tag.read(dis);
            return tag;
         } catch (IllegalArgumentException var3) {
            throw new UnknownTagException("Unknown tag type '" + typeId + "'!");
         }
      }
   }

   public static void writeNamedTag(@NotNull Tag<?> tag, @NotNull DataOutput dos) throws IOException {
      dos.writeByte(tag.getId());
      if (tag.getId() != Tag.TagID.TAG_END.getId()) {
         dos.writeUTF(tag.getTagName());
         tag.write(dos);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Tag)) {
         return false;
      } else {
         Tag<?> tag = (Tag<?>)obj;
         return Objects.equals(this.getValue(), tag.getValue());
      }
   }

   @NotNull
   public static <T> Tag<T> createTagOfType(@NotNull Class<T> type) {
      if (type.isArray()) {
         Class<?> arrayType = type.getComponentType();
         if (arrayType == Byte.class || arrayType == byte.class) {
            return new ByteArrayTag();
         } else if (arrayType == Short.class || arrayType == short.class) {
            return new ShortArrayTag();
         } else if (arrayType == Long.class || arrayType == long.class) {
            return new LongArrayTag();
         } else if (arrayType != Double.class && arrayType != double.class) {
            throw new IllegalArgumentException("No NBT Tag type for array of '" + arrayType + "'!");
         } else {
            return new DoubleArrayTag();
         }
      } else if (type == Byte.class || type == byte.class) {
         return new ByteTag();
      } else if (type == Short.class || type == short.class) {
         return new ShortTag();
      } else if (type == Integer.class || type == int.class) {
         return new IntTag();
      } else if (type == Long.class || type == long.class) {
         return new LongTag();
      } else if (type == Float.class || type == float.class) {
         return new FloatTag();
      } else if (type == Double.class || type == double.class) {
         return new DoubleTag();
      } else if (type == String.class) {
         return new StringTag();
      } else if (List.class.isAssignableFrom(type)) {
         return new ListTag();
      } else if (Map.class.isAssignableFrom(type)) {
         return new CompoundTag();
      } else {
         throw new IllegalArgumentException("No NBT Tag type for type '" + type + "'!");
      }
   }

   @NotNull
   public static JsonElement serializeToJson(Tag<?> tag) {
      JsonObject tagObject = new JsonObject();
      tagObject.addProperty("type", Tag.TagID.fromId(tag.getId()).getIdString());
      tagObject.add("value", tag.toJson());
      return tagObject;
   }

   @NotNull
   public static Tag<?> deserializeFromJson(@NotNull JsonElement element) throws JsonParseException {
      if (!element.isJsonObject()) {
         throw new JsonParseException("Tag element must be a JsonObject! " + element);
      } else {
         JsonObject object = element.getAsJsonObject();
         if (!object.has("type")) {
            throw new JsonParseException("Tag element does not have type defined!");
         } else {
            String type = object.get("type").getAsString();
            Tag<?> tag = Tag.TagID.fromIdString(type).getNewTag();
            if (object.has("value")) {
               tag.fromJson(object.get("value"));
            }

            return tag;
         }
      }
   }

   public static enum TagID {
      TAG_END(0, "TAG_End", EndTag::new),
      TAG_BYTE(1, "TAG_Byte", ByteTag::new),
      TAG_SHORT(2, "TAG_Short", ShortTag::new),
      TAG_INT(3, "TAG_Int", IntTag::new),
      TAG_LONG(4, "TAG_Long", LongTag::new),
      TAG_FLOAT(5, "TAG_Float", FloatTag::new),
      TAG_DOUBLE(6, "TAG_Double", DoubleTag::new),
      TAG_BYTE_ARRAY(7, "TAG_Byte_Array", ByteArrayTag::new),
      TAG_STRING(8, "TAG_String", StringTag::new),
      TAG_LIST(9, "TAG_List", ListTag::new),
      TAG_COMPOUND(10, "TAG_Compound", CompoundTag::new),
      TAG_SHORT_ARRAY(11, "TAG_Short_Array", ShortArrayTag::new),
      TAG_DOUBLE_ARRAY(12, "TAG_Double_Array", DoubleArrayTag::new),
      TAG_LONG_ARRAY(13, "TAG_Long_Array", LongArrayTag::new);

      private final byte id;
      @NotNull
      private final String name;
      @NotNull
      private final Supplier<? extends Tag<?>> tagSupplier;

      private TagID(final @NotNull int/* @NotNull */ id, @NotNull final String name, @NotNull final Supplier<? extends Tag<?>> tagSupplier) {
         this.id = (byte)id;
         this.name = name;
         this.tagSupplier = tagSupplier;
      }

      public byte getId() {
         return this.id;
      }

      @NotNull
      public String getIdString() {
         return this.name;
      }

      @NotNull
      public Tag<?> getNewTag() {
         return (Tag<?>)this.tagSupplier.get();
      }

      @NotNull
      public static Tag.TagID fromId(byte id) {
         switch (id) {
            case 0:
               return TAG_END;
            case 1:
               return TAG_BYTE;
            case 2:
               return TAG_SHORT;
            case 3:
               return TAG_INT;
            case 4:
               return TAG_LONG;
            case 5:
               return TAG_FLOAT;
            case 6:
               return TAG_DOUBLE;
            case 7:
               return TAG_BYTE_ARRAY;
            case 8:
               return TAG_STRING;
            case 9:
               return TAG_LIST;
            case 10:
               return TAG_COMPOUND;
            case 11:
               return TAG_SHORT_ARRAY;
            case 12:
               return TAG_DOUBLE_ARRAY;
            case 13:
               return TAG_LONG_ARRAY;
            default:
               throw new IllegalArgumentException("No NBT Tag type for id '" + id + "'!");
         }
      }

      @NotNull
      public static Tag.TagID fromIdString(@NotNull String idString) {
         switch (idString) {
            case "TAG_End":
               return TAG_END;
            case "TAG_Byte":
               return TAG_BYTE;
            case "TAG_Short":
               return TAG_SHORT;
            case "TAG_Int":
               return TAG_INT;
            case "TAG_Long":
               return TAG_LONG;
            case "TAG_Float":
               return TAG_FLOAT;
            case "TAG_Double":
               return TAG_DOUBLE;
            case "TAG_Byte_Array":
               return TAG_BYTE_ARRAY;
            case "TAG_String":
               return TAG_STRING;
            case "TAG_List":
               return TAG_LIST;
            case "TAG_Compound":
               return TAG_COMPOUND;
            case "TAG_Short_Array":
               return TAG_SHORT_ARRAY;
            case "TAG_Double_Array":
               return TAG_DOUBLE_ARRAY;
            case "TAG_Long_Array":
               return TAG_LONG_ARRAY;
            default:
               throw new IllegalArgumentException("No NBT Tag type for type '" + idString + "'!");
         }
      }
   }
}
