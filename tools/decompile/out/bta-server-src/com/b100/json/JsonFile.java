package com.b100.json;

import com.b100.json.element.JsonObject;
import com.b100.utils.FileUtils;
import com.b100.utils.StringUtils;
import com.b100.utils.Utils;
import java.io.File;

public class JsonFile {
   private File file;
   private JsonObject rootObject;
   private JsonParser jsonParser = JsonParser.instance;

   public JsonFile(File file) {
      this.file = Utils.requireNonNull(file);
   }

   public JsonFile(String path) {
      this(new File(Utils.requireNonNull(path)));
   }

   public JsonObject getRootObject() {
      if (this.rootObject == null) {
         if (this.fileExists()) {
            this.load();
         }

         if (this.rootObject == null) {
            this.rootObject = new JsonObject();
         }
      }

      return this.rootObject;
   }

   public void load() {
      try {
         this.rootObject = this.jsonParser.parse(this.file);
      } catch (Exception var2) {
         throw new RuntimeException("Error loading file " + this.file.getAbsolutePath(), var2);
      }
   }

   public JsonFile setRootObject(JsonObject object) {
      this.rootObject = object;
      return this;
   }

   public void save() {
      StringUtils.saveStringToFile(FileUtils.createNewFile(this.file), this.getRootObject().toString());
   }

   public boolean fileExists() {
      return this.file.exists() && this.file.isFile();
   }

   public File getFile() {
      return this.file;
   }

   public void setFile(File file) {
      this.file = Utils.requireNonNull(file);
   }

   public void setJsonParser(JsonParser jsonParser) {
      this.jsonParser = Utils.requireNonNull(jsonParser);
   }

   public JsonParser getJsonParser() {
      return this.jsonParser;
   }
}
