package net.minecraft.util.datafix.fixes;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.JsonOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.fixes.References;

public class LevelDataGeneratorOptionsFix extends DataFix {
   static final Map MAP = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put("0", "minecraft:ocean");
      hashMap.put("1", "minecraft:plains");
      hashMap.put("2", "minecraft:desert");
      hashMap.put("3", "minecraft:mountains");
      hashMap.put("4", "minecraft:forest");
      hashMap.put("5", "minecraft:taiga");
      hashMap.put("6", "minecraft:swamp");
      hashMap.put("7", "minecraft:river");
      hashMap.put("8", "minecraft:nether");
      hashMap.put("9", "minecraft:the_end");
      hashMap.put("10", "minecraft:frozen_ocean");
      hashMap.put("11", "minecraft:frozen_river");
      hashMap.put("12", "minecraft:snowy_tundra");
      hashMap.put("13", "minecraft:snowy_mountains");
      hashMap.put("14", "minecraft:mushroom_fields");
      hashMap.put("15", "minecraft:mushroom_field_shore");
      hashMap.put("16", "minecraft:beach");
      hashMap.put("17", "minecraft:desert_hills");
      hashMap.put("18", "minecraft:wooded_hills");
      hashMap.put("19", "minecraft:taiga_hills");
      hashMap.put("20", "minecraft:mountain_edge");
      hashMap.put("21", "minecraft:jungle");
      hashMap.put("22", "minecraft:jungle_hills");
      hashMap.put("23", "minecraft:jungle_edge");
      hashMap.put("24", "minecraft:deep_ocean");
      hashMap.put("25", "minecraft:stone_shore");
      hashMap.put("26", "minecraft:snowy_beach");
      hashMap.put("27", "minecraft:birch_forest");
      hashMap.put("28", "minecraft:birch_forest_hills");
      hashMap.put("29", "minecraft:dark_forest");
      hashMap.put("30", "minecraft:snowy_taiga");
      hashMap.put("31", "minecraft:snowy_taiga_hills");
      hashMap.put("32", "minecraft:giant_tree_taiga");
      hashMap.put("33", "minecraft:giant_tree_taiga_hills");
      hashMap.put("34", "minecraft:wooded_mountains");
      hashMap.put("35", "minecraft:savanna");
      hashMap.put("36", "minecraft:savanna_plateau");
      hashMap.put("37", "minecraft:badlands");
      hashMap.put("38", "minecraft:wooded_badlands_plateau");
      hashMap.put("39", "minecraft:badlands_plateau");
      hashMap.put("40", "minecraft:small_end_islands");
      hashMap.put("41", "minecraft:end_midlands");
      hashMap.put("42", "minecraft:end_highlands");
      hashMap.put("43", "minecraft:end_barrens");
      hashMap.put("44", "minecraft:warm_ocean");
      hashMap.put("45", "minecraft:lukewarm_ocean");
      hashMap.put("46", "minecraft:cold_ocean");
      hashMap.put("47", "minecraft:deep_warm_ocean");
      hashMap.put("48", "minecraft:deep_lukewarm_ocean");
      hashMap.put("49", "minecraft:deep_cold_ocean");
      hashMap.put("50", "minecraft:deep_frozen_ocean");
      hashMap.put("127", "minecraft:the_void");
      hashMap.put("129", "minecraft:sunflower_plains");
      hashMap.put("130", "minecraft:desert_lakes");
      hashMap.put("131", "minecraft:gravelly_mountains");
      hashMap.put("132", "minecraft:flower_forest");
      hashMap.put("133", "minecraft:taiga_mountains");
      hashMap.put("134", "minecraft:swamp_hills");
      hashMap.put("140", "minecraft:ice_spikes");
      hashMap.put("149", "minecraft:modified_jungle");
      hashMap.put("151", "minecraft:modified_jungle_edge");
      hashMap.put("155", "minecraft:tall_birch_forest");
      hashMap.put("156", "minecraft:tall_birch_hills");
      hashMap.put("157", "minecraft:dark_forest_hills");
      hashMap.put("158", "minecraft:snowy_taiga_mountains");
      hashMap.put("160", "minecraft:giant_spruce_taiga");
      hashMap.put("161", "minecraft:giant_spruce_taiga_hills");
      hashMap.put("162", "minecraft:modified_gravelly_mountains");
      hashMap.put("163", "minecraft:shattered_savanna");
      hashMap.put("164", "minecraft:shattered_savanna_plateau");
      hashMap.put("165", "minecraft:eroded_badlands");
      hashMap.put("166", "minecraft:modified_wooded_badlands_plateau");
      hashMap.put("167", "minecraft:modified_badlands_plateau");
   });

   public LevelDataGeneratorOptionsFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> var1 = this.getOutputSchema().getType(References.LEVEL);
      return this.fixTypeEverywhereTyped("LevelDataGeneratorOptionsFix", this.getInputSchema().getType(References.LEVEL), var1, (var1x) -> {
         Dynamic<?> var2 = var1x.write();
         Optional<String> var3 = var2.get("generatorOptions").asString();
         Dynamic<?> var4;
         if("flat".equalsIgnoreCase(var2.get("generatorName").asString(""))) {
            String var5 = (String)var3.orElse("");
            var4 = var2.set("generatorOptions", convert(var5, var2.getOps()));
         } else if("buffet".equalsIgnoreCase(var2.get("generatorName").asString("")) && var3.isPresent()) {
            Dynamic<JsonElement> var5 = new Dynamic(JsonOps.INSTANCE, GsonHelper.parse((String)var3.get(), true));
            var4 = var2.set("generatorOptions", var5.convert(var2.getOps()));
         } else {
            var4 = var2;
         }

         return (Typed)((Optional)var1.readTyped(var4).getSecond()).orElseThrow(() -> {
            return new IllegalStateException("Could not read new level type.");
         });
      });
   }

   private static Dynamic convert(String string, DynamicOps dynamicOps) {
      Iterator<String> var2 = Splitter.on(';').split(string).iterator();
      String var4 = "minecraft:plains";
      Map<String, Map<String, String>> var5 = Maps.newHashMap();
      List<Pair<Integer, String>> var3;
      if(!string.isEmpty() && var2.hasNext()) {
         var3 = getLayersInfoFromString((String)var2.next());
         if(!var3.isEmpty()) {
            if(var2.hasNext()) {
               var4 = (String)MAP.getOrDefault(var2.next(), "minecraft:plains");
            }

            if(var2.hasNext()) {
               String[] vars6 = ((String)var2.next()).toLowerCase(Locale.ROOT).split(",");

               for(String var10 : vars6) {
                  String[] vars11 = var10.split("\\(", 2);
                  if(!vars11[0].isEmpty()) {
                     var5.put(vars11[0], Maps.newHashMap());
                     if(vars11.length > 1 && vars11[1].endsWith(")") && vars11[1].length() > 1) {
                        String[] vars12 = vars11[1].substring(0, vars11[1].length() - 1).split(" ");

                        for(String var16 : vars12) {
                           String[] vars17 = var16.split("=", 2);
                           if(vars17.length == 2) {
                              ((Map)var5.get(vars11[0])).put(vars17[0], vars17[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               var5.put("village", Maps.newHashMap());
            }
         }
      } else {
         var3 = Lists.newArrayList();
         var3.add(Pair.of(Integer.valueOf(1), "minecraft:bedrock"));
         var3.add(Pair.of(Integer.valueOf(2), "minecraft:dirt"));
         var3.add(Pair.of(Integer.valueOf(1), "minecraft:grass_block"));
         var5.put("village", Maps.newHashMap());
      }

      T var6 = dynamicOps.createList(var3.stream().map((pair) -> {
         return dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("height"), dynamicOps.createInt(((Integer)pair.getFirst()).intValue()), dynamicOps.createString("block"), dynamicOps.createString((String)pair.getSecond())));
      }));
      T var7 = dynamicOps.createMap((Map)var5.entrySet().stream().map((map$Entry) -> {
         return Pair.of(dynamicOps.createString(((String)map$Entry.getKey()).toLowerCase(Locale.ROOT)), dynamicOps.createMap((Map)((Map)map$Entry.getValue()).entrySet().stream().map((map$Entry) -> {
            return Pair.of(dynamicOps.createString((String)map$Entry.getKey()), dynamicOps.createString((String)map$Entry.getValue()));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("layers"), var6, dynamicOps.createString("biome"), dynamicOps.createString(var4), dynamicOps.createString("structures"), var7)));
   }

   @Nullable
   private static Pair getLayerInfoFromString(String string) {
      String[] vars1 = string.split("\\*", 2);
      int var2;
      if(vars1.length == 2) {
         try {
            var2 = Integer.parseInt(vars1[0]);
         } catch (NumberFormatException var4) {
            return null;
         }
      } else {
         var2 = 1;
      }

      String var3 = vars1[vars1.length - 1];
      return Pair.of(Integer.valueOf(var2), var3);
   }

   private static List getLayersInfoFromString(String string) {
      List<Pair<Integer, String>> list = Lists.newArrayList();
      String[] vars2 = string.split(",");

      for(String var6 : vars2) {
         Pair<Integer, String> var7 = getLayerInfoFromString(var6);
         if(var7 == null) {
            return Collections.emptyList();
         }

         list.add(var7);
      }

      return list;
   }
}
