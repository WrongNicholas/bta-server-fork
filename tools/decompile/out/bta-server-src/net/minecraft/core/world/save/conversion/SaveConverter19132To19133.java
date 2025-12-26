package net.minecraft.core.world.save.conversion;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.ProgressListener;
import net.minecraft.core.world.save.mcregion.RegionFile;
import net.minecraft.core.world.save.mcregion.RegionFileCache;
import org.slf4j.Logger;

public class SaveConverter19132To19133 extends SaveConverterMCRegionBase {
   private static final Logger LOGGER = LogUtils.getLogger();

   @Override
   public int fromVersion() {
      return 19132;
   }

   @Override
   public int toVersion() {
      return 19133;
   }

   @Override
   int convertDimensionRegions(Dimension dimension, ArrayList<File> regionFiles, int conversionsPerformed, int totalConversions, ProgressListener progress) {
      Collections.sort(regionFiles);
      int progressValue = 0;
      int conversions = 0;

      for (File region : regionFiles) {
         progress.progressStagePercentage(progressValue);
         RegionFile regionFile = new RegionFile(region);

         for (int x = 0; x < 32; x++) {
            for (int z = 0; z < 32; z++) {
               try {
                  DataOutputStream outputStream = regionFile.getChunkDataOutputStream(x, z);
                  DataInputStream inputStream = regionFile.getChunkDataInputStream(x, z);
                  if (inputStream != null) {
                     CompoundTag chunkData = NbtIo.read(inputStream);
                     byte[] ids = chunkData.getCompound("Level").getByteArray("Blocks");
                     byte[] expandedIds = new byte[65536];

                     for (int i = 0; i < ids.length; i += 128) {
                        System.arraycopy(ids, i, expandedIds, i * 2, 128);
                     }

                     short[] shortIds = new short[65536];

                     for (int i = 0; i < expandedIds.length; i++) {
                        shortIds[i] = expandedIds[i];
                     }

                     byte[] nibbleData = chunkData.getCompound("Level").getByteArray("Data");
                     byte[] byteData = new byte[nibbleData.length * 2];

                     for (int i = 0; i < byteData.length; i++) {
                        int nibbleIndex = i >> 1;
                        if ((i & 1) == 0) {
                           byteData[i] = (byte)(nibbleData[nibbleIndex] & 15);
                        } else {
                           byteData[i] = (byte)(nibbleData[nibbleIndex] >> 4 & 15);
                        }
                     }

                     byte[] expandedByteData = new byte[65536];

                     for (int ix = 0; ix < byteData.length; ix += 128) {
                        System.arraycopy(byteData, ix, expandedByteData, ix * 2, 128);
                     }

                     ChunkConverter.converters[0].convertBlocksAndMetadata(shortIds, expandedByteData);
                     byte[] skylightMap = chunkData.getCompound("Level").getByteArray("SkyLight");
                     byte[] skylightMapExtended = new byte[32768];

                     for (int ix = 0; ix < skylightMap.length; ix += 64) {
                        System.arraycopy(skylightMap, ix, skylightMapExtended, ix * 2, 64);
                     }

                     byte[] blocklightMap = chunkData.getCompound("Level").getByteArray("BlockLight");
                     byte[] blocklightMapExtended = new byte[32768];

                     for (int ix = 0; ix < blocklightMap.length; ix += 64) {
                        System.arraycopy(blocklightMap, ix, blocklightMapExtended, ix * 2, 64);
                     }

                     chunkData.getCompound("Level").putShortArray("Blocks", shortIds);
                     chunkData.getCompound("Level").putByteArray("Data", expandedByteData);
                     chunkData.getCompound("Level").putByteArray("SkyLight", skylightMapExtended);
                     chunkData.getCompound("Level").putByteArray("BlockLight", blocklightMapExtended);
                     NbtIo.write(chunkData, outputStream);
                     inputStream.close();
                     outputStream.close();
                  }
               } catch (IOException var27) {
                  LOGGER.error("Failed to convert chunk X:{}, Z:{}, in region {}!", x, z, region.toPath(), var27);
               }
            }
         }

         conversions++;
         progressValue = (int)Math.round(100.0 * (conversionsPerformed + conversions) / totalConversions);
      }

      RegionFileCache.flushCache();
      return conversions;
   }
}
