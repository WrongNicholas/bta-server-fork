package net.minecraft.core.net.command.helpers;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Block;
import org.jetbrains.annotations.Nullable;

public class BlockInput {
   @Nullable
   private final Block<?> block;
   private final int metadata;
   private final CompoundTag tag;

   public BlockInput(@Nullable Block<?> block, int metadata, CompoundTag tag) {
      this.block = block;
      this.metadata = metadata;
      this.tag = tag;
   }

   @Nullable
   public Block<?> getBlock() {
      return this.block;
   }

   public int getBlockId() {
      return this.block == null ? 0 : this.block.id();
   }

   public int getMetadata() {
      return this.metadata;
   }

   public CompoundTag getTag() {
      return this.tag;
   }
}
