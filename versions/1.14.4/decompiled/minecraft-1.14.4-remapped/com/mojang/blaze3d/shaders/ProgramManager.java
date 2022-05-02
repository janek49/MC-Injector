package com.mojang.blaze3d.shaders;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.shaders.Effect;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class ProgramManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static ProgramManager instance;

   public static void createInstance() {
      instance = new ProgramManager();
   }

   public static ProgramManager getInstance() {
      return instance;
   }

   public void releaseProgram(Effect effect) {
      effect.getFragmentProgram().close();
      effect.getVertexProgram().close();
      GLX.glDeleteProgram(effect.getId());
   }

   public int createProgram() throws IOException {
      int var1 = GLX.glCreateProgram();
      if(var1 <= 0) {
         throw new IOException("Could not create shader program (returned program ID " + var1 + ")");
      } else {
         return var1;
      }
   }

   public void linkProgram(Effect effect) throws IOException {
      effect.getFragmentProgram().attachToEffect(effect);
      effect.getVertexProgram().attachToEffect(effect);
      GLX.glLinkProgram(effect.getId());
      int var2 = GLX.glGetProgrami(effect.getId(), GLX.GL_LINK_STATUS);
      if(var2 == 0) {
         LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", effect.getVertexProgram().getName(), effect.getFragmentProgram().getName());
         LOGGER.warn(GLX.glGetProgramInfoLog(effect.getId(), '耀'));
      }

   }
}
