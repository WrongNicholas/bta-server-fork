package net.minecraft.core.net;

import com.mojang.logging.LogUtils;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;
import org.slf4j.Logger;

public class NetworkManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final boolean silent;
   public static int PACKET_DELAY = 1;
   public static int TIMEOUT_TIME = 90000;
   public static final Object threadCounterLock = new Object();
   public static int readThreads;
   public static int writeThreads;
   private final Object writeLock;
   private Socket socket;
   private final SocketAddress address;
   private DataInputStream dis;
   private DataOutputStream dos;
   private boolean running;
   private final List<Packet> incoming;
   private final List<Packet> outgoing;
   private final List<Packet> outgoingSlow;
   private PacketHandler packetListener;
   private boolean quitting;
   private final Thread writeThread;
   private final Thread readThread;
   private boolean disconnected;
   private String disconnectReason;
   private Object[] disconnectReasonObjects;
   private int noInputTicks;
   private int estimatedRemaining;
   public int fakeLag;
   private int slowWriteDelay;
   public final int incomingThroughput;

   public NetworkManager(Socket socket, String s, PacketHandler nethandler, int incomingThroughput) throws IOException {
      this(socket, s, nethandler, incomingThroughput, false);
   }

   public NetworkManager(Socket socket, String s, PacketHandler nethandler, int incomingThroughput, boolean silent) throws IOException {
      this.silent = silent;
      this.writeLock = new Object();
      this.running = true;
      this.incoming = Collections.synchronizedList(new ArrayList<>());
      this.outgoing = Collections.synchronizedList(new ArrayList<>());
      this.outgoingSlow = Collections.synchronizedList(new ArrayList<>());
      this.quitting = false;
      this.disconnected = false;
      this.disconnectReason = "";
      this.noInputTicks = 0;
      this.estimatedRemaining = 0;
      this.fakeLag = 0;
      this.incomingThroughput = incomingThroughput;
      this.slowWriteDelay = 0;
      this.socket = socket;
      this.address = socket.getRemoteSocketAddress();
      this.packetListener = nethandler;

      try {
         socket.setSoTimeout(TIMEOUT_TIME * 1000 / 20);
         socket.setTrafficClass(24);
      } catch (SocketException var7) {
         if (!silent) {
            System.err.println(var7.getMessage());
         }
      }

      this.dis = new DataInputStream(socket.getInputStream());
      this.dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), 65536));
      this.readThread = new Thread(s + " read thread") {
         @Override
         public void run() {
            synchronized (NetworkManager.threadCounterLock) {
               NetworkManager.readThreads++;
            }

            try {
               while (NetworkManager.this.running && !NetworkManager.this.quitting) {
                  while (NetworkManager.this.readPacket()) {
                  }

                  try {
                     sleep(NetworkManager.PACKET_DELAY);
                  } catch (InterruptedException var13) {
                  }
               }
            } finally {
               synchronized (NetworkManager.threadCounterLock) {
                  NetworkManager.readThreads--;
               }
            }
         }
      };
      this.writeThread = new Thread(s + " write thread") {
         @Override
         public void run() {
            synchronized (NetworkManager.threadCounterLock) {
               NetworkManager.writeThreads++;
            }

            try {
               while (NetworkManager.this.running) {
                  while (NetworkManager.this.sendPacket()) {
                  }

                  try {
                     sleep(NetworkManager.PACKET_DELAY);
                  } catch (InterruptedException var14) {
                  }

                  try {
                     if (NetworkManager.this.dos != null) {
                        NetworkManager.this.dos.flush();
                     }
                  } catch (IOException var16) {
                     if (!NetworkManager.this.disconnected) {
                        NetworkManager.this.onNetworkError(var16);
                     } else {
                        NetworkManager.LOGGER.error("IO Exception while flushing Data Output Stream!", (Throwable)var16);
                     }
                  }
               }
            } finally {
               synchronized (NetworkManager.threadCounterLock) {
                  NetworkManager.writeThreads--;
               }
            }
         }
      };
      this.readThread.start();
      this.writeThread.start();
   }

   public void setNetHandler(PacketHandler nethandler) {
      this.packetListener = nethandler;
   }

   public void addToSendQueue(Packet packet) {
      if (!this.quitting) {
         synchronized (this.writeLock) {
            this.estimatedRemaining = this.estimatedRemaining + packet.getEstimatedSize() + 1;
            if (packet.isChunkDataPacket) {
               this.outgoingSlow.add(packet);
            } else {
               this.outgoing.add(packet);
            }
         }
      }
   }

   private boolean sendPacket() {
      boolean hasWrittenPacket = false;

      try {
         if (!this.outgoing.isEmpty() && (this.fakeLag == 0 || System.currentTimeMillis() - this.outgoing.get(0).creationTimeMillis >= this.fakeLag)) {
            Packet packet;
            synchronized (this.writeLock) {
               packet = this.outgoing.remove(0);
               this.estimatedRemaining = this.estimatedRemaining - (packet.getEstimatedSize() + 1);
            }

            Packet.writePacket(packet, this.dos);
            hasWrittenPacket = true;
         }

         if (this.slowWriteDelay-- <= 0
            && !this.outgoingSlow.isEmpty()
            && (this.fakeLag == 0 || System.currentTimeMillis() - this.outgoingSlow.get(0).creationTimeMillis >= this.fakeLag)) {
            Packet packet;
            synchronized (this.writeLock) {
               packet = this.outgoingSlow.remove(0);
               this.estimatedRemaining = this.estimatedRemaining - (packet.getEstimatedSize() + 1);
            }

            Packet.writePacket(packet, this.dos);
            this.slowWriteDelay = 0;
            hasWrittenPacket = true;
         }

         return hasWrittenPacket;
      } catch (Exception var8) {
         if (!this.disconnected) {
            this.onNetworkError(var8);
         }

         return false;
      }
   }

   public void wakeThreads() {
      this.readThread.interrupt();
      this.writeThread.interrupt();
   }

   private boolean readPacket() {
      boolean hasReadPacket = false;

      try {
         Packet packet = Packet.readPacket(this.dis, this.packetListener.isServerHandler());
         if (packet != null) {
            this.incoming.add(packet);
            hasReadPacket = true;
         } else {
            this.networkShutdown("disconnect.endOfStream", new Object[0]);
         }

         return hasReadPacket;
      } catch (Exception var3) {
         if (!this.disconnected) {
            this.onNetworkError(var3);
         }

         return false;
      }
   }

   private void onNetworkError(Exception exception) {
      if (!this.silent) {
         LOGGER.error("Unexpected network error!", (Throwable)exception);
      }

      this.networkShutdown("disconnect.genericReason", new Object[]{"Internal exception: " + exception.toString()});
   }

   public void networkShutdown(String reason, Object[] reasonObjects) {
      if (this.running) {
         this.disconnected = true;
         this.disconnectReason = reason;
         this.disconnectReasonObjects = reasonObjects;
         new Thread(() -> {
            try {
               Thread.sleep(5000L);
               if (this.readThread.isAlive()) {
                  try {
                     this.readThread.stop();
                  } catch (Throwable var3) {
                  }
               }

               if (this.writeThread.isAlive()) {
                  try {
                     this.writeThread.stop();
                  } catch (Throwable var2x) {
                  }
               }
            } catch (InterruptedException var4x) {
               LOGGER.error("Interrupted while stopping read/write threads!", (Throwable)var4x);
            }
         }).start();
         this.running = false;

         try {
            this.dis.close();
            this.dis = null;
         } catch (Throwable var6) {
         }

         try {
            this.dos.close();
            this.dos = null;
         } catch (Throwable var5) {
         }

         try {
            this.socket.close();
            this.socket = null;
         } catch (Throwable var4) {
         }
      }
   }

   public void processReadPackets() {
      if (this.estimatedRemaining > 1048576) {
         this.networkShutdown("disconnect.overflow", new Object[0]);
      }

      if (this.incoming.isEmpty()) {
         if (this.noInputTicks++ == TIMEOUT_TIME) {
            this.networkShutdown("disconnect.timeout", new Object[0]);
         }
      } else {
         this.noInputTicks = 0;
      }

      if (this.incomingThroughput < 0) {
         while (!this.incoming.isEmpty()) {
            Packet packet = this.incoming.remove(0);
            if (!this.disconnected) {
               packet.handlePacket(this.packetListener);
            }
         }
      } else {
         for (int i = this.incomingThroughput; !this.incoming.isEmpty() && i >= 0; i--) {
            Packet packet = this.incoming.remove(0);
            if (!this.disconnected) {
               packet.handlePacket(this.packetListener);
            }
         }
      }

      this.wakeThreads();
      if (this.disconnected && this.incoming.isEmpty()) {
         this.packetListener.handleErrorMessage(this.disconnectReason, this.disconnectReasonObjects);
      }
   }

   public SocketAddress getRemoteAddress() {
      return this.address;
   }

   public void serverShutdown() {
      this.wakeThreads();
      this.quitting = true;
      this.readThread.interrupt();
      new Thread(() -> {
         try {
            Thread.sleep(2000L);
            if (this.running) {
               this.writeThread.interrupt();
               this.networkShutdown("disconnect.closed", new Object[0]);
            }
         } catch (Exception var2) {
            LOGGER.error("Exception on Server Shutdown!", (Throwable)var2);
         }
      }).start();
   }

   public int getNumChunkDataPackets() {
      return this.outgoingSlow.size();
   }
}
