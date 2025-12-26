package net.minecraft.core.enums;

public enum LightLayer {
   Sky(15),
   Block(0);

   public final int defaultLightLevel;

   private LightLayer(int defaultLightLevel) {
      this.defaultLightLevel = defaultLightLevel;
   }
}
