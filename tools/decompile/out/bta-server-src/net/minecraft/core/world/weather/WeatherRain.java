package net.minecraft.core.world.weather;

public class WeatherRain extends Weather {
   public WeatherRain(int id) {
      super(id);
   }

   @Override
   public float[] modifyFogColor(float r, float g, float b, float intensity) {
      float f6 = 1.0F - intensity * 0.5F;
      float f8 = 1.0F - intensity * 0.4F;
      r *= f6;
      g *= f6;
      b *= f8;
      return new float[]{r, g, b};
   }
}
