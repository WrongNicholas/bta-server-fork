package net.minecraft.core.entity.monster;

import com.mojang.nbt.tags.CompoundTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobZombieArmored extends MobZombie {
   public static final int DATA_BREAK_POINTS = 6;
   public static final int DATA_BREAK_ORDER = 7;
   public static final int DATA_GENERIC_FLAGS = 8;
   public static final int MASK_FLAG_SWORD = 1;
   private static final ItemStack defaultHeldItem = new ItemStack(Items.TOOL_SWORD_IRON, 1);

   public MobZombieArmored(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "zombie_armored");
      this.scoreValue = 1000;
   }

   @Override
   public void spawnInit() {
      super.spawnInit();
      Random rand = new Random();
      this.setHoldingSword(rand.nextInt(5) == 0);
      List<Integer> armorItems = new ArrayList<>();
      armorItems.add(0);
      armorItems.add(1);
      armorItems.add(2);
      armorItems.add(3);
      int[] armorBreakOrder = new int[4];

      for (int i = 0; i < 4; i++) {
         int index = rand.nextInt(armorItems.size());
         armorBreakOrder[i] = armorItems.get(index);
         armorItems.remove(index);
      }

      this.setArmorBreakOrder(armorBreakOrder);
      int[] armorBreakPoints = new int[4];

      for (int i = 0; i < 4; i++) {
         armorBreakPoints[i] = 30 - 10 * i + rand.nextInt(10);
      }

      this.setArmorBreakPoints(armorBreakPoints);
   }

   @Override
   public void defineSynchedData() {
      this.entityData.define(6, 0, Integer.class);
      this.entityData.define(7, (short)0, Short.class);
      this.entityData.define(8, (byte)0, Byte.class);
   }

   @Override
   public int getMaxHealth() {
      return 40;
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
   }

   @Override
   public boolean hurt(Entity attacker, int i, DamageType type) {
      int lastHealth = this.getHealth();
      boolean result = super.hurt(attacker, i, type);

      for (int j = 0; j < 4; j++) {
         int[] armorBreakPoints = this.getArmorBreakPoints();
         if (this.getHealth() <= armorBreakPoints[j] && lastHealth > armorBreakPoints[j]) {
            int numChainlinks = this.random.nextInt(2);

            for (int k = 0; k < numChainlinks; k++) {
               EntityItem entityitem = this.dropItem(new ItemStack(Items.CHAINLINK), 1.0F);
               entityitem.yd = entityitem.yd + this.random.nextFloat() * 0.05F;
               entityitem.xd = entityitem.xd + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;
               entityitem.zd = entityitem.zd + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;
            }
         }
      }

      return result;
   }

   public void setArmorBreakPoints(int[] breakPoints) {
      int value = breakPoints[0];
      value |= breakPoints[1] << 6;
      value |= breakPoints[2] << 12;
      value |= breakPoints[3] << 18;
      this.entityData.set(6, value);
   }

   public int[] getArmorBreakPoints() {
      int value = this.entityData.getInt(6);
      return new int[]{value & 63, (value & 4032) >> 6, (value & 258048) >> 12, (value & 16515072) >> 18};
   }

   public void setArmorBreakOrder(int[] breakPoints) {
      short value = (short)breakPoints[0];
      value = (short)(value | (short)(breakPoints[1] << 2));
      value = (short)(value | (short)(breakPoints[2] << 4));
      value = (short)(value | (short)(breakPoints[3] << 6));
      this.entityData.set(7, value);
   }

   public int[] getArmorBreakOrder() {
      short value = this.entityData.getShort(7);
      return new int[]{value & 3, (value & 12) >> 2, (value & 48) >> 4, (value & 192) >> 6};
   }

   public void setHoldingSword(boolean flag) {
      if (flag) {
         this.entityData.set(8, (byte)(this.entityData.getByte(8) | 1));
         this.attackStrength = 7;
      } else {
         this.entityData.set(8, (byte)(this.entityData.getByte(8) & -2));
         this.attackStrength = 5;
      }
   }

   public boolean isHoldingSword() {
      byte data = this.entityData.getByte(8);
      return (data & 1) != 0;
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("breakpoints", this.entityData.getInt(6));
      tag.putShort("breakorder", this.entityData.getShort(7));
      tag.putBoolean("hasSword", this.isHoldingSword());
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      if (tag.containsKey("breakpoints")) {
         this.entityData.set(6, tag.getInteger("breakpoints"));
      }

      if (tag.containsKey("breakorder")) {
         this.entityData.set(7, tag.getShort("breakorder"));
      }

      if (tag.containsKey("hasSword")) {
         this.setHoldingSword(tag.getBoolean("hasSword"));
      }
   }

   @Override
   public ItemStack getHeldItem() {
      return this.isHoldingSword() ? defaultHeldItem : null;
   }
}
