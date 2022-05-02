package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.math.Matrix4f;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

@ClientJarOnly
public class PostChain implements AutoCloseable {
   private final RenderTarget screenTarget;
   private final ResourceManager resourceManager;
   private final String name;
   private final List passes = Lists.newArrayList();
   private final Map customRenderTargets = Maps.newHashMap();
   private final List fullSizedTargets = Lists.newArrayList();
   private Matrix4f shaderOrthoMatrix;
   private int screenWidth;
   private int screenHeight;
   private float time;
   private float lastStamp;

   public PostChain(TextureManager textureManager, ResourceManager resourceManager, RenderTarget screenTarget, ResourceLocation resourceLocation) throws IOException, JsonSyntaxException {
      this.resourceManager = resourceManager;
      this.screenTarget = screenTarget;
      this.time = 0.0F;
      this.lastStamp = 0.0F;
      this.screenWidth = screenTarget.viewWidth;
      this.screenHeight = screenTarget.viewHeight;
      this.name = resourceLocation.toString();
      this.updateOrthoMatrix();
      this.load(textureManager, resourceLocation);
   }

   private void load(TextureManager textureManager, ResourceLocation resourceLocation) throws IOException, JsonSyntaxException {
      Resource var3 = null;

      try {
         var3 = this.resourceManager.getResource(resourceLocation);
         JsonObject var4 = GsonHelper.parse((Reader)(new InputStreamReader(var3.getInputStream(), StandardCharsets.UTF_8)));
         if(GsonHelper.isArrayNode(var4, "targets")) {
            JsonArray var5 = var4.getAsJsonArray("targets");
            int var6 = 0;

            for(JsonElement var8 : var5) {
               try {
                  this.parseTargetNode(var8);
               } catch (Exception var17) {
                  ChainedJsonException var10 = ChainedJsonException.forException(var17);
                  var10.prependJsonKey("targets[" + var6 + "]");
                  throw var10;
               }

               ++var6;
            }
         }

         if(GsonHelper.isArrayNode(var4, "passes")) {
            JsonArray var5 = var4.getAsJsonArray("passes");
            int var6 = 0;

            for(JsonElement var8 : var5) {
               try {
                  this.parsePassNode(textureManager, var8);
               } catch (Exception var16) {
                  ChainedJsonException var10 = ChainedJsonException.forException(var16);
                  var10.prependJsonKey("passes[" + var6 + "]");
                  throw var10;
               }

               ++var6;
            }
         }
      } catch (Exception var18) {
         ChainedJsonException var5 = ChainedJsonException.forException(var18);
         var5.setFilenameAndFlush(resourceLocation.getPath());
         throw var5;
      } finally {
         IOUtils.closeQuietly(var3);
      }

   }

   private void parseTargetNode(JsonElement jsonElement) throws ChainedJsonException {
      if(GsonHelper.isStringValue(jsonElement)) {
         this.addTempTarget(jsonElement.getAsString(), this.screenWidth, this.screenHeight);
      } else {
         JsonObject var2 = GsonHelper.convertToJsonObject(jsonElement, "target");
         String var3 = GsonHelper.getAsString(var2, "name");
         int var4 = GsonHelper.getAsInt(var2, "width", this.screenWidth);
         int var5 = GsonHelper.getAsInt(var2, "height", this.screenHeight);
         if(this.customRenderTargets.containsKey(var3)) {
            throw new ChainedJsonException(var3 + " is already defined");
         }

         this.addTempTarget(var3, var4, var5);
      }

   }

   private void parsePassNode(TextureManager textureManager, JsonElement jsonElement) throws IOException {
      JsonObject var3 = GsonHelper.convertToJsonObject(jsonElement, "pass");
      String var4 = GsonHelper.getAsString(var3, "name");
      String var5 = GsonHelper.getAsString(var3, "intarget");
      String var6 = GsonHelper.getAsString(var3, "outtarget");
      RenderTarget var7 = this.getRenderTarget(var5);
      RenderTarget var8 = this.getRenderTarget(var6);
      if(var7 == null) {
         throw new ChainedJsonException("Input target \'" + var5 + "\' does not exist");
      } else if(var8 == null) {
         throw new ChainedJsonException("Output target \'" + var6 + "\' does not exist");
      } else {
         PostPass var9 = this.addPass(var4, var7, var8);
         JsonArray var10 = GsonHelper.getAsJsonArray(var3, "auxtargets", (JsonArray)null);
         if(var10 != null) {
            int var11 = 0;

            for(JsonElement var13 : var10) {
               try {
                  JsonObject var14 = GsonHelper.convertToJsonObject(var13, "auxtarget");
                  String var15 = GsonHelper.getAsString(var14, "name");
                  String var16 = GsonHelper.getAsString(var14, "id");
                  RenderTarget var17 = this.getRenderTarget(var16);
                  if(var17 == null) {
                     ResourceLocation var18 = new ResourceLocation("textures/effect/" + var16 + ".png");
                     Resource var19 = null;

                     try {
                        var19 = this.resourceManager.getResource(var18);
                     } catch (FileNotFoundException var29) {
                        throw new ChainedJsonException("Render target or texture \'" + var16 + "\' does not exist");
                     } finally {
                        IOUtils.closeQuietly(var19);
                     }

                     textureManager.bind(var18);
                     TextureObject var20 = textureManager.getTexture(var18);
                     int var21 = GsonHelper.getAsInt(var14, "width");
                     int var22 = GsonHelper.getAsInt(var14, "height");
                     boolean var23 = GsonHelper.getAsBoolean(var14, "bilinear");
                     if(var23) {
                        GlStateManager.texParameter(3553, 10241, 9729);
                        GlStateManager.texParameter(3553, 10240, 9729);
                     } else {
                        GlStateManager.texParameter(3553, 10241, 9728);
                        GlStateManager.texParameter(3553, 10240, 9728);
                     }

                     var9.addAuxAsset(var15, Integer.valueOf(var20.getId()), var21, var22);
                  } else {
                     var9.addAuxAsset(var15, var17, var17.width, var17.height);
                  }
               } catch (Exception var31) {
                  ChainedJsonException var15 = ChainedJsonException.forException(var31);
                  var15.prependJsonKey("auxtargets[" + var11 + "]");
                  throw var15;
               }

               ++var11;
            }
         }

         JsonArray var11 = GsonHelper.getAsJsonArray(var3, "uniforms", (JsonArray)null);
         if(var11 != null) {
            int var12 = 0;

            for(JsonElement var14 : var11) {
               try {
                  this.parseUniformNode(var14);
               } catch (Exception var28) {
                  ChainedJsonException var16 = ChainedJsonException.forException(var28);
                  var16.prependJsonKey("uniforms[" + var12 + "]");
                  throw var16;
               }

               ++var12;
            }
         }

      }
   }

