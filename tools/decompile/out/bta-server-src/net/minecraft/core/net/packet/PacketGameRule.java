package net.minecraft.core.net.packet;

import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.core.data.gamerule.GameRuleCollection;
import net.minecraft.core.net.handler.PacketHandler;

public class PacketGameRule extends Packet {
   private GameRuleCollection gameRules;

   public PacketGameRule() {
      this.gameRules = null;
   }

   public PacketGameRule(GameRuleCollection gameRules) {
      this.gameRules = gameRules;
   }

   public GameRuleCollection getGameRules() {
      return this.gameRules;
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      CompoundTag tag = readCompressedCompoundTag(dis);
      this.gameRules = GameRuleCollection.readFromNBT(tag);
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      CompoundTag tag = new CompoundTag();
      GameRuleCollection.writeToNBT(tag, this.gameRules);
      writeCompressedCompoundTag(tag, dos);
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleGameRule(this);
   }

   @Override
   public int getEstimatedSize() {
      return 0;
   }
}
