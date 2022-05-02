package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.VillagerHeadModel;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.metadata.animation.VillagerMetaDataSection;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;

@ClientJarOnly
public class VillagerProfessionLayer extends RenderLayer implements ResourceManagerReloadListener {
   private static final Int2ObjectMap LEVEL_LOCATIONS = (Int2ObjectMap)Util.make(new Int2ObjectOpenHashMap(), (int2ObjectOpenHashMap) -> {
      int2ObjectOpenHashMap.put(1, new ResourceLocation("stone"));
      int2ObjectOpenHashMap.put(2, new ResourceLocation("iron"));
      int2ObjectOpenHashMap.put(3, new ResourceLocation("gold"));
      int2ObjectOpenHashMap.put(4, new ResourceLocation("emerald"));
      int2ObjectOpenHashMap.put(5, new ResourceLocation("diamond"));
   });
   private final Object2ObjectMap typeHatCache = new Object2ObjectOpenHashMap();
   private final Object2ObjectMap professionHatCache = new Object2ObjectOpenHashMap();
   private final ReloadableResourceManager resourceManager;
   private final String path;

   public VillagerProfessionLayer(RenderLayerParent renderLayerParent, ReloadableResourceManager resourceManager, String path) {
      super(renderLayerParent);
      this.resourceManager = resourceManager;
      this.path = path;
      resourceManager.registerReloadListener(this);
   }

   public void render(LivingEntity livingEntity, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      if(!livingEntity.isInvisible()) {
         VillagerData var9 = ((VillagerDataHolder)livingEntity).getVillagerData();
         VillagerType var10 = var9.getType();
         VillagerProfession var11 = var9.getProfession();
         VillagerMetaDataSection.Hat var12 = this.getHatData(this.typeHatCache, "type", Registry.VILLAGER_TYPE, var10);
         VillagerMetaDataSection.Hat var13 = this.getHatData(this.professionHatCache, "profession", Registry.VILLAGER_PROFESSION, var11);
         M var14 = this.getParentModel();
         this.bindTexture(this.getResourceLocation("type", Registry.VILLAGER_TYPE.getKey(var10)));
         ((VillagerHeadModel)var14).hatVisible(var13 == VillagerMetaDataSection.Hat.NONE || var13 == VillagerMetaDataSection.Hat.PARTIAL && var12 != VillagerMetaDataSection.Hat.FULL);
         var14.render(livingEntity, var2, var3, var5, var6, var7, var8);
         ((VillagerHeadModel)var14).hatVisible(true);
         if(var11 != VillagerProfession.NONE && !livingEntity.isBaby()) {
            this.bindTexture(this.getResourceLocation("profession", Registry.VILLAGER_PROFESSION.getKey(var11)));
            var14.render(livingEntity, var2, var3, var5, var6, var7, var8);
            this.bindTexture(this.getResourceLocation("profession_level", (ResourceLocation)LEVEL_LOCATIONS.get(Mth.clamp(var9.getLevel(), 1, LEVEL_LOCATIONS.size()))));
            var14.render(livingEntity, var2, var3, var5, var6, var7, var8);
         }

      }
   }

   public boolean colorsOnDamage() {
      return true;
   }

   private ResourceLocation getResourceLocation(String string, ResourceLocation var2) {
      return new ResourceLocation(var2.getNamespace(), "textures/entity/" + this.path + "/" + string + "/" + var2.getPath() + ".png");
   }

   public VillagerMetaDataSection.Hat getHatData(Object2ObjectMap object2ObjectMap, String string, DefaultedRegistry defaultedRegistry, Object object) {
      return (VillagerMetaDataSection.Hat)object2ObjectMap.computeIfAbsent(object, (var4) -> {
         try {
            Resource var5 = this.resourceManager.getResource(this.getResourceLocation(string, defaultedRegistry.getKey(object)));
            Throwable var6 = null;

            VillagerMetaDataSection.Hat var8;
            try {
               VillagerMetaDataSection var7 = (VillagerMetaDataSection)var5.getMetadata(VillagerMetaDataSection.SERIALIZER);
               if(var7 == null) {
                  return VillagerMetaDataSection.Hat.NONE;
               }

               var8 = var7.getHat();
            } catch (Throwable var19) {
               var6 = var19;
               throw var19;
            } finally {
               if(var5 != null) {
                  if(var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var18) {
                        var6.addSuppressed(var18);
                     }
                  } else {
                     var5.close();
                  }
               }

            }

            return var8;
         } catch (IOException var21) {
            return VillagerMetaDataSection.Hat.NONE;
         }
      });
   }

   public void onResourceManagerReload(ResourceManager resourceManager) {
      this.professionHatCache.clear();
      this.typeHatCache.clear();
   }
}
