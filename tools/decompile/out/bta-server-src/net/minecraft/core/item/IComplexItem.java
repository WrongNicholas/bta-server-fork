package net.minecraft.core.item;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.world.World;

public interface IComplexItem {
   Packet sendPacketData(ItemStack var1, World var2, Player var3);
}
