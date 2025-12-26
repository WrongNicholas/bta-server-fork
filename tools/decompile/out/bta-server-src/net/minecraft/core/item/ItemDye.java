package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicSign;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobSheep;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.type.WorldTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemDye extends Item {
   public ItemDye(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
   }

   @Override
   public String getLanguageKey(ItemStack itemstack) {
      return super.getKey() + "." + DyeColor.colorFromItemMeta(itemstack.getMetadata()).colorID;
   }

   @Override
   public boolean onUseItemOnBlock(
      @NotNull ItemStack itemstack,
      @Nullable Player player,
      @NotNull World world,
      int blockX,
      int blockY,
      int blockZ,
      @NotNull Side side,
      double xPlaced,
      double yPlaced
   ) {
      Block<?> block = world.getBlock(blockX, blockY, blockZ);
      if (Block.hasLogicClass(block, BlockLogicSign.class)) {
         TileEntitySign sign = (TileEntitySign)world.getTileEntity(blockX, blockY, blockZ);
         if (DyeColor.WHITE.itemMeta - itemstack.getMetadata() == sign.getColor().id) {
            return false;
         } else {
            sign.setColor(TextFormatting.get(DyeColor.WHITE.itemMeta - itemstack.getMetadata()));
            if (player == null || player.getGamemode().consumeBlocks()) {
               itemstack.stackSize--;
            }

            return true;
         }
      } else {
         if (itemstack.getMetadata() == DyeColor.WHITE.itemMeta) {
            if (Block.hasLogicClass(block, IBonemealable.class)
               && ((IBonemealable)block.getLogic()).onBonemealUsed(itemstack, player, world, blockX, blockY, blockZ, side, xPlaced, yPlaced)) {
               return true;
            }

            if (block == Blocks.DIRT) {
               if (!world.isClientSide && Blocks.lightBlock[world.getBlockId(blockX, blockY + 1, blockZ)] <= 2) {
                  int grass = Blocks.GRASS.id();
                  if (world.dimensionData.getWorldType() == WorldTypes.OVERWORLD_RETRO) {
                     grass = Blocks.GRASS_RETRO.id();
                  }

                  world.setBlockWithNotify(blockX, blockY, blockZ, grass);
                  if (player == null || player.getGamemode().consumeBlocks()) {
                     itemstack.stackSize--;
                  }
               }

               return true;
            }

            if (block == Blocks.DIRT_SCORCHED) {
               if (!world.isClientSide && Blocks.lightBlock[world.getBlockId(blockX, blockY + 1, blockZ)] <= 2) {
                  int grassx = Blocks.GRASS_SCORCHED.id();
                  world.setBlockWithNotify(blockX, blockY, blockZ, grassx);
                  if (player == null || player.getGamemode().consumeBlocks()) {
                     itemstack.stackSize--;
                  }
               }

               return true;
            }

            if (block != null && block.hasTag(BlockTags.GROWS_FLOWERS)) {
               if (!world.isClientSide) {
                  if (player == null || player.getGamemode().consumeBlocks()) {
                     itemstack.stackSize--;
                  }

                  label176:
                  for (int j1 = 0; j1 < 128; j1++) {
                     int k1 = blockX;
                     int l1 = blockY + 1;
                     int i2 = blockZ;

                     for (int j2 = 0; j2 < j1 / 16; j2++) {
                        k1 += itemRand.nextInt(3) - 1;
                        l1 += (itemRand.nextInt(3) - 1) * itemRand.nextInt(3) / 2;
                        i2 += itemRand.nextInt(3) - 1;
                        int id1 = world.getBlockId(k1, l1 - 1, i2);
                        if (Blocks.blocksList[id1] == null || !Blocks.blocksList[id1].hasTag(BlockTags.GROWS_FLOWERS)) {
                           continue label176;
                        }
                     }

                     if (world.getBlockId(k1, l1, i2) == 0) {
                        if (itemRand.nextInt(10) != 0) {
                           if (world.getBlockId(k1, l1 - 1, i2) != Blocks.DIRT_SCORCHED.id()
                              && world.getBlockId(k1, l1 - 1, i2) != Blocks.DIRT_SCORCHED_RICH.id()) {
                              world.setBlockWithNotify(k1, l1, i2, Blocks.TALLGRASS.id());
                           } else {
                              world.setBlockWithNotify(k1, l1, i2, Blocks.SPINIFEX.id());
                           }
                        } else {
                           int r = itemRand.nextInt(12);
                           Biome biome = world.getBlockBiome(k1, l1, i2);
                           if (r < 1) {
                              world.setBlockWithNotify(k1, l1, i2, Blocks.FLOWER_YELLOW.id());
                           } else if (r < 2) {
                              world.setBlockWithNotify(k1, l1, i2, Blocks.FLOWER_RED.id());
                           } else if (r >= 4 || biome != Biomes.OVERWORLD_BIRCH_FOREST && biome != Biomes.OVERWORLD_SEASONAL_FOREST) {
                              if (r >= 6 || biome != Biomes.OVERWORLD_MEADOW && biome != Biomes.OVERWORLD_BOREAL_FOREST && biome != Biomes.OVERWORLD_SHRUBLAND) {
                                 if (r >= 8
                                    || biome != Biomes.OVERWORLD_FOREST
                                       && biome != Biomes.OVERWORLD_SWAMPLAND
                                       && biome != Biomes.OVERWORLD_RAINFOREST
                                       && biome != Biomes.OVERWORLD_GRASSLANDS
                                       && biome != Biomes.OVERWORLD_CAATINGA) {
                                    if (r < 10
                                       && (
                                          biome == Biomes.OVERWORLD_PLAINS
                                             || biome == Biomes.OVERWORLD_GRASSLANDS
                                             || biome == Biomes.OVERWORLD_OUTBACK
                                             || biome == Biomes.OVERWORLD_OUTBACK_GRASSY
                                       )) {
                                       world.setBlockWithNotify(k1, l1, i2, Blocks.FLOWER_ORANGE.id());
                                    }
                                 } else {
                                    world.setBlockWithNotify(k1, l1, i2, Blocks.FLOWER_LIGHT_BLUE.id());
                                 }
                              } else {
                                 world.setBlockWithNotify(k1, l1, i2, Blocks.FLOWER_PURPLE.id());
                              }
                           } else {
                              world.setBlockWithNotify(k1, l1, i2, Blocks.FLOWER_PINK.id());
                           }
                        }
                     }
                  }
               }

               return true;
            }
         }

         return false;
      }
   }

   @Override
   public void onUseByActivator(
      ItemStack itemStack,
      TileEntityActivator activatorBlock,
      World world,
      Random random,
      int blockX,
      int blockY,
      int blockZ,
      double offX,
      double offY,
      double offZ,
      Direction direction
   ) {
      this.onUseItemOnBlock(
         itemStack,
         null,
         world,
         blockX + direction.getOffsetX(),
         blockY + direction.getOffsetY(),
         blockZ + direction.getOffsetZ(),
         direction.getSide(),
         0.5,
         0.5
      );
   }

   @Override
   public boolean useItemOnEntity(ItemStack itemstack, Mob mob, Player player) {
      if (mob instanceof MobSheep) {
         MobSheep mobSheep = (MobSheep)mob;
         DyeColor woolColor = DyeColor.colorFromItemMeta(itemstack.getMetadata());
         if (mobSheep.getFleeceColor() != woolColor && itemstack.consumeItem(player)) {
            mobSheep.setFleeceColor(woolColor);
            return true;
         }
      }

      return false;
   }
}
