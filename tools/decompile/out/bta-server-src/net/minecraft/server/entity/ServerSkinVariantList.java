package net.minecraft.server.entity;

import net.minecraft.core.entity.SkinVariantList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerSkinVariantList extends SkinVariantList {
   @NotNull
   @Override
   public String getSkinReference(@Nullable String variantJsonPath, @NotNull String defaultKey, int skinIndex) {
      return "";
   }

   @Override
   public int nextSkinVariant(@Nullable String variantJsonPath, int skinIndex) {
      return skinIndex;
   }
}
