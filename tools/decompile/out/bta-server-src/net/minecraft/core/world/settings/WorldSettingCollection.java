package net.minecraft.core.world.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class WorldSettingCollection {
   private WorldSetting<?>[] settings = null;

   public WorldSetting<?>[] getSettings() {
      if (this.settings == null) {
         List<Field> annotatedFields = new ArrayList<>();
         Field[] fields = this.getClass().getDeclaredFields();

         for (Field field : fields) {
            if (field.isAnnotationPresent(WorldSettingCollection.Setting.class)) {
               annotatedFields.add(field);
            }
         }

         this.settings = new WorldSetting[annotatedFields.size()];

         for (int i = 0; i < annotatedFields.size(); i++) {
            try {
               this.settings[i] = (WorldSetting<?>)annotatedFields.get(i).get(this);
            } catch (Exception var7) {
            }
         }
      }

      return this.settings;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.FIELD)
   protected @interface Setting {
   }
}
