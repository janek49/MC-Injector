package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.NativeImage;

@ClientJarOnly
public interface HttpTextureProcessor {
   NativeImage process(NativeImage var1);

   void onTextureDownloaded();
}
