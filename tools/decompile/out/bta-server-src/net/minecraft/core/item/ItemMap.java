package net.minecraft.core.item;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.MaterialColor;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketMapData;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.saveddata.maps.ItemMapSavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemMap extends Item implements IComplexItem {
   private static final Map<Integer, Map<Integer, Integer>> blockCounts = new HashMap<>();

   protected ItemMap(String name, String namespaceId, int id) {
      super(name, namespaceId, id);
   }

   @NotNull
   public static ItemMapSavedData getOrCreateSavedData(short meta, byte scale, World world) {
      String s = String.format("map_%s_scale_%s", meta, scale);
      ItemMapSavedData mapData = (ItemMapSavedData)world.getSavedData(ItemMapSavedData.class, s);
      if (mapData == null) {
         if (scale == 3) {
            mapData = (ItemMapSavedData)world.getSavedData(ItemMapSavedData.class, String.format("map_%s", meta));
            if (mapData != null) {
               for (byte i = 0; i <= 4; i++) {
                  String _s = String.format("map_%s_scale_%s", meta, i);
                  if (i == 3) {
                     world.setSavedData(_s, mapData);
                  } else {
                     ItemMapSavedData d = new ItemMapSavedData(_s);
                     d.x = mapData.x;
                     d.z = mapData.z;
                     d.scale = i;
                     d.dimension = mapData.dimension;
                     d.setDirty();
                     world.setSavedData(_s, d);
                  }
               }

               return mapData;
            }
         }

         mapData = new ItemMapSavedData(s);
         mapData.x = world.getLevelData().getSpawnX();
         mapData.z = world.getLevelData().getSpawnZ();
         mapData.scale = scale;
         mapData.dimension = (byte)world.dimension.id;
         mapData.setDirty();
         world.setSavedData(s, mapData);
      }

      return mapData;
   }

   public static boolean hasInitialized(ItemStack stack) {
      return stack.getData().getBooleanOrDefault("initialized", false);
   }

   @Nullable
   public ItemMapSavedData getOrCreateSavedData(ItemStack stack, World world) {
      if (!hasInitialized(stack)) {
         return null;
      } else {
         byte scale = this.getScale(stack);
         int meta = stack.getMetadata();
         String s = String.format("map_%s_scale_%s", meta, scale);
         ItemMapSavedData mapData = (ItemMapSavedData)world.getSavedData(ItemMapSavedData.class, s);
         if (mapData == null) {
            if (scale == 3) {
               mapData = (ItemMapSavedData)world.getSavedData(ItemMapSavedData.class, String.format("map_%s", meta));
               if (mapData != null) {
                  for (byte i = 0; i <= 4; i++) {
                     String _s = String.format("map_%s_scale_%s", meta, i);
                     if (i == 3) {
                        world.setSavedData(_s, mapData);
                     } else {
                        ItemMapSavedData d = new ItemMapSavedData(_s);
                        d.x = mapData.x;
                        d.z = mapData.z;
                        d.scale = i;
                        d.dimension = mapData.dimension;
                        d.setDirty();
                        world.setSavedData(_s, d);
                     }
                  }

                  return mapData;
               }
            }

            mapData = new ItemMapSavedData(s);
            mapData.x = world.getLevelData().getSpawnX();
            mapData.z = world.getLevelData().getSpawnZ();
            mapData.scale = scale;
            mapData.dimension = (byte)world.dimension.id;
            mapData.setDirty();
            world.setSavedData(s, mapData);
         }

         return mapData;
      }
   }

   @Override
   public ItemStack onUseItem(ItemStack itemstack, World world, Player entityplayer) {
      if (!hasInitialized(itemstack)) {
         this.initMap(itemstack, world, entityplayer);
      } else {
         this.setScale(itemstack, (byte)(this.getScale(itemstack) + (entityplayer.isSneaking() ? 1 : -1)));
      }

      return itemstack;
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (world.getBlockId(blockX, blockY, blockZ) == Blocks.FLAG.id() && hasInitialized(itemstack)) {
         for (byte scale = 0; scale <= 4; scale++) {
            ItemMapSavedData mapData = getOrCreateSavedData((short)itemstack.getMetadata(), scale, world);
            TileEntityFlag flag = (TileEntityFlag)world.getTileEntity(blockX, blockY, blockZ);
            if (mapData.createNewWaypoint(flag) == ItemMapSavedData.FlagError.FULL) {
               player.sendTranslatedChatMessage("flag.full");
               break;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void update(World world, Entity entity, ItemMapSavedData data) {
      if (world.dimension.id == data.dimension) {
         int mapWidth = 128;
         int mapHeight = 128;
         int blocksPerPixel = 1 << data.scale;
         int mapCenterX = data.x;
         int mapCenterZ = data.z;
         int entityOnMapX = MathHelper.floor(entity.x - mapCenterX) / blocksPerPixel + 64;
         int entityOnMapZ = MathHelper.floor(entity.z - mapCenterZ) / blocksPerPixel + 64;
         int discoverRadius = 128 / blocksPerPixel;
         if (world.worldType.hasCeiling()) {
            discoverRadius /= 2;
         }

         data.tick++;

         for (int x = entityOnMapX - discoverRadius + 1; x < entityOnMapX + discoverRadius; x++) {
            if ((x & 15) == (data.tick & 15)) {
               int minZ = 255;
               int maxZ = 0;
               double previousPixelHeight = 0.0;
               boolean previousChunkLoaded = false;

               for (int z = entityOnMapZ - discoverRadius - 1; z < entityOnMapZ + discoverRadius; z++) {
                  if (x >= 0 && z >= -1 && x < 128 && z < 128) {
                     int distX = x - entityOnMapX;
                     int distZ = z - entityOnMapZ;
                     boolean flag = distX * distX + distZ * distZ > (discoverRadius - 2) * (discoverRadius - 2);
                     int blockX = (mapCenterX / blocksPerPixel + x - 64) * blocksPerPixel;
                     int blockZ = (mapCenterZ / blocksPerPixel + z - 64) * blocksPerPixel;
                     if (!world.isBlockLoaded(blockX, world.getHeightBlocks() / 2, blockZ)) {
                        previousChunkLoaded = false;
                     } else {
                        blockCounts.clear();
                        int blockInChunkX = blockX & 15;
                        int blockInChunkZ = blockZ & 15;
                        int waterDepth = 0;
                        double pixelHeight = 0.0;
                        if (world.worldType.hasCeiling()) {
                           int rand = blockX + blockZ * 231871;
                           rand = rand * rand * 31287121 + rand * 11;
                           if ((rand >> 20 & 1) == 0) {
                              blockCounts.putIfAbsent(Blocks.DIRT.id(), new HashMap<>());
                              blockCounts.get(Blocks.DIRT.id()).put(-1, blockCounts.get(Blocks.DIRT.id()).getOrDefault(-1, 0) + 10);
                           } else {
                              blockCounts.putIfAbsent(Blocks.STONE.id(), new HashMap<>());
                              blockCounts.get(Blocks.STONE.id()).put(-1, blockCounts.get(Blocks.STONE.id()).getOrDefault(-1, 0) + 10);
                           }

                           pixelHeight = 100.0;
                        } else {
                           Chunk chunk = world.getChunkFromBlockCoords(blockX, blockZ);

                           for (int _x = 0; _x < blocksPerPixel; _x++) {
                              for (int _z = 0; _z < blocksPerPixel; _z++) {
                                 int height = chunk.getHeightValue(_x + blockInChunkX, _z + blockInChunkZ);
                                 int id = 0;
                                 if (height > 0) {
                                    boolean runLoop;
                                    do {
                                       runLoop = false;
                                       id = chunk.getBlockID(_x + blockInChunkX, height, _z + blockInChunkZ);
                                       if (id <= 0 || height > 0 && Blocks.blocksList[id].getMaterialColor() == MaterialColor.none) {
                                          runLoop = true;
                                          height--;
                                       }
                                    } while (runLoop);

                                    if (Blocks.blocksList[id].getMaterial() == Material.water) {
                                       int var10001 = _x + blockInChunkX;
                                       int colorIndex = height - 1;
                                       int _blockID = chunk.getBlockID(var10001, height, _z + blockInChunkZ);
                                       waterDepth++;

                                       while (colorIndex > 0 && _blockID > 0 && Blocks.blocksList[_blockID].getMaterial() == Material.water) {
                                          _blockID = chunk.getBlockID(_x + blockInChunkX, colorIndex--, _z + blockInChunkZ);
                                          waterDepth++;
                                       }
                                    }
                                 }

                                 pixelHeight += (double)height / (blocksPerPixel * blocksPerPixel);
                                 int meta = chunk.getBlockMetadata(_x + blockInChunkX, height, _z + blockInChunkZ);
                                 blockCounts.putIfAbsent(id, new HashMap<>());
                                 blockCounts.get(id).put(-1, blockCounts.get(id).getOrDefault(-1, 0) + 1);
                                 blockCounts.get(id).put(meta, blockCounts.get(id).getOrDefault(meta, 0) + 1);
                              }
                           }
                        }

                        waterDepth /= blocksPerPixel * blocksPerPixel;
                        int max = 0;
                        int id = 0;

                        for (Entry<Integer, Map<Integer, Integer>> e : blockCounts.entrySet()) {
                           if (e.getValue().getOrDefault(-1, -1) > max) {
                              id = e.getKey();
                              max = e.getValue().get(-1);
                           }
                        }

                        int meta = 0;
                        max = 0;

                        for (int i = 0; i < 256; i++) {
                           if (blockCounts.get(id).getOrDefault(i, -1) > max) {
                              meta = i;
                              max = blockCounts.get(id).get(i);
                           }
                        }

                        double heightDifference = (pixelHeight - previousPixelHeight) * 4.0 / (blocksPerPixel + 4) + ((x + z & 1) - 0.5) * 0.4;
                        byte brightness = 1;
                        if (heightDifference > 0.6) {
                           brightness = 2;
                        }

                        if (heightDifference < -0.6) {
                           brightness = 0;
                        }

                        int colorIndex = 0;
                        if (id > 0) {
                           MaterialColor mapcolor = Blocks.blocksList[id].getMaterialColor();
                           if (mapcolor == MaterialColor.water) {
                              double d3 = waterDepth * 0.1 + (x + z & 1) * 0.2;
                              brightness = 1;
                              if (d3 < 0.5) {
                                 brightness = 2;
                              }

                              if (d3 > 0.9) {
                                 brightness = 0;
                              }
                           }

                           colorIndex = MaterialColor.getColorIndexFromBlock(Blocks.blocksList[id], meta);
                        }

                        previousPixelHeight = pixelHeight;
                        if (!previousChunkLoaded) {
                           previousChunkLoaded = true;
                        } else if (z >= 0 && distX * distX + distZ * distZ < discoverRadius * discoverRadius && (!flag || (x + z & 1) != 0)) {
                           byte currentColor = data.colors[x + z * 128];
                           byte finalColor = (byte)(colorIndex << 2 | brightness & 3);
                           if (currentColor != finalColor) {
                              if (minZ > z) {
                                 minZ = z;
                              }

                              if (maxZ < z) {
                                 maxZ = z;
                              }

                              data.colors[x + z * 128] = finalColor;
                           }
                        }
                     }
                  }
               }

               if (minZ <= maxZ) {
                  data.setDirty(x, minZ, maxZ);
               }
            }
         }
      }
   }

   @Override
   public void inventoryTick(ItemStack itemstack, World world, Entity entity, int slotId, boolean isHeld) {
      if (!world.isClientSide) {
         if (hasInitialized(itemstack)) {
            byte mapScale = this.getScale(itemstack);

            for (byte scale = 0; scale <= 4; scale++) {
               ItemMapSavedData mapData = getOrCreateSavedData((short)itemstack.getMetadata(), scale, world);
               if (entity instanceof Player) {
                  Player player = (Player)entity;
                  if (scale == mapScale) {
                     mapData.tickCarriedBy(player, itemstack);
                  }

                  mapData.tickForWaypoints(player, itemstack);
               }

               if (scale == mapScale && isHeld) {
                  this.update(world, entity, mapData);
               }
            }
         }

         ItemMapSavedData mapDatax = this.getOrCreateSavedData(itemstack, world);
         if (mapDatax != null) {
            if (entity instanceof Player) {
               Player player = (Player)entity;
               mapDatax.tickCarriedBy(player, itemstack);
            }

            if (isHeld) {
               this.update(world, entity, mapDatax);
            }
         }
      }
   }

   public void initMap(ItemStack itemstack, World world, Player player) {
      boolean insert = itemstack.stackSize > 1;
      if (insert) {
         itemstack.stackSize--;
         itemstack = itemstack.copy();
         itemstack.stackSize = 1;
      }

      itemstack.setMetadata(world.getUniqueDataId("map"));

      for (byte i = 0; i <= 4; i++) {
         String s = "map_" + itemstack.getMetadata() + "_scale_" + i;
         ItemMapSavedData mapdata = new ItemMapSavedData(s);
         world.setSavedData(s, mapdata);
         mapdata.x = MathHelper.floor(player.x) >> 4 << 4;
         mapdata.z = MathHelper.floor(player.z) >> 4 << 4;
         mapdata.scale = i;
         mapdata.dimension = (byte)world.dimension.id;
         mapdata.setDirty();
      }

      itemstack.getData().putBoolean("initialized", true);
      if (insert) {
         player.inventory.insertItem(itemstack, true);
      }
   }

   @Override
   public Packet sendPacketData(ItemStack itemstack, World world, Player player) {
      ItemMapSavedData itemMapSavedData = this.getOrCreateSavedData(itemstack, world);
      if (itemMapSavedData != null) {
         byte[] mapDataBytes = itemMapSavedData.getMapDataBytes(itemstack, world, player);
         return mapDataBytes == null
            ? null
            : new PacketMapData((short)Items.MAP.id, (short)itemstack.getMetadata(), this.getScale(itemstack), mapDataBytes, itemMapSavedData.mapWaypoints);
      } else {
         return null;
      }
   }

   @Override
   public String getTranslatedName(ItemStack itemstack) {
      return !hasInitialized(itemstack)
         ? I18n.getInstance().translateKey(itemstack.getItemKey() + ".blank.name")
         : super.getTranslatedName(itemstack) + " #" + itemstack.getMetadata();
   }

   private byte getScale(ItemStack itemStack) {
      return itemStack.getData().getByteOrDefault("scale", (byte)3);
   }

   private void setScale(ItemStack itemStack, byte scale) {
      byte currentScale = this.getScale(itemStack);

      while (scale < 0) {
         scale = (byte)(scale + 5);
      }

      if (currentScale != scale) {
         scale = (byte)(scale % 5);
         itemStack.getData().putByte("scale", scale);
      }
   }
}
