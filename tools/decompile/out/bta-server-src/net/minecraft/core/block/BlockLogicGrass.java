package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;

public class BlockLogicGrass extends BlockLogic {
   public final Block<?> dirt;

   public BlockLogicGrass(Block<?> block, Block<?> dirt) {
      super(block, Material.grass);
      block.setTicking(true);
      this.dirt = dirt;
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (!world.isClientSide) {
         if (world.getBlockLightValue(x, y + 1, z) < 4 && Blocks.lightBlock[world.getBlockId(x, y + 1, z)] > 2) {
            if (rand.nextInt(4) != 0) {
               return;
            }

            world.setBlockWithNotify(x, y, z, this.dirt.id());
         } else if (world.getBlockLightValue(x, y + 1, z) >= 9) {
            for (int i = 0; i < 4; i++) {
               int x1 = x + rand.nextInt(3) - 1;
               int y1 = y + rand.nextInt(5) - 3;
               int z1 = z + rand.nextInt(3) - 1;
               if (world.getBlockId(x1, y1, z1) == this.dirt.id()
                  && world.getBlockLightValue(x1, y1 + 1, z1) >= 4
                  && Blocks.lightBlock[world.getBlockId(x1, y1 + 1, z1)] <= 2) {
                  world.setBlockWithNotify(x1, y1, z1, this.block.id());
               }
            }

            if (world.getGameRuleValue(GameRules.DO_SEASONAL_GROWTH)
               && world.getBlockId(x, y + 1, z) == 0
               && world.getSeasonManager().getCurrentSeason() != null
               && world.getSeasonManager().getCurrentSeason().growFlowers
               && rand.nextInt(256) == 0) {
               int idToSpawn = 0;
               int r = rand.nextInt(400);
               if (r < 26) {
                  idToSpawn = Blocks.FLOWER_RED.id();
               } else if (r < 41) {
                  idToSpawn = Blocks.FLOWER_YELLOW.id();
               } else if (r < 60) {
                  Biome biome = world.getBlockBiome(x, y + 1, z);
                  if (biome == Biomes.OVERWORLD_BIRCH_FOREST || biome == Biomes.OVERWORLD_SEASONAL_FOREST) {
                     idToSpawn = Blocks.FLOWER_PINK.id();
                  } else if (biome == Biomes.OVERWORLD_MEADOW || biome == Biomes.OVERWORLD_BOREAL_FOREST || biome == Biomes.OVERWORLD_SHRUBLAND) {
                     idToSpawn = Blocks.FLOWER_PURPLE.id();
                  } else if (biome == Biomes.OVERWORLD_FOREST
                     || biome == Biomes.OVERWORLD_SWAMPLAND
                     || biome == Biomes.OVERWORLD_RAINFOREST
                     || biome == Biomes.OVERWORLD_CAATINGA) {
                     idToSpawn = Blocks.FLOWER_LIGHT_BLUE.id();
                  }
               } else if (rand.nextInt(8) == 0) {
                  idToSpawn = Blocks.TALLGRASS_FERN.id();
               } else {
                  idToSpawn = Blocks.TALLGRASS.id();
               }

               world.setBlockWithNotify(x, y + 1, z, idToSpawn);
            }
         }
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case SILK_TOUCH:
         case PICK_BLOCK:
            return new ItemStack[]{new ItemStack(this)};
         default:
            return new ItemStack[]{new ItemStack(this.dirt)};
      }
   }
}
