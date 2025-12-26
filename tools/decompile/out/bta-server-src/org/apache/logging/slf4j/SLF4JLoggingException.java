package org.apache.logging.slf4j;

public class SLF4JLoggingException extends RuntimeException {
   private static final long serialVersionUID = -1618650972455089998L;

   public SLF4JLoggingException(final String msg) {
      super(msg);
   }

   public SLF4JLoggingException(final String msg, final Exception ex) {
      super(msg, ex);
   }

   public SLF4JLoggingException(final Exception ex) {
      super(ex);
   }
}
