package net.minecraft.core.world.save;

import java.io.File;
import java.util.List;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.chunk.ChunkLoaderRegionAsync;
import net.minecraft.core.world.chunk.IChunkLoader;

public class SaveHandlerCore extends SaveHandlerBase {
   public SaveHandlerCore(ISaveFormat saveFormat, File savesDir, String worldDirName, boolean isMultiplayer) {
      super(saveFormat, savesDir, worldDirName, isMultiplayer);
   }

   @Override
   public IChunkLoader getChunkLoader(Dimension dimension) {
      File dimDir = this.saveFormat.getDimensionRootDir(this.worldDirName, dimension);
      dimDir.mkdirs();
      return new ChunkLoaderRegionAsync(dimDir);
   }

   @Override
   public void saveLevelDataAndPlayerData(LevelData levelData, List<Player> playerList) {
      levelData.setSaveVersion(this.saveFormat.getSaveVersion());
      super.saveLevelDataAndPlayerData(levelData, playerList);
   }
}
