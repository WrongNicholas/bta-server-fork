package net.minecraft.core.entity.animal;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.MobPathfinder;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobWaterAnimal extends MobPathfinder implements WaterCreature {
   public MobWaterAnimal(World world) {
      super(world);
      this.scoreValue = 10;
   }

   @Override
   public boolean canBreatheUnderwater() {
      return true;
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
   }

   @Override
   public boolean canSpawnHere() {
      return this.world.checkIfAABBIsClear(this.bb);
   }

   @Override
   public int getAmbientSoundInterval() {
      return 120;
   }
}
