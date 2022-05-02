package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class ChunkToProtochunkFix extends DataFix {
   public ChunkToProtochunkFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.CHUNK);
      Type<?> var2 = this.getOutputSchema().getType(References.CHUNK);
      Type<?> var3 = var1.findFieldType("Level");
      Type<?> var4 = var2.findFieldType("Level");
      Type<?> var5 = var3.findFieldType("TileTicks");
      OpticFinder<?> var6 = DSL.fieldFinder("Level", var3);
      OpticFinder<?> var7 = DSL.fieldFinder("TileTicks", var5);
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ChunkToProtoChunkFix", var1, this.getOutputSchema().getType(References.CHUNK), (var3) -> {
         return var3.updateTyped(var6, var4, (var2) -> {
            Optional<? extends Stream<? extends Dynamic<?>>> var3 = var2.getOptionalTyped(var7).map(Typed::write).flatMap(Dynamic::asStreamOpt);
            Dynamic<?> var4 = (Dynamic)var2.get(DSL.remainderFinder());
            boolean var5 = var4.get("TerrainPopulated").asBoolean(false) && (!var4.get("LightPopulated").asNumber().isPresent() || var4.get("LightPopulated").asBoolean(false));
            var4 = var4.set("Status", var4.createString(var5?"mobs_spawned":"empty"));
            var4 = var4.set("hasLegacyStructureData", var4.createBoolean(true));
            Dynamic<?> var6;
            if(var5) {
               Optional<ByteBuffer> var7 = var4.get("Biomes").asByteBufferOpt();
               if(var7.isPresent()) {
                  ByteBuffer var8 = (ByteBuffer)var7.get();
                  int[] vars9 = new int[256];

                  for(int var10 = 0; var10 < vars9.length; ++var10) {
                     if(var10 < var8.capacity()) {
                        vars9[var10] = var8.get(var10) & 255;
                     }
                  }

                  var4 = var4.set("Biomes", var4.createIntList(Arrays.stream(vars9)));
               }

               List<Dynamic<?>> var9 = (List)IntStream.range(0, 16).mapToObj((var1) -> {
                  return var4x.createList(Stream.empty());
               }).collect(Collectors.toList());
               if(var3.isPresent()) {
                  ((Stream)var3.get()).forEach((var2) -> {
                     int var3 = var2.get("x").asInt(0);
                     int var4 = var2.get("y").asInt(0);
                     int var5 = var2.get("z").asInt(0);
                     short var6 = packOffsetCoordinates(var3, var4, var5);
                     var9.set(var4 >> 4, ((Dynamic)var9.get(var4 >> 4)).merge(var4x.createShort(var6)));
                  });
                  var4 = var4.set("ToBeTicked", var4.createList(var9.stream()));
               }

               var6 = var2.set(DSL.remainderFinder(), var4).write();
            } else {
               var6 = var4;
            }

            return (Typed)((Optional)var4.readTyped(var6).getSecond()).orElseThrow(() -> {
               return new IllegalStateException("Could not read the new chunk");
            });
         });
      }), this.writeAndRead("Structure biome inject", this.getInputSchema().getType(References.STRUCTURE_FEATURE), this.getOutputSchema().getType(References.STRUCTURE_FEATURE)));
   }

   private static short packOffsetCoordinates(int var0, int var1, int var2) {
      return (short)(var0 & 15 | (var1 & 15) << 4 | (var2 & 15) << 8);
   }
}
