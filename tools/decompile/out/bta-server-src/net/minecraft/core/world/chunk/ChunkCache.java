package net.minecraft.core.world.chunk;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.season.SeasonManager;
import org.jetbrains.annotations.Nullable;

public class ChunkCache implements WorldSource {
   private final int chunkX;
   private final int chunkZ;
   private final Chunk[][] chunkArray;
   private final World worldObj;

   public ChunkCache(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      this.worldObj = world;
      this.chunkX = Math.floorDiv(minX, 16);
      this.chunkZ = Math.floorDiv(minZ, 16);
      int maxChunkX = Math.floorDiv(maxX, 16);
      int maxChunkZ = Math.floorDiv(maxZ, 16);
      this.chunkArray = new Chunk[maxChunkX - this.chunkX + 1][maxChunkZ - this.chunkZ + 1];

      for (int x = this.chunkX; x <= maxChunkX; x++) {
         for (int z = this.chunkZ; z <= maxChunkZ; z++) {
            this.chunkArray[x - this.chunkX][z - this.chunkZ] = world.getChunkFromChunkCoords(x, z);
         }
      }
   }

   @Override
   public int getBlockId(int x, int y, int z) {
      if (y < 0) {
         return 0;
      } else if (y >= this.worldObj.getHeightBlocks()) {
         return 0;
      } else {
         int l = Math.floorDiv(x, 16) - this.chunkX;
         int i1 = Math.floorDiv(z, 16) - this.chunkZ;
         if (l >= 0 && l < this.chunkArray.length && i1 >= 0 && i1 < this.chunkArray[l].length) {
            Chunk chunk = this.chunkArray[l][i1];
            return chunk == null ? 0 : chunk.getBlockID(x & 15, y, z & 15);
         } else {
            return 0;
         }
      }
   }

   @Nullable
   @Override
   public Block<?> getBlock(int x, int y, int z) {
      return Blocks.getBlock(this.getBlockId(x, y, z));
   }

   @Override
   public TileEntity getTileEntity(int x, int y, int z) {
      int l = Math.floorDiv(x, 16) - this.chunkX;
      int i1 = Math.floorDiv(z, 16) - this.chunkZ;
      return this.chunkArray[l][i1].getTileEntity(x & 15, y, z & 15);
   }

