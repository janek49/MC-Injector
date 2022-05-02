package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.ZombieVillagerModel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.VillagerProfessionLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.monster.ZombieVillager;

@ClientJarOnly
public class ZombieVillagerRenderer extends HumanoidMobRenderer {
   private static final ResourceLocation ZOMBIE_VILLAGER_LOCATION = new ResourceLocation("textures/entity/zombie_villager/zombie_villager.png");

   public ZombieVillagerRenderer(EntityRenderDispatcher entityRenderDispatcher, ReloadableResourceManager reloadableResourceManager) {
      super(entityRenderDispatcher, new ZombieVillagerModel(), 0.5F);
      this.addLayer(new HumanoidArmorLayer(this, new ZombieVillagerModel(0.5F, true), new ZombieVillagerModel(1.0F, true)));
      this.addLayer(new VillagerProfessionLayer(this, reloadableResourceManager, "zombie_villager"));
   }

   protected ResourceLocation getTextureLocation(ZombieVillager zombieVillager) {
      return ZOMBIE_VILLAGER_LOCATION;
   }

   protected void setupRotations(ZombieVillager zombieVillager, float var2, float var3, float var4) {
      if(zombieVillager.isConverting()) {
         var3 += (float)(Math.cos((double)zombieVillager.tickCount * 3.25D) * 3.141592653589793D * 0.25D);
      }

      super.setupRotations(zombieVillager, var2, var3, var4);
   }
}
