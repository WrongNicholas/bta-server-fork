package net.minecraft.core.world;

import com.mojang.logging.LogUtils;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.motion.CarriedBlock;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.Side;
import org.slf4j.Logger;

public interface ICarriable {
   Logger LOGGER = LogUtils.getLogger();
   String TYPE_BLOCK = "block";

   void heldTick(World var1, Entity var2);

   boolean tryPlace(World var1, Entity var2, int var3, int var4, int var5, Side var6, double var7, double var9);

   void drop(World var1, Entity var2);

   boolean canBeCarried(World var1, Entity var2);

   ICarriable pickup(World var1, Entity var2);

   void writeToNBT(CompoundTag var1);

   void readFromNBT(CompoundTag var1);

   static ICarriable createAndLoadCarriable(Entity holder, CompoundTag tag) {
      String type = tag.getString("type");
      if (type.equalsIgnoreCase("block")) {
         return CarriedBlock.createAndLoadCarriedBlock(holder, tag);
      } else {
         LOGGER.warn("Could not identify carriable for type {}!", type);
         return null;
      }
   }
}
