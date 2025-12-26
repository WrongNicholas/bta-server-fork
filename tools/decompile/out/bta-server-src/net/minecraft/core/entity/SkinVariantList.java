package net.minecraft.core.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SkinVariantList {
   @NotNull
   public abstract String getSkinReference(@Nullable String var1, @NotNull String var2, int var3);

   public abstract int nextSkinVariant(@Nullable String var1, int var2);
}
