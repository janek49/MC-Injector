package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.DebugMemoryUntracker;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Matrix4f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.system.MemoryUtil;

@ClientJarOnly
public class GlStateManager {
   private static final int LIGHT_COUNT = 8;
   private static final int TEXTURE_COUNT = 8;
   private static final FloatBuffer MATRIX_BUFFER = (FloatBuffer)GLX.make(MemoryUtil.memAllocFloat(16), (floatBuffer) -> {
      DebugMemoryUntracker.untrack(MemoryUtil.memAddress(floatBuffer));
   });
   private static final FloatBuffer COLOR_BUFFER = (FloatBuffer)GLX.make(MemoryUtil.memAllocFloat(4), (floatBuffer) -> {
      DebugMemoryUntracker.untrack(MemoryUtil.memAddress(floatBuffer));
   });
   private static final GlStateManager.AlphaState ALPHA_TEST = new GlStateManager.AlphaState();
   private static final GlStateManager.BooleanState LIGHTING = new GlStateManager.BooleanState(2896);
   private static final GlStateManager.BooleanState[] LIGHT_ENABLE = (GlStateManager.BooleanState[])IntStream.range(0, 8).mapToObj((i) -> {
      return new GlStateManager.BooleanState(16384 + i);
   }).toArray((i) -> {
      return new GlStateManager.BooleanState[i];
   });
   private static final GlStateManager.ColorMaterialState COLOR_MATERIAL = new GlStateManager.ColorMaterialState();
   private static final GlStateManager.BlendState BLEND = new GlStateManager.BlendState();
   private static final GlStateManager.DepthState DEPTH = new GlStateManager.DepthState();
   private static final GlStateManager.FogState FOG = new GlStateManager.FogState();
   private static final GlStateManager.CullState CULL = new GlStateManager.CullState();
   private static final GlStateManager.PolygonOffsetState POLY_OFFSET = new GlStateManager.PolygonOffsetState();
   private static final GlStateManager.ColorLogicState COLOR_LOGIC = new GlStateManager.ColorLogicState();
   private static final GlStateManager.TexGenState TEX_GEN = new GlStateManager.TexGenState();
   private static final GlStateManager.ClearState CLEAR = new GlStateManager.ClearState();
   private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
   private static final GlStateManager.BooleanState NORMALIZE = new GlStateManager.BooleanState(2977);
   private static int activeTexture;
   private static final GlStateManager.TextureState[] TEXTURES = (GlStateManager.TextureState[])IntStream.range(0, 8).mapToObj((i) -> {
      return new GlStateManager.TextureState();
   }).toArray((i) -> {
      return new GlStateManager.TextureState[i];
   });
   private static int shadeModel = 7425;
   private static final GlStateManager.BooleanState RESCALE_NORMAL = new GlStateManager.BooleanState('耺');
   private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();
   private static final GlStateManager.Color COLOR = new GlStateManager.Color();
   private static final float DEFAULTALPHACUTOFF = 0.1F;

   public static void pushLightingAttributes() {
      GL11.glPushAttrib(8256);
   }

   public static void pushTextureAttributes() {
      GL11.glPushAttrib(270336);
   }

   public static void popAttributes() {
      GL11.glPopAttrib();
   }

   public static void disableAlphaTest() {
      ALPHA_TEST.mode.disable();
   }

   public static void enableAlphaTest() {
      ALPHA_TEST.mode.enable();
   }

   public static void alphaFunc(int var0, float var1) {
      if(var0 != ALPHA_TEST.func || var1 != ALPHA_TEST.reference) {
         ALPHA_TEST.func = var0;
         ALPHA_TEST.reference = var1;
         GL11.glAlphaFunc(var0, var1);
      }

   }

   public static void enableLighting() {
      LIGHTING.enable();
   }

   public static void disableLighting() {
      LIGHTING.disable();
   }

   public static void enableLight(int i) {
      LIGHT_ENABLE[i].enable();
   }

   public static void disableLight(int i) {
      LIGHT_ENABLE[i].disable();
   }

   public static void enableColorMaterial() {
      COLOR_MATERIAL.enable.enable();
   }

   public static void disableColorMaterial() {
      COLOR_MATERIAL.enable.disable();
   }

   public static void colorMaterial(int var0, int var1) {
      if(var0 != COLOR_MATERIAL.face || var1 != COLOR_MATERIAL.mode) {
         COLOR_MATERIAL.face = var0;
         COLOR_MATERIAL.mode = var1;
         GL11.glColorMaterial(var0, var1);
      }

   }

   public static void light(int var0, int var1, FloatBuffer floatBuffer) {
      GL11.glLightfv(var0, var1, floatBuffer);
   }

