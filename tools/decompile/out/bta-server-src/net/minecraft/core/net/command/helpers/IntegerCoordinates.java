package net.minecraft.core.net.command.helpers;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import org.jetbrains.annotations.Nullable;

public class IntegerCoordinates {
   private final IntegerCoordinate x;
   private final IntegerCoordinate y;
   private final IntegerCoordinate z;

   public IntegerCoordinates(IntegerCoordinate x, IntegerCoordinate y, IntegerCoordinate z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public int getX(@Nullable Integer sourceX) throws CommandSyntaxException {
      return this.x.get(sourceX);
   }

   public int getY(@Nullable Integer sourceY) throws CommandSyntaxException {
      return this.y.get(sourceY);
   }

   public int getZ(@Nullable Integer sourceZ) throws CommandSyntaxException {
      return this.z.get(sourceZ);
   }

   public int getX(@Nullable Double sourceX) throws CommandSyntaxException {
      return sourceX == null ? this.x.get(null) : this.x.get((int)Math.floor(sourceX));
   }

   public int getY(@Nullable Double sourceY) throws CommandSyntaxException {
      return sourceY == null ? this.y.get(null) : this.y.get((int)Math.floor(sourceY));
   }

   public int getZ(@Nullable Double sourceZ) throws CommandSyntaxException {
      return sourceZ == null ? this.z.get(null) : this.z.get((int)Math.floor(sourceZ));
   }

   public int getX(CommandSource source) throws CommandSyntaxException {
      if (source.getCoordinates(true) == null) {
         if (!this.x.isRelative()) {
            return this.x.get(0);
         } else {
            throw CommandExceptions.notInWorld().create();
         }
      } else {
         return this.x.get((int)Math.floor(source.getCoordinates(true).x));
      }
   }

   public int getY(CommandSource source, boolean offsetHeight) throws CommandSyntaxException {
      if (source.getCoordinates(offsetHeight) == null) {
         if (!this.y.isRelative()) {
            return this.y.get(0);
         } else {
            throw CommandExceptions.notInWorld().create();
         }
      } else {
         return this.y.get((int)Math.floor(source.getCoordinates(offsetHeight).y));
      }
   }

   public int getZ(CommandSource source) throws CommandSyntaxException {
      if (source.getCoordinates(true) == null) {
         if (!this.z.isRelative()) {
            return this.z.get(0);
         } else {
            throw CommandExceptions.notInWorld().create();
         }
      } else {
         return this.z.get((int)Math.floor(source.getCoordinates(true).z));
      }
   }

   public boolean hasRelativeCoordinate() {
      return this.x.isRelative() || this.y.isRelative() || this.z.isRelative();
   }
}
