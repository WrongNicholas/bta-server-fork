package net.minecraft.core.world.save.conversion;

import com.mojang.nbt.tags.CompoundTag;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.ProgressListener;
import net.minecraft.core.world.save.ISaveConverter;
import net.minecraft.core.world.save.ISaveFormat;
import net.minecraft.core.world.save.SaveFormats;

public abstract class SaveConverterMCRegionBase implements ISaveConverter {
   File savesDir;
   String worldDirName;

   @Override
   public void convertSave(CompoundTag levelData, File savesDir, String worldDirName, ProgressListener progress) {
      this.savesDir = savesDir;
      this.worldDirName = worldDirName;
      List<File> dimensionDirs = new ArrayList<>();
      Map<File, ArrayList<File>> dimensionRegionFiles = new HashMap<>();
      System.out.println("Scanning folders...");
      ISaveFormat fromSaveFormat = SaveFormats.createSaveFormat(this.fromVersion(), savesDir);
      if (fromSaveFormat != null) {
         for (Dimension dim : Dimension.getDimensionList().values()) {
            dimensionDirs.add(fromSaveFormat.getDimensionRootDir(worldDirName, dim));
         }
      }

      for (File dimensionDir : dimensionDirs) {
         dimensionRegionFiles.put(dimensionDir, this.getRegionFiles(dimensionDir));
      }

      int totalConversions = 0;

      for (File dimensionDir : dimensionDirs) {
         totalConversions += dimensionRegionFiles.get(dimensionDir).size();
      }

      System.out.println("Total conversion count is " + totalConversions);
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

   abstract int convertDimensionRegions(Dimension var1, ArrayList<File> var2, int var3, int var4, ProgressListener var5);

   ArrayList<File> getRegionFiles(File dimensionDir) {
      ArrayList<File> regionFiles = new ArrayList<>();
      File[] files = new File(dimensionDir, "region").listFiles();
      if (files != null) {
         regionFiles.addAll(Arrays.asList(files));
      }

      return regionFiles;
   }
}
