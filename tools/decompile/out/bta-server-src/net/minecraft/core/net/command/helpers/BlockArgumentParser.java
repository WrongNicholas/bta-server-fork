package net.minecraft.core.net.command.helpers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.nbt.tags.CompoundTag;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.util.collection.NamespaceID;

public class BlockArgumentParser extends ArgumentParser {
   private static final SimpleCommandExceptionType INVALID_BLOCK = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.block.invalid_block")
   );
   public static final NamespaceID AIR_BLOCK = NamespaceID.getPermanent("minecraft", "block/air");
   private Block<?> block;
   private int metadata = 0;
   private CompoundTag tag = new CompoundTag();

   public BlockArgumentParser(StringReader reader) {
      super(reader);
   }

   private CompletableFuture<Suggestions> suggestBlocks(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.startPosition);
      consumer.accept(suggestionsBuilder2);
      return suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
   }

   public BlockInput parse() throws CommandSyntaxException {
      this.startPosition = this.reader.getCursor();
      this.suggestions = this::suggestBlocks;
      this.parseBlock();
      this.suggestions = this::suggestOpenMetadataOrTag;
      if (this.reader.canRead() && this.reader.peek() == '[') {
         this.metadata = this.parseMetadata();
      }

      if (this.reader.canRead() && this.reader.peek() == '{') {
         this.suggestions = CommandHelper.NO_SUGGESTIONS;
         this.tag = this.parseCompound();
      }

      return new BlockInput(this.block, this.metadata, this.tag);
   }

   private void parseBlock() throws CommandSyntaxException {
      StringBuilder builder = new StringBuilder();

      while (this.reader.canRead()) {
         char peak = this.reader.peek();
         if (peak == '[' || peak == '{' || peak == ' ') {
            break;
         }

         builder.append(this.reader.read());
      }

      String string = builder.toString();
      boolean isAir = false;
      if (CommandHelper.matchesNamespaceId(AIR_BLOCK, string)) {
         this.block = null;
         isAir = true;
      } else {
         for (Block<?> blockInList : Blocks.blocksList) {
            if (blockInList != null && CommandHelper.matchesNamespaceId(blockInList.namespaceId(), string)) {
               this.block = blockInList;
               break;
            }
         }
      }

      if (this.block == null && !isAir) {
         throw INVALID_BLOCK.createWithContext(this.reader);
      }
   }

   @Override
   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      return this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()), consumer);
   }
}
