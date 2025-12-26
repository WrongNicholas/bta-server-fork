package com.mojang.nbt.tags;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

public class CompoundTag extends Tag<Map<String, Tag<?>>> {
   public CompoundTag() {
      this.setValue(new HashMap<>());
   }

   public CompoundTag(CompoundTag tag) {
      this.setValue(new HashMap<>());
      if (tag != null) {
         for (String key : tag.getValue().keySet()) {
            this.getValue().put(key, tag.getTag(key));
         }
      }
   }

   @Override
   public void write(@NotNull DataOutput dos) throws IOException {
      for (Tag<?> tag : this.getValue().values()) {
         writeNamedTag(tag, dos);
      }

      dos.writeByte(0);
   }

   @Override
   public void fromJson(@NotNull JsonElement element) throws JsonParseException {
      if (!element.isJsonObject()) {
         throw new JsonParseException("CompoundTag element must be a JsonObject!");
      } else {
         JsonObject object = element.getAsJsonObject();
         this.getValue().clear();

         for (Entry<String, JsonElement> entry : object.entrySet()) {
            this.put(entry.getKey(), Tag.deserializeFromJson(entry.getValue()));
         }
      }
   }

   @NotNull
   @Override
   public JsonElement toJson() {
      JsonObject out = new JsonObject();

      for (Entry<String, Tag<?>> entry : this.getValue().entrySet()) {
         out.add(entry.getKey(), Tag.serializeToJson(entry.getValue()));
      }

      return out;
   }

   @Override
   public void read(@NotNull DataInput dis) throws IOException {
      this.getValue().clear();

      Tag<?> tag;
      while ((tag = readNamedTag(dis)).getId() != 0) {
         this.getValue().put(tag.getTagName(), tag);
      }
   }

   public boolean containsKey(String key) {
      return this.getValue().containsKey(key);
   }

   public Collection<Tag<?>> getValues() {
      return this.getValue().values();
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_COMPOUND.getId();
   }

   public Tag<?> getTagOrDefault(String key, Tag<?> defaultTag) {
      return this.getValue().getOrDefault(key, defaultTag);
   }

   public Tag<?> getTag(String key) {
      return this.getTagOrDefault(key, null);
   }

   public void put(String key, Tag<?> tag) {
      this.getValue().put(key, tag.setName(key));
   }

   public boolean getBoolean(String key) {
      return this.getBooleanOrDefault(key, false);
   }

   public boolean getBooleanOrDefault(String key, boolean defaultValue) {
      return this.getByteOrDefault(key, (byte)(defaultValue ? 1 : 0)) != 0;
   }

   public void putBoolean(String key, boolean value) {
      this.putByte(key, (byte)(value ? 1 : 0));
   }

   public byte getByte(String key) {
      return this.getByteOrDefault(key, (byte)0);
   }

