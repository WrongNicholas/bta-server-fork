package net.minecraft.core.block.material;

public class MaterialLiquid extends Material {
   public MaterialLiquid(MaterialColor mapcolor) {
      super(mapcolor);
      this.replaceable();
      this.destroyOnPush();
   }

   @Override
   public boolean isLiquid() {
      return true;
   }

   @Override
   public boolean blocksMotion() {
      return false;
   }

   @Override
   public boolean isSolid() {
      return false;
   }
}
