package net.minecraft.core.world;

import com.mojang.logging.LogUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.block.BlockLogicBed;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.entity.monster.MobSpider;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.entity.monster.MobZombieArmored;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.MobCategory;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.chunk.ChunkPosition;
import net.minecraft.core.world.config.spawning.SpawnerConfig;
import net.minecraft.core.world.pathfinder.Node;
import net.minecraft.core.world.pathfinder.Path;
import net.minecraft.core.world.pathfinder.PathFinder;
import org.slf4j.Logger;

public final class SpawnerMobs {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Set<ChunkCoordinate> eligibleChunksForSpawning = new HashSet<>();
   private static final Class<?>[] nightSpawnEntities = new Class[]{MobSpider.class, MobZombie.class, MobSkeleton.class, MobZombieArmored.class};

   private static ChunkPosition getRandomSpawningPointInChunk(World world, int chunkX, int chunkZ) {
      int newX = chunkX * 16 + world.rand.nextInt(16);
      int newY = world.rand.nextInt(world.getHeightBlocks());
      int newZ = chunkZ * 16 + world.rand.nextInt(16);
      return new ChunkPosition(newX, newY, newZ);
   }

   public static int performSpawning(World world, SpawnerConfig spawnerConfig) {
      if (!spawnerConfig.canHostileSpawn(world) && !spawnerConfig.canPassiveSpawn(world)) {
         return 0;
      } else {
         eligibleChunksForSpawning.clear();

         for (Player player : world.players) {
            int playerChunkX = MathHelper.floor(player.x / 16.0);
            int playerChunkZ = MathHelper.floor(player.z / 16.0);
            byte spawnRadius = 8;

            for (int dx = -8; dx <= 8; dx++) {
               for (int dz = -8; dz <= 8; dz++) {
                  if (world.isChunkLoaded(dx + playerChunkX, dz + playerChunkZ)) {
                     eligibleChunksForSpawning.add(new ChunkCoordinate(dx + playerChunkX, dz + playerChunkZ));
                  }
               }
            }
         }

         int totalSpawned = 0;
         ChunkCoordinates spawnPoint = world.getSpawnPoint();
         ChunkCoordinate[] spawnChunks = new ChunkCoordinate[9];

         for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
               spawnChunks[x * 3 + z] = new ChunkCoordinate((spawnPoint.x & 15) + (x - 1), (spawnPoint.z & 15) + (z - 1));
            }
         }

