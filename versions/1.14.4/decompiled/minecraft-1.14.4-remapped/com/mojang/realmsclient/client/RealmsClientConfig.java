package com.mojang.realmsclient.client;

import com.fox2code.repacker.ClientJarOnly;
import java.net.Proxy;

@ClientJarOnly
public class RealmsClientConfig {
   private static Proxy proxy;

   public static Proxy getProxy() {
      return proxy;
   }

   public static void setProxy(Proxy proxy) {
      if(proxy == null) {
         proxy = proxy;
      }

   }
}
