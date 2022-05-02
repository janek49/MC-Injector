package net.minecraft.client.renderer.block.model.multipart;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

@ClientJarOnly
public class AndCondition implements Condition {
   private final Iterable conditions;

   public AndCondition(Iterable conditions) {
      this.conditions = conditions;
   }

   public Predicate getPredicate(StateDefinition stateDefinition) {
      List<Predicate<BlockState>> var2 = (List)Streams.stream(this.conditions).map((condition) -> {
         return condition.getPredicate(stateDefinition);
      }).collect(Collectors.toList());
      return (blockState) -> {
         return var2.stream().allMatch((predicate) -> {
            return predicate.test(blockState);
         });
      };
   }
}
