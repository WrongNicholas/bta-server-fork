package net.minecraft.core.world.chunk;

import com.mojang.logging.LogUtils;
import net.minecraft.core.block.Blocks;
import org.slf4j.Logger;

public class MissingBlockFixer {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final short[] blocksArray = new short[Blocks.blocksList.length];

   public static void fixMissingBlocks(short[] blocks) {
      for (int i = 0; i < blocks.length; i++) {
         blocks[i] = blocksArray[blocks[i] & 16383];
      }
   }

   static {
      try {
         for (int i = 0; i < Blocks.blocksList.length; i++) {
            short id = (short)i;
            if (id != 0 && Blocks.blocksList[id & 16383] == null) {
               id = 0;
            }

            blocksArray[i] = id;
         }
      } catch (Exception var2) {
         LOGGER.error("Exception initializing blocksArray!", (Throwable)var2);
      }
   }
}
