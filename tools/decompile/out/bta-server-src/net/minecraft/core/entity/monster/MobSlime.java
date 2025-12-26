package net.minecraft.core.entity.monster;

import com.mojang.nbt.tags.CompoundTag;
import java.util.List;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

public class MobSlime extends Mob implements Enemy {
   public static final int DATA_SLIME_SIZE = 16;
   public float squish;
   public float oSquish;
   private int jumpDelay;
   private final boolean hasSlimeSplit;
   private boolean sizeSet = false;

   public MobSlime(World world) {
      super(world);
      this.jumpDelay = 0;
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "slime");
      this.heightOffset = 0.0F;
      this.jumpDelay = 20;
      this.scoreValue = 100;
      this.hasSlimeSplit = false;
      this.mobDrops.add(new WeightedRandomLootObject(Items.SLIMEBALL.getDefaultStack(), 0, 2));
   }

   public MobSlime(World world, boolean isSplit) {
      super(world);
      this.jumpDelay = 0;
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "slime");
      this.heightOffset = 0.0F;
      this.jumpDelay = 20;
      this.scoreValue = 100;
      this.hasSlimeSplit = isSplit;
      this.mobDrops.add(new WeightedRandomLootObject(Items.SLIMEBALL.getDefaultStack(), 0, 2));
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(16, (byte)1, Byte.class);
   }

   @Override
   public void spawnInit() {
      super.defineSynchedData();
      if (!this.hasSlimeSplit) {
         int size = this.random.nextInt(3) + 1;
         if (size == 3) {
            size = 4;
         }

         this.setSlimeSize(size);
      }
   }

   public void setSlimeSize(int i) {
      this.entityData.set(16, (byte)i);
      this.setSize(0.5F * i, 0.5F * i);
      this.setHealthRaw(this.getMaxHealth());
      this.setPos(this.x, this.y, this.z);
   }

   @Override
   public int getMaxHealth() {
      return this.getSlimeSize() * this.getSlimeSize();
   }

   public int getSlimeSize() {
      return this.entityData.getByte(16);
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("Size", this.getSlimeSize() - 1);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      this.setSlimeSize(tag.getInteger("Size") + 1);
      this.sizeSet = true;
      super.readAdditionalSaveData(tag);
   }

   @Override
   public void tick() {
      if (!this.sizeSet) {
         this.setSlimeSize(this.getSlimeSize());
         this.sizeSet = true;
      }

      this.oSquish = this.squish;
      boolean flag = this.onGround;
      super.tick();
      if (this.onGround && !flag) {
         int i = this.getSlimeSize();

         for (int j = 0; j < i * 8; j++) {
            float f = this.random.nextFloat() * (float) Math.PI * 2.0F;
            double f1 = this.random.nextFloat() * 0.5 + 0.5;
            double f2 = MathHelper.sin(f) * i * 0.5 * f1;
            double f3 = MathHelper.cos(f) * i * 0.5 * f1;
            this.world.spawnParticle("item", this.x + f2, this.bb.minY, this.z + f3, 0.0, 0.0, 0.0, Items.SLIMEBALL.id);
         }

         if (i > 2) {
            this.world
               .playSoundAtEntity(null, this, "mob.slime", this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
         }

         this.squish = -0.5F;
      }

      if (!this.world.isClientSide && !this.world.getDifficulty().canHostileMobsSpawn()) {
         this.remove();
      }

      this.squish *= 0.6F;
   }

   @Override
   protected void updateAI() {
      this.tryToDespawn();
      Player entityplayer = this.world.getClosestPlayerToEntity(this, 16.0);
      boolean targetPlayer = entityplayer != null && entityplayer.getGamemode().areMobsHostile();
      if (entityplayer != null && targetPlayer) {
         this.lookAt(entityplayer, 10.0F, 20.0F);
      }

      if (this.onGround && this.jumpDelay-- <= 0) {
         if (!targetPlayer) {
            float rotation = (this.world.rand.nextFloat() - 0.5F) * 90.0F;
            this.yRot += rotation;
         }

         this.jumpDelay = this.random.nextInt(20) + 10;
         if (entityplayer != null) {
            this.jumpDelay /= 3;
         }

         this.isJumping = true;
         if (this.getSlimeSize() > 1) {
            this.world
               .playSoundAtEntity(null, this, "mob.slime", this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
         }

         this.squish = 1.0F;
         this.moveStrafing = 1.0F - this.random.nextFloat() * 2.0F;
         this.moveForward = this.getSlimeSize();
      } else {
         this.isJumping = false;
         if (this.onGround) {
            this.moveStrafing = this.moveForward = 0.0F;
         }
      }
   }

   @Override
   public void remove() {
      int size = this.getSlimeSize();
      if (!this.world.isClientSide && size > 1 && this.getHealth() <= 0) {
         int splitCount = this.world.rand.nextInt(4) + 1;

         for (int j = 0; j < splitCount; j++) {
            float f = (j % 2 - 0.5F) * size / 4.0F;
            float f1 = (j / 2 - 0.5F) * size / 4.0F;
            MobSlime mobSlime = new MobSlime(this.world, true);
            mobSlime.setSlimeSize(size / 2);
            mobSlime.moveTo(this.x + f, this.y + 0.5, this.z + f1, this.random.nextFloat() * 360.0F, 0.0F);
            this.world.entityJoinedWorld(mobSlime);
         }
      }

      super.remove();
   }

   @Override
   public void playerTouch(Player player) {
      int i = this.getSlimeSize();
      if (i > 1 && this.canEntityBeSeen(player) && this.distanceTo(player) < 0.6 * i && player.hurt(this, i, DamageType.COMBAT)) {
         this.world.playSoundAtEntity(null, this, "mob.slimeattack", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
      }
   }

   @Override
   protected String getHurtSound() {
      return "mob.slime";
   }

   @Override
   protected String getDeathSound() {
      return "mob.slime";
   }

   @Override
   protected List<WeightedRandomLootObject> getMobDrops() {
      return this.getSlimeSize() == 1 ? this.mobDrops : null;
   }

   @Override
   public boolean canSpawnHere() {
      Chunk chunk = this.world.getChunkFromBlockCoords(MathHelper.floor(this.x), MathHelper.floor(this.z));
      if (this.y > 32.0) {
         return false;
      } else if (chunk.getChunkRandom(987234911L).nextInt(10) != 0) {
         return false;
      } else {
         return !this.world.getDifficulty().canHostileMobsSpawn() ? false : super.canSpawnHere();
      }
   }

   @Override
   protected float getSoundVolume() {
      return 0.6F;
   }
}
