package net.minecraft.server.net.handler;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.block.entity.TileEntityMobSpawner;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.vehicle.EntityBoat;
import net.minecraft.core.enums.ArtType;
import net.minecraft.core.enums.EnumSignPicture;
import net.minecraft.core.enums.PlacementMode;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.net.ChatEmotes;
import net.minecraft.core.net.ICommandListener;
import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.handler.PacketHandler;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketAnimate;
import net.minecraft.core.net.packet.PacketBlockUpdate;
import net.minecraft.core.net.packet.PacketBoatControl;
import net.minecraft.core.net.packet.PacketChat;
import net.minecraft.core.net.packet.PacketCommandManager;
import net.minecraft.core.net.packet.PacketContainerAck;
import net.minecraft.core.net.packet.PacketContainerClick;
import net.minecraft.core.net.packet.PacketContainerClose;
import net.minecraft.core.net.packet.PacketContainerSetSlot;
import net.minecraft.core.net.packet.PacketCustomPayload;
import net.minecraft.core.net.packet.PacketDisconnect;
import net.minecraft.core.net.packet.PacketGuidebook;
import net.minecraft.core.net.packet.PacketInteract;
import net.minecraft.core.net.packet.PacketKeepAlive;
import net.minecraft.core.net.packet.PacketMovePlayer;
import net.minecraft.core.net.packet.PacketPlayerAction;
import net.minecraft.core.net.packet.PacketRequestCommandManager;
import net.minecraft.core.net.packet.PacketRespawn;
import net.minecraft.core.net.packet.PacketSetCarriedItem;
import net.minecraft.core.net.packet.PacketSetHotbarOffset;
import net.minecraft.core.net.packet.PacketSetItemName;
import net.minecraft.core.net.packet.PacketSetMobSpawner;
import net.minecraft.core.net.packet.PacketSetPaintingArt;
import net.minecraft.core.net.packet.PacketSignUpdate;
import net.minecraft.core.net.packet.PacketUpdateCreativeInventory;
import net.minecraft.core.net.packet.PacketUpdatePlayerProfile;
import net.minecraft.core.net.packet.PacketUpdatePlayerState;
import net.minecraft.core.net.packet.PacketUseOrPlaceItemStack;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuFlag;
import net.minecraft.core.player.inventory.menu.MenuInventoryCreative;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.AES;
import net.minecraft.core.util.helper.ChatAllowedCharacters;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.RestHandler;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.util.phys.Vec3;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.command.ServerCommandSource;
import net.minecraft.server.player.PlayerListBox;
import net.minecraft.server.world.WorldServer;
import org.slf4j.Logger;

public class PacketHandlerServer extends PacketHandler implements ICommandListener {
   public static Logger LOGGER = LogUtils.getLogger();
   public NetworkManager netManager;
   public boolean connectionClosed = false;
   private final MinecraftServer mcServer;
   private PlayerServer playerEntity;
   private int field_15_f;
   private int field_22004_g;
   private int playerInAirTime;
   private double lastPosX;
   private double lastPosY;
   private double lastPosZ;
   private boolean hasMoved = true;
   private final Map<Integer, Short> guiIdMap = new HashMap<>();

   public PacketHandlerServer(MinecraftServer minecraftserver, NetworkManager networkManager, PlayerServer player) {
      this.mcServer = minecraftserver;
      this.netManager = networkManager;
      networkManager.setNetHandler(this);
      this.playerEntity = player;
      player.playerNetServerHandler = this;
   }

   public void handlePackets() {
      this.netManager.processReadPackets();
      if (this.field_15_f - this.field_22004_g > 20) {
         this.sendPacket(new PacketKeepAlive());
      }
   }

   public boolean canInteract() {
      return this.playerEntity.canInteract();
   }

   public void kickPlayer(String s) {
      this.playerEntity.func_30002_A();
      this.sendPacket(new PacketDisconnect(s));
      this.netManager.serverShutdown();
      this.mcServer
         .playerList
         .sendPacketToAllPlayers(new PacketChat(this.playerEntity.getDisplayName() + TextFormatting.YELLOW + " was kicked from the game."));
      this.mcServer
         .playerList
         .updatePlayerProfile(
            this.playerEntity.username,
            this.playerEntity.nickname,
            this.playerEntity.uuid,
            this.playerEntity.score,
            this.playerEntity.chatColor,
            false,
            this.playerEntity.isOperator()
         );
      this.mcServer.playerList.playerLoggedOut(this.playerEntity);
      this.connectionClosed = true;
      if (MinecraftServer.statsStatus) {
         RestHandler.post(
            "https://api.betterthanadventure.net/stats?serverToken=" + MinecraftServer.statsToken + "&count=" + this.mcServer.playerList.playerEntities.size()
         );
      }

      PlayerListBox.updateList();
   }

