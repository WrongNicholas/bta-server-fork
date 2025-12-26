package net.minecraft.core.achievement.stat;

import net.minecraft.core.util.collection.NamespaceID;

public class StatMob extends Stat {
   private final NamespaceID mobId;

   public StatMob(NamespaceID id, String name, NamespaceID mobId) {
      super(id, name);
      this.mobId = mobId;
   }

   public NamespaceID getMobId() {
      return this.mobId;
   }
}
