package net.minecraft.core.world.save;

import com.mojang.nbt.tags.CompoundTag;
import java.io.File;
import java.util.List;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.chunk.IChunkLoader;

public interface LevelStorage {
   LevelData getLevelData();

   CompoundTag getLevelDataRaw();

   void checkSessionLock();

   IChunkLoader getChunkLoader(Dimension var1);

   void saveLevelDataAndPlayerData(LevelData var1, List<Player> var2);

   void saveLevelData(LevelData var1);

   void saveLevelDataRaw(CompoundTag var1);

   DimensionData getDimensionData(int var1);

   CompoundTag getDimensionDataRaw(int var1);

   void saveDimensionData(int var1, DimensionData var2);

   void saveDimensionDataRaw(int var1, CompoundTag var2);

   PlayerIO getPlayerFileData();

   File getDataFile(String var1);
}
