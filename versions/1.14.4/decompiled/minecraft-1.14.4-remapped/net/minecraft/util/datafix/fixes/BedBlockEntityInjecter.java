package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class BedBlockEntityInjecter extends DataFix {
   public BedBlockEntityInjecter(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getOutputSchema().getType(References.CHUNK);
      Type<?> var2 = var1.findFieldType("Level");
      Type<?> var3 = var2.findFieldType("TileEntities");
      if(!(var3 instanceof ListType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         ListType<?> var4 = (ListType)var3;
         return this.cap(var2, var4);
      }
   }

   private TypeRewriteRule cap(Type type, ListType list$ListType) {
      Type<TE> type = list$ListType.getElement();
      OpticFinder<?> var4 = DSL.fieldFinder("Level", type);
      OpticFinder<List<TE>> var5 = DSL.fieldFinder("TileEntities", list$ListType);
      int var6 = 416;
      return TypeRewriteRule.seq(this.fixTypeEverywhere("InjectBedBlockEntityType", this.getInputSchema().findChoiceType(References.BLOCK_ENTITY), this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY), (dynamicOps) -> {
         return (pair) -> {
            return pair;
         };
      }), this.fixTypeEverywhereTyped("BedBlockEntityInjecter", this.getOutputSchema().getType(References.CHUNK), (var3) -> {
         Typed<?> var4 = var3.getTyped(var4);
         Dynamic<?> var5 = (Dynamic)var4.get(DSL.remainderFinder());
         int var6 = var5.get("xPos").asInt(0);
         int var7 = var5.get("zPos").asInt(0);
         List<TE> var8 = Lists.newArrayList((Iterable)var4.getOrCreate(var5));
         List<? extends Dynamic<?>> var9 = var5.get("Sections").asList(Function.identity());

         for(int var10 = 0; var10 < var9.size(); ++var10) {
            Dynamic<?> var11 = (Dynamic)var9.get(var10);
            int var12 = var11.get("Y").asInt(0);
            Stream<Integer> var13 = var11.get("Blocks").asStream().map((dynamic) -> {
               return Integer.valueOf(dynamic.asInt(0));
            });
            int var14 = 0;
            var13.getClass();

            for(Iterator var15 = (var13::iterator).iterator(); var15.hasNext(); ++var14) {
               int var16 = ((Integer)var15.next()).intValue();
               if(416 == (var16 & 255) << 4) {
                  int var17 = var14 & 15;
                  int var18 = var14 >> 8 & 15;
                  int var19 = var14 >> 4 & 15;
                  Map<Dynamic<?>, Dynamic<?>> var20 = Maps.newHashMap();
                  var20.put(var11.createString("id"), var11.createString("minecraft:bed"));
                  var20.put(var11.createString("x"), var11.createInt(var17 + (var6 << 4)));
                  var20.put(var11.createString("y"), var11.createInt(var18 + (var12 << 4)));
                  var20.put(var11.createString("z"), var11.createInt(var19 + (var7 << 4)));
                  var20.put(var11.createString("color"), var11.createShort((short)14));
                  var8.add(((Optional)type.read(var11.createMap(var20)).getSecond()).orElseThrow(() -> {
                     return new IllegalStateException("Could not parse newly created bed block entity.");
                  }));
               }
            }
         }

         if(!var8.isEmpty()) {
            return var3.set(var4, var4.set(var5, var8));
         } else {
            return var3;
         }
      }));
   }
}
