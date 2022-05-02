package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.client.model.PufferfishBigModel;
import net.minecraft.client.model.PufferfishMidModel;
import net.minecraft.client.model.PufferfishSmallModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Pufferfish;

@ClientJarOnly
public class PufferfishRenderer extends MobRenderer {
   private static final ResourceLocation PUFFER_LOCATION = new ResourceLocation("textures/entity/fish/pufferfish.png");
   private int puffStateO = 3;
   private final PufferfishSmallModel small = new PufferfishSmallModel();
   private final PufferfishMidModel mid = new PufferfishMidModel();
   private final PufferfishBigModel big = new PufferfishBigModel();

   public PufferfishRenderer(EntityRenderDispatcher entityRenderDispatcher) {
      super(entityRenderDispatcher, new PufferfishBigModel(), 0.2F);
   }

   @Nullable
   protected ResourceLocation getTextureLocation(Pufferfish pufferfish) {
      return PUFFER_LOCATION;
   }

   public void render(Pufferfish pufferfish, double var2, double var4, double var6, float var8, float var9) {
      int var10 = pufferfish.getPuffState();
      if(var10 != this.puffStateO) {
         if(var10 == 0) {
            this.model = this.small;
         } else if(var10 == 1) {
            this.model = this.mid;
         } else {
            this.model = this.big;
         }
      }

      this.puffStateO = var10;
      this.shadowRadius = 0.1F + 0.1F * (float)var10;
      super.render((Mob)pufferfish, var2, var4, var6, var8, var9);
   }

   protected void setupRotations(Pufferfish pufferfish, float var2, float var3, float var4) {
      GlStateManager.translatef(0.0F, Mth.cos(var2 * 0.05F) * 0.08F, 0.0F);
      super.setupRotations(pufferfish, var2, var3, var4);
   }
}
