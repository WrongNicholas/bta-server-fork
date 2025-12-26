package com.b100.utils;

public class StringWriter {
   private StringBuilder string = new StringBuilder();
   private int tabs = 0;
   private String tabString = "\t";

   public StringWriter write(Writable writable) {
      writable.write(this);
      return this;
   }

   public void write(String string) {
      for (int i = 0; i < string.length(); i++) {
         this.write(string.charAt(i));
      }
   }

   public void writeln(String string) {
      this.write(string + "\n");
   }

   public void writeln() {
      this.write("\n");
   }

   public void write(char c) {
      if (this.isLastCharLineBreak()) {
         for (int i = 0; i < this.tabs; i++) {
            this.string.append(this.tabString);
         }
      }

      this.string.append(c);
   }

   public boolean isLastCharLineBreak() {
      return this.string.length() == 0 ? false : this.string.charAt(this.string.length() - 1) == '\n';
   }

   public void addTab() {
      this.tabs++;
   }

   public void removeTab() {
      this.tabs--;
   }

   @Override
   public String toString() {
      return this.string.toString();
   }

   public String getTabString() {
      return this.tabString;
   }

   public void setTabString(String tabString) {
      this.tabString = tabString;
   }
}
