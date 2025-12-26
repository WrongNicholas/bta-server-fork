package net.minecraft.core.net.command.helpers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.nbt.tags.CompoundTag;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.core.net.command.exceptions.CommandExceptions;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.net.command.util.NbtHelper;

public abstract class ArgumentParser {
   protected StringReader reader;
   protected int startPosition = 0;
   protected BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> suggestions = CommandHelper.NO_SUGGESTIONS;

   protected ArgumentParser(StringReader reader) {
      this.reader = reader;
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      return this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()), consumer);
   }

   protected int parseMetadata() throws CommandSyntaxException {
      this.suggestions = CommandHelper.NO_SUGGESTIONS;
      this.reader.skip();
      this.reader.skipWhitespace();
      int cursor = this.reader.getCursor();
      int metadata = this.reader.readInt();
      if (metadata < 0) {
         this.reader.setCursor(cursor);
         throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow().createWithContext(this.reader, metadata, 0);
      } else {
         this.reader.skipWhitespace();
         this.suggestions = this::suggestCloseMetadata;
         if (this.reader.canRead() && this.reader.peek() == ']') {
            this.suggestions = this::suggestOpenTag;
            this.reader.skip();
            return metadata;
         } else {
            throw CommandExceptions.expectedEndOfMetadata().createWithContext(this.reader);
         }
      }
   }

   protected CompoundTag parseCompound() throws CommandSyntaxException {
      return NbtHelper.parseNbt(this.reader);
   }

   protected CompletableFuture<Suggestions> suggestOpenMetadataOrTag(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      if (!this.reader.canRead()) {
         suggestionsBuilder.suggest(String.valueOf('['));
         suggestionsBuilder.suggest(String.valueOf('{'));
      }

      return suggestionsBuilder.buildFuture();
   }

   protected CompletableFuture<Suggestions> suggestOpenTag(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      if (!this.reader.canRead()) {
         suggestionsBuilder.suggest(String.valueOf('{'));
      }

      return suggestionsBuilder.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestCloseMetadata(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      if (!this.reader.canRead()) {
         suggestionsBuilder.suggest(String.valueOf(']'));
      }

      return suggestionsBuilder.buildFuture();
   }
}
