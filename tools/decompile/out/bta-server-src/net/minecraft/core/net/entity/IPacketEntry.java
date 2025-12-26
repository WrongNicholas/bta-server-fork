package net.minecraft.core.net.entity;

import net.minecraft.core.net.packet.Packet;

public interface IPacketEntry<T> extends INetworkEntry<T> {
   Packet getSpawnPacket(EntityTrackerEntry var1, T var2);
}
