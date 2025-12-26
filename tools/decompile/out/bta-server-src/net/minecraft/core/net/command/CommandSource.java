package net.minecraft.core.net.command;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public interface CommandSource {
   Collection<String> getPlayerUsernames();

   Collection<String> getPlayerNicknames();

   default Collection<String> getChatSuggestions() {
      return this.getPlayerUsernames();
   }

   default Collection<String> getEntitySuggestions() {
      return Collections.emptyList();
   }

   @Override
   String toString();

   @Nullable
   Player getSender();

   boolean hasAdmin();

   @Nullable
   Vec3 getCoordinates(boolean var1);

   @Nullable
   Vec3 getBlockCoordinates();

   boolean messageMayBeMultiline();

   void sendMessage(String var1);

   void sendMessage(Player var1, String var2);

   default void sendTranslatableMessage(String message, Object... args) {
      this.sendMessage(I18n.getInstance().translateKeyAndFormat(message, args));
   }

   default void sendTranslatableMessage(Player player, String message, Object... args) {
      this.sendMessage(player, I18n.getInstance().translateKeyAndFormat(message, args));
   }

   void sendMessageToAllPlayers(String var1);

   void sendPacketToAllPlayers(Supplier<Packet> var1);

   World getWorld();

   World getWorld(int var1);

   void movePlayerToDimension(Player var1, int var2);

   void teleportPlayerToPos(Player var1, double var2, double var4, double var6);

   void teleportPlayerToPosAndRot(Player var1, double var2, double var4, double var6, float var8, float var9);

   String getName();
}
