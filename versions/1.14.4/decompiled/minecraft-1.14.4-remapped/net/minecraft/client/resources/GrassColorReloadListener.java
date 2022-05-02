package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import java.io.IOException;
import net.minecraft.client.resources.LegacyStuffWrapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.GrassColor;

@ClientJarOnly
public class GrassColorReloadListener extends SimplePreparableReloadListener {
   private static final ResourceLocation LOCATION = new ResourceLocation("textures/colormap/grass.png");

   protected int[] prepare(ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      try {
         return LegacyStuffWrapper.getPixels(resourceManager, LOCATION);
      } catch (IOException var4) {
         throw new IllegalStateException("Failed to load grass color texture", var4);
      }
   }

   protected void apply(int[] ints, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      GrassColor.init(ints);
   }

   // $FF: synthetic method
   protected Object prepare(ResourceManager var1, ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }
}
