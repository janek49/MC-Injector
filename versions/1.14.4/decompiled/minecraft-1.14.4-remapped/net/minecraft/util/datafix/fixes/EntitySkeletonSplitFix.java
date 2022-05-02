package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.SimpleEntityRenameFix;

public class EntitySkeletonSplitFix extends SimpleEntityRenameFix {
   public EntitySkeletonSplitFix(Schema schema, boolean var2) {
      super("EntitySkeletonSplitFix", schema, var2);
   }

   protected Pair getNewNameAndTag(String string, Dynamic dynamic) {
      if(Objects.equals(string, "Skeleton")) {
         int var3 = dynamic.get("SkeletonType").asInt(0);
         if(var3 == 1) {
            string = "WitherSkeleton";
         } else if(var3 == 2) {
            string = "Stray";
         }
      }

      return Pair.of(string, dynamic);
   }
}