   @Override
   public void handleSetMobSpawner(PacketSetMobSpawner packet) {
      WorldServer world = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
      if (this.playerEntity.getGamemode() == Gamemode.creative && world.isBlockLoaded(packet.xPosition, packet.yPosition, packet.zPosition)) {
         TileEntity tileentity = world.getTileEntity(packet.xPosition, packet.yPosition, packet.zPosition);
         if (tileentity instanceof TileEntityMobSpawner) {
            TileEntityMobSpawner tileEntityMobSpawner = (TileEntityMobSpawner)tileentity;
            NamespaceID namespaceID = EntityDispatcher.idForClass(packet.entityClass);
            tileEntityMobSpawner.setMobId(namespaceID == null ? "none" : namespaceID.toString());
            tileEntityMobSpawner.setChanged();
            world.markBlockNeedsUpdate(packet.xPosition, packet.yPosition, packet.zPosition);
         }
      }
   }

   @Override
   public void handleFlying(PacketMovePlayer packet) {
      if (this.playerEntity.isAlive()) {
         WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
         if (!this.hasMoved) {
            double dx = packet.x - this.lastPosX;
            double dy = packet.y - this.lastPosY;
            double dz = packet.z - this.lastPosZ;
            dx *= dx;
            dy *= dy;
            dz *= dz;
            if (dx * dx < 0.01 && dy * dy < 0.01 && dz * dz < 0.01) {
               this.hasMoved = true;
            }
         }

         if (this.hasMoved) {
            float yRot = this.playerEntity.yRot;
            float xRot = this.playerEntity.xRot;
            if (packet.hasRotation) {
               yRot = MathHelper.normalizeRotation(packet.yaw);
               xRot = MathHelper.normalizeRotation(packet.pitch);
            }

            boolean sleeping = this.playerEntity.isPlayerSleeping();
            boolean sitting = this.playerEntity.vehicle != null && !(this.playerEntity.vehicle instanceof Entity);
            if (!sleeping && !sitting) {
               if (this.playerEntity.vehicle instanceof Entity) {
                  this.playerEntity.vehicle.positionRider();
                  double xd = 0.0;
                  double zd = 0.0;
                  if (packet.hasPosition && packet.y == -999.0) {
                     xd = packet.x;
                     zd = packet.z;
                  }

                  this.playerEntity.onGround = packet.onGround;
                  this.playerEntity.onUpdateEntity();
                  this.playerEntity.move(xd, 0.0, zd);
                  this.playerEntity.absMoveTo(this.playerEntity.x, this.playerEntity.y, this.playerEntity.z, yRot, xRot);
                  this.playerEntity.xd = xd;
                  this.playerEntity.zd = zd;
                  worldserver.updateEntityWithOptionalForce((Entity)this.playerEntity.vehicle, false);
                  this.playerEntity.vehicle.positionRider();
                  this.mcServer.playerList.onPlayerMoved(this.playerEntity);
               } else {
                  this.lastPosX = this.playerEntity.x;
                  this.lastPosY = this.playerEntity.y;
                  this.lastPosZ = this.playerEntity.z;
                  double newPosX = this.playerEntity.x;
                  double newPosY = this.playerEntity.y;
                  double oldPosY = this.playerEntity.y;
                  double newPosZ = this.playerEntity.z;
                  if (packet.hasPosition && packet.y == -999.0) {
                     packet.hasPosition = false;
                  }

                  if (packet.hasPosition) {
                     newPosX = packet.x;
                     newPosY = packet.y;
                     newPosZ = packet.z;
                     if (Math.abs(packet.x) > 3.2E7 || Math.abs(packet.z) > 3.2E7 || Double.isNaN(packet.x) || Double.isNaN(packet.y) || Double.isNaN(packet.z)
                        )
                      {
                        LOGGER.warn("{} tried to move to an illegal position", this.playerEntity.username);
                        this.teleportAndRotate(this.lastPosX, this.lastPosY, this.lastPosZ, yRot, xRot);
                        return;
                     }
                  }

                  this.playerEntity.onUpdateEntity();
                  this.playerEntity.ySlideOffset = 0.0F;
                  this.playerEntity.absMoveTo(this.lastPosX, this.lastPosY, this.lastPosZ, yRot, xRot);
                  if (this.hasMoved) {
                     double dx = newPosX - this.playerEntity.x;
                     double dy = newPosY - this.playerEntity.y;
                     double dz = newPosZ - this.playerEntity.z;
                     double velSquared = dx * dx + dy * dy + dz * dz;
                     if (velSquared > 100.0) {
                        LOGGER.warn("{} moved too quickly!", this.playerEntity.username);
                        this.teleportAndRotate(this.lastPosX, this.lastPosY, this.lastPosZ, yRot, xRot);
                     } else {
                        float bb_expand = 0.0625F;
                        boolean insideBlockOld = worldserver.getCubes(
                              this.playerEntity, this.playerEntity.bb.copy().getInsetBoundingBox(bb_expand, bb_expand, bb_expand)
                           )
                           .isEmpty();
                        this.playerEntity.move(dx, dy, dz);
                        dx = newPosX - this.playerEntity.x;
                        double ndy = Math.max(newPosY - this.playerEntity.y, -0.0784);
                        dz = newPosZ - this.playerEntity.z;
                        velSquared = dx * dx + ndy * ndy + dz * dz;
                        boolean movedWrong = false;
                        if (this.playerEntity.pushTime < 0.1F
                           && !this.playerEntity.getGamemode().canPlayerFly()
                           && velSquared > 0.0625
                           && !this.playerEntity.isPlayerSleeping()) {
                           movedWrong = true;
                           LOGGER.warn("{} moved wrongly!", this.playerEntity.username);
                           LOGGER.warn("Got position {}, {}, {}", newPosX, newPosY, newPosZ);
                           LOGGER.warn("Expected {}, {}, {}", this.playerEntity.x, this.playerEntity.y, this.playerEntity.z);
                        }

                        this.playerEntity.absMoveTo(newPosX, newPosY, newPosZ, yRot, xRot);
                        boolean insideBlockNew = worldserver.getCubes(
                              this.playerEntity, this.playerEntity.bb.copy().getInsetBoundingBox(bb_expand, bb_expand, bb_expand)
                           )
                           .isEmpty();
                        if (this.playerEntity.pushTime < 0.1F
                           && !this.playerEntity.getGamemode().canPlayerFly()
                           && insideBlockOld
                           && (movedWrong || !insideBlockNew)
                           && !this.playerEntity.isPlayerSleeping()) {
                           this.teleportAndRotate(this.lastPosX, this.lastPosY, this.lastPosZ, yRot, xRot);
                        } else {
                           AABB aabb = this.playerEntity.bb.copy().grow(bb_expand, bb_expand, bb_expand).expand(0.0, -0.55, 0.0);
                           if (this.playerEntity.getGamemode().canPlayerFly() || this.mcServer.allowFlight || worldserver.getIsAnySolidGround(aabb)) {
                              this.playerInAirTime = 0;
                           } else if (dy > -0.03125) {
                              this.playerInAirTime++;
                              if (this.playerInAirTime > 100) {
                                 LOGGER.warn(this.playerEntity.username + " was kicked for floating too long!");
                                 this.kickPlayer("Flying is not enabled on this server");
                                 return;
                              }
                           }

                           this.playerEntity.onGround = packet.onGround;
                           this.mcServer.playerList.onPlayerMoved(this.playerEntity);
                           this.playerEntity.handleFalling(this.playerEntity.y - oldPosY, packet.onGround);
                        }
                     }
                  }
               }
            } else {
               if (sitting) {
                  this.playerEntity.vehicle.positionRider();
               }

               this.playerEntity.onUpdateEntity();
               this.playerEntity.absMoveTo(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.yRot, this.playerEntity.xRot);
               worldserver.updateEntity(this.playerEntity);
            }
         }
      }
   }

