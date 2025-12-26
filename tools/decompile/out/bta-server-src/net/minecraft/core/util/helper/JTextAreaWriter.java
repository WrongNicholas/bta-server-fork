package net.minecraft.core.util.helper;

import java.io.IOException;
import java.io.Writer;
import javax.swing.JTextArea;

public class JTextAreaWriter extends Writer {
   private final JTextArea textArea;

   public JTextAreaWriter(JTextArea textArea) {
      this.textArea = textArea;
   }

   @Override
   public void write(char[] cbuf, int off, int len) throws IOException {
      this.textArea.append(new String(cbuf, off, len));
   }

   @Override
   public void flush() throws IOException {
   }

   @Override
   public void close() throws IOException {
   }
}
