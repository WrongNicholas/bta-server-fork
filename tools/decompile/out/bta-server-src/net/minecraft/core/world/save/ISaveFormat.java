package net.minecraft.core.world.save;

import com.mojang.nbt.tags.CompoundTag;
import java.io.File;
import java.util.List;
import net.minecraft.core.world.Dimension;

public interface ISaveFormat {
   String getFormatName();

   int getSaveVersion();

   LevelStorage getSaveHandler(String var1, boolean var2);

   List<SaveFile> getSaveFileList();

   void flushCache();

   LevelData getLevelData(String var1);

   CompoundTag getLevelDataRaw(String var1);

   DimensionData getDimensionData(String var1, int var2);

   CompoundTag getDimensionDataRaw(String var1, int var2);

   void deleteSave(String var1);

   void renameWorld(String var1, String var2);

   File getDimensionRootDir(String var1, Dimension var2);
}
