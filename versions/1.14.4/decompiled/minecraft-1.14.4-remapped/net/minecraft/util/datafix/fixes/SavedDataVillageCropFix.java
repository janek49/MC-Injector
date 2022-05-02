package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.References;

public class SavedDataVillageCropFix extends DataFix {
   public SavedDataVillageCropFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.writeFixAndRead("SavedDataVillageCropFix", this.getInputSchema().getType(References.STRUCTURE_FEATURE), this.getOutputSchema().getType(References.STRUCTURE_FEATURE), this::fixTag);
   }

   private Dynamic fixTag(Dynamic dynamic) {
      return dynamic.update("Children", SavedDataVillageCropFix::updateChildren);
   }

   private static Dynamic updateChildren(Dynamic dynamic) {
      Optional var10000 = dynamic.asStreamOpt().map(SavedDataVillageCropFix::updateChildren);
      dynamic.getClass();
      return (Dynamic)var10000.map(dynamic::createList).orElse(dynamic);
   }

   private static Stream updateChildren(Stream stream) {
      return stream.map((dynamic) -> {
         String var1 = dynamic.get("id").asString("");
         return "ViF".equals(var1)?updateSingleField(dynamic):("ViDF".equals(var1)?updateDoubleField(dynamic):dynamic);
      });
   }

   private static Dynamic updateSingleField(Dynamic dynamic) {
      dynamic = updateCrop(dynamic, "CA");
      return updateCrop(dynamic, "CB");
   }

   private static Dynamic updateDoubleField(Dynamic dynamic) {
      dynamic = updateCrop(dynamic, "CA");
      dynamic = updateCrop(dynamic, "CB");
      dynamic = updateCrop(dynamic, "CC");
      return updateCrop(dynamic, "CD");
   }

   private static Dynamic updateCrop(Dynamic var0, String string) {
      return var0.get(string).asNumber().isPresent()?var0.set(string, BlockStateData.getTag(var0.get(string).asInt(0) << 4)):var0;
   }
}
