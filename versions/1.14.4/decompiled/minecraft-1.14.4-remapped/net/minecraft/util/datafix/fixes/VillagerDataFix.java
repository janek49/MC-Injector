package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class VillagerDataFix extends NamedEntityFix {
   public VillagerDataFix(Schema schema, String string) {
      super(schema, false, "Villager profession data fix (" + string + ")", References.ENTITY, string);
   }

   protected Typed fix(Typed typed) {
      Dynamic<?> var2 = (Dynamic)typed.get(DSL.remainderFinder());
      return typed.set(DSL.remainderFinder(), var2.remove("Profession").remove("Career").remove("CareerLevel").set("VillagerData", var2.createMap(ImmutableMap.of(var2.createString("type"), var2.createString("minecraft:plains"), var2.createString("profession"), var2.createString(upgradeData(var2.get("Profession").asInt(0), var2.get("Career").asInt(0))), var2.createString("level"), DataFixUtils.orElse(var2.get("CareerLevel").get(), var2.createInt(1))))));
   }

   private static String upgradeData(int var0, int var1) {
      return var0 == 0?(var1 == 2?"minecraft:fisherman":(var1 == 3?"minecraft:shepherd":(var1 == 4?"minecraft:fletcher":"minecraft:farmer"))):(var0 == 1?(var1 == 2?"minecraft:cartographer":"minecraft:librarian"):(var0 == 2?"minecraft:cleric":(var0 == 3?(var1 == 2?"minecraft:weaponsmith":(var1 == 3?"minecraft:toolsmith":"minecraft:armorer")):(var0 == 4?(var1 == 2?"minecraft:leatherworker":"minecraft:butcher"):(var0 == 5?"minecraft:nitwit":"minecraft:none")))));
   }
}
