package net.minecraft.server.world;

import net.minecraft.core.world.ProgressListener;
import net.minecraft.server.MinecraftServer;

public class ConvertProgressUpdater implements ProgressListener {
   private long lastTimeMillis = System.currentTimeMillis();

   @Override
   public void progressStart(String s) {
   }

   @Override
   public void progressStagePercentage(int i) {
      if (System.currentTimeMillis() - this.lastTimeMillis >= 1000L) {
         this.lastTimeMillis = System.currentTimeMillis();
         MinecraftServer.LOGGER.info("Converting... " + i + "%");
      }
   }

   @Override
   public void progressStage(String s) {
   }

   @Override
   public void progressStop() {
   }
}