   public static void lightModel(int var0, FloatBuffer floatBuffer) {
      GL11.glLightModelfv(var0, floatBuffer);
   }

   public static void normal3f(float var0, float var1, float var2) {
      GL11.glNormal3f(var0, var1, var2);
   }

   public static void disableDepthTest() {
      DEPTH.mode.disable();
   }

   public static void enableDepthTest() {
      DEPTH.mode.enable();
   }

   public static void depthFunc(int i) {
      if(i != DEPTH.func) {
         DEPTH.func = i;
         GL11.glDepthFunc(i);
      }

   }

   public static void depthMask(boolean b) {
      if(b != DEPTH.mask) {
         DEPTH.mask = b;
         GL11.glDepthMask(b);
      }

   }

   public static void disableBlend() {
      BLEND.mode.disable();
   }

   public static void enableBlend() {
      BLEND.mode.enable();
   }

   public static void blendFunc(GlStateManager.SourceFactor glStateManager$SourceFactor, GlStateManager.DestFactor glStateManager$DestFactor) {
      blendFunc(glStateManager$SourceFactor.value, glStateManager$DestFactor.value);
   }

   public static void blendFunc(int var0, int var1) {
      if(var0 != BLEND.srcRgb || var1 != BLEND.dstRgb) {
         BLEND.srcRgb = var0;
         BLEND.dstRgb = var1;
         GL11.glBlendFunc(var0, var1);
      }

   }

   public static void blendFuncSeparate(GlStateManager.SourceFactor var0, GlStateManager.DestFactor var1, GlStateManager.SourceFactor var2, GlStateManager.DestFactor var3) {
      blendFuncSeparate(var0.value, var1.value, var2.value, var3.value);
   }

   public static void blendFuncSeparate(int var0, int var1, int var2, int var3) {
      if(var0 != BLEND.srcRgb || var1 != BLEND.dstRgb || var2 != BLEND.srcAlpha || var3 != BLEND.dstAlpha) {
         BLEND.srcRgb = var0;
         BLEND.dstRgb = var1;
         BLEND.srcAlpha = var2;
         BLEND.dstAlpha = var3;
         GLX.glBlendFuncSeparate(var0, var1, var2, var3);
      }

   }

   public static void blendEquation(int i) {
      GL14.glBlendEquation(i);
   }

   public static void setupSolidRenderingTextureCombine(int i) {
      COLOR_BUFFER.put(0, (float)(i >> 16 & 255) / 255.0F);
      COLOR_BUFFER.put(1, (float)(i >> 8 & 255) / 255.0F);
      COLOR_BUFFER.put(2, (float)(i >> 0 & 255) / 255.0F);
      COLOR_BUFFER.put(3, (float)(i >> 24 & 255) / 255.0F);
      texEnv(8960, 8705, COLOR_BUFFER);
      texEnv(8960, 8704, '蕰');
      texEnv(8960, '蕱', 7681);
      texEnv(8960, '薀', '蕶');
      texEnv(8960, '薐', 768);
      texEnv(8960, '蕲', 7681);
      texEnv(8960, '薈', 5890);
      texEnv(8960, '薘', 770);
   }

   public static void tearDownSolidRenderingTextureCombine() {
      texEnv(8960, 8704, 8448);
      texEnv(8960, '蕱', 8448);
      texEnv(8960, '蕲', 8448);
      texEnv(8960, '薀', 5890);
      texEnv(8960, '薈', 5890);
      texEnv(8960, '薐', 768);
      texEnv(8960, '薘', 770);
   }

   public static void enableFog() {
      FOG.enable.enable();
   }

   public static void disableFog() {
      FOG.enable.disable();
   }

   public static void fogMode(GlStateManager.FogMode glStateManager$FogMode) {
      fogMode(glStateManager$FogMode.value);
   }

   private static void fogMode(int i) {
      if(i != FOG.mode) {
         FOG.mode = i;
         GL11.glFogi(2917, i);
      }

   }

   public static void fogDensity(float f) {
      if(f != FOG.density) {
         FOG.density = f;
         GL11.glFogf(2914, f);
      }

   }

   public static void fogStart(float f) {
      if(f != FOG.start) {
         FOG.start = f;
         GL11.glFogf(2915, f);
      }

   }

   public static void fogEnd(float f) {
      if(f != FOG.end) {
         FOG.end = f;
         GL11.glFogf(2916, f);
      }

   }

   public static void fog(int var0, FloatBuffer floatBuffer) {
      GL11.glFogfv(var0, floatBuffer);
   }

