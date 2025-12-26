package net.minecraft.core.net.command.helpers;

import com.b100.utils.Utils;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class WorldFeatureParser {
   private final SimpleCommandExceptionType NO_PARAMETERS = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.world_feature.no_parameters")
   );
   private final StringReader reader;
   private int startPosition = 0;
   private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = CommandHelper.NO_SUGGESTIONS;

   public WorldFeatureParser(StringReader reader) {
      this.reader = reader;
   }

   private CompletableFuture<Suggestions> suggestWorldFeatures(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.startPosition);
      consumer.accept(suggestionsBuilder2);
      return suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
   }

   public WorldFeature parse() throws CommandSyntaxException {
      this.startPosition = this.reader.getCursor();
      this.suggestions = this::suggestWorldFeatures;
      String string = this.reader.readString();
      Class<? extends WorldFeature> worldFeatureClass = null;

      for (Entry<String, Class<? extends WorldFeature>> entry : CommandHelper.WORLD_FEATURES.entrySet()) {
         if (CommandHelper.matchesKeyString(entry.getKey(), string)) {
            worldFeatureClass = entry.getValue();
         }
      }

      if (worldFeatureClass == null) {
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(this.reader);
      } else {
         boolean hasParameters = false;
         Constructor<?> constructor = null;

         for (Constructor<?> c : worldFeatureClass.getConstructors()) {
            constructor = c;
            hasParameters = c.getParameters().length != 0;
         }

         if (hasParameters || constructor == null) {
            return this.parseParameters(worldFeatureClass);
         } else if (this.reader.canRead() && this.reader.peek() == '[') {
            throw this.NO_PARAMETERS.createWithContext(this.reader);
         } else {
            try {
               return (WorldFeature)constructor.newInstance();
            } catch (Exception var9) {
               throw new CommandSyntaxException(
                  CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
                  () -> I18n.getInstance().translateKey("command.argument_types.world_feature.invalid_world_feature")
               );
            }
         }
      }
   }

   private WorldFeature parseParameters(Class<? extends WorldFeature> worldFeatureClass) throws CommandSyntaxException {
      this.suggestions = this::suggestOpenParameters;
      if (this.reader.canRead()) {
         this.suggestions = CommandHelper.NO_SUGGESTIONS;
      }

      if (this.reader.canRead() && this.reader.peek() == '[') {
         List<Object> parameters = new ArrayList<>();
         this.reader.skip();
         Constructor<?> c = getConstructorWithMostParameters(worldFeatureClass.getConstructors());
         if (c == null) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(this.reader);
         } else {
            for (Parameter parameter : c.getParameters()) {
               this.suggestions = CommandHelper.NO_SUGGESTIONS;
               this.reader.skipWhitespace();
               Object parsedParameter = this.parseParameter(parameter);
               parameters.add(parsedParameter);
               boolean lastParameter = parameters.size() == c.getParameters().length;
               this.reader.skipWhitespace();
               if (!this.reader.canRead()) {
                  this.suggestions = lastParameter ? this::suggestParametersClose : this::suggestParametersNext;
                  throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(this.reader);
               }

               if ((!lastParameter || this.reader.peek() != ']') && (lastParameter || this.reader.peek() != ',')) {
                  throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(this.reader);
               }

               this.reader.skip();
            }

            Constructor<?> constructor = c;
            this.suggestions = CommandHelper.NO_SUGGESTIONS;

            try {
               return (WorldFeature)constructor.newInstance(Utils.toArray(Object.class, parameters));
            } catch (Exception var11) {
               throw new CommandSyntaxException(
                  CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
                  () -> I18n.getInstance().translateKey("command.argument_types.world_feature.invalid_world_feature")
               );
            }
         }
      } else {
         throw new CommandSyntaxException(
            CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument(),
            () -> I18n.getInstance().translateKey("command.argument_types.world_feature.invalid_world_feature")
         );
      }
   }

   private Object parseParameter(Parameter parameter) throws CommandSyntaxException {
      this.suggestions = CommandHelper.NO_SUGGESTIONS;
      return WorldFeatureParameterTypes.get(parameter.getType(), this.reader, this);
   }

   private CompletableFuture<Suggestions> suggestOpenParameters(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      suggestionsBuilder.suggest(String.valueOf('['));
      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestParametersNext(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      suggestionsBuilder.suggest(String.valueOf(','));
      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestParametersClose(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      suggestionsBuilder.suggest(String.valueOf(']'));
      return suggestionsBuilder.buildFuture();
   }

   public void setSuggestions(BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> biFunction) {
      this.suggestions = biFunction;
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      return this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()), consumer);
   }

   private static Constructor<?> getConstructorWithMostParameters(Constructor<?>[] constructors) {
      if (constructors.length == 0) {
         return null;
      } else {
         Constructor<?> constructor = constructors[0];

         for (Constructor<?> c : constructors) {
            if (c.getParameters().length > constructor.getParameters().length) {
               constructor = c;
            }
         }

         return constructor;
      }
   }
}
