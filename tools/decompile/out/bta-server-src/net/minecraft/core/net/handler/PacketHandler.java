package net.minecraft.core.net.handler;

import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketAESSendKey;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.net.packet.PacketAddItemEntity;
import net.minecraft.core.net.packet.PacketAddMob;
import net.minecraft.core.net.packet.PacketAddPainting;
import net.minecraft.core.net.packet.PacketAddParticle;
import net.minecraft.core.net.packet.PacketAddPlayer;
import net.minecraft.core.net.packet.PacketAnimate;
import net.minecraft.core.net.packet.PacketBedMessage;
import net.minecraft.core.net.packet.PacketBlockEvent;
import net.minecraft.core.net.packet.PacketBlockRegionUpdate;
import net.minecraft.core.net.packet.PacketBlockUpdate;
import net.minecraft.core.net.packet.PacketBoatControl;
import net.minecraft.core.net.packet.PacketChat;
import net.minecraft.core.net.packet.PacketChunkBlocksUpdate;
import net.minecraft.core.net.packet.PacketChunkVisibility;
import net.minecraft.core.net.packet.PacketCommandManager;
import net.minecraft.core.net.packet.PacketContainerAck;
import net.minecraft.core.net.packet.PacketContainerClick;
import net.minecraft.core.net.packet.PacketContainerClose;
import net.minecraft.core.net.packet.PacketContainerOpen;
import net.minecraft.core.net.packet.PacketContainerSetContent;
import net.minecraft.core.net.packet.PacketContainerSetData;
import net.minecraft.core.net.packet.PacketContainerSetSlot;
import net.minecraft.core.net.packet.PacketCustomPayload;
import net.minecraft.core.net.packet.PacketDisconnect;
import net.minecraft.core.net.packet.PacketEntityEvent;
import net.minecraft.core.net.packet.PacketEntityFling;
import net.minecraft.core.net.packet.PacketEntityNickname;
import net.minecraft.core.net.packet.PacketEntityTagData;
import net.minecraft.core.net.packet.PacketExplosion;
import net.minecraft.core.net.packet.PacketFlagOpen;
import net.minecraft.core.net.packet.PacketGameRule;
import net.minecraft.core.net.packet.PacketGuidebook;
import net.minecraft.core.net.packet.PacketInteract;
import net.minecraft.core.net.packet.PacketLogin;
import net.minecraft.core.net.packet.PacketMapData;
import net.minecraft.core.net.packet.PacketMoveEntity;
import net.minecraft.core.net.packet.PacketMovePlayer;
import net.minecraft.core.net.packet.PacketPhotoMode;
import net.minecraft.core.net.packet.PacketPingHandshake;
import net.minecraft.core.net.packet.PacketPlaySoundEffect;
import net.minecraft.core.net.packet.PacketPlaySoundEffectDirect;
import net.minecraft.core.net.packet.PacketPlayerAction;
import net.minecraft.core.net.packet.PacketPlayerGamemode;
import net.minecraft.core.net.packet.PacketPlayerList;
import net.minecraft.core.net.packet.PacketPreLogin;
import net.minecraft.core.net.packet.PacketRecipeSync;
import net.minecraft.core.net.packet.PacketRemoveEntity;
import net.minecraft.core.net.packet.PacketRequestCommandManager;
import net.minecraft.core.net.packet.PacketRespawn;
import net.minecraft.core.net.packet.PacketSetCarriedItem;
import net.minecraft.core.net.packet.PacketSetEntityData;
import net.minecraft.core.net.packet.PacketSetEntityMotion;
import net.minecraft.core.net.packet.PacketSetEquippedItem;
import net.minecraft.core.net.packet.PacketSetHealth;
import net.minecraft.core.net.packet.PacketSetHeldObject;
import net.minecraft.core.net.packet.PacketSetHotbarOffset;
import net.minecraft.core.net.packet.PacketSetItemName;
import net.minecraft.core.net.packet.PacketSetMobSpawner;
import net.minecraft.core.net.packet.PacketSetPaintingArt;
import net.minecraft.core.net.packet.PacketSetRiding;
import net.minecraft.core.net.packet.PacketSetSpawnPosition;
import net.minecraft.core.net.packet.PacketSetTime;
import net.minecraft.core.net.packet.PacketSignUpdate;
import net.minecraft.core.net.packet.PacketSleep;
import net.minecraft.core.net.packet.PacketStatistic;
import net.minecraft.core.net.packet.PacketSyncIDs;
import net.minecraft.core.net.packet.PacketTakeItemEntity;
import net.minecraft.core.net.packet.PacketTeleportEntity;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.net.packet.PacketUpdateCreativeInventory;
import net.minecraft.core.net.packet.PacketUpdatePlayerProfile;
import net.minecraft.core.net.packet.PacketUpdatePlayerState;
import net.minecraft.core.net.packet.PacketUseOrPlaceItemStack;
import net.minecraft.core.net.packet.PacketWeatherEffect;
import net.minecraft.core.net.packet.PacketWeatherStatus;

