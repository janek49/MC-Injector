package net.minecraft.world.level.levelgen;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class Heightmap {
   private static final Predicate NOT_AIR = (blockState) -> {
      return !blockState.isAir();
   };
   private static final Predicate MATERIAL_MOTION_BLOCKING = (blockState) -> {
      return blockState.getMaterial().blocksMotion();
   };
   private final BitStorage data = new BitStorage(9, 256);
   private final Predicate isOpaque;
   private final ChunkAccess chunk;

   public Heightmap(ChunkAccess chunk, Heightmap.Types heightmap$Types) {
      this.isOpaque = heightmap$Types.isOpaque();
      this.chunk = chunk;
   }

   public static void primeHeightmaps(ChunkAccess chunkAccess, Set set) {
      int var2 = set.size();
      ObjectList<Heightmap> var3 = new ObjectArrayList(var2);
      ObjectListIterator<Heightmap> var4 = var3.iterator();
      int var5 = chunkAccess.getHighestSectionPosition() + 16;
      BlockPos.PooledMutableBlockPos var6 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var7 = null;

      try {
         for(int var8 = 0; var8 < 16; ++var8) {
            for(int var9 = 0; var9 < 16; ++var9) {
               for(Heightmap.Types var11 : set) {
                  var3.add(chunkAccess.getOrCreateHeightmapUnprimed(var11));
               }

               for(int var10 = var5 - 1; var10 >= 0; --var10) {
                  var6.set(var8, var10, var9);
                  BlockState var11 = chunkAccess.getBlockState(var6);
                  if(var11.getBlock() != Blocks.AIR) {
                     while(var4.hasNext()) {
                        Heightmap var12 = (Heightmap)var4.next();
                        if(var12.isOpaque.test(var11)) {
                           var12.setHeight(var8, var9, var10 + 1);
                           var4.remove();
                        }
                     }

                     if(var3.isEmpty()) {
                        break;
                     }

                     var4.back(var2);
                  }
               }
            }
         }
      } catch (Throwable var20) {
         var7 = var20;
         throw var20;
      } finally {
         if(var6 != null) {
            if(var7 != null) {
               try {
                  var6.close();
               } catch (Throwable var19) {
                  var7.addSuppressed(var19);
               }
            } else {
               var6.close();
            }
         }

      }

   }

   public boolean update(int var1, int var2, int var3, BlockState blockState) {
      int var5 = this.getFirstAvailable(var1, var3);
      if(var2 <= var5 - 2) {
         return false;
      } else {
         if(this.isOpaque.test(blockState)) {
            if(var2 >= var5) {
               this.setHeight(var1, var3, var2 + 1);
               return true;
            }
         } else if(var5 - 1 == var2) {
            BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

            for(int var7 = var2 - 1; var7 >= 0; --var7) {
               var6.set(var1, var7, var3);
               if(this.isOpaque.test(this.chunk.getBlockState(var6))) {
                  this.setHeight(var1, var3, var7 + 1);
                  return true;
               }
            }

            this.setHeight(var1, var3, 0);
            return true;
         }

         return false;
      }
   }

   public int getFirstAvailable(int var1, int var2) {
      return this.getFirstAvailable(getIndex(var1, var2));
   }

   private int getFirstAvailable(int i) {
      return this.data.get(i);
   }

   private void setHeight(int var1, int var2, int var3) {
      this.data.set(getIndex(var1, var2), var3);
   }

   public void setRawData(long[] rawData) {
      System.arraycopy(rawData, 0, this.data.getRaw(), 0, rawData.length);
   }

   public long[] getRawData() {
      return this.data.getRaw();
   }

   private static int getIndex(int var0, int var1) {
      return var0 + var1 * 16;
   }

   public static enum Types {
      WORLD_SURFACE_WG("WORLD_SURFACE_WG", Heightmap.Usage.WORLDGEN, Heightmap.NOT_AIR),
      WORLD_SURFACE("WORLD_SURFACE", Heightmap.Usage.CLIENT, Heightmap.NOT_AIR),
      OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Heightmap.Usage.WORLDGEN, Heightmap.MATERIAL_MOTION_BLOCKING),
      OCEAN_FLOOR("OCEAN_FLOOR", Heightmap.Usage.LIVE_WORLD, Heightmap.MATERIAL_MOTION_BLOCKING),
      MOTION_BLOCKING("MOTION_BLOCKING", Heightmap.Usage.CLIENT, (blockState) -> {
         return blockState.getMaterial().blocksMotion() || !blockState.getFluidState().isEmpty();
      }),
      MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Heightmap.Usage.LIVE_WORLD, (blockState) -> {
         return (blockState.getMaterial().blocksMotion() || !blockState.getFluidState().isEmpty()) && !(blockState.getBlock() instanceof LeavesBlock);
      });

      private final String serializationKey;
      private final Heightmap.Usage usage;
      private final Predicate isOpaque;
      private static final Map REVERSE_LOOKUP = (Map)Util.make(Maps.newHashMap(), (hashMap) -> {
         for(Heightmap.Types var4 : values()) {
            hashMap.put(var4.serializationKey, var4);
         }

      });

      private Types(String serializationKey, Heightmap.Usage usage, Predicate isOpaque) {
         this.serializationKey = serializationKey;
         this.usage = usage;
         this.isOpaque = isOpaque;
      }

      public String getSerializationKey() {
         return this.serializationKey;
      }

      public boolean sendToClient() {
         return this.usage == Heightmap.Usage.CLIENT;
      }

      public boolean keepAfterWorldgen() {
         return this.usage != Heightmap.Usage.WORLDGEN;
      }

      public static Heightmap.Types getFromKey(String string) {
         return (Heightmap.Types)REVERSE_LOOKUP.get(string);
      }

      public Predicate isOpaque() {
         return this.isOpaque;
      }
   }

   public static enum Usage {
      WORLDGEN,
      LIVE_WORLD,
      CLIENT;
   }
}
