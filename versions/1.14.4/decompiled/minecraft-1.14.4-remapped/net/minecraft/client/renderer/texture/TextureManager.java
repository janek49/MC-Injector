package net.minecraft.client.renderer.texture;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.PreloadedTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.renderer.texture.TickableTextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class TextureManager implements Tickable, PreparableReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation INTENTIONAL_MISSING_TEXTURE = new ResourceLocation("");
   private final Map byPath = Maps.newHashMap();
   private final List tickableTextures = Lists.newArrayList();
   private final Map prefixRegister = Maps.newHashMap();
   private final ResourceManager resourceManager;

   public TextureManager(ResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void bind(ResourceLocation resourceLocation) {
      TextureObject var2 = (TextureObject)this.byPath.get(resourceLocation);
      if(var2 == null) {
         var2 = new SimpleTexture(resourceLocation);
         this.register(resourceLocation, var2);
      }

      var2.bind();
   }

   public boolean register(ResourceLocation resourceLocation, TickableTextureObject tickableTextureObject) {
      if(this.register((ResourceLocation)resourceLocation, (TextureObject)tickableTextureObject)) {
         this.tickableTextures.add(tickableTextureObject);
         return true;
      } else {
         return false;
      }
   }

   public boolean register(ResourceLocation resourceLocation, TextureObject textureObject) {
      boolean var3 = true;

      try {
         ((TextureObject)textureObject).load(this.resourceManager);
      } catch (IOException var8) {
         if(resourceLocation != INTENTIONAL_MISSING_TEXTURE) {
            LOGGER.warn("Failed to load texture: {}", resourceLocation, var8);
         }

         textureObject = MissingTextureAtlasSprite.getTexture();
         this.byPath.put(resourceLocation, textureObject);
         var3 = false;
      } catch (Throwable var9) {
         CrashReport var5 = CrashReport.forThrowable(var9, "Registering texture");
         CrashReportCategory var6 = var5.addCategory("Resource location being registered");
         var6.setDetail("Resource location", (Object)resourceLocation);
         var6.setDetail("Texture object class", () -> {
            return textureObject.getClass().getName();
         });
         throw new ReportedException(var5);
      }

      this.byPath.put(resourceLocation, textureObject);
      return var3;
   }

   public TextureObject getTexture(ResourceLocation resourceLocation) {
      return (TextureObject)this.byPath.get(resourceLocation);
   }

   public ResourceLocation register(String string, DynamicTexture dynamicTexture) {
      Integer var3 = (Integer)this.prefixRegister.get(string);
      if(var3 == null) {
         var3 = Integer.valueOf(1);
      } else {
         var3 = Integer.valueOf(var3.intValue() + 1);
      }

      this.prefixRegister.put(string, var3);
      ResourceLocation var4 = new ResourceLocation(String.format("dynamic/%s_%d", new Object[]{string, var3}));
      this.register((ResourceLocation)var4, (TextureObject)dynamicTexture);
      return var4;
   }

   public CompletableFuture preload(ResourceLocation resourceLocation, Executor executor) {
      if(!this.byPath.containsKey(resourceLocation)) {
         PreloadedTexture var3 = new PreloadedTexture(this.resourceManager, resourceLocation, executor);
         this.byPath.put(resourceLocation, var3);
         return var3.getFuture().thenRunAsync(() -> {
            this.register((ResourceLocation)resourceLocation, (TextureObject)var3);
         }, Minecraft.getInstance());
      } else {
         return CompletableFuture.completedFuture((Object)null);
      }
   }

   public void tick() {
      for(Tickable var2 : this.tickableTextures) {
         var2.tick();
      }

   }

   public void release(ResourceLocation resourceLocation) {
      TextureObject var2 = this.getTexture(resourceLocation);
      if(var2 != null) {
         TextureUtil.releaseTextureId(var2.getId());
      }

   }

   public CompletableFuture reload(PreparableReloadListener.PreparationBarrier preparableReloadListener$PreparationBarrier, ResourceManager resourceManager, ProfilerFiller var3, ProfilerFiller var4, Executor var5, Executor var6) {
      CompletableFuture var10000 = CompletableFuture.allOf(new CompletableFuture[]{TitleScreen.preloadResources(this, var5), this.preload(AbstractWidget.WIDGETS_LOCATION, var5)});
      preparableReloadListener$PreparationBarrier.getClass();
      return var10000.thenCompose(preparableReloadListener$PreparationBarrier::wait).thenAcceptAsync((void) -> {
         MissingTextureAtlasSprite.getTexture();
         Iterator<Entry<ResourceLocation, TextureObject>> var4 = this.byPath.entrySet().iterator();

         while(var4.hasNext()) {
            Entry<ResourceLocation, TextureObject> var5 = (Entry)var4.next();
            ResourceLocation var6 = (ResourceLocation)var5.getKey();
            TextureObject var7 = (TextureObject)var5.getValue();
            if(var7 == MissingTextureAtlasSprite.getTexture() && !var6.equals(MissingTextureAtlasSprite.getLocation())) {
               var4.remove();
            } else {
               var7.reset(this, resourceManager, var6, var6);
            }
         }

      }, var6);
   }
}
