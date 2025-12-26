package net.minecraft.core.util.helper;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSA {
   public static KeyPair RSAKeyChain;

   public static KeyPair generateKeyPair() throws Exception {
      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
      generator.initialize(2048, new SecureRandom());
      return generator.generateKeyPair();
   }

   public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
      Cipher encryptCipher = Cipher.getInstance("RSA");
      encryptCipher.init(1, publicKey);
      byte[] cipherText = encryptCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(cipherText);
   }

   public static String decrypt(String cipherText, PrivateKey privateKey) throws Exception {
      byte[] bytes = Base64.getDecoder().decode(cipherText);
      Cipher decriptCipher = Cipher.getInstance("RSA");
      decriptCipher.init(2, privateKey);
      return new String(decriptCipher.doFinal(bytes), StandardCharsets.UTF_8);
   }

   public static String getPublicKey(PublicKey publicKey) {
      return Base64.getEncoder().encodeToString(publicKey.getEncoded());
   }

   public static String getPrivateKey(PrivateKey privateKey) {
      return Base64.getEncoder().encodeToString(privateKey.getEncoded());
   }

   public static PublicKey getPublicKey(String key) throws Exception {
      byte[] byteKey = Base64.getDecoder().decode(key);
      X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePublic(X509publicKey);
   }

   public static PrivateKey getPrivateKey(String key) throws Exception {
      byte[] byteKey = Base64.getDecoder().decode(key);
      PKCS8EncodedKeySpec PKCS8privateKey = new PKCS8EncodedKeySpec(byteKey);
      KeyFactory kf = KeyFactory.getInstance("RSA");
      return kf.generatePrivate(PKCS8privateKey);
   }
}
