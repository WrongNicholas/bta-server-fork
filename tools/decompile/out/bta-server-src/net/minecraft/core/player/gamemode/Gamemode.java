package net.minecraft.core.player.gamemode;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuInventory;

public abstract class Gamemode {
   private final int id;
   private final String languageKey;
   private boolean isPlayerInvulnerable = false;
   private boolean canPlayerFly = false;
   private boolean consumeBlocks = false;
   private boolean doBlockBreakingAnim = false;
   private boolean toolDurability = false;
   private boolean dropBlockOnBreak = false;
   private boolean areMobsHostile = false;
   private boolean isImmuneToFire = false;
   private boolean instantPortalTravel = false;
   private boolean requireToolToBreak = false;
   private boolean canInteract = true;
   private boolean hideFromWorldCreation = false;
   private boolean isPermaDeath = false;
   private float blockReachDistance = 4.5F;
   private float entityReachDistance = 3.0F;
   public static final Gamemode[] gamemodesList = new Gamemode[5];
   public static final Gamemode survival = new GamemodeSurvival(0, "gamemode.survival")
      .setConsumeBlocks()
      .setDoBlockBreakingAnim()
      .setToolDurability()
      .setDropBlockOnBreak()
      .setAreMobsHostile();
   public static final Gamemode creative = new GamemodeCreative(1, "gamemode.creative")
      .setIsPlayerInvulnerable()
      .setCanPlayerFly()
      .setIsImmuneToFire()
      .setInstantPortalTravel()
      .setBlockReachDistance(6.0F)
      .setEntityReachDistance(4.5F);
   public static final Gamemode hardcore = new GamemodeSurvival(2, "gamemode.hardcore")
      .setConsumeBlocks()
      .setDoBlockBreakingAnim()
      .setToolDurability()
      .setDropBlockOnBreak()
      .setAreMobsHostile()
      .setPermaDeath();
   public static final Gamemode adventure = new GamemodeSurvival(3, "gamemode.adventure")
      .setConsumeBlocks()
      .setDoBlockBreakingAnim()
      .setToolDurability()
      .setDropBlockOnBreak()
      .setAreMobsHostile()
      .setRequireToolToBreak();
   public static final Gamemode spectator = new GamemodeSurvival(4, "gamemode.spectator")
      .setIsPlayerInvulnerable()
      .setCanPlayerFly()
      .setIsImmuneToFire()
      .setInstantPortalTravel()
      .setBlockReachDistance(5000.0F)
      .setEntityReachDistance(5000.0F)
      .setCantInteract()
      .setHideFromWorldCreation();

   public Gamemode(int id, String languageKey) {
      this.id = id;
      this.languageKey = languageKey;
      gamemodesList[id] = this;
   }

   public int getId() {
      return this.id;
   }

   public String getLanguageKey() {
      return this.languageKey;
   }

   public Gamemode setIsPlayerInvulnerable() {
      this.isPlayerInvulnerable = true;
      return this;
   }

   public boolean isPlayerInvulnerable() {
      return this.isPlayerInvulnerable;
   }

   public Gamemode setCanPlayerFly() {
      this.canPlayerFly = true;
      return this;
   }

   public boolean canPlayerFly() {
      return this.canPlayerFly;
   }

   public Gamemode setConsumeBlocks() {
      this.consumeBlocks = true;
      return this;
   }

   public boolean consumeBlocks() {
      return this.consumeBlocks;
   }

   public Gamemode setDoBlockBreakingAnim() {
      this.doBlockBreakingAnim = true;
      return this;
   }

   public boolean doBlockBreakingAnim() {
      return this.doBlockBreakingAnim;
   }

   public Gamemode setToolDurability() {
      this.toolDurability = true;
      return this;
   }

   public boolean toolDurability() {
      return this.toolDurability;
   }

   public Gamemode setDropBlockOnBreak() {
      this.dropBlockOnBreak = true;
      return this;
   }

   public boolean dropBlockOnBreak() {
      return this.dropBlockOnBreak;
   }

   public Gamemode setAreMobsHostile() {
      this.areMobsHostile = true;
      return this;
   }

   public boolean areMobsHostile() {
      return this.areMobsHostile;
   }

   public Gamemode setIsImmuneToFire() {
      this.isImmuneToFire = true;
      return this;
   }

   public boolean isImmuneToFire() {
      return this.isImmuneToFire;
   }

   public Gamemode setInstantPortalTravel() {
      this.instantPortalTravel = true;
      return this;
   }

   public boolean instantPortalTravel() {
      return this.instantPortalTravel;
   }

   public Gamemode setBlockReachDistance(float blockReachDistance) {
      this.blockReachDistance = blockReachDistance;
      return this;
   }

   public float getBlockReachDistance() {
      return this.blockReachDistance;
   }

   public Gamemode setEntityReachDistance(float entityReachDistance) {
      this.entityReachDistance = entityReachDistance;
      return this;
   }

   public float getEntityReachDistance() {
      return this.entityReachDistance;
   }

   public boolean doesRequireToolToBreak() {
      return this.requireToolToBreak;
   }

   public Gamemode setRequireToolToBreak() {
      this.requireToolToBreak = true;
      return this;
   }

   public boolean canInteract() {
      return this.canInteract;
   }

   public Gamemode setCantInteract() {
      this.canInteract = false;
      return this;
   }

   public boolean isHiddenFromWorldCreation() {
      return this.hideFromWorldCreation;
   }

   public Gamemode setHideFromWorldCreation() {
      this.hideFromWorldCreation = true;
      return this;
   }

   public boolean isPermaDeath() {
      return this.isPermaDeath;
   }

   public Gamemode setPermaDeath() {
      this.isPermaDeath = true;
      return this;
   }

   public abstract MenuInventory getContainer(ContainerInventory var1, boolean var2);

   public static List<Gamemode> getPublicGamemodes() {
      List<Gamemode> gamemodes = new ArrayList<>();

      for (Gamemode gamemode : gamemodesList) {
         if (!gamemode.isHiddenFromWorldCreation()) {
            gamemodes.add(gamemode);
         }
      }

      return gamemodes;
   }
}
