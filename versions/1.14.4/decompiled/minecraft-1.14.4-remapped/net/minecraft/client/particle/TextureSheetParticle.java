package net.minecraft.client.particle;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.Level;

@ClientJarOnly
public abstract class TextureSheetParticle extends SingleQuadParticle {
   protected TextureAtlasSprite sprite;

   protected TextureSheetParticle(Level level, double var2, double var4, double var6) {
      super(level, var2, var4, var6);
   }

   protected TextureSheetParticle(Level level, double var2, double var4, double var6, double var8, double var10, double var12) {
      super(level, var2, var4, var6, var8, var10, var12);
   }

   protected void setSprite(TextureAtlasSprite sprite) {
      this.sprite = sprite;
   }

   protected float getU0() {
      return this.sprite.getU0();
   }

   protected float getU1() {
      return this.sprite.getU1();
   }

   protected float getV0() {
      return this.sprite.getV0();
   }

   protected float getV1() {
      return this.sprite.getV1();
   }

   public void pickSprite(SpriteSet spriteSet) {
      this.setSprite(spriteSet.get(this.random));
   }

   public void setSpriteFromAge(SpriteSet spriteFromAge) {
      this.setSprite(spriteFromAge.get(this.age, this.lifetime));
   }
}
