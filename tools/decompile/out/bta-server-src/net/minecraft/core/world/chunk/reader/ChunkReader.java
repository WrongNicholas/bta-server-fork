package net.minecraft.core.world.chunk.reader;

import com.mojang.nbt.tags.CompoundTag;
import java.util.Map;
import net.minecraft.core.world.World;
import net.minecraft.core.world.data.ChunkNibbleArray;
import net.minecraft.core.world.data.ChunkUnsignedByteArray;

public abstract class ChunkReader {
   protected final CompoundTag tag;
   protected final World world;

   public ChunkReader(World world, CompoundTag chunkTag) {
      this.world = world;
      this.tag = chunkTag;
   }

   public abstract int getX();

   public abstract int getZ();

   public abstract short[] getBlocks(int var1);

   public abstract ChunkUnsignedByteArray getData(int var1);

   public abstract ChunkNibbleArray getSkyLight(int var1);

   public abstract ChunkNibbleArray getBlockLight(int var1);

   public abstract short[] getHeightMap();

   public abstract int getAverageBlockHeight();

   public abstract boolean getIsTerrainPopulated();

   public abstract double[] getTemperatureMap();

   public abstract double[] getHumidityMap();

   public abstract byte[] getBiomeMap(int var1, Map<Integer, String> var2);

   public abstract Map<Integer, String> getBiomeRegistry();
}
