package net.minecraft.core.world.save;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import net.minecraft.core.data.gamerule.GameRuleCollection;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.Difficulty;
import net.minecraft.core.world.config.spawning.SpawnerConfig;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class LevelData {
   private static final Logger LOGGER = LogUtils.getLogger();
   private long randomSeed;
   private int spawnX;
   private int spawnY;
   private int spawnZ;
   private long worldTime;
   private long lastTimePlayed;
   private long sizeOnDisk;
   private CompoundTag playerTag;
   private int dimension;
   private int gamemode;
   @NotNull
   private String levelName = "DEFAULT NAME";
   private int saveVersion;
   private boolean cheatsEnabled;
   @NotNull
   private Difficulty difficulty = Difficulty.NORMAL;
   private boolean difficultyLocked;
   private GameRuleCollection gameRules;
   private SpawnerConfig spawnerConfig;

   public LevelData(CompoundTag tag) {
      this.readFromCompoundTag(tag);
   }

   public LevelData(long seed, @NotNull String name) {
      this.randomSeed = seed;
      this.levelName = name;
      this.spawnerConfig = new SpawnerConfig();
      this.gameRules = new GameRuleCollection();
   }

   public LevelData(long seed, @NotNull String name, GameRuleCollection gameRules) {
      this.randomSeed = seed;
      this.levelName = name;
      this.spawnerConfig = new SpawnerConfig();
      this.gameRules = gameRules;
   }

   public LevelData(LevelData levelData) {
      this.randomSeed = levelData.randomSeed;
      this.spawnX = levelData.spawnX;
      this.spawnY = levelData.spawnY;
      this.spawnZ = levelData.spawnZ;
      this.worldTime = levelData.worldTime;
      this.lastTimePlayed = levelData.lastTimePlayed;
      this.sizeOnDisk = levelData.sizeOnDisk;
      this.playerTag = levelData.playerTag;
      this.dimension = levelData.dimension;
      this.levelName = levelData.levelName;
      this.saveVersion = levelData.saveVersion;
      this.cheatsEnabled = levelData.cheatsEnabled;
      this.gamemode = levelData.gamemode;
      this.gameRules = levelData.gameRules;
      this.spawnerConfig = levelData.getSpawnerConfig();
      this.difficulty = levelData.difficulty;
      this.difficultyLocked = levelData.difficultyLocked;
   }

   public LevelData(File worldDir) throws IOException {
      CompoundTag nbtRootData = null;
      if (!worldDir.exists()) {
         throw new IOException();
      } else {
         File worldLevelDat = new File(worldDir, "level.dat");
         if (worldLevelDat.exists()) {
            CompoundTag nbtRoot = NbtIo.readCompressed(Files.newInputStream(worldLevelDat.toPath()));
            nbtRootData = nbtRoot.getCompound("Data");
         } else {
            worldLevelDat = new File(worldDir, "level.dat_old");
            if (worldLevelDat.exists()) {
               CompoundTag oldNbtRoot = NbtIo.readCompressed(Files.newInputStream(worldLevelDat.toPath()));
               nbtRootData = oldNbtRoot.getCompound("Data");
            }
         }

         if (nbtRootData != null) {
            this.readFromCompoundTag(nbtRootData);
            if (this.gameRules == null) {
               this.gameRules = new GameRuleCollection();
            }

            if (this.spawnerConfig == null) {
               this.spawnerConfig = new SpawnerConfig();
            }
         } else {
            throw new IOException();
         }
      }
   }

   public CompoundTag getNBTTagCompound() {
      CompoundTag nbttagcompound = new CompoundTag();
      this.updateTagCompound(nbttagcompound, this.playerTag);
      return nbttagcompound;
   }

   public CompoundTag getNBTTagCompoundWithPlayer(List<Player> list) {
      CompoundTag nbttagcompound = new CompoundTag();
      Player entityplayer = null;
      CompoundTag nbttagcompound1 = null;
      if (!list.isEmpty()) {
         entityplayer = list.get(0);
      }

      if (entityplayer != null) {
         nbttagcompound1 = new CompoundTag();
         entityplayer.saveWithoutId(nbttagcompound1);
      }

      this.updateTagCompound(nbttagcompound, nbttagcompound1);
      return nbttagcompound;
   }

   private void updateTagCompound(CompoundTag levelTag, CompoundTag playerTag) {
      levelTag.putLong("RandomSeed", this.randomSeed);
      levelTag.putInt("SpawnX", this.spawnX);
      levelTag.putInt("SpawnY", this.spawnY);
      levelTag.putInt("SpawnZ", this.spawnZ);
      levelTag.putLong("Time", this.worldTime);
      levelTag.putLong("SizeOnDisk", this.sizeOnDisk);
      levelTag.putLong("LastPlayed", System.currentTimeMillis());
      levelTag.putString("LevelName", this.levelName);
      levelTag.putInt("version", this.saveVersion);
      levelTag.putBoolean("CheatsEnabled", this.cheatsEnabled);
      levelTag.putInt("Difficulty", this.difficulty.id());
      levelTag.putBoolean("DifficultyLock", this.difficultyLocked);
      if (playerTag != null) {
         levelTag.putCompound("Player", playerTag);
      }

      CompoundTag gameRulesTag = new CompoundTag();
      GameRuleCollection.writeToNBT(gameRulesTag, this.gameRules);
      levelTag.put("GameRules", gameRulesTag);
      if (this.spawnerConfig != null) {
         CompoundTag spawnerConfigTag = new CompoundTag();
         this.spawnerConfig.writeToTag(spawnerConfigTag);
         levelTag.put("SpawnConfig", spawnerConfigTag);
      }
   }

   private void readFromCompoundTag(CompoundTag tag) {
      this.randomSeed = tag.getLong("RandomSeed");
      this.spawnX = tag.getInteger("SpawnX");
      this.spawnY = tag.getInteger("SpawnY");
      this.spawnZ = tag.getInteger("SpawnZ");
      this.worldTime = tag.getLong("Time");
      this.lastTimePlayed = tag.getLong("LastPlayed");
      this.sizeOnDisk = tag.getLong("SizeOnDisk");
      this.levelName = tag.getString("LevelName");
      this.saveVersion = tag.getInteger("version");
      this.cheatsEnabled = tag.getBoolean("CheatsEnabled");
      this.setDifficulty(tag.getIntegerOrDefault("Difficulty", Difficulty.NORMAL.id()), true);
      this.difficultyLocked = tag.getBooleanOrDefault("DifficultyLock", false);
      if (tag.containsKey("Player")) {
         this.playerTag = tag.getCompound("Player");
         this.dimension = this.playerTag.getInteger("Dimension");
         this.gamemode = this.playerTag.getInteger("Gamemode");
      }

      if (tag.containsKey("GameRules")) {
         CompoundTag gameRulesTag = tag.getCompound("GameRules");
         this.gameRules = GameRuleCollection.readFromNBT(gameRulesTag);
      } else {
         this.gameRules = new GameRuleCollection();
      }

      if (tag.containsKey("SpawnConfig")) {
         CompoundTag spawnConfigTag = tag.getCompound("SpawnConfig");
         this.spawnerConfig = SpawnerConfig.createFromTag(spawnConfigTag);
      } else {
         this.spawnerConfig = new SpawnerConfig();
      }
   }

   public long getRandomSeed() {
      return this.randomSeed;
   }

   public int getSpawnX() {
      return this.spawnX;
   }

   public int getSpawnY() {
      return this.spawnY;
   }

   public int getSpawnZ() {
      return this.spawnZ;
   }

   public long getWorldTime() {
      return this.worldTime;
   }

   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }

   public CompoundTag getPlayerNBTTagCompound() {
      return this.playerTag;
   }

   public int getDimension() {
      return this.dimension;
   }

   public void setSpawnX(int x) {
      this.spawnX = x;
   }

   public void setSpawnY(int y) {
      this.spawnY = y;
   }

   public void setSpawnZ(int z) {
      this.spawnZ = z;
   }

   public void setWorldTime(long time) {
      this.worldTime = time;
   }

   public void setSizeOnDisk(long size) {
      this.sizeOnDisk = size;
   }

   public void setPlayerNBTTagCompound(CompoundTag nbttagcompound) {
      this.playerTag = nbttagcompound;
   }

   public void setSpawn(int x, int y, int z) {
      this.spawnX = x;
      this.spawnY = y;
      this.spawnZ = z;
   }

   public String getWorldName() {
      return this.levelName;
   }

   public void setWorldName(String s) {
      this.levelName = s;
   }

   public int getSaveVersion() {
      return this.saveVersion;
   }

   public void setSaveVersion(int i) {
      this.saveVersion = i;
   }

   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public int getGamemode() {
      return this.gamemode;
   }

   public void setGamemode(int gamemode) {
      this.gamemode = gamemode;
   }

   public GameRuleCollection getGameRules() {
      return this.gameRules;
   }

   public SpawnerConfig getSpawnerConfig() {
      return this.spawnerConfig;
   }

   public boolean getCheatsEnabled() {
      return this.cheatsEnabled;
   }

   public void setCheatsEnabled(boolean enabled) {
      this.cheatsEnabled = enabled;
   }

   public void setDifficulty(Difficulty difficulty, boolean overrideLock) {
      if (overrideLock || !this.difficultyLocked) {
         this.difficulty = difficulty;
      }
   }

   public void setDifficulty(int difficulty, boolean overrideLock) {
      if (overrideLock || !this.difficultyLocked) {
         if (difficulty < 0) {
            LOGGER.warn("Attempted to set difficulty to '{}' which is less then 0!", difficulty);
            difficulty = 0;
         }

         if (difficulty >= Difficulty.values().length) {
            LOGGER.error("Attempted to set difficulty to '{}' which is more then the '{}' difficulties!", difficulty, Difficulty.values().length);
            difficulty = Difficulty.values().length - 1;
         }

         this.setDifficulty(Difficulty.values()[difficulty], overrideLock);
      }
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }

   public void setDifficultyLocked() {
      this.difficultyLocked = true;
   }

   public boolean getDifficultyLocked() {
      return this.difficultyLocked;
   }
}
