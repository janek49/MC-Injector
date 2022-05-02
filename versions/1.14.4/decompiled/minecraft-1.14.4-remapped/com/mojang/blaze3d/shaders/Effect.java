package com.mojang.blaze3d.shaders;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.shaders.Program;

@ClientJarOnly
public interface Effect {
   int getId();

   void markDirty();

   Program getVertexProgram();

   Program getFragmentProgram();
}
