package net.minecraft.core.world.save.mcregion;

import com.mojang.logging.LogUtils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

public class RegionFileCache {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<File, Reference<RegionFile>> cache = new HashMap<>();

   private RegionFileCache() {
   }

   public static synchronized RegionFile loadRegionFileFromCoords(File worldDir, int x, int z) {
      File regionDir = new File(worldDir, "region");
      File regionFile = new File(regionDir, "r." + (x >> 5) + "." + (z >> 5) + ".mcr");
      Reference<RegionFile> reference = cache.get(regionFile);
      if (reference != null) {
         RegionFile regionfile = reference.get();
         if (regionfile != null) {
            return regionfile;
         }
      }

      if (!regionDir.exists()) {
         regionDir.mkdirs();
      }

      if (cache.size() >= 256) {
         flushCache();
      }

      RegionFile loadedRegion = new RegionFile(regionFile);
      cache.put(regionFile, new SoftReference<>(loadedRegion));
      return loadedRegion;
   }

   public static synchronized void flushCache() {
      for (Reference<RegionFile> reference : cache.values()) {
         try {
            RegionFile regionfile = reference.get();
            if (regionfile != null) {
               regionfile.close();
            }
         } catch (IOException var3) {
            LOGGER.error("Exception while flushing references", (Throwable)var3);
         }
      }

      cache.clear();
   }

   public static int getSizeDelta(File worldDir, int x, int z) {
      RegionFile regionfile = loadRegionFileFromCoords(worldDir, x, z);
      return regionfile.getSizeDeltaBytes();
   }

   public static DataInputStream getChunkInputStream(File worldDir, int x, int z) {
      RegionFile regionFile = loadRegionFileFromCoords(worldDir, x, z);
      return regionFile.getChunkDataInputStream(x & 31, z & 31);
   }

   public static DataOutputStream getChunkOutputStream(File worldDir, int x, int z) {
      RegionFile regionfile = loadRegionFileFromCoords(worldDir, x, z);
      return regionfile.getChunkDataOutputStream(x & 31, z & 31);
   }
}
