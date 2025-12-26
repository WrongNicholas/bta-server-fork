package net.minecraft.core.entity.animal;

import com.mojang.nbt.tags.CompoundTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.item.IArmorItem;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.material.ArmorMaterial;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.UUIDHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pathfinder.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MobWolf extends MobAnimal {
   public static final Map<ArmorMaterial, IArmorItem> ARMOR_MATERIALS = new HashMap<>();
   public static final int DATA_HELD_ITEM = 15;
   public static final int DATA_GENERIC_FLAGS = 16;
   public static final int MASK_SITTING = 1;
   public static final int MASK_ANGRY = 2;
   public static final int MASK_TAMED = 4;
   public static final int DATA_OWNER_UUID = 17;
   public static final int DATA_ARMOR_MATERIAL = 19;
   private boolean looksWithInterest;
   private float interestedAngle;
   private float interestedAngleOld;
   private boolean isWolfShaking;
   private boolean field_25052_g;
   private float timeWolfIsShaking;
   private float prevTimeWolfIsShaking;
   private ItemStack armor = null;

   public MobWolf(World world) {
      super(world);
      this.looksWithInterest = false;
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "wolf");
      this.setSize(0.8F, 0.8F);
      this.moveSpeed = 1.1F;
      this.scoreValue = 500;
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(15, null, ItemStack.class);
      this.entityData.define(16, (byte)0, Byte.class);
      this.entityData.define(17, null, UUID.class);
      this.entityData.define(19, "", String.class);
   }

   @Override
   public boolean sendDeathMessage(Entity entityKilledBy) {
      return super.sendDeathMessage(entityKilledBy) || this.isWolfTamed();
   }

   @Override
   public void spawnInit() {
      if (this.random.nextInt(25) == 0) {
         this.setWolfHeldItem(new ItemStack(Items.RECORD_DOG, 1));
      }
   }

   @Nullable
   public ArmorMaterial getArmorMaterial() {
      ArmorMaterial material = null;
      String name = this.entityData.getString(19);

      for (ArmorMaterial mat : ArmorMaterial.getArmorMaterials()) {
         if (mat.identifier.toString().equalsIgnoreCase(name)) {
            material = mat;
            break;
         }
      }

      return material;
   }

   public void setArmor(ItemStack stack) {
      ArmorMaterial material = null;
      this.armor = stack;
      if (stack != null && stack.getItem() instanceof IArmorItem && ARMOR_MATERIALS.containsValue(stack.getItem())) {
         material = ((IArmorItem)stack.getItem()).getArmorMaterial();
      }

      if (material == null) {
         this.entityData.set(19, "");
      } else {
         this.entityData.set(19, material.identifier.toString());
      }
   }

   @Override
   protected boolean makeStepSound() {
      return false;
   }

   @Override
   public String getEntityTexture() {
      if (this.isWolfTamed()) {
         return "/assets/minecraft/textures/entity/wolf_tame/" + this.getTextureReference() + ".png";
      } else {
         return this.isWolfAngry() ? "/assets/minecraft/textures/entity/wolf_angry/" + this.getTextureReference() + ".png" : super.getEntityTexture();
      }
   }

   @NotNull
   @Override
   public String getDefaultEntityTexture() {
      if (this.isWolfTamed()) {
         return "/assets/minecraft/textures/entity/wolf_tame/0.png";
      } else {
         return this.isWolfAngry() ? "/assets/minecraft/textures/entity/wolf_angry/0.png" : super.getDefaultEntityTexture();
      }
   }

   @Override
   public boolean collidesWith(Entity entity) {
      if (!(entity instanceof Player)) {
         return true;
      } else {
         Player player = (Player)entity;
         return !player.uuid.equals(this.getWolfOwner()) || this.isWolfSitting();
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putBoolean("Angry", this.isWolfAngry());
      tag.putBoolean("Sitting", this.isWolfSitting());
      if (this.getHeldItem() != null) {
         CompoundTag heldItemNBT = new CompoundTag();
         this.getHeldItem().writeToNBT(heldItemNBT);
         tag.putCompound("HeldItem", heldItemNBT);
      }

      if (this.getArmorMaterial() != null) {
         CompoundTag armorTag = new CompoundTag();
         this.armor.writeToNBT(armorTag);
         tag.putCompound("Armor", armorTag);
      }

      UUIDHelper.writeToTag(tag, this.getWolfOwner(), "OwnerUUID");
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.setWolfAngry(tag.getBoolean("Angry"));
      this.setWolfSitting(tag.getBoolean("Sitting"));
      CompoundTag heldItemStack = tag.getCompound("HeldItem");
      if (heldItemStack != null) {
         ItemStack stack = ItemStack.readItemStackFromNbt(heldItemStack);
         if (stack != null) {
            this.setWolfHeldItem(stack);
         }
      }

      if (tag.containsKey("Armor")) {
         ArmorMaterial material = null;
         String name = tag.getString("Armor");
         if (!name.isEmpty()) {
            for (ArmorMaterial mat : ArmorMaterial.getArmorMaterials()) {
               if (mat.identifier.toString().equalsIgnoreCase(name)) {
                  material = mat;
                  break;
               }
            }

            Item item = ARMOR_MATERIALS.get(material).asItem();
            this.setArmor(item.getDefaultStack());
         } else {
            CompoundTag armorTag = tag.getCompound("Armor");
            ItemStack armor = ItemStack.readItemStackFromNbt(armorTag);
            this.setArmor(armor);
         }
      }

      UUID ownerUUID = UUIDHelper.readFromTag(tag, "OwnerUUID");
      if (ownerUUID == null) {
         String s = tag.getString("Owner");
         if (!s.isEmpty()) {
            UUIDHelper.runConversionAction(s, uuid -> {
               this.setWolfOwner(uuid);
               this.setWolfTamed(true);
            }, null);
         }
      } else {
         this.setWolfOwner(ownerUUID);
         this.setWolfTamed(true);
      }
   }

   @Override
   protected boolean canDespawn() {
      return !this.isWolfTamed() && super.canDespawn();
   }

   @Override
   public String getLivingSound() {
      if (this.isWolfAngry()) {
         return "mob.wolf.growl";
      } else if (this.random.nextInt(3) == 0) {
         return this.isWolfTamed() && this.getHealth() < this.getMaxHealth() / 2 ? "mob.wolf.whine" : "mob.wolf.panting";
      } else {
         return "mob.wolf.bark";
      }
   }

   @Override
   protected String getHurtSound() {
      return "mob.wolf.hurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.wolf.death";
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
   protected void dropDeathItems() {
      if (this.getHeldItem() != null && this.getHeldItem().stackSize > 0) {
         this.dropItem(this.getHeldItem(), 0.0F);
         this.setWolfHeldItem(null);
      }

      if (this.armor != null && this.armor.stackSize > 0) {
         this.dropItem(this.armor, 0.0F);
         this.setArmor(null);
      }

      super.dropDeathItems();
   }

   @Override
   protected void updateAI() {
      super.updateAI();
      if (this.getTarget() instanceof EntityItem
         && (
            this.getTarget().isInWater()
               || this.getTarget().isInLava()
               || this.getTarget().isInWall()
               || !this.getTarget().onGround
               || this.getTarget().isRemoved()
         )) {
         this.setTarget(null);
      }

      if (this.isWolfSitting()) {
         this.setTarget(null);
      }

      if (!this.hasAttacked && !this.hasPath() && this.isWolfTamed() && this.vehicle == null) {
         Player owner = this.world.getPlayerEntityByUUID(this.getWolfOwner());
         if (owner != null) {
            float ownerDistance = owner.distanceTo(this);
            if (ownerDistance > 5.0F) {
               this.setPathToOwnerOrTeleport(owner, ownerDistance);
            }
         } else if (this.isInWater()) {
            this.setWolfSitting(false);
         }
      } else if (this.getTarget() == null && !this.hasPath() && !this.isWolfTamed() && this.world.rand.nextInt(100) == 0) {
         List<MobSheep> nearbySheep = this.world
            .getEntitiesWithinAABB(MobSheep.class, AABB.getTemporaryBB(this.x, this.y, this.z, this.x + 1.0, this.y + 1.0, this.z + 1.0).grow(16.0, 4.0, 16.0));
         if (!nearbySheep.isEmpty()) {
            this.setTarget(nearbySheep.get(this.world.rand.nextInt(nearbySheep.size())));
         }
      }

      if (this.getTarget() == null) {
         ItemStack heldItemSlot = this.getHeldItem();
         if (heldItemSlot == null || heldItemSlot.itemID <= 0) {
            List<EntityItem> triedItems = new ArrayList<>();
            List<EntityItem> nearbyItems = this.world
               .getEntitiesWithinAABB(
                  EntityItem.class, AABB.getTemporaryBB(this.x, this.y, this.z, this.x + 1.0, this.y + 1.0, this.z + 1.0).grow(16.0, 4.0, 16.0)
               );
            if (!nearbyItems.isEmpty()) {
               while (triedItems.size() != nearbyItems.size()) {
                  EntityItem item = nearbyItems.get(this.world.rand.nextInt(nearbyItems.size()));
                  if (!triedItems.contains(item)) {
                     if (!item.isInWater() && !item.isInLava() && !item.isInWall() && item.onGround && !item.isRemoved()) {
                        this.setTarget(item);
                        break;
                     }

                     triedItems.add(item);
                  }
               }
            }
         }
      }

      if (this.isInWater()) {
         this.setWolfSitting(false);
      }
   }

   @Override
   protected void checkForPlayerHoldingItem() {
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.looksWithInterest = false;
      if (this.hasCurrentTarget() && !this.hasPath() && !this.isWolfAngry()) {
         Entity entity = this.getCurrentTarget();
         if (entity instanceof Player) {
            Player entityplayer = (Player)entity;
            ItemStack itemstack = entityplayer.inventory.getCurrentItem();
            if (itemstack != null) {
               if (!this.isWolfTamed() && itemstack.itemID == Items.BONE.id) {
                  this.looksWithInterest = true;
               } else if (this.isWolfTamed() && Item.itemsList[itemstack.itemID] instanceof ItemFood) {
                  this.looksWithInterest = ((ItemFood)Item.itemsList[itemstack.itemID]).getIsWolfsFavoriteMeat();
               }
            }
         }
      }

      if (!this.isMultiplayerEntity && this.isWolfShaking && !this.field_25052_g && !this.hasPath() && this.onGround) {
         this.field_25052_g = true;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
         this.world.sendTrackedEntityStatusUpdatePacket(this, (byte)8);
      }
   }

   @Override
   public ItemStack getHeldItem() {
      return this.entityData.getItemStack(15);
   }

   @Override
   public void tick() {
      super.tick();
      List<Entity> entitiesNearWolf = this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb);
      if (entitiesNearWolf != null && !entitiesNearWolf.isEmpty() && this.isAlive()) {
         for (Entity entity : entitiesNearWolf) {
            if (entity instanceof EntityItem && this.getHeldItem() == null) {
               EntityItem entityItem = (EntityItem)entity;
               if (entityItem.item != null && entityItem.item.stackSize > 0 && entityItem.pickupDelay == 0) {
                  this.setWolfHeldItem(entityItem.item.copy());
                  entityItem.item.stackSize = 0;
                  entity.outOfWorld();
                  break;
               }
            }
         }
      }

      this.interestedAngleOld = this.interestedAngle;
      if (this.looksWithInterest) {
         this.interestedAngle = this.interestedAngle + (1.0F - this.interestedAngle) * 0.4F;
      } else {
         this.interestedAngle = this.interestedAngle + (0.0F - this.interestedAngle) * 0.4F;
      }

      if (this.looksWithInterest) {
         this.numTicksToChaseTarget = 10;
      }

      if (this.isInWaterOrRain()) {
         this.isWolfShaking = true;
         this.field_25052_g = false;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
      } else if ((this.isWolfShaking || this.field_25052_g) && this.field_25052_g) {
         if (this.timeWolfIsShaking == 0.0F) {
            this.world
               .playSoundAtEntity(null, this, "mob.wolf.shake", this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         }

         this.prevTimeWolfIsShaking = this.timeWolfIsShaking;
         this.timeWolfIsShaking += 0.05F;
         if (this.prevTimeWolfIsShaking >= 2.0F) {
            this.isWolfShaking = false;
            this.field_25052_g = false;
            this.prevTimeWolfIsShaking = 0.0F;
            this.timeWolfIsShaking = 0.0F;
         }

         if (this.timeWolfIsShaking > 0.4F) {
            float f = (float)this.bb.minY;
            int i = (int)(MathHelper.sin((this.timeWolfIsShaking - 0.4F) * (float) Math.PI) * 7.0F);

            for (int j = 0; j < i; j++) {
               double offX = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth * 0.5F;
               double offZ = (this.random.nextFloat() * 2.0F - 1.0F) * this.bbWidth * 0.5F;
               this.world.spawnParticle("splash", this.x + offX, f + 0.8F, this.z + offZ, this.xd, this.yd, this.zd, 0);
            }
         }
      }
   }

   public boolean getWolfShaking() {
      return this.isWolfShaking;
   }

   public float getShadingWhileShaking(float f) {
      return 0.75F + (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * f) / 2.0F * 0.25F;
   }

   public float getShakeAngle(float f, float f1) {
      float f2 = (this.prevTimeWolfIsShaking + (this.timeWolfIsShaking - this.prevTimeWolfIsShaking) * f + f1) / 1.8F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      } else if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      return MathHelper.sin(f2 * (float) Math.PI) * MathHelper.sin(f2 * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
   }

   public float getInterestedAngle(float f) {
      return (this.interestedAngleOld + (this.interestedAngle - this.interestedAngleOld) * f) * 0.15F * (float) Math.PI;
   }

   @Override
   public float getHeadHeight() {
      return this.bbHeight * 0.8F;
   }

   @Override
   protected int getLookingTilt() {
      return this.isWolfSitting() ? 20 : super.getLookingTilt();
   }

   private void setPathToOwnerOrTeleport(Entity owner, float distance) {
      Path path = this.world.getPathToEntity(this, owner, 16.0F);
      if (path == null && distance > 12.0F) {
         int targetX = MathHelper.floor(owner.x);
         int targetY = MathHelper.floor(owner.bb.minY);
         int targetZ = MathHelper.floor(owner.z);
         byte searchRadius = 2;

         for (int _x = -2; _x <= 2; _x++) {
            for (int _z = -2; _z <= 2; _z++) {
               if ((Math.abs(_x) > 1 || Math.abs(_z) > 1)
                  && this.world.isBlockNormalCube(targetX + _x, targetY - 1, targetZ + _z)
                  && !this.world.isBlockNormalCube(targetX + _x, targetY, targetZ + _z)
                  && !this.world.isBlockNormalCube(targetX + _x, targetY + 1, targetZ + _z)) {
                  this.moveTo(targetX + _x + 0.5F, targetY, targetZ + _z + 0.5F, this.yRot, this.xRot);
                  this.fallDistance = 0.0F;
                  return;
               }
            }
         }
      } else {
         this.setPathToEntity(path);
      }
   }

   @Override
   protected boolean isMovementCeased() {
      return this.isWolfSitting() || this.field_25052_g;
   }

   @Override
   public boolean hurt(Entity attacker, int i, DamageType type) {
      this.setWolfSitting(false);
      if (attacker != null && !(attacker instanceof Player) && !(attacker instanceof ProjectileArrow)) {
         i = (i + 1) / 2;
      }

      if (!super.hurt(attacker, i, type)) {
         return false;
      } else {
         if (!this.isWolfTamed() && !this.isWolfAngry()) {
            if (attacker instanceof Player) {
               this.setWolfAngry(true);
               this.target = attacker;
            }

            if (attacker instanceof ProjectileArrow && ((ProjectileArrow)attacker).owner != null) {
               attacker = ((ProjectileArrow)attacker).owner;
            }

            if (attacker instanceof Mob) {
               for (MobWolf mobWolf : this.world
                  .getEntitiesWithinAABB(
                     MobWolf.class, AABB.getTemporaryBB(this.x, this.y, this.z, this.x + 1.0, this.y + 1.0, this.z + 1.0).grow(16.0, 4.0, 16.0)
                  )) {
                  if (!mobWolf.isWolfTamed() && mobWolf.target == null) {
                     mobWolf.target = attacker;
                     if (attacker instanceof Player) {
                        mobWolf.setWolfAngry(true);
                     }
                  }
               }
            }
         } else if (attacker != this && attacker != null) {
            if (this.isWolfTamed() && attacker instanceof Player && ((Player)attacker).uuid.equals(this.getWolfOwner())) {
               return true;
            }

            this.target = attacker;
         }

         return true;
      }
   }

   @Override
   protected Entity findPlayerToAttack() {
      return this.isWolfAngry() ? this.world.getClosestPlayerToEntity(this, 16.0) : null;
   }

   @Override
   protected void attackEntity(@NotNull Entity entity, float distance) {
      if (!(entity instanceof EntityItem) && entity != this.world.getPlayerEntityByUUID(this.getWolfOwner())) {
         if (!(distance > 2.0F) || !(distance < 6.0F) || this.random.nextInt(10) != 0) {
            if (distance < 1.5 && entity.bb.maxY > this.bb.minY && entity.bb.minY < this.bb.maxY) {
               this.attackTime = 20;
               byte damage = 2;
               if (this.isWolfTamed()) {
                  damage = 4;
               }

               entity.hurt(this, damage, DamageType.COMBAT);
            }
         } else if (this.onGround) {
            double dx = entity.x - this.x;
            double dz = entity.z - this.z;
            float targetDistance = MathHelper.sqrt(dx * dx + dz * dz);
            this.xd = dx / targetDistance * 0.5 * 0.8 + this.xd * 0.2;
            this.zd = dz / targetDistance * 0.5 * 0.8 + this.zd * 0.2;
            this.yd = 0.4;
         }
      }
   }

   @Override
   protected void damageEntity(int damage, DamageType damageType) {
      if (this.getArmorMaterial() == null) {
         super.damageEntity(damage, damageType);
      } else {
         float protection = 1.0F - this.getArmorMaterial().getProtection(damageType);
         protection = Math.max(protection, 0.01F);
         double d = damage * protection;
         int newDamage = (int)(this.random.nextFloat() > 0.5 ? Math.floor(d) : Math.ceil(d));
         super.damageEntity(newDamage, damageType);
      }
   }

   @Override
   public void lavaHurt() {
      if (!this.fireImmune) {
         float protection = 0.0F;
         ArmorMaterial material;
         if ((material = this.getArmorMaterial()) != null) {
            protection = 1.0F - material.getProtection(DamageType.FIRE);
         }

         protection = Math.max(protection, 0.01F);
         this.hurt(null, 4, DamageType.FIRE);
         this.remainingFireTicks = (int)(80.0F + 520.0F * protection);
         this.maxFireTicks = this.remainingFireTicks;
      }
   }

   @Override
   public void fireHurt() {
      if (!this.fireImmune) {
         float protection = 0.0F;
         ArmorMaterial material;
         if ((material = this.getArmorMaterial()) != null) {
            protection = 1.0F - material.getProtection(DamageType.FIRE);
         }

         protection = Math.max(protection, 0.01F);
         this.hurt(null, 1, DamageType.FIRE);
         this.remainingFireTicks = (int)(40.0F + 260.0F * protection);
         this.maxFireTicks = this.remainingFireTicks;
      }
   }

   @Override
   public boolean interact(@NotNull Player player) {
      if (super.interact(player)) {
         return true;
      } else {
         ItemStack itemstack = player.inventory.getCurrentItem();
         if (!this.isWolfTamed()) {
            if (itemstack != null && itemstack.itemID == Items.BONE.id && !this.isWolfAngry()) {
               itemstack.consumeItem(player);
               if (itemstack.stackSize <= 0) {
                  player.inventory.setItem(player.inventory.getCurrentItemIndex(), null);
               }

               if (!this.world.isClientSide) {
                  if (this.random.nextInt(3) == 0) {
                     this.setWolfTamed(true);
                     this.setPathToEntity(null);
                     this.setWolfSitting(true);
                     this.setHealthRaw(this.getMaxHealth());
                     this.setWolfOwner(player.uuid);
                     this.showHeartsOrSmokeFX(true);
                     this.world.sendTrackedEntityStatusUpdatePacket(this, (byte)7);
                  } else {
                     this.showHeartsOrSmokeFX(false);
                     this.world.sendTrackedEntityStatusUpdatePacket(this, (byte)6);
                  }
               }

               return true;
            }
         } else {
            if (itemstack != null && Item.itemsList[itemstack.itemID] instanceof ItemFood) {
               ItemFood itemfood = (ItemFood)Item.itemsList[itemstack.itemID];
               if (itemfood.getIsWolfsFavoriteMeat() && this.getHealth() < this.getMaxHealth()) {
                  if (player.getGamemode().consumeBlocks()) {
                     itemstack.stackSize--;
                     if (itemstack.stackSize <= 0) {
                        player.inventory.setItem(player.inventory.getCurrentItemIndex(), null);
                     }
                  }

                  this.heal(itemfood.getHealAmount());
                  return true;
               }
            }

            if (itemstack != null && itemstack.getItem() instanceof IArmorItem && player.uuid.equals(this.getWolfOwner())) {
               IArmorItem armorItem = (IArmorItem)itemstack.getItem();
               if (armorItem.getArmorMaterial() != null && ARMOR_MATERIALS.containsValue(armorItem)) {
                  if (this.armor != null) {
                     this.dropItem(this.armor, 0.0F);
                     this.setArmor(null);
                  }

                  this.setArmor(new ItemStack(itemstack.getItem(), 1, itemstack.getMetadata(), itemstack.getData()));
                  if (player.getGamemode().consumeBlocks()) {
                     itemstack.stackSize--;
                     if (itemstack.stackSize <= 0) {
                        player.inventory.setItem(player.inventory.getCurrentItemIndex(), null);
                     }
                  }

                  return true;
               }
            }

            if (itemstack == null && player.isSneaking() && this.armor != null && player.uuid.equals(this.getWolfOwner())) {
               this.dropItem(this.armor, 0.0F);
               this.setArmor(null);
               return true;
            }

            if (player.uuid.equals(this.getWolfOwner())) {
               if (!this.world.isClientSide) {
                  ItemStack heldItemSlot = this.getHeldItem();
                  if (this.isWolfSitting() && heldItemSlot != null && heldItemSlot.itemID > 0 && !player.isSneaking()) {
                     player.inventory.insertItem(heldItemSlot, true);
                     if (heldItemSlot.stackSize <= 0) {
                        this.setWolfHeldItem(null);
                     }
                  } else {
                     this.setWolfSitting(!this.isWolfSitting());
                     this.isJumping = false;
                     this.setPathToEntity(null);
                  }
               }

               return true;
            }
         }

         return false;
      }
   }

   void showHeartsOrSmokeFX(boolean doSmoke) {
      String s = "heart";
      if (!doSmoke) {
         s = "smoke";
      }

      for (int i = 0; i < 7; i++) {
         double motionX = this.random.nextGaussian() * 0.02;
         double motionY = this.random.nextGaussian() * 0.02;
         double motionZ = this.random.nextGaussian() * 0.02;
         this.world
            .spawnParticle(
               s,
               this.x + this.random.nextFloat() * this.bbWidth * 2.0F - this.bbWidth,
               this.y + 0.5 + this.random.nextFloat() * this.bbHeight,
               this.z + this.random.nextFloat() * this.bbWidth * 2.0F - this.bbWidth,
               motionX,
               motionY,
               motionZ,
               0
            );
      }
   }

   @Override
   public int getMaxHealth() {
      return this.isWolfTamed() ? 20 : 8;
   }

   @Override
   public void handleEntityEvent(byte byte0, float attackedAtYaw) {
      if (byte0 == 7) {
         this.showHeartsOrSmokeFX(true);
      } else if (byte0 == 6) {
         this.showHeartsOrSmokeFX(false);
      } else if (byte0 == 8) {
         this.field_25052_g = true;
         this.timeWolfIsShaking = 0.0F;
         this.prevTimeWolfIsShaking = 0.0F;
      } else {
         super.handleEntityEvent(byte0, attackedAtYaw);
      }
   }

   @Nullable
   public UUID getWolfOwner() {
      return this.entityData.getUUID(17);
   }

   public void setWolfOwner(UUID uuid) {
      this.entityData.set(17, uuid);
   }

   public boolean isWolfSitting() {
      return (this.entityData.getByte(16) & 1) != 0;
   }

   public void setWolfSitting(boolean flag) {
      byte data = this.entityData.getByte(16);
      if (flag) {
         this.entityData.set(16, (byte)(data | 1));
      } else {
         this.entityData.set(16, (byte)(data & -2));
      }
   }

   public boolean isWolfAngry() {
      return (this.entityData.getByte(16) & 2) != 0;
   }

   public void setWolfAngry(boolean flag) {
      byte data = this.entityData.getByte(16);
      if (flag) {
         this.entityData.set(16, (byte)(data | 2));
      } else {
         this.entityData.set(16, (byte)(data & -3));
      }
   }

   public void setWolfHeldItem(ItemStack itemStack) {
      this.entityData.set(15, itemStack);
   }

   public boolean isWolfTamed() {
      return (this.entityData.getByte(16) & 4) != 0;
   }

   public void setWolfTamed(boolean flag) {
      byte data = this.entityData.getByte(16);
      if (flag) {
         this.entityData.set(16, (byte)(data | 4));
      } else {
         this.entityData.set(16, (byte)(data & -5));
      }
   }

   static {
      ARMOR_MATERIALS.put(ArmorMaterial.LEATHER, (IArmorItem)Items.ARMOR_CHESTPLATE_LEATHER);
      ARMOR_MATERIALS.put(ArmorMaterial.CHAINMAIL, (IArmorItem)Items.ARMOR_CHESTPLATE_CHAINMAIL);
      ARMOR_MATERIALS.put(ArmorMaterial.IRON, (IArmorItem)Items.ARMOR_CHESTPLATE_IRON);
      ARMOR_MATERIALS.put(ArmorMaterial.GOLD, (IArmorItem)Items.ARMOR_CHESTPLATE_GOLD);
      ARMOR_MATERIALS.put(ArmorMaterial.DIAMOND, (IArmorItem)Items.ARMOR_CHESTPLATE_DIAMOND);
      ARMOR_MATERIALS.put(ArmorMaterial.STEEL, (IArmorItem)Items.ARMOR_CHESTPLATE_STEEL);
   }
}
