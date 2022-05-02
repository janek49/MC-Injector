package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.SimplestEntityRenameFix;

public class EntityTippedArrowFix extends SimplestEntityRenameFix {
   public EntityTippedArrowFix(Schema schema, boolean var2) {
      super("EntityTippedArrowFix", schema, var2);
   }

   protected String rename(String string) {
      return Objects.equals(string, "TippedArrow")?"Arrow":string;
   }
}
