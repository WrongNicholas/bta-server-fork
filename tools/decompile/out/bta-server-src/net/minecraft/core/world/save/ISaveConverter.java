package net.minecraft.core.world.save;

import com.mojang.nbt.tags.CompoundTag;
import java.io.File;
import net.minecraft.core.world.ProgressListener;

public interface ISaveConverter {
   int fromVersion();

   int toVersion();

   void convertSave(CompoundTag var1, File var2, String var3, ProgressListener var4);
}
