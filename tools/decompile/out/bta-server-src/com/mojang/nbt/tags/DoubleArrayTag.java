package com.mojang.nbt.tags;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public class DoubleArrayTag extends Tag<double[]> {
   public DoubleArrayTag() {
      this(new double[0]);
   }

   public DoubleArrayTag(double[] array) {
      super(array);
   }

   @Override
   void write(@NotNull DataOutput dos) throws IOException {
      dos.writeInt(this.getValue().length);
      byte[] bytes = new byte[((double[])this.getValue()).length * 8];
      ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().put(this.getValue());
      dos.write(bytes);
   }

   @Override
   public void fromJson(@NotNull JsonElement element) throws JsonParseException {
      if (!element.isJsonArray()) {
         throw new JsonParseException("DoubleArrayTag json value must be a json array!");
      } else {
         JsonArray array = element.getAsJsonArray();
         double[] doubles = new double[array.size()];

         for (int i = 0; i < doubles.length; i++) {
            doubles[i] = array.get(i).getAsDouble();
         }

         this.setValue(doubles);
      }
   }

   @NotNull
   @Override
   public JsonElement toJson() {
      double[] doubles = this.getValue();
      JsonArray array = new JsonArray(doubles.length);

      for (int i = 0; i < doubles.length; i++) {
         array.add(doubles[i]);
      }

      return array;
   }

   @Override
   void read(@NotNull DataInput dis) throws IOException {
      int length = dis.readInt();
      this.setValue(new double[length]);
      byte[] bytes = new byte[length * 8];
      dis.readFully(bytes);
      ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().get(this.getValue());
   }

   @Override
   public byte getId() {
      return Tag.TagID.TAG_DOUBLE_ARRAY.getId();
   }

   @Override
   public String toString() {
      if (this.getValue().length > 16) {
         return "\"" + this.getTagName() + "\": [ " + this.getValue().length + " doubles ]";
      } else {
         StringBuilder sb = new StringBuilder();
         sb.append("\"").append(this.getTagName()).append("\": [ ");

         for (double d : this.getValue()) {
            sb.append(d).append(", ");
         }

         sb.append("];");
         return sb.toString();
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof DoubleArrayTag)) {
         return false;
      } else {
         DoubleArrayTag otherTag = (DoubleArrayTag)obj;
         double[] thisData = this.getValue();
         double[] otherData = otherTag.getValue();
         return Arrays.equals(thisData, otherData);
      }
   }
}
