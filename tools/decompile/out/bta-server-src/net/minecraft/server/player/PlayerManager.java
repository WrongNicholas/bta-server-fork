package net.minecraft.server.player;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketBlockRegionUpdate;
import net.minecraft.core.net.packet.PacketBlockUpdate;
import net.minecraft.core.net.packet.PacketChunkBlocksUpdate;
import net.minecraft.core.net.packet.PacketChunkVisibility;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.PlayerHash;
import net.minecraft.server.world.WorldServer;
import org.slf4j.Logger;

public class PlayerManager {
   public static final Logger LOGGER = LogUtils.getLogger();
   public final List<PlayerServer> players;
   private final PlayerHash playerInstances;
   private final List<PlayerManager.PlayerInstance> playerInstancesToUpdate;
   private final MinecraftServer server;
   private final int dimension;
   private final int viewRadius;
   private final int[][] field_22089_e = new int[][]{{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

   public PlayerManager(MinecraftServer server, int dimension, int viewRadius) {
      this.players = new ArrayList<>();
      this.playerInstances = new PlayerHash();
      this.playerInstancesToUpdate = new ArrayList<>();
      if (viewRadius > 64) {
         throw new IllegalArgumentException("Too big view radius!");
      } else {
         if (viewRadius > 15) {
            LOGGER.warn("View radius '{}' is greater then 15, expect possibly extreme hit to performance!", viewRadius);
         } else if (viewRadius < 3) {
            throw new IllegalArgumentException("Too small view radius!");
         }

         this.viewRadius = viewRadius;
         this.server = server;
         this.dimension = dimension;
      }
   }

   public void tick() {
      for (PlayerManager.PlayerInstance playerInstance : this.playerInstancesToUpdate) {
         playerInstance.tick();
      }

      this.playerInstancesToUpdate.clear();
   }

   private PlayerManager.PlayerInstance getPlayerInstance(int chunkX, int chunkZ, boolean makeNewIfAbsent) {
      long key = chunkX + 2147483647L | chunkZ + 2147483647L << 32;
      PlayerManager.PlayerInstance instance = (PlayerManager.PlayerInstance)this.playerInstances.getValueByKey(key);
      if (instance == null && makeNewIfAbsent) {
         instance = new PlayerManager.PlayerInstance(chunkX, chunkZ);
         this.playerInstances.add(key, instance);
      }

      return instance;
   }

   public void markBlockNeedsUpdate(int x, int y, int z) {
      int xChunk = x >> 4;
      int zChunk = z >> 4;
      PlayerManager.PlayerInstance instance = this.getPlayerInstance(xChunk, zChunk, false);
      if (instance != null) {
         instance.markBlockNeedsUpdate(x & 15, y, z & 15);
      }
   }

   public void addPlayer(PlayerServer player) {
      int xChunk = (int)player.x >> 4;
      int zChunk = (int)player.z >> 4;
      player.viewingX = player.x;
      player.viewingZ = player.z;
      int k = 0;
      int radius = this.viewRadius;
      int dx = 0;
      int dz = 0;
      this.getPlayerInstance(xChunk, zChunk, true).addPlayer(player);

      for (int k1 = 1; k1 <= radius * 2; k1++) {
         for (int i2 = 0; i2 < 2; i2++) {
            int[] ai = this.field_22089_e[k++ % 4];

            for (int j2 = 0; j2 < k1; j2++) {
               dx += ai[0];
               dz += ai[1];
               this.getPlayerInstance(xChunk + dx, zChunk + dz, true).addPlayer(player);
            }
         }
      }

      k %= 4;

      for (int l1 = 0; l1 < radius * 2; l1++) {
         dx += this.field_22089_e[k][0];
         dz += this.field_22089_e[k][1];
         this.getPlayerInstance(xChunk + dx, zChunk + dz, true).addPlayer(player);
      }

      this.players.add(player);
   }

   public void removePlayer(PlayerServer entityplayermp) {
      int i = (int)entityplayermp.viewingX >> 4;
      int j = (int)entityplayermp.viewingZ >> 4;

      for (int k = i - this.viewRadius; k <= i + this.viewRadius; k++) {
         for (int l = j - this.viewRadius; l <= j + this.viewRadius; l++) {
            PlayerManager.PlayerInstance playerinstance = this.getPlayerInstance(k, l, false);
            if (playerinstance != null) {
               playerinstance.removePlayer(entityplayermp);
            }
         }
      }

      this.players.remove(entityplayermp);
   }

   private boolean chunkWithinViewRadius(int chunkX, int chunkZ, int originX, int originZ) {
      int cDX = chunkX - originX;
      int cDZ = chunkZ - originZ;
      return cDZ >= -this.viewRadius && cDZ <= this.viewRadius && cDX >= -this.viewRadius && cDX <= this.viewRadius;
   }

   public void onPlayerMoved(PlayerServer player) {
      int chunkX = (int)player.x >> 4;
      int chunkZ = (int)player.z >> 4;
      double dx = player.viewingX - player.x;
      double dz = player.viewingZ - player.z;
      double dSqr = dx * dx + dz * dz;
      if (!(dSqr < 64.0)) {
         int playerChunkX = (int)player.viewingX >> 4;
         int playerChunkZ = (int)player.viewingZ >> 4;
         int cDX = chunkX - playerChunkX;
         int cDZ = chunkZ - playerChunkZ;
         if (cDX != 0 || cDZ != 0) {
            for (int _cx = chunkX - this.viewRadius; _cx <= chunkX + this.viewRadius; _cx++) {
               for (int _cz = chunkZ - this.viewRadius; _cz <= chunkZ + this.viewRadius; _cz++) {
                  if (!this.chunkWithinViewRadius(_cx, _cz, playerChunkX, playerChunkZ)) {
                     this.getPlayerInstance(_cx, _cz, true).addPlayer(player);
                  }

                  if (!this.chunkWithinViewRadius(_cx - cDX, _cz - cDZ, chunkX, chunkZ)) {
                     PlayerManager.PlayerInstance playerinstance = this.getPlayerInstance(_cx - cDX, _cz - cDZ, false);
                     if (playerinstance != null) {
                        playerinstance.removePlayer(player);
                     }
                  }
               }
            }

            player.viewingX = player.x;
            player.viewingZ = player.z;
         }
      }
   }

   public int getMaxTrackingDistance() {
      return this.viewRadius * 16 - 16;
   }

   class PlayerInstance {
      private final List<PlayerServer> players = new ArrayList<>();
      private final int chunkX;
      private final int chunkZ;
      private final ChunkCoordinate currentChunk;
      private final int[] blocksToUpdate = new int[10];
      private int numBlocksToUpdate = 0;
      private int minX;
      private int maxX;
      private int minY;
      private int maxY;
      private int minZ;
      private int maxZ;

      public PlayerInstance(int chunkX, int chunkZ) {
         this.chunkX = chunkX;
         this.chunkZ = chunkZ;
         this.currentChunk = new ChunkCoordinate(chunkX, chunkZ);
         PlayerManager.this.server.getDimensionWorld(PlayerManager.this.dimension).chunkProviderServer.prepareChunk(chunkX, chunkZ);
      }

      public void addPlayer(PlayerServer player) {
         if (this.players.contains(player)) {
            throw new IllegalStateException("Failed to add player. " + player + " already is in chunk " + this.chunkX + ", " + this.chunkZ);
         } else {
            player.field_420_ah.add(this.currentChunk);
            player.playerNetServerHandler.sendPacket(new PacketChunkVisibility(this.currentChunk.x, this.currentChunk.z, true));
            this.players.add(player);
            player.loadedChunks.add(this.currentChunk);
         }
      }

      public void removePlayer(PlayerServer player) {
         if (this.players.contains(player)) {
            this.players.remove(player);
            if (this.players.isEmpty()) {
               long key = this.chunkX + 2147483647L | this.chunkZ + 2147483647L << 32;
               PlayerManager.this.playerInstances.remove(key);
               if (this.numBlocksToUpdate > 0) {
                  PlayerManager.this.playerInstancesToUpdate.remove(this);
               }

               PlayerManager.this.server.getDimensionWorld(PlayerManager.this.dimension).chunkProviderServer.dropChunk(this.chunkX, this.chunkZ);
            }

            player.loadedChunks.remove(this.currentChunk);
            if (player.field_420_ah.contains(this.currentChunk)) {
               player.playerNetServerHandler.sendPacket(new PacketChunkVisibility(this.chunkX, this.chunkZ, false));
            }
         }
      }

      public void markBlockNeedsUpdate(int x, int y, int z) {
         if (this.numBlocksToUpdate == 0) {
            PlayerManager.this.playerInstancesToUpdate.add(this);
            this.minX = this.maxX = x;
            this.minY = this.maxY = y;
            this.minZ = this.maxZ = z;
         }

         if (this.minX > x) {
            this.minX = x;
         }

         if (this.maxX < x) {
            this.maxX = x;
         }

         if (this.minY > y) {
            this.minY = y;
         }

         if (this.maxY < y) {
            this.maxY = y;
         }

         if (this.minZ > z) {
            this.minZ = z;
         }

         if (this.maxZ < z) {
            this.maxZ = z;
         }

         if (this.numBlocksToUpdate < 10) {
            int index = Chunk.makeBlockIndex(x, y, z);

            for (int i = 0; i < this.numBlocksToUpdate; i++) {
               if (this.blocksToUpdate[i] == index) {
                  return;
               }
            }

            this.blocksToUpdate[this.numBlocksToUpdate++] = index;
         }
      }

      public void sendPacketToPlayersInInstance(Packet packet) {
         for (int i = 0; i < this.players.size(); i++) {
            PlayerServer entityplayermp = this.players.get(i);
            if (entityplayermp.field_420_ah.contains(this.currentChunk)) {
               entityplayermp.playerNetServerHandler.sendPacket(packet);
            }
         }
      }

      public void tick() {
         WorldServer world = PlayerManager.this.server.getDimensionWorld(PlayerManager.this.dimension);
         if (this.numBlocksToUpdate != 0) {
            if (this.numBlocksToUpdate == 1) {
               int x = this.chunkX * 16 + this.minX;
               int y = this.minY;
               int z = this.chunkZ * 16 + this.minZ;
               this.sendPacketToPlayersInInstance(new PacketBlockUpdate(x, y, z, world));
               if (Blocks.isEntityTile[world.getBlockId(x, y, z)]) {
                  this.updateTileEntity(world.getTileEntity(x, y, z));
               }
            } else if (this.numBlocksToUpdate >= 10) {
               this.minY = this.minY / 2 * 2;
               this.maxY = (this.maxY / 2 + 1) * 2;
               int minWorldX = this.minX + this.chunkX * 16;
               int minWorldY = this.minY;
               int minWorldZ = this.minZ + this.chunkZ * 16;
               int xSize = this.maxX - this.minX + 1;
               int ySize = this.maxY - this.minY + 2;
               int zSize = this.maxZ - this.minZ + 1;
               this.sendPacketToPlayersInInstance(new PacketBlockRegionUpdate(minWorldX, minWorldY, minWorldZ, xSize, ySize, zSize, world));
               List<TileEntity> list = world.getBlockEntitiesWithinBounds(
                  minWorldX, minWorldY, minWorldZ, minWorldX + xSize, minWorldY + ySize, minWorldZ + zSize
               );

               for (int j3 = 0; j3 < list.size(); j3++) {
                  this.updateTileEntity(list.get(j3));
               }
            } else {
               this.sendPacketToPlayersInInstance(new PacketChunkBlocksUpdate(this.chunkX, this.chunkZ, this.blocksToUpdate, this.numBlocksToUpdate, world));

               for (int k = 0; k < this.numBlocksToUpdate; k++) {
                  int x = this.chunkX * 16 + (this.blocksToUpdate[k] >> 0 & 15);
                  int y = this.blocksToUpdate[k] >> 8;
                  int z = this.chunkZ * 16 + (this.blocksToUpdate[k] >> 4 & 15);
                  if (Blocks.isEntityTile[world.getBlockId(x, y, z)]) {
                     this.updateTileEntity(world.getTileEntity(x, y, z));
                  }
               }
            }

            this.numBlocksToUpdate = 0;
         }
      }

      public void updateTileEntity(TileEntity tileentity) {
         if (tileentity != null) {
            Packet packet = tileentity.getDescriptionPacket();
            if (packet != null) {
               this.sendPacketToPlayersInInstance(packet);
            }
         }
      }
   }
}
