package net.minecraft.core.net.command.helpers;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;

public class DoubleCoordinates {
   private final DoubleCoordinate x;
   private final DoubleCoordinate y;
   private final DoubleCoordinate z;

   public DoubleCoordinates(DoubleCoordinate x, DoubleCoordinate y, DoubleCoordinate z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public double getX(double sourceX) throws CommandSyntaxException {
      return this.x.get(sourceX);
   }

   public double getY(double sourceY) throws CommandSyntaxException {
      return this.y.get(sourceY);
   }

   public double getZ(double sourceZ) throws CommandSyntaxException {
      return this.z.get(sourceZ);
   }

   public double getX(CommandSource source) throws CommandSyntaxException {
      if (source.getCoordinates(false) == null) {
         if (!this.x.isRelative()) {
            return this.x.get(0.0);
         } else {
            throw CommandExceptions.notInWorld().create();
         }
      } else {
         return this.x.get(source.getCoordinates(false).x);
      }
   }

   public double getY(CommandSource source, boolean offsetHeight) throws CommandSyntaxException {
      if (source.getCoordinates(offsetHeight) == null) {
         if (!this.y.isRelative()) {
            return this.y.get(0.0);
         } else {
            throw CommandExceptions.notInWorld().create();
         }
      } else {
         return this.y.get(source.getCoordinates(offsetHeight).y);
      }
   }

   public double getZ(CommandSource source) throws CommandSyntaxException {
      if (source.getCoordinates(false) == null) {
         if (!this.z.isRelative()) {
            return this.z.get(0.0);
         } else {
            throw CommandExceptions.notInWorld().create();
         }
      } else {
         return this.z.get(source.getCoordinates(false).z);
      }
   }

   public boolean hasRelativeCoordinates() {
      return this.x.isRelative() || this.y.isRelative() || this.z.isRelative();
   }
}
