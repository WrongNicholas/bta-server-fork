package net.minecraft.core.world.save.mcregion;

import java.io.File;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.save.SaveFormatBase;

public class SaveFormat19134 extends SaveFormatBase {
   public SaveFormat19134(File savesDir) {
      super(savesDir);
   }

   @Override
   public String getFormatName() {
      return "BTARegion";
   }

   @Override
   public int getSaveVersion() {
      return 19134;
   }

   @Override
   public void flushCache() {
      RegionFileCache.flushCache();
   }

   @Override
   public File getDimensionRootDir(String worldDirName, Dimension dimension) {
      File worldDir = new File(this.savesDir, worldDirName);
      File worldDimsDir = new File(worldDir, "dimensions");
      return new File(worldDimsDir, Integer.toString(dimension.id));
   }
}
