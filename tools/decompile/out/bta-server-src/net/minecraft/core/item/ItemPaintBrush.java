package net.minecraft.core.item;

import com.mojang.nbt.tags.CompoundTag;
import java.util.List;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicSign;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.IPaintable;
import net.minecraft.core.block.IPainted;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobSheep;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemPaintBrush extends Item {
   private static final int DAMAGE_PER_DYE = 8;

   public ItemPaintBrush(String translationKey, String namespaceId, int id) {
      super(translationKey, namespaceId, id);
      this.setMaxStackSize(1);
      this.setMaxDamage(64);
   }

   @Override
   public boolean onUseItemOnBlock(
      @NotNull ItemStack itemstack,
      @Nullable Player player,
      @NotNull World world,
      int blockX,
      int blockY,
      int blockZ,
      @NotNull Side side,
      double xPlaced,
      double yPlaced
   ) {
      int id = world.getBlockId(blockX, blockY, blockZ);
      Block<?> block = Blocks.getBlock(id);
      if (Block.hasLogicClass(block, BlockLogicSign.class) && player != null && player.isSneaking()) {
         TileEntitySign sign = (TileEntitySign)world.getTileEntity(blockX, blockY, blockZ);
         DyeColor color = getColor(itemstack);
         if (color != null && color.blockMeta != sign.getColor().id) {
            sign.setColor(TextFormatting.get(color.blockMeta));
            this.consumePaint(itemstack, player);
            return true;
         }
      } else if (Block.hasLogicClass(block, IPaintable.class)) {
         IPaintable paintable = (IPaintable)block.getLogic();
         if (!paintable.canBePainted()) {
            return false;
         }

         DyeColor color = getColor(itemstack);
         if (color != null) {
            if (paintable instanceof IPainted && ((IPainted)paintable).getColor(world, blockX, blockY, blockZ) == color) {
               return false;
            }

            paintable.setColor(world, blockX, blockY, blockZ, color);
            this.consumePaint(itemstack, player);
            return true;
         }
      }

      return false;
   }

   @Override
   public void onUseByActivator(
      ItemStack itemStack,
      TileEntityActivator activatorBlock,
      World world,
      Random random,
      int blockX,
      int blockY,
      int blockZ,
      double offX,
      double offY,
      double offZ,
      Direction direction
   ) {
      int x = blockX + direction.getOffsetX();
      int y = blockY + direction.getOffsetY();
      int z = blockZ + direction.getOffsetZ();
      if (!this.onUseItemOnBlock(itemStack, null, world, x, y, z, direction.getSide(), 0.5, 0.5)) {
         blockX += direction.getOffsetX();
         blockY += direction.getOffsetY();
         blockZ += direction.getOffsetZ();
         AABB box = AABB.getTemporaryBB(blockX, blockY, blockZ, blockX + 1, blockY + 1, blockZ + 1);
         List<MobSheep> entities = world.getEntitiesWithinAABB(MobSheep.class, box);
         if (!entities.isEmpty()) {
            MobSheep sheep = entities.get(0);
            DyeColor woolColor = getColor(itemStack);
            if (sheep.getFleeceColor() != woolColor) {
               sheep.setFleeceColor(woolColor);
               this.consumePaint(itemStack, null);
            }
         }
      }
   }

   public void consumePaint(@NotNull ItemStack itemstack, @Nullable Player player) {
      if (itemstack.getMetadata() >= itemstack.getMaxDamage()) {
         setColor(itemstack, null);
         itemstack.setMetadata(this.getMaxDamage());
      } else {
         itemstack.damageItem(1, player);
      }
   }

   @Override
   public boolean useItemOnEntity(ItemStack itemstack, Mob mob, Player player) {
      if (mob instanceof MobSheep) {
         MobSheep mobSheep = (MobSheep)mob;
         DyeColor woolColor = getColor(itemstack);
         if (mobSheep.getFleeceColor() != woolColor) {
            mobSheep.setFleeceColor(woolColor);
            this.consumePaint(itemstack, player);
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean hasInventoryInteraction() {
      return true;
   }

   @Override
   public ItemStack onInventoryInteract(Player player, Slot slot, ItemStack stackInSlot, boolean isItemGrabbed) {
      if (isItemGrabbed) {
         return stackInSlot;
      } else {
         DyeColor currentColor = getColor(stackInSlot);
         int dyeCount = this.getDyeCount(stackInSlot);
         ItemStack grabbedItem = player.inventory.getHeldItemStack();
         if (grabbedItem != null && grabbedItem.getItem() instanceof ItemDye) {
            DyeColor newColor = DyeColor.colorFromItemMeta(grabbedItem.getMetadata());
            if (currentColor != newColor) {
               setColor(stackInSlot, newColor);
               this.setDyeCount(stackInSlot, 8);
               grabbedItem.stackSize--;
               if (grabbedItem.stackSize <= 0) {
                  player.inventory.setHeldItemStack(null);
               }
            } else if (dyeCount < this.getMaxDamage()) {
               int newDyeCount = Math.min(dyeCount + 8, stackInSlot.getMaxDamage());
               this.setDyeCount(stackInSlot, newDyeCount);
               grabbedItem.stackSize--;
               if (grabbedItem.stackSize <= 0) {
                  player.inventory.setHeldItemStack(null);
               }
            }
         }

         return stackInSlot;
      }
   }

   @Override
   public boolean showFullDurability() {
      return false;
   }

   public int getDyeCount(ItemStack stack) {
      return stack.getMaxDamage() - stack.getMetadata();
   }

   public void setDyeCount(ItemStack stack, int count) {
      stack.setMetadata(stack.getMaxDamage() - count);
   }

   @Nullable
   public static DyeColor getColor(@NotNull ItemStack stack) {
      CompoundTag tag = stack.getData();
      if (tag.containsKey("Color")) {
         int color = tag.getInteger("Color");
         return color < 0 ? null : DyeColor.colorFromItemMeta(color & 15);
      } else {
         return null;
      }
   }

   public static void setColor(@NotNull ItemStack stack, @Nullable DyeColor color) {
      CompoundTag tag = stack.getData();
      if (color == null) {
         tag.putInt("Color", -1);
      } else {
         tag.putInt("Color", color.itemMeta);
      }
   }
}
