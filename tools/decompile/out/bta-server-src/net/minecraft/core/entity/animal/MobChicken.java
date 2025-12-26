package net.minecraft.core.entity.animal;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobChicken extends MobAnimal {
   public float flap = 0.0F;
   public float flapSpeed = 0.0F;
   public float oFlapSpeed;
   public float oFlap;
   public float flapping = 1.0F;
   public int eggTimer;

   public MobChicken(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "chicken");
      this.setSize(0.4F, 0.8F);
      this.eggTimer = this.random.nextInt(6000) + 6000;
      this.mobDrops.add(new WeightedRandomLootObject(Items.FEATHER_CHICKEN.getDefaultStack(), 0, 2));
   }

   @Override
   public int getMaxHealth() {
      return 4;
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      this.oFlap = this.flap;
      this.oFlapSpeed = this.flapSpeed;
      this.flapSpeed = (float)(this.flapSpeed + (this.onGround ? -1 : 4) * 0.3);
      if (this.flapSpeed < 0.0F) {
         this.flapSpeed = 0.0F;
      }

      if (this.flapSpeed > 1.0F) {
         this.flapSpeed = 1.0F;
      }

      if (!this.onGround && this.flapping < 1.0F) {
         this.flapping = 1.0F;
      }

      this.flapping = (float)(this.flapping * 0.9);
      if (!this.onGround && this.yd < 0.0) {
         this.yd *= 0.6;
      }

      this.flap = this.flap + this.flapping * 2.0F;
      if (!this.world.isClientSide && --this.eggTimer <= 0) {
         this.world.playSoundAtEntity(null, this, "mob.chickenplop", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         this.dropItem(Items.EGG_CHICKEN.id, 1);
         this.eggTimer = this.random.nextInt(6000) + 6000;
      }
   }

   @Override
   protected void causeFallDamage(float distance) {
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
   public String getLivingSound() {
      return "mob.chicken";
   }

   @Override
   protected String getHurtSound() {
      return "mob.chickenhurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.chickenhurt";
   }

   @Override
   public boolean isFavouriteItem(ItemStack itemStack) {
      return itemStack != null && itemStack.getItem().hasTag(ItemTags.CHICKENS_FAVOURITE_ITEM);
   }
}
