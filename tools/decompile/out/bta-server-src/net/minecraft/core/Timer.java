package net.minecraft.core;

public class Timer {
   public float ticksPerSecond;
   private double lastTime;
   public int frames;
   public float partialTicks;
   public float fps = 1.0F;
   public float ticks = 0.0F;
   private long currentTime;
   private long msPerTick;
   private long passedTime;
   private double averageFrameTime = 1.0;

   public Timer(float ticksPerSecond) {
      this.ticksPerSecond = ticksPerSecond;
      this.currentTime = System.currentTimeMillis();
      this.msPerTick = System.nanoTime() / 1000000L;
   }

   public void advanceTime() {
      long currentTime = System.currentTimeMillis();
      long timeDelta = currentTime - this.currentTime;
      long nanoTime = System.nanoTime() / 1000000L;
      double d = nanoTime / 1000.0;
      if (timeDelta > 1000L) {
         this.lastTime = d;
      } else if (timeDelta < 0L) {
         this.lastTime = d;
      } else {
         this.passedTime += timeDelta;
         if (this.passedTime > 1000L) {
            long l3 = nanoTime - this.msPerTick;
            double d2 = (double)this.passedTime / l3;
            this.averageFrameTime = this.averageFrameTime + (d2 - this.averageFrameTime) * 0.2;
            this.msPerTick = nanoTime;
            this.passedTime = 0L;
         }

         if (this.passedTime < 0L) {
            this.msPerTick = nanoTime;
         }
      }

      this.currentTime = currentTime;
      double d1 = (d - this.lastTime) * this.averageFrameTime;
      this.lastTime = d;
      if (d1 < 0.0) {
         d1 = 0.0;
      }

      if (d1 > 1.0) {
         d1 = 1.0;
      }

      this.ticks = (float)(this.ticks + d1 * this.fps * this.ticksPerSecond);
      this.frames = (int)this.ticks;
      this.ticks = this.ticks - this.frames;
      if (this.frames > 10) {
         this.frames = 10;
      }

      this.partialTicks = this.ticks;
   }
}