   public byte getByteOrDefault(String key, byte defaultValue) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof ByteTag) ? defaultValue : ((ByteTag)tag).getValue();
   }

   public void putByte(String key, byte value) {
      this.put(key, new ByteTag(value));
   }

   public short getShort(String key) {
      return this.getShortOrDefault(key, (short)0);
   }

   public short getShortOrDefault(String key, short defaultValue) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof ShortTag) ? defaultValue : ((ShortTag)tag).getValue();
   }

   public void putShort(String key, short value) {
      this.put(key, new ShortTag(value));
   }

   public int getInteger(String key) {
      return this.getIntegerOrDefault(key, 0);
   }

   public int getIntegerOrDefault(String key, int defaultValue) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof IntTag) ? defaultValue : ((IntTag)tag).getValue();
   }

   public void putInt(String key, int value) {
      this.put(key, new IntTag(value));
   }

   public long getLong(String key) {
      return this.getLongOrDefault(key, 0L);
   }

   public long getLongOrDefault(String key, long defaultValue) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof LongTag) ? defaultValue : ((LongTag)tag).getValue();
   }

   public void putLong(String key, long value) {
      this.put(key, new LongTag(value));
   }

   public float getFloat(String key) {
      return this.getFloatOrDefault(key, 0.0F);
   }

   public float getFloatOrDefault(String key, float defaultValue) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof FloatTag) ? defaultValue : ((FloatTag)tag).getValue();
   }

   public void putFloat(String key, float value) {
      this.put(key, new FloatTag(value));
   }

   public double getDouble(String key) {
      return this.getDoubleOrDefault(key, 0.0);
   }

   public double getDoubleOrDefault(String key, double defaultValue) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof DoubleTag) ? defaultValue : ((DoubleTag)tag).getValue();
   }

   public void putDouble(String key, double value) {
      this.put(key, new DoubleTag(value));
   }

   public String getString(String key) {
      return this.getStringOrDefault(key, "");
   }

   public String getStringOrDefault(String key, String defaultValue) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof StringTag) ? defaultValue : ((StringTag)tag).getValue();
   }

   public void putString(String key, String value) {
      this.put(key, new StringTag(value));
   }

   public byte[] getByteArray(String key) {
      return this.getByteArrayOrDefault(key, new byte[0]);
   }

   public byte[] getByteArrayOrDefault(String key, byte[] defaultArray) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof ByteArrayTag) ? defaultArray : ((ByteArrayTag)tag).getValue();
   }

   public void putByteArray(String key, byte[] array) {
      this.put(key, new ByteArrayTag(array));
   }

   public short[] getShortArray(String key) {
      return this.getShortArrayOrDefault(key, new short[0]);
   }

   public short[] getShortArrayOrDefault(String key, short[] defaultArray) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof ShortArrayTag) ? defaultArray : ((ShortArrayTag)tag).getValue();
   }

   public void putShortArray(String key, short[] array) {
      this.put(key, new ShortArrayTag(array));
   }

   public double[] getDoubleArray(String key) {
      return this.getDoubleArrayOrDefault(key, new double[0]);
   }

   public double[] getDoubleArrayOrDefault(String key, double[] defaultArray) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof DoubleArrayTag) ? defaultArray : ((DoubleArrayTag)tag).getValue();
   }

   public void putDoubleArray(String key, double[] array) {
      this.put(key, new DoubleArrayTag(array));
   }

   public long[] getLongArray(String key) {
      return this.getLongArrayOrDefault(key, new long[0]);
   }

   public long[] getLongArrayOrDefault(String key, long[] defaultArray) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof LongArrayTag) ? defaultArray : ((LongArrayTag)tag).getValue();
   }

   public void putLongArray(String key, long[] array) {
      this.put(key, new LongArrayTag(array));
   }

   public CompoundTag getCompound(String key) {
      return this.getCompoundOrDefault(key, new CompoundTag());
   }

   public CompoundTag getCompoundOrDefault(String key, CompoundTag defaultCompound) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof CompoundTag) ? defaultCompound : (CompoundTag)tag;
   }

   public void putCompound(String key, CompoundTag compound) {
      this.put(key, compound);
   }

   public ListTag getList(String key) {
      return this.getListOrDefault(key, new ListTag());
   }

   public ListTag getListOrDefault(String key, ListTag defaultList) {
      Tag<?> tag = this.getTagOrDefault(key, null);
      return !(tag instanceof ListTag) ? defaultList : (ListTag)tag;
   }

   public void putList(String key, ListTag list) {
      this.put(key, list);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("\"").append(this.getTagName()).append("\": { ");

      for (Tag<?> tag : this.getValue().values()) {
         sb.append(tag.toString()).append("; ");
      }

      sb.append("}");
      return sb.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof CompoundTag)) {
         return false;
      } else {
         Map<String, Tag<?>> data1 = this.getValue();
         Map<String, Tag<?>> data2 = ((CompoundTag)o).getValue();
         if (data1.size() != data2.size()) {
            return false;
         } else {
            for (String key : data1.keySet()) {
               Tag<?> value1 = this.getTag(key);
               Tag<?> value2 = ((CompoundTag)o).getTag(key);
               if (value1 != null && value2 != null) {
                  if (!value1.equals(value2)) {
                     return false;
                  }
               } else if (value1 != value2) {
                  return false;
               }
            }

            return true;
         }
      }
   }
}
