package net.minecraft.server.world.chunk.provider;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.world.ProgressListener;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.chunk.EmptyChunk;
import net.minecraft.core.world.chunk.IChunkLoader;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.server.world.WorldServer;
import org.slf4j.Logger;

public class ChunkProviderServer implements IChunkProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Set<Integer> droppedChunksSet = new HashSet<>();
   private final Chunk emptyChunk;
   public ChunkGenerator chunkGenerator;
   private final IChunkLoader chunkLoader;
   public boolean chunkLoadOverride = false;
   private final Map<Integer, Chunk> chunkMap = new HashMap<>();
   private final List<Chunk> chunkList = new ArrayList<>();
   private final WorldServer world;

   public ChunkProviderServer(WorldServer world, IChunkLoader chunkLoader, ChunkGenerator chunkGenerator) {
      this.emptyChunk = new EmptyChunk(world, 0, 0);
      this.world = world;
      this.chunkLoader = chunkLoader;
      this.chunkGenerator = chunkGenerator;
   }

   @Override
   public boolean isChunkLoaded(int chunkX, int chunkZ) {
      return this.chunkMap.containsKey(ChunkCoordinate.toInt(chunkX, chunkZ));
   }

   public void dropChunk(int chunkX, int chunkZ) {
      ChunkCoordinates spawnCoords = this.world.getSpawnPoint();
      int spawnDistanceX = chunkX * 16 + 8 - spawnCoords.x;
      int spawnDistanceZ = chunkZ * 16 + 8 - spawnCoords.z;
      int r = 128;
      if (spawnDistanceX < -r || spawnDistanceX > r || spawnDistanceZ < -r || spawnDistanceZ > r) {
         this.droppedChunksSet.add(ChunkCoordinate.toInt(chunkX, chunkZ));
      }
   }

   @Override
   public Chunk prepareChunk(int chunkX, int chunkZ) {
      int chunkRefId = ChunkCoordinate.toInt(chunkX, chunkZ);
      this.droppedChunksSet.remove(chunkRefId);
      Chunk chunk = this.chunkMap.get(chunkRefId);
      if (chunk == null) {
         chunk = this.loadChunkFromFile(chunkX, chunkZ);
         if (chunk == null) {
            if (this.chunkGenerator != null) {
               chunk = this.chunkGenerator.generate(chunkX, chunkZ);
               chunk.fixMissingBlocks();
            } else {
               chunk = this.emptyChunk;
            }
         }

         this.chunkMap.put(chunkRefId, chunk);
         this.chunkList.add(chunk);
         if (chunk != null) {
            chunk.onLoad();
         }

         if (!chunk.isTerrainPopulated
            && this.isChunkLoaded(chunkX + 1, chunkZ + 1)
            && this.isChunkLoaded(chunkX, chunkZ + 1)
            && this.isChunkLoaded(chunkX + 1, chunkZ)) {
            this.populate(this, chunkX, chunkZ);
         }

         if (this.isChunkLoaded(chunkX - 1, chunkZ)
            && !this.provideChunk(chunkX - 1, chunkZ).isTerrainPopulated
            && this.isChunkLoaded(chunkX - 1, chunkZ + 1)
            && this.isChunkLoaded(chunkX, chunkZ + 1)
            && this.isChunkLoaded(chunkX - 1, chunkZ)) {
            this.populate(this, chunkX - 1, chunkZ);
         }

         if (this.isChunkLoaded(chunkX, chunkZ - 1)
            && !this.provideChunk(chunkX, chunkZ - 1).isTerrainPopulated
            && this.isChunkLoaded(chunkX + 1, chunkZ - 1)
            && this.isChunkLoaded(chunkX, chunkZ - 1)
            && this.isChunkLoaded(chunkX + 1, chunkZ)) {
            this.populate(this, chunkX, chunkZ - 1);
         }

         if (this.isChunkLoaded(chunkX - 1, chunkZ - 1)
            && !this.provideChunk(chunkX - 1, chunkZ - 1).isTerrainPopulated
            && this.isChunkLoaded(chunkX - 1, chunkZ - 1)
            && this.isChunkLoaded(chunkX, chunkZ - 1)
            && this.isChunkLoaded(chunkX - 1, chunkZ)) {
            this.populate(this, chunkX - 1, chunkZ - 1);
         }

         if (this.world.getCurrentWeather() != null) {
            this.world.getCurrentWeather().doChunkLoadEffect(this.world, chunk);
         }
      }

      return chunk;
   }

   @Override
   public void regenerateChunk(int chunkX, int chunkZ) {
      int chunkRefId = ChunkCoordinate.toInt(chunkX, chunkZ);
      this.droppedChunksSet.remove(chunkRefId);
      this.chunkList.remove(this.chunkMap.get(chunkRefId));
      this.chunkMap.remove(chunkRefId);
      if (this.chunkGenerator != null) {
         Chunk chunk = this.chunkGenerator.generate(chunkX, chunkZ);
         chunk.fixMissingBlocks();
         this.chunkMap.put(chunkRefId, chunk);
         if (!chunk.isTerrainPopulated
            && this.isChunkLoaded(chunkX + 1, chunkZ + 1)
            && this.isChunkLoaded(chunkX, chunkZ + 1)
            && this.isChunkLoaded(chunkX + 1, chunkZ)) {
            this.populate(this, chunkX, chunkZ);
         }

         if (this.isChunkLoaded(chunkX - 1, chunkZ)
            && !this.provideChunk(chunkX - 1, chunkZ).isTerrainPopulated
            && this.isChunkLoaded(chunkX - 1, chunkZ + 1)
            && this.isChunkLoaded(chunkX, chunkZ + 1)
            && this.isChunkLoaded(chunkX - 1, chunkZ)) {
            this.populate(this, chunkX - 1, chunkZ);
         }

         if (this.isChunkLoaded(chunkX, chunkZ - 1)
            && !this.provideChunk(chunkX, chunkZ - 1).isTerrainPopulated
            && this.isChunkLoaded(chunkX + 1, chunkZ - 1)
            && this.isChunkLoaded(chunkX, chunkZ - 1)
            && this.isChunkLoaded(chunkX + 1, chunkZ)) {
            this.populate(this, chunkX, chunkZ - 1);
         }

         if (this.isChunkLoaded(chunkX - 1, chunkZ - 1)
            && !this.provideChunk(chunkX - 1, chunkZ - 1).isTerrainPopulated
            && this.isChunkLoaded(chunkX - 1, chunkZ - 1)
            && this.isChunkLoaded(chunkX, chunkZ - 1)
            && this.isChunkLoaded(chunkX - 1, chunkZ)) {
            this.populate(this, chunkX - 1, chunkZ - 1);
         }
      }
   }

   @Override
   public Chunk provideChunk(int chunkX, int chunkZ) {
      Chunk chunk = this.chunkMap.get(ChunkCoordinate.toInt(chunkX, chunkZ));
      if (chunk == null) {
         return !this.world.findingSpawnPoint && !this.chunkLoadOverride ? this.emptyChunk : this.prepareChunk(chunkX, chunkZ);
      } else {
         return chunk;
      }
   }

   private Chunk loadChunkFromFile(int chunkX, int chunkZ) {
      if (this.chunkLoader == null) {
         return null;
      } else {
         try {
            Chunk chunk = this.chunkLoader.loadChunk(this.world, chunkX, chunkZ);
            if (chunk != null) {
               chunk.lastSaveTime = this.world.getWorldTime();
            }

            return chunk;
         } catch (Exception var4) {
            LOGGER.error("Failed to load chunk at X:{}, Z:{} to disk!", chunkX, chunkZ, var4);
            return null;
         }
      }
   }

   private void saveChunkToFile(Chunk chunk) {
      if (this.chunkLoader != null) {
         try {
            chunk.lastSaveTime = this.world.getWorldTime();
            this.chunkLoader.saveChunk(this.world, chunk);
         } catch (IOException var3) {
            LOGGER.error("Failed to save chunk at X:{}, Z:{} to disk!", chunk.xPosition, chunk.zPosition, var3);
         }
      }
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ) {
      Chunk chunk = this.provideChunk(chunkX, chunkZ);
      if (!chunk.isTerrainPopulated) {
         chunk.isTerrainPopulated = true;
         if (this.chunkGenerator != null) {
            this.chunkGenerator.decorate(chunk);
            chunk.setChunkModified();
         }
      }
   }

   @Override
   public boolean saveChunks(boolean saveImmediately, ProgressListener progressUpdate) {
      saveImmediately |= this.world.mcServer.forceSaveAllChunksOnAutosave;
      int chunksSaved = 0;

      for (Chunk chunk : this.chunkList) {
         if (chunk.needsSaving(saveImmediately)) {
            this.saveChunkToFile(chunk);
            chunk.isModified = false;
            if (++chunksSaved == this.world.mcServer.chunksSavedPerAutosave && !saveImmediately) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public boolean tick() {
      if (!this.world.dontSave) {
         for (int i = 0; i < 100; i++) {
            if (!this.droppedChunksSet.isEmpty()) {
               Integer integer = this.droppedChunksSet.iterator().next();
               Chunk chunk = this.chunkMap.get(integer);
               this.saveChunkToFile(chunk);
               chunk.onUnload();
               this.droppedChunksSet.remove(integer);
               this.chunkMap.remove(integer);
               this.chunkList.remove(chunk);
            }
         }
      }

      return false;
   }

   @Override
   public void unloadAllChunks() {
      this.chunkMap.clear();
      this.chunkList.clear();
      this.droppedChunksSet.clear();
      this.chunkGenerator = null;
      System.gc();
   }

   @Override
   public boolean canSave() {
      return !this.world.dontSave;
   }

   @Override
   public String getInfoString() {
      return "";
   }

   @Override
   public void setCurrentChunkOver(int chunkX, int chunkZ) {
   }
}
