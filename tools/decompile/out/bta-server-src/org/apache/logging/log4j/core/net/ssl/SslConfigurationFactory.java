package org.apache.logging.log4j.core.net.ssl;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public class SslConfigurationFactory {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static SslConfiguration sslConfiguration = createSslConfiguration(PropertiesUtil.getProperties());
   private static final String trustStorelocation = "log4j2.trustStoreLocation";
   private static final String trustStorePassword = "log4j2.trustStorePassword";
   private static final String trustStorePasswordFile = "log4j2.trustStorePasswordFile";
   private static final String trustStorePasswordEnvVar = "log4j2.trustStorePasswordEnvironmentVariable";
   private static final String trustStoreKeyStoreType = "log4j2.trustStoreKeyStoreType";
   private static final String trustStoreKeyManagerFactoryAlgorithm = "log4j2.trustStoreKeyManagerFactoryAlgorithm";
   private static final String keyStoreLocation = "log4j2.keyStoreLocation";
   private static final String keyStorePassword = "log4j2.keyStorePassword";
   private static final String keyStorePasswordFile = "log4j2.keyStorePasswordFile";
   private static final String keyStorePasswordEnvVar = "log4j2.keyStorePasswordEnvironmentVariable";
   private static final String keyStoreType = "log4j2.keyStoreType";
   private static final String keyStoreKeyManagerFactoryAlgorithm = "log4j2.keyStoreKeyManagerFactoryAlgorithm";
   private static final String verifyHostName = "log4j2.sslVerifyHostName";

   static SslConfiguration createSslConfiguration(PropertiesUtil props) {
      KeyStoreConfiguration keyStoreConfiguration = null;
      TrustStoreConfiguration trustStoreConfiguration = null;
      String location = props.getStringProperty("log4j2.trustStoreLocation");
      String storeType = props.getStringProperty("log4j2.trustStoreKeyStoreType");
      if (Strings.isNotEmpty(location) || storeType != null) {
         String password = props.getStringProperty("log4j2.trustStorePassword");
         char[] passwordChars = getPassword(password, storeType);

         try {
            trustStoreConfiguration = TrustStoreConfiguration.createKeyStoreConfiguration(
               Strings.trimToNull(location),
               passwordChars,
               props.getStringProperty("log4j2.trustStorePasswordEnvironmentVariable"),
               props.getStringProperty("log4j2.trustStorePasswordFile"),
               storeType,
               props.getStringProperty("log4j2.trustStoreKeyManagerFactoryAlgorithm")
            );
         } catch (Exception var9) {
            LOGGER.warn("Unable to create trust store configuration due to: {} {}", var9.getClass().getName(), var9.getMessage());
         }
      }

      location = props.getStringProperty("log4j2.keyStoreLocation");
      storeType = props.getStringProperty("log4j2.keyStoreType");
      if (Strings.isNotEmpty(location) || storeType != null) {
         String password = props.getStringProperty("log4j2.keyStorePassword");
         char[] passwordChars = getPassword(password, storeType);

         try {
            keyStoreConfiguration = KeyStoreConfiguration.createKeyStoreConfiguration(
               Strings.trimToNull(location),
               passwordChars,
               props.getStringProperty("log4j2.keyStorePasswordEnvironmentVariable"),
               props.getStringProperty("log4j2.keyStorePasswordFile"),
               storeType,
               props.getStringProperty("log4j2.keyStoreKeyManagerFactoryAlgorithm")
            );
         } catch (Exception var8) {
            LOGGER.warn("Unable to create key store configuration due to: {} {}", var8.getClass().getName(), var8.getMessage());
         }
      }

      if (trustStoreConfiguration == null && keyStoreConfiguration == null) {
         return null;
      } else {
         boolean isVerifyHostName = props.getBooleanProperty("log4j2.sslVerifyHostName", false);
         return SslConfiguration.createSSLConfiguration(null, keyStoreConfiguration, trustStoreConfiguration, isVerifyHostName);
      }
   }

   private static char[] getPassword(final String password, final String keyStoreType) {
      if (!keyStoreType.equals("JKS") && !keyStoreType.equals("PKCS12")) {
         return Strings.isEmpty(password) ? null : password.toCharArray();
      } else {
         return password != null ? password.toCharArray() : null;
      }
   }

   public static SslConfiguration getSslConfiguration() {
      return sslConfiguration;
   }
}
