package net.minecraft.core.entity.monster;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.GetMonsterSkinUrlThread;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobHuman extends MobMonster {
   public String skinUrl;
   public boolean slimModel = false;

   public MobHuman(World world) {
      super(world);
      new GetMonsterSkinUrlThread(this);
   }

   @Override
   protected Entity findPlayerToAttack() {
      return null;
   }

   @Override
   protected void onLabelled() {
      super.onLabelled();
      new GetMonsterSkinUrlThread(this);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      new GetMonsterSkinUrlThread(this);
   }
}