   public void teleport(double x, double y, double z) {
      this.hasMoved = false;
      this.lastPosX = x;
      this.lastPosY = y;
      this.lastPosZ = z;
      this.playerEntity.absMoveTo(x, y, z, this.playerEntity.yRot, this.playerEntity.xRot);
      this.playerEntity.playerNetServerHandler.sendPacket(new PacketMovePlayer.Pos(x, y + 1.625, z, false));
   }

   public void teleportAndRotate(double x, double y, double z, float yaw, float pitch) {
      this.hasMoved = false;
      this.lastPosX = x;
      this.lastPosY = y;
      this.lastPosZ = z;
      this.playerEntity.absMoveTo(x, y, z, yaw, pitch);
      this.playerEntity.playerNetServerHandler.sendPacket(new PacketMovePlayer.PosRot(x, y + 1.625, z, yaw, pitch, false));
   }

   @Override
   public void handleBlockDig(PacketPlayerAction playerActionPacket) {
      int x = playerActionPacket.xPosition;
      int y = playerActionPacket.yPosition;
      int z = playerActionPacket.zPosition;
      if (this.canInteract() && this.playerEntity.getHeldObject() == null) {
         WorldServer world = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
         if (playerActionPacket.action == 4) {
            this.playerEntity.dropCurrentItem(false);
         } else if (playerActionPacket.action == 5) {
            this.playerEntity.dropCurrentItem(true);
         } else if (playerActionPacket.action == 6) {
            this.playerEntity.pickBlock(x, y, z);
         } else {
            double playerDistX = this.playerEntity.x - (x + 0.5);
            double playerDistY = this.playerEntity.y - (y + 0.5);
            double playerDistZ = this.playerEntity.z - (z + 0.5);
            double playerDist = playerDistX * playerDistX + playerDistY * playerDistY + playerDistZ * playerDistZ;
            if (!(playerDist > 44.0)) {
               boolean ignoreSpawnProtection = this.mcServer.spawnProtectionRange <= 0
                  || world.dimension.id != 0
                  || this.mcServer.playerList.isOp(this.playerEntity.uuid);
               ChunkCoordinates spawnPos = world.getSpawnPoint();
               int spawnDistX = (int)MathHelper.abs(x - spawnPos.x);
               int spawnDistZ = (int)MathHelper.abs(z - spawnPos.z);
               int distanceFromSpawn = Math.max(spawnDistX, spawnDistZ);
               if (distanceFromSpawn <= this.mcServer.spawnProtectionRange && !ignoreSpawnProtection) {
                  this.playerEntity.playerNetServerHandler.sendPacket(new PacketBlockUpdate(x, y, z, world));
               } else {
                  if (playerActionPacket.action == 0) {
                     this.playerEntity.playerController.startMining(x, y, z, playerActionPacket.side);
                  } else if (playerActionPacket.action == 1) {
                     this.playerEntity.playerController.hitBlock(x, y, z, playerActionPacket.side, playerActionPacket.xHit, playerActionPacket.yHit);
                  } else if (playerActionPacket.action == 2 && !this.playerEntity.playerController.destroyBlock(x, y, z, playerActionPacket.side)) {
                     this.playerEntity.playerNetServerHandler.sendPacket(new PacketBlockUpdate(x, y, z, world));
                  }
               }
            }
         }
      }
   }

