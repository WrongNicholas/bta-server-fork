package net.minecraft.core.world.chunk;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.World;

public class DisplayChunk extends EmptyChunk {
   public static int displayBlockId = 0;
   public static int displayBlockMeta = 0;
   public static int displayBlockPosX = 0;
   public static int displayBlockPosY = 0;
   public static int displayBlockPosZ = 0;
   public static TileEntity displayTileEntity = null;

   public DisplayChunk(World world, int x, int z) {
      super(world, x, z);
   }

   @Override
   public int getBlockID(int x, int y, int z) {
      return x == displayBlockPosX && y == displayBlockPosY && z == displayBlockPosZ ? displayBlockId : 0;
   }

   @Override
   public int getBlockMetadata(int x, int y, int z) {
      return x == displayBlockPosX && y == displayBlockPosY && z == displayBlockPosZ ? displayBlockMeta : 0;
   }

   @Override
   public int getBrightness(LightLayer layer, int x, int y, int z) {
      return 15;
   }

   @Override
   public boolean setTileEntity(int x, int y, int z, TileEntity tileEntity) {
      if (x == displayBlockPosX && y == displayBlockPosY && z == displayBlockPosZ) {
         displayTileEntity = tileEntity;
         displayTileEntity.worldObj = this.world;
         displayTileEntity.x = x;
         displayTileEntity.y = y;
         displayTileEntity.z = z;
      }

      return true;
   }

   @Override
   public void removeTileEntity(int x, int y, int z) {
      if (x == displayBlockPosX && y == displayBlockPosY && z == displayBlockPosZ) {
         displayTileEntity = null;
      }
   }

   @Override
   public TileEntity getTileEntity(int x, int y, int z) {
      return x == displayBlockPosX && y == displayBlockPosY && z == displayBlockPosZ ? displayTileEntity : null;
   }

   public static void setDisplayTileEntity(World world, TileEntity entity) {
      displayTileEntity = entity;
      if (displayTileEntity != null) {
         displayTileEntity.worldObj = world;
         displayTileEntity.x = displayBlockPosX;
         displayTileEntity.y = displayBlockPosY;
         displayTileEntity.z = displayBlockPosZ;
      }
   }
}
