package net.minecraft.core.net.command.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.Tag;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.WorldFeatureAlgae;
import net.minecraft.core.world.generate.feature.WorldFeatureCactus;
import net.minecraft.core.world.generate.feature.WorldFeatureClay;
import net.minecraft.core.world.generate.feature.WorldFeatureDeadBush;
import net.minecraft.core.world.generate.feature.WorldFeatureDungeon;
import net.minecraft.core.world.generate.feature.WorldFeatureFire;
import net.minecraft.core.world.generate.feature.WorldFeatureFlowers;
import net.minecraft.core.world.generate.feature.WorldFeatureGlowstoneA;
import net.minecraft.core.world.generate.feature.WorldFeatureGlowstoneB;
import net.minecraft.core.world.generate.feature.WorldFeatureGrassPatch;
import net.minecraft.core.world.generate.feature.WorldFeatureLabyrinth;
import net.minecraft.core.world.generate.feature.WorldFeatureLake;
import net.minecraft.core.world.generate.feature.WorldFeatureLiquid;
import net.minecraft.core.world.generate.feature.WorldFeatureMeadow;
import net.minecraft.core.world.generate.feature.WorldFeatureMudPatch;
import net.minecraft.core.world.generate.feature.WorldFeatureNetherLava;
import net.minecraft.core.world.generate.feature.WorldFeatureOre;
import net.minecraft.core.world.generate.feature.WorldFeaturePermaice;
import net.minecraft.core.world.generate.feature.WorldFeaturePumice;
import net.minecraft.core.world.generate.feature.WorldFeaturePumpkin;
import net.minecraft.core.world.generate.feature.WorldFeatureRichScorchedDirt;
import net.minecraft.core.world.generate.feature.WorldFeatureSpinifexPatch;
import net.minecraft.core.world.generate.feature.WorldFeatureSponge;
import net.minecraft.core.world.generate.feature.WorldFeatureSugarCane;
import net.minecraft.core.world.generate.feature.WorldFeatureSugarCaneTall;
import net.minecraft.core.world.generate.feature.WorldFeatureTallGrass;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureCherryTreeFancy;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeCaatinga;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeCherry;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeEucalyptus;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancyRainforest;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreePalm;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeShapeSwamp;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeShrub;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeTaigaBushy;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeTaigaTall;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeTall;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeThorn;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTree;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTreeBamboo;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTreeCone;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTreeMangrove;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTreeNormal;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTreePalm;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTreeProcedural;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTreeRainforest;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTreeRound;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTreeStick;
import org.jetbrains.annotations.NotNull;

public class CommandHelper {
   public static final Map<String, Class<? extends WorldFeature>> WORLD_FEATURES = new HashMap<>();
   public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> NO_SUGGESTIONS = (builder, consumer) -> builder.buildFuture();
   public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>> SUGGEST_BLOCKS = (builder, consumer) -> {
      String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);

      for (Block<?> block : Blocks.blocksList) {
         if (block != null) {
            getStringToSuggest(block.getKey(), remaining).ifPresent(builder::suggest);
         }
      }

