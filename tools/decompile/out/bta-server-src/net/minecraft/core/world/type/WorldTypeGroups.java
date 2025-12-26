package net.minecraft.core.world.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.world.Dimension;

public class WorldTypeGroups {
   public static final List<WorldTypeGroups.Group> GROUPS = new ArrayList<>();
   public static final WorldTypeGroups.Group DEFAULT = new WorldTypeGroups.Group(WorldTypes.OVERWORLD_EXTENDED);

   static {
      GROUPS.add(DEFAULT);
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_DEFAULT));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_WINTER));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_FLOATING));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_AMPLIFIED));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_ISLANDS));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_INLAND));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.FLAT));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_HELL));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_WOODS));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_PARADISE));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_CLASSIC));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_INDEV));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_RETRO));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.OVERWORLD_SKYBLOCK).with(Dimension.NETHER, WorldTypes.NETHER_SKYBLOCK));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.EMPTY));
      GROUPS.add(new WorldTypeGroups.Group(WorldTypes.DEBUG));
   }

   public static class Group {
      private final Map<Dimension, WorldType> worldTypes = new HashMap<>();

      public Group(WorldType overworldType) {
         this.worldTypes.put(Dimension.OVERWORLD, overworldType);
      }

      public WorldTypeGroups.Group with(Dimension dimension, WorldType worldType) {
         this.worldTypes.put(dimension, worldType);
         return this;
      }

      public WorldType get(Dimension dimension) {
         return this.worldTypes.get(dimension);
      }
   }
}
