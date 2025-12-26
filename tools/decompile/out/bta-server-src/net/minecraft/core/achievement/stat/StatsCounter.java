package net.minecraft.core.achievement.stat;

import com.b100.json.JsonParser;
import com.b100.json.element.JsonElement;
import com.b100.json.element.JsonEntry;
import com.b100.json.element.JsonObject;
import com.b100.utils.FileUtils;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.MD5String;
import net.minecraft.core.achievement.Achievement;
import net.minecraft.core.player.Session;
import net.minecraft.core.util.HardIllegalArgumentException;
import net.minecraft.core.util.collection.NamespaceID;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;

public class StatsCounter {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<Stat, Integer> sessionStats = new HashMap<>();
   private final Map<Stat, Integer> savedStats = new HashMap<>();
   private boolean modified = false;
   private final StatsSyncher statsSyncher;

   public StatsCounter(Session session, File mcDir) {
      File statsFolder = new File(mcDir, "stats");
      FileUtils.createFolder(statsFolder);
      relocateStatFiles(mcDir, statsFolder);
      this.statsSyncher = new StatsSyncher(session, this, statsFolder);
   }

   public void add(Stat stat, int amount) {
      this.addToMap(this.savedStats, stat, amount);
      this.addToMap(this.sessionStats, stat, amount);
      this.modified = true;
   }

   private void addToMap(Map<Stat, Integer> map, Stat stat, int amount) {
      int currentValue = map.getOrDefault(stat, 0);
      map.put(stat, currentValue + amount);
   }

   public @Unmodifiable Map<Stat, Integer> getSavedStats() {
      return Collections.unmodifiableMap(this.savedStats);
   }

   public void addToBoth(Map<Stat, Integer> changes) {
      if (changes != null) {
         this.modified = true;

         for (Stat stat : changes.keySet()) {
            this.addToMap(this.savedStats, stat, changes.get(stat));
            this.addToMap(this.sessionStats, stat, changes.get(stat));
         }
      }
   }

   public void addToLocal(Map<Stat, Integer> changes) {
      if (changes != null) {
         for (Stat stat : changes.keySet()) {
            int currentValue = this.savedStats.getOrDefault(stat, 0);
            this.sessionStats.put(stat, currentValue + changes.get(stat));
         }
      }
   }

   public void addToSaved(Map<Stat, Integer> map) {
      if (map != null) {
         this.modified = true;

         for (Stat stat : map.keySet()) {
            this.addToMap(this.savedStats, stat, map.get(stat));
         }
      }
   }

   public boolean isUnlocked(Achievement achievement) {
      return this.sessionStats.containsKey(achievement);
   }

   public boolean canUnlock(Achievement achievement) {
      return achievement.parent == null || this.isUnlocked(achievement.parent);
   }

   public int readStat(Stat stat) {
      return this.sessionStats.getOrDefault(stat, 0);
   }

   public void fetchStats() {
   }

   public void saveStats() {
      this.statsSyncher.saveStatsToDisk(this.getSavedStats());
   }

   public void tick() {
      if (this.modified && this.statsSyncher.pendingSave()) {
         this.statsSyncher.startSavingThread(this.getSavedStats());
      }

      this.statsSyncher.tick();
   }

   public void wipe(String stupidityCheck) {
      if (!stupidityCheck.toLowerCase(Locale.ROOT).equals("I want to delete all the saved stats".toLowerCase(Locale.ROOT))) {
         throw new RuntimeException("Did not properly confirm desire to delete saved stats");
      } else {
         this.sessionStats.clear();
         this.savedStats.clear();
      }
   }

   public static void relocateStatFiles(File minecraftFolder, File statsFolder) {
      File[] files = minecraftFolder.listFiles();
      if (files != null) {
         for (File file : files) {
            if (file.getName().startsWith("stats_") && file.getName().endsWith(".dat")) {
               File destination = new File(statsFolder, file.getName());
               if (!destination.exists()) {
                  LOGGER.info("Relocating {}", file.getName());
                  file.renameTo(destination);
               }
            }
         }
      }
   }

   public static Map<Stat, Integer> readStatsFromString(String jsonString) {
      Map<Stat, Integer> map = new HashMap<>();

      try {
         JsonParser parser = new JsonParser();
         JsonObject rootObject = parser.parseString(jsonString);

         for (JsonElement element : rootObject.getArray("stats-change")) {
            JsonObject object = element.getAsObject();
            JsonEntry entry = object.entryList().get(0);
            String statId = entry.name;
            int value = entry.value.getAsNumber().getInteger();

            Stat stat;
            try {
               if (statId.contains(":")) {
                  stat = StatList.getStat(NamespaceID.getTemp(statId));
               } else {
                  stat = StatList.StatConverter.getStatFromDeprecatedID(Integer.parseInt(statId));
               }
            } catch (NumberFormatException | HardIllegalArgumentException var13) {
               LOGGER.warn("Exception parsing statID: '{}'", statId, var13);
               continue;
            }

            if (stat == null) {
               LOGGER.warn("{} is not a valid stat", statId);
            } else {
               map.put(stat, value);
            }
         }
      } catch (Exception var14) {
         LOGGER.error("Exception while reading stats string!", (Throwable)var14);
      }

      return map;
   }

   public static String getStatFileContentString(UUID uuid, String sessionID, Map<Stat, Integer> map) {
      StringBuilder displayString = new StringBuilder();
      StringBuilder checkSumString = new StringBuilder();
      boolean flag = true;
      displayString.append("{\r\n");
      if (uuid != null && sessionID != null) {
         displayString.append("  \"user\":{\r\n");
         displayString.append("    \"uuid\":\"").append(uuid).append("\",\r\n");
         displayString.append("    \"sessionid\":\"").append(sessionID).append("\"\r\n");
         displayString.append("  },\r\n");
      }

      displayString.append("  \"stats-change\":[");

      for (Stat stat : map.keySet()) {
         if (!flag) {
            displayString.append("},");
         } else {
            flag = false;
         }

         displayString.append("\r\n    {\"").append((CharSequence)stat.statId).append("\":").append(map.get(stat));
         checkSumString.append((CharSequence)stat.statId).append(",");
         checkSumString.append(map.get(stat)).append(",");
      }

      if (!flag) {
         displayString.append("}");
      }

      MD5String md5string = new MD5String(sessionID);
      displayString.append("\r\n  ],\r\n");
      displayString.append("  \"checksum\":\"").append(md5string.getString(checkSumString.toString())).append("\"\r\n");
      displayString.append("}");
      return displayString.toString();
   }
}