      return builder.buildFuture();
   };

   public static CompletableFuture<Suggestions> suggest(String string, SuggestionsBuilder suggestionsBuilder) {
      String stringRemaining = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
      if (matchesSubStr(stringRemaining, string.toLowerCase(Locale.ROOT))) {
         suggestionsBuilder.suggest(string);
      }

      return suggestionsBuilder.buildFuture();
   }

   public static CompletableFuture<Suggestions> suggest(Iterable<String> iterable, SuggestionsBuilder suggestionsBuilder) {
      String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

      for (String string2 : iterable) {
         if (matchesSubStr(string, string2.toLowerCase(Locale.ROOT))) {
            suggestionsBuilder.suggest(string2);
         }
      }

      return suggestionsBuilder.buildFuture();
   }

   public static boolean matchesSubStr(String string, String string2) {
      for (int i = 0; !string2.startsWith(string, i); i++) {
         if ((i = string2.indexOf(95, i)) < 0) {
            return false;
         }
      }

      return true;
   }

   public static Optional<String> getStringToSuggest(String checkedString, String input) {
      if (checkedString.toLowerCase(Locale.ROOT).startsWith(input.toLowerCase(Locale.ROOT))) {
         return Optional.of(checkedString);
      } else if (checkedString.contains(":") && checkedString.length() - 1 >= checkedString.indexOf(58) + 1) {
         return checkedString.toLowerCase(Locale.ROOT).substring(checkedString.indexOf(58) + 1).startsWith(input.toLowerCase(Locale.ROOT))
            ? Optional.of(checkedString.substring(checkedString.indexOf(58) + 1))
            : Optional.empty();
      } else {
         return checkedString.contains(".")
               && checkedString.length() - 1 >= checkedString.indexOf(46) + 1
               && checkedString.toLowerCase(Locale.ROOT).substring(checkedString.indexOf(46) + 1).startsWith(input.toLowerCase(Locale.ROOT))
            ? Optional.of(checkedString.substring(checkedString.indexOf(46) + 1))
            : Optional.empty();
      }
   }

   public static boolean matchesKeyString(String checkedString, String input) {
      if (checkedString.equals(input)) {
         return true;
      } else {
         return checkedString.contains(":") && checkedString.length() - 1 >= checkedString.indexOf(58) + 1
            ? checkedString.substring(checkedString.indexOf(58) + 1).equals(input)
            : checkedString.contains(".")
               && checkedString.length() - 1 >= checkedString.indexOf(46) + 1
               && checkedString.substring(checkedString.indexOf(46) + 1).equals(input);
      }
   }

   public static boolean matchesNamespaceId(NamespaceID namespaceID, String input) {
      if (input.contains(":")) {
         return namespaceID.toString().equalsIgnoreCase(input);
      } else {
         return namespaceID.namespace().equals("minecraft") ? namespaceID.value().equalsIgnoreCase(input) : false;
      }
   }

   public static String getEntityName(Entity entity) {
      return Entity.getNameFromEntity(entity, true);
   }

   public static int getVolume(CommandSource source, IntegerCoordinates first, IntegerCoordinates second) throws CommandSyntaxException {
      return (int)(
         MathHelper.abs(first.getX(source) - second.getX(source))
            * MathHelper.abs(first.getY(source, true) - second.getY(source, true))
            * MathHelper.abs(first.getZ(source) - second.getZ(source))
      );
   }

   public static float linearInterpolation(float factor, float min, float max) {
      return min + factor * (max - min);
   }

   public static boolean blockEntitiesAreEqual(CompoundTag first, CompoundTag second) {
      if (first == null && second == null) {
         return true;
      } else if (first != null && second != null) {
         for (Entry<String, Tag<?>> entry : first.getValue().entrySet()) {
            if (!entry.getKey().equals("x")
               && !entry.getKey().equals("y")
               && !entry.getKey().equals("z")
               && (
                  !second.getValue().containsKey(entry.getKey())
                     || second.getValue().get(entry.getKey()) != entry.getValue()
                        && !second.getValue().get(entry.getKey()).equals(entry.getValue())
                        && !second.getValue().get(entry.getKey()).getValue().equals(entry.getValue().getValue())
               )) {
               return false;
            }
         }

         for (Entry<String, Tag<?>> entryx : second.getValue().entrySet()) {
            if (!entryx.getKey().equals("x")
               && !entryx.getKey().equals("y")
               && !entryx.getKey().equals("z")
               && (
                  !first.getValue().containsKey(entryx.getKey())
                     || first.getValue().get(entryx.getKey()) != entryx.getValue()
                        && !first.getValue().get(entryx.getKey()).equals(entryx.getValue())
                        && !first.getValue().get(entryx.getKey()).getValue().equals(entryx.getValue().getValue())
               )) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static void setTileEntity(WorldSource world, int x, int y, int z, CompoundTag tag) {
      if (tag != null && world.getTileEntity(x, y, z) != null) {
         tag.putInt("x", x);
         tag.putInt("y", y);
         tag.putInt("z", z);
         world.getTileEntity(x, y, z).readFromNBT(tag);
      }
   }

   public static void setTileEntity(WorldSource world, int x, int y, int z, TileEntity tileEntity) {
      setTileEntity(world, x, y, z, tagFrom(tileEntity));
   }

   public static CompoundTag tagFrom(TileEntity tileEntity) {
      CompoundTag tag = new CompoundTag();
      if (tileEntity != null) {
         tileEntity.writeToNBT(tag);
      }

      return tag;
   }

   public static void init() {
      registerWorldFeatureClass(WorldFeatureAlgae.class);
      registerWorldFeatureClass(WorldFeatureCactus.class);
      registerWorldFeatureClass(WorldFeatureClay.class);
      registerWorldFeatureClass(WorldFeatureDeadBush.class);
      registerWorldFeatureClass(WorldFeatureDungeon.class);
      registerWorldFeatureClass(WorldFeatureFire.class);
      registerWorldFeatureClass(WorldFeatureFlowers.class);
      registerWorldFeatureClass(WorldFeatureGlowstoneA.class);
      registerWorldFeatureClass(WorldFeatureGlowstoneB.class);
      registerWorldFeatureClass(WorldFeatureGrassPatch.class);
      registerWorldFeatureClass(WorldFeatureLabyrinth.class);
      registerWorldFeatureClass(WorldFeatureLake.class);
      registerWorldFeatureClass(WorldFeatureLiquid.class);
      registerWorldFeatureClass(WorldFeatureMeadow.class);
      registerWorldFeatureClass(WorldFeatureMudPatch.class);
      registerWorldFeatureClass(WorldFeatureNetherLava.class);
      registerWorldFeatureClass(WorldFeatureOre.class);
      registerWorldFeatureClass(WorldFeaturePermaice.class);
      registerWorldFeatureClass(WorldFeaturePumice.class);
      registerWorldFeatureClass(WorldFeaturePumpkin.class);
      registerWorldFeatureClass(WorldFeatureRichScorchedDirt.class);
      registerWorldFeatureClass(WorldFeatureSpinifexPatch.class);
      registerWorldFeatureClass(WorldFeatureSponge.class);
      registerWorldFeatureClass(WorldFeatureSugarCane.class);
      registerWorldFeatureClass(WorldFeatureSugarCaneTall.class);
      registerWorldFeatureClass(WorldFeatureTallGrass.class);
      registerWorldFeatureClass(WorldFeatureCherryTreeFancy.class);
      registerWorldFeatureClass(WorldFeatureTree.class);
      registerWorldFeatureClass(WorldFeatureTreeCaatinga.class);
      registerWorldFeatureClass(WorldFeatureTreeCherry.class);
      registerWorldFeatureClass(WorldFeatureTreeEucalyptus.class);
      registerWorldFeatureClass(WorldFeatureTreeFancy.class);
      registerWorldFeatureClass(WorldFeatureTreeFancyRainforest.class);
      registerWorldFeatureClass(WorldFeatureTreePalm.class);
      registerWorldFeatureClass(WorldFeatureTreeShapeSwamp.class);
      registerWorldFeatureClass(WorldFeatureTreeShrub.class);
      registerWorldFeatureClass(WorldFeatureTreeTaigaBushy.class);
      registerWorldFeatureClass(WorldFeatureTreeTaigaTall.class);
      registerWorldFeatureClass(WorldFeatureTreeTall.class);
      registerWorldFeatureClass(WorldFeatureTreeThorn.class);
      registerWorldFeatureClass(WorldFeatureSpoonerTree.class);
      registerWorldFeatureClass(WorldFeatureSpoonerTreeBamboo.class);
      registerWorldFeatureClass(WorldFeatureSpoonerTreeCone.class);
      registerWorldFeatureClass(WorldFeatureSpoonerTreeMangrove.class);
      registerWorldFeatureClass(WorldFeatureSpoonerTreeNormal.class);
      registerWorldFeatureClass(WorldFeatureSpoonerTreePalm.class);
      registerWorldFeatureClass(WorldFeatureSpoonerTreeProcedural.class);
      registerWorldFeatureClass(WorldFeatureSpoonerTreeRainforest.class);
      registerWorldFeatureClass(WorldFeatureSpoonerTreeRound.class);
      registerWorldFeatureClass(WorldFeatureSpoonerTreeStick.class);
   }

   public static <T extends WorldFeature> void registerWorldFeatureClass(@NotNull Class<T> tClass) {
      registerWorldFeatureClass(tClass, tClass.getSimpleName().substring(12));
   }

   public static <T extends WorldFeature> void registerWorldFeatureClass(@NotNull Class<T> tClass, @NotNull String name) {
      WORLD_FEATURES.put(name, tClass);
   }
}
