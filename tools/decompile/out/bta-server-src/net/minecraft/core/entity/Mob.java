package net.minecraft.core.entity;

import com.b100.utils.StringUtils;
import com.mojang.nbt.tags.CompoundTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Global;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.PlacementMode;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Mob extends Entity {
   public static final int DATA_VARIANT = 1;
   public static final int DATA_HEALTH = 2;
   @NotNull
   protected Map<ItemFood, ConsumedFood> consumedFood = new HashMap<>();
   public int heartsHalvesLife;
   public float yBodyRot;
   public float yBodyRotO;
   protected float ridingRotUnused;
   protected float prevRidingRotUnused;
   protected float unusedRotation1;
   protected float prevRotationUnused;
   protected int scoreValue;
   public boolean isMultiplayerEntity;
   public float prevSwingProgress;
   public float swingProgress;
   public int prevHealth;
   public int bonusHealth;
   public int prevBonusHealth;
   private int livingSoundTime;
   public int hurtTime;
   public int maxHurtTime;
   public float attackedAtYaw;
   public int deathTime;
   public int attackTime;
   public float prevCameraPitch;
   public float cameraPitch;
   protected boolean dead;
   public float walkAnimSpeedO;
   public float walkAnimSpeed;
   public float walkAnimPos;
   protected int newPosRotationIncrements;
   protected double newPosX;
   protected double newPosY;
   protected double newPosZ;
   protected double newRotationYaw;
   protected double newRotationPitch;
   protected int lastDamage;
   protected int entityAge;
   protected float moveStrafing;
   protected float moveForward;
   protected float randomYawVelocity;
   protected boolean isJumping;
   protected float defaultPitch;
   protected float moveSpeed;
   protected float noclipSpeed = 0.5F;
   protected float flightSmoothness = 0.5F;
   @Nullable
   private Entity currentTarget = null;
   protected int numTicksToChaseTarget;
   @NotNull
   public String nickname = "";
   public byte chatColor = 0;
   @NotNull
   protected NamespaceID textureIdentifier = NamespaceID.getPermanent("minecraft", "char");
   protected float lastStrafe = 0.0F;
   public boolean isSkating = false;
   @Nullable
   public Direction rotationLock = null;
   @Nullable
   public Direction rotationLockHorizontal = null;
   @Nullable
   public Direction rotationLockVertical = null;
   @NotNull
   public PlacementMode placementModeOverride = PlacementMode.FACING;
   @NotNull
   public final List<WeightedRandomLootObject> mobDrops = new ArrayList<>();

   public Mob(@Nullable World world) {
      super(world);
      this.heartsHalvesLife = 20;
      this.yBodyRot = 0.0F;
      this.yBodyRotO = 0.0F;
      this.scoreValue = 0;
      this.isMultiplayerEntity = false;
      this.attackedAtYaw = 0.0F;
      this.deathTime = 0;
      this.attackTime = 0;
      this.dead = false;
      this.lastDamage = 0;
      this.entityAge = 0;
      this.isJumping = false;
      this.defaultPitch = 0.0F;
      this.moveSpeed = 0.7F;
      this.numTicksToChaseTarget = 0;
      this.blocksBuilding = true;
      this.setPos(this.x, this.y, this.z);
      this.yRot = (float)(Math.random() * Math.PI * 2.0);
      this.footSize = 0.5F;
      int variant = this.random.nextInt(255);
      this.entityData.define(1, (byte)variant, Byte.class);
      this.entityData.define(2, this.getMaxHealth(), Integer.class);
      this.setSkinVariant(variant);
      this.speed = 0.1F;
      this.flySpeed = 0.02F;
   }

   @Override
   protected void defineSynchedData() {
   }

   public boolean canEntityBeSeen(Entity entity) {
      return this.world
            .checkBlockCollisionBetweenPoints(
               Vec3.getTempVec3(this.x, this.y + this.getHeadHeight(), this.z), Vec3.getTempVec3(entity.x, entity.y + entity.getHeadHeight(), entity.z)
            )
         == null;
   }

   @Override
   public String getEntityTexture() {
      String basePath = String.format("/assets/%s/textures/entity/%s/", this.textureIdentifier.namespace(), this.textureIdentifier.value());
      return basePath + this.getTextureReference() + ".png";
   }

   public String getTextureReference() {
      SkinVariantList variantList = Global.accessor.getSkinVariantList();
      String basePath = String.format("/assets/%s/textures/entity/%s/", this.textureIdentifier.namespace(), this.textureIdentifier.value());
      return variantList.getSkinReference(basePath + "variants.json", "0", this.getSkinVariant());
   }

   public boolean cycleVariant() {
      String basePath = String.format("/assets/%s/textures/entity/%s/", this.textureIdentifier.namespace(), this.textureIdentifier.value());
      int skinVar = this.getSkinVariant();
      this.setSkinVariant(Global.accessor.getSkinVariantList().nextSkinVariant(basePath + "variants.json", skinVar));
      return skinVar != this.getSkinVariant();
   }

   public int getSkinVariant() {
      return Byte.toUnsignedInt(this.entityData.getByte(1));
   }

   public void setSkinVariant(int skinVariant) {
      this.entityData.set(1, (byte)skinVariant);
   }

   @NotNull
   public String getDefaultEntityTexture() {
      return String.format("/assets/%s/textures/entity/%s/0.png", this.textureIdentifier.namespace(), this.textureIdentifier.value());
   }

   @Override
   public boolean isPickable() {
      return !this.removed;
   }

   @Override
   public boolean isPushable() {
      return !this.removed;
   }

   @Override
   public float getHeadHeight() {
      return this.bbHeight * 0.85F;
   }

   public int getAmbientSoundInterval() {
      return 80;
   }

   @NotNull
   public String getDisplayName() {
      return TextFormatting.get(this.chatColor) + this.nickname;
   }

   @Override
   public boolean interact(@NotNull Player player) {
      ItemStack item = player.inventory.getCurrentItem();
      if (item != null && item.itemID == Items.LABEL.id && item.hasCustomName()) {
         if (item.hasCustomColor()) {
            this.chatColor = item.getCustomColor();
         } else {
            this.chatColor = 0;
         }

         this.setNickname(StringUtils.substring(item.getCustomName(), 0, 32));
         return false;
      } else {
         return false;
      }
   }

   protected void onLabelled() {
   }

   public void setNickname(@NotNull String nickname) {
      this.nickname = nickname;
      this.hadNicknameSet = true;
      this.onLabelled();
   }

   public void playLivingSound() {
      if (this.world != null) {
         String s = this.getLivingSound();
         if (s != null && !this.world.isClientSide) {
            this.world.playSoundAtEntity(null, this, s, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         }
      }
   }

   public void playHurtSound() {
      if (this.world != null) {
         this.world
            .playSoundAtEntity(null, this, this.getHurtSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
      }
   }

   public void playDeathSound() {
      if (this.world != null) {
         this.world
            .playSoundAtEntity(null, this, this.getDeathSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
      }
   }

   @Override
   public void baseTick() {
      this.prevSwingProgress = this.swingProgress;
      super.baseTick();
      if (this.world != null) {
         if (this.random.nextInt(1000) < this.livingSoundTime++) {
            this.livingSoundTime = -this.getAmbientSoundInterval();
            this.playLivingSound();
         }

         if (this.isAlive() && this.isInWall() && !this.noPhysics) {
            this.hurt(null, 1, null);
         }

         if (this.fireImmune || this.world.isClientSide) {
            this.remainingFireTicks = 0;
         }

         this.trySuffocate();
         this.prevCameraPitch = this.cameraPitch;
         if (this.attackTime > 0) {
            this.attackTime--;
         }

         if (this.hurtTime > 0) {
            this.hurtTime--;
         }

         if (this.heartsFlashTime > 0) {
            this.heartsFlashTime--;
         }

         if (this.getHealth() <= 0) {
            this.deathTime++;
            if (this.deathTime > 20) {
               this.beforeRemove();
               this.remove();

               for (int j = 0; j < 20; j++) {
                  double motionX = this.random.nextGaussian() * 0.02;
                  double motionY = this.random.nextGaussian() * 0.02;
                  double motionZ = this.random.nextGaussian() * 0.02;
                  this.world
                     .spawnParticle(
                        "explode",
                        this.x + this.random.nextFloat() * this.bbWidth * 2.0F - this.bbWidth,
                        this.y + this.random.nextFloat() * this.bbHeight,
                        this.z + this.random.nextFloat() * this.bbWidth * 2.0F - this.bbWidth,
                        motionX,
                        motionY,
                        motionZ,
                        0
                     );
               }
            }
         }

         List<ItemFood> finishedFood = new ArrayList<>();

         for (Entry<ItemFood, ConsumedFood> entry : this.consumedFood.entrySet()) {
            entry.getValue().tick();
            if (entry.getValue().isFinished()) {
               finishedFood.add(entry.getKey());
            }
         }

         for (ItemFood food : finishedFood) {
            this.consumedFood.remove(food);
         }

         this.prevRotationUnused = this.unusedRotation1;
         this.yBodyRotO = this.yBodyRot;
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
      }
   }

   public void trySuffocate() {
      if (this.world != null) {
         if (this.isAlive() && this.isUnderLiquid(Material.water) && !this.canBreatheUnderwater()) {
            this.airSupply--;
            if (this.airSupply == -20) {
               this.airSupply = 0;

               for (int i = 0; i < 8; i++) {
                  double offX = this.random.nextFloat() - this.random.nextFloat();
                  double offY = this.random.nextFloat() - this.random.nextFloat();
                  double offZ = this.random.nextFloat() - this.random.nextFloat();
                  this.world.spawnParticle("bubble", this.x + offX, this.y + offY, this.z + offZ, this.xd, this.yd, this.zd, 0);
               }

               this.hurt(null, 2, DamageType.DROWN);
            }

            this.remainingFireTicks = 0;
         } else {
            this.airSupply = this.airMaxSupply;
         }
      }
   }

   public void spawnExplosionParticle() {
      if (this.world != null) {
         for (int i = 0; i < 20; i++) {
            double d = this.random.nextGaussian() * 0.02;
            double d1 = this.random.nextGaussian() * 0.02;
            double d2 = this.random.nextGaussian() * 0.02;
            double d3 = 10.0;
            this.world
               .spawnParticle(
                  "explode",
                  this.x + this.random.nextFloat() * this.bbWidth * 2.0F - this.bbWidth - d * d3,
                  this.y + this.random.nextFloat() * this.bbHeight - d1 * d3,
                  this.z + this.random.nextFloat() * this.bbWidth * 2.0F - this.bbWidth - d2 * d3,
                  d,
                  d1,
                  d2,
                  0
               );
         }
      }
   }

   @Override
   public void rideTick() {
      super.rideTick();
      this.ridingRotUnused = this.prevRidingRotUnused;
      this.prevRidingRotUnused = 0.0F;
   }

   @Override
   public void lerpTo(double x, double y, double z, float yRot, float xRot, int i) {
      this.heightOffset = 0.0F;
      this.newPosX = x;
      this.newPosY = y;
      this.newPosZ = z;
      this.newRotationYaw = yRot;
      this.newRotationPitch = xRot;
      this.newPosRotationIncrements = i;
   }

   @Override
   public void tick() {
      super.tick();
      this.onLivingUpdate();
      double d = this.x - this.xo;
      double d1 = this.z - this.zo;
      float f = MathHelper.sqrt(d * d + d1 * d1);
      float f1 = this.yBodyRot;
      float f2 = 0.0F;
      this.ridingRotUnused = this.prevRidingRotUnused;
      float f3 = 0.0F;
      if (f > 0.05F) {
         f3 = 1.0F;
         f2 = f * 3.0F;
         f1 = (float)Math.atan2(d1, d) * 180.0F / (float) Math.PI - 90.0F;
      }

      if (this.swingProgress > 0.0F) {
         f1 = this.yRot;
      }

      if (!this.onGround) {
         f3 = 0.0F;
      }

      this.prevRidingRotUnused = this.prevRidingRotUnused + (f3 - this.prevRidingRotUnused) * 0.3F;
      float f4 = f1 - this.yBodyRot;

      while (f4 < -180.0F) {
         f4 += 360.0F;
      }

      while (f4 >= 180.0F) {
         f4 -= 360.0F;
      }

      this.yBodyRot += f4 * 0.3F;
      float f5 = this.yRot - this.yBodyRot;

      while (f5 < -180.0F) {
         f5 += 360.0F;
      }

      while (f5 >= 180.0F) {
         f5 -= 360.0F;
      }

      boolean flag = f5 < -90.0F || f5 >= 90.0F;
      if (f5 < -75.0F) {
         f5 = -75.0F;
      }

      if (f5 >= 75.0F) {
         f5 = 75.0F;
      }

      this.yBodyRot = this.yRot - f5;
      if (f5 * f5 > 2500.0F) {
         this.yBodyRot += f5 * 0.2F;
      }

      if (flag) {
         f2 *= -1.0F;
      }

      while (this.yRot - this.yRotO < -180.0F) {
         this.yRotO -= 360.0F;
      }

      while (this.yRot - this.yRotO >= 180.0F) {
         this.yRotO += 360.0F;
      }

      while (this.yBodyRot - this.yBodyRotO < -180.0F) {
         this.yBodyRotO -= 360.0F;
      }

      while (this.yBodyRot - this.yBodyRotO >= 180.0F) {
         this.yBodyRotO += 360.0F;
      }

      while (this.xRot - this.xRotO < -180.0F) {
         this.xRotO -= 360.0F;
      }

      while (this.xRot - this.xRotO >= 180.0F) {
         this.xRotO += 360.0F;
      }

      this.unusedRotation1 += f2;
   }

   @Override
   protected void setSize(float width, float height) {
      super.setSize(width, height);
   }

   public void heal(int i) {
      if (this.getHealth() > 0) {
         this.setHealthRaw(this.getHealth() + i);
         if (this.getHealth() > this.getMaxHealth()) {
            this.setHealthRaw(this.getMaxHealth());
         }

         this.heartsFlashTime = this.heartsHalvesLife / 2;
      }
   }

   public int getHealth() {
      return this.entityData.getInt(2);
   }

   public void setHealthRaw(int health) {
      this.entityData.set(2, health);
   }

   public int getMaxHealth() {
      return 10;
   }

   public int getTotalHealingRemaining() {
      int totalHealingRemaining = 0;

      for (ConsumedFood value : this.consumedFood.values()) {
         totalHealingRemaining += value.getHealRemaining();
      }

      return totalHealingRemaining;
   }

   public void eatFood(ItemFood food) {
      if (food != null) {
         if (food.getTicksPerHeal() == 0 || this.world.getGameRuleValue(GameRules.INSTANT_HEALING)) {
            this.heal(food.getHealAmount());
         } else if (this.consumedFood.containsKey(food)) {
            this.consumedFood.get(food).addFood();
         } else {
            this.consumedFood.put(food, new ConsumedFood(this, food));
         }
      }
   }

   @Override
   public boolean hurt(Entity attacker, int damage, DamageType type) {
      if (this.world.isClientSide) {
         return false;
      } else {
         this.entityAge = 0;
         if (this.getHealth() <= 0) {
            return false;
         } else {
            this.walkAnimSpeed = 1.5F;
            boolean flag = true;
            if (this.heartsFlashTime > this.heartsHalvesLife / 2.0F) {
               if (damage <= this.lastDamage) {
                  return false;
               }

               this.damageEntity(damage - this.lastDamage, type);
               this.lastDamage = damage;
               flag = false;
            } else {
               this.lastDamage = damage;
               this.prevHealth = this.getHealth();
               this.prevBonusHealth = this.bonusHealth;
               this.heartsFlashTime = this.heartsHalvesLife;
               this.damageEntity(damage, type);
               if (damage > 0) {
                  this.hurtTime = this.maxHurtTime = 10;
               }
            }

            this.attackedAtYaw = 0.0F;
            if (flag) {
               this.markHurt();
               if (attacker == null) {
                  this.attackedAtYaw = (int)(Math.random() * 2.0) * 180;
               } else {
                  double d = attacker.x - this.x;

                  double d1;
                  for (d1 = attacker.z - this.z; d * d + d1 * d1 < 1.0E-4; d1 = (Math.random() - Math.random()) * 0.01) {
                     d = (Math.random() - Math.random()) * 0.01;
                  }

                  this.attackedAtYaw = (float)(Math.atan2(d1, d) * 180.0 / Math.PI) - this.yRot;
                  this.knockBack(attacker, damage, d, d1);
               }

               this.world.sendTrackedEntityStatusUpdatePacket(this, (byte)2, this.attackedAtYaw);
            }

            if (this.getHealth() <= 0) {
               if (flag) {
                  this.playDeathSound();
               }

               this.onDeath(attacker);
            } else if (flag && damage > 0) {
               this.playHurtSound();
            }

            return true;
         }
      }
   }

   @Override
   public void animateHurt() {
      this.hurtTime = this.maxHurtTime = 10;
      this.attackedAtYaw = 0.0F;
   }

   protected void damageEntity(int i, DamageType damageType) {
      this.setHealthRaw(this.getHealth() - i);
   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   @Nullable
   public String getLivingSound() {
      return null;
   }

   protected String getHurtSound() {
      return "random.hurt";
   }

   protected String getDeathSound() {
      return "random.hurt";
   }

   public void knockBack(Entity entity, int i, double d, double d1) {
      float f = MathHelper.sqrt(d * d + d1 * d1);
      float f1 = 0.4F;
      this.xd /= 2.0;
      this.yd /= 2.0;
      this.zd /= 2.0;
      this.xd -= d / f * f1;
      this.yd += 0.4F;
      this.zd -= d1 / f * f1;
      if (this.yd > 0.4F) {
         this.yd = 0.4F;
      }
   }

   public void onDeath(Entity entityKilledBy) {
      if (this.scoreValue >= 0 && entityKilledBy != null) {
         entityKilledBy.awardKillScore(this, this.scoreValue);
      }

      if (entityKilledBy != null) {
         entityKilledBy.killed(this);
      }

      this.dead = true;
      if (!this.world.isClientSide) {
         this.dropDeathItems();
      }

      if (this.vehicle != null) {
         this.vehicle.ejectRider();
         this.vehicle = null;
      }

      this.world.sendTrackedEntityStatusUpdatePacket(this, (byte)3);
      if (this.sendDeathMessage(entityKilledBy)) {
         this.world.sendGlobalMessage(this.getDeathMessage(entityKilledBy));
      }
   }

   public boolean sendDeathMessage(Entity entityKilledBy) {
      return !this.nickname.isEmpty();
   }

   public String getDeathMessage(Entity entityKilledBy) {
      TextFormatting deathMsgColor = TextFormatting.RED;
      TextFormatting reset = TextFormatting.RESET;
      String name = Entity.getNameFromEntity(this, true);
      if (entityKilledBy != null) {
         String murdererName = Entity.getNameFromEntity(entityKilledBy, true);
         return String.format("%s%s was killed by %s%s", name, deathMsgColor, reset, murdererName);
      } else if (this.world.isMaterialInBB(this.bb, Material.lava)) {
         return String.format("%s%s swam in lava.", name, deathMsgColor);
      } else if (this.world.getBlockId(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z)) == Blocks.SPIKES.id()) {
         return String.format("%s%s got impaled.", name, deathMsgColor);
      } else if (this.fallDistance > 0.0F) {
         return String.format("%s%s broke their legs.", name, deathMsgColor);
      } else if (this.airSupply <= 0) {
         return String.format("%s%s suffocated.", name, deathMsgColor);
      } else {
         return this.remainingFireTicks > 0 ? String.format("%s%s was incinerated.", name, deathMsgColor) : String.format("%s%s died", name, deathMsgColor);
      }
   }

   protected void dropDeathItems() {
      List<WeightedRandomLootObject> drops = this.getMobDrops();
      if (drops != null) {
         for (WeightedRandomLootObject lootObject : drops) {
            ItemStack stack = lootObject.getItemStack();
            if (stack != null) {
               for (int i = 0; i < stack.stackSize; i++) {
                  this.dropItem(new ItemStack(stack.itemID, 1, stack.getMetadata(), stack.getData()), 0.0F);
               }
            }
         }
      }
   }

   protected List<WeightedRandomLootObject> getMobDrops() {
      return this.mobDrops;
   }

   @Override
   protected void causeFallDamage(float distance) {
      super.causeFallDamage(distance);
      int i = (int)Math.ceil(distance - 3.0F);
      if (i > 0) {
         this.hurt(null, i, DamageType.FALL);
         int j = this.world.getBlockId(MathHelper.floor(this.x), MathHelper.floor(this.y - 0.2 - this.heightOffset), MathHelper.floor(this.z));
         if (j > 0) {
            this.world.playBlockSoundEffect(this, this.x, this.y - this.heightOffset, this.z, Blocks.blocksList[j], EnumBlockSoundEffectType.ENTITY_LAND);
         }
      }
   }

   public void setFlySpeed(float f) {
      this.noclipSpeed = f;
   }

   public void setFlightSmoothness(float flightSmoothness) {
      this.flightSmoothness = flightSmoothness;
   }

   public boolean canSkate() {
      return false;
   }

   public void moveEntityWithHeading(float moveStrafing, float moveForward) {
      int floorBlockId = this.world.getBlockId(MathHelper.floor(this.x), MathHelper.floor(this.bb.minY) - 1, MathHelper.floor(this.z));
      this.muteStepSounds = false;
      this.isSkating = false;
      if (this.noPhysics) {
         int vertical = 0;
         if (this.isSneaking()) {
            vertical--;
         }

         if (this.isJumping) {
            vertical++;
         }

         float adjustedSpeed = this.noclipSpeed;
         float flySpeed = MathHelper.lerp(0.5F, 0.1F, this.flightSmoothness) * 2.0F * adjustedSpeed;
         float verticalFlySpeed = MathHelper.lerp(0.3F, 0.15F, this.flightSmoothness)
            * 4.0F
            * (adjustedSpeed > 0.25F ? (adjustedSpeed - 0.25F) * 0.5F + 0.25F : 0.25F);
         this.yd += verticalFlySpeed * vertical;
         this.moveRelative(moveStrafing, moveForward, flySpeed);
         this.move(this.xd, this.yd, this.zd);
         float mult = MathHelper.lerp(0.5F, 0.91F, this.flightSmoothness);
         float verticalMult = MathHelper.lerp(0.4F, 0.6F, this.flightSmoothness);
         this.xd *= mult;
         this.yd *= verticalMult;
         this.zd *= mult;
      } else if (this.isInWater()) {
         double d = this.y;
         this.moveRelative(moveStrafing, moveForward, 0.02F);
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.8;
         this.yd *= 0.8;
         this.zd *= 0.8;
         this.yd -= 0.02;
         int blockX = MathHelper.floor(this.x);
         int blockY = MathHelper.floor(this.y - this.heightOffset);
         int blockZ = MathHelper.floor(this.z);
         Block<?> b = this.world.getBlock(blockX, blockY, blockZ);
         float depth = 0.6F;
         if (b != null) {
            depth = 1.0F - (float)b.getBlockBoundsFromState(this.world, blockX, blockY, blockZ).maxY;
         }

         if (this.horizontalCollision && this.isFree(this.xd, this.yd + depth + 0.2 - this.y + d, this.zd)) {
            this.yd = 0.3;
         }
      } else if (this.isInLava()) {
         double d1 = this.y;
         this.moveRelative(moveStrafing, moveForward, 0.02F);
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.5;
         this.yd *= 0.5;
         this.zd *= 0.5;
         this.yd -= 0.02;
         if (this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6 - this.y + d1, this.zd)) {
            this.yd = 0.3;
         }
      } else if (this.canSkate() && Blocks.blocksList[floorBlockId] != null && Blocks.blocksList[floorBlockId].hasTag(BlockTags.SKATEABLE)) {
         this.muteStepSounds = true;
         this.isSkating = true;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= 0.99;
         this.zd *= 0.99;
         this.yd -= 0.08;
         this.yd *= 0.98;
         double speedSquared = this.xd * this.xd + this.zd * this.zd;
         if (Math.abs(this.xd) < 0.001) {
            this.xd = 0.0;
         }

         if (Math.abs(this.zd) < 0.001) {
            this.zd = 0.0;
         }

         if (this.isSneaking()) {
            this.xd *= 0.8;
            this.zd *= 0.8;
            this.world
               .playSoundAtEntity(
                  null,
                  this,
                  "random.skate",
                  (float)Math.sqrt(speedSquared) * 2.0F + this.random.nextFloat() * 0.2F - 0.25F,
                  1.75F + (this.random.nextFloat() * 0.4F - 0.2F)
               );
         }

         if (this.lastStrafe < 0.0F && moveStrafing > 0.0F || this.lastStrafe > 0.0F && moveStrafing < 0.0F) {
            this.moveRelative(moveStrafing * 0.1F, 0.0F, 0.125F);
            float speedBoost = 0.025F;
            float maxSpeed = 0.5F;
            float dx = MathHelper.sin(this.yRot * (float) Math.PI / 180.0F);
            float dz = MathHelper.cos(this.yRot * (float) Math.PI / 180.0F);
            double newMotionX = this.xd - speedBoost * dx;
            double newMotionZ = this.zd + speedBoost * dz;
            if (newMotionX * newMotionX + newMotionZ * newMotionZ < maxSpeed * maxSpeed) {
               this.xd = newMotionX;
               this.zd = newMotionZ;
            }

            this.world
               .playSoundAtEntity(
                  null,
                  this,
                  "random.skate",
                  (float)Math.sqrt(speedSquared) * 0.75F + this.random.nextFloat() * 0.2F,
                  2.0F + (this.random.nextFloat() * 0.4F - 0.2F)
               );
         }

         if (moveStrafing != 0.0F) {
            this.lastStrafe = moveStrafing;
         }
      } else {
         float movementScale = 0.91F;
         if (this.onGround) {
            movementScale = 0.5460001F;
            if (floorBlockId > 0) {
               movementScale = Blocks.blocksList[floorBlockId].friction * 0.91F;
            }
         }

         float f3 = 0.1627714F / (movementScale * movementScale * movementScale);
         this.moveRelative(moveStrafing, moveForward, this.onGround ? this.speed * f3 : this.flySpeed);
         if (this.canClimb()) {
            float f4 = 0.15F;
            if (this.xd < -f4) {
               this.xd = -f4;
            }

            if (this.xd > f4) {
               this.xd = f4;
            }

            if (this.zd < -f4) {
               this.zd = -f4;
            }

            if (this.zd > f4) {
               this.zd = f4;
            }

            this.fallDistance = 0.0F;
            if (this.yd < -0.25) {
               this.yd = -0.25;
            }

            if (this.isSneaking() && this.yd < 0.0) {
               this.yd = 0.0;
            }
         }

         this.move(this.xd, this.yd, this.zd);
         if ((this.horizontalCollision || this.isJumping) && this.canClimb()) {
            this.yd = 0.25;
         }

         if (!this.onGround) {
            movementScale = MathHelper.lerp(movementScale, 1.0F, this.pushTime);
         }

         this.yd -= 0.08;
         this.yd *= 0.98;
         this.xd *= movementScale;
         this.zd *= movementScale;
      }

      this.walkAnimSpeedO = this.walkAnimSpeed;
      double d2 = this.x - this.xo;
      double d3 = this.z - this.zo;
      float f5 = MathHelper.sqrt(d2 * d2 + d3 * d3) * 4.0F;
      if (f5 > 1.0F) {
         f5 = 1.0F;
      }

      this.walkAnimSpeed = this.walkAnimSpeed + (f5 - this.walkAnimSpeed) * 0.4F;
      this.walkAnimPos = this.walkAnimPos + this.walkAnimSpeed;
   }

   public boolean canClimb() {
      int i = MathHelper.floor(this.x);
      int j = MathHelper.floor(this.bb.minY);
      int k = MathHelper.floor(this.z);
      Block<?> block = this.world.getBlock(i, j, k);
      return block != null && block.isClimbable(this.world, i, j, k);
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      tag.putShort("Health", (short)this.getHealth());
      tag.putShort("HurtTime", (short)this.hurtTime);
      tag.putShort("DeathTime", (short)this.deathTime);
      tag.putShort("AttackTime", (short)this.attackTime);
      tag.putString("Nickname", this.nickname);
      tag.putByte("ChatColor", this.chatColor);
      tag.putByte("SkinVariant", this.entityData.getByte(1));
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      this.setHealthRaw(tag.getShort("Health"));
      if (!tag.containsKey("Health")) {
         this.setHealthRaw(this.getMaxHealth());
      }

      this.hurtTime = tag.getShort("HurtTime");
      this.deathTime = tag.getShort("DeathTime");
      this.attackTime = tag.getShort("AttackTime");
      this.nickname = StringUtils.substring(tag.getString("Nickname"), 0, 32);
      this.chatColor = tag.getByte("ChatColor");
      this.setSkinVariant(tag.getByte("SkinVariant"));
   }

   @Override
   public boolean isAlive() {
      return !this.removed && this.getHealth() > 0;
   }

   public boolean canBreatheUnderwater() {
      return false;
   }

   public void onLivingUpdate() {
      if (this.world != null) {
         if (this.newPosRotationIncrements > 0) {
            double d = this.x + (this.newPosX - this.x) / this.newPosRotationIncrements;
            double d1 = this.y + (this.newPosY - this.y) / this.newPosRotationIncrements;
            double d2 = this.z + (this.newPosZ - this.z) / this.newPosRotationIncrements;
            double d3 = this.newRotationYaw - this.yRot;

            while (d3 < -180.0) {
               d3 += 360.0;
            }

            while (d3 >= 180.0) {
               d3 -= 360.0;
            }

            this.yRot = (float)(this.yRot + d3 / this.newPosRotationIncrements);
            this.xRot = (float)(this.xRot + (this.newRotationPitch - this.xRot) / this.newPosRotationIncrements);
            this.newPosRotationIncrements--;
            this.setPos(d, d1, d2);
            this.setRot(this.yRot, this.xRot);
            List<AABB> list1 = this.world.getCubes(this, this.bb.getInsetBoundingBox(0.03125, 0.0, 0.03125));
            if (!list1.isEmpty()) {
               double d4 = 0.0;

               for (int j = 0; j < list1.size(); j++) {
                  AABB aabb = list1.get(j);
                  if (aabb.maxY > d4) {
                     d4 = aabb.maxY;
                  }
               }

               d1 += d4 - this.bb.minY;
               this.setPos(d, d1, d2);
            }
         }

         if (this.isMovementBlocked()) {
            this.isJumping = false;
            this.moveStrafing = 0.0F;
            this.moveForward = 0.0F;
            this.randomYawVelocity = 0.0F;
         } else if (!this.isMultiplayerEntity) {
            this.updateAI();
         }

         boolean inWater = this.isInWater();
         boolean inLava = this.isInLava();
         if (this.isJumping) {
            if (inWater) {
               this.yd += 0.04;
            } else if (inLava) {
               this.yd += 0.04;
            } else if (this.onGround) {
               this.jump();
            }
         }

         this.moveStrafing *= 0.98F;
         this.moveForward *= 0.98F;
         this.randomYawVelocity *= 0.9F;
         this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
         List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb.grow(0.2, 0.0, 0.2));
         if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
               Entity entity = list.get(i);
               if (entity.isPushable()) {
                  entity.push(this);
               }
            }
         }
      }
   }

   protected boolean isMovementBlocked() {
      return this.getHealth() <= 0;
   }

   protected void jump() {
      if (!this.noPhysics) {
         this.yd = 0.42;
         if (this.isSprinting()) {
            float f = this.yRot * 0.01745329F;
            this.xd = this.xd - MathHelper.sin(f) * 0.2F;
            this.zd = this.zd + MathHelper.cos(f) * 0.2F;
         }
      }
   }

   protected boolean canDespawn() {
      return this.nickname.isEmpty();
   }

   public void tryToDespawn() {
      Player entityplayer = this.world.getClosestPlayerToEntity(this, -1.0);
      if (this.canDespawn() && entityplayer != null) {
         double d = entityplayer.x - this.x;
         double d1 = entityplayer.y - this.y;
         double d2 = entityplayer.z - this.z;
         double d3 = d * d + d1 * d1 + d2 * d2;
         if (d3 > 16384.0) {
            this.remove();
         }

         if (this.entityAge > 600 && this.random.nextInt(800) == 0) {
            if (d3 < 1024.0) {
               this.entityAge = 0;
            } else {
               this.remove();
            }
         }
      }
   }

   protected void updateAI() {
      this.entityAge++;
      this.tryToDespawn();
      this.moveStrafing = 0.0F;
      this.moveForward = 0.0F;
      float f = 8.0F;
      if (this.random.nextFloat() < 0.02F) {
         Player entityplayer1 = this.world.getClosestPlayerToEntity(this, f);
         if (entityplayer1 != null) {
            this.currentTarget = entityplayer1;
            this.numTicksToChaseTarget = 10 + this.random.nextInt(20);
         } else {
            this.randomYawVelocity = (this.random.nextFloat() - 0.5F) * 20.0F;
         }
      }

      if (this.currentTarget != null) {
         this.lookAt(this.currentTarget, 10.0F, this.getLookingTilt());
         if (this.numTicksToChaseTarget-- <= 0 || this.currentTarget.removed || this.currentTarget.distanceToSqr(this) > f * f) {
            this.currentTarget = null;
         }
      } else {
         if (this.random.nextFloat() < 0.05F) {
            this.randomYawVelocity = (this.random.nextFloat() - 0.5F) * 20.0F;
         }

         this.yRot = this.yRot + this.randomYawVelocity;
         this.xRot = this.defaultPitch;
      }

      boolean inWater = this.isInWater();
      boolean inLava = this.isInLava();
      if (inWater || inLava) {
         this.isJumping = this.random.nextFloat() < 0.8F;
      }
   }

   protected int getLookingTilt() {
      return 40;
   }

   public void lookAt(Entity entity, float yRot, float xRot) {
      double x = entity.x - this.x;
      double y = entity.z - this.z;
      double y2;
      if (entity instanceof Mob) {
         Mob mob = (Mob)entity;
         y2 = mob.y + mob.getHeadHeight() - (this.y + this.getHeadHeight());
      } else {
         y2 = (entity.bb.minY + entity.bb.maxY) / 2.0 - (this.y + this.getHeadHeight());
      }

      double d3 = MathHelper.sqrt(x * x + y * y);
      float f2 = (float)(Math.atan2(y, x) * 180.0 / Math.PI) - 90.0F;
      float f3 = (float)(-(Math.atan2(-y2, d3) * 180.0 / Math.PI));
      this.xRot = -this.rotationLerp(this.xRot, f3, xRot);
      this.yRot = this.rotationLerp(this.yRot, f2, yRot);
   }

   public boolean hasCurrentTarget() {
      return this.currentTarget != null;
   }

   public Entity getCurrentTarget() {
      return this.currentTarget;
   }

   private float rotationLerp(float f, float f1, float f2) {
      float f3 = f1 - f;

      while (f3 < -180.0F) {
         f3 += 360.0F;
      }

      while (f3 >= 180.0F) {
         f3 -= 360.0F;
      }

      if (f3 > f2) {
         f3 = f2;
      }

      if (f3 < -f2) {
         f3 = -f2;
      }

      return f + f3;
   }

   public void beforeRemove() {
   }

   public boolean canSpawnHere() {
      int blockX = MathHelper.floor(this.x);
      int blockY = MathHelper.floor(this.bb.minY);
      int blockZ = MathHelper.floor(this.z);
      return Blocks.hasTag(this.world.getBlockId(blockX, blockY, blockZ), BlockTags.PREVENT_MOB_SPAWNS)
         ? false
         : this.world.checkIfAABBIsClear(this.bb) && this.world.getCubes(this, this.bb).size() == 0 && !this.world.getIsAnyLiquid(this.bb);
   }

   @Override
   public void outOfWorld() {
      this.hurt(null, 4, null);
   }

   public float getSwingProgress(float partialTick) {
      float f1 = this.swingProgress - this.prevSwingProgress;
      if (f1 < 0.0F) {
         f1++;
      }

      return this.prevSwingProgress + f1 * partialTick;
   }

   public Vec3 getPosition(float partialTick, boolean headOffset) {
      if (partialTick == 1.0F) {
         return Vec3.getTempVec3(this.x, this.y + (headOffset ? this.getHeadHeight() : 0.0F), this.z);
      } else {
         double x = MathHelper.lerp(this.xo, this.x, (double)partialTick);
         double y = MathHelper.lerp(this.yo, this.y, (double)partialTick);
         double z = MathHelper.lerp(this.zo, this.z, (double)partialTick);
         return Vec3.getTempVec3(x, y + (headOffset ? this.getHeadHeight() : 0.0F), z);
      }
   }

   @Override
   public Vec3 getLookAngle() {
      return this.getViewVector(1.0F);
   }

   public Vec3 getViewVector(float partialTick) {
      if (partialTick == 1.0F) {
         float z = MathHelper.cos(-this.yRot * (float) (Math.PI / 180.0) - (float) Math.PI);
         float x = MathHelper.sin(-this.yRot * (float) (Math.PI / 180.0) - (float) Math.PI);
         float xzLen = -MathHelper.cos(-this.xRot * (float) (Math.PI / 180.0));
         float y = MathHelper.sin(-this.xRot * (float) (Math.PI / 180.0));
         return Vec3.getTempVec3(x * xzLen, y, z * xzLen);
      } else {
         float pitch = this.xRotO + (this.xRot - this.xRotO) * partialTick;
         float yaw = this.yRotO + (this.yRot - this.yRotO) * partialTick;
         float xzLen = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
         float x = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
         float y = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
         float z = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
         return Vec3.getTempVec3(x * xzLen, y, z * xzLen);
      }
   }

   public HitResult rayTrace(double distance, float partialTick, boolean collideWithFluids, boolean ignoreNonColliderBlocks) {
      Vec3 head = this.getPosition(partialTick, true);
      Vec3 viewDirection = this.getViewVector(partialTick);
      Vec3 furthestPoint = head.add(viewDirection.x * distance, viewDirection.y * distance, viewDirection.z * distance);
      return this.world.checkBlockCollisionBetweenPoints(head, furthestPoint, collideWithFluids, ignoreNonColliderBlocks, false);
   }

   public int getMaxSpawnedInChunk() {
      return 4;
   }

   @Nullable
   public ItemStack getHeldItem() {
      return null;
   }

   @Override
   public void handleEntityEvent(byte byte0, float attackedAtYaw) {
      if (byte0 == 2) {
         this.walkAnimSpeed = 1.5F;
         this.heartsFlashTime = this.heartsHalvesLife;
         this.hurtTime = this.maxHurtTime = 10;
         this.attackedAtYaw = attackedAtYaw;
         this.hurt(null, 0, null);
      } else if (byte0 == 3) {
         this.setHealthRaw(0);
         this.onDeath(null);
      } else {
         super.handleEntityEvent(byte0, attackedAtYaw);
      }
   }

   public boolean isPlayerSleeping() {
      return false;
   }

   public Direction getHorizontalPlacementDirection(Side side) {
      return this.getHorizontalPlacementDirection(side, PlacementMode.FACING);
   }

   public Direction getHorizontalPlacementDirection(Side side, PlacementMode mode) {
      if (this.rotationLockHorizontal != null && this.rotationLockHorizontal != Direction.NONE) {
         return this.rotationLockHorizontal;
      } else {
         if (this.placementModeOverride != PlacementMode.DEFAULT) {
            mode = this.placementModeOverride;
         }

         return mode == PlacementMode.SIDE && side != null && side.isHorizontal() ? side.getDirection().getOpposite() : Direction.getHorizontalDirection(this);
      }
   }

   public Direction getPlacementDirection(Side side) {
      return this.getPlacementDirection(side, PlacementMode.FACING);
   }

   public Direction getPlacementDirection(Side side, PlacementMode mode) {
      if (this.rotationLock != null && this.rotationLock != Direction.NONE) {
         return this.rotationLock;
      } else {
         if (this.placementModeOverride != PlacementMode.DEFAULT) {
            mode = this.placementModeOverride;
         }

         return mode == PlacementMode.SIDE && side != null ? side.getDirection() : Direction.getDirection(this);
      }
   }

   public Direction getVerticalPlacementDirection(Side side, double sideHeight) {
      return this.getVerticalPlacementDirection(side, sideHeight, PlacementMode.SIDE);
   }

   public Direction getVerticalPlacementDirection(Side side, double sideHeight, PlacementMode mode) {
      if (this.rotationLockVertical != null && this.rotationLockVertical != Direction.NONE) {
         return this.rotationLockVertical;
      } else {
         if (this.placementModeOverride != PlacementMode.DEFAULT) {
            mode = this.placementModeOverride;
         }

         if (mode == PlacementMode.SIDE) {
            if (side.isVertical()) {
               return side.getDirection().getOpposite();
            } else {
               return sideHeight > 0.5 ? Direction.UP : Direction.DOWN;
            }
         } else {
            return this.xRot < 0.0F ? Direction.UP : Direction.DOWN;
         }
      }
   }
}
