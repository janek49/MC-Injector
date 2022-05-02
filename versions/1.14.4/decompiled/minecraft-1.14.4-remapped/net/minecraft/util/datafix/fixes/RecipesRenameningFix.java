package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class RecipesRenameningFix extends DataFix {
   private static final Map RECIPES = ImmutableMap.builder().put("minecraft:acacia_bark", "minecraft:acacia_wood").put("minecraft:birch_bark", "minecraft:birch_wood").put("minecraft:dark_oak_bark", "minecraft:dark_oak_wood").put("minecraft:jungle_bark", "minecraft:jungle_wood").put("minecraft:oak_bark", "minecraft:oak_wood").put("minecraft:spruce_bark", "minecraft:spruce_wood").build();

   public RecipesRenameningFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, String>> var1 = DSL.named(References.RECIPE.typeName(), DSL.namespacedString());
      if(!Objects.equals(var1, this.getInputSchema().getType(References.RECIPE))) {
         throw new IllegalStateException("Recipe type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("Recipes renamening fix", var1, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond((string) -> {
                  return (String)RECIPES.getOrDefault(string, string);
               });
            };
         });
      }
   }
}
