package net.minecraft.core.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.enums.Difficulty;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityLightning extends Entity {
   private int life;
   public long seed = 0L;
   private int flashes;

   public EntityLightning(World world) {
      this(world, 0.0, 0.0, 0.0);
   }

   public EntityLightning(World world, double x, double y, double z) {
      super(world);
      this.moveTo(x, y, z, 0.0F, 0.0F);
      this.life = 2;
      this.seed = this.random.nextLong();
      this.flashes = this.random.nextInt(3) + 1;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.life == 2) {
         this.world
            .playSoundEffect(
               null, SoundCategory.WEATHER_SOUNDS, this.x, this.y, this.z, "ambient.weather.thunder", 10000.0F, 0.8F + this.random.nextFloat() * 0.2F
            );
         this.world.playSoundEffect(null, SoundCategory.WEATHER_SOUNDS, this.x, this.y, this.z, "random.explode", 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
      }

      this.life--;
      if (this.life < 0) {
         if (this.flashes == 0) {
            this.remove();
         } else if (this.life < -this.random.nextInt(10)) {
            this.flashes--;
            this.life = 1;
            this.seed = this.random.nextLong();
            if (this.world.areBlocksLoaded(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z), 10)) {
               int i = MathHelper.floor(this.x);
               int j = MathHelper.floor(this.y);
               int k = MathHelper.floor(this.z);
               if (this.world.getBlockId(i, j, k) == 0 && Blocks.FIRE.canPlaceBlockAt(this.world, i, j, k)) {
                  this.world.setBlockWithNotify(i, j, k, Blocks.FIRE.id());
               }
            }
         }
      }

      if (this.life >= 0) {
         double radius = 3.0;

         for (Entity entity : this.world
            .getEntitiesWithinAABBExcludingEntity(
               this, AABB.getTemporaryBB(this.x - radius, this.y - radius, this.z - radius, this.x + radius, this.y + 6.0 + radius, this.z + radius)
            )) {
            entity.thunderHit(this);
         }

         this.world.lightningFlicker = 2;
      }
   }

   @Override
   protected void defineSynchedData() {
   }

   @Override
   public void spawnInit() {
      if (this.world.getDifficulty().id() >= Difficulty.NORMAL.id()
         && this.world.areBlocksLoaded(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z), 10)) {
         int i = MathHelper.floor(this.x);
         int k = MathHelper.floor(this.y);
         int i1 = MathHelper.floor(this.z);
         if (this.world.getBlockId(i, k, i1) == 0 && Blocks.FIRE.canPlaceBlockAt(this.world, i, k, i1)) {
            this.world.setBlockWithNotify(i, k, i1, Blocks.FIRE.id());
         }

         for (int j = 0; j < 4; j++) {
            int l = MathHelper.floor(this.x) + this.random.nextInt(3) - 1;
            int j1 = MathHelper.floor(this.y) + this.random.nextInt(3) - 1;
            int k1 = MathHelper.floor(this.z) + this.random.nextInt(3) - 1;
            if (this.world.getBlockId(l, j1, k1) == 0 && Blocks.FIRE.canPlaceBlockAt(this.world, l, j1, k1)) {
               this.world.setBlockWithNotify(l, j1, k1, Blocks.FIRE.id());
            }
         }
      }
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
   }

   @Override
   public boolean shouldRender(Vec3 vec3) {
      return this.life >= 0;
   }
}
