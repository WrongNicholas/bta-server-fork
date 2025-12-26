package net.minecraft.core.block;

@FunctionalInterface
public interface BlockLogicSupplier<T extends BlockLogic> {
   T get(Block<T> var1);
}
