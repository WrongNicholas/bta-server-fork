package net.minecraft.core.net.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.Key;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.util.helper.AES;

public class PacketChat extends Packet {
   public static final String ENCRYPTION_ERROR_MESSAGE = "This crash is caused by outdated Java, please update to 8u161 or newer! If your Java version is out of date due to a technical requirement, please add the JCE Unlimited Strength Jurisdiction Policy Files to your installation. https://www.oracle.com/java/technologies/javase-jce-all-downloads.html";
   public String message;
   public int type;
   public boolean encrypted;

   public PacketChat() {
   }

   public PacketChat(String string) {
      this(string, 0, null);
   }

   public PacketChat(String string, Key key) {
      this(string, 0, key);
   }

   public PacketChat(String string, int type, Key key) {
      if (string.length() > 512) {
         string = string.substring(0, 512);
      }

      if (key != null) {
         try {
            this.message = AES.encrypt(string, key);
            this.encrypted = true;
         } catch (Exception var5) {
            throw new RuntimeException(
               "This crash is caused by outdated Java, please update to 8u161 or newer! If your Java version is out of date due to a technical requirement, please add the JCE Unlimited Strength Jurisdiction Policy Files to your installation. https://www.oracle.com/java/technologies/javase-jce-all-downloads.html",
               var5
            );
         }
      } else {
         this.message = string;
         this.encrypted = false;
      }

      this.type = type;
   }

   @Override
   public void read(DataInputStream in) throws IOException {
      this.type = in.readByte();
      this.message = readStringUTF16BE(in, 1024);
      this.encrypted = in.readBoolean();
   }

   @Override
   public void write(DataOutputStream out) throws IOException {
      out.writeByte(this.type);
      writeStringUTF16BE(this.message, out);
      out.writeBoolean(this.encrypted);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleChat(this);
   }

   @Override
   public int getEstimatedSize() {
      return this.message.length() + 1;
   }
}
