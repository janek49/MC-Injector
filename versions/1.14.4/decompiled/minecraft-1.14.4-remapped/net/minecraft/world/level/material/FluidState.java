package net.minecraft.world.level.material;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface FluidState extends StateHolder {
   Fluid getType();

   default boolean isSource() {
      return this.getType().isSource(this);
   }

   default boolean isEmpty() {
      return this.getType().isEmpty();
   }

   default float getHeight(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getType().getHeight(this, blockGetter, blockPos);
   }

   default float getOwnHeight() {
      return this.getType().getOwnHeight(this);
   }

   default int getAmount() {
      return this.getType().getAmount(this);
   }

   default boolean shouldRenderBackwardUpFace(BlockGetter blockGetter, BlockPos blockPos) {
      for(int var3 = -1; var3 <= 1; ++var3) {
         for(int var4 = -1; var4 <= 1; ++var4) {
            BlockPos var5 = blockPos.offset(var3, 0, var4);
            FluidState var6 = blockGetter.getFluidState(var5);
            if(!var6.getType().isSame(this.getType()) && !blockGetter.getBlockState(var5).isSolidRender(blockGetter, var5)) {
               return true;
            }
         }
      }

      return false;
   }

   default void tick(Level level, BlockPos blockPos) {
      this.getType().tick(level, blockPos, this);
   }

   default void animateTick(Level level, BlockPos blockPos, Random random) {
      this.getType().animateTick(level, blockPos, this, random);
   }

   default boolean isRandomlyTicking() {
      return this.getType().isRandomlyTicking();
   }

   default void randomTick(Level level, BlockPos blockPos, Random random) {
      this.getType().randomTick(level, blockPos, this, random);
   }

   default Vec3 getFlow(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getType().getFlow(blockGetter, blockPos, this);
   }

   default BlockState createLegacyBlock() {
      return this.getType().createLegacyBlock(this);
   }

   @Nullable
   default ParticleOptions getDripParticle() {
      return this.getType().getDripParticle();
   }

   default BlockLayer getRenderLayer() {
      return this.getType().getRenderLayer();
   }

   default boolean is(Tag tag) {
      return this.getType().is(tag);
   }

   default float getExplosionResistance() {
      return this.getType().getExplosionResistance();
   }

   default boolean canBeReplacedWith(BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
      return this.getType().canBeReplacedWith(this, blockGetter, blockPos, fluid, direction);
   }

   static default Dynamic serialize(DynamicOps dynamicOps, FluidState fluidState) {
      ImmutableMap<Property<?>, Comparable<?>> var2 = fluidState.getValues();
      T var3;
      if(var2.isEmpty()) {
         var3 = dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.FLUID.getKey(fluidState.getType()).toString())));
      } else {
         var3 = dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.FLUID.getKey(fluidState.getType()).toString()), dynamicOps.createString("Properties"), dynamicOps.createMap((Map)var2.entrySet().stream().map((map$Entry) -> {
            return Pair.of(dynamicOps.createString(((Property)map$Entry.getKey()).getName()), dynamicOps.createString(StateHolder.getName((Property)map$Entry.getKey(), (Comparable)map$Entry.getValue())));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
      }

      return new Dynamic(dynamicOps, var3);
   }

   static default FluidState deserialize(Dynamic dynamic) {
      DefaultedRegistry var10000 = Registry.FLUID;
      Optional var10003 = dynamic.getElement("Name");
      DynamicOps var10004 = dynamic.getOps();
      var10004.getClass();
      Fluid var1 = (Fluid)var10000.get(new ResourceLocation((String)var10003.flatMap(var10004::getStringValue).orElse("minecraft:empty")));
      Map<String, String> var2 = dynamic.get("Properties").asMap((dynamic) -> {
         return dynamic.asString("");
      }, (dynamic) -> {
         return dynamic.asString("");
      });
      FluidState var3 = var1.defaultFluidState();
      StateDefinition<Fluid, FluidState> var4 = var1.getStateDefinition();

      for(Entry<String, String> var6 : var2.entrySet()) {
         String var7 = (String)var6.getKey();
         Property<?> var8 = var4.getProperty(var7);
         if(var8 != null) {
            var3 = (FluidState)StateHolder.setValueHelper(var3, var8, var7, dynamic.toString(), (String)var6.getValue());
         }
      }

      return var3;
   }

   default VoxelShape getShape(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getType().getShape(this, blockGetter, blockPos);
   }
}
