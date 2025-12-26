package net.minecraft.core.net;

import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class ServerAddress {
   private final String ipAddress;
   private final int serverPort;

   private ServerAddress(String ip, int port) {
      this.ipAddress = ip;
      this.serverPort = port;
   }

   public String getIP() {
      return this.ipAddress;
   }

   public int getPort() {
      return this.serverPort;
   }

   public static ServerAddress resolveServerIP(String ipToResolve) {
      if (ipToResolve == null) {
         return null;
      } else {
         String[] ipSplit = ipToResolve.split(":");
         if (ipToResolve.startsWith("[")) {
            int ipv6End = ipToResolve.indexOf("]");
            if (ipv6End > 0) {
               String ipv6IP = ipToResolve.substring(1, ipv6End);
               String port = ipToResolve.substring(ipv6End + 1).trim();
               if (port.startsWith(":") && !port.isEmpty()) {
                  port = port.substring(1);
                  ipSplit = new String[]{ipv6IP, port};
               } else {
                  ipSplit = new String[]{ipv6IP};
               }
            }
         }

         if (ipSplit.length > 2) {
            ipSplit = new String[]{ipToResolve};
         }

         String ip = ipSplit[0];
         int port = ipSplit.length > 1 ? parseIntWithDefault(ipSplit[1], 25565) : 25565;
         if (port == 25565) {
            String[] recordSplit = resolveSRVRecord(ip);
            ip = recordSplit[0];
            port = parseIntWithDefault(recordSplit[1], 25565);
         }

         return new ServerAddress(ip, port);
      }
   }

   private static String[] resolveSRVRecord(String ip) {
      try {
         Hashtable<String, String> contextTable = new Hashtable<>();
         contextTable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         contextTable.put("java.naming.provider.url", "dns:");
         InitialDirContext context = new InitialDirContext(contextTable);
         Attributes attr = context.getAttributes("_minecraft._tcp." + ip, new String[]{"SRV"});
         String[] ipSplit = attr.get("srv").get().toString().split(" ", 4);
         return new String[]{ipSplit[3], ipSplit[2]};
      } catch (Throwable var5) {
         return new String[]{ip, Integer.toString(25565)};
      }
   }

   private static int parseIntWithDefault(String str, int defaultVal) {
      try {
         return Integer.parseInt(str.trim());
      } catch (Exception var3) {
         return defaultVal;
      }
   }
}
