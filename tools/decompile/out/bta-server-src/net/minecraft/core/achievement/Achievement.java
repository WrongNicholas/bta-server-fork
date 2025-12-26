package net.minecraft.core.achievement;

import net.minecraft.core.achievement.stat.Stat;
import net.minecraft.core.achievement.stat.StatDescFormatter;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.util.collection.NamespaceID;

public class Achievement extends Stat {
   public static final Achievement.Type TYPE_NORMAL = new Achievement.Type("minecraft:gui/screen/achievement/achievement", -1, -8355712, false);
   public static final Achievement.Type TYPE_SPECIAL = new Achievement.Type("minecraft:gui/screen/achievement/achievement_special", -128, -8355776, false);
   public static final Achievement.Type TYPE_SECRET = new Achievement.Type("minecraft:gui/screen/achievement/achievement_secret", -8355585, -12566400, true);
   public final Achievement parent;
   private final String description;
   private StatDescFormatter statStringFormatter;
   public final int iconItemId;
   private Achievement.Type type = TYPE_NORMAL;

   public Achievement(NamespaceID id, String name, IItemConvertible icon, Achievement parent) {
      this(id, name, icon.asItem().id, parent);
   }

   public Achievement(NamespaceID id, String name, int iconItemId, Achievement parent) {
      super(NamespaceID.getPermanent(id.namespace(), "achievement/" + id.value()), "achievement." + name);
      this.iconItemId = iconItemId;
      this.description = "achievement." + name;
      this.parent = parent;
   }

   public Achievement setClientsideAchievement() {
      this.clientside = true;
      return this;
   }

   public Achievement setType(Achievement.Type type) {
      this.type = type;
      return this;
   }

   public Achievement registerAchievement() {
      super.registerStat();
      Achievements.achievementList.add(this);
      return this;
   }

   @Override
   public boolean isAchievement() {
      return true;
   }

   public String getDescription() {
      return this.statStringFormatter != null
         ? this.statStringFormatter.formatString(I18n.getInstance().translateDescKey(this.description))
         : I18n.getInstance().translateDescKey(this.description);
   }

   public Achievement setDescriptionFormatter(StatDescFormatter descFormatter) {
      this.statStringFormatter = descFormatter;
      return this;
   }

   public Achievement.Type getType() {
      return this.type;
   }

   @Override
   public Stat registerStat() {
      return this.registerAchievement();
   }

   @Override
   public Stat setClientside() {
      return this.setClientsideAchievement();
   }

   public static class Type {
      public final String texture;
      public final int colorName;
      public final int colorNameLocked;
      public final boolean hidden;

      public Type(String texture, int colorName, int colorNameLocked, boolean hidden) {
         this.texture = texture;
         this.colorName = colorName;
         this.colorNameLocked = colorNameLocked;
         this.hidden = hidden;
      }
   }
}
