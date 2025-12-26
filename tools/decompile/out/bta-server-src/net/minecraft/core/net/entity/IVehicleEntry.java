package net.minecraft.core.net.entity;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public interface IVehicleEntry<T> extends IPacketEntry<T> {
   Entity getEntity(
      World var1,
      double var2,
      double var4,
      double var6,
      int var8,
      boolean var9,
      double var10,
      double var12,
      double var14,
      Entity var16,
      @Nullable CompoundTag var17
   );

   PacketAddEntity getSpawnPacket(EntityTrackerEntry var1, T var2);
}
