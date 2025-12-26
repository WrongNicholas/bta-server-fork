package net.minecraft.core.block.support;

import net.minecraft.core.util.helper.Side;

public class FullSupport implements ISupport {
   public static final FullSupport INSTANCE = new FullSupport();

   private FullSupport() {
   }

   @Override
   public boolean canSupport(ISupport other, Side side) {
      return true;
   }
}
