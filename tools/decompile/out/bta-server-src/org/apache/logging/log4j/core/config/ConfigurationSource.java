package org.apache.logging.log4j.core.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import org.apache.logging.log4j.core.net.UrlConnectionFactory;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.Source;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.LoaderUtil;

public class ConfigurationSource {
   public static final ConfigurationSource NULL_SOURCE = new ConfigurationSource(Constants.EMPTY_BYTE_ARRAY, null, 0L);
   public static final ConfigurationSource COMPOSITE_SOURCE = new ConfigurationSource(Constants.EMPTY_BYTE_ARRAY, null, 0L);
   private static final String HTTPS = "https";
   private static final String JAR = "jar";
   private final InputStream stream;
   private volatile byte[] data;
   private volatile Source source;
   private final long lastModified;
   private volatile long modifiedMillis;

   public ConfigurationSource(final InputStream stream, final File file) {
      this.stream = Objects.requireNonNull(stream, "stream is null");
      this.data = null;
      this.source = new Source(file);
      long modified = 0L;

      try {
         modified = file.lastModified();
      } catch (Exception var6) {
      }

      this.lastModified = modified;
   }

   public ConfigurationSource(final InputStream stream, final Path path) {
      this.stream = Objects.requireNonNull(stream, "stream is null");
      this.data = null;
      this.source = new Source(path);
      long modified = 0L;

      try {
         modified = Files.getLastModifiedTime(path).toMillis();
      } catch (Exception var6) {
      }

      this.lastModified = modified;
   }

   public ConfigurationSource(final InputStream stream, final URL url) {
      this.stream = Objects.requireNonNull(stream, "stream is null");
      this.data = null;
      this.lastModified = 0L;
      this.source = new Source(url);
   }

   public ConfigurationSource(final InputStream stream, final URL url, long lastModified) {
      this.stream = Objects.requireNonNull(stream, "stream is null");
      this.data = null;
      this.lastModified = lastModified;
      this.source = new Source(url);
   }

   public ConfigurationSource(final InputStream stream) throws IOException {
      this(toByteArray(stream), null, 0L);
   }

   public ConfigurationSource(final Source source, final byte[] data, long lastModified) {
      Objects.requireNonNull(source, "source is null");
      this.data = Objects.requireNonNull(data, "data is null");
      this.stream = new ByteArrayInputStream(data);
      this.lastModified = lastModified;
      this.source = source;
   }

   private ConfigurationSource(final byte[] data, final URL url, long lastModified) {
      this.data = Objects.requireNonNull(data, "data is null");
      this.stream = new ByteArrayInputStream(data);
      this.lastModified = lastModified;
      if (url == null) {
         this.data = data;
      } else {
         this.source = new Source(url);
      }
   }

   private static byte[] toByteArray(final InputStream inputStream) throws IOException {
      int buffSize = Math.max(4096, inputStream.available());
      ByteArrayOutputStream contents = new ByteArrayOutputStream(buffSize);
      byte[] buff = new byte[buffSize];

      for (int length = inputStream.read(buff); length > 0; length = inputStream.read(buff)) {
         contents.write(buff, 0, length);
      }

      return contents.toByteArray();
   }

   public File getFile() {
      return this.source == null ? null : this.source.getFile();
   }

   private boolean isFile() {
      return this.source == null ? false : this.source.getFile() != null;
   }

   private boolean isURL() {
      return this.source == null ? false : this.source.getURI() != null;
   }

   private boolean isLocation() {
      return this.source == null ? false : this.source.getLocation() != null;
   }

   public URL getURL() {
      return this.source == null ? null : this.source.getURL();
   }

   @Deprecated
   public void setSource(Source source) {
      this.source = source;
   }

   public void setData(byte[] data) {
      this.data = data;
   }

   public void setModifiedMillis(long modifiedMillis) {
      this.modifiedMillis = modifiedMillis;
   }

   public URI getURI() {
      return this.source == null ? null : this.source.getURI();
   }

   public long getLastModified() {
      return this.lastModified;
   }

   public String getLocation() {
      return this.source == null ? null : this.source.getLocation();
   }

   public InputStream getInputStream() {
      return this.stream;
   }

   public ConfigurationSource resetInputStream() throws IOException {
      if (this.source != null && this.data != null) {
         return new ConfigurationSource(this.source, this.data, this.lastModified);
      } else if (this.isFile()) {
         return new ConfigurationSource(new FileInputStream(this.getFile()), this.getFile());
      } else if (this.isURL() && this.data != null) {
         return new ConfigurationSource(this.data, this.getURL(), this.modifiedMillis == 0L ? this.lastModified : this.modifiedMillis);
      } else if (this.isURL()) {
         return fromUri(this.getURI());
      } else {
         return this.data != null ? new ConfigurationSource(this.data, null, this.lastModified) : null;
      }
   }

   @Override
   public String toString() {
      if (this.isLocation()) {
         return this.getLocation();
      } else if (this == NULL_SOURCE) {
         return "NULL_SOURCE";
      } else {
         int length = this.data == null ? -1 : this.data.length;
         return "stream (" + length + " bytes, unknown location)";
      }
   }

   public static ConfigurationSource fromUri(final URI configLocation) {
      File configFile = FileUtils.fileFromUri(configLocation);
      if (configFile != null && configFile.exists() && configFile.canRead()) {
         try {
            return new ConfigurationSource(new FileInputStream(configFile), configFile);
         } catch (FileNotFoundException var5) {
            ConfigurationFactory.LOGGER.error("Cannot locate file {}", configLocation.getPath(), var5);
         }
      }

      if (ConfigurationFactory.isClassLoaderUri(configLocation)) {
         ClassLoader loader = LoaderUtil.getThreadContextClassLoader();
         String path = ConfigurationFactory.extractClassLoaderUriPath(configLocation);
         return fromResource(path, loader);
      } else if (!configLocation.isAbsolute()) {
         ConfigurationFactory.LOGGER.error("File not found in file system or classpath: {}", configLocation.toString());
         return null;
      } else {
         try {
            return getConfigurationSource(configLocation.toURL());
         } catch (MalformedURLException var4) {
            ConfigurationFactory.LOGGER.error("Invalid URL {}", configLocation.toString(), var4);
            return null;
         }
      }
   }

   public static ConfigurationSource fromResource(final String resource, final ClassLoader loader) {
      URL url = Loader.getResource(resource, loader);
      return url == null ? null : getConfigurationSource(url);
   }

   private static ConfigurationSource getConfigurationSource(URL url) {
      try {
         File file = FileUtils.fileFromUri(url.toURI());
         URLConnection urlConnection = UrlConnectionFactory.createConnection(url);

         try {
            if (file != null) {
               return new ConfigurationSource(urlConnection.getInputStream(), FileUtils.fileFromUri(url.toURI()));
            } else if ("jar".equals(url.getProtocol())) {
               long lastModified = new File(((JarURLConnection)urlConnection).getJarFile().getName()).lastModified();
               return new ConfigurationSource(urlConnection.getInputStream(), url, lastModified);
            } else {
               return new ConfigurationSource(urlConnection.getInputStream(), url, urlConnection.getLastModified());
            }
         } catch (FileNotFoundException var5) {
            ConfigurationFactory.LOGGER.info("Unable to locate file {}, ignoring.", url.toString());
            return null;
         }
      } catch (URISyntaxException | IOException var6) {
         ConfigurationFactory.LOGGER.warn("Error accessing {} due to {}, ignoring.", url.toString(), var6.getMessage());
         return null;
      }
   }
}
