package net.woji.platform.event;

import net.woji.api.event.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class EventBusImpl implements EventBus {
  
  public final List<Object> listeners = new ArrayList<>();

  @Override
  public void post(Object event)
  {
    for (Object listener : listeners)
    {
      for (Method method : listener.getClass().getDeclaredMethods())
      {
        if (method.getParameterCount() != 1) continue;

        Class<?> param = method.getParameterTypes()[0];
        if (!param.isAssignableFrom(event.getClass())) continue;

        try
        {
          method.setAccessible(true);
          method.invoke(listener, event);
        }
        catch (Throwable t)
        {
          t.printStackTrace();
        }
      }
    }
  }
  @Override
  public void register(Object listener)
  {
    listeners.add(listener);
  }
}
