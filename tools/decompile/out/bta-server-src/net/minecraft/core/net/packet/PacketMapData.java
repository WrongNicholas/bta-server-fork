package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.world.saveddata.maps.MapWaypoint;

public class PacketMapData extends Packet {
   public short itemId;
   public short meta;
   public byte scale;
   public byte[] mapData;
   public List<MapWaypoint> waypoints;

   public PacketMapData() {
      this.isChunkDataPacket = true;
   }

   public PacketMapData(short itemId, short meta, byte scale, byte[] mapData, List<MapWaypoint> waypoints) {
      this.isChunkDataPacket = true;
      this.itemId = itemId;
      this.meta = meta;
      this.waypoints = waypoints;
      this.mapData = mapData;
      this.scale = scale;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      this.itemId = dis.readShort();
      this.meta = dis.readShort();
      this.scale = dis.readByte();
      int wayPointAmount = dis.readByte();
      this.waypoints = new ArrayList<>();

      for (int i = 0; i < wayPointAmount; i++) {
         this.waypoints.add(new MapWaypoint(dis));
      }

      this.mapData = new byte[dis.readByte() & 255];
      dis.readFully(this.mapData);
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      dos.writeShort(this.itemId);
      dos.writeShort(this.meta);
      dos.writeByte(this.scale);
      dos.writeByte(this.waypoints.size());

      for (int i = 0; i < (byte)this.waypoints.size(); i++) {
         this.waypoints.get(i).writeToOutputStream(dos);
      }

      dos.writeByte(this.mapData.length);
      dos.write(this.mapData);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleMapData(this);
   }

   @Override
   public int getEstimatedSize() {
      return 4 + this.mapData.length;
   }
}
