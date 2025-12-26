package net.minecraft.core.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketSetMobSpawner;
import net.minecraft.core.util.phys.AABB;
import org.slf4j.Logger;

public class TileEntityMobSpawner extends TileEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   public int delay;
   private String mobId;
   public double yaw;
   public double yaw2 = 0.0;
   private int nearby;

   public TileEntityMobSpawner() {
      this.mobId = "minecraft:pig";
      this.delay = 20;
   }

   public String getMobId() {
      return this.mobId;
   }

   public void setMobId(String mobId) {
      if (mobId != null && mobId.equalsIgnoreCase("none")) {
         this.mobId = null;
      } else {
         this.mobId = mobId;
      }

      try {
         if (EntityDispatcher.Legacy.isLegacyKey(mobId)) {
            this.mobId = EntityDispatcher.idForClass(EntityDispatcher.Legacy.getClassFromEncodeID(mobId)).toString();
         }
      } catch (Exception var3) {
         LOGGER.error("Failed to convert legacy mobspawner id!", (Throwable)var3);
      }
   }

   public boolean anyPlayerInRange() {
      return this.worldObj != null && this.worldObj.getClosestPlayer(this.x + 0.5, this.y + 0.5, this.z + 0.5, 16.0) != null;
   }

   @Override
   public void tick() {
      if (this.worldObj.getBlockId(this.x, this.y, this.z) == Blocks.MOBSPAWNER.id()) {
         this.yaw2 = this.yaw;
         if (this.anyPlayerInRange()) {
            double xPos = (double)this.x + this.worldObj.rand.nextFloat();
            double yPos = (double)this.y + this.worldObj.rand.nextFloat();
            double zPos = (double)this.z + this.worldObj.rand.nextFloat();
            this.worldObj.spawnParticle("smoke", xPos, yPos, zPos, 0.0, 0.0, 0.0, 0);
            this.worldObj.spawnParticle("flame", xPos, yPos, zPos, 0.0, 0.0, 0.0, 0);

            for (this.yaw = this.yaw + 1000.0F / (this.delay + 200.0F); this.yaw > 360.0; this.yaw2 -= 360.0) {
               this.yaw -= 360.0;
            }

            if (!this.worldObj.isClientSide) {
               if (!this.worldObj.getDifficulty().canHostileMobsSpawn()) {
                  return;
               }

               if (this.mobId == null || this.mobId.equalsIgnoreCase("none")) {
                  return;
               }

               if (this.delay == -1) {
                  this.updateDelay();
               }

               if (this.delay > 0) {
                  this.delay--;
                  return;
               }

               if (this.mobId == null) {
                  this.nearby = this.countNearbySpawners();
               }

               byte byte0 = 4;

               for (int i = 0; i < byte0; i++) {
                  Mob mob = (Mob)EntityDispatcher.createEntityInWorld(this.mobId, this.worldObj);
                  if (mob == null) {
                     return;
                  }

                  int j = this.worldObj
                     .getEntitiesWithinAABB(mob.getClass(), AABB.getTemporaryBB(this.x, this.y, this.z, this.x + 1, this.y + 1, this.z + 1).grow(8.0, 4.0, 8.0))
                     .size();
                  if (j >= 6) {
                     this.updateDelay();
                     return;
                  }

                  double d6 = this.x + (this.worldObj.rand.nextDouble() - this.worldObj.rand.nextDouble()) * 4.0;
                  double d7 = this.y + this.worldObj.rand.nextInt(3) - 1;
                  double d8 = this.z + (this.worldObj.rand.nextDouble() - this.worldObj.rand.nextDouble()) * 4.0;
                  mob.moveTo(d6, d7, d8, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
                  if (!mob.canSpawnHere()) {
                     this.updateDelay();
                  } else {
                     this.worldObj.entityJoinedWorld(mob);

                     for (int k = 0; k < 20; k++) {
                        double d1 = this.x + 0.5 + (this.worldObj.rand.nextFloat() - 0.5) * 2.0;
                        double d3 = this.y + 0.5 + (this.worldObj.rand.nextFloat() - 0.5) * 2.0;
                        double d5 = this.z + 0.5 + (this.worldObj.rand.nextFloat() - 0.5) * 2.0;
                        this.worldObj.spawnParticle("smoke", d1, d3, d5, 0.0, 0.0, 0.0, 0);
                        this.worldObj.spawnParticle("flame", d1, d3, d5, 0.0, 0.0, 0.0, 0);
                        this.worldObj.playSoundAtEntity(null, mob, "mob.ghast.fireball", 0.025F, 0.75F);
                     }

                     mob.spawnExplosionParticle();
                     this.updateDelay();
                  }
               }
            }

            super.tick();
         }
      }
   }

   @Override
   public Packet getDescriptionPacket() {
      return new PacketSetMobSpawner(this.x, this.y, this.z, EntityDispatcher.classForId(this.mobId));
   }

   public int countNearbySpawners() {
      int r = 4;
      int count = 0;

      for (int i = -r; i <= r; i++) {
         for (int j = -r; j <= r; j++) {
            for (int k = -r; k <= r; k++) {
               int id = this.worldObj.getBlockId(this.x + i, this.y + j, this.z + k);
               if (id == Blocks.MOBSPAWNER.id()) {
                  count++;
               }
            }
         }
      }

      return count;
   }

   private void updateDelay() {
      this.delay = 200 + this.worldObj.rand.nextInt(600);
      if (this.mobId == null) {
         this.delay *= 2;
         LOGGER.debug("x2");
         if (this.nearby > 1) {
            LOGGER.debug("x4");
            this.delay *= 4;
         }
      }
   }

   @Override
   public void readFromNBT(CompoundTag tag) {
      super.readFromNBT(tag);
      this.setMobId(tag.getString("EntityId"));
      this.delay = tag.getShort("Delay");
   }

   @Override
   public void writeToNBT(CompoundTag tag) {
      super.writeToNBT(tag);
      tag.putString("EntityId", this.mobId != null ? this.mobId : "none");
      tag.putShort("Delay", (short)this.delay);
   }
}
