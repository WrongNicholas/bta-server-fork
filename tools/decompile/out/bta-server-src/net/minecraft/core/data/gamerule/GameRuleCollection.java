package net.minecraft.core.data.gamerule;

import com.mojang.nbt.tags.CompoundTag;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.data.registry.Registries;

public final class GameRuleCollection {
   private final Map<GameRule<?>, Object> gameRules = new HashMap<>();

   public GameRuleCollection() {
      for (GameRule<?> gameRule : Registries.GAME_RULES) {
         this.gameRules.put(gameRule, gameRule.getDefaultValue());
      }
   }

  @SuppressWarnings("unchecked")
  private static <T> void setValueCaptured(GameRuleCollection c, GameRule<?> rule, Object value) {
      c.setValue((GameRule<T>) rule, (T) value);
  }

   public <T> T getValue(GameRule<T> gameRule) {
      Object o = this.gameRules.get(gameRule);
      if (o == null) {
         o = this.gameRules.put(gameRule, gameRule.getDefaultValue());
      }

      return (T)o;
   }

   public <T> void setValue(GameRule<T> gameRule, T value) {
      this.gameRules.put(gameRule, value);
   }

   public void setValues(GameRuleCollection collection) {
      if (collection != null) {
         for (GameRule<?> gameRule : Registries.GAME_RULES) {
            Object o = collection.getValue(gameRule);
            if (o == null) {
               o = gameRule.getDefaultValue();
            }

            this.gameRules.put(gameRule, o);
         }
      }
   }

   public GameRuleCollection copy() {
      GameRuleCollection out = new GameRuleCollection();

      for (GameRule<?> gameRule : this.gameRules.keySet()) {
         Object o = this.gameRules.get(gameRule);
         out.setValueCaptured(out,gameRule, o);
      }

      return out;
   }

   public static GameRuleCollection readFromNBT(CompoundTag tag) {
      GameRuleCollection collection = new GameRuleCollection();

      for (GameRule<?> gameRule : Registries.GAME_RULES) {
         collection.setValueCaptured(collection, gameRule, gameRule.readFromNBT(tag));
      }

      return collection;
   }

   public static void writeToNBT(CompoundTag tag, GameRuleCollection collection) {
      for (GameRule<?> gameRule : Registries.GAME_RULES) {
         Object o = collection.getValue(gameRule);
         if (o == null) {
            o = gameRule.getDefaultValue();
         }

         ((GameRule<Object>)gameRule).writeToNBT(tag, o);
      }
   }
}
