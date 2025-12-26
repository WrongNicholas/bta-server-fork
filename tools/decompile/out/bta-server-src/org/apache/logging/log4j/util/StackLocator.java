package org.apache.logging.log4j.util;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

public final class StackLocator {
   static final int JDK_7U25_OFFSET;
   private static final Method GET_CALLER_CLASS_METHOD;
   private static final StackLocator INSTANCE;
   private static final Class<?> DEFAULT_CALLER_CLASS = null;

   public static StackLocator getInstance() {
      return INSTANCE;
   }

   private StackLocator() {
   }

   @PerformanceSensitive
   public Class<?> getCallerClass(final Class<?> sentinelClass, final Predicate<Class<?>> callerPredicate) {
      if (sentinelClass == null) {
         throw new IllegalArgumentException("sentinelClass cannot be null");
      } else if (callerPredicate == null) {
         throw new IllegalArgumentException("callerPredicate cannot be null");
      } else {
         boolean foundSentinel = false;

         Class<?> clazz;
         for (int i = 2; null != (clazz = this.getCallerClass(i)); i++) {
            if (sentinelClass.equals(clazz)) {
               foundSentinel = true;
            } else if (foundSentinel && callerPredicate.test(clazz)) {
               return clazz;
            }
         }

         return DEFAULT_CALLER_CLASS;
      }
   }

   @PerformanceSensitive
   public Class<?> getCallerClass(final int depth) {
      if (depth < 0) {
         throw new IndexOutOfBoundsException(Integer.toString(depth));
      } else if (GET_CALLER_CLASS_METHOD == null) {
         return DEFAULT_CALLER_CLASS;
      } else {
         try {
            return (Class<?>)GET_CALLER_CLASS_METHOD.invoke(null, depth + 1 + JDK_7U25_OFFSET);
         } catch (Exception var3) {
            return DEFAULT_CALLER_CLASS;
         }
      }
   }

   @PerformanceSensitive
   public Class<?> getCallerClass(final String fqcn, final String pkg) {
      boolean next = false;

      Class<?> clazz;
      for (int i = 2; null != (clazz = this.getCallerClass(i)); i++) {
         if (fqcn.equals(clazz.getName())) {
            next = true;
         } else if (next && clazz.getName().startsWith(pkg)) {
            return clazz;
         }
      }

      return DEFAULT_CALLER_CLASS;
   }

   @PerformanceSensitive
   public Class<?> getCallerClass(final Class<?> anchor) {
      boolean next = false;

      Class<?> clazz;
      for (int i = 2; null != (clazz = this.getCallerClass(i)); i++) {
         if (anchor.equals(clazz)) {
            next = true;
         } else if (next) {
            return clazz;
         }
      }

      return Object.class;
   }

   @PerformanceSensitive
   public Deque<Class<?>> getCurrentStackTrace() {
      if (PrivateSecurityManagerStackTraceUtil.isEnabled()) {
         return PrivateSecurityManagerStackTraceUtil.getCurrentStackTrace();
      } else {
         Deque<Class<?>> classes = new ArrayDeque<>();

         Class<?> clazz;
         for (int i = 1; null != (clazz = this.getCallerClass(i)); i++) {
            classes.push(clazz);
         }

         return classes;
      }
   }

   public StackTraceElement calcLocation(final String fqcnOfLogger) {
      if (fqcnOfLogger == null) {
         return null;
      } else {
         StackTraceElement[] stackTrace = new Throwable().getStackTrace();
         boolean found = false;

         for (int i = 0; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            if (fqcnOfLogger.equals(className)) {
               found = true;
            } else if (found && !fqcnOfLogger.equals(className)) {
               return stackTrace[i];
            }
         }

         return null;
      }
   }

   public StackTraceElement getStackTraceElement(final int depth) {
      int i = 0;

      for (StackTraceElement element : new Throwable().getStackTrace()) {
         if (this.isValid(element)) {
            if (i == depth) {
               return element;
            }

            i++;
         }
      }

      throw new IndexOutOfBoundsException(Integer.toString(depth));
   }

   private boolean isValid(final StackTraceElement element) {
      if (element.isNativeMethod()) {
         return false;
      } else {
         String cn = element.getClassName();
         if (cn.startsWith("sun.reflect.")) {
            return false;
         } else {
            String mn = element.getMethodName();
            if (!cn.startsWith("java.lang.reflect.") || !mn.equals("invoke") && !mn.equals("newInstance")) {
               if (cn.startsWith("jdk.internal.reflect.")) {
                  return false;
               } else {
                  return cn.equals("java.lang.Class") && mn.equals("newInstance")
                     ? false
                     : !cn.equals("java.lang.invoke.MethodHandle") || !mn.startsWith("invoke");
               }
            } else {
               return false;
            }
         }
      }
   }

   static {
      int java7u25CompensationOffset = 0;

      Method getCallerClassMethod;
      try {
         Class<?> sunReflectionClass = LoaderUtil.loadClass("sun.reflect.Reflection");
         getCallerClassMethod = sunReflectionClass.getDeclaredMethod("getCallerClass", int.class);
         Object o = getCallerClassMethod.invoke(null, 0);
         getCallerClassMethod.invoke(null, 0);
         if (o != null && o == sunReflectionClass) {
            o = getCallerClassMethod.invoke(null, 1);
            if (o == sunReflectionClass) {
               System.out.println("WARNING: Unexpected result from sun.reflect.Reflection.getCallerClass(int), adjusting offset for future calls.");
               java7u25CompensationOffset = 1;
            }
         } else {
            getCallerClassMethod = null;
            java7u25CompensationOffset = -1;
         }
      } catch (LinkageError | Exception var4) {
         System.out.println("WARNING: sun.reflect.Reflection.getCallerClass is not supported. This will impact performance.");
         getCallerClassMethod = null;
         java7u25CompensationOffset = -1;
      }

      GET_CALLER_CLASS_METHOD = getCallerClassMethod;
      JDK_7U25_OFFSET = java7u25CompensationOffset;
      INSTANCE = new StackLocator();
   }
}
