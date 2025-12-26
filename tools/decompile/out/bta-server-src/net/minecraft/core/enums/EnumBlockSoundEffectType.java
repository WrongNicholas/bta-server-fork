package net.minecraft.core.enums;

public enum EnumBlockSoundEffectType {
   STEP {
      @Override
      public float modifyVolume(float volume) {
         return volume * 0.15F;
      }

      @Override
      public float modifyPitch(float pitch) {
         return pitch;
      }
   },
   PLACE {
      @Override
      public float modifyVolume(float volume) {
         return (volume + 1.0F) / 2.0F;
      }

      @Override
      public float modifyPitch(float pitch) {
         return pitch * 0.8F;
      }
   },
   MINE {
      @Override
      public float modifyVolume(float volume) {
         return (volume + 1.0F) / 2.0F;
      }

      @Override
      public float modifyPitch(float pitch) {
         return pitch * 0.8F;
      }
   },
   DIG {
      @Override
      public float modifyVolume(float volume) {
         return (volume + 1.0F) / 8.0F;
      }

      @Override
      public float modifyPitch(float pitch) {
         return pitch * 0.5F;
      }
   },
   ENTITY_LAND {
      @Override
      public float modifyVolume(float volume) {
         return volume * 0.5F;
      }

      @Override
      public float modifyPitch(float pitch) {
         return pitch * 0.75F;
      }
   };

   private EnumBlockSoundEffectType() {
   }

   public abstract float modifyVolume(float var1);

   public abstract float modifyPitch(float var1);
}
