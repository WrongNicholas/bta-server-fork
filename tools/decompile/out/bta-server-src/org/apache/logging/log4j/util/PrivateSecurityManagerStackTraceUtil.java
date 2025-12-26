package org.apache.logging.log4j.util;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

final class PrivateSecurityManagerStackTraceUtil {
   private static final PrivateSecurityManagerStackTraceUtil.PrivateSecurityManager SECURITY_MANAGER;

   private PrivateSecurityManagerStackTraceUtil() {
   }

   static boolean isEnabled() {
      return SECURITY_MANAGER != null;
   }

   static Deque<Class<?>> getCurrentStackTrace() {
      Class<?>[] array = SECURITY_MANAGER.getClassContext();
      Deque<Class<?>> classes = new ArrayDeque<>(array.length);
      Collections.addAll(classes, array);
      return classes;
   }

   static {
      PrivateSecurityManagerStackTraceUtil.PrivateSecurityManager psm;
      try {
         SecurityManager sm = System.getSecurityManager();
         if (sm != null) {
            sm.checkPermission(new RuntimePermission("createSecurityManager"));
         }

         psm = new PrivateSecurityManagerStackTraceUtil.PrivateSecurityManager();
      } catch (SecurityException var2) {
         psm = null;
      }

      SECURITY_MANAGER = psm;
   }

   private static final class PrivateSecurityManager extends SecurityManager {
      private PrivateSecurityManager() {
      }

      @Override
      protected Class<?>[] getClassContext() {
         return super.getClassContext();
      }
   }
}
