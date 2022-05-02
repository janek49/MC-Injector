package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public abstract class EntityRenameFix extends DataFix {
   protected final String name;

   public EntityRenameFix(String name, Schema schema, boolean var3) {
      super(schema, var3);
      this.name = name;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<String> var1 = this.getInputSchema().findChoiceType(References.ENTITY);
      TaggedChoiceType<String> var2 = this.getOutputSchema().findChoiceType(References.ENTITY);
      return this.fixTypeEverywhere(this.name, var1, var2, (dynamicOps) -> {
         return (var4) -> {
            String var5 = (String)var4.getFirst();
            Type<?> var6 = (Type)var1.types().get(var5);
            Pair<String, Typed<?>> var7 = this.fix(var5, this.getEntity(var4.getSecond(), dynamicOps, var6));
            Type<?> var8 = (Type)var2.types().get(var7.getFirst());
            if(!var8.equals(((Typed)var7.getSecond()).getType(), true, true)) {
               throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", new Object[]{var8, ((Typed)var7.getSecond()).getType()}));
            } else {
               return Pair.of(var7.getFirst(), ((Typed)var7.getSecond()).getValue());
            }
         };
      });
   }

   private Typed getEntity(Object object, DynamicOps dynamicOps, Type type) {
      return new Typed(type, dynamicOps, object);
   }

   protected abstract Pair fix(String var1, Typed var2);
}
