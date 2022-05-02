package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;

public class SpikeConfiguration implements FeatureConfiguration {
   private final boolean crystalInvulnerable;
   private final List spikes;
   @Nullable
   private final BlockPos crystalBeamTarget;

   public SpikeConfiguration(boolean crystalInvulnerable, List spikes, @Nullable BlockPos crystalBeamTarget) {
      this.crystalInvulnerable = crystalInvulnerable;
      this.spikes = spikes;
      this.crystalBeamTarget = crystalBeamTarget;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      // $FF: Couldn't be decompiled
   }

   public static SpikeConfiguration deserialize(Dynamic dynamic) {
      List<SpikeFeature.EndSpike> var1 = dynamic.get("spikes").asList(SpikeFeature.EndSpike::deserialize);
      List<Integer> var2 = dynamic.get("crystalBeamTarget").asList((dynamic) -> {
         return Integer.valueOf(dynamic.asInt(0));
      });
      BlockPos var3;
      if(var2.size() == 3) {
         var3 = new BlockPos(((Integer)var2.get(0)).intValue(), ((Integer)var2.get(1)).intValue(), ((Integer)var2.get(2)).intValue());
      } else {
         var3 = null;
      }

      return new SpikeConfiguration(dynamic.get("crystalInvulnerable").asBoolean(false), var1, var3);
   }

   public boolean isCrystalInvulnerable() {
      return this.crystalInvulnerable;
   }

   public List getSpikes() {
      return this.spikes;
   }

   @Nullable
   public BlockPos getCrystalBeamTarget() {
      return this.crystalBeamTarget;
   }
}
