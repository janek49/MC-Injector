package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.VillagerTradeItemLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.npc.Villager;

@ClientJarOnly
public class VillagerRenderer extends MobRenderer {
   private static final ResourceLocation VILLAGER_BASE_SKIN = new ResourceLocation("textures/entity/villager/villager.png");

   public VillagerRenderer(EntityRenderDispatcher entityRenderDispatcher, ReloadableResourceManager reloadableResourceManager) {
      super(entityRenderDispatcher, new VillagerModel(0.0F), 0.5F);
      this.addLayer(new CustomHeadLayer(this));
      this.addLayer(new VillagerProfessionLayer(this, reloadableResourceManager, "villager"));
      this.addLayer(new VillagerTradeItemLayer(this));
   }

   protected ResourceLocation getTextureLocation(Villager villager) {
      return VILLAGER_BASE_SKIN;
   }

   protected void scale(Villager villager, float var2) {
      float var3 = 0.9375F;
      if(villager.isBaby()) {
         var3 = (float)((double)var3 * 0.5D);
         this.shadowRadius = 0.25F;
      } else {
         this.shadowRadius = 0.5F;
      }

      GlStateManager.scalef(var3, var3, var3);
   }
}
