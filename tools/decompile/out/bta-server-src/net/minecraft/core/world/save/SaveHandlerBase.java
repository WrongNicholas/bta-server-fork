package net.minecraft.core.world.save;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import net.minecraft.core.MinecraftException;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.chunk.ChunkLoaderLegacy;
import net.minecraft.core.world.chunk.IChunkLoader;
import org.slf4j.Logger;

public abstract class SaveHandlerBase implements LevelStorage {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final String worldDirName;
   protected final File saveDirectory;
   protected final File playersDirectory;
   protected final File dataDirectory;
   protected final long now = System.currentTimeMillis();
   protected final ISaveFormat saveFormat;

   public SaveHandlerBase(ISaveFormat saveFormat, File savesDir, String worldDirName, boolean isMultiplayer) {
      this.worldDirName = worldDirName;
      this.saveFormat = saveFormat;
      this.saveDirectory = new File(savesDir, worldDirName);
      this.saveDirectory.mkdirs();
      this.playersDirectory = new File(this.saveDirectory, "players");
      this.dataDirectory = new File(this.saveDirectory, "data");
      this.dataDirectory.mkdirs();
      if (isMultiplayer) {
         this.playersDirectory.mkdirs();
      }

      this.lockSession();
   }

   private void lockSession() {
      try {
         File file = new File(this.saveDirectory, "session.lock");
         DataOutputStream stream = new DataOutputStream(Files.newOutputStream(file.toPath()));

         try {
            stream.writeLong(this.now);
         } catch (Throwable var6) {
            try {
               stream.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         stream.close();
      } catch (IOException var7) {
         LOGGER.error("Failed to check session lock!", (Throwable)var7);
         throw new RuntimeException("Failed to check session lock, aborting");
      }
   }

   protected File getSaveDirectory() {
      return this.saveDirectory;
   }

   @Override
   public void checkSessionLock() {
      try {
         File file = new File(this.saveDirectory, "session.lock");
         DataInputStream datainputstream = new DataInputStream(Files.newInputStream(file.toPath()));

         try {
            if (datainputstream.readLong() != this.now) {
               throw new MinecraftException("The save is being accessed from another location, aborting");
            }
         } catch (Throwable var6) {
            try {
               datainputstream.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         datainputstream.close();
      } catch (IOException var7) {
         throw new MinecraftException("Failed to check session lock, aborting");
      }
   }

   @Override
   public IChunkLoader getChunkLoader(Dimension dimension) {
      File dimDir = this.saveFormat.getDimensionRootDir(this.worldDirName, dimension);
      dimDir.mkdirs();
      return new ChunkLoaderLegacy(dimDir, true);
   }

   @Override
   public LevelData getLevelData() {
      return this.saveFormat.getLevelData(this.worldDirName);
   }

   @Override
   public CompoundTag getLevelDataRaw() {
      return this.saveFormat.getLevelDataRaw(this.worldDirName);
   }

   @Override
   public DimensionData getDimensionData(int dimensionId) {
      return this.saveFormat.getDimensionData(this.worldDirName, dimensionId);
   }

   @Override
   public CompoundTag getDimensionDataRaw(int dimensionId) {
      return this.saveFormat.getDimensionDataRaw(this.worldDirName, dimensionId);
   }

   @Override
   public void saveLevelDataAndPlayerData(LevelData levelData, List<Player> playerList) {
      CompoundTag dataTag = levelData.getNBTTagCompoundWithPlayer(playerList);
      CompoundTag savedTag = new CompoundTag();
      savedTag.put("Data", dataTag);

      try {
         File file = new File(this.saveDirectory, "level.dat_new");
         File file1 = new File(this.saveDirectory, "level.dat_old");
         File file2 = new File(this.saveDirectory, "level.dat");
         NbtIo.writeCompressed(savedTag, Files.newOutputStream(file.toPath()));
         if (file1.exists()) {
            file1.delete();
         }

         file2.renameTo(file1);
         if (file2.exists()) {
            file2.delete();
         }

         file.renameTo(file2);
         if (file.exists()) {
            file.delete();
         }
      } catch (Exception var8) {
         LOGGER.error("Failed to save level data to disk!", (Throwable)var8);
      }
   }

   @Override
   public void saveLevelData(LevelData levelData) {
      this.saveLevelDataRaw(levelData.getNBTTagCompound());
   }

   @Override
   public void saveLevelDataRaw(CompoundTag levelDataTag) {
      CompoundTag rootTag = new CompoundTag();
      rootTag.put("Data", levelDataTag);

      try {
         File levelDatNew = new File(this.saveDirectory, "level.dat_new");
         File levelDatOld = new File(this.saveDirectory, "level.dat_old");
         File levelDat = new File(this.saveDirectory, "level.dat");
         NbtIo.writeCompressed(rootTag, Files.newOutputStream(levelDatNew.toPath()));
         if (levelDatOld.exists()) {
            levelDatOld.delete();
         }

         levelDat.renameTo(levelDatOld);
         if (levelDat.exists()) {
            levelDat.delete();
         }

         levelDatNew.renameTo(levelDat);
         if (levelDatNew.exists()) {
            levelDatNew.delete();
         }
      } catch (Exception var6) {
         LOGGER.error("Failed to save level data to disk!", (Throwable)var6);
      }
   }

   @Override
   public void saveDimensionData(int dimensionId, DimensionData dimensionData) {
      this.saveDimensionDataRaw(dimensionId, dimensionData.toNBTTag());
   }

   @Override
   public void saveDimensionDataRaw(int dimensionId, CompoundTag dimensionDataTag) {
      CompoundTag rootTag = new CompoundTag();
      rootTag.put("Data", dimensionDataTag);
      File dimensionsDirectory = new File(this.saveDirectory, "dimensions");
      File dimensionDirectory = new File(dimensionsDirectory, Integer.toString(dimensionId));

      try {
         File dimensionDatNew = new File(dimensionDirectory, "dimension.dat_new");
         File dimensionDatOld = new File(dimensionDirectory, "dimension.dat_old");
         File dimensionDat = new File(dimensionDirectory, "dimension.dat");
         if (!dimensionDirectory.exists()) {
            dimensionDirectory.mkdirs();
         }

         NbtIo.writeCompressed(rootTag, Files.newOutputStream(dimensionDatNew.toPath()));
         if (dimensionDatOld.exists()) {
            dimensionDatOld.delete();
         }

         dimensionDat.renameTo(dimensionDatOld);
         if (dimensionDat.exists()) {
            dimensionDat.delete();
         }

         dimensionDatNew.renameTo(dimensionDat);
         if (dimensionDatNew.exists()) {
            dimensionDatNew.delete();
         }
      } catch (Exception var9) {
         LOGGER.error("Failed to save dimension {} data to disk!", dimensionId, var9);
      }
   }

   @Override
   public File getDataFile(String fileName) {
      return new File(this.dataDirectory, fileName + ".dat");
   }

   @Override
   public PlayerIO getPlayerFileData() {
      return null;
   }
}
