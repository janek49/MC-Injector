package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.util.Mth;

@ClientJarOnly
public class PanoramaRenderer {
   private final Minecraft minecraft;
   private final CubeMap cubeMap;
   private float time;

   public PanoramaRenderer(CubeMap cubeMap) {
      this.cubeMap = cubeMap;
      this.minecraft = Minecraft.getInstance();
   }

   public void render(float var1, float var2) {
      this.time += var1;
      this.cubeMap.render(this.minecraft, Mth.sin(this.time * 0.001F) * 5.0F + 25.0F, -this.time * 0.1F, var2);
      this.minecraft.window.setupGuiState(Minecraft.ON_OSX);
   }
}
