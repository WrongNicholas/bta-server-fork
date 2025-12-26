package net.woji.platform.plugin;

import net.woji.api.Plugin;
import org.slf4j.Logger;

import java.net.URLClassLoader;

public class PluginContainer {
  private final Plugin plugin;
  private final URLClassLoader classLoader;
  private final Logger logger;

  public PluginContainer(Plugin plugin, URLClassLoader classLoader, Logger logger) {
    this.plugin = plugin;
    this.classLoader = classLoader;
    this.logger = logger;
  }

  public Plugin getPlugin() {
    return plugin;
  }

  public URLClassLoader getClassLoader() {
    return classLoader;
  }
}
