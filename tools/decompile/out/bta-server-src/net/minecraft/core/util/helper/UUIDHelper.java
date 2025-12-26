package net.minecraft.core.util.helper;

import com.b100.json.JsonParser;
import com.b100.json.element.JsonObject;
import com.b100.utils.StringUtils;
import com.mojang.logging.LogUtils;
import com.mojang.nbt.tags.CompoundTag;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class UUIDHelper {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static String urlUUID = "https://api.minecraftservices.com/minecraft/profile/lookup/name/%s";
   private static final JsonParser jsonParser = new JsonParser();
   public static final Map<String, UUID> nameToUUIDMap = new HashMap<>();

   public static boolean isUUID(String usernameOrUUID) {
      return usernameOrUUID.length() == 36 || usernameOrUUID.length() == 32;
   }

   public static void runConversionAction(String username, @Nullable UUIDHelper.UUIDFunction successAction, @Nullable UUIDHelper.StringFunction failAction) {
      if (nameToUUIDMap.containsKey(username)) {
         UUID uuid = nameToUUIDMap.get(username);
         if (uuid != null) {
            if (successAction != null) {
               successAction.run(uuid);
            }
         } else if (failAction != null) {
            failAction.run(username);
         }
      } else {
         new Thread(() -> {
            try {
               UUID uuidx = getUUIDFromName(username);
               if (uuidx != null) {
                  if (successAction != null) {
                     successAction.run(uuidx);
                  }

                  nameToUUIDMap.put(username, uuidx);
               } else {
                  if (failAction != null) {
                     failAction.run(username);
                  }

                  nameToUUIDMap.put(username, null);
               }
            } catch (Exception var4) {
               var4.printStackTrace();
               if (failAction != null) {
                  failAction.run(username);
               }
            }
         }).start();
      }
   }

   @Nullable
   public static UUID getUUIDFromName(String username) {
      if (nameToUUIDMap.containsKey(username)) {
         return nameToUUIDMap.get(username);
      } else {
         String string;
         try {
            string = StringUtils.getWebsiteContentAsString(String.format(urlUUID, username));
         } catch (Exception var5) {
            LOGGER.error("Can't connect to Mojang API.", (Throwable)var5);
            return null;
         }

         if (string.isEmpty()) {
            LOGGER.error("Player {} doesn't exist!", username);
            return null;
         } else {
            String uuid;
            try {
               JsonObject contentParsed = jsonParser.parse(string);
               uuid = contentParsed.getString("id");
            } catch (Exception var4) {
               var4.printStackTrace();
               return null;
            }

            if (uuid == null) {
               return null;
            } else {
               uuid = untrimUUID(uuid);
               return UUID.fromString(uuid);
            }
         }
      }
   }

   public static String untrimUUID(String uuid) {
      if (uuid.length() == 32) {
         String s1 = uuid.substring(0, 8);
         String s2 = uuid.substring(8, 12);
         String s3 = uuid.substring(12, 16);
         String s4 = uuid.substring(16, 20);
         String s5 = uuid.substring(20, 32);
         return s1 + "-" + s2 + "-" + s3 + "-" + s4 + "-" + s5;
      } else {
         return uuid;
      }
   }

   @Nullable
   public static UUID readFromTag(@NotNull CompoundTag tag, @NotNull String keyBase) {
      if (tag.containsKey(keyBase + "_msb") && tag.containsKey(keyBase + "_lsb")) {
         long msb = tag.getLong(keyBase + "_msb");
         long lsb = tag.getLong(keyBase + "_lsb");
         return new UUID(msb, lsb);
      } else {
         return null;
      }
   }

   public static void writeToTag(@NotNull CompoundTag tag, @Nullable UUID uuid, @NotNull String keyBase) {
      if (uuid != null) {
         tag.putLong(keyBase + "_msb", uuid.getMostSignificantBits());
         tag.putLong(keyBase + "_lsb", uuid.getLeastSignificantBits());
      }
   }

   @FunctionalInterface
   public interface StringFunction {
      void run(String var1);
   }

   @FunctionalInterface
   public interface UUIDFunction {
      void run(UUID var1);
   }
}
