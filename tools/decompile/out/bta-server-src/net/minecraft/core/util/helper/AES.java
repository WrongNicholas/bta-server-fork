package net.minecraft.core.util.helper;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.HashMap;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class AES {
   public static HashMap<String, Key> keyChain = new HashMap<>();
   public static Key clientKeyChain;

   public static Key generateKey() throws Exception {
      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(256);
      return keyGenerator.generateKey();
   }

   public static String encrypt(String plainText, Key key) throws Exception {
      Cipher encryptCipher = Cipher.getInstance("AES");
      encryptCipher.init(1, key);
      byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(cipherText);
   }

   public static String decrypt(String cipherText, Key key) throws Exception {
      byte[] bytes = Base64.getDecoder().decode(cipherText);
      Cipher decryptCipher = Cipher.getInstance("AES");
      decryptCipher.init(2, key);
      return new String(decryptCipher.doFinal(bytes), StandardCharsets.UTF_8);
   }

   public static String getKey(Key key) {
      return Base64.getEncoder().encodeToString(key.getEncoded());
   }

   public static Key getKey(String key) throws Exception {
      byte[] byteKey = Base64.getDecoder().decode(key);
      return new SecretKeySpec(byteKey, "AES");
   }
}
