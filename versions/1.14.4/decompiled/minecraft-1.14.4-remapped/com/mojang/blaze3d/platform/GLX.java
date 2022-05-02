package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.SnooperAccess;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexBufferObject;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;
import oshi.SystemInfo;
import oshi.hardware.Processor;

@ClientJarOnly
public class GLX {
   private static final Logger LOGGER = LogManager.getLogger();
   public static boolean isNvidia;
   public static boolean isAmd;
   public static int GL_FRAMEBUFFER;
   public static int GL_RENDERBUFFER;
   public static int GL_COLOR_ATTACHMENT0;
   public static int GL_DEPTH_ATTACHMENT;
   public static int GL_FRAMEBUFFER_COMPLETE;
   public static int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
   public static int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
   public static int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
   public static int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
   private static GLX.FboMode fboMode;
   public static final boolean useFbo = true;
   private static boolean hasShaders;
   private static boolean useShaderArb;
   public static int GL_LINK_STATUS;
   public static int GL_COMPILE_STATUS;
   public static int GL_VERTEX_SHADER;
   public static int GL_FRAGMENT_SHADER;
   private static boolean useMultitextureArb;
   public static int GL_TEXTURE0;
   public static int GL_TEXTURE1;
   public static int GL_TEXTURE2;
   private static boolean useTexEnvCombineArb;
   public static int GL_COMBINE;
   public static int GL_INTERPOLATE;
   public static int GL_PRIMARY_COLOR;
   public static int GL_CONSTANT;
   public static int GL_PREVIOUS;
   public static int GL_COMBINE_RGB;
   public static int GL_SOURCE0_RGB;
   public static int GL_SOURCE1_RGB;
   public static int GL_SOURCE2_RGB;
   public static int GL_OPERAND0_RGB;
   public static int GL_OPERAND1_RGB;
   public static int GL_OPERAND2_RGB;
   public static int GL_COMBINE_ALPHA;
   public static int GL_SOURCE0_ALPHA;
   public static int GL_SOURCE1_ALPHA;
   public static int GL_SOURCE2_ALPHA;
   public static int GL_OPERAND0_ALPHA;
   public static int GL_OPERAND1_ALPHA;
   public static int GL_OPERAND2_ALPHA;
   private static boolean separateBlend;
   public static boolean useSeparateBlendExt;
   public static boolean isOpenGl21;
   public static boolean usePostProcess;
   private static String capsString = "";
   private static String cpuInfo;
   public static final boolean useVbo = true;
   public static boolean needVbo;
   private static boolean useVboArb;
   public static int GL_ARRAY_BUFFER;
   public static int GL_STATIC_DRAW;
   private static final Map LOOKUP_MAP = (Map)make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put(Integer.valueOf(0), "No error");
      hashMap.put(Integer.valueOf(1280), "Enum parameter is invalid for this function");
      hashMap.put(Integer.valueOf(1281), "Parameter is invalid for this function");
      hashMap.put(Integer.valueOf(1282), "Current state is invalid for this function");
      hashMap.put(Integer.valueOf(1283), "Stack overflow");
      hashMap.put(Integer.valueOf(1284), "Stack underflow");
      hashMap.put(Integer.valueOf(1285), "Out of memory");
      hashMap.put(Integer.valueOf(1286), "Operation on incomplete framebuffer");
      hashMap.put(Integer.valueOf(1286), "Operation on incomplete framebuffer");
   });

   public static void populateSnooperWithOpenGL(SnooperAccess snooperAccess) {
      snooperAccess.setFixedData("opengl_version", GlStateManager.getString(7938));
      snooperAccess.setFixedData("opengl_vendor", GlStateManager.getString(7936));
      GLCapabilities var1 = GL.getCapabilities();
      snooperAccess.setFixedData("gl_caps[ARB_arrays_of_arrays]", Boolean.valueOf(var1.GL_ARB_arrays_of_arrays));
      snooperAccess.setFixedData("gl_caps[ARB_base_instance]", Boolean.valueOf(var1.GL_ARB_base_instance));
      snooperAccess.setFixedData("gl_caps[ARB_blend_func_extended]", Boolean.valueOf(var1.GL_ARB_blend_func_extended));
      snooperAccess.setFixedData("gl_caps[ARB_clear_buffer_object]", Boolean.valueOf(var1.GL_ARB_clear_buffer_object));
      snooperAccess.setFixedData("gl_caps[ARB_color_buffer_float]", Boolean.valueOf(var1.GL_ARB_color_buffer_float));
      snooperAccess.setFixedData("gl_caps[ARB_compatibility]", Boolean.valueOf(var1.GL_ARB_compatibility));
      snooperAccess.setFixedData("gl_caps[ARB_compressed_texture_pixel_storage]", Boolean.valueOf(var1.GL_ARB_compressed_texture_pixel_storage));
      snooperAccess.setFixedData("gl_caps[ARB_compute_shader]", Boolean.valueOf(var1.GL_ARB_compute_shader));
      snooperAccess.setFixedData("gl_caps[ARB_copy_buffer]", Boolean.valueOf(var1.GL_ARB_copy_buffer));
      snooperAccess.setFixedData("gl_caps[ARB_copy_image]", Boolean.valueOf(var1.GL_ARB_copy_image));
      snooperAccess.setFixedData("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(var1.GL_ARB_depth_buffer_float));
      snooperAccess.setFixedData("gl_caps[ARB_compute_shader]", Boolean.valueOf(var1.GL_ARB_compute_shader));
      snooperAccess.setFixedData("gl_caps[ARB_copy_buffer]", Boolean.valueOf(var1.GL_ARB_copy_buffer));
      snooperAccess.setFixedData("gl_caps[ARB_copy_image]", Boolean.valueOf(var1.GL_ARB_copy_image));
      snooperAccess.setFixedData("gl_caps[ARB_depth_buffer_float]", Boolean.valueOf(var1.GL_ARB_depth_buffer_float));
      snooperAccess.setFixedData("gl_caps[ARB_depth_clamp]", Boolean.valueOf(var1.GL_ARB_depth_clamp));
      snooperAccess.setFixedData("gl_caps[ARB_depth_texture]", Boolean.valueOf(var1.GL_ARB_depth_texture));
      snooperAccess.setFixedData("gl_caps[ARB_draw_buffers]", Boolean.valueOf(var1.GL_ARB_draw_buffers));
      snooperAccess.setFixedData("gl_caps[ARB_draw_buffers_blend]", Boolean.valueOf(var1.GL_ARB_draw_buffers_blend));
      snooperAccess.setFixedData("gl_caps[ARB_draw_elements_base_vertex]", Boolean.valueOf(var1.GL_ARB_draw_elements_base_vertex));
      snooperAccess.setFixedData("gl_caps[ARB_draw_indirect]", Boolean.valueOf(var1.GL_ARB_draw_indirect));
      snooperAccess.setFixedData("gl_caps[ARB_draw_instanced]", Boolean.valueOf(var1.GL_ARB_draw_instanced));
      snooperAccess.setFixedData("gl_caps[ARB_explicit_attrib_location]", Boolean.valueOf(var1.GL_ARB_explicit_attrib_location));
      snooperAccess.setFixedData("gl_caps[ARB_explicit_uniform_location]", Boolean.valueOf(var1.GL_ARB_explicit_uniform_location));
      snooperAccess.setFixedData("gl_caps[ARB_fragment_layer_viewport]", Boolean.valueOf(var1.GL_ARB_fragment_layer_viewport));
      snooperAccess.setFixedData("gl_caps[ARB_fragment_program]", Boolean.valueOf(var1.GL_ARB_fragment_program));
      snooperAccess.setFixedData("gl_caps[ARB_fragment_shader]", Boolean.valueOf(var1.GL_ARB_fragment_shader));
      snooperAccess.setFixedData("gl_caps[ARB_fragment_program_shadow]", Boolean.valueOf(var1.GL_ARB_fragment_program_shadow));
      snooperAccess.setFixedData("gl_caps[ARB_framebuffer_object]", Boolean.valueOf(var1.GL_ARB_framebuffer_object));
      snooperAccess.setFixedData("gl_caps[ARB_framebuffer_sRGB]", Boolean.valueOf(var1.GL_ARB_framebuffer_sRGB));
      snooperAccess.setFixedData("gl_caps[ARB_geometry_shader4]", Boolean.valueOf(var1.GL_ARB_geometry_shader4));
      snooperAccess.setFixedData("gl_caps[ARB_gpu_shader5]", Boolean.valueOf(var1.GL_ARB_gpu_shader5));
      snooperAccess.setFixedData("gl_caps[ARB_half_float_pixel]", Boolean.valueOf(var1.GL_ARB_half_float_pixel));
      snooperAccess.setFixedData("gl_caps[ARB_half_float_vertex]", Boolean.valueOf(var1.GL_ARB_half_float_vertex));
      snooperAccess.setFixedData("gl_caps[ARB_instanced_arrays]", Boolean.valueOf(var1.GL_ARB_instanced_arrays));
      snooperAccess.setFixedData("gl_caps[ARB_map_buffer_alignment]", Boolean.valueOf(var1.GL_ARB_map_buffer_alignment));
      snooperAccess.setFixedData("gl_caps[ARB_map_buffer_range]", Boolean.valueOf(var1.GL_ARB_map_buffer_range));
      snooperAccess.setFixedData("gl_caps[ARB_multisample]", Boolean.valueOf(var1.GL_ARB_multisample));
      snooperAccess.setFixedData("gl_caps[ARB_multitexture]", Boolean.valueOf(var1.GL_ARB_multitexture));
      snooperAccess.setFixedData("gl_caps[ARB_occlusion_query2]", Boolean.valueOf(var1.GL_ARB_occlusion_query2));
      snooperAccess.setFixedData("gl_caps[ARB_pixel_buffer_object]", Boolean.valueOf(var1.GL_ARB_pixel_buffer_object));
      snooperAccess.setFixedData("gl_caps[ARB_seamless_cube_map]", Boolean.valueOf(var1.GL_ARB_seamless_cube_map));
      snooperAccess.setFixedData("gl_caps[ARB_shader_objects]", Boolean.valueOf(var1.GL_ARB_shader_objects));
      snooperAccess.setFixedData("gl_caps[ARB_shader_stencil_export]", Boolean.valueOf(var1.GL_ARB_shader_stencil_export));
      snooperAccess.setFixedData("gl_caps[ARB_shader_texture_lod]", Boolean.valueOf(var1.GL_ARB_shader_texture_lod));
      snooperAccess.setFixedData("gl_caps[ARB_shadow]", Boolean.valueOf(var1.GL_ARB_shadow));
      snooperAccess.setFixedData("gl_caps[ARB_shadow_ambient]", Boolean.valueOf(var1.GL_ARB_shadow_ambient));
      snooperAccess.setFixedData("gl_caps[ARB_stencil_texturing]", Boolean.valueOf(var1.GL_ARB_stencil_texturing));
      snooperAccess.setFixedData("gl_caps[ARB_sync]", Boolean.valueOf(var1.GL_ARB_sync));
      snooperAccess.setFixedData("gl_caps[ARB_tessellation_shader]", Boolean.valueOf(var1.GL_ARB_tessellation_shader));
      snooperAccess.setFixedData("gl_caps[ARB_texture_border_clamp]", Boolean.valueOf(var1.GL_ARB_texture_border_clamp));
      snooperAccess.setFixedData("gl_caps[ARB_texture_buffer_object]", Boolean.valueOf(var1.GL_ARB_texture_buffer_object));
      snooperAccess.setFixedData("gl_caps[ARB_texture_cube_map]", Boolean.valueOf(var1.GL_ARB_texture_cube_map));
      snooperAccess.setFixedData("gl_caps[ARB_texture_cube_map_array]", Boolean.valueOf(var1.GL_ARB_texture_cube_map_array));
      snooperAccess.setFixedData("gl_caps[ARB_texture_non_power_of_two]", Boolean.valueOf(var1.GL_ARB_texture_non_power_of_two));
      snooperAccess.setFixedData("gl_caps[ARB_uniform_buffer_object]", Boolean.valueOf(var1.GL_ARB_uniform_buffer_object));
      snooperAccess.setFixedData("gl_caps[ARB_vertex_blend]", Boolean.valueOf(var1.GL_ARB_vertex_blend));
      snooperAccess.setFixedData("gl_caps[ARB_vertex_buffer_object]", Boolean.valueOf(var1.GL_ARB_vertex_buffer_object));
      snooperAccess.setFixedData("gl_caps[ARB_vertex_program]", Boolean.valueOf(var1.GL_ARB_vertex_program));
      snooperAccess.setFixedData("gl_caps[ARB_vertex_shader]", Boolean.valueOf(var1.GL_ARB_vertex_shader));
      snooperAccess.setFixedData("gl_caps[EXT_bindable_uniform]", Boolean.valueOf(var1.GL_EXT_bindable_uniform));
      snooperAccess.setFixedData("gl_caps[EXT_blend_equation_separate]", Boolean.valueOf(var1.GL_EXT_blend_equation_separate));
      snooperAccess.setFixedData("gl_caps[EXT_blend_func_separate]", Boolean.valueOf(var1.GL_EXT_blend_func_separate));
      snooperAccess.setFixedData("gl_caps[EXT_blend_minmax]", Boolean.valueOf(var1.GL_EXT_blend_minmax));
      snooperAccess.setFixedData("gl_caps[EXT_blend_subtract]", Boolean.valueOf(var1.GL_EXT_blend_subtract));
      snooperAccess.setFixedData("gl_caps[EXT_draw_instanced]", Boolean.valueOf(var1.GL_EXT_draw_instanced));
      snooperAccess.setFixedData("gl_caps[EXT_framebuffer_multisample]", Boolean.valueOf(var1.GL_EXT_framebuffer_multisample));
      snooperAccess.setFixedData("gl_caps[EXT_framebuffer_object]", Boolean.valueOf(var1.GL_EXT_framebuffer_object));
      snooperAccess.setFixedData("gl_caps[EXT_framebuffer_sRGB]", Boolean.valueOf(var1.GL_EXT_framebuffer_sRGB));
      snooperAccess.setFixedData("gl_caps[EXT_geometry_shader4]", Boolean.valueOf(var1.GL_EXT_geometry_shader4));
      snooperAccess.setFixedData("gl_caps[EXT_gpu_program_parameters]", Boolean.valueOf(var1.GL_EXT_gpu_program_parameters));
      snooperAccess.setFixedData("gl_caps[EXT_gpu_shader4]", Boolean.valueOf(var1.GL_EXT_gpu_shader4));
      snooperAccess.setFixedData("gl_caps[EXT_packed_depth_stencil]", Boolean.valueOf(var1.GL_EXT_packed_depth_stencil));
      snooperAccess.setFixedData("gl_caps[EXT_separate_shader_objects]", Boolean.valueOf(var1.GL_EXT_separate_shader_objects));
      snooperAccess.setFixedData("gl_caps[EXT_shader_image_load_store]", Boolean.valueOf(var1.GL_EXT_shader_image_load_store));
      snooperAccess.setFixedData("gl_caps[EXT_shadow_funcs]", Boolean.valueOf(var1.GL_EXT_shadow_funcs));
      snooperAccess.setFixedData("gl_caps[EXT_shared_texture_palette]", Boolean.valueOf(var1.GL_EXT_shared_texture_palette));
      snooperAccess.setFixedData("gl_caps[EXT_stencil_clear_tag]", Boolean.valueOf(var1.GL_EXT_stencil_clear_tag));
      snooperAccess.setFixedData("gl_caps[EXT_stencil_two_side]", Boolean.valueOf(var1.GL_EXT_stencil_two_side));
      snooperAccess.setFixedData("gl_caps[EXT_stencil_wrap]", Boolean.valueOf(var1.GL_EXT_stencil_wrap));
      snooperAccess.setFixedData("gl_caps[EXT_texture_array]", Boolean.valueOf(var1.GL_EXT_texture_array));
      snooperAccess.setFixedData("gl_caps[EXT_texture_buffer_object]", Boolean.valueOf(var1.GL_EXT_texture_buffer_object));
      snooperAccess.setFixedData("gl_caps[EXT_texture_integer]", Boolean.valueOf(var1.GL_EXT_texture_integer));
      snooperAccess.setFixedData("gl_caps[EXT_texture_sRGB]", Boolean.valueOf(var1.GL_EXT_texture_sRGB));
      snooperAccess.setFixedData("gl_caps[ARB_vertex_shader]", Boolean.valueOf(var1.GL_ARB_vertex_shader));
      snooperAccess.setFixedData("gl_caps[gl_max_vertex_uniforms]", Integer.valueOf(GlStateManager.getInteger('譊')));
      GlStateManager.getError();
      snooperAccess.setFixedData("gl_caps[gl_max_fragment_uniforms]", Integer.valueOf(GlStateManager.getInteger('證')));
      GlStateManager.getError();
      snooperAccess.setFixedData("gl_caps[gl_max_vertex_attribs]", Integer.valueOf(GlStateManager.getInteger('衩')));
      GlStateManager.getError();
      snooperAccess.setFixedData("gl_caps[gl_max_vertex_texture_image_units]", Integer.valueOf(GlStateManager.getInteger('譌')));
      GlStateManager.getError();
      snooperAccess.setFixedData("gl_caps[gl_max_texture_image_units]", Integer.valueOf(GlStateManager.getInteger('衲')));
      GlStateManager.getError();
      snooperAccess.setFixedData("gl_caps[gl_max_array_texture_layers]", Integer.valueOf(GlStateManager.getInteger('裿')));
      GlStateManager.getError();
   }

   public static String getOpenGLVersionString() {
      return GLFW.glfwGetCurrentContext() == 0L?"NO CONTEXT":GlStateManager.getString(7937) + " GL version " + GlStateManager.getString(7938) + ", " + GlStateManager.getString(7936);
   }

   public static int getRefreshRate(Window window) {
      long var1 = GLFW.glfwGetWindowMonitor(window.getWindow());
      if(var1 == 0L) {
         var1 = GLFW.glfwGetPrimaryMonitor();
      }

      GLFWVidMode var3 = var1 == 0L?null:GLFW.glfwGetVideoMode(var1);
      return var3 == null?0:var3.refreshRate();
   }

   public static String getLWJGLVersion() {
      return Version.getVersion();
   }

   public static LongSupplier initGlfw() {
      Window.checkGlfwError((integer, string) -> {
         throw new IllegalStateException(String.format("GLFW error before init: [0x%X]%s", new Object[]{integer, string}));
      });
      List<String> var0 = Lists.newArrayList();
      GLFWErrorCallback var1 = GLFW.glfwSetErrorCallback((var1, var2) -> {
         var0.add(String.format("GLFW error during init: [0x%X]%s", new Object[]{Integer.valueOf(var1), Long.valueOf(var2)}));
      });
      if(!GLFW.glfwInit()) {
         throw new IllegalStateException("Failed to initialize GLFW, errors: " + Joiner.on(",").join(var0));
      } else {
         LongSupplier var2 = () -> {
            return (long)(GLFW.glfwGetTime() * 1.0E9D);
         };

         for(String var4 : var0) {
            LOGGER.error("GLFW error collected during initialization: {}", var4);
         }

         setGlfwErrorCallback(var1);
         return var2;
      }
   }

   public static void setGlfwErrorCallback(GLFWErrorCallbackI glfwErrorCallback) {
      GLFW.glfwSetErrorCallback(glfwErrorCallback).free();
   }

   public static boolean shouldClose(Window window) {
      return GLFW.glfwWindowShouldClose(window.getWindow());
   }

   public static void pollEvents() {
      GLFW.glfwPollEvents();
   }

   public static String getOpenGLVersion() {
      return GlStateManager.getString(7938);
   }

   public static String getRenderer() {
      return GlStateManager.getString(7937);
   }

   public static String getVendor() {
      return GlStateManager.getString(7936);
   }

   public static void setupNvFogDistance() {
      if(GL.getCapabilities().GL_NV_fog_distance) {
         GlStateManager.fogi('蕚', '蕛');
      }

   }

   public static boolean supportsOpenGL2() {
      return GL.getCapabilities().OpenGL20;
   }

   public static void withTextureRestore(Runnable runnable) {
      GL11.glPushAttrib(270336);

      try {
         runnable.run();
      } finally {
         GL11.glPopAttrib();
      }

   }

   public static ByteBuffer allocateMemory(int i) {
      return MemoryUtil.memAlloc(i);
   }

   public static void freeMemory(Buffer buffer) {
      MemoryUtil.memFree(buffer);
   }

   public static void init() {
      GLCapabilities var0 = GL.getCapabilities();
      useMultitextureArb = var0.GL_ARB_multitexture && !var0.OpenGL13;
      useTexEnvCombineArb = var0.GL_ARB_texture_env_combine && !var0.OpenGL13;
      if(useMultitextureArb) {
         capsString = capsString + "Using ARB_multitexture.\n";
         GL_TEXTURE0 = '蓀';
         GL_TEXTURE1 = '蓁';
         GL_TEXTURE2 = '蓂';
      } else {
         capsString = capsString + "Using GL 1.3 multitexturing.\n";
         GL_TEXTURE0 = '蓀';
         GL_TEXTURE1 = '蓁';
         GL_TEXTURE2 = '蓂';
      }

      if(useTexEnvCombineArb) {
         capsString = capsString + "Using ARB_texture_env_combine.\n";
         GL_COMBINE = '蕰';
         GL_INTERPOLATE = '蕵';
         GL_PRIMARY_COLOR = '蕷';
         GL_CONSTANT = '蕶';
         GL_PREVIOUS = '蕸';
         GL_COMBINE_RGB = '蕱';
         GL_SOURCE0_RGB = '薀';
         GL_SOURCE1_RGB = '薁';
         GL_SOURCE2_RGB = '薂';
         GL_OPERAND0_RGB = '薐';
         GL_OPERAND1_RGB = '薑';
         GL_OPERAND2_RGB = '薒';
         GL_COMBINE_ALPHA = '蕲';
         GL_SOURCE0_ALPHA = '薈';
         GL_SOURCE1_ALPHA = '薉';
         GL_SOURCE2_ALPHA = '薊';
         GL_OPERAND0_ALPHA = '薘';
         GL_OPERAND1_ALPHA = '薙';
         GL_OPERAND2_ALPHA = '薚';
      } else {
         capsString = capsString + "Using GL 1.3 texture combiners.\n";
         GL_COMBINE = '蕰';
         GL_INTERPOLATE = '蕵';
         GL_PRIMARY_COLOR = '蕷';
         GL_CONSTANT = '蕶';
         GL_PREVIOUS = '蕸';
         GL_COMBINE_RGB = '蕱';
         GL_SOURCE0_RGB = '薀';
         GL_SOURCE1_RGB = '薁';
         GL_SOURCE2_RGB = '薂';
         GL_OPERAND0_RGB = '薐';
         GL_OPERAND1_RGB = '薑';
         GL_OPERAND2_RGB = '薒';
         GL_COMBINE_ALPHA = '蕲';
         GL_SOURCE0_ALPHA = '薈';
         GL_SOURCE1_ALPHA = '薉';
         GL_SOURCE2_ALPHA = '薊';
         GL_OPERAND0_ALPHA = '薘';
         GL_OPERAND1_ALPHA = '薙';
         GL_OPERAND2_ALPHA = '薚';
      }

      useSeparateBlendExt = var0.GL_EXT_blend_func_separate && !var0.OpenGL14;
      separateBlend = var0.OpenGL14 || var0.GL_EXT_blend_func_separate;
      capsString = capsString + "Using framebuffer objects because ";
      if(var0.OpenGL30) {
         capsString = capsString + "OpenGL 3.0 is supported and separate blending is supported.\n";
         fboMode = GLX.FboMode.BASE;
         GL_FRAMEBUFFER = '赀';
         GL_RENDERBUFFER = '赁';
         GL_COLOR_ATTACHMENT0 = '賠';
         GL_DEPTH_ATTACHMENT = '贀';
         GL_FRAMEBUFFER_COMPLETE = '賕';
         GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = '賖';
         GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = '賗';
         GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = '賛';
         GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = '賜';
      } else if(var0.GL_ARB_framebuffer_object) {
         capsString = capsString + "ARB_framebuffer_object is supported and separate blending is supported.\n";
         fboMode = GLX.FboMode.ARB;
         GL_FRAMEBUFFER = '赀';
         GL_RENDERBUFFER = '赁';
         GL_COLOR_ATTACHMENT0 = '賠';
         GL_DEPTH_ATTACHMENT = '贀';
         GL_FRAMEBUFFER_COMPLETE = '賕';
         GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = '賗';
         GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = '賖';
         GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = '賛';
         GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = '賜';
      } else {
         if(!var0.GL_EXT_framebuffer_object) {
            throw new IllegalStateException("The driver does not appear to support framebuffer objects");
         }

         capsString = capsString + "EXT_framebuffer_object is supported.\n";
         fboMode = GLX.FboMode.EXT;
         GL_FRAMEBUFFER = '赀';
         GL_RENDERBUFFER = '赁';
         GL_COLOR_ATTACHMENT0 = '賠';
         GL_DEPTH_ATTACHMENT = '贀';
         GL_FRAMEBUFFER_COMPLETE = '賕';
         GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = '賗';
         GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = '賖';
         GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = '賛';
         GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = '賜';
      }

      isOpenGl21 = var0.OpenGL21;
      hasShaders = isOpenGl21 || var0.GL_ARB_vertex_shader && var0.GL_ARB_fragment_shader && var0.GL_ARB_shader_objects;
      capsString = capsString + "Shaders are " + (hasShaders?"":"not ") + "available because ";
      if(hasShaders) {
         if(var0.OpenGL21) {
            capsString = capsString + "OpenGL 2.1 is supported.\n";
            useShaderArb = false;
            GL_LINK_STATUS = '讂';
            GL_COMPILE_STATUS = '讁';
            GL_VERTEX_SHADER = '謱';
            GL_FRAGMENT_SHADER = '謰';
         } else {
            capsString = capsString + "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
            useShaderArb = true;
            GL_LINK_STATUS = '讂';
            GL_COMPILE_STATUS = '讁';
            GL_VERTEX_SHADER = '謱';
            GL_FRAGMENT_SHADER = '謰';
         }
      } else {
         capsString = capsString + "OpenGL 2.1 is " + (var0.OpenGL21?"":"not ") + "supported, ";
         capsString = capsString + "ARB_shader_objects is " + (var0.GL_ARB_shader_objects?"":"not ") + "supported, ";
         capsString = capsString + "ARB_vertex_shader is " + (var0.GL_ARB_vertex_shader?"":"not ") + "supported, and ";
         capsString = capsString + "ARB_fragment_shader is " + (var0.GL_ARB_fragment_shader?"":"not ") + "supported.\n";
      }

      usePostProcess = hasShaders;
      String var1 = GL11.glGetString(7936).toLowerCase(Locale.ROOT);
      isNvidia = var1.contains("nvidia");
      useVboArb = !var0.OpenGL15 && var0.GL_ARB_vertex_buffer_object;
      capsString = capsString + "VBOs are available because ";
      if(useVboArb) {
         capsString = capsString + "ARB_vertex_buffer_object is supported.\n";
         GL_STATIC_DRAW = '裤';
         GL_ARRAY_BUFFER = '袒';
      } else {
         capsString = capsString + "OpenGL 1.5 is supported.\n";
         GL_STATIC_DRAW = '裤';
         GL_ARRAY_BUFFER = '袒';
      }

      isAmd = var1.contains("ati");
      if(isAmd) {
         needVbo = true;
      }

      try {
         Processor[] vars2 = (new SystemInfo()).getHardware().getProcessors();
         cpuInfo = String.format("%dx %s", new Object[]{Integer.valueOf(vars2.length), vars2[0]}).replaceAll("\\s+", " ");
      } catch (Throwable var3) {
         ;
      }

   }

   public static boolean isNextGen() {
      return usePostProcess;
   }

   public static String getCapsString() {
      return capsString;
   }

   public static int glGetProgrami(int var0, int var1) {
      return useShaderArb?ARBShaderObjects.glGetObjectParameteriARB(var0, var1):GL20.glGetProgrami(var0, var1);
   }

   public static void glAttachShader(int var0, int var1) {
      if(useShaderArb) {
         ARBShaderObjects.glAttachObjectARB(var0, var1);
      } else {
         GL20.glAttachShader(var0, var1);
      }

   }

   public static void glDeleteShader(int i) {
      if(useShaderArb) {
         ARBShaderObjects.glDeleteObjectARB(i);
      } else {
         GL20.glDeleteShader(i);
      }

   }

   public static int glCreateShader(int i) {
      return useShaderArb?ARBShaderObjects.glCreateShaderObjectARB(i):GL20.glCreateShader(i);
   }

   public static void glShaderSource(int var0, CharSequence charSequence) {
      if(useShaderArb) {
         ARBShaderObjects.glShaderSourceARB(var0, charSequence);
      } else {
         GL20.glShaderSource(var0, charSequence);
      }

   }

   public static void glCompileShader(int i) {
      if(useShaderArb) {
         ARBShaderObjects.glCompileShaderARB(i);
      } else {
         GL20.glCompileShader(i);
      }

   }

   public static int glGetShaderi(int var0, int var1) {
      return useShaderArb?ARBShaderObjects.glGetObjectParameteriARB(var0, var1):GL20.glGetShaderi(var0, var1);
   }

   public static String glGetShaderInfoLog(int var0, int var1) {
      return useShaderArb?ARBShaderObjects.glGetInfoLogARB(var0, var1):GL20.glGetShaderInfoLog(var0, var1);
   }

   public static String glGetProgramInfoLog(int var0, int var1) {
      return useShaderArb?ARBShaderObjects.glGetInfoLogARB(var0, var1):GL20.glGetProgramInfoLog(var0, var1);
   }

   public static void glUseProgram(int i) {
      if(useShaderArb) {
         ARBShaderObjects.glUseProgramObjectARB(i);
      } else {
         GL20.glUseProgram(i);
      }

   }

   public static int glCreateProgram() {
      return useShaderArb?ARBShaderObjects.glCreateProgramObjectARB():GL20.glCreateProgram();
   }

   public static void glDeleteProgram(int i) {
      if(useShaderArb) {
         ARBShaderObjects.glDeleteObjectARB(i);
      } else {
         GL20.glDeleteProgram(i);
      }

   }

   public static void glLinkProgram(int i) {
      if(useShaderArb) {
         ARBShaderObjects.glLinkProgramARB(i);
      } else {
         GL20.glLinkProgram(i);
      }

   }

   public static int glGetUniformLocation(int var0, CharSequence charSequence) {
      return useShaderArb?ARBShaderObjects.glGetUniformLocationARB(var0, charSequence):GL20.glGetUniformLocation(var0, charSequence);
   }

   public static void glUniform1(int var0, IntBuffer intBuffer) {
      if(useShaderArb) {
         ARBShaderObjects.glUniform1ivARB(var0, intBuffer);
      } else {
         GL20.glUniform1iv(var0, intBuffer);
      }

   }

   public static void glUniform1i(int var0, int var1) {
      if(useShaderArb) {
         ARBShaderObjects.glUniform1iARB(var0, var1);
      } else {
         GL20.glUniform1i(var0, var1);
      }

   }

   public static void glUniform1(int var0, FloatBuffer floatBuffer) {
      if(useShaderArb) {
         ARBShaderObjects.glUniform1fvARB(var0, floatBuffer);
      } else {
         GL20.glUniform1fv(var0, floatBuffer);
      }

   }

   public static void glUniform2(int var0, IntBuffer intBuffer) {
      if(useShaderArb) {
         ARBShaderObjects.glUniform2ivARB(var0, intBuffer);
      } else {
         GL20.glUniform2iv(var0, intBuffer);
      }

   }

   public static void glUniform2(int var0, FloatBuffer floatBuffer) {
      if(useShaderArb) {
         ARBShaderObjects.glUniform2fvARB(var0, floatBuffer);
      } else {
         GL20.glUniform2fv(var0, floatBuffer);
      }

   }

   public static void glUniform3(int var0, IntBuffer intBuffer) {
      if(useShaderArb) {
         ARBShaderObjects.glUniform3ivARB(var0, intBuffer);
      } else {
         GL20.glUniform3iv(var0, intBuffer);
      }

   }

   public static void glUniform3(int var0, FloatBuffer floatBuffer) {
      if(useShaderArb) {
         ARBShaderObjects.glUniform3fvARB(var0, floatBuffer);
      } else {
         GL20.glUniform3fv(var0, floatBuffer);
      }

   }

   public static void glUniform4(int var0, IntBuffer intBuffer) {
      if(useShaderArb) {
         ARBShaderObjects.glUniform4ivARB(var0, intBuffer);
      } else {
         GL20.glUniform4iv(var0, intBuffer);
      }

   }

   public static void glUniform4(int var0, FloatBuffer floatBuffer) {
      if(useShaderArb) {
         ARBShaderObjects.glUniform4fvARB(var0, floatBuffer);
      } else {
         GL20.glUniform4fv(var0, floatBuffer);
      }

   }

   public static void glUniformMatrix2(int var0, boolean var1, FloatBuffer floatBuffer) {
      if(useShaderArb) {
         ARBShaderObjects.glUniformMatrix2fvARB(var0, var1, floatBuffer);
      } else {
         GL20.glUniformMatrix2fv(var0, var1, floatBuffer);
      }

   }

   public static void glUniformMatrix3(int var0, boolean var1, FloatBuffer floatBuffer) {
      if(useShaderArb) {
         ARBShaderObjects.glUniformMatrix3fvARB(var0, var1, floatBuffer);
      } else {
         GL20.glUniformMatrix3fv(var0, var1, floatBuffer);
      }

   }

   public static void glUniformMatrix4(int var0, boolean var1, FloatBuffer floatBuffer) {
      if(useShaderArb) {
         ARBShaderObjects.glUniformMatrix4fvARB(var0, var1, floatBuffer);
      } else {
         GL20.glUniformMatrix4fv(var0, var1, floatBuffer);
      }

   }

   public static int glGetAttribLocation(int var0, CharSequence charSequence) {
      return useShaderArb?ARBVertexShader.glGetAttribLocationARB(var0, charSequence):GL20.glGetAttribLocation(var0, charSequence);
   }

   public static int glGenBuffers() {
      return useVboArb?ARBVertexBufferObject.glGenBuffersARB():GL15.glGenBuffers();
   }

   public static void glGenBuffers(IntBuffer intBuffer) {
      if(useVboArb) {
         ARBVertexBufferObject.glGenBuffersARB(intBuffer);
      } else {
         GL15.glGenBuffers(intBuffer);
      }

   }

   public static void glBindBuffer(int var0, int var1) {
      if(useVboArb) {
         ARBVertexBufferObject.glBindBufferARB(var0, var1);
      } else {
         GL15.glBindBuffer(var0, var1);
      }

   }

   public static void glBufferData(int var0, ByteBuffer byteBuffer, int var2) {
      if(useVboArb) {
         ARBVertexBufferObject.glBufferDataARB(var0, byteBuffer, var2);
      } else {
         GL15.glBufferData(var0, byteBuffer, var2);
      }

   }

   public static void glDeleteBuffers(int i) {
      if(useVboArb) {
         ARBVertexBufferObject.glDeleteBuffersARB(i);
      } else {
         GL15.glDeleteBuffers(i);
      }

   }

   public static void glDeleteBuffers(IntBuffer intBuffer) {
      if(useVboArb) {
         ARBVertexBufferObject.glDeleteBuffersARB(intBuffer);
      } else {
         GL15.glDeleteBuffers(intBuffer);
      }

   }

   public static boolean useVbo() {
      return true;
   }

   public static void glBindFramebuffer(int var0, int var1) {
      switch(fboMode) {
      case BASE:
         GL30.glBindFramebuffer(var0, var1);
         break;
      case ARB:
         ARBFramebufferObject.glBindFramebuffer(var0, var1);
         break;
      case EXT:
         EXTFramebufferObject.glBindFramebufferEXT(var0, var1);
      }

   }

   public static void glBindRenderbuffer(int var0, int var1) {
      switch(fboMode) {
      case BASE:
         GL30.glBindRenderbuffer(var0, var1);
         break;
      case ARB:
         ARBFramebufferObject.glBindRenderbuffer(var0, var1);
         break;
      case EXT:
         EXTFramebufferObject.glBindRenderbufferEXT(var0, var1);
      }

   }

   public static void glDeleteRenderbuffers(int i) {
      switch(fboMode) {
      case BASE:
         GL30.glDeleteRenderbuffers(i);
         break;
      case ARB:
         ARBFramebufferObject.glDeleteRenderbuffers(i);
         break;
      case EXT:
         EXTFramebufferObject.glDeleteRenderbuffersEXT(i);
      }

   }

   public static void glDeleteFramebuffers(int i) {
      switch(fboMode) {
      case BASE:
         GL30.glDeleteFramebuffers(i);
         break;
      case ARB:
         ARBFramebufferObject.glDeleteFramebuffers(i);
         break;
      case EXT:
         EXTFramebufferObject.glDeleteFramebuffersEXT(i);
      }

   }

   public static int glGenFramebuffers() {
      switch(fboMode) {
      case BASE:
         return GL30.glGenFramebuffers();
      case ARB:
         return ARBFramebufferObject.glGenFramebuffers();
      case EXT:
         return EXTFramebufferObject.glGenFramebuffersEXT();
      default:
         return -1;
      }
   }

   public static int glGenRenderbuffers() {
      switch(fboMode) {
      case BASE:
         return GL30.glGenRenderbuffers();
      case ARB:
         return ARBFramebufferObject.glGenRenderbuffers();
      case EXT:
         return EXTFramebufferObject.glGenRenderbuffersEXT();
      default:
         return -1;
      }
   }

   public static void glRenderbufferStorage(int var0, int var1, int var2, int var3) {
      switch(fboMode) {
      case BASE:
         GL30.glRenderbufferStorage(var0, var1, var2, var3);
         break;
      case ARB:
         ARBFramebufferObject.glRenderbufferStorage(var0, var1, var2, var3);
         break;
      case EXT:
         EXTFramebufferObject.glRenderbufferStorageEXT(var0, var1, var2, var3);
      }

   }

   public static void glFramebufferRenderbuffer(int var0, int var1, int var2, int var3) {
      switch(fboMode) {
      case BASE:
         GL30.glFramebufferRenderbuffer(var0, var1, var2, var3);
         break;
      case ARB:
         ARBFramebufferObject.glFramebufferRenderbuffer(var0, var1, var2, var3);
         break;
      case EXT:
         EXTFramebufferObject.glFramebufferRenderbufferEXT(var0, var1, var2, var3);
      }

   }

   public static int glCheckFramebufferStatus(int i) {
      switch(fboMode) {
      case BASE:
         return GL30.glCheckFramebufferStatus(i);
      case ARB:
         return ARBFramebufferObject.glCheckFramebufferStatus(i);
      case EXT:
         return EXTFramebufferObject.glCheckFramebufferStatusEXT(i);
      default:
         return -1;
      }
   }

   public static void glFramebufferTexture2D(int var0, int var1, int var2, int var3, int var4) {
      switch(fboMode) {
      case BASE:
         GL30.glFramebufferTexture2D(var0, var1, var2, var3, var4);
         break;
      case ARB:
         ARBFramebufferObject.glFramebufferTexture2D(var0, var1, var2, var3, var4);
         break;
      case EXT:
         EXTFramebufferObject.glFramebufferTexture2DEXT(var0, var1, var2, var3, var4);
      }

   }

   public static int getBoundFramebuffer() {
      switch(fboMode) {
      case BASE:
         return GlStateManager.getInteger('貦');
      case ARB:
         return GlStateManager.getInteger('貦');
      case EXT:
         return GlStateManager.getInteger('貦');
      default:
         return 0;
      }
   }

   public static void glActiveTexture(int i) {
      if(useMultitextureArb) {
         ARBMultitexture.glActiveTextureARB(i);
      } else {
         GL13.glActiveTexture(i);
      }

   }

   public static void glClientActiveTexture(int i) {
      if(useMultitextureArb) {
         ARBMultitexture.glClientActiveTextureARB(i);
      } else {
         GL13.glClientActiveTexture(i);
      }

   }

   public static void glMultiTexCoord2f(int var0, float var1, float var2) {
      if(useMultitextureArb) {
         ARBMultitexture.glMultiTexCoord2fARB(var0, var1, var2);
      } else {
         GL13.glMultiTexCoord2f(var0, var1, var2);
      }

   }

   public static void glBlendFuncSeparate(int var0, int var1, int var2, int var3) {
      if(separateBlend) {
         if(useSeparateBlendExt) {
            EXTBlendFuncSeparate.glBlendFuncSeparateEXT(var0, var1, var2, var3);
         } else {
            GL14.glBlendFuncSeparate(var0, var1, var2, var3);
         }
      } else {
         GL11.glBlendFunc(var0, var1);
      }

   }

   public static boolean isUsingFBOs() {
      return true;
   }

   public static String getCpuInfo() {
      return cpuInfo == null?"<unknown>":cpuInfo;
   }

   public static void renderCrosshair(int i) {
      renderCrosshair(i, true, true, true);
   }

   public static void renderCrosshair(int var0, boolean var1, boolean var2, boolean var3) {
      GlStateManager.disableTexture();
      GlStateManager.depthMask(false);
      Tesselator var4 = Tesselator.getInstance();
      BufferBuilder var5 = var4.getBuilder();
      GL11.glLineWidth(4.0F);
      var5.begin(1, DefaultVertexFormat.POSITION_COLOR);
      if(var1) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var5.vertex((double)var0, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if(var2) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var5.vertex(0.0D, (double)var0, 0.0D).color(0, 0, 0, 255).endVertex();
      }

      if(var3) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var5.vertex(0.0D, 0.0D, (double)var0).color(0, 0, 0, 255).endVertex();
      }

      var4.end();
      GL11.glLineWidth(2.0F);
      var5.begin(1, DefaultVertexFormat.POSITION_COLOR);
      if(var1) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
         var5.vertex((double)var0, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
      }

      if(var2) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(0, 255, 0, 255).endVertex();
         var5.vertex(0.0D, (double)var0, 0.0D).color(0, 255, 0, 255).endVertex();
      }

      if(var3) {
         var5.vertex(0.0D, 0.0D, 0.0D).color(127, 127, 255, 255).endVertex();
         var5.vertex(0.0D, 0.0D, (double)var0).color(127, 127, 255, 255).endVertex();
      }

      var4.end();
      GL11.glLineWidth(1.0F);
      GlStateManager.depthMask(true);
      GlStateManager.enableTexture();
   }

   public static String getErrorString(int i) {
      return (String)LOOKUP_MAP.get(Integer.valueOf(i));
   }

   public static Object make(Supplier supplier) {
      return supplier.get();
   }

   public static Object make(Object var0, Consumer consumer) {
      consumer.accept(var0);
      return var0;
   }

   @ClientJarOnly
   static enum FboMode {
      BASE,
      ARB,
      EXT;
   }
}
