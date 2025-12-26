package net.minecraft.core.world.save.conversion;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.tags.CompoundTag;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.data.legacy.LegacyWorldTypes;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.ProgressListener;
import net.minecraft.core.world.save.DimensionData;
import net.minecraft.core.world.save.ISaveFormat;
import net.minecraft.core.world.save.SaveFormats;
import org.slf4j.Logger;

public class SaveConverter19133To19134 extends SaveConverterMCRegionBase {
   private static final Logger LOGGER = LogUtils.getLogger();

   @Override
   public int fromVersion() {
      return 19133;
   }

   @Override
   public int toVersion() {
      return 19134;
   }

   @Override
   public void convertSave(CompoundTag levelData, File savesDir, String worldDirName, ProgressListener progress) {
      this.savesDir = savesDir;
      this.worldDirName = worldDirName;
      ISaveFormat fromFormat = SaveFormats.createSaveFormat(this.fromVersion(), savesDir);
      ISaveFormat toFormat = SaveFormats.createSaveFormat(this.toVersion(), savesDir);
      if (fromFormat != null && toFormat != null) {
         for (Dimension dim : Dimension.getDimensionList().values()) {
            File fromDimDir = fromFormat.getDimensionRootDir(worldDirName, dim);
            File toDimDir = toFormat.getDimensionRootDir(worldDirName, dim);
            if (fromDimDir.exists()) {
               this.copyDirectoryRecursively(new File(fromDimDir, "region"), new File(toDimDir, "region"));
               if (fromDimDir.getName().startsWith("DIM-")) {
                  this.deleteDirectoryRecursively(fromDimDir);
                  fromDimDir.delete();
               } else {
                  this.deleteDirectoryRecursively(new File(fromDimDir, "region"));
                  new File(fromDimDir, "region").delete();
               }
            }

            byte[] worldTypes = levelData.getByteArrayOrDefault("WorldTypes", null);
            DimensionData dimensionData;
            if (worldTypes != null) {
               int worldType = worldTypes[dim.id];
               dimensionData = new DimensionData(LegacyWorldTypes.getWorldTypeById(worldType));
            } else {
               dimensionData = new DimensionData(dim.defaultWorldType);
            }

            toFormat.getSaveHandler(worldDirName, false).saveDimensionData(dim.id, dimensionData);
         }

         List<File> dimensionDirs = new ArrayList<>();
         Map<File, ArrayList<File>> dimensionRegionFiles = new HashMap<>();
         LOGGER.info("Scanning folders...");

         for (Dimension dim : Dimension.getDimensionList().values()) {
            dimensionDirs.add(toFormat.getDimensionRootDir(worldDirName, dim));
         }

         for (File dimensionDir : dimensionDirs) {
            dimensionRegionFiles.put(dimensionDir, this.getRegionFiles(dimensionDir));
         }

         int totalConversions = 0;

         for (File dimensionDir : dimensionDirs) {
            totalConversions += dimensionRegionFiles.get(dimensionDir).size();
         }

         LOGGER.info("Total conversion count is {}", totalConversions);
         progress.progressStagePercentage(0);
         int numConversionsDone = 0;
         int dimId = 0;

         for (File dimensionDir : dimensionDirs) {
            numConversionsDone += this.convertDimensionRegions(
               Dimension.getDimensionList().get(dimId), dimensionRegionFiles.get(dimensionDir), numConversionsDone, totalConversions, progress
            );
            dimId++;
         }
      }
   }

   @Override
   int convertDimensionRegions(Dimension dimension, ArrayList<File> regionFiles, int conversionsPerformed, int totalConversions, ProgressListener progress) {
      return 0;
   }

   void copyDirectoryRecursively(File sourceDir, File destDir) {
      destDir.mkdirs();
      File[] dirFiles = sourceDir.listFiles();
      if (dirFiles != null) {
         for (File file : dirFiles) {
            if (file.isDirectory()) {
               this.copyDirectoryRecursively(file, new File(destDir, file.getName()));
            } else {
               try {
                  Files.copy(file.toPath(), new File(destDir, file.getName()).toPath());
               } catch (IOException var9) {
                  LOGGER.error("Failed to copy file {} to {}!", file.toPath(), new File(destDir, file.getName()).toPath(), var9);
               }
            }
         }
      }
   }

   void deleteDirectoryRecursively(File sourceDir) {
      File[] dirFiles = sourceDir.listFiles();
      if (dirFiles != null) {
         for (File file : dirFiles) {
            if (file.isDirectory()) {
               this.deleteDirectoryRecursively(file);
            }

            file.delete();
         }
      }
   }
}
