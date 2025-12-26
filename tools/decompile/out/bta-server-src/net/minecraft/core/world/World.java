package net.minecraft.core.world;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.tags.CompoundTag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.core.Global;
import net.minecraft.core.NextTickListEntry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.gamerule.GameRule;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobFireflyCluster;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.Difficulty;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.debug.Debug;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkCache;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.chunk.IChunkLoader;
import net.minecraft.core.world.chunk.LightUpdate;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import net.minecraft.core.world.config.spawning.SpawnerConfig;
import net.minecraft.core.world.pathfinder.Path;
import net.minecraft.core.world.pathfinder.PathFinder;
import net.minecraft.core.world.save.DimensionData;
import net.minecraft.core.world.save.LevelData;
import net.minecraft.core.world.save.LevelStorage;
import net.minecraft.core.world.saveddata.SavedData;
import net.minecraft.core.world.saveddata.SavedDataStorage;
import net.minecraft.core.world.season.SeasonManager;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherManager;
import net.minecraft.core.world.weather.Weathers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class World implements WorldSource {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static boolean AUTOSAVE = true;
   public static final int HEIGHT_BLOCKS = 256;
   public static final int MAX_BRIGHTNESS = 15;
   public static final int MAX_BLOCK_X = 32000000;
   public static final int MAX_BLOCK_Z = 32000000;
   public static final int MIN_BLOCK_X = -32000000;
   public static final int MIN_BLOCK_Z = -32000000;
   public static final int MAX_SCHEDULED_TICKS_IN_TICK = 1000;
   public static final int ANIMATION_TICKS_PER_TICK = 1000;
   private final List<LightUpdate> lightingToUpdate = new LinkedList<>();
   public List<Entity> loadedEntityList = new ArrayList<>();
   private final List<Entity> unloadedEntityList = new ArrayList<>();
   private final TreeSet<NextTickListEntry> scheduledTickTreeSet = new TreeSet<>();
   private final Set<NextTickListEntry> scheduledTickSet = new HashSet<>();
   public List<TileEntity> loadedTileEntityList = new ArrayList<>();
   public List<Player> players = new ArrayList<>();
   public List<Entity> weatherEffects = new ArrayList<>();
   public final ArrayList<AABB> collidingBoundingBoxes = new ArrayList<>();
   public List<LevelListener> listeners = new ArrayList<>();
   private final Set<ChunkCoordinate> positionsToUpdate = new HashSet<>();
   private final List<Entity> entityBuffer = new ArrayList<>();
   private final Set<NextTickListEntry> immediatelyUpdatedPositions = new HashSet<>();
   public boolean scheduledUpdatesAreImmediate = false;
   public int skyDarken = 0;
   protected int updateLCG = new Random().nextInt();
   public int lightningFlicker = 0;
   public int rainbowTicks = 0;
   public int startingRainbowTicks = 0;
   public boolean dayCanHaveRainbow = true;
   public boolean noNeighborUpdate = false;
   private long lockTimestamp = System.currentTimeMillis();
   public Random rand = new Random();
   public boolean isNewWorld = false;
   public Dimension dimension;
   public WorldType worldType;
   public IChunkProvider chunkProvider;
   public LevelStorage saveHandler;
   protected LevelData levelData;
   public DimensionData dimensionData;
   public boolean findingSpawnPoint;
   protected boolean enoughPlayersSleeping;
   public SavedDataStorage savedDataStorage;
   private boolean updatingBlockEntities;
   private int lightingUpdatesCounter = 0;
   static int lightingUpdatesScheduled = 0;
   private int caveSoundCounter = this.rand.nextInt(12000);
   public boolean isClientSide = false;
   public int sleepPercent = 100;
   private long runtime = 0L;
   public int dayCountLastTick;
   public BiomeProvider biomeProvider;
   public AuroraProvider auroraProvider;
   public SeasonManager seasonManager;
   public WeatherManager weatherManager;
   private CommandManager commandManager;

   public World(LevelStorage saveHandler, String name, long seed, Dimension dimension, WorldType worldType) {
      this.saveHandler = saveHandler;
      this.savedDataStorage = new SavedDataStorage(saveHandler);
      this.levelData = saveHandler.getLevelData();
      this.isNewWorld = this.levelData == null;
      if (dimension != null) {
         this.dimension = dimension;
      } else if (this.levelData != null) {
         this.dimension = Dimension.getDimensionList().get(this.levelData.getDimension());
      } else {
         this.dimension = Dimension.getDimensionList().get(0);
      }

      this.dimensionData = saveHandler.getDimensionData(this.dimension.id);
      if (worldType != null) {
         this.worldType = worldType;
      } else if (this.dimensionData != null) {
         this.worldType = this.dimensionData.getWorldType();
      } else {
         this.worldType = this.dimension.defaultWorldType;
      }

      boolean isNewWorld = false;
      if (this.levelData == null) {
         this.levelData = new LevelData(seed, name);
         isNewWorld = true;
      } else {
         this.levelData.setWorldName(name);
      }

      if (this.dimensionData == null) {
         this.dimensionData = new DimensionData(this.worldType);
         this.weatherManager = new WeatherManager(this);
      } else {
         this.weatherManager = new WeatherManager(
            this,
            this.dimensionData.getCurrentWeather(),
            this.dimensionData.getNextWeather(),
            this.dimensionData.getWeatherDuration(),
            this.dimensionData.getWeatherIntensity(),
            this.dimensionData.getWeatherPower()
         );
      }

      this.chunkProvider = this.createChunkProvider();
      this.auroraProvider = new AuroraProvider(this, seed);
      this.seasonManager = SeasonManager.fromConfig(this, this.worldType.getSeasonConfig());
      this.biomeProvider = this.worldType.createBiomeProvider(this);
      if (isNewWorld) {
         this.worldType.onWorldCreation(this);
         this.getInitialSpawnLocation();
      }

      this.updateSkyBrightness();
      this.commandManager = new CommandManager(Global.isServer);
      this.commandManager.init();
   }

   public World(World world, Dimension dimension) {
      this.lockTimestamp = world.lockTimestamp;
      this.saveHandler = world.saveHandler;
      this.levelData = new LevelData(world.levelData);
      this.dimensionData = world.saveHandler.getDimensionData(dimension.id);
      if (this.dimensionData == null) {
         this.dimensionData = new DimensionData(dimension.defaultWorldType);
         this.weatherManager = new WeatherManager(this);
      } else {
         this.weatherManager = new WeatherManager(
            this,
            this.dimensionData.getCurrentWeather(),
            this.dimensionData.getNextWeather(),
            this.dimensionData.getWeatherDuration(),
            this.dimensionData.getWeatherIntensity(),
            this.dimensionData.getWeatherPower()
         );
      }

      this.worldType = this.dimensionData.getWorldType();
      this.savedDataStorage = new SavedDataStorage(this.saveHandler);
      this.dimension = dimension;
      this.chunkProvider = this.createChunkProvider();
      this.seasonManager = SeasonManager.fromConfig(this, this.worldType.getSeasonConfig());
      this.biomeProvider = this.worldType.createBiomeProvider(this);
      this.updateSkyBrightness();
      this.auroraProvider = new AuroraProvider(this, this.getRandomSeed());
      this.commandManager = new CommandManager(Global.isServer);
      this.commandManager.init();
   }

   public World(LevelStorage saveHandler, String name, Dimension dimension, WorldType worldType, long seed) {
      this.saveHandler = saveHandler;
      this.levelData = new LevelData(seed, name);
      this.dimension = dimension;
      this.dimensionData = new DimensionData(worldType);
      this.worldType = worldType;
      this.savedDataStorage = new SavedDataStorage(saveHandler);
      this.chunkProvider = this.createChunkProvider();
      this.seasonManager = SeasonManager.fromConfig(this, this.worldType.getSeasonConfig());
      this.updateSkyBrightness();
      this.weatherManager = new WeatherManager(this);
      this.auroraProvider = new AuroraProvider(this, seed);
      this.biomeProvider = this.worldType.createBiomeProvider(this);
      this.commandManager = new CommandManager(Global.isServer);
      this.commandManager.init();
   }

   public World() {
   }

   public <T> T getGameRuleValue(GameRule<T> gameRule) {
      return this.levelData.getGameRules().getValue(gameRule);
   }

   public SpawnerConfig getSpawnerConfig() {
      return this.levelData.getSpawnerConfig();
   }

   public int getHeightBlocks() {
      return 256;
   }

   public BiomeProvider getBiomeProvider() {
      return this.biomeProvider;
   }

   public CommandManager getCommandManager() {
      return this.commandManager;
   }

   protected IChunkProvider createChunkProvider() {
      IChunkLoader chunkLoader = this.saveHandler.getChunkLoader(this.dimension);
      return Global.accessor.createChunkProvider(this, chunkLoader);
   }

   protected void getInitialSpawnLocation() {
      this.findingSpawnPoint = true;
      this.getWorldType().getInitialSpawnLocation(this);
      this.findingSpawnPoint = false;
   }

   public void getRespawnLocation() {
      this.getWorldType().getRespawnLocation(this);
   }

   public int getTopBlock(int x, int z) {
      int y = this.levelData.getSpawnY() - 1;

      while (!this.isAirBlock(x, y + 1, z)) {
         y++;
      }

      return this.getBlockId(x, y, z);
   }

   public void spawnPlayerWithLoadedChunks(Player player) {
      try {
         CompoundTag nbttagcompound = this.levelData.getPlayerNBTTagCompound();
         if (nbttagcompound != null) {
            player.load(nbttagcompound);
            this.levelData.setPlayerNBTTagCompound(null);
         }

         int chunkX = MathHelper.floor_float((int)player.x) / 16;
         int chunkZ = MathHelper.floor_float((int)player.z) / 16;
         this.chunkProvider.setCurrentChunkOver(chunkX, chunkZ);
         this.entityJoinedWorld(player);
      } catch (Exception var5) {
         LOGGER.error("Failed to spawn player with loaded chunks!", (Throwable)var5);
      }
   }

   public void saveWorld(boolean saveImmediately, ProgressListener progressUpdate, boolean saveLevelData) {
      if (this.chunkProvider.canSave()) {
         if (saveLevelData) {
            if (progressUpdate != null) {
               progressUpdate.progressStart("Saving level data");
            }

            this.saveWorldData();
         }

         if (progressUpdate != null) {
            progressUpdate.progressStage("Saving chunks");
         }

         this.chunkProvider.saveChunks(saveImmediately, progressUpdate);
         if (progressUpdate != null) {
            progressUpdate.progressStop();
         }
      }
   }

   private void saveWorldData() {
      this.checkSessionLock();
      this.saveHandler.saveLevelDataAndPlayerData(this.levelData, this.players);
      this.saveHandler.saveDimensionData(this.dimension.id, this.dimensionData);
      this.savedDataStorage.save();
   }

   public boolean pauseScreenSave(int i) {
      if (!this.chunkProvider.canSave()) {
         return true;
      } else {
         if (i == 0) {
            this.saveWorldData();
         }

         return this.chunkProvider.saveChunks(false, null);
      }
   }

   @Nullable
   public Weather getCurrentWeather() {
      return this.weatherManager == null ? null : this.weatherManager.getCurrentWeather();
   }

   @Override
   public int getBlockId(int x, int y, int z) {
      if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
         return 0;
      } else if (y < 0) {
         return 0;
      } else {
         return y >= this.getHeightBlocks() ? 0 : this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16)).getBlockID(x & 15, y, z & 15);
      }
   }

   @Nullable
   @Override
   public Block<?> getBlock(int x, int y, int z) {
      return Blocks.getBlock(this.getBlockId(x, y, z));
   }

   @Override
   public double getBlockTemperature(int x, int z) {
      if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
         int chunkX = Math.floorDiv(x, 16);
         int chunkZ = Math.floorDiv(z, 16);
         double temperature = Double.NEGATIVE_INFINITY;
         Chunk chunk = null;
         if (this.isChunkLoaded(chunkX, chunkZ)) {
            chunk = this.getChunkFromChunkCoords(chunkX, chunkZ);
            temperature = chunk.getBlockTemperature(x & 15, z & 15);
         }

         if (temperature == Double.NEGATIVE_INFINITY) {
            temperature = this.getBiomeProvider().getTemperature(x, z);
            if (chunk != null) {
               chunk.setBlockTemperature(x & 15, z & 15, temperature);
            }
         }

         return temperature;
      } else {
         return 0.0;
      }
   }

   @Override
   public double getBlockHumidity(int x, int z) {
      if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
         int chunkX = Math.floorDiv(x, 16);
         int chunkZ = Math.floorDiv(z, 16);
         double humidity = Double.NEGATIVE_INFINITY;
         Chunk chunk = null;
         if (this.isChunkLoaded(chunkX, chunkZ)) {
            chunk = this.getChunkFromChunkCoords(chunkX, chunkZ);
            humidity = chunk.getBlockHumidity(x & 15, z & 15);
         }

         if (humidity == Double.NEGATIVE_INFINITY) {
            humidity = this.getBiomeProvider().getHumidity(x, z);
            if (chunk != null) {
               chunk.setBlockHumidity(x & 15, z & 15, humidity);
            }
         }

         return humidity;
      } else {
         return 0.0;
      }
   }

   @Override
   public SeasonManager getSeasonManager() {
      return this.seasonManager;
   }

   public double getBlockVariety(int x, int z) {
      if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
         int chunkX = Math.floorDiv(x, 16);
         int chunkZ = Math.floorDiv(z, 16);
         double variety = Double.NEGATIVE_INFINITY;
         Chunk chunk = null;
         if (this.isChunkLoaded(chunkX, chunkZ)) {
            chunk = this.getChunkFromChunkCoords(chunkX, chunkZ);
            variety = chunk.getBlockVariety(x & 15, z & 15);
         }

         if (variety == Double.NEGATIVE_INFINITY) {
            variety = this.getBiomeProvider().getVariety(x, z);
            if (chunk != null) {
               chunk.setBlockVariety(x & 15, z & 15, variety);
            }
         }

         return variety;
      } else {
         return 0.0;
      }
   }

   @NotNull
   @Override
   public Biome getBlockBiome(int x, int y, int z) {
      if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
         int chunkX = Math.floorDiv(x, 16);
         int chunkZ = Math.floorDiv(z, 16);
         Biome biome = null;
         Chunk chunk = null;
         if (this.isChunkLoaded(chunkX, chunkZ)) {
            chunk = this.getChunkFromChunkCoords(chunkX, chunkZ);
            biome = chunk.getBlockBiome(x & 15, y, z & 15);
         }

         if (biome == null) {
            biome = this.getBiomeProvider().getBiome(x, y, z);
            if (chunk != null) {
               chunk.setBlockBiome(x & 15, y, z & 15, biome);
            }
         }

         assert biome != null : "Biome should never be null!";

         return biome;
      } else {
         return Biomes.OVERWORLD_PLAINS;
      }
   }

   public boolean getBlockLitInteriorSurface(int x, int y, int z) {
      if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
         return true;
      } else if (y < 0) {
         return true;
      } else {
         return y >= this.getHeightBlocks() ? true : Block.getIsLitInteriorSurface(this, x, y, z);
      }
   }

   public boolean isAirBlock(int x, int y, int z) {
      return this.getBlockId(x, y, z) == 0;
   }

   public boolean isBlockLoaded(int x, int y, int z) {
      return y >= 0 && y < this.getHeightBlocks() ? this.isChunkLoaded(Math.floorDiv(x, 16), Math.floorDiv(z, 16)) : false;
   }

   public boolean areBlocksLoaded(int x, int y, int z, int range) {
      return this.areBlocksLoaded(x - range, y - range, z - range, x + range, y + range, z + range);
   }

   public boolean areBlocksLoaded(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      if (maxY >= 0 && minY < this.getHeightBlocks()) {
         minX >>= 4;
         minZ >>= 4;
         maxX >>= 4;
         maxZ >>= 4;

         for (int chunkX = minX; chunkX <= maxX; chunkX++) {
            for (int chunkZ = minZ; chunkZ <= maxZ; chunkZ++) {
               if (!this.isChunkLoaded(chunkX, chunkZ)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isChunkLoaded(int x, int z) {
      return this.chunkProvider.isChunkLoaded(x, z);
   }

   public Chunk getChunkFromBlockCoords(int x, int z) {
      return this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
   }

   public Chunk getChunkFromChunkCoords(int x, int z) {
      return this.chunkProvider.provideChunk(x, z);
   }

   public boolean setBlockAndMetadata(int x, int y, int z, int id, int meta) {
      if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
         return false;
      } else if (y < 0) {
         return false;
      } else if (y >= this.getHeightBlocks()) {
         return false;
      } else {
         Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
         return chunk.setBlockIDWithMetadata(x & 15, y, z & 15, id, meta);
      }
   }

   public boolean setBlockRaw(int x, int y, int z, int id) {
      return this.setBlockAndMetadataRaw(x, y, z, id, this.getBlockMetadata(x, y, z));
   }

   public boolean setBlockAndMetadataRaw(int x, int y, int z, int id, int meta) {
      if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
         return false;
      } else if (y < 0) {
         return false;
      } else if (y >= this.getHeightBlocks()) {
         return false;
      } else {
         Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
         return chunk.setBlockIDWithMetadataRaw(x & 15, y, z & 15, id, meta);
      }
   }

   public boolean setBlock(int x, int y, int z, int id) {
      if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
         return false;
      } else if (y < 0) {
         return false;
      } else if (y >= this.getHeightBlocks()) {
         return false;
      } else {
         Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
         return chunk.setBlockID(x & 15, y, z & 15, id);
      }
   }

   @Override
   public Material getBlockMaterial(int x, int y, int z) {
      int l = this.getBlockId(x, y, z);
      return l == 0 ? Material.air : Blocks.blocksList[l].getMaterial();
   }

   @Override
   public int getBlockMetadata(int x, int y, int z) {
      if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
         return 0;
      } else if (y < 0) {
         return 0;
      } else if (y >= this.getHeightBlocks()) {
         return 0;
      } else {
         Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
         x &= 15;
         z &= 15;
         return chunk.getBlockMetadata(x, y, z);
      }
   }

   public void setBlockMetadataWithNotify(int x, int y, int z, int meta) {
      if (this.setBlockMetadata(x, y, z, meta)) {
         this.markBlockNeedsUpdate(x, y, z);
         int id = this.getBlockId(x, y, z);
         if (Blocks.neighborNotifyOnMetadataChangeDisabled[id & 16383]) {
            this.notifyBlocksOfNeighborChange(x, y, z, id);
         }
      }
   }

   public boolean setBlockMetadata(int x, int y, int z, int meta) {
      if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
         return false;
      } else if (y < 0) {
         return false;
      } else if (y >= this.getHeightBlocks()) {
         return false;
      } else {
         Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
         x &= 15;
         z &= 15;
         chunk.setBlockMetadata(x, y, z, meta);
         return true;
      }
   }

   public boolean setBlockWithNotify(int x, int y, int z, int id) {
      if (this.setBlock(x, y, z, id)) {
         this.notifyBlockChange(x, y, z, id);
         return true;
      } else {
         return false;
      }
   }

   public boolean setBlockAndMetadataWithNotify(int x, int y, int z, int id, int meta) {
      if (this.setBlockAndMetadata(x, y, z, id, meta)) {
         this.notifyBlockChange(x, y, z, id);
         return true;
      } else {
         return false;
      }
   }

   public void markBlockNeedsUpdate(int x, int y, int z) {
      for (LevelListener listener : this.listeners) {
         listener.blockChanged(x, y, z);
      }
   }

   public void notifyBlockChange(int x, int y, int z, int id) {
      this.markBlockNeedsUpdate(x, y, z);
      this.notifyBlocksOfNeighborChange(x, y, z, id);
   }

   public void markBlocksDirtyVertical(int x, int z, int y0, int y1) {
      if (y0 > y1) {
         int t = y1;
         y1 = y0;
         y0 = t;
      }

      this.markBlocksDirty(x, y0, z, x, y1, z);
   }

   public void markBlockDirty(int x, int y, int z) {
      for (LevelListener listener : this.listeners) {
         listener.setBlocksDirty(x, y, z, x, y, z);
      }
   }

   public void markBlocksDirty(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      for (LevelListener listener : this.listeners) {
         listener.setBlocksDirty(minX, minY, minZ, maxX, maxY, maxZ);
      }
   }

   public void notifyBlocksOfNeighborChange(int x, int y, int z, int blockId) {
      this.notifyBlockOfNeighborChange(x - 1, y, z, blockId);
      this.notifyBlockOfNeighborChange(x + 1, y, z, blockId);
      this.notifyBlockOfNeighborChange(x, y - 1, z, blockId);
      this.notifyBlockOfNeighborChange(x, y + 1, z, blockId);
      this.notifyBlockOfNeighborChange(x, y, z - 1, blockId);
      this.notifyBlockOfNeighborChange(x, y, z + 1, blockId);
   }

   private void notifyBlockOfNeighborChange(int x, int y, int z, int blockId) {
      if (!this.noNeighborUpdate && !this.isClientSide) {
         Block<?> block = Blocks.blocksList[this.getBlockId(x, y, z)];
         if (block != null) {
            block.onNeighborBlockChange(this, x, y, z, blockId);
         }
      }
   }

   public boolean canBlockSeeTheSky(int x, int y, int z) {
      return this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16)).canBlockSeeTheSky(x & 15, y, z & 15);
   }

   public int getFullBlockLightValue(int x, int y, int z) {
      if (y < 0) {
         return 0;
      } else {
         if (y >= this.getHeightBlocks()) {
            y = this.getHeightBlocks() - 1;
         }

         return this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16)).getRawBrightness(x & 15, y, z & 15, 0);
      }
   }

   public int getBlockLightValue(int x, int y, int z) {
      return this.getBlockLightValue_do(x, y, z, true);
   }

   public int getBlockLightValue_do(int x, int y, int z, boolean first) {
      if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
         return 15;
      } else if (first && this.getBlockLitInteriorSurface(x, y, z)) {
         int i1 = this.getBlockLightValue_do(x, y + 1, z, false);
         int j1 = this.getBlockLightValue_do(x + 1, y, z, false);
         int k1 = this.getBlockLightValue_do(x - 1, y, z, false);
         int l1 = this.getBlockLightValue_do(x, y, z + 1, false);
         int i2 = this.getBlockLightValue_do(x, y, z - 1, false);
         if (j1 > i1) {
            i1 = j1;
         }

         if (k1 > i1) {
            i1 = k1;
         }

         if (l1 > i1) {
            i1 = l1;
         }

         if (i2 > i1) {
            i1 = i2;
         }

         return i1;
      } else if (y < 0) {
         return 0;
      } else {
         if (y >= this.getHeightBlocks()) {
            y = this.getHeightBlocks() - 1;
         }

         Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
         x &= 15;
         z &= 15;
         return chunk.getRawBrightness(x, y, z, this.skyDarken);
      }
   }

   public boolean canExistingBlockSeeTheSky(int x, int y, int z) {
      if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
         return false;
      } else if (y < 0) {
         return false;
      } else if (y >= this.getHeightBlocks()) {
         return true;
      } else if (!this.isChunkLoaded(Math.floorDiv(x, 16), Math.floorDiv(z, 16))) {
         return false;
      } else {
         Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
         x &= 15;
         z &= 15;
         return chunk.canBlockSeeTheSky(x, y, z);
      }
   }

   public int getHeightValue(int x, int z) {
      if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
         if (!this.isChunkLoaded(Math.floorDiv(x, 16), Math.floorDiv(z, 16))) {
            return 0;
         } else {
            Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
            return chunk.getHeightValue(x & 15, z & 15);
         }
      } else {
         return 0;
      }
   }

   public void neighborLightPropagationChanged(LightLayer lightLayer, int x, int y, int z, int lightValue) {
      if (!this.worldType.hasCeiling() || lightLayer != LightLayer.Sky) {
         if (this.isBlockLoaded(x, y, z)) {
            if (lightLayer == LightLayer.Sky) {
               if (this.canExistingBlockSeeTheSky(x, y, z)) {
                  lightValue = 15;
               }
            } else if (lightLayer == LightLayer.Block) {
               int i1 = this.getBlockId(x, y, z);
               if (Blocks.lightEmission[i1] > lightValue) {
                  lightValue = Blocks.lightEmission[i1];
               }
            }

            if (this.getSavedLightValue(lightLayer, x, y, z) != lightValue) {
               this.scheduleLightingUpdate(lightLayer, x, y, z, x, y, z);
            }
         }
      }
   }

   @Override
   public int getSavedLightValue(LightLayer layer, int x, int y, int z) {
      if (y < 0) {
         y = 0;
      }

      if (y >= this.getHeightBlocks()) {
         y = this.getHeightBlocks() - 1;
      }

      if (y >= 0 && y < this.getHeightBlocks() && x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
         int l = Math.floorDiv(x, 16);
         int i1 = Math.floorDiv(z, 16);
         if (!this.isChunkLoaded(l, i1)) {
            return 0;
         } else {
            Chunk chunk = this.getChunkFromChunkCoords(l, i1);
            return chunk.getBrightness(layer, x & 15, y, z & 15);
         }
      } else {
         return layer.defaultLightLevel;
      }
   }

   @Override
   public boolean isRetro() {
      return this.worldType.isRetro();
   }

   public void setLightValue(LightLayer layer, int x, int y, int z, int value) {
      if (x >= -32000000 && z >= -32000000 && x < 32000000 && z <= 32000000) {
         if (y >= 0) {
            if (y < this.getHeightBlocks()) {
               if (this.isChunkLoaded(Math.floorDiv(x, 16), Math.floorDiv(z, 16))) {
                  Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
                  chunk.setBrightness(layer, x & 15, y, z & 15, value);

                  for (LevelListener listener : this.listeners) {
                     listener.blockChanged(x, y, z);
                  }
               }
            }
         }
      }
   }

   @Override
   public float getBrightness(int x, int y, int z, int blockLightValue) {
      int i1 = this.getBlockLightValue(x, y, z);
      if (i1 < blockLightValue) {
         i1 = blockLightValue;
      }

      return this.worldType.getBrightnessRamp()[i1];
   }

   @Override
   public int getLightmapCoord(int x, int y, int z, int blockLightValue) {
      int skyLight = this.getSavedLightValue(LightLayer.Sky, x, y, z);
      int blockLight = Math.max(this.getSavedLightValue(LightLayer.Block, x, y, z), blockLightValue);
      if (this.getBlockLitInteriorSurface(x, y, z)) {
         skyLight = Math.max(skyLight, this.getSavedLightValue(LightLayer.Sky, x, y + 1, z));
         skyLight = Math.max(skyLight, this.getSavedLightValue(LightLayer.Sky, x, y - 1, z));
         skyLight = Math.max(skyLight, this.getSavedLightValue(LightLayer.Sky, x + 1, y, z));
         skyLight = Math.max(skyLight, this.getSavedLightValue(LightLayer.Sky, x - 1, y, z));
         skyLight = Math.max(skyLight, this.getSavedLightValue(LightLayer.Sky, x, y, z + 1));
         skyLight = Math.max(skyLight, this.getSavedLightValue(LightLayer.Sky, x, y, z - 1));
         blockLight = Math.max(blockLight, this.getSavedLightValue(LightLayer.Block, x, y + 1, z));
         blockLight = Math.max(blockLight, this.getSavedLightValue(LightLayer.Block, x, y - 1, z));
         blockLight = Math.max(blockLight, this.getSavedLightValue(LightLayer.Block, x + 1, y, z));
         blockLight = Math.max(blockLight, this.getSavedLightValue(LightLayer.Block, x - 1, y, z));
         blockLight = Math.max(blockLight, this.getSavedLightValue(LightLayer.Block, x, y, z + 1));
         blockLight = Math.max(blockLight, this.getSavedLightValue(LightLayer.Block, x, y, z - 1));
      }

      return this.getLightmapCoord(skyLight, blockLight);
   }

   @Override
   public int getLightmapCoord(int skylight, int blocklight) {
      return 0;
   }

   @Override
   public float getLightBrightness(int x, int y, int z) {
      return this.worldType.getBrightnessRamp()[this.getBlockLightValue(x, y, z)];
   }

   public boolean isDaytime() {
      return this.skyDarken < 4;
   }

   @Nullable
   public HitResult checkBlockCollisionBetweenPoints(Vec3 start, Vec3 end) {
      return this.checkBlockCollisionBetweenPoints(start, end, false, false, false);
   }

   @Nullable
   public HitResult checkBlockCollisionBetweenPoints(Vec3 start, Vec3 end, boolean shouldCollideWithFluids) {
      return this.checkBlockCollisionBetweenPoints(start, end, shouldCollideWithFluids, false, false);
   }

   @Nullable
   public HitResult checkBlockCollisionBetweenPoints(
      Vec3 start, Vec3 end, boolean shouldCollideWithFluids, boolean ignoreNonColliderBlocks, boolean useSelectorBoxes
   ) {
      if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
         return null;
      } else if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
         int blockEndX = MathHelper.floor(end.x);
         int blockEndY = MathHelper.floor(end.y);
         int blockEndZ = MathHelper.floor(end.z);
         int blockStartX = MathHelper.floor(start.x);
         int blockStartY = MathHelper.floor(start.y);
         int blockStartZ = MathHelper.floor(start.z);
         int id = this.getBlockId(blockStartX, blockStartY, blockStartZ);
         int meta = this.getBlockMetadata(blockStartX, blockStartY, blockStartZ);
         Block<?> block = Blocks.blocksList[id];
         if ((
               !ignoreNonColliderBlocks
                  || block == null
                  || (
                        useSelectorBoxes
                           ? block.getSelectedBoundingBoxFromPool(this, blockStartX, blockStartY, blockStartZ)
                           : block.getCollisionBoundingBoxFromPool(this, blockStartX, blockStartY, blockStartZ)
                     )
                     != null
            )
            && id > 0
            && block.canCollideCheck(meta, shouldCollideWithFluids)) {
            HitResult hitResult = block.collisionRayTrace(this, blockStartX, blockStartY, blockStartZ, start, end, useSelectorBoxes);
            if (hitResult != null) {
               return hitResult;
            }
         }

         int l1 = 200;

         while (l1-- >= 0) {
            if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
               return null;
            }

            if (blockStartX == blockEndX && blockStartY == blockEndY && blockStartZ == blockEndZ) {
               return null;
            }

            boolean flag2 = true;
            boolean flag3 = true;
            boolean flag4 = true;
            double d = 999.0;
            double d1 = 999.0;
            double d2 = 999.0;
            if (blockEndX > blockStartX) {
               d = blockStartX + 1.0;
            } else if (blockEndX < blockStartX) {
               d = blockStartX + 0.0;
            } else {
               flag2 = false;
            }

            if (blockEndY > blockStartY) {
               d1 = blockStartY + 1.0;
            } else if (blockEndY < blockStartY) {
               d1 = blockStartY + 0.0;
            } else {
               flag3 = false;
            }

            if (blockEndZ > blockStartZ) {
               d2 = blockStartZ + 1.0;
            } else if (blockEndZ < blockStartZ) {
               d2 = blockStartZ + 0.0;
            } else {
               flag4 = false;
            }

            double d3 = 999.0;
            double d4 = 999.0;
            double d5 = 999.0;
            double d6 = end.x - start.x;
            double d7 = end.y - start.y;
            double d8 = end.z - start.z;
            if (flag2) {
               d3 = (d - start.x) / d6;
            }

            if (flag3) {
               d4 = (d1 - start.y) / d7;
            }

            if (flag4) {
               d5 = (d2 - start.z) / d8;
            }

            byte byte0 = 0;
            if (d3 < d4 && d3 < d5) {
               if (blockEndX > blockStartX) {
                  byte0 = 4;
               } else {
                  byte0 = 5;
               }

               start.x = d;
               start.y += d7 * d3;
               start.z += d8 * d3;
            } else if (d4 < d5) {
               if (blockEndY > blockStartY) {
                  byte0 = 0;
               } else {
                  byte0 = 1;
               }

               start.x += d6 * d4;
               start.y = d1;
               start.z += d8 * d4;
            } else {
               if (blockEndZ > blockStartZ) {
                  byte0 = 2;
               } else {
                  byte0 = 3;
               }

               start.x += d6 * d5;
               start.y += d7 * d5;
               start.z = d2;
            }

            Vec3 vec32 = Vec3.getTempVec3(start.x, start.y, start.z);
            blockStartX = (int)(vec32.x = MathHelper.floor(start.x));
            if (byte0 == 5) {
               blockStartX--;
               vec32.x++;
            }

            blockStartY = (int)(vec32.y = MathHelper.floor(start.y));
            if (byte0 == 1) {
               blockStartY--;
               vec32.y++;
            }

            blockStartZ = (int)(vec32.z = MathHelper.floor(start.z));
            if (byte0 == 3) {
               blockStartZ--;
               vec32.z++;
            }

            Block<?> block1 = this.getBlock(blockStartX, blockStartY, blockStartZ);
            int metadata1 = this.getBlockMetadata(blockStartX, blockStartY, blockStartZ);
            if ((
                  !ignoreNonColliderBlocks
                     || block1 == null
                     || (
                           useSelectorBoxes
                              ? block1.getSelectedBoundingBoxFromPool(this, blockStartX, blockStartY, blockStartZ)
                              : block1.getCollisionBoundingBoxFromPool(this, blockStartX, blockStartY, blockStartZ)
                        )
                        != null
               )
               && block1 != null
               && block1.canCollideCheck(metadata1, shouldCollideWithFluids)) {
               HitResult hitResult = block1.collisionRayTrace(this, blockStartX, blockStartY, blockStartZ, start, end, useSelectorBoxes);
               if (hitResult != null) {
                  return hitResult;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public void playSoundAtEntity(@Nullable Entity player, @NotNull Entity entity, String soundPath, float volume, float pitch) {
      for (LevelListener listener : this.listeners) {
         listener.playSound(player, soundPath, SoundCategory.ENTITY_SOUNDS, entity.x, entity.y - entity.heightOffset, entity.z, volume, pitch);
      }
   }

   public void playSoundEffect(@Nullable Entity player, SoundCategory category, double x, double y, double z, String soundPath, float volume, float pitch) {
      for (LevelListener listener : this.listeners) {
         listener.playSound(player, soundPath, category, x, y, z, volume, pitch);
      }
   }

   public void playBlockSoundEffect(@Nullable Entity player, double x, double y, double z, Block<?> block, EnumBlockSoundEffectType soundType) {
      if (block != null) {
         BlockSound sound = block.getSound();
         if (sound != null) {
            String name;
            if (soundType == EnumBlockSoundEffectType.MINE) {
               name = sound.getBreakSoundName();
            } else {
               name = sound.getStepSoundName();
            }

            this.playSoundEffect(
               player, SoundCategory.WORLD_SOUNDS, x, y, z, name, soundType.modifyVolume(sound.getVolume()), soundType.modifyPitch(sound.getPitch())
            );
         }
      }
   }

   public void playRecord(String soundPath, String author, int x, int y, int z) {
      for (int l = 0; l < this.listeners.size(); l++) {
         this.listeners.get(l).playStreamingMusic(soundPath, author, x, y, z);
      }
   }

   public void spawnParticle(String particleKey, double x, double y, double z, double motionX, double motionY, double motionZ, int data, double maxDistance) {
      for (int i = 0; i < this.listeners.size(); i++) {
         this.listeners.get(i).addParticle(particleKey, x, y, z, motionX, motionY, motionZ, data, maxDistance);
      }
   }

   public void spawnParticle(String particleKey, double x, double y, double z, double motionX, double motionY, double motionZ, int data) {
      for (int i = 0; i < this.listeners.size(); i++) {
         this.listeners.get(i).addParticle(particleKey, x, y, z, motionX, motionY, motionZ, data);
      }
   }

   public boolean addWeatherEffect(Entity entity) {
      this.weatherEffects.add(entity);
      return true;
   }

   public void addRainbow(int rainbowTicks) {
      this.rainbowTicks = this.startingRainbowTicks = rainbowTicks;
   }

   public boolean entityJoinedWorld(Entity entity) {
      int i = MathHelper.floor(entity.x / 16.0);
      int j = MathHelper.floor(entity.z / 16.0);
      boolean flag = entity instanceof Player;
      if (!flag && !this.isChunkLoaded(i, j)) {
         return false;
      } else {
         if (entity instanceof Player) {
            Player entityplayer = (Player)entity;
            this.players.add(entityplayer);
            this.updateEnoughPlayersSleepingFlag(null);
         }

         this.getChunkFromChunkCoords(i, j).addEntity(entity);
         this.loadedEntityList.add(entity);
         this.obtainEntitySkin(entity);
         return true;
      }
   }

   protected void obtainEntitySkin(Entity entity) {
      for (int i = 0; i < this.listeners.size(); i++) {
         this.listeners.get(i).entityAdded(entity);
      }
   }

   protected void releaseEntitySkin(Entity entity) {
      for (int i = 0; i < this.listeners.size(); i++) {
         this.listeners.get(i).entityRemoved(entity);
      }
   }

   public void setEntityDead(Entity entity) {
      if (entity.passenger != null) {
         entity.passenger.startRiding(null);
      }

      if (entity.vehicle != null) {
         entity.startRiding(null);
      }

      entity.remove();
      if (entity instanceof Player) {
         this.players.remove((Player)entity);
         this.updateEnoughPlayersSleepingFlag(null);
      }
   }

   public void removePlayer(Entity entity) {
      entity.remove();
      if (entity instanceof Player) {
         this.players.remove((Player)entity);
         this.updateEnoughPlayersSleepingFlag(null);
      }

      int i = entity.chunkCoordX;
      int j = entity.chunkCoordZ;
      if (entity.addedToChunk && this.isChunkLoaded(i, j)) {
         this.getChunkFromChunkCoords(i, j).removeEntity(entity);
      }

      this.loadedEntityList.remove(entity);
      this.releaseEntitySkin(entity);
   }

   public void addListener(LevelListener iworldaccess) {
      this.listeners.add(iworldaccess);
   }

   public void removeListener(LevelListener iworldaccess) {
      this.listeners.remove(iworldaccess);
   }

   public List<AABB> getCubes(Entity entity, AABB aabb) {
      this.collidingBoundingBoxes.clear();
      int minX = MathHelper.floor(aabb.minX);
      int maxX = MathHelper.floor(aabb.maxX + 1.0);
      int minY = MathHelper.floor(aabb.minY);
      int maxY = MathHelper.floor(aabb.maxY + 1.0);
      int minZ = MathHelper.floor(aabb.minZ);
      int maxZ = MathHelper.floor(aabb.maxZ + 1.0);

      for (int dx = minX - 1; dx <= maxX; dx++) {
         for (int dz = minZ - 1; dz <= maxZ; dz++) {
            if (this.isBlockLoaded(dx, 64, dz)) {
               for (int dy = minY - 1; dy <= maxY; dy++) {
                  Block<?> block = this.getBlock(dx, dy, dz);
                  if (block != null) {
                     int metadata = this.getBlockMetadata(dx, dy, dz);
                     if (block.collidesWithEntity(entity, this, dx, dy, dz) && entity.collidesWithBlock(block, metadata)) {
                        block.getCollidingBoundingBoxes(this, dx, dy, dz, aabb, this.collidingBoundingBoxes);
                     }
                  }
               }
            }
         }
      }

      double radius = 0.25;

      for (Entity e : this.getEntitiesWithinAABBExcludingEntity(entity, aabb.grow(radius, radius, radius))) {
         AABB entityBB = e.getBb();
         if (entityBB != null && entityBB.intersects(aabb)) {
            this.collidingBoundingBoxes.add(entityBB);
         }
      }

      return this.collidingBoundingBoxes;
   }

   public List<AABB> getCollidingSolidBlockBoundingBoxes(Entity entity, AABB axisalignedbb) {
      this.collidingBoundingBoxes.clear();
      int minX = MathHelper.floor(axisalignedbb.minX);
      int maxX = MathHelper.floor(axisalignedbb.maxX + 1.0);
      int minY = MathHelper.floor(axisalignedbb.minY);
      int maxY = MathHelper.floor(axisalignedbb.maxY + 1.0);
      int minZ = MathHelper.floor(axisalignedbb.minZ);
      int maxZ = MathHelper.floor(axisalignedbb.maxZ + 1.0);

      for (int dx = minX - 1; dx <= maxX; dx++) {
         for (int dz = minZ - 1; dz <= maxZ; dz++) {
            if (this.isBlockLoaded(dx, 64, dz)) {
               for (int dy = minY - 1; dy <= maxY; dy++) {
                  Block<?> block = Blocks.blocksList[this.getBlockId(dx, dy, dz)];
                  if (block != null
                     && block.isSolidRender()
                     && (
                        !(entity instanceof EntityItem)
                           || block.id() != Blocks.MESH.id() && block.id() != Blocks.SPIKES.id() && block.id() != Blocks.MOBSPAWNER.id()
                     )) {
                     block.getCollidingBoundingBoxes(this, dx, dy, dz, axisalignedbb, this.collidingBoundingBoxes);
                  }
               }
            }
         }
      }

      double d = 0.25;
      List<Entity> list = this.getEntitiesWithinAABBExcludingEntity(entity, axisalignedbb.grow(d, d, d));

      for (int j2 = 0; j2 < list.size(); j2++) {
         AABB aabb = list.get(j2).getBb();
         if (aabb != null && aabb.intersects(axisalignedbb)) {
            this.collidingBoundingBoxes.add(aabb);
         }
      }

      return this.collidingBoundingBoxes;
   }

   public float getAmbientBrightness(float partialTicks) {
      float angle = this.getCelestialAngle(partialTicks);
      float bright = 1.0F - (MathHelper.cos(angle * (float) Math.PI * 2.0F) * 2.0F + 0.2F);
      bright = 1.0F - MathHelper.clamp(bright, 0.0F, 1.0F);
      Weather weather = this.weatherManager.getCurrentWeather();
      if (weather != null) {
         if (weather == Weathers.OVERWORLD_RAIN) {
            float power = this.weatherManager.getWeatherIntensity() * this.weatherManager.getWeatherPower();
            bright = (float)(bright * (1.0 - power * 5.0F / 16.0));
         }

         if (weather == Weathers.OVERWORLD_STORM) {
            float power = this.weatherManager.getWeatherIntensity() * this.weatherManager.getWeatherPower();
            bright = (float)(bright * (1.0 - power * 5.0F / 16.0));
            bright = (float)(bright * (1.0 - power * 5.0F / 16.0));
         }
      }

      return bright * 0.8F + 0.2F;
   }

   public float getCelestialAngle(float partialTick) {
      if (!this.getGameRuleValue(GameRules.DO_DAY_CYCLE)) {
         partialTick = 1.0F;
      }

      return this.worldType.getCelestialAngle(this, this.levelData.getWorldTime(), partialTick);
   }

   public int findTopSolidBlock(int x, int z) {
      Chunk chunk = this.getChunkFromBlockCoords(x, z);
      int y = this.getHeightBlocks() - 1;
      x &= 15;

      for (int var8 = z & 15; y > 0; y--) {
         int id = chunk.getBlockID(x, y, var8);
         Material material = id != 0 ? Blocks.blocksList[id].getMaterial() : Material.air;
         if (material.blocksMotion() || material.isLiquid()) {
            return y + 1;
         }
      }

      return -1;
   }

   public int findTopSolidNonLiquidBlock(int x, int z) {
      Chunk chunk = this.getChunkFromBlockCoords(x, z);
      int y = this.getHeightBlocks() - 1;
      x &= 15;

      for (int var8 = z & 15; y > 0; y--) {
         Block<?> block = Blocks.getBlock(chunk.getBlockID(x, y, var8));
         Material material = block != null ? block.getMaterial() : Material.air;
         if (material.blocksMotion()) {
            return y + 1;
         }
      }

      return -1;
   }

   public float getStarBrightness(float partialTick) {
      float f1 = this.getCelestialAngle(partialTick);
      float f2 = 1.0F - (MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.75F);
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      return f2 * f2 * 0.5F;
   }

   public void scheduleBlockUpdate(int x, int y, int z, int id, int delay) {
      NextTickListEntry entry = new NextTickListEntry(x, y, z, id);
      byte radius = 8;
      if (this.scheduledUpdatesAreImmediate && !this.immediatelyUpdatedPositions.contains(entry)) {
         if (this.areBlocksLoaded(entry.x - radius, entry.y - radius, entry.z - radius, entry.x + radius, entry.y + radius, entry.z + radius)) {
            int id1 = this.getBlockId(entry.x, entry.y, entry.z);
            if (id1 == entry.blockId && id1 > 0) {
               this.immediatelyUpdatedPositions.add(entry);
               Blocks.blocksList[id1].updateTick(this, entry.x, entry.y, entry.z, this.rand);
            }
         }
      } else {
         if (this.areBlocksLoaded(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius)) {
            if (id > 0) {
               entry.setDelay(delay + this.runtime);
            }

            if (!this.scheduledTickSet.contains(entry)) {
               this.scheduledTickSet.add(entry);
               this.scheduledTickTreeSet.add(entry);
            }
         }
      }
   }

   public void updateEntities() {
      for (int i = 0; i < this.weatherEffects.size(); i++) {
         Entity entity = this.weatherEffects.get(i);
         entity.tick();
         if (entity.removed) {
            this.weatherEffects.remove(i--);
         }
      }

      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for (int j = 0; j < this.unloadedEntityList.size(); j++) {
         Entity entity1 = this.unloadedEntityList.get(j);
         int i1 = entity1.chunkCoordX;
         int k1 = entity1.chunkCoordZ;
         if (entity1.addedToChunk && this.isChunkLoaded(i1, k1)) {
            this.getChunkFromChunkCoords(i1, k1).removeEntity(entity1);
         }
      }

      for (int k = 0; k < this.unloadedEntityList.size(); k++) {
         this.releaseEntitySkin(this.unloadedEntityList.get(k));
      }

      this.unloadedEntityList.clear();

      for (int l = 0; l < this.loadedEntityList.size(); l++) {
         Entity entity2 = this.loadedEntityList.get(l);
         if (entity2.vehicle != null) {
            if (!entity2.vehicle.isRemoved() && entity2.vehicle.getPassenger() == entity2) {
               continue;
            }

            entity2.vehicle.setPassenger(null);
            entity2.vehicle = null;
         }

         if (!entity2.removed) {
            this.updateEntity(entity2);
         }

         if (entity2.removed) {
            int j1 = entity2.chunkCoordX;
            int l1 = entity2.chunkCoordZ;
            if (entity2.addedToChunk && this.isChunkLoaded(j1, l1)) {
               this.getChunkFromChunkCoords(j1, l1).removeEntity(entity2);
            }

            this.loadedEntityList.remove(l--);
            this.releaseEntitySkin(entity2);
         }
      }

      this.updatingBlockEntities = true;

      for (int ix = this.loadedTileEntityList.size() - 1; ix >= 0; ix--) {
         TileEntity tileEntity = this.loadedTileEntityList.get(ix);
         if (!tileEntity.isInvalid() && this.isBlockLoaded(tileEntity.x, tileEntity.y, tileEntity.z)) {
            tileEntity.tick();
         }

         if (tileEntity.isInvalid()) {
            this.loadedTileEntityList.remove(ix);
            Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(tileEntity.x, 16), Math.floorDiv(tileEntity.z, 16));
            if (chunk != null) {
               chunk.removeTileEntity(tileEntity);
            }
         }
      }

      this.updatingBlockEntities = false;
   }

   public void addAllBlockEntities(Collection<TileEntity> blockEntities) {
      this.loadedTileEntityList.addAll(blockEntities);
   }

   public void updateEntity(Entity entity) {
      this.updateEntityWithOptionalForce(entity, true);
   }

   public void updateEntityWithOptionalForce(Entity entity, boolean flag) {
      int i = MathHelper.floor(entity.x);
      int j = MathHelper.floor(entity.z);
      byte byte0 = 32;
      if (flag && !this.areBlocksLoaded(i - byte0, 0, j - byte0, i + byte0, this.getHeightBlocks(), j + byte0)) {
         if (entity instanceof Mob) {
            ((Mob)entity).tryToDespawn();
         }
      } else {
         entity.xo = entity.x;
         entity.yo = entity.y;
         entity.zo = entity.z;
         entity.yRotO = entity.yRot;
         entity.xRotO = entity.xRot;
         if (flag && entity.addedToChunk) {
            if (entity.vehicle != null) {
               entity.rideTick();
            } else {
               entity.tick();
            }
         }

         if (Double.isNaN(entity.x) || Double.isInfinite(entity.x)) {
            entity.x = entity.xo;
         }

         if (Double.isNaN(entity.y) || Double.isInfinite(entity.y)) {
            entity.y = entity.yo;
         }

         if (Double.isNaN(entity.z) || Double.isInfinite(entity.z)) {
            entity.z = entity.zo;
         }

         if (Double.isNaN(entity.xRot) || Double.isInfinite(entity.xRot)) {
            entity.xRot = entity.xRotO;
         }

         if (Double.isNaN(entity.yRot) || Double.isInfinite(entity.yRot)) {
            entity.yRot = entity.yRotO;
         }

         int k = MathHelper.floor(entity.x / 16.0);
         int l = MathHelper.floor(entity.y / 16.0);
         int i1 = MathHelper.floor(entity.z / 16.0);
         if (!entity.addedToChunk || entity.chunkCoordX != k || entity.chunkCoordY != l || entity.chunkCoordZ != i1) {
            if (entity.addedToChunk && this.isChunkLoaded(entity.chunkCoordX, entity.chunkCoordZ)) {
               this.getChunkFromChunkCoords(entity.chunkCoordX, entity.chunkCoordZ).removeEntityAtIndex(entity, entity.chunkCoordY);
            }

            if (this.isChunkLoaded(k, i1)) {
               entity.addedToChunk = true;
               this.getChunkFromChunkCoords(k, i1).addEntity(entity);
            } else {
               entity.addedToChunk = false;
            }
         }

         if (flag && entity.addedToChunk && entity.passenger != null) {
            if (!entity.passenger.removed && entity.passenger.vehicle == entity) {
               this.updateEntity(entity.passenger);
            } else {
               entity.passenger.vehicle = null;
               entity.passenger = null;
            }
         }
      }
   }

   public boolean checkIfAABBIsClear(AABB axisalignedbb) {
      if (axisalignedbb == null) {
         return true;
      } else {
         List<Entity> list = this.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb);

         for (int i = 0; i < list.size(); i++) {
            Entity entity = list.get(i);
            if (!(entity instanceof MobFireflyCluster) && !entity.removed && entity.blocksBuilding) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean getIsAnySolidGround(AABB boundingBox) {
      int minX = MathHelper.floor(boundingBox.minX);
      int maxX = MathHelper.floor(boundingBox.maxX);
      int minY = MathHelper.floor(boundingBox.minY);
      int maxY = MathHelper.floor(boundingBox.maxY);
      int minZ = MathHelper.floor(boundingBox.minZ);
      int maxZ = MathHelper.floor(boundingBox.maxZ);

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               Block<?> block = Blocks.blocksList[this.getBlockId(x, y, z)];
               if (block != null) {
                  return true;
               }
            }
         }
      }

      for (Entity entity : this.getEntitiesWithinAABBExcludingEntity(null, boundingBox)) {
         AABB aabb = entity.getBb();
         if (aabb != null && aabb.intersects(boundingBox)) {
            this.collidingBoundingBoxes.add(aabb);
         }
      }

      return false;
   }

   public boolean getIsAnyLiquid(AABB boundingBox) {
      int minX = MathHelper.floor(boundingBox.minX);
      int maxX = MathHelper.floor(boundingBox.maxX);
      int minY = MathHelper.floor(boundingBox.minY);
      int maxY = MathHelper.floor(boundingBox.maxY);
      int minZ = MathHelper.floor(boundingBox.minZ);
      int maxZ = MathHelper.floor(boundingBox.maxZ);

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            for (int z = minZ; z <= maxZ; z++) {
               Block<?> block = Blocks.blocksList[this.getBlockId(x, y, z)];
               if (block != null && block.getMaterial().isLiquid()) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public boolean isBoundingBoxBurning(AABB aabb) {
      return this.isMaterialInBB(aabb, Material.fire, Material.lava);
   }

   public boolean handleMaterialAcceleration(AABB aabb, Material material, Entity entity, boolean addVelocity) {
      int i = MathHelper.floor(aabb.minX);
      int j = MathHelper.floor(aabb.maxX + 1.0);
      int k = MathHelper.floor(aabb.minY);
      int l = MathHelper.floor(aabb.maxY + 1.0);
      int i1 = MathHelper.floor(aabb.minZ);
      int j1 = MathHelper.floor(aabb.maxZ + 1.0);
      if (!this.areBlocksLoaded(i, k, i1, j, l, j1)) {
         return false;
      } else {
         boolean flag = false;
         Vec3 vec3 = Vec3.getTempVec3(0.0, 0.0, 0.0);

         for (int k1 = i; k1 < j; k1++) {
            for (int l1 = k; l1 < l; l1++) {
               for (int i2 = i1; i2 < j1; i2++) {
                  Block<?> block = Blocks.blocksList[this.getBlockId(k1, l1, i2)];
                  if (block != null && this.isMaterialInBB(aabb, material)) {
                     flag = true;
                     block.handleEntityInside(this, k1, l1, i2, entity, vec3);
                  }
               }
            }
         }

         if (vec3.length() > 0.0 && addVelocity) {
            vec3 = vec3.normalize();
            double d = 0.014;
            entity.xd = entity.xd + vec3.x * d;
            entity.yd = entity.yd + vec3.y * d;
            entity.zd = entity.zd + vec3.z * d;
         }

         return flag;
      }
   }

   public boolean isMaterialInBB(AABB aabb, Material... materials) {
      int minX_i = MathHelper.floor(aabb.minX);
      int maxX_i = MathHelper.floor(aabb.maxX + 1.0);
      int minY_i = MathHelper.floor(aabb.minY);
      int maxY_i = MathHelper.floor(aabb.maxY + 1.0);
      int minZ_i = MathHelper.floor(aabb.minZ);
      int maxZ_i = MathHelper.floor(aabb.maxZ + 1.0);

      for (int _x = minX_i; _x < maxX_i; _x++) {
         for (int _y = minY_i; _y < maxY_i; _y++) {
            for (int _z = minZ_i; _z < maxZ_i; _z++) {
               Block<?> block = Blocks.blocksList[this.getBlockId(_x, _y, _z)];
               if (block != null) {
                  boolean isMaterial = false;

                  for (Material m : materials) {
                     if (block.getMaterial() == m) {
                        isMaterial = true;
                        break;
                     }
                  }

                  if (isMaterial && aabb.intersects(block.getBlockBoundsFromState(this, _x, _y, _z).move(_x, _y, _z))) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public boolean isAABBInMaterial(AABB aabb, Material material) {
      int i = MathHelper.floor(aabb.minX);
      int j = MathHelper.floor(aabb.maxX + 1.0);
      int k = MathHelper.floor(aabb.minY);
      int l = MathHelper.floor(aabb.maxY + 1.0);
      int i1 = MathHelper.floor(aabb.minZ);
      int j1 = MathHelper.floor(aabb.maxZ + 1.0);

      for (int k1 = i; k1 < j; k1++) {
         for (int l1 = k; l1 < l; l1++) {
            for (int i2 = i1; i2 < j1; i2++) {
               Block<?> block = Blocks.blocksList[this.getBlockId(k1, l1, i2)];
               if (block != null && block.getMaterial() == material) {
                  int j2 = this.getBlockMetadata(k1, l1, i2);
                  double d = l1 + 1;
                  if (j2 < 8) {
                     d = l1 + 1 - j2 / 8.0;
                  }

                  if (d >= aabb.minY) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   public Explosion createExplosion(Entity entity, double x, double y, double z, float explosionSize) {
      return this.createExplosion(entity, x, y, z, explosionSize, false, false);
   }

   public Explosion createExplosion(Entity entity, double x, double y, double z, float explosionSize, boolean flaming, boolean isCannonBall) {
      Explosion explosion;
      if (!isCannonBall) {
         explosion = new Explosion(this, entity, x, y, z, explosionSize);
      } else {
         explosion = new ExplosionCannonball(this, entity, x, y, z, explosionSize);
      }

      explosion.isFlaming = flaming;
      explosion.explode();
      explosion.addEffects(true);
      return explosion;
   }

   public float getSeenPercent(Vec3 vec3, AABB aabb) {
      double d = 1.0 / ((aabb.maxX - aabb.minX) * 2.0 + 1.0);
      double d1 = 1.0 / ((aabb.maxY - aabb.minY) * 2.0 + 1.0);
      double d2 = 1.0 / ((aabb.maxZ - aabb.minZ) * 2.0 + 1.0);
      int i = 0;
      int j = 0;

      for (float f = 0.0F; f <= 1.0F; f = (float)(f + d)) {
         for (float f1 = 0.0F; f1 <= 1.0F; f1 = (float)(f1 + d1)) {
            for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)(f2 + d2)) {
               double d3 = aabb.minX + (aabb.maxX - aabb.minX) * f;
               double d4 = aabb.minY + (aabb.maxY - aabb.minY) * f1;
               double d5 = aabb.minZ + (aabb.maxZ - aabb.minZ) * f2;
               if (this.checkBlockCollisionBetweenPoints(Vec3.getTempVec3(d3, d4, d5), vec3) == null) {
                  i++;
               }

               j++;
            }
         }
      }

      return (float)i / j;
   }

   public void onBlockHit(Player entityplayer, int x, int y, int z, Side side) {
      x += side.getOffsetX();
      y += side.getOffsetY();
      z += side.getOffsetZ();
      if (this.getBlockId(x, y, z) == Blocks.FIRE.id()) {
         this.playBlockEvent(entityplayer, 1004, x, y, z, 0);
         this.setBlockWithNotify(x, y, z, 0);
      }
   }

   public Entity findSubclassOf(Class<? extends Entity> clazz) {
      return null;
   }

   public String getNumLoadedEntitiesString() {
      return "All: " + this.loadedEntityList.size();
   }

   public String getChunkProviderInfoString() {
      return this.chunkProvider.getInfoString();
   }

   @Override
   public TileEntity getTileEntity(int x, int y, int z) {
      Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
      return chunk != null ? chunk.getTileEntity(x & 15, y, z & 15) : null;
   }

   public void setTileEntity(int x, int y, int z, TileEntity tileEntity) {
      if (!tileEntity.isInvalid()) {
         Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
         if (chunk != null && chunk.setTileEntity(x & 15, y, z & 15, tileEntity) && !this.loadedTileEntityList.contains(tileEntity)) {
            this.loadedTileEntityList.add(tileEntity);
         }
      }
   }

   public void removeBlockTileEntity(int x, int y, int z) {
      TileEntity tileEntity = this.getTileEntity(x, y, z);
      if (tileEntity != null && this.updatingBlockEntities) {
         tileEntity.invalidate();
      } else {
         if (tileEntity != null) {
            this.loadedTileEntityList.remove(tileEntity);
         }

         Chunk chunk = this.getChunkFromChunkCoords(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
         if (chunk != null) {
            chunk.removeTileEntity(tileEntity);
         }
      }
   }

   public void replaceBlockTileEntity(int x, int y, int z, TileEntity tileEntity) {
      this.removeBlockTileEntity(x, y, z);
      this.setTileEntity(x, y, z, tileEntity);
   }

   @Override
   public boolean isBlockOpaqueCube(int x, int y, int z) {
      Block<?> block = Blocks.blocksList[this.getBlockId(x, y, z)];
      return block == null ? false : block.isSolidRender();
   }

   public boolean canPlaceOnSurfaceOfBlock(int x, int y, int z) {
      Block<?> block = Blocks.blocksList[this.getBlockId(x, y, z)];
      return block == null ? false : block.canPlaceOnSurfaceOnCondition(this, x, y, z);
   }

   @Override
   public boolean isBlockNormalCube(int x, int y, int z) {
      Block<?> block = Blocks.blocksList[this.getBlockId(x, y, z)];
      return block == null ? false : block.getMaterial().isSolidBlocking() && block.renderAsNormalBlockOnCondition(this, x, y, z);
   }

   public boolean canPlaceInsideBlock(int x, int y, int z) {
      int id = this.getBlockId(x, y, z);
      return id == 0 || Blocks.getBlock(id).hasTag(BlockTags.PLACE_OVERWRITES);
   }

   public void onUnload() {
      try {
         this.updateEntities();
      } catch (Throwable var5) {
         LOGGER.error("Unhandled exception while unloading all entities!", var5);
         throw var5;
      } finally {
         this.loadedEntityList.clear();
         this.unloadedEntityList.clear();
      }
   }

   public void saveWorldIndirectly(ProgressListener iprogressupdate) {
      this.saveWorld(true, iprogressupdate, true);
   }

   public boolean updatingLighting() {
      if (this.lightingUpdatesCounter >= 50) {
         return false;
      } else {
         this.lightingUpdatesCounter++;

         try {
            for (int i = 499; !this.lightingToUpdate.isEmpty() && i >= 0; i--) {
               this.lightingToUpdate.remove(this.lightingToUpdate.size() - 1).performLightUpdate(this);
               if (i == 0) {
                  return true;
               }
            }

            return false;
         } finally {
            this.lightingUpdatesCounter--;
         }
      }
   }

   public void scheduleLightingUpdate(LightLayer layer, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      if (!this.worldType.hasCeiling() || layer != LightLayer.Sky) {
         lightingUpdatesScheduled++;

         try {
            if (lightingUpdatesScheduled != 50) {
               int centerX = (maxX + minX) / 2;
               int centerZ = (maxZ + minZ) / 2;
               if (this.isBlockLoaded(centerX, 64, centerZ)) {
                  if (!this.getChunkFromBlockCoords(centerX, centerZ).isChunkEmpty()) {
                     int numLightingUpdates = this.lightingToUpdate.size();
                     if (numLightingUpdates > 5) {
                        numLightingUpdates = 5;
                     }

                     for (int i = 0; i < numLightingUpdates; i++) {
                        LightUpdate lightUpdate = this.lightingToUpdate.get(this.lightingToUpdate.size() - i - 1);
                        if (lightUpdate.layer == layer && lightUpdate.expandToContain(minX, minY, minZ, maxX, maxY, maxZ)) {
                           return;
                        }
                     }

                     this.lightingToUpdate.add(new LightUpdate(layer, minX, minY, minZ, maxX, maxY, maxZ));
                     int maxLightingUpdates = 1000000;
                     if (this.lightingToUpdate.size() > maxLightingUpdates) {
                        System.out.println("More than " + maxLightingUpdates + " updates, dropping first " + maxLightingUpdates / 2 + " lighting updates");
                        this.lightingToUpdate.subList(0, maxLightingUpdates / 2).clear();
                     }
                  }
               }
            }
         } finally {
            lightingUpdatesScheduled--;
         }
      }
   }

   public void updateSkyBrightness() {
      int i = this.worldType.getSkyDarken(this, this.getWorldTime(), 1.0F);
      if (i != this.skyDarken) {
         this.skyDarken = i;
      }
   }

   public void allChanged(boolean lightChanged, boolean seasonChanged) {
      for (int i = 0; i < this.listeners.size(); i++) {
         this.listeners.get(i).allChanged(lightChanged, seasonChanged);
      }
   }

   protected void updateSleepingPlayers() {
      if (this.areEnoughPlayersFullyAsleep()) {
         boolean wasInterrupted = false;
         if (this.getSpawnerConfig().canHostileSpawn(this) && this.getPlayersRequiredToSkipNight() <= 1) {
            wasInterrupted = SpawnerMobs.performSleepSpawning(this, this.players);
         }

         if (!wasInterrupted) {
            long timePlusOneDay = this.levelData.getWorldTime() + 24000L;
            this.levelData.setWorldTime(timePlusOneDay - timePlusOneDay % 24000L + this.worldType.getSunriseTick(this) + 1000L);
            this.wakeUpAllPlayers();
         }
      }
   }

   public void tick() {
      Debug.push("misc");
      this.immediatelyUpdatedPositions.clear();
      Debug.change("weather");
      this.weatherManager.tick();
      Debug.change("misc");
      this.updateSleepingPlayers();
      Debug.change("spawning");
      SpawnerMobs.performSpawning(this, this.getSpawnerConfig());
      Debug.change("chunk");
      this.chunkProvider.tick();
      Debug.change("autosave");
      int autosaveTimeInSeconds = Global.accessor.getAutosaveTimer();
      int autosaveTimeInTicks = autosaveTimeInSeconds * 20;
      AUTOSAVE = autosaveTimeInTicks != 0;
      if (AUTOSAVE && this.runtime % autosaveTimeInTicks == 0L) {
         this.saveWorld(false, null, true);
      }

      Debug.change("misc");
      if (this.getGameRuleValue(GameRules.DO_DAY_CYCLE)) {
         this.levelData.setWorldTime(this.levelData.getWorldTime() + 1L);
      }

      this.runtime++;
      this.tickUpdates(false);
      Debug.change("blocks&cavesSounds");
      this.updateBlocksAndPlayCaveSounds();
      Debug.change("season&light");
      this.updateSeasonAndLight();
      if (this.rainbowTicks > 0) {
         this.rainbowTicks--;
      }

      Debug.pop();
   }

   public void updateSeasonAndLight() {
      boolean seasonChanged = false;
      boolean lightChanged = false;
      int currentDayCount = (int)(this.levelData.getWorldTime() / 24000L);
      if (currentDayCount != this.dayCountLastTick) {
         this.dayCountLastTick = currentDayCount;
         if (!this.isClientSide) {
            this.dayCanHaveRainbow = this.rand.nextInt(3) == 0;
         } else {
            this.dayCanHaveRainbow = true;
         }

         seasonChanged = true;
      }

      int currentSkyDarken = this.worldType.getSkyDarken(this, this.getWorldTime(), 1.0F);
      if (currentSkyDarken != this.skyDarken) {
         this.skyDarken = currentSkyDarken;
         lightChanged = true;
      }

      if (seasonChanged || lightChanged) {
         this.allChanged(lightChanged, seasonChanged);
      }
   }

   protected void updateBlocksAndPlayCaveSounds() {
      this.positionsToUpdate.clear();

      for (int i = 0; i < this.players.size(); i++) {
         Player entityplayer = this.players.get(i);
         int playerChunkX = MathHelper.floor(entityplayer.x / 16.0);
         int playerChunkZ = MathHelper.floor(entityplayer.z / 16.0);
         byte radius = 9;

         for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
               this.positionsToUpdate.add(new ChunkCoordinate(x + playerChunkX, z + playerChunkZ));
            }
         }
      }

      if (this.caveSoundCounter > 0) {
         this.caveSoundCounter--;
      }

      for (ChunkCoordinate coordinate : this.positionsToUpdate) {
         int chunkBlockX = coordinate.x * 16;
         int chunkBlockZ = coordinate.z * 16;
         if (this.isChunkLoaded(coordinate.x, coordinate.z)) {
            Chunk chunk = this.getChunkFromChunkCoords(coordinate.x, coordinate.z);
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int randVal = this.updateLCG >> 2;
            int blockX = randVal & 15;
            int blockZ = randVal / 256 & 15;
            if (this.caveSoundCounter == 0) {
               int blockY = randVal / 65536 & 0xFF;
               int id = chunk.getBlockID(blockX, blockY, blockZ);
               blockX += chunkBlockX;
               blockZ += chunkBlockZ;
               if (id == 0
                  && this.getFullBlockLightValue(blockX, blockY, blockZ) <= this.rand.nextInt(8)
                  && this.getSavedLightValue(LightLayer.Sky, blockX, blockY, blockZ) <= 0) {
                  Player closestPlayer = this.getClosestPlayer(blockX + 0.5, blockY + 0.5, blockZ + 0.5, 8.0);
                  if (closestPlayer != null && closestPlayer.distanceToSqr(blockX + 0.5, blockY + 0.5, blockZ + 0.5) > 4.0) {
                     this.playSoundEffect(
                        null,
                        SoundCategory.CAVE_SOUNDS,
                        blockX + 0.5,
                        blockY + 0.5,
                        blockZ + 0.5,
                        "ambient.cave.cave",
                        0.7F,
                        0.8F + this.rand.nextFloat() * 0.2F
                     );
                     this.caveSoundCounter = this.rand.nextInt(12000) + 6000;
                  }
               }
            }

            if (this.getCurrentWeather() != null) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               randVal = this.updateLCG >> 2;
               int weatherBlockX = randVal & 15;
               int weatherBlockZ = randVal / 256 & 15;
               this.getCurrentWeather().doEnvironmentUpdate(this, this.rand, chunkBlockX + weatherBlockX, chunkBlockZ + weatherBlockZ);
            }

            for (int i = 0; i < 80; i++) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int iRandVal = this.updateLCG >> 2;
               int iBlockX = iRandVal & 15;
               int iBlockZ = iRandVal / 256 & 15;
               int iBlockY = iRandVal / 65536 & 0xFF;
               int id = chunk.getBlockID(iBlockX, iBlockY, iBlockZ);
               if (Blocks.shouldTick[id]) {
                  Blocks.blocksList[id].updateTick(this, iBlockX + chunkBlockX, iBlockY, iBlockZ + chunkBlockZ, this.rand);
               }

               i++;
            }
         }
      }
   }

   public boolean tickUpdates(boolean all) {
      int count = this.scheduledTickTreeSet.size();
      if (count != this.scheduledTickSet.size()) {
         throw new IllegalStateException("TickNextTick list out of sync");
      } else {
         if (count > 1000) {
            count = 1000;
         }

         for (int j = 0; j < count; j++) {
            NextTickListEntry entry = this.scheduledTickTreeSet.first();
            if (!all && entry.delay > this.runtime) {
               break;
            }

            this.scheduledTickTreeSet.remove(entry);
            this.scheduledTickSet.remove(entry);
            byte radius = 8;
            if (this.areBlocksLoaded(entry.x - 8, entry.y - 8, entry.z - 8, entry.x + 8, entry.y + 8, entry.z + 8)) {
               int blockId = this.getBlockId(entry.x, entry.y, entry.z);
               if (blockId == entry.blockId && blockId > 0) {
                  Blocks.blocksList[blockId].updateTick(this, entry.x, entry.y, entry.z, this.rand);
               }
            }
         }

         return !this.scheduledTickTreeSet.isEmpty();
      }
   }

   public void randomDisplayUpdates(int x, int y, int z) {
      byte radius = 16;
      Random random = new Random();

      for (int i = 0; i < 1000; i++) {
         int rx = x + this.rand.nextInt(16) - this.rand.nextInt(16);
         int ry = y + this.rand.nextInt(16) - this.rand.nextInt(16);
         int rz = z + this.rand.nextInt(16) - this.rand.nextInt(16);
         Block<?> block = this.getBlock(rx, ry, rz);
         if (block != null) {
            block.animationTick(this, rx, ry, rz, random);
         }
      }
   }

   public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity entity, AABB aabb) {
      this.entityBuffer.clear();
      int minX = MathHelper.floor((aabb.minX - 2.0) / 16.0);
      int maxX = MathHelper.floor((aabb.maxX + 2.0) / 16.0);
      int minZ = MathHelper.floor((aabb.minZ - 2.0) / 16.0);
      int maxZ = MathHelper.floor((aabb.maxZ + 2.0) / 16.0);

      for (int x = minX; x <= maxX; x++) {
         for (int z = minZ; z <= maxZ; z++) {
            if (this.isChunkLoaded(x, z)) {
               this.getChunkFromChunkCoords(x, z).getEntitiesWithin(entity, aabb, this.entityBuffer);
            }
         }
      }

      return this.entityBuffer;
   }

   @NotNull
   public <T extends Entity> List<T> getEntitiesWithinAABB(Class<T> ofClass, AABB aabb) {
      int minX = MathHelper.floor((aabb.minX - 2.0) / 16.0);
      int maxX = MathHelper.floor((aabb.maxX + 2.0) / 16.0);
      int minZ = MathHelper.floor((aabb.minZ - 2.0) / 16.0);
      int maxZ = MathHelper.floor((aabb.maxZ + 2.0) / 16.0);
      List<T> entities = new ArrayList<>();

      for (int x = minX; x <= maxX; x++) {
         for (int z = minZ; z <= maxZ; z++) {
            if (this.isChunkLoaded(x, z)) {
               this.getChunkFromChunkCoords(x, z).getEntitiesWithin(ofClass, aabb, entities);
            }
         }
      }

      return entities;
   }

   public List<Entity> getLoadedEntityList() {
      return this.loadedEntityList;
   }

   public List<TileEntity> getLoadedTileEntityList() {
      return this.loadedTileEntityList;
   }

   public void updateTileEntityChunkAndSendToPlayer(int x, int y, int z, TileEntity tileEntity) {
      if (this.isBlockLoaded(x, y, z)) {
         this.getChunkFromBlockCoords(x, z).setChunkModified();
      }

      for (int l = 0; l < this.listeners.size(); l++) {
         this.listeners.get(l).tileEntityChanged(x, y, z, tileEntity);
      }
   }

   public int countEntities(Class<?> clazz) {
      int i = 0;

      for (Entity entity : this.loadedEntityList) {
         if (clazz.isAssignableFrom(entity.getClass())) {
            i++;
         }
      }

      return i;
   }

   public void addLoadedEntities(List<Entity> list) {
      this.loadedEntityList.addAll(list);

      for (int i = 0; i < list.size(); i++) {
         this.obtainEntitySkin(list.get(i));
      }
   }

   public void unloadEntities(List<Entity> list) {
      this.unloadedEntityList.addAll(list);
   }

   public void dropOldChunks() {
      while (this.chunkProvider.tick()) {
      }
   }

   public boolean canBlockBePlacedAt(int blockId, int x, int y, int z, boolean flag, Side side) {
      if (y >= 0 && y < this.getHeightBlocks()) {
         int j1 = this.getBlockId(x, y, z);
         Block<?> block = Blocks.blocksList[j1];
         Block<?> block1 = Blocks.blocksList[blockId];
         AABB axisalignedbb = block1.getCollisionBoundingBoxFromPool(this, x, y, z);
         if (flag) {
            axisalignedbb = null;
         }

         if (axisalignedbb != null && !this.checkIfAABBIsClear(axisalignedbb)) {
            return false;
         } else {
            if (block != null && block.hasTag(BlockTags.PLACE_OVERWRITES)) {
               block = null;
            }

            return blockId > 0 && block == null && block1.canPlaceBlockOnSide(this, x, y, z, side);
         }
      } else {
         return false;
      }
   }

   @Nullable
   public Path getPathToEntity(Entity entity, Entity entityToTravelTo, float distance) {
      int x1 = MathHelper.floor(entity.x);
      int y2 = MathHelper.floor(entity.y);
      int z1 = MathHelper.floor(entity.z);
      int radius = (int)(distance + 16.0F);
      int xMin = x1 - radius;
      int yMin = y2 - radius;
      int zMin = z1 - radius;
      int xMax = x1 + radius;
      int yMax = y2 + radius;
      int zMax = z1 + radius;
      ChunkCache chunkcache = new ChunkCache(this, xMin, yMin, zMin, xMax, yMax, zMax);
      return new PathFinder(chunkcache).findPath(entity, entityToTravelTo, distance);
   }

   @Nullable
   public Path getEntityPathToXYZ(Entity entity, int x, int y, int z, float distance) {
      int eBlockX = MathHelper.floor(entity.x);
      int eBlockY = MathHelper.floor(entity.y);
      int eBlockZ = MathHelper.floor(entity.z);
      int cacheRadius = (int)(distance + 8.0F);
      int cacheMinX = eBlockX - cacheRadius;
      int cacheMinY = eBlockY - cacheRadius;
      int cacheMinZ = eBlockZ - cacheRadius;
      int cacheMaxX = eBlockX + cacheRadius;
      int cacheMaxY = eBlockY + cacheRadius;
      int cacheMaxZ = eBlockZ + cacheRadius;
      ChunkCache chunkcache = new ChunkCache(this, cacheMinX, cacheMinY, cacheMinZ, cacheMaxX, cacheMaxY, cacheMaxZ);
      return new PathFinder(chunkcache).findPath(entity, x, y, z, distance);
   }

   public boolean getDirectSignal(int x, int y, int z, Side side) {
      Block<?> block = this.getBlock(x, y, z);
      return block == null ? false : block.getDirectSignal(this, x, y, z, side);
   }

   public boolean hasDirectSignal(int x, int y, int z) {
      for (Side side : Side.sides) {
         if (this.getDirectSignal(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), side)) {
            return true;
         }
      }

      return false;
   }

   public boolean getSignal(int x, int y, int z, Side side) {
      Block<?> block = this.getBlock(x, y, z);
      if (this.isBlockNormalCube(x, y, z) && block != Blocks.BLOCK_REDSTONE && block != Blocks.PUMPKIN_REDSTONE) {
         return this.hasDirectSignal(x, y, z);
      } else {
         return block == null ? false : block.getSignal(this, x, y, z, side);
      }
   }

   public boolean hasNeighborSignal(int x, int y, int z) {
      for (Side side : Side.sides) {
         if (this.getSignal(x + side.getOffsetX(), y + side.getOffsetY(), z + side.getOffsetZ(), side)) {
            return true;
         }
      }

      return false;
   }

   public Player getClosestPlayerToEntity(Entity entity, double radius) {
      return this.getClosestPlayer(entity.x, entity.y, entity.z, radius);
   }

   public Player getClosestPlayer(double x, double y, double z, double radius) {
      double closestDistance = Double.POSITIVE_INFINITY;
      Player entityplayer = null;
      if (radius < 0.0) {
         for (Player entityPlayer1 : this.players) {
            double currentDistance = entityPlayer1.distanceToSqr(x, y, z);
            if (currentDistance < closestDistance) {
               closestDistance = currentDistance;
               entityplayer = entityPlayer1;
            }
         }
      } else {
         double rSquared = radius * radius;

         for (Player entityPlayer1x : this.players) {
            double currentDistance = entityPlayer1x.distanceToSqr(x, y, z);
            if (currentDistance < rSquared && currentDistance < closestDistance) {
               closestDistance = currentDistance;
               entityplayer = entityPlayer1x;
            }
         }
      }

      return entityplayer;
   }

   public Player getPlayerEntityByName(String s) {
      for (Player player : this.players) {
         if (s.equals(player.username)) {
            return player;
         }
      }

      return null;
   }

   public Player getPlayerEntityByUUID(@Nullable UUID uuid) {
      for (Player player : this.players) {
         if (player.uuid.equals(uuid)) {
            return player;
         }
      }

      return null;
   }

   public void setChunkData(int x, int y, int z, int width, int height, int length, byte[] data) {
      int minChunkX = Math.floorDiv(x, 16);
      int minChunkZ = Math.floorDiv(z, 16);
      int maxChunkX = Math.floorDiv(x + width - 1, 16);
      int maxChunkZ = Math.floorDiv(z + length - 1, 16);
      int startIndex = 0;
      int minY = y;
      int maxY = y + height;
      if (y < 0) {
         minY = 0;
      }

      if (maxY > this.getHeightBlocks()) {
         maxY = this.getHeightBlocks();
      }

      for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
         int minX = x - chunkX * 16;
         int maxX = x + width - chunkX * 16;
         if (minX < 0) {
            minX = 0;
         }

         if (maxX > 16) {
            maxX = 16;
         }

         for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
            int minZ = z - chunkZ * 16;
            int maxZ = z + length - chunkZ * 16;
            if (minZ < 0) {
               minZ = 0;
            }

            if (maxZ > 16) {
               maxZ = 16;
            }

            startIndex = this.getChunkFromChunkCoords(chunkX, chunkZ).setChunkData(data, minX, minY, minZ, maxX, maxY, maxZ, startIndex);
            this.markBlocksDirty(chunkX * 16 + minX, minY, chunkZ * 16 + minZ, chunkX * 16 + maxX, maxY, chunkZ * 16 + maxZ);
         }
      }
   }

   public byte[] getChunkData(int x, int y, int z, int xSize, int ySize, int zSize) {
      byte[] data = new byte[xSize * ySize * zSize * 8];
      int minChunkX = Math.floorDiv(x, 16);
      int minChunkZ = Math.floorDiv(z, 16);
      int maxChunkX = Math.floorDiv(x + xSize - 1, 16);
      int maxChunkZ = Math.floorDiv(z + zSize - 1, 16);
      int startIndex = 0;
      int minY = y;
      int maxY = y + ySize;
      if (y < 0) {
         minY = 0;
      }

      if (maxY > this.getHeightBlocks()) {
         maxY = this.getHeightBlocks();
      }

      for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
         int minX = x - chunkX * 16;
         int maxX = x + xSize - chunkX * 16;
         if (minX < 0) {
            minX = 0;
         }

         if (maxX > 16) {
            maxX = 16;
         }

         for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
            int minZ = z - chunkZ * 16;
            int maxZ = z + zSize - chunkZ * 16;
            if (minZ < 0) {
               minZ = 0;
            }

            if (maxZ > 16) {
               maxZ = 16;
            }

            startIndex = this.getChunkFromChunkCoords(chunkX, chunkZ).getChunkData(data, minX, minY, minZ, maxX, maxY, maxZ, startIndex);
         }
      }

      return data;
   }

   public void sendQuittingDisconnectingPacket() {
   }

   public void checkSessionLock() {
      this.saveHandler.checkSessionLock();
   }

   public void setWorldTime(long l) {
      this.levelData.setWorldTime(l);
   }

   public void setWorldTimeUpdateTicks(long l) {
      long l1 = l - this.levelData.getWorldTime();

      for (NextTickListEntry nextticklistentry : this.scheduledTickSet) {
         nextticklistentry.delay += l1;
      }

      this.setWorldTime(l);
   }

   public long getRandomSeed() {
      return this.levelData.getRandomSeed();
   }

   public long getWorldTime() {
      return this.levelData.getWorldTime();
   }

   public Difficulty getDifficulty() {
      return this.levelData.getDifficulty();
   }

   public void setDifficulty(Difficulty difficulty, boolean overrideLock) {
      this.levelData.setDifficulty(difficulty, overrideLock);
   }

   public void setDifficulty(int difficulty, boolean overrideLock) {
      this.levelData.setDifficulty(difficulty, overrideLock);
   }

   public ChunkCoordinates getSpawnPoint() {
      return new ChunkCoordinates(this.levelData.getSpawnX(), this.levelData.getSpawnY(), this.levelData.getSpawnZ());
   }

   public void setSpawnPoint(ChunkCoordinates chunkcoordinates) {
      this.levelData.setSpawn(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z);
   }

   public void joinEntityInSurroundings(Entity entity) {
      int i = MathHelper.floor(entity.x / 16.0);
      int j = MathHelper.floor(entity.z / 16.0);
      byte byte0 = 2;

      for (int k = i - byte0; k <= i + byte0; k++) {
         for (int l = j - byte0; l <= j + byte0; l++) {
            this.getChunkFromChunkCoords(k, l);
         }
      }

      if (!this.loadedEntityList.contains(entity)) {
         this.loadedEntityList.add(entity);
      }
   }

   public boolean canMineBlock(Player player, int x, int y, int z) {
      return true;
   }

   public void sendTrackedEntityStatusUpdatePacket(Entity entityId, byte entityStatus) {
   }

   public void sendTrackedEntityStatusUpdatePacket(Entity entityId, byte entityStatus, float attackedAtYaw) {
   }

   public void sendTrackedEntityDataPacket(Entity entity) {
   }

   public void updateEntityList() {
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for (int i = 0; i < this.unloadedEntityList.size(); i++) {
         Entity entity = this.unloadedEntityList.get(i);
         int cX = entity.chunkCoordX;
         int cZ = entity.chunkCoordZ;
         if (entity.addedToChunk && this.isChunkLoaded(cX, cZ)) {
            this.getChunkFromChunkCoords(cX, cZ).removeEntity(entity);
         }
      }

      for (int j = 0; j < this.unloadedEntityList.size(); j++) {
         this.releaseEntitySkin(this.unloadedEntityList.get(j));
      }

      this.unloadedEntityList.clear();

      for (int ix = 0; ix < this.loadedEntityList.size(); ix++) {
         Entity entity = this.loadedEntityList.get(ix);
         if (entity.vehicle != null) {
            if (!entity.vehicle.isRemoved() && entity.vehicle.getPassenger() == entity) {
               continue;
            }

            entity.vehicle.setPassenger(null);
            entity.vehicle = null;
         }

         if (entity.removed) {
            int cX = entity.chunkCoordX;
            int cZ = entity.chunkCoordZ;
            if (entity.addedToChunk && this.isChunkLoaded(cX, cZ)) {
               this.getChunkFromChunkCoords(cX, cZ).removeEntity(entity);
            }

            this.loadedEntityList.remove(ix--);
            this.releaseEntitySkin(entity);
         }
      }
   }

   public IChunkProvider getChunkProvider() {
      return this.chunkProvider;
   }

   public void triggerEvent(int x, int y, int z, int index, int data) {
      int j1 = this.getBlockId(x, y, z);
      if (j1 > 0) {
         Blocks.blocksList[j1].triggerEvent(this, x, y, z, index, data);
      }
   }

   public LevelStorage getSaveHandler() {
      return this.saveHandler;
   }

   public LevelData getLevelData() {
      return this.levelData;
   }

   public void updateEnoughPlayersSleepingFlag(Player player) {
      this.enoughPlayersSleeping = false;
      if (!this.players.isEmpty()) {
         int playersSleeping = 0;
         int req = this.getPlayersRequiredToSkipNight();

         for (Player p : this.players) {
            if (p.isPlayerSleeping()) {
               playersSleeping++;
            }
         }

         if (playersSleeping >= req) {
            this.enoughPlayersSleeping = true;
         }
      }
   }

   public int getPlayersRequiredToSkipNight() {
      return (int)(this.players.size() * (this.sleepPercent / 100.0));
   }

   protected void wakeUpAllPlayers() {
      this.enoughPlayersSleeping = false;

      for (Player entityplayer : this.players) {
         if (entityplayer.isPlayerSleeping()) {
            entityplayer.wakeUpPlayer(false, false);
         }
      }

      if (this.getCurrentWeather() != null && this.getCurrentWeather().spawnRainParticles) {
         this.weatherManager.overrideWeather(this.worldType.getDefaultWeather());
      }
   }

   public boolean areEnoughPlayersFullyAsleep() {
      if (this.enoughPlayersSleeping && !this.isClientSide) {
         int fullyAsleep = 0;

         for (Player player : this.players) {
            if (player.isPlayerFullyAsleep()) {
               fullyAsleep++;
            }
         }

         return fullyAsleep >= this.getPlayersRequiredToSkipNight() && fullyAsleep > 0;
      } else {
         return false;
      }
   }

   public boolean canBlockBeRainedOn(int x, int y, int z) {
      if (this.getCurrentWeather() == null) {
         return false;
      } else if (!this.getCurrentWeather().isPrecipitation) {
         return false;
      } else if (!this.canBlockSeeTheSky(x, y, z)) {
         return false;
      } else if (this.getHeightValue(x, z) > y) {
         return false;
      } else {
         Biome biome = this.getBlockBiome(x, y, z);

         for (int q = 0; q < biome.blockedWeathers.length; q++) {
            if (biome.blockedWeathers[q] == this.getCurrentWeather()) {
               return false;
            }
         }

         return true;
      }
   }

   public void setSavedData(String id, SavedData savedData) {
      this.savedDataStorage.set(id, savedData);
   }

   public SavedData getSavedData(Class<? extends SavedData> saveDataClass, String id) {
      return this.savedDataStorage.load(saveDataClass, id);
   }

   public int getUniqueDataId(String s) {
      return this.savedDataStorage.getFreeMetadataFor(s);
   }

   public void playBlockEvent(int id, int x, int y, int z, int data) {
      this.playBlockEvent(null, id, x, y, z, data);
   }

   public void playBlockEvent(@Nullable Player player, int id, int x, int y, int z, int data) {
      for (LevelListener listener : this.listeners) {
         listener.levelEvent(player, id, x, y, z, data);
      }
   }

   public WorldType getWorldType() {
      return this.worldType;
   }

   public EntityItem dropItem(int x, int y, int z, ItemStack itemStack) {
      float f = 0.7F;
      double x1 = this.rand.nextFloat() * f + (1.0F - f) * 0.5;
      double y1 = this.rand.nextFloat() * f + (1.0F - f) * 0.5;
      double z1 = this.rand.nextFloat() * f + (1.0F - f) * 0.5;
      EntityItem item = new EntityItem(this, x + x1, y + y1, z + z1, itemStack);
      item.pickupDelay = 10;
      this.entityJoinedWorld(item);
      return item;
   }

   public void sendGlobalMessage(String message) {
      for (Player player : this.players) {
         player.sendMessage(message);
      }
   }
}
