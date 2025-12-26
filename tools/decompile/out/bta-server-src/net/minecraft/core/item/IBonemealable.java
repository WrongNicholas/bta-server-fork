package net.minecraft.core.item;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public interface IBonemealable {
   boolean onBonemealUsed(ItemStack var1, @Nullable Player var2, World var3, int var4, int var5, int var6, Side var7, double var8, double var10);
}
