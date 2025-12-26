package com.b100.utils;

import java.io.PrintStream;
import java.util.List;

public class InvalidCharacterException extends RuntimeException {
   private static final long serialVersionUID = 1L;
   private StringReader reader;
   private int line = 1;
   private int column = 1;

   public InvalidCharacterException(StringReader stringReader) {
      this.reader = stringReader;
      String string = this.reader.string();

      for (int i = 0; i < this.reader.position(); i++) {
         char c = string.charAt(i);
         if (c == '\n') {
            this.line++;
            this.column = 1;
         } else {
            this.column++;
         }
      }
   }

   public String getLinePreview() {
      StringWriter stringWriter = new StringWriter();
      stringWriter.writeln(this.getMessage());
      stringWriter.writeln();
      List<String> lines = this.reader.lines();
      int startLine = Math.max(0, this.line - 4);
      int endLine = Math.min(lines.size() - 1, this.line);

      for (int i = startLine; i < endLine; i++) {
         String line = lines.get(i);
         stringWriter.writeln(line);
         if (i + 1 == this.line) {
            for (int j = 0; j < line.length(); j++) {
               int l = getPrintChar(line.charAt(j), true).length();
               boolean thisChar = j + 1 == this.column;

               for (int k = 0; k < l; k++) {
                  stringWriter.write((char)(thisChar ? '^' : ' '));
               }
            }

            stringWriter.writeln();
         }
      }

      return stringWriter.toString();
   }

   public int getLine() {
      return this.line;
   }

   public int getColumn() {
      return this.column;
   }

   @Override
   public String getMessage() {
      return "Invalid character \""
         + getPrintChar(this.reader.get(), false)
         + "\" at line "
         + this.line
         + " column "
         + this.column
         + " (index "
         + this.reader.position()
         + ")";
   }

   public static String getPrintChar(char c, boolean a) {
      if (c == '\\') {
         return a ? "\\" : "\\\\";
      } else if (c == '\n') {
         return a ? " " : "\\n";
      } else if (c == '\t') {
         return a ? " " : "\\t";
      } else {
         return "" + c;
      }
   }

   @Override
   public void printStackTrace(PrintStream s) {
      try {
         s.println(this.getLinePreview());
      } catch (Exception var3) {
         s.println("Could not create line preview: " + var3.getClass().getName() + ": " + var3.getMessage());
      }

      super.printStackTrace(s);
   }
}
