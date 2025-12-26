package net.minecraft.core.world.save;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.world.save.conversion.SaveConverter19132To19133;
import net.minecraft.core.world.save.conversion.SaveConverter19133To19134;
import net.minecraft.core.world.save.conversion.SaveConverterLegacyTo19132;

public class SaveConverters {
   public static List<ISaveConverter> saveConverters = new ArrayList<>();

   static {
      saveConverters.add(new SaveConverterLegacyTo19132());
      saveConverters.add(new SaveConverter19132To19133());
      saveConverters.add(new SaveConverter19133To19134());
   }
}
