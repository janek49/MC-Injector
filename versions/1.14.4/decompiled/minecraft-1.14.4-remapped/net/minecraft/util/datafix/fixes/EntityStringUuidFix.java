package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class EntityStringUuidFix extends DataFix {
   public EntityStringUuidFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityStringUuidFix", this.getInputSchema().getType(References.ENTITY), (typed) -> {
         return typed.update(DSL.remainderFinder(), (dynamic) -> {
            Optional<String> var1 = dynamic.get("UUID").asString();
            if(var1.isPresent()) {
               UUID var2 = UUID.fromString((String)var1.get());
               return dynamic.remove("UUID").set("UUIDMost", dynamic.createLong(var2.getMostSignificantBits())).set("UUIDLeast", dynamic.createLong(var2.getLeastSignificantBits()));
            } else {
               return dynamic;
            }
         });
      });
   }
}
