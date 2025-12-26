package net.minecraft.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.nbt.tags.CompoundTag;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.core.Global;
import net.minecraft.core.MinecraftAccessor;
import net.minecraft.core.achievement.stat.StatList;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.data.DataLoader;
import net.minecraft.core.data.legacy.LegacyWorldTypes;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.SkinVariantList;
import net.minecraft.core.item.Items;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.ICommandListener;
import net.minecraft.core.net.IUpdatePlayerListBox;
import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.PropertyManager;
import net.minecraft.core.net.ServerCommandEntry;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.commands.CommandAchievement;
import net.minecraft.core.net.command.commands.CommandBiome;
import net.minecraft.core.net.command.commands.CommandChunk;
import net.minecraft.core.net.command.commands.CommandClear;
import net.minecraft.core.net.command.commands.CommandClone;
import net.minecraft.core.net.command.commands.CommandDamage;
import net.minecraft.core.net.command.commands.CommandFill;
import net.minecraft.core.net.command.commands.CommandGameMode;
import net.minecraft.core.net.command.commands.CommandGameRule;
import net.minecraft.core.net.command.commands.CommandGive;
import net.minecraft.core.net.command.commands.CommandHeal;
import net.minecraft.core.net.command.commands.CommandHelp;
import net.minecraft.core.net.command.commands.CommandKill;
import net.minecraft.core.net.command.commands.CommandMessage;
import net.minecraft.core.net.command.commands.CommandMobSpawning;
import net.minecraft.core.net.command.commands.CommandParticle;
import net.minecraft.core.net.command.commands.CommandPlace;
import net.minecraft.core.net.command.commands.CommandPlaySound;
import net.minecraft.core.net.command.commands.CommandSay;
import net.minecraft.core.net.command.commands.CommandSeed;
import net.minecraft.core.net.command.commands.CommandSetBlock;
import net.minecraft.core.net.command.commands.CommandSetSpawn;
import net.minecraft.core.net.command.commands.CommandSpawn;
import net.minecraft.core.net.command.commands.CommandSummon;
import net.minecraft.core.net.command.commands.CommandTeleport;
import net.minecraft.core.net.command.commands.CommandTellRaw;
import net.minecraft.core.net.command.commands.CommandTestFor;
import net.minecraft.core.net.command.commands.CommandTime;
import net.minecraft.core.net.command.commands.CommandWeather;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.net.packet.PacketSetTime;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.sound.SoundTypes;
import net.minecraft.core.util.helper.RSA;
import net.minecraft.core.util.helper.RestHandler;
import net.minecraft.core.util.helper.UUIDHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.provider.BiomeProviderOverworld;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.chunk.IChunkLoader;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import net.minecraft.core.world.save.ISaveConverter;
import net.minecraft.core.world.save.ISaveFormat;
import net.minecraft.core.world.save.LevelData;
import net.minecraft.core.world.save.LevelStorage;
import net.minecraft.core.world.save.SaveConverters;
import net.minecraft.core.world.save.SaveFormats;
import net.minecraft.core.world.save.SaveHandlerServer;
import net.minecraft.core.world.save.mcregion.SaveFormat19134;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypes;
import net.minecraft.server.entity.EntityTrackerImpl;
import net.minecraft.server.entity.ServerSkinVariantList;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.gui.ServerGui;
import net.minecraft.server.net.NetworkListenThread;
import net.minecraft.server.net.PlayerList;
import net.minecraft.server.net.command.ConsoleCommandSource;
import net.minecraft.server.net.command.commands.CommandBan;
import net.minecraft.server.net.command.commands.CommandColor;
import net.minecraft.server.net.command.commands.CommandDeop;
import net.minecraft.server.net.command.commands.CommandDifficulty;
import net.minecraft.server.net.command.commands.CommandEmotes;
import net.minecraft.server.net.command.commands.CommandKick;
import net.minecraft.server.net.command.commands.CommandList;
import net.minecraft.server.net.command.commands.CommandMe;
import net.minecraft.server.net.command.commands.CommandNickname;
import net.minecraft.server.net.command.commands.CommandOp;
import net.minecraft.server.net.command.commands.CommandSave;
import net.minecraft.server.net.command.commands.CommandScore;
import net.minecraft.server.net.command.commands.CommandStop;
import net.minecraft.server.net.command.commands.CommandUnban;
import net.minecraft.server.net.command.commands.CommandWhitelist;
import net.minecraft.server.net.command.commands.CommandWhoIs;
import net.minecraft.server.world.ConvertProgressUpdater;
import net.minecraft.server.world.WorldManager;
import net.minecraft.server.world.WorldServer;
import net.minecraft.server.world.WorldServerMulti;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import net.woji.platform.PlatformCore;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class MinecraftServer implements Runnable, ICommandListener, MinecraftAccessor {
   public static Logger LOGGER = LogUtils.getLogger();
   public static final String VERSION = Global.VERSION;
   private static MinecraftServer instance;
   public static HashMap<String, Integer> field_6037_b = new HashMap<>();
   @Nullable
   public Boolean argOffline = null;
   @Nullable
   public String argWorld = null;
   public NetworkListenThread networkServer;
   public PropertyManager propertyManager;
   public Map<Integer, WorldServer> dimensionWorlds;
   public PlayerList playerList;
   private boolean serverRunning;
   public boolean serverStopped;
   int deathTime;
   public String currentTask;
   public int percentDone;
   private final List<IUpdatePlayerListBox> playerListBoxes;
   private final List<ServerCommandEntry> commands;
   public Map<Integer, EntityTrackerImpl> entityTrackerMap;
   public boolean onlineMode;
   public boolean pvpOn;
   public boolean allowFlight;
   public int spawnProtectionRange;
   public WorldType defaultWorldType;
   public int difficulty;
   public String joinMessage = null;
   public int summonLimit;
   public String language;
   public int sleepPercentage = 100;
   public int maxPlayers = 20;
   public Gamemode defaultGamemode;
   public static String statsToken;
   public static boolean statsStatus;
   public boolean disablePhotoMode = false;
   public String motd = "";
   private final SkinVariantList skinVariantList = new ServerSkinVariantList();
   public int autoSaveInterval = 5;
   public int chunksSavedPerAutosave = 24;
   public boolean forceSaveAllChunksOnAutosave = false;
   private boolean spawnAnimals;
   private boolean spawnHostiles;


   // Plugin platform
   static final PlatformCore PLATFORM_CORE = new PlatformCore();

   public MinecraftServer() {
      Global.accessor = this;
      this.serverRunning = true;
      this.serverStopped = false;
      this.deathTime = 0;
      this.playerListBoxes = new ArrayList<>();
      this.commands = Collections.synchronizedList(new ArrayList<>());
      Thread t = new Thread(() -> {
         while (true) {
            try {
               Thread.sleep(2147483647L);
            } catch (InterruptedException var1x) {
            }
         }
      });
      t.setDaemon(true);
      t.start();

   }

   private boolean startServer() throws UnknownHostException {
      instance = this;
      Global.isServer = true;
      Thread commandReadingThreads = new Thread(() -> {
         BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));
         String sx = null;

         try {
            while (!this.serverStopped && isServerRunning(this) && (sx = bufferedreader.readLine()) != null) {
               this.addCommand(sx, this);
            }
         } catch (IOException var4) {
            LOGGER.error("Exception occurred while adding command '{}'!", sx, var4);
         }
      });
      commandReadingThreads.setDaemon(true);
      commandReadingThreads.start();
      LOGGER.info("Starting Better than Adventure! server for version {}", VERSION);
      if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
         LOGGER.warn("**** NOT ENOUGH RAM!");
         LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
      }

      Blocks.init();
      Items.init();
      Dimension.init();
      EntityDispatcher.init();
      TileEntityDispatcher.init();
      CommandHelper.init();
      this.initCommands();
      LOGGER.info("Loading properties");
      this.propertyManager = new PropertyManager(new File("server.properties"));
      String s = this.propertyManager.getStringProperty("server-ip", "");
      this.onlineMode = this.argOffline == null ? this.propertyManager.getBooleanProperty("online-mode", true) : !this.argOffline;
      this.spawnAnimals = this.propertyManager.getBooleanProperty("spawn-animals", true);
      this.spawnHostiles = this.propertyManager.getBooleanProperty("spawn-monsters", true);
      this.pvpOn = this.propertyManager.getBooleanProperty("pvp", true);
      this.allowFlight = this.propertyManager.getBooleanProperty("allow-flight", false);
      statsToken = this.propertyManager.getStringProperty("stats-token", "only-enter-a-value-if-you-are-a-registered-server");
      this.defaultWorldType = Registries.WORLD_TYPES
         .getItem(this.propertyManager.getStringProperty("world-type", Registries.WORLD_TYPES.getKey(WorldTypes.OVERWORLD_EXTENDED)));
      this.spawnProtectionRange = this.propertyManager.getIntProperty("spawn-protection", 0);
      this.difficulty = this.propertyManager.getIntProperty("difficulty", 2);
      this.joinMessage = this.propertyManager.getStringProperty("join-message", "");
      this.summonLimit = this.propertyManager.getIntProperty("summon-limit", 10);
      this.sleepPercentage = this.propertyManager.getIntProperty("sleep-percentage", 0);
      NetworkManager.PACKET_DELAY = this.propertyManager.getIntProperty("packet-delay", 1);
      NetworkManager.TIMEOUT_TIME = this.propertyManager.getIntProperty("login-timeout-time", 90000);
      this.maxPlayers = this.propertyManager.getIntProperty("max-players", 20);
      this.language = this.propertyManager.getStringProperty("language", "en_US");
      this.disablePhotoMode = this.propertyManager.getBooleanProperty("disable-photomode", false);
      this.motd = this.propertyManager.getStringProperty("motd", "A Better than Adventure! Server");
      this.autoSaveInterval = this.propertyManager.getIntProperty("autosaveInterval", 5);
      this.chunksSavedPerAutosave = this.propertyManager.getIntProperty("maxChunksSavedPerAutosave", 24);
      this.forceSaveAllChunksOnAutosave = this.propertyManager.getBooleanProperty("forceSaveAllChunksOnAutosave", false);
      EntityItem.enableItemClumping = this.propertyManager.getBooleanProperty("enable-item-clumping", true);
      UUIDHelper.urlUUID = this.propertyManager.getStringProperty("uuid-service", "https://api.minecraftservices.com/minecraft/profile/lookup/name/%s");
      String gamemodeProperty = this.propertyManager.getStringProperty("default-gamemode", "survival");
      this.defaultGamemode = null;

      for (Gamemode gamemode : Gamemode.gamemodesList) {
         if (gamemode.getLanguageKey().replace("gamemode.", "").equals(gamemodeProperty)) {
            this.defaultGamemode = gamemode;
            break;
         }
      }

      if (this.defaultGamemode == null) {
         try {
            this.defaultGamemode = Gamemode.gamemodesList[Integer.parseInt(gamemodeProperty)];
         } catch (Exception var20) {
            this.defaultGamemode = Gamemode.survival;
         }
      }

      if (this.defaultWorldType == null) {
         String worldTypeString = this.propertyManager.getStringProperty("world-type", null);
         if (worldTypeString == null) {
            this.defaultWorldType = WorldTypes.OVERWORLD_EXTENDED;
         }

         WorldType legacyWorldType = LegacyWorldTypes.getWorldTypeByKey(worldTypeString);
         if (legacyWorldType != null) {
            this.defaultWorldType = legacyWorldType;
            this.propertyManager.setProperty("world-type", Registries.WORLD_TYPES.getKey(this.defaultWorldType));
            this.propertyManager.saveProperties();
         } else {
            this.defaultWorldType = WorldTypes.OVERWORLD_EXTENDED;
         }
      }

      this.entityTrackerMap = new HashMap<>();
      new Registries();
      BiomeProviderOverworld.init();
      DataLoader.loadRecipesFromFile("/recipes/blast_furnace.json");
      DataLoader.loadRecipesFromFile("/recipes/furnace.json");
      DataLoader.loadRecipesFromFile("/recipes/trommel.json");
      DataLoader.loadRecipesFromFile("/recipes/workbench.json");
      DataLoader.loadDataPacks(this);
      int recipes = Registries.RECIPES.getAllRecipes().size();
      int groups = Registries.RECIPES.getAllGroups().size();
      int namespaces = Registries.RECIPES.size();
      int itemGroups = Registries.ITEM_GROUPS.size();
      this.logInfo(String.format("%d item groups.", itemGroups));
      this.logInfo(String.format("%d recipes in %d groups in %d namespaces.", recipes, groups, namespaces));
      SoundTypes.registerSounds();
      I18n.initialize(this.language);
      StatList.init();
      if (!statsToken.equals("only-enter-a-value-if-you-are-a-registered-server") && !statsToken.isEmpty()) {
         statsStatus = true;
         int errorCode = RestHandler.post("https://api.betterthanadventure.net/stats?serverToken=" + statsToken + "&count=0");
         switch (errorCode) {
            case 401:
               LOGGER.warn("Your server stats token is invalid. Please clear server.properties -> stats-token");
               statsStatus = false;
               break;
            case 503:
               LOGGER.warn("Cannot access server stats API! Your server might be offline.");
               statsStatus = false;
         }
      }

      InetAddress inetaddress = null;
      if (!s.isEmpty()) {
         inetaddress = InetAddress.getByName(s);
      }

      int i = this.propertyManager.getIntProperty("server-port", 25565);
      LOGGER.info("Starting Minecraft server on " + (!s.isEmpty() ? s : "*") + ":" + i);

      try {
         this.networkServer = new NetworkListenThread(this, inetaddress, i);
      } catch (IOException var19) {
         LOGGER.warn("**** FAILED TO BIND TO PORT!");
         LOGGER.warn("The exception was: " + var19);
         LOGGER.warn("Perhaps a server is already running on that port?");
         return false;
      }

      if (!this.onlineMode) {
         LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
         LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
         LOGGER.warn(
            "While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose."
         );
         LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
      }

      this.playerList = new PlayerList(this);

      for (Entry<Integer, Dimension> entry : Dimension.getDimensionList().entrySet()) {
         this.entityTrackerMap.put(entry.getKey(), new EntityTrackerImpl(this, entry.getValue()));
      }

      long time = System.nanoTime();
      String lName = this.argWorld == null ? this.propertyManager.getStringProperty("level-name", "world") : this.argWorld;
      String lSeed = this.propertyManager.getStringProperty("level-seed", "");
      long randomLong = new Random().nextLong();
      if (!lSeed.isEmpty()) {
         try {
            randomLong = Long.parseLong(lSeed);
         } catch (NumberFormatException var18) {
            randomLong = lSeed.hashCode();
         }
      }

      LOGGER.info("Preparing level \"{}\"", lName);
      this.initWorld(new SaveFormat19134(new File(".")), lName, randomLong);
      LOGGER.info("Generating RSA key...");

      try {
         RSA.RSAKeyChain = RSA.generateKeyPair();
      } catch (Exception var17) {
         throw new RuntimeException(var17);
      }

      LOGGER.info("Done (" + (System.nanoTime() - time) + "ns)! For help, type \"help\" or \"?\"");
      return true;
   }

   public void initCommands() {
      CommandManager.registerCommand(new CommandAchievement());
      CommandManager.registerCommand(new CommandClear());
      CommandManager.registerCommand(new CommandKill());
      CommandManager.registerCommand(new CommandSeed());
      CommandManager.registerCommand(new CommandSetBlock());
      CommandManager.registerCommand(new CommandSummon());
      CommandManager.registerCommand(new CommandTeleport());
      CommandManager.registerCommand(new CommandMessage());
      CommandManager.registerCommand(new CommandSetSpawn());
      CommandManager.registerCommand(new CommandTime());
      CommandManager.registerCommand(new CommandGameMode());
      CommandManager.registerCommand(new CommandWeather());
      CommandManager.registerCommand(new CommandSpawn());
      CommandManager.registerCommand(new CommandPlace());
      CommandManager.registerCommand(new CommandHelp());
      CommandManager.registerCommand(new CommandChunk());
      CommandManager.registerCommand(new CommandGive());
      CommandManager.registerCommand(new CommandGameRule());
      CommandManager.registerCommand(new CommandFill());
      CommandManager.registerCommand(new CommandClone());
      CommandManager.registerCommand(new CommandBiome());
      CommandManager.registerCommand(new CommandSay());
      CommandManager.registerCommand(new CommandTellRaw());
      CommandManager.registerCommand(new CommandTestFor());
      CommandManager.registerCommand(new CommandDamage());
      CommandManager.registerCommand(new CommandHeal());
      CommandManager.registerCommand(new CommandMobSpawning());
      CommandManager.registerCommand(new CommandPlaySound());
      CommandManager.registerCommand(new CommandParticle());
      CommandManager.registerServerCommand(new CommandStop());
      CommandManager.registerServerCommand(new CommandOp());
      CommandManager.registerServerCommand(new CommandDeop());
      CommandManager.registerServerCommand(new CommandList());
      CommandManager.registerServerCommand(new CommandDifficulty());
      CommandManager.registerServerCommand(new CommandColor());
      CommandManager.registerServerCommand(new CommandNickname());
      CommandManager.registerServerCommand(new CommandWhoIs());
      CommandManager.registerServerCommand(new CommandScore());
      CommandManager.registerServerCommand(new CommandMe());
      CommandManager.registerServerCommand(new CommandEmotes());
      CommandManager.registerServerCommand(new CommandBan());
      CommandManager.registerServerCommand(new CommandUnban());
      CommandManager.registerServerCommand(new CommandKick());
      CommandManager.registerServerCommand(new CommandWhitelist());
      CommandManager.registerServerCommand(new CommandSave());
   }

   private void convertWorld(ISaveFormat saveFormat, String worldDirName) {
      int worldSaveVersion = 0;

      try {
         LevelData info = new LevelData(new File(worldDirName));
         worldSaveVersion = info.getSaveVersion();
      } catch (IOException var5) {
         worldSaveVersion = saveFormat.getSaveVersion();
      }

      if (worldSaveVersion < 19134) {
         this.doWorldConversion(worldSaveVersion, worldDirName);
      }
   }

   private void doWorldConversion(int fromVersion, String worldDirName) {
      ConvertProgressUpdater updater = new ConvertProgressUpdater();
      ISaveFormat fromWorldFormat = SaveFormats.createSaveFormat(fromVersion, new File("."));
      if (fromWorldFormat != null) {
         ISaveConverter converterToUse = null;

         for (ISaveConverter converter : SaveConverters.saveConverters) {
            if (converter.fromVersion() == fromVersion && (converterToUse == null || converter.toVersion() > converterToUse.toVersion())) {
               converterToUse = converter;
            }
         }

         if (converterToUse != null) {
            ISaveFormat toWorldFormat = SaveFormats.createSaveFormat(converterToUse.toVersion(), new File("."));
            if (toWorldFormat != null) {
               CompoundTag levelDataTag = toWorldFormat.getLevelDataRaw(worldDirName);
               converterToUse.convertSave(levelDataTag, new File("."), worldDirName, updater);
               levelDataTag.putInt("version", toWorldFormat.getSaveVersion());
               LevelStorage saveHandler = toWorldFormat.getSaveHandler(worldDirName, false);
               saveHandler.saveLevelDataRaw(levelDataTag);
            }
         }
      }
   }

   private void initWorld(ISaveFormat saveFormat, String worldDirName, long l) {
      this.convertWorld(saveFormat, worldDirName);
      this.dimensionWorlds = new HashMap<>();
      SaveHandlerServer saveHandler = new SaveHandlerServer(saveFormat, new File("."), worldDirName, true);

      for (Entry<Integer, Dimension> entry : Dimension.getDimensionList().entrySet()) {
         int dimId = entry.getKey();
         Dimension dimension = entry.getValue();
         WorldServer worldServer;
         if (dimId == 0) {
            worldServer = new WorldServer(this, saveHandler, worldDirName, dimId, this.defaultWorldType, l);
         } else {
            worldServer = new WorldServerMulti(this, saveHandler, worldDirName, dimId, dimension.defaultWorldType, l, this.dimensionWorlds.get(0));
         }

         this.dimensionWorlds.put(dimId, worldServer);
         worldServer.addListener(new WorldManager(this, this.dimensionWorlds.get(dimId)));
         worldServer.setDifficulty(this.difficulty, true);
         worldServer.sleepPercent = this.sleepPercentage;
         worldServer.getSpawnerConfig().setHostileSpawning(this.spawnHostiles);
         worldServer.getSpawnerConfig().setPassiveSpawning(this.spawnAnimals);
         this.playerList.setPlayerManager(this.dimensionWorlds);
      }

      char c = 196;
      long l1 = System.currentTimeMillis();

      for (WorldServer worldServer : this.dimensionWorlds.values()) {
         LOGGER.info("Preparing start region for level {}", worldServer.dimension);
         if ((worldServer.dimension != Dimension.NETHER || this.propertyManager.getBooleanProperty("allow-nether", true))
            && (worldServer.dimension != Dimension.PARADISE || this.propertyManager.getBooleanProperty("allow-paradise", false))) {
            ChunkCoordinates chunkcoordinates = worldServer.getSpawnPoint();

            for (int k = -c; k <= c && this.serverRunning; k += 16) {
               for (int i1 = -c; i1 <= c && this.serverRunning; i1 += 16) {
                  long l2 = System.currentTimeMillis();
                  if (l2 < l1) {
                     l1 = l2;
                  }

                  if (l2 > l1 + 1000L) {
                     int j1 = (c * 2 + 1) * (c * 2 + 1);
                     int k1 = (k + c) * (c * 2 + 1) + i1 + 1;
                     this.outputPercentRemaining("Preparing spawn area", k1 * 100 / j1);
                     l1 = l2;
                  }

                  worldServer.chunkProviderServer.prepareChunk(chunkcoordinates.x + k >> 4, chunkcoordinates.z + i1 >> 4);

                  while (worldServer.updatingLighting() && this.serverRunning) {
                  }
               }
            }
         }
      }

      this.clearCurrentTask();
   }

   private void outputPercentRemaining(String s, int i) {
      this.currentTask = s;
      this.percentDone = i;
      LOGGER.info("{}: {}%", s, i);
   }

   private void clearCurrentTask() {
      this.currentTask = null;
      this.percentDone = 0;
   }

   private void saveServerWorld() {
      LOGGER.info("Saving chunks");

      for (WorldServer worldServer : this.dimensionWorlds.values()) {
         worldServer.saveWorld(true, null, worldServer.dimension == Dimension.OVERWORLD);
         worldServer.checkLock();
      }
   }

   public void stopServer() {
      LOGGER.info("Stopping server");
      if (statsStatus) {
         RestHandler.post("https://api.betterthanadventure.net/stats?serverToken=" + statsToken + "&count=0");
      }

      this.propertyManager.setProperty("difficulty", this.difficulty);
      this.propertyManager.saveProperties();
      if (this.playerList != null) {
         this.playerList.savePlayerStates();
      }

      this.saveServerWorld();
   }

   public void initiateShutdown() {
      this.serverRunning = false;
   }

   @Override
   public void run() {
      try {
         if (this.startServer()) {
            PLATFORM_CORE.init(this);
            PLATFORM_CORE.enablePlugins();
            
            long startTime = System.currentTimeMillis();
            long timeSinceLastTick = 0L;

            for (int i = 0; this.serverRunning; Thread.sleep(1L)) {
               this.networkServer.handleNetworkListenThread();
               long currentTime = System.currentTimeMillis();
               long timeChange = currentTime - startTime;
               if (timeChange > 2000L) {
                  LOGGER.warn("Can't keep up! Did the system time change, or is the server overloaded?");
                  timeChange = 2000L;
               }

               if (timeChange < 0L) {
                  LOGGER.warn("Time ran backwards! Did the system time change?");
                  timeChange = 0L;
               }

               timeSinceLastTick += timeChange;
               startTime = currentTime;
               if (this.dimensionWorlds.get(Dimension.OVERWORLD.id).areEnoughPlayersFullyAsleep()) {
                  this.doTick();
                  timeSinceLastTick = 0L;
               } else {
                  while (timeSinceLastTick > 10L) {
                     timeSinceLastTick -= 10L;
                     i++;

                     for (PlayerServer player : this.playerList.playerEntities) {
                        player.tickSendChunks();
                     }

                     if (i % 5 == 0) {
                        this.doTick();
                        i = 0;
                     }
                  }
               }
            }
         } else {
            while (this.serverRunning) {
               this.commandLineParser();

               try {
                  Thread.sleep(10L);
               } catch (InterruptedException var60) {
                  LOGGER.error("", (Throwable)var60);
               }
            }
         }
      } catch (Throwable var61) {
         LOGGER.error("Unexpected exception", var61);

         while (this.serverRunning) {
            this.commandLineParser();

            try {
               Thread.sleep(10L);
            } catch (InterruptedException var59) {
               LOGGER.error("", (Throwable)var59);
            }
         }
      } finally {
         try {
            PLATFORM_CORE.shutdown();
            this.stopServer();
            this.serverStopped = true;
         } catch (Throwable var57) {
            LOGGER.error("Caught throwable in shutdown sequence!", var57);
         } finally {
            System.exit(0);
         }
      }
   }

   private void doTick() {
      ArrayList<String> arraylist = new ArrayList<>();

      for (String s : field_6037_b.keySet()) {
         int i1 = field_6037_b.get(s);
         if (i1 > 0) {
            field_6037_b.put(s, i1 - 1);
         } else {
            arraylist.add(s);
         }
      }

      for (int i = 0; i < arraylist.size(); i++) {
         field_6037_b.remove(arraylist.get(i));
      }

      AABB.initializePool();
      Vec3.initializePool();
      this.deathTime++;

      for (WorldServer worldServer : this.dimensionWorlds.values()) {
         if ((worldServer.dimension != Dimension.NETHER || this.propertyManager.getBooleanProperty("allow-nether", true))
            && (worldServer.dimension != Dimension.PARADISE || this.propertyManager.getBooleanProperty("allow-paradise", false))) {
            if (this.deathTime % 20 == 0) {
               this.playerList.sendPacketToAllPlayersInDimension(new PacketSetTime(worldServer.getWorldTime()), worldServer.dimension.id);
            }

            try {
               worldServer.tick();
            } catch (Throwable var7) {
               LOGGER.error("Unhandled exception while ticking dimension {}!", worldServer.dimension, var7);
               this.initiateShutdown();
               break;
            }

            try {
               while (worldServer.updatingLighting()) {
               }
            } catch (Throwable var8) {
               LOGGER.error("Unhandled exception while updating lighting in dimension {}!", worldServer.dimension, var8);
            }

            try {
               worldServer.updateEntities();
            } catch (Throwable var6) {
               LOGGER.error("Unhandled exception while updating entities in dimension {}!", worldServer.dimension, var6);
               this.initiateShutdown();
               break;
            }
         }
      }

      this.networkServer.handleNetworkListenThread();
      this.playerList.onTick();

      for (EntityTrackerImpl tracker : this.entityTrackerMap.values()) {
         tracker.tick();
      }

      for (int l = 0; l < this.playerListBoxes.size(); l++) {
         this.playerListBoxes.get(l).update();
      }

      try {
         this.commandLineParser();
      } catch (Exception var5) {
         LOGGER.warn("Unexpected exception while parsing console command", (Throwable)var5);
      }
   }

   public void addCommand(String s, ICommandListener icommandlistener) {
      this.commands.add(new ServerCommandEntry(s, icommandlistener));
   }

   public void commandLineParser() {
      while (!this.commands.isEmpty()) {
         ServerCommandEntry serverCommand = this.commands.remove(0);
         String command = serverCommand.command;
         ConsoleCommandSource source = new ConsoleCommandSource(this);

         try {
            this.getDimensionWorld(0).getCommandManager().execute(command, source);
         } catch (CommandSyntaxException var5) {
            source.sendMessage(var5.getMessage());
         }
      }
   }

   public void addPlayerListBox(IUpdatePlayerListBox listBox) {
      this.playerListBoxes.add(listBox);
   }

   public static void main(String[] args) {
      boolean nogui = false;
      Boolean offline = null;
      String world = null;
      int pointer = 0;

      while (pointer < args.length) {
         String exception = args[pointer];
         switch (exception) {
            case "nogui":
            case "--nogui":
               nogui = true;
               pointer++;
               break;
            case "--offline":
               offline = true;
               pointer++;
               break;
            case "--world":
               try {
                  world = args[pointer + 1];
                  pointer++;
               } catch (Exception var9) {
                  var9.printStackTrace();
               }

               pointer++;
               break;
            default:
               pointer++;
         }
      }

      try {
         MinecraftServer server = new MinecraftServer();
         server.argOffline = offline;
         server.argWorld = world;
         if (!nogui && !GraphicsEnvironment.isHeadless()) {
            ServerGui.initGui(server);
         }

         new Thread(server, "Server thread").start();
      } catch (Exception var8) {
         LOGGER.error("Failed to start the minecraft server", (Throwable)var8);
      }
   }

   public File getFile(String s) {
      return new File(s);
   }

   @Override
   public File getMinecraftDir() {
      return new File(".");
   }

   @Override
   public String getMinecraftVersion() {
      return VERSION;
   }

   @Override
   public IChunkProvider createChunkProvider(World world, IChunkLoader chunkLoader) {
      return new ChunkProviderServer((WorldServer)world, chunkLoader, world.worldType.createChunkGenerator(world));
   }

   @Override
   public void logInfo(String s) {
      LOGGER.info(s);
   }

   @Override
   public String getUsername() {
      return "CONSOLE";
   }

   public WorldServer getDimensionWorld(int dimId) {
      return this.dimensionWorlds.get(dimId);
   }

   public EntityTrackerImpl getEntityTracker(int dimId) {
      return this.entityTrackerMap.get(dimId);
   }

   public static boolean isServerRunning(MinecraftServer minecraftserver) {
      return minecraftserver.serverRunning;
   }

   public static MinecraftServer getInstance() {
      return instance;
   }

   @Override
   public int getAutosaveTimer() {
      return this.autoSaveInterval;
   }

   @Override
   public SkinVariantList getSkinVariantList() {
      return this.skinVariantList;
   }
}
