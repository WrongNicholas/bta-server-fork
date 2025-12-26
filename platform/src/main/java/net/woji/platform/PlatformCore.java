package net.woji.platform;

import net.woji.platform.plugin.PluginManager;
import net.woji.api.event.EventBus;
import net.woji.platform.event.EventBusImpl;

public final class PlatformCore {

  private static PluginManager pluginManager;
  private static boolean initialized = false;

  private static final EventBus EVENT_BUS = new EventBusImpl();

  public PlatformCore() {}

  public static EventBus getEventBus()
  {
    return EVENT_BUS;
  }

  public static void init(Object server)
  {
    if (initialized) return;

    // Load PluginManager
    pluginManager = new PluginManager();
    pluginManager.loadPlugins();

    initialized = true;
  }

  public static void enablePlugins() {
    if (!initialized) return;

    // Enable Plugins
    pluginManager.enablePlugins();
  }

  public static void shutdown()
  {
    if (!initialized) return;

    // Disable Plugins
    pluginManager.disablePlugins();
    initialized = false;
  }

  public static PluginManager getPluginManager() {
    return pluginManager;
  }
}
