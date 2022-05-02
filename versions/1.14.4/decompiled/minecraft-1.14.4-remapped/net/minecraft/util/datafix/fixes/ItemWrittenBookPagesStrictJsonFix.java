package net.minecraft.util.datafix.fixes;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.fixes.BlockEntitySignTextStrictJsonFix;
import net.minecraft.util.datafix.fixes.References;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix extends DataFix {
   public ItemWrittenBookPagesStrictJsonFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public Dynamic fixTag(Dynamic dynamic) {
      return dynamic.update("pages", (var1) -> {
         Optional var10000 = var1.asStreamOpt().map((stream) -> {
            return stream.map((dynamic) -> {
               if(!dynamic.asString().isPresent()) {
                  return dynamic;
               } else {
                  String var1 = dynamic.asString("");
                  Component var2 = null;
                  if(!"null".equals(var1) && !StringUtils.isEmpty(var1)) {
                     if(var1.charAt(0) == 34 && var1.charAt(var1.length() - 1) == 34 || var1.charAt(0) == 123 && var1.charAt(var1.length() - 1) == 125) {
                        try {
                           var2 = (Component)GsonHelper.fromJson(BlockEntitySignTextStrictJsonFix.GSON, var1, Component.class, true);
                           if(var2 == null) {
                              var2 = new TextComponent("");
                           }
                        } catch (JsonParseException var6) {
                           ;
                        }

                        if(var2 == null) {
                           try {
                              var2 = Component.Serializer.fromJson(var1);
                           } catch (JsonParseException var5) {
                              ;
                           }
                        }

                        if(var2 == null) {
                           try {
                              var2 = Component.Serializer.fromJsonLenient(var1);
                           } catch (JsonParseException var4) {
                              ;
                           }
                        }

                        if(var2 == null) {
                           var2 = new TextComponent(var1);
                        }
                     } else {
                        var2 = new TextComponent(var1);
                     }
                  } else {
                     var2 = new TextComponent("");
                  }

                  return dynamic.createString(Component.Serializer.toJson(var2));
               }
            });
         });
         dynamic.getClass();
         return (Dynamic)DataFixUtils.orElse(var10000.map(dynamic::createList), dynamic.emptyList());
      });
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<?> var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", var1, (var2x) -> {
         return var2x.updateTyped(var2, (typed) -> {
            return typed.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }
}
