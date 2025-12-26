package net.minecraft.core.block;

public class BlockLogicButtonPlanks extends BlockLogicButton {
   public BlockLogicButtonPlanks(Block<?> block) {
      super(block);
   }

   @Override
   public int tickDelay() {
      return 10;
   }
}
