package net.minecraft.core.net.entity.entries;

import net.minecraft.core.entity.animal.MobFireflyCluster;
import org.jetbrains.annotations.NotNull;

public class NetEntryFirefly extends NetEntryAnimal<MobFireflyCluster> {
   @NotNull
   @Override
   public Class<MobFireflyCluster> getAppliedClass() {
      return MobFireflyCluster.class;
   }

   @Override
   public int getPacketDelay() {
      return 40;
   }
}