   public void handleSendInitialPlayerList() {
      for (PlayerServer playerServer : this.mcServer.playerList.playerEntities) {
         this.sendPacket(
            new PacketUpdatePlayerProfile(
               playerServer.username, playerServer.nickname, playerServer.uuid, playerServer.score, playerServer.chatColor, true, playerServer.isOperator()
            )
         );
      }

      PlayerListBox.updateList();
   }

   @Override
   public void handlePlace(PacketUseOrPlaceItemStack packet) {
      if (this.canInteract()) {
         WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
         ItemStack itemstack = this.playerEntity.inventory.getCurrentItem();
         boolean ignoreSpawnProtection = this.mcServer.spawnProtectionRange <= 0
            || worldserver.dimension.id != 0
            || this.mcServer.playerList.isOp(this.playerEntity.uuid);
         switch (packet.type) {
            case 0:
               int xx = packet.xPosition;
               int yx = packet.yPosition;
               int zx = packet.zPosition;
               Direction directionx = packet.direction;
               double xPlacedx = packet.xPlaced;
               double yPlacedx = packet.yPlaced;
               ChunkCoordinates chunkcoordinatesx = worldserver.getSpawnPoint();
               int i1x = (int)MathHelper.abs(xx - chunkcoordinatesx.x);
               int j1x = (int)MathHelper.abs(zx - chunkcoordinatesx.z);
               if (i1x > j1x) {
                  j1x = i1x;
               }

               if (this.hasMoved
                  && this.playerEntity.distanceToSqr(xx + 0.5, yx + 0.5, zx + 0.5) < 64.0
                  && (j1x > this.mcServer.spawnProtectionRange || ignoreSpawnProtection)) {
                  this.playerEntity
                     .playerController
                     .useOrPlaceItemStackOnTile(this.playerEntity, worldserver, itemstack, xx, yx, zx, directionx.getSide(), xPlacedx, yPlacedx);
               }

               this.playerEntity.playerNetServerHandler.sendPacket(new PacketBlockUpdate(xx, yx, zx, worldserver));
               xx += directionx.getOffsetX();
               yx += directionx.getOffsetY();
               zx += directionx.getOffsetZ();
               this.playerEntity.playerNetServerHandler.sendPacket(new PacketBlockUpdate(xx, yx, zx, worldserver));
               break;
            case 1:
               if (itemstack == null) {
                  return;
               }

               this.playerEntity.playerController.useItemStackOnNothing(this.playerEntity, worldserver, itemstack);
               break;
            case 2:
               int x = packet.xPosition;
               int y = packet.yPosition;
               int z = packet.zPosition;
               Direction direction = packet.direction;
               double xPlaced = packet.xPlaced;
               double yPlaced = packet.yPlaced;
               ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
               int i1 = (int)MathHelper.abs(x - chunkcoordinates.x);
               int j1 = (int)MathHelper.abs(z - chunkcoordinates.z);
               if (i1 > j1) {
                  j1 = i1;
               }

               if (this.hasMoved
                  && this.playerEntity.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) < 64.0
                  && (j1 > this.mcServer.spawnProtectionRange || ignoreSpawnProtection)) {
                  this.playerEntity
                     .playerController
                     .placeItemStackOnTile(this.playerEntity, worldserver, itemstack, x, y, z, direction.getSide(), xPlaced, yPlaced);
               }

               this.playerEntity.playerNetServerHandler.sendPacket(new PacketBlockUpdate(x, y, z, worldserver));
               x += direction.getOffsetX();
               y += direction.getOffsetY();
               z += direction.getOffsetZ();
               this.playerEntity.playerNetServerHandler.sendPacket(new PacketBlockUpdate(x, y, z, worldserver));
         }

         itemstack = this.playerEntity.inventory.getCurrentItem();
         if (itemstack != null && itemstack.stackSize <= 0) {
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.getCurrentItemIndex()] = null;
         }

