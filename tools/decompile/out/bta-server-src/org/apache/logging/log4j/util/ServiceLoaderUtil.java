package org.apache.logging.log4j.util;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public final class ServiceLoaderUtil {
   private ServiceLoaderUtil() {
   }

   public static <T> Stream<T> loadServices(final Class<T> serviceType, Lookup lookup) {
      return loadServices(serviceType, lookup, false);
   }

   public static <T> Stream<T> loadServices(final Class<T> serviceType, Lookup lookup, boolean useTccl) {
      return loadServices(serviceType, lookup, useTccl, true);
   }

   static <T> Stream<T> loadServices(final Class<T> serviceType, final Lookup lookup, final boolean useTccl, final boolean verbose) {
      ClassLoader classLoader = lookup.lookupClass().getClassLoader();
      Stream<T> services = loadClassloaderServices(serviceType, lookup, classLoader, verbose);
      if (useTccl) {
         ClassLoader contextClassLoader = LoaderUtil.getThreadContextClassLoader();
         if (contextClassLoader != classLoader) {
            services = Stream.concat(services, loadClassloaderServices(serviceType, lookup, contextClassLoader, verbose));
         }
      }

      if (OsgiServiceLocator.isAvailable()) {
         services = Stream.concat(services, OsgiServiceLocator.loadServices(serviceType, lookup, verbose));
      }

      Set<Class<?>> classes = new HashSet<>();
      return services.filter(service -> classes.add(service.getClass()));
   }

   static <T> Stream<T> loadClassloaderServices(final Class<T> serviceType, final Lookup lookup, final ClassLoader classLoader, final boolean verbose) {
      return StreamSupport.stream(new ServiceLoaderUtil.ServiceLoaderSpliterator<>(serviceType, lookup, classLoader, verbose), false);
   }

   static <T> Iterable<T> callServiceLoader(Lookup lookup, Class<T> serviceType, ClassLoader classLoader, boolean verbose) {
      try {
         MethodHandle loadHandle = lookup.findStatic(ServiceLoader.class, "load", MethodType.methodType(ServiceLoader.class, Class.class, ClassLoader.class));
         CallSite callSite = LambdaMetafactory.metafactory(
            lookup,
            "run",
            MethodType.methodType(PrivilegedAction.class, Class.class, ClassLoader.class),
            MethodType.methodType(Object.class),
            loadHandle,
            MethodType.methodType(ServiceLoader.class)
         );
         PrivilegedAction<ServiceLoader<T>> action = (PrivilegedAction)callSite.getTarget().bindTo(serviceType).bindTo(classLoader).invoke();
         ServiceLoader<T> serviceLoader;
         if (System.getSecurityManager() == null) {
            serviceLoader = action.run();
         } else {
            MethodHandle privilegedHandle = lookup.findStatic(
               AccessController.class, "doPrivileged", MethodType.methodType(Object.class, PrivilegedAction.class)
            );
            serviceLoader = (ServiceLoader)privilegedHandle.invoke((PrivilegedAction)action);
         }

         return serviceLoader;
      } catch (Throwable var9) {
         if (verbose) {
            StatusLogger.getLogger().error("Unable to load services for service {}", serviceType, var9);
         }

         return Collections.emptyList();
      }
   }

   private static class ServiceLoaderSpliterator<S> implements Spliterator<S> {
      private final Iterator<S> serviceIterator;
      private final Logger logger;
      private final String serviceName;

      public ServiceLoaderSpliterator(final Class<S> serviceType, final Lookup lookup, final ClassLoader classLoader, final boolean verbose) {
         this.serviceIterator = ServiceLoaderUtil.callServiceLoader(lookup, serviceType, classLoader, verbose).iterator();
         this.logger = verbose ? StatusLogger.getLogger() : null;
         this.serviceName = serviceType.toString();
      }

      @Override
      public boolean tryAdvance(Consumer<? super S> action) {
         while (this.serviceIterator.hasNext()) {
            try {
               action.accept(this.serviceIterator.next());
               return true;
            } catch (ServiceConfigurationError var3) {
               if (this.logger != null) {
                  this.logger.warn("Unable to load service class for service {}", this.serviceName, var3);
               }
            }
         }

         return false;
      }

      @Override
      public Spliterator<S> trySplit() {
         return null;
      }

      @Override
      public long estimateSize() {
         return Long.MAX_VALUE;
      }

      @Override
      public int characteristics() {
         return 1280;
      }
   }
}
