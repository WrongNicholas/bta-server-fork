package net.minecraft.core.world;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.chunk.ChunkPosition;

public class Explosion {
   public boolean isFlaming = false;
   protected Random ExplosionRNG = new Random();
   protected World worldObj;
   public double explosionX;
   public double explosionY;
   public double explosionZ;
   public Entity exploder;
   public float explosionSize;
   public Set<ChunkPosition> destroyedBlockPositions = new HashSet<>();
   public boolean destroyBlocks;

   public Explosion(World world, Entity entity, double x, double y, double z, float explosionSize) {
      this.worldObj = world;
      this.exploder = entity;
      this.explosionSize = explosionSize;
      this.explosionX = x;
      this.explosionY = y;
      this.explosionZ = z;
      this.destroyBlocks = true;
      if (!world.getGameRuleValue(GameRules.MOB_GRIEFING)) {
         this.destroyBlocks = entity == null || entity instanceof Player;
      }
   }

   public void explode() {
      this.calculateBlocksToDestroy();
      this.damageEntities();
      if (this.destroyBlocks && this.isFlaming) {
         this.createFire();
      }
   }

   public void addEffects(boolean particles) {
      if (!this.worldObj.isClientSide) {
         this.worldObj
            .playSoundEffect(
               null,
               SoundCategory.WORLD_SOUNDS,
               this.explosionX,
               this.explosionY,
               this.explosionZ,
               "random.explode",
               4.0F,
               (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F
            );
      }

      List<ChunkPosition> arraylist = new ArrayList<>(this.destroyedBlockPositions);

      for (int i = arraylist.size() - 1; i >= 0; i--) {
         ChunkPosition chunkposition = arraylist.get(i);
         int x = chunkposition.x;
         int y = chunkposition.y;
         int z = chunkposition.z;
         TileEntity tileEntity = this.worldObj.getTileEntity(x, y, z);
         if (particles) {
            double xPos = (double)x + this.worldObj.rand.nextFloat();
            double yPos = (double)y + this.worldObj.rand.nextFloat();
            double zPos = (double)z + this.worldObj.rand.nextFloat();
            double d3 = xPos - this.explosionX;
            double d4 = yPos - this.explosionY;
            double d5 = zPos - this.explosionZ;
            double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.5 / (d6 / this.explosionSize + 0.1);
            d7 *= this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F;
            d3 *= d7;
            d4 *= d7;
            d5 *= d7;
            this.worldObj
               .spawnParticle("explode", (xPos + this.explosionX) / 2.0, (yPos + this.explosionY) / 2.0, (zPos + this.explosionZ) / 2.0, d3, d4, d5, 0);
            this.worldObj.spawnParticle("smoke", xPos, yPos, zPos, d3, d4, d5, 0);
         }

         if (this.destroyBlocks) {
            Block<?> block = this.worldObj.getBlock(x, y, z);
            if (block != null) {
               block.dropBlockWithCause(this.worldObj, EnumDropCause.EXPLOSION, x, y, z, this.worldObj.getBlockMetadata(x, y, z), tileEntity, null);
               this.worldObj.setBlockWithNotify(x, y, z, 0);
               block.onBlockDestroyedByExplosion(this.worldObj, x, y, z);
            }
         }
      }
   }

   protected void calculateBlocksToDestroy() {
      int i = 16;

      for (int j = 0; j < i; j++) {
         for (int l = 0; l < i; l++) {
            for (int j1 = 0; j1 < i; j1++) {
               if (j == 0 || j == i - 1 || l == 0 || l == i - 1 || j1 == 0 || j1 == i - 1) {
                  double d = j / (i - 1.0F) * 2.0F - 1.0F;
                  double d1 = l / (i - 1.0F) * 2.0F - 1.0F;
                  double d2 = j1 / (i - 1.0F) * 2.0F - 1.0F;
                  double d3 = Math.sqrt(d * d + d1 * d1 + d2 * d2);
                  d /= d3;
                  d1 /= d3;
                  d2 /= d3;
                  float f1 = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
                  double d5 = this.explosionX;
                  double d7 = this.explosionY;
                  double d9 = this.explosionZ;

                  for (float f2 = 0.3F; !(f1 <= 0.0F); f1 -= f2 * 0.75F) {
                     int j4 = MathHelper.floor(d5);
                     int k4 = MathHelper.floor(d7);
                     int l4 = MathHelper.floor(d9);
                     int i5 = this.worldObj.getBlockId(j4, k4, l4);
                     if (i5 > 0) {
                        f1 -= (Blocks.blocksList[i5].getBlastResistance(this.exploder) + 0.3F) * f2;
                     }

                     if (f1 > 0.0F) {
                        this.destroyedBlockPositions.add(new ChunkPosition(j4, k4, l4));
                     }

                     d5 += d * f2;
                     d7 += d1 * f2;
                     d9 += d2 * f2;
                  }
               }
            }
         }
      }
   }

   protected void damageEntities() {
      float explosionSize2 = this.explosionSize * 2.0F;
      int x1 = MathHelper.floor(this.explosionX - explosionSize2 - 1.0);
      int x2 = MathHelper.floor(this.explosionX + explosionSize2 + 1.0);
      int y1 = MathHelper.floor(this.explosionY - explosionSize2 - 1.0);
      int y2 = MathHelper.floor(this.explosionY + explosionSize2 + 1.0);
      int z1 = MathHelper.floor(this.explosionZ - explosionSize2 - 1.0);
      int z2 = MathHelper.floor(this.explosionZ + explosionSize2 + 1.0);
      List<Entity> list = new ArrayList<>(this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, AABB.getTemporaryBB(x1, y1, z1, x2, y2, z2)));
      Vec3 vec3 = Vec3.getTempVec3(this.explosionX, this.explosionY, this.explosionZ);

      for (Entity entity : list) {
         double d4 = entity.distanceTo(this.explosionX, this.explosionY, this.explosionZ) / explosionSize2;
         if (d4 <= 1.0) {
            double xComp = entity.x - this.explosionX;
            double yComp = entity.y - this.explosionY;
            double zComp = entity.z - this.explosionZ;
            double distance = MathHelper.sqrt(xComp * xComp + yComp * yComp + zComp * zComp);
            xComp /= distance;
            yComp /= distance;
            zComp /= distance;
            double d12 = this.worldObj.getSeenPercent(vec3, entity.bb);
            double d13 = (1.0 - d4) * d12;
            entity.hurt(this.exploder, (int)((d13 * d13 + d13) / 2.0 * 8.0 * explosionSize2 + 1.0), DamageType.BLAST);
            double flingForce = d13 * 2.0;
            entity.fling(xComp * flingForce, yComp * flingForce, zComp * flingForce, 1.0F);
         }
      }
   }

   protected void createFire() {
      List<ChunkPosition> arraylist = new ArrayList<>(this.destroyedBlockPositions);

      for (int l2 = arraylist.size() - 1; l2 >= 0; l2--) {
         ChunkPosition chunkposition = arraylist.get(l2);
         int x1 = chunkposition.x;
         int y1 = chunkposition.y;
         int z1 = chunkposition.z;
         if (this.worldObj.getBlockId(x1, y1, z1) == 0 && Blocks.solid[this.worldObj.getBlockId(x1, y1 - 1, z1)] && this.ExplosionRNG.nextInt(3) == 0) {
            this.worldObj.setBlockWithNotify(x1, y1, z1, Blocks.FIRE.id());
         }
      }
   }
}
