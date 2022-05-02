package net.minecraft.util.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class OminousBannerBlockEntityRenameFix extends NamedEntityFix {
   public OminousBannerBlockEntityRenameFix(Schema schema, boolean var2) {
      super(schema, var2, "OminousBannerBlockEntityRenameFix", References.BLOCK_ENTITY, "minecraft:banner");
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), this::fixTag);
   }

   private Dynamic fixTag(Dynamic dynamic) {
      Optional<String> var2 = dynamic.get("CustomName").asString();
      if(var2.isPresent()) {
         String var3 = (String)var2.get();
         var3 = var3.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
         return dynamic.set("CustomName", dynamic.createString(var3));
      } else {
         return dynamic;
      }
   }
}