public abstract class PacketHandler {
   public abstract boolean isServerHandler();

   public void handleMapChunk(PacketBlockRegionUpdate blockRegionUpdatePacket) {
   }

   public void handleInvalidPacket(Packet packet) {
   }

   public void handleErrorMessage(String message, Object[] objects) {
   }

   public void handleKickDisconnect(PacketDisconnect packet255KickdisconnectPacket) {
      this.handleInvalidPacket(packet255KickdisconnectPacket);
   }

   public void handleLogin(PacketLogin loginPacket) {
      this.handleInvalidPacket(loginPacket);
   }

   public void handleFlying(PacketMovePlayer movePlayerPacket) {
      this.handleInvalidPacket(movePlayerPacket);
   }

   public void handleMultiBlockChange(PacketChunkBlocksUpdate chunkBlocksUpdatePacket) {
      this.handleInvalidPacket(chunkBlocksUpdatePacket);
   }

   public void handleBlockDig(PacketPlayerAction playerActionPacket) {
      this.handleInvalidPacket(playerActionPacket);
   }

   public void handleBlockUpdate(PacketBlockUpdate blockUpdatePacket) {
      this.handleInvalidPacket(blockUpdatePacket);
   }

   public void handlePreChunk(PacketChunkVisibility chunkVisibilityPacket) {
      this.handleInvalidPacket(chunkVisibilityPacket);
   }

   public void handleNamedEntitySpawn(PacketAddPlayer addPlayerPacket) {
      this.handleInvalidPacket(addPlayerPacket);
   }

   public void handleEntity(PacketMoveEntity moveEntityPacket) {
      this.handleInvalidPacket(moveEntityPacket);
   }

   public void handleEntityTeleport(PacketTeleportEntity teleportEntityPacket) {
      this.handleInvalidPacket(teleportEntityPacket);
   }

   public void handleEntityFling(PacketEntityFling packetEntityFling) {
      this.handleInvalidPacket(packetEntityFling);
   }

   public void handlePlace(PacketUseOrPlaceItemStack useItemPacket) {
      this.handleInvalidPacket(useItemPacket);
   }

   public void handleBlockItemSwitch(PacketSetCarriedItem setCarriedItemPacket) {
      this.handleInvalidPacket(setCarriedItemPacket);
   }

   public void handleDestroyEntity(PacketRemoveEntity removeEntityPacket) {
      this.handleInvalidPacket(removeEntityPacket);
   }

   public void handleSetPaintingMotive(PacketSetPaintingArt setPaintingArtPacket) {
      this.handleInvalidPacket(setPaintingArtPacket);
   }

   public void handlePickupSpawn(PacketAddItemEntity addItemEntityPacket) {
      this.handleInvalidPacket(addItemEntityPacket);
   }

   public void handleCollect(PacketTakeItemEntity takeItemEntityPacket) {
      this.handleInvalidPacket(takeItemEntityPacket);
   }

   public void handleChat(PacketChat chatPacket) {
      this.handleInvalidPacket(chatPacket);
   }

   public void handleVehicleSpawn(PacketAddEntity addEntityPacket) {
      this.handleInvalidPacket(addEntityPacket);
   }

   public void handleAnimation(PacketAnimate animatePacket) {
      this.handleInvalidPacket(animatePacket);
   }

