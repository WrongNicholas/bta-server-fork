package com.b100.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public abstract class ReflectUtils {
   public static Field getField(Class<?> clazz, String... names) {
      if (clazz == null) {
         throw new NullPointerException();
      } else if (names.length == 0) {
         throw new RuntimeException("No Name!");
      } else {
         try {
            for (String name : names) {
               try {
                  return clazz.getDeclaredField(name);
               } catch (Exception var8) {
                  try {
                     return clazz.getField(name);
                  } catch (Exception var7) {
                  }
               }
            }

            StringBuilder allNames = new StringBuilder();

            for (int i = 0; i < names.length; i++) {
               if (i > 0) {
                  allNames.append(' ');
               }

               allNames.append(names[i]);
            }

            throw new RuntimeException("Class " + clazz.getName() + " doesn't have any of these Fields: " + allNames.toString());
         } catch (Exception var9) {
            throw new RuntimeException(var9);
         }
      }
   }

   public static <E> E getValue(Field field, Object object, Class<E> clazz) {
      if (field == null) {
         throw new NullPointerException("Field is null!");
      } else if (clazz == null) {
         throw new NullPointerException("Class is null!");
      } else {
         setFieldAccessible(field);
         Object value = getValue(field, object);
         if (value == null) {
            return null;
         } else if (value.getClass().isAssignableFrom(clazz)) {
            return clazz.cast(value);
         } else {
            throw new ClassCastException(value.getClass().getName() + " cannot be cast to " + clazz.getName());
         }
      }
   }

   public static Object getValue(Field field, Object object) {
      if (field == null) {
         throw new NullPointerException("Field is null!");
      } else if (!isStatic(field) && object == null) {
         throw new NullPointerException("Object is null!");
      } else {
         setFieldAccessible(field);

         try {
            return field.get(object);
         } catch (Exception var3) {
            throw new RuntimeException("Could not get value!", var3);
         }
      }
   }

   public static void setFieldAccessible(Field field) {
      try {
         field.setAccessible(true);
      } catch (Exception var2) {
         throw new RuntimeException("Could not set field accessible: " + field + ": " + var2.getClass().getName() + ": " + var2.getMessage(), var2);
      }
   }

   public static float getFloatValue(Field field, Object object) {
      return getValue(field, object, Float.class);
   }

   public static void setValue(Field field, Object object, Object value) {
      try {
         field.set(object, value);
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   public static void printAllStaticFields(Class<?> clazz) {
      if (clazz == null) {
         throw new NullPointerException("Class is null!");
      } else {
         for (Field field : Utils.combineArray(Field.class, clazz.getFields(), clazz.getDeclaredFields())) {
            if (isStatic(field)) {
               printField(field, null);
            }
         }
      }
   }

   public static void printAllFields(Class<?> clazz, Object object) {
      if (clazz == null) {
         throw new NullPointerException("Class is null!");
      } else if (object == null) {
         throw new NullPointerException("Object is null!");
      } else {
         for (Field field : Utils.combineArray(Field.class, clazz.getFields(), clazz.getDeclaredFields())) {
            if (!isStatic(field)) {
               printField(field, object);
            }
         }
      }
   }

   private static void printField(Field field, Object object) {
      try {
         field.setAccessible(true);
         Object value = field.get(object);
         System.out.println(field.getType().getName() + " " + field.getName() + " = " + value);
      } catch (Exception var3) {
         System.out
            .println(field.getType().getName() + " " + field.getName() + " (Could not get Value: " + var3.getClass().getName() + ": " + var3.getMessage() + ")");
      }
   }

   public static boolean isStatic(Field field) {
      return Modifier.isStatic(field.getModifiers());
   }

   public static <F, E> E[] getAllObjects(Class<F> fromClass, Class<E> ofClass, F instance) {
      return getAllObjects(fromClass, ofClass, instance, null);
   }

   public static <F, E> E[] getAllObjects(Class<F> fromClass, Class<E> ofClass) {
      return getAllObjects(fromClass, ofClass, null);
   }

   public static <F, E> E[] getAllObjects(Class<F> fromClass, Class<E> ofClass, F instance, ReflectUtils.Condition<Field, E> condition) {
      List<E> list = new ArrayList<>();
      Field[] fields = fromClass.getDeclaredFields();

      for (Field field : fields) {
         try {
            E e = (E)field.get(instance);
            if (e != null && ofClass.isAssignableFrom(e.getClass()) && (condition == null || condition.isTrue(field, e))) {
               list.add(e);
            }
         } catch (Exception var11) {
         }
      }

      return Utils.toArray(ofClass, list);
   }

   public interface Condition<E, F> {
      boolean isTrue(E var1, F var2);
   }
}
