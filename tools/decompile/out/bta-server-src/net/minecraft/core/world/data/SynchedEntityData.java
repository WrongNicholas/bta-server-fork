package net.minecraft.core.world.data;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SynchedEntityData {
   public static final int TYPE_BYTE = 0;
   public static final int TYPE_SHORT = 1;
   public static final int TYPE_INT = 2;
   public static final int TYPE_FLOAT = 3;
   public static final int TYPE_STRING = 4;
   public static final int TYPE_ITEMSTACK = 5;
   public static final int TYPE_CHUNK_COORDINATE = 6;
   public static final int TYPE_UUID = 7;
   public static final int MAX_DATA_VALUE = 31;
   public static final int META_NULL = 1;
   public static final int MAX_STRING_SIZE = 1024;
   public static final int END_MARKER = 255;
   private static final HashMap<Class<?>, Integer> DATA_TYPES = new HashMap<>();
   private final Map<Integer, SynchedEntityData.@NotNull DataItem<?>> itemsById = new HashMap<>();
   private boolean isDirty = true;

   public <T> void define(int id, T value, Class<T> valueClass) {
      Integer integer = DATA_TYPES.get(valueClass);
      if (integer == null) {
         throw new IllegalArgumentException("Unknown data type: " + valueClass);
      } else if (id > 31) {
         throw new IllegalArgumentException("Data value id is too big with " + id + "! (Max is " + 31 + ")");
      } else if (id < 0) {
         throw new IllegalArgumentException("Data value id must be positive! Wrong id " + id);
      } else if (this.itemsById.containsKey(id)) {
         throw new IllegalArgumentException("Duplicate id value for " + id + "!");
      } else {
         SynchedEntityData.DataItem<T> item = new SynchedEntityData.DataItem<>(integer, id, value);
         this.itemsById.put(id, item);
      }
   }

   public byte getByte(int id) {
      return (Byte)this.itemsById.get(id).getValue();
   }

   public short getShort(int id) {
      return (Short)this.itemsById.get(id).getValue();
   }

   public int getInt(int id) {
      return (Integer)this.itemsById.get(id).getValue();
   }

   public String getString(int id) {
      return (String)this.itemsById.get(id).getValue();
   }

   public ItemStack getItemStack(int id) {
      return (ItemStack)this.itemsById.get(id).getValue();
   }

   public UUID getUUID(int id) {
      return (UUID)this.itemsById.get(id).getValue();
   }

   public <T> void set(int id, @Nullable T value) {
      SynchedEntityData.DataItem<T> item = (SynchedEntityData.DataItem<T>)this.itemsById.get(id);
      if (value == null && item.getValue() != null) {
         item.setValue(null);
         item.setDirty(true);
         this.isDirty = true;
      } else {
         if (value != null && !value.equals(item.getValue())) {
            item.setValue(value);
            item.setDirty(true);
            this.isDirty = true;
         }
      }
   }

   public boolean isDirty() {
      return this.isDirty;
   }

   public static void pack(List<SynchedEntityData.DataItem<?>> items, DataOutputStream dos) throws IOException {
      if (items != null) {
         for (SynchedEntityData.DataItem<?> di : items) {
            writeDataItem(dos, di);
         }
      }

      dos.writeByte(255);
   }

   public List<SynchedEntityData.DataItem<?>> packDirty() {
      ArrayList<SynchedEntityData.DataItem<?>> out = null;
      if (this.isDirty) {
         for (SynchedEntityData.DataItem<?> di : this.itemsById.values()) {
            if (di.isDirty()) {
               di.setDirty(false);
               if (out == null) {
                  out = new ArrayList<>();
               }

               out.add(di);
            }
         }
      }

      this.isDirty = false;
      return out;
   }

   public void packAll(DataOutputStream dos) throws IOException {
      for (SynchedEntityData.DataItem<?> di : this.itemsById.values()) {
         writeDataItem(dos, di);
      }

      dos.writeByte(255);
   }

   private static <T> void writeDataItem(DataOutputStream dos, SynchedEntityData.DataItem<T> di) throws IOException {
      dos.writeByte(di.getId() & 0xFF);
      dos.writeByte(di.getType() & 0xFF);
      byte metadata = 0;
      T value = di.getValue();
      if (value == null) {
         metadata = (byte)(metadata | 1);
         dos.writeByte(metadata);
      } else {
         switch (di.getType()) {
            case 0:
               dos.write(metadata);
               dos.writeByte((Byte)value);
               break;
            case 1:
               dos.write(metadata);
               dos.writeShort((Short)value);
               break;
            case 2:
               dos.write(metadata);
               dos.writeInt((Integer)value);
               break;
            case 3:
               dos.write(metadata);
               dos.writeFloat((Float)value);
               break;
            case 4:
               String s = (String)value;
               dos.write(metadata);
               Packet.writeStringUTF8(s, dos);
               break;
            case 5:
               ItemStack itemStack = (ItemStack)value;
               dos.write(metadata);
               dos.writeShort(itemStack.getItem().id);
               dos.writeByte(itemStack.stackSize);
               dos.writeShort(itemStack.getMetadata());
               Packet.writeCompressedCompoundTag(itemStack.getData(), dos);
               break;
            case 6:
               ChunkCoordinates chunkcoordinates = (ChunkCoordinates)value;
               dos.write(metadata);
               dos.writeInt(chunkcoordinates.x);
               dos.writeInt(chunkcoordinates.y);
               dos.writeInt(chunkcoordinates.z);
               break;
            case 7:
               UUID uuid = (UUID)value;
               dos.write(metadata);
               dos.writeLong(uuid.getMostSignificantBits());
               dos.writeLong(uuid.getLeastSignificantBits());
         }
      }
   }

   @Nullable
   public static List<SynchedEntityData.DataItem<?>> unpack(DataInputStream dis) throws IOException {
      ArrayList<SynchedEntityData.DataItem<?>> out = null;

      int id;
      while ((id = dis.readByte() & 255) != 255) {
         if (out == null) {
            out = new ArrayList<>();
         }

         int type = dis.readByte() & 255;
         byte metadata = dis.readByte();
         SynchedEntityData.DataItem<?> di = new SynchedEntityData.DataItem(type, id, null);
         if ((metadata & 1) == 0) {
            switch (type) {
               case 0:
                  di = new SynchedEntityData.DataItem<>(type, id, dis.readByte());
                  break;
               case 1:
                  di = new SynchedEntityData.DataItem<>(type, id, dis.readShort());
                  break;
               case 2:
                  di = new SynchedEntityData.DataItem<>(type, id, dis.readInt());
                  break;
               case 3:
                  di = new SynchedEntityData.DataItem<>(type, id, dis.readFloat());
                  break;
               case 4:
                  di = new SynchedEntityData.DataItem<>(type, id, Packet.readStringUTF8(dis, 1024));
                  break;
               case 5:
                  short itemId = dis.readShort();
                  byte itemCount = dis.readByte();
                  short itemData = dis.readShort();
                  CompoundTag tag = Packet.readCompressedCompoundTag(dis);
                  di = new SynchedEntityData.DataItem<>(type, id, new ItemStack(itemId, itemCount, itemData, tag));
                  break;
               case 6:
                  int x = dis.readInt();
                  int y = dis.readInt();
                  int z = dis.readInt();
                  di = new SynchedEntityData.DataItem<>(type, id, new ChunkCoordinates(x, y, z));
                  break;
               case 7:
                  long msb = dis.readLong();
                  long lsb = dis.readLong();
                  di = new SynchedEntityData.DataItem<>(type, id, new UUID(msb, lsb));
            }
         }

         out.add(di);
      }

      return out;
   }

   public void assignValues(@NotNull List<SynchedEntityData.DataItem<?>> dataItems) {
      for (SynchedEntityData.DataItem<?> di : dataItems) {
         SynchedEntityData.DataItem<?> currentDi = this.itemsById.get(di.getId());
         if (currentDi != null) {
            ((SynchedEntityData.DataItem<Object>)currentDi).setValue(di.getValue());
         }
      }
   }

   static {
      DATA_TYPES.put(Byte.class, 0);
      DATA_TYPES.put(Short.class, 1);
      DATA_TYPES.put(Integer.class, 2);
      DATA_TYPES.put(Float.class, 3);
      DATA_TYPES.put(String.class, 4);
      DATA_TYPES.put(ItemStack.class, 5);
      DATA_TYPES.put(ChunkCoordinates.class, 6);
      DATA_TYPES.put(UUID.class, 7);
   }

   public static class DataItem<T> {
      private final int type;
      private final int id;
      @Nullable
      private T value;
      private boolean dirty;

      public DataItem(int type, int id, @Nullable T value) {
         this.id = id;
         this.value = value;
         this.type = type;
         this.dirty = true;
      }

      public int getId() {
         return this.id;
      }

      public void setValue(@Nullable T value) {
         this.value = value;
      }

      @Nullable
      public T getValue() {
         return this.value;
      }

      public int getType() {
         return this.type;
      }

      public boolean isDirty() {
         return this.dirty;
      }

      public void setDirty(boolean dirty) {
         this.dirty = dirty;
      }

      @Override
      public String toString() {
         return "DataItem{type=" + this.type + ", id=" + this.id + ", value=" + this.value + ", dirty=" + this.dirty + '}';
      }
   }
}
