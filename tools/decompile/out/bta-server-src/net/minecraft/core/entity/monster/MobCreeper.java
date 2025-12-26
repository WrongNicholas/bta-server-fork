package net.minecraft.core.entity.monster;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityLightning;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MobCreeper extends MobMonster {
   public static final int DATA_CREEPER_STATE = 16;
   public static final int DATA_POWERED = 17;
   int timeSinceIgnited;
   int lastActiveTime;

   public MobCreeper(@Nullable World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "creeper");
      this.scoreValue = 800;
      this.setSize(0.6F, 1.6F);
      this.mobDrops.add(new WeightedRandomLootObject(Items.SULPHUR.getDefaultStack(), 0, 2));
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(16, (byte)-1, Byte.class);
      this.entityData.define(17, (byte)0, Byte.class);
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      if (this.entityData.getByte(17) == 1) {
         tag.putBoolean("powered", true);
      }
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.entityData.set(17, (byte)(tag.getBoolean("powered") ? 1 : 0));
   }

   @Override
   protected void attackBlockedEntity(@NotNull Entity entity, float f) {
      if (!this.world.isClientSide) {
         if (this.timeSinceIgnited > 0) {
            this.setCreeperState(-1);
            this.timeSinceIgnited--;
            if (this.timeSinceIgnited < 0) {
               this.timeSinceIgnited = 0;
            }
         }
      }
   }

   @Override
   public void tick() {
      this.lastActiveTime = this.timeSinceIgnited;
      if (this.world.isClientSide) {
         int i = this.getCreeperState();
         if (i > 0 && this.timeSinceIgnited == 0 && !this.world.isClientSide) {
            this.world.playSoundAtEntity(null, this, "mob.creeper.fuse", 1.0F, 1.0F);
         }

         this.timeSinceIgnited += i;
         if (this.timeSinceIgnited < 0) {
            this.timeSinceIgnited = 0;
         }

         if (this.timeSinceIgnited >= 30) {
            this.timeSinceIgnited = 30;
         }
      }

      super.tick();
   }

   @Override
   protected String getHurtSound() {
      return "mob.creeper";
   }

   @Override
   protected String getDeathSound() {
      return "mob.creeperdeath";
   }

   @Override
   public void onDeath(Entity entityKilledBy) {
      super.onDeath(entityKilledBy);
      if (entityKilledBy instanceof MobSkeleton) {
         this.dropItem(Items.RECORD_13.id + this.random.nextInt(11), 1);
      }
   }

   @Override
   protected void attackEntity(@NotNull Entity entity, float distance) {
      if (!this.world.isClientSide) {
         int i = this.getCreeperState();
         if (i <= 0 && distance < 3.0F || i > 0 && distance < 7.0F) {
            if (this.timeSinceIgnited == 0 && !this.world.isClientSide) {
               this.world.playSoundAtEntity(null, this, "mob.creeper.fuse", 1.0F, 1.0F);
            }

            this.setCreeperState(1);
            this.timeSinceIgnited++;
            if (this.timeSinceIgnited >= 30) {
               if (this.getPowered()) {
                  this.world.createExplosion(this, this.x, this.y + this.bbHeight / 2.0F, this.z, 6.0F);
               } else {
                  this.world.createExplosion(this, this.x, this.y + this.bbHeight / 2.0F, this.z, 3.0F);
               }

               this.remove();
            }

            this.hasAttacked = true;
         } else {
            this.setCreeperState(-1);
            this.timeSinceIgnited--;
            if (this.timeSinceIgnited < 0) {
               this.timeSinceIgnited = 0;
            }
         }
      }
   }

   public boolean getPowered() {
      return this.entityData.getByte(17) == 1;
   }

   public float getSwelling(float partialTick) {
      return (this.lastActiveTime + (this.timeSinceIgnited - this.lastActiveTime) * partialTick) / 28.0F;
   }

   private int getCreeperState() {
      return this.entityData.getByte(16);
   }

   private void setCreeperState(int i) {
      this.entityData.set(16, (byte)i);
   }

   @Override
   public void thunderHit(EntityLightning bolt) {
      super.thunderHit(bolt);
      this.entityData.set(17, (byte)1);
   }
}
