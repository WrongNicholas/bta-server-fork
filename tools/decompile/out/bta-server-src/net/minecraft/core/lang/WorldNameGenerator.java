package net.minecraft.core.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldNameGenerator {
   private List<String> adjectives = null;
   private List<String> nouns = null;
   private List<String> placeNouns = null;
   private List<String> formats = null;
   private final Random rand = new Random();

   public WorldNameGenerator(List<String> lines) {
      int mode = -1;

      for (String line : lines) {
         if (!line.startsWith("#") && !line.isEmpty()) {
            if (line.equals("[adjectives]")) {
               this.adjectives = new ArrayList<>();
               mode = 0;
            } else if (line.equals("[nouns]")) {
               this.nouns = new ArrayList<>();
               mode = 1;
            } else if (line.equals("[place-nouns]")) {
               this.placeNouns = new ArrayList<>();
               mode = 2;
            } else if (line.equals("[formats]")) {
               this.formats = new ArrayList<>();
               mode = 3;
            } else {
               switch (mode) {
                  case 0:
                     this.adjectives.add(line);
                     break;
                  case 1:
                     this.nouns.add(line);
                     break;
                  case 2:
                     this.placeNouns.add(line);
                     break;
                  case 3:
                     this.formats.add(line);
               }
            }
         }
      }

      if (this.adjectives == null || this.nouns == null || this.placeNouns == null || this.formats == null) {
         throw new RuntimeException("Invalid world name generator file!");
      }
   }

   public String getRandomWorldName() {
      String adjective = this.adjectives.get(this.rand.nextInt(this.adjectives.size()));
      String noun = this.nouns.get(this.rand.nextInt(this.nouns.size()));
      String placeNoun = this.placeNouns.get(this.rand.nextInt(this.placeNouns.size()));
      String format = this.formats.get(this.rand.nextInt(this.formats.size()));
      adjective = adjective.substring(0, 1).toUpperCase() + adjective.substring(1);
      noun = noun.substring(0, 1).toUpperCase() + noun.substring(1);
      placeNoun = placeNoun.substring(0, 1).toUpperCase() + placeNoun.substring(1);
      return format.replace("%a", adjective).replace("%n", noun).replace("%p", placeNoun);
   }
}
