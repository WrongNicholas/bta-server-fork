package net.minecraft.server.player;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.core.net.IUpdatePlayerListBox;
import net.minecraft.core.net.packet.PacketPlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;

public class PlayerListBox extends JList<String> implements IUpdatePlayerListBox {
   private final MinecraftServer mcServer;
   private int updateCounter = 0;

   public PlayerListBox(MinecraftServer minecraftserver) {
      this.mcServer = minecraftserver;
      minecraftserver.addPlayerListBox(this);
   }

   @Override
   public void update() {
      if (this.updateCounter++ % 20 == 0) {
         Vector<String> vector = new Vector<>();

         for (int i = 0; i < this.mcServer.playerList.playerEntities.size(); i++) {
            vector.add(this.mcServer.playerList.playerEntities.get(i).username);
         }

         this.setListData(vector);
      }
   }

   public static void updateList() {
      MinecraftServer server = MinecraftServer.getInstance();
      int playerCount = server.playerList.playerEntities.size();
      String[] players = new String[playerCount];
      String[] scores = new String[playerCount];

      for (int i = 0; i < playerCount; i++) {
         PlayerServer player = server.playerList.playerEntities.get(i);
         players[i] = player.getDisplayName();
         scores[i] = String.valueOf(player.getScore());
      }

      server.playerList.sendPacketToAllPlayers(new PacketPlayerList(playerCount, players, scores));
   }
}
