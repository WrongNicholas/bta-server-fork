package net.minecraft.server.gui;

import com.mojang.logging.LogQueues;
import com.mojang.logging.LogUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.Thread.UncaughtExceptionHandler;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.core.net.ICommandListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.player.PlayerListBox;
import org.slf4j.Logger;

public class ServerGui extends JComponent implements ICommandListener {
   public static Logger LOGGER = LogUtils.getLogger();
   public static Font MONOSPACED = new Font("Monospaced", 0, 12);
   private final MinecraftServer mcServer;
   private Thread logAppenderThread;

   public static void initGui(final MinecraftServer mcServer) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception var3) {
         LOGGER.warn("Could not set UI Look and Feel!", (Throwable)var3);
      }

      ServerGui servergui = new ServerGui(mcServer);
      JFrame jframe = new JFrame("Minecraft server");
      jframe.add(servergui);
      jframe.pack();
      jframe.setLocationRelativeTo(null);
      jframe.setVisible(true);
      jframe.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            mcServer.stopServer();
            mcServer.initiateShutdown();

            while (!mcServer.serverStopped) {
               try {
                  Thread.sleep(100L);
               } catch (InterruptedException var3) {
                  ServerGui.LOGGER.error("Interrupt in window close!", (Throwable)var3);
               }
            }

            System.exit(0);
         }
      });
      servergui.start();
   }

   public ServerGui(MinecraftServer mcServer) {
      this.mcServer = mcServer;
      this.setPreferredSize(new Dimension(854, 480));
      this.setLayout(new BorderLayout());

      try {
         this.add(this.getLogComponent(), "Center");
         this.add(this.getStatsComponent(), "West");
      } catch (Exception var3) {
         LOGGER.error("Error constructing ServerGUI!", (Throwable)var3);
      }
   }

   private JComponent getStatsComponent() {
      JPanel jpanel = new JPanel(new BorderLayout());
      jpanel.add(new GuiStatsComponent(), "North");
      jpanel.add(this.getPlayerListComponent(), "Center");
      jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
      return jpanel;
   }

   private JComponent getPlayerListComponent() {
      PlayerListBox playerlistbox = new PlayerListBox(this.mcServer);
      JScrollPane jscrollpane = new JScrollPane(playerlistbox, 22, 30);
      jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
      return jscrollpane;
   }

   private JComponent getLogComponent() {
      JPanel jPanel = new JPanel(new BorderLayout());
      JTextArea jTextArea = new JTextArea();
      JScrollPane jScrollPane = new JScrollPane(jTextArea, 22, 30);
      jTextArea.setEditable(false);
      jTextArea.setFont(MONOSPACED);
      JTextField jTextField = new JTextField();
      jTextField.addActionListener(e -> {
         String s = jTextField.getText().trim();
         if (!s.isEmpty()) {
            this.mcServer.addCommand(s, this);
         }

         jTextField.setText("");
      });
      jPanel.add(jScrollPane, "Center");
      jPanel.add(jTextField, "South");
      jPanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
      this.logAppenderThread = new Thread(() -> {
         while (true) {
            String logEvent = LogQueues.getNextLogEvent("ServerGuiConsole");
            if (logEvent == null) {
               return;
            }

            this.print(jTextArea, jScrollPane, logEvent);
         }
      });
      this.logAppenderThread.setUncaughtExceptionHandler(new ServerGui.DefaultUncaughtExceptionHandler(LOGGER));
      this.logAppenderThread.setDaemon(true);
      return jPanel;
   }

   public void print(JTextArea textArea, JScrollPane scrollPane, String message) {
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> this.print(textArea, scrollPane, message));
      } else {
         Document document = textArea.getDocument();
         JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
         boolean scrollMax = false;
         if (scrollPane.getViewport().getView() == textArea) {
            scrollMax = scrollBar.getValue() + scrollBar.getSize().getHeight() + MONOSPACED.getSize() * 4 > scrollBar.getMaximum();
         }

         try {
            document.insertString(document.getLength(), message, null);
         } catch (BadLocationException var8) {
         }

         if (scrollMax) {
            scrollBar.setValue(Integer.MAX_VALUE);
         }
      }
   }

   @Override
   public void logInfo(String s) {
      LOGGER.info(s);
   }

   @Override
   public String getUsername() {
      return "CONSOLE";
   }

   public void start() {
      this.logAppenderThread.start();
   }

   static MinecraftServer getMinecraftServer(ServerGui servergui) {
      return servergui.mcServer;
   }

   public static class DefaultUncaughtExceptionHandler implements UncaughtExceptionHandler {
      private final Logger logger;

      public DefaultUncaughtExceptionHandler(Logger logger) {
         this.logger = logger;
      }

      @Override
      public void uncaughtException(Thread thread, Throwable t) {
         this.logger.error("Caught previously unhandled exception :", t);
      }
   }
}
