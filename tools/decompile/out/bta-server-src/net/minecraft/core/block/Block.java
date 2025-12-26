package net.minecraft.core.block;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.achievement.stat.Stat;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.MaterialColor;
import net.minecraft.core.block.support.ISupport;
import net.minecraft.core.data.tag.ITaggable;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.HardIllegalArgumentException;
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

public final class Block<T extends BlockLogic> implements ITaggable<Block<?>>, IItemConvertible, BlockInterface {
   public static boolean disableNormalEntityLogic = false;
   private final int id;
   @NotNull
   private final String key;
   @NotNull
   private final NamespaceID namespaceID;
   @NotNull
   private final BlockLogicSupplier<T> logicSupplier;
   private T logic;
   public float blockHardness;
   public float blastResistance;
   public boolean enableStats;
   public boolean isLitInteriorSurface;
   public float blockParticleGravity;
   public float friction;
   public int emission;
   public Integer lightBlock = null;
   public boolean immovable = false;
   public boolean isEntityTile = false;
   public boolean disabledNeighborMetaNotify = false;
   public boolean shouldTick = false;
   @Nullable
   public Supplier<TileEntity> entitySupplier;
   public MaterialColor overrideColor;
   private BlockSound blockSound = BlockSounds.DEFAULT;
   @NotNull
   public Supplier<Item> blockItemSupplier = () -> new ItemBlock<>(this);
   @Nullable
   Supplier<@NotNull IItemConvertible> statParent = null;

   Block(int id, @NotNull String translationKey, @NotNull String namespaceId, @NotNull BlockLogicSupplier<T> logicSupplier) {
      this.namespaceID = this.setupNamespaceId(namespaceId).makePermanent();
      this.key = this.setupTranslationKey(translationKey);
      this.id = this.setupNumericId(id);
      this.logicSupplier = Objects.requireNonNull(logicSupplier);
      this.enableStats = true;
      this.blockParticleGravity = 1.0F;
      this.friction = 0.6F;
      this.immovable = false;
   }

   private int setupNumericId(int id) {
      if (id < 0) {
         throw new IllegalArgumentException("Numeric id of block '" + this.namespaceID + "' must not be negative!");
      } else if (id >= Blocks.blocksList.length) {
         throw new IllegalArgumentException("Numeric id of block '" + this.namespaceID + "' must not be greater than '" + (Blocks.blocksList.length - 1) + "'!");
      } else {
         return id;
      }
   }

   @NotNull
   private String setupTranslationKey(String translationKey) {
      if (translationKey == null) {
         throw new NullPointerException("Translation key of block '" + this.namespaceID + "' must not be null!");
      } else {
         if (!translationKey.startsWith("tile.")) {
            translationKey = "tile." + translationKey;
         }

         return translationKey;
      }
   }

