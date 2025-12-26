package net.minecraft.core.util.helper;

import java.util.ArrayList;
import java.util.List;

public class DamageType {
   private static final List<DamageType> values = new ArrayList<>();
   public static final DamageType COMBAT = new DamageType("damagetype.combat", true, true, "minecraft:gui/hud/protection_combat");
   public static final DamageType BLAST = new DamageType("damagetype.blast", true, true, "minecraft:gui/hud/protection_blast");
   public static final DamageType FALL = new DamageType("damagetype.fall", true, true, "minecraft:gui/hud/protection_fall");
   public static final DamageType FIRE = new DamageType("damagetype.fire", true, true, "minecraft:gui/hud/protection_fire");
   public static final DamageType DROWN = new DamageType("damagetype.drown", false, false, "minecraft:gui/hud/protection_combat");
   public static final DamageType GENERIC = new DamageType("damagetype.generic", true, false, "minecraft:gui/hud/protection_combat");
   private final String languageKey;
   private final boolean shouldDamageArmor;
   private final boolean shouldDisplay;
   private final String iconIndex;

   public static List<DamageType> values() {
      return values;
   }

   public DamageType(String languageKey, boolean shouldDamageArmor, boolean shouldDisplay, String icon) {
      values().add(this);
      this.languageKey = languageKey;
      this.shouldDamageArmor = shouldDamageArmor;
      this.shouldDisplay = shouldDisplay;
      this.iconIndex = icon;
   }

   public String getLanguageKey() {
      return this.languageKey;
   }

   public boolean shouldDamageArmor() {
      return this.shouldDamageArmor;
   }

   public boolean shouldDisplay() {
      return this.shouldDisplay;
   }

   public String getIcon() {
      return this.iconIndex;
   }
}
