package net.minecraft.core.net;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SavedServerList {
   private File serverListFile;
   public List<ServerData> servers = new ArrayList<>();

   private SavedServerList() {
   }

   public static SavedServerList readFromFile(File file) {
      SavedServerList serverList = new SavedServerList();
      serverList.serverListFile = file;

      try {
         InputStream fileStream = Files.newInputStream(file.toPath());
         DataInputStream dataStream = new DataInputStream(fileStream);
         CompoundTag tag = new CompoundTag();
         tag.read(dataStream);

         for (Tag<?> subTag : tag.getValues()) {
            if (subTag instanceof CompoundTag) {
               serverList.servers.add(ServerData.readFromTag((CompoundTag)subTag));
            }
         }

         fileStream.close();
         dataStream.close();
         return serverList;
      } catch (Exception var7) {
         return serverList;
      }
   }

   public void writeToFile() {
      try {
         OutputStream fileStream = Files.newOutputStream(this.serverListFile.toPath());
         DataOutputStream dataStream = new DataOutputStream(fileStream);
         CompoundTag tag = new CompoundTag();

         for (int i = 0; i < this.servers.size(); i++) {
            this.servers.get(i).writeToTag(tag, i);
         }

         tag.write(dataStream);
         fileStream.close();
         dataStream.close();
      } catch (Exception var5) {
      }
   }
}
