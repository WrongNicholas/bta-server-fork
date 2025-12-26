package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.core.net.NetworkManager;

public class GuiStatsComponent extends JComponent {
   private final int[] memoryUse = new int[256];
   private int updateCounter = 0;
   private final String[] displayStrings = new String[10];

   public GuiStatsComponent() {
      this.setPreferredSize(new Dimension(256, 196));
      this.setMinimumSize(new Dimension(256, 196));
      this.setMaximumSize(new Dimension(256, 196));
      new Timer(500, e -> update(this)).start();
      this.setBackground(Color.BLACK);
   }

   private void updateStats() {
      long l = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      System.gc();
      this.displayStrings[0] = "Memory use: "
         + l / 1024L / 1024L
         + " mb ("
         + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory()
         + "% free)";
      this.displayStrings[1] = "Threads: " + NetworkManager.readThreads + " + " + NetworkManager.writeThreads;
      this.memoryUse[this.updateCounter++ & 0xFF] = (int)(l * 100L / Runtime.getRuntime().maxMemory());
      this.repaint();
   }

   @Override
   public void paint(Graphics g) {
      g.setColor(new Color(16777215));
      g.fillRect(0, 0, 256, 192);

      for (int i = 0; i < 256; i++) {
         int k = this.memoryUse[i + this.updateCounter & 0xFF];
         g.setColor(new Color(k + 28 << 16));
         g.fillRect(i, 100 - k, 1, k);
      }

      g.setColor(Color.BLACK);

      for (int j = 0; j < this.displayStrings.length; j++) {
         String s = this.displayStrings[j];
         if (s != null) {
            g.drawString(s, 32, 116 + j * 16);
         }
      }
   }

   static void update(GuiStatsComponent guistatscomponent) {
      guistatscomponent.updateStats();
   }
}
