package net.minecraft.core.net;

import com.mojang.nbt.tags.CompoundTag;

public class ServerData {
   public String nickname;
   public String address;
   public boolean showIp;
   public boolean isUp = false;
   public int protocolVersion = 0;
   public String version = "";
   public String motd = "";
   public int currentPlayers;
   public int maxPlayers;
   public long latency;

   public ServerData(String nickname, String address, boolean showIp) {
      this.nickname = nickname;
      this.address = address;
      this.showIp = showIp;
   }

   public static ServerData readFromTag(CompoundTag tag) {
      return new ServerData(tag.getString("nickname"), tag.getString("address"), tag.getBoolean("showIp"));
   }

   public void writeToTag(CompoundTag tag, int index) {
      CompoundTag subTag = new CompoundTag();
      subTag.putString("nickname", this.nickname);
      subTag.putString("address", this.address);
      subTag.putBoolean("showIp", this.showIp);
      tag.putCompound(Integer.toString(index), subTag);
   }

   public ServerAddress getServerAddress() {
      return ServerAddress.resolveServerIP(this.address);
   }
}
