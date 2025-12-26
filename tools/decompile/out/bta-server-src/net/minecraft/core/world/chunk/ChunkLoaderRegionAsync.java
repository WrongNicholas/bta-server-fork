package net.minecraft.core.world.chunk;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.core.world.World;
import org.slf4j.Logger;

public class ChunkLoaderRegionAsync implements IChunkLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Queue<Chunk> chunksToSave = new LinkedList<>();
   private static final Queue<World> worldsToSave = new LinkedList<>();
   private static final Queue<ChunkLoaderRegion> chunkLoaders = new LinkedList<>();
   private static final Lock lockChunksToSave = new ReentrantLock();
   private static final Condition notEmpty = lockChunksToSave.newCondition();
   private static final ChunkLoaderRegionAsync.ChunkSaveThread chunkSaveThread = new ChunkLoaderRegionAsync.ChunkSaveThread();
   private final ChunkLoaderRegion chunkLoaderRegion;

   public ChunkLoaderRegionAsync(File worldDir) {
      this.chunkLoaderRegion = new ChunkLoaderRegion(worldDir);
   }

   @Override
   public Chunk loadChunk(World world, int x, int z) throws IOException {
      return this.chunkLoaderRegion.loadChunk(world, x, z);
   }

   @Override
   public void saveChunk(World world, Chunk chunk) throws IOException {
      lockChunksToSave.lock();

      try {
         if (!chunksToSave.contains(chunk)) {
            chunksToSave.add(chunk);
            worldsToSave.add(world);
            chunkLoaders.add(this.chunkLoaderRegion);
            notEmpty.signal();
         }
      } finally {
         lockChunksToSave.unlock();
      }
   }

   @Override
   public boolean isSaving() {
      return !chunksToSave.isEmpty();
   }

   static {
      chunkSaveThread.start();
      System.out.println("Spawned new chunk save thread!");
   }

   private static class ChunkSaveThread extends Thread {
      public volatile boolean shouldStop = false;

      private ChunkSaveThread() {
      }

      @Override
      public void run() {
         while (!this.shouldStop) {
            ChunkLoaderRegionAsync.lockChunksToSave.lock();

            try {
               if (!ChunkLoaderRegionAsync.chunksToSave.isEmpty()) {
                  Chunk chunk = ChunkLoaderRegionAsync.chunksToSave.remove();
                  World world = ChunkLoaderRegionAsync.worldsToSave.remove();
                  ChunkLoaderRegion chunkLoaderRegion = ChunkLoaderRegionAsync.chunkLoaders.remove();
                  chunkLoaderRegion.saveChunk(world, chunk);
               } else {
                  ChunkLoaderRegionAsync.notEmpty.await();
               }
            } catch (InterruptedException | IOException var7) {
               ChunkLoaderRegionAsync.LOGGER.error("Exception while attempting to save chunks in async loader", (Throwable)var7);
            } finally {
               ChunkLoaderRegionAsync.lockChunksToSave.unlock();
            }
         }
      }
   }
}
