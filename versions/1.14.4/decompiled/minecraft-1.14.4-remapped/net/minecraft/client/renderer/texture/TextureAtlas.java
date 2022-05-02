package net.minecraft.client.renderer.texture;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.PngInfo;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.StitcherException;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TickableTextureObject;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class TextureAtlas extends AbstractTexture implements TickableTextureObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation LOCATION_BLOCKS = new ResourceLocation("textures/atlas/blocks.png");
   public static final ResourceLocation LOCATION_PARTICLES = new ResourceLocation("textures/atlas/particles.png");
   public static final ResourceLocation LOCATION_PAINTINGS = new ResourceLocation("textures/atlas/paintings.png");
   public static final ResourceLocation LOCATION_MOB_EFFECTS = new ResourceLocation("textures/atlas/mob_effects.png");
   private final List animatedTextures = Lists.newArrayList();
   private final Set sprites = Sets.newHashSet();
   private final Map texturesByName = Maps.newHashMap();
   private final String path;
   private final int maxSupportedTextureSize;
   private int maxMipLevel;
   private final TextureAtlasSprite missingTextureSprite = MissingTextureAtlasSprite.newInstance();

   public TextureAtlas(String path) {
      this.path = path;
      this.maxSupportedTextureSize = Minecraft.maxSupportedTextureSize();
   }

   public void load(ResourceManager resourceManager) throws IOException {
   }

   public void reload(TextureAtlas.Preparations textureAtlas$Preparations) {
      this.sprites.clear();
      this.sprites.addAll(textureAtlas$Preparations.sprites);
      LOGGER.info("Created: {}x{} {}-atlas", Integer.valueOf(textureAtlas$Preparations.width), Integer.valueOf(textureAtlas$Preparations.height), this.path);
      TextureUtil.prepareImage(this.getId(), this.maxMipLevel, textureAtlas$Preparations.width, textureAtlas$Preparations.height);
      this.clearTextureData();

      for(TextureAtlasSprite var3 : textureAtlas$Preparations.regions) {
         this.texturesByName.put(var3.getName(), var3);

         try {
            var3.uploadFirstFrame();
         } catch (Throwable var7) {
            CrashReport var5 = CrashReport.forThrowable(var7, "Stitching texture atlas");
            CrashReportCategory var6 = var5.addCategory("Texture being stitched together");
            var6.setDetail("Atlas path", (Object)this.path);
            var6.setDetail("Sprite", (Object)var3);
            throw new ReportedException(var5);
         }

         if(var3.isAnimation()) {
            this.animatedTextures.add(var3);
         }
      }

   }

   public TextureAtlas.Preparations prepareToStitch(ResourceManager resourceManager, Iterable iterable, ProfilerFiller profilerFiller) {
      Set<ResourceLocation> var4 = Sets.newHashSet();
      profilerFiller.push("preparing");
      iterable.forEach((resourceLocation) -> {
         if(resourceLocation == null) {
            throw new IllegalArgumentException("Location cannot be null!");
         } else {
            var4.add(resourceLocation);
         }
      });
      int var5 = this.maxSupportedTextureSize;
      Stitcher var6 = new Stitcher(var5, var5, this.maxMipLevel);
      int var7 = Integer.MAX_VALUE;
      int var8 = 1 << this.maxMipLevel;
      profilerFiller.popPush("extracting_frames");

      for(TextureAtlasSprite var10 : this.getBasicSpriteInfos(resourceManager, var4)) {
         var7 = Math.min(var7, Math.min(var10.getWidth(), var10.getHeight()));
         int var11 = Math.min(Integer.lowestOneBit(var10.getWidth()), Integer.lowestOneBit(var10.getHeight()));
         if(var11 < var8) {
            LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", var10.getName(), Integer.valueOf(var10.getWidth()), Integer.valueOf(var10.getHeight()), Integer.valueOf(Mth.log2(var8)), Integer.valueOf(Mth.log2(var11)));
            var8 = var11;
         }

         var6.registerSprite(var10);
      }

      int var9 = Math.min(var7, var8);
      int var10 = Mth.log2(var9);
      if(var10 < this.maxMipLevel) {
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.path, Integer.valueOf(this.maxMipLevel), Integer.valueOf(var10), Integer.valueOf(var9));
         this.maxMipLevel = var10;
      }

      profilerFiller.popPush("mipmapping");
      this.missingTextureSprite.applyMipmapping(this.maxMipLevel);
      profilerFiller.popPush("register");
      var6.registerSprite(this.missingTextureSprite);
      profilerFiller.popPush("stitching");

      try {
         var6.stitch();
      } catch (StitcherException var14) {
         CrashReport var12 = CrashReport.forThrowable(var14, "Stitching");
         CrashReportCategory var13 = var12.addCategory("Stitcher");
         var13.setDetail("Sprites", var14.getAllSprites().stream().map((textureAtlasSprite) -> {
            return String.format("%s[%dx%d]", new Object[]{textureAtlasSprite.getName(), Integer.valueOf(textureAtlasSprite.getWidth()), Integer.valueOf(textureAtlasSprite.getHeight())});
         }).collect(Collectors.joining(",")));
         var13.setDetail("Max Texture Size", (Object)Integer.valueOf(var5));
         throw new ReportedException(var12);
      }

      profilerFiller.popPush("loading");
      List<TextureAtlasSprite> var11 = this.getLoadedSprites(resourceManager, var6);
      profilerFiller.pop();
      return new TextureAtlas.Preparations(var4, var6.getWidth(), var6.getHeight(), var11);
   }

   private Collection getBasicSpriteInfos(ResourceManager resourceManager, Set set) {
      List<CompletableFuture<?>> var3 = new ArrayList();
      ConcurrentLinkedQueue<TextureAtlasSprite> var4 = new ConcurrentLinkedQueue();

      for(ResourceLocation var6 : set) {
         if(!this.missingTextureSprite.getName().equals(var6)) {
            var3.add(CompletableFuture.runAsync(() -> {
               ResourceLocation resourceLocation = this.getResourceLocation(var6);

               TextureAtlasSprite var5;
               try {
                  Resource var6 = resourceManager.getResource(resourceLocation);
                  Throwable var7 = null;

                  try {
                     PngInfo var8 = new PngInfo(var6.toString(), var6.getInputStream());
                     AnimationMetadataSection var9 = (AnimationMetadataSection)var6.getMetadata(AnimationMetadataSection.SERIALIZER);
                     var5 = new TextureAtlasSprite(var6, var8, var9);
                  } catch (Throwable var19) {
                     var7 = var19;
                     throw var19;
                  } finally {
                     if(var6 != null) {
                        if(var7 != null) {
                           try {
                              var6.close();
                           } catch (Throwable var18) {
                              var7.addSuppressed(var18);
                           }
                        } else {
                           var6.close();
                        }
                     }

                  }
               } catch (RuntimeException var21) {
                  LOGGER.error("Unable to parse metadata from {} : {}", resourceLocation, var21);
                  return;
               } catch (IOException var22) {
                  LOGGER.error("Using missing texture, unable to load {} : {}", resourceLocation, var22);
                  return;
               }

               var4.add(var5);
            }, Util.backgroundExecutor()));
         }
      }

      CompletableFuture.allOf((CompletableFuture[])var3.toArray(new CompletableFuture[0])).join();
      return var4;
   }

   private List getLoadedSprites(ResourceManager resourceManager, Stitcher stitcher) {
      ConcurrentLinkedQueue<TextureAtlasSprite> var3 = new ConcurrentLinkedQueue();
      List<CompletableFuture<?>> var4 = new ArrayList();

      for(TextureAtlasSprite var6 : stitcher.gatherSprites()) {
         if(var6 == this.missingTextureSprite) {
            var3.add(var6);
         } else {
            var4.add(CompletableFuture.runAsync(() -> {
               if(this.load(resourceManager, var6)) {
                  var3.add(var6);
               }

            }, Util.backgroundExecutor()));
         }
      }

      CompletableFuture.allOf((CompletableFuture[])var4.toArray(new CompletableFuture[0])).join();
      return new ArrayList(var3);
   }

   private boolean load(ResourceManager resourceManager, TextureAtlasSprite textureAtlasSprite) {
      ResourceLocation var3 = this.getResourceLocation(textureAtlasSprite.getName());
      Resource var4 = null;

      label12: {
         boolean var16;
         try {
            var4 = resourceManager.getResource(var3);
            textureAtlasSprite.loadData(var4, this.maxMipLevel + 1);
            break label12;
         } catch (RuntimeException var13) {
            LOGGER.error("Unable to parse metadata from {}", var3, var13);
            var16 = false;
         } catch (IOException var14) {
            LOGGER.error("Using missing texture, unable to load {}", var3, var14);
            var16 = false;
            return var16;
         } finally {
            IOUtils.closeQuietly(var4);
         }

         return var16;
      }

      try {
         textureAtlasSprite.applyMipmapping(this.maxMipLevel);
         return true;
      } catch (Throwable var12) {
         CrashReport var6 = CrashReport.forThrowable(var12, "Applying mipmap");
         CrashReportCategory var7 = var6.addCategory("Sprite being mipmapped");
         var7.setDetail("Sprite name", () -> {
            return textureAtlasSprite.getName().toString();
         });
         var7.setDetail("Sprite size", () -> {
            return textureAtlasSprite.getWidth() + " x " + textureAtlasSprite.getHeight();
         });
         var7.setDetail("Sprite frames", () -> {
            return textureAtlasSprite.getFrameCount() + " frames";
         });
         var7.setDetail("Mipmap levels", (Object)Integer.valueOf(this.maxMipLevel));
         throw new ReportedException(var6);
      }
   }

   private ResourceLocation getResourceLocation(ResourceLocation resourceLocation) {
      return new ResourceLocation(resourceLocation.getNamespace(), String.format("%s/%s%s", new Object[]{this.path, resourceLocation.getPath(), ".png"}));
   }

   public TextureAtlasSprite getTexture(String string) {
      return this.getSprite(new ResourceLocation(string));
   }

   public void cycleAnimationFrames() {
      this.bind();

      for(TextureAtlasSprite var2 : this.animatedTextures) {
         var2.cycleFrames();
      }

   }

   public void tick() {
      this.cycleAnimationFrames();
   }

   public void setMaxMipLevel(int maxMipLevel) {
      this.maxMipLevel = maxMipLevel;
   }

   public TextureAtlasSprite getSprite(ResourceLocation resourceLocation) {
      TextureAtlasSprite textureAtlasSprite = (TextureAtlasSprite)this.texturesByName.get(resourceLocation);
      return textureAtlasSprite == null?this.missingTextureSprite:textureAtlasSprite;
   }

   public void clearTextureData() {
      for(TextureAtlasSprite var2 : this.texturesByName.values()) {
         var2.wipeFrameData();
      }

      this.texturesByName.clear();
      this.animatedTextures.clear();
   }

   @ClientJarOnly
   public static class Preparations {
      final Set sprites;
      final int width;
      final int height;
      final List regions;

      public Preparations(Set sprites, int width, int height, List regions) {
         this.sprites = sprites;
         this.width = width;
         this.height = height;
         this.regions = regions;
      }
   }
}
