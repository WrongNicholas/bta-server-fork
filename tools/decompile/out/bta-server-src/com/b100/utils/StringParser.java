package com.b100.utils;

import java.io.File;
import java.io.InputStream;

public interface StringParser<E> {
   E parseString(String var1);

   default E parseStream(InputStream stream) {
      return this.parseString(StringUtils.readInputString(stream));
   }

   default E parseFileContent(File file) {
      return this.parseString(StringUtils.getFileContentAsString(file));
   }

   default E parseFileContent(String path) {
      return this.parseFileContent(new File(path));
   }

   default E parseWebsite(String url) {
      return this.parseString(StringUtils.getWebsiteContentAsString(url));
   }
}
