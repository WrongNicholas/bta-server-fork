package net.minecraft.core.net.command.helpers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.util.CommandHelper;

public class WorldFeatureParameterTypes {
   private static final Map<Class<?>, WorldFeatureParameterTypes.ReadFunction> FEATURE_MAP = new HashMap<>();

   public static void register(Class<?> clazz, WorldFeatureParameterTypes.ReadFunction function) {
      FEATURE_MAP.put(clazz, function);
   }

   public static Map<Class<?>, WorldFeatureParameterTypes.ReadFunction> getFeatures() {
      return new HashMap<>(FEATURE_MAP);
   }

   public static Object get(Class<?> clazz, StringReader stringReader, WorldFeatureParser parser) throws CommandSyntaxException {
      if (FEATURE_MAP.containsKey(clazz)) {
         return FEATURE_MAP.get(clazz).handle(stringReader, parser);
      } else {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(stringReader);
      }
   }

   private static Block<?> parseBlock(StringReader reader, WorldFeatureParser parser) throws CommandSyntaxException {
      parser.setSuggestions(CommandHelper.SUGGEST_BLOCKS);
      int cursor = reader.getCursor();
      String string = reader.readString();

      for (Block<?> blockInList : Blocks.blocksList) {
         if (blockInList != null && CommandHelper.matchesKeyString(blockInList.getKey(), string)) {
            return blockInList;
         }
      }

      reader.setCursor(cursor);
      throw new CommandSyntaxException(
         CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
         () -> I18n.getInstance().translateKey("command.argument_types.block.invalid_block")
      );
   }

   static {
      FEATURE_MAP.put(int.class, (reader, parser) -> reader.readInt());
      FEATURE_MAP.put(Block.class, WorldFeatureParameterTypes::parseBlock);
      FEATURE_MAP.put(String.class, (reader, parser) -> reader.readString());
   }

   public interface ReadFunction {
      Object handle(StringReader var1, WorldFeatureParser var2) throws CommandSyntaxException;
   }
}
