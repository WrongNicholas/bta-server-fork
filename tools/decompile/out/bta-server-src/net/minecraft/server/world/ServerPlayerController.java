package net.minecraft.server.world;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.PacketBlockUpdate;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.PlayerServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerPlayerController {
   private WorldServer thisWorld;
   public Player player;
   private int blockHitTicks;
   private int currentlyMiningX;
   private int currentlyMiningY;
   private int currentlyMiningZ;
   private int ticksRan;
   private boolean destroyBlock;
   private int destroyX;
   private int destroyY;
   private int destroyZ;
   private Side destroySide;
   private int destroyBlockTicks;

   public ServerPlayerController(WorldServer worldserver) {
      this.thisWorld = worldserver;
   }

   public void tick() {
      this.ticksRan++;
      if (this.destroyBlock) {
         int i = this.ticksRan - this.destroyBlockTicks;
         int j = this.thisWorld.getBlockId(this.destroyX, this.destroyY, this.destroyZ);
         if (j != 0) {
            Block<?> block = Blocks.blocksList[j];
            float f = block.blockStrength(this.thisWorld, this.destroyX, this.destroyY, this.destroyZ, this.destroySide, this.player) * (i + 1);
            if (f >= 1.0F) {
               this.destroyBlock = false;
               this.mineBlock(this.destroyX, this.destroyY, this.destroyZ, this.destroySide);
            }
         } else {
            this.destroyBlock = false;
         }
      }
   }

   public void startMining(int x, int y, int z, Side side) {
      if (!this.player.getGamemode().doBlockBreakingAnim()) {
         this.mineBlock(x, y, z, side);
      } else {
         this.blockHitTicks = this.ticksRan;
         Block<?> block = this.thisWorld.getBlock(x, y, z);
         if (block != null && block.blockStrength(this.thisWorld, x, y, z, side, this.player) >= 1.0F) {
            this.mineBlock(x, y, z, side);
         } else {
            this.currentlyMiningX = x;
            this.currentlyMiningY = y;
            this.currentlyMiningZ = z;
         }
      }
   }

   public void hitBlock(int x, int y, int z, Side side, double xHit, double yHit) {
      this.thisWorld.onBlockHit(this.player, x, y, z, side);
      Block<?> block = this.thisWorld.getBlock(x, y, z);
      if (block != null) {
         block.onBlockLeftClicked(this.thisWorld, x, y, z, this.player, side, xHit, yHit);
      }
   }

   public boolean destroyBlock(int x, int y, int z, Side side) {
      if (x == this.currentlyMiningX && y == this.currentlyMiningY && z == this.currentlyMiningZ) {
         int l = this.ticksRan - this.blockHitTicks;
         int i1 = this.thisWorld.getBlockId(x, y, z);
         if (i1 != 0) {
            Block<?> block = Blocks.blocksList[i1];
            float f = block.blockStrength(this.thisWorld, x, y, z, side, this.player) * (l + 1);
            if (f >= 0.7F) {
               return this.mineBlock(x, y, z, side);
            }

            if (!this.destroyBlock) {
               this.destroyBlock = true;
               this.destroyX = x;
               this.destroyY = y;
               this.destroyZ = z;
               this.destroySide = side;
               this.destroyBlockTicks = this.blockHitTicks;
            }
         }
      }

      return false;
   }

   public boolean removeBlock(int x, int y, int z) {
      return this.thisWorld.setBlockWithNotify(x, y, z, 0);
   }

   public boolean mineBlock(int x, int y, int z, Side side) {
      ItemStack heldItemStack = this.player.getHeldItem();
      int id = this.thisWorld.getBlockId(x, y, z);
      int meta = this.thisWorld.getBlockMetadata(x, y, z);
      Item heldItem = null;
      if (heldItemStack != null) {
         heldItem = heldItemStack.getItem();
         if (!heldItemStack.beforeDestroyBlock(this.thisWorld, id, x, y, z, side, this.player)) {
            return false;
         }
      }

      Block<?> block = Blocks.getBlock(id);
      TileEntity tileEntity = this.thisWorld.getTileEntity(x, y, z);
      this.thisWorld.playBlockEvent(this.player, 2001, x, y, z, id);
      boolean flag = this.removeBlock(x, y, z);
      if (flag && block != null) {
         block.onBlockDestroyedByPlayer(this.thisWorld, x, y, z, side, meta, this.player, heldItem);
      }

      if (flag && this.player.canHarvestBlock(Blocks.blocksList[id]) && this.player.getGamemode().dropBlockOnBreak()) {
         Blocks.blocksList[id].harvestBlock(this.thisWorld, this.player, x, y, z, meta, tileEntity);
         ((PlayerServer)this.player).playerNetServerHandler.sendPacket(new PacketBlockUpdate(x, y, z, this.thisWorld));
      }

      ItemStack itemstack = this.player.getCurrentEquippedItem();
      if (itemstack != null) {
         itemstack.onDestroyBlock(this.thisWorld, id, x, y, z, side, this.player);
         if (itemstack.stackSize <= 0) {
            this.player.destroyCurrentEquippedItem();
         }
      }

      return flag;
   }

   @Deprecated
   public boolean useItemOn(
      Player entityplayer, World world, ItemStack itemstack, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      return this.useOrPlaceItemStackOnTile(entityplayer, world, itemstack, blockX, blockY, blockZ, side, xPlaced, yPlaced);
   }

   public boolean useOrPlaceItemStackOnTile(
      Player entityplayer, World world, ItemStack itemstack, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (!entityplayer.getGamemode().canInteract()) {
         return false;
      } else {
         int blockId = world.getBlockId(blockX, blockY, blockZ);
         if (this.player.getHeldObject() == null) {
            if (this.player.isSneaking() && itemstack == null) {
               TileEntity tileEntity = world.getTileEntity(blockX, blockY, blockZ);
               if (tileEntity != null && tileEntity.canBeCarried(world, this.player)) {
                  this.player.setHeldObject(tileEntity.pickup(world, this.player));
                  return true;
               }
            }

            if ((!this.player.isSneaking() || itemstack == null)
               && blockId > 0
               && Blocks.blocksList[blockId].onBlockRightClicked(world, blockX, blockY, blockZ, this.player, side, xPlaced, yPlaced)) {
               return true;
            } else {
               return itemstack == null ? false : itemstack.useItem(this.player, world, blockX, blockY, blockZ, side, xPlaced, yPlaced);
            }
         } else if (!this.player.isSneaking()
            && blockId > 0
            && Blocks.blocksList[blockId].onBlockRightClicked(world, blockX, blockY, blockZ, this.player, side, xPlaced, yPlaced)) {
            return true;
         } else {
            boolean didPlace = this.player.getHeldObject().tryPlace(world, this.player, blockX, blockY, blockZ, side, xPlaced, yPlaced);
            if (didPlace) {
               this.player.setHeldObject(null);
            }

            return didPlace;
         }
      }
   }

   @Deprecated
   public boolean useItemOnNothing(Player entityplayer, World world, ItemStack itemstack) {
      return this.useItemStackOnNothing(entityplayer, world, itemstack);
   }

   public boolean useItemStackOnNothing(Player entityplayer, World world, ItemStack itemstack) {
      if (!entityplayer.getGamemode().canInteract()) {
         return false;
      } else {
         int i = itemstack.stackSize;
         ItemStack itemstack1 = itemstack.useItemRightClick(world, entityplayer);
         if (itemstack1 != itemstack || itemstack1 != null && itemstack1.stackSize != i) {
            entityplayer.inventory.mainInventory[entityplayer.inventory.getCurrentItemIndex()] = itemstack1;
            if (itemstack1.stackSize <= 0) {
               entityplayer.inventory.mainInventory[entityplayer.inventory.getCurrentItemIndex()] = null;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   @Deprecated
   public boolean onlyUseItemStackOn(
      @NotNull Player player,
      @NotNull World world,
      @Nullable ItemStack itemstack,
      int blockX,
      int blockY,
      int blockZ,
      Side side,
      double xPlaced,
      double yPlaced
   ) {
      return this.placeItemStackOnTile(player, world, itemstack, blockX, blockY, blockZ, side, xPlaced, yPlaced);
   }

   public boolean placeItemStackOnTile(
      @NotNull Player player,
      @NotNull World world,
      @Nullable ItemStack itemstack,
      int blockX,
      int blockY,
      int blockZ,
      Side side,
      double xPlaced,
      double yPlaced
   ) {
      if (!player.getGamemode().canInteract()) {
         return false;
      } else if (player.getHeldObject() == null) {
         return itemstack == null ? false : itemstack.useItem(player, world, blockX, blockY, blockZ, side, xPlaced, yPlaced);
      } else {
         return false;
      }
   }
}
