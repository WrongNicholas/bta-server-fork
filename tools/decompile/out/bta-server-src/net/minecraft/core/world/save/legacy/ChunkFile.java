package net.minecraft.core.world.save.legacy;

import java.io.File;
import java.util.regex.Matcher;

public class ChunkFile implements Comparable<ChunkFile> {
   private final File chunkFile;
   private final int x;
   private final int z;

   public ChunkFile(File chunkFile) {
      this.chunkFile = chunkFile;
      Matcher matcher = ChunkFilePattern.pattern.matcher(chunkFile.getName());
      if (matcher.matches()) {
         this.x = Integer.parseInt(matcher.group(1), 36);
         this.z = Integer.parseInt(matcher.group(2), 36);
      } else {
         this.x = 0;
         this.z = 0;
      }
   }

   public int compareChunks(ChunkFile chunkfile) {
      int myX = this.x >> 5;
      int otherX = chunkfile.x >> 5;
      if (myX == otherX) {
         int myZ = this.z >> 5;
         int otherZ = chunkfile.z >> 5;
         return myZ - otherZ;
      } else {
         return myX - otherX;
      }
   }

   public File getChunkFile() {
      return this.chunkFile;
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public int compareTo(ChunkFile chunkFile) {
      return this.compareChunks(chunkFile);
   }
}
