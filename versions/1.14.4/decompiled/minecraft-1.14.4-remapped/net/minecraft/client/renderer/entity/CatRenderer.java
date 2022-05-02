package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.CatModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

@ClientJarOnly
public class CatRenderer extends MobRenderer {
   public CatRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new CatModel(0.0F), 0.4F);
      this.addLayer(new CatCollarLayer(this));
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Cat cat) {
      return cat.getResourceLocation();
   }

   protected void scale(Cat cat, float var2) {
      super.scale(cat, var2);
      GlStateManager.scalef(0.8F, 0.8F, 0.8F);
   }

   protected void setupRotations(Cat cat, float var2, float var3, float var4) {
      super.setupRotations(cat, var2, var3, var4);
      float var5 = cat.getLieDownAmount(var4);
      if(var5 > 0.0F) {
         GlStateManager.translatef(0.4F * var5, 0.15F * var5, 0.1F * var5);
         GlStateManager.rotatef(Mth.rotLerp(var5, 0.0F, 90.0F), 0.0F, 0.0F, 1.0F);
         BlockPos var6 = new BlockPos(cat);

         for(Player var9 : cat.level.getEntitiesOfClass(Player.class, (new AABB(var6)).inflate(2.0D, 2.0D, 2.0D))) {
            if(var9.isSleeping()) {
               GlStateManager.translatef(0.15F * var5, 0.0F, 0.0F);
               break;
            }
         }
      }

   }
}
