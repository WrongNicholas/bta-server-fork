package com.b100.utils;

import java.util.ArrayList;
import java.util.List;

public class StringReader {
   private String string;
   private int i;

   public StringReader(String string) {
      this.string = Utils.requireNonNull(string);
   }

   public char get() {
      return this.string.charAt(this.i);
   }

   public void skipWhitespace() {
      while (this.i < this.string.length() && this.isWhitespace(this.get())) {
         this.i++;
      }
   }

   public char getAndSkip() {
      char c = this.get();
      this.next();
      return c;
   }

   public void expectAndSkip(char c) {
      this.expect(c);
      this.next();
   }

   public void expect(char c) {
      this.expectOne("" + c);
   }

   public void expectOne(String chars) {
      for (int i = 0; i < chars.length(); i++) {
         if (chars.charAt(i) == this.get()) {
            return;
         }
      }

      throw new InvalidCharacterException(this);
   }

   public boolean isWhitespace(char c) {
      return c == ' ' || c == '\t' || c == '\n';
   }

   public void next() {
      this.i++;
   }

   public boolean isNext(String string) {
      return this.string.substring(this.i, this.i + string.length()).equals(string);
   }

   public void skip(int i) {
      this.i += i;
   }

   public void expectAndSkip(String string) {
      if (!this.isNext(string)) {
         throw new InvalidCharacterException(this);
      } else {
         this.skip(string.length());
      }
   }

   public int remainingCharacters() {
      return this.string.length() - this.i;
   }

   public String readUntilCharacter(char endChar) {
      StringBuilder builder = new StringBuilder();

      while (this.i < this.string.length()) {
         char c = this.get();
         if (c == endChar) {
            break;
         }

         builder.append(c);
         this.next();
      }

      return builder.toString();
   }

   public String string() {
      return this.string;
   }

   public int position() {
      return this.i;
   }

   public List<String> lines() {
      List<String> lines = new ArrayList<>();
      String line = "";

      for (int i = 0; i < this.string.length(); i++) {
         char c = this.string.charAt(i);
         if (c == '\n') {
            lines.add(line);
            line = "";
         } else {
            line = line + c;
         }
      }

      lines.add(line);
      return lines;
   }
}
