package net.minecraft.core.data.gamerule;

import com.mojang.nbt.tags.ByteTag;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;

public class GameRuleBoolean extends GameRule<Boolean> {
   public GameRuleBoolean(String key, Boolean defaultValue) {
      super(key, defaultValue);
   }

   public void writeToNBT(CompoundTag tag, Boolean value) {
      ByteTag ruleTag = new ByteTag();
      ruleTag.setValue((byte)(value ? 1 : 0));
      tag.put(this.getKey(), ruleTag);
   }

   public Boolean readFromNBT(CompoundTag tag) {
      Tag<?> ruleTag = tag.getTag(this.getKey());
      if (ruleTag instanceof ByteTag) {
         ByteTag ruleTagByte = (ByteTag)ruleTag;
         return ruleTagByte.getValue() != 0;
      } else {
         return this.getDefaultValue();
      }
   }

   public Boolean parseFromString(String string) {
      if (string.equalsIgnoreCase("true")) {
         return true;
      } else if (string.equalsIgnoreCase("false")) {
         return false;
      } else {
         try {
            int val = Integer.parseInt(string);
            return val != 0;
         } catch (NumberFormatException var3) {
            return null;
         }
      }
   }
}
