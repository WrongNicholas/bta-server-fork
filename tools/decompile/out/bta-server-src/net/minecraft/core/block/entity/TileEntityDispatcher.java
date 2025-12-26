package net.minecraft.core.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.tags.CompoundTag;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.piston.TileEntityMovingPistonBlock;
import net.minecraft.core.util.collection.NamespaceID;
import org.slf4j.Logger;

public class TileEntityDispatcher {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<NamespaceID, Class<? extends TileEntity>> idToClassMap = new HashMap<>();
   private static final Map<String, Class<? extends TileEntity>> stringToClassMap = new HashMap<>();
   private static final Map<Class<? extends TileEntity>, NamespaceID> classToIdMap = new HashMap<>();
   private static boolean hasInit = false;

   public static void addMapping(Class<? extends TileEntity> entityClass, NamespaceID id) {
      if (idToClassMap.containsKey(id)) {
         throw new IllegalArgumentException("Duplicate id: " + id);
      } else {
         id.makePermanent();
         idToClassMap.put(id, entityClass);
         stringToClassMap.put(id.toString(), entityClass);
         classToIdMap.put(entityClass, id);
      }
   }

   public static TileEntity createAndLoadEntity(CompoundTag compoundTag) {
      TileEntity tileEntity = null;
      Class<? extends TileEntity> entityClass = getClassFromID(compoundTag.getString("id"));

      try {
         if (entityClass != null) {
            tileEntity = entityClass.newInstance();
         }
      } catch (Exception var4) {
         LOGGER.error("Exception when instancing class '{}'!", entityClass.getSimpleName(), var4);
      }

      if (tileEntity != null) {
         tileEntity.readFromNBT(compoundTag);
      } else {
         LOGGER.warn("Skipping TileEntity with id {}", compoundTag.getString("id"));
      }

      return tileEntity;
   }

   public static Class<? extends TileEntity> getClassFromID(String id) {
      return TileEntityDispatcher.Legacy.isLegacyKey(id) ? TileEntityDispatcher.Legacy.getClassFromID(id) : stringToClassMap.get(id);
   }

   public static Class<? extends TileEntity> getClassFromID(NamespaceID id) {
      return idToClassMap.get(id);
   }

   public static NamespaceID getIDFromClass(Class<? extends TileEntity> clazz) {
      return classToIdMap.get(clazz);
   }

   public static void init() {
      if (!hasInit) {
         hasInit = true;
         addMapping(TileEntityFurnace.class, NamespaceID.getPermanent("minecraft", "furnace"));
         addMapping(TileEntityChest.class, NamespaceID.getPermanent("minecraft", "chest"));
         addMapping(TileEntityJukebox.class, NamespaceID.getPermanent("minecraft", "jukebox"));
         addMapping(TileEntityDispenser.class, NamespaceID.getPermanent("minecraft", "dispenser"));
         addMapping(TileEntityActivator.class, NamespaceID.getPermanent("minecraft", "activator"));
         addMapping(TileEntitySign.class, NamespaceID.getPermanent("minecraft", "sign"));
         addMapping(TileEntityMobSpawner.class, NamespaceID.getPermanent("minecraft", "mob_spawner"));
         addMapping(TileEntityMovingPistonBlock.class, NamespaceID.getPermanent("minecraft", "piston_moving"));
         addMapping(TileEntityFurnaceBlast.class, NamespaceID.getPermanent("minecraft", "furnace_blast"));
         addMapping(TileEntitySensor.class, NamespaceID.getPermanent("minecraft", "sensor"));
         addMapping(TileEntityTrommel.class, NamespaceID.getPermanent("minecraft", "trommel"));
         addMapping(TileEntityBasket.class, NamespaceID.getPermanent("minecraft", "basket"));
         addMapping(TileEntityFlag.class, NamespaceID.getPermanent("minecraft", "flag"));
         addMapping(TileEntitySeat.class, NamespaceID.getPermanent("minecraft", "seat"));
         addMapping(TileEntityFlowerJar.class, NamespaceID.getPermanent("minecraft", "jar_flower"));
         addMapping(TileEntityMeshGold.class, NamespaceID.getPermanent("minecraft", "mesh_gold"));
      }
   }

   @Deprecated
   public static class Legacy {
      private static final Map<String, Class<? extends TileEntity>> nameToClassMap = new HashMap<>();
      private static final Map<Class<? extends TileEntity>, String> classToNameMap = new HashMap<>();

      @Deprecated
      public static Class<? extends TileEntity> getClassFromID(String id) {
         return nameToClassMap.get(id);
      }

      @Deprecated
      public static String getIDFromClass(Class<? extends TileEntity> clazz) {
         return classToNameMap.get(clazz);
      }

      @Deprecated
      public static void addMapping(Class<? extends TileEntity> entityClass, String name) {
         if (nameToClassMap.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate id: " + name);
         } else {
            nameToClassMap.put(name, entityClass);
            classToNameMap.put(entityClass, name);
         }
      }

      public static boolean isLegacyKey(String id) {
         return nameToClassMap.containsKey(id);
      }

      static {
         addMapping(TileEntityFurnace.class, "Furnace");
         addMapping(TileEntityChest.class, "Chest");
         addMapping(TileEntityJukebox.class, "RecordPlayer");
         addMapping(TileEntityDispenser.class, "Trap");
         addMapping(TileEntityActivator.class, "Activator");
         addMapping(TileEntitySign.class, "Sign");
         addMapping(TileEntityMobSpawner.class, "MobSpawner");
         addMapping(TileEntityNoteblock.class, "Music");
         addMapping(TileEntityMovingPistonBlock.class, "Piston");
         addMapping(TileEntityFurnaceBlast.class, "BlastFurnace");
         addMapping(TileEntitySensor.class, "Sensor");
         addMapping(TileEntityTrommel.class, "Trommel");
         addMapping(TileEntityBasket.class, "Basket");
         addMapping(TileEntityFlag.class, "Flag");
         addMapping(TileEntitySeat.class, "Seat");
         addMapping(TileEntityFlowerJar.class, "FlowerJar");
         addMapping(TileEntityMeshGold.class, "MeshGold");
      }
   }
}
