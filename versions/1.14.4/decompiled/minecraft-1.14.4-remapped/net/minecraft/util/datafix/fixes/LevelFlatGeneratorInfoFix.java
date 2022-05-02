package net.minecraft.util.datafix.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.EntityBlockStateFix;
import net.minecraft.util.datafix.fixes.References;
import org.apache.commons.lang3.math.NumberUtils;

public class LevelFlatGeneratorInfoFix extends DataFix {
   private static final Splitter SPLITTER = Splitter.on(';').limit(5);
   private static final Splitter LAYER_SPLITTER = Splitter.on(',');
   private static final Splitter OLD_AMOUNT_SPLITTER = Splitter.on('x').limit(2);
   private static final Splitter AMOUNT_SPLITTER = Splitter.on('*').limit(2);
   private static final Splitter BLOCK_SPLITTER = Splitter.on(':').limit(3);

   public LevelFlatGeneratorInfoFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("LevelFlatGeneratorInfoFix", this.getInputSchema().getType(References.LEVEL), (typed) -> {
         return typed.update(DSL.remainderFinder(), this::fix);
      });
   }

   private Dynamic fix(Dynamic dynamic) {
      return dynamic.get("generatorName").asString("").equalsIgnoreCase("flat")?dynamic.update("generatorOptions", (dynamic) -> {
         Optional var10000 = dynamic.asString().map(this::fixString);
         dynamic.getClass();
         return (Dynamic)DataFixUtils.orElse(var10000.map(dynamic::createString), dynamic);
      }):dynamic;
   }

   @VisibleForTesting
   String fixString(String string) {
      if(string.isEmpty()) {
         return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
      } else {
         Iterator<String> var2 = SPLITTER.split(string).iterator();
         String var3 = (String)var2.next();
         int var4;
         String var5;
         if(var2.hasNext()) {
            var4 = NumberUtils.toInt(var3, 0);
            var5 = (String)var2.next();
         } else {
            var4 = 0;
            var5 = var3;
         }

         if(var4 >= 0 && var4 <= 3) {
            StringBuilder var6 = new StringBuilder();
            Splitter var7 = var4 < 3?OLD_AMOUNT_SPLITTER:AMOUNT_SPLITTER;
            var6.append((String)StreamSupport.stream(LAYER_SPLITTER.split(var5).spliterator(), false).map((var2) -> {
               List<String> var5 = var7.splitToList(var2);
               int var3;
               String var4;
               if(var5.size() == 2) {
                  var3 = NumberUtils.toInt((String)var5.get(0));
                  var4 = (String)var5.get(1);
               } else {
                  var3 = 1;
                  var4 = (String)var5.get(0);
               }

               List<String> var6 = BLOCK_SPLITTER.splitToList(var4);
               int var7 = ((String)var6.get(0)).equals("minecraft")?1:0;
               String var8 = (String)var6.get(var7);
               int var9 = var4 == 3?EntityBlockStateFix.getBlockId("minecraft:" + var8):NumberUtils.toInt(var8, 0);
               int var10 = var7 + 1;
               int var11 = var6.size() > var10?NumberUtils.toInt((String)var6.get(var10), 0):0;
               return (var3 == 1?"":var3 + "*") + BlockStateData.getTag(var9 << 4 | var11).get("Name").asString("");
            }).collect(Collectors.joining(",")));

            while(var2.hasNext()) {
               var6.append(';').append((String)var2.next());
            }

            return var6.toString();
         } else {
            return "minecraft:bedrock,2*minecraft:dirt,minecraft:grass_block;1;village";
         }
      }
   }
}
