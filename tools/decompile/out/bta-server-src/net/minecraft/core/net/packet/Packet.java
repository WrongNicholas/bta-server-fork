package net.minecraft.core.net.packet;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.net.handler.PacketHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public abstract class Packet {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final List<Integer> INTERESTING_PACKETS = Arrays.asList(50, 51, 52);
   private static final Map<Integer, Class<? extends Packet>> packetIdToClassMap = new HashMap<>();
   private static final Map<Class<? extends Packet>, Integer> packetClassToIdMap = new HashMap<>();
   private static final Set<Integer> clientBoundPacketIds = new HashSet<>();
   private static final Set<Integer> serverBoundPacketIds = new HashSet<>();
   public static final int MAX_USERNAME_SIZE = 16;
   public static final int MAX_NICKNAME_SIZE = 256;
   public static final int MAX_AES_KEY_SIZE = 392;
   public static final int MAX_MESSAGE_SIZE = 1024;
   public final long creationTimeMillis = System.currentTimeMillis();
   public boolean isChunkDataPacket = false;

   public static void addMapping(int id, boolean clientBound, boolean serverBound, Class<? extends Packet> packetClass) {
      if (packetIdToClassMap.containsKey(id)) {
         throw new IllegalArgumentException("Duplicate packet id:" + id);
      } else if (packetClassToIdMap.containsKey(packetClass)) {
         throw new IllegalArgumentException("Duplicate packet class:" + packetClass);
      } else {
         packetIdToClassMap.put(id, packetClass);
         packetClassToIdMap.put(packetClass, id);
         if (clientBound) {
            clientBoundPacketIds.add(id);
         }

         if (serverBound) {
            serverBoundPacketIds.add(id);
         }
      }
   }

   public static Packet getNewPacket(int id) {
      Class<? extends Packet> packetClass = packetIdToClassMap.get(id);

      try {
         return packetClass == null ? null : packetClass.newInstance();
      } catch (Exception var3) {
         LOGGER.error("Exception instancing packet class '{}'!", packetClass.getSimpleName(), var3);
         LOGGER.warn("Skipping packet with id {}", id);
         return null;
      }
   }

   public final int getId() {
      return packetClassToIdMap.get(this.getClass());
   }

   public static Packet readPacket(DataInputStream dis, boolean isServer) throws IOException {
      try {
         int id = dis.read();
         if (id == -1) {
            return null;
         } else if ((!isServer || serverBoundPacketIds.contains(id)) && (isServer || clientBoundPacketIds.contains(id))) {
            Packet packet = getNewPacket(id);
            if (packet == null) {
               throw new IOException("Bad packet id " + id);
            } else {
               packet.read(dis);
               return packet;
            }
         } else {
            throw new IOException("Bad packet id " + id);
         }
      } catch (EOFException var4) {
         LOGGER.warn("Reached end of stream");
         return null;
      }
   }

   public static void writePacket(Packet packet, DataOutputStream dos) throws IOException {
      dos.write(packet.getId());
      packet.write(dos);
   }

   public static void writeStringUTF8(String string, DataOutputStream dos) throws IOException {
      if (string.length() > 32767) {
         throw new IOException("String too big");
      } else {
         byte[] buf = string.getBytes(StandardCharsets.UTF_8);
         dos.writeShort(buf.length);
         dos.write(buf);
      }
   }

   public static String readStringUTF8(DataInputStream dis, int maxLength) throws IOException {
      short length = dis.readShort();
      if (length < 0) {
         throw new IOException("Received string length is less than zero! Weird string!");
      } else if (length > maxLength) {
         throw new IOException("Received string length longer than maximum allowed (" + length + " > " + maxLength + ")");
      } else {
         byte[] data = new byte[length];
         dis.readFully(data);
         return new String(data, StandardCharsets.UTF_8);
      }
   }

   public static void writeStringUTF16BE(String string, DataOutputStream dos) throws IOException {
      if (string.length() > 32767) {
         throw new IOException("String too big");
      } else {
         byte[] buf = string.getBytes(StandardCharsets.UTF_16BE);
         dos.writeShort(string.length());
         dos.write(buf);
      }
   }

   public static String readStringUTF16BE(DataInputStream dis, int maxLength) throws IOException {
      short length = dis.readShort();
      if (length < 0) {
         throw new IOException("Received string length is less than zero! Weird string!");
      } else if (length > maxLength) {
         throw new IOException("Received string length longer than maximum allowed (" + length + " > " + maxLength + ")");
      } else {
         byte[] data = new byte[length * 2];
         dis.readFully(data);
         return new String(data, StandardCharsets.UTF_16BE);
      }
   }

   public static void writeCompressedCompoundTag(@NotNull CompoundTag tag, DataOutputStream dos) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      NbtIo.writeCompressed(tag, baos);
      byte[] buffer = baos.toByteArray();
      dos.writeShort((short)buffer.length);
      dos.write(buffer);
   }

   public static CompoundTag readCompressedCompoundTag(DataInputStream dis) throws IOException {
      int length = Short.toUnsignedInt(dis.readShort());
      if (length == 0) {
         return null;
      } else {
         byte[] data = new byte[length];
         dis.readFully(data);
         return NbtIo.readCompressed(new ByteArrayInputStream(data));
      }
   }

   public static void writeUUID(@NotNull UUID uuid, DataOutputStream dos) throws IOException {
      dos.writeLong(uuid.getMostSignificantBits());
      dos.writeLong(uuid.getLeastSignificantBits());
   }

   public static UUID readUUID(DataInputStream dis) throws IOException {
      long msb = dis.readLong();
      long lsb = dis.readLong();
      return new UUID(msb, lsb);
   }

   public static Map<Class<? extends Packet>, Integer> getPacketClassToIdMap() {
      return Collections.unmodifiableMap(packetClassToIdMap);
   }

   public static Map<Integer, Class<? extends Packet>> getPacketIdToClassMap() {
      return Collections.unmodifiableMap(packetIdToClassMap);
   }

   public abstract void read(DataInputStream var1) throws IOException;

   public abstract void write(DataOutputStream var1) throws IOException;

   public abstract void handlePacket(PacketHandler var1);

   public abstract int getEstimatedSize();

   static {
      addMapping(0, true, true, PacketKeepAlive.class);
      addMapping(1, true, true, PacketLogin.class);
      addMapping(2, true, true, PacketPreLogin.class);
      addMapping(3, true, true, PacketChat.class);
      addMapping(4, true, false, PacketSetTime.class);
      addMapping(5, true, false, PacketSetEquippedItem.class);
      addMapping(6, true, false, PacketSetSpawnPosition.class);
      addMapping(7, false, true, PacketInteract.class);
      addMapping(8, true, false, PacketSetHealth.class);
      addMapping(9, true, true, PacketRespawn.class);
      addMapping(10, true, true, PacketMovePlayer.class);
      addMapping(11, true, true, PacketMovePlayer.Pos.class);
      addMapping(12, true, true, PacketMovePlayer.Rot.class);
      addMapping(13, true, true, PacketMovePlayer.PosRot.class);
      addMapping(14, false, true, PacketPlayerAction.class);
      addMapping(15, false, true, PacketUseOrPlaceItemStack.class);
      addMapping(16, true, true, PacketSetCarriedItem.class);
      addMapping(17, true, false, PacketSleep.class);
      addMapping(18, true, true, PacketAnimate.class);
      addMapping(19, false, true, PacketUpdatePlayerState.class);
      addMapping(20, true, false, PacketAddPlayer.class);
      addMapping(21, true, false, PacketAddItemEntity.class);
      addMapping(22, true, false, PacketTakeItemEntity.class);
      addMapping(23, true, false, PacketAddEntity.class);
      addMapping(24, true, false, PacketAddMob.class);
      addMapping(25, true, false, PacketAddPainting.class);
      addMapping(26, true, true, PacketBoatControl.class);
      addMapping(27, true, true, PacketSetHeldObject.class);
      addMapping(28, true, false, PacketSetEntityMotion.class);
      addMapping(29, true, false, PacketRemoveEntity.class);
      addMapping(30, true, false, PacketMoveEntity.class);
      addMapping(31, true, false, PacketMoveEntity.Pos.class);
      addMapping(32, true, false, PacketMoveEntity.Rot.class);
      addMapping(33, true, false, PacketMoveEntity.PosRot.class);
      addMapping(34, true, false, PacketTeleportEntity.class);
      addMapping(35, true, true, PacketEntityNickname.class);
      addMapping(36, true, false, PacketEntityFling.class);
      addMapping(38, true, false, PacketEntityEvent.class);
      addMapping(39, true, false, PacketSetRiding.class);
      addMapping(40, true, false, PacketSetEntityData.class);
      addMapping(41, true, false, PacketPlayerGamemode.class);
      addMapping(42, true, false, PacketEntityTagData.class);
      addMapping(50, true, false, PacketChunkVisibility.class);
      addMapping(51, true, false, PacketBlockRegionUpdate.class);
      addMapping(52, true, false, PacketChunkBlocksUpdate.class);
      addMapping(53, true, false, PacketBlockUpdate.class);
      addMapping(54, true, false, PacketBlockEvent.class);
      addMapping(60, true, false, PacketExplosion.class);
      addMapping(61, true, false, PacketPlaySoundEffect.class);
      addMapping(62, true, false, PacketPlaySoundEffectDirect.class);
      addMapping(63, true, false, PacketAddParticle.class);
      addMapping(70, true, false, PacketBedMessage.class);
      addMapping(71, true, false, PacketWeatherEffect.class);
      addMapping(72, true, false, PacketUpdatePlayerProfile.class);
      addMapping(73, true, false, PacketWeatherStatus.class);
      addMapping(74, true, false, PacketGameRule.class);
      addMapping(75, true, false, PacketRecipeSync.class);
      addMapping(100, true, false, PacketContainerOpen.class);
      addMapping(101, true, true, PacketContainerClose.class);
      addMapping(102, false, true, PacketContainerClick.class);
      addMapping(103, true, false, PacketContainerSetSlot.class);
      addMapping(104, true, false, PacketContainerSetContent.class);
      addMapping(105, true, false, PacketContainerSetData.class);
      addMapping(106, true, true, PacketContainerAck.class);
      addMapping(107, false, true, PacketUpdateCreativeInventory.class);
      addMapping(108, true, true, PacketSetHotbarOffset.class);
      addMapping(120, true, false, PacketCommandManager.class);
      addMapping(121, false, true, PacketRequestCommandManager.class);
      addMapping(130, false, true, PacketSignUpdate.class);
      addMapping(131, true, false, PacketMapData.class);
      addMapping(132, true, true, PacketSetMobSpawner.class);
      addMapping(133, false, true, PacketGuidebook.class);
      addMapping(136, true, false, PacketAESSendKey.class);
      addMapping(137, false, true, PacketSetItemName.class);
      addMapping(138, true, false, PacketPlayerList.class);
      addMapping(139, false, true, PacketSetPaintingArt.class);
      addMapping(140, true, false, PacketTileEntityData.class);
      addMapping(142, true, false, PacketFlagOpen.class);
      addMapping(143, true, false, PacketPhotoMode.class);
      addMapping(200, true, false, PacketStatistic.class);
      addMapping(201, true, false, PacketSyncIDs.class);
      addMapping(250, true, true, PacketCustomPayload.class);
      addMapping(254, false, true, PacketPingHandshake.class);
      addMapping(255, true, true, PacketDisconnect.class);
   }
}
