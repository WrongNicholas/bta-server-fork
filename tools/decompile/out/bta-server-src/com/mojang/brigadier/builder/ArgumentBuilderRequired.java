package com.mojang.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;

public class ArgumentBuilderRequired<S, T> extends ArgumentBuilder<S, ArgumentBuilderRequired<S, T>> {
   private final String name;
   private final ArgumentType<T> type;
   private SuggestionProvider<S> suggestionsProvider = null;

   private ArgumentBuilderRequired(String name, ArgumentType<T> type) {
      this.name = name;
      this.type = type;
   }

   public static <S, T> ArgumentBuilderRequired<S, T> argument(String name, ArgumentType<T> type) {
      return new ArgumentBuilderRequired<>(name, type);
   }

   public ArgumentBuilderRequired<S, T> suggests(SuggestionProvider<S> provider) {
      this.suggestionsProvider = provider;
      return this.getThis();
   }

   public SuggestionProvider<S> getSuggestionsProvider() {
      return this.suggestionsProvider;
   }

   protected ArgumentBuilderRequired<S, T> getThis() {
      return this;
   }

   public ArgumentType<T> getType() {
      return this.type;
   }

   public String getName() {
      return this.name;
   }

   public ArgumentCommandNode<S, T> build() {
      ArgumentCommandNode<S, T> result = new ArgumentCommandNode<>(
         this.getName(),
         this.getType(),
         this.getCommand(),
         this.getRequirement(),
         this.getRedirect(),
         this.getRedirectModifier(),
         this.isFork(),
         this.getSuggestionsProvider()
      );

      for (CommandNode<S> argument : this.getArguments()) {
         result.addChild(argument);
      }

      return result;
   }
}
