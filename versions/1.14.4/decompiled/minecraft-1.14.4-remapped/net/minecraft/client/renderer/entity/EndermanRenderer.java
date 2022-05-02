package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Random;
import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CarriedBlockLayer;
import net.minecraft.client.renderer.entity.layers.EnderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class EndermanRenderer extends MobRenderer {
   private static final ResourceLocation ENDERMAN_LOCATION = new ResourceLocation("textures/entity/enderman/enderman.png");
   private final Random random = new Random();

   public EndermanRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new EndermanModel(0.0F), 0.5F);
      this.addLayer(new EnderEyesLayer(this));
      this.addLayer(new CarriedBlockLayer(this));
   }

   public void render(EnderMan enderMan, double var2, double var4, double var6, float var8, float var9) {
      BlockState var10 = enderMan.getCarriedBlock();
      EndermanModel<EnderMan> var11 = (EndermanModel)this.getModel();
      var11.carrying = var10 != null;
      var11.creepy = enderMan.isCreepy();
      if(enderMan.isCreepy()) {
         double var12 = 0.02D;
         var2 += this.random.nextGaussian() * 0.02D;
         var6 += this.random.nextGaussian() * 0.02D;
      }

      super.render((Mob)enderMan, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation getTextureLocation(EnderMan enderMan) {
      return ENDERMAN_LOCATION;
   }
}
