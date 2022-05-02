package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import net.minecraft.resources.ResourceLocation;

public class NamespacedSchema extends Schema {
   public NamespacedSchema(int var1, Schema schema) {
      super(var1, schema);
   }

   public static String ensureNamespaced(String string) {
      ResourceLocation var1 = ResourceLocation.tryParse(string);
      return var1 != null?var1.toString():string;
   }

   public Type getChoiceType(TypeReference dSL$TypeReference, String string) {
      return super.getChoiceType(dSL$TypeReference, ensureNamespaced(string));
   }
}
