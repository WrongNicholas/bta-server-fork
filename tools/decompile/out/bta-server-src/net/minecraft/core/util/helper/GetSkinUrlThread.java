package net.minecraft.core.util.helper;

import com.b100.json.JsonParser;
import com.b100.json.element.JsonArray;
import com.b100.json.element.JsonObject;
import com.b100.utils.StringUtils;
import com.mojang.logging.LogUtils;
import java.util.Base64;
import java.util.UUID;
import net.minecraft.core.entity.player.Player;
import org.slf4j.Logger;

public class GetSkinUrlThread extends Thread {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static int counter = 0;
   private static final String urlUUID = "https://api.mojang.com/users/profiles/minecraft/";
   private static final String urlSkin = "https://sessionserver.mojang.com/session/minecraft/profile/";
   private static final JsonParser jsonParser = new JsonParser();
   private Player player;
   private UUID uuid;

   public GetSkinUrlThread(Player player) {
      this.player = player;
      this.setDaemon(true);
      this.setName("GetSkinUrlThread " + counter++);
      this.start();
   }

   @Override
   public void run() {
      String name = this.player.username;
      this.uuid = this.player.uuid;
      if (name != null && !name.isEmpty()) {
         String string = null;

         for (int i = 0; i < 3; i++) {
            string = this.getSkinObject(name);
            if (string != null && !string.isEmpty()) {
               break;
            }

            try {
               sleep(5000L);
            } catch (InterruptedException var7) {
               break;
            }
         }

         if (string != null && !string.isEmpty()) {
            JsonObject object = jsonParser.parse(string);
            JsonArray properties = object.getArray("properties");
            JsonObject textureProperty = properties.query(e -> e.getAsObject().getString("name").equalsIgnoreCase("textures")).getAsObject();
            JsonObject texturesObject = jsonParser.parse(decodeBase64(textureProperty.getString("value"))).getObject("textures");
            if (texturesObject.has("SKIN")) {
               this.player.skinURL = texturesObject.getObject("SKIN").getString("url");
               if (texturesObject.getObject("SKIN").has("metadata") && texturesObject.getObject("SKIN").getObject("metadata").getString("model").equals("slim")
                  )
                {
                  this.player.slimModel = true;
               }
            }

            if (texturesObject.has("CAPE")) {
               this.player.capeURL = texturesObject.getObject("CAPE").getString("url");
            }

            LOGGER.info("Skin URL: {}", this.player.skinURL);
            LOGGER.info("Cape URL: {}", this.player.capeURL);
         }
      }
   }

   public static String decodeBase64(String string) {
      return new String(Base64.getDecoder().decode(string));
   }

   private String getSkinObject(String name) {
      if (this.uuid == null) {
         this.uuid = UUIDHelper.getUUIDFromName(name);
         if (this.uuid == null) {
            return null;
         }
      }

      LOGGER.info("Loading Skin for Player {}...", name);

      try {
         return StringUtils.getWebsiteContentAsString("https://sessionserver.mojang.com/session/minecraft/profile/" + this.uuid);
      } catch (Exception var4) {
         LOGGER.warn("Invalid UUID {}, or can't connect to the Mojang API.", this.uuid);
         return null;
      }
   }
}
