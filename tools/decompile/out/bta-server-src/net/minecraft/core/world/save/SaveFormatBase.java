package net.minecraft.core.world.save;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.Dimension;
import org.slf4j.Logger;

public abstract class SaveFormatBase implements ISaveFormat {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final File savesDir;

   public SaveFormatBase(File savesDir) {
      if (!savesDir.exists()) {
         savesDir.mkdirs();
      }

      this.savesDir = savesDir;
   }

   @Override
   public LevelData getLevelData(String worldDirName) {
      CompoundTag tag = this.getLevelDataRaw(worldDirName);
      return tag == null ? null : new LevelData(tag);
   }

   @Override
   public CompoundTag getLevelDataRaw(String worldDirName) {
      File worldDir = new File(this.savesDir, worldDirName);
      if (!worldDir.exists()) {
         return null;
      } else {
         File worldLevelDat = new File(worldDir, "level.dat");
         if (worldLevelDat.exists()) {
            try {
               CompoundTag nbtRoot = NbtIo.readCompressed(Files.newInputStream(worldLevelDat.toPath()));
               return nbtRoot.getCompound("Data");
            } catch (Exception var7) {
               LOGGER.error("Exception loading level dat for world '{}'!", worldDirName, var7);
            }
         }

         worldLevelDat = new File(worldDir, "level.dat_old");
         if (worldLevelDat.exists()) {
            try {
               CompoundTag oldNbtRoot = NbtIo.readCompressed(Files.newInputStream(worldLevelDat.toPath()));
               return oldNbtRoot.getCompound("Data");
            } catch (Exception var6) {
               LOGGER.error("Exception loading level dat old for world '{}'!", worldDirName, var6);
            }
         }

         return null;
      }
   }

   @Override
   public DimensionData getDimensionData(String worldDirName, int dimensionId) {
      CompoundTag tag = this.getDimensionDataRaw(worldDirName, dimensionId);
      return tag == null ? null : new DimensionData(tag);
   }

   @Override
   public CompoundTag getDimensionDataRaw(String worldDirName, int dimensionId) {
      File worldDir = new File(this.savesDir, worldDirName);
      if (!worldDir.exists()) {
         return null;
      } else {
         File dimensionsDir = new File(worldDir, "dimensions");
         File dimensionDir = new File(dimensionsDir, "" + dimensionId);
         File dimensionDat = new File(dimensionDir, "dimension.dat");
         if (dimensionDat.exists()) {
            try {
               CompoundTag nbtRoot = NbtIo.readCompressed(Files.newInputStream(dimensionDat.toPath()));
               return nbtRoot.getCompound("Data");
            } catch (Exception var10) {
               LOGGER.error("Exception loading dimension dat '{}' for world '{}'!", dimensionId, worldDirName, var10);
            }
         }

         dimensionDat = new File(dimensionDir, "dimension.dat_old");
         if (dimensionDat.exists()) {
            try {
               CompoundTag oldNbtRoot = NbtIo.readCompressed(Files.newInputStream(dimensionDat.toPath()));
               return oldNbtRoot.getCompound("Data");
            } catch (Exception var9) {
               LOGGER.error("Exception loading dimension dat old '{}' for world '{}'!", dimensionId, worldDirName, var9);
            }
         }

         return null;
      }
   }

   @Override
   public List<SaveFile> getSaveFileList() {
      ArrayList<SaveFile> saveFileList = new ArrayList<>();
      File[] saveDirFiles = this.savesDir.listFiles();
      if (saveDirFiles != null) {
         for (File file : saveDirFiles) {
            if (file.isDirectory()) {
               String worldDirName = file.getName();
               LevelData levelData = this.getLevelData(worldDirName);
               Map<Integer, DimensionData> dimensionData = new HashMap<>();
               boolean needsConversion = false;
               String worldName;
               long lastTimePlayed;
               long size;
               if (levelData == null) {
                  worldName = I18n.getInstance().translateKey("save.data.missing.data");
                  lastTimePlayed = System.currentTimeMillis();
                  size = -1L;
               } else {
                  lastTimePlayed = levelData.getLastTimePlayed();
                  size = levelData.getSizeOnDisk();
                  needsConversion = levelData.getSaveVersion() != this.getSaveVersion();
                  worldName = levelData.getWorldName();
                  if (worldName == null || MathHelper.stringNullOrLengthZero(worldName)) {
                     worldName = worldDirName;
                  }

                  for (Dimension dimension : Dimension.getDimensionList().values()) {
                     dimensionData.put(dimension.id, this.getDimensionData(worldDirName, dimension.id));
                  }
               }

               saveFileList.add(new SaveFile(worldDirName, worldName, levelData, dimensionData, lastTimePlayed, size, needsConversion));
            }
         }
      }

      return saveFileList;
   }

   @Override
   public void renameWorld(String worldDirName, String newName) {
      File worldDir = new File(this.savesDir, worldDirName);
      if (worldDir.exists()) {
         File worldLevelDat = new File(worldDir, "level.dat");
         if (worldLevelDat.exists()) {
            try {
               CompoundTag nbtRoot = NbtIo.readCompressed(Files.newInputStream(worldLevelDat.toPath()));
               CompoundTag nbtRootData = nbtRoot.getCompound("Data");
               nbtRootData.putString("LevelName", newName);
               NbtIo.writeCompressed(nbtRoot, Files.newOutputStream(worldLevelDat.toPath()));
            } catch (Exception var7) {
               LOGGER.error("Failed to rename world '{}' to '{}'!", worldDirName, newName, var7);
            }
         }
      }
   }

   @Override
   public void deleteSave(String worldDirName) {
      File worldDir = new File(this.savesDir, worldDirName);
      if (worldDir.exists()) {
         File[] worldDirFiles = worldDir.listFiles();
         if (worldDirFiles != null) {
            deleteFilesRecursively(worldDirFiles);
            worldDir.delete();
         }
      }
   }

   @Override
   public LevelStorage getSaveHandler(String worldDirName, boolean isMultiplayer) {
      return new SaveHandlerCore(this, this.savesDir, worldDirName, isMultiplayer);
   }

   protected static void deleteFilesRecursively(File[] dirFiles) {
      for (File file : dirFiles) {
         if (file.isDirectory()) {
            File[] subDirFiles = file.listFiles();
            if (subDirFiles != null) {
               deleteFilesRecursively(subDirFiles);
            }
         }

         file.delete();
      }
   }
}
