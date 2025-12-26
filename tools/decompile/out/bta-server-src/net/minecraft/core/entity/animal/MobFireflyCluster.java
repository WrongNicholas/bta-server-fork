package net.minecraft.core.entity.animal;

import com.mojang.nbt.tags.CompoundTag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.MobFlying;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import org.jetbrains.annotations.NotNull;

public class MobFireflyCluster extends MobFlying implements AmbientCreature {
   public static final int DATA_COLOR_ID = 16;
   public static final int DATA_CLUSTER_SIZE_ID = 17;
   public static final int DATA_COUNT_ID = 18;
   public final int maxLifetime = 12000;
   public final int maxClusterSize = 6;
   public final int slowUpdateCooldownMax = 500;
   public final int maxFireflyCountPerCluster = 64;
   public int courseChangeCooldown;
   public int fireflyAge = 0;
   private int syncedClusterSize = -1;
   private int slowUpdateCooldown;

   public MobFireflyCluster(World world) {
      super(world);
      if (world != null && !world.isClientSide) {
         this.setClusterSize(this.random.nextInt(6));
         this.setFireflyCount(this.random.nextInt(5) + 3);
      } else {
         this.setClusterSize(0);
         this.setFireflyCount(0);
      }

      this.setSizeBasedOnClusterSize(this.getClusterSize());
      this.courseChangeCooldown = 0;
      this.moveSpeed = 0.05F;
      this.fireImmune = true;
      if (world != null) {
         this.slowUpdateCooldown = world.rand.nextInt(500);
      }
   }

   @Override
   public void spawnInit() {
      super.defineSynchedData();
      Biome currentBiome = this.world.getBlockBiome((int)this.x, (int)this.y, (int)this.z);
      MobFireflyCluster.FireflyColor color = null;

      label34:
      for (MobFireflyCluster.FireflyColor fireFlyColor : MobFireflyCluster.FireflyColor.fireFlyColors.values()) {
         Biome[] spawnBiomes = fireFlyColor.getSpawnBiomes();
         if (spawnBiomes != null) {
            Biome[] var7 = spawnBiomes;
            int var8 = spawnBiomes.length;
            int var9 = 0;

            while (true) {
               if (var9 < var8) {
                  Biome spawnBiome = var7[var9];
                  if (spawnBiome != currentBiome) {
                     var9++;
                     continue;
                  }

                  color = fireFlyColor;
               }

               if (color != null) {
                  break label34;
               }
               break;
            }
         }
      }

      if (color == null) {
         color = MobFireflyCluster.FireflyColor.GREEN;
      }

      this.setColor(color);
   }

   protected MobFireflyCluster.FireflyColor getColourForBiome(Biome currentBiome) {
      for (MobFireflyCluster.FireflyColor c : MobFireflyCluster.FireflyColor.fireFlyColors.values()) {
         if (c.getSpawnBiomes() != null) {
            for (Biome b : c.getSpawnBiomes()) {
               if (b == currentBiome) {
                  return c;
               }
            }
         }
      }

      return MobFireflyCluster.FireflyColor.GREEN;
   }

   @Override
   protected boolean makeStepSound() {
      return false;
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putByte("colour", this.entityData.getByte(16));
      tag.putInt("FireflyAge", this.fireflyAge);
      tag.putInt("FireflyCount", this.getFireflyCount());
      tag.putInt("ClusterSize", this.getClusterSize());
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.entityData.set(16, tag.getByte("colour"));
      this.fireflyAge = tag.getInteger("FireflyAge");
      this.setFireflyCount(tag.getInteger("FireflyCount"));
      this.setClusterSize(tag.getInteger("ClusterSize"));
      this.setPos(this.x, this.y, this.z);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(16, (byte)0, Byte.class);
      this.entityData.define(17, (byte)0, Byte.class);
      this.entityData.define(18, (byte)0, Byte.class);
   }

   public void setColor(MobFireflyCluster.FireflyColor colour) {
      this.entityData.set(16, (byte)colour.getId());
   }

   public MobFireflyCluster.FireflyColor getColor() {
      int id = this.entityData.getByte(16);
      return MobFireflyCluster.FireflyColor.fireFlyColors.get(id);
   }

   public void setClusterSize(int clusterSize) {
      this.entityData.set(17, (byte)clusterSize);
      this.setSizeBasedOnClusterSize(clusterSize);
   }

   public int getClusterSize() {
      return this.entityData.getByte(17) & 0xFF;
   }