   public static void fogi(int var0, int var1) {
      GL11.glFogi(var0, var1);
   }

   public static void enableCull() {
      CULL.enable.enable();
   }

   public static void disableCull() {
      CULL.enable.disable();
   }

   public static void cullFace(GlStateManager.CullFace glStateManager$CullFace) {
      cullFace(glStateManager$CullFace.value);
   }

   private static void cullFace(int i) {
      if(i != CULL.mode) {
         CULL.mode = i;
         GL11.glCullFace(i);
      }

   }

   public static void polygonMode(int var0, int var1) {
      GL11.glPolygonMode(var0, var1);
   }

   public static void enablePolygonOffset() {
      POLY_OFFSET.fill.enable();
   }

   public static void disablePolygonOffset() {
      POLY_OFFSET.fill.disable();
   }

   public static void enableLineOffset() {
      POLY_OFFSET.line.enable();
   }

   public static void disableLineOffset() {
      POLY_OFFSET.line.disable();
   }

   public static void polygonOffset(float var0, float var1) {
      if(var0 != POLY_OFFSET.factor || var1 != POLY_OFFSET.units) {
         POLY_OFFSET.factor = var0;
         POLY_OFFSET.units = var1;
         GL11.glPolygonOffset(var0, var1);
      }

   }

   public static void enableColorLogicOp() {
      COLOR_LOGIC.enable.enable();
   }

   public static void disableColorLogicOp() {
      COLOR_LOGIC.enable.disable();
   }

   public static void logicOp(GlStateManager.LogicOp glStateManager$LogicOp) {
      logicOp(glStateManager$LogicOp.value);
   }

   public static void logicOp(int i) {
      if(i != COLOR_LOGIC.op) {
         COLOR_LOGIC.op = i;
         GL11.glLogicOp(i);
      }

   }

   public static void enableTexGen(GlStateManager.TexGen glStateManager$TexGen) {
      getTexGen(glStateManager$TexGen).enable.enable();
   }

   public static void disableTexGen(GlStateManager.TexGen glStateManager$TexGen) {
      getTexGen(glStateManager$TexGen).enable.disable();
   }

   public static void texGenMode(GlStateManager.TexGen glStateManager$TexGen, int var1) {
      GlStateManager.TexGenCoord var2 = getTexGen(glStateManager$TexGen);
      if(var1 != var2.mode) {
         var2.mode = var1;
         GL11.glTexGeni(var2.coord, 9472, var1);
      }

   }

   public static void texGenParam(GlStateManager.TexGen glStateManager$TexGen, int var1, FloatBuffer floatBuffer) {
      GL11.glTexGenfv(getTexGen(glStateManager$TexGen).coord, var1, floatBuffer);
   }

   private static GlStateManager.TexGenCoord getTexGen(GlStateManager.TexGen glStateManager$TexGen) {
      switch(glStateManager$TexGen) {
      case S:
         return TEX_GEN.s;
      case T:
         return TEX_GEN.t;
      case R:
         return TEX_GEN.r;
      case Q:
         return TEX_GEN.q;
      default:
         return TEX_GEN.s;
      }
   }

   public static void activeTexture(int i) {
      if(activeTexture != i - GLX.GL_TEXTURE0) {
         activeTexture = i - GLX.GL_TEXTURE0;
         GLX.glActiveTexture(i);
      }

   }

   public static void enableTexture() {
      TEXTURES[activeTexture].enable.enable();
   }

   public static void disableTexture() {
      TEXTURES[activeTexture].enable.disable();
   }

   public static void texEnv(int var0, int var1, FloatBuffer floatBuffer) {
      GL11.glTexEnvfv(var0, var1, floatBuffer);
   }

   public static void texEnv(int var0, int var1, int var2) {
      GL11.glTexEnvi(var0, var1, var2);
   }

   public static void texEnv(int var0, int var1, float var2) {
      GL11.glTexEnvf(var0, var1, var2);
   }

   public static void texParameter(int var0, int var1, float var2) {
      GL11.glTexParameterf(var0, var1, var2);
   }

   public static void texParameter(int var0, int var1, int var2) {
      GL11.glTexParameteri(var0, var1, var2);
   }

   public static int getTexLevelParameter(int var0, int var1, int var2) {
      return GL11.glGetTexLevelParameteri(var0, var1, var2);
   }

   public static int genTexture() {
      return GL11.glGenTextures();
   }

   public static void deleteTexture(int i) {
      GL11.glDeleteTextures(i);

      for(GlStateManager.TextureState var4 : TEXTURES) {
         if(var4.binding == i) {
            var4.binding = -1;
         }
      }

   }

