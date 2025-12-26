package net.minecraft.core.entity;

import net.minecraft.core.item.ItemFood;
import org.jetbrains.annotations.NotNull;

public class ConsumedFood {
   @NotNull
   private final Mob entity;
   @NotNull
   private final ItemFood food;
   private int healRemaining;
   private int tickCounter = 0;

   public ConsumedFood(@NotNull Mob entity, @NotNull ItemFood food) {
      this.entity = entity;
      this.food = food;
      this.healRemaining = food.getHealAmount();
   }

   public void addFood() {
      this.healRemaining = this.healRemaining + this.food.getHealAmount();
   }

   public int getHealRemaining() {
      return this.healRemaining;
   }

   public boolean isFinished() {
      return this.healRemaining <= 0;
   }

   public void tick() {
      if (!this.isFinished()) {
         this.tickCounter++;
         if (this.tickCounter >= this.food.getTicksPerHeal()) {
            this.tickCounter = 0;
            this.healRemaining--;
            this.entity.heal(1);
         }
      }
   }
}
