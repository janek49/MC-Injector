package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Arrays;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@ClientJarOnly
public class BreakingQuad extends BakedQuad {
   private final TextureAtlasSprite breakingIcon;

   public BreakingQuad(BakedQuad bakedQuad, TextureAtlasSprite breakingIcon) {
      super(Arrays.copyOf(bakedQuad.getVertices(), bakedQuad.getVertices().length), bakedQuad.tintIndex, FaceBakery.calculateFacing(bakedQuad.getVertices()), bakedQuad.getSprite());
      this.breakingIcon = breakingIcon;
      this.calculateBreakingUVs();
   }

   private void calculateBreakingUVs() {
      for(int var1 = 0; var1 < 4; ++var1) {
         int var2 = 7 * var1;
         this.vertices[var2 + 4] = Float.floatToRawIntBits(this.breakingIcon.getU((double)this.sprite.getUOffset(Float.intBitsToFloat(this.vertices[var2 + 4]))));
         this.vertices[var2 + 4 + 1] = Float.floatToRawIntBits(this.breakingIcon.getV((double)this.sprite.getVOffset(Float.intBitsToFloat(this.vertices[var2 + 4 + 1]))));
      }

   }
}
