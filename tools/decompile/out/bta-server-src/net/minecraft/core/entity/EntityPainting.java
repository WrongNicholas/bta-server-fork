package net.minecraft.core.entity;

import com.mojang.nbt.tags.CompoundTag;
import java.util.HashMap;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.ArtType;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class EntityPainting extends Entity {
   public static final int DATA_ITEM_ID = 16;
   public static final int DATA_ITEM_METADATA_ID = 17;
   private int updateCounter = 0;
   public int direction = 0;
   public int blockX;
   public int blockY;
   public int blockZ;
   public ArtType art;
   private static final HashMap<String, NamespaceID> borderMaterialMap = new HashMap<>();

   public EntityPainting(World world) {
      super(world);
      this.heightOffset = 0.0F;
      this.art = ArtType.Kebab;
      this.setSize(0.5F, 0.5F);
   }

   @Override
   public boolean showBoundingBoxOnHover() {
      return true;
   }

   public EntityPainting(World world, int x, int y, int z, int side, String motive) {
      this(world);
      this.blockX = x;
      this.blockY = y;
      this.blockZ = z;
      this.art = ArtType.map.getOrDefault(motive, this.art);
      this.setDirection(side);
   }

   @Override
   protected void defineSynchedData() {
      this.entityData.define(16, (short)0, Short.class);
      this.entityData.define(17, (byte)0, Byte.class);
   }

   public void setDirection(int direction) {
      this.direction = direction;
      this.yRotO = this.yRot = direction * 90;
      float sizeX = this.art.sizeX;
      float sizeY = this.art.sizeY;
      float sizeZ = this.art.sizeX;
      if (direction != 0 && direction != 2) {
         sizeX = 0.5F;
      } else {
         sizeZ = 0.5F;
      }

      sizeX /= 32.0F;
      sizeY /= 32.0F;
      sizeZ /= 32.0F;
      double centerX = this.blockX + 0.5;
      double centerY = this.blockY + 0.5;
      double centerZ = this.blockZ + 0.5;
      float offsetFromWall = 0.53F;
      if (direction == 0) {
         centerZ -= offsetFromWall;
      }

      if (direction == 1) {
         centerX -= offsetFromWall;
      }

      if (direction == 2) {
         centerZ += offsetFromWall;
      }

      if (direction == 3) {
         centerX += offsetFromWall;
      }

      if (direction == 0) {
         centerX -= this.offsetFromCenter(this.art.sizeX);
      }

      if (direction == 1) {
         centerZ += this.offsetFromCenter(this.art.sizeX);
      }

      if (direction == 2) {
         centerX += this.offsetFromCenter(this.art.sizeX);
      }

      if (direction == 3) {
         centerZ -= this.offsetFromCenter(this.art.sizeX);
      }

      centerY += this.offsetFromCenter(this.art.sizeY);
      this.setPos(centerX, centerY, centerZ);
      float expand = -0.0F;
      this.bb
         .set(
            centerX - sizeX - expand,
            centerY - sizeY - expand,
            centerZ - sizeZ - expand,
            centerX + sizeX + expand,
            centerY + sizeY + expand,
            centerZ + sizeZ + expand
         );
      if (direction == 0 || direction == 2) {
         this.bb.minZ -= 0.01F;
         this.bb.maxZ += 0.01F;
      }

      if (direction == 1 || direction == 3) {
         this.bb.minX -= 0.01F;
         this.bb.maxX += 0.01F;
      }
   }

   private float offsetFromCenter(int i) {
      if (i == 32) {
         return 0.5F;
      } else {
         return i != 64 ? 0.0F : 0.5F;
      }
   }

   @Override
   public void tick() {
      if (this.updateCounter++ == 100 && !this.world.isClientSide) {
         this.updateCounter = 0;
         if (!this.canStay()) {
            this.drop();
            this.remove();
         }
      }
   }

   public boolean canStay() {
      if (!this.world.getCubes(this, this.bb).isEmpty()) {
         return false;
      } else {
         int xSize = this.art.sizeX / 16;
         int ySize = this.art.sizeY / 16;
         int xPosition = this.blockX;
         int yPosition = MathHelper.floor(this.y - this.art.sizeY / 32.0F);
         int zPosition = this.blockZ;
         if (this.direction == 0) {
            xPosition = MathHelper.floor(this.x - this.art.sizeX / 32.0F);
         }

         if (this.direction == 1) {
            zPosition = MathHelper.floor(this.z - this.art.sizeX / 32.0F);
         }

         if (this.direction == 2) {
            xPosition = MathHelper.floor(this.x - this.art.sizeX / 32.0F);
         }

         if (this.direction == 3) {
            zPosition = MathHelper.floor(this.z - this.art.sizeX / 32.0F);
         }

         for (int dx = 0; dx < xSize; dx++) {
            for (int dy = 0; dy < ySize; dy++) {
               Material material;
               if (this.direction != 0 && this.direction != 2) {
                  material = this.world.getBlockMaterial(this.blockX, yPosition + dy, zPosition + dx);
               } else {
                  material = this.world.getBlockMaterial(xPosition + dx, yPosition + dy, this.blockZ);
               }

               if (!material.isSolid()) {
                  return false;
               }
            }
         }

         for (Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb)) {
            if (entity instanceof EntityPainting) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public boolean isPickable() {
      return true;
   }

   @Override
   public boolean hurt(Entity entity, int i, DamageType type) {
      if (!this.removed && !this.world.isClientSide) {
         this.markHurt();
         if (!(entity instanceof Player) || ((Player)entity).getGamemode().dropBlockOnBreak()) {
            this.drop();
            this.remove();
         }

         this.remove();
         this.world.playBlockSoundEffect(null, this.x, this.y + this.heightOffset, this.z, Blocks.PLANKS_OAK, EnumBlockSoundEffectType.MINE);
      }

      return true;
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      tag.putByte("Dir", (byte)this.direction);
      tag.putString("Motive", this.art.key);
      tag.putInt("TileX", this.blockX);
      tag.putInt("TileY", this.blockY);
      tag.putInt("TileZ", this.blockZ);
      ItemStack stack = this.getBorderStack();
      if (stack != null) {
         tag.putBoolean("itemExists", true);
         tag.putInt("itemID", stack.itemID);
         tag.putInt("itemMeta", stack.getMetadata());
      } else {
         tag.putBoolean("itemExists", false);
      }
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      this.direction = tag.getByte("Dir");
      this.blockX = tag.getInteger("TileX");
      this.blockY = tag.getInteger("TileY");
      this.blockZ = tag.getInteger("TileZ");
      String motive = tag.getString("Motive");
      this.art = ArtType.map.getOrDefault(motive, ArtType.Kebab);
      this.setDirection(this.direction);
      if (tag.getBoolean("itemExists")) {
         int id = tag.getInteger("itemID");
         int meta = tag.getInteger("itemMeta");
         this.setStack(new ItemStack(id, 1, meta));
      } else {
         this.setStack(null);
      }
   }

   @Override
   public void move(double xd, double yd, double zd) {
      if (!this.world.isClientSide && xd * xd + yd * yd + zd * zd > 0.0) {
         this.drop();
         this.remove();
      }
   }

   @Override
   public void push(double x, double y, double z) {
      if (!this.world.isClientSide && x * x + y * y + z * z > 0.0) {
         this.drop();
         this.remove();
      }
   }

   protected void drop() {
      if (!this.removed) {
         Direction d;
         switch (this.direction) {
            case 0:
               d = Direction.NORTH;
               break;
            case 1:
               d = Direction.WEST;
               break;
            case 2:
               d = Direction.SOUTH;
               break;
            case 3:
            default:
               d = Direction.EAST;
         }

         double x = this.x + d.getOffsetX() / 4.0;
         double y = this.y + d.getOffsetY() / 4.0;
         double z = this.z + d.getOffsetZ() / 4.0;
         this.world.entityJoinedWorld(new EntityItem(this.world, x, y, z, new ItemStack(Items.PAINTING)));
         if (this.getBorderStack() != null) {
            this.world.entityJoinedWorld(new EntityItem(this.world, x, y, z, this.getBorderStack()));
            this.setStack(null);
         }
      }
   }

   @Override
   public boolean interact(@NotNull Player player) {
      if (player.getHeldItem() != null) {
         ItemStack stack = player.getHeldItem();
         ItemStack paintingStack = this.getBorderStack();
         if (getBorder(stack) == null) {
            return false;
         } else {
            this.setStack(new ItemStack(stack.getItem(), 1, stack.getMetadata()));
            stack.consumeItem(player);
            if (stack.stackSize <= 0) {
               player.inventory.setItem(player.inventory.getCurrentItemIndex(), null);
            }

            if (paintingStack != null && player.getGamemode().consumeBlocks()) {
               player.inventory.insertItem(paintingStack, true);
               if (paintingStack.stackSize > 0) {
                  player.dropPlayerItem(paintingStack);
               }
            }

            return true;
         }
      } else if (this.getBorderStack() != null) {
         if (player.getGamemode().consumeBlocks()) {
            player.inventory.insertItem(this.getBorderStack(), true);
         }

         this.setStack(null);
         return true;
      } else {
         return super.interact(player);
      }
   }

   public ItemStack getBorderStack() {
      int id = this.entityData.getShort(16);
      int meta = Byte.toUnsignedInt(this.entityData.getByte(17));
      return id != 0 ? new ItemStack(id, 1, meta) : null;
   }

   public void setStack(ItemStack stack) {
      if (stack != null) {
         this.entityData.set(16, (short)stack.itemID);
         this.entityData.set(17, (byte)stack.getMetadata());
      } else {
         this.entityData.set(16, (short)0);
         this.entityData.set(17, (byte)0);
      }
   }

   public static void addBorder(ItemStack stack, NamespaceID imagePath) {
      borderMaterialMap.put(stack.itemID + "|" + stack.getMetadata(), imagePath.makePermanent());
   }

   public static NamespaceID getBorder(ItemStack stack) {
      return borderMaterialMap.get(stack.itemID + "|" + stack.getMetadata());
   }

   static {
      addBorder(Items.INGOT_IRON.getDefaultStack(), NamespaceID.getPermanent("minecraft", "border_iron"));
      addBorder(Items.INGOT_GOLD.getDefaultStack(), NamespaceID.getPermanent("minecraft", "border_gold"));
      addBorder(Items.INGOT_STEEL.getDefaultStack(), NamespaceID.getPermanent("minecraft", "border_steel"));
      addBorder(new ItemStack(Items.DYE, 1, 4), NamespaceID.getPermanent("minecraft", "border_lapis"));
      addBorder(Items.DIAMOND.getDefaultStack(), NamespaceID.getPermanent("minecraft", "border_diamond"));
      addBorder(Items.DUST_REDSTONE.getDefaultStack(), NamespaceID.getPermanent("minecraft", "border_redstone"));
      addBorder(Items.OLIVINE.getDefaultStack(), NamespaceID.getPermanent("minecraft", "border_olivine"));
      addBorder(Items.QUARTZ.getDefaultStack(), NamespaceID.getPermanent("minecraft", "border_quartz"));
   }
}
