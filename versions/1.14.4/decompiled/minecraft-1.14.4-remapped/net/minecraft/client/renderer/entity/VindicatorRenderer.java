package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.IllagerRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vindicator;

@ClientJarOnly
public class VindicatorRenderer extends IllagerRenderer {
   private static final ResourceLocation VINDICATOR = new ResourceLocation("textures/entity/illager/vindicator.png");

   public VindicatorRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new IllagerModel(0.0F, 0.0F, 64, 64), 0.5F);
      this.addLayer(new ItemInHandLayer(this) {
         public void render(Vindicator vindicator, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
            if(vindicator.isAggressive()) {
               super.render((LivingEntity)vindicator, var2, var3, var4, var5, var6, var7, var8);
            }

         }
      });
   }

   protected ResourceLocation getTextureLocation(Vindicator vindicator) {
      return VINDICATOR;
   }
}
