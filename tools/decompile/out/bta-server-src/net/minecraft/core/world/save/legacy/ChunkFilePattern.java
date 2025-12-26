package net.minecraft.core.world.save.legacy;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkFilePattern implements FilenameFilter {
   public static final Pattern pattern = Pattern.compile("c\\.(-?[0-9a-z]+)\\.(-?[0-9a-z]+)\\.dat");

   @Override
   public boolean accept(File dir, String name) {
      Matcher matcher = pattern.matcher(name);
      return matcher.matches();
   }
}
