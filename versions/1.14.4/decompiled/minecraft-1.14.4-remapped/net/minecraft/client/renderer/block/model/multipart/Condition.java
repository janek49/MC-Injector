package net.minecraft.client.renderer.block.model.multipart;

import com.fox2code.repacker.ClientJarOnly;
import java.util.function.Predicate;
import net.minecraft.world.level.block.state.StateDefinition;

@FunctionalInterface
@ClientJarOnly
public interface Condition {
   Condition TRUE = (stateDefinition) -> {
      return (blockState) -> {
         return true;
      };
   };
   Condition FALSE = (stateDefinition) -> {
      return (blockState) -> {
         return false;
      };
   };

   Predicate getPredicate(StateDefinition var1);
}
