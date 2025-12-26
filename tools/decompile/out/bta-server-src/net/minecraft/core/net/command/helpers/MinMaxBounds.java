package net.minecraft.core.net.command.helpers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.lang.I18n;
import org.jetbrains.annotations.Nullable;

public abstract class MinMaxBounds<T extends Number> {
   private static final SimpleCommandExceptionType EMPTY_VALUE = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.range.empty")
   );
   private static final SimpleCommandExceptionType SWAPPED_VALUES = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.range.swapped")
   );
   @Nullable
   protected final T min;
   @Nullable
   protected final T max;

   public MinMaxBounds(@Nullable T min, @Nullable T max) {
      this.min = min;
      this.max = max;
   }

   public T getMax() {
      return this.max;
   }

   public T getMin() {
      return this.min;
   }

   public boolean isAny() {
      return this.max == null && this.min == null;
   }

   public static <T extends Number, R extends MinMaxBounds<T>> R fromReader(
      StringReader reader,
      Function<String, T> parser,
      Supplier<SimpleCommandExceptionType> supplier,
      MinMaxBounds.BoundsFromReaderFactory<T, R> boundsFromReaderFactory
   ) throws CommandSyntaxException {
      if (!reader.canRead()) {
         throw EMPTY_VALUE.createWithContext(reader);
      } else {
         int cursor = reader.getCursor();

         try {
            T number1 = readNumber(reader, parser, supplier);
            T number2;
            if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
               reader.skip();
               reader.skip();
               number2 = readNumber(reader, parser, supplier);
            } else {
               number2 = number1;
            }

            if (number1 == null && number2 == null) {
               throw EMPTY_VALUE.createWithContext(reader);
            } else {
               return boundsFromReaderFactory.create(reader, number1, number2);
            }
         } catch (CommandSyntaxException var7) {
            reader.setCursor(cursor);
            throw EMPTY_VALUE.createWithContext(reader);
         }
      }
   }

   private static <T extends Number> T readNumber(StringReader reader, Function<String, T> parser, Supplier<SimpleCommandExceptionType> supplier) throws CommandSyntaxException {
      int cursor = reader.getCursor();

      while (reader.canRead() && isAllowedChatCharacter(reader)) {
         reader.skip();
      }

      String numberString = reader.getString().substring(cursor, reader.getCursor());
      if (numberString.isEmpty()) {
         return null;
      } else {
         try {
            return parser.apply(numberString);
         } catch (NumberFormatException var6) {
            throw supplier.get().createWithContext(reader);
         }
      }
   }

   private static boolean isAllowedChatCharacter(StringReader reader) {
      char c = reader.peek();
      if ((c < '0' || c > '9') && c != '-') {
         return c != '.' ? false : !reader.canRead(2) || reader.peek(1) != '.';
      } else {
         return true;
      }
   }

   @FunctionalInterface
   protected interface BoundsFromReaderFactory<T extends Number, R extends MinMaxBounds<T>> {
      R create(StringReader var1, T var2, T var3) throws CommandSyntaxException;
   }

   public static class Doubles extends MinMaxBounds<Double> {
      public static MinMaxBounds.Doubles ANY = new MinMaxBounds.Doubles(null, null);

      public Doubles(@Nullable Double min, @Nullable Double max) {
         super(min, max);
      }

      private static MinMaxBounds.Doubles create(StringReader stringReader, @Nullable Double double1, @Nullable Double double2) throws CommandSyntaxException {
         if (double1 != null && double2 != null && double1 > double2) {
            throw MinMaxBounds.SWAPPED_VALUES.createWithContext(stringReader);
         } else {
            return new MinMaxBounds.Doubles(double1, double2);
         }
      }

      public boolean contains(double number) {
         return this.min != null && this.min > number ? false : this.max == null || this.max >= number;
      }

      public static MinMaxBounds.Doubles fromReader(StringReader reader) throws CommandSyntaxException {
         return MinMaxBounds.fromReader(
            reader, Double::parseDouble, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerExpectedInt, MinMaxBounds.Doubles::create
         );
      }
   }

   public static class Integers extends MinMaxBounds<Integer> {
      public static MinMaxBounds.Integers ANY = new MinMaxBounds.Integers(null, null);

      public Integers(@Nullable Integer min, @Nullable Integer max) {
         super(min, max);
      }

      private static MinMaxBounds.Integers create(StringReader stringReader, @Nullable Integer integer1, @Nullable Integer integer2) throws CommandSyntaxException {
         if (integer1 != null && integer2 != null && integer1 > integer2) {
            throw MinMaxBounds.SWAPPED_VALUES.createWithContext(stringReader);
         } else {
            return new MinMaxBounds.Integers(integer1, integer2);
         }
      }

      public boolean contains(int number) {
         return this.min != null && this.min > number ? false : this.max == null || this.max >= number;
      }

      public static MinMaxBounds.Integers fromReader(StringReader reader) throws CommandSyntaxException {
         return MinMaxBounds.fromReader(reader, Integer::parseInt, CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerExpectedInt, MinMaxBounds.Integers::create);
      }
   }
}
