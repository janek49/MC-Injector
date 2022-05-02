package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class HeightmapRenamingFix extends DataFix {
   public HeightmapRenamingFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.CHUNK);
      OpticFinder<?> var2 = var1.findField("Level");
      return this.fixTypeEverywhereTyped("HeightmapRenamingFix", var1, (var2x) -> {
         return var2x.updateTyped(var2, (typed) -> {
            return typed.update(DSL.remainderFinder(), this::fix);
         });
      });
   }

   private Dynamic fix(Dynamic dynamic) {
      Optional<? extends Dynamic<?>> var2 = dynamic.get("Heightmaps").get();
      if(!var2.isPresent()) {
         return dynamic;
      } else {
         Dynamic<?> var3 = (Dynamic)var2.get();
         Optional<? extends Dynamic<?>> var4 = var3.get("LIQUID").get();
         if(var4.isPresent()) {
            var3 = var3.remove("LIQUID");
            var3 = var3.set("WORLD_SURFACE_WG", (Dynamic)var4.get());
         }

         Optional<? extends Dynamic<?>> var5 = var3.get("SOLID").get();
         if(var5.isPresent()) {
            var3 = var3.remove("SOLID");
            var3 = var3.set("OCEAN_FLOOR_WG", (Dynamic)var5.get());
            var3 = var3.set("OCEAN_FLOOR", (Dynamic)var5.get());
         }

         Optional<? extends Dynamic<?>> var6 = var3.get("LIGHT").get();
         if(var6.isPresent()) {
            var3 = var3.remove("LIGHT");
            var3 = var3.set("LIGHT_BLOCKING", (Dynamic)var6.get());
         }

         Optional<? extends Dynamic<?>> var7 = var3.get("RAIN").get();
         if(var7.isPresent()) {
            var3 = var3.remove("RAIN");
            var3 = var3.set("MOTION_BLOCKING", (Dynamic)var7.get());
            var3 = var3.set("MOTION_BLOCKING_NO_LEAVES", (Dynamic)var7.get());
         }

         return dynamic.set("Heightmaps", var3);
      }
   }
}
