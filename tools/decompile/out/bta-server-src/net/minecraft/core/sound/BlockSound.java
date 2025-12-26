package net.minecraft.core.sound;

public class BlockSound {
   private final String stepSound;
   private final String breakSound;
   private final float volume;
   private final float pitch;

   public BlockSound(String stepSound, String breakSound, float volume, float pitch) {
      this.stepSound = stepSound;
      this.breakSound = breakSound;
      this.volume = volume;
      this.pitch = pitch;
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public String getStepSoundName() {
      return this.stepSound;
   }

   public String getBreakSoundName() {
      return this.breakSound;
   }
}