         for (MobCategory creatureType : MobCategory.values()) {
            if ((!creatureType.isPeaceful() || spawnerConfig.canPassiveSpawn(world))
               && (creatureType.isPeaceful() || spawnerConfig.canHostileSpawn(world))
               && world.countEntities(creatureType.getBaseClass()) <= creatureType.getMaxCreaturesPerChunk() * eligibleChunksForSpawning.size() / 256) {
               label175:
               for (ChunkCoordinate chunk : eligibleChunksForSpawning) {
                  boolean checkSpawnDist = false;

                  for (ChunkCoordinate c : spawnChunks) {
                     if (c.equals(chunk)) {
                        checkSpawnDist = true;
                        break;
                     }
                  }

                  if (world.isChunkLoaded(chunk.x, chunk.z)) {
                     int blockX = chunk.x * 16;
                     int blockZ = chunk.z * 16;
                     Biome biome = world.getBlockBiome(blockX, world.getHeightBlocks() - 1, blockZ);
                     List<SpawnListEntry> spawnableList = biome.getSpawnableList(creatureType);
                     if (spawnableList != null && !spawnableList.isEmpty()) {
                        int totalSpawnRarity = 0;

                        for (SpawnListEntry listEntry : spawnableList) {
                           totalSpawnRarity += listEntry.spawnFrequency;
                        }

                        int calculatedSpawnChance = world.rand.nextInt(totalSpawnRarity);
                        SpawnListEntry spawnListEntry = spawnableList.get(0);

                        for (SpawnListEntry listEntry : spawnableList) {
                           calculatedSpawnChance -= listEntry.spawnFrequency;
                           if (calculatedSpawnChance < 0) {
                              spawnListEntry = listEntry;
                              break;
                           }
                        }

                        if (spawnerConfig.canMobSpawn(EntityDispatcher.idForClass(spawnListEntry.entityClass))) {
                           ChunkPosition pos = getRandomSpawningPointInChunk(world, chunk.x, chunk.z);
                           int x = pos.x;
                           int y = pos.y;
                           int z = pos.z;
                           if (!world.isBlockNormalCube(x, y, z) && world.getBlockMaterial(x, y, z) == creatureType.getSpawnMaterial()) {
                              int countSpawned = 0;
                              byte range = 6;

                              for (int i = 0; i < 3; i++) {
                                 int ix = x;
                                 int iy = y;
                                 int iz = z;

                                 for (int spawnAttempt = 0; spawnAttempt < 4; spawnAttempt++) {
                                    ix += world.rand.nextInt(6) - world.rand.nextInt(6);
                                    iy += world.rand.nextInt(2) - world.rand.nextInt(2);
                                    iz += world.rand.nextInt(6) - world.rand.nextInt(6);
                                    if (world.isBlockLoaded(ix, iy, iz) && canCreatureTypeSpawnAtLocation(creatureType, world, ix, iy, iz)) {
                                       double dx = ix + 0.5;
                                       double dy = iy;
                                       double dzx = iz + 0.5;
                                       if (world.getClosestPlayer(dx, dy, dzx, 24.0) == null) {
                                          if (checkSpawnDist) {
                                             double spawnDistanceX = dx - spawnPoint.x;
                                             double spawnDistanceY = dy - spawnPoint.y;
                                             double spawnDistanceZ = dzx - spawnPoint.z;
                                             double spawnDistanceSquared = spawnDistanceX * spawnDistanceX
                                                + spawnDistanceY * spawnDistanceY
                                                + spawnDistanceZ * spawnDistanceZ;
                                             if (spawnDistanceSquared < 576.0) {
                                                continue;
                                             }
                                          }

                                          Mob mobToSpawn;
                                          try {
                                             mobToSpawn = (Mob)spawnListEntry.entityClass.getConstructor(World.class).newInstance(world);
                                          } catch (Exception var44) {
                                             LOGGER.error(
                                                "Error spawning entity class '{}' class missing default constructor for World! Skipping spawn attempt!",
                                                spawnListEntry.entityClass.getSimpleName(),
                                                var44
                                             );
                                             return totalSpawned;
                                          }

                                          mobToSpawn.moveTo(dx, dy, dzx, world.rand.nextFloat() * 360.0F, 0.0F);
                                          if (mobToSpawn.canSpawnHere()) {
                                             countSpawned++;
                                             mobToSpawn.spawnInit();
                                             world.entityJoinedWorld(mobToSpawn);
                                             if (countSpawned >= mobToSpawn.getMaxSpawnedInChunk()) {
                                                continue label175;
                                             }
                                          }
                                       }
                                    }
                                 }
                              }

                              totalSpawned += countSpawned;
                           }
                        }
                     }
                  }
               }
            }
         }

