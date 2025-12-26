package net.minecraft.core.net;

import com.b100.utils.StringUtils;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.slf4j.Logger;

public final class CertificateHelper {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static SSLSocketFactory factory;
   private static final String prefix = "/certificates/";
   private static final String[] certificates = new String[]{
      "ISRG-Root-X1.pem",
      "ISRG-Root-X2.pem",
      "DigiCert-Global-Root-CA.pem",
      "DigiCert-Global-Root-G2.pem",
      "DigiCert-Global-Root-G3.pem",
      "GTS-Root-R1.pem",
      "GTS-Root-R2.pem",
      "GTS-Root-R3.pem",
      "GTS-Root-R4.pem",
      "USERTrust-ECC-Certification-Authority.pem"
   };

   public static void installCertificates() throws Exception {
      LOGGER.info("Using bundled let's encrypt certificates");
      TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      X509TrustManager letsEncryptTrustManager = makeLetsEncryptTrustManager();
      tmf.init((KeyStore)null);
      SSLContext sslContext = SSLContext.getInstance("TLS");
      TrustManager[] systemTrustManagers = tmf.getTrustManagers();
      if (systemTrustManagers.length == 1 && systemTrustManagers[0] instanceof X509TrustManager) {
         X509TrustManager systemTrustManager = (X509TrustManager)systemTrustManagers[0];
         sslContext.init(null, new TrustManager[]{new CertificateHelper.X509DualTrustManager(letsEncryptTrustManager, systemTrustManager)}, null);
         factory = sslContext.getSocketFactory();
      } else {
         throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString((Object[])systemTrustManagers));
      }
   }

   public static void install(HttpsURLConnection connection) {
      if (factory != null) {
         connection.setSSLSocketFactory(factory);
      }
   }

   public static SSLSocketFactory getFactory() {
      return factory;
   }

   public static X509TrustManager makeLetsEncryptTrustManager() throws GeneralSecurityException, IOException {
      LinkedList<Certificate> certificates = new LinkedList<>();
      CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

      for (String certificate : CertificateHelper.certificates) {
         try {
            certificates.addAll(
               certificateFactory.generateCertificates(Objects.requireNonNull(CertificateHelper.class.getResourceAsStream("/certificates/" + certificate)))
            );
         } catch (Exception var8) {
            LOGGER.warn("Failed to generate cerfiticate for: {}", certificate, var8);
         }
      }

      char[] password = "password".toCharArray();
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(null, password);
      int index = 0;

      for (Certificate certificate : certificates) {
         String certificateAlias = Integer.toString(index++);
         keyStore.setCertificateEntry(certificateAlias, certificate);
      }

      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(keyStore, password);
      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(keyStore);
      TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
      if (trustManagers.length == 1 && trustManagers[0] instanceof X509TrustManager) {
         return (X509TrustManager)trustManagers[0];
      } else {
         throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString((Object[])trustManagers));
      }
   }

   public static InputStream getWebsiteAsStream(String url) {
      StringUtils.validateStringNotEmpty(url);
      URL u = null;

      try {
         u = new URL(url);
      } catch (Exception var4) {
         throw new RuntimeException(url, var4);
      }

      try {
         URLConnection connection = u.openConnection();
         if (connection instanceof HttpsURLConnection) {
            connection.setDoInput(true);
            connection.setDoOutput(false);
            install((HttpsURLConnection)connection);
         }

         connection.connect();
         return connection.getInputStream();
      } catch (IOException var3) {
         throw new RuntimeException(u.toString(), var3);
      }
   }

   public static InputStream getWebsiteAsStream(URL url) {
      try {
         URLConnection connection = url.openConnection();
         if (connection instanceof HttpsURLConnection) {
            connection.setDoInput(true);
            connection.setDoOutput(false);
            install((HttpsURLConnection)connection);
         }

         connection.connect();
         return connection.getInputStream();
      } catch (IOException var2) {
         throw new RuntimeException(url.toString(), var2);
      }
   }

   public static class X509DualTrustManager implements X509TrustManager {
      private final X509TrustManager letsEncrypt;
      private final X509TrustManager system;
      private final X509Certificate[] certificates;

      public X509DualTrustManager(X509TrustManager letsEncrypt, X509TrustManager system) {
         this.letsEncrypt = letsEncrypt;
         this.system = system;
         HashSet<X509Certificate> certificates = new HashSet<>();
         certificates.addAll(Arrays.asList(this.letsEncrypt.getAcceptedIssuers()));
         certificates.addAll(Arrays.asList(this.system.getAcceptedIssuers()));
         this.certificates = certificates.toArray(new X509Certificate[0]);
      }

      @Override
      public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
         try {
            this.letsEncrypt.checkClientTrusted(chain, authType);
         } catch (SecurityException | CertificateException var4) {
            this.system.checkClientTrusted(chain, authType);
         }
      }

      @Override
      public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
         try {
            this.letsEncrypt.checkServerTrusted(chain, authType);
         } catch (SecurityException | CertificateException var4) {
            this.system.checkServerTrusted(chain, authType);
         }
      }

      @Override
      public X509Certificate[] getAcceptedIssuers() {
         return this.certificates;
      }
   }
}
