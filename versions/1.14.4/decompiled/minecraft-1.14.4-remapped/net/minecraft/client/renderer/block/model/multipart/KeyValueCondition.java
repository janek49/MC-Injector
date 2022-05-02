package net.minecraft.client.renderer.block.model.multipart;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.block.model.multipart.Condition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

@ClientJarOnly
public class KeyValueCondition implements Condition {
   private static final Splitter PIPE_SPLITTER = Splitter.on('|').omitEmptyStrings();
   private final String key;
   private final String value;

   public KeyValueCondition(String key, String value) {
      this.key = key;
      this.value = value;
   }

   public Predicate getPredicate(StateDefinition stateDefinition) {
      Property<?> var2 = stateDefinition.getProperty(this.key);
      if(var2 == null) {
         throw new RuntimeException(String.format("Unknown property \'%s\' on \'%s\'", new Object[]{this.key, ((Block)stateDefinition.getOwner()).toString()}));
      } else {
         String var3 = this.value;
         boolean var4 = !var3.isEmpty() && var3.charAt(0) == 33;
         if(var4) {
            var3 = var3.substring(1);
         }

         List<String> var5 = PIPE_SPLITTER.splitToList(var3);
         if(var5.isEmpty()) {
            throw new RuntimeException(String.format("Empty value \'%s\' for property \'%s\' on \'%s\'", new Object[]{this.value, this.key, ((Block)stateDefinition.getOwner()).toString()}));
         } else {
            Predicate<BlockState> var6;
            if(var5.size() == 1) {
               var6 = this.getBlockStatePredicate(stateDefinition, var2, var3);
            } else {
               List<Predicate<BlockState>> var7 = (List)var5.stream().map((string) -> {
                  return this.getBlockStatePredicate(stateDefinition, var2, string);
               }).collect(Collectors.toList());
               var6 = (blockState) -> {
                  return var7.stream().anyMatch((predicate) -> {
                     return predicate.test(blockState);
                  });
               };
            }

            return var4?var6.negate():var6;
         }
      }
   }

   private Predicate getBlockStatePredicate(StateDefinition stateDefinition, Property property, String string) {
      Optional<?> var4 = property.getValue(string);
      if(!var4.isPresent()) {
         throw new RuntimeException(String.format("Unknown value \'%s\' for property \'%s\' on \'%s\' in \'%s\'", new Object[]{string, this.key, ((Block)stateDefinition.getOwner()).toString(), this.value}));
      } else {
         return (blockState) -> {
            return blockState.getValue(property).equals(var4.get());
         };
      }
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("key", this.key).add("value", this.value).toString();
   }
}
