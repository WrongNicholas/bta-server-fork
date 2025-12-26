package net.minecraft.core.util.helper;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import java.io.PrintStream;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LoggedPrintStream extends PrintStream {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected final String name;

   public LoggedPrintStream(String name, OutputStream sourceStream) {
      super(sourceStream);
      this.name = name;
   }

   public static void wrapStreams() {
      System.setErr(new LoggedPrintStream("STDERR", System.err));
      System.setOut(new LoggedPrintStream("STDOUT", System.out));
   }

   @Override
   public void println(@Nullable String message) {
      this.logLine(message);
   }

   @Override
   public void println(Object object) {
      this.logLine(String.valueOf(object));
   }

   protected void logLine(@Nullable String $$0) {
      LOGGER.info("[{}]: {}", this.name, $$0);
   }

   public static String removeColorCodes(String string) {
      StringBuilder builder = new StringBuilder();
      boolean a = false;

      for (int i = 0; i < string.length(); i++) {
         char c = string.charAt(i);
         if (c == 167) {
            a = true;
         } else if (a) {
            a = false;
         } else {
            builder.append(c);
         }
      }

      return builder.toString();
   }
}