   public void handlePlayerState(PacketUpdatePlayerState updatePlayerStatePacket) {
      this.handleInvalidPacket(updatePlayerStatePacket);
   }

   public void handleHandshake(PacketPreLogin preLoginPacket) {
      this.handleInvalidPacket(preLoginPacket);
   }

   public void handleMobSpawn(PacketAddMob addMobPacket) {
      this.handleInvalidPacket(addMobPacket);
   }

   public void handleUpdateTime(PacketSetTime setTimePacket) {
      this.handleInvalidPacket(setTimePacket);
   }

   public void handleSpawnPosition(PacketSetSpawnPosition setSpawnPositionPacket) {
      this.handleInvalidPacket(setSpawnPositionPacket);
   }

   public void handleEntityVelocity(PacketSetEntityMotion setEntityMotionPacket) {
      this.handleInvalidPacket(setEntityMotionPacket);
   }

   public void handleEntityMetadata(PacketSetEntityData setEntityDataPacket) {
      this.handleInvalidPacket(setEntityDataPacket);
   }

   public void handleAttachEntity(PacketSetRiding setRidingPacket) {
      this.handleInvalidPacket(setRidingPacket);
   }

   public void handleUseEntity(PacketInteract interactPacket) {
      this.handleInvalidPacket(interactPacket);
   }

   public void handleEntityStatus(PacketEntityEvent enitityEventPacket) {
      this.handleInvalidPacket(enitityEventPacket);
   }

   public void handleEntityTagData(PacketEntityTagData entityTagDataPacket) {
      this.handleInvalidPacket(entityTagDataPacket);
   }

   public void handleUpdateHealth(PacketSetHealth setHealthPacket) {
      this.handleInvalidPacket(setHealthPacket);
   }

   public void handleUpdatePlayerProfile(PacketUpdatePlayerProfile updatePlayerProfilePacket) {
      this.handleInvalidPacket(updatePlayerProfilePacket);
   }

   public void handleRespawn(PacketRespawn respawnPacket) {
      this.handleInvalidPacket(respawnPacket);
   }

   public void handleExplosion(PacketExplosion explosionPacket) {
      this.handleInvalidPacket(explosionPacket);
   }

   public void handleOpenWindow(PacketContainerOpen containerOpenPacket) {
      this.handleInvalidPacket(containerOpenPacket);
   }

   public void handleCloseWindow(PacketContainerClose containerClosePacket) {
      this.handleInvalidPacket(containerClosePacket);
   }

   public void handleWindowClick(PacketContainerClick containerClickPacket) {
      this.handleInvalidPacket(containerClickPacket);
   }

   public void handleTileEntityData(PacketTileEntityData packetTileEntityData) {
      this.handleInvalidPacket(packetTileEntityData);
   }

   public void handleSetSlot(PacketContainerSetSlot containerSetslotPacket) {
      this.handleInvalidPacket(containerSetslotPacket);
   }

   public void handleWindowItems(PacketContainerSetContent containerSetContentPacket) {
      this.handleInvalidPacket(containerSetContentPacket);
   }

   public void handleUpdateSign(PacketSignUpdate signUpdatePacket) {
      this.handleInvalidPacket(signUpdatePacket);
   }

   public void handleSetMobSpawner(PacketSetMobSpawner setMobSpawnerPacket) {
      this.handleInvalidPacket(setMobSpawnerPacket);
   }

   public void handleUpdateProgressbar(PacketContainerSetData containerSetDataPacket) {
      this.handleInvalidPacket(containerSetDataPacket);
   }

   public void handlePlayerInventory(PacketSetEquippedItem setEquippedItemPacket) {
      this.handleInvalidPacket(setEquippedItemPacket);
   }

   public void handlePlayerHeldObject(PacketSetHeldObject setHeldObjectPacket) {
      this.handleInvalidPacket(setHeldObjectPacket);
   }

   public void handleTransaction(PacketContainerAck containerAckPacket) {
      this.handleInvalidPacket(containerAckPacket);
   }

   public void handleEntityPainting(PacketAddPainting addPaintingPacket) {
      this.handleInvalidPacket(addPaintingPacket);
   }

