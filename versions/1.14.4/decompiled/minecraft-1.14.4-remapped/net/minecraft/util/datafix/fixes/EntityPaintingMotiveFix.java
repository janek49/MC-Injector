package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityPaintingMotiveFix extends NamedEntityFix {
   private static final Map MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put("donkeykong", "donkey_kong");
      hashMap.put("burningskull", "burning_skull");
      hashMap.put("skullandroses", "skull_and_roses");
   });

   public EntityPaintingMotiveFix(Schema schema, boolean var2) {
      super(schema, var2, "EntityPaintingMotiveFix", References.ENTITY, "minecraft:painting");
   }

   public Dynamic fixTag(Dynamic dynamic) {
      Optional<String> var2 = dynamic.get("Motive").asString();
      if(var2.isPresent()) {
         String var3 = ((String)var2.get()).toLowerCase(Locale.ROOT);
         return dynamic.set("Motive", dynamic.createString((new ResourceLocation((String)MAP.getOrDefault(var3, var3))).toString()));
      } else {
         return dynamic;
      }
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), this::fixTag);
   }
}
