package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.SimpleEntityRenameFix;

public class EntityElderGuardianSplitFix extends SimpleEntityRenameFix {
   public EntityElderGuardianSplitFix(Schema schema, boolean var2) {
      super("EntityElderGuardianSplitFix", schema, var2);
   }

   protected Pair getNewNameAndTag(String string, Dynamic dynamic) {
      return Pair.of(Objects.equals(string, "Guardian") && dynamic.get("Elder").asBoolean(false)?"ElderGuardian":string, dynamic);
   }
}
