package net.minecraft.core.world.chunk.provider;

import net.minecraft.core.world.ProgressListener;
import net.minecraft.core.world.chunk.Chunk;

public interface IChunkProvider {
   boolean isChunkLoaded(int var1, int var2);

   Chunk provideChunk(int var1, int var2);

   Chunk prepareChunk(int var1, int var2);

   void regenerateChunk(int var1, int var2);

   void populate(IChunkProvider var1, int var2, int var3);

   boolean saveChunks(boolean var1, ProgressListener var2);

   boolean tick();

   boolean canSave();

   String getInfoString();

   void unloadAllChunks();

   void setCurrentChunkOver(int var1, int var2);
}
