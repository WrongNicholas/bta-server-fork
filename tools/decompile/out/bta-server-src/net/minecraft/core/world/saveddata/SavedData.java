package net.minecraft.core.world.saveddata;

import com.mojang.nbt.tags.CompoundTag;

public abstract class SavedData {
   public final String id;
   private boolean dirty;

   public SavedData(String s) {
      this.id = s;
   }

   public abstract void load(CompoundTag var1);

   public abstract void save(CompoundTag var1);

   public void setDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }

   public boolean isDirty() {
      return this.dirty;
   }
}
