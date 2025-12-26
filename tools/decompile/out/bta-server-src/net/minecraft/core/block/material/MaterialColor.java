package net.minecraft.core.block.material;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;

public class MaterialColor {
   private static final int ALLOCATED_COLORS = 64;
   private static final MaterialColor[] materialColors = new MaterialColor[64];
   public static final MaterialColor none = new MaterialColor(0, 0);
   public static final MaterialColor grass = new MaterialColor(1, 8368696);
   public static final MaterialColor sand = new MaterialColor(2, 16247203);
   public static final MaterialColor wool = new MaterialColor(3, 10987431);
   public static final MaterialColor fire = new MaterialColor(4, 16711680);
   public static final MaterialColor ice = new MaterialColor(5, 10526975);
   public static final MaterialColor metal = new MaterialColor(6, 10987431);
   public static final MaterialColor plant = new MaterialColor(7, 31744);
   public static final MaterialColor snow = new MaterialColor(8, 16777215);
   public static final MaterialColor clay = new MaterialColor(9, 10791096);
   public static final MaterialColor dirt = new MaterialColor(10, 8872261);
   public static final MaterialColor stone = new MaterialColor(11, 7368816);
   public static final MaterialColor water = new MaterialColor(12, 4210943);
   public static final MaterialColor wood = new MaterialColor(13, 6837042);
   public static final MaterialColor limestone = new MaterialColor(14, 14273434);
   public static final MaterialColor granite = new MaterialColor(15, 13345147);
   public static final MaterialColor basalt = new MaterialColor(16, 3026478);
   public static final MaterialColor permafrost = new MaterialColor(17, 10991308);
   public static final MaterialColor marble = new MaterialColor(18, 15395552);
   public static final MaterialColor slate = new MaterialColor(19, 7236499);
   public static final MaterialColor netherrack = new MaterialColor(20, 7016467);
   public static final MaterialColor brick = new MaterialColor(21, 8144182);
   public static final MaterialColor iron = new MaterialColor(30, 15527148);
   public static final MaterialColor gold = new MaterialColor(31, 16643670);
   public static final MaterialColor lapis = new MaterialColor(32, 1984679);
   public static final MaterialColor coal = new MaterialColor(33, 1315860);
   public static final MaterialColor olivine = new MaterialColor(34, 8182073);
   public static final MaterialColor quartz = new MaterialColor(35, 16043475);
   public static final MaterialColor redstone = new MaterialColor(36, 9633792);
   public static final MaterialColor steel = new MaterialColor(37, 4276545);
   public static final MaterialColor diamond = new MaterialColor(38, 8709087);
   public static final MaterialColor dirtScorched = new MaterialColor(40, 11693101);
   public static final MaterialColor grassScorched = new MaterialColor(41, 11507802);
   public static final MaterialColor mud = new MaterialColor(42, 4795920);
   public static final MaterialColor birchLeaves = new MaterialColor(43, 6526066);
   public static final MaterialColor thornLeaves = new MaterialColor(44, 9605952);
   public static final MaterialColor pineLeaves = new MaterialColor(45, 2440229);
   public static final MaterialColor palmLeaves = new MaterialColor(46, 6320915);
   public static final MaterialColor cherryLeaves = new MaterialColor(47, 12808079);
   public static final MaterialColor paintedWhite = new MaterialColor(48, 16777215);
   public static final MaterialColor paintedOrange = new MaterialColor(49, 15233318);
   public static final MaterialColor paintedMagenta = new MaterialColor(50, 11813826);
   public static final MaterialColor paintedLightblue = new MaterialColor(51, 5930698);
   public static final MaterialColor paintedYellow = new MaterialColor(52, 13878623);
   public static final MaterialColor paintedLime = new MaterialColor(53, 5550671);
   public static final MaterialColor paintedPink = new MaterialColor(54, 14380941);
   public static final MaterialColor paintedGrey = new MaterialColor(55, 4737096);
   public static final MaterialColor paintedSilver = new MaterialColor(56, 9871006);
   public static final MaterialColor paintedCyan = new MaterialColor(57, 3770262);
   public static final MaterialColor paintedPurple = new MaterialColor(58, 8144091);
   public static final MaterialColor paintedBlue = new MaterialColor(59, 2175465);
   public static final MaterialColor paintedBrown = new MaterialColor(60, 7221788);
   public static final MaterialColor paintedGreen = new MaterialColor(61, 2515750);
   public static final MaterialColor paintedRed = new MaterialColor(62, 248722484);
   public static final MaterialColor paintedBlack = new MaterialColor(63, 131586);
   private static final Map<Integer, Integer> blockIdColorIndexMap = new HashMap<>();
   private final int col;
   public final int id;

