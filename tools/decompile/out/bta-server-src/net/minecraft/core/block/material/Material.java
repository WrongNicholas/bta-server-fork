package net.minecraft.core.block.material;

public class Material {
   public static final int PISTON_PUSHABLE = 0;
   public static final int PISTON_DESTROY_ON_PUSH = 1;
   public static final int PISTON_CANT_PUSH = 2;
   public static final int LIGHTNING_NEUTRAL = 0;
   public static final int LIGHTNING_STRONG_CONDUCTOR = 20;
   public static final int LIGHTNING_LIGHT_CONDUCTOR = 5;
   public static final int LIGHTNING_LIGHT_INSULATOR = -5;
   public static final int LIGHTNING_STRONG_INSULATOR = -20;
   private boolean flammable;
   private boolean replaceable;
   private boolean notSolidBlocking;
   public final MaterialColor color;
   private boolean alwaysDestroyable;
   private boolean isStone;
   private boolean isMetal;
   private int pushReaction = 0;
   private int lightningConductivity = 0;
   public static final Material air = new MaterialGas(MaterialColor.none);
   public static final Material grass = new Material(MaterialColor.grass);
   public static final Material dirt = new Material(MaterialColor.dirt);
   public static final Material wood = new Material(MaterialColor.wood).flammable();
   public static final Material stone = new Material(MaterialColor.stone).setConductivity(-5).setAsStone().notAlwaysDestroyable();
   public static final Material basalt = new Material(MaterialColor.basalt).setConductivity(-5).setAsStone().notAlwaysDestroyable();
   public static final Material limestone = new Material(MaterialColor.limestone).setConductivity(-5).setAsStone().notAlwaysDestroyable();
   public static final Material granite = new Material(MaterialColor.granite).setConductivity(-5).setAsStone().notAlwaysDestroyable();
   public static final Material permafrost = new Material(MaterialColor.permafrost).setConductivity(-5).setAsStone().notAlwaysDestroyable();
   public static final Material marble = new Material(MaterialColor.marble).setConductivity(-5).setAsStone().notAlwaysDestroyable();
   public static final Material slate = new Material(MaterialColor.slate).setConductivity(-5).setAsStone().notAlwaysDestroyable();
   public static final Material netherrack = new Material(MaterialColor.netherrack).setConductivity(-5).setAsStone().notAlwaysDestroyable();
   public static final Material metal = new Material(MaterialColor.metal).setConductivity(20).setAsMetal().notAlwaysDestroyable();
   public static final Material steel = new Material(MaterialColor.steel).setConductivity(20).setAsMetal().notAlwaysDestroyable();
   public static final Material water = new MaterialLiquid(MaterialColor.water).setConductivity(5).destroyOnPush();
   public static final Material lava = new MaterialLiquid(MaterialColor.fire).destroyOnPush();
   public static final Material leaves = new Material(MaterialColor.plant).flammable().notSolidBlocking().destroyOnPush();
   public static final Material plant = new MaterialDecoration(MaterialColor.plant).destroyOnPush().replaceable();
   public static final Material sponge = new Material(MaterialColor.wool);
   public static final Material cloth = new Material(MaterialColor.wool).flammable();
   public static final Material fire = new MaterialGas(MaterialColor.none).destroyOnPush();
   public static final Material sand = new Material(MaterialColor.sand);
   public static final Material soulsand = new Material(MaterialColor.paintedBrown);
   public static final Material decoration = new MaterialDecoration(MaterialColor.none).destroyOnPush();
   public static final Material glass = new Material(MaterialColor.none).setConductivity(-20).notSolidBlocking();
   public static final Material explosive = new Material(MaterialColor.fire).flammable().notSolidBlocking();
   public static final Material coral = new Material(MaterialColor.plant).destroyOnPush();
   public static final Material ice = new Material(MaterialColor.ice).notSolidBlocking();
   public static final Material topSnow = new MaterialDecoration(MaterialColor.snow).replaceable().notSolidBlocking().notAlwaysDestroyable().destroyOnPush();
   public static final Material snow = new Material(MaterialColor.snow).notAlwaysDestroyable();
   public static final Material cactus = new Material(MaterialColor.plant).notSolidBlocking().destroyOnPush();
   public static final Material clay = new Material(MaterialColor.clay);
   public static final Material vegetable = new Material(MaterialColor.plant).destroyOnPush();
   public static final Material portal = new MaterialPortal(MaterialColor.none).notPushable();
   public static final Material cake = new Material(MaterialColor.none).destroyOnPush();
   public static final Material web = new Material(MaterialColor.wool).notAlwaysDestroyable().destroyOnPush();
   public static final Material piston = new Material(MaterialColor.stone);
   public static final Material moss = new Material(MaterialColor.grass).flammable().notAlwaysDestroyable();
   public static final Material woodWet = new Material(MaterialColor.grass).setConductivity(5);

   public Material(MaterialColor color) {
      this.alwaysDestroyable = true;
      this.color = color;
   }

   public boolean isLiquid() {
      return false;
   }

   public boolean isSolid() {
      return true;
   }

   public boolean blocksLight() {
      return true;
   }

   public boolean blocksMotion() {
      return true;
   }

   public Material setAsStone() {
      this.isStone = true;
      return this;
   }

   public boolean isStone() {
      return this.isStone;
   }

   public Material setAsMetal() {
      this.isMetal = true;
      return this;
   }

   public boolean isMetal() {
      return this.isMetal;
   }

   public Material notSolidBlocking() {
      this.notSolidBlocking = true;
      return this;
   }

   public Material notAlwaysDestroyable() {
      this.alwaysDestroyable = false;
      return this;
   }

   public Material flammable() {
      this.flammable = true;
      return this;
   }

   public boolean isFlammable() {
      return this.flammable;
   }

   public Material replaceable() {
      this.replaceable = true;
      return this;
   }

   public boolean isReplaceable() {
      return this.replaceable;
   }

   public boolean isSolidBlocking() {
      return this.notSolidBlocking ? false : this.blocksMotion();
   }

   public boolean isAlwaysDestroyable() {
      return this.alwaysDestroyable;
   }

   public int getPushReaction() {
      return this.pushReaction;
   }

   public Material destroyOnPush() {
      this.pushReaction = 1;
      return this;
   }

   public Material notPushable() {
      this.pushReaction = 2;
      return this;
   }

   public int getConductivity() {
      return this.lightningConductivity;
   }

   public Material setConductivity(int conductivity) {
      this.lightningConductivity = conductivity;
      return this;
   }
}
