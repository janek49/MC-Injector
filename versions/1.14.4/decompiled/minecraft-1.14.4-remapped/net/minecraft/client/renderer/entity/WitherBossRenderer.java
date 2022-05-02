package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.WitherBossModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.WitherArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.boss.wither.WitherBoss;

@ClientJarOnly
public class WitherBossRenderer extends MobRenderer {
   private static final ResourceLocation WITHER_INVULNERABLE_LOCATION = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation WITHER_LOCATION = new ResourceLocation("textures/entity/wither/wither.png");

   public WitherBossRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new WitherBossModel(0.0F), 1.0F);
      this.addLayer(new WitherArmorLayer(this));
   }

   protected ResourceLocation getTextureLocation(WitherBoss witherBoss) {
      int var2 = witherBoss.getInvulnerableTicks();
      return var2 > 0 && (var2 > 80 || var2 / 5 % 2 != 1)?WITHER_INVULNERABLE_LOCATION:WITHER_LOCATION;
   }

   protected void scale(WitherBoss witherBoss, float var2) {
      float var3 = 2.0F;
      int var4 = witherBoss.getInvulnerableTicks();
      if(var4 > 0) {
         var3 -= ((float)var4 - var2) / 220.0F * 0.5F;
      }

      GlStateManager.scalef(var3, var3, var3);
   }
}
