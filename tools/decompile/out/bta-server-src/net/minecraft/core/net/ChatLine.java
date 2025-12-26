package net.minecraft.core.net;

public class ChatLine {
   public String message;
   public int updateCounter;

   public ChatLine(String s) {
      this.message = s;
      this.updateCounter = 0;
   }
}
