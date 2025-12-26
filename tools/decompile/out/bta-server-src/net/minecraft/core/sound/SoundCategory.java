package net.minecraft.core.sound;

public enum SoundCategory {
   MUSIC("note.harp"),
   WORLD_SOUNDS("tile.piston.out"),
   WEATHER_SOUNDS("ambient.weather.rain"),
   ENTITY_SOUNDS("mob.sheep"),
   CAVE_SOUNDS("ambient.cave.cave"),
   GUI_SOUNDS("random.click");

   public final String changedSound;

   private SoundCategory(String changedSound) {
      this.changedSound = changedSound;
   }
}
