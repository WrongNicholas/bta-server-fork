package net.minecraft.core.world.config.spawning;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.tags.ByteTag;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.World;
import org.slf4j.Logger;

public class SpawnerConfig {
   private static final Logger LOGGER = LogUtils.getLogger();
   private boolean allowPassive = true;
   private boolean allowHostile = true;
   private final Map<NamespaceID, Boolean> mobSpawnOverrides = new HashMap<>();

   public boolean canPassiveSpawn(World world) {
      return this.allowPassive;
   }

   public void setPassiveSpawning(boolean flag) {
      this.allowPassive = flag;
   }

   public boolean canHostileSpawn(World world) {
      return this.allowHostile && world.getDifficulty().canHostileMobsSpawn();
   }

   public void setHostileSpawning(boolean flag) {
      this.allowHostile = flag;
   }

   public boolean canMobSpawn(NamespaceID mobID) {
      return this.mobSpawnOverrides.getOrDefault(mobID, true);
   }

   public void setMobSpawn(NamespaceID mobID, boolean flag) {
      this.mobSpawnOverrides.put(mobID, flag);
   }

   public Collection<Entry<NamespaceID, Boolean>> getSpawnEntries() {
      return this.mobSpawnOverrides.entrySet();
   }

   public void writeToTag(CompoundTag tag) {
      tag.putBoolean("passive", this.allowPassive);
      tag.putBoolean("hostile", this.allowHostile);
      CompoundTag overridesTag = new CompoundTag();

      for (Entry<NamespaceID, Boolean> e : this.mobSpawnOverrides.entrySet()) {
         overridesTag.putBoolean(e.getKey().toString(), e.getValue());
      }

      tag.putCompound("overrides", overridesTag);
   }

   public void readFromTag(CompoundTag tag) {
      this.allowPassive = tag.getBoolean("passive");
      this.allowHostile = tag.getBoolean("hostile");
      CompoundTag overrides = tag.getCompound("overrides");

      for (Entry<String, Tag<?>> tagEntry : overrides.getValue().entrySet()) {
         try {
            this.mobSpawnOverrides.put(NamespaceID.getPermanent(tagEntry.getKey()), ((ByteTag)tagEntry.getValue()).getValue() != 0);
         } catch (HardIllegalArgumentException var6) {
            LOGGER.error("Failed to convert string '{}' to namespace id!", tagEntry.getKey(), var6);
         }
      }
   }

   public static SpawnerConfig createFromTag(CompoundTag tag) {
      SpawnerConfig config = new SpawnerConfig();
      if (tag != null) {
         config.readFromTag(tag);
      }

      return config;
   }
}
