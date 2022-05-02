package com.mojang.blaze3d.platform;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.DebugMemoryUntracker;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;

@ClientJarOnly
public class GlDebug {
   private static final Logger LOGGER = LogManager.getLogger();
   protected static final ByteBuffer BYTE_BUFFER = MemoryTracker.createByteBuffer(64);
   protected static final FloatBuffer FLOAT_BUFFER = BYTE_BUFFER.asFloatBuffer();
   protected static final IntBuffer INT_BUFFER = BYTE_BUFFER.asIntBuffer();
   private static final Joiner NEWLINE_JOINER = Joiner.on('\n');
   private static final Joiner STATEMENT_JOINER = Joiner.on("; ");
   private static final Map BY_ID = Maps.newHashMap();
   private static final List DEBUG_LEVELS = ImmutableList.of(Integer.valueOf('酆'), Integer.valueOf('酇'), Integer.valueOf('酈'), Integer.valueOf('艫'));
   private static final List DEBUG_LEVELS_ARB = ImmutableList.of(Integer.valueOf('酆'), Integer.valueOf('酇'), Integer.valueOf('酈'));
   private static final Map SAVED_STATES = Maps.newHashMap();

   private static String printUnknownToken(int i) {
      return "Unknown (0x" + Integer.toHexString(i).toUpperCase() + ")";
   }

   private static String sourceToString(int i) {
      switch(i) {
      case 33350:
         return "API";
      case 33351:
         return "WINDOW SYSTEM";
      case 33352:
         return "SHADER COMPILER";
      case 33353:
         return "THIRD PARTY";
      case 33354:
         return "APPLICATION";
      case 33355:
         return "OTHER";
      default:
         return printUnknownToken(i);
      }
   }

   private static String typeToString(int i) {
      switch(i) {
      case 33356:
         return "ERROR";
      case 33357:
         return "DEPRECATED BEHAVIOR";
      case 33358:
         return "UNDEFINED BEHAVIOR";
      case 33359:
         return "PORTABILITY";
      case 33360:
         return "PERFORMANCE";
      case 33361:
         return "OTHER";
      case 33384:
         return "MARKER";
      default:
         return printUnknownToken(i);
      }
   }

   private static String severityToString(int i) {
      switch(i) {
      case 33387:
         return "NOTIFICATION";
      case 37190:
         return "HIGH";
      case 37191:
         return "MEDIUM";
      case 37192:
         return "LOW";
      default:
         return printUnknownToken(i);
      }
   }

   private static void printDebugLog(int var0, int var1, int var2, int var3, int var4, long var5, long var7) {
      LOGGER.info("OpenGL debug message, id={}, source={}, type={}, severity={}, message={}", Integer.valueOf(var2), sourceToString(var0), typeToString(var1), severityToString(var3), GLDebugMessageCallback.getMessage(var4, var5));
   }

   private static void setup(int var0, String string) {
      BY_ID.merge(Integer.valueOf(var0), string, (var0, var1) -> {
         return var0 + "/" + var1;
      });
   }

