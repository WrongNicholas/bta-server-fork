package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.BlockLogicChest;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.block.entity.TileEntityMobSpawner;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class WorldFeatureDungeon extends WorldFeature {
   public int blockIdWalls;
   public int blockIdFloor;
   @Nullable
   public String mobOverride;
   public WeightedRandomBag<WeightedRandomLootObject> chestLoot;
   public WeightedRandomBag<String> spawnerMonsters;

   @MethodParametersAnnotation(names = {"blockIdWalls", "blockIdFloor", "mobOverrideDispatcherId"})
   public WorldFeatureDungeon(int blockIdWalls, int blockIdFloor, @Nullable String mobOverride) {
      this.blockIdWalls = blockIdWalls;
      this.blockIdFloor = blockIdFloor;
      this.mobOverride = mobOverride;
      this.chestLoot = new WeightedRandomBag<>();
      this.chestLoot.addEntry(new WeightedRandomLootObject(Items.SADDLE.getDefaultStack()), 100.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(Items.INGOT_IRON.getDefaultStack(), 1, 4), 100.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(Items.FOOD_BREAD.getDefaultStack()), 100.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(Items.WHEAT.getDefaultStack(), 1, 4), 100.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(Items.SULPHUR.getDefaultStack(), 1, 4), 100.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(Items.STRING.getDefaultStack(), 1, 4), 100.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(Items.BUCKET.getDefaultStack()), 100.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(Items.FOOD_APPLE_GOLD.getDefaultStack()), 1.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(Items.DUST_REDSTONE.getDefaultStack(), 1, 4), 50.0);

      for (int i = 0; i < 9; i++) {
         this.chestLoot.addEntry(new WeightedRandomLootObject(new ItemStack(Item.itemsList[Items.RECORD_13.id + i]), 1), 1.0);
      }

      this.chestLoot.addEntry(new WeightedRandomLootObject(Blocks.SAPLING_CACAO.getDefaultStack()), 100.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(Blocks.SPONGE_DRY.getDefaultStack(), 1, 4), 100.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(Items.BONE.getDefaultStack(), 1, 4), 100.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(Items.FOOD_APPLE.getDefaultStack()), 100.0);
      this.chestLoot.addEntry(new WeightedRandomLootObject(null), 239.0);
      this.spawnerMonsters = new WeightedRandomBag<>();
      this.spawnerMonsters.addEntry("Skeleton", 1.0);
      this.spawnerMonsters.addEntry("Zombie", 2.0);
      this.spawnerMonsters.addEntry("Spider", 1.0);
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      byte height = 3;
      int width = random.nextInt(2) + 2;
      int length = random.nextInt(2) + 2;
      int j1 = 0;

      for (int k1 = x - width - 1; k1 <= x + width + 1; k1++) {
         for (int j2 = y - 1; j2 <= y + height + 1; j2++) {
            for (int i3 = z - length - 1; i3 <= z + length + 1; i3++) {
               Material material = world.getBlockMaterial(k1, j2, i3);
               if (j2 == y - 1 && !material.isSolid()) {
                  return false;
               }

               if (j2 == y + height + 1 && !material.isSolid()) {
                  return false;
               }

               if ((k1 == x - width - 1 || k1 == x + width + 1 || i3 == z - length - 1 || i3 == z + length + 1)
                  && j2 == y
                  && world.isAirBlock(k1, j2, i3)
                  && world.isAirBlock(k1, j2 + 1, i3)) {
                  j1++;
               }
            }
         }
      }

      if (j1 >= 1 && j1 <= 5) {
         for (int l1 = x - width - 1; l1 <= x + width + 1; l1++) {
            for (int k2 = y + height; k2 >= y - 1; k2--) {
               for (int j3 = z - length - 1; j3 <= z + length + 1; j3++) {
                  if (l1 != x - width - 1 && k2 != y - 1 && j3 != z - length - 1 && l1 != x + width + 1 && k2 != y + height + 1 && j3 != z + length + 1) {
                     world.setBlockWithNotify(l1, k2, j3, 0);
                  } else if (k2 >= 0 && !world.getBlockMaterial(l1, k2 - 1, j3).isSolid()) {
                     world.setBlockWithNotify(l1, k2, j3, 0);
                  } else if (world.getBlockMaterial(l1, k2, j3).isSolid()) {
                     if (k2 == y - 1 && random.nextInt(4) != 0) {
                        world.setBlockWithNotify(l1, k2, j3, this.blockIdFloor);
                     } else {
                        world.setBlockWithNotify(l1, k2, j3, this.blockIdWalls);
                     }
                  }
               }
            }
         }

         for (int i2 = 0; i2 < 2; i2++) {
            for (int l2 = 0; l2 < 3; l2++) {
               int k3 = x + random.nextInt(width * 2 + 1) - width;
               int i4 = z + random.nextInt(length * 2 + 1) - length;
               if (world.isAirBlock(k3, y, i4)) {
                  int j4 = 0;
                  if (world.getBlockMaterial(k3 - 1, y, i4).isSolid()) {
                     j4++;
                  }

                  if (world.getBlockMaterial(k3 + 1, y, i4).isSolid()) {
                     j4++;
                  }

                  if (world.getBlockMaterial(k3, y, i4 - 1).isSolid()) {
                     j4++;
                  }

                  if (world.getBlockMaterial(k3, y, i4 + 1).isSolid()) {
                     j4++;
                  }

                  if (j4 == 1) {
                     world.setBlockWithNotify(k3, y, i4, Blocks.CHEST_PLANKS_OAK.id());
                     BlockLogicChest.setDefaultDirection(world, k3, y, i4);
                     TileEntityChest tileentitychest = (TileEntityChest)world.getTileEntity(k3, y, i4);

                     for (int k4 = 0; k4 < 8; k4++) {
                        ItemStack itemstack = this.pickCheckLootItem(random);
                        if (itemstack != null) {
                           tileentitychest.setItem(random.nextInt(tileentitychest.getContainerSize()), itemstack);
                        }
                     }
                     break;
                  }
               }
            }
         }

         world.setBlockWithNotify(x, y, z, Blocks.MOBSPAWNER.id());
         TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)world.getTileEntity(x, y, z);
         tileentitymobspawner.setMobId(this.pickMobSpawner(random));
         return true;
      } else {
         return false;
      }
   }

   private ItemStack pickCheckLootItem(Random random) {
      return this.chestLoot.getRandom(random).getItemStack(random);
   }

   private String pickMobSpawner(Random random) {
      return this.mobOverride != null ? this.mobOverride : this.spawnerMonsters.getRandom(random);
   }
}
