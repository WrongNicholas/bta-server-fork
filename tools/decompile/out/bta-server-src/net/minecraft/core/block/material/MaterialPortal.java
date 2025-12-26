package net.minecraft.core.block.material;

public class MaterialPortal extends Material {
   public MaterialPortal(MaterialColor mapcolor) {
      super(mapcolor);
   }

   @Override
   public boolean isSolid() {
      return false;
   }

   @Override
   public boolean blocksLight() {
      return false;
   }

   @Override
   public boolean blocksMotion() {
      return false;
   }
}
