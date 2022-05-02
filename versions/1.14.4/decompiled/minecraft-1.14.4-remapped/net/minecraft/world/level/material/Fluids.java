package net.minecraft.world.level.material;

import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.core.Registry;
import net.minecraft.world.level.material.EmptyFluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import net.minecraft.world.level.material.WaterFluid;

public class Fluids {
   public static final Fluid EMPTY = register("empty", new EmptyFluid());
   public static final FlowingFluid FLOWING_WATER = (FlowingFluid)register("flowing_water", new WaterFluid.Flowing());
   public static final FlowingFluid WATER = (FlowingFluid)register("water", new WaterFluid.Source());
   public static final FlowingFluid FLOWING_LAVA = (FlowingFluid)register("flowing_lava", new LavaFluid.Flowing());
   public static final FlowingFluid LAVA = (FlowingFluid)register("lava", new LavaFluid.Source());

   private static Fluid register(String string, Fluid var1) {
      return (Fluid)Registry.register(Registry.FLUID, (String)string, var1);
   }

   static {
      for(Fluid var1 : Registry.FLUID) {
         UnmodifiableIterator var2 = var1.getStateDefinition().getPossibleStates().iterator();

         while(var2.hasNext()) {
            FluidState var3 = (FluidState)var2.next();
            Fluid.FLUID_STATE_REGISTRY.add(var3);
         }
      }

   }
}
