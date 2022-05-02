package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.fixes.References;

public class VillagerRebuildLevelAndXpFix extends DataFix {
   private static final int[] LEVEL_XP_THRESHOLDS = new int[]{0, 10, 50, 100, 150};

   public static int getMinXpPerLevel(int i) {
      return LEVEL_XP_THRESHOLDS[Mth.clamp(i - 1, 0, LEVEL_XP_THRESHOLDS.length - 1)];
   }

   public VillagerRebuildLevelAndXpFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getChoiceType(References.ENTITY, "minecraft:villager");
      OpticFinder<?> var2 = DSL.namedChoice("minecraft:villager", var1);
      OpticFinder<?> var3 = var1.findField("Offers");
      Type<?> var4 = var3.type();
      OpticFinder<?> var5 = var4.findField("Recipes");
      ListType<?> var6 = (ListType)var5.type();
      OpticFinder<?> var7 = var6.getElement().finder();
      return this.fixTypeEverywhereTyped("Villager level and xp rebuild", this.getInputSchema().getType(References.ENTITY), (var5x) -> {
         return var5x.updateTyped(var2, var1, (var3x) -> {
            Dynamic<?> var4 = (Dynamic)var3x.get(DSL.remainderFinder());
            int var5 = ((Number)var4.get("VillagerData").get("level").asNumber().orElse(Integer.valueOf(0))).intValue();
            Typed<?> var6 = var3x;
            if(var5 == 0 || var5 == 1) {
               int var7 = ((Integer)var3x.getOptionalTyped(var3).flatMap((typed) -> {
                  return typed.getOptionalTyped(var5);
               }).map((typed) -> {
                  return Integer.valueOf(typed.getAllTyped(var7).size());
               }).orElse(Integer.valueOf(0))).intValue();
               var5 = Mth.clamp(var7 / 2, 1, 5);
               if(var5 > 1) {
                  var6 = addLevel(var3x, var5);
               }
            }

            Optional<Number> var7 = var4.get("Xp").asNumber();
            if(!var7.isPresent()) {
               var6 = addXpFromLevel(var6, var5);
            }

            return var6;
         });
      });
   }

   private static Typed addLevel(Typed var0, int var1) {
      return var0.update(DSL.remainderFinder(), (var1x) -> {
         return var1x.update("VillagerData", (var1x) -> {
            return var1x.set("level", var1x.createInt(var1));
         });
      });
   }

   private static Typed addXpFromLevel(Typed var0, int var1) {
      int var2 = getMinXpPerLevel(var1);
      return var0.update(DSL.remainderFinder(), (var1) -> {
         return var1.set("Xp", var1.createInt(var2));
      });
   }
}
