package net.minecraft.core.achievement.stat;

import net.minecraft.core.util.collection.NamespaceID;

public class StatBasic extends Stat {
   public StatBasic(NamespaceID id, String name, StatValueFormatter type) {
      super(id, name, type);
   }

   public StatBasic(NamespaceID id, String name) {
      super(id, name);
   }

   @Override
   public Stat registerStat() {
      super.registerStat();
      StatList.basicStats.add(this);
      return this;
   }
}
