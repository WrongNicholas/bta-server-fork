package net.minecraft.core.block.material;

public class MaterialDecoration extends Material {
   public MaterialDecoration(MaterialColor mapcolor) {
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
