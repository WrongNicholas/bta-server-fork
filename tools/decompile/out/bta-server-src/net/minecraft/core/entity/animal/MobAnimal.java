package net.minecraft.core.entity.animal;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.MobPathfinder;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Seasons;
import org.jetbrains.annotations.NotNull;

public abstract class MobAnimal extends MobPathfinder implements Creature {
   public Player closestPlayer;

   public MobAnimal(World world) {
      super(world);
      this.scoreValue = 10;
   }

   @Override
   protected void updateAI() {
      super.updateAI();
      this.checkForPlayerHoldingItem();
   }

   protected void checkForPlayerHoldingItem() {
      if (this.target == null) {
         this.closestPlayer = this.world.getClosestPlayer(this.x, this.y, this.z, 10.0);
      }

      if (this.closestPlayer != null) {
         if (this.isFavouriteItem(this.closestPlayer.getHeldItem())) {
            this.setTarget(this.closestPlayer);
         } else {
            this.setTarget(null);
            this.closestPlayer = null;
         }
      }

      if (this.target != null) {
         float distanceToEntity = this.target.distanceTo(this);
         if (distanceToEntity < 3.0F) {
            this.moveForward = 0.0F;
         }
      }
   }

   @Override
   public int getMaxSpawnedInChunk() {
      if (this.world.getSeasonManager().getCurrentSeason() == Seasons.OVERWORLD_SPRING) {
         return 8;
      } else {
         return this.world.getSeasonManager().getCurrentSeason() == Seasons.OVERWORLD_WINTER ? 1 : 4;
      }
   }

   @Override
   protected float getBlockPathWeight(int x, int y, int z) {
      return this.world.getBlockId(x, y - 1, z) == Blocks.GRASS.id() ? 10.0F : this.world.getLightBrightness(x, y, z) - 0.5F;
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
   }

   @Override
   public boolean canSpawnHere() {
      int x = MathHelper.floor(this.x);
      int y = MathHelper.floor(this.bb.minY);
      int z = MathHelper.floor(this.z);
      int id = this.world.getBlockId(x, y - 1, z);
      return Blocks.blocksList[id] == null
         ? false
         : Blocks.blocksList[id].hasTag(BlockTags.PASSIVE_MOBS_SPAWN) && this.world.getFullBlockLightValue(x, y, z) > 8 && super.canSpawnHere();
   }

   @Override
   public int getAmbientSoundInterval() {
      return 120;
   }

   public boolean isFavouriteItem(ItemStack itemStack) {
      return false;
   }
}
