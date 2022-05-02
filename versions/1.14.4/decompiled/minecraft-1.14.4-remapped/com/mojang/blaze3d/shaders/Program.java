package com.mojang.blaze3d.shaders;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.shaders.Effect;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

@ClientJarOnly
public class Program {
   private final Program.Type type;
   private final String name;
   private final int id;
   private int references;

   private Program(Program.Type type, int id, String name) {
      this.type = type;
      this.id = id;
      this.name = name;
   }

   public void attachToEffect(Effect effect) {
      ++this.references;
      GLX.glAttachShader(effect.getId(), this.id);
   }

   public void close() {
      --this.references;
      if(this.references <= 0) {
         GLX.glDeleteShader(this.id);
         this.type.getPrograms().remove(this.name);
      }

   }

   public String getName() {
      return this.name;
   }

   public static Program compileShader(Program.Type program$Type, String string, InputStream inputStream) throws IOException {
      String string = TextureUtil.readResourceAsString(inputStream);
      if(string == null) {
         throw new IOException("Could not load program " + program$Type.getName());
      } else {
         int var4 = GLX.glCreateShader(program$Type.getGlType());
         GLX.glShaderSource(var4, string);
         GLX.glCompileShader(var4);
         if(GLX.glGetShaderi(var4, GLX.GL_COMPILE_STATUS) == 0) {
            String var5 = StringUtils.trim(GLX.glGetShaderInfoLog(var4, '耀'));
            throw new IOException("Couldn\'t compile " + program$Type.getName() + " program: " + var5);
         } else {
            Program var5 = new Program(program$Type, var4, string);
            program$Type.getPrograms().put(string, var5);
            return var5;
         }
      }
   }

   @ClientJarOnly
   public static enum Type {
      VERTEX("vertex", ".vsh", GLX.GL_VERTEX_SHADER),
      FRAGMENT("fragment", ".fsh", GLX.GL_FRAGMENT_SHADER);

      private final String name;
      private final String extension;
      private final int glType;
      private final Map programs = Maps.newHashMap();

      private Type(String name, String extension, int glType) {
         this.name = name;
         this.extension = extension;
         this.glType = glType;
      }

      public String getName() {
         return this.name;
      }

      public String getExtension() {
         return this.extension;
      }

      private int getGlType() {
         return this.glType;
      }

      public Map getPrograms() {
         return this.programs;
      }
   }
}
