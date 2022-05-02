package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class EntityEquipmentToArmorAndHandFix extends DataFix {
   public EntityEquipmentToArmorAndHandFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.cap(this.getInputSchema().getTypeRaw(References.ITEM_STACK));
   }

   private TypeRewriteRule cap(Type type) {
      Type<Pair<Either<List<IS>, Unit>, Dynamic<?>>> type = DSL.and(DSL.optional(DSL.field("Equipment", DSL.list(type))), DSL.remainderType());
      Type<Pair<Either<List<IS>, Unit>, Pair<Either<List<IS>, Unit>, Dynamic<?>>>> var3 = DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(type))), DSL.optional(DSL.field("HandItems", DSL.list(type))), DSL.remainderType());
      OpticFinder<Pair<Either<List<IS>, Unit>, Dynamic<?>>> var4 = DSL.typeFinder(type);
      OpticFinder<List<IS>> var5 = DSL.fieldFinder("Equipment", DSL.list(type));
      return this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix", this.getInputSchema().getType(References.ENTITY), this.getOutputSchema().getType(References.ENTITY), (var4x) -> {
         Either<List<IS>, Unit> var5 = Either.right(DSL.unit());
         Either<List<IS>, Unit> var6 = Either.right(DSL.unit());
         Dynamic<?> var7 = (Dynamic)var4x.getOrCreate(DSL.remainderFinder());
         Optional<List<IS>> var8 = var4x.getOptional(var5);
         if(var8.isPresent()) {
            List<IS> var9 = (List)var8.get();
            IS var10 = ((Optional)type.read(var7.emptyMap()).getSecond()).orElseThrow(() -> {
               return new IllegalStateException("Could not parse newly created empty itemstack.");
            });
            if(!var9.isEmpty()) {
               var5 = Either.left(Lists.newArrayList(new Object[]{var9.get(0), var10}));
            }

            if(var9.size() > 1) {
               List<IS> var11 = Lists.newArrayList(new Object[]{var10, var10, var10, var10});

               for(int var12 = 1; var12 < Math.min(var9.size(), 5); ++var12) {
                  var11.set(var12 - 1, var9.get(var12));
               }

               var6 = Either.left(var11);
            }
         }

         Optional<? extends Stream<? extends Dynamic<?>>> var10 = var7.get("DropChances").asStreamOpt();
         if(var10.isPresent()) {
            Iterator<? extends Dynamic<?>> var11 = Stream.concat((Stream)var10.get(), Stream.generate(() -> {
               return var7.createInt(0);
            })).iterator();
            float var12 = ((Dynamic)var11.next()).asFloat(0.0F);
            if(!var7.get("HandDropChances").get().isPresent()) {
               Dynamic<?> var13 = var7.emptyMap().merge(var7.createFloat(var12)).merge(var7.createFloat(0.0F));
               var7 = var7.set("HandDropChances", var13);
            }

            if(!var7.get("ArmorDropChances").get().isPresent()) {
               Dynamic<?> var13 = var7.emptyMap().merge(var7.createFloat(((Dynamic)var11.next()).asFloat(0.0F))).merge(var7.createFloat(((Dynamic)var11.next()).asFloat(0.0F))).merge(var7.createFloat(((Dynamic)var11.next()).asFloat(0.0F))).merge(var7.createFloat(((Dynamic)var11.next()).asFloat(0.0F)));
               var7 = var7.set("ArmorDropChances", var13);
            }

            var7 = var7.remove("DropChances");
         }

         return var4x.set(var4, var3, Pair.of(var5, Pair.of(var6, var7)));
      });
   }
}
