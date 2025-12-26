package net.minecraft.core.world.save.mcregion;

import java.io.File;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.save.SaveFormatBase;

public class SaveFormat19132 extends SaveFormatBase {
   public SaveFormat19132(File savesDir) {
      super(savesDir);
   }

   @Override
   public String getFormatName() {
      return "MCRegion";
   }

   @Override
   public int getSaveVersion() {
      return 19132;
   }

   @Override
   public void flushCache() {
      RegionFileCache.flushCache();
   }

   @Override
   public File getDimensionRootDir(String worldDirName, Dimension dimension) {
      File worldDir = new File(this.savesDir, worldDirName);
      return dimension == Dimension.OVERWORLD ? worldDir : new File(worldDir, "DIM-" + dimension.id);
   }
}
