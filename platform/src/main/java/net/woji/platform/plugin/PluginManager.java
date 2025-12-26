package net.woji.platform.plugin;

import net.woji.api.Plugin;
import net.woji.api.PluginContext;
import net.woji.platform.PluginContextImpl;
import net.woji.platform.plugin.PluginContainer;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);

  private final List<PluginContainer> plugins = new ArrayList<>();

  public void loadPlugins() {
    File pluginsDir = new File("plugins");
    if (!pluginsDir.exists()) {
      pluginsDir.mkdirs();
      return;
    }

    File[] jars = pluginsDir.listFiles(f -> f.isFile() && f.getName().endsWith(".jar"));
    if (jars == null) return;

    for (File jar : jars)
    {
      LOGGER.info("Found jar: " + jar);
      try 
      {
        URLClassLoader cl = new URLClassLoader(
            new URL[]{jar.toURI().toURL()},
            this.getClass().getClassLoader()
        );

        // need to read plugins.json properly
        String mainClass = "com.example.helloworld.HelloWorldPlugin";

        Class<?> pluginClass = Class.forName(mainClass, true, cl);
        Object instance = pluginClass.getDeclaredConstructor().newInstance();

        if (!(instance instanceof Plugin)) {
          continue;
        }

        Plugin plugin = (Plugin) instance;

        String pluginKey = jar.getName();
        Logger logger = LoggerFactory.getLogger("Plugin/" + pluginKey);

        PluginContext pluginContext = new PluginContextImpl(logger);

        PluginContainer container = new PluginContainer(
            plugin,
            cl,
            logger
        );

        plugin.init(pluginContext);
        plugins.add(container);
        LOGGER.info("Loading plugin: "+pluginKey);
        plugin.onLoad();
      }
      catch (Throwable t)
      {
        t.printStackTrace();
      }
    }
  }

  public void enablePlugins() {
    for (PluginContainer c : plugins) {
      try
      {
        c.getPlugin().onEnable();
      }
      catch (Throwable t)
      {
        t.printStackTrace();
      }
    }
  }

  public void disablePlugins() {
    for (PluginContainer c : plugins) {
      try
      {
        c.getPlugin().onDisable();
      }
      catch (Throwable t)
      {
        t.printStackTrace();
      }
      plugins.clear();
    }
  }

}
