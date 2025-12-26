package com.mojang.brigadier.builder;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class ArgumentBuilderLiteral<S> extends ArgumentBuilder<S, ArgumentBuilderLiteral<S>> {
   private final String literal;

   protected ArgumentBuilderLiteral(String literal) {
      this.literal = literal;
   }

   public static <S> ArgumentBuilderLiteral<S> literal(String name) {
      return new ArgumentBuilderLiteral<>(name);
   }

   protected ArgumentBuilderLiteral<S> getThis() {
      return this;
   }

   public String getLiteral() {
      return this.literal;
   }

   public LiteralCommandNode<S> build() {
      LiteralCommandNode<S> result = new LiteralCommandNode<>(
         this.getLiteral(), this.getCommand(), this.getRequirement(), this.getRedirect(), this.getRedirectModifier(), this.isFork()
      );

      for (CommandNode<S> argument : this.getArguments()) {
         result.addChild(argument);
      }

      return result;
   }
}
