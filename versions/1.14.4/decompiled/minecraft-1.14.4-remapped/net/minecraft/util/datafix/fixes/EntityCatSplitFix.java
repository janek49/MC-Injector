package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.SimpleEntityRenameFix;

public class EntityCatSplitFix extends SimpleEntityRenameFix {
   public EntityCatSplitFix(Schema schema, boolean var2) {
      super("EntityCatSplitFix", schema, var2);
   }

   protected Pair getNewNameAndTag(String string, Dynamic dynamic) {
      if(Objects.equals("minecraft:ocelot", string)) {
         int var3 = dynamic.get("CatType").asInt(0);
         if(var3 == 0) {
            String var4 = dynamic.get("Owner").asString("");
            String var5 = dynamic.get("OwnerUUID").asString("");
            if(var4.length() > 0 || var5.length() > 0) {
               dynamic.set("Trusting", dynamic.createBoolean(true));
            }
         } else if(var3 > 0 && var3 < 4) {
            dynamic = dynamic.set("CatType", dynamic.createInt(var3));
            dynamic = dynamic.set("OwnerUUID", dynamic.createString(dynamic.get("OwnerUUID").asString("")));
            return Pair.of("minecraft:cat", dynamic);
         }
      }

      return Pair.of(string, dynamic);
   }
}
