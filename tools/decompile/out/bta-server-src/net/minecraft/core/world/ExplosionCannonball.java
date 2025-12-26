package net.minecraft.core.world;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.chunk.ChunkPosition;

public class ExplosionCannonball extends Explosion {
   public ExplosionCannonball(World world, Entity entity, double d, double d1, double d2, float f) {
      super(world, entity, d, d1, d2, f);
   }

   @Override
   public void explode() {
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

      this.explosionSize *= 2.0F;
      int k = MathHelper.floor(this.explosionX - this.explosionSize - 1.0);
      int i1 = MathHelper.floor(this.explosionX + this.explosionSize + 1.0);
      int k1 = MathHelper.floor(this.explosionY - this.explosionSize - 1.0);
      int l1 = MathHelper.floor(this.explosionY + this.explosionSize + 1.0);
      int i2 = MathHelper.floor(this.explosionZ - this.explosionSize - 1.0);
      int j2 = MathHelper.floor(this.explosionZ + this.explosionSize + 1.0);
      List<Entity> list = this.worldObj.getEntitiesWithinAABB(Entity.class, AABB.getTemporaryBB(k, k1, i2, i1, l1, j2));
      Vec3 vec3 = Vec3.getTempVec3(this.explosionX, this.explosionY, this.explosionZ);

      for (int k2 = 0; k2 < list.size(); k2++) {
         Entity entity = list.get(k2);
         double d4 = entity.distanceTo(this.explosionX, this.explosionY, this.explosionZ) / this.explosionSize;
         if (d4 <= 1.0) {
            double d6 = entity.x - this.explosionX;
            double d8 = entity.y - this.explosionY;
            double d10 = entity.z - this.explosionZ;
            double d11 = MathHelper.sqrt(d6 * d6 + d8 * d8 + d10 * d10);
            d6 *= 2.5;
            d8 /= d11;
            d10 *= 2.5;
            double d12 = this.worldObj.getSeenPercent(vec3, entity.bb);
            double d13 = (1.0 - d4) * d12;
            entity.hurt(this.exploder, (int)((d13 * d13 + d13) / 2.0 * 8.0 * this.explosionSize + 1.0), DamageType.BLAST);
            entity.fling(d6 * d13, d8 * d13 + 0.5, d10 * d13, 1.0F);
         }
      }

      ArrayList<ChunkPosition> arraylist = new ArrayList<>(this.destroyedBlockPositions);
      if (this.isFlaming) {
         for (int l2 = arraylist.size() - 1; l2 >= 0; l2--) {
            ChunkPosition chunkposition = arraylist.get(l2);
            int i3 = chunkposition.x;
            int j3 = chunkposition.y;
            int k3 = chunkposition.z;
            int l3 = this.worldObj.getBlockId(i3, j3, k3);
            int i4 = this.worldObj.getBlockId(i3, j3 - 1, k3);
            if (l3 == 0 && Blocks.solid[i4] && this.ExplosionRNG.nextInt(3) == 0) {
               this.worldObj.setBlockWithNotify(i3, j3, k3, Blocks.FIRE.id());
            }
         }
      }
   }

   @Override
   public void addEffects(boolean flag) {
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

      ArrayList<ChunkPosition> arraylist = new ArrayList<>(this.destroyedBlockPositions);

      for (int i = arraylist.size() - 1; i >= 0; i--) {
         ChunkPosition chunkposition = arraylist.get(i);
         int x = chunkposition.x;
         int y = chunkposition.y;
         int z = chunkposition.z;
         Block<?> block = this.worldObj.getBlock(x, y, z);
         TileEntity tileEntity = this.worldObj.getTileEntity(x, y, z);
         if (flag) {
            double d = (double)x + this.worldObj.rand.nextFloat();
            double d1 = (double)y + this.worldObj.rand.nextFloat();
            double d2 = (double)z + this.worldObj.rand.nextFloat();
            double d3 = d - this.explosionX;
            double d4 = d1 - this.explosionY;
            double d5 = d2 - this.explosionZ;
            double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.5 / (d6 / this.explosionSize + 0.1);
            d7 *= this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F;
            d3 *= d7;
            d4 *= d7;
            d5 *= d7;
            this.worldObj.spawnParticle("explode", (d + this.explosionX) / 2.0, (d1 + this.explosionY) / 2.0, (d2 + this.explosionZ) / 2.0, d3, d4, d5, 0);
            this.worldObj.spawnParticle("smoke", d, d1, d2, d3, d4, d5, 0);
         }

         if (block != null
            && (
               block == Blocks.GLASS
                  || block.getMaterial() == Material.topSnow
                  || block.getMaterial() == Material.plant
                  || block.getMaterial() == Material.web
                  || block.getMaterial() == Material.decoration
                  || block.getMaterial() == Material.ice
            )) {
            block.dropBlockWithCause(this.worldObj, EnumDropCause.EXPLOSION, x, y, z, this.worldObj.getBlockMetadata(x, y, z), tileEntity, null);
            this.worldObj.setBlockWithNotify(x, y, z, 0);
            block.onBlockDestroyedByExplosion(this.worldObj, x, y, z);
         }
      }
   }
}
