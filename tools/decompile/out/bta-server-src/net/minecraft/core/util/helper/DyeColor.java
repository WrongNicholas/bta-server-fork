package net.minecraft.core.util.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public enum DyeColor {
   WHITE(0, 15, "white", 15790320),
   ORANGE(1, 14, "orange", 15435844),
   MAGENTA(2, 13, "magenta", 12801229),
   LIGHT_BLUE(3, 12, "lightblue", 6719955),
   YELLOW(4, 11, "yellow", 14602026),
   LIME(5, 10, "lime", 4312372),
   PINK(6, 9, "pink", 14188952),
   GRAY(7, 8, "gray", 4408131),
   SILVER(8, 7, "silver", 10132122),
   CYAN(9, 6, "cyan", 2651799),
   PURPLE(10, 5, "purple", 8073150),
   BLUE(11, 4, "blue", 2437522),
   BROWN(12, 3, "brown", 5320730),
   GREEN(13, 2, "green", 3887386),
   RED(14, 1, "red", 11743532),
   BLACK(15, 0, "black", 1973019);

   public static final int COLOR_AMOUNT = 16;
   public static final int MASK_COLOR = 15;
   private static final DyeColor[] blockMetaToColor = new DyeColor[16];
   private static final DyeColor[] itemMetaToColor = new DyeColor[16];
   private static final List<DyeColor> blockMetaColorsList;
   private static final List<DyeColor> itemMetaColorsList;
   public final int blockMeta;
   public final int itemMeta;
   public final String colorID;
   public final Color color;

   private DyeColor(int blockMeta, int itemMeta, String colorID, int color) {
      this.blockMeta = blockMeta;
      this.itemMeta = itemMeta;
      this.colorID = colorID;
      this.color = new Color().setARGB(color | 0xFF000000);
   }

   @NotNull
   public static DyeColor colorFromBlockMeta(int blockMeta) {
      return blockMetaToColor[blockMeta & 15];
   }

   @NotNull
   public static DyeColor colorFromItemMeta(int itemMeta) {
      return itemMetaToColor[itemMeta & 15];
   }

   public static @Unmodifiable List<DyeColor> blockOrderedColors() {
      return blockMetaColorsList;
   }

   public static @Unmodifiable List<DyeColor> itemOrderedColors() {
      return itemMetaColorsList;
   }

   static {
      for (DyeColor c : values()) {
         blockMetaToColor[c.blockMeta] = c;
         itemMetaToColor[c.itemMeta] = c;
      }

      blockMetaColorsList = Collections.unmodifiableList(Arrays.asList(blockMetaToColor));
      itemMetaColorsList = Collections.unmodifiableList(Arrays.asList(itemMetaToColor));
   }
}
