package net.minecraft.world.entity.ai.gossip;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.world.entity.ai.gossip.GossipType;

public class GossipContainer {
   private final Map gossips = Maps.newHashMap();

   public void decay() {
      Iterator<GossipContainer.EntityGossips> var1 = this.gossips.values().iterator();

      while(var1.hasNext()) {
         GossipContainer.EntityGossips var2 = (GossipContainer.EntityGossips)var1.next();
         var2.decay();
         if(var2.isEmpty()) {
            var1.remove();
         }
      }

   }

   private Stream unpack() {
      return this.gossips.entrySet().stream().flatMap((map$Entry) -> {
         return ((GossipContainer.EntityGossips)map$Entry.getValue()).unpack((UUID)map$Entry.getKey());
      });
   }

   private Collection selectGossipsForTransfer(Random random, int var2) {
      List<GossipContainer.GossipEntry> var3 = (List)this.unpack().collect(Collectors.toList());
      if(var3.isEmpty()) {
         return Collections.emptyList();
      } else {
         int[] vars4 = new int[var3.size()];
         int var5 = 0;

         for(int var6 = 0; var6 < var3.size(); ++var6) {
            GossipContainer.GossipEntry var7 = (GossipContainer.GossipEntry)var3.get(var6);
            var5 += Math.abs(var7.weightedValue());
            vars4[var6] = var5 - 1;
         }

         Set<GossipContainer.GossipEntry> var6 = Sets.newIdentityHashSet();

         for(int var7 = 0; var7 < var2; ++var7) {
            int var8 = random.nextInt(var5);
            int var9 = Arrays.binarySearch(vars4, var8);
            var6.add(var3.get(var9 < 0?-var9 - 1:var9));
         }

         return var6;
      }
   }

   private GossipContainer.EntityGossips getOrCreate(UUID uUID) {
      return (GossipContainer.EntityGossips)this.gossips.computeIfAbsent(uUID, (uUID) -> {
         return new GossipContainer.EntityGossips();
      });
   }

   public void transferFrom(GossipContainer gossipContainer, Random random, int var3) {
      Collection<GossipContainer.GossipEntry> var4 = gossipContainer.selectGossipsForTransfer(random, var3);
      var4.forEach((gossipContainer$GossipEntry) -> {
         int var2 = gossipContainer$GossipEntry.value - gossipContainer$GossipEntry.type.decayPerTransfer;
         if(var2 >= 2) {
            this.getOrCreate(gossipContainer$GossipEntry.target).entries.mergeInt(gossipContainer$GossipEntry.type, var2, GossipContainer::mergeValuesForTransfer);
         }

      });
   }

   public int getReputation(UUID uUID, Predicate predicate) {
      GossipContainer.EntityGossips var3 = (GossipContainer.EntityGossips)this.gossips.get(uUID);
      return var3 != null?var3.weightedValue(predicate):0;
   }

   public void add(UUID uUID, GossipType gossipType, int var3) {
      GossipContainer.EntityGossips var4 = this.getOrCreate(uUID);
      var4.entries.mergeInt(gossipType, var3, (var2, var3) -> {
         return Integer.valueOf(this.mergeValuesForAddition(gossipType, var2.intValue(), var3.intValue()));
      });
      var4.makeSureValueIsntTooLowOrTooHigh(gossipType);
      if(var4.isEmpty()) {
         this.gossips.remove(uUID);
      }

   }

   public Dynamic store(DynamicOps dynamicOps) {
      return new Dynamic(dynamicOps, dynamicOps.createList(this.unpack().map((gossipContainer$GossipEntry) -> {
         return gossipContainer$GossipEntry.store(dynamicOps);
      }).map(Dynamic::getValue)));
   }

   public void update(Dynamic dynamic) {
      dynamic.asStream().map(GossipContainer.GossipEntry::load).flatMap(Util::toStream).forEach((gossipContainer$GossipEntry) -> {
         this.getOrCreate(gossipContainer$GossipEntry.target).entries.put(gossipContainer$GossipEntry.type, gossipContainer$GossipEntry.value);
      });
   }

   private static int mergeValuesForTransfer(int var0, int var1) {
      return Math.max(var0, var1);
   }

   private int mergeValuesForAddition(GossipType gossipType, int var2, int var3) {
      int var4 = var2 + var3;
      return var4 > gossipType.max?Math.max(gossipType.max, var2):var4;
   }

   static class EntityGossips {
      private final Object2IntMap entries;

      private EntityGossips() {
         this.entries = new Object2IntOpenHashMap();
      }

      public int weightedValue(Predicate predicate) {
         return this.entries.object2IntEntrySet().stream().filter((object2IntMap$Entry) -> {
            return predicate.test(object2IntMap$Entry.getKey());
         }).mapToInt((object2IntMap$Entry) -> {
            return object2IntMap$Entry.getIntValue() * ((GossipType)object2IntMap$Entry.getKey()).weight;
         }).sum();
      }

      public Stream unpack(UUID uUID) {
         return this.entries.object2IntEntrySet().stream().map((object2IntMap$Entry) -> {
            return new GossipContainer.GossipEntry(uUID, (GossipType)object2IntMap$Entry.getKey(), object2IntMap$Entry.getIntValue());
         });
      }

      public void decay() {
         ObjectIterator<Entry<GossipType>> var1 = this.entries.object2IntEntrySet().iterator();

         while(var1.hasNext()) {
            Entry<GossipType> var2 = (Entry)var1.next();
            int var3 = var2.getIntValue() - ((GossipType)var2.getKey()).decayPerDay;
            if(var3 < 2) {
               var1.remove();
            } else {
               var2.setValue(var3);
            }
         }

      }

      public boolean isEmpty() {
         return this.entries.isEmpty();
      }

      public void makeSureValueIsntTooLowOrTooHigh(GossipType gossipType) {
         int var2 = this.entries.getInt(gossipType);
         if(var2 > gossipType.max) {
            this.entries.put(gossipType, gossipType.max);
         }

         if(var2 < 2) {
            this.remove(gossipType);
         }

      }

      public void remove(GossipType gossipType) {
         this.entries.removeInt(gossipType);
      }
   }

   static class GossipEntry {
      public final UUID target;
      public final GossipType type;
      public final int value;

      public GossipEntry(UUID target, GossipType type, int value) {
         this.target = target;
         this.type = type;
         this.value = value;
      }

      public int weightedValue() {
         return this.value * this.type.weight;
      }

      public String toString() {
         return "GossipEntry{target=" + this.target + ", type=" + this.type + ", value=" + this.value + '}';
      }

      public Dynamic store(DynamicOps dynamicOps) {
         return Util.writeUUID("Target", this.target, new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("Type"), dynamicOps.createString(this.type.id), dynamicOps.createString("Value"), dynamicOps.createInt(this.value)))));
      }

      public static Optional load(Dynamic dynamic) {
         return dynamic.get("Type").asString().map(GossipType::byId).flatMap((gossipType) -> {
            return Util.readUUID("Target", dynamic).flatMap((uUID) -> {
               return dynamic.get("Value").asNumber().map((number) -> {
                  return new GossipContainer.GossipEntry(uUID, gossipType, number.intValue());
               });
            });
         });
      }
   }
}
