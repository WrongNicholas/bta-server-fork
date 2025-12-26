package net.minecraft.core.world.save.legacy;

import java.io.File;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.save.SaveFormatBase;

public class SaveFormatLegacy extends SaveFormatBase {
   public SaveFormatLegacy(File savesDir) {
      super(savesDir);
   }

   @Override
   public String getFormatName() {
      return "Legacy";
   }

   @Override
   public int getSaveVersion() {
      return 0;
   }

   @Override
   public void flushCache() {
   }

   @Override
   public File getDimensionRootDir(String worldDirName, Dimension dimension) {
      File worldDir = new File(this.savesDir, worldDirName);
      return dimension == Dimension.OVERWORLD ? worldDir : new File(worldDir, "DIM-" + dimension.id);
   }
}
