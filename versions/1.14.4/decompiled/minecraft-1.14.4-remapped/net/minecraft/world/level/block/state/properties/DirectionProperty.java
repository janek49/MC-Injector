package net.minecraft.world.level.block.state.properties;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class DirectionProperty extends EnumProperty {
   protected DirectionProperty(String string, Collection collection) {
      super(string, Direction.class, collection);
   }

   public static DirectionProperty create(String string, Predicate predicate) {
      return create(string, (Collection)Arrays.stream(Direction.values()).filter(predicate).collect(Collectors.toList()));
   }

   public static DirectionProperty create(String string, Direction... directions) {
      return create(string, (Collection)Lists.newArrayList(directions));
   }

   public static DirectionProperty create(String string, Collection collection) {
      return new DirectionProperty(string, collection);
   }
}
