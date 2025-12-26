package net.minecraft.core.block;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.support.ISupport;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
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

interface BlockInterface {
   int id();

   @NotNull
   String getKey();

   @NotNull
   NamespaceID namespaceId();

   boolean isCubeShaped();

   boolean canPlaceOnSurface();

   boolean canPlaceOnSurfaceOnCondition(World var1, int var2, int var3, int var4);

   boolean renderAsNormalBlockOnCondition(WorldSource var1, int var2, int var3, int var4);

   boolean canPlaceOnSurfaceOfBlock(World var1, int var2, int var3, int var4);

   ItemStack[] getBreakResult(World var1, EnumDropCause var2, int var3, int var4, int var5, int var6, TileEntity var7);

   ItemStack[] getBreakResult(World var1, EnumDropCause var2, int var3, TileEntity var4);

   AABB getBounds();

   AABB getBoundsRaw();

   float getBlockBrightness(WorldSource var1, int var2, int var3, int var4);

   int getLightmapCoord(WorldSource var1, int var2, int var3, int var4);

   float getAmbientOcclusionStrength(WorldSource var1, int var2, int var3, int var4);

   boolean getIsBlockSolid(WorldSource var1, int var2, int var3, int var4, Side var5);

   AABB getSelectedBoundingBoxFromPool(WorldSource var1, int var2, int var3, int var4);

   BoundingVolume getBoundingVolume(World var1, int var2, int var3, int var4);

   void getCollidingBoundingBoxes(World var1, int var2, int var3, int var4, AABB var5, ArrayList<AABB> var6);

   boolean collidesWithEntity(Entity var1, World var2, int var3, int var4, int var5);

   AABB getCollisionBoundingBoxFromPool(WorldSource var1, int var2, int var3, int var4);

   boolean isSolidRender();

   boolean blocksLight();

   boolean canCollideCheck(int var1, boolean var2);

   boolean isCollidable();

   void updateTick(World var1, int var2, int var3, int var4, Random var5);

   void animationTick(World var1, int var2, int var3, int var4, Random var5);

   void onNeighborBlockChange(World var1, int var2, int var3, int var4, int var5);

   int tickDelay();

   void onBlockPlacedByWorld(World var1, int var2, int var3, int var4);

   void onBlockRemoved(World var1, int var2, int var3, int var4, int var5);

   void onBlockDestroyedByPlayer(World var1, int var2, int var3, int var4, Side var5, int var6, Player var7, Item var8);

   float blockStrength(World var1, int var2, int var3, int var4, Side var5, Player var6);

   boolean getImmovable();

   float getBlastResistance(Entity var1);

   HitResult collisionRayTrace(World var1, int var2, int var3, int var4, Vec3 var5, Vec3 var6, boolean var7);

   void onBlockDestroyedByExplosion(World var1, int var2, int var3, int var4);

   boolean canPlaceBlockOnSide(World var1, int var2, int var3, int var4, Side var5);

   boolean canPlaceBlockAt(World var1, int var2, int var3, int var4);

   boolean onBlockRightClicked(World var1, int var2, int var3, int var4, Player var5, Side var6, double var7, double var9);

   void onActivatorInteract(World var1, int var2, int var3, int var4, TileEntityActivator var5, Direction var6);

   void onEntityWalking(World var1, int var2, int var3, int var4, Entity var5);

   void onBlockLeftClicked(World var1, int var2, int var3, int var4, Player var5, Side var6, double var7, double var9);

   void handleEntityInside(World var1, int var2, int var3, int var4, Entity var5, Vec3 var6);

   AABB getBlockBoundsFromState(WorldSource var1, int var2, int var3, int var4);

   boolean getSignal(WorldSource var1, int var2, int var3, int var4, Side var5);

   boolean isSignalSource();

   void onEntityCollidedWithBlock(World var1, int var2, int var3, int var4, Entity var5);

   boolean getDirectSignal(World var1, int var2, int var3, int var4, Side var5);

   void harvestBlock(World var1, Player var2, int var3, int var4, int var5, int var6, TileEntity var7);

   void dropBlockWithCause(World var1, EnumDropCause var2, int var3, int var4, int var5, int var6, TileEntity var7, Player var8);

   ISupport getSupport(World var1, int var2, int var3, int var4, Side var5);

   boolean canBlockStay(World var1, int var2, int var3, int var4);

   void onBlockPlacedByMob(World var1, int var2, int var3, int var4, @NotNull Side var5, Mob var6, double var7, double var9);

   void onBlockPlacedOnSide(World var1, int var2, int var3, int var4, @NotNull Side var5, double var6, double var8);

   String getLanguageKey(int var1);

   void triggerEvent(World var1, int var2, int var3, int var4, int var5, int var6);

   int getPistonPushReaction(World var1, int var2, int var3, int var4);

   int getPlacedBlockMetadata(@Nullable Player var1, ItemStack var2, World var3, int var4, int var5, int var6, Side var7, double var8, double var10);

   boolean isClimbable(World var1, int var2, int var3, int var4);
}