   public boolean getBlockLitInteriorSurface(int x, int y, int z) {
      if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
         return true;
      } else if (y < 0) {
         return true;
      } else {
         return y >= this.worldObj.getHeightBlocks() ? true : Block.getIsLitInteriorSurface(this.worldObj, x, y, z);
      }
   }

   @Override
   public float getBrightness(int x, int y, int z, int blockLightValue) {
      int i1 = this.getLightValue(x, y, z);
      if (i1 < blockLightValue) {
         i1 = blockLightValue;
      }

      return this.worldObj.worldType.getBrightnessRamp()[i1];
   }

   @Override
   public int getLightmapCoord(int x, int y, int z, int blockLightValue) {
      int skyLight = this.worldObj.getSavedLightValue(LightLayer.Sky, x, y, z);
      int blockLight = Math.max(this.worldObj.getSavedLightValue(LightLayer.Block, x, y, z), blockLightValue);
      if (this.getBlockLitInteriorSurface(x, y, z)) {
         skyLight = Math.max(skyLight, this.worldObj.getSavedLightValue(LightLayer.Sky, x, y + 1, z));
         skyLight = Math.max(skyLight, this.worldObj.getSavedLightValue(LightLayer.Sky, x, y - 1, z));
         skyLight = Math.max(skyLight, this.worldObj.getSavedLightValue(LightLayer.Sky, x + 1, y, z));
         skyLight = Math.max(skyLight, this.worldObj.getSavedLightValue(LightLayer.Sky, x - 1, y, z));
         skyLight = Math.max(skyLight, this.worldObj.getSavedLightValue(LightLayer.Sky, x, y, z + 1));
         skyLight = Math.max(skyLight, this.worldObj.getSavedLightValue(LightLayer.Sky, x, y, z - 1));
         blockLight = Math.max(blockLight, this.worldObj.getSavedLightValue(LightLayer.Block, x, y + 1, z));
         blockLight = Math.max(blockLight, this.worldObj.getSavedLightValue(LightLayer.Block, x, y - 1, z));
         blockLight = Math.max(blockLight, this.worldObj.getSavedLightValue(LightLayer.Block, x + 1, y, z));
         blockLight = Math.max(blockLight, this.worldObj.getSavedLightValue(LightLayer.Block, x - 1, y, z));
         blockLight = Math.max(blockLight, this.worldObj.getSavedLightValue(LightLayer.Block, x, y, z + 1));
         blockLight = Math.max(blockLight, this.worldObj.getSavedLightValue(LightLayer.Block, x, y, z - 1));
      }

      return this.getLightmapCoord(skyLight, blockLight);
   }

   @Override
   public int getLightmapCoord(int skylight, int blocklight) {
      return this.worldObj.getLightmapCoord(skylight, blocklight);
   }

   @Override
   public float getLightBrightness(int x, int y, int z) {
      return this.worldObj.worldType.getBrightnessRamp()[this.getLightValue(x, y, z)];
   }

   public int getLightValue(int x, int y, int z) {
      return this.getLightValueExt(x, y, z, true);
   }

   public int getLightValueExt(int x, int y, int z, boolean first) {
      if (x < -32000000 || z < -32000000 || x >= 32000000 || z > 32000000) {
         return 15;
      } else if (first && this.getBlockLitInteriorSurface(x, y, z)) {
         int k1 = this.getLightValueExt(x, y + 1, z, false);
         int i2 = this.getLightValueExt(x + 1, y, z, false);
         int j2 = this.getLightValueExt(x - 1, y, z, false);
         int k2 = this.getLightValueExt(x, y, z + 1, false);
         int l2 = this.getLightValueExt(x, y, z - 1, false);
         if (i2 > k1) {
            k1 = i2;
         }

         if (j2 > k1) {
            k1 = j2;
         }

         if (k2 > k1) {
            k1 = k2;
         }

         if (l2 > k1) {
            k1 = l2;
         }

         return k1;
      } else if (y < 0) {
         return 0;
      } else if (y >= this.worldObj.getHeightBlocks()) {
         int i1 = 15 - this.worldObj.skyDarken;
         if (i1 < 0) {
            i1 = 0;
         }

         return i1;
      } else {
         int j1 = Math.floorDiv(x, 16) - this.chunkX;
         int l1 = Math.floorDiv(z, 16) - this.chunkZ;
         return this.chunkArray[j1][l1].getRawBrightness(x & 15, y, z & 15, this.worldObj.skyDarken);
      }
   }

   @Override
   public int getBlockMetadata(int x, int y, int z) {
      if (y < 0) {
         return 0;
      } else if (y >= this.worldObj.getHeightBlocks()) {
         return 0;
      } else {
         int chunkX = Math.floorDiv(x, 16) - this.chunkX;
         int chunkZ = Math.floorDiv(z, 16) - this.chunkZ;
         return this.chunkArray[chunkX][chunkZ].getBlockMetadata(x & 15, y, z & 15);
      }
   }

   @Override
   public Material getBlockMaterial(int x, int y, int z) {
      int l = this.getBlockId(x, y, z);
      return l == 0 ? Material.air : Blocks.blocksList[l].getMaterial();
   }

   @Override
   public boolean isBlockOpaqueCube(int x, int y, int z) {
      Block<?> block = Blocks.blocksList[this.getBlockId(x, y, z)];
      return block == null ? false : block.isSolidRender();
   }

   @Override
   public boolean isBlockNormalCube(int x, int y, int z) {
      Block<?> block = Blocks.blocksList[this.getBlockId(x, y, z)];
      return block == null ? false : block.getMaterial().blocksMotion() && block.isCubeShaped();
   }

   @Override
   public double getBlockTemperature(int x, int z) {
      return this.worldObj.getBlockTemperature(x, z);
   }

   @Override
   public double getBlockHumidity(int x, int z) {
      return this.worldObj.getBlockHumidity(x, z);
   }

   @Override
   public SeasonManager getSeasonManager() {
      return this.worldObj.getSeasonManager();
   }

   @Override
   public Biome getBlockBiome(int x, int y, int z) {
      return this.worldObj.getBlockBiome(x, y, z);
   }

   @Override
   public int getSavedLightValue(LightLayer layer, int x, int y, int z) {
      return this.worldObj.getSavedLightValue(layer, x, y, z);
   }

   @Override
   public boolean isRetro() {
      return this.worldObj.isRetro();
   }
}