   private MaterialColor(int id, int col) {
      this.id = id;
      this.col = col;
      materialColors[id] = this;
   }

   private static int makeBlockIndex(Block<?> block, int meta) {
      return block.id() << 8 | meta;
   }

   public static void registerManualBlockColor(Block<?> block, int meta, MaterialColor color) {
      int blockIndex = makeBlockIndex(block, meta);
      blockIdColorIndexMap.put(blockIndex, color.id);
   }

   public static int getColorIndexFromBlock(Block<?> block, int meta) {
      int blockIndex = makeBlockIndex(block, meta);
      if (blockIdColorIndexMap.containsKey(blockIndex)) {
         return blockIdColorIndexMap.get(blockIndex);
      } else {
         blockIndex = makeBlockIndex(block, 0);
         return blockIdColorIndexMap.containsKey(blockIndex) ? blockIdColorIndexMap.get(blockIndex) : block.getMaterialColor().id;
      }
   }

   public static int getColorFromIndex(int colorIndex) {
      colorIndex &= 63;
      MaterialColor col = materialColors[colorIndex];
      return col == null ? none.col : col.col;
   }

   public static void assignManualEntries() {
      for (int i = 0; i < 16; i++) {
         MaterialColor color = materialColors[i + paintedWhite.id];
         registerManualBlockColor(Blocks.PLANKS_OAK_PAINTED, i, color);
         registerManualBlockColor(Blocks.WOOL, i, color);
         registerManualBlockColor(Blocks.FENCE_PLANKS_OAK_PAINTED, i, color);

         for (int j = 0; j < 16; j++) {
            registerManualBlockColor(Blocks.SLAB_PLANKS_PAINTED, i << 4 | j, color);
            registerManualBlockColor(Blocks.CHEST_PLANKS_OAK_PAINTED, i << 4 | j, color);
            registerManualBlockColor(Blocks.CHEST_LEGACY_PAINTED, i << 4 | j, color);
            registerManualBlockColor(Blocks.STAIRS_PLANKS_PAINTED, i << 4 | j, color);
            registerManualBlockColor(Blocks.TRAPDOOR_PLANKS_PAINTED, i << 4 | j, color);
            registerManualBlockColor(Blocks.DOOR_PLANKS_PAINTED_BOTTOM, i << 4 | j, color);
            registerManualBlockColor(Blocks.DOOR_PLANKS_PAINTED_TOP, i << 4 | j, color);
            registerManualBlockColor(Blocks.PRESSURE_PLATE_PLANKS_OAK_PAINTED, i << 4 | j, color);
         }

         registerManualBlockColor(Blocks.LAMP_ACTIVE, i, color);
         registerManualBlockColor(Blocks.LAMP_IDLE, i, color);
         registerManualBlockColor(Blocks.LAMP_INVERTED_ACTIVE, i, color);
         registerManualBlockColor(Blocks.LAMP_INVERTED_IDLE, i, color);
      }

      for (int i = 1; i < 256; i++) {
         registerManualBlockColor(Blocks.FARMLAND_DIRT, i, mud);
      }
   }
}
