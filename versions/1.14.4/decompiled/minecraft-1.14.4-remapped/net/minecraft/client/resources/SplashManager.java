package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

@ClientJarOnly
public class SplashManager extends SimplePreparableReloadListener {
   private static final ResourceLocation SPLASHES_LOCATION = new ResourceLocation("texts/splashes.txt");
   private static final Random RANDOM = new Random();
   private final List splashes = Lists.newArrayList();
   private final User user;

   public SplashManager(User user) {
      this.user = user;
   }

   protected List prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      try {
         Resource var3 = Minecraft.getInstance().getResourceManager().getResource(SPLASHES_LOCATION);
         Throwable var4 = null;

         Object var7;
         try {
            BufferedReader var5 = new BufferedReader(new InputStreamReader(var3.getInputStream(), StandardCharsets.UTF_8));
            Throwable var6 = null;

            try {
               var7 = (List)var5.lines().map(String::trim).filter((string) -> {
                  return string.hashCode() != 125780783;
               }).collect(Collectors.toList());
            } catch (Throwable var32) {
               var7 = var32;
               var6 = var32;
               throw var32;
            } finally {
               if(var5 != null) {
                  if(var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var31) {
                        var6.addSuppressed(var31);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (Throwable var34) {
            var4 = var34;
            throw var34;
         } finally {
            if(var3 != null) {
               if(var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var30) {
                     var4.addSuppressed(var30);
                  }
               } else {
                  var3.close();
               }
            }

         }

         return (List)var7;
      } catch (IOException var36) {
         return Collections.emptyList();
      }
   }

   protected void apply(List list, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      this.splashes.clear();
      this.splashes.addAll(list);
   }

   @Nullable
   public String getSplash() {
      Calendar var1 = Calendar.getInstance();
      var1.setTime(new Date());
      return var1.get(2) + 1 == 12 && var1.get(5) == 24?"Merry X-mas!":(var1.get(2) + 1 == 1 && var1.get(5) == 1?"Happy new year!":(var1.get(2) + 1 == 10 && var1.get(5) == 31?"OOoooOOOoooo! Spooky!":(this.splashes.isEmpty()?null:(this.user != null && RANDOM.nextInt(this.splashes.size()) == 42?this.user.getName().toUpperCase(Locale.ROOT) + " IS YOU":(String)this.splashes.get(RANDOM.nextInt(this.splashes.size()))))));
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
