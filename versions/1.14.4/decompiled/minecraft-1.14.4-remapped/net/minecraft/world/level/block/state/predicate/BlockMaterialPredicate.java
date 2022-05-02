package net.minecraft.world.level.block.state.predicate;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class BlockMaterialPredicate implements Predicate {
   private static final BlockMaterialPredicate AIR = new BlockMaterialPredicate(Material.AIR, null) {
      public boolean test(@Nullable BlockState blockState) {
         return blockState != null && blockState.isAir();
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((BlockState)var1);
      }
   };
   private final Material material;

   private BlockMaterialPredicate(Material material) {
      this.material = material;
   }

   public static BlockMaterialPredicate forMaterial(Material material) {
      return material == Material.AIR?AIR:new BlockMaterialPredicate(material);
   }

   public boolean test(@Nullable BlockState blockState) {
      return blockState != null && blockState.getMaterial() == this.material;
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1) {
      return this.test((BlockState)var1);
   }
}
