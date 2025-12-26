package net.minecraft.core.util.helper;

import com.b100.json.JsonParser;
import com.b100.json.element.JsonArray;
import com.b100.json.element.JsonObject;
import com.b100.utils.StringUtils;
import com.mojang.logging.LogUtils;
import java.util.Base64;
import java.util.UUID;
import net.minecraft.core.entity.monster.MobHuman;
import org.slf4j.Logger;

public class GetMonsterSkinUrlThread extends Thread {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static int counter = 0;
   private static final String urlUUID = "https://api.mojang.com/users/profiles/minecraft/";
   private static final String urlSkin = "https://sessionserver.mojang.com/session/minecraft/profile/";
   private static final JsonParser jsonParser = new JsonParser();
   private MobHuman monster;
   private UUID uuid;

   public GetMonsterSkinUrlThread(MobHuman monster) {
      this.monster = monster;
      this.setDaemon(true);
      this.setName("GetSkinUrlThread " + counter++);
      this.start();
   }

   @Override
   public void run() {
      String name = this.monster.nickname;
      if (name != null && !name.isEmpty()) {
         String string = null;

         for (int i = 0; i < 3; i++) {
            string = this.getSkinObject(name);
            if (string != null) {
               break;
            }

            try {
               sleep(5000L);
            } catch (InterruptedException var7) {
               break;
            }
         }

         if (string != null) {
            JsonObject object = jsonParser.parse(string);
            JsonArray properties = object.getArray("properties");
            JsonObject textureProperty = properties.query(e -> e.getAsObject().getString("name").equalsIgnoreCase("textures")).getAsObject();
            JsonObject texturesObject = jsonParser.parse(decodeBase64(textureProperty.getString("value"))).getObject("textures");
            if (texturesObject.has("SKIN")) {
               this.monster.skinUrl = texturesObject.getObject("SKIN").getString("url");
               if (texturesObject.getObject("SKIN").has("metadata") && texturesObject.getObject("SKIN").getObject("metadata").getString("model").equals("slim")
                  )
                {
                  this.monster.slimModel = true;
               }
            }

            LOGGER.info("Skin URL: {}", this.monster.skinUrl);
         }
      }
   }

   public static String decodeBase64(String string) {
      return new String(Base64.getDecoder().decode(string));
   }

   private String getSkinObject(String name) {
      this.uuid = UUIDHelper.getUUIDFromName(name);
      if (this.uuid == null) {
         return null;
      } else {
         LOGGER.info("Loading Skin for Player {}...", name);

         try {
            return StringUtils.getWebsiteContentAsString("https://sessionserver.mojang.com/session/minecraft/profile/" + this.uuid);
         } catch (Exception var4) {
            LOGGER.warn("Invalid UUID {}, or can't connect to the Mojang API.", this.uuid);
            return null;
         }
      }
   }
}
