package net.minecraft.server.net;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.slf4j.Logger;

public class NetworkListenThread {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ServerSocket serverSocket;
   private final Thread networkAcceptThread;
   public volatile boolean isListening = false;
   private int connectionCounter = 0;
   private final ArrayList<PacketHandlerLogin> pendingConnections = new ArrayList<>();
   private final ArrayList<PacketHandlerServer> playerList = new ArrayList<>();
   public MinecraftServer mcServer;

   public NetworkListenThread(MinecraftServer minecraftserver, InetAddress address, int i) throws IOException {
      this.mcServer = minecraftserver;
      this.serverSocket = new ServerSocket(i, 0, address);
      this.serverSocket.setPerformancePreferences(0, 2, 1);
      this.isListening = true;
      this.networkAcceptThread = new Thread(() -> {
         while (this.isListening) {
            try {
               Socket socket = this.serverSocket.accept();
               if (socket != null) {
                  PacketHandlerLogin loginHandler = new PacketHandlerLogin(this.mcServer, socket, "Connection #" + this.connectionCounter++);
                  this.addPendingConnection(loginHandler);
               }
            } catch (IOException var3x) {
               LOGGER.error("Exception in network login thread!", (Throwable)var3x);
            }
         }
      });
      this.networkAcceptThread.start();
   }

   public void addPlayer(PacketHandlerServer netserverhandler) {
      this.playerList.add(netserverhandler);
   }

   private void addPendingConnection(PacketHandlerLogin netloginhandler) {
      if (netloginhandler == null) {
         throw new IllegalArgumentException("Got null pendingconnection!");
      } else {
         this.pendingConnections.add(netloginhandler);
      }
   }

   public void handleNetworkListenThread() {
      for (int i = 0; i < this.pendingConnections.size(); i++) {
         PacketHandlerLogin netloginhandler = this.pendingConnections.get(i);

         try {
            netloginhandler.tryLogin();
         } catch (Exception var5) {
            netloginhandler.kickUser("Internal server error");
            LOGGER.warn("Failed to handle packet: {}", var5, var5);
         }

         if (netloginhandler.finishedProcessing) {
            this.pendingConnections.remove(i--);
         }

         netloginhandler.netManager.wakeThreads();
      }

      for (int j = 0; j < this.playerList.size(); j++) {
         PacketHandlerServer packetHandlerServer = this.playerList.get(j);

         try {
            packetHandlerServer.handlePackets();
         } catch (Exception var4) {
            LOGGER.warn("Failed to handle packet: {}", var4, var4);
            packetHandlerServer.kickPlayer("Internal server error");
         }

         if (packetHandlerServer.connectionClosed) {
            this.playerList.remove(j--);
         }

         packetHandlerServer.netManager.wakeThreads();
      }
   }
}