   public void setFireflyCount(int fireflyCount) {
      this.entityData.set(18, (byte)fireflyCount);
   }

   public int getFireflyCount() {
      return this.entityData.getByte(18) & 0xFF;
   }

   @Override
   protected void updateAI() {
   }

   @Override
   public boolean canSpawnHere() {
      int x = MathHelper.floor(this.x);
      int y = MathHelper.floor(this.bb.minY);
      int z = MathHelper.floor(this.z);
      int id = this.world.getBlockId(x, y - 1, z);
      if (this.shouldRemove()) {
         return false;
      } else if (this.world.getSavedLightValue(LightLayer.Block, x, y, z) > 0) {
         return false;
      } else if (this.world.getSavedLightValue(LightLayer.Sky, x, y, z) > this.random.nextInt(32)) {
         return false;
      } else {
         int blockLight = this.world.getBlockLightValue(x, y, z);
         if (this.world.getCurrentWeather() != null && this.world.getCurrentWeather().doMobsSpawnInDaylight) {
            blockLight /= 2;
         }

         return blockLight <= 4
            && Blocks.blocksList[id] != null
            && Blocks.blocksList[id].hasTag(BlockTags.FIREFLIES_CAN_SPAWN)
            && this.world.checkIfAABBIsClear(this.bb.grow(-2.0, 0.0, -2.0));
      }
   }

