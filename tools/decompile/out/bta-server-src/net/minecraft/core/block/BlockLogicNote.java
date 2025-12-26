package net.minecraft.core.block;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class BlockLogicNote extends BlockLogic {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MASK_NOTE = 63;
   public static final int MASK_POWERED = 128;
   public static final byte MAX_NOTE = 24;

   public BlockLogicNote(Block<?> block) {
      super(block, Material.wood);
      block.withEntity(null);
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      boolean flag = world.hasNeighborSignal(x, y, z);
      int data = world.getBlockMetadata(x, y, z);
      if (isPowered(data) && !flag) {
         world.setBlockMetadata(x, y, z, setPowered(data, false));
      } else if (!isPowered(data) && flag) {
         this.triggerNote(world, x, y, z);
         world.setBlockMetadata(x, y, z, setPowered(data, true));
      }
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (!world.isClientSide) {
         this.changePitch(world, x, y, z, player.isSneaking() ? -1 : 1);
         this.triggerNote(world, x, y, z);
      }

      return true;
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      this.changePitch(world, x, y, z, 1);
      this.triggerNote(world, x, y, z);
   }

   @Override
   public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      if (!world.isClientSide) {
         this.triggerNote(world, x, y, z);
      }
   }

   public void changePitch(World world, int x, int y, int z, int delta) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockMetadata(x, y, z, setNote(meta, (getNote(meta) + 25 + delta) % 25));
   }

   public void triggerNote(World world, int x, int y, int z) {
      if (!world.isBlockOpaqueCube(x, y + 1, z)) {
         world.triggerEvent(
            x, y, z, BlockLogicNote.Instrument.getInstrumentFromBlock(world.getBlock(x, y - 1, z)).index, getNote(world.getBlockMetadata(x, y, z))
         );
      }
   }

   public static int setNote(int data, int note) {
      return data & -64 | note & 63;
   }

   public static int getNote(int data) {
      return data & 63;
   }

   public static boolean isPowered(int data) {
      return (data & 128) != 0;
   }

   public static int setPowered(int data, boolean flag) {
      return flag ? data | 128 : data & -129;
   }

   @Override
   public void triggerEvent(World world, int x, int y, int z, int index, int data) {
      float f = (float)Math.pow(2.0, (data - 12) / 12.0);
      String s = BlockLogicNote.Instrument.getInstrumentFromIndex(index).soundKey;
      world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "note." + s, 3.0F, f);
      world.spawnParticle("note", x + 0.5, y + 1.2, z + 0.5, 0.0, 0.0, 0.0, data);
   }

   public static class Instrument {
      public static final Map<Integer, BlockLogicNote.Instrument> instrumentMap = new HashMap<>();
      public final int index;
      public final String soundKey;
      public static BlockLogicNote.Instrument DEFAULT = BlockLogicNote.Instrument.HARP;
      public static final BlockLogicNote.Instrument BD = new BlockLogicNote.Instrument(1, "bd");
      public static final BlockLogicNote.Instrument SNARE = new BlockLogicNote.Instrument(2, "snare");
      public static final BlockLogicNote.Instrument HAT = new BlockLogicNote.Instrument(3, "hat");
      public static final BlockLogicNote.Instrument BASS_ATTACK = new BlockLogicNote.Instrument(4, "bassattack");
      public static final BlockLogicNote.Instrument HARP = new BlockLogicNote.Instrument(0, "harp");
      public static final BlockLogicNote.Instrument CHANT = new BlockLogicNote.Instrument(5, "chant");
      public static final BlockLogicNote.Instrument DRUM_STEEL = new BlockLogicNote.Instrument(6, "drum_steel");
      public static final BlockLogicNote.Instrument CHIP_TRIANGLE = new BlockLogicNote.Instrument(7, "chip_triangle");
      public static final BlockLogicNote.Instrument GUITAR_ELECTRIC = new BlockLogicNote.Instrument(8, "guitar_electric");
      public static final BlockLogicNote.Instrument CELESTA = new BlockLogicNote.Instrument(9, "celesta");
      public static final BlockLogicNote.Instrument WOOD_BLOCK = new BlockLogicNote.Instrument(10, "wood_block");

      public Instrument(int index, String soundKey) {
         this.index = index;
         this.soundKey = soundKey;

         assert !instrumentMap.containsKey(index) : "Only 1 instrument can be assigned per index!";

         instrumentMap.put(index, this);
      }

      @NotNull
      public static BlockLogicNote.Instrument getInstrumentFromIndex(int index) {
         if (instrumentMap.containsKey(index)) {
            return instrumentMap.get(index);
         } else {
            BlockLogicNote.LOGGER.warn("No instrument assign to index '{}'!", index);
            return instrumentMap.getOrDefault(index, DEFAULT);
         }
      }

      @NotNull
      public static BlockLogicNote.Instrument getInstrumentFromBlock(@Nullable Block<?> block) {
         if (block == null) {
            return DEFAULT;
         } else if (block == Blocks.BLOCK_OLIVINE) {
            return CHIP_TRIANGLE;
         } else if (block == Blocks.BLOCK_REDSTONE || block == Blocks.PUMPKIN_REDSTONE || block.getLogic() instanceof BlockLogicOreRedstone) {
            return GUITAR_ELECTRIC;
         } else if (block != Blocks.CHEST_PLANKS_OAK && block != Blocks.CHEST_PLANKS_OAK_PAINTED) {
            Material material = block.getMaterial();
            if (material == Material.permafrost || material == Material.ice) {
               return CELESTA;
            } else if (material.isStone()) {
               return BD;
            } else if (material == Material.sand) {
               return SNARE;
            } else if (material == Material.glass) {
               return HAT;
            } else if (material == Material.wood) {
               return BASS_ATTACK;
            } else if (material == Material.soulsand) {
               return CHANT;
            } else {
               return material == Material.steel ? DRUM_STEEL : DEFAULT;
            }
         } else {
            return WOOD_BLOCK;
         }
      }
   }
}
