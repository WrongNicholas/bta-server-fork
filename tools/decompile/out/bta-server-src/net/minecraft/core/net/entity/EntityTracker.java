package net.minecraft.core.net.entity;

import java.util.Set;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.Packet;

public interface EntityTracker {
   void trackEntity(Entity var1);

   EntityTrackerEntry trackEntity(Entity var1, int var2, int var3, boolean var4);

   void untrackEntity(Entity var1);

   void tick();

   void sendPacketToTrackedPlayers(Entity var1, Packet var2);

   void sendPacketToTrackedPlayersAndTrackedEntity(Entity var1, Packet var2);

   void removeTrackedPlayerSymmetric(Player var1);

   Set<? extends EntityTrackerEntry> getTrackedEntries();
}
