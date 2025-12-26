package net.minecraft.core.entity.monster;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.MobPathfinder;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Seasons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MobMonster extends MobPathfinder implements Enemy {
   protected int attackStrength = 2;

   public MobMonster(@Nullable World world) {
      super(world);
   }

   @Override
   public int getMaxHealth() {
      return 20;
   }

   @Override
   protected void updateAI() {
      if (this.closestFireflyEntity == null) {
         this.closestFireflyEntity = this.getClosestFireflyToEntity((int)this.x, (int)this.y, (int)this.z, 8.0F);
      }

      if (this.closestFireflyEntity != null) {
         double dX = this.x - this.closestFireflyEntity.x;
         double dZ = this.z - this.closestFireflyEntity.z;
         double hypotenuse = Math.sqrt(dX * dX + dZ * dZ);
         double scaleFactor = hypotenuse / 8.0;
         dX *= scaleFactor;
         dZ *= scaleFactor;
         Vec3 distanceXYZ = Vec3.getTempVec3(this.x + dX, this.y, this.z + dZ);
         this.pathToEntity = this.world
            .getEntityPathToXYZ(this, MathHelper.floor(distanceXYZ.x), MathHelper.floor(this.y), MathHelper.floor(distanceXYZ.z), 16.0F);
      }

      super.updateAI();
   }

   @Override
   public void onLivingUpdate() {
      float f = this.getBrightness(1.0F);
      if (f > 0.5F) {
         this.entityAge += 2;
      }

      super.onLivingUpdate();
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.world.isClientSide && !this.world.getDifficulty().canHostileMobsSpawn()) {
         this.remove();
      }
   }

   @Override
   protected Entity findPlayerToAttack() {
      Player entityplayer = this.world.getClosestPlayerToEntity(this, 16.0);
      return entityplayer != null && this.canEntityBeSeen(entityplayer) && entityplayer.getGamemode().areMobsHostile() ? entityplayer : null;
   }

   @Override
   public int getMaxSpawnedInChunk() {
      if (this.world.getSeasonManager().getCurrentSeason() == Seasons.OVERWORLD_SPRING) {
         return 2;
      } else {
         return this.world.getSeasonManager().getCurrentSeason() == Seasons.OVERWORLD_WINTER ? 8 : 4;
      }
   }

   @Override
   public boolean hurt(Entity attacker, int i, DamageType type) {
      if (super.hurt(attacker, i, type)) {
         if (this.passenger != attacker && this.vehicle != attacker) {
            if (attacker != this) {
               this.target = attacker;
            }

            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected void attackEntity(@NotNull Entity entity, float distance) {
      if (this.attackTime <= 0 && distance < 2.0F && entity.bb.maxY > this.bb.minY && entity.bb.minY < this.bb.maxY) {
         this.attackTime = 20;
         entity.hurt(this, this.attackStrength, DamageType.COMBAT);
      }
   }

   @Override
   protected float getBlockPathWeight(int x, int y, int z) {
      return 0.5F - this.world.getLightBrightness(x, y, z);
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
   }

   @Override
   public boolean canSpawnHere() {
      int blockX = MathHelper.floor(this.x);
      int blockY = MathHelper.floor(this.bb.minY);
      int blockZ = MathHelper.floor(this.z);
      if (this.world.getSavedLightValue(LightLayer.Block, blockX, blockY, blockZ) > 0) {
         return false;
      } else if (this.world.getSavedLightValue(LightLayer.Sky, blockX, blockY, blockZ) > this.random.nextInt(32)) {
         return false;
      } else {
         int blockLight = this.world.getBlockLightValue(blockX, blockY, blockZ);
         if (this.world.getCurrentWeather() != null && this.world.getCurrentWeather().doMobsSpawnInDaylight) {
            blockLight /= 2;
         }

         return blockLight <= 4 && super.canSpawnHere();
      }
   }
}
