package net.minecraft.core.world;

import net.minecraft.core.entity.Entity;

public interface IVehicle {
   boolean isRemoved();

   Entity ejectRider();

   void positionRider();

   void setPassenger(Entity var1);

   Entity getPassenger();

   void moveExitingEntity(Entity var1);

   float getYRotDelta();

   float getXRotDelta();
}
