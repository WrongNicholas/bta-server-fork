package net.minecraft.core.net.entity.entries;

import net.minecraft.core.entity.animal.MobSquid;
import org.jetbrains.annotations.NotNull;

public class NetEntrySquid extends NetEntryAnimal<MobSquid> {
   @NotNull
   @Override
   public Class<MobSquid> getAppliedClass() {
      return MobSquid.class;
   }

   @Override
   public boolean sendMotionUpdates() {
      return true;
   }
}
