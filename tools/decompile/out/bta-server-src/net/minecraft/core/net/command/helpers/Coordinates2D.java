package net.minecraft.core.net.command.helpers;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Coordinates2D {
   private final IntegerCoordinate x;
   private final IntegerCoordinate z;

   public Coordinates2D(IntegerCoordinate x, IntegerCoordinate z) {
      this.x = x;
      this.z = z;
   }

   public Coordinates2D(int x, int z) {
      this(new IntegerCoordinate(false, x), new IntegerCoordinate(false, z));
   }

   public int getX(@Nullable Integer sourceX) throws CommandSyntaxException {
      return this.x.get(sourceX == null ? null : MathHelper.floor(sourceX.intValue() / 16.0));
   }

   public int getZ(@Nullable Integer sourceZ) throws CommandSyntaxException {
      return this.z.get(sourceZ == null ? null : MathHelper.floor(sourceZ.intValue() / 16.0));
   }

   public int getX(@Nullable Double sourceX) throws CommandSyntaxException {
      return this.x.get(sourceX == null ? null : MathHelper.floor(sourceX / 16.0));
   }

   public int getZ(@Nullable Double sourceZ) throws CommandSyntaxException {
      return this.z.get(sourceZ == null ? null : MathHelper.floor(sourceZ / 16.0));
   }

   public int getX(CommandSource source) throws CommandSyntaxException {
      Vec3 sourceCoordinates = source.getCoordinates(true);
      if (sourceCoordinates == null) {
         if (!this.x.isRelative()) {
            return this.x.get(0);
         } else {
            throw CommandExceptions.notInWorld().create();
         }
      } else {
         return this.x.get(MathHelper.floor(sourceCoordinates.x / 16.0));
      }
   }

   public int getZ(CommandSource source) throws CommandSyntaxException {
      Vec3 sourceCoordinates = source.getCoordinates(true);
      if (sourceCoordinates == null) {
         if (!this.z.isRelative()) {
            return this.z.get(0);
         } else {
            throw CommandExceptions.notInWorld().create();
         }
      } else {
         return this.z.get(MathHelper.floor(sourceCoordinates.z / 16.0));
      }
   }

   public boolean hasRelativeCoordinate() {
      return this.x.isRelative() || this.z.isRelative();
   }
}
