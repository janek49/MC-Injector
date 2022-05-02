package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class EntityRidingToPassengersFix extends DataFix {
   public EntityRidingToPassengersFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      Schema var1 = this.getInputSchema();
      Schema var2 = this.getOutputSchema();
      Type<?> var3 = var1.getTypeRaw(References.ENTITY_TREE);
      Type<?> var4 = var2.getTypeRaw(References.ENTITY_TREE);
      Type<?> var5 = var1.getTypeRaw(References.ENTITY);
      return this.cap(var1, var2, var3, var4, var5);
   }

   private TypeRewriteRule cap(Schema var1, Schema var2, Type var3, Type var4, Type var5) {
      Type<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> var6 = DSL.named(References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Riding", var3)), var5));
      Type<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> var7 = DSL.named(References.ENTITY_TREE.typeName(), DSL.and(DSL.optional(DSL.field("Passengers", DSL.list(var4))), var5));
      Type<?> var8 = var1.getType(References.ENTITY_TREE);
      Type<?> var9 = var2.getType(References.ENTITY_TREE);
      if(!Objects.equals(var8, var6)) {
         throw new IllegalStateException("Old entity type is not what was expected.");
      } else if(!var9.equals(var7, true, true)) {
         throw new IllegalStateException("New entity type is not what was expected.");
      } else {
         OpticFinder<Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>>> var10 = DSL.typeFinder(var6);
         OpticFinder<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> var11 = DSL.typeFinder(var7);
         OpticFinder<NewEntityTree> var12 = DSL.typeFinder(var4);
         Type<?> var13 = var1.getType(References.PLAYER);
         Type<?> var14 = var2.getType(References.PLAYER);
         return TypeRewriteRule.seq(this.fixTypeEverywhere("EntityRidingToPassengerFix", var6, var7, (dynamicOps) -> {
            return (var6) -> {
               Optional<Pair<String, Pair<Either<List<NewEntityTree>, Unit>, Entity>>> var7 = Optional.empty();
               Pair<String, Pair<Either<OldEntityTree, Unit>, Entity>> var8 = var6;

               while(true) {
                  Either<List<NewEntityTree>, Unit> var9 = (Either)DataFixUtils.orElse(var7.map((pair) -> {
                     Typed<NewEntityTree> var5 = (Typed)var4.pointTyped(dynamicOps).orElseThrow(() -> {
                        return new IllegalStateException("Could not create new entity tree");
                     });
                     NewEntityTree var6 = var5.set(var11, pair).getOptional(var12).orElseThrow(() -> {
                        return new IllegalStateException("Should always have an entity tree here");
                     });
                     return Either.left(ImmutableList.of(var6));
                  }), Either.right(DSL.unit()));
                  var7 = Optional.of(Pair.of(References.ENTITY_TREE.typeName(), Pair.of(var9, ((Pair)var8.getSecond()).getSecond())));
                  Optional<OldEntityTree> var10 = ((Either)((Pair)var8.getSecond()).getFirst()).left();
                  if(!var10.isPresent()) {
                     return (Pair)var7.orElseThrow(() -> {
                        return new IllegalStateException("Should always have an entity tree here");
                     });
                  }

                  var8 = (Pair)(new Typed(var3, dynamicOps, var10.get())).getOptional(var10).orElseThrow(() -> {
                     return new IllegalStateException("Should always have an entity here");
                  });
               }
            };
         }), this.writeAndRead("player RootVehicle injecter", var13, var14));
      }
   }
}
