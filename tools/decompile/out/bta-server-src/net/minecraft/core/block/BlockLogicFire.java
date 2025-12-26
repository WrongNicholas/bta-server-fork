package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicFire extends BlockLogic {
   public static final int FLAME_INSTANT = 60;
   public static final int FLAME_EASY = 30;
   public static final int FLAME_MEDIUM = 15;
   public static final int FLAME_HARD = 5;
   public static final int BURN_INSTANT = 100;
   public static final int BURN_EASY = 60;
   public static final int BURN_MEDIUM = 20;
   public static final int BURN_HARD = 5;
   public static final int BURN_NEVER = 0;
   private static final int[] flameChance = new int[Blocks.blocksList.length];
   private static final int[] burnChance = new int[Blocks.blocksList.length];

   public BlockLogicFire(Block<?> block) {
      super(block, Material.fire);
      block.setTicking(true);
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return null;
   }

   @Override
   public void initializeBlock() {
      setFlammable(Blocks.PLANKS_OAK, 5, 20);
      setFlammable(Blocks.PLANKS_OAK_PAINTED, 5, 20);
      setFlammable(Blocks.FENCE_PLANKS_OAK, 5, 20);
      setFlammable(Blocks.FENCE_PLANKS_OAK_PAINTED, 5, 20);
      setFlammable(Blocks.SLAB_PLANKS_OAK, 5, 20);
      setFlammable(Blocks.SLAB_PLANKS_PAINTED, 5, 20);
      setFlammable(Blocks.STAIRS_PLANKS_OAK, 5, 20);
      setFlammable(Blocks.LOG_OAK, 5, 5);
      setFlammable(Blocks.LOG_PINE, 5, 5);
      setFlammable(Blocks.LOG_BIRCH, 5, 5);
      setFlammable(Blocks.LOG_CHERRY, 5, 5);
      setFlammable(Blocks.LOG_OAK_MOSSY, 5, 5);
      setFlammable(Blocks.LOG_EUCALYPTUS, 5, 5);
      setFlammable(Blocks.LOG_THORN, 5, 5);
      setFlammable(Blocks.LOG_PALM, 5, 5);
      setFlammable(Blocks.LEAVES_OAK, 30, 60);
      setFlammable(Blocks.LEAVES_PINE, 30, 60);
      setFlammable(Blocks.LEAVES_BIRCH, 30, 60);
      setFlammable(Blocks.LEAVES_CHERRY, 30, 60);
      setFlammable(Blocks.LEAVES_CHERRY_FLOWERING, 30, 60);
      setFlammable(Blocks.LEAVES_OAK_RETRO, 30, 60);
      setFlammable(Blocks.LEAVES_SHRUB, 30, 60);
      setFlammable(Blocks.LEAVES_EUCALYPTUS, 30, 60);
      setFlammable(Blocks.LEAVES_CACAO, 30, 60);
      setFlammable(Blocks.LEAVES_THORN, 30, 60);
      setFlammable(Blocks.LEAVES_PALM, 30, 60);
      setFlammable(Blocks.BOOKSHELF_PLANKS_OAK, 30, 20);
      setFlammable(Blocks.TNT, 15, 100);
      setFlammable(Blocks.WOOL, 30, 60);
      setFlammable(Blocks.MOSS_STONE, 100, 30);
      setFlammable(Blocks.MOSS_BASALT, 100, 30);
      setFlammable(Blocks.MOSS_LIMESTONE, 100, 30);
      setFlammable(Blocks.MOSS_GRANITE, 100, 30);
   }

   public static void setFlammable(Block<?> block, int flameChance, int burnChance) {
      BlockLogicFire.flameChance[block.id()] = flameChance;
      BlockLogicFire.burnChance[block.id()] = burnChance;
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      for (int i = 0; i < 8; i++) {
         world.spawnParticle("largesmoke", x + Math.random(), y + 0.5, z + Math.random(), 0.0, 0.0, 0.0, 0);
      }
   }

   @Override
   public int tickDelay() {
      return 40;
   }

   public void setBurnResult(World world, int x, int y, int z) {
      world.setBlockWithNotify(x, y, z, this.getBurnResultId(world, x, y, z));
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      Block<?> blockUnder = world.getBlock(x, y - 1, z);
      boolean infiniBurn = blockUnder != null && blockUnder.hasTag(BlockTags.INFINITE_BURN);
      if (!this.isValidFireLocation(world, x, y, z)) {
         this.setBurnResult(world, x, y, z);
      }

      if (infiniBurn
         || world.getCurrentWeather() == null
         || !world.getCurrentWeather().isPrecipitation
         || !world.canBlockBeRainedOn(x, y, z)
            && !world.canBlockBeRainedOn(x - 1, y, z)
            && !world.canBlockBeRainedOn(x + 1, y, z)
            && !world.canBlockBeRainedOn(x, y, z - 1)
            && !world.canBlockBeRainedOn(x, y, z + 1)) {
         int meta = world.getBlockMetadata(x, y, z);
         if (meta < 15) {
            world.setBlockMetadata(x, y, z, meta + rand.nextInt(3) / 2);
         }

         world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay());
         if (!infiniBurn && !this.canNeighborCatchFire(world, x, y, z)) {
            if (!world.isBlockNormalCube(x, y - 1, z) || meta > 3) {
               this.setBurnResult(world, x, y, z);
            }
         } else if (!infiniBurn && !canBurn(world, x, y - 1, z) && meta == 15 && rand.nextInt(4) == 0) {
            this.setBurnResult(world, x, y, z);
         } else {
            this.checkBurn(world, x + 1, y, z, 300, rand, meta);
            this.checkBurn(world, x - 1, y, z, 300, rand, meta);
            this.checkBurn(world, x, y - 1, z, 250, rand, meta);
            this.checkBurn(world, x, y + 1, z, 250, rand, meta);
            this.checkBurn(world, x, y, z - 1, 300, rand, meta);
            this.checkBurn(world, x, y, z + 1, 300, rand, meta);

            for (int x1 = x - 1; x1 <= x + 1; x1++) {
               for (int z1 = z - 1; z1 <= z + 1; z1++) {
                  for (int y1 = y - 1; y1 <= y + 4; y1++) {
                     if (x1 != x || y1 != y || z1 != z) {
                        int a = 100;
                        if (y1 > y + 1) {
                           a += (y1 - (y + 1)) * 100;
                        }

                        int b = this.getFireChance(world, x1, y1, z1);
                        if (b > 0) {
                           int c = (b + 40) / (meta + 30);
                           if (c > 0
                              && rand.nextInt(a) <= c
                              && (world.getCurrentWeather() == null || !world.getCurrentWeather().isPrecipitation || !world.canBlockBeRainedOn(x1, y1, z1))
                              && !world.canBlockBeRainedOn(x1 - 1, y1, z)
                              && !world.canBlockBeRainedOn(x1 + 1, y1, z1)
                              && !world.canBlockBeRainedOn(x1, y1, z1 - 1)
                              && !world.canBlockBeRainedOn(x1, y1, z1 + 1)
                              && world.getGameRuleValue(GameRules.DO_FIRE_SPREAD)
                              && this.getBurnResultId(world, x1, y1, z1) == 0) {
                              world.setBlockAndMetadataWithNotify(x1, y1, z1, this.block.id(), Math.min(meta + rand.nextInt(5) / 4, 15));
                           }
                        }
                     }
                  }
               }
            }
         }
      } else {
         this.setBurnResult(world, x, y, z);
      }
   }

   private void checkBurn(World world, int x, int y, int z, int chance, Random random, int meta) {
      if (world.getGameRuleValue(GameRules.DO_FIRE_SPREAD)) {
         if (random.nextInt(chance) < burnChance[world.getBlockId(x, y, z)]) {
            boolean isTNT = world.getBlockId(x, y, z) == Blocks.TNT.id();
            if (random.nextInt(meta + 10) >= 5 || world.canBlockBeRainedOn(x, y, z)) {
               this.setBurnResult(world, x, y, z);
            } else if (this.getBurnResultId(world, x, y, z) == 0) {
               world.setBlockAndMetadataWithNotify(x, y, z, this.block.id(), Math.min(meta + random.nextInt(5) / 4, 15));
            }

            if (isTNT) {
               Blocks.TNT.getLogic().ignite(world, x, y, z, true);
            }
         }
      }
   }

   private boolean canNeighborCatchFire(World world, int x, int y, int z) {
      if (canBurn(world, x + 1, y, z)) {
         return true;
      } else if (canBurn(world, x - 1, y, z)) {
         return true;
      } else if (canBurn(world, x, y - 1, z)) {
         return true;
      } else if (canBurn(world, x, y + 1, z)) {
         return true;
      } else {
         return canBurn(world, x, y, z - 1) ? true : canBurn(world, x, y, z + 1);
      }
   }

   private int getFireChance(World world, int x, int y, int z) {
      int flammability = 0;
      if (!world.isAirBlock(x, y, z)) {
         return 0;
      } else {
         flammability = getFlammability(world, x + 1, y, z, flammability);
         flammability = getFlammability(world, x - 1, y, z, flammability);
         flammability = getFlammability(world, x, y - 1, z, flammability);
         flammability = getFlammability(world, x, y + 1, z, flammability);
         flammability = getFlammability(world, x, y, z - 1, flammability);
         return getFlammability(world, x, y, z + 1, flammability);
      }
   }

   public static boolean canBurn(WorldSource worldSource, int x, int y, int z) {
      return flameChance[worldSource.getBlockId(x, y, z)] > 0;
   }

   public static boolean canBurn(int blockId) {
      return flameChance[blockId] > 0;
   }

   public static int getFlammability(World world, int x, int y, int z, int currentFlameChance) {
      int blockFlameChance = flameChance[world.getBlockId(x, y, z)];
      return Math.max(blockFlameChance, currentFlameChance);
   }

   public boolean isValidFireLocation(World world, int x, int y, int z) {
      return world.isBlockNormalCube(x, y - 1, z) || this.canNeighborCatchFire(world, x, y, z);
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return this.isValidFireLocation(world, x, y, z);
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!world.isBlockNormalCube(x, y - 1, z) && !this.canNeighborCatchFire(world, x, y, z)) {
         world.setBlockWithNotify(x, y, z, this.getBurnResultId(world, x, y, z));
      }
   }

   protected int getBurnResultId(World world, int x, int y, int z) {
      int id = world.getBlockId(x, y, z);
      Block<?> stoneBlock = BlockLogicMoss.getStoneBlock(id);
      return stoneBlock != null ? stoneBlock.id() : 0;
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      if (world.getBlockId(x, y - 1, z) != Blocks.OBSIDIAN.id() || !((BlockLogicPortal) Blocks.PORTAL_NETHER.getLogic()).tryToCreatePortal(world, x, y, z, null)) {
         if (!world.isBlockNormalCube(x, y - 1, z) && !this.canNeighborCatchFire(world, x, y, z)) {
            world.setBlockWithNotify(x, y, z, this.getBurnResultId(world, x, y, z));
         } else {
            world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay());
         }
      }
   }

   public void ignite(World world, int x, int y, int z) {
      boolean hasCreatedFire = false;
      if (!hasCreatedFire) {
         hasCreatedFire = this.tryIgnite(world, x, y + 1, z);
      }

      if (!hasCreatedFire) {
         hasCreatedFire = this.tryIgnite(world, x - 1, y, z);
      }

      if (!hasCreatedFire) {
         hasCreatedFire = this.tryIgnite(world, x + 1, y, z);
      }

      if (!hasCreatedFire) {
         hasCreatedFire = this.tryIgnite(world, x, y, z - 1);
      }

      if (!hasCreatedFire) {
         hasCreatedFire = this.tryIgnite(world, x, y, z + 1);
      }

      if (!hasCreatedFire) {
         hasCreatedFire = this.tryIgnite(world, x, y - 1, z);
      }

      if (!hasCreatedFire) {
         world.setBlock(x, y, z, Blocks.FIRE.id());
      }
   }

   private boolean tryIgnite(World world, int x, int y, int z) {
      int tile = world.getBlockId(x, y, z);
      if (tile == Blocks.FIRE.id()) {
         return true;
      } else if (tile == 0) {
         world.setBlock(x, y, z, Blocks.FIRE.id());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (rand.nextInt(24) == 0) {
         world.playSoundEffect(
            null, SoundCategory.WORLD_SOUNDS, x + 0.5F, y + 0.5F, z + 0.5F, "fire.fire", 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F
         );
      }

      if (!world.isBlockNormalCube(x, y - 1, z) && !canBurn(world, x, y - 1, z)) {
         if (canBurn(world, x - 1, y, z)) {
            for (int i1 = 0; i1 < 2; i1++) {
               double f1 = (double)x + rand.nextFloat() * 0.1F;
               double f7 = (double)y + rand.nextFloat();
               double f13 = (double)z + rand.nextFloat();
               world.spawnParticle("largesmoke", f1, f7, f13, 0.0, 0.0, 0.0, 0);
            }
         }

         if (canBurn(world, x + 1, y, z)) {
            for (int j1 = 0; j1 < 2; j1++) {
               double f2 = (double)(x + 1) - rand.nextFloat() * 0.1F;
               double f8 = (double)y + rand.nextFloat();
               double f14 = (double)z + rand.nextFloat();
               world.spawnParticle("largesmoke", f2, f8, f14, 0.0, 0.0, 0.0, 0);
            }
         }

         if (canBurn(world, x, y, z - 1)) {
            for (int k1 = 0; k1 < 2; k1++) {
               double f3 = (double)x + rand.nextFloat();
               double f9 = (double)y + rand.nextFloat();
               double f15 = (double)z + rand.nextFloat() * 0.1F;
               world.spawnParticle("largesmoke", f3, f9, f15, 0.0, 0.0, 0.0, 0);
            }
         }

         if (canBurn(world, x, y, z + 1)) {
            for (int l1 = 0; l1 < 2; l1++) {
               double f4 = (double)x + rand.nextFloat();
               double f10 = (double)y + rand.nextFloat();
               double f16 = (double)(z + 1) - rand.nextFloat() * 0.1F;
               world.spawnParticle("largesmoke", f4, f10, f16, 0.0, 0.0, 0.0, 0);
            }
         }

         if (canBurn(world, x, y + 1, z)) {
            for (int i2 = 0; i2 < 2; i2++) {
               double f5 = (double)x + rand.nextFloat();
               double f11 = (double)(y + 1) - rand.nextFloat() * 0.1F;
               double f17 = (double)z + rand.nextFloat();
               world.spawnParticle("largesmoke", f5, f11, f17, 0.0, 0.0, 0.0, 0);
            }
         }
      } else {
         for (int l = 0; l < 3; l++) {
            double f = (double)x + rand.nextFloat();
            double f6 = (double)y + rand.nextFloat() * 0.5F + 0.5;
            double f12 = (double)z + rand.nextFloat();
            world.spawnParticle("largesmoke", f, f6, f12, 0.0, 0.0, 0.0, 0);
         }
      }
   }
}
