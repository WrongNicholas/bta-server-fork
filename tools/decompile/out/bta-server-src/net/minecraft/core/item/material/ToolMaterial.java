package net.minecraft.core.item.material;

public class ToolMaterial {
   public static final ToolMaterial wood = new ToolMaterial().setDurability(64).setEfficiency(2.0F, 4.0F).setMiningLevel(0);
   public static final ToolMaterial stone = new ToolMaterial().setDurability(128).setEfficiency(4.0F, 6.0F).setMiningLevel(1);
   public static final ToolMaterial iron = new ToolMaterial().setDurability(384).setEfficiency(6.0F, 8.0F).setMiningLevel(2);
   public static final ToolMaterial steel = new ToolMaterial().setDurability(4608).setEfficiency(7.0F, 14.0F).setMiningLevel(3);
   public static final ToolMaterial diamond = new ToolMaterial()
      .setDurability(1536)
      .setEfficiency(14.0F, 45.0F)
      .setMiningLevel(3)
      .setDamage(4)
      .setBlockHitDelay(4);
   public static final ToolMaterial gold = new ToolMaterial().setDurability(256).setEfficiency(7.0F, 12.0F).setMiningLevel(2).setSilkTouch(true);
   private int miningLevel;
   private int durability;
   private float efficiency;
   private float hasteEfficiency;
   private int damage;
   private boolean silkTouch;
   private int blockHitDelay = 5;

   public int getMiningLevel() {
      return this.miningLevel;
   }

   public int getDurability() {
      return this.durability;
   }

   public float getEfficiency(boolean haste) {
      return haste ? this.hasteEfficiency : this.efficiency;
   }

   public int getDamage() {
      return this.damage;
   }

   public boolean isSilkTouch() {
      return this.silkTouch;
   }

   public ToolMaterial setMiningLevel(int miningLevel) {
      this.miningLevel = miningLevel;
      this.damage = miningLevel;
      return this;
   }

   public ToolMaterial setDurability(int durability) {
      this.durability = durability;
      return this;
   }

   public ToolMaterial setDamage(int damage) {
      this.damage = damage;
      return this;
   }

   public ToolMaterial setEfficiency(float efficiency, float hasteEfficiency) {
      this.efficiency = efficiency;
      this.hasteEfficiency = hasteEfficiency;
      return this;
   }

   public ToolMaterial setSilkTouch(boolean silkTouch) {
      this.silkTouch = silkTouch;
      return this;
   }

   public ToolMaterial setBlockHitDelay(int miningDelay) {
      this.blockHitDelay = miningDelay;
      return this;
   }
}
