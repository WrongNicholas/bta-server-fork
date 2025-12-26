package net.minecraft.core.achievement.stat;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.collection.NamespaceID;

public class Stat {
   private static final NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.US);
   public static StatValueFormatter statTypeSimple = new SimpleValueFormatter();
   private static final DecimalFormat decimalFormat = new DecimalFormat("########0.00");
   public static StatValueFormatter statTypeTime = new TimeValueFormatter();
   public static StatValueFormatter statTypeDistance = new DistanceValueFormatter();
   public final NamespaceID statId;
   final String statKey;
   public boolean clientside = false;
   private final StatValueFormatter statType;

   public Stat(NamespaceID id, String key, StatValueFormatter statType) {
      this.statId = id.makePermanent();
      this.statKey = key;
      this.statType = statType;
   }

   public Stat(NamespaceID id, String key) {
      this(id, key, statTypeSimple);
   }

   public String getStatName() {
      return I18n.getInstance().translateKey(this.statKey);
   }

   public String getStatKey() {
      return this.statKey;
   }

   public Stat setClientside() {
      this.clientside = true;
      return this;
   }

   public Stat registerStat() {
      if (StatList.statMap.containsKey(this.statId)) {
         throw new RuntimeException(
            "Duplicate stat id: \"" + StatList.statMap.get(this.statId).getStatName() + "\" and \"" + this.statKey + "\" at id " + this.statId
         );
      } else {
         StatList.registeredStats.add(this);
         StatList.statMap.put(this.statId, this);
         return this;
      }
   }

   public boolean isAchievement() {
      return false;
   }

   public String getFormattedValue(int value) {
      return this.statType.formatValue(value);
   }

   @Override
   public String toString() {
      return this.statKey;
   }

   static NumberFormat getNumberFormat() {
      return numberFormat;
   }

   static DecimalFormat getDecimalFormat() {
      return decimalFormat;
   }
}
