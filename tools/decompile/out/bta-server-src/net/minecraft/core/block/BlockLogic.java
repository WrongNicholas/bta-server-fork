package net.minecraft.core.block;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.MaterialColor;
import net.minecraft.core.block.support.FullSupport;
import net.minecraft.core.block.support.ISupport;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemToolShears;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.BoundingVolume;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogic implements BlockInterface, IItemConvertible {
   protected final AABB bounds = AABB.getPermanentBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   @NotNull
   public final Block<?> block;
   @NotNull
   protected Material material;

   public BlockLogic(Block<?> block, Material material) {
      this.block = Objects.requireNonNull(block);
      this.material = Objects.requireNonNull(material);
   }

   public void initializeBlock() {
   }

   @Override
   public final int id() {
      return this.block.id();
   }

   @NotNull
   @Override
   public final String getKey() {
      return this.block.getKey();
   }

   @NotNull
   @Override
   public final NamespaceID namespaceId() {
      return this.block.namespaceId();
   }

   @NotNull
   public Material getMaterial() {
      return this.material;
   }

   @NotNull
   public MaterialColor getMaterialColor() {
      return this.material.color;
   }

   @Override
   public boolean isCubeShaped() {
      return true;
   }

   @Override
   public boolean canPlaceOnSurface() {
      return this.isCubeShaped();
   }

   @Override
   public boolean canPlaceOnSurfaceOnCondition(World world, int x, int y, int z) {
      return this.canPlaceOnSurface();
   }

   @Override
   public boolean renderAsNormalBlockOnCondition(WorldSource world, int x, int y, int z) {
      return this.isCubeShaped();
   }

   @Override
   public boolean canPlaceOnSurfaceOfBlock(World world, int x, int y, int z) {
      return this.canPlaceOnSurface();
   }

   @Override
   public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
      return this.getBreakResult(world, dropCause, meta, tileEntity);
   }

   @Override
   public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return dropCause != EnumDropCause.IMPROPER_TOOL ? new ItemStack[]{new ItemStack(this.block)} : null;
   }

   public void setBlockBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      this.bounds.set(minX, minY, minZ, maxX, maxY, maxZ);
   }

   @Override
   public AABB getBounds() {
      return this.bounds.copy();
   }

   @Override
   public AABB getBoundsRaw() {
      return this.bounds;
   }

   @Override
   public float getBlockBrightness(WorldSource blockAccess, int x, int y, int z) {
      return blockAccess.getBrightness(x, y, z, this.block.emission);
   }

   @Override
   public int getLightmapCoord(WorldSource blockAccess, int x, int y, int z) {
      return blockAccess.getLightmapCoord(x, y, z, this.block.emission);
   }

   @Override
   public float getAmbientOcclusionStrength(WorldSource blockAccess, int x, int y, int z) {
      return this.isSolidRender() ? 1.0F : 0.0F;
   }

   @Override
   public boolean getIsBlockSolid(WorldSource blockAccess, int x, int y, int z, Side side) {
      return blockAccess.getBlockMaterial(x, y, z).isSolid();
   }

   @Override
   public AABB getSelectedBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return this.getBlockBoundsFromState(world, x, y, z).move(x, y, z);
   }

   @Override
   public BoundingVolume getBoundingVolume(World world, int x, int y, int z) {
      return null;
   }

   @Override
   public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList<AABB> aabbList) {
      this.addIntersectingBoundingBox(aabb, this.getCollisionBoundingBoxFromPool(world, x, y, z), aabbList);
   }

   protected void addIntersectingBoundingBox(AABB sourceAABB, AABB aabbToAdd, ArrayList<AABB> aabbList) {
      if (aabbToAdd != null && sourceAABB.intersects(aabbToAdd)) {
         aabbList.add(aabbToAdd);
      }
   }

   @Override
   public boolean collidesWithEntity(Entity entity, World world, int x, int y, int z) {
      return true;
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return this.getBlockBoundsFromState(world, x, y, z).move(x, y, z);
   }

   @Override
   public boolean isSolidRender() {
      return true;
   }

   @Override
   public boolean blocksLight() {
      return false;
   }

   @Override
   public boolean canCollideCheck(int meta, boolean shouldCollideWithFluids) {
      return this.isCollidable();
   }

   @Override
   public boolean isCollidable() {
      return true;
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
   }

   @Override
   public int tickDelay() {
      return 10;
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
   }

   @Override
   public void onBlockDestroyedByPlayer(World world, int x, int y, int z, Side side, int meta, Player player, Item item) {
   }

   @Override
   public float blockStrength(World world, int x, int y, int z, Side side, Player player) {
      if (this.block.blockHardness < 0.0F) {
         return 0.0F;
      } else {
         return !player.canHarvestBlock(this.block)
            ? 1.0F / this.block.blockHardness / 100.0F
            : player.getCurrentPlayerStrVsBlock(this.block) / this.block.blockHardness / 30.0F;
      }
   }

   @Override
   public boolean getImmovable() {
      return this.block.immovable;
   }

   @Override
   public float getBlastResistance(Entity entity) {
      return this.block.blastResistance / 5.0F;
   }

   @Override
   public HitResult collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end, boolean useSelectorBoxes) {
      AABB bounds = useSelectorBoxes ? this.getSelectedBoundingBoxFromPool(world, x, y, z).move(-x, -y, -z) : this.getBlockBoundsFromState(world, x, y, z);
      start = start.add(-x, -y, -z);
      end = end.add(-x, -y, -z);
      Vec3 minVX = start.clipX(end, bounds.minX);
      Vec3 maxVX = start.clipX(end, bounds.maxX);
      Vec3 minVY = start.clipY(end, bounds.minY);
      Vec3 maxVY = start.clipY(end, bounds.maxY);
      Vec3 minVZ = start.clipZ(end, bounds.minZ);
      Vec3 maxVZ = start.clipZ(end, bounds.maxZ);
      if (!this.isVecInsideYZBounds(bounds, minVX)) {
         minVX = null;
      }

      if (!this.isVecInsideYZBounds(bounds, maxVX)) {
         maxVX = null;
      }

      if (!this.isVecInsideXZBounds(bounds, minVY)) {
         minVY = null;
      }

      if (!this.isVecInsideXZBounds(bounds, maxVY)) {
         maxVY = null;
      }

      if (!this.isVecInsideXYBounds(bounds, minVZ)) {
         minVZ = null;
      }

      if (!this.isVecInsideXYBounds(bounds, maxVZ)) {
         maxVZ = null;
      }

      Vec3 vec38 = null;
      if (minVX != null && (vec38 == null || start.distanceToSquared(minVX) < start.distanceToSquared(vec38))) {
         vec38 = minVX;
      }

      if (maxVX != null && (vec38 == null || start.distanceToSquared(maxVX) < start.distanceToSquared(vec38))) {
         vec38 = maxVX;
      }

      if (minVY != null && (vec38 == null || start.distanceToSquared(minVY) < start.distanceToSquared(vec38))) {
         vec38 = minVY;
      }

      if (maxVY != null && (vec38 == null || start.distanceToSquared(maxVY) < start.distanceToSquared(vec38))) {
         vec38 = maxVY;
      }

      if (minVZ != null && (vec38 == null || start.distanceToSquared(minVZ) < start.distanceToSquared(vec38))) {
         vec38 = minVZ;
      }

      if (maxVZ != null && (vec38 == null || start.distanceToSquared(maxVZ) < start.distanceToSquared(vec38))) {
         vec38 = maxVZ;
      }

      if (vec38 == null) {
         return null;
      } else {
         Side side = Side.NONE;
         if (vec38 == minVX) {
            side = Side.WEST;
         }

         if (vec38 == maxVX) {
            side = Side.EAST;
         }

         if (vec38 == minVY) {
            side = Side.BOTTOM;
         }

         if (vec38 == maxVY) {
            side = Side.TOP;
         }

         if (vec38 == minVZ) {
            side = Side.NORTH;
         }

         if (vec38 == maxVZ) {
            side = Side.SOUTH;
         }

         return new HitResult(x, y, z, side, vec38.add(x, y, z));
      }
   }

   private boolean isVecInsideYZBounds(AABB bounds, @Nullable Vec3 vec3) {
      return vec3 == null ? false : vec3.y >= bounds.minY && vec3.y <= bounds.maxY && vec3.z >= bounds.minZ && vec3.z <= bounds.maxZ;
   }

   private boolean isVecInsideXZBounds(AABB bounds, @Nullable Vec3 vec3) {
      return vec3 == null ? false : vec3.x >= bounds.minX && vec3.x <= bounds.maxX && vec3.z >= bounds.minZ && vec3.z <= bounds.maxZ;
   }

   private boolean isVecInsideXYBounds(AABB bounds, @Nullable Vec3 vec3) {
      return vec3 == null ? false : vec3.x >= bounds.minX && vec3.x <= bounds.maxX && vec3.y >= bounds.minY && vec3.y <= bounds.maxY;
   }

   @Override
   public void onBlockDestroyedByExplosion(World world, int x, int y, int z) {
   }

   @Override
   public boolean canPlaceBlockOnSide(World world, int x, int y, int z, Side side) {
      return this.canPlaceBlockAt(world, x, y, z);
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return world.canPlaceInsideBlock(x, y, z);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      return false;
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
   }

   @Override
   public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
   }

   @Override
   public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
   }

   @Override
   public void handleEntityInside(World world, int x, int y, int z, Entity entity, Vec3 entityVelocity) {
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      return this.getBounds();
   }

   @Override
   public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
      return false;
   }

   @Override
   public boolean isSignalSource() {
      return false;
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
   }

   @Override
   public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
      return false;
   }

   @Override
   public void harvestBlock(World world, Player player, int x, int y, int z, int meta, TileEntity tileEntity) {
      player.addStat(this.block.getStat("stat_mined"), 1);
      ItemStack heldItemStack = player.inventory.getCurrentItem();
      Item heldItem = heldItemStack != null ? Item.itemsList[heldItemStack.itemID] : null;
      if (heldItem != null) {
         if (heldItem.isSilkTouch() && player.canHarvestBlock(this.block)) {
            this.dropBlockWithCause(world, EnumDropCause.SILK_TOUCH, x, y, z, meta, tileEntity, player);
            return;
         }

         if (heldItem instanceof ItemToolShears && (this.block.hasTag(BlockTags.SHEARS_DO_SILK_TOUCH) || this.block.hasTag(BlockTags.MINEABLE_BY_SHEARS))) {
            ItemToolShears heldShears = (ItemToolShears)heldItem;
            this.dropBlockWithCause(world, EnumDropCause.SILK_TOUCH, x, y, z, meta, tileEntity, player);
            heldShears.onBlockSheared(player, heldItemStack);
            return;
         }
      }

      if (player.canHarvestBlock(this.block)) {
         this.dropBlockWithCause(world, EnumDropCause.PROPER_TOOL, x, y, z, meta, tileEntity, player);
      } else {
         this.dropBlockWithCause(world, EnumDropCause.IMPROPER_TOOL, x, y, z, meta, tileEntity, player);
      }
   }

   @Override
   public void dropBlockWithCause(World world, EnumDropCause cause, int x, int y, int z, int meta, TileEntity tileEntity, Player player) {
      if (!world.isClientSide) {
         ItemStack[] drops = this.getBreakResult(world, cause, x, y, z, meta, tileEntity);
         if (drops != null) {
            int j = 0;

            for (int dropsLength = drops.length; j < dropsLength; j++) {
               ItemStack drop = drops[j];
               if (drop != null) {
                  if (this.block.hasTag(BlockTags.INSTANT_PICKUP) && player != null) {
                     player.inventory.insertItem(drop, true);
                     if (drop.stackSize <= 0) {
                        continue;
                     }
                  }

                  if (EntityItem.enableItemClumping) {
                     world.dropItem(x, y, z, drop.copy());
                  } else {
                     for (int i = 0; i < drop.stackSize; i++) {
                        ItemStack drop1 = drop.copy();
                        drop1.stackSize = 1;
                        world.dropItem(x, y, z, drop1);
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public ISupport getSupport(World world, int x, int y, int z, Side side) {
      return FullSupport.INSTANCE;
   }

   @Override
   public boolean canBlockStay(World world, int x, int y, int z) {
      return true;
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
      this.onBlockPlacedOnSide(world, x, y, z, side, xPlaced, yPlaced);
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
   }

   @Override
   public String getLanguageKey(int meta) {
      return this.block.getKey();
   }

   @Override
   public void triggerEvent(World world, int x, int y, int z, int index, int data) {
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return this.material.getPushReaction();
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      return 0;
   }

   @Override
   public boolean isClimbable(World world, int x, int y, int z) {
      return false;
   }

   @Override
   public Item asItem() {
      return this.block.asItem();
   }

   @Override
   public ItemStack getDefaultStack() {
      return this.block.getDefaultStack();
   }
}
