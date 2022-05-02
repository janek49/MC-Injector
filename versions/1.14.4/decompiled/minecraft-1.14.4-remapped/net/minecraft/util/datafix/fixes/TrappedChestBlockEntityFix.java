package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.LeavesFix;
import net.minecraft.util.datafix.fixes.References;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrappedChestBlockEntityFix extends DataFix {
   private static final Logger LOGGER = LogManager.getLogger();

   public TrappedChestBlockEntityFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getOutputSchema().getType(References.CHUNK);
      Type<?> var2 = var1.findFieldType("Level");
      Type<?> var3 = var2.findFieldType("TileEntities");
      if(!(var3 instanceof ListType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         ListType<?> var4 = (ListType)var3;
         OpticFinder<? extends List<?>> var5 = DSL.fieldFinder("TileEntities", var4);
         Type<?> var6 = this.getInputSchema().getType(References.CHUNK);
         OpticFinder<?> var7 = var6.findField("Level");
         OpticFinder<?> var8 = var7.type().findField("Sections");
         Type<?> var9 = var8.type();
         if(!(var9 instanceof ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
         } else {
            Type<?> var10 = ((ListType)var9).getElement();
            OpticFinder<?> var11 = DSL.typeFinder(var10);
            return TypeRewriteRule.seq((new AddNewChoices(this.getOutputSchema(), "AddTrappedChestFix", References.BLOCK_ENTITY)).makeRule(), this.fixTypeEverywhereTyped("Trapped Chest fix", var6, (var5x) -> {
               return var5x.updateTyped(var7, (var4) -> {
                  Optional<? extends Typed<?>> var5 = var4.getOptionalTyped(var8);
                  if(!var5.isPresent()) {
                     return var4;
                  } else {
                     List<? extends Typed<?>> var6 = ((Typed)var5.get()).getAllTyped(var11);
                     IntSet var7 = new IntOpenHashSet();

                     for(Typed<?> var9 : var6) {
                        TrappedChestBlockEntityFix.TrappedChestSection var10 = new TrappedChestBlockEntityFix.TrappedChestSection(var9, this.getInputSchema());
                        if(!var10.isSkippable()) {
                           for(int var11 = 0; var11 < 4096; ++var11) {
                              int var12 = var10.getBlock(var11);
                              if(var10.isTrappedChest(var12)) {
                                 var7.add(var10.getIndex() << 12 | var11);
                              }
                           }
                        }
                     }

                     Dynamic<?> var8 = (Dynamic)var4.get(DSL.remainderFinder());
                     int var9 = var8.get("xPos").asInt(0);
                     int var10 = var8.get("zPos").asInt(0);
                     TaggedChoiceType<String> var11 = this.getInputSchema().findChoiceType(References.BLOCK_ENTITY);
                     return var4.updateTyped(var5, (var4) -> {
                        return var4.updateTyped(var11xx.finder(), (var4) -> {
                           Dynamic<?> var5 = (Dynamic)var4.getOrCreate(DSL.remainderFinder());
                           int var6 = var5.get("x").asInt(0) - (var9 << 4);
                           int var7 = var5.get("y").asInt(0);
                           int var8 = var5.get("z").asInt(0) - (var10 << 4);
                           return var7.contains(LeavesFix.getIndex(var6, var7, var8))?var4.update(var11xx.finder(), (pair) -> {
                              return pair.mapFirst((string) -> {
                                 if(!Objects.equals(string, "minecraft:chest")) {
                                    LOGGER.warn("Block Entity was expected to be a chest");
                                 }

                                 return "minecraft:trapped_chest";
                              });
                           }):var4;
                        });
                     });
                  }
               });
            }));
         }
      }
   }

   public static final class TrappedChestSection extends LeavesFix.Section {
      @Nullable
      private IntSet chestIds;

      public TrappedChestSection(Typed typed, Schema schema) {
         super(typed, schema);
      }

      protected boolean skippable() {
         this.chestIds = new IntOpenHashSet();

         for(int var1 = 0; var1 < this.palette.size(); ++var1) {
            Dynamic<?> var2 = (Dynamic)this.palette.get(var1);
            String var3 = var2.get("Name").asString("");
            if(Objects.equals(var3, "minecraft:trapped_chest")) {
               this.chestIds.add(var1);
            }
         }

         return this.chestIds.isEmpty();
      }

      public boolean isTrappedChest(int i) {
         return this.chestIds.contains(i);
      }
   }
}
