package net.minecraft.core.lang;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;

public class Language {
   private static final Logger LOGGER = LogUtils.getLogger();
   protected List<String> reportedMissingTranslations = new ArrayList<>();
   protected final Properties entries;
   private final String id;
   private final String name;
   private final String region;
   private final List<String> credits;
   protected List<String> tips = null;
   protected WorldNameGenerator worldNameGenerator = null;

   Language(String id, String name, String region, List<String> credits, ZipFile zipFile) {
      this.id = id;
      this.name = name;
      this.region = region;
      this.credits = credits;
      this.entries = new Properties();
      if (zipFile != null) {
         Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

         while (zipEntries.hasMoreElements()) {
            ZipEntry entry = zipEntries.nextElement();
            if (entry.getName().endsWith(".lang")) {
               try {
                  InputStream in = zipFile.getInputStream(entry);

                  try {
                     InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);

                     try {
                        this.entries.load(reader);
                     } catch (Throwable var24) {
                        try {
                           reader.close();
                        } catch (Throwable var21) {
                           var24.addSuppressed(var21);
                        }

                        throw var24;
                     }

                     reader.close();
                  } catch (Throwable var27) {
                     if (in != null) {
                        try {
                           in.close();
                        } catch (Throwable var20) {
                           var27.addSuppressed(var20);
                        }
                     }

                     throw var27;
                  }

                  if (in != null) {
                     in.close();
                  }
               } catch (IOException var28) {
                  LOGGER.error("Failed to load lang file '{}' in '{}'", entry, name, var28);
               }
            } else if (this.tips == null && entry.getName().endsWith("tips.txt")) {
               try {
                  InputStream in = zipFile.getInputStream(entry);

                  try {
                     InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);

                     try {
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        try {
                           this.tips = new ArrayList<>();

                           String st;
                           while ((st = bufferedReader.readLine()) != null) {
                              this.tips.add(st);
                           }
                        } catch (Throwable var29) {
                           try {
                              bufferedReader.close();
                           } catch (Throwable var19) {
                              var29.addSuppressed(var19);
                           }

                           throw var29;
                        }

                        bufferedReader.close();
                     } catch (Throwable var30) {
                        try {
                           reader.close();
                        } catch (Throwable var18) {
                           var30.addSuppressed(var18);
                        }

                        throw var30;
                     }

                     reader.close();
                  } catch (Throwable var31) {
                     if (in != null) {
                        try {
                           in.close();
                        } catch (Throwable var17) {
                           var31.addSuppressed(var17);
                        }
                     }

                     throw var31;
                  }

                  if (in != null) {
                     in.close();
                  }
               } catch (IOException var32) {
                  LOGGER.error("Failed to load 'tips.txt' file in '{}'", this.getName(), var32);
               }
            } else if (entry.getName().endsWith("worldnames.txt")) {
               try {
                  InputStream in = zipFile.getInputStream(entry);

                  try {
                     InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);

                     try {
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        try {
                           this.worldNameGenerator = new WorldNameGenerator(bufferedReader.lines().collect(Collectors.toList()));
                        } catch (Throwable var22) {
                           try {
                              bufferedReader.close();
                           } catch (Throwable var16) {
                              var22.addSuppressed(var16);
                           }

                           throw var22;
                        }

                        bufferedReader.close();
                     } catch (Throwable var23) {
                        try {
                           reader.close();
                        } catch (Throwable var15) {
                           var23.addSuppressed(var15);
                        }

                        throw var23;
                     }

                     reader.close();
                  } catch (Throwable var25) {
                     if (in != null) {
                        try {
                           in.close();
                        } catch (Throwable var14) {
                           var25.addSuppressed(var14);
                        }
                     }

                     throw var25;
                  }

                  if (in != null) {
                     in.close();
                  }
               } catch (IOException var26) {
                  LOGGER.error("Failed to load 'worldnames.txt' file in '{}'", this.getName(), var26);
               }
            }
         }

         try {
            zipFile.close();
         } catch (IOException var13) {
            throw new RuntimeException(var13);
         }
      }
   }

   Language(String id, String name, String region, List<String> credits, File directory) {
      this.id = id;
      this.name = name;
      this.region = region;
      this.credits = credits;
      this.entries = new Properties();
      if (directory != null) {
         this.loadFromDirectory(directory);
      }
   }

   private void loadFromDirectory(File directory) {
      assert directory.isDirectory() : "Directory file must actually be a directory!";

      File[] files = directory.listFiles();
      if (files != null) {
         for (int i = 0; i < files.length; i++) {
            File currentFile = files[i];
            if (currentFile.isDirectory()) {
               this.loadFromDirectory(currentFile);
            } else if (currentFile.getPath().endsWith(".lang")) {
               try {
                  InputStream in = Files.newInputStream(currentFile.toPath());

                  try {
                     InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);

                     try {
                        this.entries.load(reader);
                     } catch (Throwable var20) {
                        try {
                           reader.close();
                        } catch (Throwable var17) {
                           var20.addSuppressed(var17);
                        }

                        throw var20;
                     }

                     reader.close();
                  } catch (Throwable var23) {
                     if (in != null) {
                        try {
                           in.close();
                        } catch (Throwable var16) {
                           var23.addSuppressed(var16);
                        }
                     }

                     throw var23;
                  }

                  if (in != null) {
                     in.close();
                  }
               } catch (IOException var24) {
                  LOGGER.error("Failed to load lang file '{}' in '{}'", currentFile.getName(), this.name, var24);
               }
            } else if (this.tips == null && currentFile.getPath().endsWith("tips.txt")) {
               try {
                  InputStream stream = Files.newInputStream(currentFile.toPath());

                  try {
                     InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

                     try {
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        try {
                           this.tips = new ArrayList<>();

                           String st;
                           while ((st = bufferedReader.readLine()) != null) {
                              this.tips.add(st);
                           }
                        } catch (Throwable var25) {
                           try {
                              bufferedReader.close();
                           } catch (Throwable var15) {
                              var25.addSuppressed(var15);
                           }

                           throw var25;
                        }

                        bufferedReader.close();
                     } catch (Throwable var26) {
                        try {
                           reader.close();
                        } catch (Throwable var14) {
                           var26.addSuppressed(var14);
                        }

                        throw var26;
                     }

                     reader.close();
                  } catch (Throwable var27) {
                     if (stream != null) {
                        try {
                           stream.close();
                        } catch (Throwable var13) {
                           var27.addSuppressed(var13);
                        }
                     }

                     throw var27;
                  }

                  if (stream != null) {
                     stream.close();
                  }
               } catch (IOException var28) {
                  LOGGER.error("Failed to load 'tips.txt' file in '{}'", this.getName(), var28);
               }
            } else if (currentFile.getPath().endsWith("worldnames.txt")) {
               try {
                  InputStream stream = Files.newInputStream(currentFile.toPath());

                  try {
                     InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

                     try {
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        try {
                           this.worldNameGenerator = new WorldNameGenerator(bufferedReader.lines().collect(Collectors.toList()));
                        } catch (Throwable var18) {
                           try {
                              bufferedReader.close();
                           } catch (Throwable var12) {
                              var18.addSuppressed(var12);
                           }

                           throw var18;
                        }

                        bufferedReader.close();
                     } catch (Throwable var19) {
                        try {
                           reader.close();
                        } catch (Throwable var11) {
                           var19.addSuppressed(var11);
                        }

                        throw var19;
                     }

                     reader.close();
                  } catch (Throwable var21) {
                     if (stream != null) {
                        try {
                           stream.close();
                        } catch (Throwable var10) {
                           var21.addSuppressed(var10);
                        }
                     }

                     throw var21;
                  }

                  if (stream != null) {
                     stream.close();
                  }
               } catch (IOException var22) {
                  LOGGER.error("Failed to load 'worldnames.txt' file in '{}'", this.getName(), var22);
               }
            }
         }
      }
   }

   public String translateKey(String key) {
      if (key == null) {
         return null;
      } else {
         String ret = this.entries.getProperty(key);
         if (ret == null) {
            if (!this.reportedMissingTranslations.contains(key)) {
               LOGGER.error("Missing translation: {}\n at {}", key, Thread.currentThread().getStackTrace()[2]);
               this.reportedMissingTranslations.add(key);
            }

            return Language.Default.INSTANCE.translateKey(key);
         } else {
            return ret;
         }
      }
   }

   public String translateKeyAndFormat(String formatKey, Object... args) {
      String format = this.entries.getProperty(formatKey, formatKey);
      if (formatKey.equals(format)) {
         format = Language.Default.INSTANCE.entries.getProperty(formatKey, formatKey);
      }

      return String.format(format, args);
   }

   public List<String> getTips() {
      return this.tips == null ? Language.Default.INSTANCE.getTips() : this.tips;
   }

   public String getRandomWorldName() {
      return this.worldNameGenerator == null ? Language.Default.INSTANCE.getRandomWorldName() : this.worldNameGenerator.getRandomWorldName();
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getCredits() {
      return this.credits;
   }

   public String getRegion() {
      return this.region;
   }

   public int keySize() {
      return this.entries.size();
   }

   void onReload() {
   }

   public boolean isDefault() {
      return false;
   }

   public static final class Default extends Language {
      public static final Language.Default INSTANCE = new Language.Default();

      private Default() {
         super("en_US", "English", "US", Arrays.asList("Mojang", "jonkadelic", "skydeckagogo"), (ZipFile)null);
         this.entries.clear();

         for (String path : I18n.getFilesInDirectory("/lang/en_US/")) {
            if (path.endsWith(".lang")) {
               try {
                  InputStream stream = I18n.getResourceAsStream(path);

                  try {
                     InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

                     try {
                        this.entries.load(reader);
                     } catch (Throwable var18) {
                        try {
                           reader.close();
                        } catch (Throwable var17) {
                           var18.addSuppressed(var17);
                        }

                        throw var18;
                     }

                     reader.close();
                  } catch (Throwable var23) {
                     if (stream != null) {
                        try {
                           stream.close();
                        } catch (Throwable var16) {
                           var23.addSuppressed(var16);
                        }
                     }

                     throw var23;
                  }

                  if (stream != null) {
                     stream.close();
                  }
               } catch (IOException var24) {
                  Language.LOGGER.error("Failed to load lang file '{}' in '{}'", path, this.getName(), var24);
               }
            } else if (this.tips == null && path.endsWith("/tips.txt")) {
               try {
                  InputStream stream = I18n.getResourceAsStream(path);

                  try {
                     InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

                     try {
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        try {
                           this.tips = new ArrayList<>();

                           String st;
                           while ((st = bufferedReader.readLine()) != null) {
                              this.tips.add(st);
                           }
                        } catch (Throwable var25) {
                           try {
                              bufferedReader.close();
                           } catch (Throwable var15) {
                              var25.addSuppressed(var15);
                           }

                           throw var25;
                        }

                        bufferedReader.close();
                     } catch (Throwable var26) {
                        try {
                           reader.close();
                        } catch (Throwable var14) {
                           var26.addSuppressed(var14);
                        }

                        throw var26;
                     }

                     reader.close();
                  } catch (Throwable var27) {
                     if (stream != null) {
                        try {
                           stream.close();
                        } catch (Throwable var13) {
                           var27.addSuppressed(var13);
                        }
                     }

                     throw var27;
                  }

                  if (stream != null) {
                     stream.close();
                  }
               } catch (IOException var28) {
                  Language.LOGGER.error("Failed to load 'tips.txt' file in '{}'", this.getName(), var28);
               }
            } else if (path.endsWith("/worldnames.txt")) {
               try {
                  InputStream stream = I18n.getResourceAsStream(path);

                  try {
                     InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);

                     try {
                        BufferedReader bufferedReader = new BufferedReader(reader);

                        try {
                           this.worldNameGenerator = new WorldNameGenerator(bufferedReader.lines().collect(Collectors.toList()));
                        } catch (Throwable var19) {
                           try {
                              bufferedReader.close();
                           } catch (Throwable var12) {
                              var19.addSuppressed(var12);
                           }

                           throw var19;
                        }

                        bufferedReader.close();
                     } catch (Throwable var20) {
                        try {
                           reader.close();
                        } catch (Throwable var11) {
                           var20.addSuppressed(var11);
                        }

                        throw var20;
                     }

                     reader.close();
                  } catch (Throwable var21) {
                     if (stream != null) {
                        try {
                           stream.close();
                        } catch (Throwable var10) {
                           var21.addSuppressed(var10);
                        }
                     }

                     throw var21;
                  }

                  if (stream != null) {
                     stream.close();
                  }
               } catch (IOException var22) {
                  Language.LOGGER.error("Failed to load 'worldnames.txt' file in '{}'", this.getName(), var22);
               }
            }
         }
      }

      @Override
      public String translateKey(String key) {
         if (key == null) {
            return null;
         } else {
            String ret = this.entries.getProperty(key);
            if (ret == null) {
               if (!this.reportedMissingTranslations.contains(key)) {
                  this.reportedMissingTranslations.add(key);
                  Language.LOGGER.error("Missing translation in default language: {}\n at {}", key, Thread.currentThread().getStackTrace()[3]);
               }

               return key;
            } else {
               return ret;
            }
         }
      }

      @Override
      public boolean isDefault() {
         return true;
      }
   }
}
