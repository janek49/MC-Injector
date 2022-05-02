package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.EntityRenameFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityHorseSplitFix extends EntityRenameFix {
   public EntityHorseSplitFix(Schema schema, boolean var2) {
      super("EntityHorseSplitFix", schema, var2);
   }

   protected Pair fix(String string, Typed typed) {
      Dynamic<?> var3 = (Dynamic)typed.get(DSL.remainderFinder());
      if(Objects.equals("EntityHorse", string)) {
         int var5 = var3.get("Type").asInt(0);
         String var4;
         switch(var5) {
         case 0:
         default:
            var4 = "Horse";
            break;
         case 1:
            var4 = "Donkey";
            break;
         case 2:
            var4 = "Mule";
            break;
         case 3:
            var4 = "ZombieHorse";
            break;
         case 4:
            var4 = "SkeletonHorse";
         }

         var3.remove("Type");
         Type<?> var6 = (Type)this.getOutputSchema().findChoiceType(References.ENTITY).types().get(var4);
         return Pair.of(var4, ((Optional)var6.readTyped(typed.write()).getSecond()).orElseThrow(() -> {
            return new IllegalStateException("Could not parse the new horse");
         }));
      } else {
         return Pair.of(string, typed);
      }
   }
}
