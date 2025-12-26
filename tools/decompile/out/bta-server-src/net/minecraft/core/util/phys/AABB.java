package net.minecraft.core.util.phys;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.util.helper.Side;

public class AABB {
   private static final List<AABB> pool = new ArrayList<>();
   private static int poolPointer = 0;
   public double minX;
   public double minY;
   public double minZ;
   public double maxX;
   public double maxY;
   public double maxZ;

   public static AABB getPermanentBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public static void deinitializePool() {
      pool.clear();
      poolPointer = 0;
   }

   public static void initializePool() {
      poolPointer = 0;
   }

   public static AABB getTemporaryBB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      if (poolPointer >= pool.size()) {
         pool.add(getPermanentBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
      }

      return pool.get(poolPointer++).set(minX, minY, minZ, maxX, maxY, maxZ);
   }

   private AABB(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
   }

   public AABB set(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
      return this;
   }

   public AABB expand(double stretchX, double stretchY, double stretchZ) {
      double minX = this.minX;
      double minY = this.minY;
      double minZ = this.minZ;
      double maxX = this.maxX;
      double maxY = this.maxY;
      double maxZ = this.maxZ;
      if (stretchX < 0.0) {
         minX += stretchX;
      }

      if (stretchX > 0.0) {
         maxX += stretchX;
      }

      if (stretchY < 0.0) {
         minY += stretchY;
      }

      if (stretchY > 0.0) {
         maxY += stretchY;
      }

      if (stretchZ < 0.0) {
         minZ += stretchZ;
      }

      if (stretchZ > 0.0) {
         maxZ += stretchZ;
      }

      return getTemporaryBB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public AABB grow(double d, double d1, double d2) {
      double d3 = this.minX - d;
      double d4 = this.minY - d1;
      double d5 = this.minZ - d2;
      double d6 = this.maxX + d;
      double d7 = this.maxY + d1;
      double d8 = this.maxZ + d2;
      return getTemporaryBB(d3, d4, d5, d6, d7, d8);
   }

   public AABB cloneMove(double offX, double offY, double offZ) {
      return getTemporaryBB(this.minX + offX, this.minY + offY, this.minZ + offZ, this.maxX + offX, this.maxY + offY, this.maxZ + offZ);
   }

   public double clipXCollide(AABB aabb, double d) {
      if (aabb.maxY <= this.minY || aabb.minY >= this.maxY) {
         return d;
      } else if (!(aabb.maxZ <= this.minZ) && !(aabb.minZ >= this.maxZ)) {
         if (d > 0.0 && aabb.maxX <= this.minX) {
            double d1 = this.minX - aabb.maxX;
            if (d1 < d) {
               d = d1;
            }
         }

         if (d < 0.0 && aabb.minX >= this.maxX) {
            double d2 = this.maxX - aabb.minX;
            if (d2 > d) {
               d = d2;
            }
         }

         return d;
      } else {
         return d;
      }
   }

   public double clipYCollide(AABB aabb, double d) {
      if (aabb.maxX <= this.minX || aabb.minX >= this.maxX) {
         return d;
      } else if (!(aabb.maxZ <= this.minZ) && !(aabb.minZ >= this.maxZ)) {
         if (d > 0.0 && aabb.maxY <= this.minY) {
            double d1 = this.minY - aabb.maxY;
            if (d1 < d) {
               d = d1;
            }
         }

         if (d < 0.0 && aabb.minY >= this.maxY) {
            double d2 = this.maxY - aabb.minY;
            if (d2 > d) {
               d = d2;
            }
         }

         return d;
      } else {
         return d;
      }
   }

   public double clipZCollide(AABB aabb, double d) {
      if (aabb.maxX <= this.minX || aabb.minX >= this.maxX) {
         return d;
      } else if (!(aabb.maxY <= this.minY) && !(aabb.minY >= this.maxY)) {
         if (d > 0.0 && aabb.maxZ <= this.minZ) {
            double d1 = this.minZ - aabb.maxZ;
            if (d1 < d) {
               d = d1;
            }
         }

         if (d < 0.0 && aabb.minZ >= this.maxZ) {
            double d2 = this.maxZ - aabb.minZ;
            if (d2 > d) {
               d = d2;
            }
         }

         return d;
      } else {
         return d;
      }
   }

   public boolean intersects(AABB aabb) {
      return aabb.maxX > this.minX && aabb.minX < this.maxX && aabb.maxY > this.minY && aabb.minY < this.maxY && aabb.maxZ > this.minZ && aabb.minZ < this.maxZ;
   }

   public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      return maxX > this.minX && minX < this.maxX && maxY > this.minY && minY < this.maxY && maxZ > this.minZ && minZ < this.maxZ;
   }

   public boolean intersectsInner(AABB aabb) {
      return aabb.maxX >= this.minX
         && aabb.minX <= this.maxX
         && aabb.maxY >= this.minY
         && aabb.minY <= this.maxY
         && aabb.maxZ >= this.minZ
         && aabb.minZ <= this.maxZ;
   }

   public AABB move(double offX, double offY, double offZ) {
      this.minX += offX;
      this.minY += offY;
      this.minZ += offZ;
      this.maxX += offX;
      this.maxY += offY;
      this.maxZ += offZ;
      return this;
   }

