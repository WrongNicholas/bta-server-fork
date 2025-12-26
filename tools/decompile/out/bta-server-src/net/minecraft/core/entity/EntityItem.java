package net.minecraft.core.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLog;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemDiscMusic;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityItem extends Entity {
   private static final int LIFETIME = 6000;
   public ItemStack item;
   public int age;
   public int pickupDelay;
   private int health;
   public float bobOffset;
   public int basketPickupDelay = 0;
   public static boolean enableItemClumping = true;

   public EntityItem(World world, double x, double y, double z, ItemStack itemstack) {
      super(world);
      this.age = 0;
      this.health = 8;
      this.bobOffset = (float)(Math.random() * Math.PI * 2.0);
      this.setSize(0.25F, 0.25F);
      this.heightOffset = this.bbHeight / 2.0F;
      this.setPos(x, y, z);
      this.item = itemstack;
      this.yRot = (float)(Math.random() * 360.0);
      this.xd = (float)(Math.random() * 0.2 - 0.1);
      this.yd = 0.2;
      this.zd = (float)(Math.random() * 0.2 - 0.1);
      if (itemstack == null) {
         System.err.println("Created EntityItem with no item!");
         Thread.dumpStack();
      }
   }

   @Override
   protected boolean makeStepSound() {
      return false;
   }

   public EntityItem(World world) {
      super(world);
      this.age = 0;
      this.health = 5;
      this.bobOffset = (float)(Math.random() * Math.PI * 2.0);
      this.setSize(0.25F, 0.25F);
      this.heightOffset = this.bbHeight / 2.0F;
   }

   @Override
   protected void defineSynchedData() {
   }

   @Override
   public void tick() {
      super.tick();
      if (this.item == null) {
         System.err.println("Removing EntityItem with no item!");
         Thread.dumpStack();
         this.remove();
      } else if (this.item.stackSize <= 0) {
         this.remove();
      } else {
         if (this.pickupDelay > 0) {
            this.pickupDelay--;
         }

         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         this.yd -= 0.04;
         if (this.isInLava()) {
            this.yd = 0.2;
            this.xd = (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
            this.zd = (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
            this.world.playSoundAtEntity(null, this, "random.fizz", 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
         }

         this.checkInTile(this.x, (this.bb.minY + this.bb.maxY) / 2.0, this.z);
         this.move(this.xd, this.yd, this.zd);
         if (!this.world.isClientSide
            && enableItemClumping
            && (
               MathHelper.floor(this.x) != MathHelper.floor(this.xo)
                  || MathHelper.floor(this.y) != MathHelper.floor(this.yo)
                  || MathHelper.floor(this.z) != MathHelper.floor(this.zo)
                  || this.age % 25 == 0
            )) {
            this.clumpToNearbyStack();
         }

         float friction = 0.98F;
         if (this.onGround) {
            friction = 0.588F;
            int blockId = this.world.getBlockId(MathHelper.floor(this.x), MathHelper.floor(this.bb.minY) - 1, MathHelper.floor(this.z));
            if (blockId > 0) {
               friction = Blocks.blocksList[blockId].friction * 0.98F;
            }
         }

         this.xd *= friction;
         this.yd *= 0.98;
         this.zd *= friction;
         if (this.wasInWater && this.yd < 0.0) {
            this.xd *= 0.95F;
            this.yd *= 0.45F;
            this.zd *= 0.95F;
         }

         if (this.onGround) {
            this.yd *= -0.5;
         }

         this.age++;
         if (this.age >= 6000) {
            this.remove();
         }
      }
   }

   @Override
   public boolean checkAndHandleWater(boolean addVelocity) {
      return this.world.handleMaterialAcceleration(this.bb, Material.water, this, addVelocity);
   }

   @Override
   protected void burn(int damage) {
      this.hurt(null, damage, DamageType.FIRE);
   }

   @Override
   public boolean hurt(Entity entity, int i, DamageType type) {
      this.markHurt();
      this.health -= i;
      if (this.health <= 0) {
         this.remove();
      }

      return false;
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      tag.putShort("Health", (byte)this.health);
      tag.putShort("Age", (short)this.age);
      if (this.item != null) {
         tag.putCompound("Item", this.item.writeToNBT(new CompoundTag()));
      }
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      this.health = tag.getShort("Health") & 255;
      this.age = tag.getShort("Age");
      if (tag.containsKey("Item")) {
         this.item = ItemStack.readItemStackFromNbt(tag.getCompound("Item"));
      }
   }

   @Override
   public void playerTouch(Player player) {
      if (!this.world.isClientSide) {
         if (this.pickupDelay == 0) {
            int previousStackSize = this.item.stackSize;
            player.inventory.insertItem(this.item, true);
            if (this.item.stackSize < previousStackSize) {
               if (this.item.itemID < 16384) {
                  Block<?> b = Blocks.blocksList[this.item.itemID];
                  if (b != null && b.getLogic() instanceof BlockLogicLog) {
                     player.triggerAchievement(Achievements.MINE_WOOD);
                  }
               }

               if (this.item.itemID == Items.LEATHER.id) {
                  player.triggerAchievement(Achievements.KILL_COW);
               }

               if (this.item.itemID == Items.DIAMOND.id) {
                  player.triggerAchievement(Achievements.GET_DIAMONDS);
               }

               if (this.item.itemID == Items.NETHERCOAL.id) {
                  player.triggerAchievement(Achievements.GET_NETHERCOAL);
               }

               if (this.item.itemID == Blocks.ICE.id()) {
                  player.triggerAchievement(Achievements.CRUSH_BLOCKS);
               }

               if (this.item.stackSize <= 0) {
                  this.world.playSoundAtEntity(player, this, "item.pickup", 1.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 4.0F);
                  player.onItemPickup(this, this.item);
               }

               if ((
                     this.item.itemID == Items.LANTERN_FIREFLY_GREEN.id
                        || this.item.itemID == Items.LANTERN_FIREFLY_BLUE.id
                        || this.item.itemID == Items.LANTERN_FIREFLY_ORANGE.id
                        || this.item.itemID == Items.LANTERN_FIREFLY_RED.id
                  )
                  && player.getStat(Items.LANTERN_FIREFLY_RED.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.LANTERN_FIREFLY_GREEN.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.LANTERN_FIREFLY_BLUE.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.LANTERN_FIREFLY_ORANGE.getStat("stat_picked_up")) > 0) {
                  player.triggerAchievement(Achievements.CAUGHT_EM_ALL);
               }

               if (this.item.getItem() instanceof ItemDiscMusic
                  && player.getStat(Items.RECORD_13.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.RECORD_CAT.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.RECORD_BLOCKS.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.RECORD_CHIRP.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.RECORD_FAR.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.RECORD_MALL.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.RECORD_MELLOHI.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.RECORD_STAL.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.RECORD_STRAD.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.RECORD_WARD.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.RECORD_WAIT.getStat("stat_picked_up")) > 0
                  && player.getStat(Items.RECORD_DOG.getStat("stat_picked_up")) > 0) {
                  player.triggerAchievement(Achievements.ALL_MUSIC_DISCS);
               }

               if (this.item.stackSize <= 0) {
                  this.remove();
               }
            }
         }
      }
   }

   public void clumpToNearbyStack() {
      AABB boundingBox = this.bb.grow(0.5, 0.0, 0.5);

      for (EntityItem otherEntity : this.world.getEntitiesWithinAABB(EntityItem.class, boundingBox)) {
         if (otherEntity != this && otherEntity.isAlive() && this.isAlive()) {
            ItemStack thisItemStack = this.item;
            ItemStack otherItemStack = otherEntity.item;
            if (this.item.canStackWith(otherEntity.item) && thisItemStack.stackSize + otherItemStack.stackSize <= 64) {
               if (otherItemStack.stackSize > thisItemStack.stackSize) {
                  combineItems(otherEntity, this);
               } else {
                  combineItems(this, otherEntity);
               }
            }
         }
      }
   }

   private static void combineItems(EntityItem entityItem1, EntityItem entityItem2) {
      entityItem1.item.stackSize = entityItem1.item.stackSize + entityItem2.item.stackSize;
      entityItem2.item.stackSize = 0;
      entityItem1.age = Math.min(entityItem1.age, entityItem2.age);
      entityItem1.pickupDelay = Math.max(entityItem1.pickupDelay, entityItem2.pickupDelay);
      entityItem2.remove();
   }
}
