package net.minecraft.core.block.material;

public class MaterialGas extends Material {
   public MaterialGas(MaterialColor mapcolor) {
      super(mapcolor);
      this.replaceable();
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
