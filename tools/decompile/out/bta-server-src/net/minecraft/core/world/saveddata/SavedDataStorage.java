package net.minecraft.core.world.saveddata;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ShortTag;
import com.mojang.nbt.tags.Tag;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.world.save.LevelStorage;
import org.slf4j.Logger;

public class SavedDataStorage {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final LevelStorage storage;
   private final Map<Serializable, SavedData> cache = new HashMap<>();
   private final List<SavedData> loadedData = new ArrayList<>();
   private final Map<String, Short> idCounts = new HashMap<>();

   public SavedDataStorage(LevelStorage storage) {
      this.storage = storage;
      this.loadMetadata();
   }

   public SavedData load(Class<? extends SavedData> savedDataClass, String id) {
      SavedData savedData = this.cache.get(id);
      if (savedData != null) {
         return savedData;
      } else {
         if (this.storage != null) {
            try {
               File file = this.storage.getDataFile(id);
               if (file != null && file.exists()) {
                  try {
                     savedData = savedDataClass.getConstructor(String.class).newInstance(id);
                  } catch (Exception var7) {
                     throw new RuntimeException("Failed to instantiate " + savedDataClass.toString(), var7);
                  }

                  InputStream fis = Files.newInputStream(file.toPath());
                  CompoundTag tag = NbtIo.readCompressed(fis);
                  fis.close();
                  savedData.load(tag.getCompound("data"));
               }
            } catch (Exception var8) {
               LOGGER.error("Exception loading saved data for id '{}'!", id, var8);
            }
         }

         if (savedData != null) {
            this.cache.put(id, savedData);
            this.loadedData.add(savedData);
         }

         return savedData;
      }
   }

   public void set(String id, SavedData savedData) {
      if (savedData == null) {
         throw new RuntimeException("Can't set null data");
      } else {
         if (this.cache.containsKey(id)) {
            this.loadedData.remove(this.cache.remove(id));
         }

         this.cache.put(id, savedData);
         this.loadedData.add(savedData);
      }
   }

   public void save() {
      for (SavedData savedData : this.loadedData) {
         if (savedData.isDirty()) {
            this.save(savedData);
            savedData.setDirty(false);
         }
      }
   }

   private void save(SavedData savedData) {
      if (this.storage != null) {
         try {
            File file = this.storage.getDataFile(savedData.id);
            if (file != null) {
               CompoundTag dataTag = new CompoundTag();
               savedData.save(dataTag);
               CompoundTag tag = new CompoundTag();
               tag.putCompound("data", dataTag);
               OutputStream fos = Files.newOutputStream(file.toPath());
               NbtIo.writeCompressed(tag, fos);
               fos.close();
            }
         } catch (Exception var6) {
            LOGGER.error("Failed to save data to '{}'!", this.storage.getDataFile(savedData.id).getPath(), var6);
         }
      }
   }

   private void loadMetadata() {
      try {
         this.idCounts.clear();
         if (this.storage == null) {
            return;
         }

         File file = this.storage.getDataFile("idcounts");
         if (file != null && file.exists()) {
            DataInputStream dis = new DataInputStream(Files.newInputStream(file.toPath()));
            CompoundTag tag = NbtIo.read(dis);
            dis.close();

            for (Tag<?> element : tag.getValues()) {
               if (element instanceof ShortTag) {
                  ShortTag shortTag = (ShortTag)element;
                  String type = shortTag.getTagName();
                  short metadata = shortTag.getValue();
                  this.idCounts.put(type, metadata);
               }
            }
         }
      } catch (Exception var9) {
         LOGGER.error("Failed to load all savedata id counts!", (Throwable)var9);
      }
   }

   public int getFreeMetadataFor(String type) {
      Short metadata = this.idCounts.get(type);
      if (metadata == null) {
         metadata = (short)0;
      } else {
         metadata = (short)(metadata + 1);
      }

      this.idCounts.put(type, metadata);
      if (this.storage == null) {
         return metadata;
      } else {
         try {
            File file = this.storage.getDataFile("idcounts");
            if (file != null) {
               CompoundTag tag = new CompoundTag();

               for (String dataType : this.idCounts.keySet()) {
                  short count = this.idCounts.get(dataType);
                  tag.putShort(dataType, count);
               }

               DataOutputStream dos = new DataOutputStream(Files.newOutputStream(file.toPath()));
               NbtIo.write(tag, dos);
               dos.close();
            }
         } catch (Exception var8) {
            LOGGER.error("Failed to create data file for '{}'!", this.storage.getDataFile("idcounts").getPath(), var8);
         }

         return metadata;
      }
   }
}
