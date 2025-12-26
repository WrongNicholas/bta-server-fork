package net.minecraft.core.world.save.conversion;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import net.minecraft.core.data.legacy.LegacyWorldTypes;
import net.minecraft.core.world.ProgressListener;
import net.minecraft.core.world.save.ISaveConverter;
import net.minecraft.core.world.save.legacy.ChunkFile;
import net.minecraft.core.world.save.legacy.ChunkFilePattern;
import net.minecraft.core.world.save.legacy.ChunkFolderPattern;
import net.minecraft.core.world.save.mcregion.RegionFile;
import net.minecraft.core.world.save.mcregion.RegionFileCache;
import net.minecraft.core.world.type.WorldTypes;
import org.slf4j.Logger;

public class SaveConverterLegacyTo19132 implements ISaveConverter {
   private static final Logger LOGGER = LogUtils.getLogger();

   @Override
   public int fromVersion() {
      return 0;
   }

   @Override
   public int toVersion() {
      return 19132;
   }

   @Override
   public void convertSave(CompoundTag tag, File savesDir, String saveDirName, ProgressListener progress) {
      File dim0dir = new File(savesDir, saveDirName);
      File dim1dir = new File(dim0dir, "DIM-1");
      progress.progressStagePercentage(0);
      System.out.println("Scanning folders...");
      ArrayList<ChunkFile> dim0ChunkFiles = new ArrayList<>();
      ArrayList<File> dim0ChunkDirs = new ArrayList<>();
      ArrayList<ChunkFile> dim1ChunkFiles = new ArrayList<>();
      ArrayList<File> dim1ChunkDirs = new ArrayList<>();
      this.getOldFormatFolders(dim0dir, dim0ChunkFiles, dim0ChunkDirs);
      if (dim1dir.exists()) {
         this.getOldFormatFolders(dim1dir, dim1ChunkFiles, dim1ChunkDirs);
      }

      int totalConversions = dim0ChunkFiles.size() + dim1ChunkFiles.size() + dim0ChunkDirs.size() + dim1ChunkDirs.size();
      System.out.println("Total conversion count is " + totalConversions);
      this.convertDimensionChunks(dim0dir, dim0ChunkFiles, 0, totalConversions, progress);
      this.convertDimensionChunks(dim1dir, dim1ChunkFiles, dim0ChunkFiles.size(), totalConversions, progress);
      this.deleteOldWorldData(dim0ChunkDirs, dim0ChunkFiles.size() + dim1ChunkFiles.size(), totalConversions, progress);
      if (dim1dir.exists()) {
         this.deleteOldWorldData(dim1ChunkDirs, dim0ChunkFiles.size() + dim1ChunkFiles.size() + dim0ChunkDirs.size(), totalConversions, progress);
      }

      tag.putByteArray(
         "WorldTypes",
         new byte[]{
            (byte)LegacyWorldTypes.getWorldTypeId(WorldTypes.OVERWORLD_RETRO),
            (byte)LegacyWorldTypes.getWorldTypeId(WorldTypes.NETHER_DEFAULT),
            (byte)LegacyWorldTypes.getWorldTypeId(WorldTypes.PARADISE_DEFAULT)
         }
      );
   }

   private void getOldFormatFolders(File file, ArrayList<ChunkFile> chunkFileList, ArrayList<File> chunkDirList) {
      ChunkFolderPattern chunkfolderpattern = new ChunkFolderPattern();
      ChunkFilePattern chunkfilepattern = new ChunkFilePattern();
      File[] chunkDir1Files = file.listFiles(chunkfolderpattern);
      if (chunkDir1Files != null) {
         for (File chunkDir1File : chunkDir1Files) {
            chunkDirList.add(chunkDir1File);
            File[] chunkDir2Files = chunkDir1File.listFiles(chunkfolderpattern);
            if (chunkDir2Files != null) {
               for (File chunkDir2File : chunkDir2Files) {
                  File[] chunkFiles = chunkDir2File.listFiles(chunkfilepattern);
                  if (chunkFiles != null) {
                     for (File chunkFile : chunkFiles) {
                        chunkFileList.add(new ChunkFile(chunkFile));
                     }
                  }
               }
            }
         }
      }
   }

   private void convertDimensionChunks(File file, ArrayList<ChunkFile> chunkFileList, int conversionsPerformed, int totalConversions, ProgressListener progress) {
      Collections.sort(chunkFileList);
      byte[] fileBuffer = new byte[4096];

      for (ChunkFile chunkfile : chunkFileList) {
         int x = chunkfile.getX();
         int z = chunkfile.getZ();
         RegionFile regionfile = RegionFileCache.loadRegionFileFromCoords(file, x, z);
         if (!regionfile.chunkExists(x & 31, z & 31)) {
            try {
               DataInputStream datainputstream = new DataInputStream(new GZIPInputStream(Files.newInputStream(chunkfile.getChunkFile().toPath())));
               DataOutputStream dataoutputstream = regionfile.getChunkDataOutputStream(x & 31, z & 31);

               int j1;
               while ((j1 = datainputstream.read(fileBuffer)) != -1) {
                  dataoutputstream.write(fileBuffer, 0, j1);
               }

               dataoutputstream.close();
               datainputstream.close();
            } catch (IOException var16) {
               LOGGER.error("Failed to convert chunk X:{}, Z:{}, in region {}!", x, z, regionfile);
            }
         }

         conversionsPerformed++;
         int numConversions = (int)Math.round(100.0 * conversionsPerformed / totalConversions);
         progress.progressStagePercentage(numConversions);
      }

      RegionFileCache.flushCache();
   }

   private void deleteOldWorldData(List<File> files, int conversionsPerformed, int totalConversions, ProgressListener progressListener) {
      for (File file : files) {
         File[] afile = file.listFiles();
         deleteFilesRecursively(afile);
         file.delete();
         conversionsPerformed++;
         int k = (int)Math.round(100.0 * conversionsPerformed / totalConversions);
         progressListener.progressStagePercentage(k);
      }
   }

   protected static void deleteFilesRecursively(File[] dirFiles) {
      for (File file : dirFiles) {
         if (file.isDirectory()) {
            File[] subDirFiles = file.listFiles();
            if (subDirFiles != null) {
               deleteFilesRecursively(subDirFiles);
            }
         }

         file.delete();
      }
   }
}
