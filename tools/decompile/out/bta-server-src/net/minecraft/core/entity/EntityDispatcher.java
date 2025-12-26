package net.minecraft.core.entity;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.tags.CompoundTag;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.entity.animal.MobChicken;
import net.minecraft.core.entity.animal.MobCow;
import net.minecraft.core.entity.animal.MobFireflyCluster;
import net.minecraft.core.entity.animal.MobPig;
import net.minecraft.core.entity.animal.MobSheep;
import net.minecraft.core.entity.animal.MobSquid;
import net.minecraft.core.entity.animal.MobWolf;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.entity.monster.MobGhast;
import net.minecraft.core.entity.monster.MobGiant;
import net.minecraft.core.entity.monster.MobHuman;
import net.minecraft.core.entity.monster.MobScorpion;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.entity.monster.MobSlime;
import net.minecraft.core.entity.monster.MobSnowman;
import net.minecraft.core.entity.monster.MobSpider;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.entity.monster.MobZombieArmored;
import net.minecraft.core.entity.monster.MobZombiePig;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.entity.projectile.ProjectileArrowGolden;
import net.minecraft.core.entity.projectile.ProjectileArrowPurple;
import net.minecraft.core.entity.projectile.ProjectileCannonball;
import net.minecraft.core.entity.projectile.ProjectileEgg;
import net.minecraft.core.entity.projectile.ProjectileFireball;
import net.minecraft.core.entity.projectile.ProjectilePebble;
import net.minecraft.core.entity.projectile.ProjectileSnowball;
import net.minecraft.core.entity.vehicle.EntityBoat;
import net.minecraft.core.entity.vehicle.EntityMinecart;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class EntityDispatcher {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static int mappingCount = 0;
   public static final Map<Class<? extends Entity>, NamespaceID> classToIdMap = new HashMap<>();
   public static final Map<NamespaceID, Class<? extends Entity>> idToClassMap = new HashMap<>();
   public static final Map<String, Class<? extends Entity>> stringIdToClassMap = new HashMap<>();
   private static final Map<Integer, Class<? extends Entity>> numericIdToClassMap = new HashMap<>();
   private static final Map<Integer, String> numericIdToStringIdMap = new HashMap<>();
   private static final Map<Class<? extends Entity>, Integer> classToNumericIdMap = new HashMap<>();
   private static final Map<Class<? extends Entity>, String> classToNameKeyMap = new HashMap<>();
   private static boolean hasInit = false;

   private EntityDispatcher() {
   }

   public static void addMapping(@NotNull Class<? extends Entity> entityClass, @NotNull NamespaceID namespaceID) {
      addMapping(entityClass, namespaceID, null);
   }

   public static void addMapping(@NotNull Class<? extends Entity> entityClass, @NotNull NamespaceID namespaceID, @Nullable String nameKey) {
      if (classToIdMap.containsKey(entityClass)) {
         throw new IllegalArgumentException(
            "EntityDispatcher already contains an assignment for class "
               + entityClass.getSimpleName()
               + " with namespace "
               + classToIdMap.get(entityClass)
               + "!"
         );
      } else {
         namespaceID.makePermanent();
         classToIdMap.put(entityClass, namespaceID);
         idToClassMap.put(namespaceID, entityClass);
         stringIdToClassMap.put(namespaceID.toString(), entityClass);
         int numericId = mappingCount++;
         numericIdToClassMap.put(numericId, entityClass);
         numericIdToStringIdMap.put(numericId, namespaceID.toString());
         classToNumericIdMap.put(entityClass, numericId);
         if (nameKey != null) {
            classToNameKeyMap.put(entityClass, nameKey);
         }
      }
   }

   @Nullable
   public static Class<? extends Entity> classForId(@NotNull NamespaceID id) {
      return idToClassMap.get(id);
   }

   @Nullable
   public static Class<? extends Entity> classForId(@NotNull String id) {
      return stringIdToClassMap.get(id);
   }

   @Nullable
   public static NamespaceID idForClass(Class<? extends Entity> entityClass) {
      return classToIdMap.get(entityClass);
   }

   @Nullable
   public static Class<? extends Entity> classForNumericId(int id) {
      return numericIdToClassMap.get(id);
   }

   @Nullable
   public static String nameKeyForClass(Class<? extends Entity> entityClass) {
      return classToNameKeyMap.get(entityClass);
   }

   public static int numericIdForClass(Class<? extends Entity> entityClass) {
      return classToNumericIdMap.getOrDefault(entityClass, -1);
   }

   public static void setNumericIds(Map<Integer, String> mapping) {
      numericIdToClassMap.clear();
      numericIdToStringIdMap.clear();
      classToNumericIdMap.clear();

      for (Entry<Integer, String> entry : mapping.entrySet()) {
         Class<? extends Entity> entityClass = classForId(entry.getValue());
         if (entityClass == null) {
            LOGGER.error("Could not find entityClass assigned to namespace id {}! Skipping!", entry.getValue());
         } else {
            numericIdToClassMap.put(entry.getKey(), entityClass);
            numericIdToStringIdMap.put(entry.getKey(), entry.getValue());
            classToNumericIdMap.put(entityClass, entry.getKey());
            if (entry.getKey() >= mappingCount) {
               mappingCount = entry.getKey() + 1;
            }
         }
      }
   }

   public static Map<Integer, String> getEntityIds() {
      return numericIdToStringIdMap;
   }

   @Nullable
   public static Entity createEntityInWorld(@Nullable String id, World world) {
      if (id == null) {
         return null;
      } else if (EntityDispatcher.Legacy.isLegacyKey(id)) {
         return createEntityInWorld(EntityDispatcher.Legacy.getClassFromEncodeID(id), world);
      } else {
         Class<? extends Entity> entityClass = classForId(id);
         if (entityClass == null) {
            LOGGER.warn("EntityDispatcher could not find entity class for id '{}'!", id);
            return null;
         } else {
            return createEntityInWorld(entityClass, world);
         }
      }
   }

   @Nullable
   public static Entity createEntityInWorld(NamespaceID id, World world) {
      return createEntityInWorld(classForId(id), world);
   }

   @Nullable
   public static Entity createEntityInWorld(Class<? extends Entity> clazz, World world) {
      Entity entity = null;

      try {
         if (clazz == null) {
            return null;
         }

         entity = clazz.getConstructor(World.class).newInstance(world);
      } catch (Exception var4) {
         LOGGER.error("Failed to instance for class '{}'!", clazz.getSimpleName(), var4);
      }

      return entity;
   }

   public static Entity createEntityFromNBT(CompoundTag compoundTag, World world) {
      String id = compoundTag.getStringOrDefault("id", "UNKNOWN");
      Entity entity = createEntityInWorld(id, world);
      if (entity != null) {
         entity.load(compoundTag);
      } else {
         LOGGER.warn("Skipping Entity with id {}", compoundTag.getStringOrDefault("id", "UNKNOWN"));
      }

      return entity;
   }

   @Nullable
   public static Entity createEntity(int id, World world) {
      Entity entity = null;

      try {
         Class<? extends Entity> clazz = classForNumericId(id);
         if (clazz != null) {
            entity = clazz.getConstructor(World.class).newInstance(world);
         } else {
            LOGGER.error("No entity class assign to id {}!", id);
         }
      } catch (Exception var4) {
         LOGGER.error("Failed to instance entity id '{}'!", id, var4);
      }

      return entity;
   }

   public static void init() {
      if (!hasInit) {
         hasInit = true;
         addMapping(EntityItem.class, NamespaceID.getPermanent("minecraft", "item"));
         addMapping(EntityPainting.class, NamespaceID.getPermanent("minecraft", "painting"));
         addMapping(EntityLightning.class, NamespaceID.getPermanent("minecraft", "lightning"));
         addMapping(EntityFallingBlock.class, NamespaceID.getPermanent("minecraft", "falling_block"));
         addMapping(EntityMinecart.class, NamespaceID.getPermanent("minecraft", "minecart"));
         addMapping(EntityBoat.class, NamespaceID.getPermanent("minecraft", "boat"));
         addMapping(EntityPrimedTNT.class, NamespaceID.getPermanent("minecraft", "primed_tnt"));
         addMapping(ProjectilePebble.class, NamespaceID.getPermanent("minecraft", "pebble"));
         addMapping(ProjectileEgg.class, NamespaceID.getPermanent("minecraft", "egg"));
         addMapping(ProjectileArrow.class, NamespaceID.getPermanent("minecraft", "arrow"));
         addMapping(ProjectileArrowGolden.class, NamespaceID.getPermanent("minecraft", "arrow_golden"));
         addMapping(ProjectileArrowPurple.class, NamespaceID.getPermanent("minecraft", "arrow_purple"));
         addMapping(ProjectileSnowball.class, NamespaceID.getPermanent("minecraft", "snowball"));
         addMapping(ProjectileFireball.class, NamespaceID.getPermanent("minecraft", "fireball"));
         addMapping(ProjectileCannonball.class, NamespaceID.getPermanent("minecraft", "cannonball"));
         addMapping(MobHuman.class, NamespaceID.getPermanent("minecraft", "human"));
         addMapping(MobZombie.class, NamespaceID.getPermanent("minecraft", "zombie"), "guidebook.section.mob.zombie.name");
         addMapping(MobZombieArmored.class, NamespaceID.getPermanent("minecraft", "zombie_armored"), "guidebook.section.mob.armored_zombie.name");
         addMapping(MobZombiePig.class, NamespaceID.getPermanent("minecraft", "zombie_pigman"), "guidebook.section.mob.zombie_pigman.name");
         addMapping(MobCreeper.class, NamespaceID.getPermanent("minecraft", "creeper"), "guidebook.section.mob.creeper.name");
         addMapping(MobSkeleton.class, NamespaceID.getPermanent("minecraft", "skeleton"), "guidebook.section.mob.skeleton.name");
         addMapping(MobSpider.class, NamespaceID.getPermanent("minecraft", "spider"), "guidebook.section.mob.spider.name");
         addMapping(MobGiant.class, NamespaceID.getPermanent("minecraft", "giant"));
         addMapping(MobSlime.class, NamespaceID.getPermanent("minecraft", "slime"), "guidebook.section.mob.slime.name");
         addMapping(MobGhast.class, NamespaceID.getPermanent("minecraft", "ghast"), "guidebook.section.mob.ghast.name");
         addMapping(MobSnowman.class, NamespaceID.getPermanent("minecraft", "snowman"), "guidebook.section.mob.snowman.name");
         addMapping(MobScorpion.class, NamespaceID.getPermanent("minecraft", "scorpion"));
         addMapping(MobPig.class, NamespaceID.getPermanent("minecraft", "pig"), "guidebook.section.mob.pig.name");
         addMapping(MobSheep.class, NamespaceID.getPermanent("minecraft", "sheep"), "guidebook.section.mob.sheep.name");
         addMapping(MobCow.class, NamespaceID.getPermanent("minecraft", "cow"), "guidebook.section.mob.cow.name");
         addMapping(MobChicken.class, NamespaceID.getPermanent("minecraft", "chicken"), "guidebook.section.mob.chicken.name");
         addMapping(MobSquid.class, NamespaceID.getPermanent("minecraft", "squid"), "guidebook.section.mob.squid.name");
         addMapping(MobWolf.class, NamespaceID.getPermanent("minecraft", "wolf"), "guidebook.section.mob.wolf.name");
         addMapping(MobFireflyCluster.class, NamespaceID.getPermanent("minecraft", "firefly_cluster"), "guidebook.section.mob.firefly_cluster.name");
      }
   }

   @Deprecated
   public static class Legacy {
      public static final Map<String, Class<? extends Entity>> keyToClassMap = new HashMap<>();
      public static final Map<Class<? extends Entity>, String> classToKeyMap = new HashMap<>();
      private static final Map<Integer, Class<? extends Entity>> idToClassMap = new HashMap<>();
      private static final Map<Class<? extends Entity>, Integer> classToIdMap = new HashMap<>();
      private static int maxIdSize = 0;

      @Deprecated
      public static void addMapping(Class<? extends Entity> clazz, String key, int id) {
         keyToClassMap.put(key, clazz);
         classToKeyMap.put(clazz, key);
         idToClassMap.put(id, clazz);
         classToIdMap.put(clazz, id);
         if (key.length() > maxIdSize) {
            maxIdSize = key.length();
         }
      }

      @Deprecated
      public static int getEntityID(Entity entity) {
         return getEntityID((Class<? extends Entity>)entity.getClass());
      }

      @Deprecated
      public static int getEntityID(Class<? extends Entity> entityClass) {
         return classToIdMap.get(entityClass);
      }

      @Deprecated
      public static String getEncodeId(Entity entity) {
         return classToKeyMap.get(entity.getClass());
      }

      @Deprecated
      public static String getEncodeId(Class<? extends Entity> clazz) {
         return classToKeyMap.get(clazz);
      }

      @Deprecated
      public static Class<? extends Entity> getClassFromID(int id) {
         return idToClassMap.get(id);
      }

      @Deprecated
      public static Class<? extends Entity> getClassFromEncodeID(String encodeID) {
         return keyToClassMap.get(encodeID);
      }

      @Deprecated
      public static int getLargestIdSize() {
         return maxIdSize;
      }

      public static boolean isLegacyKey(String key) {
         return keyToClassMap.containsKey(key);
      }

      static {
         addMapping(EntityItem.class, "Item", 1);
         addMapping(EntityLightning.class, "Lightning", 8);
         addMapping(EntityPainting.class, "Painting", 9);
         addMapping(ProjectileArrow.class, "Arrow", 10);
         addMapping(ProjectileSnowball.class, "Snowball", 11);
         addMapping(ProjectileFireball.class, "Fireball", 12);
         addMapping(EntityPrimedTNT.class, "PrimedTnt", 20);
         addMapping(EntityFallingBlock.class, "FallingSand", 21);
         addMapping(EntityMinecart.class, "Minecart", 40);
         addMapping(EntityBoat.class, "Boat", 41);
         addMapping(MobZombieArmored.class, "ArmouredZombie", 42);
         addMapping(ProjectileArrowGolden.class, "ArrowGolden", 43);
         addMapping(ProjectileCannonball.class, "Cannonball", 44);
         addMapping(ProjectileArrowPurple.class, "ArrowPurple", 45);
         addMapping(ProjectilePebble.class, "Pebble", 46);
         addMapping(MobFireflyCluster.class, "FireflyCluster", 47);
         addMapping(Mob.class, "Mob", 48);
         addMapping(MobHuman.class, "MobMonster", 49);
         addMapping(MobCreeper.class, "Creeper", 50);
         addMapping(MobSkeleton.class, "Skeleton", 51);
         addMapping(MobSpider.class, "Spider", 52);
         addMapping(MobGiant.class, "Giant", 53);
         addMapping(MobZombie.class, "Zombie", 54);
         addMapping(MobSlime.class, "Slime", 55);
         addMapping(MobGhast.class, "Ghast", 56);
         addMapping(MobZombiePig.class, "PigZombie", 57);
         addMapping(MobSnowman.class, "Snowman", 58);
         addMapping(MobScorpion.class, "Scorpion", 59);
         addMapping(MobPig.class, "Pig", 90);
         addMapping(MobSheep.class, "Sheep", 91);
         addMapping(MobCow.class, "Cow", 92);
         addMapping(MobChicken.class, "Chicken", 93);
         addMapping(MobSquid.class, "Squid", 94);
         addMapping(MobWolf.class, "Wolf", 95);
      }
   }
}
