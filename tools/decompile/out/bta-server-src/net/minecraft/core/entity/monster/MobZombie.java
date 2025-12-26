package net.minecraft.core.entity.monster;

import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.Weathers;

public class MobZombie extends MobMonster {
   public MobZombie(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "zombie");
      this.moveSpeed = 0.5F;
      this.attackStrength = 5;
      this.scoreValue = 300;
      this.mobDrops.add(new WeightedRandomLootObject(Items.CLOTH.getDefaultStack(), 0, 2));
   }

   @Override
   public void onLivingUpdate() {
      if (this.world.isDaytime()) {
         float f = this.getBrightness(1.0F);
         Weather weather = this.world.getCurrentWeather();
         if (f > 0.5F
            && this.world.canBlockSeeTheSky(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z))
            && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F
            && weather != null
            && !weather.isDamp
            && (this.world.getCurrentWeather() != Weathers.OVERWORLD_FOG || this.world.weatherManager.getWeatherPower() < 0.75F)) {
            this.remainingFireTicks = 300;
         }
      }

      super.onLivingUpdate();
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
}
