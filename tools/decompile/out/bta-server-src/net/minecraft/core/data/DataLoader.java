package net.minecraft.core.data;

import com.b100.utils.FileUtils;
import com.b100.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.core.MinecraftAccessor;
import net.minecraft.core.WeightedRandomBag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.data.registry.recipe.HasJsonAdapter;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.data.registry.recipe.adapter.ItemStackJsonAdapter;
import net.minecraft.core.data.registry.recipe.adapter.RecipeJsonAdapter;
import net.minecraft.core.data.registry.recipe.adapter.RecipeSymbolJsonAdapter;
import net.minecraft.core.data.registry.recipe.adapter.WeightedRandomBagJsonAdapter;
import net.minecraft.core.data.registry.recipe.adapter.WeightedRandomLootObjectJsonAdapter;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.packet.PacketRecipeSync;
import org.slf4j.Logger;

public class DataLoader {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void loadItemGroupsFromString(String json) {
      int i = 0;
      GsonBuilder builder = new GsonBuilder();
      builder.setPrettyPrinting();
      builder.registerTypeAdapter(ItemStack.class, new ItemStackJsonAdapter());
      builder.registerTypeAdapter(RecipeSymbol.class, new RecipeSymbolJsonAdapter());
      Gson gson = builder.create();
      JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

      for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
         List<ItemStack> stacks = gson.fromJson(entry.getValue(), (new TypeToken<List<ItemStack>>() {}).getType());
         Registries.ITEM_GROUPS.register(entry.getKey(), stacks);
         i++;
      }

