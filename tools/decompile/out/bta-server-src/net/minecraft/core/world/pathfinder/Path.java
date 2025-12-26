package net.minecraft.core.world.pathfinder;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.phys.Vec3;

public class Path {
   private final Node[] nodes;
   public final int length;
   private int index;

   public Path(Node[] nodes) {
      this.nodes = nodes;
      this.length = nodes.length;
   }

   public void next() {
      this.index++;
   }

   public boolean isDone() {
      return this.index >= this.nodes.length;
   }

   public Node last() {
      return this.length > 0 ? this.nodes[this.length - 1] : null;
   }

   public Vec3 getPos(Entity entity) {
      double x = this.nodes[this.index].x + (int)(entity.bbWidth + 2.0F) * 0.5;
      double y = this.nodes[this.index].y;
      double z = this.nodes[this.index].z + (int)(entity.bbWidth + 2.0F) * 0.5;
      return Vec3.getTempVec3(x, y, z);
   }
}
