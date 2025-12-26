package net.minecraft.core.net.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.helpers.EntitySelectorParser;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.net.command.util.ConcatIterable;

public class ArgumentTypeEntity implements ArgumentType<EntitySelector> {
   private static final Collection<String> EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "entity.11203");
   private final boolean singleEntity;
   private final boolean playerOnly;
   private final boolean showNicknames;

   private ArgumentTypeEntity(boolean singleEntity, boolean playerOnly, boolean showNicknames) {
      this.singleEntity = singleEntity;
      this.playerOnly = playerOnly;
      this.showNicknames = showNicknames;
   }

   public static ArgumentTypeEntity entities() {
      return new ArgumentTypeEntity(false, false, false);
   }

   public static ArgumentTypeEntity entity() {
      return new ArgumentTypeEntity(true, false, false);
   }

   public static ArgumentTypeEntity usernames() {
      return new ArgumentTypeEntity(false, true, false);
   }

   public static ArgumentTypeEntity username() {
      return new ArgumentTypeEntity(true, true, false);
   }

   public static ArgumentTypeEntity nicknames() {
      return new ArgumentTypeEntity(false, true, true);
   }

   public static ArgumentTypeEntity nickname() {
      return new ArgumentTypeEntity(true, true, true);
   }

   public EntitySelector parse(StringReader reader) throws CommandSyntaxException {
      int cursor = reader.getCursor();
      EntitySelectorParser entitySelectorParser = new EntitySelectorParser(reader);
      EntitySelector entitySelector = entitySelectorParser.parse();
      if (this.singleEntity && entitySelector.getMaxResults() > 1) {
         reader.setCursor(cursor);
         if (this.playerOnly) {
            throw CommandExceptions.singlePlayerOnly().createWithContext(reader);
         } else {
            throw CommandExceptions.singleEntityOnly().createWithContext(reader);
         }
      } else if (this.playerOnly && entitySelector.includesEntities() && !entitySelector.isCurrentEntity()) {
         reader.setCursor(cursor);
         throw CommandExceptions.playerOnly().createWithContext(reader);
      } else {
         return entitySelector;
      }
   }

   @Override
   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      S s = context.getSource();
      if (s instanceof CommandSource) {
         CommandSource source = (CommandSource)s;
         StringReader stringReader = new StringReader(builder.getInput());
         stringReader.setCursor(builder.getStart());
         EntitySelectorParser entitySelectorParser = new EntitySelectorParser(stringReader, source.hasAdmin());

         try {
            entitySelectorParser.parse();
         } catch (CommandSyntaxException var8) {
         }

         return entitySelectorParser.fillSuggestions(builder, suggestionsBuilder -> {
            Collection<String> collection;
            if (this.showNicknames) {
               collection = source.getPlayerNicknames();
            } else {
               collection = source.getPlayerUsernames();
            }

            Iterable<String> iterable = (Iterable<String>)(this.playerOnly ? collection : new ConcatIterable<>(collection, source.getEntitySuggestions()));
            CommandHelper.suggest(iterable, suggestionsBuilder);
         });
      } else {
         return Suggestions.empty();
      }
   }

   @Override
   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
