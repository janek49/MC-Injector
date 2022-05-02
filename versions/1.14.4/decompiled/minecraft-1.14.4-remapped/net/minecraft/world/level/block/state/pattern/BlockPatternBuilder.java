package net.minecraft.world.level.block.state.pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class BlockPatternBuilder {
   private static final Joiner COMMA_JOINED = Joiner.on(",");
   private final List pattern = Lists.newArrayList();
   private final Map lookup = Maps.newHashMap();
   private int height;
   private int width;

   private BlockPatternBuilder() {
      this.lookup.put(Character.valueOf(' '), Predicates.alwaysTrue());
   }

   public BlockPatternBuilder aisle(String... strings) {
      if(!ArrayUtils.isEmpty(strings) && !StringUtils.isEmpty(strings[0])) {
         if(this.pattern.isEmpty()) {
            this.height = strings.length;
            this.width = strings[0].length();
         }

         if(strings.length != this.height) {
            throw new IllegalArgumentException("Expected aisle with height of " + this.height + ", but was given one with a height of " + strings.length + ")");
         } else {
            for(String var5 : strings) {
               if(var5.length() != this.width) {
                  throw new IllegalArgumentException("Not all rows in the given aisle are the correct width (expected " + this.width + ", found one with " + var5.length() + ")");
               }

               for(char var9 : var5.toCharArray()) {
                  if(!this.lookup.containsKey(Character.valueOf(var9))) {
                     this.lookup.put(Character.valueOf(var9), (Object)null);
                  }
               }
            }

            this.pattern.add(strings);
            return this;
         }
      } else {
         throw new IllegalArgumentException("Empty pattern for aisle");
      }
   }

   public static BlockPatternBuilder start() {
      return new BlockPatternBuilder();
   }

   public BlockPatternBuilder where(char var1, Predicate predicate) {
      this.lookup.put(Character.valueOf(var1), predicate);
      return this;
   }

   public BlockPattern build() {
      return new BlockPattern(this.createPattern());
   }

   private Predicate[][][] createPattern() {
      this.ensureAllCharactersMatched();
      Predicate<BlockInWorld>[][][] predicates = (Predicate[][][])((Predicate[][][])Array.newInstance(Predicate.class, new int[]{this.pattern.size(), this.height, this.width}));

      for(int var2 = 0; var2 < this.pattern.size(); ++var2) {
         for(int var3 = 0; var3 < this.height; ++var3) {
            for(int var4 = 0; var4 < this.width; ++var4) {
               predicates[var2][var3][var4] = (Predicate)this.lookup.get(Character.valueOf(((String[])this.pattern.get(var2))[var3].charAt(var4)));
            }
         }
      }

      return predicates;
   }

   private void ensureAllCharactersMatched() {
      List<Character> var1 = Lists.newArrayList();

      for(Entry<Character, Predicate<BlockInWorld>> var3 : this.lookup.entrySet()) {
         if(var3.getValue() == null) {
            var1.add(var3.getKey());
         }
      }

      if(!var1.isEmpty()) {
         throw new IllegalStateException("Predicates for character(s) " + COMMA_JOINED.join(var1) + " are missing");
      }
   }
}
