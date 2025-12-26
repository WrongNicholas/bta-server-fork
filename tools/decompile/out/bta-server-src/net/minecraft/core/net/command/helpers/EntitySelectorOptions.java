package net.minecraft.core.net.command.helpers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.text.ChainText;
import net.minecraft.core.lang.text.Text;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.net.command.util.NbtHelper;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.LoggedPrintStream;

public class EntitySelectorOptions {
   private static final DynamicCommandExceptionType INAPPLICABLE_OPTION = new DynamicCommandExceptionType(
      value -> () -> I18n.getInstance().translateKeyAndFormat("command.argument_types.entity.selector.options.inapplicable", value)
   );
   private static final DynamicCommandExceptionType UNKNOWN_OPTION = new DynamicCommandExceptionType(
      value -> () -> I18n.getInstance().translateKeyAndFormat("command.argument_types.entity.selector.options.unknown", value)
   );
   private static final DynamicCommandExceptionType UNKNOWN_GAME_MODE = new DynamicCommandExceptionType(
      value -> () -> I18n.getInstance().translateKeyAndFormat("command.argument_types.entity.selector.options.gamemode.invalid", value)
   );
   private static final DynamicCommandExceptionType UNKNOWN_SORT = new DynamicCommandExceptionType(
      value -> () -> I18n.getInstance().translateKeyAndFormat("command.argument_types.entity.selector.options.sort.invalid", value)
   );
   private static final DynamicCommandExceptionType UNKNOWN_ENTITY_TYPE = new DynamicCommandExceptionType(
      value -> () -> I18n.getInstance().translateKeyAndFormat("command.argument_types.entity.selector.options.type.invalid", value)
   );
   private static final SimpleCommandExceptionType NEGATIVE_DISTANCE = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.entity.selector.options.distance.invalid")
   );
   private static final SimpleCommandExceptionType LIMIT_TOO_SMALL = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.entity.selector.options.limit.invalid")
   );
   private final StringReader reader;
   private final String key;
   private static final Map<String, EntitySelectorOptions.Option> OPTIONS = new HashMap<>();

   public EntitySelectorOptions(StringReader reader, String key) {
      this.reader = reader;
      this.key = key;
   }

   public static void register(String key, EntitySelectorOptions.Modifier modifier, Predicate<EntitySelectorParser> canUse, Text description) {
      OPTIONS.put(key, new EntitySelectorOptions.Option(modifier, canUse, description));
   }

   public static EntitySelectorOptions.Modifier get(EntitySelectorParser entitySelectorParser, String string, int i) throws CommandSyntaxException {
      EntitySelectorOptions.Option option = OPTIONS.get(string);
      if (option != null) {
         if (option.canUse.test(entitySelectorParser)) {
            return option.modifier;
         } else {
            throw INAPPLICABLE_OPTION.createWithContext(entitySelectorParser.getReader(), string);
         }
      } else {
         entitySelectorParser.getReader().setCursor(i);
         throw UNKNOWN_OPTION.createWithContext(entitySelectorParser.getReader(), string);
      }
   }

   public static void suggestNames(EntitySelectorParser entitySelectorParser, SuggestionsBuilder suggestionsBuilder) {
      String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

      for (Entry<String, EntitySelectorOptions.Option> entry : OPTIONS.entrySet()) {
         if (entry.getValue().canUse.test(entitySelectorParser) && entry.getKey().toLowerCase(Locale.ROOT).startsWith(string)) {
            suggestionsBuilder.suggest(entry.getKey() + "=", () -> entry.getValue().description.toString());
         }
      }
   }

   static {
      register("x", parser -> {
         double x = parser.getReader().readDouble();
         parser.setX(x);
      }, entitySelectorParser -> entitySelectorParser.getX() == null, ChainText.text().trans("command.argument_types.entity.selector.options.x.description"));
      register("y", parser -> {
         double y = parser.getReader().readDouble();
         parser.setY(y);
      }, entitySelectorParser -> entitySelectorParser.getY() == null, ChainText.text().trans("command.argument_types.entity.selector.options.y.description"));
      register("z", parser -> {
         double z = parser.getReader().readDouble();
         parser.setZ(z);
      }, entitySelectorParser -> entitySelectorParser.getZ() == null, ChainText.text().trans("command.argument_types.entity.selector.options.z.description"));
      register(
         "dx",
         parser -> {
            double dx = parser.getReader().readDouble();
            parser.setDeltaX(dx);
         },
         entitySelectorParser -> entitySelectorParser.getDeltaX() == null,
         ChainText.text().trans("command.argument_types.entity.selector.options.dx.description")
      );
      register(
         "dy",
         parser -> {
            double dy = parser.getReader().readDouble();
            parser.setDeltaY(dy);
         },
         entitySelectorParser -> entitySelectorParser.getDeltaY() == null,
         ChainText.text().trans("command.argument_types.entity.selector.options.dy.description")
      );
      register(
         "dz",
         parser -> {
            double dz = parser.getReader().readDouble();
            parser.setDeltaZ(dz);
         },
         entitySelectorParser -> entitySelectorParser.getDeltaZ() == null,
         ChainText.text().trans("command.argument_types.entity.selector.options.dz.description")
      );
      register(
         "name",
         parser -> {
            int i = parser.getReader().getCursor();
            boolean bl = parser.shouldInvertValue();
            String string = parser.getReader().readString();
            if (parser.hasNameNotEquals() && !bl) {
               parser.getReader().setCursor(i);
               throw INAPPLICABLE_OPTION.createWithContext(parser.getReader(), "name");
            } else {
               if (bl) {
                  parser.setHasNameNotEquals(true);
               } else {
                  parser.setHasNameEquals(true);
               }

               parser.addPredicate(
                  entity -> {
                     if (entity instanceof Mob) {
                        return entity instanceof Player
                           ? ((Player)entity).username.equals(string) != bl
                           : LoggedPrintStream.removeColorCodes(((Mob)entity).getDisplayName()).equals(string) != bl;
                     } else {
                        return entity instanceof EntityItem && ((EntityItem)entity).item != null
                           ? LoggedPrintStream.removeColorCodes(((EntityItem)entity).item.getDisplayName()).equals(string)
                           : bl;
                     }
                  }
               );
            }
         },
         entitySelectorParser -> !entitySelectorParser.hasNameEquals(),
         ChainText.text().trans("command.argument_types.entity.selector.options.name.description")
      );
      register("distance", parser -> {
         int cursor = parser.getReader().getCursor();
         MinMaxBounds.Doubles bounds = MinMaxBounds.Doubles.fromReader(parser.getReader());
         if ((bounds.getMin() == null || !((Double)bounds.getMin() < 0.0)) && (bounds.getMax() == null || !((Double)bounds.getMax() < 0.0))) {
            parser.setDistance(bounds);
         } else {
            parser.getReader().setCursor(cursor);
            throw NEGATIVE_DISTANCE.createWithContext(parser.getReader());
         }
      }, parser -> parser.getDistance().isAny(), ChainText.text().trans("command.argument_types.entity.selector.options.distance.description"));
      register("type", parser -> {
         int cursor = parser.getReader().getCursor();
         boolean invert = parser.shouldInvertValue();
         parser.setSuggestions((builderx, consumer) -> {
            String string = builderx.getRemaining().toLowerCase(Locale.ROOT);
            if (!string.isEmpty() && string.charAt(0) == '!') {
               string = string.substring(1);
            }

            CommandHelper.suggest("!Player", builderx);
            CommandHelper.suggest("Player", builderx);

            for (NamespaceID key : EntityDispatcher.idToClassMap.keySet()) {
               if (key.toString().startsWith(string) || key.namespace().equals("minecraft") && key.value().startsWith(string)) {
                  CommandHelper.suggest("!" + key, builderx);
                  if (!invert) {
                     if (key.namespace().equals("minecraft")) {
                        CommandHelper.suggest(key.value(), builderx);
                     }

                     CommandHelper.suggest(key.toString(), builderx);
                  }
               }
            }

            return builderx.buildFuture();
         });
         if (parser.isTypeInverse() && !invert) {
            parser.getReader().setCursor(cursor);
            throw INAPPLICABLE_OPTION.createWithContext(parser.getReader(), "type");
         } else {
            if (invert) {
               parser.setTypeInverse(true);
            }

            StringBuilder builder = new StringBuilder();

            while (parser.getReader().canRead()) {
               char peak = parser.getReader().peek();
               if (peak != ':' && !StringReader.isAllowedInUnquotedString(peak)) {
                  break;
               }

               builder.append(parser.getReader().read());
            }

            String type = builder.toString();
            if (type.equals("Player")) {
               parser.setLimitToType(Player.class);
            } else if (EntityDispatcher.classForId(type) != null) {
               parser.setLimitToType(EntityDispatcher.classForId(type));
            } else {
               if (EntityDispatcher.classForId("minecraft:" + type) == null) {
                  parser.getReader().setCursor(cursor);
                  throw UNKNOWN_ENTITY_TYPE.createWithContext(parser.getReader(), type);
               }

               parser.setLimitToType(EntityDispatcher.classForId("minecraft:" + type));
            }
         }
      }, parser -> !parser.hasType(), ChainText.text().trans("command.argument_types.entity.selector.options.type.description"));
      register("limit", parser -> {
         int cursor = parser.getReader().getCursor();
         int limit = parser.getReader().readInt();
         if (limit < 1) {
            parser.getReader().setCursor(cursor);
            throw LIMIT_TOO_SMALL.createWithContext(parser.getReader());
         } else {
            parser.setMaxResults(limit);
            parser.setHasLimit(true);
         }
      }, parser -> !parser.isCurrentEntity() && !parser.hasLimit(), ChainText.text().trans("command.argument_types.entity.selector.options.limit.description"));
      register(
         "sort",
         parser -> {
            int i = parser.getReader().getCursor();
            String string = parser.getReader().readUnquotedString();
            parser.setSuggestions(
               (suggestionsBuilder, consumer) -> CommandHelper.suggest(Arrays.asList("nearest", "furthest", "random", "arbitrary"), suggestionsBuilder)
            );
            BiConsumer<Entity, List<? extends Entity>> sort;
            switch (string) {
               case "nearest":
                  sort = EntitySelectorParser.ORDER_NEAREST;
                  break;
               case "furthest":
                  sort = EntitySelectorParser.ORDER_FURTHEST;
                  break;
               case "random":
                  sort = EntitySelectorParser.ORDER_RANDOM;
                  break;
               case "arbitrary":
                  sort = EntitySelectorParser.ORDER_ARBITRARY;
                  break;
               default:
                  parser.getReader().setCursor(i);
                  throw UNKNOWN_SORT.createWithContext(parser.getReader(), string);
            }

            parser.setOrder(sort);
            parser.setSorted(true);
         },
         parser -> !parser.isSorted(),
         ChainText.text().trans("command.argument_types.entity.selector.options.sort.description")
      );
      register(
         "gamemode",
         parser -> {
            parser.setSuggestions((builder, consumer) -> {
               String string = builder.getRemaining().toLowerCase(Locale.ROOT);
               boolean bl = !parser.hasGamemodeNotEquals();
               boolean bl2 = true;
               if (!string.isEmpty()) {
                  if (string.charAt(0) == '!') {
                     bl = false;
                     string = string.substring(1);
                  } else {
                     bl2 = false;
                  }
               }

               for (Gamemode gameMode : Gamemode.gamemodesList) {
                  if (CommandHelper.getStringToSuggest(gameMode.getLanguageKey(), string).isPresent()) {
                     String stringToSuggest = CommandHelper.getStringToSuggest(gameMode.getLanguageKey(), string).get();
                     if (bl2) {
                        builder.suggest("!" + stringToSuggest);
                     }

                     if (bl) {
                        builder.suggest(stringToSuggest);
                     }
                  }
               }

               return builder.buildFuture();
            });
            int cursor = parser.getReader().getCursor();
            boolean invert = parser.shouldInvertValue();
            String value = parser.getReader().readUnquotedString();
            Gamemode gamemode = null;

            for (Gamemode iteratedGameMode : Gamemode.gamemodesList) {
               if (CommandHelper.matchesKeyString(iteratedGameMode.getLanguageKey(), value)) {
                  gamemode = iteratedGameMode;
               }
            }

            if (gamemode == null) {
               parser.getReader().setCursor(cursor);
               throw UNKNOWN_GAME_MODE.createWithContext(parser.getReader(), value);
            } else {
               parser.setIncludesEntities(false);
               parser.addPredicate(
                  entity -> !(entity instanceof Player) ? false : CommandHelper.matchesKeyString(((Player)entity).gamemode.getLanguageKey(), value) != invert
               );
               if (invert) {
                  parser.setHasGamemodeNotEquals(true);
               } else {
                  parser.setHasGamemodeEquals(true);
               }
            }
         },
         parser -> !parser.hasGamemodeEquals(),
         ChainText.text().trans("command.argument_types.entity.selector.options.gamemode.description")
      );
      register(
         "nbt",
         parser -> {
            boolean invert = parser.shouldInvertValue();
            CompoundTag nbt = NbtHelper.parseNbt(parser.getReader());
            parser.addPredicate(
               entity -> {
                  if (nbt.getValue().isEmpty()) {
                     return !invert;
                  } else {
                     CompoundTag entityNbt = new CompoundTag();
                     entity.addAdditionalSaveData(entityNbt);

                     for (Entry<String, Tag<?>> entry : nbt.getValue().entrySet()) {
                        if ((
                              !entityNbt.getValue().containsKey(entry.getKey())
                                 || entityNbt.getValue().get(entry.getKey()) != entry.getValue()
                                    && !entityNbt.getValue().get(entry.getKey()).equals(entry.getValue())
                                    && !entityNbt.getValue().get(entry.getKey()).getValue().equals(entry.getValue().getValue())
                           )
                           && !invert) {
                           return false;
                        }
                     }

                     return true;
                  }
               }
            );
            if (invert) {
               parser.setHasNotNbt(true);
            } else {
               parser.setHasNbt(true);
            }
         },
         parser -> !parser.hasNbt(),
         ChainText.text().trans("command.argument_types.entity.selector.options.nbt.description")
      );
   }

   @FunctionalInterface
   interface Modifier {
      void handle(EntitySelectorParser var1) throws CommandSyntaxException;
   }

   static class Option {
      final EntitySelectorOptions.Modifier modifier;
      final Predicate<EntitySelectorParser> canUse;
      final Text description;

      Option(EntitySelectorOptions.Modifier modifier, Predicate<EntitySelectorParser> canUse, Text description) {
         this.modifier = modifier;
         this.canUse = canUse;
         this.description = description;
      }
   }
}
