package net.minecraft.core.net.entity;

import java.util.List;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.Packet;

public interface EntityTrackerEntry {
   void tick(List<Player> var1);

   void sendPacketToTrackedPlayers(Packet var1);

   void sendPacketToTrackedPlayersAndTrackedEntity(Packet var1);

   void sendDestroyEntityPacketToTrackedPlayers();

   void removeFromTrackedPlayers(Player var1);

   void updatePlayerEntity(Player var1);

   void updatePlayerEntities(List<Player> var1);

   void removeTrackedPlayerSymmetric(Player var1);

   Entity getTrackedEntity();
}
