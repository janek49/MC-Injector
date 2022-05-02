package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.ai.behavior.GateBehavior;

public class RunOne extends GateBehavior {
   public RunOne(List list) {
      this(ImmutableMap.of(), list);
   }

   public RunOne(Map map, List list) {
      super(map, ImmutableSet.of(), GateBehavior.OrderPolicy.SHUFFLED, GateBehavior.RunningPolicy.RUN_ONE, list);
   }
}
