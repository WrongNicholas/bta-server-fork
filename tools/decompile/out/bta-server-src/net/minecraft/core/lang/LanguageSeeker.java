package net.minecraft.core.lang;

import com.b100.json.JsonParser;
import com.b100.json.element.JsonArray;
import com.b100.json.element.JsonElement;
import com.b100.json.element.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraft.core.Global;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LanguageSeeker {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final File LANGUAGE_DIR = Global.accessor.getMinecraftDir().toPath().resolve("languages").toFile();

   @Nullable
   public static Language seek(String id) throws IOException {
      if (id.equals("en_US")) {
         return Language.Default.INSTANCE;
      } else {
         Language ret = null;
         if (LANGUAGE_DIR.exists() && LANGUAGE_DIR.isDirectory()) {
            File[] files = LANGUAGE_DIR.listFiles();
            if (files != null) {
               for (File file : files) {
                  if (file.getName().endsWith(".zip")) {
                     ZipFile zip = new ZipFile(file);

                     try {
                        InputStream stream = zip.getInputStream(zip.getEntry("lang_info.json"));

                        label113: {
                           try {
                              String content = readInputString(stream);
                              JsonObject json = JsonParser.instance.parseString(content);
                              String langId = json.getString("id");
                              String langName = json.getString("name");
                              String langRegion = json.getString("region");
                              JsonArray array = json.getArray("credits");
                              List<String> langCredits = new ArrayList<>();

                              for (JsonElement element : array) {
                                 String string = element.getAsString().toString();
                                 if (string != null) {
                                    langCredits.add(string);
                                 }
                              }

                              if (langId != null && langName != null && !langCredits.isEmpty() && langId.equals(id)) {
                                 ret = new Language(id, langName, langRegion, langCredits, zip);
                                 break label113;
                              }
                           } catch (Throwable var21) {
                              if (stream != null) {
                                 try {
                                    stream.close();
                                 } catch (Throwable var19) {
                                    var21.addSuppressed(var19);
                                 }
                              }

                              throw var21;
                           }

                           if (stream != null) {
                              stream.close();
                           }
                           continue;
                        }

                        if (stream != null) {
                           stream.close();
                        }
                        break;
                     } catch (IOException var22) {
                        throw new RuntimeException(var22);
                     }
                  } else if (file.isDirectory()) {
                     File info = new File(file, "lang_info.json");
                     if (info.exists()) {
                        try {
                           InputStream stream = Files.newInputStream(info.toPath());

                           label133: {
                              try {
                                 String content = readInputString(stream);
                                 JsonObject json = JsonParser.instance.parseString(content);
                                 String langId = json.getString("id");
                                 String langName = json.getString("name");
                                 String langRegion = json.getString("region");
                                 JsonArray array = json.getArray("credits");
                                 List<String> langCredits = new ArrayList<>();

                                 for (JsonElement elementx : array) {
                                    String string = elementx.getAsString().toString();
                                    if (string != null) {
                                       langCredits.add(string);
                                    }
                                 }

                                 if (langId != null && langName != null && !langCredits.isEmpty() && langId.equals(id)) {
                                    ret = new Language(id, langName, langRegion, langCredits, file);
                                    break label133;
                                 }
                              } catch (Throwable var23) {
                                 if (stream != null) {
                                    try {
                                       stream.close();
                                    } catch (Throwable var20) {
                                       var23.addSuppressed(var20);
                                    }
                                 }

                                 throw var23;
                              }

                              if (stream != null) {
                                 stream.close();
                              }
                              continue;
                           }

                           if (stream != null) {
                              stream.close();
                           }
                           break;
                        } catch (IOException var24) {
                           LOGGER.error("Exception loading json 'lang_info.json'!", (Throwable)var24);
                        }
                     }
                  }
               }
            }
         }

         return ret;
      }
   }

   public static List<Language> getAvailableLanguages() throws IOException {
      List<Language> ret = new ArrayList<>();
      ret.add(Language.Default.INSTANCE);
      if (LANGUAGE_DIR.exists() && LANGUAGE_DIR.isDirectory()) {
         File[] files = LANGUAGE_DIR.listFiles();
         if (files != null) {
            for (File file : files) {
               if (file.getName().endsWith(".zip")) {
                  ZipFile zip = new ZipFile(file);
                  ZipEntry entry = zip.getEntry("lang_info.json");
                  if (entry == null) {
                     LOGGER.error("Language pack '{}' is missing a `lang_info.json` file!", file);
                  } else {
                     try {
                        InputStream stream = zip.getInputStream(entry);

                        label97: {
                           try {
                              if (stream != null) {
                                 String content = readInputString(stream);
                                 JsonObject json = JsonParser.instance.parseString(content);
                                 String langId = json.getString("id");
                                 String langName = json.getString("name");
                                 String langRegion = json.getString("region");
                                 JsonArray array = json.getArray("credits");
                                 List<String> langCredits = new ArrayList<>();

                                 for (JsonElement element : array) {
                                    String string = element.getAsString().toString();
                                    if (string != null) {
                                       langCredits.add(string);
                                    }
                                 }

                                 ret.add(new Language(langId, langName, langRegion, langCredits, zip));
                                 break label97;
                              }

                              LOGGER.error("Language pack '{}' could not read `lang_info.json` file as stream!", file);
                           } catch (Throwable var21) {
                              if (stream != null) {
                                 try {
                                    stream.close();
                                 } catch (Throwable var20) {
                                    var21.addSuppressed(var20);
                                 }
                              }

                              throw var21;
                           }

                           if (stream != null) {
                              stream.close();
                           }
                           continue;
                        }

                        if (stream != null) {
                           stream.close();
                        }
                     } catch (IOException var22) {
                        LOGGER.error("Exception loading json 'lang_info.json'!", (Throwable)var22);
                     }
                  }
               } else if (file.isDirectory()) {
                  File info = new File(file, "lang_info.json");
                  if (info.exists()) {
                     try {
                        InputStream stream = Files.newInputStream(info.toPath());

                        try {
                           String content = readInputString(stream);
                           JsonObject json = JsonParser.instance.parseString(content);
                           String langId = json.getString("id");
                           String langName = json.getString("name");
                           String langRegion = json.getString("region");
                           JsonArray array = json.getArray("credits");
                           List<String> langCredits = new ArrayList<>();

                           for (JsonElement elementx : array) {
                              String string = elementx.getAsString().toString();
                              if (string != null) {
                                 langCredits.add(string);
                              }
                           }

                           ret.add(new Language(langId, langName, langRegion, langCredits, file));
                        } catch (Throwable var23) {
                           if (stream != null) {
                              try {
                                 stream.close();
                              } catch (Throwable var19) {
                                 var23.addSuppressed(var19);
                              }
                           }

                           throw var23;
                        }

                        if (stream != null) {
                           stream.close();
                        }
                     } catch (IOException var24) {
                        LOGGER.error("Exception loading json 'lang_info.json'!", (Throwable)var24);
                     }
                  }
               }
            }
         }
      }

      return ret;
   }

   private static String readInputString(InputStream inputStream) {
      if (inputStream == null) {
         throw new NullPointerException();
      } else {
         try {
            InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String line = null;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
               if (firstLine) {
                  firstLine = false;
               } else {
                  line = "\n" + line;
               }

               builder.append(line);
            }

            br.close();
            reader.close();
            return builder.toString();
         } catch (Exception var6) {
            throw new RuntimeException(var6);
         }
      }
   }
}
