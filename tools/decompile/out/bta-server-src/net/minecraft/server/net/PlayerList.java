package net.minecraft.server.net;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketBedMessage;
import net.minecraft.core.net.packet.PacketChat;
import net.minecraft.core.net.packet.PacketGameRule;
import net.minecraft.core.net.packet.PacketPlayerGamemode;
import net.minecraft.core.net.packet.PacketRespawn;
import net.minecraft.core.net.packet.PacketSetTime;
import net.minecraft.core.net.packet.PacketUpdatePlayerProfile;
import net.minecraft.core.util.helper.AES;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.UUIDHelper;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.PortalHandler;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.save.PlayerIO;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import net.minecraft.server.player.PlayerListBox;
import net.minecraft.server.player.PlayerManager;
import net.minecraft.server.world.ServerPlayerController;
import net.minecraft.server.world.WorldServer;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class PlayerList {
   public static Logger logger = LogUtils.getLogger();
   public List<PlayerServer> playerEntities;
   private final MinecraftServer server;
   private final Map<Integer, PlayerManager> playerManagers;
   private final Set<UUID> bans;
   private final Set<String> ipBans;
   private final Set<UUID> ops;
   private final Set<UUID> whitelist;
   private final File bansFile;
   private final File ipBansFile;
   private final File opsFile;
   private final File whitelistFile;
   private PlayerIO playerIo;
   public boolean whitelistEnforced;
   AtomicInteger awaitedLoadThreads = new AtomicInteger(0);

   public PlayerList(MinecraftServer server) {
      this.playerEntities = new ArrayList<>();
      this.bans = new HashSet<>();
      this.ipBans = new HashSet<>();
      this.ops = new HashSet<>();
      this.whitelist = new HashSet<>();
      this.playerManagers = new HashMap<>();
      this.server = server;
      this.bansFile = server.getFile("banned-players.txt");
      this.ipBansFile = server.getFile("banned-ips.txt");
      this.opsFile = server.getFile("ops.txt");
      this.whitelistFile = server.getFile("white-list.txt");
      int viewDistance = server.propertyManager.getIntProperty("view-distance", 10);

      for (Integer dim : Dimension.getDimensionList().keySet()) {
         this.playerManagers.put(dim, new PlayerManager(server, dim, viewDistance));
      }

      this.whitelistEnforced = server.propertyManager.getBooleanProperty("white-list", false);
      this.loadBannedPlayers();
      this.loadBannedList();
      this.loadOps();
      this.loadWhiteList();
      this.saveBannedPlayers();
      this.saveBannedList();
      this.saveOps();
      this.saveWhiteList();
      if (this.awaitedLoadThreads.get() > 0) {
         new Thread(() -> {
            long startTime = System.currentTimeMillis();

            while (this.awaitedLoadThreads.get() > 0) {
               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var5) {
                  var5.printStackTrace();
                  break;
               }

               if (System.currentTimeMillis() - startTime > 600000L) {
                  break;
               }
            }

            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var4x) {
               var4x.printStackTrace();
            }

            this.saveBannedPlayers();
            this.saveBannedList();
            this.saveOps();
            this.saveWhiteList();
         }).start();
      }
   }

   public void setPlayerManager(Map<Integer, WorldServer> worldServerMap) {
      this.playerIo = worldServerMap.get(0).getSaveHandler().getPlayerFileData();
   }

   public void syncPlayerDimension(PlayerServer player) {
      for (PlayerManager playerManager : this.playerManagers.values()) {
         playerManager.removePlayer(player);
      }

      this.getPlayerManager(player.dimension).addPlayer(player);
      WorldServer world = this.server.getDimensionWorld(player.dimension);
      world.chunkProviderServer.prepareChunk((int)player.x >> 4, (int)player.z >> 4);
   }

   public int getMaxTrackingDistance() {
      return this.playerManagers.get(0).getMaxTrackingDistance();
   }

   public PlayerManager getPlayerManager(int dim) {
      return this.playerManagers.get(dim);
   }

   public void load(PlayerServer player) {
      this.playerIo.load(player);
   }

   public void playerLoggedIn(PlayerServer player) {
      this.playerEntities.add(player);
      player.playerNetServerHandler.handleSendInitialPlayerList();
      WorldServer world = this.server.getDimensionWorld(player.dimension);
      world.chunkProviderServer.prepareChunk((int)player.x >> 4, (int)player.z >> 4);
      if (!world.getWorldType().hasCeiling()) {
         while (world.getCollidingSolidBlockBoundingBoxes(player, player.bb).size() != 0) {
            player.setPos(player.x, player.y + 1.0, player.z);
         }
      }

      world.entityJoinedWorld(player);
      this.getPlayerManager(player.dimension).addPlayer(player);
   }

   public void onPlayerMoved(PlayerServer player) {
      this.getPlayerManager(player.dimension).onPlayerMoved(player);
   }

   public void playerLoggedOut(PlayerServer entityplayermp) {
      this.playerIo.save(entityplayermp);
      this.server.getDimensionWorld(entityplayermp.dimension).setEntityDead(entityplayermp);
      this.playerEntities.remove(entityplayermp);
      this.getPlayerManager(entityplayermp.dimension).removePlayer(entityplayermp);
   }

   public PlayerServer getPlayerForLogin(PacketHandlerLogin handler, String username, UUID uuid) {
      if (this.bans.contains(uuid)) {
         handler.kickUser("You are banned from this server!");
         return null;
      } else if (!this.isAllowedToLogin(uuid)) {
         handler.kickUser("You are not white-listed on this server!");
         return null;
      } else {
         String ip = handler.netManager.getRemoteAddress().toString();
         ip = ip.substring(ip.indexOf("/") + 1);
         ip = ip.substring(0, ip.indexOf(":"));
         if (this.ipBans.contains(ip)) {
            handler.kickUser("Your IP address is banned from this server!");
            return null;
         } else if (this.playerEntities.size() >= MinecraftServer.getInstance().maxPlayers) {
            handler.kickUser("The server is full!");
            return null;
         } else {
            for (PlayerServer player : new ArrayList<>(this.playerEntities)) {
               if (player.username.equalsIgnoreCase(username) || player.uuid.equals(uuid)) {
                  player.playerNetServerHandler.kickPlayer("You are logged in from another location!");
               }
            }

            return new PlayerServer(this.server, this.server.getDimensionWorld(0), username, uuid, new ServerPlayerController(this.server.getDimensionWorld(0)));
         }
      }
   }

   public PlayerServer recreatePlayerEntity(PlayerServer previousPlayer, int dimension) {
      this.server.getEntityTracker(previousPlayer.dimension).removeTrackedPlayerSymmetric(previousPlayer);
      this.server.getEntityTracker(previousPlayer.dimension).untrackEntity(previousPlayer);
      this.getPlayerManager(previousPlayer.dimension).removePlayer(previousPlayer);
      this.playerEntities.remove(previousPlayer);
      this.server.getDimensionWorld(previousPlayer.dimension).removePlayer(previousPlayer);
      ChunkCoordinates chunkcoordinates = previousPlayer.getPlayerSpawnCoordinate();
      previousPlayer.dimension = dimension;
      PlayerServer newPlayer = new PlayerServer(
         this.server,
         this.server.getDimensionWorld(previousPlayer.dimension),
         previousPlayer.username,
         previousPlayer.uuid,
         new ServerPlayerController(this.server.getDimensionWorld(previousPlayer.dimension))
      );
      newPlayer.id = previousPlayer.id;
      newPlayer.chatColor = previousPlayer.chatColor;
      newPlayer.nickname = previousPlayer.nickname;
      newPlayer.gamemode = previousPlayer.gamemode;
      newPlayer.playerNetServerHandler = previousPlayer.playerNetServerHandler;
      newPlayer.inventory.transferAllContents(previousPlayer.inventory);
      WorldServer worldserver = this.server.getDimensionWorld(previousPlayer.dimension);
      if (chunkcoordinates != null) {
         ChunkCoordinates bedCoordinates = Player.getValidBedSpawnCoordinates(this.server.getDimensionWorld(previousPlayer.dimension), chunkcoordinates);
         if (bedCoordinates != null) {
            newPlayer.setPos(bedCoordinates.x + 0.5F, bedCoordinates.y + 0.1F, bedCoordinates.z + 0.5F);
            newPlayer.xRot = 0.0F;
            newPlayer.yRot = 0.0F;
            newPlayer.setPlayerSpawnCoordinate(chunkcoordinates);
         } else {
            newPlayer.playerNetServerHandler.sendPacket(new PacketBedMessage(0));
         }
      }

      worldserver.chunkProviderServer.prepareChunk((int)newPlayer.x >> 4, (int)newPlayer.z >> 4);

      while (!worldserver.getCubes(newPlayer, newPlayer.bb).isEmpty()) {
         newPlayer.setPos(newPlayer.x, newPlayer.y + 1.0, newPlayer.z);
      }

      newPlayer.playerNetServerHandler
         .sendPacket(new PacketRespawn((byte)newPlayer.dimension, (byte)Registries.WORLD_TYPES.getNumericIdOfItem(worldserver.worldType)));
      this.server.playerList.sendPacketToAllPlayers(new PacketPlayerGamemode(newPlayer.id, newPlayer.gamemode.getId()));
      newPlayer.playerNetServerHandler.teleportAndRotate(newPlayer.x, newPlayer.y, newPlayer.z, newPlayer.yRot, newPlayer.xRot);
      this.setTime(newPlayer, worldserver);
      this.getPlayerManager(newPlayer.dimension).addPlayer(newPlayer);
      worldserver.entityJoinedWorld(newPlayer);
      this.playerEntities.add(newPlayer);
      newPlayer.setupCraftingInventoryListener();
      newPlayer.func_22068_s();
      newPlayer.playerNetServerHandler.sendPacket(new PacketGameRule(this.server.getDimensionWorld(0).getLevelData().getGameRules()));
      return newPlayer;
   }

   public void sendPlayerToOtherDimension(PlayerServer playerServer, int targetDim, DyeColor portalColor, boolean generatePortal) {
      WorldServer worldserver = this.server.getDimensionWorld(playerServer.dimension);
      Dimension lastDim = Dimension.getDimensionList().get(playerServer.dimension);
      Dimension newDim = Dimension.getDimensionList().get(targetDim);
      playerServer.dimension = targetDim;
      WorldServer worldserver1 = this.server.getDimensionWorld(playerServer.dimension);
      playerServer.playerNetServerHandler
         .sendPacket(new PacketRespawn((byte)playerServer.dimension, (byte)Registries.WORLD_TYPES.getNumericIdOfItem(worldserver1.worldType)));
      worldserver.removePlayer(playerServer);
      playerServer.removed = false;
      double x = playerServer.x;
      double y = playerServer.y;
      double z = playerServer.z;
      x *= Dimension.getCoordScale(lastDim, newDim);
      z *= Dimension.getCoordScale(lastDim, newDim);
      playerServer.teleport(x, y, z, playerServer.yRot, playerServer.xRot);
      ChunkCoordinates newCoordinates = new ChunkCoordinates(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
      if (playerServer.dimensionEnterCoordinate != null) {
         double dx = playerServer.dimensionEnterCoordinate.x - x;
         double dy = playerServer.dimensionEnterCoordinate.y - y;
         double dz = playerServer.dimensionEnterCoordinate.z - z;
         double distSqr = dx * dx + dy * dy + dz * dz;
         if (distSqr > 6.4E7) {
            playerServer.addStat(Achievements.FAST_TRAVEL, 1);
         }
      }

      playerServer.dimensionEnterCoordinate = newCoordinates;
      if (playerServer.isAlive()) {
         worldserver.updateEntityWithOptionalForce(playerServer, false);
      }

      if (playerServer.isAlive()) {
         worldserver1.entityJoinedWorld(playerServer);
         playerServer.teleport(x, playerServer.y, z, playerServer.yRot, playerServer.xRot);
         worldserver1.updateEntityWithOptionalForce(playerServer, false);
         if (generatePortal) {
            worldserver1.chunkProviderServer.chunkLoadOverride = true;
            new PortalHandler().teleportEntity(worldserver1, playerServer, portalColor, lastDim, newDim);
            worldserver1.chunkProviderServer.chunkLoadOverride = false;
         }
      }

      this.syncPlayerDimension(playerServer);
      playerServer.playerNetServerHandler.teleportAndRotate(playerServer.x, playerServer.y, playerServer.z, playerServer.yRot, playerServer.xRot);
      this.server.playerList.sendPacketToAllPlayers(new PacketPlayerGamemode(playerServer.id, playerServer.gamemode.getId()));
      playerServer.setWorld(worldserver1);
      this.setTime(playerServer, worldserver1);
      this.initializePlayerObject(playerServer);
      playerServer.playerNetServerHandler.sendPacket(new PacketGameRule(this.server.getDimensionWorld(0).getLevelData().getGameRules()));
   }

   public void onTick() {
      for (PlayerManager playerManager : this.playerManagers.values()) {
         playerManager.tick();
      }
   }

   public void markBlockNeedsUpdate(int x, int y, int z, int dimension) {
      this.getPlayerManager(dimension).markBlockNeedsUpdate(x, y, z);
   }

   public void sendPacketToAllPlayers(Packet packet) {
      for (PlayerServer player : this.playerEntities) {
         player.playerNetServerHandler.sendPacket(packet);
      }
   }

   public void updatePlayerProfile(String username, String nickname, UUID uuid, int score, byte chatColor, boolean isOnline, boolean isOperator) {
      this.sendPacketToAllPlayers(new PacketUpdatePlayerProfile(username, nickname, uuid, score, chatColor, isOnline, isOperator));
      PlayerListBox.updateList();
   }

   public void sendEncryptedChatToAllPlayers(String message) {
      for (PlayerServer player : this.playerEntities) {
         player.playerNetServerHandler.sendPacket(new PacketChat(message, 0, AES.keyChain.get(player.username)));
      }
   }

   public void sendPacketToAllPlayersInDimension(Packet packet, int i) {
      for (PlayerServer player : this.playerEntities) {
         if (player.dimension == i) {
            player.playerNetServerHandler.sendPacket(packet);
         }
      }
   }

   public String getPlayerList() {
      StringBuilder s = new StringBuilder();

      for (int i = 0; i < this.playerEntities.size(); i++) {
         if (i > 0) {
            s.append(", ");
         }

         s.append(this.playerEntities.get(i).getDisplayName());
      }

      return s.toString();
   }

   public synchronized void banPlayer(UUID uuid) {
      this.bans.add(uuid);
      this.saveBannedPlayers();
   }

   public synchronized void pardonPlayer(UUID uuid) {
      this.bans.remove(uuid);
      this.saveBannedPlayers();
   }

   private synchronized void loadBannedPlayers() {
      try {
         this.bans.clear();
         BufferedReader bufferedreader = new BufferedReader(new FileReader(this.bansFile));

         String s;
         while ((s = bufferedreader.readLine()) != null) {
            if (!UUIDHelper.isUUID(s)) {
               this.awaitedLoadThreads.addAndGet(1);
               UUIDHelper.runConversionAction(s, uuid -> {
                  this.bans.add(uuid);
                  this.awaitedLoadThreads.addAndGet(-1);
               }, username -> this.awaitedLoadThreads.addAndGet(-1));
            } else {
               this.bans.add(UUID.fromString(s));
            }
         }

         bufferedreader.close();
      } catch (Exception var3) {
         logger.warn("Failed to load ban list: " + var3);
      }
   }

   private synchronized void saveBannedPlayers() {
      try {
         PrintWriter printwriter = new PrintWriter(new FileWriter(this.bansFile, false));

         for (UUID uuid : this.bans) {
            printwriter.println(uuid);
         }

         printwriter.close();
      } catch (Exception var4) {
         logger.warn("Failed to save ban list: " + var4);
      }
   }

   public synchronized void banIP(String s) {
      this.ipBans.add(s.toLowerCase());
      this.saveBannedList();
   }

   public synchronized void pardonIP(String s) {
      this.ipBans.remove(s.toLowerCase());
      this.saveBannedList();
   }

   private synchronized void loadBannedList() {
      try {
         this.ipBans.clear();
         BufferedReader bufferedreader = new BufferedReader(new FileReader(this.ipBansFile));

         String ip;
         while ((ip = bufferedreader.readLine()) != null) {
            this.ipBans.add(ip.trim().toLowerCase());
         }

         bufferedreader.close();
      } catch (Exception var3) {
         logger.warn("Failed to load ip ban list: " + var3);
      }
   }

   private synchronized void saveBannedList() {
      try {
         PrintWriter printwriter = new PrintWriter(new FileWriter(this.ipBansFile, false));

         for (String ip : this.ipBans) {
            printwriter.println(ip);
         }

         printwriter.close();
      } catch (Exception var4) {
         logger.warn("Failed to save ip ban list: " + var4);
      }
   }

   public synchronized void opPlayer(UUID uuid) {
      this.ops.add(uuid);
      this.saveOps();
   }

   public synchronized void deopPlayer(UUID uuid) {
      this.ops.remove(uuid);
      this.saveOps();
   }

   private synchronized void loadOps() {
      try {
         this.ops.clear();
         BufferedReader bufferedreader = new BufferedReader(new FileReader(this.opsFile));

         String entry;
         while ((entry = bufferedreader.readLine()) != null) {
            if (!UUIDHelper.isUUID(entry)) {
               this.awaitedLoadThreads.addAndGet(1);
               UUIDHelper.runConversionAction(entry, uuid -> {
                  this.ops.add(uuid);
                  this.awaitedLoadThreads.addAndGet(-1);
               }, username -> this.awaitedLoadThreads.addAndGet(-1));
            } else {
               this.ops.add(UUID.fromString(entry));
            }
         }

         bufferedreader.close();
      } catch (Exception var3) {
         logger.warn("Failed to load ip ban list: " + var3);
      }
   }

   private synchronized void saveOps() {
      try {
         PrintWriter printwriter = new PrintWriter(new FileWriter(this.opsFile, false));

         for (UUID uuid : this.ops) {
            printwriter.println(uuid.toString());
         }

         printwriter.close();
      } catch (Exception var4) {
         logger.warn("Failed to save ip ban list: " + var4);
      }
   }

   private synchronized void loadWhiteList() {
      try {
         this.whitelist.clear();
         BufferedReader bufferedreader = new BufferedReader(new FileReader(this.whitelistFile));

         String entry;
         while ((entry = bufferedreader.readLine()) != null) {
            if (!UUIDHelper.isUUID(entry)) {
               this.awaitedLoadThreads.addAndGet(1);
               UUIDHelper.runConversionAction(entry, uuid -> {
                  this.whitelist.add(uuid);
                  this.awaitedLoadThreads.addAndGet(-1);
               }, username -> this.awaitedLoadThreads.addAndGet(-1));
            } else {
               this.whitelist.add(UUID.fromString(entry));
            }
         }

         bufferedreader.close();
      } catch (Exception var3) {
         logger.warn("Failed to load white-list: " + var3);
      }
   }

   private synchronized void saveWhiteList() {
      try {
         PrintWriter printwriter = new PrintWriter(new FileWriter(this.whitelistFile, false));

         for (UUID uuid : this.whitelist) {
            printwriter.println(uuid.toString());
         }

         printwriter.close();
      } catch (Exception var4) {
         logger.warn("Failed to save white-list: " + var4);
      }
   }

   public boolean isAllowedToLogin(UUID uuid) {
      return !this.whitelistEnforced || this.ops.contains(uuid) || this.whitelist.contains(uuid);
   }

   public boolean isOp(UUID uuid) {
      return this.ops.contains(uuid);
   }

   @Nullable
   public PlayerServer getPlayerEntity(String s) {
      for (PlayerServer player : this.playerEntities) {
         if (player.username.equalsIgnoreCase(s)) {
            return player;
         }
      }

      return null;
   }

   public void sendChatMessageToPlayer(String s, String s1) {
      PlayerServer player = this.getPlayerEntity(s);
      if (player != null) {
         player.playerNetServerHandler.sendPacket(new PacketChat(s1));
      }
   }

   public void sendPacketToPlayersAroundPoint(double x, double y, double z, double radius, int dimension, Packet packet) {
      this.sendPacketToOtherPlayersAroundPoint(null, x, y, z, radius, dimension, packet);
   }

   public void sendPacketToOtherPlayersAroundPoint(@Nullable Player player, double x, double y, double z, double radius, int dimension, Packet packet) {
      for (PlayerServer otherPlayer : this.playerEntities) {
         if (otherPlayer != player && otherPlayer.dimension == dimension) {
            double dx = x - otherPlayer.x;
            double dy = y - otherPlayer.y;
            double dz = z - otherPlayer.z;
            if (dx * dx + dy * dy + dz * dz < radius * radius) {
               otherPlayer.playerNetServerHandler.sendPacket(packet);
            }
         }
      }
   }

   public void sendChatMessageToAllOps(String s) {
      PacketChat chatPacket = new PacketChat(s);

      for (PlayerServer player : this.playerEntities) {
         if (this.isOp(player.uuid)) {
            player.playerNetServerHandler.sendPacket(chatPacket);
         }
      }
   }

   public boolean sendPacketToPlayer(String s, Packet packet) {
      PlayerServer player = this.getPlayerEntity(s);
      if (player != null) {
         player.playerNetServerHandler.sendPacket(packet);
         return true;
      } else {
         return false;
      }
   }

   public void savePlayerStates() {
      for (PlayerServer playerEntity : this.playerEntities) {
         this.playerIo.save(playerEntity);
      }
   }

   public void sendTileEntityToPlayer(int x, int y, int z, int dimension, TileEntity tileEntity) {
      this.getPlayerManager(dimension).markBlockNeedsUpdate(x, y, z);
   }

   public synchronized void addToWhiteList(UUID uuid) {
      this.whitelist.add(uuid);
      this.saveWhiteList();
   }

   public synchronized void removeFromWhiteList(UUID uuid) {
      this.whitelist.remove(uuid);
      this.saveWhiteList();
   }

   public Set<UUID> getWhitelist() {
      return this.whitelist;
   }

   public void reloadWhiteList() {
      this.loadWhiteList();
   }

   public void setTime(PlayerServer player, WorldServer worldserver) {
      player.playerNetServerHandler.sendPacket(new PacketSetTime(worldserver.getWorldTime()));
   }

   public void initializePlayerObject(PlayerServer player) {
      player.initializeCraftingInventory(player.inventorySlots);
      player.initializeLastFieldValues();
   }
}
