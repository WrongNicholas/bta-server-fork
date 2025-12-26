package org.apache.logging.log4j.core.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.net.ssl.SslConfiguration;
import org.apache.logging.log4j.core.net.ssl.SslConfigurationFactory;
import org.apache.logging.log4j.core.util.AbstractWatcher;
import org.apache.logging.log4j.core.util.AuthorizationProvider;
import org.apache.logging.log4j.core.util.Source;
import org.apache.logging.log4j.core.util.Watcher;
import org.apache.logging.log4j.core.util.internal.HttpInputStreamUtil;
import org.apache.logging.log4j.core.util.internal.LastModifiedSource;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

@Plugin(name = "http", category = "Watcher", elementType = "watcher", printObject = true)
@PluginAliases("https")
public class HttpWatcher extends AbstractWatcher {
   private final Logger LOGGER = StatusLogger.getLogger();
   private final SslConfiguration sslConfiguration = SslConfigurationFactory.getSslConfiguration();
   private AuthorizationProvider authorizationProvider;
   private URL url;
   private volatile long lastModifiedMillis;
   private static final String HTTP = "http";
   private static final String HTTPS = "https";

   public HttpWatcher(
      final Configuration configuration,
      final Reconfigurable reconfigurable,
      final List<ConfigurationListener> configurationListeners,
      final long lastModifiedMillis
   ) {
      super(configuration, reconfigurable, configurationListeners);
      this.lastModifiedMillis = lastModifiedMillis;
   }

   @Override
   public long getLastModified() {
      return this.lastModifiedMillis;
   }

   @Override
   public boolean isModified() {
      return this.refreshConfiguration();
   }

   @Override
   public void watching(final Source source) {
      if (!source.getURI().getScheme().equals("http") && !source.getURI().getScheme().equals("https")) {
         throw new IllegalArgumentException("HttpWatcher requires a url using the HTTP or HTTPS protocol, not " + source.getURI().getScheme());
      } else {
         try {
            this.url = source.getURI().toURL();
            this.authorizationProvider = ConfigurationFactory.authorizationProvider(PropertiesUtil.getProperties());
         } catch (MalformedURLException var3) {
            throw new IllegalArgumentException("Invalid URL for HttpWatcher " + source.getURI(), var3);
         }

         super.watching(source);
      }
   }

   @Override
   public Watcher newWatcher(final Reconfigurable reconfigurable, final List<ConfigurationListener> listeners, final long lastModifiedMillis) {
      HttpWatcher watcher = new HttpWatcher(this.getConfiguration(), reconfigurable, listeners, lastModifiedMillis);
      if (this.getSource() != null) {
         watcher.watching(this.getSource());
      }

      return watcher;
   }

   private boolean refreshConfiguration() {
      try {
         LastModifiedSource source = new LastModifiedSource(this.url.toURI(), this.lastModifiedMillis);
         HttpInputStreamUtil.Result result = HttpInputStreamUtil.getInputStream(source, this.authorizationProvider);
         switch (result.getStatus()) {
            case NOT_MODIFIED:
               this.LOGGER.debug("Configuration Not Modified");
               return false;
            case SUCCESS:
               ConfigurationSource configSource = this.getConfiguration().getConfigurationSource();

               try {
                  configSource.setData(HttpInputStreamUtil.readStream(result.getInputStream()));
                  configSource.setModifiedMillis(source.getLastModified());
                  this.LOGGER.debug("Content was modified for {}", this.url.toString());
                  return true;
               } catch (IOException var5) {
                  this.LOGGER.error("Error accessing configuration at {}: {}", this.url, var5.getMessage());
                  return false;
               }
            case NOT_FOUND:
               this.LOGGER.info("Unable to locate configuration at {}", this.url.toString());
               return false;
            default:
               this.LOGGER.warn("Unexpected error accessing configuration at {}", this.url.toString());
               return false;
         }
      } catch (URISyntaxException var6) {
         this.LOGGER.error("Bad configuration URL: {}, {}", this.url.toString(), var6.getMessage());
         return false;
      }
   }
}
