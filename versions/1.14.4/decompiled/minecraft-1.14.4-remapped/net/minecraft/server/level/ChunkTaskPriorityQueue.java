package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.level.ChunkPos;

public class ChunkTaskPriorityQueue {
   public static final int PRIORITY_LEVEL_COUNT = ChunkMap.MAX_CHUNK_DISTANCE + 2;
   private final List taskQueue;
   private volatile int firstQueue;
   private final String name;
   private final LongSet acquired;
   private final int maxTasks;

   public ChunkTaskPriorityQueue(String name, int maxTasks) {
      this.taskQueue = (List)IntStream.range(0, PRIORITY_LEVEL_COUNT).mapToObj((i) -> {
         return new Long2ObjectLinkedOpenHashMap();
      }).collect(Collectors.toList());
      this.firstQueue = PRIORITY_LEVEL_COUNT;
      this.acquired = new LongOpenHashSet();
      this.name = name;
      this.maxTasks = maxTasks;
   }

   protected void resortChunkTasks(int var1, ChunkPos chunkPos, int var3) {
      if(var1 < PRIORITY_LEVEL_COUNT) {
         Long2ObjectLinkedOpenHashMap<List<Optional<T>>> var4 = (Long2ObjectLinkedOpenHashMap)this.taskQueue.get(var1);
         List<Optional<T>> var5 = (List)var4.remove(chunkPos.toLong());
         if(var1 == this.firstQueue) {
            while(this.firstQueue < PRIORITY_LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap)this.taskQueue.get(this.firstQueue)).isEmpty()) {
               ++this.firstQueue;
            }
         }

         if(var5 != null && !var5.isEmpty()) {
            ((List)((Long2ObjectLinkedOpenHashMap)this.taskQueue.get(var3)).computeIfAbsent(chunkPos.toLong(), (l) -> {
               return Lists.newArrayList();
            })).addAll(var5);
            this.firstQueue = Math.min(this.firstQueue, var3);
         }

      }
   }

   protected void submit(Optional optional, long var2, int var4) {
      ((List)((Long2ObjectLinkedOpenHashMap)this.taskQueue.get(var4)).computeIfAbsent(var2, (l) -> {
         return Lists.newArrayList();
      })).add(optional);
      this.firstQueue = Math.min(this.firstQueue, var4);
   }

   protected void release(long var1, boolean var3) {
      for(Long2ObjectLinkedOpenHashMap<List<Optional<T>>> var5 : this.taskQueue) {
         List<Optional<T>> var6 = (List)var5.get(var1);
         if(var6 != null) {
            if(var3) {
               var6.clear();
            } else {
               var6.removeIf((optional) -> {
                  return !optional.isPresent();
               });
            }

            if(var6.isEmpty()) {
               var5.remove(var1);
            }
         }
      }

      while(this.firstQueue < PRIORITY_LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap)this.taskQueue.get(this.firstQueue)).isEmpty()) {
         ++this.firstQueue;
      }

      this.acquired.remove(var1);
   }

   private Runnable acquire(long l) {
      return () -> {
         this.acquired.add(l);
      };
   }

   @Nullable
   public Stream pop() {
      if(this.acquired.size() >= this.maxTasks) {
         return null;
      } else if(this.firstQueue >= PRIORITY_LEVEL_COUNT) {
         return null;
      } else {
         int var1 = this.firstQueue;
         Long2ObjectLinkedOpenHashMap<List<Optional<T>>> var2 = (Long2ObjectLinkedOpenHashMap)this.taskQueue.get(var1);
         long var3 = var2.firstLongKey();

         List<Optional<T>> var5;
         for(var5 = (List)var2.removeFirst(); this.firstQueue < PRIORITY_LEVEL_COUNT && ((Long2ObjectLinkedOpenHashMap)this.taskQueue.get(this.firstQueue)).isEmpty(); ++this.firstQueue) {
            ;
         }

         return var5.stream().map((optional) -> {
            return (Either)optional.map(Either::left).orElseGet(() -> {
               return Either.right(this.acquire(var3));
            });
         });
      }
   }

   public String toString() {
      return this.name + " " + this.firstQueue + "...";
   }

   @VisibleForTesting
   LongSet getAcquired() {
      return new LongOpenHashSet(this.acquired);
   }
}
