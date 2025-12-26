package net.minecraft.core.util.phys;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.Side;

public class HitResult {
   public final HitResult.HitType hitType;
   public final int x;
   public final int y;
   public final int z;
   public final Side side;
   public final Vec3 location;
   public final Entity entity;

   public HitResult(int x, int y, int z, Side side, Vec3 vec3) {
      this.hitType = HitResult.HitType.TILE;
      this.x = x;
      this.y = y;
      this.z = z;
      this.side = side;
      this.location = Vec3.getTempVec3(vec3.x, vec3.y, vec3.z);
      this.entity = null;
   }

   public HitResult(Entity entity) {
      this.x = 0;
      this.y = 0;
      this.z = 0;
      this.side = Side.NONE;
      this.hitType = HitResult.HitType.ENTITY;
      this.entity = entity;
      this.location = Vec3.getTempVec3(entity.x, entity.y, entity.z);
   }

   public double distanceToSquared(Entity entity) {
      double diffX = this.location.x - entity.x;
      double diffY = this.location.y - entity.y;
      double diffZ = this.location.z - entity.z;
      return diffX * diffX + diffY * diffY + diffZ * diffZ;
   }

   public double distanceTo(Entity entity) {
      return Math.sqrt(this.distanceToSquared(entity));
   }

   public static enum HitType {
      TILE,
      ENTITY;
   }
}
