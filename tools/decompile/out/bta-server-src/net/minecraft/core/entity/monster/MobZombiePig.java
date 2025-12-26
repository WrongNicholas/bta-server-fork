package net.minecraft.core.entity.monster;

import com.mojang.nbt.tags.CompoundTag;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobZombiePig extends MobZombie {
   private int angerLevel = 0;
   private int randomSoundDelay = 0;
   private static final ItemStack defaultHeldItem = new ItemStack(Items.TOOL_SWORD_GOLD, 1);

   public MobZombiePig(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "pigzombie");
      this.moveSpeed = 0.5F;
      this.attackStrength = 5;
      this.fireImmune = false;
      this.scoreValue = 500;
   }

   @Override
   public void tick() {
      if (this.tickCount % 200 == 0 && this.target instanceof Player && ((Player)this.target).getStat(Achievements.MOST_WANTED) <= 0) {
         int agroNumber = 1;

         for (Entity e : this.world.loadedEntityList) {
            if (e instanceof MobZombiePig && ((MobZombiePig)e).target == this.target) {
               agroNumber++;
            }

            if (agroNumber >= 20) {
               ((Player)this.target).addStat(Achievements.MOST_WANTED, 1);
               break;
            }
         }
      }

      this.moveSpeed = this.target == null ? 0.5F : 0.95F;
      if (this.randomSoundDelay > 0 && --this.randomSoundDelay == 0) {
         this.world
            .playSoundAtEntity(
               null, this, "mob.zombiepig.zpigangry", this.getSoundVolume() * 2.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.8F
            );
      }

      super.tick();
   }

   @Override
   public boolean canSpawnHere() {
      int x = MathHelper.floor(this.x);
      int y = MathHelper.floor(this.y);
      int z = MathHelper.floor(this.z);
      if (this.world.getBlockId(x, y, z) != 0) {
         return false;
      } else {
         Block<?> below = this.world.getBlock(x, y - 1, z);
         if (!BlockTags.NETHER_MOBS_SPAWN.appliesTo(below)) {
            return false;
         } else {
            int blockLight = this.world.getSavedLightValue(LightLayer.Block, x, y, z);
            return blockLight > 7
               ? false
               : this.world.getDifficulty().canHostileMobsSpawn()
                  && this.world.checkIfAABBIsClear(this.bb)
                  && this.world.getCubes(this, this.bb).isEmpty()
                  && !this.world.getIsAnyLiquid(this.bb);
         }
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putShort("Anger", (short)this.angerLevel);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.angerLevel = tag.getShort("Anger");
   }

   @Override
   protected Entity findPlayerToAttack() {
      return this.angerLevel == 0 ? null : super.findPlayerToAttack();
   }

   @Override
   public boolean hurt(Entity attacker, int i, DamageType type) {
      if (type == DamageType.FIRE) {
         return false;
      } else {
         if (attacker instanceof Player) {
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb.grow(32.0, 32.0, 32.0));

            for (int j = 0; j < list.size(); j++) {
               Entity e = list.get(j);
               if (e instanceof MobZombiePig) {
                  MobZombiePig mobZombiePig = (MobZombiePig)e;
                  mobZombiePig.becomeAngryAt(attacker);
               }
            }

            this.becomeAngryAt(attacker);
         }

         return super.hurt(attacker, i, type);
      }
   }

   private void becomeAngryAt(Entity entity) {
      this.target = entity;
      this.angerLevel = 400 + this.random.nextInt(400);
      this.randomSoundDelay = this.random.nextInt(40);
   }

   @Override
   public String getLivingSound() {
      return "mob.zombiepig.zpig";
   }

   @Override
   protected String getHurtSound() {
      return "mob.zombiepig.zpighurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.zombiepig.zpigdeath";
   }

   @Override
   protected List<WeightedRandomLootObject> getMobDrops() {
      List<WeightedRandomLootObject> drops = new ArrayList<>();
      float drop = this.world.rand.nextFloat();
      if (!(drop < 0.5F)) {
         if (drop < 0.95F) {
            if (this.isOnFire()) {
               drops.add(new WeightedRandomLootObject(Items.FOOD_PORKCHOP_COOKED.getDefaultStack(), 0, 1));
            } else {
               drops.add(new WeightedRandomLootObject(Items.FOOD_PORKCHOP_RAW.getDefaultStack(), 0, 1));
            }
         } else if (drop < 0.99F) {
            drops.add(new WeightedRandomLootObject(Items.ORE_RAW_GOLD.getDefaultStack(), 0, 1));
         } else {
            drops.add(new WeightedRandomLootObject(Items.TOOL_SWORD_GOLD.getDefaultStack(), 0, 1));
         }
      }

      return drops;
   }

   @Override
   public ItemStack getHeldItem() {
      return defaultHeldItem;
   }
}
