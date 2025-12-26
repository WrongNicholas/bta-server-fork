package net.minecraft.core;

import java.io.File;
import java.util.Collection;
import net.minecraft.core.entity.SkinVariantList;
import net.minecraft.core.sound.SoundTypes;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.IChunkLoader;
import net.minecraft.core.world.chunk.provider.IChunkProvider;

public interface MinecraftAccessor {
   File getMinecraftDir();

   IChunkProvider createChunkProvider(World var1, IChunkLoader var2);

   int getAutosaveTimer();

   SkinVariantList getSkinVariantList();

   String getMinecraftVersion();

   default boolean isFullbrightEnabled() {
      return false;
   }

   default Collection<String> getAvailableSoundKeys() {
      return SoundTypes.getSoundIds().values();
   }

   default void copyToClipboard(String str) {
   }

   default String readFromClipboard() {
      return "";
   }
}
