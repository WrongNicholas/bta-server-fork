package net.minecraft.core.item.material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;

public class ArmorMaterial {
   private static final List<ArmorMaterial> ARMOR_MATERIALS = new ArrayList<>();
   public final NamespaceID identifier;
   public final int durability;
   private final Map<DamageType, Float> protection = new HashMap<>();
   public static final ArmorMaterial LEATHER = register(
      new ArmorMaterial(NamespaceID.getPermanent("minecraft", "cloth"), 180)
         .withProtectionPercentage(DamageType.COMBAT, 20.0F)
         .withProtectionPercentage(DamageType.BLAST, 20.0F)
         .withProtectionPercentage(DamageType.FIRE, 20.0F)
         .withProtectionPercentage(DamageType.FALL, 120.0F)
   );
   public static final ArmorMaterial CHAINMAIL = register(
      new ArmorMaterial(NamespaceID.getPermanent("minecraft", "chain"), 240)
         .withProtectionPercentage(DamageType.COMBAT, 120.0F)
         .withProtectionPercentage(DamageType.BLAST, 35.0F)
         .withProtectionPercentage(DamageType.FIRE, 35.0F)
         .withProtectionPercentage(DamageType.FALL, 35.0F)
   );
   public static final ArmorMaterial IRON = register(
      new ArmorMaterial(NamespaceID.getPermanent("minecraft", "iron"), 200)
         .withProtectionPercentage(DamageType.COMBAT, 45.0F)
         .withProtectionPercentage(DamageType.BLAST, 45.0F)
         .withProtectionPercentage(DamageType.FIRE, 45.0F)
         .withProtectionPercentage(DamageType.FALL, 45.0F)
   );
   public static final ArmorMaterial GOLD = register(
      new ArmorMaterial(NamespaceID.getPermanent("minecraft", "gold"), 120)
         .withProtectionPercentage(DamageType.COMBAT, 70.0F)
         .withProtectionPercentage(DamageType.BLAST, 70.0F)
         .withProtectionPercentage(DamageType.FIRE, 70.0F)
         .withProtectionPercentage(DamageType.FALL, 70.0F)
   );
   public static final ArmorMaterial DIAMOND = register(
      new ArmorMaterial(NamespaceID.getPermanent("minecraft", "diamond"), 800)
         .withProtectionPercentage(DamageType.COMBAT, 66.0F)
         .withProtectionPercentage(DamageType.BLAST, 66.0F)
         .withProtectionPercentage(DamageType.FIRE, 124.0F)
         .withProtectionPercentage(DamageType.FALL, 66.0F)
   );
   public static final ArmorMaterial STEEL = register(
      new ArmorMaterial(NamespaceID.getPermanent("minecraft", "steel"), 1200)
         .withProtectionPercentage(DamageType.COMBAT, 55.0F)
         .withProtectionPercentage(DamageType.BLAST, 150.0F)
         .withProtectionPercentage(DamageType.FIRE, 55.0F)
         .withProtectionPercentage(DamageType.FALL, 55.0F)
   );

   public static List<ArmorMaterial> getArmorMaterials() {
      return Collections.unmodifiableList(ARMOR_MATERIALS);
   }

   public static ArmorMaterial register(ArmorMaterial material) {
      ARMOR_MATERIALS.add(material);
      return material;
   }

   public ArmorMaterial(NamespaceID identifier, int durability) {
      this.identifier = identifier.makePermanent();
      this.durability = durability;
      ARMOR_MATERIALS.add(this);
   }

   public float getProtection(DamageType damageType) {
      if (damageType == null) {
         return 0.0F;
      } else {
         Float protection = this.protection.get(damageType);
         return protection != null ? protection : 0.0F;
      }
   }

   public ArmorMaterial withProtectionPercentage(DamageType damageType, float percent) {
      this.protection.put(damageType, percent / 100.0F);
      return this;
   }
}
