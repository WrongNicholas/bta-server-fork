package net.minecraft.core.util;

public class HardIllegalArgumentException extends Exception {
   public HardIllegalArgumentException() {
   }

   public HardIllegalArgumentException(String message) {
      super(message);
   }

   public HardIllegalArgumentException(String message, Throwable cause) {
      super(message, cause);
   }

   public HardIllegalArgumentException(Throwable cause) {
      super(cause);
   }

   protected HardIllegalArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}
