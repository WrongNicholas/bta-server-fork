package net.minecraft.server.net.handler;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.Global;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketAESSendKey;
import net.minecraft.core.net.packet.PacketChat;
import net.minecraft.core.net.packet.PacketCommandManager;
import net.minecraft.core.net.packet.PacketDisconnect;
import net.minecraft.core.net.packet.PacketGameRule;
import net.minecraft.core.net.packet.PacketLogin;
import net.minecraft.core.net.packet.PacketPhotoMode;
import net.minecraft.core.net.packet.PacketPingHandshake;
import net.minecraft.core.net.packet.PacketPlayerGamemode;
import net.minecraft.core.net.packet.PacketPreLogin;
import net.minecraft.core.net.packet.PacketRecipeSync;
import net.minecraft.core.net.packet.PacketSetCarriedItem;
import net.minecraft.core.net.packet.PacketSetHeldObject;
import net.minecraft.core.net.packet.PacketSetHotbarOffset;
import net.minecraft.core.net.packet.PacketSetSpawnPosition;
import net.minecraft.core.net.packet.PacketSetTime;
import net.minecraft.core.net.packet.PacketSyncIDs;
import net.minecraft.core.sound.SoundTypes;
import net.minecraft.core.util.helper.AES;
import net.minecraft.core.util.helper.RSA;
import net.minecraft.core.util.helper.RestHandler;
import net.minecraft.core.util.helper.UUIDHelper;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.command.ServerCommandSource;
import net.minecraft.server.world.WorldServer;
import org.slf4j.Logger;

