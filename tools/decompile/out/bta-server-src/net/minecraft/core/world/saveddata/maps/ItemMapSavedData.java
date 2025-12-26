package net.minecraft.core.world.saveddata.maps;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

public class ItemMapSavedData extends SavedData {
   public static final byte MAX_SCALE = 4;
   public int x;
   public int z;
   public byte dimension;
   public byte scale;
   public byte[] colors;
   public int tick;
   public List<ItemMapSavedData.HoldingPlayer> carriedBy;
   private final Map<Player, ItemMapSavedData.HoldingPlayer> carriedByPlayers;
   public List<ItemMapSavedData.MapDecoration> decorations;
   public final int MAX_WAYPOINTS = 32;
   public List<MapWaypoint> mapWaypoints;

   public ItemMapSavedData(String id) {
      super(id);
      this.colors = new byte[16384];
      this.carriedBy = new ArrayList<>();
      this.carriedByPlayers = new HashMap<>();
      this.decorations = new ArrayList<>();
      this.mapWaypoints = new ArrayList<>();
   }

   @Override
   public void load(CompoundTag tag) {
      this.dimension = tag.getByte("dimension");
      this.x = tag.getInteger("xCenter");
      this.z = tag.getInteger("zCenter");
      this.scale = tag.getByte("scale");
      if (this.scale < 0) {
         this.scale = 0;
      }

      if (this.scale > 4) {
         this.scale = 4;
      }

      short width = tag.getShort("width");
      short height = tag.getShort("height");
      if (width == 128 && height == 128) {
         this.colors = tag.getByteArray("colors");
      } else {
         byte[] abyte0 = tag.getByteArray("colors");
         this.colors = new byte[16384];
         int i = (128 - width) / 2;
         int j = (128 - height) / 2;

         for (int k = 0; k < height; k++) {
            int l = k + j;

            for (int i1 = 0; i1 < width; i1++) {
               int j1 = i1 + i;
               this.colors[j1 + l * 128] = abyte0[i1 + k * width];
            }
         }
      }

      ListTag waypointsList = tag.getList("Waypoints");

      for (int index = 0; index < waypointsList.tagCount(); index++) {
         CompoundTag waypointNBT = (CompoundTag)waypointsList.tagAt(index);
         this.mapWaypoints.add(new MapWaypoint(waypointNBT));
      }
   }

   @Override
   public void save(CompoundTag tag) {
      tag.putByte("dimension", this.dimension);
      tag.putInt("xCenter", this.x);
      tag.putInt("zCenter", this.z);
      tag.putByte("scale", this.scale);
      tag.putShort("width", (short)128);
      tag.putShort("height", (short)128);
      tag.putByteArray("colors", this.colors);
      ListTag waypointsList = new ListTag();

      for (MapWaypoint mapWaypoint : this.mapWaypoints) {
         if (mapWaypoint != null) {
            CompoundTag waypointNBT = new CompoundTag();
            mapWaypoint.writeToNBT(waypointNBT);
            waypointsList.addTag(waypointNBT);
         }
      }

      tag.put("Waypoints", waypointsList);
   }

   public void tickForWaypoints(Player player, ItemStack stack) {
      for (MapWaypoint mapWaypoint : this.mapWaypoints) {
         if (player.distanceToSqr(mapWaypoint.xPos, mapWaypoint.yPos, mapWaypoint.zPos) <= 256.0
            && player.world.getBlockId(mapWaypoint.xPos, mapWaypoint.yPos, mapWaypoint.zPos) != Blocks.FLAG.id()) {
            this.mapWaypoints.remove(mapWaypoint);
            break;
         }
      }
   }

   public void tickCarriedBy(Player player, ItemStack stack) {
      if (!this.carriedByPlayers.containsKey(player)) {
         ItemMapSavedData.HoldingPlayer mapinfo = new ItemMapSavedData.HoldingPlayer(player);
         this.carriedByPlayers.put(player, mapinfo);
         this.carriedBy.add(mapinfo);
      }

      this.decorations.clear();

      for (int i = 0; i < this.carriedBy.size(); i++) {
         ItemMapSavedData.HoldingPlayer mapinfo1 = this.carriedBy.get(i);
         if (!mapinfo1.player.removed && mapinfo1.player.inventory.func_28018_c(stack)) {
            float f = (float)(mapinfo1.player.x - this.x) / (1 << this.scale);
            float f1 = (float)(mapinfo1.player.z - this.z) / (1 << this.scale);
            byte color = 0;
            byte xCoord = (byte)MathHelper.clamp(f * 2.0F + 0.5, -128.0, 127.0);
            byte zCoord = (byte)MathHelper.clamp(f1 * 2.0F + 0.5, -128.0, 127.0);
            byte rotation = (byte)Math.round(player.yRot % 360.0F * 16.0F / 360.0F);
            if (this.dimension == 1) {
               int l = this.tick / 10;
               rotation = (byte)(l * l * 34187121 + l * 121 >> 15 & 15);
            }

            if (mapinfo1.player.dimension == this.dimension) {
               this.decorations.add(new ItemMapSavedData.MapDecoration(color, xCoord, zCoord, (byte)(rotation % 16)));
            }
         } else {
            this.carriedByPlayers.remove(mapinfo1.player);
            this.carriedBy.remove(mapinfo1);
         }
      }
   }

