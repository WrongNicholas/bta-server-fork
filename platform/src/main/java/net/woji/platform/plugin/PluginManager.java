package net.woji.platform.plugin;

import com.google.gson.Gson;
import net.woji.api.Plugin;
import net.woji.api.PluginContext;
import net.woji.platform.PluginContextImpl;
import net.woji.platform.plugin.PluginContainer;
import net.woji.platform.plugin.PluginDescription;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginManager {

  private static final Gson GSON = new Gson();
  private static final Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);


  private PluginDescription readDescription(File jarFile) throws Exception {
    try (JarFile jar = new JarFile(jarFile)) {
      var entry = jar.getJarEntry("plugin.json");
      if (entry == null) {
        throw new IllegalStateException("Plugin " + jarFile.getName() + " is missing plugin.json!");
      }

      try (InputStream inputStream = jar.getInputStream(entry);
          InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

        PluginDescription d = GSON.fromJson(reader, PluginDescription.class);
        if (d == null || d.main == null || d.main.isEmpty()) {
          throw new IllegalStateException("Plugin " + jarFile.getName() + " is missing 'main' in plugin.json");
        }
        return d;
      }
    }
  }


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
        PluginDescription description = readDescription(jar);
        String mainClass = description.main;
        String pluginKey = (description.id != null && !description.id.isEmpty()) ? description.id : jar.getName();

        Class<?> pluginClass = Class.forName(mainClass, true, cl);
        Object instance = pluginClass.getDeclaredConstructor().newInstance();

        if (!(instance instanceof Plugin)) {
          continue;
        }

        Plugin plugin = (Plugin) instance;

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
    }

    plugins.clear();
  }

}