public class PacketHandlerLogin extends PacketHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Random rand = new Random();
   public NetworkManager netManager;
   public boolean finishedProcessing = false;
   private final MinecraftServer mcServer;
   private int loginTimer = 0;
   private String username = null;
   private PacketLogin loginPacket = null;
   private String serverId = "";
   public static String kickMessage = null;

   public PacketHandlerLogin(MinecraftServer minecraftserver, Socket socket, String s) throws IOException {
      this.mcServer = minecraftserver;
      this.netManager = new NetworkManager(socket, s, this, 100);
      this.netManager.fakeLag = 0;
   }

   public void tryLogin() {
      if (this.loginPacket != null) {
         this.doLogin(this.loginPacket);
         this.loginPacket = null;
      }

      if (this.loginTimer++ == 30000) {
         this.kickUser("Took too long to log in");
      } else {
         this.netManager.processReadPackets();
      }
   }

   public void kickUser(String s) {
      this.kickUser(s, false);
   }

   public void kickUser(String s, boolean silent) {
      try {
         if (!silent) {
            LOGGER.info("Disconnecting {}: {}", this.getUserAndIPString(), s);
         }

         this.netManager.addToSendQueue(new PacketDisconnect(s));
         this.netManager.serverShutdown();
         this.finishedProcessing = true;
      } catch (Exception var4) {
         LOGGER.error("Error while kicking user '{}'!", s, var4);
      }
   }

   @Override
   public void handleHandshake(PacketPreLogin preLoginPacket) {
      if (this.mcServer.onlineMode) {
         this.serverId = Long.toHexString(rand.nextLong());
         this.netManager.addToSendQueue(new PacketPreLogin(this.serverId));
      } else {
         this.netManager.addToSendQueue(new PacketPreLogin("-"));
      }
   }

   @Override
   public void handleLogin(PacketLogin loginPacket) {
      this.username = loginPacket.username;
      if (loginPacket.playerEntityIdAndProtocolVersion != 29444) {
         if (loginPacket.playerEntityIdAndProtocolVersion > 29444) {
            this.kickUser("Outdated server!");
         } else {
            this.kickUser("Outdated client!");
         }
      } else {
         if (!this.mcServer.onlineMode) {
            this.doLogin(loginPacket);
         } else {
            new Thread(
                  () -> {
                     try {
                        String s = getServerId(this);
                        URL url = new URL(
                           "http://session.minecraft.net/game/checkserver.jsp?user="
                              + URLEncoder.encode(loginPacket.username, "UTF-8")
                              + "&serverId="
                              + URLEncoder.encode(s, "UTF-8")
                        );
                        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(url.openStream()));
                        String s1 = bufferedreader.readLine();
                        bufferedreader.close();
                        if (s1.equals("YES")) {
                           setLoginPacket(this, loginPacket);
                        } else {
                           this.kickUser("Failed to verify username!");
                        }
                     } catch (Exception var6) {
                        LOGGER.error("Exception while trying to verify user '{}', kicking!", this.getUserAndIPString(), var6);
                        this.kickUser("Failed to verify username! [internal error " + var6 + "]");
                     }
                  }
               )
               .start();
         }
      }
   }

   public void doLogin(PacketLogin loginPacket) {
      if (this.mcServer.onlineMode) {
         UUID mojangUUID = UUIDHelper.getUUIDFromName(loginPacket.username);
         if (mojangUUID == null) {
            this.kickUser("Cannot authenticate UUID!");
            return;
         }

         if (!mojangUUID.equals(loginPacket.uuid)) {
            this.kickUser("UUID mismatch!");
            return;
         }
      }

      PlayerServer player = this.mcServer.playerList.getPlayerForLogin(this, loginPacket.username, loginPacket.uuid);
      if (player != null) {
         this.mcServer.playerList.load(player);
         player.setWorld(this.mcServer.getDimensionWorld(player.dimension));
         LOGGER.info(this.getUserAndIPString() + " logged in with entity id " + player.id + " at (" + player.x + ", " + player.y + ", " + player.z + ")");
         WorldServer worldserver = this.mcServer.getDimensionWorld(player.dimension);
         ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
         PacketHandlerServer packetHandler = new PacketHandlerServer(this.mcServer, this.netManager, player);

         try {
            packetHandler.sendPacket(
               new PacketLogin(
                  "",
                  new UUID(0L, 0L),
                  player.id,
                  worldserver.getRandomSeed(),
                  (byte)worldserver.dimension.id,
                  (byte)Registries.WORLD_TYPES.getNumericIdOfItem(worldserver.dimensionData.getWorldType()),
                  NetworkManager.PACKET_DELAY,
                  RSA.getPublicKey(RSA.RSAKeyChain.getPublic())
               )
            );
            Key aesKey = AES.generateKey();
            packetHandler.sendPacket(new PacketAESSendKey(RSA.encrypt(AES.getKey(aesKey), RSA.getPublicKey(loginPacket.publicKey))));
            AES.keyChain.put(player.username, aesKey);
         } catch (Exception var8) {
            throw new RuntimeException(var8);
         }

         for (RecipeEntryBase<?, ?, ?> recipe : Registries.RECIPES.getAllSerializableRecipes()) {
            player.playerNetServerHandler.sendPacket(new PacketRecipeSync(recipe, Registries.RECIPES.getAllSerializableRecipes().size()));
         }

         player.playerNetServerHandler.sendPacket(new PacketSyncIDs(0, SoundTypes.getSoundIds()));
         player.playerNetServerHandler.sendPacket(new PacketSyncIDs(1, EntityDispatcher.getEntityIds()));
         packetHandler.sendPacket(new PacketSetSpawnPosition(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z));
         this.mcServer.playerList.setTime(player, worldserver);
         this.mcServer
            .playerList
            .sendPacketToAllPlayers(new PacketChat(TextFormatting.YELLOW + player.getDisplayName() + TextFormatting.YELLOW + " joined the game."));
         this.mcServer.playerList.updatePlayerProfile(player.username, player.nickname, player.uuid, player.score, player.chatColor, true, player.isOperator());
         if (this.mcServer.joinMessage != null && !this.mcServer.joinMessage.isEmpty()) {
            player.playerNetServerHandler.sendPacket(new PacketChat(this.mcServer.joinMessage));
         }

         this.mcServer.playerList.playerLoggedIn(player);
         packetHandler.teleportAndRotate(player.x, player.y, player.z, player.yRot, player.xRot);
         this.mcServer.networkServer.addPlayer(packetHandler);
         this.mcServer.playerList.sendPacketToAllPlayers(new PacketPlayerGamemode(player.id, player.gamemode.getId()));
         packetHandler.sendPacket(new PacketSetTime(worldserver.getWorldTime()));
         player.setupCraftingInventoryListener();
         if (MinecraftServer.statsStatus) {
            RestHandler.post(
               "https://api.betterthanadventure.net/stats?serverToken="
                  + MinecraftServer.statsToken
                  + "&count="
                  + this.mcServer.playerList.playerEntities.size()
            );
         }

         player.playerNetServerHandler.sendPacket(new PacketPhotoMode(this.mcServer.disablePhotoMode));
         player.playerNetServerHandler.sendPacket(new PacketGameRule(this.mcServer.getDimensionWorld(0).getLevelData().getGameRules()));
         player.playerNetServerHandler
            .sendPacket(
               new PacketCommandManager(
                  this.mcServer.getDimensionWorld(player.dimension).getCommandManager().getDispatcher(), new ServerCommandSource(this.mcServer, player), "", 0
               )
            );
         player.playerNetServerHandler.sendPacket(new PacketSetHeldObject(player.id, player.getHeldObject()));
         player.playerNetServerHandler.sendPacket(new PacketSetHotbarOffset(player.inventory.getHotbarOffset()));
         player.playerNetServerHandler.sendPacket(new PacketSetCarriedItem(player.inventory.getCurrentItemIndex()));
      }

      this.finishedProcessing = true;
   }

   @Override
   public void handleErrorMessage(String message, Object[] objects) {
      LOGGER.info("{} lost connection", this.getUserAndIPString());
      LOGGER.error(message, objects);
      this.finishedProcessing = true;
   }

   @Override
   public void handlePingHandshake(PacketPingHandshake pingHandshakePacket) {
      String msg = "";
      if (pingHandshakePacket.pingHostString.equals("MC|PingHost")) {
         msg = "§1\u00000\u0000BTA " + Global.VERSION + '\u0000' + this.mcServer.motd + '\u0000' + 0 + '\u0000' + 0;
      } else if (pingHandshakePacket.pingHostString.equals("BTAPingHost")) {
         msg = "§1\u000029444\u0000"
            + Global.VERSION
            + '\u0000'
            + this.mcServer.motd
            + '\u0000'
            + this.mcServer.playerList.playerEntities.size()
            + '\u0000'
            + this.mcServer.maxPlayers;
      }

      this.kickUser(msg, true);
   }

   @Override
   public void handleInvalidPacket(Packet packet) {
      if (kickMessage != null) {
         this.kickUser(kickMessage);
         kickMessage = null;
      } else {
         this.kickUser("Protocol error");
      }
   }

   public String getUserAndIPString() {
      return this.username != null ? this.username + " [" + this.netManager.getRemoteAddress().toString() + "]" : this.netManager.getRemoteAddress().toString();
   }

   @Override
   public boolean isServerHandler() {
      return true;
   }

   public static String getServerId(PacketHandlerLogin packetHandlerLogin) {
      return packetHandlerLogin.serverId;
   }

   public static PacketLogin setLoginPacket(PacketHandlerLogin packetHandlerLogin, PacketLogin loginPacket) {
      return packetHandlerLogin.loginPacket = loginPacket;
   }
}
