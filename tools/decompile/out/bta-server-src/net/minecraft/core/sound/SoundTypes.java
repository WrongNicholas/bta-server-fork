package net.minecraft.core.sound;

import com.b100.utils.StringUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;

public class SoundTypes {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<Integer, String> idToSoundNameMap = new HashMap<>();
   private static final Map<String, Integer> soundNameToIdMap = new HashMap<>();
   private static final String format = "/assets/%s/sounds/sounds.json";
   private static int idCount = 0;

   public static void registerSounds() {
      loadSoundsJson("minecraft");
   }

   public static void loadSoundsJson(String namespace) {
      JsonObject o;
      try {
         o = loadStreamAsElement(SoundTypes.class.getResourceAsStream(String.format("/assets/%s/sounds/sounds.json", namespace))).getAsJsonObject();
      } catch (Exception var5) {
         LOGGER.error("Failed to load sounds.json from '{}' as a json object!", namespace, var5);
         return;
      }

      boolean isMC = namespace.equals("minecraft");

      for (String s : o.asMap().keySet()) {
         register(namespace + ":" + s);
         if (isMC) {
            register(s);
         }
      }
   }

   private static JsonElement loadStreamAsElement(InputStream stream) throws JsonParseException {
      return JsonParser.parseString(StringUtils.readInputString(stream));
   }

   public static boolean register(String soundPath) {
      if (soundPath.length() > 128) {
         throw new RuntimeException("Sound Path is too long: '" + soundPath + "'!");
      } else if (soundNameToIdMap.containsKey(soundPath)) {
         return false;
      } else {
         int id = idCount++;
         idCount %= 65536;
         soundNameToIdMap.put(soundPath, id);
         idToSoundNameMap.put(id, soundPath);
         return true;
      }
   }

   public static int getSoundId(String soundPath) {
      Integer id = soundNameToIdMap.get(soundPath);
      return id == null ? -1 : id;
   }

   public static String getSoundById(int id) {
      return idToSoundNameMap.get(id);
   }

   public static void setSoundIds(Map<Integer, String> soundIds) {
      idToSoundNameMap.clear();
      soundNameToIdMap.clear();

      for (Entry<Integer, String> entry : soundIds.entrySet()) {
         int id = entry.getKey();
         String name = entry.getValue();
         idToSoundNameMap.put(id, name);
         soundNameToIdMap.put(name, id);
      }
   }

   public static Map<Integer, String> getSoundIds() {
      return idToSoundNameMap;
   }
}
