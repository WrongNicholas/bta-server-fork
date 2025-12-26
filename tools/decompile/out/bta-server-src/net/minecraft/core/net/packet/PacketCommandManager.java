package net.minecraft.core.net.packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.handler.PacketHandler;
import org.jetbrains.annotations.NotNull;

public class PacketCommandManager extends Packet {
   private CommandDispatcher<CommandSource> dispatcher;
   private CommandSource source;
   private String text;
   private int cursor;
   public JsonObject suggestions;

   public PacketCommandManager(CommandDispatcher<CommandSource> dispatcher, CommandSource source, String text, int cursor) {
      this.dispatcher = dispatcher;
      this.source = source;
      this.text = text;
      this.cursor = cursor;
   }

   public PacketCommandManager() {
   }

   @Override
   public void read(DataInputStream dis) throws IOException {
      String str = dis.readUTF();
      this.suggestions = JsonParser.parseString(str).getAsJsonObject();
   }

   @Override
   public void write(DataOutputStream dos) throws IOException {
      String str = getDispatcherSuggestions(this.dispatcher, this.source, this.text, this.cursor).toString();
      dos.writeUTF(str);
   }

   private static JsonObject getDispatcherSuggestions(CommandDispatcher<CommandSource> dispatcher, CommandSource source, String text, int cursor) {
      JsonObject object = new JsonObject();
      JsonArray suggestions = new JsonArray();
      JsonArray exceptions = new JsonArray();
      JsonArray usage = new JsonArray();
      StringReader reader = new StringReader(text);
      if (reader.canRead() && reader.peek() == '/') {
         reader.skip();
      }

      ParseResults<CommandSource> parseResults = dispatcher.parse(reader, source);
      JsonObject readerJson = new JsonObject();
      readerJson.addProperty("a", parseResults.getReader().canRead());
      int readerCursor = Math.max(parseResults.getReader().getCursor(), 0);
      readerJson.addProperty("b", readerCursor);
      int remainingTextLength = Math.min(readerCursor + parseResults.getReader().getRemainingLength(), text.length());
      readerJson.addProperty("c", remainingTextLength);
      readerJson.addProperty("d", parseResults.getReader().getString());
      object.add("e", readerJson);
      object.addProperty("p", parseResults.getContext().findSuggestionContext(cursor).startPos);
      CompletableFuture<Suggestions> pendingSuggestions = getCompletionSuggestions(parseResults, cursor, source);
      pendingSuggestions.thenRun(() -> {
         if (pendingSuggestions.isDone()) {
            for (Suggestion suggestion : pendingSuggestions.join().getList()) {
               JsonObject suggestionJson = new JsonObject();
               suggestionJson.addProperty("f", suggestion.getText());
               JsonObject range = new JsonObject();
               range.addProperty("h", suggestion.getRange().getStart());
               range.addProperty("i", suggestion.getRange().getEnd());
               suggestionJson.add("g", range);
               if (suggestion.getTooltip() != null) {
                  suggestionJson.addProperty("j", suggestion.getTooltip().getString());
               }

               suggestions.add(suggestionJson);
            }
         }
      });
      CommandSyntaxException parseException = CommandManager.getParseException(parseResults);
      if (!parseResults.getExceptions().isEmpty()) {
         for (CommandSyntaxException entry : parseResults.getExceptions().values()) {
            JsonObject exceptionJson = new JsonObject();
            exceptionJson.addProperty("f", entry.getMessage());
            exceptions.add(exceptionJson);
         }
      } else if (parseException != null) {
         JsonObject exceptionJson = new JsonObject();
         exceptionJson.addProperty("f", parseException.getMessage());
         exceptions.add(exceptionJson);
      } else if (parseResults.getContext().getRootNode() != null && parseResults.getContext().getRange().getStart() <= cursor) {
         JsonObject commandUsage = new JsonObject();

         for (Entry<CommandNode<CommandSource>, String> entry : dispatcher.getSmartUsage(parseResults.getContext().findSuggestionContext(cursor).parent, source)
            .entrySet()) {
            if (!(entry.getKey() instanceof LiteralCommandNode)) {
               commandUsage.addProperty("f", entry.getValue());
               usage.add(commandUsage);
            }
         }
      }

      JsonObject lastChild = getLastChild(parseResults);
      object.add("k", lastChild);
      object.add("m", suggestions);
      object.add("n", exceptions);
      object.add("o", usage);
      return object;
   }

   private static CompletableFuture<Suggestions> getCompletionSuggestions(ParseResults<CommandSource> parse, int cursor, CommandSource source) {
      CommandContextBuilder<CommandSource> context = parse.getContext();
      SuggestionContext<CommandSource> nodeBeforeCursor = context.findSuggestionContext(cursor);
      CommandNode<CommandSource> parent = nodeBeforeCursor.parent;
      if (parent == null) {
         return Suggestions.empty();
      } else {
         int start = Math.min(nodeBeforeCursor.startPos, cursor);
         String fullInput = parse.getReader().getString();
         String truncatedInput = fullInput.substring(0, cursor);
         String truncatedInputLowerCase = truncatedInput.toLowerCase(Locale.ROOT);
         List<CompletableFuture<Suggestions>> futuresList = new ArrayList<>();
         int i = 0;

         for (CommandNode<CommandSource> node : parent.getChildren()) {
            if (node.canUse(source)) {
               CompletableFuture<Suggestions> future = Suggestions.empty();

               try {
                  future = node.listSuggestions(context.build(truncatedInput), new SuggestionsBuilder(truncatedInput, truncatedInputLowerCase, start));
               } catch (CommandSyntaxException var16) {
               }

               futuresList.add(future);
               i++;
            }
         }

         CompletableFuture<Suggestions>[] futures = new CompletableFuture[i];

         for (int j = 0; j < futures.length; j++) {
            futures[j] = futuresList.get(j);
         }

         CompletableFuture<Suggestions> result = new CompletableFuture<>();
         CompletableFuture.allOf(futures).thenRun(() -> {
            List<Suggestions> suggestions = new ArrayList<>();

            for (CompletableFuture<Suggestions> futurex : futuresList) {
               suggestions.add(futurex.join());
            }

            result.complete(Suggestions.merge(fullInput, suggestions));
         });
         return result;
      }
   }

   @NotNull
   private static JsonObject getLastChild(ParseResults<CommandSource> parseResults) {
      JsonObject lastChild = new JsonObject();
      CommandContextBuilder<CommandSource> builder = parseResults.getContext().getLastChild();
      JsonArray arguments = new JsonArray();

      for (ParsedArgument<CommandSource, ?> parsedArgument : builder.getArguments().values()) {
         JsonObject argument = new JsonObject();
         JsonObject range = new JsonObject();
         range.addProperty("h", parsedArgument.getRange().getStart());
         range.addProperty("i", parsedArgument.getRange().getEnd());
         argument.add("g", range);
         arguments.add(argument);
      }

      lastChild.add("l", arguments);
      return lastChild;
   }

   @Override
   public void handlePacket(PacketHandler packetHandler) {
      packetHandler.handleCommandManagerPacket(this);
   }

   @Override
   public int getEstimatedSize() {
      return 1;
   }
}
