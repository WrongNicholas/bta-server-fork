package net.minecraft.server.world;

import net.minecraft.core.world.save.LevelStorage;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.server.MinecraftServer;

public class WorldServerMulti extends WorldServer {
   public WorldServerMulti(
      MinecraftServer minecraftserver, LevelStorage isavehandler, String name, int dimensionId, WorldType worldType, long seed, WorldServer worldserver
   ) {
      super(minecraftserver, isavehandler, name, dimensionId, worldType, seed);
      this.savedDataStorage = worldserver.savedDataStorage;
   }
}
