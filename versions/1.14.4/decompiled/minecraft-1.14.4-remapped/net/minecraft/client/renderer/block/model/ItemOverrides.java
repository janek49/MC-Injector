package net.minecraft.client.renderer.block.model;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@ClientJarOnly
public class ItemOverrides {
   public static final ItemOverrides EMPTY = new ItemOverrides();
   private final List overrides = Lists.newArrayList();
   private final List overrideModels;

   private ItemOverrides() {
      this.overrideModels = Collections.emptyList();
   }

   public ItemOverrides(ModelBakery modelBakery, BlockModel blockModel, Function function, List list) {
      this.overrideModels = (List)list.stream().map((itemOverride) -> {
         UnbakedModel var4 = (UnbakedModel)function.apply(itemOverride.getModel());
         return Objects.equals(var4, blockModel)?null:modelBakery.bake(itemOverride.getModel(), BlockModelRotation.X0_Y0);
      }).collect(Collectors.toList());
      Collections.reverse(this.overrideModels);

      for(int var5 = list.size() - 1; var5 >= 0; --var5) {
         this.overrides.add(list.get(var5));
      }

   }

   @Nullable
   public BakedModel resolve(BakedModel var1, ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity livingEntity) {
      if(!this.overrides.isEmpty()) {
         for(int var5 = 0; var5 < this.overrides.size(); ++var5) {
            ItemOverride var6 = (ItemOverride)this.overrides.get(var5);
            if(var6.test(itemStack, level, livingEntity)) {
               BakedModel var7 = (BakedModel)this.overrideModels.get(var5);
               if(var7 == null) {
                  return var1;
               }

               return var7;
            }
         }
      }

      return var1;
   }
}
