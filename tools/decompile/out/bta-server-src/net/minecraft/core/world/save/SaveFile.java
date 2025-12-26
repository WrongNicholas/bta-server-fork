package net.minecraft.core.world.save;

import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SaveFile implements Comparable<SaveFile> {
   @NotNull
   private final String fileName;
   @NotNull
   private final String displayName;
   @Nullable
   private final LevelData levelData;
   @NotNull
   private final Map<Integer, DimensionData> dimensionData;
   private final long lastTimePlayed;
   private final long sizeOnDisk;
   private final boolean needsConversion;
   private final boolean corrupted;

   public SaveFile(
      @NotNull String fileName,
      @NotNull String displayName,
      @Nullable LevelData levelData,
      @NotNull Map<Integer, DimensionData> dimensionData,
      long lastTimePlayed,
      long sizeOnDisk,
      boolean needsConversion
   ) {
      this.fileName = Objects.requireNonNull(fileName);
      this.displayName = Objects.requireNonNull(displayName);
      this.lastTimePlayed = lastTimePlayed;
      this.sizeOnDisk = sizeOnDisk;
      this.needsConversion = needsConversion;
      this.levelData = levelData;
      this.dimensionData = Objects.requireNonNull(dimensionData);
      this.corrupted = levelData == null || !needsConversion && dimensionData.get(0) == null;
   }

   public String getFileName() {
      return this.fileName;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   @Nullable
   public LevelData getLevelData() {
      return this.levelData;
   }

   @NotNull
   public Map<Integer, DimensionData> getDimensionData() {
      return this.dimensionData;
   }

   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }

   public boolean getNeedsConversion() {
      return this.needsConversion;
   }

   public boolean isCorrupted() {
      return this.corrupted;
   }

   public long getLastTimePlayed() {
      return this.lastTimePlayed;
   }

   public int getMostRecentSave(SaveFile save) {
      if (this.lastTimePlayed < save.lastTimePlayed) {
         return 1;
      } else {
         return this.lastTimePlayed > save.lastTimePlayed ? -1 : this.fileName.compareTo(save.fileName);
      }
   }

   public int compareTo(@NotNull SaveFile save) {
      return this.getMostRecentSave(save);
   }
}
