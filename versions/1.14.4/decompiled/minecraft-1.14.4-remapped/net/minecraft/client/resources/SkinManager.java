package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.HttpTextureProcessor;
import net.minecraft.client.renderer.MobSkinTextureProcessor;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class SkinManager {
   private static final ExecutorService EXECUTOR_SERVICE = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());
   private final TextureManager textureManager;
   private final File skinsDirectory;
   private final MinecraftSessionService sessionService;
   private final LoadingCache insecureSkinCache;

   public SkinManager(TextureManager textureManager, File skinsDirectory, MinecraftSessionService sessionService) {
      this.textureManager = textureManager;
      this.skinsDirectory = skinsDirectory;
      this.sessionService = sessionService;
      this.insecureSkinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader() {
         public Map load(GameProfile gameProfile) throws Exception {
            try {
               return Minecraft.getInstance().getMinecraftSessionService().getTextures(gameProfile, false);
            } catch (Throwable var3) {
               return Maps.newHashMap();
            }
         }

         // $FF: synthetic method
         public Object load(Object var1) throws Exception {
            return this.load((GameProfile)var1);
         }
      });
   }

   public ResourceLocation registerTexture(MinecraftProfileTexture minecraftProfileTexture, Type minecraftProfileTexture$Type) {
      return this.registerTexture(minecraftProfileTexture, minecraftProfileTexture$Type, (SkinManager.SkinTextureCallback)null);
   }

   public ResourceLocation registerTexture(final MinecraftProfileTexture minecraftProfileTexture, final Type minecraftProfileTexture$Type, @Nullable final SkinManager.SkinTextureCallback skinManager$SkinTextureCallback) {
      String var4 = Hashing.sha1().hashUnencodedChars(minecraftProfileTexture.getHash()).toString();
      final ResourceLocation var5 = new ResourceLocation("skins/" + var4);
      TextureObject var6 = this.textureManager.getTexture(var5);
      if(var6 != null) {
         if(skinManager$SkinTextureCallback != null) {
            skinManager$SkinTextureCallback.onSkinTextureAvailable(minecraftProfileTexture$Type, var5, minecraftProfileTexture);
         }
      } else {
         File var7 = new File(this.skinsDirectory, var4.length() > 2?var4.substring(0, 2):"xx");
         File var8 = new File(var7, var4);
         final HttpTextureProcessor var9 = minecraftProfileTexture$Type == Type.SKIN?new MobSkinTextureProcessor():null;
         HttpTexture var10 = new HttpTexture(var8, minecraftProfileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkin(), new HttpTextureProcessor() {
            public NativeImage process(NativeImage nativeImage) {
               return var9 != null?var9.process(nativeImage):nativeImage;
            }

            public void onTextureDownloaded() {
               if(var9 != null) {
                  var9.onTextureDownloaded();
               }

               if(skinManager$SkinTextureCallback != null) {
                  skinManager$SkinTextureCallback.onSkinTextureAvailable(minecraftProfileTexture$Type, var5, minecraftProfileTexture);
               }

            }
         });
         this.textureManager.register((ResourceLocation)var5, (TextureObject)var10);
      }

      return var5;
   }

   public void registerSkins(GameProfile gameProfile, SkinManager.SkinTextureCallback skinManager$SkinTextureCallback, boolean var3) {
      EXECUTOR_SERVICE.submit(() -> {
         Map<Type, MinecraftProfileTexture> var4 = Maps.newHashMap();

         try {
            var4.putAll(this.sessionService.getTextures(gameProfile, var3));
         } catch (InsecureTextureException var7) {
            ;
         }

         if(var4.isEmpty()) {
            gameProfile.getProperties().clear();
            if(gameProfile.getId().equals(Minecraft.getInstance().getUser().getGameProfile().getId())) {
               gameProfile.getProperties().putAll(Minecraft.getInstance().getProfileProperties());
               var4.putAll(this.sessionService.getTextures(gameProfile, false));
            } else {
               this.sessionService.fillProfileProperties(gameProfile, var3);

               try {
                  var4.putAll(this.sessionService.getTextures(gameProfile, var3));
               } catch (InsecureTextureException var6) {
                  ;
               }
            }
         }

         Minecraft.getInstance().execute(() -> {
            if(var4.containsKey(Type.SKIN)) {
               this.registerTexture((MinecraftProfileTexture)var4.get(Type.SKIN), Type.SKIN, skinManager$SkinTextureCallback);
            }

            if(var4.containsKey(Type.CAPE)) {
               this.registerTexture((MinecraftProfileTexture)var4.get(Type.CAPE), Type.CAPE, skinManager$SkinTextureCallback);
            }

         });
      });
   }

   public Map getInsecureSkinInformation(GameProfile gameProfile) {
      return (Map)this.insecureSkinCache.getUnchecked(gameProfile);
   }

   @ClientJarOnly
   public interface SkinTextureCallback {
      void onSkinTextureAvailable(Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
   }
}
