package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class MonsterRoomPlacementConfiguration implements DecoratorConfiguration {
   public final int chance;

   public MonsterRoomPlacementConfiguration(int chance) {
      this.chance = chance;
   }

   public Dynamic serialize(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("chance"), dynamicOps.createInt(this.chance))));
   }

   public static MonsterRoomPlacementConfiguration deserialize(Dynamic dynamic) {
      int var1 = dynamic.get("chance").asInt(0);
      return new MonsterRoomPlacementConfiguration(var1);
   }
}
