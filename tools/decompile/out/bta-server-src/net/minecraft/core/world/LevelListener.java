package net.minecraft.core.world;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.sound.SoundCategory;
import org.jetbrains.annotations.Nullable;

public interface LevelListener {
   int EVENT_DISPENSER_ITEM = 1000;
   int EVENT_DISPENSER_EMPTY = 1001;
   int EVENT_DISPENSER_PROJECTILE = 1002;
   int EVENT_DOOR_SOUND = 1003;
   int EVENT_DISPENSER_PARTICLES = 2000;
   int EVENT_BLOCK_BREAK = 2001;
   int EVENT_FIRE_EXTINGUISH = 1004;
   int EVENT_JUKEBOX_TOGGLE = 1005;
   int EVENT_PISTON_EXTEND = 1006;
   int EVENT_PISTON_RETRACT = 1007;

   void blockChanged(int var1, int var2, int var3);

   void setBlocksDirty(int var1, int var2, int var3, int var4, int var5, int var6);

   void playSound(Entity var1, String var2, SoundCategory var3, double var4, double var6, double var8, float var10, float var11);

   void addParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12, int var14, double var15);

   void addParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12, int var14);

   void entityAdded(Entity var1);

   void entityRemoved(Entity var1);

   void allChanged(boolean var1, boolean var2);

   void playStreamingMusic(String var1, String var2, int var3, int var4, int var5);

   void tileEntityChanged(int var1, int var2, int var3, TileEntity var4);

   void levelEvent(@Nullable Player var1, int var2, int var3, int var4, int var5, int var6);
}
