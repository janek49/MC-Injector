package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.ItemIdFix;
import net.minecraft.util.datafix.fixes.ItemStackTheFlatteningFix;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityJukeboxFix extends NamedEntityFix {
   public BlockEntityJukeboxFix(Schema schema, boolean var2) {
      super(schema, var2, "BlockEntityJukeboxFix", References.BLOCK_ENTITY, "minecraft:jukebox");
   }

   protected Typed fix(Typed typed) {
      Type<?> var2 = this.getInputSchema().getChoiceType(References.BLOCK_ENTITY, "minecraft:jukebox");
      Type<?> var3 = var2.findFieldType("RecordItem");
      OpticFinder<?> var4 = DSL.fieldFinder("RecordItem", var3);
      Dynamic<?> var5 = (Dynamic)typed.get(DSL.remainderFinder());
      int var6 = var5.get("Record").asInt(0);
      if(var6 > 0) {
         var5.remove("Record");
         String var7 = ItemStackTheFlatteningFix.updateItem(ItemIdFix.getItem(var6), 0);
         if(var7 != null) {
            Dynamic<?> var8 = var5.emptyMap();
            var8 = var8.set("id", var8.createString(var7));
            var8 = var8.set("Count", var8.createByte((byte)1));
            return typed.set(var4, (Typed)((Optional)var3.readTyped(var8).getSecond()).orElseThrow(() -> {
               return new IllegalStateException("Could not create record item stack.");
            })).set(DSL.remainderFinder(), var5);
         }
      }

      return typed;
   }
}
