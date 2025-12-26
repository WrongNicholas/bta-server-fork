package net.minecraft.server.net.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketChat;
import net.minecraft.core.util.helper.AES;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import org.jetbrains.annotations.NotNull;

public class ServerCommandSource implements CommandSource, IServerCommandSource {
   public final MinecraftServer server;
   public final PlayerServer player;

   public ServerCommandSource(MinecraftServer server, PlayerServer player) {
      this.server = server;
      this.player = player;
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
      return "ServerCommandSource{" + this.server + ", " + this.player + "}";
   }

   @NotNull
   @Override
   public Player getSender() {
      return this.player;
   }

   @Override
   public boolean hasAdmin() {
      return this.server.playerList.isOp(this.getSender().uuid);
   }

   @NotNull
   @Override
   public Vec3 getCoordinates(boolean offsetHeight) {
      Vec3 position = this.getSender().getPosition(1.0F, false);
      return offsetHeight ? position.add(0.0, -this.getSender().heightOffset, 0.0) : position;
   }

   @NotNull
   @Override
   public Vec3 getBlockCoordinates() {
      Vec3 coordinates = this.getCoordinates(true);
      return Vec3.getTempVec3(MathHelper.floor(coordinates.x), MathHelper.floor(coordinates.y), MathHelper.floor(coordinates.z));
   }

   @Override
   public boolean messageMayBeMultiline() {
      return !this.getSender().username.equals("pr_ib");
   }

   @Override
   public void sendMessage(String message) {
      this.player.playerNetServerHandler.sendPacket(new PacketChat(message, AES.keyChain.get(this.player.username)));
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
      return (World)(this.player == null ? this.server.getDimensionWorld(0) : this.player.world);
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
      return this.player.username;
   }

   @Override
   public MinecraftServer getServer() {
      return this.server;
   }
}
