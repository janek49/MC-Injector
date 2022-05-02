package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Effect;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.shaders.Uniform;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.TextureObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ChainedJsonException;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class EffectInstance implements Effect, AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AbstractUniform DUMMY_UNIFORM = new AbstractUniform();
   private static EffectInstance lastAppliedEffect;
   private static int lastProgramId = -1;
   private final Map samplerMap = Maps.newHashMap();
   private final List samplerNames = Lists.newArrayList();
   private final List samplerLocations = Lists.newArrayList();
   private final List uniforms = Lists.newArrayList();
   private final List uniformLocations = Lists.newArrayList();
   private final Map uniformMap = Maps.newHashMap();
   private final int programId;
   private final String name;
   private final boolean cull;
   private boolean dirty;
   private final BlendMode blend;
   private final List attributes;
   private final List attributeNames;
   private final Program vertexProgram;
   private final Program fragmentProgram;

   public EffectInstance(ResourceManager resourceManager, String name) throws IOException {
      ResourceLocation var3 = new ResourceLocation("shaders/program/" + name + ".json");
      this.name = name;
      Resource var4 = null;

      try {
         var4 = resourceManager.getResource(var3);
         JsonObject var5 = GsonHelper.parse((Reader)(new InputStreamReader(var4.getInputStream(), StandardCharsets.UTF_8)));
         String var6 = GsonHelper.getAsString(var5, "vertex");
         String var7 = GsonHelper.getAsString(var5, "fragment");
         JsonArray var8 = GsonHelper.getAsJsonArray(var5, "samplers", (JsonArray)null);
         if(var8 != null) {
            int var9 = 0;

            for(JsonElement var11 : var8) {
               try {
                  this.parseSamplerNode(var11);
               } catch (Exception var24) {
                  ChainedJsonException var13 = ChainedJsonException.forException(var24);
                  var13.prependJsonKey("samplers[" + var9 + "]");
                  throw var13;
               }

               ++var9;
            }
         }

         JsonArray var9 = GsonHelper.getAsJsonArray(var5, "attributes", (JsonArray)null);
         if(var9 != null) {
            int var10 = 0;
            this.attributes = Lists.newArrayListWithCapacity(var9.size());
            this.attributeNames = Lists.newArrayListWithCapacity(var9.size());

            for(JsonElement var12 : var9) {
               try {
                  this.attributeNames.add(GsonHelper.convertToString(var12, "attribute"));
               } catch (Exception var23) {
                  ChainedJsonException var14 = ChainedJsonException.forException(var23);
                  var14.prependJsonKey("attributes[" + var10 + "]");
                  throw var14;
               }

               ++var10;
            }
         } else {
            this.attributes = null;
            this.attributeNames = null;
         }

         JsonArray var10 = GsonHelper.getAsJsonArray(var5, "uniforms", (JsonArray)null);
         if(var10 != null) {
            int var11 = 0;

            for(JsonElement var13 : var10) {
               try {
                  this.parseUniformNode(var13);
               } catch (Exception var22) {
                  ChainedJsonException var15 = ChainedJsonException.forException(var22);
                  var15.prependJsonKey("uniforms[" + var11 + "]");
                  throw var15;
               }

               ++var11;
            }
         }

         this.blend = parseBlendNode(GsonHelper.getAsJsonObject(var5, "blend", (JsonObject)null));
         this.cull = GsonHelper.getAsBoolean(var5, "cull", true);
         this.vertexProgram = getOrCreate(resourceManager, Program.Type.VERTEX, var6);
         this.fragmentProgram = getOrCreate(resourceManager, Program.Type.FRAGMENT, var7);
         this.programId = ProgramManager.getInstance().createProgram();
         ProgramManager.getInstance().linkProgram(this);
         this.updateLocations();
         if(this.attributeNames != null) {
            for(String var12 : this.attributeNames) {
               int var13 = GLX.glGetAttribLocation(this.programId, var12);
               this.attributes.add(Integer.valueOf(var13));
            }
         }
      } catch (Exception var25) {
         ChainedJsonException var7 = ChainedJsonException.forException(var25);
         var7.setFilenameAndFlush(var3.getPath());
         throw var7;
      } finally {
         IOUtils.closeQuietly(var4);
      }

      this.markDirty();
   }

   public static Program getOrCreate(ResourceManager resourceManager, Program.Type program$Type, String string) throws IOException {
      Program program = (Program)program$Type.getPrograms().get(string);
      if(program == null) {
         ResourceLocation var4 = new ResourceLocation("shaders/program/" + string + program$Type.getExtension());
         Resource var5 = resourceManager.getResource(var4);

         try {
            program = Program.compileShader(program$Type, string, var5.getInputStream());
         } finally {
            IOUtils.closeQuietly(var5);
         }
      }

      return program;
   }

   public static BlendMode parseBlendNode(JsonObject jsonObject) {
      if(jsonObject == null) {
         return new BlendMode();
      } else {
         int var1 = '耆';
         int var2 = 1;
         int var3 = 0;
         int var4 = 1;
         int var5 = 0;
         boolean var6 = true;
         boolean var7 = false;
         if(GsonHelper.isStringValue(jsonObject, "func")) {
            var1 = BlendMode.stringToBlendFunc(jsonObject.get("func").getAsString());
            if(var1 != '耆') {
               var6 = false;
            }
         }

         if(GsonHelper.isStringValue(jsonObject, "srcrgb")) {
            var2 = BlendMode.stringToBlendFactor(jsonObject.get("srcrgb").getAsString());
            if(var2 != 1) {
               var6 = false;
            }
         }

         if(GsonHelper.isStringValue(jsonObject, "dstrgb")) {
            var3 = BlendMode.stringToBlendFactor(jsonObject.get("dstrgb").getAsString());
            if(var3 != 0) {
               var6 = false;
            }
         }

         if(GsonHelper.isStringValue(jsonObject, "srcalpha")) {
            var4 = BlendMode.stringToBlendFactor(jsonObject.get("srcalpha").getAsString());
            if(var4 != 1) {
               var6 = false;
            }

            var7 = true;
         }

         if(GsonHelper.isStringValue(jsonObject, "dstalpha")) {
            var5 = BlendMode.stringToBlendFactor(jsonObject.get("dstalpha").getAsString());
            if(var5 != 0) {
               var6 = false;
            }

            var7 = true;
         }

         return var6?new BlendMode():(var7?new BlendMode(var2, var3, var4, var5, var1):new BlendMode(var2, var3, var1));
      }
   }

   public void close() {
      for(Uniform var2 : this.uniforms) {
         var2.close();
      }

      ProgramManager.getInstance().releaseProgram(this);
   }

   public void clear() {
      GLX.glUseProgram(0);
      lastProgramId = -1;
      lastAppliedEffect = null;

      for(int var1 = 0; var1 < this.samplerLocations.size(); ++var1) {
         if(this.samplerMap.get(this.samplerNames.get(var1)) != null) {
            GlStateManager.activeTexture(GLX.GL_TEXTURE0 + var1);
            GlStateManager.bindTexture(0);
         }
      }

   }

   public void apply() {
      this.dirty = false;
      lastAppliedEffect = this;
      this.blend.apply();
      if(this.programId != lastProgramId) {
         GLX.glUseProgram(this.programId);
         lastProgramId = this.programId;
      }

      if(this.cull) {
         GlStateManager.enableCull();
      } else {
         GlStateManager.disableCull();
      }

      for(int var1 = 0; var1 < this.samplerLocations.size(); ++var1) {
         if(this.samplerMap.get(this.samplerNames.get(var1)) != null) {
            GlStateManager.activeTexture(GLX.GL_TEXTURE0 + var1);
            GlStateManager.enableTexture();
            Object var2 = this.samplerMap.get(this.samplerNames.get(var1));
            int var3 = -1;
            if(var2 instanceof RenderTarget) {
               var3 = ((RenderTarget)var2).colorTextureId;
            } else if(var2 instanceof TextureObject) {
               var3 = ((TextureObject)var2).getId();
            } else if(var2 instanceof Integer) {
               var3 = ((Integer)var2).intValue();
            }

            if(var3 != -1) {
               GlStateManager.bindTexture(var3);
               GLX.glUniform1i(GLX.glGetUniformLocation(this.programId, (CharSequence)this.samplerNames.get(var1)), var1);
            }
         }
      }

      for(Uniform var2 : this.uniforms) {
         var2.upload();
      }

   }

   public void markDirty() {
      this.dirty = true;
   }

   @Nullable
   public Uniform getUniform(String string) {
      return (Uniform)this.uniformMap.get(string);
   }

   public AbstractUniform safeGetUniform(String string) {
      Uniform var2 = this.getUniform(string);
      return (AbstractUniform)(var2 == null?DUMMY_UNIFORM:var2);
   }

   private void updateLocations() {
      int var1 = 0;

      for(int var2 = 0; var1 < this.samplerNames.size(); ++var2) {
         String var3 = (String)this.samplerNames.get(var1);
         int var4 = GLX.glGetUniformLocation(this.programId, var3);
         if(var4 == -1) {
            LOGGER.warn("Shader {}could not find sampler named {} in the specified shader program.", this.name, var3);
            this.samplerMap.remove(var3);
            this.samplerNames.remove(var2);
            --var2;
         } else {
            this.samplerLocations.add(Integer.valueOf(var4));
         }

         ++var1;
      }

      for(Uniform var2 : this.uniforms) {
         String var3 = var2.getName();
         int var4 = GLX.glGetUniformLocation(this.programId, var3);
         if(var4 == -1) {
            LOGGER.warn("Could not find uniform named {} in the specified shader program.", var3);
         } else {
            this.uniformLocations.add(Integer.valueOf(var4));
            var2.setLocation(var4);
            this.uniformMap.put(var3, var2);
         }
      }

   }

   private void parseSamplerNode(JsonElement jsonElement) {
      JsonObject var2 = GsonHelper.convertToJsonObject(jsonElement, "sampler");
      String var3 = GsonHelper.getAsString(var2, "name");
      if(!GsonHelper.isStringValue(var2, "file")) {
         this.samplerMap.put(var3, (Object)null);
         this.samplerNames.add(var3);
      } else {
         this.samplerNames.add(var3);
      }
   }

   public void setSampler(String string, Object object) {
      if(this.samplerMap.containsKey(string)) {
         this.samplerMap.remove(string);
      }

      this.samplerMap.put(string, object);
      this.markDirty();
   }

   private void parseUniformNode(JsonElement jsonElement) throws ChainedJsonException {
      JsonObject var2 = GsonHelper.convertToJsonObject(jsonElement, "uniform");
      String var3 = GsonHelper.getAsString(var2, "name");
      int var4 = Uniform.getTypeFromString(GsonHelper.getAsString(var2, "type"));
      int var5 = GsonHelper.getAsInt(var2, "count");
      float[] vars6 = new float[Math.max(var5, 16)];
      JsonArray var7 = GsonHelper.getAsJsonArray(var2, "values");
      if(var7.size() != var5 && var7.size() > 1) {
         throw new ChainedJsonException("Invalid amount of values specified (expected " + var5 + ", found " + var7.size() + ")");
      } else {
         int var8 = 0;

         for(JsonElement var10 : var7) {
            try {
               vars6[var8] = GsonHelper.convertToFloat(var10, "value");
            } catch (Exception var13) {
               ChainedJsonException var12 = ChainedJsonException.forException(var13);
               var12.prependJsonKey("values[" + var8 + "]");
               throw var12;
            }

            ++var8;
         }

         if(var5 > 1 && var7.size() == 1) {
            while(var8 < var5) {
               vars6[var8] = vars6[0];
               ++var8;
            }
         }

         int var9 = var5 > 1 && var5 <= 4 && var4 < 8?var5 - 1:0;
         Uniform var10 = new Uniform(var3, var4 + var9, var5, this);
         if(var4 <= 3) {
            var10.setSafe((int)vars6[0], (int)vars6[1], (int)vars6[2], (int)vars6[3]);
         } else if(var4 <= 7) {
            var10.setSafe(vars6[0], vars6[1], vars6[2], vars6[3]);
         } else {
            var10.set(vars6);
         }

         this.uniforms.add(var10);
      }
   }

   public Program getVertexProgram() {
      return this.vertexProgram;
   }

   public Program getFragmentProgram() {
      return this.fragmentProgram;
   }

   public int getId() {
      return this.programId;
   }
}
