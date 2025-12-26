package net.minecraft.core.world;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicPortal;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.world.save.DimensionData;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypes;
import org.jetbrains.annotations.Nullable;

public class Dimension {
   public int id;
   public final String languageKey;
   public final Dimension homeDim;
   public final WorldType defaultWorldType;
   public final float worldScale;
   @Nullable
   public final Block<? extends BlockLogicPortal> portalBlock;
   private static final Map<Integer, Dimension> dimensionList = new HashMap<>();
   public static final Dimension OVERWORLD = new Dimension("overworld", null, 1.0F, null, WorldTypes.OVERWORLD_DEFAULT);
   @SuppressWarnings("unchecked")
   private static final Block<? extends BlockLogicPortal> NETHER_PORTAL =
       (Block<? extends BlockLogicPortal>) (Block<?>) Blocks.PORTAL_NETHER;

   public static final Dimension NETHER =
       new Dimension("nether", OVERWORLD, 0.125F, NETHER_PORTAL, WorldTypes.NETHER_DEFAULT);

   @SuppressWarnings("unchecked")
   private static final Block<? extends BlockLogicPortal> PARADISE_PORTAL =
       (Block<? extends BlockLogicPortal>) (Block<?>) Blocks.PORTAL_PARADISE;

   public static final Dimension PARADISE =
      new Dimension("paradise", OVERWORLD, 8.0F, PARADISE_PORTAL, WorldTypes.PARADISE_DEFAULT);

   public Dimension(
      String languageKey, Dimension homeDim, float worldScale, @Nullable Block<? extends BlockLogicPortal> portalBlock, WorldType defaultWorldType
   ) {
      this.languageKey = languageKey;
      this.homeDim = homeDim;
      this.worldScale = worldScale;
      this.portalBlock = portalBlock;
      this.defaultWorldType = defaultWorldType;
   }

   public DimensionData getDimensionData(World world) {
      if (world.dimension == this) {
         return world.dimensionData;
      } else {
         DimensionData dimensionData = world.saveHandler.getDimensionData(this.id);
         if (dimensionData == null) {
            dimensionData = new DimensionData(this.defaultWorldType);
         }

         return dimensionData;
      }
   }

   public boolean canPortal(Dimension dim) {
      return this.homeDim == null ? true : dim == this.homeDim;
   }

   public static float getCoordScale(Dimension oldDim, Dimension newDim) {
      if (oldDim.homeDim == newDim) {
         return 1.0F / oldDim.worldScale;
      } else {
         return newDim.homeDim == oldDim ? newDim.worldScale : 1.0F;
      }
   }

   public String getTranslatedName() {
      return I18n.getInstance().translateNameKey("dimension." + this.languageKey);
   }

   @Override
   public String toString() {
      return "Dimension: [ ID: " + this.id + " Name: " + this.getTranslatedName() + " ]";
   }

   public static Map<Integer, Dimension> getDimensionList() {
      return Collections.unmodifiableMap(dimensionList);
   }

   public static void registerDimension(int id, Dimension dimension) throws IllegalArgumentException {
      if (dimensionList.containsKey(id)) {
         throw new IllegalArgumentException("Dimension with ID " + id + " is already registered!");
      } else {
         dimension.id = id;
         dimensionList.put(id, dimension);
      }
   }

   public static void init() {
      registerDimension(0, OVERWORLD);
      registerDimension(1, NETHER);
      registerDimension(2, PARADISE);
   }
}
