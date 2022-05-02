package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import javax.annotation.Nullable;
import net.minecraft.client.model.TurtleModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Turtle;

@ClientJarOnly
public class TurtleRenderer extends MobRenderer {
   private static final ResourceLocation TURTLE_LOCATION = new ResourceLocation("textures/entity/turtle/big_sea_turtle.png");

   public TurtleRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new TurtleModel(0.0F), 0.7F);
   }

   public void render(Turtle turtle, double var2, double var4, double var6, float var8, float var9) {
      if(turtle.isBaby()) {
         this.shadowRadius *= 0.5F;
      }

      super.render((Mob)turtle, var2, var4, var6, var8, var9);
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Turtle turtle) {
      return TURTLE_LOCATION;
   }
}
