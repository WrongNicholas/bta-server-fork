package net.minecraft.core.net.entity;

import org.jetbrains.annotations.NotNull;

public interface INetworkEntry<T> {
   @NotNull
   Class<? extends T> getAppliedClass();
}
