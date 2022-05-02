package net.minecraft.world.inventory;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ContainerLevelAccess {
   ContainerLevelAccess NULL = new ContainerLevelAccess() {
      public Optional evaluate(BiFunction biFunction) {
         return Optional.empty();
      }
   };

   static default ContainerLevelAccess create(final Level level, final BlockPos blockPos) {
      return new ContainerLevelAccess() {
         public Optional evaluate(BiFunction biFunction) {
            return Optional.of(biFunction.apply(level, blockPos));
         }
      };
   }

   Optional evaluate(BiFunction var1);

   default Object evaluate(BiFunction biFunction, Object var2) {
      return this.evaluate(biFunction).orElse(var2);
   }

   default void execute(BiConsumer biConsumer) {
      this.evaluate((level, blockPos) -> {
         biConsumer.accept(level, blockPos);
         return Optional.empty();
      });
   }
}