   public static void bindTexture(int i) {
      if(i != TEXTURES[activeTexture].binding) {
         TEXTURES[activeTexture].binding = i;
         GL11.glBindTexture(3553, i);
      }

   }

   public static void texImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, @Nullable IntBuffer intBuffer) {
      GL11.glTexImage2D(var0, var1, var2, var3, var4, var5, var6, var7, intBuffer);
   }

   public static void texSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8) {
      GL11.glTexSubImage2D(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public static void copyTexSubImage2D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      GL11.glCopyTexSubImage2D(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public static void getTexImage(int var0, int var1, int var2, int var3, long var4) {
      GL11.glGetTexImage(var0, var1, var2, var3, var4);
   }

   public static void enableNormalize() {
      NORMALIZE.enable();
   }

   public static void disableNormalize() {
      NORMALIZE.disable();
   }

   public static void shadeModel(int i) {
      if(i != shadeModel) {
         shadeModel = i;
         GL11.glShadeModel(i);
      }

   }

   public static void enableRescaleNormal() {
      RESCALE_NORMAL.enable();
   }

   public static void disableRescaleNormal() {
      RESCALE_NORMAL.disable();
   }

   public static void viewport(int var0, int var1, int var2, int var3) {
      GlStateManager.Viewport.INSTANCE.x = var0;
      GlStateManager.Viewport.INSTANCE.y = var1;
      GlStateManager.Viewport.INSTANCE.width = var2;
      GlStateManager.Viewport.INSTANCE.height = var3;
      GL11.glViewport(var0, var1, var2, var3);
   }

   public static void colorMask(boolean var0, boolean var1, boolean var2, boolean var3) {
      if(var0 != COLOR_MASK.red || var1 != COLOR_MASK.green || var2 != COLOR_MASK.blue || var3 != COLOR_MASK.alpha) {
         COLOR_MASK.red = var0;
         COLOR_MASK.green = var1;
         COLOR_MASK.blue = var2;
         COLOR_MASK.alpha = var3;
         GL11.glColorMask(var0, var1, var2, var3);
      }

   }

   public static void stencilFunc(int var0, int var1, int var2) {
      if(var0 != STENCIL.func.func || var0 != STENCIL.func.ref || var0 != STENCIL.func.mask) {
         STENCIL.func.func = var0;
         STENCIL.func.ref = var1;
         STENCIL.func.mask = var2;
         GL11.glStencilFunc(var0, var1, var2);
      }

   }

   public static void stencilMask(int i) {
      if(i != STENCIL.mask) {
         STENCIL.mask = i;
         GL11.glStencilMask(i);
      }

   }

   public static void stencilOp(int var0, int var1, int var2) {
      if(var0 != STENCIL.fail || var1 != STENCIL.zfail || var2 != STENCIL.zpass) {
         STENCIL.fail = var0;
         STENCIL.zfail = var1;
         STENCIL.zpass = var2;
         GL11.glStencilOp(var0, var1, var2);
      }

   }

   public static void clearDepth(double d) {
      if(d != CLEAR.depth) {
         CLEAR.depth = d;
         GL11.glClearDepth(d);
      }

   }

   public static void clearColor(float var0, float var1, float var2, float var3) {
      if(var0 != CLEAR.color.r || var1 != CLEAR.color.g || var2 != CLEAR.color.b || var3 != CLEAR.color.a) {
         CLEAR.color.r = var0;
         CLEAR.color.g = var1;
         CLEAR.color.b = var2;
         CLEAR.color.a = var3;
         GL11.glClearColor(var0, var1, var2, var3);
      }

   }

   public static void clearStencil(int i) {
      if(i != CLEAR.stencil) {
         CLEAR.stencil = i;
         GL11.glClearStencil(i);
      }

   }

   public static void clear(int var0, boolean var1) {
      GL11.glClear(var0);
      if(var1) {
         getError();
      }

   }

   public static void matrixMode(int i) {
      GL11.glMatrixMode(i);
   }

   public static void loadIdentity() {
      GL11.glLoadIdentity();
   }

   public static void pushMatrix() {
      GL11.glPushMatrix();
   }

   public static void popMatrix() {
      GL11.glPopMatrix();
   }

   public static void getMatrix(int var0, FloatBuffer floatBuffer) {
      GL11.glGetFloatv(var0, floatBuffer);
   }

   public static Matrix4f getMatrix4f(int i) {
      GL11.glGetFloatv(i, MATRIX_BUFFER);
      MATRIX_BUFFER.rewind();
      Matrix4f matrix4f = new Matrix4f();
      matrix4f.load(MATRIX_BUFFER);
      MATRIX_BUFFER.rewind();
      return matrix4f;
   }

   public static void ortho(double var0, double var2, double var4, double var6, double var8, double var10) {
      GL11.glOrtho(var0, var2, var4, var6, var8, var10);
   }

   public static void rotatef(float var0, float var1, float var2, float var3) {
      GL11.glRotatef(var0, var1, var2, var3);
   }

   public static void rotated(double var0, double var2, double var4, double var6) {
      GL11.glRotated(var0, var2, var4, var6);
   }

   public static void scalef(float var0, float var1, float var2) {
      GL11.glScalef(var0, var1, var2);
   }

   public static void scaled(double x, double x, double x) {
      GL11.glScaled(x, x, x);
   }

   public static void translatef(float var0, float var1, float var2) {
      GL11.glTranslatef(var0, var1, var2);
   }

   public static void translated(double x, double x, double x) {
      GL11.glTranslated(x, x, x);
   }

   public static void multMatrix(FloatBuffer floatBuffer) {
      GL11.glMultMatrixf(floatBuffer);
   }

   public static void multMatrix(Matrix4f matrix4f) {
      matrix4f.store(MATRIX_BUFFER);
      MATRIX_BUFFER.rewind();
      GL11.glMultMatrixf(MATRIX_BUFFER);
   }

   public static void color4f(float var0, float var1, float var2, float var3) {
      if(var0 != COLOR.r || var1 != COLOR.g || var2 != COLOR.b || var3 != COLOR.a) {
         COLOR.r = var0;
         COLOR.g = var1;
         COLOR.b = var2;
         COLOR.a = var3;
         GL11.glColor4f(var0, var1, var2, var3);
      }

   }

   public static void color3f(float var0, float var1, float var2) {
      color4f(var0, var1, var2, 1.0F);
   }

   public static void texCoord2f(float var0, float var1) {
      GL11.glTexCoord2f(var0, var1);
   }

   public static void vertex3f(float var0, float var1, float var2) {
      GL11.glVertex3f(var0, var1, var2);
   }

   public static void clearCurrentColor() {
      COLOR.r = -1.0F;
      COLOR.g = -1.0F;
      COLOR.b = -1.0F;
      COLOR.a = -1.0F;
   }

   public static void normalPointer(int var0, int var1, int var2) {
      GL11.glNormalPointer(var0, var1, (long)var2);
   }

   public static void normalPointer(int var0, int var1, ByteBuffer byteBuffer) {
      GL11.glNormalPointer(var0, var1, byteBuffer);
   }

   public static void texCoordPointer(int var0, int var1, int var2, int var3) {
      GL11.glTexCoordPointer(var0, var1, var2, (long)var3);
   }

   public static void texCoordPointer(int var0, int var1, int var2, ByteBuffer byteBuffer) {
      GL11.glTexCoordPointer(var0, var1, var2, byteBuffer);
   }

   public static void vertexPointer(int var0, int var1, int var2, int var3) {
      GL11.glVertexPointer(var0, var1, var2, (long)var3);
   }

   public static void vertexPointer(int var0, int var1, int var2, ByteBuffer byteBuffer) {
      GL11.glVertexPointer(var0, var1, var2, byteBuffer);
   }

   public static void colorPointer(int var0, int var1, int var2, int var3) {
      GL11.glColorPointer(var0, var1, var2, (long)var3);
   }

   public static void colorPointer(int var0, int var1, int var2, ByteBuffer byteBuffer) {
      GL11.glColorPointer(var0, var1, var2, byteBuffer);
   }

   public static void disableClientState(int i) {
      GL11.glDisableClientState(i);
   }

   public static void enableClientState(int i) {
      GL11.glEnableClientState(i);
   }

   public static void begin(int i) {
      GL11.glBegin(i);
   }

   public static void end() {
      GL11.glEnd();
   }

   public static void drawArrays(int var0, int var1, int var2) {
      GL11.glDrawArrays(var0, var1, var2);
   }

   public static void lineWidth(float f) {
      GL11.glLineWidth(f);
   }

   public static void callList(int i) {
      GL11.glCallList(i);
   }

   public static void deleteLists(int var0, int var1) {
      GL11.glDeleteLists(var0, var1);
   }

   public static void newList(int var0, int var1) {
      GL11.glNewList(var0, var1);
   }

   public static void endList() {
      GL11.glEndList();
   }

   public static int genLists(int i) {
      return GL11.glGenLists(i);
   }

   public static void pixelStore(int var0, int var1) {
      GL11.glPixelStorei(var0, var1);
   }

   public static void pixelTransfer(int var0, float var1) {
      GL11.glPixelTransferf(var0, var1);
   }

   public static void readPixels(int var0, int var1, int var2, int var3, int var4, int var5, ByteBuffer byteBuffer) {
      GL11.glReadPixels(var0, var1, var2, var3, var4, var5, byteBuffer);
   }

   public static void readPixels(int var0, int var1, int var2, int var3, int var4, int var5, long var6) {
      GL11.glReadPixels(var0, var1, var2, var3, var4, var5, var6);
   }

   public static int getError() {
      return GL11.glGetError();
   }

   public static String getString(int i) {
      return GL11.glGetString(i);
   }

   public static void getInteger(int var0, IntBuffer intBuffer) {
      GL11.glGetIntegerv(var0, intBuffer);
   }

   public static int getInteger(int i) {
      return GL11.glGetInteger(i);
   }

   public static void setProfile(GlStateManager.Profile profile) {
      profile.apply();
   }

   public static void unsetProfile(GlStateManager.Profile glStateManager$Profile) {
      glStateManager$Profile.clean();
   }

   @ClientJarOnly
   static class AlphaState {
      public final GlStateManager.BooleanState mode;
      public int func;
      public float reference;

      private AlphaState() {
         this.mode = new GlStateManager.BooleanState(3008);
         this.func = 519;
         this.reference = -1.0F;
      }
   }

   @ClientJarOnly
   static class BlendState {
      public final GlStateManager.BooleanState mode;
      public int srcRgb;
      public int dstRgb;
      public int srcAlpha;
      public int dstAlpha;

      private BlendState() {
         this.mode = new GlStateManager.BooleanState(3042);
         this.srcRgb = 1;
         this.dstRgb = 0;
         this.srcAlpha = 1;
         this.dstAlpha = 0;
      }
   }

   @ClientJarOnly
   static class BooleanState {
      private final int state;
      private boolean enabled;

      public BooleanState(int state) {
         this.state = state;
      }

      public void disable() {
         this.setEnabled(false);
      }

      public void enable() {
         this.setEnabled(true);
      }

      public void setEnabled(boolean enabled) {
         if(enabled != this.enabled) {
            this.enabled = enabled;
            if(enabled) {
               GL11.glEnable(this.state);
            } else {
               GL11.glDisable(this.state);
            }
         }

      }
   }

   @ClientJarOnly
   static class ClearState {
      public double depth;
      public final GlStateManager.Color color;
      public int stencil;

      private ClearState() {
         this.depth = 1.0D;
         this.color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
      }
   }

   @ClientJarOnly
   static class Color {
      public float r;
      public float g;
      public float b;
      public float a;

      public Color() {
         this(1.0F, 1.0F, 1.0F, 1.0F);
      }

      public Color(float r, float g, float b, float a) {
         this.r = 1.0F;
         this.g = 1.0F;
         this.b = 1.0F;
         this.a = 1.0F;
         this.r = r;
         this.g = g;
         this.b = b;
         this.a = a;
      }
   }

   @ClientJarOnly
   static class ColorLogicState {
      public final GlStateManager.BooleanState enable;
      public int op;

      private ColorLogicState() {
         this.enable = new GlStateManager.BooleanState(3058);
         this.op = 5379;
      }
   }

   @ClientJarOnly
   static class ColorMask {
      public boolean red;
      public boolean green;
      public boolean blue;
      public boolean alpha;

      private ColorMask() {
         this.red = true;
         this.green = true;
         this.blue = true;
         this.alpha = true;
      }
   }

   @ClientJarOnly
   static class ColorMaterialState {
      public final GlStateManager.BooleanState enable;
      public int face;
      public int mode;

      private ColorMaterialState() {
         this.enable = new GlStateManager.BooleanState(2903);
         this.face = 1032;
         this.mode = 5634;
      }
   }

   @ClientJarOnly
   public static enum CullFace {
      FRONT(1028),
      BACK(1029),
      FRONT_AND_BACK(1032);

      public final int value;

      private CullFace(int value) {
         this.value = value;
      }
   }

   @ClientJarOnly
   static class CullState {
      public final GlStateManager.BooleanState enable;
      public int mode;

      private CullState() {
         this.enable = new GlStateManager.BooleanState(2884);
         this.mode = 1029;
      }
   }

   @ClientJarOnly
   static class DepthState {
      public final GlStateManager.BooleanState mode;
      public boolean mask;
      public int func;

      private DepthState() {
         this.mode = new GlStateManager.BooleanState(2929);
         this.mask = true;
         this.func = 513;
      }
   }

   @ClientJarOnly
   public static enum DestFactor {
      CONSTANT_ALPHA('考'),
      CONSTANT_COLOR('老'),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA('耄'),
      ONE_MINUS_CONSTANT_COLOR('耂'),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_COLOR(768),
      ZERO(0);

      public final int value;

      private DestFactor(int value) {
         this.value = value;
      }
   }

   @ClientJarOnly
   public static enum FogMode {
      LINEAR(9729),
      EXP(2048),
      EXP2(2049);

      public final int value;

      private FogMode(int value) {
         this.value = value;
      }
   }

   @ClientJarOnly
   static class FogState {
      public final GlStateManager.BooleanState enable;
      public int mode;
      public float density;
      public float start;
      public float end;

      private FogState() {
         this.enable = new GlStateManager.BooleanState(2912);
         this.mode = 2048;
         this.density = 1.0F;
         this.end = 1.0F;
      }
   }

   @ClientJarOnly
   public static enum LogicOp {
      AND(5377),
      AND_INVERTED(5380),
      AND_REVERSE(5378),
      CLEAR(5376),
      COPY(5379),
      COPY_INVERTED(5388),
      EQUIV(5385),
      INVERT(5386),
      NAND(5390),
      NOOP(5381),
      NOR(5384),
      OR(5383),
      OR_INVERTED(5389),
      OR_REVERSE(5387),
      SET(5391),
      XOR(5382);

      public final int value;

      private LogicOp(int value) {
         this.value = value;
      }
   }

   @ClientJarOnly
   static class PolygonOffsetState {
      public final GlStateManager.BooleanState fill;
      public final GlStateManager.BooleanState line;
      public float factor;
      public float units;

      private PolygonOffsetState() {
         this.fill = new GlStateManager.BooleanState('耷');
         this.line = new GlStateManager.BooleanState(10754);
      }
   }

   @ClientJarOnly
   public static enum Profile {
      DEFAULT {
         public void apply() {
            GlStateManager.disableAlphaTest();
            GlStateManager.alphaFunc(519, 0.0F);
            GlStateManager.disableLighting();
            GL11.glLightModelfv(2899, Lighting.getBuffer(0.2F, 0.2F, 0.2F, 1.0F));

            for(int var1 = 0; var1 < 8; ++var1) {
               GlStateManager.disableLight(var1);
               GL11.glLightfv(16384 + var1, 4608, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
               GL11.glLightfv(16384 + var1, 4611, Lighting.getBuffer(0.0F, 0.0F, 1.0F, 0.0F));
               if(var1 == 0) {
                  GL11.glLightfv(16384 + var1, 4609, Lighting.getBuffer(1.0F, 1.0F, 1.0F, 1.0F));
                  GL11.glLightfv(16384 + var1, 4610, Lighting.getBuffer(1.0F, 1.0F, 1.0F, 1.0F));
               } else {
                  GL11.glLightfv(16384 + var1, 4609, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
                  GL11.glLightfv(16384 + var1, 4610, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 1.0F));
               }
            }

            GlStateManager.disableColorMaterial();
            GlStateManager.colorMaterial(1032, 5634);
            GlStateManager.disableDepthTest();
            GlStateManager.depthFunc(513);
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GL14.glBlendEquation('耆');
            GlStateManager.disableFog();
            GL11.glFogi(2917, 2048);
            GlStateManager.fogDensity(1.0F);
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(1.0F);
            GL11.glFogfv(2918, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            if(GL.getCapabilities().GL_NV_fog_distance) {
               GL11.glFogi(2917, '蕜');
            }

            GlStateManager.polygonOffset(0.0F, 0.0F);
            GlStateManager.disableColorLogicOp();
            GlStateManager.logicOp(5379);
            GlStateManager.disableTexGen(GlStateManager.TexGen.S);
            GlStateManager.texGenMode(GlStateManager.TexGen.S, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9474, Lighting.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9217, Lighting.getBuffer(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.T);
            GlStateManager.texGenMode(GlStateManager.TexGen.T, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9474, Lighting.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9217, Lighting.getBuffer(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.R);
            GlStateManager.texGenMode(GlStateManager.TexGen.R, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9474, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9217, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.disableTexGen(GlStateManager.TexGen.Q);
            GlStateManager.texGenMode(GlStateManager.TexGen.Q, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9474, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9217, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.activeTexture(0);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9986);
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
            GL11.glTexParameteri(3553, '脽', 1000);
            GL11.glTexParameteri(3553, '脻', 1000);
            GL11.glTexParameteri(3553, '脺', -1000);
            GL11.glTexParameterf(3553, '蔁', 0.0F);
            GL11.glTexEnvi(8960, 8704, 8448);
            GL11.glTexEnvfv(8960, 8705, Lighting.getBuffer(0.0F, 0.0F, 0.0F, 0.0F));
            GL11.glTexEnvi(8960, '蕱', 8448);
            GL11.glTexEnvi(8960, '蕲', 8448);
            GL11.glTexEnvi(8960, '薀', 5890);
            GL11.glTexEnvi(8960, '薁', '蕸');
            GL11.glTexEnvi(8960, '薂', '蕶');
            GL11.glTexEnvi(8960, '薈', 5890);
            GL11.glTexEnvi(8960, '薉', '蕸');
            GL11.glTexEnvi(8960, '薊', '蕶');
            GL11.glTexEnvi(8960, '薐', 768);
            GL11.glTexEnvi(8960, '薑', 768);
            GL11.glTexEnvi(8960, '薒', 770);
            GL11.glTexEnvi(8960, '薘', 770);
            GL11.glTexEnvi(8960, '薙', 770);
            GL11.glTexEnvi(8960, '薚', 770);
            GL11.glTexEnvf(8960, '蕳', 1.0F);
            GL11.glTexEnvf(8960, 3356, 1.0F);
            GlStateManager.disableNormalize();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableRescaleNormal();
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.clearDepth(1.0D);
            GL11.glLineWidth(1.0F);
            GL11.glNormal3f(0.0F, 0.0F, 1.0F);
            GL11.glPolygonMode(1028, 6914);
            GL11.glPolygonMode(1029, 6914);
         }

         public void clean() {
         }
      },
      PLAYER_SKIN {
         public void apply() {
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         }

         public void clean() {
            GlStateManager.disableBlend();
         }
      },
      TRANSPARENT_MODEL {
         public void apply() {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         public void clean() {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.depthMask(true);
         }
      };

      private Profile() {
      }

      public abstract void apply();

      public abstract void clean();
   }

   @ClientJarOnly
   public static enum SourceFactor {
      CONSTANT_ALPHA('考'),
      CONSTANT_COLOR('老'),
      DST_ALPHA(772),
      DST_COLOR(774),
      ONE(1),
      ONE_MINUS_CONSTANT_ALPHA('耄'),
      ONE_MINUS_CONSTANT_COLOR('耂'),
      ONE_MINUS_DST_ALPHA(773),
      ONE_MINUS_DST_COLOR(775),
      ONE_MINUS_SRC_ALPHA(771),
      ONE_MINUS_SRC_COLOR(769),
      SRC_ALPHA(770),
      SRC_ALPHA_SATURATE(776),
      SRC_COLOR(768),
      ZERO(0);

      public final int value;

      private SourceFactor(int value) {
         this.value = value;
      }
   }

   @ClientJarOnly
   static class StencilFunc {
      public int func;
      public int ref;
      public int mask;

      private StencilFunc() {
         this.func = 519;
         this.mask = -1;
      }
   }

   @ClientJarOnly
   static class StencilState {
      public final GlStateManager.StencilFunc func;
      public int mask;
      public int fail;
      public int zfail;
      public int zpass;

      private StencilState() {
         this.func = new GlStateManager.StencilFunc();
         this.mask = -1;
         this.fail = 7680;
         this.zfail = 7680;
         this.zpass = 7680;
      }
   }

   @ClientJarOnly
   public static enum TexGen {
      S,
      T,
      R,
      Q;
   }

   @ClientJarOnly
   static class TexGenCoord {
      public final GlStateManager.BooleanState enable;
      public final int coord;
      public int mode = -1;

      public TexGenCoord(int coord, int var2) {
         this.coord = coord;
         this.enable = new GlStateManager.BooleanState(var2);
      }
   }

   @ClientJarOnly
   static class TexGenState {
      public final GlStateManager.TexGenCoord s;
      public final GlStateManager.TexGenCoord t;
      public final GlStateManager.TexGenCoord r;
      public final GlStateManager.TexGenCoord q;

      private TexGenState() {
         this.s = new GlStateManager.TexGenCoord(8192, 3168);
         this.t = new GlStateManager.TexGenCoord(8193, 3169);
         this.r = new GlStateManager.TexGenCoord(8194, 3170);
         this.q = new GlStateManager.TexGenCoord(8195, 3171);
      }
   }

   @ClientJarOnly
   static class TextureState {
      public final GlStateManager.BooleanState enable;
      public int binding;

      private TextureState() {
         this.enable = new GlStateManager.BooleanState(3553);
      }
   }

   @ClientJarOnly
   public static enum Viewport {
      INSTANCE;

      protected int x;
      protected int y;
      protected int width;
      protected int height;
   }
}