         return totalSpawned;
      }
   }

   private static boolean canCreatureTypeSpawnAtLocation(MobCategory mobCategory, World world, int x, int y, int z) {
      return mobCategory.getSpawnMaterial() == Material.water
         ? world.getBlockMaterial(x, y, z).isLiquid() && !world.isBlockNormalCube(x, y + 1, z)
         : world.isBlockNormalCube(x, y - 1, z) && !world.isBlockNormalCube(x, y, z) && !world.getBlockMaterial(x, y, z).isLiquid();
   }

   public static boolean performSleepSpawning(World world, List<Player> list) {
      if (!world.getGameRuleValue(GameRules.DO_NIGHTMARES)) {
         return false;
      } else {
         boolean spawnedEntity = false;
         PathFinder pathFinder = new PathFinder(world);

         for (Player player : list) {
            if (player.isPlayerSleeping() && !player.getGamemode().isPlayerInvulnerable()) {
               Class<?>[] entityClasses = nightSpawnEntities;
               if (entityClasses != null && entityClasses.length != 0) {
                  boolean spawnedEntityForPlayer = false;

                  for (int i = 0; i < 20 && !spawnedEntityForPlayer; i++) {
                     int x = MathHelper.floor(player.x) + world.rand.nextInt(32) - world.rand.nextInt(32);
                     int z = MathHelper.floor(player.z) + world.rand.nextInt(32) - world.rand.nextInt(32);
                     int y = MathHelper.floor(player.y) + world.rand.nextInt(16) - world.rand.nextInt(16);
                     y = MathHelper.clamp(y, 1, world.getHeightBlocks());
                     int spawnY = y;

                     while (spawnY > 2 && !world.isBlockNormalCube(x, spawnY - 1, z)) {
                        spawnY--;
                     }

                     while (!canCreatureTypeSpawnAtLocation(MobCategory.monster, world, x, spawnY, z) && spawnY < y + 16 && spawnY < world.getHeightBlocks()) {
                        spawnY++;
                     }

                     if (spawnY < y + 16) {
                        float entityX = x + 0.5F;
                        float entityY = spawnY;
                        float entityZ = z + 0.5F;
                        Class<?> eClass = entityClasses[world.rand.nextInt(entityClasses.length)];

                        Mob entity;
                        try {
                           entity = (Mob)eClass.getConstructor(World.class).newInstance(world);
                        } catch (Exception var22) {
                           LOGGER.error("Exception instancing class '{}'!", eClass.getSimpleName(), var22);
                           return spawnedEntity;
                        }

                        entity.moveTo(entityX, entityY, entityZ, world.rand.nextFloat() * 360.0F, 0.0F);
                        if (entity.canSpawnHere()) {
                           Path pathEntity = pathFinder.findPath(entity, player, 32.0F);
                           if (pathEntity != null && pathEntity.length > 1) {
                              Node pathPoint = pathEntity.last();
                              boolean isCollisionFree = world.checkBlockCollisionBetweenPoints(
                                    Vec3.getTempVec3(player.x, player.y + 1.5, player.z), Vec3.getTempVec3(pathPoint.x, pathPoint.y + 1.5F, pathPoint.z)
                                 )
                                 == null;
                              if (Math.abs(pathPoint.x + 0.5F - player.x) < 1.5
                                 && Math.abs(pathPoint.z + 0.5F - player.z) < 1.5
                                 && Math.abs(pathPoint.y + 0.5F - player.y) < 1.5
                                 && isCollisionFree) {
                                 ChunkCoordinates pos = BlockLogicBed.getNearestEmptyChunkCoordinates(
                                    world, MathHelper.floor(player.x), MathHelper.floor(player.y), MathHelper.floor(player.z), 1
                                 );
                                 if (pos == null) {
                                    pos = new ChunkCoordinates(x, spawnY + 1, z);
                                 }

                                 entity.moveTo(pos.x + 0.5F, pos.y, pos.z + 0.5F, 0.0F, 0.0F);
                                 entity.spawnInit();
                                 world.entityJoinedWorld(entity);
                                 player.wakeUpPlayer(true, false);
                                 world.playBlockEvent(
                                    null,
                                    2001,
                                    (int)player.x,
                                    (int)player.y - 1,
                                    (int)player.z,
                                    world.getBlockId((int)player.x, (int)player.y - 1, (int)player.z)
                                 );
                                 entity.playLivingSound();
                                 spawnedEntity = true;
                                 spawnedEntityForPlayer = true;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return spawnedEntity;
      }
   }
}
