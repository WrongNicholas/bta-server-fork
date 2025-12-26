package net.minecraft.core.util.phys;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.util.helper.MathHelper;

public class Vec3 {
   private static final List<Vec3> pool = new ArrayList<>();
   private static int poolPointer = 0;
   public double x;
   public double y;
   public double z;

   public static Vec3 getPermanentVec3(double x, double y, double z) {
      return new Vec3(x, y, z);
   }

   public static void deinitializePool() {
      pool.clear();
      poolPointer = 0;
   }

   public static void initializePool() {
      poolPointer = 0;
   }

   public static Vec3 getTempVec3(double x, double y, double z) {
      if (poolPointer >= pool.size()) {
         pool.add(getPermanentVec3(0.0, 0.0, 0.0));
      }

      return pool.get(poolPointer++).set(x, y, z);
   }

   public Vec3 makePermanent() {
      if (pool.contains(this)) {
         pool.remove(this);
         poolPointer--;
      }

      return this;
   }

   private Vec3(double x, double y, double z) {
      if (x == -0.0) {
         x = 0.0;
      }

      if (y == -0.0) {
         y = 0.0;
      }

      if (z == -0.0) {
         z = 0.0;
      }

      this.x = x;
      this.y = y;
      this.z = z;
   }

   private Vec3 set(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
      return this;
   }

   public Vec3 vectorTo(Vec3 vec3) {
      return getTempVec3(vec3.x - this.x, vec3.y - this.y, vec3.z - this.z);
   }

   public Vec3 normalize() {
      double d = MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      return d < 1.0E-4 ? getTempVec3(0.0, 0.0, 0.0) : this.scale(1.0 / d);
   }

   public double dotProduct(Vec3 vec3) {
      return this.x * vec3.x + this.y * vec3.y + this.z * vec3.z;
   }

   public Vec3 crossProduct(Vec3 vec3) {
      return getTempVec3(this.y * vec3.z - this.z * vec3.y, this.z * vec3.x - this.x * vec3.z, this.x * vec3.y - this.y * vec3.x);
   }

   public Vec3 add(double x, double y, double z) {
      return getTempVec3(this.x + x, this.y + y, this.z + z);
   }

   public Vec3 scale(double scalar) {
      return getTempVec3(this.x * scalar, this.y * scalar, this.z * scalar);
   }

   public double distanceTo(Vec3 vec3) {
      double diffX = vec3.x - this.x;
      double diffY = vec3.y - this.y;
      double diffZ = vec3.z - this.z;
      return MathHelper.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
   }

   public double distanceToSquared(Vec3 vec3) {
      double diffX = vec3.x - this.x;
      double diffY = vec3.y - this.y;
      double diffZ = vec3.z - this.z;
      return diffX * diffX + diffY * diffY + diffZ * diffZ;
   }

   public double distanceToSquared(double x, double y, double z) {
      double diffX = x - this.x;
      double diffY = y - this.y;
      double diffZ = z - this.z;
      return diffX * diffX + diffY * diffY + diffZ * diffZ;
   }

   public double length() {
      return MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public Vec3 clipX(Vec3 vec3, double d) {
      double diffX = vec3.x - this.x;
      double diffY = vec3.y - this.y;
      double diffZ = vec3.z - this.z;
      if (diffX * diffX < 1.0E-7F) {
         return null;
      } else {
         double d4 = (d - this.x) / diffX;
         return !(d4 < 0.0) && !(d4 > 1.0) ? getTempVec3(this.x + diffX * d4, this.y + diffY * d4, this.z + diffZ * d4) : null;
      }
   }

   public Vec3 clipY(Vec3 vec3, double d) {
      double diffX = vec3.x - this.x;
      double diffY = vec3.y - this.y;
      double diffZ = vec3.z - this.z;
      if (diffY * diffY < 1.0E-7F) {
         return null;
      } else {
         double d4 = (d - this.y) / diffY;
         return !(d4 < 0.0) && !(d4 > 1.0) ? getTempVec3(this.x + diffX * d4, this.y + diffY * d4, this.z + diffZ * d4) : null;
      }
   }

   public Vec3 clipZ(Vec3 vec3, double d) {
      double diffX = vec3.x - this.x;
      double diffY = vec3.y - this.y;
      double diffZ = vec3.z - this.z;
      if (diffZ * diffZ < 1.0E-7F) {
         return null;
      } else {
         double d4 = (d - this.z) / diffZ;
         return !(d4 < 0.0) && !(d4 > 1.0) ? getTempVec3(this.x + diffX * d4, this.y + diffY * d4, this.z + diffZ * d4) : null;
      }
   }

   @Override
   public String toString() {
      return "(" + this.x + ", " + this.y + ", " + this.z + ")";
   }

   public Vec3 lerp(Vec3 vec3, double progress) {
      return getTempVec3(this.x + (vec3.x - this.x) * progress, this.y + (vec3.y - this.y) * progress, this.z + (vec3.z - this.z) * progress);
   }

   public void rotateAroundX(float radians) {
      float cos = MathHelper.cos(radians);
      float sin = MathHelper.sin(radians);
      double x = this.x;
      double y = this.y * cos + this.z * sin;
      double z = this.z * cos - this.y * sin;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void rotateAroundY(float radians) {
      float cos = MathHelper.cos(radians);
      float sin = MathHelper.sin(radians);
      double x = this.x * cos + this.z * sin;
      double y = this.y;
      double z = this.z * cos - this.x * sin;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void rotateAroundZ(float radians) {
      float cos = MathHelper.cos(radians);
      float sin = MathHelper.sin(radians);
      double x = this.x * cos + this.y * sin;
      double y = this.y * cos - this.x * sin;
      double z = this.z;
      this.x = x;
      this.y = y;
      this.z = z;
   }
}
