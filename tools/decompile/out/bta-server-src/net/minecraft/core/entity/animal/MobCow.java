package net.minecraft.core.entity.animal;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemBucketEmpty;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobCow extends MobAnimal {
   public MobCow(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "cow");
      this.setSize(0.9F, 1.3F);
      this.mobDrops.add(new WeightedRandomLootObject(Items.LEATHER.getDefaultStack(), 1, 5));
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
      return "mob.cow";
   }

   @Override
   protected String getHurtSound() {
      return "mob.cowhurt";
   }

   @Override
   protected String getDeathSound() {
      return "mob.cowhurt";
   }

   @Override
   protected float getSoundVolume() {
      return 0.4F;
   }

   @Override
   public boolean interact(@NotNull Player player) {
      ItemStack itemstack = player.inventory.getCurrentItem();
      if (itemstack != null && itemstack.itemID == Items.BUCKET.id) {
         ItemBucketEmpty.useBucket(player, new ItemStack(Items.BUCKET_MILK));
         return true;
      } else {
         return super.interact(player);
      }
   }

   @Override
   public boolean isFavouriteItem(ItemStack itemStack) {
      return itemStack != null && itemStack.getItem().hasTag(ItemTags.COWS_FAVOURITE_ITEM);
   }
}
