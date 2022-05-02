package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

@ClientJarOnly
public class BakedQuad {
   protected final int[] vertices;
   protected final int tintIndex;
   protected final Direction direction;
   protected final TextureAtlasSprite sprite;

   public BakedQuad(int[] vertices, int tintIndex, Direction direction, TextureAtlasSprite sprite) {
      this.vertices = vertices;
      this.tintIndex = tintIndex;
      this.direction = direction;
      this.sprite = sprite;
   }

   public TextureAtlasSprite getSprite() {
      return this.sprite;
   }

   public int[] getVertices() {
      return this.vertices;
   }

   public boolean isTinted() {
      return this.tintIndex != -1;
   }

   public int getTintIndex() {
      return this.tintIndex;
   }

   public Direction getDirection() {
      return this.direction;
   }
}
