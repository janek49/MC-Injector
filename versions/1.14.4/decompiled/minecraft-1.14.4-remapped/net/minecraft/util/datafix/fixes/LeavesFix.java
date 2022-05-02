package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import javax.annotation.Nullable;
import net.minecraft.util.BitStorage;
import net.minecraft.util.datafix.fixes.References;

public class LeavesFix extends DataFix {
   private static final int[][] DIRECTIONS = new int[][]{{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};
   private static final Object2IntMap LEAVES = (Object2IntMap)DataFixUtils.make(new Object2IntOpenHashMap(), (object2IntOpenHashMap) -> {
      object2IntOpenHashMap.put("minecraft:acacia_leaves", 0);
      object2IntOpenHashMap.put("minecraft:birch_leaves", 1);
      object2IntOpenHashMap.put("minecraft:dark_oak_leaves", 2);
      object2IntOpenHashMap.put("minecraft:jungle_leaves", 3);
      object2IntOpenHashMap.put("minecraft:oak_leaves", 4);
      object2IntOpenHashMap.put("minecraft:spruce_leaves", 5);
   });
   private static final Set LOGS = ImmutableSet.of("minecraft:acacia_bark", "minecraft:birch_bark", "minecraft:dark_oak_bark", "minecraft:jungle_bark", "minecraft:oak_bark", "minecraft:spruce_bark", new String[]{"minecraft:acacia_log", "minecraft:birch_log", "minecraft:dark_oak_log", "minecraft:jungle_log", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:stripped_acacia_log", "minecraft:stripped_birch_log", "minecraft:stripped_dark_oak_log", "minecraft:stripped_jungle_log", "minecraft:stripped_oak_log", "minecraft:stripped_spruce_log"});

   public LeavesFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.CHUNK);
      OpticFinder<?> var2 = var1.findField("Level");
      OpticFinder<?> var3 = var2.type().findField("Sections");
      Type<?> var4 = var3.type();
      if(!(var4 instanceof ListType)) {
         throw new IllegalStateException("Expecting sections to be a list.");
      } else {
         Type<?> var5 = ((ListType)var4).getElement();
         OpticFinder<?> var6 = DSL.typeFinder(var5);
         return this.fixTypeEverywhereTyped("Leaves fix", var1, (var4) -> {
            return var4.updateTyped(var2, (var3x) -> {
               int[] vars4 = new int[]{0};
               Typed<?> var5 = var3x.updateTyped(var3, (var3) -> {
                  Int2ObjectMap<LeavesFix.LeavesSection> var4 = new Int2ObjectOpenHashMap((Map)var3.getAllTyped(var6).stream().map((typed) -> {
                     return new LeavesFix.LeavesSection(typed, this.getInputSchema());
                  }).collect(Collectors.toMap(LeavesFix.Section::getIndex, (leavesFix$LeavesSection) -> {
                     return leavesFix$LeavesSection;
                  })));
                  if(var4.values().stream().allMatch(LeavesFix.Section::isSkippable)) {
                     return var3;
                  } else {
                     List<IntSet> var5 = Lists.newArrayList();

                     for(int var6 = 0; var6 < 7; ++var6) {
                        var5.add(new IntOpenHashSet());
                     }

                     ObjectIterator var6x = var4.values().iterator();

                     while(var6x.hasNext()) {
                        LeavesFix.LeavesSection var7 = (LeavesFix.LeavesSection)var6x.next();
                        if(!var7.isSkippable()) {
                           for(int var8 = 0; var8 < 4096; ++var8) {
                              int var9 = var7.getBlock(var8);
                              if(var7.isLog(var9)) {
                                 ((IntSet)var5.get(0)).add(var7.getIndex() << 12 | var8);
                              } else if(var7.isLeaf(var9)) {
                                 int var10 = this.getX(var8);
                                 int var11 = this.getZ(var8);
                                 vars4[0] |= getSideMask(var10 == 0, var10 == 15, var11 == 0, var11 == 15);
                              }
                           }
                        }
                     }

                     for(int var6 = 1; var6 < 7; ++var6) {
                        IntSet var7 = (IntSet)var5.get(var6 - 1);
                        IntSet var8 = (IntSet)var5.get(var6);
                        IntIterator var9 = var7.iterator();

                        while(var9.hasNext()) {
                           int var10 = var9.nextInt();
                           int var11 = this.getX(var10);
                           int var12 = this.getY(var10);
                           int var13 = this.getZ(var10);

                           for(int[] vars17 : DIRECTIONS) {
                              int var18 = var11 + vars17[0];
                              int var19 = var12 + vars17[1];
                              int var20 = var13 + vars17[2];
                              if(var18 >= 0 && var18 <= 15 && var20 >= 0 && var20 <= 15 && var19 >= 0 && var19 <= 255) {
                                 LeavesFix.LeavesSection var21 = (LeavesFix.LeavesSection)var4.get(var19 >> 4);
                                 if(var21 != null && !var21.isSkippable()) {
                                    int var22 = getIndex(var18, var19 & 15, var20);
                                    int var23 = var21.getBlock(var22);
                                    if(var21.isLeaf(var23)) {
                                       int var24 = var21.getDistance(var23);
                                       if(var24 > var6) {
                                          var21.setDistance(var22, var23, var6);
                                          var8.add(getIndex(var18, var19, var20));
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }

                     return var3.updateTyped(var6, (var1) -> {
                        return ((LeavesFix.LeavesSection)var4.get(((Dynamic)var1.get(DSL.remainderFinder())).get("Y").asInt(0))).write(var1);
                     });
                  }
               });
               if(vars4[0] != 0) {
                  var5 = var5.update(DSL.remainderFinder(), (var1) -> {
                     Dynamic<?> var2 = (Dynamic)DataFixUtils.orElse(var1.get("UpgradeData").get(), var1.emptyMap());
                     return var1.set("UpgradeData", var2.set("Sides", var1.createByte((byte)(var2.get("Sides").asByte((byte)0) | vars4[0]))));
                  });
               }

               return var5;
            });
         });
      }
   }

   public static int getIndex(int var0, int var1, int var2) {
      return var1 << 8 | var2 << 4 | var0;
   }

   private int getX(int i) {
      return i & 15;
   }

   private int getY(int i) {
      return i >> 8 & 255;
   }

   private int getZ(int i) {
      return i >> 4 & 15;
   }

   public static int getSideMask(boolean var0, boolean var1, boolean var2, boolean var3) {
      int var4 = 0;
      if(var2) {
         if(var1) {
            var4 |= 2;
         } else if(var0) {
            var4 |= 128;
         } else {
            var4 |= 1;
         }
      } else if(var3) {
         if(var0) {
            var4 |= 32;
         } else if(var1) {
            var4 |= 8;
         } else {
            var4 |= 16;
         }
      } else if(var1) {
         var4 |= 4;
      } else if(var0) {
         var4 |= 64;
      }

      return var4;
   }

   public static final class LeavesSection extends LeavesFix.Section {
      @Nullable
      private IntSet leaveIds;
      @Nullable
      private IntSet logIds;
      @Nullable
      private Int2IntMap stateToIdMap;

      public LeavesSection(Typed typed, Schema schema) {
         super(typed, schema);
      }

      protected boolean skippable() {
         this.leaveIds = new IntOpenHashSet();
         this.logIds = new IntOpenHashSet();
         this.stateToIdMap = new Int2IntOpenHashMap();

         for(int var1 = 0; var1 < this.palette.size(); ++var1) {
            Dynamic<?> var2 = (Dynamic)this.palette.get(var1);
            String var3 = var2.get("Name").asString("");
            if(LeavesFix.LEAVES.containsKey(var3)) {
               boolean var4 = Objects.equals(var2.get("Properties").get("decayable").asString(""), "false");
               this.leaveIds.add(var1);
               this.stateToIdMap.put(this.getStateId(var3, var4, 7), var1);
               this.palette.set(var1, this.makeLeafTag(var2, var3, var4, 7));
            }

            if(LeavesFix.LOGS.contains(var3)) {
               this.logIds.add(var1);
            }
         }

         return this.leaveIds.isEmpty() && this.logIds.isEmpty();
      }

      private Dynamic makeLeafTag(Dynamic var1, String string, boolean var3, int var4) {
         Dynamic<?> var5 = var1.emptyMap();
         var5 = var5.set("persistent", var5.createString(var3?"true":"false"));
         var5 = var5.set("distance", var5.createString(Integer.toString(var4)));
         Dynamic<?> var6 = var1.emptyMap();
         var6 = var6.set("Properties", var5);
         var6 = var6.set("Name", var6.createString(string));
         return var6;
      }

      public boolean isLog(int i) {
         return this.logIds.contains(i);
      }

      public boolean isLeaf(int i) {
         return this.leaveIds.contains(i);
      }

      private int getDistance(int i) {
         return this.isLog(i)?0:Integer.parseInt(((Dynamic)this.palette.get(i)).get("Properties").get("distance").asString(""));
      }

      private void setDistance(int var1, int var2, int var3) {
         Dynamic<?> var4 = (Dynamic)this.palette.get(var2);
         String var5 = var4.get("Name").asString("");
         boolean var6 = Objects.equals(var4.get("Properties").get("persistent").asString(""), "true");
         int var7 = this.getStateId(var5, var6, var3);
         if(!this.stateToIdMap.containsKey(var7)) {
            int var8 = this.palette.size();
            this.leaveIds.add(var8);
            this.stateToIdMap.put(var7, var8);
            this.palette.add(this.makeLeafTag(var4, var5, var6, var3));
         }

         int var8 = this.stateToIdMap.get(var7);
         if(1 << this.storage.getBits() <= var8) {
            BitStorage var9 = new BitStorage(this.storage.getBits() + 1, 4096);

            for(int var10 = 0; var10 < 4096; ++var10) {
               var9.set(var10, this.storage.get(var10));
            }

            this.storage = var9;
         }

         this.storage.set(var1, var8);
      }
   }

   public abstract static class Section {
      private final Type blockStateType = DSL.named(References.BLOCK_STATE.typeName(), DSL.remainderType());
      protected final OpticFinder paletteFinder;
      protected final List palette;
      protected final int index;
      @Nullable
      protected BitStorage storage;

      public Section(Typed typed, Schema schema) {
         this.paletteFinder = DSL.fieldFinder("Palette", DSL.list(this.blockStateType));
         if(!Objects.equals(schema.getType(References.BLOCK_STATE), this.blockStateType)) {
            throw new IllegalStateException("Block state type is not what was expected.");
         } else {
            Optional<List<Pair<String, Dynamic<?>>>> var3 = typed.getOptional(this.paletteFinder);
            this.palette = (List)var3.map((list) -> {
               return (List)list.stream().map(Pair::getSecond).collect(Collectors.toList());
            }).orElse(ImmutableList.of());
            Dynamic<?> var4 = (Dynamic)typed.get(DSL.remainderFinder());
            this.index = var4.get("Y").asInt(0);
            this.readStorage(var4);
         }
      }

      protected void readStorage(Dynamic dynamic) {
         if(this.skippable()) {
            this.storage = null;
         } else {
            long[] vars2 = ((LongStream)dynamic.get("BlockStates").asLongStreamOpt().get()).toArray();
            int var3 = Math.max(4, DataFixUtils.ceillog2(this.palette.size()));
            this.storage = new BitStorage(var3, 4096, vars2);
         }

      }

      public Typed write(Typed typed) {
         return this.isSkippable()?typed:typed.update(DSL.remainderFinder(), (dynamic) -> {
            return dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(this.storage.getRaw())));
         }).set(this.paletteFinder, this.palette.stream().map((dynamic) -> {
            return Pair.of(References.BLOCK_STATE.typeName(), dynamic);
         }).collect(Collectors.toList()));
      }

      public boolean isSkippable() {
         return this.storage == null;
      }

      public int getBlock(int i) {
         return this.storage.get(i);
      }

      protected int getStateId(String string, boolean var2, int var3) {
         return LeavesFix.LEAVES.get(string).intValue() << 5 | (var2?16:0) | var3;
      }

      int getIndex() {
         return this.index;
      }

      protected abstract boolean skippable();
   }
}