   @Override
   public boolean isPickable() {
      return false;
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   public boolean isSelectable() {
      return false;
   }

   @Override
   public void push(Entity entity) {
   }

   @Override
   public boolean hurt(Entity attacker, int i, DamageType type) {
      return false;
   }

   @Override
   public String getLivingSound() {
      return "";
   }

   @Override
   protected String getHurtSound() {
      return "";
   }

   @Override
   protected String getDeathSound() {
      return "";
   }

   @Override
   public void tick() {
      super.tick();
      this.fireflyAge++;
      double dy = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbHeight;
      double dx = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
      double dz = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth;
      if (this.random.nextInt(Math.max(1, 10 - this.getFireflyCount())) == 0) {
         this.world.spawnParticle(this.getColor().getParticleName(), this.x + dx, this.y + dy, this.z + dz, this.xd, this.yd, this.zd, 0, 256.0);
      }

      if (this.world.isClientSide) {
         int clusterSize = this.getClusterSize();
         if (this.syncedClusterSize != clusterSize) {
            this.setSizeBasedOnClusterSize(clusterSize);
            this.syncedClusterSize = clusterSize;
            this.setPos(this.x, this.y, this.z);
         }
      }

      this.slowUpdates();
      if (!this.world.isClientSide && this.shouldRemove()) {
         this.remove();
      }
   }

   private boolean shouldRemove() {
      if (this.getFireflyCount() <= 0) {
         return true;
      } else {
         return !this.world.getWorldType().hasCeiling() && this.world.isDaytime() ? true : this.fireflyAge > 12000;
      }
   }

   private void slowUpdates() {
      if (--this.slowUpdateCooldown <= 0) {
         this.setPos(this.x, this.y, this.z);
         if (!this.world.isClientSide) {
            this.merge();
            if (this.getClusterSize() < 6) {
               this.expandIfPossible();
            }

            int blockX = MathHelper.floor(this.x);
            int blockY = MathHelper.floor(this.y);
            int blockZ = MathHelper.floor(this.z);
            MobFireflyCluster.FireflyColor colorForBiome = this.getColourForBiome(this.world.getBlockBiome(blockX, blockY, blockZ));
            if (colorForBiome != this.getColor()) {
               this.setColor(colorForBiome);
            }
         }
      }
   }

   private void merge() {
      this.slowUpdateCooldown = 500;
      List<MobFireflyCluster> nearbyFireflies = this.world.getEntitiesWithinAABB(MobFireflyCluster.class, this.bb.grow(2.0, 2.0, 2.0));
      int nearbyCount = nearbyFireflies.size();
      if (nearbyCount != 1) {
         for (int i = 0; i < nearbyCount; i++) {
            MobFireflyCluster fireflyCluster = nearbyFireflies.get(i);
            if (fireflyCluster != this && fireflyCluster.getColor() == this.getColor()) {
               if (fireflyCluster.getFireflyCount() <= this.getFireflyCount()
                  && (fireflyCluster.getFireflyCount() != this.getFireflyCount() || !this.world.rand.nextBoolean())) {
                  this.merge(fireflyCluster, this);
               } else {
                  this.merge(this, fireflyCluster);
               }

               if (!this.isAlive()) {
                  return;
               }
            }
         }
      }
   }

   private void merge(MobFireflyCluster from, MobFireflyCluster to) {
      if (from.getFireflyCount() + to.getFireflyCount() < 64) {
         to.setFireflyCount(to.getFireflyCount() + from.getFireflyCount());
         from.remove();
      }
   }

   public void expandIfPossible() {
      int prevSize = this.getClusterSize();
      this.setSizeBasedOnClusterSize(prevSize + 1);
      this.setPos(this.x, this.y, this.z);
      if (this.world.getCollidingSolidBlockBoundingBoxes(this, this.bb).isEmpty()) {
         this.setClusterSize(prevSize + 1);
      } else {
         this.setSizeBasedOnClusterSize(prevSize);
         this.setPos(this.x, this.y, this.z);
      }
   }

   public void setSizeBasedOnClusterSize(int clusterSize) {
      this.setSize(0.1F + clusterSize, 0.1F + clusterSize * 0.6F);
   }

   @Override
   public int getMaxSpawnedInChunk() {
      return 1;
   }

   @Override
   public boolean canRide() {
      return false;
   }

   @Override
   public boolean cycleVariant() {
      MobFireflyCluster.FireflyColor color = MobFireflyCluster.FireflyColor.fireFlyColors
         .get(this.getColor().getId() + 1 % MobFireflyCluster.FireflyColor.fireFlyColors.size());
      this.setColor(color == null ? MobFireflyCluster.FireflyColor.GREEN : color);
      return true;
   }

   public static class FireflyColor {
      public static Map<Integer, MobFireflyCluster.FireflyColor> fireFlyColors = new HashMap<>();
      public static MobFireflyCluster.FireflyColor GREEN = register(
         new MobFireflyCluster.FireflyColor(
            0,
            "fireflyGreen",
            new Biome[]{Biomes.OVERWORLD_RAINFOREST, Biomes.OVERWORLD_SWAMPLAND, Biomes.OVERWORLD_FOREST, Biomes.OVERWORLD_SEASONAL_FOREST},
            new float[]{0.7F, 0.95F, 0.35F}
         )
      );
      public static MobFireflyCluster.FireflyColor ORANGE = register(
         new MobFireflyCluster.FireflyColor(
            1,
            "fireflyOrange",
            new Biome[]{
               Biomes.OVERWORLD_DESERT, Biomes.OVERWORLD_OUTBACK, Biomes.OVERWORLD_OUTBACK_GRASSY, Biomes.OVERWORLD_CAATINGA, Biomes.OVERWORLD_CAATINGA_PLAINS
            },
            new float[]{1.0F, 0.9F, 0.58F}
         )
      );
      public static MobFireflyCluster.FireflyColor BLUE = register(
         new MobFireflyCluster.FireflyColor(
            2,
            "fireflyBlue",
            new Biome[]{Biomes.OVERWORLD_TAIGA, Biomes.OVERWORLD_TUNDRA, Biomes.OVERWORLD_BOREAL_FOREST, Biomes.OVERWORLD_GLACIER, Biomes.PARADISE_PARADISE},
            new float[]{0.67F, 1.0F, 0.99F}
         )
      );
      public static MobFireflyCluster.FireflyColor RED = register(
         new MobFireflyCluster.FireflyColor(3, "fireflyRed", new Biome[]{Biomes.NETHER_NETHER}, new float[]{1.0F, 0.25F, 0.25F})
      );
      private final int id;
      private final String particleName;
      private Biome[] spawnBiomes;
      private float[] midColor;

      public static MobFireflyCluster.FireflyColor register(MobFireflyCluster.FireflyColor color) {
         fireFlyColors.put(color.getId(), color);
         return color;
      }

      public int getId() {
         return this.id;
      }

      public String getParticleName() {
         return this.particleName;
      }

      public Biome[] getSpawnBiomes() {
         return this.spawnBiomes;
      }

      public float[] getMidColor() {
         return this.midColor;
      }

      public void setSpawnBiomes(Biome[] biomes) {
         this.spawnBiomes = biomes;
      }

      public FireflyColor(int id, String particleName, Biome[] spawnBiomes, float[] midColor) {
         this.id = id;
         this.particleName = particleName;
         this.spawnBiomes = spawnBiomes;
         this.midColor = midColor;
      }
   }
}