   public void handleBoatControl(PacketBoatControl boatControlPacket) {
      this.handleInvalidPacket(boatControlPacket);
   }

   public void handleBlockEvent(PacketBlockEvent blockEventPacket) {
      this.handleInvalidPacket(blockEventPacket);
   }

   public void handleStatistic(PacketStatistic statisticPacket) {
      this.handleInvalidPacket(statisticPacket);
   }

   public void handleSleep(PacketSleep sleepPacket) {
      this.handleInvalidPacket(sleepPacket);
   }

   public void handleBed(PacketBedMessage bedMessagePacket) {
      this.handleInvalidPacket(bedMessagePacket);
   }

   public void handleWeather(PacketWeatherEffect weatherEffectPacket) {
      this.handleInvalidPacket(weatherEffectPacket);
   }

   public void handleMapData(PacketMapData mapdataPacket) {
      this.handleInvalidPacket(mapdataPacket);
   }

   public void handlePlaySoundEffect(PacketPlaySoundEffect playSoundEffectPacket) {
      this.handleInvalidPacket(playSoundEffectPacket);
   }

   public void handlePlaySoundDirectly(PacketPlaySoundEffectDirect playSoundEffectDirectPacket) {
      this.handleInvalidPacket(playSoundEffectDirectPacket);
   }

   public void handleSpawnParticle(PacketAddParticle addParticlePacket) {
      this.handleInvalidPacket(addParticlePacket);
   }

   public void handleEntityPlayerGamemode(PacketPlayerGamemode playerGamemodePacket) {
      this.handleInvalidPacket(playerGamemodePacket);
   }

   public void handleUpdateCreativeInventory(PacketUpdateCreativeInventory updateCreativeInventoryPacket) {
      this.handleInvalidPacket(updateCreativeInventoryPacket);
   }

   public void handleWeatherStatus(PacketWeatherStatus weatherStatusPacket) {
      this.handleInvalidPacket(weatherStatusPacket);
   }

   public void handleOpenGuidebook(PacketGuidebook guidebookPacket) {
      this.handleInvalidPacket(guidebookPacket);
   }

   public void handleSetHotbarOffset(PacketSetHotbarOffset setHotbarOffsetPacket) {
      this.handleInvalidPacket(setHotbarOffsetPacket);
   }

   public void handleEntityNickname(PacketEntityNickname entityNicknamePacket) {
      this.handleInvalidPacket(entityNicknamePacket);
   }

   public void handleSendKey(PacketAESSendKey AESSendKeyPacket) {
      this.handleInvalidPacket(AESSendKeyPacket);
   }

   public void handleItemName(PacketSetItemName setItemNamePacket) {
      this.handleInvalidPacket(setItemNamePacket);
   }

   public void handlePlayerList(PacketPlayerList playerListPacket) {
      this.handleInvalidPacket(playerListPacket);
   }

   public void handleOpenFlagWindow(PacketFlagOpen flagOpenPacket) {
      this.handleInvalidPacket(flagOpenPacket);
   }

   public void handlePhotoMode(PacketPhotoMode photoModePacket) {
      this.handleInvalidPacket(photoModePacket);
   }

   public void handlePingHandshake(PacketPingHandshake pingHandshakePacket) {
      this.handleInvalidPacket(pingHandshakePacket);
   }

   public void handleGameRule(PacketGameRule gameRulePacket) {
      this.handleInvalidPacket(gameRulePacket);
   }

   public void handleSyncedRecipe(PacketRecipeSync recipeSyncPacket) {
      this.handleInvalidPacket(recipeSyncPacket);
   }

   public void handleIds(PacketSyncIDs syncIDsPacket) {
      this.handleInvalidPacket(syncIDsPacket);
   }

   public void handleCustomPayload(PacketCustomPayload customPayloadPacket) {
      this.handleInvalidPacket(customPayloadPacket);
   }

   public void handleCommandManagerPacket(PacketCommandManager commandManagerPacket) {
      this.handleInvalidPacket(commandManagerPacket);
   }

   public void handleRequestCommandManagerPacket(PacketRequestCommandManager requestCommandManagerPacket) {
      this.handleInvalidPacket(requestCommandManagerPacket);
   }
}
