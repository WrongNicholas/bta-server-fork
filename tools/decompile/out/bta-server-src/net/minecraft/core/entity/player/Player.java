package net.minecraft.core.entity.player;

import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.achievement.stat.Stat;
import net.minecraft.core.achievement.stat.StatList;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicBed;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.entity.TileEntityDispenser;
import net.minecraft.core.block.entity.TileEntityFlag;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.block.entity.TileEntityTrommel;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.EntityFishingBobber;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.EntityLightning;
import net.minecraft.core.entity.EntityPrimedTNT;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobPig;
import net.minecraft.core.entity.animal.MobSheep;
import net.minecraft.core.entity.animal.MobWolf;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.entity.monster.MobGhast;
import net.minecraft.core.entity.monster.MobGiant;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.entity.monster.MobSlime;
import net.minecraft.core.entity.monster.MobSnowman;
import net.minecraft.core.entity.monster.MobSpider;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.entity.monster.MobZombiePig;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.entity.projectile.ProjectileArrowGolden;
import net.minecraft.core.entity.projectile.ProjectileCannonball;
import net.minecraft.core.entity.projectile.ProjectileFireball;
import net.minecraft.core.entity.vehicle.EntityBoat;
import net.minecraft.core.entity.vehicle.EntityMinecart;
import net.minecraft.core.enums.ArtType;
import net.minecraft.core.enums.Difficulty;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.EnumSleepStatus;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tool.ItemToolSword;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.menu.MenuInventory;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.ICarriable;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;
import net.minecraft.core.world.chunk.provider.IChunkProvider;
import net.minecraft.core.world.weather.Weathers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Player extends Mob {
   @NotNull
   public static final UUID UUID_MAK = UUID.fromString("2c045432-9b19-46cc-b7f5-3c90a458b604");
   @NotNull
   public static final UUID UUID_JONK = UUID.fromString("73eaf85f-863e-47e4-9ae4-e4dd40dbc58a");
   @NotNull
   public static final UUID UUID_SHIGN = UUID.fromString("49f6ea39-30b2-4441-b07c-7bfe0beeb61e");
   public static final int DATA_UNKNOWN = 16;
   public static final int DATA_SELECTED_ART = 17;
   public static final int DATA_HELD_ARROW = 18;
   public static final TextFormatting deathMsgColor = TextFormatting.RED;
   public ContainerInventory inventory;
   public MenuAbstract inventorySlots;
   public MenuAbstract craftingInventory;
   public Gamemode gamemode = Gamemode.survival;
   public int score;
   public List<String> messageHistory = new ArrayList<>();
   public float cameraVelocityOld;
   public float cameraVelocity;
   public boolean isSwinging;
   public int swingProgressInt;
   public String username;
   public UUID uuid;
   public int dimension;
   public double xdO;
   public double ydO;
   public double zd0;
   protected boolean sleeping;
   public ChunkCoordinates bedChunkCoordinates;
   public ChunkCoordinates dimensionEnterCoordinate;
   private int sleepTimer;
   public float sleepOffX;
   public float sleepOffY;
   public float sleepOffZ;
   @Nullable
   private ChunkCoordinates playerSpawnCoordinate;
   private ChunkCoordinates lastDeathCoordinate;
   private ChunkCoordinates startMinecartRidingCoordinate;
   public int timeUntilPortal;
   protected boolean inPortal;
   public int portalID;
   public DyeColor portalColor;
   public float timeInPortal;
   public float prevTimeInPortal;
   private final int damageRemainder;
   @Nullable
   public EntityFishingBobber bobberEntity;
   @Nullable
   protected ICarriable heldObject;
   public volatile String skinURL;
   public volatile String capeURL;
   public boolean slimModel = false;
   protected float baseSpeed = 0.1F;
   protected float baseFlySpeed = 0.02F;
   protected boolean isDwarf = false;
   public int lastRenderTick = 0;
   public float wobbleTimer = 0.0F;

   public Player(World world) {
      super(world);
      this.inventory = new ContainerInventory(this);
      this.score = 0;
      this.isSwinging = false;
      this.swingProgressInt = 0;
      this.timeUntilPortal = 20;
      this.inPortal = false;
      this.bobberEntity = null;
      this.inventorySlots = new MenuInventory(this.inventory, !world.isClientSide);
      this.craftingInventory = this.inventorySlots;
      this.heightOffset = 1.62F;
      ChunkCoordinates chunkcoordinates = world.getSpawnPoint();
      this.moveTo(chunkcoordinates.x + 0.5, chunkcoordinates.y + 1, chunkcoordinates.z + 0.5, 0.0F, 0.0F);
      this.fireImmuneTicks = 20;
      this.textureIdentifier = NamespaceID.getPermanent("minecraft", "char");
      this.damageRemainder = 0;
   }

   @Override
   public int getMaxHealth() {
      return 20;
   }

   public ArtType getSelectedArt() {
      return ArtType.values.get(this.entityData.getByte(17));
   }

   public void setSelectedArt(ArtType selectedArt) {
      this.entityData.set(17, (byte)ArtType.values.indexOf(selectedArt));
   }

   private void setupDwarfMode() {
      boolean dwarfModeEnabled = this.world.getGameRuleValue(GameRules.DWARF_MODE);
      if (dwarfModeEnabled && !this.isDwarf) {
         this.setSize(0.6F, 0.8F);
         this.heightOffset = 0.62F;
         this.setPos(this.x, this.y - 1.0, this.z);
      } else if (!dwarfModeEnabled && this.isDwarf) {
         this.setSize(0.6F, 1.8F);
         this.heightOffset = 1.62F;
         this.setPos(this.x, this.y + 1.0, this.z);
      }

      this.isDwarf = dwarfModeEnabled;
   }

   public boolean isDwarf() {
      return this.isDwarf;
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(16, (byte)0, Byte.class);
      this.entityData.define(17, (byte)0, Byte.class);
      this.entityData.define(18, -1, Integer.class);
   }

   @Override
   public boolean canSkate() {
      return this.inventory.armorInventory[0] != null && this.inventory.armorInventory[0].itemID == Items.ARMOR_BOOTS_ICESKATES.id;
   }

   @Override
   public void tick() {

      if (this.world.dimension == Dimension.NETHER) {
         this.addStat(Achievements.ENTER_NETHER, 1);
      }

      this.xdO = this.xd;
      this.ydO = this.yd;
      this.zd0 = this.zd;
      this.setupDwarfMode();
      if (this.isPlayerSleeping()) {
         this.sleepTimer++;
         if (this.sleepTimer > 100) {
            this.sleepTimer = 100;
         }

         if (!this.world.isClientSide) {
            if (!this.isInBed()) {
               this.wakeUpPlayer(true, true);
            } else if (this.world.isDaytime() && this.world.getCurrentWeather() != Weathers.OVERWORLD_STORM) {
               this.wakeUpPlayer(false, true);
            }
         }
      } else if (this.sleepTimer > 0) {
         this.sleepTimer++;
         if (this.sleepTimer >= 110) {
            this.sleepTimer = 0;
         }
      }

      super.tick();
      if (this.heldObject != null) {
         this.heldObject.heldTick(this.world, this);
      }

      if (!this.world.isClientSide && this.craftingInventory != null && !this.craftingInventory.stillValid(this)) {
         this.closeScreen();
         this.craftingInventory = this.inventorySlots;
      }

      this.addStat(StatList.minutesPlayedStat, 1);
      if (this.vehicle == null) {
         this.startMinecartRidingCoordinate = null;
      }
   }

   @Override
   protected boolean isMovementBlocked() {
      return this.getHealth() <= 0 || this.isPlayerSleeping();
   }

   protected void closeScreen() {
      this.craftingInventory = this.inventorySlots;
   }

   @Override
   public boolean canInteract() {
      return this.getGamemode().canInteract() && this.isAlive();
   }

   @Override
   public void rideTick() {
      double d = this.x;
      double d1 = this.y;
      double d2 = this.z;
      super.rideTick();
      this.cameraVelocityOld = this.cameraVelocity;
      this.cameraVelocity = 0.0F;
      this.addMountedMovementStat(this.x - d, this.y - d1, this.z - d2);
   }

   @Override
   public void resetPos() {
      this.heightOffset = 1.62F;
      this.setSize(0.6F, 1.8F);
      super.resetPos();
      this.setHealthRaw(this.getMaxHealth());
      this.deathTime = 0;
      this.setupDwarfMode();
   }

   @Override
   protected void updateAI() {
      if (this.isSwinging) {
         this.swingProgressInt++;
         if (this.swingProgressInt >= 8) {
            this.swingProgressInt = 0;
            this.isSwinging = false;
         }
      } else {
         this.swingProgressInt = 0;
      }

      this.swingProgress = this.swingProgressInt / 8.0F;
   }

   @Override
   public void onLivingUpdate() {
      if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.getHealth() < this.getMaxHealth() && this.tickCount % 20 * 12 == 0) {
         this.heal(1);
      }

      this.inventory.decrementAnimations();
      this.cameraVelocityOld = this.cameraVelocity;
      super.onLivingUpdate();
      this.speed = this.baseSpeed;
      this.flySpeed = this.baseFlySpeed;
      if (this.isSprinting()) {
         this.speed = (float)(this.speed + this.baseSpeed * 0.3);
         this.flySpeed = (float)(this.flySpeed + this.baseFlySpeed * 0.3);
      }

      double velocity = MathHelper.sqrt(this.xd * this.xd + this.zd * this.zd);
      double pitch = (float)Math.atan(-this.yd * 0.2) * 15.0F;
      if (velocity > 0.1F) {
         velocity = 0.1F;
      }

      if (!this.onGround || this.getHealth() <= 0) {
         velocity = 0.0;
      }

      if (this.onGround || this.getHealth() <= 0) {
         pitch = 0.0;
      }

      this.cameraVelocity = this.cameraVelocity + (float)((velocity - this.cameraVelocity) * 0.4F);
      this.cameraPitch = this.cameraPitch + (float)((pitch - this.cameraPitch) * 0.8F);
      if (!this.dead
         && this.lastDeathCoordinate != null
         && this.distanceTo(this.lastDeathCoordinate.x, this.lastDeathCoordinate.y, this.lastDeathCoordinate.z) < 8.0) {
         this.lastDeathCoordinate = null;
      }

      if (this.getHealth() > 0) {
         List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.bb.grow(1.0, 0.5, 1.0));
         if (list != null) {
            for (Entity entity : list) {
               if (!entity.removed) {
                  this.collideWithPlayer(entity);
               }
            }
         }
      }
   }

   @Override
   public void playHurtSound() {
      float pitch = 1.0F;
      if (this.world.getGameRuleValue(GameRules.DWARF_MODE)) {
         pitch = 2.0F;
      }

      this.world.playSoundAtEntity(null, this, this.getHurtSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + pitch);
   }

   @Override
   public void playDeathSound() {
      float pitch = 1.0F;
      if (this.world.getGameRuleValue(GameRules.DWARF_MODE)) {
         pitch = 2.0F;
      }

      this.world.playSoundAtEntity(null, this, this.getDeathSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + pitch);
   }

   private void collideWithPlayer(Entity entity) {
      entity.playerTouch(this);
      this.addStat(StatList.mobEncounterStats.get(EntityDispatcher.idForClass((Class<? extends Entity>)entity.getClass())), 1);
   }

   public int getScore() {
      return this.score;
   }

   @Override
   public void onDeath(Entity entityKilledBy) {
      if (this.getGamemode().isPermaDeath()) {
         this.setGamemode(Gamemode.spectator);
      }

      super.onDeath(entityKilledBy);
      this.setSize(0.2F, 0.2F);
      this.setPos(this.x, this.y, this.z);
      this.lastDeathCoordinate = new ChunkCoordinates((int)this.x, (int)this.y, (int)this.z);
      this.yd = 0.1;
      if (UUID_MAK.equals(this.uuid) || this.username.equals("MaggAndGeez")) {
         this.dropPlayerItemWithRandomChoice(new ItemStack(Items.AMMO_FIREBALL, 1), true);
      } else if (UUID_JONK.equals(this.uuid) || this.username.equals("jonkadelic")) {
         this.dropPlayerItemWithRandomChoice(new ItemStack(Items.FOOD_COOKIE, 1), true);
      } else if (UUID_SHIGN.equals(this.uuid) || this.username.equals("ShignBright")) {
         this.dropPlayerItemWithRandomChoice(new ItemStack(Items.SIGN_PAINTED, 1, DyeColor.PINK.itemMeta), true);
      }

      if (!this.world.getGameRuleValue(GameRules.KEEP_INVENTORY)) {
         this.craftingInventory.onCraftGuiClosed(this);
         this.inventory.dropAllItems();
      }

      if (this.heldObject != null) {
         this.heldObject.drop(this.world, this);
         this.heldObject = null;
      }

      if (entityKilledBy != null) {
         this.xd = -MathHelper.cos((this.attackedAtYaw + this.yRot) * (float) Math.PI / 180.0F) * 0.1F;
         this.zd = -MathHelper.sin((this.attackedAtYaw + this.yRot) * (float) Math.PI / 180.0F) * 0.1F;
      } else {
         this.xd = this.zd = 0.0;
      }

      this.heightOffset = 0.1F;
      this.addStat(StatList.deathsStat, 1);
   }

   @Override
   public float getShadowHeightOffs() {
      return super.getShadowHeightOffs() - this.getHeightOffset();
   }

   @Override
   public boolean sendDeathMessage(Entity entityKilledBy) {
      return true;
   }

   @Override
   public String getDeathMessage(Entity entityKilledBy) {
      if (this.random.nextInt(8000) == 666) {
         return this.getDisplayName() + deathMsgColor + " was killed by Herobrine.";
      } else if (entityKilledBy instanceof MobZombie) {
         return entityKilledBy instanceof MobZombiePig
            ? this.getDisplayName() + deathMsgColor + " discovered mob mentality."
            : this.getDisplayName() + deathMsgColor + " became a zombie.";
      } else if (entityKilledBy instanceof MobCreeper) {
         return deathMsgColor + "Aw man, " + this.getDisplayName() + deathMsgColor + " didn't see the creeper.";
      } else if (entityKilledBy instanceof MobSkeleton) {
         return this.getDisplayName() + deathMsgColor + " was a victim of aimbot.";
      } else if (entityKilledBy instanceof MobSpider) {
         return this.getDisplayName() + deathMsgColor + " spent too much time on the web.";
      } else if (entityKilledBy instanceof MobGhast) {
         return this.getDisplayName() + deathMsgColor + " didn't return the serve.";
      } else if (entityKilledBy instanceof MobSlime) {
         return this.getDisplayName() + deathMsgColor + " was slimed.";
      } else if (entityKilledBy instanceof MobWolf) {
         return this.getDisplayName() + deathMsgColor + " got what they deserved.";
      } else if (entityKilledBy instanceof ProjectileArrowGolden) {
         if (((ProjectileArrowGolden)entityKilledBy).owner instanceof Player) {
            return ((Player)((ProjectileArrowGolden)entityKilledBy).owner).uuid.equals(this.uuid)
               ? this.getDisplayName() + deathMsgColor + " got gold in their eye."
               : this.getDisplayName() + deathMsgColor + " got pierced by " + ((ProjectileArrow)entityKilledBy).owner.getDisplayName() + deathMsgColor + ".";
         } else {
            return this.getDisplayName() + deathMsgColor + " is Swiss cheese.";
         }
      } else if (entityKilledBy instanceof ProjectileArrow) {
         if (((ProjectileArrow)entityKilledBy).owner instanceof MobSkeleton) {
            return this.getDisplayName() + deathMsgColor + " was a victim of aimbot.";
         } else if (((ProjectileArrow)entityKilledBy).owner instanceof Player) {
            return ((Player)((ProjectileArrow)entityKilledBy).owner).uuid.equals(this.uuid)
               ? this.getDisplayName() + deathMsgColor + " lost a game of Russian roulette."
               : this.getDisplayName()
                  + deathMsgColor
                  + " got 360 noscoped by "
                  + ((ProjectileArrow)entityKilledBy).owner.getDisplayName()
                  + deathMsgColor
                  + ".";
         } else {
            return this.getDisplayName() + deathMsgColor + " is a pincushion.";
         }
      } else if (entityKilledBy instanceof ProjectileCannonball) {
         if (((ProjectileCannonball)entityKilledBy).owner instanceof Player) {
            return ((Player)((ProjectileCannonball)entityKilledBy).owner).uuid.equals(this.uuid)
               ? this.getDisplayName() + deathMsgColor + " jumped too high."
               : this.getDisplayName()
                  + deathMsgColor
                  + " was fragmented by "
                  + ((ProjectileCannonball)entityKilledBy).owner.getDisplayName()
                  + deathMsgColor
                  + ".";
         } else {
            return this.getDisplayName() + deathMsgColor + " was detonated remotely.";
         }
      } else if (entityKilledBy instanceof ProjectileFireball) {
         return this.getDisplayName() + deathMsgColor + " didn't return the serve.";
      } else if (entityKilledBy instanceof MobGiant) {
         return this.getDisplayName() + deathMsgColor + " was killed by- wait, what?!";
      } else if (entityKilledBy instanceof EntityLightning) {
         return this.getDisplayName() + deathMsgColor + " was grounded.";
      } else if (entityKilledBy instanceof EntityPrimedTNT) {
         return this.getDisplayName() + deathMsgColor + " was killed by Popbob.";
      } else if (entityKilledBy instanceof Player) {
         return this.getDisplayName() + deathMsgColor + " was backstabbed by " + ((Player)entityKilledBy).getDisplayName() + deathMsgColor + ".";
      } else if (entityKilledBy instanceof MobPig) {
         return this.getDisplayName() + deathMsgColor + " is *really* bad at Minecraft.";
      } else if (this.world.getBlockMaterial(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z)) == Material.lava) {
         return this.getDisplayName() + deathMsgColor + " got lost in the sauce.";
      } else if (this.world.getBlockId(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z)) == Blocks.SPIKES.id()) {
         return this.getDisplayName() + deathMsgColor + " fell for the trap.";
      } else if (this.fallDistance > 0.0F) {
         return this.getDisplayName() + deathMsgColor + " forgot to tie their laces.";
      } else if (this.airSupply <= 0) {
         return this.getDisplayName() + deathMsgColor + " is sleeping with the fishes.";
      } else {
         return this.remainingFireTicks > 0
            ? this.getDisplayName() + deathMsgColor + " was cooked well done."
            : this.getDisplayName() + deathMsgColor + " died mysteriously.";
      }
   }

   @Override
   protected void dropDeathItems() {
   }

   @Override
   public void awardKillScore(Entity entity, int i) {
      this.score += i;
      if (entity instanceof Player) {
         this.addStat(StatList.playerKillsStat, 1);
      } else {
         this.addStat(StatList.mobKillsStat, 1);
      }
   }

   public void dropCurrentItem(boolean dropFullStack) {
      this.dropPlayerItemWithRandomChoice(this.inventory.removeItem(this.inventory.getCurrentItemIndex(), dropFullStack ? 64 : 1), false);
   }

   public void dropPlayerItem(ItemStack itemstack) {
      this.dropPlayerItemWithRandomChoice(itemstack, false);
   }

   public void setHotbarOffset(int offset) {
      this.inventory.setHotbarOffset(offset, false);
   }

   @Override
   public void setHeldObject(@Nullable ICarriable heldObject) {
      this.heldObject = heldObject;
   }

   @Nullable
   public ICarriable getHeldObject() {
      return this.heldObject;
   }

   @Override
   public boolean isPickable() {
      return this.getGamemode().canInteract() ? super.isPickable() : false;
   }

   @Override
   public boolean isPushable() {
      return this.getGamemode().canInteract() ? super.isPushable() : false;
   }

   @Override
   public boolean isSelectable() {
      return this.getGamemode().canInteract() ? super.isSelectable() : false;
   }

   @Override
   public void push(Entity entity) {
      if (!this.noPhysics && this.getGamemode().canInteract()) {
         super.push(entity);
      }
   }

   public void dropPlayerItemWithRandomChoice(ItemStack itemstack, boolean flag) {
      if (this.getGamemode().canInteract()) {
         if (itemstack != null) {
            EntityItem entityitem = new EntityItem(this.world, this.x, this.y - 0.3F + this.getHeadHeight(), this.z, itemstack);
            entityitem.pickupDelay = 40;
            float f = 0.1F;
            if (flag) {
               float f2 = this.random.nextFloat() * 0.5F;
               float f4 = this.random.nextFloat() * (float) Math.PI * 2.0F;
               entityitem.xd = -MathHelper.sin(f4) * f2;
               entityitem.zd = MathHelper.cos(f4) * f2;
               entityitem.yd = 0.2;
            } else {
               float f1 = 0.3F;
               entityitem.xd = -MathHelper.sin(this.yRot / 180.0F * (float) Math.PI) * MathHelper.cos(this.xRot / 180.0F * (float) Math.PI) * f1;
               entityitem.zd = MathHelper.cos(this.yRot / 180.0F * (float) Math.PI) * MathHelper.cos(this.xRot / 180.0F * (float) Math.PI) * f1;
               entityitem.yd = -MathHelper.sin(this.xRot / 180.0F * (float) Math.PI) * f1 + 0.1F;
               f1 = 0.02F;
               float f3 = this.random.nextFloat() * (float) Math.PI * 2.0F;
               f1 *= this.random.nextFloat();
               entityitem.xd = entityitem.xd + Math.cos(f3) * f1;
               entityitem.yd = entityitem.yd + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F;
               entityitem.zd = entityitem.zd + Math.sin(f3) * f1;
            }

            this.joinEntityItemWithWorld(entityitem);
            this.addStat(StatList.dropStat, 1);
         }
      }
   }

   protected void joinEntityItemWithWorld(EntityItem entityitem) {
      this.world.entityJoinedWorld(entityitem);
   }

   public float getCurrentPlayerStrVsBlock(Block<?> block) {
      float f = this.inventory.getStrVsBlock(block);
      float defaultBreakSpeed = this.inventory.getCurrentItem() != null && this.inventory.getCurrentItem().getItem() instanceof ItemToolSword ? 1.51F : 1.01F;
      if (this.getGamemode().doesRequireToolToBreak() && f <= defaultBreakSpeed) {
         return 0.0F;
      } else {
         if (this.isUnderLiquid(Material.water)) {
            f /= 5.0F;
         }

         if (!this.onGround && !this.canClimb() && this.vehicle == null) {
            f /= 5.0F;
         }

         return f;
      }
   }

   public boolean canHarvestBlock(Block<?> block) {
      return block == null ? false : this.inventory.canHarvestBlock(block);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag tag) {
      super.readAdditionalSaveData(tag);
      this.nickname = this.nickname.replace(" ", "");
      ListTag nbttaglist = tag.getList("Inventory");
      this.inventory.readFromNBT(nbttaglist);
      this.inventory.setCurrentItemIndex(tag.getIntegerOrDefault("CurrentItem", this.inventory.getCurrentItemIndex()), true);
      this.inventory.setHotbarOffset(tag.getIntegerOrDefault("HotbarOffset", this.inventory.getHotbarOffset()), true);
      int dim = tag.getInteger("Dimension");
      if (dim == -1) {
         dim = 1;
      }

      this.dimension = dim;
      this.sleeping = tag.getBoolean("Sleeping");
      this.sleepTimer = tag.getShort("SleepTimer");
      this.setGamemodeOnLogin(Gamemode.gamemodesList[tag.getInteger("Gamemode")]);
      this.noPhysics = tag.getBoolean("Noclip") && this.gamemode.canPlayerFly();
      if (this.sleeping) {
         this.bedChunkCoordinates = new ChunkCoordinates(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z));
         this.wakeUpPlayer(true, true);
      }

      if (tag.containsKey("SpawnX") && tag.containsKey("SpawnY") && tag.containsKey("SpawnZ")) {
         this.playerSpawnCoordinate = new ChunkCoordinates(tag.getInteger("SpawnX"), tag.getInteger("SpawnY"), tag.getInteger("SpawnZ"));
      }

      if (tag.containsKey("LastDeathX") && tag.containsKey("LastDeathY") && tag.containsKey("LastDeathZ")) {
         this.lastDeathCoordinate = new ChunkCoordinates(tag.getInteger("LastDeathX"), tag.getInteger("LastDeathY"), tag.getInteger("LastDeathZ"));
      }

      if (tag.containsKey("HeldObject")) {
         CompoundTag heldTag = tag.getCompound("HeldObject");
         this.heldObject = ICarriable.createAndLoadCarriable(this, heldTag);
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag tag) {
      super.addAdditionalSaveData(tag);
      tag.put("Inventory", this.inventory.writeToNBT(new ListTag()));
      tag.putInt("CurrentItem", this.inventory.getCurrentItemIndex());
      tag.putInt("HotbarOffset", this.inventory.getHotbarOffset());
      tag.putInt("Dimension", this.dimension);
      tag.putBoolean("Sleeping", this.sleeping);
      tag.putShort("SleepTimer", (short)this.sleepTimer);
      tag.putInt("Gamemode", this.gamemode.getId());
      tag.putBoolean("Noclip", this.noPhysics && this.gamemode.canPlayerFly());
      if (this.playerSpawnCoordinate != null) {
         tag.putInt("SpawnX", this.playerSpawnCoordinate.x);
         tag.putInt("SpawnY", this.playerSpawnCoordinate.y);
         tag.putInt("SpawnZ", this.playerSpawnCoordinate.z);
      }

      if (this.lastDeathCoordinate != null) {
         tag.putInt("LastDeathX", this.lastDeathCoordinate.x);
         tag.putInt("LastDeathY", this.lastDeathCoordinate.y);
         tag.putInt("LastDeathZ", this.lastDeathCoordinate.z);
      }

      if (this.heldObject != null) {
         CompoundTag heldTag = new CompoundTag();
         this.heldObject.writeToNBT(heldTag);
         tag.put("HeldObject", heldTag);
      }
   }

   @NotNull
   @Override
   public String getDisplayName() {
      String name = this.nickname;
      if (name.isEmpty()) {
         name = this.username;
      } else {
         name = TextFormatting.ITALIC + name;
      }

      return TextFormatting.get(this.chatColor) + name;
   }

   public void displayContainerScreen(Container container) {
   }

   public void displayChestScreen(Container container, double x, double y, double z) {
   }

   public void displayWorkbenchScreen(int x, int y, int z) {
   }

   public void displayLabelEditorScreen(ItemStack itemStack, int slot) {
   }

   public void displayPaintingPickerScreen() {
   }

   public void displayMobPickerScreen(int x, int y, int z) {
   }

   public void displayWandMobPickerScreen(ItemStack itemStack) {
   }

   public void displayGuidebookScreen() {
   }

   public void onItemPickup(Entity entity, ItemStack item) {
   }

   @Override
   public float getHeadHeight() {
      return 0.0F;
   }

   protected void resetHeight() {
      this.heightOffset = this.getHeightOffset();
   }

   public float getHeightOffset() {
      return this.isDwarf ? 0.62F : 1.62F;
   }

   public boolean killPlayer() {
      return super.hurt(null, 100, null);
   }

   @Override
   public boolean hurt(Entity attacker, int damage, DamageType type) {
      this.entityAge = 0;
      if (this.getHealth() <= 0) {
         return false;
      } else if (this.gamemode.isPlayerInvulnerable()) {
         return false;
      } else {
         if (this.isPlayerSleeping() && !this.world.isClientSide) {
            this.wakeUpPlayer(true, true);
         }

         if (attacker instanceof MobMonster || attacker instanceof ProjectileArrow) {
            switch (this.world.getDifficulty()) {
               case PEACEFUL:
                  damage = 0;
                  break;
               case EASY:
                  damage = damage / 3 + 1;
                  break;
               case HARD:
                  damage = damage * 3 / 2;
            }
         }

         Entity blamedAttacker = attacker;
         if (damage == 0 && !(attacker instanceof MobSnowman)) {
            return false;
         } else {
            if (attacker instanceof ProjectileArrow && ((ProjectileArrow)attacker).owner != null) {
               blamedAttacker = ((ProjectileArrow)attacker).owner;
            }

            if (blamedAttacker instanceof Mob) {
               this.alertWolves((Mob)blamedAttacker, false);
            }

            this.addStat(StatList.damageTakenStat, damage);
            if (attacker != null) {
               this.addStat(StatList.mobEncounterStats.get(EntityDispatcher.idForClass((Class<? extends Entity>)attacker.getClass())), 1);
            }

            return super.hurt(attacker, damage, type);
         }
      }
   }

   protected boolean func_27025_G() {
      return false;
   }

   protected void alertWolves(Mob attacker, boolean flag) {
      if (!(attacker instanceof MobCreeper) && !(attacker instanceof MobGhast)) {
         if (attacker instanceof MobWolf) {
            MobWolf wolf = (MobWolf)attacker;
            if (wolf.isWolfTamed() && this.uuid.equals(wolf.getWolfOwner())) {
               return;
            }
         }

         if (!(attacker instanceof Player) || this.func_27025_G()) {
            for (MobWolf wolf : this.world
               .getEntitiesWithinAABB(
                  MobWolf.class, AABB.getTemporaryBB(this.x, this.y, this.z, this.x + 1.0, this.y + 1.0, this.z + 1.0).grow(16.0, 4.0, 16.0)
               )) {
               if (wolf.isWolfTamed() && wolf.getTarget() == null && this.uuid.equals(wolf.getWolfOwner()) && (!flag || !wolf.isWolfSitting())) {
                  wolf.setTarget(attacker);
               }
            }
         }
      }
   }

   @Override
   protected void damageEntity(int damage, DamageType damageType) {
      float protection = 1.0F - this.inventory.getTotalProtectionAmount(damageType);
      protection = Math.max(protection, 0.01F);
      double d = damage * protection;
      int newDamage = (int)(this.random.nextFloat() > 0.5 ? Math.floor(d) : Math.ceil(d));
      int preventedDamage = damage - newDamage;
      if (damageType != null && damageType.shouldDamageArmor()) {
         int armorDamage = (int)Math.ceil(preventedDamage / 4.0);
         this.inventory.damageArmor(armorDamage);
      }

      super.damageEntity(newDamage, damageType);
   }

   @Override
   public void lavaHurt() {
      if (!this.fireImmune) {
         float protection = 1.0F - this.inventory.getTotalProtectionAmount(DamageType.FIRE);
         protection = Math.max(protection, 0.01F);
         this.hurt(null, 4, DamageType.FIRE);
         this.remainingFireTicks = (int)(80.0F + 520.0F * protection);
         this.maxFireTicks = this.remainingFireTicks;
      }
   }

   @Override
   public void fireHurt() {
      if (!this.fireImmune) {
         float protection = 1.0F - this.inventory.getTotalProtectionAmount(DamageType.FIRE);
         protection = Math.max(protection, 0.01F);
         this.hurt(null, 1, DamageType.FIRE);
         this.remainingFireTicks = (int)(40.0F + 260.0F * protection);
         this.maxFireTicks = this.remainingFireTicks;
      }
   }

   public void displayFurnaceScreen(TileEntityFurnace tileEntity) {
   }

   public void displayTrommelScreen(TileEntityTrommel tileEntity) {
   }

   public void displayDispenserScreen(TileEntityDispenser tileEntity) {
   }

   public void displayActivatorScreen(TileEntityActivator tileEntity) {
   }

   public void displaySignEditorScreen(TileEntitySign tileEntity) {
   }

   public void displayFlagEditorScreen(TileEntityFlag tileEntity) {
   }

   @Override
   public boolean interact(@NotNull Player player) {
      return false;
   }

   public boolean useCurrentItemOnEntity(Entity entity) {
      boolean flag = false;
      if (entity.interact(this)) {
         this.swingItem();
         flag = true;
      }

      ItemStack itemstack = this.getCurrentEquippedItem();
      if (itemstack != null && entity instanceof Mob) {
         if (itemstack.useItemOnEntity((Mob)entity, this)) {
            this.swingItem();
            flag = true;
         }

         if (itemstack.stackSize <= 0) {
            this.destroyCurrentEquippedItem();
         }
      }

      return flag;
   }

   public ItemStack getCurrentEquippedItem() {
      return this.inventory.getCurrentItem();
   }

   public void destroyCurrentEquippedItem() {
      this.inventory.setItem(this.inventory.getCurrentItemIndex(), null);
   }

   @Override
   public double getRidingHeight() {
      return this.isDwarf ? this.heightOffset + 0.05 : this.heightOffset - 0.5F;
   }

   public void swingItem() {
      this.swingProgressInt = -1;
      this.isSwinging = true;
   }

   public void attackTargetEntityWithCurrentItem(Entity entity) {
      if (this.getGamemode().canInteract()) {
         int i = this.inventory.getDamageVsEntity(entity);
         if (i > 0) {
            if (this.yd < 0.0) {
               i++;
            }

            entity.hurt(this, i, DamageType.COMBAT);
            ItemStack itemstack = this.getCurrentEquippedItem();
            if (itemstack != null && entity instanceof Mob) {
               itemstack.hitEntity((Mob)entity, this);
               if (itemstack.stackSize <= 0) {
                  this.destroyCurrentEquippedItem();
               }
            }

            if (entity instanceof Mob) {
               if (entity.isAlive()) {
                  this.alertWolves((Mob)entity, true);
               }

               this.addStat(StatList.damageDealtStat, i);
            }
         }

         this.addStat(StatList.mobEncounterStats.get(EntityDispatcher.idForClass((Class<? extends Entity>)entity.getClass())), 1);
      }
   }

   public void respawnPlayer() {
   }

   public abstract void animate4();

   public void onItemStackChanged(ItemStack itemstack) {
   }

   @Override
   public void remove() {
      super.remove();
      this.inventorySlots.onCraftGuiClosed(this);
      if (this.craftingInventory != null) {
         this.craftingInventory.onCraftGuiClosed(this);
      }
   }

   @Override
   public boolean isInWall() {
      return !this.sleeping && super.isInWall();
   }

   public EnumSleepStatus sleepInBedAt(int x, int y, int z) {
      if (!this.world.isClientSide) {
         if (this.isPlayerSleeping() || !this.isAlive()) {
            return EnumSleepStatus.OTHER_PROBLEM;
         }

         if (!this.world.worldType.mayRespawn()) {
            return EnumSleepStatus.NOT_POSSIBLE_HERE;
         }

         if (Math.abs(this.x - x) > 3.0 || Math.abs(this.y - y) > 3.0 || Math.abs(this.z - z) > 3.0) {
            return EnumSleepStatus.TOO_FAR_AWAY;
         }

         if ((this.bedChunkCoordinates == null || !this.bedChunkCoordinates.equals(x, y, z))
            && (this.playerSpawnCoordinate == null || !this.playerSpawnCoordinate.equals(x, y, z))) {
            this.sendTranslatedChatMessage("bed.setSpawn");
            this.setPlayerSpawnCoordinate(new ChunkCoordinates(x, y, z));
         }

         if (!this.world.getGameRuleValue(GameRules.ALLOW_SLEEPING)) {
            this.sendStatusMessage(I18n.getInstance().translateKey("bed.noAllowSleeping"));
            return EnumSleepStatus.DISABLED_BY_GAMERULE;
         }

         if (this.world.isDaytime() && this.world.getCurrentWeather() != Weathers.OVERWORLD_STORM) {
            this.sendStatusMessage(I18n.getInstance().translateKey("bed.noSleep"));
            return EnumSleepStatus.NOT_POSSIBLE_NOW;
         }
      }

      this.setPlayerSleeping(x, y, z);
      return EnumSleepStatus.OK;
   }

   private void setPlayerSleeping(int x, int y, int z) {
      this.setSize(0.2F, 0.2F);
      this.heightOffset = 0.2F;
      if (this.world.isBlockLoaded(x, y, z)) {
         int meta = this.world.getBlockMetadata(x, y, z);
         int dir = BlockLogicBed.getDirection(meta);
         float xOff = 0.5F;
         float zOff = 0.5F;
         if (dir == 0) {
            zOff = 0.9F;
         } else if (dir == 1) {
            xOff = 0.1F;
         } else if (dir == 2) {
            zOff = 0.1F;
         } else if (dir == 3) {
            xOff = 0.9F;
         }

         this.func_22052_e(dir);
         this.setPos(x + xOff, y + 0.9375F, z + zOff);
      } else {
         this.setPos(x + 0.5F, y + 0.9375F, z + 0.5F);
      }

      this.sleeping = true;
      this.sleepTimer = 0;
      this.xd = this.zd = this.yd = 0.0;
      this.bedChunkCoordinates = new ChunkCoordinates(x, y, z);
      if (!this.world.isClientSide) {
         this.world.updateEnoughPlayersSleepingFlag(this);
      }
   }

   private void func_22052_e(int i) {
      this.sleepOffX = 0.0F;
      this.sleepOffZ = 0.0F;
      switch (i) {
         case 0:
            this.sleepOffZ = -1.8F;
            break;
         case 1:
            this.sleepOffX = 1.8F;
            break;
         case 2:
            this.sleepOffZ = 1.8F;
            break;
         case 3:
            this.sleepOffX = -1.8F;
      }
   }

   public void wakeUpPlayer(boolean flag, boolean flag1) {
      this.setSize(0.6F, 1.8F);
      this.resetHeight();
      ChunkCoordinates chunkcoordinates = this.bedChunkCoordinates;
      Block<?> b;
      if (chunkcoordinates != null
         && (b = this.world.getBlock(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z)) != null
         && b.getLogic() instanceof BlockLogicBed) {
         BlockLogicBed.setBedOccupied(this.world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, false);
         ChunkCoordinates chunkcoordinates2 = BlockLogicBed.getNearestEmptyChunkCoordinates(
            this.world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, 0
         );
         if (chunkcoordinates2 == null) {
            chunkcoordinates2 = new ChunkCoordinates(chunkcoordinates.x, chunkcoordinates.y + 1, chunkcoordinates.z);
         }

         this.setPos(chunkcoordinates2.x + 0.5F, chunkcoordinates2.y + this.heightOffset + 0.1F, chunkcoordinates2.z + 0.5F);
      }

      this.sleeping = false;
      if (!this.world.isClientSide && flag1) {
         this.world.updateEnoughPlayersSleepingFlag(this);
      }

      if (flag) {
         this.sleepTimer = 0;
      } else {
         this.sleepTimer = 100;
      }
   }

   private boolean isInBed() {
      Block<?> b;
      return (b = this.world.getBlock(this.bedChunkCoordinates.x, this.bedChunkCoordinates.y, this.bedChunkCoordinates.z)) != null
         && b.getLogic() instanceof BlockLogicBed;
   }

   @Nullable
   public static ChunkCoordinates getValidBedSpawnCoordinates(@NotNull World world, @NotNull ChunkCoordinates coords) {
      IChunkProvider chunkProvider = world.getChunkProvider();
      chunkProvider.setCurrentChunkOver(coords.x >> 4, coords.z >> 4);
      chunkProvider.prepareChunk(coords.x - 3 >> 4, coords.z - 3 >> 4);
      chunkProvider.prepareChunk(coords.x + 3 >> 4, coords.z - 3 >> 4);
      chunkProvider.prepareChunk(coords.x - 3 >> 4, coords.z + 3 >> 4);
      chunkProvider.prepareChunk(coords.x + 3 >> 4, coords.z + 3 >> 4);
      return Block.hasLogicClass(world.getBlock(coords.x, coords.y, coords.z), BlockLogicBed.class)
         ? BlockLogicBed.getNearestEmptyChunkCoordinates(world, coords.x, coords.y, coords.z, 0)
         : null;
   }

   public float getBedOrientationInDegrees() {
      if (this.bedChunkCoordinates != null) {
         int i = this.world.getBlockMetadata(this.bedChunkCoordinates.x, this.bedChunkCoordinates.y, this.bedChunkCoordinates.z);
         int j = BlockLogicBed.getDirection(i);
         switch (j) {
            case 0:
               return 90.0F;
            case 1:
               return 0.0F;
            case 2:
               return 270.0F;
            case 3:
               return 180.0F;
         }
      }

      return 0.0F;
   }

   @Override
   public boolean isPlayerSleeping() {
      return this.sleeping;
   }

   public boolean isPlayerFullyAsleep() {
      return this.sleeping && this.sleepTimer >= 100;
   }

   public int getSleepTimer() {
      return this.sleepTimer;
   }

   public void sendTranslatedChatMessage(String message) {
      this.sendMessage(I18n.getInstance().translateKey(message));
   }

   public abstract void sendMessage(String var1);

   public abstract void sendStatusMessage(String var1);

   @Override
   public ItemStack getHeldItem() {
      return this.inventory.getCurrentItem();
   }

   public void updateCreativeInventory(int page, String searchText) {
   }

   @Nullable
   public ChunkCoordinates getPlayerSpawnCoordinate() {
      return this.playerSpawnCoordinate;
   }

   public void setPlayerSpawnCoordinate(ChunkCoordinates chunkcoordinates) {
      if (chunkcoordinates != null) {
         this.playerSpawnCoordinate = new ChunkCoordinates(chunkcoordinates);
      } else {
         this.playerSpawnCoordinate = null;
      }
   }

   public void triggerAchievement(Stat statbase) {
      this.addStat(statbase, 1);
   }

   public void addStat(Stat statbase, int i) {
   }

   public int getStat(Stat statbase) {
      return 0;
   }

   @Override
   protected void jump() {
      super.jump();
      this.addStat(StatList.jumpStat, 1);
   }

   @Override
   public void moveEntityWithHeading(float moveStrafing, float moveForward) {
      double d = this.x;
      double d1 = this.y;
      double d2 = this.z;
      super.moveEntityWithHeading(moveStrafing, moveForward);
      this.addMovementStat(this.x - d, this.y - d1, this.z - d2);
   }

   private void addMovementStat(double d, double d1, double d2) {
      if (this.vehicle == null) {
         if (this.isUnderLiquid(Material.water)) {
            int i = Math.round(MathHelper.sqrt(d * d + d1 * d1 + d2 * d2) * 100.0F);
            if (i > 0) {
               this.addStat(StatList.distanceDoveStat, i);
            }
         } else if (this.isInWater()) {
            int j = Math.round(MathHelper.sqrt(d * d + d2 * d2) * 100.0F);
            if (j > 0) {
               this.addStat(StatList.distanceSwumStat, j);
               if (this.dimension == Dimension.NETHER.id) {
                  this.addStat(Achievements.SWIM_NETHER, 1);
               }
            }
         } else if (this.canClimb()) {
            if (d1 > 0.0) {
               this.addStat(StatList.distanceClimbedStat, (int)Math.round(d1 * 100.0));
            }
         } else if (this.onGround) {
            int k = Math.round(MathHelper.sqrt(d * d + d2 * d2) * 100.0F);
            if (k > 0) {
               this.addStat(StatList.distanceWalkedStat, k);
            }
         } else {
            int l = Math.round(MathHelper.sqrt(d * d + d2 * d2) * 100.0F);
            if (l > 25) {
               this.addStat(StatList.distanceFlownStat, l);
            }
         }
      }
   }

   private void addMountedMovementStat(double d, double d1, double d2) {
      if (this.vehicle != null) {
         int i = Math.round(MathHelper.sqrt(d * d + d1 * d1 + d2 * d2) * 100.0F);
         if (i > 0) {
            if (this.vehicle instanceof EntityMinecart) {
               this.addStat(StatList.distanceByMinecartStat, i);
               if (this.startMinecartRidingCoordinate == null) {
                  this.startMinecartRidingCoordinate = new ChunkCoordinates(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z));
               } else if (this.startMinecartRidingCoordinate.getSqDistanceTo(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z))
                  >= 1000.0) {
                  this.addStat(Achievements.ON_A_RAIL, 1);
               }
            } else if (this.vehicle instanceof EntityBoat) {
               this.addStat(StatList.distanceByBoatStat, i);
            } else if (this.vehicle instanceof MobPig) {
               this.addStat(StatList.distanceByPigStat, i);
            }
         }
      }
   }

   @Override
   protected void causeFallDamage(float distance) {
      if (distance >= 2.0F) {
         this.addStat(StatList.distanceFallenStat, (int)Math.round(distance * 100.0));
      }

      super.causeFallDamage(distance);
   }

   @Override
   public void killed(Mob mob) {
      if (mob instanceof MobMonster) {
         this.triggerAchievement(Achievements.KILL_ENEMY);
      }
   }

   @Override
   public void handlePortal(int portalBlockId, DyeColor portalColor) {
      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal = 10;
      } else {
         this.portalID = portalBlockId;
         this.portalColor = portalColor;
         this.inPortal = true;
      }
   }

   @Override
   public boolean canBreatheUnderwater() {
      return this.gamemode.isPlayerInvulnerable();
   }

   public Gamemode getGamemode() {
      return this.gamemode;
   }

   public void setGamemodeOnLogin(Gamemode gamemode) {
      this.gamemode = gamemode;
      MenuInventory newContainer = gamemode.getContainer(this.inventory, !this.world.isClientSide);
      if (this.craftingInventory == this.inventorySlots) {
         this.craftingInventory = newContainer;
      }

      this.inventorySlots = newContainer;
      if (!gamemode.canPlayerFly()) {
         this.noPhysics = false;
      }

      this.fireImmune = gamemode.isImmuneToFire();
   }

   public void setGamemode(Gamemode gamemode) {
      this.gamemode = gamemode;
      this.fireImmune = gamemode.isImmuneToFire();
   }

   public void setNoclip(boolean noclip) {
      this.noPhysics = noclip;
      if (this.noPhysics) {
         this.yd += 0.079F;
      }
   }

   public void setLastDeathCoordinate(ChunkCoordinates lastDeathCoordinate) {
      this.lastDeathCoordinate = lastDeathCoordinate;
   }

   public ChunkCoordinates getLastDeathCoordinate() {
      return this.lastDeathCoordinate;
   }

   public void pickBlock(int x, int y, int z) {
      Block<?> block = this.world.getBlock(x, y, z);
      int meta = this.world.getBlockMetadata(x, y, z);
      if (block != null) {
         TileEntity tileEntity = this.world.getTileEntity(x, y, z);
         ItemStack[] result = block.getBreakResult(this.world, EnumDropCause.PICK_BLOCK, x, y, z, meta, tileEntity);
         ItemStack selectItem = result != null ? result[0] : null;
         if (selectItem != null) {
            for (int i = 0; i < 9; i++) {
               ItemStack stack = this.inventory.getItem(i + this.inventory.getHotbarOffset());
               if (stack != null && stack.itemID == selectItem.itemID && stack.getMetadata() == selectItem.getMetadata()) {
                  this.setCurrentItem(i + this.inventory.getHotbarOffset());
                  return;
               }
            }

            int emptyHotbarSlot = -1;
            int slot = this.inventory.getCurrentItemIndex();

            for (int ix = 0; ix < 9; ix++) {
               if (this.inventory.getItem(ix + this.inventory.getHotbarOffset()) == null) {
                  emptyHotbarSlot = ix + this.inventory.getHotbarOffset();
                  slot = ix + this.inventory.getHotbarOffset();
                  break;
               }
            }

            int itemSlot = -1;
            int stackSize = -1;

            for (int ixx = 0; ixx < 36; ixx++) {
               ItemStack stack = this.inventory.getItem(ixx);
               if (stack != null
                  && stack.itemID == selectItem.itemID
                  && stack.getMetadata() == selectItem.getMetadata()
                  && (stackSize == -1 || stack.stackSize < stackSize)) {
                  itemSlot = ixx;
                  stackSize = stack.stackSize;
               }
            }

            if (itemSlot != -1) {
               this.swapItems(slot, itemSlot);
               this.setCurrentItem(slot);
            } else {
               if (this.getGamemode() == Gamemode.creative) {
                  int emptySlot = -1;

                  for (int ixxx = 0; ixxx < 36; ixxx++) {
                     if (this.inventory.getItem(ixxx) == null) {
                        emptySlot = ixxx;
                        break;
                     }
                  }

                  int createItemInsertSlot = emptyHotbarSlot != -1 ? emptyHotbarSlot : this.inventory.getCurrentItemIndex();
                  selectItem.stackSize = 1;
                  if (emptySlot != -1) {
                     this.swapItems(emptySlot, createItemInsertSlot);
                  }

                  this.inventory.setItem(createItemInsertSlot, selectItem);
                  this.setCurrentItem(createItemInsertSlot);
               }
            }
         }
      }
   }

   public void setCurrentItem(int i) {
      this.inventory.setCurrentItemIndex(i, false);
   }

   public void swapItems(int slot1, int slot2) {
      ItemStack stack1 = this.inventory.getItem(slot1);
      ItemStack stack2 = this.inventory.getItem(slot2);
      this.inventory.setItem(slot2, stack1);
      this.inventory.setItem(slot1, stack2);
   }

   public Item getNextArrow() {
      Item nextArrow = null;
      ItemStack quiverSlot = this.inventory.armorItemInSlot(2);
      if (quiverSlot != null && quiverSlot.itemID == Items.ARMOR_QUIVER.id && quiverSlot.getMetadata() < quiverSlot.getMaxDamage()) {
         nextArrow = Items.AMMO_ARROW;
      } else if (quiverSlot != null && quiverSlot.itemID == Items.ARMOR_QUIVER_GOLD.id) {
         nextArrow = Items.AMMO_ARROW_PURPLE;
      } else if (this.hasItem(Items.AMMO_ARROW_GOLD)) {
         nextArrow = Items.AMMO_ARROW_GOLD;
      } else if (this.hasItem(Items.AMMO_ARROW)) {
         nextArrow = Items.AMMO_ARROW;
      }

      return nextArrow;
   }

   public int getArrowId() {
      return this.entityData.getInt(18);
   }

   public boolean hasItem(Item item) {
      for (ItemStack stack : this.inventory.mainInventory) {
         if (stack != null && stack.getItem() == item) {
            return true;
         }
      }

      return false;
   }

   public static class SortByUsername implements Comparator<Player> {
      public int compare(Player o1, Player o2) {
         return o1.username.compareTo(o2.username);
      }
   }
}
