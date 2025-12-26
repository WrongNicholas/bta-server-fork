package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

class DefaultAsyncWaitStrategyFactory implements AsyncWaitStrategyFactory {
   static final String DEFAULT_WAIT_STRATEGY_CLASSNAME = TimeoutBlockingWaitStrategy.class.getName();
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final String propertyName;

   public DefaultAsyncWaitStrategyFactory(String propertyName) {
      this.propertyName = propertyName;
   }

   @Override
   public WaitStrategy createWaitStrategy() {
      String strategy = PropertiesUtil.getProperties().getStringProperty(this.propertyName, "TIMEOUT");
      LOGGER.trace("DefaultAsyncWaitStrategyFactory property {}={}", this.propertyName, strategy);
      String strategyUp = Strings.toRootUpperCase(strategy);
      switch (strategyUp) {
         case "SLEEP":
            long sleepTimeNs = parseAdditionalLongProperty(this.propertyName, "SleepTimeNs", 100L);
            String key = getFullPropertyKey(this.propertyName, "Retries");
            int retries = PropertiesUtil.getProperties().getIntegerProperty(key, 200);
            LOGGER.trace("DefaultAsyncWaitStrategyFactory creating SleepingWaitStrategy(retries={}, sleepTimeNs={})", retries, sleepTimeNs);
            return new SleepingWaitStrategy(retries, sleepTimeNs);
         case "YIELD":
            LOGGER.trace("DefaultAsyncWaitStrategyFactory creating YieldingWaitStrategy");
            return new YieldingWaitStrategy();
         case "BLOCK":
            LOGGER.trace("DefaultAsyncWaitStrategyFactory creating BlockingWaitStrategy");
            return new BlockingWaitStrategy();
         case "BUSYSPIN":
            LOGGER.trace("DefaultAsyncWaitStrategyFactory creating BusySpinWaitStrategy");
            return new BusySpinWaitStrategy();
         case "TIMEOUT":
            return createDefaultWaitStrategy(this.propertyName);
         default:
            return createDefaultWaitStrategy(this.propertyName);
      }
   }

   static WaitStrategy createDefaultWaitStrategy(final String propertyName) {
      long timeoutMillis = parseAdditionalLongProperty(propertyName, "Timeout", 10L);
      LOGGER.trace("DefaultAsyncWaitStrategyFactory creating TimeoutBlockingWaitStrategy(timeout={}, unit=MILLIS)", timeoutMillis);
      return new TimeoutBlockingWaitStrategy(timeoutMillis, TimeUnit.MILLISECONDS);
   }

   private static String getFullPropertyKey(final String strategyKey, final String additionalKey) {
      if (strategyKey.startsWith("AsyncLogger.")) {
         return "AsyncLogger." + additionalKey;
      } else {
         return strategyKey.startsWith("AsyncLoggerConfig.") ? "AsyncLoggerConfig." + additionalKey : strategyKey + additionalKey;
      }
   }

   private static long parseAdditionalLongProperty(final String propertyName, final String additionalKey, long defaultValue) {
      String key = getFullPropertyKey(propertyName, additionalKey);
      return PropertiesUtil.getProperties().getLongProperty(key, defaultValue);
   }
}
