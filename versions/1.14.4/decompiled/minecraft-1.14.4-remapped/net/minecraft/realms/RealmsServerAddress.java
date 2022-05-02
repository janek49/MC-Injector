package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.multiplayer.ServerAddress;

@ClientJarOnly
public class RealmsServerAddress {
   private final String host;
   private final int port;

   protected RealmsServerAddress(String host, int port) {
      this.host = host;
      this.port = port;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public static RealmsServerAddress parseString(String string) {
      ServerAddress var1 = ServerAddress.parseString(string);
      return new RealmsServerAddress(var1.getHost(), var1.getPort());
   }
}