   @NotNull
   private NamespaceID setupNamespaceId(String namespaceId) {
      if (namespaceId == null) {
         throw new NullPointerException("NamespaceId must not be null!");
      } else {
         if (!namespaceId.contains(":")) {
            namespaceId = "minecraft:" + namespaceId;
         }

         try {
            return NamespaceID.getPermanent(namespaceId);
         } catch (HardIllegalArgumentException var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   public T getLogic() {
      return this.logic;
   }

   public Block<T> withEntity(@Nullable Supplier<TileEntity> entitySupplier) {
      this.isEntityTile = true;
      this.entitySupplier = entitySupplier;
      return this;
   }

   public Block<T> withDisabledNeighborNotifyOnMetadataChange() {
      this.disabledNeighborMetaNotify = true;
      return this;
   }

   public Block<T> withLightBlock(int blockAmount) {
      this.lightBlock = blockAmount;
      return this;
   }

   public Block<T> withLightEmission(float lightEmission) {
      this.emission = (int)(15.0F * lightEmission);
      return this;
   }

   public Block<T> withLightEmission(int lightEmission) {
      this.emission = lightEmission & 15;
      return this;
   }

   public Block<T> withImmovableFlagSet() {
      this.immovable = true;
      return this;
   }

   public Block<T> withBlastResistance(float blastResistance) {
      this.blastResistance = blastResistance * 3.0F;
      return this;
   }

   public Block<T> withSound(BlockSound sound) {
      this.blockSound = sound;
      return this;
   }

   public Block<T> withHardness(float blockHardness) {
      this.blockHardness = blockHardness;
      if (this.blastResistance < blockHardness * 5.0F) {
         this.blastResistance = blockHardness * 5.0F;
      }

      return this;
   }

   public Block<T> withLitInteriorSurface(boolean isLit) {
      this.isLitInteriorSurface = isLit;
      return this;
   }

   public Block<T> withSetUnbreakable() {
      this.withHardness(-1.0F);
      return this;
   }

   public Block<T> setBlockItem(@NotNull Supplier<Item> itemSupplier) {
      this.blockItemSupplier = itemSupplier;
      return this;
   }

   public Block<T> setBlockItem(@NotNull Function<Block<T>, Item> itemSupplier) {
      this.blockItemSupplier = () -> itemSupplier.apply(this);
      return this;
   }

   public Block<T> withOverrideColor(MaterialColor color) {
      this.overrideColor = color;
      return this;
   }

   public Block<T> setStatParent(@NotNull Supplier<IItemConvertible> icon) {
      this.statParent = icon;
      return this;
   }

   public Block<T> withDisabledStats() {
      this.enableStats = false;
      return this;
   }

   public Block<T> setTicking(boolean tick) {
      this.shouldTick = tick;
      return this;
   }

   public BlockSound getSound() {
      return this.blockSound;
   }

   public Material getMaterial() {
      return this.logic.getMaterial();
   }

   public float getHardness() {
      return this.blockHardness;
   }

   public boolean getEnableStats() {
      return this.enableStats && this.getHardness() >= 0.0F;
   }

   @Nullable
   public Stat getStat(String statID) {
      return this.asItem().getStat(statID);
   }

   public MaterialColor getMaterialColor() {
      return this.overrideColor != null ? this.overrideColor : this.logic.getMaterialColor();
   }

   @Override
   public int id() {
      return this.id;
   }

   @NotNull
   @Override
   public String getKey() {
      return this.key;
   }

   @NotNull
   @Override
   public NamespaceID namespaceId() {
      return this.namespaceID;
   }

   public void init() {
      this.logic = this.logicSupplier.get(this);
   }

   @Override
   public boolean isIn(Tag<Block<?>> tag) {
      return tag.appliesTo(this);
   }

   @Override
   public Item asItem() {
      return Item.itemsList[this.id];
   }

   @Override
   public ItemStack getDefaultStack() {
      return new ItemStack(this);
   }

   public static boolean isBuried(World world, int x, int y, int z) {
      boolean buried = true;
      if (Blocks.lightBlock[world.getBlockId(x + 1, y, z)] <= 2) {
         buried = false;
      }

      if (Blocks.lightBlock[world.getBlockId(x - 1, y, z)] <= 2) {
         buried = false;
      }

      if (Blocks.lightBlock[world.getBlockId(x, y, z + 1)] <= 2) {
         buried = false;
      }

      if (Blocks.lightBlock[world.getBlockId(x, y, z - 1)] <= 2) {
         buried = false;
      }

      if (Blocks.lightBlock[world.getBlockId(x, y + 1, z)] <= 2) {
         buried = false;
      }

      if (Blocks.lightBlock[world.getBlockId(x, y - 1, z)] <= 2) {
         buried = false;
      }

      return buried;
   }

   public static boolean getIsLitInteriorSurface(World world, int x, int y, int z) {
      int l = world.getBlockId(x, y, z);
      return l != 0 && Blocks.blocksList[l] != null && Blocks.blocksList[l].isLitInteriorSurface;
   }

   @SafeVarargs
   public final Block<T> withTags(Tag<Block<?>>... tags) {
      for (Tag<Block<?>> tag : tags) {
         tag.tag(this);
      }

      return this;
   }

   public boolean hasTag(Tag<Block<?>> tag) {
      return tag.appliesTo(this);
   }

   @Override
   public boolean isCubeShaped() {
      return this.logic.isCubeShaped();
   }

   @Override
   public boolean canPlaceOnSurface() {
      return this.logic.canPlaceOnSurface();
   }

   @Override
   public boolean canPlaceOnSurfaceOnCondition(World world, int x, int y, int z) {
      return this.logic.canPlaceOnSurfaceOnCondition(world, x, y, z);
   }

   @Override
   public boolean renderAsNormalBlockOnCondition(WorldSource world, int x, int y, int z) {
      return this.logic.renderAsNormalBlockOnCondition(world, x, y, z);
   }

   @Override
   public boolean canPlaceOnSurfaceOfBlock(World world, int x, int y, int z) {
      return this.logic.canPlaceOnSurfaceOfBlock(world, x, y, z);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
      return this.logic.getBreakResult(world, dropCause, x, y, z, meta, tileEntity);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return this.logic.getBreakResult(world, dropCause, meta, tileEntity);
   }

   @Override
   public AABB getBounds() {
      return this.logic.getBounds();
   }

   @Override
   public AABB getBoundsRaw() {
      return this.logic.getBoundsRaw();
   }

   @Override
   public float getBlockBrightness(WorldSource blockAccess, int x, int y, int z) {
      return this.logic.getBlockBrightness(blockAccess, x, y, z);
   }

   @Override
   public int getLightmapCoord(WorldSource blockAccess, int x, int y, int z) {
      return this.logic.getLightmapCoord(blockAccess, x, y, z);
   }

   @Override
   public float getAmbientOcclusionStrength(WorldSource blockAccess, int x, int y, int z) {
      return this.logic.getAmbientOcclusionStrength(blockAccess, x, y, z);
   }

   @Override
   public boolean getIsBlockSolid(WorldSource blockAccess, int x, int y, int z, Side side) {
      return this.logic.getIsBlockSolid(blockAccess, x, y, z, side);
   }

   @Override
   public AABB getSelectedBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return this.logic.getSelectedBoundingBoxFromPool(world, x, y, z);
   }

   @Override
   public BoundingVolume getBoundingVolume(World world, int x, int y, int z) {
      return this.logic.getBoundingVolume(world, x, y, z);
   }

   @Override
   public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList<AABB> aabbList) {
      this.logic.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
   }

   @Override
   public boolean collidesWithEntity(Entity entity, World world, int x, int y, int z) {
      return this.logic.collidesWithEntity(entity, world, x, y, z);
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return this.logic.getCollisionBoundingBoxFromPool(world, x, y, z);
   }

   @Override
   public boolean isSolidRender() {
      return this.logic.isSolidRender();
   }

   @Override
   public boolean blocksLight() {
      return this.logic.blocksLight();
   }

   @Override
   public boolean canCollideCheck(int meta, boolean shouldCollideWithFluids) {
      return this.logic.canCollideCheck(meta, shouldCollideWithFluids);
   }

   @Override
   public boolean isCollidable() {
      return this.logic.isCollidable();
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      this.logic.updateTick(world, x, y, z, rand);
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      this.logic.animationTick(world, x, y, z, rand);
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      this.logic.onNeighborBlockChange(world, x, y, z, blockId);
   }

   @Override
   public int tickDelay() {
      return this.logic.tickDelay();
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      if (this.isEntityTile && !disableNormalEntityLogic && this.entitySupplier != null) {
         world.setTileEntity(x, y, z, this.entitySupplier.get());
      }

      this.logic.onBlockPlacedByWorld(world, x, y, z);
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      this.logic.onBlockRemoved(world, x, y, z, data);
      if (this.isEntityTile && !disableNormalEntityLogic) {
         if (!world.isClientSide) {
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity != null) {
               tileEntity.dropContents(world, x, y, z);
            }
         }

         world.removeBlockTileEntity(x, y, z);
      }
   }