         this.playerEntity.isChangingQuantityOnly = true;
         this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.getCurrentItemIndex()] = ItemStack.copyItemStack(
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.getCurrentItemIndex()]
         );
         Slot slot = this.playerEntity.craftingInventory.getSlotFor(this.playerEntity.inventory, this.playerEntity.inventory.getCurrentItemIndex());
         this.playerEntity.craftingInventory.broadcastChanges();
         this.playerEntity.isChangingQuantityOnly = false;
         if (slot == null) {
            LOGGER.warn(
               "Couldn't find slot for index '{}' in {}'s inventory while they were in the {} container!",
               this.playerEntity.inventory.getCurrentItemIndex(),
               this.playerEntity.username,
               this.playerEntity.craftingInventory.getClass().getSimpleName()
            );
         } else {
            if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packet.itemStack)) {
               this.sendPacket(
                  new PacketContainerSetSlot(this.playerEntity.craftingInventory.containerId, slot.index, this.playerEntity.inventory.getCurrentItem())
               );
            }
         }
      }
   }

   @Override
   public void handleErrorMessage(String message, Object[] objects) {
      LOGGER.info(this.playerEntity.username + " lost connection: " + message);
      this.mcServer.playerList.sendPacketToAllPlayers(new PacketChat(this.playerEntity.getDisplayName() + TextFormatting.YELLOW + " left the game."));
      this.mcServer
         .playerList
         .updatePlayerProfile(
            this.playerEntity.username,
            this.playerEntity.nickname,
            this.playerEntity.uuid,
            this.playerEntity.score,
            this.playerEntity.chatColor,
            false,
            this.playerEntity.isOperator()
         );
      this.mcServer.playerList.playerLoggedOut(this.playerEntity);
      this.connectionClosed = true;
      if (MinecraftServer.statsStatus) {
         RestHandler.post(
            "https://api.betterthanadventure.net/stats?serverToken=" + MinecraftServer.statsToken + "&count=" + this.mcServer.playerList.playerEntities.size()
         );
      }

      PlayerListBox.updateList();
   }

   @Override
   public void handleInvalidPacket(Packet packet) {
      LOGGER.warn("{} wasn't prepared to deal with a {}", this.getClass(), packet.getClass());
      this.kickPlayer("Protocol error, unexpected packet");
   }

   public void sendPacket(Packet packet) {
      this.netManager.addToSendQueue(packet);
      this.field_22004_g = this.field_15_f;
   }

   @Override
   public void handleBlockItemSwitch(PacketSetCarriedItem setCarriedItemPacket) {
      if (setCarriedItemPacket.id >= 0 && setCarriedItemPacket.id <= ContainerInventory.playerMainInventorySize()) {
         this.playerEntity.inventory.setCurrentItemIndex(setCarriedItemPacket.id, false);
         if (setCarriedItemPacket.id != this.playerEntity.inventory.getCurrentItemIndex()) {
            this.sendPacket(new PacketSetCarriedItem(setCarriedItemPacket.id));
         }
      } else {
         LOGGER.warn("{} tried to set an invalid carried item", this.playerEntity.username);
      }
   }

   @Override
   public void handleChat(PacketChat packet) {
      String message;
      if (packet.encrypted) {
         try {
            message = AES.decrypt(packet.message, AES.keyChain.get(this.playerEntity.username));
         } catch (Exception var5) {
            throw new RuntimeException(
               "This crash is caused by outdated Java, please update to 8u161 or newer! If your Java version is out of date due to a technical requirement, please add the JCE Unlimited Strength Jurisdiction Policy Files to your installation. https://www.oracle.com/java/technologies/javase-jce-all-downloads.html",
               var5
            );
         }
      } else {
         message = packet.message;
      }

      if (message.length() > 256) {
         message = message.substring(0, 255);
      }

      message = message.trim();

      for (int i = 0; i < message.length(); i++) {
         char c = message.charAt(i);
         if (ChatAllowedCharacters.ALLOWED_CHARACTERS.indexOf(c) < 0) {
            this.sendPacket(new PacketChat(String.valueOf(TextFormatting.GRAY) + TextFormatting.ITALIC + "[SERVER] Illegal characters in chat message."));
            return;
         }
      }

      if (message.startsWith("/")) {
         this.handleSlashCommand(message);
      } else {
         message = ChatEmotes.process(message);
         message = "<" + this.playerEntity.getDisplayName() + TextFormatting.RESET + "> " + TextFormatting.WHITE + message;
         LOGGER.info(message);
         this.mcServer.playerList.sendEncryptedChatToAllPlayers(message);
      }
   }

   private void handleSlashCommand(String s) {
      ServerCommandSource serverCommandSource = new ServerCommandSource(this.mcServer, this.playerEntity);

      try {
         this.playerEntity.world.getCommandManager().execute(s.substring(1), serverCommandSource);
      } catch (CommandSyntaxException var4) {
         this.playerEntity
            .playerNetServerHandler
            .sendPacket(new PacketChat(TextFormatting.RED + var4.getMessage(), AES.keyChain.get(this.playerEntity.username)));
      }
   }

   @Override
   public void handleAnimation(PacketAnimate packet) {
      if (this.playerEntity.isAlive()) {
         if (packet.animate == 1) {
            this.playerEntity.swingItem();
         }
      }
   }

   @Override
   public void handlePlayerState(PacketUpdatePlayerState updatePlayerStatePacket) {
      if (this.playerEntity.isAlive()) {
         if (updatePlayerStatePacket.state == 1) {
            this.playerEntity.setSneaking(true);
            if (this.playerEntity.vehicle != null) {
               this.playerEntity.vehicle.ejectRider();
            }
         } else if (updatePlayerStatePacket.state == 2) {
            this.playerEntity.setSneaking(false);
         } else if (updatePlayerStatePacket.state == 3) {
            this.playerEntity.wakeUpPlayer(false, true);
            this.hasMoved = false;
         }
      }
   }

   @Override
   public void handleBoatControl(PacketBoatControl boatControlPacket) {
      if (this.canInteract() && !Double.isNaN(boatControlPacket.targetXD) && !Double.isNaN(boatControlPacket.targetZD)) {
         if (this.playerEntity.vehicle != null && this.playerEntity.vehicle instanceof EntityBoat) {
            EntityBoat boat = (EntityBoat)this.playerEntity.vehicle;
            boat.handleControlDirect(boatControlPacket.targetXD, boatControlPacket.targetZD, boatControlPacket.targetYRot);
            boatControlPacket.entityId = boat.id;
            this.mcServer
               .playerList
               .sendPacketToOtherPlayersAroundPoint(this.playerEntity, boat.x, boat.y, boat.z, 128.0, boat.world.dimension.id, boatControlPacket);
         }
      }
   }

   @Override
   public void handleKickDisconnect(PacketDisconnect packet) {
      this.netManager.networkShutdown("disconnect.quitting", new Object[0]);
   }

   public int getNumChunkDataPackets() {
      return this.netManager.getNumChunkDataPackets();
   }

   @Override
   public void logInfo(String s) {
      this.sendPacket(new PacketChat("§7" + s));
   }

   @Override
   public String getUsername() {
      return this.playerEntity.username;
   }

   @Override
   public void handleUseEntity(PacketInteract interactPacket) {
      if (this.playerEntity.getGamemode().canInteract() && this.playerEntity.isAlive()) {
         WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
         Entity targetEntity = worldserver.getEntityFromId(interactPacket.targetEntityID);
         if (targetEntity != null && this.playerEntity.distanceToSqr(targetEntity) < 36.0) {
            boolean canAttack = this.playerEntity.canEntityBeSeen(targetEntity);
            if (!canAttack) {
               float f1 = MathHelper.cos(-this.playerEntity.yRot * 0.01745329F - (float) Math.PI);
               float f2 = MathHelper.sin(-this.playerEntity.yRot * 0.01745329F - (float) Math.PI);
               float f3 = -MathHelper.cos(-this.playerEntity.xRot * 0.01745329F);
               float f4 = MathHelper.sin(-this.playerEntity.xRot * 0.01745329F);
               Vec3 viewVector = Vec3.getTempVec3(f2 * f3, f4, f1 * f3);
               viewVector.x *= 8.0;
               viewVector.y *= 8.0;
               viewVector.z *= 8.0;
               viewVector.x = viewVector.x + this.playerEntity.x;
               viewVector.y = viewVector.y + this.playerEntity.y;
               viewVector.z = viewVector.z + this.playerEntity.z;
               Vec3 playerViewPos = Vec3.getTempVec3(this.playerEntity.x, this.playerEntity.y + this.playerEntity.getHeadHeight(), this.playerEntity.z);
               HitResult movingObjectPosition = targetEntity.bb.clip(playerViewPos, viewVector);
               canAttack = movingObjectPosition != null && worldserver.checkBlockCollisionBetweenPoints(playerViewPos, movingObjectPosition.location) == null;
            }

            if (canAttack) {
               if (interactPacket.action == 0) {
                  this.playerEntity.useCurrentItemOnEntity(targetEntity);
               } else if (interactPacket.action == 1) {
                  this.playerEntity.attackTargetEntityWithCurrentItem(targetEntity);
               }
            }
         }
      }
   }

   @Override
   public void handleOpenGuidebook(PacketGuidebook packet) {
      this.playerEntity.displayGUIGuidebook();
   }

   @Override
   public void handleRespawn(PacketRespawn packet) {
      if (this.playerEntity.getHealth() <= 0) {
         this.playerEntity = this.mcServer.playerList.recreatePlayerEntity(this.playerEntity, 0);
      }
   }

   @Override
   public void handleCloseWindow(PacketContainerClose containerClosePacket) {
      this.playerEntity.closeCraftingGui();
   }

   @Override
   public void handleWindowClick(PacketContainerClick containerClickPacket) {
      if (this.playerEntity.craftingInventory.containerId == containerClickPacket.window_Id && this.playerEntity.craftingInventory.isSynched(this.playerEntity)
         )
       {
         ItemStack itemstack = this.playerEntity.craftingInventory.clicked(containerClickPacket.action, containerClickPacket.args, this.playerEntity);
         if (ItemStack.areItemStacksEqual(containerClickPacket.itemStack, itemstack)) {
            this.playerEntity.playerNetServerHandler.sendPacket(new PacketContainerAck(containerClickPacket.window_Id, containerClickPacket.actionId, true));
            this.playerEntity.isChangingQuantityOnly = true;
            this.playerEntity.craftingInventory.broadcastChanges();
            this.playerEntity.updateHeldItem();
            this.playerEntity.isChangingQuantityOnly = false;
         } else {
            this.guiIdMap.put(this.playerEntity.craftingInventory.containerId, containerClickPacket.actionId);
            this.playerEntity.playerNetServerHandler.sendPacket(new PacketContainerAck(containerClickPacket.window_Id, containerClickPacket.actionId, false));
            this.playerEntity.craftingInventory.setSynched(this.playerEntity, false);
            ArrayList<ItemStack> arraylist = new ArrayList<>();

            for (int i = 0; i < this.playerEntity.craftingInventory.slots.size(); i++) {
               arraylist.add(this.playerEntity.craftingInventory.slots.get(i).getItemStack());
            }

            this.playerEntity.updateCraftingInventory(this.playerEntity.craftingInventory, arraylist);
         }
      }
   }

   @Override
   public void handleTransaction(PacketContainerAck packet) {
      Short short1 = this.guiIdMap.get(this.playerEntity.craftingInventory.containerId);
      if (short1 != null
         && packet.shortWindowId == short1
         && this.playerEntity.craftingInventory.containerId == packet.windowId
         && !this.playerEntity.craftingInventory.isSynched(this.playerEntity)) {
         this.playerEntity.craftingInventory.setSynched(this.playerEntity, true);
      }
   }

   @Override
   public void handleUpdateSign(PacketSignUpdate signUpdatePacket) {
      if (this.playerEntity.getGamemode().canInteract() && this.playerEntity.isAlive()) {
         WorldServer worldserver = this.mcServer.getDimensionWorld(this.playerEntity.dimension);
         if (worldserver.isBlockLoaded(signUpdatePacket.xPosition, signUpdatePacket.yPosition, signUpdatePacket.zPosition)) {
            TileEntity tileEntity = worldserver.getTileEntity(signUpdatePacket.xPosition, signUpdatePacket.yPosition, signUpdatePacket.zPosition);
            if (tileEntity instanceof TileEntitySign) {
               TileEntitySign sign = (TileEntitySign)tileEntity;
               if (!sign.isEditableBy(this.playerEntity)) {
                  LOGGER.warn("Player {} just tried to change non-editable sign", this.playerEntity.username);
                  return;
               }
            }

            for (int i = 0; i < 4; i++) {
               boolean isLineValid = true;
               if (signUpdatePacket.signLines[i].length() > 15) {
                  isLineValid = false;
               } else {
                  signUpdatePacket.signLines[i] = signUpdatePacket.signLines[i].replaceAll("§", "$");

                  for (int l = 0; l < signUpdatePacket.signLines[i].length(); l++) {
                     if (ChatAllowedCharacters.ALLOWED_CHARACTERS.indexOf(signUpdatePacket.signLines[i].charAt(l)) < 0) {
                        isLineValid = false;
                        break;
                     }
                  }
               }

               if (!isLineValid) {
                  signUpdatePacket.signLines[i] = "!?";
               }
            }

            if (tileEntity instanceof TileEntitySign) {
               int x = signUpdatePacket.xPosition;
               int y = signUpdatePacket.yPosition;
               int z = signUpdatePacket.zPosition;
               TileEntitySign sign = (TileEntitySign)tileEntity;
               System.arraycopy(signUpdatePacket.signLines, 0, sign.signText, 0, 4);
               sign.setColor(TextFormatting.FORMATTINGS[signUpdatePacket.color]);
               sign.setPicture(EnumSignPicture.values()[signUpdatePacket.picture]);
               sign.setChanged();
               worldserver.markBlockNeedsUpdate(x, y, z);
            }
         }
      }
   }

   @Override
   public void handleUpdateCreativeInventory(PacketUpdateCreativeInventory packet) {
      if (this.playerEntity.craftingInventory.containerId == packet.windowId && this.playerEntity.craftingInventory instanceof MenuInventoryCreative) {
         ((MenuInventoryCreative)this.playerEntity.craftingInventory).setInventoryStatus(packet.page, packet.searchText);
      }
   }

   @Override
   public void handleSetHotbarOffset(PacketSetHotbarOffset packet) {
      this.playerEntity.inventory.setHotbarOffset(packet.hotbarOffset, false);
      if (packet.hotbarOffset != this.playerEntity.inventory.getHotbarOffset()) {
         this.sendPacket(new PacketSetHotbarOffset(packet.hotbarOffset));
      }
   }

   @Override
   public void handleItemName(PacketSetItemName packet) {
      String name = packet.name;
      if (name.length() > 16) {
         name = packet.name.substring(0, 16);
      }

      this.playerEntity.inventory.getItem(packet.slot).setCustomName(name);
   }

   @Override
   public void handleSetPaintingMotive(PacketSetPaintingArt setPaintingArtPacket) {
      this.playerEntity.setSelectedArt(ArtType.values.get(setPaintingArtPacket.motive));
   }

   @Override
   public void handleCustomPayload(PacketCustomPayload customPayloadPacket) {
      if ("BTA:RotationLock".equals(customPayloadPacket.channel)) {
         if (customPayloadPacket.data.length == 4) {
            this.playerEntity.rotationLock = Direction.getDirectionById(customPayloadPacket.data[0]);
            this.playerEntity.rotationLockHorizontal = Direction.getDirectionById(customPayloadPacket.data[1]);
            this.playerEntity.rotationLockVertical = Direction.getDirectionById(customPayloadPacket.data[2]);
            this.playerEntity.placementModeOverride = PlacementMode.get(customPayloadPacket.data[3]);
         }
      } else if ("BTA:Flag".equals(customPayloadPacket.channel)) {
         if (customPayloadPacket.data.length == 385 && this.playerEntity.craftingInventory instanceof MenuFlag) {
            MenuFlag menuFlag = (MenuFlag)this.playerEntity.craftingInventory;
            TileEntityFlag flag = menuFlag.flag;
            if (flag.owner != null && !flag.owner.equals(this.playerEntity.uuid)) {
               LOGGER.warn("Player '{}' tried editing a flag that belongs to '{}'!", this.playerEntity.username, flag.owner);
               this.playerEntity.world.markBlockNeedsUpdate(flag.x, flag.y, flag.z);
            } else {
               flag.owner = this.playerEntity.uuid;
               byte header = customPayloadPacket.data[0];
               boolean flipped = (header & 1) != 0;
               System.arraycopy(customPayloadPacket.data, 1, flag.flagColors, 0, 384);
               flag.setFlipped(flipped);
               this.playerEntity.world.markBlockNeedsUpdate(flag.x, flag.y, flag.z);
            }
         }
      } else if ("BTA:WandMonster".equals(customPayloadPacket.channel)
         && this.playerEntity.getHeldItem() != null
         && this.playerEntity.getHeldItem().getItem() == Items.WAND_MONSTER_SPAWNER) {
         String id = new String(customPayloadPacket.data, StandardCharsets.UTF_8);
         this.playerEntity.getHeldItem().getData().putString("monster", id);
      }
   }

   @Override
   public void handleRequestCommandManagerPacket(PacketRequestCommandManager packet) {
      this.sendPacket(
         new PacketCommandManager(
            this.mcServer.getDimensionWorld(this.playerEntity.dimension).getCommandManager().getDispatcher(),
            new ServerCommandSource(this.mcServer, this.playerEntity),
            packet.text,
            packet.cursor
         )
      );
   }

   @Override
   public boolean isServerHandler() {
      return true;
   }
}
