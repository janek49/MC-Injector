package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityHorseSaddleFix extends NamedEntityFix {
   public EntityHorseSaddleFix(Schema schema, boolean var2) {
      super(schema, var2, "EntityHorseSaddleFix", References.ENTITY, "EntityHorse");
   }

   protected Typed fix(Typed typed) {
      OpticFinder<Pair<String, String>> var2 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), DSL.namespacedString()));
      Type<?> var3 = this.getInputSchema().getTypeRaw(References.ITEM_STACK);
      OpticFinder<?> var4 = DSL.fieldFinder("SaddleItem", var3);
      Optional<? extends Typed<?>> var5 = typed.getOptionalTyped(var4);
      Dynamic<?> var6 = (Dynamic)typed.get(DSL.remainderFinder());
      if(!var5.isPresent() && var6.get("Saddle").asBoolean(false)) {
         Typed<?> var7 = (Typed)var3.pointTyped(typed.getOps()).orElseThrow(IllegalStateException::<init>);
         var7 = var7.set(var2, Pair.of(References.ITEM_NAME.typeName(), "minecraft:saddle"));
         Dynamic<?> var8 = var6.emptyMap();
         var8 = var8.set("Count", var8.createByte((byte)1));
         var8 = var8.set("Damage", var8.createShort((short)0));
         var7 = var7.set(DSL.remainderFinder(), var8);
         var6.remove("Saddle");
         return typed.set(var4, var7).set(DSL.remainderFinder(), var6);
      } else {
         return typed;
      }
   }
}
