package net.minecraft.core.util.helper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestHandler {
   public static int post(String url) {
      try {
         URL target = new URL(url);
         HttpURLConnection conn = (HttpURLConnection)target.openConnection();
         conn.setRequestMethod("POST");
         conn.connect();
         return conn.getResponseCode();
      } catch (IOException var3) {
         return 503;
      }
   }
}
