package net.minecraft.core.entity.monster;

import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.World;

public class MobGiant extends MobMonster {
   public MobGiant(World world) {
      super(world);
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "zombie");
      this.moveSpeed = 0.5F;
      this.attackStrength = 50;
      this.heightOffset *= 6.0F;
      this.setSize(this.bbWidth * 6.0F, this.bbHeight * 6.0F);
   }

   @Override
   public int getMaxHealth() {
      return super.getMaxHealth() * 10;
   }

   @Override
   protected float getBlockPathWeight(int x, int y, int z) {
      return this.world.getLightBrightness(x, y, z) - 0.5F;
   }
}
