package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.datafix.fixes.References;

public class EntityCustomNameToComponentFix extends DataFix {
   public EntityCustomNameToComponentFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<String> var1 = DSL.fieldFinder("id", DSL.namespacedString());
      return this.fixTypeEverywhereTyped("EntityCustomNameToComponentFix", this.getInputSchema().getType(References.ENTITY), (var1x) -> {
         return var1x.update(DSL.remainderFinder(), (var2) -> {
            Optional<String> var3 = var1x.getOptional(var1);
            return var3.isPresent() && Objects.equals(var3.get(), "minecraft:commandblock_minecart")?var2:fixTagCustomName(var2);
         });
      });
   }

   public static Dynamic fixTagCustomName(Dynamic dynamic) {
      String var1 = dynamic.get("CustomName").asString("");
      return var1.isEmpty()?dynamic.remove("CustomName"):dynamic.set("CustomName", dynamic.createString(Component.Serializer.toJson(new TextComponent(var1))));
   }
}
