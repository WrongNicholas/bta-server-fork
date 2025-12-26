package net.minecraft.core.net.command.helpers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.nbt.tags.CompoundTag;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.util.CommandHelper;

public class ItemStackArgumentParser extends ArgumentParser {
   private static final SimpleCommandExceptionType INVALID_ITEM = new SimpleCommandExceptionType(
      () -> I18n.getInstance().translateKey("command.argument_types.item.invalid_item")
   );
   private Item item;
   private int metadata = 0;
   private CompoundTag tag = new CompoundTag();

   public ItemStackArgumentParser(StringReader reader) {
      super(reader);
   }

   private CompletableFuture<Suggestions> suggestItems(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      SuggestionsBuilder suggestionsBuilder2 = suggestionsBuilder.createOffset(this.startPosition);
      consumer.accept(suggestionsBuilder2);
      return suggestionsBuilder.add(suggestionsBuilder2).buildFuture();
   }

   public ItemStack parse() throws CommandSyntaxException {
      this.startPosition = this.reader.getCursor();
      this.suggestions = this::suggestItems;
      this.parseItem();
      if (this.item == null) {
         throw INVALID_ITEM.createWithContext(this.reader);
      } else {
         this.suggestions = this::suggestOpenMetadataOrTag;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.metadata = this.parseMetadata();
         }

         if (this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = CommandHelper.NO_SUGGESTIONS;
            this.tag = this.parseCompound();
         }

         if (this.item != null) {
            return new ItemStack(this.item, 1, this.metadata, this.tag);
         } else {
            throw INVALID_ITEM.createWithContext(this.reader);
         }
      }
   }

   private void parseItem() throws CommandSyntaxException {
      StringBuilder builder = new StringBuilder();

      while (this.reader.canRead()) {
         char peak = this.reader.peek();
         if (peak == '[' || peak == '{' || peak == ' ') {
            break;
         }

         builder.append(this.reader.read());
      }

      String string = builder.toString();

      for (Item itemInList : Item.itemsList) {
         if (itemInList != null && CommandHelper.matchesNamespaceId(itemInList.namespaceID, string)) {
            this.item = itemInList;
            break;
         }
      }
   }

   @Override
   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder suggestionsBuilder, Consumer<SuggestionsBuilder> consumer) {
      return this.suggestions.apply(suggestionsBuilder.createOffset(this.reader.getCursor()), consumer);
   }
}
