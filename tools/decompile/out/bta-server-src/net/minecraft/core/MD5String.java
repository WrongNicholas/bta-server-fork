package net.minecraft.core;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5String {
   private String string;

   public MD5String(String s) {
      this.string = s;
   }

   public String getString(String s) {
      try {
         String s1 = this.string + s;
         MessageDigest md = MessageDigest.getInstance("MD5");
         md.update(s1.getBytes(), 0, s1.length());
         return new BigInteger(1, md.digest()).toString(16);
      } catch (NoSuchAlgorithmException var4) {
         throw new RuntimeException(var4);
      }
   }
}
