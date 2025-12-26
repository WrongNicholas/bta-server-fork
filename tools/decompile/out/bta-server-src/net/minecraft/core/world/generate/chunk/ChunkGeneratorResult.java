package net.minecraft.core.world.generate.chunk;

import java.util.Arrays;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.chunk.ChunkSection;

public class ChunkGeneratorResult {
   private final short[][] sectionBlocksArray = new short[16][];
   private final int[] numBlocksInSectionArray = new int[16];

   public ChunkGeneratorResult() {
      Arrays.fill(this.numBlocksInSectionArray, 0);
   }

   public void setBlock(int x, int y, int z, int id) {
      if (x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16 && id >= 0) {
         int section = y / 16;
         int cy = y % 16;
         Block<?> newBlock = Blocks.getBlock(id);
         if (this.sectionBlocksArray[section] == null) {
            if (newBlock == null) {
               return;
            }

            this.sectionBlocksArray[section] = new short[4096];
         }

         Block<?> currentBlock = Blocks.getBlock(this.getBlock(x, y, z));
         if (newBlock == null && currentBlock != null) {
            this.numBlocksInSectionArray[section]--;
         } else if (newBlock != null && currentBlock == null) {
            this.numBlocksInSectionArray[section]++;
         }

         this.sectionBlocksArray[section][ChunkSection.makeBlockIndex(x, cy, z)] = (short)id;
      }
   }

   public int getBlock(int x, int y, int z) {
      if (x >= 0 && x < 16 && y >= 0 && y < 256 && z >= 0 && z < 16) {
         int section = y / 16;
         int cy = y % 16;
         return this.sectionBlocksArray[section] == null ? 0 : this.sectionBlocksArray[section][ChunkSection.makeBlockIndex(x, cy, z)];
      } else {
         return 0;
      }
   }

   public short[] getSectionBlocks(int ySection) {
      if (ySection < 0 || ySection >= 16) {
         return null;
      } else {
         return this.numBlocksInSectionArray[ySection] <= 0 ? null : this.sectionBlocksArray[ySection];
      }
   }
}
