package net.minecraft.core.world.save;

import net.minecraft.core.entity.player.Player;

public interface PlayerIO {
   void save(Player var1);

   void load(Player var1);
}
