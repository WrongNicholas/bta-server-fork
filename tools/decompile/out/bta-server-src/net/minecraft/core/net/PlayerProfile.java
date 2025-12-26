package net.minecraft.core.net;

import java.util.UUID;
import net.minecraft.core.net.command.TextFormatting;

public class PlayerProfile {
   public String playerName;
   public UUID uuid;
   public String nickname;
   public int scoreTotal;
   public byte chatColor;
   public boolean isOperator;

   public PlayerProfile(String name, String nickname, UUID uuid, int score, byte chatColour, boolean isOperator) {
      this.playerName = name;
      this.uuid = uuid;
      this.nickname = nickname;
      this.scoreTotal = score;
      this.chatColor = chatColour;
      this.isOperator = isOperator;
   }

   public String getDisplayName() {
      String name = this.nickname;
      if (name.isEmpty()) {
         name = this.playerName;
      } else {
         name = TextFormatting.ITALIC + name;
      }

      return TextFormatting.get(this.chatColor) + name;
   }
}
