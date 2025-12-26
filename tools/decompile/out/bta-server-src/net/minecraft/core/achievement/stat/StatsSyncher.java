package net.minecraft.core.achievement.stat;

import com.b100.utils.StringUtils;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import net.minecraft.core.player.Session;
import org.slf4j.Logger;

public class StatsSyncher {
   private static final Logger LOGGER = LogUtils.getLogger();
   private volatile boolean busy = false;
   private volatile Map<Stat, Integer> queuedLocalStatsChanges = null;
   private volatile Map<Stat, Integer> queuedSavedStatsChanges = null;
   private final StatsCounter statsCounter;
   private final File unsentStatsFile;
   private final File statsFile;
   private final File unsentStatsFileTemp;
   private final File statsFileTemp;
   private final File unsentStatsFileOld;
   private final File statsFileOld;
   private final Session session;
   private int saveTimer = 0;
   private int fetchChangesTimer = 0;

   public StatsSyncher(Session session, StatsCounter statsCounter, File statsDir) {
      this.statsFile = new File(statsDir, "stats_" + session.uuid + ".dat");
      this.statsFileOld = new File(statsDir, "stats_" + session.uuid + ".old");
      this.statsFileTemp = new File(statsDir, "stats_" + session.uuid + ".tmp");
      this.unsentStatsFile = new File(statsDir, "stats_" + session.uuid + "_unsent.dat");
      this.unsentStatsFileOld = new File(statsDir, "stats_" + session.uuid + "_unsent.old");
      this.unsentStatsFileTemp = new File(statsDir, "stats_" + session.uuid + "_unsent.tmp");
      new File(statsDir, "stats_" + session.username.toLowerCase() + ".dat").renameTo(this.statsFile);
      new File(statsDir, "stats_" + session.username.toLowerCase() + ".old").renameTo(this.statsFileOld);
      new File(statsDir, "stats_" + session.username.toLowerCase() + ".tmp").renameTo(this.statsFileTemp);
      new File(statsDir, "stats_" + session.username.toLowerCase() + "_unsent.dat").renameTo(this.unsentStatsFile);
      new File(statsDir, "stats_" + session.username.toLowerCase() + "_unsent.old").renameTo(this.unsentStatsFileOld);
      new File(statsDir, "stats_" + session.username.toLowerCase() + "_unsent.tmp").renameTo(this.unsentStatsFileTemp);
      this.statsCounter = statsCounter;
      this.session = session;
      if (this.unsentStatsFile.exists()) {
         statsCounter.addToBoth(this.readStatsFromFile(this.unsentStatsFile, this.unsentStatsFileTemp, this.unsentStatsFileOld));
      }

      this.startRetrievalThread();
   }

   private void renameFile(File parent, String child, File fileToRename) {
      File destFile = new File(parent, child);
      if (destFile.exists() && !destFile.isDirectory() && !fileToRename.exists()) {
         destFile.renameTo(fileToRename);
      }
   }

   private Map<Stat, Integer> readStatsFromFile(File statsFile, File tempStatsFile, File oldStatsFile) {
      if (statsFile.exists()) {
         return this.readStatsFromFile(statsFile);
      } else if (oldStatsFile.exists()) {
         return this.readStatsFromFile(oldStatsFile);
      } else {
         return tempStatsFile.exists() ? this.readStatsFromFile(tempStatsFile) : null;
      }
   }

   private Map<Stat, Integer> readStatsFromFile(File statsFile) {
      try {
         return StatsCounter.readStatsFromString(StringUtils.getFileContentAsString(statsFile));
      } catch (Exception var3) {
         LOGGER.warn("Failed to read stats from file '{}'!", statsFile.getPath(), var3);
         return null;
      }
   }

   private void saveStats(Map<Stat, Integer> map, File statsFile, File tempStatsFile, File oldStatsFile) throws IOException {
      PrintWriter writer = new PrintWriter(new FileWriter(tempStatsFile, false));

      try {
         writer.print(StatsCounter.getStatFileContentString(this.session.uuid, "local", map));
      } catch (Throwable var9) {
         try {
            writer.close();
         } catch (Throwable var8) {
            var9.addSuppressed(var8);
         }

         throw var9;
      }

      writer.close();
      if (oldStatsFile.exists()) {
         oldStatsFile.delete();
      }

      if (statsFile.exists()) {
         statsFile.renameTo(oldStatsFile);
      }

      tempStatsFile.renameTo(statsFile);
   }

   public void startRetrievalThread() {
      if (this.busy) {
         throw new IllegalStateException("Can't get stats from server while StatsSyncher is busy!");
      } else {
         this.saveTimer = 100;
         this.busy = true;
         new Thread(() -> {
            try {
               if (this.queuedLocalStatsChanges != null) {
                  this.saveStats(this.queuedLocalStatsChanges, this.statsFile, this.statsFileTemp, this.statsFileOld);
               } else if (this.statsFile.exists()) {
                  this.queuedLocalStatsChanges = this.readStatsFromFile(this.statsFile, this.statsFileTemp, this.statsFileOld);
               }
            } catch (Exception var5) {
               LOGGER.error("Unexpected exception in retrieval thread!", (Throwable)var5);
            } finally {
               this.busy = false;
            }
         }).start();
      }
   }

   public void startSavingThread(Map<Stat, Integer> mapToSave) {
      if (this.busy) {
         throw new IllegalStateException("Can't save stats while StatsSyncher is busy!");
      } else {
         this.saveTimer = 100;
         this.busy = true;
         new Thread(() -> {
            try {
               this.saveStats(mapToSave, this.unsentStatsFile, this.unsentStatsFileTemp, this.unsentStatsFileOld);
            } catch (Exception var6) {
               LOGGER.error("Unexpected exception in saving thread!", (Throwable)var6);
            } finally {
               this.busy = false;
            }
         }).start();
      }
   }

   public void saveStatsToDisk(Map<Stat, Integer> map) {
      int i = 30;

      while (this.busy) {
         if (--i <= 0) {
            break;
         }

         try {
            Thread.sleep(100L);
         } catch (InterruptedException var10) {
            LOGGER.warn("Interrupted!", (Throwable)var10);
         }
      }

      this.busy = true;

      try {
         this.saveStats(map, this.unsentStatsFile, this.unsentStatsFileTemp, this.unsentStatsFileOld);
      } catch (Exception var8) {
         LOGGER.error("Exception saving stats!", (Throwable)var8);
      } finally {
         this.busy = false;
      }
   }

   public boolean pendingSave() {
      return this.saveTimer <= 0 && !this.busy && this.queuedSavedStatsChanges == null;
   }

   public void tick() {
      if (this.saveTimer > 0) {
         this.saveTimer--;
      }

      if (this.fetchChangesTimer > 0) {
         this.fetchChangesTimer--;
      }

      if (this.queuedSavedStatsChanges != null) {
         this.statsCounter.addToSaved(this.queuedSavedStatsChanges);
         this.queuedSavedStatsChanges = null;
      }

      if (this.queuedLocalStatsChanges != null) {
         this.statsCounter.addToLocal(this.queuedLocalStatsChanges);
         this.queuedLocalStatsChanges = null;
      }
   }
}