   public static void enableDebugCallback(int var0, boolean var1) {
      if(var0 > 0) {
         GLCapabilities var2 = GL.getCapabilities();
         if(var2.GL_KHR_debug) {
            GL11.glEnable('鋠');
            if(var1) {
               GL11.glEnable('艂');
            }

            for(int var3 = 0; var3 < DEBUG_LEVELS.size(); ++var3) {
               boolean var4 = var3 < var0;
               KHRDebug.glDebugMessageControl(4352, 4352, ((Integer)DEBUG_LEVELS.get(var3)).intValue(), (int[])null, var4);
            }

            KHRDebug.glDebugMessageCallback((GLDebugMessageCallbackI)GLX.make(GLDebugMessageCallback.create(GlDebug::printDebugLog), DebugMemoryUntracker::untrack), 0L);
         } else if(var2.GL_ARB_debug_output) {
            if(var1) {
               GL11.glEnable('艂');
            }

            for(int var3 = 0; var3 < DEBUG_LEVELS_ARB.size(); ++var3) {
               boolean var4 = var3 < var0;
               ARBDebugOutput.glDebugMessageControlARB(4352, 4352, ((Integer)DEBUG_LEVELS_ARB.get(var3)).intValue(), (int[])null, var4);
            }

            ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI)GLX.make(GLDebugMessageARBCallback.create(GlDebug::printDebugLog), DebugMemoryUntracker::untrack), 0L);
         }

      }
   }

   static {
      setup(256, "GL11.GL_ACCUM");
      setup(257, "GL11.GL_LOAD");
      setup(258, "GL11.GL_RETURN");
      setup(259, "GL11.GL_MULT");
      setup(260, "GL11.GL_ADD");
      setup(512, "GL11.GL_NEVER");
      setup(513, "GL11.GL_LESS");
      setup(514, "GL11.GL_EQUAL");
      setup(515, "GL11.GL_LEQUAL");
      setup(516, "GL11.GL_GREATER");
      setup(517, "GL11.GL_NOTEQUAL");
      setup(518, "GL11.GL_GEQUAL");
      setup(519, "GL11.GL_ALWAYS");
      setup(0, "GL11.GL_POINTS");
      setup(1, "GL11.GL_LINES");
      setup(2, "GL11.GL_LINE_LOOP");
      setup(3, "GL11.GL_LINE_STRIP");
      setup(4, "GL11.GL_TRIANGLES");
      setup(5, "GL11.GL_TRIANGLE_STRIP");
      setup(6, "GL11.GL_TRIANGLE_FAN");
      setup(7, "GL11.GL_QUADS");
      setup(8, "GL11.GL_QUAD_STRIP");
      setup(9, "GL11.GL_POLYGON");
      setup(0, "GL11.GL_ZERO");
      setup(1, "GL11.GL_ONE");
      setup(768, "GL11.GL_SRC_COLOR");
      setup(769, "GL11.GL_ONE_MINUS_SRC_COLOR");
      setup(770, "GL11.GL_SRC_ALPHA");
      setup(771, "GL11.GL_ONE_MINUS_SRC_ALPHA");
      setup(772, "GL11.GL_DST_ALPHA");
      setup(773, "GL11.GL_ONE_MINUS_DST_ALPHA");
      setup(774, "GL11.GL_DST_COLOR");
      setup(775, "GL11.GL_ONE_MINUS_DST_COLOR");
      setup(776, "GL11.GL_SRC_ALPHA_SATURATE");
      setup('老', "GL14.GL_CONSTANT_COLOR");
      setup('耂', "GL14.GL_ONE_MINUS_CONSTANT_COLOR");
      setup('考', "GL14.GL_CONSTANT_ALPHA");
      setup('耄', "GL14.GL_ONE_MINUS_CONSTANT_ALPHA");
      setup(1, "GL11.GL_TRUE");
      setup(0, "GL11.GL_FALSE");
      setup(12288, "GL11.GL_CLIP_PLANE0");
      setup(12289, "GL11.GL_CLIP_PLANE1");
      setup(12290, "GL11.GL_CLIP_PLANE2");
      setup(12291, "GL11.GL_CLIP_PLANE3");
      setup(12292, "GL11.GL_CLIP_PLANE4");
      setup(12293, "GL11.GL_CLIP_PLANE5");
      setup(5120, "GL11.GL_BYTE");
      setup(5121, "GL11.GL_UNSIGNED_BYTE");
      setup(5122, "GL11.GL_SHORT");
      setup(5123, "GL11.GL_UNSIGNED_SHORT");
      setup(5124, "GL11.GL_INT");
      setup(5125, "GL11.GL_UNSIGNED_INT");
      setup(5126, "GL11.GL_FLOAT");
      setup(5127, "GL11.GL_2_BYTES");
      setup(5128, "GL11.GL_3_BYTES");
      setup(5129, "GL11.GL_4_BYTES");
      setup(5130, "GL11.GL_DOUBLE");
      setup(0, "GL11.GL_NONE");
      setup(1024, "GL11.GL_FRONT_LEFT");
      setup(1025, "GL11.GL_FRONT_RIGHT");
      setup(1026, "GL11.GL_BACK_LEFT");
      setup(1027, "GL11.GL_BACK_RIGHT");
      setup(1028, "GL11.GL_FRONT");
      setup(1029, "GL11.GL_BACK");
      setup(1030, "GL11.GL_LEFT");
      setup(1031, "GL11.GL_RIGHT");
      setup(1032, "GL11.GL_FRONT_AND_BACK");
      setup(1033, "GL11.GL_AUX0");
      setup(1034, "GL11.GL_AUX1");
      setup(1035, "GL11.GL_AUX2");
      setup(1036, "GL11.GL_AUX3");
      setup(0, "GL11.GL_NO_ERROR");
      setup(1280, "GL11.GL_INVALID_ENUM");
      setup(1281, "GL11.GL_INVALID_VALUE");
      setup(1282, "GL11.GL_INVALID_OPERATION");
      setup(1283, "GL11.GL_STACK_OVERFLOW");
      setup(1284, "GL11.GL_STACK_UNDERFLOW");
      setup(1285, "GL11.GL_OUT_OF_MEMORY");
      setup(1536, "GL11.GL_2D");
      setup(1537, "GL11.GL_3D");
      setup(1538, "GL11.GL_3D_COLOR");
      setup(1539, "GL11.GL_3D_COLOR_TEXTURE");
      setup(1540, "GL11.GL_4D_COLOR_TEXTURE");
      setup(1792, "GL11.GL_PASS_THROUGH_TOKEN");
      setup(1793, "GL11.GL_POINT_TOKEN");
      setup(1794, "GL11.GL_LINE_TOKEN");
      setup(1795, "GL11.GL_POLYGON_TOKEN");
      setup(1796, "GL11.GL_BITMAP_TOKEN");
      setup(1797, "GL11.GL_DRAW_PIXEL_TOKEN");
      setup(1798, "GL11.GL_COPY_PIXEL_TOKEN");
      setup(1799, "GL11.GL_LINE_RESET_TOKEN");
      setup(2048, "GL11.GL_EXP");
      setup(2049, "GL11.GL_EXP2");
      setup(2304, "GL11.GL_CW");
      setup(2305, "GL11.GL_CCW");
      setup(2560, "GL11.GL_COEFF");
      setup(2561, "GL11.GL_ORDER");
      setup(2562, "GL11.GL_DOMAIN");
      setup(2816, "GL11.GL_CURRENT_COLOR");
      setup(2817, "GL11.GL_CURRENT_INDEX");
      setup(2818, "GL11.GL_CURRENT_NORMAL");
      setup(2819, "GL11.GL_CURRENT_TEXTURE_COORDS");
      setup(2820, "GL11.GL_CURRENT_RASTER_COLOR");
      setup(2821, "GL11.GL_CURRENT_RASTER_INDEX");
      setup(2822, "GL11.GL_CURRENT_RASTER_TEXTURE_COORDS");
      setup(2823, "GL11.GL_CURRENT_RASTER_POSITION");
      setup(2824, "GL11.GL_CURRENT_RASTER_POSITION_VALID");
      setup(2825, "GL11.GL_CURRENT_RASTER_DISTANCE");
      setup(2832, "GL11.GL_POINT_SMOOTH");
      setup(2833, "GL11.GL_POINT_SIZE");
      setup(2834, "GL11.GL_POINT_SIZE_RANGE");
      setup(2835, "GL11.GL_POINT_SIZE_GRANULARITY");
      setup(2848, "GL11.GL_LINE_SMOOTH");
      setup(2849, "GL11.GL_LINE_WIDTH");
      setup(2850, "GL11.GL_LINE_WIDTH_RANGE");
      setup(2851, "GL11.GL_LINE_WIDTH_GRANULARITY");
      setup(2852, "GL11.GL_LINE_STIPPLE");
      setup(2853, "GL11.GL_LINE_STIPPLE_PATTERN");
      setup(2854, "GL11.GL_LINE_STIPPLE_REPEAT");
      setup(2864, "GL11.GL_LIST_MODE");
      setup(2865, "GL11.GL_MAX_LIST_NESTING");
      setup(2866, "GL11.GL_LIST_BASE");
      setup(2867, "GL11.GL_LIST_INDEX");
      setup(2880, "GL11.GL_POLYGON_MODE");
      setup(2881, "GL11.GL_POLYGON_SMOOTH");
      setup(2882, "GL11.GL_POLYGON_STIPPLE");
      setup(2883, "GL11.GL_EDGE_FLAG");
      setup(2884, "GL11.GL_CULL_FACE");
      setup(2885, "GL11.GL_CULL_FACE_MODE");
      setup(2886, "GL11.GL_FRONT_FACE");
      setup(2896, "GL11.GL_LIGHTING");
      setup(2897, "GL11.GL_LIGHT_MODEL_LOCAL_VIEWER");
      setup(2898, "GL11.GL_LIGHT_MODEL_TWO_SIDE");
      setup(2899, "GL11.GL_LIGHT_MODEL_AMBIENT");
      setup(2900, "GL11.GL_SHADE_MODEL");
      setup(2901, "GL11.GL_COLOR_MATERIAL_FACE");
      setup(2902, "GL11.GL_COLOR_MATERIAL_PARAMETER");
      setup(2903, "GL11.GL_COLOR_MATERIAL");
      setup(2912, "GL11.GL_FOG");
      setup(2913, "GL11.GL_FOG_INDEX");
      setup(2914, "GL11.GL_FOG_DENSITY");
      setup(2915, "GL11.GL_FOG_START");
      setup(2916, "GL11.GL_FOG_END");
      setup(2917, "GL11.GL_FOG_MODE");
      setup(2918, "GL11.GL_FOG_COLOR");
      setup(2928, "GL11.GL_DEPTH_RANGE");
      setup(2929, "GL11.GL_DEPTH_TEST");
      setup(2930, "GL11.GL_DEPTH_WRITEMASK");
      setup(2931, "GL11.GL_DEPTH_CLEAR_VALUE");
      setup(2932, "GL11.GL_DEPTH_FUNC");
      setup(2944, "GL11.GL_ACCUM_CLEAR_VALUE");
      setup(2960, "GL11.GL_STENCIL_TEST");
      setup(2961, "GL11.GL_STENCIL_CLEAR_VALUE");
      setup(2962, "GL11.GL_STENCIL_FUNC");
      setup(2963, "GL11.GL_STENCIL_VALUE_MASK");
      setup(2964, "GL11.GL_STENCIL_FAIL");
      setup(2965, "GL11.GL_STENCIL_PASS_DEPTH_FAIL");
      setup(2966, "GL11.GL_STENCIL_PASS_DEPTH_PASS");
      setup(2967, "GL11.GL_STENCIL_REF");
      setup(2968, "GL11.GL_STENCIL_WRITEMASK");
      setup(2976, "GL11.GL_MATRIX_MODE");
      setup(2977, "GL11.GL_NORMALIZE");
      setup(2978, "GL11.GL_VIEWPORT");
      setup(2979, "GL11.GL_MODELVIEW_STACK_DEPTH");
      setup(2980, "GL11.GL_PROJECTION_STACK_DEPTH");
      setup(2981, "GL11.GL_TEXTURE_STACK_DEPTH");
      setup(2982, "GL11.GL_MODELVIEW_MATRIX");
      setup(2983, "GL11.GL_PROJECTION_MATRIX");
      setup(2984, "GL11.GL_TEXTURE_MATRIX");
      setup(2992, "GL11.GL_ATTRIB_STACK_DEPTH");
      setup(2993, "GL11.GL_CLIENT_ATTRIB_STACK_DEPTH");
      setup(3008, "GL11.GL_ALPHA_TEST");
      setup(3009, "GL11.GL_ALPHA_TEST_FUNC");
      setup(3010, "GL11.GL_ALPHA_TEST_REF");
      setup(3024, "GL11.GL_DITHER");
      setup(3040, "GL11.GL_BLEND_DST");
      setup(3041, "GL11.GL_BLEND_SRC");
      setup(3042, "GL11.GL_BLEND");
      setup(3056, "GL11.GL_LOGIC_OP_MODE");
      setup(3057, "GL11.GL_INDEX_LOGIC_OP");
      setup(3058, "GL11.GL_COLOR_LOGIC_OP");
      setup(3072, "GL11.GL_AUX_BUFFERS");
      setup(3073, "GL11.GL_DRAW_BUFFER");
      setup(3074, "GL11.GL_READ_BUFFER");
      setup(3088, "GL11.GL_SCISSOR_BOX");
      setup(3089, "GL11.GL_SCISSOR_TEST");
      setup(3104, "GL11.GL_INDEX_CLEAR_VALUE");
      setup(3105, "GL11.GL_INDEX_WRITEMASK");
      setup(3106, "GL11.GL_COLOR_CLEAR_VALUE");
      setup(3107, "GL11.GL_COLOR_WRITEMASK");
      setup(3120, "GL11.GL_INDEX_MODE");
      setup(3121, "GL11.GL_RGBA_MODE");
      setup(3122, "GL11.GL_DOUBLEBUFFER");
      setup(3123, "GL11.GL_STEREO");
      setup(3136, "GL11.GL_RENDER_MODE");
      setup(3152, "GL11.GL_PERSPECTIVE_CORRECTION_HINT");
      setup(3153, "GL11.GL_POINT_SMOOTH_HINT");
      setup(3154, "GL11.GL_LINE_SMOOTH_HINT");
      setup(3155, "GL11.GL_POLYGON_SMOOTH_HINT");
      setup(3156, "GL11.GL_FOG_HINT");
      setup(3168, "GL11.GL_TEXTURE_GEN_S");
      setup(3169, "GL11.GL_TEXTURE_GEN_T");
      setup(3170, "GL11.GL_TEXTURE_GEN_R");
      setup(3171, "GL11.GL_TEXTURE_GEN_Q");
      setup(3184, "GL11.GL_PIXEL_MAP_I_TO_I");
      setup(3185, "GL11.GL_PIXEL_MAP_S_TO_S");
      setup(3186, "GL11.GL_PIXEL_MAP_I_TO_R");
      setup(3187, "GL11.GL_PIXEL_MAP_I_TO_G");
      setup(3188, "GL11.GL_PIXEL_MAP_I_TO_B");
      setup(3189, "GL11.GL_PIXEL_MAP_I_TO_A");
      setup(3190, "GL11.GL_PIXEL_MAP_R_TO_R");
      setup(3191, "GL11.GL_PIXEL_MAP_G_TO_G");
      setup(3192, "GL11.GL_PIXEL_MAP_B_TO_B");
      setup(3193, "GL11.GL_PIXEL_MAP_A_TO_A");
      setup(3248, "GL11.GL_PIXEL_MAP_I_TO_I_SIZE");
      setup(3249, "GL11.GL_PIXEL_MAP_S_TO_S_SIZE");
      setup(3250, "GL11.GL_PIXEL_MAP_I_TO_R_SIZE");
      setup(3251, "GL11.GL_PIXEL_MAP_I_TO_G_SIZE");
      setup(3252, "GL11.GL_PIXEL_MAP_I_TO_B_SIZE");
      setup(3253, "GL11.GL_PIXEL_MAP_I_TO_A_SIZE");
      setup(3254, "GL11.GL_PIXEL_MAP_R_TO_R_SIZE");
      setup(3255, "GL11.GL_PIXEL_MAP_G_TO_G_SIZE");
      setup(3256, "GL11.GL_PIXEL_MAP_B_TO_B_SIZE");
      setup(3257, "GL11.GL_PIXEL_MAP_A_TO_A_SIZE");
      setup(3312, "GL11.GL_UNPACK_SWAP_BYTES");
      setup(3313, "GL11.GL_UNPACK_LSB_FIRST");
      setup(3314, "GL11.GL_UNPACK_ROW_LENGTH");
      setup(3315, "GL11.GL_UNPACK_SKIP_ROWS");
      setup(3316, "GL11.GL_UNPACK_SKIP_PIXELS");
      setup(3317, "GL11.GL_UNPACK_ALIGNMENT");
      setup(3328, "GL11.GL_PACK_SWAP_BYTES");
      setup(3329, "GL11.GL_PACK_LSB_FIRST");
      setup(3330, "GL11.GL_PACK_ROW_LENGTH");
      setup(3331, "GL11.GL_PACK_SKIP_ROWS");
      setup(3332, "GL11.GL_PACK_SKIP_PIXELS");
      setup(3333, "GL11.GL_PACK_ALIGNMENT");
      setup(3344, "GL11.GL_MAP_COLOR");
      setup(3345, "GL11.GL_MAP_STENCIL");
      setup(3346, "GL11.GL_INDEX_SHIFT");
      setup(3347, "GL11.GL_INDEX_OFFSET");
      setup(3348, "GL11.GL_RED_SCALE");
      setup(3349, "GL11.GL_RED_BIAS");
      setup(3350, "GL11.GL_ZOOM_X");
      setup(3351, "GL11.GL_ZOOM_Y");
      setup(3352, "GL11.GL_GREEN_SCALE");
      setup(3353, "GL11.GL_GREEN_BIAS");
      setup(3354, "GL11.GL_BLUE_SCALE");
      setup(3355, "GL11.GL_BLUE_BIAS");
      setup(3356, "GL11.GL_ALPHA_SCALE");
      setup(3357, "GL11.GL_ALPHA_BIAS");
      setup(3358, "GL11.GL_DEPTH_SCALE");
      setup(3359, "GL11.GL_DEPTH_BIAS");
      setup(3376, "GL11.GL_MAX_EVAL_ORDER");
      setup(3377, "GL11.GL_MAX_LIGHTS");
      setup(3378, "GL11.GL_MAX_CLIP_PLANES");
      setup(3379, "GL11.GL_MAX_TEXTURE_SIZE");
      setup(3380, "GL11.GL_MAX_PIXEL_MAP_TABLE");
      setup(3381, "GL11.GL_MAX_ATTRIB_STACK_DEPTH");
      setup(3382, "GL11.GL_MAX_MODELVIEW_STACK_DEPTH");
      setup(3383, "GL11.GL_MAX_NAME_STACK_DEPTH");
      setup(3384, "GL11.GL_MAX_PROJECTION_STACK_DEPTH");
      setup(3385, "GL11.GL_MAX_TEXTURE_STACK_DEPTH");
      setup(3386, "GL11.GL_MAX_VIEWPORT_DIMS");
      setup(3387, "GL11.GL_MAX_CLIENT_ATTRIB_STACK_DEPTH");
      setup(3408, "GL11.GL_SUBPIXEL_BITS");
      setup(3409, "GL11.GL_INDEX_BITS");
      setup(3410, "GL11.GL_RED_BITS");
      setup(3411, "GL11.GL_GREEN_BITS");
      setup(3412, "GL11.GL_BLUE_BITS");
      setup(3413, "GL11.GL_ALPHA_BITS");
      setup(3414, "GL11.GL_DEPTH_BITS");
      setup(3415, "GL11.GL_STENCIL_BITS");
      setup(3416, "GL11.GL_ACCUM_RED_BITS");
      setup(3417, "GL11.GL_ACCUM_GREEN_BITS");
      setup(3418, "GL11.GL_ACCUM_BLUE_BITS");
      setup(3419, "GL11.GL_ACCUM_ALPHA_BITS");
      setup(3440, "GL11.GL_NAME_STACK_DEPTH");
      setup(3456, "GL11.GL_AUTO_NORMAL");
      setup(3472, "GL11.GL_MAP1_COLOR_4");
      setup(3473, "GL11.GL_MAP1_INDEX");
      setup(3474, "GL11.GL_MAP1_NORMAL");
      setup(3475, "GL11.GL_MAP1_TEXTURE_COORD_1");
      setup(3476, "GL11.GL_MAP1_TEXTURE_COORD_2");
      setup(3477, "GL11.GL_MAP1_TEXTURE_COORD_3");
      setup(3478, "GL11.GL_MAP1_TEXTURE_COORD_4");
      setup(3479, "GL11.GL_MAP1_VERTEX_3");
      setup(3480, "GL11.GL_MAP1_VERTEX_4");
      setup(3504, "GL11.GL_MAP2_COLOR_4");
      setup(3505, "GL11.GL_MAP2_INDEX");
      setup(3506, "GL11.GL_MAP2_NORMAL");
      setup(3507, "GL11.GL_MAP2_TEXTURE_COORD_1");
      setup(3508, "GL11.GL_MAP2_TEXTURE_COORD_2");
      setup(3509, "GL11.GL_MAP2_TEXTURE_COORD_3");
      setup(3510, "GL11.GL_MAP2_TEXTURE_COORD_4");
      setup(3511, "GL11.GL_MAP2_VERTEX_3");
      setup(3512, "GL11.GL_MAP2_VERTEX_4");
      setup(3536, "GL11.GL_MAP1_GRID_DOMAIN");
      setup(3537, "GL11.GL_MAP1_GRID_SEGMENTS");
      setup(3538, "GL11.GL_MAP2_GRID_DOMAIN");
      setup(3539, "GL11.GL_MAP2_GRID_SEGMENTS");
      setup(3552, "GL11.GL_TEXTURE_1D");
      setup(3553, "GL11.GL_TEXTURE_2D");
      setup(3568, "GL11.GL_FEEDBACK_BUFFER_POINTER");
      setup(3569, "GL11.GL_FEEDBACK_BUFFER_SIZE");
      setup(3570, "GL11.GL_FEEDBACK_BUFFER_TYPE");
      setup(3571, "GL11.GL_SELECTION_BUFFER_POINTER");
      setup(3572, "GL11.GL_SELECTION_BUFFER_SIZE");
      setup(4096, "GL11.GL_TEXTURE_WIDTH");
      setup(4097, "GL11.GL_TEXTURE_HEIGHT");
      setup(4099, "GL11.GL_TEXTURE_INTERNAL_FORMAT");
      setup(4100, "GL11.GL_TEXTURE_BORDER_COLOR");
      setup(4101, "GL11.GL_TEXTURE_BORDER");
      setup(4352, "GL11.GL_DONT_CARE");
      setup(4353, "GL11.GL_FASTEST");
      setup(4354, "GL11.GL_NICEST");
      setup(16384, "GL11.GL_LIGHT0");
      setup(16385, "GL11.GL_LIGHT1");
      setup(16386, "GL11.GL_LIGHT2");
      setup(16387, "GL11.GL_LIGHT3");
      setup(16388, "GL11.GL_LIGHT4");
      setup(16389, "GL11.GL_LIGHT5");
      setup(16390, "GL11.GL_LIGHT6");
      setup(16391, "GL11.GL_LIGHT7");
      setup(4608, "GL11.GL_AMBIENT");
      setup(4609, "GL11.GL_DIFFUSE");
      setup(4610, "GL11.GL_SPECULAR");
      setup(4611, "GL11.GL_POSITION");
      setup(4612, "GL11.GL_SPOT_DIRECTION");
      setup(4613, "GL11.GL_SPOT_EXPONENT");
      setup(4614, "GL11.GL_SPOT_CUTOFF");
      setup(4615, "GL11.GL_CONSTANT_ATTENUATION");
      setup(4616, "GL11.GL_LINEAR_ATTENUATION");
      setup(4617, "GL11.GL_QUADRATIC_ATTENUATION");
      setup(4864, "GL11.GL_COMPILE");
      setup(4865, "GL11.GL_COMPILE_AND_EXECUTE");
      setup(5376, "GL11.GL_CLEAR");
      setup(5377, "GL11.GL_AND");
      setup(5378, "GL11.GL_AND_REVERSE");
      setup(5379, "GL11.GL_COPY");
      setup(5380, "GL11.GL_AND_INVERTED");
      setup(5381, "GL11.GL_NOOP");
      setup(5382, "GL11.GL_XOR");
      setup(5383, "GL11.GL_OR");
      setup(5384, "GL11.GL_NOR");
      setup(5385, "GL11.GL_EQUIV");
      setup(5386, "GL11.GL_INVERT");
      setup(5387, "GL11.GL_OR_REVERSE");
      setup(5388, "GL11.GL_COPY_INVERTED");
      setup(5389, "GL11.GL_OR_INVERTED");
      setup(5390, "GL11.GL_NAND");
      setup(5391, "GL11.GL_SET");
      setup(5632, "GL11.GL_EMISSION");
      setup(5633, "GL11.GL_SHININESS");
      setup(5634, "GL11.GL_AMBIENT_AND_DIFFUSE");
      setup(5635, "GL11.GL_COLOR_INDEXES");
      setup(5888, "GL11.GL_MODELVIEW");
      setup(5889, "GL11.GL_PROJECTION");
      setup(5890, "GL11.GL_TEXTURE");
      setup(6144, "GL11.GL_COLOR");
      setup(6145, "GL11.GL_DEPTH");
      setup(6146, "GL11.GL_STENCIL");
      setup(6400, "GL11.GL_COLOR_INDEX");
      setup(6401, "GL11.GL_STENCIL_INDEX");
      setup(6402, "GL11.GL_DEPTH_COMPONENT");
      setup(6403, "GL11.GL_RED");
      setup(6404, "GL11.GL_GREEN");
      setup(6405, "GL11.GL_BLUE");
      setup(6406, "GL11.GL_ALPHA");
      setup(6407, "GL11.GL_RGB");
      setup(6408, "GL11.GL_RGBA");
      setup(6409, "GL11.GL_LUMINANCE");
      setup(6410, "GL11.GL_LUMINANCE_ALPHA");
      setup(6656, "GL11.GL_BITMAP");
      setup(6912, "GL11.GL_POINT");
      setup(6913, "GL11.GL_LINE");
      setup(6914, "GL11.GL_FILL");
      setup(7168, "GL11.GL_RENDER");
      setup(7169, "GL11.GL_FEEDBACK");
      setup(7170, "GL11.GL_SELECT");
      setup(7424, "GL11.GL_FLAT");
      setup(7425, "GL11.GL_SMOOTH");
      setup(7680, "GL11.GL_KEEP");
      setup(7681, "GL11.GL_REPLACE");
      setup(7682, "GL11.GL_INCR");
      setup(7683, "GL11.GL_DECR");
      setup(7936, "GL11.GL_VENDOR");
      setup(7937, "GL11.GL_RENDERER");
      setup(7938, "GL11.GL_VERSION");
      setup(7939, "GL11.GL_EXTENSIONS");
      setup(8192, "GL11.GL_S");
      setup(8193, "GL11.GL_T");
      setup(8194, "GL11.GL_R");
      setup(8195, "GL11.GL_Q");
      setup(8448, "GL11.GL_MODULATE");
      setup(8449, "GL11.GL_DECAL");
      setup(8704, "GL11.GL_TEXTURE_ENV_MODE");
      setup(8705, "GL11.GL_TEXTURE_ENV_COLOR");
      setup(8960, "GL11.GL_TEXTURE_ENV");
      setup(9216, "GL11.GL_EYE_LINEAR");
      setup(9217, "GL11.GL_OBJECT_LINEAR");
      setup(9218, "GL11.GL_SPHERE_MAP");
      setup(9472, "GL11.GL_TEXTURE_GEN_MODE");
      setup(9473, "GL11.GL_OBJECT_PLANE");
      setup(9474, "GL11.GL_EYE_PLANE");
      setup(9728, "GL11.GL_NEAREST");
      setup(9729, "GL11.GL_LINEAR");
      setup(9984, "GL11.GL_NEAREST_MIPMAP_NEAREST");
      setup(9985, "GL11.GL_LINEAR_MIPMAP_NEAREST");
      setup(9986, "GL11.GL_NEAREST_MIPMAP_LINEAR");
      setup(9987, "GL11.GL_LINEAR_MIPMAP_LINEAR");
      setup(10240, "GL11.GL_TEXTURE_MAG_FILTER");
      setup(10241, "GL11.GL_TEXTURE_MIN_FILTER");
      setup(10242, "GL11.GL_TEXTURE_WRAP_S");
      setup(10243, "GL11.GL_TEXTURE_WRAP_T");
      setup(10496, "GL11.GL_CLAMP");
      setup(10497, "GL11.GL_REPEAT");
      setup(-1, "GL11.GL_ALL_CLIENT_ATTRIB_BITS");
      setup('耸', "GL11.GL_POLYGON_OFFSET_FACTOR");
      setup(10752, "GL11.GL_POLYGON_OFFSET_UNITS");
      setup(10753, "GL11.GL_POLYGON_OFFSET_POINT");
      setup(10754, "GL11.GL_POLYGON_OFFSET_LINE");
      setup('耷', "GL11.GL_POLYGON_OFFSET_FILL");
      setup('耻', "GL11.GL_ALPHA4");
      setup('耼', "GL11.GL_ALPHA8");
      setup('耽', "GL11.GL_ALPHA12");
      setup('耾', "GL11.GL_ALPHA16");
      setup('耿', "GL11.GL_LUMINANCE4");
      setup('聀', "GL11.GL_LUMINANCE8");
      setup('聁', "GL11.GL_LUMINANCE12");
      setup('聂', "GL11.GL_LUMINANCE16");
      setup('聃', "GL11.GL_LUMINANCE4_ALPHA4");
      setup('聄', "GL11.GL_LUMINANCE6_ALPHA2");
      setup('聅', "GL11.GL_LUMINANCE8_ALPHA8");
      setup('聆', "GL11.GL_LUMINANCE12_ALPHA4");
      setup('聇', "GL11.GL_LUMINANCE12_ALPHA12");
      setup('聈', "GL11.GL_LUMINANCE16_ALPHA16");
      setup('聉', "GL11.GL_INTENSITY");
      setup('聊', "GL11.GL_INTENSITY4");
      setup('聋', "GL11.GL_INTENSITY8");
      setup('职', "GL11.GL_INTENSITY12");
      setup('聍', "GL11.GL_INTENSITY16");
      setup(10768, "GL11.GL_R3_G3_B2");
      setup('聏', "GL11.GL_RGB4");
      setup('聐', "GL11.GL_RGB5");
      setup('聑', "GL11.GL_RGB8");
      setup('聒', "GL11.GL_RGB10");
      setup('聓', "GL11.GL_RGB12");
      setup('联', "GL11.GL_RGB16");
      setup('聕', "GL11.GL_RGBA2");
      setup('聖', "GL11.GL_RGBA4");
      setup('聗', "GL11.GL_RGB5_A1");
      setup('聘', "GL11.GL_RGBA8");
      setup('聙', "GL11.GL_RGB10_A2");
      setup('聚', "GL11.GL_RGBA12");
      setup('聛', "GL11.GL_RGBA16");
      setup('聜', "GL11.GL_TEXTURE_RED_SIZE");
      setup('聝', "GL11.GL_TEXTURE_GREEN_SIZE");
      setup('聞', "GL11.GL_TEXTURE_BLUE_SIZE");
      setup('聟', "GL11.GL_TEXTURE_ALPHA_SIZE");
      setup('聠', "GL11.GL_TEXTURE_LUMINANCE_SIZE");
      setup('聡', "GL11.GL_TEXTURE_INTENSITY_SIZE");
      setup('聣', "GL11.GL_PROXY_TEXTURE_1D");
      setup('聤', "GL11.GL_PROXY_TEXTURE_2D");
      setup('聦', "GL11.GL_TEXTURE_PRIORITY");
      setup('聧', "GL11.GL_TEXTURE_RESIDENT");
      setup('聨', "GL11.GL_TEXTURE_BINDING_1D");
      setup('聩', "GL11.GL_TEXTURE_BINDING_2D");
      setup('聴', "GL11.GL_VERTEX_ARRAY");
      setup('聵', "GL11.GL_NORMAL_ARRAY");
      setup('聶', "GL11.GL_COLOR_ARRAY");
      setup('職', "GL11.GL_INDEX_ARRAY");
      setup('聸', "GL11.GL_TEXTURE_COORD_ARRAY");
      setup('聹', "GL11.GL_EDGE_FLAG_ARRAY");
      setup('聺', "GL11.GL_VERTEX_ARRAY_SIZE");
      setup('聻', "GL11.GL_VERTEX_ARRAY_TYPE");
      setup('聼', "GL11.GL_VERTEX_ARRAY_STRIDE");
      setup('聾', "GL11.GL_NORMAL_ARRAY_TYPE");
      setup('聿', "GL11.GL_NORMAL_ARRAY_STRIDE");
      setup('肁', "GL11.GL_COLOR_ARRAY_SIZE");
      setup('肂', "GL11.GL_COLOR_ARRAY_TYPE");
      setup('肃', "GL11.GL_COLOR_ARRAY_STRIDE");
      setup('肅', "GL11.GL_INDEX_ARRAY_TYPE");
      setup('肆', "GL11.GL_INDEX_ARRAY_STRIDE");
      setup('肈', "GL11.GL_TEXTURE_COORD_ARRAY_SIZE");
      setup('肉', "GL11.GL_TEXTURE_COORD_ARRAY_TYPE");
      setup('肊', "GL11.GL_TEXTURE_COORD_ARRAY_STRIDE");
      setup('肌', "GL11.GL_EDGE_FLAG_ARRAY_STRIDE");
      setup('肎', "GL11.GL_VERTEX_ARRAY_POINTER");
      setup('肏', "GL11.GL_NORMAL_ARRAY_POINTER");
      setup('肐', "GL11.GL_COLOR_ARRAY_POINTER");
      setup('肑', "GL11.GL_INDEX_ARRAY_POINTER");
      setup('肒', "GL11.GL_TEXTURE_COORD_ARRAY_POINTER");
      setup('肓', "GL11.GL_EDGE_FLAG_ARRAY_POINTER");
      setup(10784, "GL11.GL_V2F");
      setup(10785, "GL11.GL_V3F");
      setup(10786, "GL11.GL_C4UB_V2F");
      setup(10787, "GL11.GL_C4UB_V3F");
      setup(10788, "GL11.GL_C3F_V3F");
      setup(10789, "GL11.GL_N3F_V3F");
      setup(10790, "GL11.GL_C4F_N3F_V3F");
      setup(10791, "GL11.GL_T2F_V3F");
      setup(10792, "GL11.GL_T4F_V4F");
      setup(10793, "GL11.GL_T2F_C4UB_V3F");
      setup(10794, "GL11.GL_T2F_C3F_V3F");
      setup(10795, "GL11.GL_T2F_N3F_V3F");
      setup(10796, "GL11.GL_T2F_C4F_N3F_V3F");
      setup(10797, "GL11.GL_T4F_C4F_N3F_V4F");
      setup(3057, "GL11.GL_LOGIC_OP");
      setup(4099, "GL11.GL_TEXTURE_COMPONENTS");
      setup('聪', "GL12.GL_TEXTURE_BINDING_3D");
      setup('聫', "GL12.GL_PACK_SKIP_IMAGES");
      setup('聬', "GL12.GL_PACK_IMAGE_HEIGHT");
      setup('聭', "GL12.GL_UNPACK_SKIP_IMAGES");
      setup('聮', "GL12.GL_UNPACK_IMAGE_HEIGHT");
      setup('聯', "GL12.GL_TEXTURE_3D");
      setup('聰', "GL12.GL_PROXY_TEXTURE_3D");
      setup('聱', "GL12.GL_TEXTURE_DEPTH");
      setup('聲', "GL12.GL_TEXTURE_WRAP_R");
      setup('聳', "GL12.GL_MAX_3D_TEXTURE_SIZE");
      setup('胠', "GL12.GL_BGR");
      setup('胡', "GL12.GL_BGRA");
      setup('耲', "GL12.GL_UNSIGNED_BYTE_3_3_2");
      setup('荢', "GL12.GL_UNSIGNED_BYTE_2_3_3_REV");
      setup('荣', "GL12.GL_UNSIGNED_SHORT_5_6_5");
      setup('荤', "GL12.GL_UNSIGNED_SHORT_5_6_5_REV");
      setup('耳', "GL12.GL_UNSIGNED_SHORT_4_4_4_4");
      setup('荥', "GL12.GL_UNSIGNED_SHORT_4_4_4_4_REV");
      setup('耴', "GL12.GL_UNSIGNED_SHORT_5_5_5_1");
      setup('荦', "GL12.GL_UNSIGNED_SHORT_1_5_5_5_REV");
      setup('耵', "GL12.GL_UNSIGNED_INT_8_8_8_8");
      setup('荧', "GL12.GL_UNSIGNED_INT_8_8_8_8_REV");
      setup('耶', "GL12.GL_UNSIGNED_INT_10_10_10_2");
      setup('荨', "GL12.GL_UNSIGNED_INT_2_10_10_10_REV");
      setup('耺', "GL12.GL_RESCALE_NORMAL");
      setup('臸', "GL12.GL_LIGHT_MODEL_COLOR_CONTROL");
      setup('臹', "GL12.GL_SINGLE_COLOR");
      setup('臺', "GL12.GL_SEPARATE_SPECULAR_COLOR");
      setup('脯', "GL12.GL_CLAMP_TO_EDGE");
      setup('脺', "GL12.GL_TEXTURE_MIN_LOD");
      setup('脻', "GL12.GL_TEXTURE_MAX_LOD");
      setup('脼', "GL12.GL_TEXTURE_BASE_LEVEL");
      setup('脽', "GL12.GL_TEXTURE_MAX_LEVEL");
      setup('胨', "GL12.GL_MAX_ELEMENTS_VERTICES");
      setup('胩', "GL12.GL_MAX_ELEMENTS_INDICES");
      setup('葭', "GL12.GL_ALIASED_POINT_SIZE_RANGE");
      setup('葮', "GL12.GL_ALIASED_LINE_WIDTH_RANGE");
      setup('蓀', "GL13.GL_TEXTURE0");
      setup('蓁', "GL13.GL_TEXTURE1");
      setup('蓂', "GL13.GL_TEXTURE2");
      setup('蓃', "GL13.GL_TEXTURE3");
      setup('蓄', "GL13.GL_TEXTURE4");
      setup('蓅', "GL13.GL_TEXTURE5");
      setup('蓆', "GL13.GL_TEXTURE6");
      setup('蓇', "GL13.GL_TEXTURE7");
      setup('蓈', "GL13.GL_TEXTURE8");
      setup('蓉', "GL13.GL_TEXTURE9");
      setup('蓊', "GL13.GL_TEXTURE10");
      setup('蓋', "GL13.GL_TEXTURE11");
      setup('蓌', "GL13.GL_TEXTURE12");
      setup('蓍', "GL13.GL_TEXTURE13");
      setup('蓎', "GL13.GL_TEXTURE14");
      setup('蓏', "GL13.GL_TEXTURE15");
      setup('蓐', "GL13.GL_TEXTURE16");
      setup('蓑', "GL13.GL_TEXTURE17");
      setup('蓒', "GL13.GL_TEXTURE18");
      setup('蓓', "GL13.GL_TEXTURE19");
      setup('蓔', "GL13.GL_TEXTURE20");
      setup('蓕', "GL13.GL_TEXTURE21");
      setup('蓖', "GL13.GL_TEXTURE22");
      setup('蓗', "GL13.GL_TEXTURE23");
      setup('蓘', "GL13.GL_TEXTURE24");
      setup('蓙', "GL13.GL_TEXTURE25");
      setup('蓚', "GL13.GL_TEXTURE26");
      setup('蓛', "GL13.GL_TEXTURE27");
      setup('蓜', "GL13.GL_TEXTURE28");
      setup('蓝', "GL13.GL_TEXTURE29");
      setup('蓞', "GL13.GL_TEXTURE30");
      setup('蓟', "GL13.GL_TEXTURE31");
      setup('蓠', "GL13.GL_ACTIVE_TEXTURE");
      setup('蓡', "GL13.GL_CLIENT_ACTIVE_TEXTURE");
      setup('蓢', "GL13.GL_MAX_TEXTURE_UNITS");
      setup('蔑', "GL13.GL_NORMAL_MAP");
      setup('蔒', "GL13.GL_REFLECTION_MAP");
      setup('蔓', "GL13.GL_TEXTURE_CUBE_MAP");
      setup('蔔', "GL13.GL_TEXTURE_BINDING_CUBE_MAP");
      setup('蔕', "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X");
      setup('蔖', "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X");
      setup('蔗', "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y");
      setup('蔘', "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y");
      setup('蔙', "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z");
      setup('蔚', "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z");
      setup('蔛', "GL13.GL_PROXY_TEXTURE_CUBE_MAP");
      setup('蔜', "GL13.GL_MAX_CUBE_MAP_TEXTURE_SIZE");
      setup('蓩', "GL13.GL_COMPRESSED_ALPHA");
      setup('蓪', "GL13.GL_COMPRESSED_LUMINANCE");
      setup('蓫', "GL13.GL_COMPRESSED_LUMINANCE_ALPHA");
      setup('蓬', "GL13.GL_COMPRESSED_INTENSITY");
      setup('蓭', "GL13.GL_COMPRESSED_RGB");
      setup('蓮', "GL13.GL_COMPRESSED_RGBA");
      setup('蓯', "GL13.GL_TEXTURE_COMPRESSION_HINT");
      setup('蚠', "GL13.GL_TEXTURE_COMPRESSED_IMAGE_SIZE");
      setup('蚡', "GL13.GL_TEXTURE_COMPRESSED");
      setup('蚢', "GL13.GL_NUM_COMPRESSED_TEXTURE_FORMATS");
      setup('蚣', "GL13.GL_COMPRESSED_TEXTURE_FORMATS");
      setup('肝', "GL13.GL_MULTISAMPLE");
      setup('肞', "GL13.GL_SAMPLE_ALPHA_TO_COVERAGE");
      setup('肟', "GL13.GL_SAMPLE_ALPHA_TO_ONE");
      setup('肠', "GL13.GL_SAMPLE_COVERAGE");
      setup('肨', "GL13.GL_SAMPLE_BUFFERS");
      setup('肩', "GL13.GL_SAMPLES");
      setup('肪', "GL13.GL_SAMPLE_COVERAGE_VALUE");
      setup('肫', "GL13.GL_SAMPLE_COVERAGE_INVERT");
      setup('蓣', "GL13.GL_TRANSPOSE_MODELVIEW_MATRIX");
      setup('蓤', "GL13.GL_TRANSPOSE_PROJECTION_MATRIX");
      setup('蓥', "GL13.GL_TRANSPOSE_TEXTURE_MATRIX");
      setup('蓦', "GL13.GL_TRANSPOSE_COLOR_MATRIX");
      setup('蕰', "GL13.GL_COMBINE");
      setup('蕱', "GL13.GL_COMBINE_RGB");
      setup('蕲', "GL13.GL_COMBINE_ALPHA");
      setup('薀', "GL13.GL_SOURCE0_RGB");
      setup('薁', "GL13.GL_SOURCE1_RGB");
      setup('薂', "GL13.GL_SOURCE2_RGB");
      setup('薈', "GL13.GL_SOURCE0_ALPHA");
      setup('薉', "GL13.GL_SOURCE1_ALPHA");
      setup('薊', "GL13.GL_SOURCE2_ALPHA");
      setup('薐', "GL13.GL_OPERAND0_RGB");
      setup('薑', "GL13.GL_OPERAND1_RGB");
      setup('薒', "GL13.GL_OPERAND2_RGB");
      setup('薘', "GL13.GL_OPERAND0_ALPHA");
      setup('薙', "GL13.GL_OPERAND1_ALPHA");
      setup('薚', "GL13.GL_OPERAND2_ALPHA");
      setup('蕳', "GL13.GL_RGB_SCALE");
      setup('蕴', "GL13.GL_ADD_SIGNED");
      setup('蕵', "GL13.GL_INTERPOLATE");
      setup('蓧', "GL13.GL_SUBTRACT");
      setup('蕶', "GL13.GL_CONSTANT");
      setup('蕷', "GL13.GL_PRIMARY_COLOR");
      setup('蕸', "GL13.GL_PREVIOUS");
      setup('蚮', "GL13.GL_DOT3_RGB");
      setup('蚯', "GL13.GL_DOT3_RGBA");
      setup('脭', "GL13.GL_CLAMP_TO_BORDER");
      setup('膑', "GL14.GL_GENERATE_MIPMAP");
      setup('膒', "GL14.GL_GENERATE_MIPMAP_HINT");
      setup('膥', "GL14.GL_DEPTH_COMPONENT16");
      setup('膦', "GL14.GL_DEPTH_COMPONENT24");
      setup('膧', "GL14.GL_DEPTH_COMPONENT32");
      setup('衊', "GL14.GL_TEXTURE_DEPTH_SIZE");
      setup('衋', "GL14.GL_DEPTH_TEXTURE_MODE");
      setup('行', "GL14.GL_TEXTURE_COMPARE_MODE");
      setup('衍', "GL14.GL_TEXTURE_COMPARE_FUNC");
      setup('衎', "GL14.GL_COMPARE_R_TO_TEXTURE");
      setup('葐', "GL14.GL_FOG_COORDINATE_SOURCE");
      setup('葑', "GL14.GL_FOG_COORDINATE");
      setup('葒', "GL14.GL_FRAGMENT_DEPTH");
      setup('葓', "GL14.GL_CURRENT_FOG_COORDINATE");
      setup('葔', "GL14.GL_FOG_COORDINATE_ARRAY_TYPE");
      setup('葕', "GL14.GL_FOG_COORDINATE_ARRAY_STRIDE");
      setup('葖', "GL14.GL_FOG_COORDINATE_ARRAY_POINTER");
      setup('著', "GL14.GL_FOG_COORDINATE_ARRAY");
      setup('脦', "GL14.GL_POINT_SIZE_MIN");
      setup('脧', "GL14.GL_POINT_SIZE_MAX");
      setup('脨', "GL14.GL_POINT_FADE_THRESHOLD_SIZE");
      setup('脩', "GL14.GL_POINT_DISTANCE_ATTENUATION");
      setup('葘', "GL14.GL_COLOR_SUM");
      setup('葙', "GL14.GL_CURRENT_SECONDARY_COLOR");
      setup('葚', "GL14.GL_SECONDARY_COLOR_ARRAY_SIZE");
      setup('葛', "GL14.GL_SECONDARY_COLOR_ARRAY_TYPE");
      setup('葜', "GL14.GL_SECONDARY_COLOR_ARRAY_STRIDE");
      setup('葝', "GL14.GL_SECONDARY_COLOR_ARRAY_POINTER");
      setup('葞', "GL14.GL_SECONDARY_COLOR_ARRAY");
      setup('胈', "GL14.GL_BLEND_DST_RGB");
      setup('胉', "GL14.GL_BLEND_SRC_RGB");
      setup('胊', "GL14.GL_BLEND_DST_ALPHA");
      setup('胋', "GL14.GL_BLEND_SRC_ALPHA");
      setup('蔇', "GL14.GL_INCR_WRAP");
      setup('蔈', "GL14.GL_DECR_WRAP");
      setup('蔀', "GL14.GL_TEXTURE_FILTER_CONTROL");
      setup('蔁', "GL14.GL_TEXTURE_LOD_BIAS");
      setup('蓽', "GL14.GL_MAX_TEXTURE_LOD_BIAS");
      setup('荰', "GL14.GL_MIRRORED_REPEAT");
      setup('者', "ARBImaging.GL_BLEND_COLOR");
      setup('耉', "ARBImaging.GL_BLEND_EQUATION");
      setup('耆', "GL14.GL_FUNC_ADD");
      setup('耊', "GL14.GL_FUNC_SUBTRACT");
      setup('耋', "GL14.GL_FUNC_REVERSE_SUBTRACT");
      setup('耇', "GL14.GL_MIN");
      setup('耈', "GL14.GL_MAX");
      setup('袒', "GL15.GL_ARRAY_BUFFER");
      setup('袓', "GL15.GL_ELEMENT_ARRAY_BUFFER");
      setup('袔', "GL15.GL_ARRAY_BUFFER_BINDING");
      setup('袕', "GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING");
      setup('袖', "GL15.GL_VERTEX_ARRAY_BUFFER_BINDING");
      setup('袗', "GL15.GL_NORMAL_ARRAY_BUFFER_BINDING");
      setup('袘', "GL15.GL_COLOR_ARRAY_BUFFER_BINDING");
      setup('袙', "GL15.GL_INDEX_ARRAY_BUFFER_BINDING");
      setup('袚', "GL15.GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING");
      setup('袛', "GL15.GL_EDGE_FLAG_ARRAY_BUFFER_BINDING");
      setup('袜', "GL15.GL_SECONDARY_COLOR_ARRAY_BUFFER_BINDING");
      setup('袝', "GL15.GL_FOG_COORDINATE_ARRAY_BUFFER_BINDING");
      setup('袞', "GL15.GL_WEIGHT_ARRAY_BUFFER_BINDING");
      setup('袟', "GL15.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING");
      setup('裠', "GL15.GL_STREAM_DRAW");
      setup('裡', "GL15.GL_STREAM_READ");
      setup('裢', "GL15.GL_STREAM_COPY");
      setup('裤', "GL15.GL_STATIC_DRAW");
      setup('裥', "GL15.GL_STATIC_READ");
      setup('裦', "GL15.GL_STATIC_COPY");
      setup('裨', "GL15.GL_DYNAMIC_DRAW");
      setup('裩', "GL15.GL_DYNAMIC_READ");
      setup('裪', "GL15.GL_DYNAMIC_COPY");
      setup('袸', "GL15.GL_READ_ONLY");
      setup('袹', "GL15.GL_WRITE_ONLY");
      setup('袺', "GL15.GL_READ_WRITE");
      setup('蝤', "GL15.GL_BUFFER_SIZE");
      setup('蝥', "GL15.GL_BUFFER_USAGE");
      setup('袻', "GL15.GL_BUFFER_ACCESS");
      setup('袼', "GL15.GL_BUFFER_MAPPED");
      setup('袽', "GL15.GL_BUFFER_MAP_POINTER");
      setup('蕚', "NVFogDistance.GL_FOG_DISTANCE_MODE_NV");
      setup('蕛', "NVFogDistance.GL_EYE_RADIAL_NV");
      setup('蕜', "NVFogDistance.GL_EYE_PLANE_ABSOLUTE_NV");
   }
}
