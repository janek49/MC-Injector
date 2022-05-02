package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import net.minecraft.util.datafix.fixes.EntityRenameFix;

public abstract class SimpleEntityRenameFix extends EntityRenameFix {
   public SimpleEntityRenameFix(String string, Schema schema, boolean var3) {
      super(string, schema, var3);
   }

   protected Pair fix(String string, Typed typed) {
      Pair<String, Dynamic<?>> pair = this.getNewNameAndTag(string, (Dynamic)typed.getOrCreate(DSL.remainderFinder()));
      return Pair.of(pair.getFirst(), typed.set(DSL.remainderFinder(), pair.getSecond()));
   }

   protected abstract Pair getNewNameAndTag(String var1, Dynamic var2);
}