   public boolean contains(Vec3 vec3) {
      if (vec3.x <= this.minX || vec3.x >= this.maxX) {
         return false;
      } else {
         return !(vec3.y <= this.minY) && !(vec3.y >= this.maxY) ? vec3.z > this.minZ && vec3.z < this.maxZ : false;
      }
   }

   public double getSize() {
      double d = this.maxX - this.minX;
      double d1 = this.maxY - this.minY;
      double d2 = this.maxZ - this.minZ;
      return (d + d1 + d2) / 3.0;
   }

   public AABB shrink(double n, double n2, double n3) {
      double minX = this.minX;
      double minY = this.minY;
      double minZ = this.minZ;
      double maxX = this.maxX;
      double maxY = this.maxY;
      double maxZ = this.maxZ;
      if (n < 0.0) {
         minX -= n;
      }

      if (n > 0.0) {
         maxX -= n;
      }

      if (n2 < 0.0) {
         minY -= n2;
      }

      if (n2 > 0.0) {
         maxY -= n2;
      }

      if (n3 < 0.0) {
         minZ -= n3;
      }

      if (n3 > 0.0) {
         maxZ -= n3;
      }

      return getTemporaryBB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public AABB getInsetBoundingBox(double d, double d1, double d2) {
      double d3 = this.minX + d;
      double d4 = this.minY + d1;
      double d5 = this.minZ + d2;
      double d6 = this.maxX - d;
      double d7 = this.maxY - d1;
      double d8 = this.maxZ - d2;
      return getTemporaryBB(d3, d4, d5, d6, d7, d8);
   }

   public AABB copy() {
      return getTemporaryBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public HitResult clip(Vec3 start, Vec3 end) {
      Vec3 vec32 = start.clipX(end, this.minX);
      Vec3 vec3d3 = start.clipX(end, this.maxX);
      Vec3 vec34 = start.clipY(end, this.minY);
      Vec3 vec35 = start.clipY(end, this.maxY);
      Vec3 vec36 = start.clipZ(end, this.minZ);
      Vec3 vec37 = start.clipZ(end, this.maxZ);
      if (!this.containsX(vec32)) {
         vec32 = null;
      }

      if (!this.containsX(vec3d3)) {
         vec3d3 = null;
      }

      if (!this.containsY(vec34)) {
         vec34 = null;
      }

      if (!this.containsY(vec35)) {
         vec35 = null;
      }

      if (!this.containsZ(vec36)) {
         vec36 = null;
      }

      if (!this.containsZ(vec37)) {
         vec37 = null;
      }

      Vec3 vec38 = null;
      if (vec32 != null && (vec38 == null || start.distanceToSquared(vec32) < start.distanceToSquared(vec38))) {
         vec38 = vec32;
      }

      if (vec3d3 != null && (vec38 == null || start.distanceToSquared(vec3d3) < start.distanceToSquared(vec38))) {
         vec38 = vec3d3;
      }

      if (vec34 != null && (vec38 == null || start.distanceToSquared(vec34) < start.distanceToSquared(vec38))) {
         vec38 = vec34;
      }

      if (vec35 != null && (vec38 == null || start.distanceToSquared(vec35) < start.distanceToSquared(vec38))) {
         vec38 = vec35;
      }

      if (vec36 != null && (vec38 == null || start.distanceToSquared(vec36) < start.distanceToSquared(vec38))) {
         vec38 = vec36;
      }

      if (vec37 != null && (vec38 == null || start.distanceToSquared(vec37) < start.distanceToSquared(vec38))) {
         vec38 = vec37;
      }

      if (vec38 == null) {
         return null;
      } else {
         Side side = Side.NONE;
         if (vec38 == vec32) {
            side = Side.WEST;
         }

         if (vec38 == vec3d3) {
            side = Side.EAST;
         }

         if (vec38 == vec34) {
            side = Side.BOTTOM;
         }

         if (vec38 == vec35) {
            side = Side.TOP;
         }

         if (vec38 == vec36) {
            side = Side.NORTH;
         }

         if (vec38 == vec37) {
            side = Side.SOUTH;
         }

         return new HitResult(0, 0, 0, side, vec38);
      }
   }

   private boolean containsX(Vec3 vec3) {
      return vec3 == null ? false : vec3.y >= this.minY && vec3.y <= this.maxY && vec3.z >= this.minZ && vec3.z <= this.maxZ;
   }

   private boolean containsY(Vec3 vec3) {
      return vec3 == null ? false : vec3.x >= this.minX && vec3.x <= this.maxX && vec3.z >= this.minZ && vec3.z <= this.maxZ;
   }

   private boolean containsZ(Vec3 vec3) {
      return vec3 == null ? false : vec3.x >= this.minX && vec3.x <= this.maxX && vec3.y >= this.minY && vec3.y <= this.maxY;
   }

   public void setBB(AABB aabb) {
      this.minX = aabb.minX;
      this.minY = aabb.minY;
      this.minZ = aabb.minZ;
      this.maxX = aabb.maxX;
      this.maxY = aabb.maxY;
      this.maxZ = aabb.maxZ;
   }

   @Override
   public String toString() {
      return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
   }
}