   @NotNull
   public ItemMapSavedData.FlagError createNewWaypoint(TileEntityFlag flag) {
      if (this.mapWaypoints.size() >= 32) {
         return ItemMapSavedData.FlagError.FULL;
      } else {
         float xd = (float)((double)flag.x - this.x) / (1 << this.scale);
         float zd = (float)((double)flag.z - this.z) / (1 << this.scale);
         int maxWidth = 64;
         int maxHeight = 64;
         byte xCoord = (byte)(xd * 2.0F + 0.5);
         byte zCoord = (byte)(zd * 2.0F + 0.5);
         if (!(xd < -maxWidth) && !(zd < -maxHeight) && !(xd > maxWidth) && !(zd > maxHeight)) {
            byte[] colors = new byte[9];

            for (int y = 0; y < 3; y++) {
               for (int x = 0; x < 3; x++) {
                  colors[x * 3 + y] = flag.getColor(12 * x, 8 * y);
               }
            }

            this.mapWaypoints.add(new MapWaypoint(xCoord, zCoord, flag.x, flag.y, flag.z, colors));
            return ItemMapSavedData.FlagError.NONE;
         } else {
            return ItemMapSavedData.FlagError.OUTSIDE_RANGE;
         }
      }
   }

   public byte[] getMapDataBytes(ItemStack stack, World world, Player player) {
      ItemMapSavedData.HoldingPlayer mapinfo = this.carriedByPlayers.get(player);
      return mapinfo == null ? null : mapinfo.nextUpdatePacket(stack);
   }

   public void setDirty(int x, int y, int z) {
      super.setDirty();

      for (ItemMapSavedData.HoldingPlayer mapinfo : this.carriedBy) {
         if (mapinfo.minDirty[x] < 0 || mapinfo.minDirty[x] > y) {
            mapinfo.minDirty[x] = y;
         }

         if (mapinfo.maxDirty[x] < 0 || mapinfo.maxDirty[x] < z) {
            mapinfo.maxDirty[x] = z;
         }
      }
   }

   public ItemMapSavedData setColors(byte[] colors) {
      if (colors[0] == 0) {
         int i = colors[1] & 255;
         int k = colors[2] & 255;

         for (int l = 0; l < colors.length - 3; l++) {
            this.colors[(l + k) * 128 + i] = colors[l + 3];
         }

         this.setDirty();
      } else if (colors[0] == 1) {
         this.decorations.clear();

         for (int j = 0; j < (colors.length - 1) / 3; j++) {
            byte byte0 = (byte)(colors[j * 3 + 1] % 16);
            byte byte1 = colors[j * 3 + 2];
            byte byte2 = colors[j * 3 + 3];
            byte byte3 = (byte)(colors[j * 3 + 1] / 16);
            this.decorations.add(new ItemMapSavedData.MapDecoration(byte0, byte1, byte2, byte3));
         }
      }

      return this;
   }

   public void setMapWaypoints(List<MapWaypoint> waypoints) {
      this.mapWaypoints = waypoints;
   }

   public static enum FlagError {
      NONE,
      FULL,
      OUTSIDE_RANGE;
   }

   public class HoldingPlayer {
      public final Player player;
      public int[] minDirty = new int[128];
      public int[] maxDirty = new int[128];
      private int tick = 0;
      private int step = 0;
      private byte[] packetBuffer;

      public HoldingPlayer(Player player) {
         this.player = player;

         for (int i = 0; i < this.minDirty.length; i++) {
            this.minDirty[i] = 0;
            this.maxDirty[i] = 127;
         }
      }

      public byte[] nextUpdatePacket(ItemStack stack) {
         if (--this.step < 0) {
            this.step = 4;
            byte[] mapCoordinates = new byte[ItemMapSavedData.this.decorations.size() * 3 + 1];
            mapCoordinates[0] = 1;

            for (int j = 0; j < ItemMapSavedData.this.decorations.size(); j++) {
               ItemMapSavedData.MapDecoration decoration = ItemMapSavedData.this.decorations.get(j);
               mapCoordinates[j * 3 + 1] = (byte)(decoration.type + (decoration.rot & 15) * 16);
               mapCoordinates[j * 3 + 2] = decoration.x;
               mapCoordinates[j * 3 + 3] = decoration.y;
            }

            boolean flag = true;
            if (this.packetBuffer != null && this.packetBuffer.length == mapCoordinates.length) {
               for (int l = 0; l < mapCoordinates.length; l++) {
                  if (mapCoordinates[l] != this.packetBuffer[l]) {
                     flag = false;
                     break;
                  }
               }
            } else {
               flag = false;
            }

            if (!flag) {
               this.packetBuffer = mapCoordinates;
               return mapCoordinates;
            }
         }

         for (int i = 0; i < 10; i++) {
            int k = this.tick * 11 % 128;
            this.tick++;
            if (this.minDirty[k] >= 0) {
               int i1 = this.maxDirty[k] - this.minDirty[k] + 1;
               int j1 = this.minDirty[k];
               byte[] abyte1 = new byte[i1 + 3];
               abyte1[0] = 0;
               abyte1[1] = (byte)k;
               abyte1[2] = (byte)j1;

               for (int k1 = 0; k1 < abyte1.length - 3; k1++) {
                  abyte1[k1 + 3] = ItemMapSavedData.this.colors[(k1 + j1) * 128 + k];
               }

               this.maxDirty[k] = -1;
               this.minDirty[k] = -1;
               return abyte1;
            }
         }

         return null;
      }
   }

   public static class MapDecoration {
      public static final int ROTATION_STEPS = 16;
      public byte type;
      public byte x;
      public byte y;
      public byte rot;

      public MapDecoration(byte type, byte x, byte y, byte rot) {
         assert rot >= -16 && rot <= 16 : "Map decoration rotation byte outside of range [-16, 16]";

         this.type = type;
         this.x = x;
         this.y = y;
         this.rot = rot;
      }
   }
}
