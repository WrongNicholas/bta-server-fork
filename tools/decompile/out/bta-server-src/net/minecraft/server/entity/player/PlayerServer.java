package net.minecraft.server.entity.player;

import com.mojang.nbt.tags.CompoundTag;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.achievement.stat.Stat;
import net.minecraft.core.block.BlockLogicPortal;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.entity.TileEntityDispenser;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.block.entity.TileEntityFurnaceBlast;
import net.minecraft.core.block.entity.TileEntityTrommel;
import net.minecraft.core.crafting.ContainerListener;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.enums.EnumSleepStatus;
import net.minecraft.core.item.IComplexItem;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketAnimate;
import net.minecraft.core.net.packet.PacketBlockRegionUpdate;
import net.minecraft.core.net.packet.PacketChat;
import net.minecraft.core.net.packet.PacketContainerClose;
import net.minecraft.core.net.packet.PacketContainerOpen;
import net.minecraft.core.net.packet.PacketContainerSetContent;
import net.minecraft.core.net.packet.PacketContainerSetData;
import net.minecraft.core.net.packet.PacketContainerSetSlot;
import net.minecraft.core.net.packet.PacketEntityFling;
import net.minecraft.core.net.packet.PacketFlagOpen;
import net.minecraft.core.net.packet.PacketMovePlayer;
import net.minecraft.core.net.packet.PacketPlayerGamemode;
import net.minecraft.core.net.packet.PacketSetCarriedItem;
import net.minecraft.core.net.packet.PacketSetEquippedItem;
import net.minecraft.core.net.packet.PacketSetHealth;
import net.minecraft.core.net.packet.PacketSetHeldObject;
import net.minecraft.core.net.packet.PacketSetHotbarOffset;
import net.minecraft.core.net.packet.PacketSetRiding;
import net.minecraft.core.net.packet.PacketSleep;
import net.minecraft.core.net.packet.PacketStatistic;
import net.minecraft.core.net.packet.PacketTakeItemEntity;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.menu.MenuActivator;
import net.minecraft.core.player.inventory.menu.MenuContainer;
import net.minecraft.core.player.inventory.menu.MenuCrafting;
import net.minecraft.core.player.inventory.menu.MenuFlag;
import net.minecraft.core.player.inventory.menu.MenuFurnace;
import net.minecraft.core.player.inventory.menu.MenuGuidebook;
import net.minecraft.core.player.inventory.menu.MenuInventory;
import net.minecraft.core.player.inventory.menu.MenuTrap;
import net.minecraft.core.player.inventory.menu.MenuTrommel;
import net.minecraft.core.player.inventory.slot.SlotResult;
import net.minecraft.core.util.helper.AES;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.ICarriable;
import net.minecraft.core.world.IVehicle;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.EntityTrackerImpl;
import net.minecraft.server.net.handler.PacketHandlerServer;
import net.minecraft.server.world.ServerPlayerController;
import net.minecraft.server.world.WorldServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerServer extends Player implements ContainerListener {
   public PacketHandlerServer playerNetServerHandler;
   public MinecraftServer mcServer;
   public ServerPlayerController playerController;
   public double viewingX;
   public double viewingZ;
   public List<ChunkCoordinate> loadedChunks;
   public Set<ChunkCoordinate> field_420_ah;
   private int lastHealth;
   private int lastScore;
   private int ticksOfInvuln;
   private ItemStack[] playerInventory = new ItemStack[]{null, null, null, null, null};
   private int currentWindowId;
   public boolean isChangingQuantityOnly;

   public PlayerServer(MinecraftServer minecraftserver, World world, String username, UUID uuid, ServerPlayerController serverPlayerController) {
      super(world);
      this.loadedChunks = new LinkedList<>();
      this.field_420_ah = new HashSet<>();
      this.lastHealth = -99999999;
      this.lastScore = -99999999;
      this.ticksOfInvuln = 60;
      this.currentWindowId = 0;
      serverPlayerController.player = this;
      this.playerController = serverPlayerController;
      ChunkCoordinates chunkcoordinates = world.getSpawnPoint();
      int i = chunkcoordinates.x;
      int j = chunkcoordinates.z;
      int k = chunkcoordinates.y;
      if (!world.worldType.hasCeiling()) {
         i += this.random.nextInt(20) - 10;
         k = world.findTopSolidBlock(i, j);
         j += this.random.nextInt(20) - 10;
      }

      this.moveTo(i + 0.5, k, j + 0.5, 0.0F, 0.0F);
      this.mcServer = minecraftserver;
      this.footSize = 0.0F;
      this.username = username;
      this.uuid = uuid;
      this.heightOffset = 0.0F;
      this.gamemode = minecraftserver.defaultGamemode;
   }

   @Override
   public void moveEntityWithHeading(float moveStrafing, float moveForward) {
      super.moveEntityWithHeading(moveStrafing, moveForward);
      if (this.noPhysics) {
         this.yd = 0.0;
         this.onGround = true;
         this.fallDistance = 0.0F;
         if (this.isSneaking()) {
            this.yd = -0.4;
         }
      }
   }

   @Override
   public void setGamemode(Gamemode gamemode) {
      this.mcServer.playerList.sendPacketToAllPlayers(new PacketPlayerGamemode(this.id, gamemode.getId()));
      this.gamemode = gamemode;
      MenuInventory newContainer = gamemode.getContainer(this.inventory, !this.world.isClientSide);
      if (this.craftingInventory == this.inventorySlots) {
         this.craftingInventory = newContainer;
      }

      this.inventorySlots = newContainer;
      this.inventorySlots.addSlotListener(this);
      if (!gamemode.canPlayerFly()) {
         this.noPhysics = false;
      }

      this.fireImmune = gamemode.isImmuneToFire();
   }

   @Override
   public AABB getBb() {
      return this.noPhysics ? null : super.getBb();
   }

   @Override
   public void awardKillScore(Entity entity, int i) {
      super.awardKillScore(entity, i);
      this.mcServer.playerList.updatePlayerProfile(this.username, this.nickname, this.uuid, this.score, this.chatColor, true, this.isOperator());
   }

   public boolean isOperator() {
      return this.mcServer.playerList.isOp(this.uuid);
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.putInt("Score", this.score);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.score = tag.getInteger("Score");
   }

   @Override
   public void setWorld(World world) {
      super.setWorld(world);
      this.playerController = new ServerPlayerController((WorldServer)world);
      this.playerController.player = this;
   }

   @Override
   public void animate4() {
   }

   public void setupCraftingInventoryListener() {
      this.craftingInventory.addSlotListener(this);
   }

   @Nullable
   @Override
   public ItemStack[] getInventory() {
      return this.playerInventory;
   }

   @Override
   protected void resetHeight() {
      this.heightOffset = 0.0F;
   }

   @Override
   public float getHeadHeight() {
      return this.getHeightOffset();
   }

   @Override
   public void tick() {
      this.playerController.tick();
      this.ticksOfInvuln--;
      this.craftingInventory.broadcastChanges();

      for (int i = 0; i < 5; i++) {
         ItemStack itemstack = this.getEquipmentInSlot(i);
         if (itemstack != this.playerInventory[i]) {
            this.mcServer.getEntityTracker(this.dimension).sendPacketToTrackedPlayers(this, new PacketSetEquippedItem(this.id, i, itemstack));
            this.playerInventory[i] = itemstack;
         }
      }
   }

   @NotNull
   @Override
   public String getDisplayName() {
      String name = this.nickname;
      if (name.isEmpty()) {
         name = this.username;
      } else {
         List<Player> players = new ArrayList<>(MinecraftServer.getInstance().playerList.playerEntities);
         players.sort(new Player.SortByUsername());
         int nickNum = 0;

         for (Player player : players) {
            if (player == this) {
               break;
            }

            if (player.nickname.equals(this.nickname)) {
               nickNum++;
            }
         }

         name = TextFormatting.ITALIC + name;
         if (nickNum > 0) {
            name = name + " (" + nickNum + ")";
         }
      }

      return TextFormatting.get(this.chatColor) + name;
   }

   public ItemStack getEquipmentInSlot(int i) {
      return i == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[i - 1];
   }

   @Override
   public boolean hurt(Entity attacker, int i, DamageType type) {
      if (this.ticksOfInvuln > 0) {
         return false;
      } else {
         if (!this.mcServer.pvpOn) {
            if (attacker instanceof Player) {
               return false;
            }

            if (attacker instanceof ProjectileArrow) {
               ProjectileArrow entityarrow = (ProjectileArrow)attacker;
               if (entityarrow.owner instanceof Player) {
                  return false;
               }
            }
         }

         return super.hurt(attacker, i, type);
      }
   }

   protected boolean isPVPEnabled() {
      return this.mcServer.pvpOn;
   }

   @Override
   public void heal(int i) {
      super.heal(i);
   }

   public void onUpdateEntity() {
      super.tick();

      for (int i = 0; i < this.inventory.getContainerSize(); i++) {
         ItemStack itemstack = this.inventory.getItem(i);
         if (itemstack != null && Item.itemsList[itemstack.itemID] instanceof IComplexItem && this.playerNetServerHandler.getNumChunkDataPackets() <= 2) {
            Packet packet = ((IComplexItem)Item.itemsList[itemstack.itemID]).sendPacketData(itemstack, this.world, this);
            if (packet != null) {
               this.playerNetServerHandler.sendPacket(packet);
            }
         }
      }

      if (this.inPortal && !this.noPhysics) {
         Dimension targetDim = ((BlockLogicPortal)Blocks.blocksList[this.portalID].getLogic()).targetDimension;
         boolean netherAllowed = this.mcServer.propertyManager.getBooleanProperty("allow-nether", true);
         boolean paradiseAllowed = this.mcServer.propertyManager.getBooleanProperty("allow-paradise", false);
         if (netherAllowed && targetDim == Dimension.NETHER
            || paradiseAllowed && targetDim == Dimension.PARADISE
            || targetDim != Dimension.NETHER && targetDim != Dimension.PARADISE) {
            if (this.craftingInventory != this.inventorySlots) {
               this.usePersonalCraftingInventory();
            }

            if (this.vehicle != null) {
               this.startRiding(this.vehicle);
            } else {
               this.timeInPortal += 0.0125F;
               if (this.timeInPortal >= 1.0F || this.getGamemode().instantPortalTravel()) {
                  this.timeInPortal = 1.0F;
                  this.timeUntilPortal = 10;
                  if (this.dimension == targetDim.id) {
                     this.mcServer.playerList.sendPlayerToOtherDimension(this, 0, this.portalColor, true);
                  } else {
                     this.mcServer.playerList.sendPlayerToOtherDimension(this, targetDim.id, this.portalColor, true);
                  }
               }
            }

            this.inPortal = false;
         }
      } else {
         if (this.timeInPortal > 0.0F) {
            this.timeInPortal -= 0.05F;
         }

         if (this.timeInPortal < 0.0F) {
            this.timeInPortal = 0.0F;
         }
      }

      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal--;
      }

      if (this.getHealth() != this.lastHealth) {
         this.playerNetServerHandler.sendPacket(new PacketSetHealth(this.getHealth()));
         this.lastHealth = this.getHealth();
      }

      if (this.score != this.lastScore) {
         this.mcServer.playerList.updatePlayerProfile(this.username, this.nickname, this.uuid, this.score, this.chatColor, true, this.isOperator());
         this.lastScore = this.score;
      }
   }

   public void tickSendChunks() {
      if (!this.loadedChunks.isEmpty()) {
         ChunkCoordinate chunkCoord = this.loadedChunks.get(0);
         if (chunkCoord != null) {
            boolean canSendMoreDataPackets = this.playerNetServerHandler.getNumChunkDataPackets() < 8;
            if (canSendMoreDataPackets) {
               WorldServer worldserver = this.mcServer.getDimensionWorld(this.dimension);
               this.loadedChunks.remove(chunkCoord);
               this.playerNetServerHandler.sendPacket(new PacketBlockRegionUpdate(chunkCoord.x * 16, 0, chunkCoord.z * 16, 16, 256, 16, worldserver));
               List<TileEntity> list = worldserver.getBlockEntitiesWithinBounds(
                  chunkCoord.x * 16, 0, chunkCoord.z * 16, chunkCoord.x * 16 + 16, 256, chunkCoord.z * 16 + 16
               );

               for (int j = 0; j < list.size(); j++) {
                  this.getTileEntityInfo(list.get(j));
               }
            }
         }
      }
   }

   private void getTileEntityInfo(TileEntity tileentity) {
      if (tileentity != null) {
         Packet packet = tileentity.getDescriptionPacket();
         if (packet != null) {
            this.playerNetServerHandler.sendPacket(packet);
         }
      }
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.tickCount % 10 == 0) {
         Item arrow = this.getNextArrow();
         this.entityData.set(18, arrow == null ? -1 : arrow.id);
      }
   }

   @Override
   public void onItemPickup(Entity entity, ItemStack item) {
      if (!entity.removed) {
         EntityTrackerImpl entitytracker = this.mcServer.getEntityTracker(this.dimension);
         if (entity instanceof EntityItem) {
            entitytracker.sendPacketToTrackedPlayers(entity, new PacketTakeItemEntity(entity.id, this.id));
         }

         if (entity instanceof ProjectileArrow) {
            entitytracker.sendPacketToTrackedPlayers(entity, new PacketTakeItemEntity(entity.id, this.id));
         }
      }

      super.onItemPickup(entity, item);
      this.craftingInventory.broadcastChanges();
   }

   @Override
   public void swingItem() {
      if (!this.isSwinging) {
         this.swingProgressInt = -1;
         this.isSwinging = true;
         EntityTrackerImpl entitytracker = this.mcServer.getEntityTracker(this.dimension);
         entitytracker.sendPacketToTrackedPlayers(this, new PacketAnimate(this, 1));
      }
   }

   public void func_22068_s() {
   }

   @Override
   public EnumSleepStatus sleepInBedAt(int x, int y, int z) {
      EnumSleepStatus status = super.sleepInBedAt(x, y, z);
      if (status == EnumSleepStatus.OK) {
         EntityTrackerImpl entitytracker = this.mcServer.getEntityTracker(this.dimension);
         PacketSleep sleepPacket = new PacketSleep(this, 0, x, y, z);
         entitytracker.sendPacketToTrackedPlayers(this, sleepPacket);
         this.playerNetServerHandler.teleportAndRotate(this.x, this.y, this.z, this.yRot, this.xRot);
         this.playerNetServerHandler.sendPacket(sleepPacket);
      }

      return status;
   }

   @Override
   public void wakeUpPlayer(boolean flag, boolean flag1) {
      if (this.isPlayerSleeping()) {
         EntityTrackerImpl entitytracker = this.mcServer.getEntityTracker(this.dimension);
         entitytracker.sendPacketToTrackedPlayersAndTrackedEntity(this, new PacketAnimate(this, 3));
         super.wakeUpPlayer(flag, flag1);
      }

      if (this.playerNetServerHandler != null) {
         this.playerNetServerHandler.teleportAndRotate(this.x, this.y, this.z, this.yRot, this.xRot);
      }
   }

   @Override
   public void startRiding(IVehicle vehicle) {
      if (this.canRide()) {
         if (this.vehicle != vehicle) {
            super.startRiding(vehicle);
            if (vehicle instanceof Entity) {
               this.playerNetServerHandler.sendPacket(new PacketSetRiding(this, (Entity)this.vehicle));
            } else if (vehicle instanceof TileEntity) {
               TileEntity tileEntity = (TileEntity)vehicle;
               this.playerNetServerHandler.sendPacket(new PacketSetRiding(this, tileEntity.x, tileEntity.y, tileEntity.z));
            }

            this.playerNetServerHandler.teleport(this.x, this.y, this.z);
         }
      }
   }

   @Override
   protected void checkFallDamage(double yd, boolean onGround) {
   }

   public void handleFalling(double dy, boolean onGround) {
      super.checkFallDamage(dy, onGround);
   }

   private void getNextWindowId() {
      this.currentWindowId = this.currentWindowId % 100 + 1;
   }

   @Override
   public void displayWorkbenchScreen(int x, int y, int z) {
      this.getNextWindowId();
      this.playerNetServerHandler.sendPacket(new PacketContainerOpen(this.currentWindowId, 1, "Crafting", 9));
      this.craftingInventory.onCraftGuiClosed(this);
      this.craftingInventory = new MenuCrafting(this.inventory, this.world, x, y, z);
      this.craftingInventory.containerId = this.currentWindowId;
      this.craftingInventory.addSlotListener(this);
   }

   @Override
   public void displayPaintingPickerScreen() {
      this.playerNetServerHandler.sendPacket(new PacketContainerOpen(this.currentWindowId, 7, "Painting", 0));
   }

   @Override
   public void displayContainerScreen(Container container) {
      this.getNextWindowId();
      this.playerNetServerHandler.sendPacket(new PacketContainerOpen(this.currentWindowId, 0, container.getNameTranslationKey(), container.getContainerSize()));
      this.craftingInventory.onCraftGuiClosed(this);
      this.craftingInventory = new MenuContainer(this.inventory, container);
      this.craftingInventory.containerId = this.currentWindowId;
      this.craftingInventory.addSlotListener(this);
   }

   @Override
   public void displayChestScreen(Container container, double x, double y, double z) {
      PacketHandlerServer.LOGGER.info("{} interacted with chest at ({}, {}, {})", this.username, x, y, z);
      this.displayContainerScreen(container);
   }

   @Override
   public void displayFurnaceScreen(TileEntityFurnace tileEntity) {
      this.getNextWindowId();
      if (tileEntity instanceof TileEntityFurnaceBlast) {
         this.playerNetServerHandler
            .sendPacket(new PacketContainerOpen(this.currentWindowId, 4, tileEntity.getNameTranslationKey(), tileEntity.getContainerSize()));
      } else {
         this.playerNetServerHandler
            .sendPacket(new PacketContainerOpen(this.currentWindowId, 2, tileEntity.getNameTranslationKey(), tileEntity.getContainerSize()));
      }

      this.craftingInventory.onCraftGuiClosed(this);
      this.craftingInventory = new MenuFurnace(this.inventory, tileEntity);
      this.craftingInventory.containerId = this.currentWindowId;
      this.craftingInventory.addSlotListener(this);
   }

   @Override
   public void displayDispenserScreen(TileEntityDispenser tileEntity) {
      this.getNextWindowId();
      this.playerNetServerHandler
         .sendPacket(new PacketContainerOpen(this.currentWindowId, 3, tileEntity.getNameTranslationKey(), tileEntity.getContainerSize()));
      this.craftingInventory.onCraftGuiClosed(this);
      this.craftingInventory = new MenuTrap(this.inventory, tileEntity);
      this.craftingInventory.containerId = this.currentWindowId;
      this.craftingInventory.addSlotListener(this);
   }

   @Override
   public void displayActivatorScreen(TileEntityActivator tileEntity) {
      this.getNextWindowId();
      this.playerNetServerHandler
         .sendPacket(new PacketContainerOpen(this.currentWindowId, 6, tileEntity.getNameTranslationKey(), tileEntity.getContainerSize()));
      this.craftingInventory.onCraftGuiClosed(this);
      this.craftingInventory = new MenuActivator(this.inventory, tileEntity);
      this.craftingInventory.containerId = this.currentWindowId;
      this.craftingInventory.addSlotListener(this);
   }

   @Override
   public void displayTrommelScreen(TileEntityTrommel tileEntity) {
      this.getNextWindowId();
      this.playerNetServerHandler
         .sendPacket(new PacketContainerOpen(this.currentWindowId, 5, tileEntity.getNameTranslationKey(), tileEntity.getContainerSize()));
      this.craftingInventory.onCraftGuiClosed(this);
      this.craftingInventory = new MenuTrommel(this.inventory, tileEntity);
      this.craftingInventory.containerId = this.currentWindowId;
      this.craftingInventory.addSlotListener(this);
   }

   @Override
   public void displayFlagEditorScreen(TileEntityFlag tileEntity) {
      this.getNextWindowId();
      this.playerNetServerHandler.sendPacket(new PacketFlagOpen(this.currentWindowId, tileEntity.x, tileEntity.y, tileEntity.z));
      this.craftingInventory.onCraftGuiClosed(this);
      this.craftingInventory = new MenuFlag(this.inventory, tileEntity);
      this.craftingInventory.containerId = this.currentWindowId;
      this.craftingInventory.addSlotListener(this);
   }

   public void displayGUIGuidebook() {
      this.getNextWindowId();
      this.craftingInventory.onCraftGuiClosed(this);
      this.craftingInventory = new MenuGuidebook();
      this.craftingInventory.containerId = this.currentWindowId;
      this.craftingInventory.addSlotListener(this);
   }

   @Override
   public void updateInventorySlot(MenuAbstract container, int i, ItemStack itemstack) {
      if (!(container.getSlot(i) instanceof SlotResult)) {
         if (!this.isChangingQuantityOnly) {
            if (this.playerNetServerHandler != null) {
               this.playerNetServerHandler.sendPacket(new PacketContainerSetSlot(container.containerId, i, itemstack));
            }
         }
      }
   }

   public void initializeCraftingInventory(MenuAbstract container) {
      this.updateCraftingInventory(container, container.getSlotStackList());
   }

   @Override
   public void updateCraftingInventory(MenuAbstract container, List<ItemStack> list) {
      if (this.playerNetServerHandler != null) {
         this.playerNetServerHandler.sendPacket(new PacketContainerSetContent(container.containerId, list));
      }
   }

   @Override
   public void updateCraftingInventoryInfo(MenuAbstract container, int id, int value) {
      this.playerNetServerHandler.sendPacket(new PacketContainerSetData(container.containerId, id, value));
   }

   @Override
   public void onItemStackChanged(ItemStack itemstack) {
   }

   public void usePersonalCraftingInventory() {
      this.playerNetServerHandler.sendPacket(new PacketContainerClose(this.craftingInventory.containerId));
      this.closeCraftingGui();
   }

   public void updateHeldItem() {
      if (!this.isChangingQuantityOnly) {
         this.playerNetServerHandler.sendPacket(new PacketContainerSetSlot(-1, -1, this.inventory.getHeldItemStack()));
      }
   }

   public void closeCraftingGui() {
      this.craftingInventory.onCraftGuiClosed(this);
      this.craftingInventory = this.inventorySlots;
   }

   public void setMovementType(float f, float f1, boolean flag, boolean flag1, float f2, float f3) {
      this.moveStrafing = f;
      this.moveForward = f1;
      this.isJumping = flag;
      this.setSneaking(flag1);
      this.xRot = f2;
      this.yRot = f3;
   }

   @Override
   public void addStat(Stat stat, int i) {
      if (stat != null) {
         if (!stat.clientside) {
            while (i > 100) {
               this.playerNetServerHandler.sendPacket(new PacketStatistic(stat.statId, 100));
               i -= 100;
            }

            if (i > 0) {
               this.playerNetServerHandler.sendPacket(new PacketStatistic(stat.statId, i));
            }
         }
      }
   }

   public void func_30002_A() {
      if (this.vehicle != null) {
         this.startRiding(this.vehicle);
      }

      if (this.passenger != null) {
         this.passenger.startRiding(this);
      }

      if (this.sleeping) {
         this.wakeUpPlayer(true, false);
      }
   }

   public void initializeLastFieldValues() {
      this.lastHealth = Integer.MIN_VALUE;
      this.lastScore = Integer.MIN_VALUE;
   }

   public void func_22061_a(String s) {
      I18n stringtranslate = I18n.getInstance();
      String s1 = stringtranslate.translateKey(s);
      this.playerNetServerHandler.sendPacket(new PacketChat(s1));
   }

   @Override
   public void setCurrentItem(int i) {
      super.setCurrentItem(i);
      this.playerNetServerHandler.sendPacket(new PacketSetCarriedItem(i));
   }

   @Override
   public void fling(double xd, double yd, double zd, float pushTime) {
      super.fling(xd, yd, zd, pushTime);
      this.playerNetServerHandler.sendPacket(new PacketMovePlayer.PosRot(this.x, this.y + 1.625, this.z, this.yRot, this.xRot, false));
      this.playerNetServerHandler.sendPacket(new PacketEntityFling(this.id, this.xd, this.yd, this.zd, this.pushTime, this.pushesThisTick));
   }

   @Override
   public void setHeldObject(@Nullable ICarriable heldObject) {
      super.setHeldObject(heldObject);
      PacketSetHeldObject object = new PacketSetHeldObject(this.id, heldObject);
      this.playerNetServerHandler.sendPacket(object);
      this.mcServer.getEntityTracker(this.dimension).sendPacketToTrackedPlayers(this, object);
      this.playerNetServerHandler.sendPacket(new PacketSetHotbarOffset(this.inventory.getHotbarOffset()));
      this.playerNetServerHandler.sendPacket(new PacketSetCarriedItem(this.inventory.getCurrentItemIndex()));
   }

   @Override
   public void sendMessage(String message) {
      this.playerNetServerHandler.sendPacket(new PacketChat(message, 0, AES.keyChain.get(this.username)));
   }

   @Override
   public void sendStatusMessage(String message) {
      this.playerNetServerHandler.sendPacket(new PacketChat(message, 1, AES.keyChain.get(this.username)));
   }

   public void teleport(double x, double y, double z, float yaw, float pitch) {
      this.playerNetServerHandler.teleportAndRotate(x, y, z, yaw, pitch);
   }

   @Override
   public boolean deferVehicleBehavior() {
      return true;
   }
}