   @Override
   public void onBlockDestroyedByPlayer(World world, int x, int y, int z, Side side, int meta, Player player, Item item) {
      this.logic.onBlockDestroyedByPlayer(world, x, y, z, side, meta, player, item);
   }

   @Override
   public float blockStrength(World world, int x, int y, int z, Side side, Player player) {
      return this.logic.blockStrength(world, x, y, z, side, player);
   }

   @Override
   public boolean getImmovable() {
      return this.logic.getImmovable();
   }

   @Override
   public float getBlastResistance(Entity entity) {
      return this.logic.getBlastResistance(entity);
   }

   @Override
   public HitResult collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end, boolean useSelectorBoxes) {
      return this.logic.collisionRayTrace(world, x, y, z, start, end, useSelectorBoxes);
   }

   @Override
   public void onBlockDestroyedByExplosion(World world, int x, int y, int z) {
      this.logic.onBlockDestroyedByExplosion(world, x, y, z);
   }

   @Override
   public boolean canPlaceBlockOnSide(World world, int x, int y, int z, Side side) {
      return this.logic.canPlaceBlockOnSide(world, x, y, z, side);
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return this.logic.canPlaceBlockAt(world, x, y, z);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      return this.logic.onBlockRightClicked(world, x, y, z, player, side, xHit, yHit);
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      this.logic.onActivatorInteract(world, x, y, z, activator, direction);
   }

   @Override
   public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
      this.logic.onEntityWalking(world, x, y, z, entity);
   }

   @Override
   public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      this.logic.onBlockLeftClicked(world, x, y, z, player, side, xHit, yHit);
   }

   @Override
   public void handleEntityInside(World world, int x, int y, int z, Entity entity, Vec3 entityVelocity) {
      this.logic.handleEntityInside(world, x, y, z, entity, entityVelocity);
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      return this.logic.getBlockBoundsFromState(world, x, y, z);
   }

   @Override
   public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
      return this.logic.getSignal(worldSource, x, y, z, side);
   }

   @Override
   public boolean isSignalSource() {
      return this.logic.isSignalSource();
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      this.logic.onEntityCollidedWithBlock(world, x, y, z, entity);
   }

   @Override
   public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
      return this.logic.getDirectSignal(world, x, y, z, side);
   }

   @Override
   public void harvestBlock(World world, Player player, int x, int y, int z, int meta, TileEntity tileEntity) {
      this.logic.harvestBlock(world, player, x, y, z, meta, tileEntity);
   }

   @Override
   public void dropBlockWithCause(World world, EnumDropCause cause, int x, int y, int z, int meta, TileEntity tileEntity, Player player) {
      this.logic.dropBlockWithCause(world, cause, x, y, z, meta, tileEntity, player);
   }

   @Override
   public ISupport getSupport(World world, int x, int y, int z, Side side) {
      return this.logic.getSupport(world, x, y, z, side);
   }

   @Override
   public boolean canBlockStay(World world, int x, int y, int z) {
      return this.logic.canBlockStay(world, x, y, z);
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
      this.logic.onBlockPlacedByMob(world, x, y, z, side, mob, xPlaced, yPlaced);
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      this.logic.onBlockPlacedOnSide(world, x, y, z, side, xPlaced, yPlaced);
   }

   @Override
   public String getLanguageKey(int meta) {
      return this.logic.getLanguageKey(meta);
   }

   @Override
   public void triggerEvent(World world, int x, int y, int z, int index, int data) {
      this.logic.triggerEvent(world, x, y, z, index, data);
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return this.logic.getPistonPushReaction(world, x, y, z);
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      return this.logic.getPlacedBlockMetadata(player, stack, world, x, y, z, side, xPlaced, yPlaced);
   }

   @Override
   public boolean isClimbable(World world, int x, int y, int z) {
      return this.logic.isClimbable(world, x, y, z);
   }

   public static boolean hasLogicClass(@Nullable Block<?> block, Class<?> logicClass) {
      return block != null && logicClass.isAssignableFrom(block.getLogic().getClass());
   }
}
