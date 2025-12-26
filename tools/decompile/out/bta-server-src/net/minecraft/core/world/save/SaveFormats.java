package net.minecraft.core.world.save;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.world.save.legacy.SaveFormatLegacy;
import net.minecraft.core.world.save.mcregion.SaveFormat19132;
import net.minecraft.core.world.save.mcregion.SaveFormat19133;
import net.minecraft.core.world.save.mcregion.SaveFormat19134;
import org.slf4j.Logger;

public class SaveFormats {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static Map<Integer, Class<? extends ISaveFormat>> saveFormats = new HashMap<>();

   public static ISaveFormat createSaveFormat(int version, File savesDir) {
      if (!saveFormats.containsKey(version)) {
         return null;
      } else {
         try {
            return saveFormats.get(version).getConstructor(File.class).newInstance(savesDir);
         } catch (Exception var3) {
            LOGGER.error("Exception instancing ISaveFormat from '{}'!", version, var3);
            return null;
         }
      }
   }

   static {
      saveFormats.put(0, SaveFormatLegacy.class);
      saveFormats.put(19132, SaveFormat19132.class);
      saveFormats.put(19133, SaveFormat19133.class);
      saveFormats.put(19134, SaveFormat19134.class);
   }
}
