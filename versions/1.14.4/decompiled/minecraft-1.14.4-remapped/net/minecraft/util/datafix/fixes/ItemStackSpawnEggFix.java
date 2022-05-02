package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class ItemStackSpawnEggFix extends DataFix {
   private static final Map MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put("minecraft:bat", "minecraft:bat_spawn_egg");
      hashMap.put("minecraft:blaze", "minecraft:blaze_spawn_egg");
      hashMap.put("minecraft:cave_spider", "minecraft:cave_spider_spawn_egg");
      hashMap.put("minecraft:chicken", "minecraft:chicken_spawn_egg");
      hashMap.put("minecraft:cow", "minecraft:cow_spawn_egg");
      hashMap.put("minecraft:creeper", "minecraft:creeper_spawn_egg");
      hashMap.put("minecraft:donkey", "minecraft:donkey_spawn_egg");
      hashMap.put("minecraft:elder_guardian", "minecraft:elder_guardian_spawn_egg");
      hashMap.put("minecraft:enderman", "minecraft:enderman_spawn_egg");
      hashMap.put("minecraft:endermite", "minecraft:endermite_spawn_egg");
      hashMap.put("minecraft:evocation_illager", "minecraft:evocation_illager_spawn_egg");
      hashMap.put("minecraft:ghast", "minecraft:ghast_spawn_egg");
      hashMap.put("minecraft:guardian", "minecraft:guardian_spawn_egg");
      hashMap.put("minecraft:horse", "minecraft:horse_spawn_egg");
      hashMap.put("minecraft:husk", "minecraft:husk_spawn_egg");
      hashMap.put("minecraft:llama", "minecraft:llama_spawn_egg");
      hashMap.put("minecraft:magma_cube", "minecraft:magma_cube_spawn_egg");
      hashMap.put("minecraft:mooshroom", "minecraft:mooshroom_spawn_egg");
      hashMap.put("minecraft:mule", "minecraft:mule_spawn_egg");
      hashMap.put("minecraft:ocelot", "minecraft:ocelot_spawn_egg");
      hashMap.put("minecraft:pufferfish", "minecraft:pufferfish_spawn_egg");
      hashMap.put("minecraft:parrot", "minecraft:parrot_spawn_egg");
      hashMap.put("minecraft:pig", "minecraft:pig_spawn_egg");
      hashMap.put("minecraft:polar_bear", "minecraft:polar_bear_spawn_egg");
      hashMap.put("minecraft:rabbit", "minecraft:rabbit_spawn_egg");
      hashMap.put("minecraft:sheep", "minecraft:sheep_spawn_egg");
      hashMap.put("minecraft:shulker", "minecraft:shulker_spawn_egg");
      hashMap.put("minecraft:silverfish", "minecraft:silverfish_spawn_egg");
      hashMap.put("minecraft:skeleton", "minecraft:skeleton_spawn_egg");
      hashMap.put("minecraft:skeleton_horse", "minecraft:skeleton_horse_spawn_egg");
      hashMap.put("minecraft:slime", "minecraft:slime_spawn_egg");
      hashMap.put("minecraft:spider", "minecraft:spider_spawn_egg");
      hashMap.put("minecraft:squid", "minecraft:squid_spawn_egg");
      hashMap.put("minecraft:stray", "minecraft:stray_spawn_egg");
      hashMap.put("minecraft:turtle", "minecraft:turtle_spawn_egg");
      hashMap.put("minecraft:vex", "minecraft:vex_spawn_egg");
      hashMap.put("minecraft:villager", "minecraft:villager_spawn_egg");
      hashMap.put("minecraft:vindication_illager", "minecraft:vindication_illager_spawn_egg");
      hashMap.put("minecraft:witch", "minecraft:witch_spawn_egg");
      hashMap.put("minecraft:wither_skeleton", "minecraft:wither_skeleton_spawn_egg");
      hashMap.put("minecraft:wolf", "minecraft:wolf_spawn_egg");
      hashMap.put("minecraft:zombie", "minecraft:zombie_spawn_egg");
      hashMap.put("minecraft:zombie_horse", "minecraft:zombie_horse_spawn_egg");
      hashMap.put("minecraft:zombie_pigman", "minecraft:zombie_pigman_spawn_egg");
      hashMap.put("minecraft:zombie_villager", "minecraft:zombie_villager_spawn_egg");
   });

   public ItemStackSpawnEggFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> var2 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<String> var3 = DSL.fieldFinder("id", DSL.namespacedString());
      OpticFinder<?> var4 = var1.findField("tag");
      OpticFinder<?> var5 = var4.type().findField("EntityTag");
      return this.fixTypeEverywhereTyped("ItemInstanceSpawnEggFix", var1, (var4x) -> {
         Optional<Pair<String, String>> var5 = var4x.getOptional(var2);
         if(var5.isPresent() && Objects.equals(((Pair)var5.get()).getSecond(), "minecraft:spawn_egg")) {
            Typed<?> var6 = var4x.getOrCreateTyped(var4);
            Typed<?> var7 = var6.getOrCreateTyped(var5);
            Optional<String> var8 = var7.getOptional(var3);
            if(var8.isPresent()) {
               return var4x.set(var2, Pair.of(References.ITEM_NAME.typeName(), MAP.getOrDefault(var8.get(), "minecraft:pig_spawn_egg")));
            }
         }

         return var4x;
      });
   }
}
