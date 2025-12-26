package net.minecraft.server.net.command;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketChat;
import net.minecraft.core.util.helper.AES;
import net.minecraft.core.util.helper.LoggedPrintStream;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ConsoleCommandSource implements CommandSource, IServerCommandSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public final MinecraftServer server;

   public ConsoleCommandSource(MinecraftServer server) {
      this.server = server;
   }

   @Override
   public Collection<String> getPlayerUsernames() {
      List<String> list = new ArrayList<>();

      for (Player player : this.server.playerList.playerEntities) {
         list.add(player.username);
      }

      return list;
   }

   @Override
   public Collection<String> getPlayerNicknames() {
      List<String> list = new ArrayList<>();

      for (Player player : this.server.playerList.playerEntities) {
         list.add(player.nickname);
      }

      return list;
   }

   @Override
   public Collection<String> getEntitySuggestions() {
      return this.getPlayerUsernames();
   }

   @Override
   public String toString() {
      return "ConsoleCommandSource{" + this.server + "}";
   }

   @Override
   public Player getSender() {
      return null;
   }

   @Override
   public boolean hasAdmin() {
      return true;
   }

   @Nullable
   @Override
   public Vec3 getCoordinates(boolean offsetHeight) {
      return null;
   }

   @Nullable
   @Override
   public Vec3 getBlockCoordinates() {
      return null;
   }

   @Override
   public boolean messageMayBeMultiline() {
      return true;
   }

   @Override
   public void sendMessage(String message) {
      LOGGER.info(LoggedPrintStream.removeColorCodes(message));
   }

   @Override
   public void sendMessage(Player player, String message) {
      this.server.playerList.sendPacketToPlayer(player.username, new PacketChat(message, AES.keyChain.get(player.username)));
   }

   @Override
   public void sendMessageToAllPlayers(String message) {
      this.getServer().playerList.sendPacketToAllPlayers(new PacketChat(message));
   }

   @Override
   public void sendPacketToAllPlayers(Supplier<Packet> packetSupplier) {
      this.getServer().playerList.sendPacketToAllPlayers(packetSupplier.get());
   }

   @Override
   public World getWorld() {
      return this.server.getDimensionWorld(0);
   }

   @Override
   public World getWorld(int dimension) {
      return this.server.getDimensionWorld(dimension);
   }

   @Override
   public void movePlayerToDimension(Player player, int dimension) {
      if (player instanceof PlayerServer) {
         this.server.playerList.sendPlayerToOtherDimension((PlayerServer)player, dimension, null, false);
      } else {
         throw new IllegalStateException("Player is not an instance of PlayerServer");
      }
   }

   @Override
   public void teleportPlayerToPos(Player player, double x, double y, double z) {
      if (player instanceof PlayerServer) {
         ((PlayerServer)player).playerNetServerHandler.teleport(x, y, z);
      } else {
         throw new IllegalStateException("Player is not an instance of PlayerServer");
      }
   }

   @Override
   public void teleportPlayerToPosAndRot(Player player, double x, double y, double z, float yaw, float pitch) {
      if (player instanceof PlayerServer) {
         ((PlayerServer)player).playerNetServerHandler.teleportAndRotate(x, y, z, yaw, pitch);
      } else {
         throw new IllegalStateException("Player is not an instance of PlayerServer");
      }
   }

   @Override
   public String getName() {
      return "Server";
   }

   @Override
   public MinecraftServer getServer() {
      return this.server;
   }
}
