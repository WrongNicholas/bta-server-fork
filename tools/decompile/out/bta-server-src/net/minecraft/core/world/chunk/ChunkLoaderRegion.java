package net.minecraft.core.world.chunk;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.UnknownTagException;
import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import net.minecraft.core.world.World;
import net.minecraft.core.world.save.LevelData;
import net.minecraft.core.world.save.mcregion.RegionFileCache;
import org.slf4j.Logger;

public class ChunkLoaderRegion implements IChunkLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final File worldDir;

   public ChunkLoaderRegion(File worldDir) {
      this.worldDir = worldDir;
   }

   @Override
   public Chunk loadChunk(World world, int x, int z) throws IOException {
      DataInputStream regionStream = RegionFileCache.getChunkInputStream(this.worldDir, x, z);
      if (regionStream != null) {
         CompoundTag tag;
         try {
            tag = NbtIo.read(regionStream);
         } catch (ZipException var8) {
            LOGGER.warn("Zipped data for chunk at {}, {} is too corrupted to read! Assuming unrecoverable and resetting!", x, z, var8);
            return null;
         } catch (UnknownTagException var9) {
            LOGGER.warn("NBT data for chunk at {}, {} is too corrupted to read! Assuming unrecoverable and resetting!", x, z, var9);
            return null;
         }

         if (!tag.containsKey("Level")) {
            LOGGER.warn("Chunk file at {},{} is missing level data, skipping", x, z);
            return null;
         } else {
            Chunk chunk = ChunkLoaderLegacy.loadChunkIntoWorldFromCompound(world, tag.getCompound("Level"));
            if (!chunk.isAtLocation(x, z)) {
               LOGGER.warn(
                  "Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", x, z, x, z, chunk.xPosition, chunk.zPosition
               );
               CompoundTag level = tag.getCompound("Level");
               level.putInt("xPos", x);
               level.putInt("zPos", z);
               chunk = ChunkLoaderLegacy.loadChunkIntoWorldFromCompound(world, level);

               assert chunk.isAtLocation(x, z);
            }

            chunk.fixMissingBlocks();
            return chunk;
         }
      } else {
         return null;
      }
   }

   @Override
   public void saveChunk(World world, Chunk chunk) throws IOException {
      world.checkSessionLock();

      try {
         DataOutputStream regionStream = RegionFileCache.getChunkOutputStream(this.worldDir, chunk.xPosition, chunk.zPosition);
         CompoundTag levelTag = new CompoundTag();
         CompoundTag chunkDataTag = new CompoundTag();
         levelTag.put("Level", chunkDataTag);
         ChunkLoaderLegacy.storeChunkInCompound(chunk, world, chunkDataTag);
         NbtIo.write(levelTag, regionStream);
         regionStream.close();
         LevelData levelData = world.getLevelData();
         levelData.setSizeOnDisk(levelData.getSizeOnDisk() + RegionFileCache.getSizeDelta(this.worldDir, chunk.xPosition, chunk.zPosition));
      } catch (Exception var7) {
         LOGGER.error("Exception while attempting to save chunk at X:{}, Z:{}", chunk.xPosition, chunk.zPosition, var7);
      }
   }

   @Override
   public boolean isSaving() {
      return false;
   }
}
