package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import java.util.function.Function;

public class AddNewChoices extends DataFix {
   private final String name;
   private final TypeReference type;

   public AddNewChoices(Schema schema, String name, TypeReference type) {
      super(schema, true);
      this.name = name;
      this.type = type;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<?> var1 = this.getInputSchema().findChoiceType(this.type);
      TaggedChoiceType<?> var2 = this.getOutputSchema().findChoiceType(this.type);
      return this.cap(this.name, var1, var2);
   }

   protected final TypeRewriteRule cap(String string, TaggedChoiceType var2, TaggedChoiceType var3) {
      if(var2.getKeyType() != var3.getKeyType()) {
         throw new IllegalStateException("Could not inject: key type is not the same");
      } else {
         return this.fixTypeEverywhere(string, var2, var3, (dynamicOps) -> {
            return (var2) -> {
               if(!var3.hasType(var2.getFirst())) {
                  throw new IllegalArgumentException(String.format("Unknown type %s in %s ", new Object[]{var2.getFirst(), this.type}));
               } else {
                  return var2;
               }
            };
         });
      }
   }
}
