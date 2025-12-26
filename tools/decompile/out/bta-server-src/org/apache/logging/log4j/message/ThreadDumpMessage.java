package org.apache.logging.log4j.message;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.util.ServiceLoaderUtil;
import org.apache.logging.log4j.util.StringBuilderFormattable;

@AsynchronouslyFormattable
public class ThreadDumpMessage implements Message, StringBuilderFormattable {
   private static final long serialVersionUID = -1103400781608841088L;
   private static ThreadDumpMessage.ThreadInfoFactory FACTORY;
   private volatile Map<ThreadInformation, StackTraceElement[]> threads;
   private final String title;
   private String formattedMessage;

   public ThreadDumpMessage(final String title) {
      this.title = title == null ? "" : title;
      this.threads = getFactory().createThreadInfo();
   }

   private ThreadDumpMessage(final String formattedMsg, final String title) {
      this.formattedMessage = formattedMsg;
      this.title = title == null ? "" : title;
   }

   private static ThreadDumpMessage.ThreadInfoFactory getFactory() {
      if (FACTORY == null) {
         FACTORY = initFactory();
      }

      return FACTORY;
   }

   private static ThreadDumpMessage.ThreadInfoFactory initFactory() {
      return ServiceLoaderUtil.loadServices(ThreadDumpMessage.ThreadInfoFactory.class, MethodHandles.lookup(), false)
         .findFirst()
         .orElseGet(() -> new ThreadDumpMessage.BasicThreadInfoFactory());
   }

   @Override
   public String toString() {
      return this.getFormattedMessage();
   }

   @Override
   public String getFormattedMessage() {
      if (this.formattedMessage != null) {
         return this.formattedMessage;
      } else {
         StringBuilder sb = new StringBuilder(255);
         this.formatTo(sb);
         return sb.toString();
      }
   }

   @Override
   public void formatTo(final StringBuilder sb) {
      sb.append(this.title);
      if (this.title.length() > 0) {
         sb.append('\n');
      }

      for (Entry<ThreadInformation, StackTraceElement[]> entry : this.threads.entrySet()) {
         ThreadInformation info = entry.getKey();
         info.printThreadInfo(sb);
         info.printStack(sb, entry.getValue());
         sb.append('\n');
      }
   }

   @Override
   public String getFormat() {
      return this.title == null ? "" : this.title;
   }

   @Override
   public Object[] getParameters() {
      return null;
   }

   protected Object writeReplace() {
      return new ThreadDumpMessage.ThreadDumpMessageProxy(this);
   }

   private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   @Override
   public Throwable getThrowable() {
      return null;
   }

   private static class BasicThreadInfoFactory implements ThreadDumpMessage.ThreadInfoFactory {
      private BasicThreadInfoFactory() {
      }

      @Override
      public Map<ThreadInformation, StackTraceElement[]> createThreadInfo() {
         Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
         Map<ThreadInformation, StackTraceElement[]> threads = new HashMap<>(map.size());

         for (Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
            threads.put(new BasicThreadInformation(entry.getKey()), entry.getValue());
         }

         return threads;
      }
   }

   private static class ThreadDumpMessageProxy implements Serializable {
      private static final long serialVersionUID = -3476620450287648269L;
      private final String formattedMsg;
      private final String title;

      ThreadDumpMessageProxy(final ThreadDumpMessage msg) {
         this.formattedMsg = msg.getFormattedMessage();
         this.title = msg.title;
      }

      protected Object readResolve() {
         return new ThreadDumpMessage(this.formattedMsg, this.title);
      }
   }

   public interface ThreadInfoFactory {
      Map<ThreadInformation, StackTraceElement[]> createThreadInfo();
   }
}
