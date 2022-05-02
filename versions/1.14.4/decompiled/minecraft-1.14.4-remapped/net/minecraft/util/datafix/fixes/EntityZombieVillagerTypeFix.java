package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityZombieVillagerTypeFix extends NamedEntityFix {
   private static final Random RANDOM = new Random();

   public EntityZombieVillagerTypeFix(Schema schema, boolean var2) {
      super(schema, var2, "EntityZombieVillagerTypeFix", References.ENTITY, "Zombie");
   }

   public Dynamic fixTag(Dynamic dynamic) {
      if(dynamic.get("IsVillager").asBoolean(false)) {
         if(!dynamic.get("ZombieType").get().isPresent()) {
            int var2 = this.getVillagerProfession(dynamic.get("VillagerProfession").asInt(-1));
            if(var2 == -1) {
               var2 = this.getVillagerProfession(RANDOM.nextInt(6));
            }

            dynamic = dynamic.set("ZombieType", dynamic.createInt(var2));
         }

         dynamic = dynamic.remove("IsVillager");
      }

      return dynamic;
   }

   private int getVillagerProfession(int i) {
      return i >= 0 && i < 6?i:-1;
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), this::fixTag);
   }
}
