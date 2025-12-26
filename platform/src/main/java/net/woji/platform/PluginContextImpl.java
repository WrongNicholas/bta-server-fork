package net.woji.platform;

import org.slf4j.Logger;
import java.nio.file.Path;

import net.woji.api.PluginContext;
import net.woji.api.event.EventBus;

public class PluginContextImpl implements PluginContext {
  private final Logger logger;

  public PluginContextImpl(Logger logger) {
    this.logger = logger;
  }

  @Override
  public Logger getLogger()
  {
    return logger;
  }

  @Override
  public Path getDataFolder()
  {
    return null;
  }

  @Override 
  public EventBus getEventBus() {
    return PlatformCore.getEventBus();
  }
}
