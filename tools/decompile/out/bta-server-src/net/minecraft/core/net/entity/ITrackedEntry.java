package net.minecraft.core.net.entity;

public interface ITrackedEntry<T> extends INetworkEntry<T> {
   int getTrackingDistance();

   int getPacketDelay();

   boolean sendMotionUpdates();

   void onEntityTracked(EntityTracker var1, EntityTrackerEntry var2, T var3);
}
