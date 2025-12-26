package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import net.minecraft.core.net.handler.PacketHandler;
import org.jetbrains.annotations.NotNull;

public class PacketCustomPayload extends Packet {
   @NotNull
   public static final String CHANNEL_FLAG = "BTA:Flag";
   @NotNull
   public static final String CHANNEL_ROTATION_LOCK = "BTA:RotationLock";
   @NotNull
   public static final String CHANNEL_WAND_MONSTER = "BTA:WandMonster";
   @NotNull
   public static final String CHANNEL_RAINBOW_START = "BTA:RainbowStart";
   public static final int MAX_CHANNEL_SIZE = 128;
   public String channel;
   public byte[] data;

   public PacketCustomPayload() {
   }

   public PacketCustomPayload(@NotNull String channel, byte[] data) {
      this.channel = channel;
      if (data != null) {
         if (data.length > 32768) {
            throw new IllegalArgumentException("Payload may not be larger than 32k");
         }

         this.data = data;
      }
   }

   public PacketCustomPayload(@NotNull String channel, ByteBuffer data) {
      this.channel = channel;
      if (data != null) {
         int remaining = data.remaining();
         if (remaining > 32768) {
            throw new IllegalArgumentException("Payload may not be larger than 32k");
         }

         byte[] d = new byte[remaining];
         data.get(d, 0, remaining);
         this.data = d;
      }
   }

   @Override
   public void read(DataInputStream in) throws IOException {
      this.channel = readStringUTF8(in, 128);
      int length = in.readInt();
      if (length > 0 && length < 32768) {
         this.data = new byte[length];
         in.read(this.data);
      }
   }

   @Override
   public void write(DataOutputStream out) throws IOException {
      writeStringUTF8(this.channel, out);
      if (this.data != null) {
         out.writeInt(this.data.length);
         out.write(this.data);
      } else {
         out.writeInt(0);
      }
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleCustomPayload(this);
   }

   @Override
   public int getEstimatedSize() {
      return this.channel.length() + 4 + this.data.length;
   }
}
