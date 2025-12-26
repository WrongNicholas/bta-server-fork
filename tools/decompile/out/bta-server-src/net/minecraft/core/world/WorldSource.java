package net.minecraft.core.world;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.season.SeasonManager;
import org.jetbrains.annotations.Nullable;

public interface WorldSource {
   int getBlockId(int var1, int var2, int var3);

   @Nullable
   Block<?> getBlock(int var1, int var2, int var3);

   TileEntity getTileEntity(int var1, int var2, int var3);

   @Nullable
   default <T> T getBlockLogic(int x, int y, int z, Class<T> logicClass) {
      Block<?> block = this.getBlock(x, y, z);
      if (block == null) {
         return null;
      } else {
         BlockLogic logic = block.getLogic();
         return logicClass.isAssignableFrom(logic.getClass()) ? logicClass.cast(logic) : null;
      }
   }

   float getBrightness(int var1, int var2, int var3, int var4);

   int getLightmapCoord(int var1, int var2, int var3, int var4);

   int getLightmapCoord(int var1, int var2);

   float getLightBrightness(int var1, int var2, int var3);

   int getBlockMetadata(int var1, int var2, int var3);

   Material getBlockMaterial(int var1, int var2, int var3);

   boolean isBlockOpaqueCube(int var1, int var2, int var3);

   boolean isBlockNormalCube(int var1, int var2, int var3);

   double getBlockTemperature(int var1, int var2);

   double getBlockHumidity(int var1, int var2);

   SeasonManager getSeasonManager();

   Biome getBlockBiome(int var1, int var2, int var3);

   int getSavedLightValue(LightLayer var1, int var2, int var3, int var4);

   boolean isRetro();
}
