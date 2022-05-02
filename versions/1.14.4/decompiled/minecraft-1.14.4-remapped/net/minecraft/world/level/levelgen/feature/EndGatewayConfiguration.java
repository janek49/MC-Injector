package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;

public class EndGatewayConfiguration implements FeatureConfiguration {
   private final Optional exit;
   private final boolean exact;

   private EndGatewayConfiguration(Optional exit, boolean exact) {
      this.exit = exit;
      this.exact = exact;
   }

   public static EndGatewayConfiguration knownExit(BlockPos blockPos, boolean var1) {
      return new EndGatewayConfiguration(Optional.of(blockPos), var1);
   }

   public static EndGatewayConfiguration delayedExitSearch() {
      return new EndGatewayConfiguration(Optional.empty(), false);
   }

   public Optional getExit() {
      return this.exit;
   }

   public boolean isExitExact() {
      return this.exact;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, this.exit.map((blockPos) -> {
         return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("exit_x"), dynamicOps.createInt(blockPos.getX()), dynamicOps.createString("exit_y"), dynamicOps.createInt(blockPos.getY()), dynamicOps.createString("exit_z"), dynamicOps.createInt(blockPos.getZ()), dynamicOps.createString("exact"), dynamicOps.createBoolean(this.exact)));
      }).orElse(dynamicOps.emptyMap()));
   }

   public static EndGatewayConfiguration deserialize(Dynamic dynamic) {
      Optional<BlockPos> var1 = dynamic.get("exit_x").asNumber().flatMap((number) -> {
         return dynamic.get("exit_y").asNumber().flatMap((var2) -> {
            return dynamic.get("exit_z").asNumber().map((var2x) -> {
               return new BlockPos(number.intValue(), var2.intValue(), var2x.intValue());
            });
         });
      });
      boolean var2 = dynamic.get("exact").asBoolean(false);
      return new EndGatewayConfiguration(var1, var2);
   }
}
