package net.minecraft.world.level.block.state.predicate;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStatePredicate implements Predicate {
   public static final Predicate ANY = (blockState) -> {
      return true;
   };
   private final StateDefinition definition;
   private final Map properties = Maps.newHashMap();

   private BlockStatePredicate(StateDefinition definition) {
      this.definition = definition;
   }

   public static BlockStatePredicate forBlock(Block block) {
      return new BlockStatePredicate(block.getStateDefinition());
   }

   public boolean test(@Nullable BlockState blockState) {
      if(blockState != null && blockState.getBlock().equals(this.definition.getOwner())) {
         if(this.properties.isEmpty()) {
            return true;
         } else {
            for(Entry<Property<?>, Predicate<Object>> var3 : this.properties.entrySet()) {
               if(!this.applies(blockState, (Property)var3.getKey(), (Predicate)var3.getValue())) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   protected boolean applies(BlockState blockState, Property property, Predicate predicate) {
      T var4 = blockState.getValue(property);
      return predicate.test(var4);
   }

   public BlockStatePredicate where(Property property, Predicate predicate) {
      if(!this.definition.getProperties().contains(property)) {
         throw new IllegalArgumentException(this.definition + " cannot support property " + property);
      } else {
         this.properties.put(property, predicate);
         return this;
      }
   }

   // $FF: synthetic method
   public boolean test(@Nullable Object var1) {
      return this.test((BlockState)var1);
   }
}
