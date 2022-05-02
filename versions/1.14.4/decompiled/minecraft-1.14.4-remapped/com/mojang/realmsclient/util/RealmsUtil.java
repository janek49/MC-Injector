package com.mojang.realmsclient.util;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.Realms;

@ClientJarOnly
public class RealmsUtil {
   private static final YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Realms.getProxy(), UUID.randomUUID().toString());
   private static final MinecraftSessionService sessionService = authenticationService.createMinecraftSessionService();
   public static LoadingCache gameProfileCache = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build(new CacheLoader() {
      public GameProfile load(String string) throws Exception {
         GameProfile gameProfile = RealmsUtil.sessionService.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(string), (String)null), false);
         if(gameProfile == null) {
            throw new Exception("Couldn\'t get profile");
         } else {
            return gameProfile;
         }
      }

      // $FF: synthetic method
      public Object load(Object var1) throws Exception {
         return this.load((String)var1);
      }
   });

   public static String uuidToName(String string) throws Exception {
      GameProfile var1 = (GameProfile)gameProfileCache.get(string);
      return var1.getName();
   }

   public static Map getTextures(String string) {
      try {
         GameProfile var1 = (GameProfile)gameProfileCache.get(string);
         return sessionService.getTextures(var1, false);
      } catch (Exception var2) {
         return new HashMap();
      }
   }

   public static void browseTo(String string) {
      Realms.openUri(string);
   }

   public static String convertToAgePresentation(Long long) {
      if(long.longValue() < 0L) {
         return "right now";
      } else {
         long var1 = long.longValue() / 1000L;
         if(var1 < 60L) {
            return (var1 == 1L?"1 second":var1 + " seconds") + " ago";
         } else if(var1 < 3600L) {
            long var3 = var1 / 60L;
            return (var3 == 1L?"1 minute":var3 + " minutes") + " ago";
         } else if(var1 < 86400L) {
            long var3 = var1 / 3600L;
            return (var3 == 1L?"1 hour":var3 + " hours") + " ago";
         } else {
            long var3 = var1 / 86400L;
            return (var3 == 1L?"1 day":var3 + " days") + " ago";
         }
      }
   }
}