      LOGGER.info("Registered {} item groups", i);
   }

   public static void loadItemGroupsFromFile(String path) {
      int i = 0;
      String jsonString = StringUtils.readInputString(DataLoader.class.getResourceAsStream(path));
      GsonBuilder builder = new GsonBuilder();
      builder.setPrettyPrinting();
      builder.registerTypeAdapter(ItemStack.class, new ItemStackJsonAdapter());
      builder.registerTypeAdapter(RecipeSymbol.class, new RecipeSymbolJsonAdapter());
      Gson gson = builder.create();
      JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

      for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
         List<ItemStack> stacks = gson.fromJson(entry.getValue(), (new TypeToken<List<ItemStack>>() {}).getType());
         Registries.ITEM_GROUPS.register(entry.getKey(), stacks);
         i++;
      }

      LOGGER.info("Registered {} item groups from {}", i, path);
   }

   public static void loadDataPacks(MinecraftAccessor mc) {
      File dataDir = new File(mc.getMinecraftDir(), "datapacks");
      if (!dataDir.exists()) {
         dataDir.mkdirs();
      }

      if (dataDir.exists() && dataDir.isDirectory()) {
         File[] filesInDir = dataDir.listFiles();
         if (filesInDir != null) {
            for (File file : filesInDir) {
               if (file.getName().toLowerCase().endsWith(".zip")) {
                  try {
                     ZipFile zipFile = new ZipFile(file);

                     try {
                        LOGGER.info("Loading data from {}", file.getName());
                        ZipEntry manifestEntry = zipFile.getEntry("manifest.json");
                        if (manifestEntry != null) {
                           InputStreamReader reader = new InputStreamReader(zipFile.getInputStream(manifestEntry));

                           try {
                              Gson gson = new Gson();
                              TypeToken<Map<String, List<String>>> token = new TypeToken<Map<String, List<String>>>() {};
                              Map<String, List<String>> map = gson.fromJson(reader, token);
                              List<String> recipeFiles = map.get("added_recipes");
                              List<String> groupFiles = map.get("added_item_groups");
                              List<String> removedRecipes = map.get("removed_recipes");
                              if (removedRecipes != null && !removedRecipes.isEmpty()) {
                                 for (String removedRecipe : removedRecipes) {
                                    String[] deconstructedKey = Registries.RECIPES.deconstructKey(removedRecipe);
                                    Registries.RECIPES.getGroupFromKey(removedRecipe).unregister(deconstructedKey[2]);
                                 }

                                 LOGGER.info("Removed {} recipes", removedRecipes.size());
                              }

                              if (groupFiles != null && !groupFiles.isEmpty()) {
                                 for (String groupFile : groupFiles) {
                                    ZipEntry groupFileEntry = zipFile.getEntry(groupFile);
                                    if (groupFileEntry != null) {
                                       InputStream recipeFileInputStream = zipFile.getInputStream(groupFileEntry);

                                       try {
                                          String contents = StringUtils.readInputString(recipeFileInputStream);
                                          LOGGER.info("Loading item groups from {}/{}", file.getName(), groupFile);
                                          loadItemGroupsFromString(contents);
                                       } catch (Throwable var27) {
                                          if (recipeFileInputStream != null) {
                                             try {
                                                recipeFileInputStream.close();
                                             } catch (Throwable var25) {
                                                var27.addSuppressed(var25);
                                             }
                                          }

                                          throw var27;
                                       }

                                       if (recipeFileInputStream != null) {
                                          recipeFileInputStream.close();
                                       }
                                    }
                                 }
                              }

                              if (recipeFiles != null && !recipeFiles.isEmpty()) {
                                 for (String recipeFile : recipeFiles) {
                                    ZipEntry recipeFileEntry = zipFile.getEntry(recipeFile);
                                    if (recipeFileEntry != null) {
                                       InputStream recipeFileInputStream = zipFile.getInputStream(recipeFileEntry);

                                       try {
                                          String contents = StringUtils.readInputString(recipeFileInputStream);
                                          LOGGER.info("Loading recipes from {}/{}", file.getName(), recipeFile);
                                          loadRecipesFromString(contents);
                                       } catch (Throwable var26) {
                                          if (recipeFileInputStream != null) {
                                             try {
                                                recipeFileInputStream.close();
                                             } catch (Throwable var24) {
                                                var26.addSuppressed(var24);
                                             }
                                          }

                                          throw var26;
                                       }

                                       if (recipeFileInputStream != null) {
                                          recipeFileInputStream.close();
                                       }
                                    }
                                 }
                              }
                           } catch (Throwable var28) {
                              try {
                                 reader.close();
                              } catch (Throwable var23) {
                                 var28.addSuppressed(var23);
                              }

                              throw var28;
                           }

                           reader.close();
                        }
                     } catch (Throwable var29) {
                        try {
                           zipFile.close();
                        } catch (Throwable var22) {
                           var29.addSuppressed(var22);
                        }

                        throw var29;
                     }

                     zipFile.close();
                  } catch (RuntimeException | IOException var30) {
                     LOGGER.error("Failed to load data from {}", file.getName(), var30);
                  }
               }
            }
         }
      }
   }

   public static void loadRecipesFromFile(String path) {
      int i = 0;
      String jsonString = StringUtils.readInputString(DataLoader.class.getResourceAsStream(path));
      JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
      GsonBuilder builder = new GsonBuilder();
      builder.setPrettyPrinting();
      List<RecipeJsonAdapter<?>> usedAdapters = new ArrayList<>();

      for (JsonElement element : jsonArray) {
         if (!element.isJsonNull()) {
            JsonObject recipeJson = element.getAsJsonObject();
            String recipeTypeId = recipeJson.get("type").getAsString();
            String recipeId = recipeJson.get("name").getAsString();
            Class<? extends RecipeEntryBase<?, ?, ?>> recipeEntryClass = Registries.RECIPE_TYPES.getItem(recipeTypeId);
            if (Arrays.asList(recipeEntryClass.getInterfaces()).contains(HasJsonAdapter.class)) {
               try {
                  RecipeEntryBase<?, ?, ?> recipeEntryPrototype = (RecipeEntryBase<?, ?, ?>)recipeEntryClass.getConstructor().newInstance();
                  RecipeJsonAdapter<?> adapter = ((HasJsonAdapter)recipeEntryPrototype).getAdapter();
                  if (!usedAdapters.contains(adapter)) {
                     builder.registerTypeAdapter(recipeEntryClass, adapter);
                     usedAdapters.add(adapter);
                  }
               } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var14) {
                  throw new RuntimeException(var14);
               }
            }

            builder.registerTypeAdapter(ItemStack.class, new ItemStackJsonAdapter());
            builder.registerTypeAdapter(RecipeSymbol.class, new RecipeSymbolJsonAdapter());
            builder.registerTypeAdapter((new TypeToken<WeightedRandomBag<WeightedRandomLootObject>>() {}).getType(), new WeightedRandomBagJsonAdapter());
            builder.registerTypeAdapter(WeightedRandomLootObject.class, new WeightedRandomLootObjectJsonAdapter());
            Gson gson = builder.create();
            RecipeEntryBase<?, ?, ?> recipe = gson.fromJson(recipeJson, (Class<RecipeEntryBase<?, ?, ?>>)recipeEntryClass);
            Registries.RECIPES.addCustomRecipe(recipeId, recipe);
            i++;
         }
      }

      LOGGER.info("Registered {} recipes from {}", i, path);
   }

   public static void loadRecipesFromString(String json) {
      int i = 0;
      JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
      GsonBuilder builder = new GsonBuilder();
      builder.setPrettyPrinting();
      List<RecipeJsonAdapter<?>> usedAdapters = new ArrayList<>();

      for (JsonElement element : jsonArray) {
         if (!element.isJsonNull()) {
            JsonObject recipeJson = element.getAsJsonObject();
            String recipeTypeId = recipeJson.get("type").getAsString();
            String recipeId = recipeJson.get("name").getAsString();
            Class<? extends RecipeEntryBase<?, ?, ?>> recipeEntryClass = Registries.RECIPE_TYPES.getItem(recipeTypeId);
            if (Arrays.asList(recipeEntryClass.getInterfaces()).contains(HasJsonAdapter.class)) {
               try {
                  RecipeEntryBase<?, ?, ?> recipeEntryPrototype = (RecipeEntryBase<?, ?, ?>)recipeEntryClass.getConstructor().newInstance();
                  RecipeJsonAdapter<?> adapter = ((HasJsonAdapter)recipeEntryPrototype).getAdapter();
                  if (!usedAdapters.contains(adapter)) {
                     builder.registerTypeAdapter(recipeEntryClass, adapter);
                     usedAdapters.add(adapter);
                  }
               } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var13) {
                  throw new RuntimeException(var13);
               }
            }

            builder.registerTypeAdapter(ItemStack.class, new ItemStackJsonAdapter());
            builder.registerTypeAdapter(RecipeSymbol.class, new RecipeSymbolJsonAdapter());
            builder.registerTypeAdapter((new TypeToken<WeightedRandomBag<WeightedRandomLootObject>>() {}).getType(), new WeightedRandomBagJsonAdapter());
            builder.registerTypeAdapter(WeightedRandomLootObject.class, new WeightedRandomLootObjectJsonAdapter());
            Gson gson = builder.create();
            RecipeEntryBase<?, ?, ?> recipe = gson.fromJson(recipeJson, (Class<RecipeEntryBase<?, ?, ?>>)recipeEntryClass);
            Registries.RECIPES.addCustomRecipe(recipeId, recipe);
            i++;
         }
      }

      LOGGER.info("Registered {} recipes", i);
   }

   public static void loadRecipeFromServer(PacketRecipeSync packet) {
      JsonArray jsonArray = JsonParser.parseString(packet.recipe).getAsJsonArray();
      GsonBuilder builder = new GsonBuilder();
      builder.setPrettyPrinting();
      JsonElement element = jsonArray.get(0);
      if (!element.isJsonNull()) {
         JsonObject recipeJson = element.getAsJsonObject();
         String recipeTypeId = recipeJson.get("type").getAsString();
         String recipeId = recipeJson.get("name").getAsString();
         Class<? extends RecipeEntryBase<?, ?, ?>> recipeEntryClass = Registries.RECIPE_TYPES.getItem(recipeTypeId);
         if (Arrays.asList(recipeEntryClass.getInterfaces()).contains(HasJsonAdapter.class)) {
            try {
               RecipeEntryBase<?, ?, ?> recipeEntryPrototype = (RecipeEntryBase<?, ?, ?>)recipeEntryClass.getConstructor().newInstance();
               RecipeJsonAdapter<?> adapter = ((HasJsonAdapter)recipeEntryPrototype).getAdapter();
               builder.registerTypeAdapter(recipeEntryClass, adapter);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException var10) {
               throw new RuntimeException(var10);
            }
         }

         builder.registerTypeAdapter(ItemStack.class, new ItemStackJsonAdapter());
         builder.registerTypeAdapter(RecipeSymbol.class, new RecipeSymbolJsonAdapter());
         builder.registerTypeAdapter((new TypeToken<WeightedRandomBag<WeightedRandomLootObject>>() {}).getType(), new WeightedRandomBagJsonAdapter());
         builder.registerTypeAdapter(WeightedRandomLootObject.class, new WeightedRandomLootObjectJsonAdapter());
         Gson gson = builder.create();
         RecipeEntryBase<?, ?, ?> recipe = gson.fromJson(recipeJson, (Class<RecipeEntryBase<?, ?, ?>>)recipeEntryClass);
         Registries.RECIPES.addCustomRecipe(recipeId, recipe);
      }
   }

   public static String serializeRecipes() {
      List<RecipeEntryBase<?, ?, ?>> recipes = Registries.RECIPES.getAllSerializableRecipes();
      GsonBuilder builder = new GsonBuilder();
      builder.setPrettyPrinting();
      List<RecipeJsonAdapter<?>> usedAdapters = new ArrayList<>();

      for (RecipeEntryBase<?, ?, ?> recipe : recipes) {
         HasJsonAdapter hasJsonAdapter = (HasJsonAdapter)recipe;
         RecipeJsonAdapter<?> adapter = hasJsonAdapter.getAdapter();
         if (!usedAdapters.contains(adapter)) {
            builder.registerTypeAdapter(recipe.getClass(), adapter);
            usedAdapters.add(adapter);
         }
      }

      builder.registerTypeAdapter(ItemStack.class, new ItemStackJsonAdapter());
      builder.registerTypeAdapter(RecipeSymbol.class, new RecipeSymbolJsonAdapter());
      builder.registerTypeAdapter((new TypeToken<WeightedRandomBag<WeightedRandomLootObject>>() {}).getType(), new WeightedRandomBagJsonAdapter());
      builder.registerTypeAdapter(WeightedRandomLootObject.class, new WeightedRandomLootObjectJsonAdapter());
      Gson gson = builder.create();
      JsonArray jsonArray = new JsonArray();

      for (RecipeEntryBase<?, ?, ?> recipex : recipes) {
         TypeAdapter<? super RecipeEntryBase<?, ?, ?>> typeAdapter = gson.getAdapter((Class<? super RecipeEntryBase<?, ?, ?>>)recipex.getClass());
         JsonElement json = typeAdapter.toJsonTree(recipex);
         jsonArray.add(json);
      }

      return gson.toJson((JsonElement)jsonArray);
   }

   public static String serializeRecipe(RecipeEntryBase<?, ?, ?> recipe) {
      if (!(recipe instanceof HasJsonAdapter)) {
         throw new IllegalArgumentException("Recipe cannot be serialized!");
      } else {
         GsonBuilder builder = new GsonBuilder();
         builder.setPrettyPrinting();
         HasJsonAdapter hasJsonAdapter = (HasJsonAdapter)recipe;
         RecipeJsonAdapter<?> adapter = hasJsonAdapter.getAdapter();
         builder.registerTypeAdapter(recipe.getClass(), adapter);
         builder.registerTypeAdapter(ItemStack.class, new ItemStackJsonAdapter());
         builder.registerTypeAdapter(RecipeSymbol.class, new RecipeSymbolJsonAdapter());
         builder.registerTypeAdapter((new TypeToken<WeightedRandomBag<WeightedRandomLootObject>>() {}).getType(), new WeightedRandomBagJsonAdapter());
         builder.registerTypeAdapter(WeightedRandomLootObject.class, new WeightedRandomLootObjectJsonAdapter());
         Gson gson = builder.create();
         JsonArray jsonArray = new JsonArray();
         TypeAdapter<? super RecipeEntryBase<?, ?, ?>> typeAdapter = gson.getAdapter((Class<? super RecipeEntryBase<?, ?, ?>>)recipe.getClass());
         jsonArray.add(typeAdapter.toJsonTree(recipe));
         return gson.toJson((JsonElement)jsonArray);
      }
   }

   public static void exportRecipes(String path) {
      List<RecipeEntryBase<?, ?, ?>> recipes = Registries.RECIPES.getAllSerializableRecipes();
      GsonBuilder builder = new GsonBuilder();
      builder.setPrettyPrinting();
      List<RecipeJsonAdapter<?>> usedAdapters = new ArrayList<>();

      for (RecipeEntryBase<?, ?, ?> recipe : recipes) {
         HasJsonAdapter hasJsonAdapter = (HasJsonAdapter)recipe;
         RecipeJsonAdapter<?> adapter = hasJsonAdapter.getAdapter();
         if (!usedAdapters.contains(adapter)) {
            builder.registerTypeAdapter(recipe.getClass(), adapter);
            usedAdapters.add(adapter);
         }
      }

      builder.registerTypeAdapter(ItemStack.class, new ItemStackJsonAdapter());
      builder.registerTypeAdapter(RecipeSymbol.class, new RecipeSymbolJsonAdapter());
      builder.registerTypeAdapter((new TypeToken<WeightedRandomBag<WeightedRandomLootObject>>() {}).getType(), new WeightedRandomBagJsonAdapter());
      builder.registerTypeAdapter(WeightedRandomLootObject.class, new WeightedRandomLootObjectJsonAdapter());
      Gson gson = builder.create();
      JsonArray jsonArray = new JsonArray();

      for (RecipeEntryBase<?, ?, ?> recipex : recipes) {
         TypeAdapter<? super RecipeEntryBase<?, ?, ?>> typeAdapter = gson.getAdapter((Class<? super RecipeEntryBase<?, ?, ?>>)recipex.getClass());
         JsonElement json = typeAdapter.toJsonTree(recipex);
         jsonArray.add(json);
      }

      File file = FileUtils.createNewFile(new File(path));

      try {
         FileWriter writer = new FileWriter(file);

         try {
            gson.toJson((JsonElement)jsonArray, writer);
         } catch (Throwable var11) {
            try {
               writer.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }

            throw var11;
         }

         writer.close();
      } catch (IOException var12) {
         throw new RuntimeException(var12);
      }
   }
}
