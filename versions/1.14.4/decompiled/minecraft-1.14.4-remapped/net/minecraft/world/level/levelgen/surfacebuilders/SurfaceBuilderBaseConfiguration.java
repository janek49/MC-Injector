package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderConfiguration;

public class SurfaceBuilderBaseConfiguration implements SurfaceBuilderConfiguration {
   private final BlockState topMaterial;
   private final BlockState underMaterial;
   private final BlockState underwaterMaterial;

   public SurfaceBuilderBaseConfiguration(BlockState topMaterial, BlockState underMaterial, BlockState underwaterMaterial) {
      this.topMaterial = topMaterial;
      this.underMaterial = underMaterial;
      this.underwaterMaterial = underwaterMaterial;
   }

   public BlockState getTopMaterial() {
      return this.topMaterial;
   }

   public BlockState getUnderMaterial() {
      return this.underMaterial;
   }

   public BlockState getUnderwaterMaterial() {
      return this.underwaterMaterial;
   }

   public static SurfaceBuilderBaseConfiguration deserialize(Dynamic dynamic) {
      BlockState var1 = (BlockState)dynamic.get("top_material").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      BlockState var2 = (BlockState)dynamic.get("under_material").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      BlockState var3 = (BlockState)dynamic.get("underwater_material").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      return new SurfaceBuilderBaseConfiguration(var1, var2, var3);
   }
}
