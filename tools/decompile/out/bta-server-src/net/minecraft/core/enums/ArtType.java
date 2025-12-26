package net.minecraft.core.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtType {
   public static Map<String, ArtType> map = new HashMap<>();
   public static List<ArtType> values = new ArrayList<>();
   private static int maxArtTitleLength;
   private static int maxArtKeyLength;
   public static final float KZ_SIZE = 512.0F;
   public static final ArtType Kebab = new ArtType("Kebab", "Kebab med tre pepperoni", "Kristoffer Zetterstrand", "minecraft:art/kebab", 16, 16);
   public static final ArtType Aztec = new ArtType("Aztec", "de_aztec", "Kristoffer Zetterstrand", "minecraft:art/aztec", 16, 16);
   public static final ArtType Alban = new ArtType("Alban", "Albanian", "Kristoffer Zetterstrand", "minecraft:art/alban", 16, 16);
   public static final ArtType Aztec2 = new ArtType("Aztec2", "de_aztec", "Kristoffer Zetterstrand", "minecraft:art/aztec2", 16, 16);
   public static final ArtType Bomb = new ArtType("Bomb", "Target successfully bombed", "Kristoffer Zetterstrand", "minecraft:art/bomb", 16, 16);
   public static final ArtType Plant = new ArtType("Plant", "Paradistrad", "Kristoffer Zetterstrand", "minecraft:art/plant", 16, 16);
   public static final ArtType Wasteland = new ArtType("Wasteland", "Wasteland", "Kristoffer Zetterstrand", "minecraft:art/wasteland", 16, 16);
   public static final ArtType Geology = new ArtType("Geology", "Geology", "Kristoffer Zetterstrand", "minecraft:art/geology", 16, 16);
   public static final ArtType CactusBird = new ArtType("CactusBird", "Cactus Bird", "Kristoffer Zetterstrand", "minecraft:art/cactus_bird", 16, 16);
   public static final ArtType Pool = new ArtType("Pool", "The pool", "Kristoffer Zetterstrand", "minecraft:art/pool", 32, 16);
   public static final ArtType Courbet = new ArtType("Courbet", "Bonjour monsieur Courbet", "Kristoffer Zetterstrand", "minecraft:art/courbet", 32, 16);
   public static final ArtType Sea = new ArtType("Sea", "Seaside", "Kristoffer Zetterstrand", "minecraft:art/sea", 32, 16);
   public static final ArtType Sunset = new ArtType("Sunset", "sunset_dense", "Kristoffer Zetterstrand", "minecraft:art/sunset", 32, 16);
   public static final ArtType Creebet = new ArtType("Creebet", "Seaside (Creebet)", "Kristoffer Zetterstrand", "minecraft:art/creebet", 32, 16);
   public static final ArtType Allegory = new ArtType("Allegory", "Allegory", "Kristoffer Zetterstrand", "minecraft:art/allegory", 32, 16);
   public static final ArtType Wanderer = new ArtType("Wanderer", "Wanderer", "Kristoffer Zetterstrand", "minecraft:art/wanderer", 16, 32);
   public static final ArtType Graham = new ArtType("Graham", "Graham", "Kristoffer Zetterstrand", "minecraft:art/graham", 16, 32);
   public static final ArtType Monologue = new ArtType("Monologue", "Monologue", "Kristoffer Zetterstrand", "minecraft:art/monologue", 16, 32);
   public static final ArtType Match = new ArtType("Match", "Match", "Kristoffer Zetterstrand", "minecraft:art/match", 32, 32);
   public static final ArtType Bust = new ArtType("Bust", "Bust", "Kristoffer Zetterstrand", "minecraft:art/bust", 32, 32);
   public static final ArtType Stage = new ArtType("Stage", "The stage is set", "Kristoffer Zetterstrand", "minecraft:art/stage", 32, 32);
   public static final ArtType Void = new ArtType("Void", "The Void", "Kristoffer Zetterstrand", "minecraft:art/void", 32, 32);
   public static final ArtType SkullAndRoses = new ArtType(
      "SkullAndRoses", "Moonlight Installation", "Kristoffer Zetterstrand", "minecraft:art/skull_and_roses", 32, 32
   );
   public static final ArtType Fighters = new ArtType("Fighters", "Fighters", "Kristoffer Zetterstrand", "minecraft:art/fighters", 64, 32);
   public static final ArtType TheBull = new ArtType("TheBull", "The Bull", "Kristoffer Zetterstrand", "minecraft:art/the_bull", 64, 32);
   public static final ArtType Dresses = new ArtType("Dresses", "Dresses", "Kristoffer Zetterstrand", "minecraft:art/dresses", 48, 48);
   public static final ArtType TheBlanket = new ArtType("TheBlanket", "The Blanket", "Kristoffer Zetterstrand", "minecraft:art/the_blanket", 48, 48);
   public static final ArtType DodFagel = new ArtType("DodFagel", "Dod Fagel", "Kristoffer Zetterstrand", "minecraft:art/dod_fagel", 48, 48);
   public static final ArtType Skeleton = new ArtType("Skeleton", "Mortal Coil", "Kristoffer Zetterstrand", "minecraft:art/skeleton", 64, 48);
   public static final ArtType DonkeyKong = new ArtType("DonkeyKong", "Kong", "Kristoffer Zetterstrand", "minecraft:art/donkey_kong", 64, 48);
   public static final ArtType DeathAndTheMaiden = new ArtType(
      "DeathAndTheMaiden", "Death and the Maiden", "Kristoffer Zetterstrand", "minecraft:art/death_and_the_maiden", 64, 48
   );
   public static final ArtType TheBox = new ArtType("TheBox", "The Box", "Kristoffer Zetterstrand", "minecraft:art/the_box", 48, 64);
   public static final ArtType TheGoldenApple = new ArtType(
      "TheGoldenApple", "The Golden Apple", "Kristoffer Zetterstrand", "minecraft:art/the_golden_apple", 48, 64
   );
   public static final ArtType Pointer = new ArtType("Pointer", "Pointer", "Kristoffer Zetterstrand", "minecraft:art/pointer", 64, 64);
   public static final ArtType Pigscene = new ArtType("Pigscene", "Pigscene", "Kristoffer Zetterstrand", "minecraft:art/pigscene", 64, 64);
   public static final ArtType BurningSkull = new ArtType("BurningSkull", "Skull on Fire", "Kristoffer Zetterstrand", "minecraft:art/burning_skull", 64, 64);
   public final String key;
   public final String title;
   public final String artist;
   public final String texture;
   public final int sizeX;
   public final int sizeY;

   public static int getMaxArtTitleLength() {
      return maxArtTitleLength;
   }

   public static int getMaxArtKeyLength() {
      return maxArtTitleLength;
   }

   public ArtType(String key, String title, String artist, String texture, int sizeX, int sizeY) {
      values.add(this);
      map.put(key, this);
      this.key = key;
      this.title = title;
      this.artist = artist;
      this.texture = texture;
      this.sizeX = sizeX;
      this.sizeY = sizeY;
      if (key.length() > maxArtKeyLength) {
         maxArtKeyLength = key.length();
      }

      if (title.length() > maxArtTitleLength) {
         maxArtTitleLength = title.length();
      }
   }

   public static ArtType getNext(ArtType art) {
      int index = values.indexOf(art);
      return values.get((index + 1) % values.size());
   }

   public static ArtType getPrevious(ArtType art) {
      int index = values.indexOf(art);
      return values.get((index - 1 + values.size()) % values.size());
   }
}
