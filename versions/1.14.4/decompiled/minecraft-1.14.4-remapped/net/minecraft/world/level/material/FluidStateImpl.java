package net.minecraft.world.level.material;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class FluidStateImpl extends AbstractStateHolder implements FluidState {
   public FluidStateImpl(Fluid fluid, ImmutableMap immutableMap) {
      super(fluid, immutableMap);
   }

   public Fluid getType() {
      return (Fluid)this.owner;
   }
}
