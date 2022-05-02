package net.minecraft.world.level;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;

public class ChunkPos {
   public static final long INVALID_CHUNK_POS = asLong(1875016, 1875016);
   public final int x;
   public final int z;

   public ChunkPos(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public ChunkPos(BlockPos blockPos) {
      this.x = blockPos.getX() >> 4;
      this.z = blockPos.getZ() >> 4;
   }

   public ChunkPos(long l) {
      this.x = (int)l;
      this.z = (int)(l >> 32);
   }

   public long toLong() {
      return asLong(this.x, this.z);
   }

   public static long asLong(int var0, int var1) {
      return (long)var0 & 4294967295L | ((long)var1 & 4294967295L) << 32;
   }

   public static int getX(long l) {
      return (int)(l & 4294967295L);
   }

   public static int getZ(long l) {
      return (int)(l >>> 32 & 4294967295L);
   }

   public int hashCode() {
      int var1 = 1664525 * this.x + 1013904223;
      int var2 = 1664525 * (this.z ^ -559038737) + 1013904223;
      return var1 ^ var2;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof ChunkPos)) {
         return false;
      } else {
         ChunkPos var2 = (ChunkPos)object;
         return this.x == var2.x && this.z == var2.z;
      }
   }

   public int getMinBlockX() {
      return this.x << 4;
   }

   public int getMinBlockZ() {
      return this.z << 4;
   }

   public int getMaxBlockX() {
      return (this.x << 4) + 15;
   }

   public int getMaxBlockZ() {
      return (this.z << 4) + 15;
   }

   public int getRegionX() {
      return this.x >> 5;
   }

   public int getRegionZ() {
      return this.z >> 5;
   }

   public int getRegionLocalX() {
      return this.x & 31;
   }

   public int getRegionLocalZ() {
      return this.z & 31;
   }

   public BlockPos getBlockAt(int var1, int var2, int var3) {
      return new BlockPos((this.x << 4) + var1, var2, (this.z << 4) + var3);
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public BlockPos getWorldPosition() {
      return new BlockPos(this.x << 4, 0, this.z << 4);
   }

   public static Stream rangeClosed(ChunkPos chunkPos, int var1) {
      return rangeClosed(new ChunkPos(chunkPos.x - var1, chunkPos.z - var1), new ChunkPos(chunkPos.x + var1, chunkPos.z + var1));
   }

   public static Stream rangeClosed(final ChunkPos var0, final ChunkPos var1) {
      int var2 = Math.abs(var0.x - var1.x) + 1;
      int var3 = Math.abs(var0.z - var1.z) + 1;
      final int var4 = var0.x < var1.x?1:-1;
      final int var5 = var0.z < var1.z?1:-1;
      return StreamSupport.stream(new AbstractSpliterator((long)(var2 * var3), (int)var0) {
         @Nullable
         private ChunkPos pos;

         public boolean tryAdvance(Consumer consumer) {
            if(this.pos == null) {
               this.pos = var0;
            } else {
               int var2 = this.pos.x;
               int var3 = this.pos.z;
               if(var2 == var1.x) {
                  if(var3 == var1.z) {
                     return false;
                  }

                  this.pos = new ChunkPos(var0.x, var3 + var5);
               } else {
                  this.pos = new ChunkPos(var2 + var4, var3);
               }
            }

            consumer.accept(this.pos);
            return true;
         }
      }, false);
   }
}
