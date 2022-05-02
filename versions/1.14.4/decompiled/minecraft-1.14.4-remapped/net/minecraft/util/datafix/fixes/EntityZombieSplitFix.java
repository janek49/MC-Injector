package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.SimpleEntityRenameFix;

public class EntityZombieSplitFix extends SimpleEntityRenameFix {
   public EntityZombieSplitFix(Schema schema, boolean var2) {
      super("EntityZombieSplitFix", schema, var2);
   }

   protected Pair getNewNameAndTag(String string, Dynamic dynamic) {
      if(Objects.equals("Zombie", string)) {
         String string = "Zombie";
         int var4 = dynamic.get("ZombieType").asInt(0);
         switch(var4) {
         case 0:
         default:
            break;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            string = "ZombieVillager";
            dynamic = dynamic.set("Profession", dynamic.createInt(var4 - 1));
            break;
         case 6:
            string = "Husk";
         }

         dynamic = dynamic.remove("ZombieType");
         return Pair.of(string, dynamic);
      } else {
         return Pair.of(string, dynamic);
      }
   }
}
