package net.minecraft.core.world.chunk;

import java.io.IOException;
import net.minecraft.core.world.World;

public interface IChunkLoader {
   Chunk loadChunk(World var1, int var2, int var3) throws IOException;

   void saveChunk(World var1, Chunk var2) throws IOException;

   boolean isSaving();
}
