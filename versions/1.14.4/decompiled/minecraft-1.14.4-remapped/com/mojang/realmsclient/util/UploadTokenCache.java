package com.mojang.realmsclient.util;

import com.fox2code.repacker.ClientJarOnly;
import java.util.HashMap;
import java.util.Map;

@ClientJarOnly
public class UploadTokenCache {
   private static final Map tokenCache = new HashMap();

   public static String get(long l) {
      return (String)tokenCache.get(Long.valueOf(l));
   }

   public static void invalidate(long l) {
      tokenCache.remove(Long.valueOf(l));
   }

   public static void put(long var0, String string) {
      tokenCache.put(Long.valueOf(var0), string);
   }
}
