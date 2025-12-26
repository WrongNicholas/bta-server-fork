package net.minecraft.core.entity.monster;

import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.projectile.ProjectileSnowball;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.season.Seasons;
import net.minecraft.core.world.weather.Weathers;
import org.jetbrains.annotations.NotNull;

public class MobSnowman extends MobMonster {
   public MobSnowman(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "snowman");
      this.moveSpeed = 0.5F;
      this.attackStrength = 5;
      this.scoreValue = 300;
      this.mobDrops.add(new WeightedRandomLootObject(Items.AMMO_SNOWBALL.getDefaultStack(), 0, 2));
   }

   @Override
   public void onLivingUpdate() {
      if (this.world.isDaytime()) {
         float f = this.getBrightness(1.0F);
         if (f > 0.5F
            && this.world.canBlockSeeTheSky(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z))
            && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F
            && (this.world.getCurrentWeather() != Weathers.OVERWORLD_FOG || this.world.weatherManager.getWeatherPower() < 0.75F)
            && this.world.getCurrentWeather() != Weathers.OVERWORLD_SNOW
            && this.world.getCurrentWeather() != Weathers.OVERWORLD_WINTER_SNOW) {
            this.remainingFireTicks = 300;
         }
      }

      super.onLivingUpdate();
   }

   @Override
   protected void attackEntity(@NotNull Entity entity, float distance) {
      if (distance < 8.0F && distance > 4.0F) {
         double dX = entity.x - this.x;
         double dZ = entity.z - this.z;
         if (this.attackTime == 0) {
            if (!this.world.isClientSide) {
               ProjectileSnowball projectileSnowball = new ProjectileSnowball(this.world, this);
               if (this.world.getBlockId((int)this.x, (int)this.y - 1, (int)this.z) == Blocks.GRAVEL.id()) {
                  projectileSnowball.damage = 1;
               }

               double d2 = entity.y + entity.getHeadHeight() - 0.2 - projectileSnowball.y;
               float f1 = MathHelper.sqrt(dX * dX + dZ * dZ) * 0.2F;
               this.world.playSoundAtEntity(null, this, "random.bow", 0.5F, 0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
               this.world.entityJoinedWorld(projectileSnowball);
               projectileSnowball.setHeadingPrecise(dX, d2 + f1, dZ, 0.6F);
            }

            this.attackTime = 30;
         }

         this.yRot = (float)(Math.atan2(dZ, dX) * 180.0 / Math.PI) - 90.0F;
         this.hasAttacked = true;
      } else if (distance <= 4.0F) {
         super.attackEntity(entity, distance);
      }
   }

   @Override
   public boolean canSpawnHere() {
      int x = (int)this.x;
      int y = (int)this.y;
      int z = (int)this.z;
      Biome biome = this.world.getBlockBiome(x, y, z);
      return super.canSpawnHere()
         && this.world.canBlockSeeTheSky(x, y, z)
         && (
            biome == Biomes.OVERWORLD_GLACIER
               || (
                     this.world.getSeasonManager().getCurrentSeason() == Seasons.OVERWORLD_WINTER
                        || this.world.getSeasonManager().getCurrentSeason() == Seasons.OVERWORLD_WINTER_ENDLESS
                  )
                  && (this.world.getCurrentWeather() == Weathers.OVERWORLD_SNOW || this.world.getCurrentWeather() == Weathers.OVERWORLD_WINTER_SNOW)
         );
   }

   @Override
   public String getLivingSound() {
      return "mob.zombie";
   }

   @Override
   protected String getHurtSound() {
      return "mob.zombiehurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.zombiedeath";
   }

   @Override
   protected void dropDeathItems() {
      if (this.random.nextInt(1000) == 0) {
         this.dropItem(Items.BUCKET_ICECREAM.id, 1);
      }

      super.dropDeathItems();
   }
}
