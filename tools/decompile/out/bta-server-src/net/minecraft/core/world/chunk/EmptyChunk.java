package net.minecraft.core.world.chunk;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class EmptyChunk extends Chunk {
   public EmptyChunk(World world, int i, int j) {
      super(world, i, j);

      for (ChunkSection section : this.sections) {
         if (section != null) {
            section.init();
         }
      }

      this.neverSave = true;
   }

   @Override
   public boolean isAtLocation(int x, int z) {
      return x == this.xPosition && z == this.zPosition;
   }

   @Override
   public int getHeightValue(int x, int z) {
      return 0;
   }

   @Override
   public void recalcHeightmapOnly() {
   }

   @Override
   public void recalcHeightmap() {
   }

   @Override
   public int getBlockID(int x, int y, int z) {
      return 0;
   }

   @Override
   public boolean setBlockIDWithMetadata(int x, int y, int z, int id, int data) {
      return true;
   }

   @Override
   public boolean setBlockID(int x, int y, int z, int id) {
      return true;
   }

   @Override
   public int getBlockMetadata(int x, int y, int z) {
      return 0;
   }

   @Override
   public void setBlockMetadata(int x, int y, int z, int value) {
   }

   @Override
   public int getBrightness(LightLayer layer, int x, int y, int z) {
      return 0;
   }

   @Override
   public void setBrightness(LightLayer layer, int x, int y, int z, int value) {
   }

   @Override
   public int getRawBrightness(int x, int y, int z, int skySubtract) {
      return 0;
   }

   @Override
   public void addEntity(Entity entity) {
   }

   @Override
   public void removeEntity(Entity entity) {
   }

   @Override
   public void removeEntityAtIndex(Entity entity, int index) {
   }

   @Override
   public boolean canBlockSeeTheSky(int x, int y, int z) {
      return false;
   }

   @Override
   public TileEntity getTileEntity(int x, int y, int z) {
      return null;
   }

   @Override
   public void addTileEntity(TileEntity tileEntity) {
   }

   @Override
   public boolean setTileEntity(int x, int y, int z, TileEntity tileEntity) {
      return false;
   }

   @Override
   public void removeTileEntity(int x, int y, int z) {
   }

   @Override
   public void onLoad() {
   }

   @Override
   public void onUnload() {
   }

   @Override
   public void setChunkModified() {
   }

   @Override
   public void getEntitiesWithin(Entity toExclude, AABB aabb, List<Entity> entities) {
   }

   @Override
   public <T extends Entity> void getEntitiesWithin(Class<T> ofClass, AABB aabb, List<@NotNull T> entities) {
   }

   @Override
   public boolean needsSaving(boolean saveImmediately) {
      return false;
   }

   @Override
   public int setChunkData(byte[] data, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int startIndex) {
      int dx = maxX - minX;
      int dy = maxY - minY;
      int dz = maxZ - minZ;
      int chunkSizeBlocks = dx * dy * dz;
      return chunkSizeBlocks * 2 + chunkSizeBlocks + chunkSizeBlocks / 2 * 2;
   }

   @Override
   public int getChunkData(byte[] data, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int startIndex) {
      int dx = maxX - minX;
      int dy = maxY - minY;
      int dz = maxZ - minZ;
      int chunkSizeBlocks = dx * dy * dz;
      int dataSize = chunkSizeBlocks * 2 + chunkSizeBlocks + chunkSizeBlocks / 2 * 2;
      Arrays.fill(data, startIndex, startIndex + dataSize, (byte)0);
      return dataSize;
   }

   @Override
   public Random getChunkRandom(long xor) {
      return new Random(
         this.world.getRandomSeed()
               + this.xPosition * this.xPosition * 4987142
               + this.xPosition * 5947611
               + this.zPosition * this.zPosition * 4392871L
               + this.zPosition * 389711
            ^ xor
      );
   }

   @Override
   public boolean isChunkEmpty() {
      return true;
   }
}