   private void parseUniformNode(JsonElement jsonElement) throws ChainedJsonException {
      JsonObject var2 = GsonHelper.convertToJsonObject(jsonElement, "uniform");
      String var3 = GsonHelper.getAsString(var2, "name");
      Uniform var4 = ((PostPass)this.passes.get(this.passes.size() - 1)).getEffect().getUniform(var3);
      if(var4 == null) {
         throw new ChainedJsonException("Uniform \'" + var3 + "\' does not exist");
      } else {
         float[] vars5 = new float[4];
         int var6 = 0;

         for(JsonElement var9 : GsonHelper.getAsJsonArray(var2, "values")) {
            try {
               vars5[var6] = GsonHelper.convertToFloat(var9, "value");
            } catch (Exception var12) {
               ChainedJsonException var11 = ChainedJsonException.forException(var12);
               var11.prependJsonKey("values[" + var6 + "]");
               throw var11;
            }

            ++var6;
         }

         switch(var6) {
         case 0:
         default:
            break;
         case 1:
            var4.set(vars5[0]);
            break;
         case 2:
            var4.set(vars5[0], vars5[1]);
            break;
         case 3:
            var4.set(vars5[0], vars5[1], vars5[2]);
            break;
         case 4:
            var4.set(vars5[0], vars5[1], vars5[2], vars5[3]);
         }

      }
   }

   public RenderTarget getTempTarget(String string) {
      return (RenderTarget)this.customRenderTargets.get(string);
   }

   public void addTempTarget(String string, int var2, int var3) {
      RenderTarget var4 = new RenderTarget(var2, var3, true, Minecraft.ON_OSX);
      var4.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.customRenderTargets.put(string, var4);
      if(var2 == this.screenWidth && var3 == this.screenHeight) {
         this.fullSizedTargets.add(var4);
      }

   }

   public void close() {
      for(RenderTarget var2 : this.customRenderTargets.values()) {
         var2.destroyBuffers();
      }

      for(PostPass var2 : this.passes) {
         var2.close();
      }

      this.passes.clear();
   }

   public PostPass addPass(String string, RenderTarget var2, RenderTarget var3) throws IOException {
      PostPass postPass = new PostPass(this.resourceManager, string, var2, var3);
      this.passes.add(this.passes.size(), postPass);
      return postPass;
   }

   private void updateOrthoMatrix() {
      this.shaderOrthoMatrix = Matrix4f.orthographic((float)this.screenTarget.width, (float)this.screenTarget.height, 0.1F, 1000.0F);
   }

   public void resize(int var1, int var2) {
      this.screenWidth = this.screenTarget.width;
      this.screenHeight = this.screenTarget.height;
      this.updateOrthoMatrix();

      for(PostPass var4 : this.passes) {
         var4.setOrthoMatrix(this.shaderOrthoMatrix);
      }

      for(RenderTarget var4 : this.fullSizedTargets) {
         var4.resize(var1, var2, Minecraft.ON_OSX);
      }

   }

   public void process(float lastStamp) {
      if(lastStamp < this.lastStamp) {
         this.time += 1.0F - this.lastStamp;
         this.time += lastStamp;
      } else {
         this.time += lastStamp - this.lastStamp;
      }

      for(this.lastStamp = lastStamp; this.time > 20.0F; this.time -= 20.0F) {
         ;
      }

      for(PostPass var3 : this.passes) {
         var3.process(this.time / 20.0F);
      }

   }

   public final String getName() {
      return this.name;
   }

   private RenderTarget getRenderTarget(String string) {
      return string == null?null:(string.equals("minecraft:main")?this.screenTarget:(RenderTarget)this.customRenderTargets.get(string));
   }
}
