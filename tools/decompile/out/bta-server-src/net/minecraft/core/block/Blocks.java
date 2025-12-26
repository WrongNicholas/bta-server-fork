package net.minecraft.core.block;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.MaterialColor;
import net.minecraft.core.block.piston.BlockLogicPistonBase;
import net.minecraft.core.block.piston.BlockLogicPistonBaseSteel;
import net.minecraft.core.block.piston.BlockLogicPistonBaseSticky;
import net.minecraft.core.block.piston.BlockLogicPistonHead;
import net.minecraft.core.block.piston.BlockLogicPistonMoving;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobFireflyCluster;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.block.ItemBlockAlgae;
import net.minecraft.core.item.block.ItemBlockLadder;
import net.minecraft.core.item.block.ItemBlockLamp;
import net.minecraft.core.item.block.ItemBlockPainted;
import net.minecraft.core.item.block.ItemBlockSlabPainted;
import net.minecraft.core.item.block.ItemBlockStairsPainted;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.world.Dimension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Blocks {
   @Nullable
   public static final Block<?> @NotNull [] blocksList = new Block[16384];
   public static final Map<NamespaceID, Block<?>> blockMap = new LinkedHashMap<>();
   @Deprecated
   public static final Map<String, Integer> keyToIdMap = new HashMap<>();
   public static final boolean[] shouldTick = new boolean[blocksList.length];
   public static final boolean[] solid = new boolean[blocksList.length];
   public static final boolean[] isEntityTile = new boolean[blocksList.length];
   public static final int[] lightBlock = new int[blocksList.length];
   public static final boolean[] translucent = new boolean[blocksList.length];
   public static final int[] lightEmission = new int[blocksList.length];
   public static final boolean[] neighborNotifyOnMetadataChangeDisabled = new boolean[blocksList.length];
   public static int highestBlockId = 0;

    private static void linkRedstoneOre(Block<? extends BlockLogic> normal, Block<?> glowing) {
       ((BlockLogicOreRedstone) normal.getLogic()).link(normal, glowing);
       ((BlockLogicOreRedstone) glowing.getLogic()).link(normal, glowing);
    }

   public static final Block<? extends BlockLogic> STONE = register("stone", "minecraft:block/stone", 1, b -> new BlockLogicStone(b, Blocks.COBBLE_STONE, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.CAVES_CUT_THROUGH, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BASALT = register("basalt", "minecraft:block/basalt", 2, b -> new BlockLogicStone(b, Blocks.COBBLE_BASALT, Material.basalt))
      .withSound(BlockSounds.STONE)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.CAVES_CUT_THROUGH, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> LIMESTONE = register(
         "limestone", "minecraft:block/limestone", 3, b -> new BlockLogicStone(b, Blocks.COBBLE_LIMESTONE, Material.limestone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.CAVES_CUT_THROUGH, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> GRANITE = register(
         "granite", "minecraft:block/granite", 4, b -> new BlockLogicStone(b, Blocks.COBBLE_GRANITE, Material.granite)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.CAVES_CUT_THROUGH, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> MARBLE = register("marble", "minecraft:block/marble", 5, b -> new BlockLogic(b, Material.marble))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLATE = register("slate", "minecraft:block/slate", 6, b -> new BlockLogic(b, Material.slate))
      .withSound(BlockSounds.STONE)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> PERMAFROST = register(
         "permafrost", "minecraft:block/permafrost", 7, b -> new BlockLogicStone(b, Blocks.COBBLE_PERMAFROST, Material.permafrost)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.CAVES_CUT_THROUGH, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> COBBLE_STONE = register(
         "cobble.stone", "minecraft:block/cobble_stone", 10, b -> new BlockLogicCobble(b, Material.stone, () -> Blocks.GRAVEL)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> COBBLE_STONE_MOSSY = register(
         "cobble.stone.mossy", "minecraft:block/cobble_stone_mossy", 11, b -> new BlockLogicCobble(b, Material.stone, () -> Blocks.GRAVEL)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.GROWS_FLOWERS);
   public static final Block<? extends BlockLogic> COBBLE_BASALT = register(
         "cobble.basalt", "minecraft:block/cobble_basalt", 12, b -> new BlockLogicCobble(b, Material.basalt, () -> Blocks.GRAVEL)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> COBBLE_LIMESTONE = register(
         "cobble.limestone", "minecraft:block/cobble_limestone", 13, b -> new BlockLogicCobble(b, Material.limestone, () -> Blocks.GRAVEL)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> COBBLE_GRANITE = register(
         "cobble.granite", "minecraft:block/cobble_granite", 14, b -> new BlockLogicCobble(b, Material.granite, () -> Blocks.GRAVEL)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> COBBLE_PERMAFROST = register(
         "cobble.permafrost", "minecraft:block/cobble_permafrost", 15, b -> new BlockLogicCobble(b, Material.permafrost, () -> Blocks.GRAVEL)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STONE_POLISHED = register("stone.polished", "minecraft:block/stone_polished", 860, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> GRANITE_POLISHED = register(
         "granite.polished", "minecraft:block/granite_polished", 861, b -> new BlockLogic(b, Material.granite)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> LIMESTONE_POLISHED = register(
         "limestone.polished", "minecraft:block/limestone_polished", 862, b -> new BlockLogic(b, Material.limestone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BASALT_POLISHED = register("basalt.polished", "minecraft:block/basalt_polished", 863, b -> new BlockLogic(b, Material.basalt))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLATE_POLISHED = register("slate.polished", "minecraft:block/slate_polished", 864, b -> new BlockLogic(b, Material.slate))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> PERMAFROST_POLISHED = register(
         "permafrost.polished", "minecraft:block/permafrost_polished", 865, b -> new BlockLogic(b, Material.permafrost)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> NETHERRACK_POLISHED = register(
         "netherrack.polished", "minecraft:block/netherrack_polished", 866, b -> new BlockLogic(b, Material.netherrack)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.4F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> PILLAR_MARBLE = register(
         "pillar.marble", "minecraft:block/pillar_marble", 20, b -> new BlockLogicAxisAligned(b, Material.marble)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> CAPSTONE_MARBLE = register("capstone.marble", "minecraft:block/capstone_marble", 21, b -> new BlockLogic(b, Material.marble))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SANDSTONE = register("sandstone", "minecraft:block/sandstone", 30, b -> new BlockLogicSandstone(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(0.8F)
      .withOverrideColor(MaterialColor.sand)
      .withTags(BlockTags.CAVES_CUT_THROUGH, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STONE_CARVED = register("stone.carved", "minecraft:block/stone_carved", 40, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledStats()
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> GRANITE_CARVED = register("granite.carved", "minecraft:block/granite_carved", 41, b -> new BlockLogic(b, Material.granite))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledStats()
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> LIMESTONE_CARVED = register(
         "limestone.carved", "minecraft:block/limestone_carved", 42, b -> new BlockLogic(b, Material.limestone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledStats()
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BASALT_CARVED = register("basalt.carved", "minecraft:block/basalt_carved", 43, b -> new BlockLogic(b, Material.basalt))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledStats()
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> PERMAFROST_CARVED = register(
         "permafrost.carved", "minecraft:block/permafrost_carved", 44, b -> new BlockLogic(b, Material.permafrost)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledStats()
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> NETHERRACK_CARVED = register(
         "netherrack.carved", "minecraft:block/netherrack_carved", 45, b -> new BlockLogic(b, Material.netherrack)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.4F)
      .withBlastResistance(1.0F)
      .withDisabledStats()
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicPlanks> PLANKS_OAK = register("planks.oak", "minecraft:block/planks_oak", 50, BlockLogicPlanks::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicPlanksPainted> PLANKS_OAK_PAINTED = register(
         "planks.oak.painted", "minecraft:block/planks_oak_painted", 51, BlockLogicPlanksPainted::new
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setBlockItem(b -> new ItemBlockPainted<>(b, false))
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicTorch> TORCH_COAL = register("torch.coal", "minecraft:block/torch_coal", 60, BlockLogicTorch::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(0.0F)
      .withLightEmission(0.9375F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS);
   public static final Block<BlockLogicLadder> LADDER_OAK = register("ladder.oak", "minecraft:block/ladder_oak", 70, BlockLogicLadder::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(0.4F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setBlockItem(ItemBlockLadder::new)
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicFence> FENCE_PLANKS_OAK = register("fence.planks.oak", "minecraft:block/fence_planks_oak", 80, BlockLogicFence::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE, BlockTags.CAN_HANG_OFF);
   public static final Block<BlockLogicFencePainted> FENCE_PLANKS_OAK_PAINTED = register(
         "fence.planks.oak.painted", "minecraft:block/fence_planks_oak_painted", 81, BlockLogicFencePainted::new
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setBlockItem(b -> new ItemBlockPainted<>(b, false))
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE, BlockTags.CAN_HANG_OFF);
   public static final Block<BlockLogicFenceGate> FENCE_GATE_PLANKS_OAK = register(
         "fencegate.planks.oak", "minecraft:block/fence_gate_planks_oak", 90, BlockLogicFenceGate::new
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicFenceGatePainted> FENCE_GATE_PLANKS_OAK_PAINTED = register(
         "fencegate.planks.oak.painted", "minecraft:block/fence_gate_planks_oak_painted", 91, BlockLogicFenceGatePainted::new
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setBlockItem(b -> new ItemBlockPainted<>(b, true))
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> BOOKSHELF_PLANKS_OAK = register(
         "bookshelf.planks.oak", "minecraft:block/bookshelf_planks_oak", 100, b -> new BlockLogicBookshelf(b, Material.wood)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(1.5F)
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicWool> WOOL = register("wool", "minecraft:block/wool", 110, BlockLogicWool::new)
      .withSound(BlockSounds.CLOTH)
      .withHardness(0.8F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setBlockItem(b -> new ItemBlockPainted<>(b, false))
      .withTags(BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<BlockLogicRope> ROPE = register("rope", "minecraft:block/rope", 111, BlockLogicRope::new)
      .withSound(BlockSounds.CLOTH)
      .withHardness(0.01F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.ROPE)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.PREVENT_MOB_SPAWNS, BlockTags.CAN_HANG_OFF, BlockTags.INSTANT_PICKUP);
   public static final Block<? extends BlockLogic> BRICK_CLAY = register("brick.clay", "minecraft:block/brick_clay", 120, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.brick)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_STONE_POLISHED = register(
         "brick.stone.polished", "minecraft:block/brick_stone_polished", 121, b -> new BlockLogic(b, Material.stone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_STONE_POLISHED_MOSSY = register(
         "brick.stone.polished.mossy", "minecraft:block/brick_stone_polished_mossy", 122, b -> new BlockLogic(b, Material.stone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.GROWS_FLOWERS);
   public static final Block<? extends BlockLogic> BRICK_SANDSTONE = register("brick.sandstone", "minecraft:block/brick_sandstone", 123, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(0.8F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.sand)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_GOLD = register("brick.gold", "minecraft:block/brick_gold", 124, b -> new BlockLogic(b, Material.metal))
      .withSound(BlockSounds.METAL)
      .withHardness(3.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.gold)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_LAPIS = register("brick.lapis", "minecraft:block/brick_lapis", 125, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.lapis)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_BASALT = register("brick.basalt", "minecraft:block/brick_basalt", 126, b -> new BlockLogic(b, Material.basalt))
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_LIMESTONE = register(
         "brick.limestone", "minecraft:block/brick_limestone", 127, b -> new BlockLogic(b, Material.limestone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_GRANITE = register("brick.granite", "minecraft:block/brick_granite", 128, b -> new BlockLogic(b, Material.granite))
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_MARBLE = register("brick.marble", "minecraft:block/brick_marble", 129, b -> new BlockLogic(b, Material.marble))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_SLATE = register("brick.slate", "minecraft:block/brick_slate", 130, b -> new BlockLogic(b, Material.slate))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_STONE = register("brick.stone", "minecraft:block/brick_stone", 131, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_PERMAFROST = register(
         "brick.permafrost", "minecraft:block/brick_permafrost", 132, b -> new BlockLogic(b, Material.permafrost)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withHardness(1.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_IRON = register("brick.iron", "minecraft:block/brick_iron", 133, b -> new BlockLogic(b, Material.metal))
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.iron)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRICK_STEEL = register("brick.steel", "minecraft:block/brick_steel", 134, b -> new BlockLogic(b, Material.steel))
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withBlastResistance(2000.0F)
      .withOverrideColor(MaterialColor.steel)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_PLANKS_OAK = register(
         "slab.planks.oak", "minecraft:block/slab_planks_oak", 140, b -> new BlockLogicSlabPaintable(b, PLANKS_OAK)
      )
      .withSound(BlockSounds.WOOD)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> SLAB_COBBLE_STONE = register(
         "slab.cobble.stone", "minecraft:block/slab_cobble_stone", 141, b -> new BlockLogicSlab(b, COBBLE_STONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_SANDSTONE = register(
         "slab.sandstone", "minecraft:block/slab_sandstone", 142, b -> new BlockLogicSlab(b, SANDSTONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_STONE_POLISHED = register(
         "slab.brick.stone.polished", "minecraft:block/slab_brick_stone_polished", 143, b -> new BlockLogicSlab(b, BRICK_STONE_POLISHED)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_STONE_POLISHED = register(
         "slab.stone.carved", "minecraft:block/slab_stone_carved", 144, b -> new BlockLogicSlab(b, STONE_CARVED)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_MARBLE = register(
         "slab.brick.marble", "minecraft:block/slab_brick_marble", 145, b -> new BlockLogicSlab(b, BRICK_MARBLE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_CLAY = register(
         "slab.brick.clay", "minecraft:block/slab_brick_clay", 146, b -> new BlockLogicSlab(b, BRICK_CLAY)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_CAPSTONE_MARBLE = register(
         "slab.capstone.marble", "minecraft:block/slab_capstone_marble", 147, b -> new BlockLogicSlab(b, CAPSTONE_MARBLE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_COBBLE_BASALT = register(
         "slab.cobble.basalt", "minecraft:block/slab_cobble_basalt", 148, b -> new BlockLogicSlab(b, COBBLE_BASALT)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_COBBLE_LIMESTONE = register(
         "slab.cobble.limestone", "minecraft:block/slab_cobble_limestone", 149, b -> new BlockLogicSlab(b, COBBLE_LIMESTONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_COBBLE_GRANITE = register(
         "slab.cobble.granite", "minecraft:block/slab_cobble_granite", 150, b -> new BlockLogicSlab(b, COBBLE_GRANITE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_BASALT = register(
         "slab.brick.basalt", "minecraft:block/slab_brick_basalt", 151, b -> new BlockLogicSlab(b, BRICK_BASALT)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_LIMESTONE = register(
         "slab.brick.limestone", "minecraft:block/slab_brick_limestone", 152, b -> new BlockLogicSlab(b, BRICK_LIMESTONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_GRANITE = register(
         "slab.brick.granite", "minecraft:block/slab_brick_granite", 153, b -> new BlockLogicSlab(b, BRICK_GRANITE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_PLANKS_PAINTED = register(
         "slab.planks.oak.painted", "minecraft:block/slab_planks_oak_painted", 154, b -> new BlockLogicSlabPainted(b, PLANKS_OAK_PAINTED)
      )
      .withSound(BlockSounds.WOOD)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .setBlockItem(ItemBlockSlabPainted::new)
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_SLATE = register(
         "slab.brick.slate", "minecraft:block/slab_brick_slate", 155, b -> new BlockLogicSlab(b, BRICK_SLATE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_STONE = register(
         "slab.brick.stone", "minecraft:block/slab_brick_stone", 156, b -> new BlockLogicSlab(b, BRICK_STONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_GRANITE_POLISHED = register(
         "slab.granite.carved", "minecraft:block/slab_granite_carved", 157, b -> new BlockLogicSlab(b, GRANITE_CARVED)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_LIMESTONE_POLISHED = register(
         "slab.limestone.carved", "minecraft:block/slab_limestone_carved", 158, b -> new BlockLogicSlab(b, LIMESTONE_CARVED)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BASALT_POLISHED = register(
         "slab.basalt.carved", "minecraft:block/slab_basalt_carved", 159, b -> new BlockLogicSlab(b, BASALT_CARVED)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_PLANKS_OAK = register(
         "stairs.planks.oak", "minecraft:block/stairs_planks_oak", 160, b -> new BlockLogicStairsPaintable(b, PLANKS_OAK)
      )
      .withSound(BlockSounds.WOOD)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> STAIRS_COBBLE_STONE = register(
         "stairs.cobble.stone", "minecraft:block/stairs_cobble_stone", 161, b -> new BlockLogicStairs(b, COBBLE_STONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_STONE_POLISHED = register(
         "stairs.brick.stone.polished", "minecraft:block/stairs_brick_stone_polished", 162, b -> new BlockLogicStairs(b, BRICK_STONE_POLISHED)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_MARBLE = register(
         "stairs.brick.marble", "minecraft:block/stairs_brick_marble", 163, b -> new BlockLogicStairs(b, BRICK_MARBLE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_COBBLE_BASALT = register(
         "stairs.cobble.basalt", "minecraft:block/stairs_cobble_basalt", 164, b -> new BlockLogicStairs(b, COBBLE_BASALT)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_COBBLE_LIMESTONE = register(
         "stairs.cobble.limestone", "minecraft:block/stairs_cobble_limestone", 165, b -> new BlockLogicStairs(b, COBBLE_LIMESTONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_COBBLE_GRANITE = register(
         "stairs.cobble.granite", "minecraft:block/stairs_cobble_granite", 166, b -> new BlockLogicStairs(b, COBBLE_GRANITE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_BASALT = register(
         "stairs.brick.basalt", "minecraft:block/stairs_brick_basalt", 167, b -> new BlockLogicStairs(b, BRICK_BASALT)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_LIMESTONE = register(
         "stairs.brick.limestone", "minecraft:block/stairs_brick_limestone", 168, b -> new BlockLogicStairs(b, BRICK_LIMESTONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_GRANITE = register(
         "stairs.brick.granite", "minecraft:block/stairs_brick_granite", 169, b -> new BlockLogicStairs(b, BRICK_GRANITE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_CLAY = register(
         "stairs.brick.clay", "minecraft:block/stairs_brick_clay", 170, b -> new BlockLogicStairs(b, BRICK_CLAY)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_PLANKS_PAINTED = register(
         "stairs.planks.oak.painted", "minecraft:block/stairs_planks_oak_painted", 171, b -> new BlockLogicStairsPainted(b, PLANKS_OAK_PAINTED)
      )
      .withSound(BlockSounds.WOOD)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .setBlockItem(ItemBlockStairsPainted::new)
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_SLATE = register(
         "stairs.brick.slate", "minecraft:block/stairs_brick_slate", 172, b -> new BlockLogicStairs(b, BRICK_SLATE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_STONE = register(
         "stairs.brick.stone", "minecraft:block/stairs_brick_stone", 173, b -> new BlockLogicStairs(b, BRICK_STONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_SANDSTONE = register(
         "stairs.sandstone", "minecraft:block/stairs_sandstone", 174, b -> new BlockLogicStairs(b, SANDSTONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_SANDSTONE = register(
         "stairs.brick.sandstone", "minecraft:block/stairs_brick_sandstone", 175, b -> new BlockLogicStairs(b, BRICK_SANDSTONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_COBBLE_PERMAFROST = register(
         "stairs.cobble.permafrost", "minecraft:block/stairs_cobble_permafrost", 176, b -> new BlockLogicStairs(b, COBBLE_PERMAFROST)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_PERMAFROST = register(
         "stairs.brick.permafrost", "minecraft:block/stairs_brick_permafrost", 177, b -> new BlockLogicStairs(b, BRICK_PERMAFROST)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> OBSIDIAN = register("obsidian", "minecraft:block/obsidian", 180, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(10.0F)
      .withBlastResistance(2000.0F)
      .withOverrideColor(MaterialColor.paintedBlack)
      .withImmovableFlagSet()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.PISTON_CRUSHING);
   public static final Block<? extends BlockLogic> GLASS = register("glass", "minecraft:block/glass", 190, b -> new BlockLogicGlass(b, Material.glass))
      .withSound(BlockSounds.GLASS)
      .withHardness(0.3F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.EXTENDS_MOTION_SENSOR_RANGE);
   public static final Block<BlockLogicGlassTinted> GLASS_TINTED = register("glass.tinted", "minecraft:block/glass_tinted", 191, BlockLogicGlassTinted::new)
      .withSound(BlockSounds.GLASS)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> GLASS_STEEL = register("glass.steel", "minecraft:block/glass_steel", 192, b -> new BlockLogicTransparent(b, Material.glass))
      .withSound(BlockSounds.GLASS)
      .withHardness(0.3F)
      .withBlastResistance(2000.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.EXTENDS_MOTION_SENSOR_RANGE);
   public static final Block<? extends BlockLogic> GRASS = register("grass", "minecraft:block/grass", 200, b -> new BlockLogicGrass(b, Blocks.DIRT))
      .withSound(BlockSounds.GRASS)
      .withHardness(0.6F)
      .withTags(
         BlockTags.MINEABLE_BY_SHOVEL,
         BlockTags.GROWS_FLOWERS,
         BlockTags.GROWS_SUGAR_CANE,
         BlockTags.PASSIVE_MOBS_SPAWN,
         BlockTags.FIREFLIES_CAN_SPAWN,
         BlockTags.GROWS_TREES,
         BlockTags.CAVES_CUT_THROUGH,
         BlockTags.CAVE_GEN_REPLACES_SURFACE
      );
   public static final Block<? extends BlockLogic> GRASS_RETRO = register("grass.retro", "minecraft:block/grass_retro", 201, b -> new BlockLogicGrass(b, Blocks.DIRT))
      .withSound(BlockSounds.GRASS)
      .withHardness(0.6F)
      .withTags(
         BlockTags.MINEABLE_BY_SHOVEL,
         BlockTags.GROWS_FLOWERS,
         BlockTags.GROWS_SUGAR_CANE,
         BlockTags.PASSIVE_MOBS_SPAWN,
         BlockTags.FIREFLIES_CAN_SPAWN,
         BlockTags.GROWS_TREES,
         BlockTags.CAVES_CUT_THROUGH,
         BlockTags.CAVE_GEN_REPLACES_SURFACE
      );
   public static final Block<? extends BlockLogic> GRASS_SCORCHED = register(
         "grass.scorched", "minecraft:block/grass_scorched", 202, b -> new BlockLogicGrass(b, Blocks.DIRT_SCORCHED)
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.6F)
      .withOverrideColor(MaterialColor.grassScorched)
      .withTags(
         BlockTags.MINEABLE_BY_SHOVEL,
         BlockTags.GROWS_FLOWERS,
         BlockTags.GROWS_SUGAR_CANE,
         BlockTags.GROWS_TREES,
         BlockTags.CAVES_CUT_THROUGH,
         BlockTags.PASSIVE_MOBS_SPAWN,
         BlockTags.FIREFLIES_CAN_SPAWN,
         BlockTags.GROWS_CACTI,
         BlockTags.CAVE_GEN_REPLACES_SURFACE
      );
   public static final Block<BlockLogicPathDirt> PATH_DIRT = register("path.dirt", "minecraft:block/path_dirt", 210, BlockLogicPathDirt::new)
      .withSound(BlockSounds.GRAVEL)
      .withHardness(0.5F)
      .withLitInteriorSurface(true)
      .withOverrideColor(MaterialColor.grassScorched)
      .withTags(BlockTags.MINEABLE_BY_SHOVEL);
   public static final Block<? extends BlockLogic> DIRT = register("dirt", "minecraft:block/dirt", 220, b -> new BlockLogic(b, Material.dirt))
      .withSound(BlockSounds.GRAVEL)
      .withHardness(0.5F)
      .withTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.GROWS_FLOWERS, BlockTags.GROWS_SUGAR_CANE, BlockTags.GROWS_TREES, BlockTags.CAVES_CUT_THROUGH);
   public static final Block<? extends BlockLogic> DIRT_SCORCHED = register("dirt.scorched", "minecraft:block/dirt_scorched", 221, b -> new BlockLogic(b, Material.dirt))
      .withSound(BlockSounds.GRAVEL)
      .withHardness(0.5F)
      .withOverrideColor(MaterialColor.dirtScorched)
      .withTags(
         BlockTags.MINEABLE_BY_SHOVEL,
         BlockTags.GROWS_FLOWERS,
         BlockTags.GROWS_SUGAR_CANE,
         BlockTags.GROWS_CACTI,
         BlockTags.GROWS_TREES,
         BlockTags.CAVES_CUT_THROUGH
      );
   public static final Block<? extends BlockLogic> DIRT_SCORCHED_RICH = register(
         "dirt.scorched.rich", "minecraft:block/dirt_scorched_rich", 222, b -> new BlockLogic(b, Material.dirt)
      )
      .withSound(BlockSounds.GRAVEL)
      .withHardness(0.6F)
      .withOverrideColor(MaterialColor.dirtScorched)
      .withTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.GROWS_FLOWERS, BlockTags.GROWS_SUGAR_CANE, BlockTags.GROWS_CACTI, BlockTags.CAVES_CUT_THROUGH);
   public static final Block<? extends BlockLogic> MUD = register("mud", "minecraft:block/mud", 225, b -> new BlockLogicMud(b, Material.dirt, false))
      .withSound(BlockSounds.GRAVEL)
      .withHardness(0.6F)
      .withOverrideColor(MaterialColor.mud)
      .withTags(
         BlockTags.MINEABLE_BY_SHOVEL,
         BlockTags.GROWS_FLOWERS,
         BlockTags.GROWS_CACTI,
         BlockTags.GROWS_TREES,
         BlockTags.CAVES_CUT_THROUGH,
         BlockTags.GROWS_SUGAR_CANE
      );
   public static final Block<? extends BlockLogic> MUD_BAKED = register("mud.baked", "minecraft:block/mud_baked", 226, b -> new BlockLogicMud(b, Material.steel, true))
      .withSound(BlockSounds.STONE)
      .withHardness(1.5F)
      .withOverrideColor(MaterialColor.dirt)
      .withTags(
         BlockTags.MINEABLE_BY_PICKAXE,
         BlockTags.GROWS_FLOWERS,
         BlockTags.GROWS_CACTI,
         BlockTags.GROWS_TREES,
         BlockTags.CAVES_CUT_THROUGH,
         BlockTags.GROWS_SUGAR_CANE
      );
   public static final Block<? extends BlockLogic> SPONGE_DRY = register("sponge.dry", "minecraft:block/sponge_dry", 230, b -> new BlockLogicSponge(b, false))
      .withSound(BlockSounds.GRASS)
      .withHardness(0.6F)
      .withOverrideColor(MaterialColor.paintedYellow)
      .withTags(BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<? extends BlockLogic> SPONGE_WET = register("sponge.wet", "minecraft:block/sponge_wet", 231, b -> new BlockLogicSponge(b, true))
      .withSound(BlockSounds.GRASS)
      .withHardness(1.0F)
      .withOverrideColor(MaterialColor.paintedYellow)
      .withTags(BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<? extends BlockLogic> PUMICE_DRY = register("pumice.dry", "minecraft:block/pumice_dry", 232, b -> new BlockLogicPumice(b, false))
      .withSound(BlockSounds.STONE)
      .withHardness(0.6F)
      .withOverrideColor(MaterialColor.fire)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> PUMICE_WET = register("pumice.wet", "minecraft:block/pumice_wet", 233, b -> new BlockLogicPumice(b, true))
      .withSound(BlockSounds.STONE)
      .withHardness(1.0F)
      .withLightEmission(0.625F)
      .withOverrideColor(MaterialColor.fire)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> MOSS_STONE = register("moss.stone", "minecraft:block/moss_stone", 240, b -> new BlockLogicMoss(b, STONE))
      .withSound(BlockSounds.STONE)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.GROWS_FLOWERS, BlockTags.GROWS_SUGAR_CANE);
   public static final Block<? extends BlockLogic> MOSS_BASALT = register("moss.basalt", "minecraft:block/moss_basalt", 241, b -> new BlockLogicMoss(b, BASALT))
      .withSound(BlockSounds.STONE)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.GROWS_FLOWERS, BlockTags.GROWS_SUGAR_CANE);
   public static final Block<? extends BlockLogic> MOSS_LIMESTONE = register("moss.limestone", "minecraft:block/moss_limestone", 242, b -> new BlockLogicMoss(b, LIMESTONE))
      .withSound(BlockSounds.STONE)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.GROWS_FLOWERS, BlockTags.GROWS_SUGAR_CANE);
   public static final Block<? extends BlockLogic> MOSS_GRANITE = register("moss.granite", "minecraft:block/moss_granite", 243, b -> new BlockLogicMoss(b, GRANITE))
      .withSound(BlockSounds.STONE)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.GROWS_FLOWERS, BlockTags.GROWS_SUGAR_CANE);
   public static final Block<BlockLogicSand> SAND = register("sand", "minecraft:block/sand", 250, BlockLogicSand::new)
      .withSound(BlockSounds.SAND)
      .withHardness(0.5F)
      .withTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.GROWS_SUGAR_CANE, BlockTags.GROWS_CACTI, BlockTags.CAVES_CUT_THROUGH, BlockTags.FIREFLIES_CAN_SPAWN);
   public static final Block<BlockLogicGravel> GRAVEL = register("gravel", "minecraft:block/gravel", 251, BlockLogicGravel::new)
      .withSound(BlockSounds.GRAVEL)
      .withHardness(0.6F)
      .withTags(BlockTags.MINEABLE_BY_SHOVEL);
   public static final Block<? extends BlockLogic> BEDROCK = register("bedrock", "minecraft:block/bedrock", 260, b -> new BlockLogicBedrock(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withSetUnbreakable()
      .withBlastResistance(6000000.0F)
      .withImmovableFlagSet()
      .withTags(BlockTags.PISTON_CRUSHING);
   public static final Block<? extends BlockLogic> BONESHALE = register("boneshale", "minecraft:block/boneshale", 261, b -> new BlockLogicBoneShale(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(16.0F)
      .withBlastResistance(6000000.0F)
      .withImmovableFlagSet()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NETHER_MOBS_SPAWN);
   public static final Block<? extends BlockLogic> FLUID_WATER_FLOWING = register(
         "fluid.water.flowing", "minecraft:block/fluid_water_flowing", 270, b -> new BlockLogicFluidFlowing(b, Material.water, Blocks.FLUID_WATER_STILL)
      )
      .withHardness(100.0F)
      .withLightBlock(3)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withDisabledStats()
      .withTags(BlockTags.IS_WATER, BlockTags.PLACE_OVERWRITES, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> FLUID_WATER_STILL = register(
         "fluid.water.still", "minecraft:block/fluid_water_still", 271, b -> new BlockLogicFluidStill(b, Material.water, FLUID_WATER_FLOWING)
      )
      .withHardness(100.0F)
      .withLightBlock(3)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withDisabledStats()
      .setStatParent(() -> FLUID_WATER_FLOWING)
      .withTags(BlockTags.IS_WATER, BlockTags.PLACE_OVERWRITES, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> FLUID_LAVA_FLOWING = register(
         "fluid.lava.flowing", "minecraft:block/fluid_lava_flowing", 272, b -> new BlockLogicFluidFlowing(b, Material.lava, Blocks.FLUID_LAVA_STILL)
      )
      .withHardness(0.0F)
      .withLightEmission(1.0F)
      .withLightBlock(255)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withDisabledStats()
      .withTags(BlockTags.IS_LAVA, BlockTags.PLACE_OVERWRITES, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> FLUID_LAVA_STILL = register(
         "fluid.lava.still", "minecraft:block/fluid_lava_still", 273, b -> new BlockLogicFluidStill(b, Material.lava, FLUID_LAVA_FLOWING)
      )
      .withHardness(100.0F)
      .withLightEmission(1.0F)
      .withLightBlock(255)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withDisabledStats()
      .setStatParent(() -> FLUID_LAVA_FLOWING)
      .withTags(BlockTags.IS_LAVA, BlockTags.PLACE_OVERWRITES, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<BlockLogicLog> LOG_OAK = register("log.oak", "minecraft:block/log_oak", 280, BlockLogicLog::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicLog> LOG_PINE = register("log.pine", "minecraft:block/log_pine", 281, BlockLogicLog::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicLog> LOG_BIRCH = register("log.birch", "minecraft:block/log_birch", 282, BlockLogicLog::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicLog> LOG_CHERRY = register("log.cherry", "minecraft:block/log_cherry", 283, BlockLogicLog::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicLog> LOG_EUCALYPTUS = register("log.eucalyptus", "minecraft:block/log_eucalyptus", 284, BlockLogicLog::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicLog> LOG_OAK_MOSSY = register("log.oak.mossy", "minecraft:block/log_oak_mossy", 285, BlockLogicLog::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE, BlockTags.GROWS_FLOWERS);
   public static final Block<BlockLogicLog> LOG_THORN = register("log.thorn", "minecraft:block/log_thorn", 286, BlockLogicLog::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicLog> LOG_PALM = register("log.palm", "minecraft:block/log_palm", 287, BlockLogicLog::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicLeavesOak> LEAVES_OAK = register("leaves.oak", "minecraft:block/leaves_oak", 290, BlockLogicLeavesOak::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.2F)
      .withLightBlock(1)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.MINEABLE_BY_AXE, BlockTags.MINEABLE_BY_HOE, BlockTags.MINEABLE_BY_SWORD, BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<? extends BlockLogic> LEAVES_OAK_RETRO = register(
         "leaves.oak.retro", "minecraft:block/leaves_oak_retro", 291, b -> new BlockLogicLeavesBase(b, Material.leaves, Blocks.SAPLING_OAK_RETRO)
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.2F)
      .withLightBlock(1)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.MINEABLE_BY_AXE, BlockTags.MINEABLE_BY_HOE, BlockTags.MINEABLE_BY_SWORD, BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<BlockLogicLeavesPine> LEAVES_PINE = register("leaves.pine", "minecraft:block/leaves_pine", 292, BlockLogicLeavesPine::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.2F)
      .withLightBlock(1)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.pineLeaves)
      .withTags(BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.MINEABLE_BY_AXE, BlockTags.MINEABLE_BY_HOE, BlockTags.MINEABLE_BY_SWORD, BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<? extends BlockLogic> LEAVES_BIRCH = register(
         "leaves.birch", "minecraft:block/leaves_birch", 293, b -> new BlockLogicLeavesBase(b, Material.leaves, Blocks.SAPLING_BIRCH)
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.2F)
      .withLightBlock(1)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.birchLeaves)
      .withTags(BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.MINEABLE_BY_AXE, BlockTags.MINEABLE_BY_HOE, BlockTags.MINEABLE_BY_SWORD, BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<BlockLogicLeavesCherry> LEAVES_CHERRY = register("leaves.cherry", "minecraft:block/leaves_cherry", 294, BlockLogicLeavesCherry::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.2F)
      .withLightBlock(1)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.cherryLeaves)
      .withTags(BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.MINEABLE_BY_AXE, BlockTags.MINEABLE_BY_HOE, BlockTags.MINEABLE_BY_SWORD, BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<BlockLogicLeavesEucalyptus> LEAVES_EUCALYPTUS = register("leaves.eucalyptus", "minecraft:block/leaves_eucalyptus", 295, BlockLogicLeavesEucalyptus::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.2F)
      .withLightBlock(1)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.MINEABLE_BY_AXE, BlockTags.MINEABLE_BY_HOE, BlockTags.MINEABLE_BY_SWORD, BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<BlockLogicLeavesShrub> LEAVES_SHRUB = register("leaves.shrub", "minecraft:block/leaves_shrub", 296, BlockLogicLeavesShrub::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.2F)
      .withLightBlock(1)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.MINEABLE_BY_AXE, BlockTags.MINEABLE_BY_HOE, BlockTags.MINEABLE_BY_SWORD, BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<BlockLogicLeavesCherryFlowering> LEAVES_CHERRY_FLOWERING = register(
         "leaves.cherry.flowering", "minecraft:block/leaves_cherry_flowering", 297, BlockLogicLeavesCherryFlowering::new
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.2F)
      .withLightBlock(1)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.cherryLeaves)
      .withTags(BlockTags.MINEABLE_BY_AXE, BlockTags.MINEABLE_BY_HOE, BlockTags.MINEABLE_BY_SWORD, BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<BlockLogicLeavesCacao> LEAVES_CACAO = register("leaves.cacao", "minecraft:block/leaves_cacao", 298, BlockLogicLeavesCacao::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.2F)
      .withLightBlock(1)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.MINEABLE_BY_AXE, BlockTags.MINEABLE_BY_HOE, BlockTags.MINEABLE_BY_SWORD, BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<? extends BlockLogic> LEAVES_THORN = register(
         "leaves.thorn", "minecraft:block/leaves_thorn", 299, b -> new BlockLogicLeavesBase(b, Material.leaves, Blocks.SAPLING_THORN)
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.2F)
      .withLightBlock(1)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.thornLeaves)
      .withTags(BlockTags.MINEABLE_BY_AXE, BlockTags.MINEABLE_BY_HOE, BlockTags.MINEABLE_BY_SWORD, BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<? extends BlockLogic> LEAVES_PALM = register(
         "leaves.palm", "minecraft:block/leaves_palm", 300, b -> new BlockLogicLeavesBase(b, Material.leaves, Blocks.SAPLING_PALM)
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.2F)
      .withLightBlock(1)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.palmLeaves)
      .withTags(BlockTags.MINEABLE_BY_AXE, BlockTags.MINEABLE_BY_HOE, BlockTags.MINEABLE_BY_SWORD, BlockTags.MINEABLE_BY_SHEARS);
   public static final Block<BlockLogicSaplingOak> SAPLING_OAK = register("sapling.oak", "minecraft:block/sapling_oak", 310, BlockLogicSaplingOak::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR);
   public static final Block<BlockLogicSaplingRetro> SAPLING_OAK_RETRO = register("sapling.oak.retro", "minecraft:block/sapling_oak_retro", 311, BlockLogicSaplingRetro::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR);
   public static final Block<BlockLogicSaplingPine> SAPLING_PINE = register("sapling.pine", "minecraft:block/sapling_pine", 312, BlockLogicSaplingPine::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR);
   public static final Block<BlockLogicSaplingBirch> SAPLING_BIRCH = register("sapling.birch", "minecraft:block/sapling_birch", 313, BlockLogicSaplingBirch::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR);
   public static final Block<BlockLogicSaplingCherry> SAPLING_CHERRY = register("sapling.cherry", "minecraft:block/sapling_cherry", 314, BlockLogicSaplingCherry::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR);
   public static final Block<BlockLogicSaplingEucalyptus> SAPLING_EUCALYPTUS = register("sapling.eucalyptus", "minecraft:block/sapling_eucalyptus", 315, BlockLogicSaplingEucalyptus::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR);
   public static final Block<BlockLogicSaplingShrub> SAPLING_SHRUB = register("sapling.shrub", "minecraft:block/sapling_shrub", 316, BlockLogicSaplingShrub::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR);
   public static final Block<BlockLogicSaplingCacao> SAPLING_CACAO = register("sapling.cacao", "minecraft:block/sapling_cacao", 317, BlockLogicSaplingCacao::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR);
   public static final Block<BlockLogicSaplingThorn> SAPLING_THORN = register("sapling.thorn", "minecraft:block/sapling_thorn", 318, BlockLogicSaplingThorn::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR);
   public static final Block<BlockLogicSaplingPalm> SAPLING_PALM = register("sapling.palm", "minecraft:block/sapling_palm", 319, BlockLogicSaplingPalm::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR);
   public static final Block<? extends BlockLogic> TALLGRASS = register("tallgrass", "minecraft:block/tallgrass", 320, b -> new BlockLogicTallGrass(b).setKilledByWeather())
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLACE_OVERWRITES, BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.SHEEPS_FAVOURITE_BLOCK);
   public static final Block<? extends BlockLogic> TALLGRASS_FERN = register(
         "tallgrass.fern", "minecraft:block/tallgrass_fern", 321, b -> new BlockLogicTallGrass(b).setKilledByWeather()
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withTags(
         BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLACE_OVERWRITES, BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.PLANTABLE_IN_JAR, BlockTags.SHEEPS_FAVOURITE_BLOCK
      );
   public static final Block<BlockLogicDeadBush> DEADBUSH = register("deadbush", "minecraft:block/deadbush", 322, BlockLogicDeadBush::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withOverrideColor(MaterialColor.wood)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLACE_OVERWRITES, BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.PLANTABLE_IN_JAR);
   public static final Block<BlockLogicSpinifex> SPINIFEX = register("spinifex", "minecraft:block/spinifex", 323, BlockLogicSpinifex::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLACE_OVERWRITES, BlockTags.SHEARS_DO_SILK_TOUCH);
   public static final Block<? extends BlockLogic> ALGAE = register("algae", "minecraft:block/algae", 324, b -> new BlockLogicAlgae(b, Material.plant))
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withLitInteriorSurface(true)
      .setBlockItem(ItemBlockAlgae::new)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLACE_OVERWRITES, BlockTags.SHEARS_DO_SILK_TOUCH);
   public static final Block<? extends BlockLogic> FLOWER_YELLOW = register(
         "flower.yellow",
         "minecraft:block/flower_yellow",
         330,
         b -> (BlockLogicFlowerStackable)new BlockLogicFlowerStackable(b).setKilledByWeather().setBonemealable()
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withOverrideColor(MaterialColor.paintedYellow)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR, BlockTags.SHEEPS_FAVOURITE_BLOCK, BlockTags.SHEARS_DO_SILK_TOUCH);
   public static final Block<? extends BlockLogic> FLOWER_RED = register(
         "flower.red",
         "minecraft:block/flower_red",
         331,
         b -> (BlockLogicFlowerStackable)new BlockLogicFlowerStackable(b).setKilledByWeather().setBonemealable()
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withOverrideColor(MaterialColor.paintedRed)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR, BlockTags.SHEEPS_FAVOURITE_BLOCK, BlockTags.SHEARS_DO_SILK_TOUCH);
   public static final Block<? extends BlockLogic> FLOWER_PINK = register(
         "flower.pink",
         "minecraft:block/flower_pink",
         332,
         b -> (BlockLogicFlowerStackable)new BlockLogicFlowerStackable(b).setKilledByWeather().setBonemealable()
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withOverrideColor(MaterialColor.paintedPink)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR, BlockTags.SHEEPS_FAVOURITE_BLOCK, BlockTags.SHEARS_DO_SILK_TOUCH);
   public static final Block<? extends BlockLogic> FLOWER_PURPLE = register(
         "flower.purple",
         "minecraft:block/flower_purple",
         333,
         b -> (BlockLogicFlowerStackable)new BlockLogicFlowerStackable(b).setKilledByWeather().setBonemealable()
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withOverrideColor(MaterialColor.paintedPurple)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR, BlockTags.SHEEPS_FAVOURITE_BLOCK, BlockTags.SHEARS_DO_SILK_TOUCH);
   public static final Block<? extends BlockLogic> FLOWER_LIGHT_BLUE = register(
         "flower.lightblue",
         "minecraft:block/flower_light_blue",
         334,
         b -> (BlockLogicFlowerStackable)new BlockLogicFlowerStackable(b).setKilledByWeather().setBonemealable()
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withOverrideColor(MaterialColor.paintedLightblue)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR, BlockTags.SHEEPS_FAVOURITE_BLOCK, BlockTags.SHEARS_DO_SILK_TOUCH);
   public static final Block<? extends BlockLogic> FLOWER_ORANGE = register(
         "flower.orange",
         "minecraft:block/flower_orange",
         335,
         b -> (BlockLogicFlowerStackable)new BlockLogicFlowerStackable(b).setKilledByWeather().setBonemealable()
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withOverrideColor(MaterialColor.paintedOrange)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR, BlockTags.SHEEPS_FAVOURITE_BLOCK, BlockTags.SHEARS_DO_SILK_TOUCH);
   public static final Block<? extends BlockLogic> MUSHROOM_BROWN = register(
         "mushroom.brown", "minecraft:block/mushroom_brown", 340, b -> new BlockLogicMushroom(b).setBonemealable()
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withLightEmission(0.125F)
      .withOverrideColor(MaterialColor.paintedBrown)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR, BlockTags.PIGS_FAVOURITE_BLOCK);
   public static final Block<? extends BlockLogic> MUSHROOM_RED = register("mushroom.red", "minecraft:block/mushroom_red", 341, b -> new BlockLogicMushroom(b).setBonemealable())
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withOverrideColor(MaterialColor.paintedRed)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLANTABLE_IN_JAR, BlockTags.PIGS_FAVOURITE_BLOCK);
   public static final Block<? extends BlockLogic> ORE_COAL_STONE = register(
         "ore.coal.stone", "minecraft:block/ore_coal_stone", 350, b -> new BlockLogicOreCoal(b, STONE, Material.stone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_COAL_BASALT = register(
         "ore.coal.basalt", "minecraft:block/ore_coal_basalt", 351, b -> new BlockLogicOreCoal(b, BASALT, Material.basalt)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_COAL_LIMESTONE = register(
         "ore.coal.limestone", "minecraft:block/ore_coal_limestone", 352, b -> new BlockLogicOreCoal(b, LIMESTONE, Material.limestone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_COAL_GRANITE = register(
         "ore.coal.granite", "minecraft:block/ore_coal_granite", 353, b -> new BlockLogicOreCoal(b, GRANITE, Material.granite)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_COAL_PERMAFROST = register(
         "ore.coal.permafrost", "minecraft:block/ore_coal_permafrost", 354, b -> new BlockLogicOreCoal(b, PERMAFROST, Material.permafrost)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_IRON_STONE = register(
         "ore.iron.stone", "minecraft:block/ore_iron_stone", 360, b -> new BlockLogicOreIron(b, STONE, Material.stone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_IRON_BASALT = register(
         "ore.iron.basalt", "minecraft:block/ore_iron_basalt", 361, b -> new BlockLogicOreIron(b, BASALT, Material.basalt)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_IRON_LIMESTONE = register(
         "ore.iron.limestone", "minecraft:block/ore_iron_limestone", 362, b -> new BlockLogicOreIron(b, LIMESTONE, Material.limestone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_IRON_GRANITE = register(
         "ore.iron.granite", "minecraft:block/ore_iron_granite", 363, b -> new BlockLogicOreIron(b, GRANITE, Material.granite)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_IRON_PERMAFROST = register(
         "ore.iron.permafrost", "minecraft:block/ore_iron_permafrost", 364, b -> new BlockLogicOreIron(b, PERMAFROST, Material.permafrost)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_GOLD_STONE = register(
         "ore.gold.stone", "minecraft:block/ore_gold_stone", 370, b -> new BlockLogicOreGold(b, STONE, Material.stone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_GOLD_BASALT = register(
         "ore.gold.basalt", "minecraft:block/ore_gold_basalt", 371, b -> new BlockLogicOreGold(b, BASALT, Material.basalt)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_GOLD_LIMESTONE = register(
         "ore.gold.limestone", "minecraft:block/ore_gold_limestone", 372, b -> new BlockLogicOreGold(b, LIMESTONE, Material.limestone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_GOLD_GRANITE = register(
         "ore.gold.granite", "minecraft:block/ore_gold_granite", 373, b -> new BlockLogicOreGold(b, GRANITE, Material.granite)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_GOLD_PERMAFROST = register(
         "ore.gold.permafrost", "minecraft:block/ore_gold_permafrost", 374, b -> new BlockLogicOreGold(b, PERMAFROST, Material.permafrost)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_LAPIS_STONE = register(
         "ore.lapis.stone", "minecraft:block/ore_lapis_stone", 380, b -> new BlockLogicOreLapis(b, STONE, Material.stone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_LAPIS_BASALT = register(
         "ore.lapis.basalt", "minecraft:block/ore_lapis_basalt", 381, b -> new BlockLogicOreLapis(b, BASALT, Material.basalt)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_LAPIS_LIMESTONE = register(
         "ore.lapis.limestone", "minecraft:block/ore_lapis_limestone", 382, b -> new BlockLogicOreLapis(b, LIMESTONE, Material.limestone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_LAPIS_GRANITE = register(
         "ore.lapis.granite", "minecraft:block/ore_lapis_granite", 383, b -> new BlockLogicOreLapis(b, GRANITE, Material.granite)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_LAPIS_PERMAFROST = register(
         "ore.lapis.permafrost", "minecraft:block/ore_lapis_permafrost", 384, b -> new BlockLogicOreLapis(b, PERMAFROST, Material.permafrost)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_REDSTONE_STONE = register(
         "ore.redstone.stone",
         "minecraft:block/ore_redstone_stone",
         390,
         b -> new BlockLogicOreRedstone(b, STONE, Material.stone, false, null, null)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_REDSTONE_BASALT = register(
         "ore.redstone.basalt",
         "minecraft:block/ore_redstone_basalt",
         391,
         b -> new BlockLogicOreRedstone(b, BASALT, Material.basalt, false, null, null)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_REDSTONE_LIMESTONE = register(
         "ore.redstone.limestone",
         "minecraft:block/ore_redstone_limestone",
         392,
         b -> new BlockLogicOreRedstone(b, LIMESTONE, Material.limestone, false, null, null)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_REDSTONE_GRANITE = register(
         "ore.redstone.granite",
         "minecraft:block/ore_redstone_granite",
         393,
         b -> new BlockLogicOreRedstone(b, GRANITE, Material.granite, false, null, null)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_REDSTONE_PERMAFROST = register(
         "ore.redstone.permafrost",
         "minecraft:block/ore_redstone_permafrost",
         394,
         b -> new BlockLogicOreRedstone(b, PERMAFROST, Material.permafrost, false, null, null)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withHardness(3.0F)
      .withBlastResistance(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_REDSTONE_GLOWING_STONE = register(
         "ore.redstone.glowing.stone",
         "minecraft:block/ore_redstone_glowing_stone",
         400,
         b -> new BlockLogicOreRedstone(b, null, Material.stone, true, null, null)
      )
      .withSound(BlockSounds.STONE)
      .withLightEmission(0.2F)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> ORE_REDSTONE_STONE)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_REDSTONE_GLOWING_BASALT = register(
         "ore.redstone.glowing.basalt",
         "minecraft:block/ore_redstone_glowing_basalt",
         401,
         b -> new BlockLogicOreRedstone(b, null, Material.basalt, true, null, null)
      )
      .withSound(BlockSounds.STONE)
      .withLightEmission(0.2F)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> ORE_REDSTONE_BASALT)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_REDSTONE_GLOWING_LIMESTONE = register(
         "ore.redstone.glowing.limestone",
         "minecraft:block/ore_redstone_glowing_limestone",
         402,
         b -> new BlockLogicOreRedstone(b, null, Material.limestone, true, null, null)
      )
      .withSound(BlockSounds.STONE)
      .withLightEmission(0.2F)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> ORE_REDSTONE_LIMESTONE)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_REDSTONE_GLOWING_GRANITE = register(
         "ore.redstone.glowing.granite",
         "minecraft:block/ore_redstone_glowing_granite",
         403,
         b -> new BlockLogicOreRedstone(b, null, Material.granite, true, null, null)
      )
      .withSound(BlockSounds.STONE)
      .withLightEmission(0.2F)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> ORE_REDSTONE_GRANITE)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_REDSTONE_GLOWING_PERMAFROST = register(
         "ore.redstone.glowing.permafrost",
         "minecraft:block/ore_redstone_glowing_permafrost",
         404,
         b -> new BlockLogicOreRedstone(b, null, Material.permafrost, true, null, null)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withLightEmission(0.2F)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> ORE_REDSTONE_PERMAFROST)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_DIAMOND_STONE = register(
         "ore.diamond.stone", "minecraft:block/ore_diamond_stone", 410, b -> new BlockLogicOreDiamond(b, STONE, Material.stone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_DIAMOND_BASALT = register(
         "ore.diamond.basalt", "minecraft:block/ore_diamond_basalt", 411, b -> new BlockLogicOreDiamond(b, BASALT, Material.basalt)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_DIAMOND_LIMESTONE = register(
         "ore.diamond.limestone", "minecraft:block/ore_diamond_limestone", 412, b -> new BlockLogicOreDiamond(b, LIMESTONE, Material.limestone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_DIAMOND_GRANITE = register(
         "ore.diamond.granite", "minecraft:block/ore_diamond_granite", 413, b -> new BlockLogicOreDiamond(b, GRANITE, Material.granite)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> ORE_DIAMOND_PERMAFROST = register(
         "ore.diamond.permafrost", "minecraft:block/ore_diamond_permafrost", 414, b -> new BlockLogicOreDiamond(b, PERMAFROST, Material.permafrost)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicOreNetherCoal> ORE_NETHERCOAL_NETHERRACK = register(
         "ore.nethercoal.netherrack", "minecraft:block/ore_nethercoal_netherrack", 420, BlockLogicOreNetherCoal::new
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withLightEmission(0.625F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NETHER_MOBS_SPAWN);
   public static final Block<? extends BlockLogic> BLOCK_COAL = register("block.coal", "minecraft:block/block_coal", 430, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.coal)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BLOCK_IRON = register("block.iron", "minecraft:block/block_iron", 431, b -> new BlockLogic(b, Material.metal))
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.iron)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BLOCK_GOLD = register("block.gold", "minecraft:block/block_gold", 432, b -> new BlockLogic(b, Material.metal))
      .withSound(BlockSounds.METAL)
      .withHardness(3.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.gold)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BLOCK_LAPIS = register("block.lapis", "minecraft:block/block_lapis", 433, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(5.0F)
      .withOverrideColor(MaterialColor.lapis)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BLOCK_REDSTONE = register(
         "block.redstone", "minecraft:block/block_redstone", 434, b -> new BlockLogicRedstone(b, Material.stone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(5.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.redstone)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BLOCK_DIAMOND = register("block.diamond", "minecraft:block/block_diamond", 435, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(5.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.diamond)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BLOCK_NETHER_COAL = register(
         "block.nethercoal", "minecraft:block/block_nethercoal", 436, b -> new BlockLogic(b, Material.netherrack)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BLOCK_STEEL = register("block.steel", "minecraft:block/block_steel", 437, b -> new BlockLogic(b, Material.steel))
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withBlastResistance(2000.0F)
      .withOverrideColor(MaterialColor.steel)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BLOCK_QUARTZ = register("block.quartz", "minecraft:block/block_quartz", 438, b -> new BlockLogic(b, Material.steel))
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.quartz)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BLOCK_OLIVINE = register("block.olivine", "minecraft:block/block_olivine", 439, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.olivine)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BLOCK_CHARCOAL = register("block.charcoal", "minecraft:block/block_charcoal", 440, b -> new BlockLogic(b, Material.stone))
      .withSound(BlockSounds.STONE)
      .withHardness(3.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.coal)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicWireRedstone> WIRE_REDSTONE = register("wire.redstone", "minecraft:block/wire_redstone", 450, BlockLogicWireRedstone::new)
      .withSound(BlockSounds.STONE)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.redstone)
      .setStatParent(() -> Items.DUST_REDSTONE)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<? extends BlockLogic> TORCH_REDSTONE_IDLE = register(
         "torch.redstone.idle", "minecraft:block/torch_redstone_idle", 460, b -> new BlockLogicTorchRedstone(b, false)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.redstone)
      .setStatParent(() -> Blocks.TORCH_REDSTONE_ACTIVE)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<? extends BlockLogic> TORCH_REDSTONE_ACTIVE = register(
         "torch.redstone.active", "minecraft:block/torch_redstone_active", 461, b -> new BlockLogicTorchRedstone(b, true)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(0.0F)
      .withLightEmission(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.redstone)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<BlockLogicButton> BUTTON_STONE = register("button.stone", "minecraft:block/button_stone", 470, BlockLogicButton::new)
      .withSound(BlockSounds.STONE)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<BlockLogicButtonPlanks> BUTTON_PLANKS = register(
         "button.planks", "minecraft:block/button_planks", 471, BlockLogicButtonPlanks::new
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_AXE, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<BlockLogicButtonPainted> BUTTON_PLANKS_PAINTED = register(
         "button.planks.painted", "minecraft:block/button_planks_painted", 472, BlockLogicButtonPainted::new
      )
      .setBlockItem(b -> new ItemBlockPainted<>(b, true))
      .withSound(BlockSounds.WOOD)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_AXE, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<BlockLogicLever> LEVER_COBBLE_STONE = register("lever.cobble.stone", "minecraft:block/lever_cobble_stone", 480, BlockLogicLever::new)
      .withSound(BlockSounds.STONE)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<? extends BlockLogic> PRESSURE_PLATE_STONE = register(
         "pressureplate.stone", "minecraft:block/pressure_plate_stone", 490, b -> new BlockLogicPressurePlate<>(b, Mob.class, Material.stone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<? extends BlockLogic> PRESSURE_PLATE_PLANKS_OAK = register(
         "pressureplate.planks.oak", "minecraft:block/pressure_plate_planks_oak", 491, b -> new BlockLogicPressurePlate<>(b, Entity.class, Material.wood)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_AXE, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<? extends BlockLogic> PRESSURE_PLATE_COBBLE_STONE = register(
         "pressureplate.cobble.stone", "minecraft:block/pressure_plate_cobble_stone", 492, b -> new BlockLogicPressurePlate<>(b, Player.class, Material.stone)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<? extends BlockLogic> PRESSURE_PLATE_PLANKS_OAK_PAINTED = register(
         "pressureplate.planks.oak.painted",
         "minecraft:block/pressure_plate_planks_oak_painted",
         493,
         b -> new BlockLogicPressurePlatePainted<>(b, Entity.class, Material.wood)
      )
      .setBlockItem(b -> new ItemBlockPainted<>(b, true))
      .withSound(BlockSounds.WOOD)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_AXE, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<? extends BlockLogic> MOTION_SENSOR_IDLE = register(
         "motionsensor.idle", "minecraft:block/motion_sensor_idle", 500, b -> new BlockLogicMotionSensor(b, false)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.stone)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> MOTION_SENSOR_ACTIVE = register(
         "motionsensor.active", "minecraft:block/motion_sensor_active", 501, b -> new BlockLogicMotionSensor(b, true)
      )
      .withSound(BlockSounds.STONE)
      .withLightEmission(0.3F)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.stone)
      .setStatParent(() -> MOTION_SENSOR_IDLE)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> REPEATER_IDLE = register(
         "repeater.idle", "minecraft:block/repeater_idle", 510, b -> new BlockLogicRepeater(b, false)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.0F)
      .withDisabledStats()
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.redstone)
      .setStatParent(() -> Items.REPEATER)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<? extends BlockLogic> REPEATER_ACTIVE = register(
         "repeater.active", "minecraft:block/repeater_active", 511, b -> new BlockLogicRepeater(b, true)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.0F)
      .withLightEmission(0.625F)
      .withDisabledStats()
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.redstone)
      .setStatParent(() -> REPEATER_IDLE)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<? extends BlockLogic> PISTON_BASE = register(
         "piston.base", "minecraft:block/piston_base", 520, b -> new BlockLogicPistonBase(b, 12)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> PISTON_BASE_STICKY = register(
         "piston.base.sticky", "minecraft:block/piston_base_sticky", 521, b -> new BlockLogicPistonBaseSticky(b, 12)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> PISTON_HEAD = register(
         "piston.head", "minecraft:block/piston_head", 522, b -> new BlockLogicPistonHead(b, 0.25, 0.25)
      )
      .withSound(BlockSounds.WOOD)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withDisabledStats()
      .withImmovableFlagSet()
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicPistonMoving> PISTON_MOVING = register(
         "piston.moving", "minecraft:block/piston_moving", 523, BlockLogicPistonMoving::new
      )
      .withSound(BlockSounds.STONE)
      .withDisabledStats()
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> PISTON_BASE_STEEL = register(
         "piston.base.steel", "minecraft:block/piston_base_steel", 524, b -> new BlockLogicPistonBaseSteel(b, 24)
      )
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withBlastResistance(2000.0F)
      .withOverrideColor(MaterialColor.steel)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> PISTON_HEAD_STEEL = register(
         "piston.head.steel", "minecraft:block/piston_head_steel", 525, b -> new BlockLogicPistonHead(b, 0.375, 0.5)
      )
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withBlastResistance(2000.0F)
      .withOverrideColor(MaterialColor.steel)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withDisabledStats()
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicNote> NOTEBLOCK = register("noteblock", "minecraft:block/noteblock", 530, BlockLogicNote::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(0.8F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> RAIL = register("rail", "minecraft:block/rail", 540, b -> new BlockLogicRail(b, false))
      .withSound(BlockSounds.METAL)
      .withHardness(0.7F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<? extends BlockLogic> RAIL_POWERED = register("rail.powered", "minecraft:block/rail_powered", 541, b -> new BlockLogicRail(b, true))
      .withSound(BlockSounds.METAL)
      .withHardness(0.7F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<BlockLogicRailDetector> RAIL_DETECTOR = register("rail.detector", "minecraft:block/rail_detector", 542, BlockLogicRailDetector::new)
      .withSound(BlockSounds.METAL)
      .withHardness(0.7F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.PREVENT_MOB_SPAWNS);
   public static final Block<? extends BlockLogic> SPIKES = register("spikes", "minecraft:block/spikes", 550, b -> new BlockLogicSpikes(b, Material.metal))
      .withSound(BlockSounds.METAL)
      .withLightBlock(3)
      .withHardness(2.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicDispenser> DISPENSER_COBBLE_STONE = register(
         "dispenser.cobble.stone", "minecraft:block/dispenser_cobble_stone", 560, BlockLogicDispenser::new
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicActivator> ACTIVATOR_COBBLE_NETHERRACK = register(
         "activator.cobble.netherrack", "minecraft:block/activator_cobble_netherrack", 561, BlockLogicActivator::new
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> TRAPDOOR_PLANKS_OAK = register(
         "trapdoor.planks.oak", "minecraft:block/trapdoor_planks_oak", 570, b -> new BlockLogicTrapDoor(b, Material.wood)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(3.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> TRAPDOOR_IRON = register("trapdoor.iron", "minecraft:block/trapdoor_iron", 571, b -> new BlockLogicTrapDoor(b, Material.metal))
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.iron)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> TRAPDOOR_GLASS = register(
         "trapdoor.glass", "minecraft:block/trapdoor_glass", 572, b -> new BlockLogicTrapDoor(b, Material.glass)
      )
      .withSound(BlockSounds.GLASS)
      .withHardness(0.3F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> TRAPDOOR_PLANKS_PAINTED = register(
         "trapdoor.planks.oak.painted", "minecraft:block/trapdoor_planks_oak_painted", 573, b -> new BlockLogicTrapDoorPainted(b, Material.wood)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(3.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setBlockItem(b -> new ItemBlockPainted<>(b, true))
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> TRAPDOOR_STEEL = register(
         "trapdoor.steel", "minecraft:block/trapdoor_steel", 574, b -> new BlockLogicTrapDoor(b, Material.steel)
      )
      .withSound(BlockSounds.METAL)
      .withOverrideColor(MaterialColor.steel)
      .withHardness(5.0F)
      .withBlastResistance(2000.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.steel)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicTNT> TNT = register("tnt", "minecraft:block/tnt", 580, BlockLogicTNT::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F);
   public static final Block<? extends BlockLogic> DOOR_PLANKS_OAK_BOTTOM = register(
         "door.planks.oak.bottom", "minecraft:block/door_planks_oak_bottom", 590, b -> new BlockLogicDoor(b, Material.wood, false, false, () -> Items.DOOR_OAK)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(3.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.DOOR_OAK)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> DOOR_PLANKS_OAK_TOP = register(
         "door.planks.oak.top", "minecraft:block/doors_planks_oak_top", 591, b -> new BlockLogicDoor(b, Material.wood, true, false, () -> Items.DOOR_OAK)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(3.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> DOOR_PLANKS_OAK_BOTTOM)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> DOOR_IRON_BOTTOM = register(
         "door.iron.bottom", "minecraft:block/door_iron_bottom", 592, b -> new BlockLogicDoor(b, Material.metal, false, true, () -> Items.DOOR_IRON)
      )
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.iron)
      .setStatParent(() -> Items.DOOR_IRON)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> DOOR_IRON_TOP = register(
         "door.iron.top", "minecraft:block/door_iron_top", 593, b -> new BlockLogicDoor(b, Material.metal, true, true, () -> Items.DOOR_IRON)
      )
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.iron)
      .setStatParent(() -> DOOR_IRON_BOTTOM)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> DOOR_PLANKS_PAINTED_BOTTOM = register(
         "door.planks.oak.painted.bottom", "minecraft:block/door_planks_oak_painted_bottom", 594, b -> new BlockLogicDoorPainted(b, Material.wood, false)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(3.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.DOOR_OAK_PAINTED)
      .setBlockItem(b -> new ItemBlockPainted<>(b, true))
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> DOOR_PLANKS_PAINTED_TOP = register(
         "door.planks.oak.painted.top", "minecraft:block/door_planks_oak_painted_top", 595, b -> new BlockLogicDoorPainted(b, Material.wood, true)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(3.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> DOOR_PLANKS_PAINTED_BOTTOM)
      .setBlockItem(b -> new ItemBlockPainted<>(b, true))
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> DOOR_GLASS_BOTTOM = register(
         "door.glass.bottom", "minecraft:block/door_glass_bottom", 596, b -> new BlockLogicDoor(b, Material.glass, false, false, () -> Items.DOOR_GLASS)
      )
      .withSound(BlockSounds.GLASS)
      .withHardness(0.3F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.DOOR_GLASS)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> DOOR_GLASS_TOP = register(
         "door.glass.top", "minecraft:block/door_glass_top", 597, b -> new BlockLogicDoor(b, Material.glass, true, false, () -> Items.DOOR_GLASS)
      )
      .withSound(BlockSounds.GLASS)
      .withHardness(0.3F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> DOOR_GLASS_BOTTOM)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> DOOR_STEEL_BOTTOM = register(
         "door.steel.bottom", "minecraft:block/door_steel_bottom", 598, b -> new BlockLogicDoor(b, Material.steel, false, true, () -> Items.DOOR_STEEL)
      )
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withOverrideColor(MaterialColor.steel)
      .withBlastResistance(2000.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.steel)
      .setStatParent(() -> Items.DOOR_STEEL)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> DOOR_STEEL_TOP = register(
         "door.steel.top", "minecraft:block/door_steel_top", 599, b -> new BlockLogicDoor(b, Material.steel, true, true, () -> Items.DOOR_STEEL)
      )
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withOverrideColor(MaterialColor.steel)
      .withBlastResistance(2000.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.steel)
      .setStatParent(() -> DOOR_STEEL_BOTTOM)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicMesh> MESH = register("mesh", "minecraft:block/mesh", 600, BlockLogicMesh::new)
      .withSound(BlockSounds.METAL)
      .withHardness(1.5F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.CAN_HANG_OFF);
   public static final Block<BlockLogicMeshGold> MESH_GOLD = register("mesh.gold", "minecraft:block/mesh_gold", 601, BlockLogicMeshGold::new)
      .withSound(BlockSounds.METAL)
      .withHardness(1.0F)
      .withOverrideColor(MaterialColor.gold)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.CAN_HANG_OFF);
   public static final Block<BlockLogicBed> BED = register("bed", "minecraft:block/bed", 610, BlockLogicBed::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(0.2F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .setStatParent(() -> Items.BED)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicSeat> SEAT = register("seat", "minecraft:block/seat", 611, BlockLogicSeat::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(0.2F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.SEAT)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> COBWEB = register("cobweb", "minecraft:block/cobweb", 620, b -> new BlockLogicCobweb(b, Material.web))
      .withSound(BlockSounds.CLOTH)
      .withLightBlock(1)
      .withHardness(4.0F)
      .withTags(BlockTags.SHEARS_DO_SILK_TOUCH, BlockTags.MINEABLE_BY_SHEARS, BlockTags.MINEABLE_BY_SWORD);
   public static final Block<BlockLogicFire> FIRE = register("fire", "minecraft:block/fire", 630, BlockLogicFire::new)
      .withSound(BlockSounds.FIRE)
      .withHardness(0.0F)
      .withLightEmission(1.0F)
      .withDisabledStats()
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLACE_OVERWRITES, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> BRAZIER_INACTIVE = register(
         "brazier.inactive", "minecraft:block/brazier_inactive", 631, b -> new BlockLogicBrazier(b, false)
      )
      .withSound(BlockSounds.METAL)
      .withHardness(1.5F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> BRAZIER_ACTIVE = register(
         "brazier.active", "minecraft:block/brazier_active", 632, b -> new BlockLogicBrazier(b, true)
      )
      .withSound(BlockSounds.FIRE)
      .withHardness(1.5F)
      .withLightEmission(1.0F)
      .setStatParent(() -> BRAZIER_INACTIVE)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicMobSpawner> MOBSPAWNER = register("mobspawner", "minecraft:block/mobspawner", 640, BlockLogicMobSpawner::new)
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withImmovableFlagSet()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicMobSpawnerDeactivated> MOBSPAWNER_DEACTIVATED = register(
         "mobspawner.deactivated", "minecraft:block/mobspawner_deactivated", 641, BlockLogicMobSpawnerDeactivated::new
      )
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicWorkbench> WORKBENCH = register("workbench", "minecraft:block/workbench", 650, BlockLogicWorkbench::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.5F)
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> FURNACE_STONE_IDLE = register(
         "furnace.stone.idle", "minecraft:block/furnace_stone_idle", 660, b -> new BlockLogicFurnace(b, false)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> FURNACE_STONE_ACTIVE = register(
         "furnace.stone.active", "minecraft:block/furnace_stone_active", 661, b -> new BlockLogicFurnace(b, true)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.5F)
      .withLightEmission(0.875F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> FURNACE_STONE_IDLE)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> FURNACE_BLAST_IDLE = register(
         "furnace.blast.idle", "minecraft:block/furnace_blast_idle", 662, b -> new BlockLogicFurnaceBlast(b, false)
      )
      .withSound(BlockSounds.METAL)
      .withHardness(3.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.steel)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> FURNACE_BLAST_ACTIVE = register(
         "furnace.blast.active", "minecraft:block/furnace_blast_active", 663, b -> new BlockLogicFurnaceBlast(b, true)
      )
      .withSound(BlockSounds.METAL)
      .withHardness(3.5F)
      .withLightEmission(0.875F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.steel)
      .setStatParent(() -> FURNACE_BLAST_IDLE)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> TROMMEL_IDLE = register(
         "trommel.idle", "minecraft:block/trommel_idle", 670, b -> new BlockLogicTrommel(b, Material.stone, false)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> TROMMEL_ACTIVE = register(
         "trommel.active", "minecraft:block/trommel_active", 671, b -> new BlockLogicTrommel(b, Material.stone, true)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(3.5F)
      .withLightEmission(0.875F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> TROMMEL_IDLE)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicChestLegacy> CHEST_LEGACY = register("chest.legacy", "minecraft:block/chest_legacy", 680, BlockLogicChestLegacy::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.5F)
      .withBlastResistance(5.0F)
      .setStatParent(() -> Blocks.CHEST_PLANKS_OAK)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicChestLegacy> CHEST_LEGACY_PAINTED = register(
         "chest.legacy.painted", "minecraft:block/chest_legacy_painted", 681, BlockLogicChestLegacy::new
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(2.5F)
      .withBlastResistance(5.0F)
      .setStatParent(() -> Blocks.CHEST_PLANKS_OAK_PAINTED)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> CHEST_PLANKS_OAK = register(
         "chest.planks.oak", "minecraft:block/chest_planks_oak", 682, b -> new BlockLogicChest(b, Material.wood)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(2.5F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> CHEST_PLANKS_OAK_PAINTED = register(
         "chest.planks.oak.painted", "minecraft:block/chest_planks_oak_painted", 683, b -> new BlockLogicChestPainted(b, Material.wood)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(2.5F)
      .withBlastResistance(5.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setBlockItem(b -> new ItemBlockPainted<>(b, true))
      .withTags(BlockTags.FENCES_CONNECT, BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicCropsWheat> CROPS_WHEAT = register("crops.wheat", "minecraft:block/crops_wheat", 690, BlockLogicCropsWheat::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.SEEDS_WHEAT)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<BlockLogicCropsPumpkin> CROPS_PUMPKIN = register("crops.pumpkin", "minecraft:block/crops_pumpkin", 691, BlockLogicCropsPumpkin::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .setStatParent(() -> Items.SEEDS_PUMPKIN)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.OVERRIDE_STEPSOUND);
   public static final Block<BlockLogicFarmland> FARMLAND_DIRT = register("farmland.dirt", "minecraft:block/farmland_dirt", 700, BlockLogicFarmland::new)
      .withSound(BlockSounds.GRAVEL)
      .withHardness(0.6F)
      .withLitInteriorSurface(true)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_SHOVEL);
   public static final Block<? extends BlockLogic> SIGN_POST_PLANKS_OAK = register(
         "sign.post.planks.oak", "minecraft:block/sign_post_planks_oak", 710, b -> new BlockLogicSign(b, true)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(1.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.SIGN)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> SIGN_WALL_PLANKS_OAK = register(
         "sign.wall.planks.oak", "minecraft:block/sign_wall_planks_oak", 711, b -> new BlockLogicSign(b, false)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(1.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> SIGN_POST_PLANKS_OAK)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> FLAG = register("flag", "minecraft:block/flag", 712, b -> new BlockLogicFlag(b, Material.wood))
      .withSound(BlockSounds.WOOD)
      .withHardness(1.0F)
      .setStatParent(() -> Items.FLAG)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> SIGN_POST_PLANKS_OAK_PAINTED = register(
         "sign.post.planks.oak.painted", "minecraft:block/sign_post_planks_oak_painted", 713, b -> new BlockLogicSignPainted(b, true)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(1.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.SIGN)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> SIGN_WALL_PLANKS_OAK_PAINTED = register(
         "sign.wall.planks.oak.painted", "minecraft:block/sign_wall_planks_oak_painted", 714, b -> new BlockLogicSignPainted(b, false)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(1.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> SIGN_POST_PLANKS_OAK)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> LAYER_SNOW = register(
         "layer.snow", "minecraft:block/layer_snow", 720, b -> new BlockLogicLayerSnow(b, Blocks.BLOCK_SNOW, Material.topSnow)
      )
      .withSound(BlockSounds.CLOTH)
      .withHardness(0.1F)
      .withLitInteriorSurface(true)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLACE_OVERWRITES, BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND);
   public static final Block<? extends BlockLogic> LAYER_LEAVES_OAK = register(
         "layer.leaves.oak", "minecraft:block/layer_leaves_oak", 721, b -> new BlockLogicLayerLeaves(b, LEAVES_OAK, Material.leaves)
      )
      .withSound(BlockSounds.GRASS)
      .withLitInteriorSurface(true)
      .withTags(
         BlockTags.BROKEN_BY_FLUIDS,
         BlockTags.PLACE_OVERWRITES,
         BlockTags.SHEARS_DO_SILK_TOUCH,
         BlockTags.MINEABLE_BY_SHEARS,
         BlockTags.MINEABLE_BY_SWORD,
         BlockTags.MINEABLE_BY_AXE,
         BlockTags.MINEABLE_BY_HOE
      );
   public static final Block<? extends BlockLogic> LAYER_SLATE = register(
         "layer.slate", "minecraft:block/layer_slate", 722, b -> new BlockLogicLayerSlate(b, SLATE, Material.slate)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicIce> ICE = register("ice", "minecraft:block/ice", 730, BlockLogicIce::new)
      .withSound(BlockSounds.GLASS)
      .withHardness(0.5F)
      .withLightBlock(3)
      .withTags(BlockTags.SKATEABLE, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> PERMAICE = register("ice.perma", "minecraft:block/permaice", 731, b -> new BlockLogicSlippery(b, Material.ice))
      .withSound(BlockSounds.GLASS)
      .withHardness(1.5F)
      .withBlastResistance(10.0F)
      .withTags(BlockTags.SKATEABLE, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicSnow> BLOCK_SNOW = register("block.snow", "minecraft:block/block_snow", 740, BlockLogicSnow::new)
      .withSound(BlockSounds.CLOTH)
      .withHardness(0.2F)
      .withTags(BlockTags.CAVES_CUT_THROUGH, BlockTags.MINEABLE_BY_SHOVEL, BlockTags.FIREFLIES_CAN_SPAWN);
   public static final Block<BlockLogicCactus> CACTUS = register("cactus", "minecraft:block/cactus", 750, BlockLogicCactus::new)
      .withSound(BlockSounds.CLOTH)
      .withHardness(0.4F)
      .withTags(BlockTags.GROWS_CACTI, BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_SHEARS, BlockTags.MINEABLE_BY_HOE);
   public static final Block<BlockLogicClay> BLOCK_CLAY = register("block.clay", "minecraft:block/block_clay", 760, BlockLogicClay::new)
      .withSound(BlockSounds.GRAVEL)
      .withHardness(0.6F)
      .withTags(BlockTags.MINEABLE_BY_SHOVEL);
   public static final Block<BlockLogicSugarcane> SUGARCANE = register("sugarcane", "minecraft:block/sugarcane", 770, BlockLogicSugarcane::new)
      .withSound(BlockSounds.GRASS)
      .withHardness(0.0F)
      .withOverrideColor(MaterialColor.paintedLime)
      .setStatParent(() -> Items.SUGARCANE)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> BLOCK_SUGARCANE = register(
         "block.sugarcane", "minecraft:block/block_sugarcane", 771, b -> new BlockLogicAxisAligned(b, Material.moss)
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.6F)
      .withOverrideColor(MaterialColor.paintedLime)
      .withTags(BlockTags.MINEABLE_BY_AXE, BlockTags.FENCES_CONNECT);
   public static final Block<? extends BlockLogic> BLOCK_SUGARCANE_BAKED = register(
         "block.sugarcane.baked", "minecraft:block/block_sugarcane_baked", 772, b -> new BlockLogicAxisAligned(b, Material.moss)
      )
      .withSound(BlockSounds.GRASS)
      .withHardness(0.6F)
      .withOverrideColor(MaterialColor.wood)
      .withTags(BlockTags.MINEABLE_BY_AXE, BlockTags.FENCES_CONNECT);
   public static final Block<BlockLogicJukebox> JUKEBOX = register("jukebox", "minecraft:block/jukebox", 780, BlockLogicJukebox::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> PUMPKIN = register("pumpkin", "minecraft:block/pumpkin", 790, b -> new BlockLogicPumpkin(b, true))
      .withSound(BlockSounds.WOOD)
      .withHardness(1.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> PUMPKIN_CARVED_IDLE = register(
         "pumpkin.carved.idle", "minecraft:block/pumpkin_carved_idle", 791, b -> new BlockLogicPumpkin(b, false)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(1.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> PUMPKIN_CARVED_ACTIVE = register(
         "pumpkin.carved.active", "minecraft:block/pumpkin_carved_active", 792, b -> new BlockLogicPumpkin(b, false)
      )
      .withSound(BlockSounds.WOOD)
      .withHardness(1.0F)
      .withLightEmission(1.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<BlockLogicPumpkinRedstone> PUMPKIN_REDSTONE = register("pumpkin.redstone", "minecraft:block/pumpkin_redstone", 793, BlockLogicPumpkinRedstone::new)
      .withSound(BlockSounds.WOOD)
      .withHardness(1.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> COBBLE_NETHERRACK = register(
         "cobble.netherrack", "minecraft:block/cobble_netherrack", 800, b -> new BlockLogicCobble(b, Material.netherrack, null)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.4F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.INFINITE_BURN, BlockTags.CAVES_CUT_THROUGH, BlockTags.NETHER_MOBS_SPAWN);
   public static final Block<BlockLogicNetherrackIgneous> COBBLE_NETHERRACK_IGNEOUS = register(
         "cobble.netherrack.igneous", "minecraft:block/cobble_netherrack_igneous", 801, BlockLogicNetherrackIgneous::new
      )
      .withSound(BlockSounds.STONE)
      .withHardness(1.25F)
      .withBlastResistance(7.0F)
      .withLightEmission(10)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> COBBLE_NETHERRACK_MOSSY = register(
         "cobble.netherrack.mossy", "minecraft:block/cobble_netherrack_mossy", 802, b -> new BlockLogicCobble(b, Material.netherrack, null)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.4F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.GROWS_FLOWERS, BlockTags.INFINITE_BURN);
   public static final Block<? extends BlockLogic> NETHERRACK = register(
         "netherrack", "minecraft:block/netherrack", 803, b -> new BlockLogicStone(b, COBBLE_NETHERRACK, Material.netherrack)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.32F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.INFINITE_BURN, BlockTags.CAVES_CUT_THROUGH, BlockTags.NETHER_MOBS_SPAWN);
   public static final Block<? extends BlockLogic> BRICK_NETHERRACK = register(
         "brick.netherrack", "minecraft:block/brick_netherrack", 804, b -> new BlockLogic(b, Material.netherrack)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.4F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_NETHERRACK_POLISHED = register(
         "slab.netherrack.carved", "minecraft:block/slab_netherrack_carved", 805, b -> new BlockLogicSlab(b, NETHERRACK_CARVED)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.4F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_COBBLE_NETHERRACK = register(
         "slab.cobble.netherrack", "minecraft:block/slab_cobble_netherrack", 806, b -> new BlockLogicSlab(b, COBBLE_NETHERRACK)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.4F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.INFINITE_BURN);
   public static final Block<? extends BlockLogic> SLAB_BRICK_NETHERRACK = register(
         "slab.brick.netherrack", "minecraft:block/slab_brick_netherrack", 807, b -> new BlockLogicSlab(b, BRICK_NETHERRACK)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.4F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_COBBLE_NETHERRACK = register(
         "stairs.cobble.netherrack", "minecraft:block/stairs_cobble_netherrack", 808, b -> new BlockLogicStairs(b, COBBLE_NETHERRACK)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.4F)
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_NETHERRACK = register(
         "stairs.brick.netherrack", "minecraft:block/stairs_brick_netherrack", 809, b -> new BlockLogicStairs(b, BRICK_NETHERRACK)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.4F)
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogicSoulSand> SOULSAND = register("soulsand", "minecraft:block/soulsand", 810, BlockLogicSoulSand::new)
      .withSound(BlockSounds.SAND)
      .withHardness(0.5F)
      .withOverrideColor(MaterialColor.paintedBrown)
      .withTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.CAVES_CUT_THROUGH, BlockTags.NETHER_MOBS_SPAWN);
   public static final Block<? extends BlockLogic> SOULSCHIST = register(
         "soulschist", "minecraft:block/soulschist", 811, b -> new BlockLogicCobble(b, Material.stone, () -> SOULSAND)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(2.0F)
      .withBlastResistance(10.0F)
      .withOverrideColor(MaterialColor.paintedBrown)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NETHER_MOBS_SPAWN);
   public static final Block<? extends BlockLogic> GLOWSTONE = register("glowstone", "minecraft:block/glowstone", 820, b -> new BlockLogicGlowStone(b, Material.stone))
      .withSound(BlockSounds.GLASS)
      .withHardness(0.3F)
      .withLightEmission(1.0F)
      .withOverrideColor(MaterialColor.paintedYellow)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<BlockLogic> PORTAL_NETHER = register(
         "portal.nether", "minecraft:block/portal_nether", 830, b -> new BlockLogicPortal(b, Dimension.NETHER, OBSIDIAN, FIRE)
      )
      .withSound(BlockSounds.GLASS)
      .withHardness(-1.0F)
      .withLightEmission(0.75F)
      .withOverrideColor(MaterialColor.paintedPurple)
      .withDisabledStats()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> PORTAL_PARADISE = register(
         "portal.paradise", "minecraft:block/portal_paradise", 831, b -> new BlockLogicPortal(b, Dimension.PARADISE, GLOWSTONE, BEDROCK)
      )
      .withSound(BlockSounds.GLASS)
      .withHardness(-1.0F)
      .withLightEmission(0.75F)
      .withOverrideColor(MaterialColor.paintedPurple)
      .withDisabledStats()
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<BlockLogicCake> CAKE = register("cake", "minecraft:block/cake", 840, BlockLogicCake::new)
      .withSound(BlockSounds.CLOTH)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.FOOD_CAKE)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<BlockLogicPiePumpkin> PUMPKIN_PIE = register("pumpkin_pie", "minecraft:block/pumpkin_pie", 841, BlockLogicPiePumpkin::new)
      .withSound(BlockSounds.CLOTH)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withOverrideColor(MaterialColor.paintedOrange)
      .setStatParent(() -> Items.FOOD_PUMPKIN_PIE)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> LAMP_IDLE = register("lamp.idle", "minecraft:block/lamp_idle", 850, b -> new BlockLogicLamp(b, false, false))
      .withSound(BlockSounds.GLASS)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setBlockItem(ItemBlockLamp::new)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> LAMP_ACTIVE = register("lamp.active", "minecraft:block/lamp_active", 851, b -> new BlockLogicLamp(b, true, false))
      .withSound(BlockSounds.GLASS)
      .withLightEmission(0.9375F)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> LAMP_IDLE)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> LAMP_INVERTED_IDLE = register(
         "lamp.inverted.idle", "minecraft:block/lamp_inverted_idle", 852, b -> new BlockLogicLamp(b, false, true)
      )
      .withSound(BlockSounds.GLASS)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Blocks.LAMP_INVERTED_ACTIVE)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> LAMP_INVERTED_ACTIVE = register(
         "lamp.inverted.active", "minecraft:block/lamp_inverted_active", 853, b -> new BlockLogicLamp(b, true, true)
      )
      .withSound(BlockSounds.GLASS)
      .withLightEmission(0.9375F)
      .withHardness(0.5F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setBlockItem(ItemBlockLamp::new)
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> LANTERN_FIREFLY_GREEN = register(
         "lantern.firefly.green",
         "minecraft:block/lantern_firefly_green",
         870,
         b -> new BlockLogicLanternFirefly(b, MobFireflyCluster.FireflyColor.GREEN, () -> Items.LANTERN_FIREFLY_GREEN)
      )
      .withSound(BlockSounds.GLASS)
      .withHardness(0.1F)
      .withLightEmission(0.9375F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.LANTERN_FIREFLY_GREEN)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> LANTERN_FIREFLY_BLUE = register(
         "lantern.firefly.blue",
         "minecraft:block/lantern_firefly_blue",
         871,
         b -> new BlockLogicLanternFirefly(b, MobFireflyCluster.FireflyColor.BLUE, () -> Items.LANTERN_FIREFLY_BLUE)
      )
      .withSound(BlockSounds.GLASS)
      .withHardness(0.1F)
      .withLightEmission(0.9375F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.LANTERN_FIREFLY_BLUE)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> LANTERN_FIREFLY_ORANGE = register(
         "lantern.firefly.orange",
         "minecraft:block/lantern_firefly_orange",
         872,
         b -> new BlockLogicLanternFirefly(b, MobFireflyCluster.FireflyColor.ORANGE, () -> Items.LANTERN_FIREFLY_ORANGE)
      )
      .withSound(BlockSounds.GLASS)
      .withHardness(0.1F)
      .withLightEmission(0.9375F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.LANTERN_FIREFLY_ORANGE)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> LANTERN_FIREFLY_RED = register(
         "lantern.firefly.red",
         "minecraft:block/lantern_firefly_red",
         873,
         b -> new BlockLogicLanternFirefly(b, MobFireflyCluster.FireflyColor.RED, () -> Items.LANTERN_FIREFLY_RED)
      )
      .withSound(BlockSounds.GLASS)
      .withHardness(0.1F)
      .withLightEmission(0.9375F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.LANTERN_FIREFLY_RED)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> JAR_GLASS = register("jar.glass", "minecraft:block/jar_glass", 874, b -> new BlockLogicJar(b, () -> Items.JAR))
      .withSound(BlockSounds.GLASS)
      .withHardness(0.1F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.JAR)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> OVERLAY_PEBBLES = register(
         "overlay.pebbles", "minecraft:block/overlay_pebbles", 880, b -> new BlockLogicOverlayPebbles(b, Material.decoration)
      )
      .withSound(BlockSounds.STONE)
      .withHardness(0.0F)
      .withDisabledNeighborNotifyOnMetadataChange()
      .setStatParent(() -> Items.AMMO_PEBBLE)
      .withTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> FENCE_CHAINLINK = register(
         "fence.chainlink", "minecraft:block/fence_chainlink", 890, b -> new BlockLogicFenceChainlink(b, Material.metal)
      )
      .withSound(BlockSounds.METAL)
      .withHardness(5.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.CHAINLINK_FENCES_CONNECT, BlockTags.CAN_HANG_OFF);
   public static final Block<? extends BlockLogic> FENCE_PAPER_WALL = register(
         "fence.paper_wall", "minecraft:block/fence_paper_wall", 891, b -> new BlockLogicFenceWallPaper(b, Material.cloth)
      )
      .withSound(BlockSounds.CLOTH)
      .withHardness(0.5F)
      .withTags(BlockTags.MINEABLE_BY_SHEARS, BlockTags.FENCES_CONNECT, BlockTags.CAN_HANG_OFF, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> FENCE_STEEL = register(
         "fence.steel", "minecraft:block/fence_steel", 892, b -> new BlockLogicFenceSteel(b, Material.steel)
      )
      .withSound(BlockSounds.METAL)
      .withHardness(8.0F)
      .withOverrideColor(MaterialColor.steel)
      .withBlastResistance(2000.0F)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.CHAINLINK_FENCES_CONNECT, BlockTags.CAN_HANG_OFF);
   public static final Block<? extends BlockLogic> BASKET = register("basket", "minecraft:block/basket", 900, b -> new BlockLogicBasket(b, Material.cloth))
      .withSound(BlockSounds.CLOTH)
      .withHardness(0.5F)
      .withOverrideColor(MaterialColor.wood)
      .setStatParent(() -> Items.BASKET)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withTags(BlockTags.NOT_IN_CREATIVE_MENU);
   public static final Block<? extends BlockLogic> PAPER_WALL = register("paper_wall", "minecraft:block/paper_wall", 910, b -> new BlockLogic(b, Material.cloth))
      .withSound(BlockSounds.CLOTH)
      .withHardness(0.7F)
      .withTags(BlockTags.MINEABLE_BY_SHEARS, BlockTags.FENCES_CONNECT, BlockTags.CAN_HANG_OFF, BlockTags.MINEABLE_BY_AXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_SANDSTONE = register(
         "slab.brick.sandstone", "minecraft:block/slab_brick_sandstone", 1000, b -> new BlockLogicSlab(b, BRICK_SANDSTONE)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_COBBLE_PERMAFROST = register(
         "slab.cobble.permafrost", "minecraft:block/slab_cobble_permafrost", 1001, b -> new BlockLogicSlab(b, COBBLE_PERMAFROST)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_PERMAFROST = register(
         "slab.brick.permafrost", "minecraft:block/slab_brick_permafrost", 1002, b -> new BlockLogicSlab(b, BRICK_PERMAFROST)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_PERMAFROST_POLISHED = register(
         "slab.permafrost.carved", "minecraft:block/slab_permafrost_carved", 1003, b -> new BlockLogicSlab(b, PERMAFROST_CARVED)
      )
      .withSound(BlockSounds.PERMAFROST)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_IRON = register(
         "slab.brick.iron", "minecraft:block/slab_brick_iron", 1004, b -> new BlockLogicSlab(b, BRICK_IRON)
      )
      .withSound(BlockSounds.METAL)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_GOLD = register(
         "slab.brick.gold", "minecraft:block/slab_brick_gold", 1005, b -> new BlockLogicSlab(b, BRICK_GOLD)
      )
      .withSound(BlockSounds.METAL)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_STEEL = register(
         "slab.brick.steel", "minecraft:block/slab_brick_steel", 1006, b -> new BlockLogicSlab(b, BRICK_STEEL)
      )
      .withSound(BlockSounds.METAL)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> SLAB_BRICK_LAPIS = register(
         "slab.brick.lapis", "minecraft:block/slab_brick_lapis", 1007, b -> new BlockLogicSlab(b, BRICK_LAPIS)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_IRON = register(
         "stairs.brick.iron", "minecraft:block/stairs_brick_iron", 1050, b -> new BlockLogicStairs(b, BRICK_IRON)
      )
      .withSound(BlockSounds.METAL)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_GOLD = register(
         "stairs.brick.gold", "minecraft:block/stairs_brick_gold", 1051, b -> new BlockLogicStairs(b, BRICK_GOLD)
      )
      .withSound(BlockSounds.METAL)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_STEEL = register(
         "stairs.brick.steel", "minecraft:block/stairs_brick_steel", 1052, b -> new BlockLogicStairs(b, BRICK_STEEL)
      )
      .withSound(BlockSounds.METAL)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   public static final Block<? extends BlockLogic> STAIRS_BRICK_LAPIS = register(
         "stairs.brick.lapis", "minecraft:block/stairs_brick_lapis", 1053, b -> new BlockLogicStairs(b, BRICK_LAPIS)
      )
      .withSound(BlockSounds.STONE)
      .withDisabledNeighborNotifyOnMetadataChange()
      .withLitInteriorSurface(true)
      .withTags(BlockTags.MINEABLE_BY_PICKAXE);
   private static boolean hasInit = false;

   public static <T extends BlockLogic> Block<T> register(String key, String namespaceId, int id, BlockLogicSupplier<T> logicSupplier) {
      Block<T> container = new Block<>(id, key, namespaceId, logicSupplier);
      if (blocksList[container.id()] != null) {
         throw new IllegalArgumentException(
            "Id '" + id + "' of block '" + container.namespaceId() + "' is already being used by '" + blocksList[container.id()].namespaceId() + "'!"
         );
      } else {
         blocksList[container.id()] = container;
         if (keyToIdMap.containsKey(container.getKey())) {
            throw new IllegalArgumentException(
               "Key '"
                  + container.getKey()
                  + "' of block '"
                  + container.namespaceId()
                  + "' is already being used by '"
                  + getBlock(keyToIdMap.get(container.getKey())).namespaceId()
                  + "'!"
            );
         } else {
            keyToIdMap.put(container.getKey(), container.id());
            if (blockMap.containsKey(container.namespaceId())) {
               throw new IllegalArgumentException(
                  "NamespaceId '"
                     + container.namespaceId()
                     + "' of block '"
                     + container.getKey()
                     + "' is already being used by '"
                     + blockMap.get(container.namespaceId()).namespaceId()
                     + "'!"
               );
            } else {
               blockMap.put(container.namespaceId(), container);
               if (highestBlockId < container.id()) {
                  highestBlockId = container.id();
               }

               return container;
            }
         }
      }
   }

   private static void resetCaches() {
      for (int i = 0; i < blocksList.length; i++) {
         Arrays.fill(solid, false);
         Arrays.fill(lightBlock, 0);
         Arrays.fill(translucent, false);
         Arrays.fill(isEntityTile, false);
         Arrays.fill(lightEmission, 0);
         Arrays.fill(lightBlock, 0);
         Arrays.fill(neighborNotifyOnMetadataChangeDisabled, false);
         Arrays.fill(shouldTick, false);
      }

      translucent[0] = true;
   }

   private static void cacheBlock(Block<?> container) {
      solid[container.id()] = container.isSolidRender();
      if (container.lightBlock == null) {
         container.lightBlock = !container.isSolidRender() && !container.blocksLight() ? 0 : 255;
      }

      lightBlock[container.id()] = container.lightBlock;
      translucent[container.id()] = !container.getMaterial().blocksLight();
      isEntityTile[container.id()] = container.isEntityTile;
      lightEmission[container.id()] = container.emission;
      neighborNotifyOnMetadataChangeDisabled[container.id()] = container.disabledNeighborMetaNotify;
      shouldTick[container.id()] = container.shouldTick;
   }

   public static void init() {
      if (!hasInit) {
         hasInit = true;
         resetCaches();

         for (Block<?> b : blocksList) {
            if (b != null) {
               b.init();
            }
         }

         linkRedstoneOre(ORE_REDSTONE_STONE, ORE_REDSTONE_GLOWING_STONE);
         linkRedstoneOre(ORE_REDSTONE_BASALT, ORE_REDSTONE_GLOWING_BASALT);
         linkRedstoneOre(ORE_REDSTONE_LIMESTONE, ORE_REDSTONE_GLOWING_LIMESTONE);
         linkRedstoneOre(ORE_REDSTONE_GRANITE, ORE_REDSTONE_GLOWING_GRANITE);
         linkRedstoneOre(ORE_REDSTONE_PERMAFROST, ORE_REDSTONE_GLOWING_PERMAFROST);

         BlockLogicMoss.initMossMap();

         for (int i = 0; i < blocksList.length; i++) {
            Block<?> block;
            if ((block = blocksList[i]) != null) {
               if (Item.itemsList[i] == null) {
                  Item item = block.blockItemSupplier.get();
                  if (block.statParent != null) {
                     item.setStatParent(block.statParent);
                  }

                  Item.itemsList[i] = item;
               }

               block.getLogic().initializeBlock();
            }
         }

         MaterialColor.assignManualEntries();

         for (Block<?> bx : blocksList) {
            if (bx != null) {
               cacheBlock(bx);
            }
         }
      }
   }

   @Nullable
   public static Block<?> getBlock(int id) {
      return blocksList[id];
   }

   public static boolean hasTag(int id, Tag<Block<?>> tag) {
      if (id < 0) {
         return false;
      } else {
         Block<?> block = blocksList[id];
         return block == null ? false : block.hasTag(tag);
      }
   }

   public static boolean hasTag(@Nullable Block<? extends BlockLogic> block, Tag<Block<?>> tag) {
      return block == null ? false : block.hasTag(tag);
   }
}
