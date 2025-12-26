package net.minecraft.core;

public class UnexpectedThrowable {
   public final String description;
   public final Throwable exception;

   public UnexpectedThrowable(String description, Throwable throwable) {
      this.description = description;
      this.exception = throwable;
   }
}
