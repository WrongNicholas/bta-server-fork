package net.minecraft.core.world.save;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.chunk.ChunkLoaderRegion;
import net.minecraft.core.world.chunk.IChunkLoader;
import org.slf4j.Logger;

public class SaveHandlerServer extends SaveHandlerBase implements PlayerIO {
   private static final Logger LOGGER = LogUtils.getLogger();

   public SaveHandlerServer(ISaveFormat saveFormat, File savesDir, String worldDirName, boolean isMultiplayer) {
      super(saveFormat, savesDir, worldDirName, isMultiplayer);
   }

   @Override
   public IChunkLoader getChunkLoader(Dimension dimension) {
      File dimDir = this.saveFormat.getDimensionRootDir(this.worldDirName, dimension);
      dimDir.mkdirs();
      return new ChunkLoaderRegion(dimDir);
   }

   @Override
   public void saveLevelDataAndPlayerData(LevelData levelData, List<Player> playerList) {
      levelData.setSaveVersion(this.saveFormat.getSaveVersion());
      super.saveLevelDataAndPlayerData(levelData, playerList);
   }

   @Override
   public void save(Player player) {
      try {
         CompoundTag nbttagcompound = new CompoundTag();
         player.saveWithoutId(nbttagcompound);
         nbttagcompound.putInt("Format", 1);
         File temp = new File(this.playersDirectory, "_tmp_.dat");
         File save = new File(this.playersDirectory, player.uuid + ".dat");
         NbtIo.writeCompressed(nbttagcompound, new FileOutputStream(temp));
         if (save.exists()) {
            save.delete();
         }

         temp.renameTo(save);
      } catch (Exception var5) {
         LOGGER.warn("Failed to save player data for " + player.username);
      }
   }

   @Override
   public void load(Player player) {
      CompoundTag nbttagcompound = this.getPlayerData(player.username, player.uuid);
      if (nbttagcompound != null) {
         player.load(nbttagcompound);
      }
   }

   @Override
   public PlayerIO getPlayerFileData() {
      return this;
   }

   private CompoundTag getPlayerData(String username, UUID uuid) {
      try {
         File uuidFile = new File(this.playersDirectory, uuid + ".dat");
         if (uuidFile.exists()) {
            return NbtIo.readCompressed(Files.newInputStream(uuidFile.toPath()));
         }

         File nameFile = new File(this.playersDirectory, username + ".dat");
         if (nameFile.exists()) {
            CompoundTag tag = NbtIo.readCompressed(Files.newInputStream(nameFile.toPath()));
            nameFile.renameTo(uuidFile);
            return tag;
         }
      } catch (Exception var6) {
         LOGGER.warn("Failed to load player data for " + username);
      }

      return null;
   }
}
