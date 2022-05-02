package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityIdFix extends DataFix {
   private static final Map ID_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put("Airportal", "minecraft:end_portal");
      hashMap.put("Banner", "minecraft:banner");
      hashMap.put("Beacon", "minecraft:beacon");
      hashMap.put("Cauldron", "minecraft:brewing_stand");
      hashMap.put("Chest", "minecraft:chest");
      hashMap.put("Comparator", "minecraft:comparator");
      hashMap.put("Control", "minecraft:command_block");
      hashMap.put("DLDetector", "minecraft:daylight_detector");
      hashMap.put("Dropper", "minecraft:dropper");
      hashMap.put("EnchantTable", "minecraft:enchanting_table");
      hashMap.put("EndGateway", "minecraft:end_gateway");
      hashMap.put("EnderChest", "minecraft:ender_chest");
      hashMap.put("FlowerPot", "minecraft:flower_pot");
      hashMap.put("Furnace", "minecraft:furnace");
      hashMap.put("Hopper", "minecraft:hopper");
      hashMap.put("MobSpawner", "minecraft:mob_spawner");
      hashMap.put("Music", "minecraft:noteblock");
      hashMap.put("Piston", "minecraft:piston");
      hashMap.put("RecordPlayer", "minecraft:jukebox");
      hashMap.put("Sign", "minecraft:sign");
      hashMap.put("Skull", "minecraft:skull");
      hashMap.put("Structure", "minecraft:structure_block");
      hashMap.put("Trap", "minecraft:dispenser");
   });

   public BlockEntityIdFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.ITEM_STACK);
      Type<?> var2 = this.getOutputSchema().getType(References.ITEM_STACK);
      TaggedChoiceType<String> var3 = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
      TaggedChoiceType<String> var4 = this.getOutputSchema().findChoiceType(References.BLOCK_ENTITY);
      return TypeRewriteRule.seq(this.convertUnchecked("item stack block entity name hook converter", var1, var2), this.fixTypeEverywhere("BlockEntityIdFix", var3, var4, (dynamicOps) -> {
         return (pair) -> {
            return pair.mapFirst((string) -> {
               return (String)ID_MAP.getOrDefault(string, string);
            });
         };
      }));
   }
}
