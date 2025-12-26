package net.minecraft.core.world;

public interface ProgressListener {
   void progressStart(String var1);

   void progressStop();

   void progressStage(String var1);

   void progressStagePercentage(int var1);
}
