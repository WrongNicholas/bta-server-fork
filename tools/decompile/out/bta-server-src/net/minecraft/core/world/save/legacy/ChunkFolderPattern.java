package net.minecraft.core.world.save.legacy;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkFolderPattern implements FileFilter {
   public static final Pattern pattern = Pattern.compile("[0-9a-z]|([0-9a-z][0-9a-z])");

   @Override
   public boolean accept(File pathname) {
      if (pathname.isDirectory()) {
         Matcher matcher = pattern.matcher(pathname.getName());
         return matcher.matches();
      } else {
         return false;
      }
   }
}
