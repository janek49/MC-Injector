package net.minecraft.client.multiplayer;

import com.fox2code.repacker.ClientJarOnly;
import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

@ClientJarOnly
public class ServerAddress {
   private final String host;
   private final int port;

   private ServerAddress(String host, int port) {
      this.host = host;
      this.port = port;
   }

   public String getHost() {
      try {
         return IDN.toASCII(this.host);
      } catch (IllegalArgumentException var2) {
         return "";
      }
   }

   public int getPort() {
      return this.port;
   }

   public static ServerAddress parseString(String string) {
      if(string == null) {
         return null;
      } else {
         String[] vars1 = string.split(":");
         if(string.startsWith("[")) {
            int var2 = string.indexOf("]");
            if(var2 > 0) {
               String var3 = string.substring(1, var2);
               String var4 = string.substring(var2 + 1).trim();
               if(var4.startsWith(":") && !var4.isEmpty()) {
                  var4 = var4.substring(1);
                  vars1 = new String[]{var3, var4};
               } else {
                  vars1 = new String[]{var3};
               }
            }
         }

         if(vars1.length > 2) {
            vars1 = new String[]{string};
         }

         String var2 = vars1[0];
         int var3 = vars1.length > 1?parseInt(vars1[1], 25565):25565;
         if(var3 == 25565) {
            String[] vars4 = lookupSrv(var2);
            var2 = vars4[0];
            var3 = parseInt(vars4[1], 25565);
         }

         return new ServerAddress(var2, var3);
      }
   }

   private static String[] lookupSrv(String string) {
      try {
         String var1 = "com.sun.jndi.dns.DnsContextFactory";
         Class.forName("com.sun.jndi.dns.DnsContextFactory");
         Hashtable<String, String> var2 = new Hashtable();
         var2.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         var2.put("java.naming.provider.url", "dns:");
         var2.put("com.sun.jndi.dns.timeout.retries", "1");
         DirContext var3 = new InitialDirContext(var2);
         Attributes var4 = var3.getAttributes("_minecraft._tcp." + string, new String[]{"SRV"});
         String[] vars5 = var4.get("srv").get().toString().split(" ", 4);
         return new String[]{vars5[3], vars5[2]};
      } catch (Throwable var6) {
         return new String[]{string, Integer.toString(25565)};
      }
   }

   private static int parseInt(String string, int var1) {
      try {
         return Integer.parseInt(string.trim());
      } catch (Exception var3) {
         return var1;
      }
   }
}
