package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.noise.ImprovedNoise;

public class WorldFeatureMudPatch extends WorldFeature {
   private static final ImprovedNoise noise = new ImprovedNoise(new Random(0L));

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      int chunkCoordX = world.getChunkFromBlockCoords(x, z).xPosition * 16;
      int chunkCoordZ = world.getChunkFromBlockCoords(x, z).zPosition * 16;

      for (int chunkX = chunkCoordX; chunkX < chunkCoordX + 16; chunkX++) {
         for (int chunkZ = chunkCoordZ; chunkZ < chunkCoordZ + 16; chunkZ++) {
            int yPos = world.getHeightValue(chunkX, chunkZ) - 1;
            float offset = (float)noise.getValue(chunkX / 30.0, yPos / 30.0, chunkZ / 30.0) * 0.75F;
            if (offset >= 0.125F) {
               for (int i = 0; i < 5; i++) {
                  if (world.getBlockId(chunkX, yPos - i, chunkZ) == Blocks.GRASS.id() || world.getBlockId(chunkX, yPos - i, chunkZ) == Blocks.DIRT.id()) {
                     world.setBlock(chunkX, yPos - i, chunkZ, Blocks.MUD.id());
                  }
               }
            }
         }
      }

      return true;
   }
}
