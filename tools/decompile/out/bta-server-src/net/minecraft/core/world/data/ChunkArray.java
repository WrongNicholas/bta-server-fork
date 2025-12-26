package net.minecraft.core.world.data;

public interface ChunkArray<T> {
   T get(int var1, int var2, int var3);

   void set(int var1, int var2, int var3, T var4);

   boolean isValid();
}
