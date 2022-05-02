package net.minecraft.client.renderer.chunk;

import com.fox2code.repacker.ClientJarOnly;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

@ClientJarOnly
public class VisGraph {
   private static final int DX = (int)Math.pow(16.0D, 0.0D);
   private static final int DZ = (int)Math.pow(16.0D, 1.0D);
   private static final int DY = (int)Math.pow(16.0D, 2.0D);
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BitSet bitSet = new BitSet(4096);
   private static final int[] INDEX_OF_EDGES = (int[])Util.make(new int[1352], (ints) -> {
      int var1 = 0;
      int var2 = 15;
      int var3 = 0;

      for(int var4 = 0; var4 < 16; ++var4) {
         for(int var5 = 0; var5 < 16; ++var5) {
            for(int var6 = 0; var6 < 16; ++var6) {
               if(var4 == 0 || var4 == 15 || var5 == 0 || var5 == 15 || var6 == 0 || var6 == 15) {
                  ints[var3++] = getIndex(var4, var5, var6);
               }
            }
         }
      }

   });
   private int empty = 4096;

   public void setOpaque(BlockPos opaque) {
      this.bitSet.set(getIndex(opaque), true);
      --this.empty;
   }

   private static int getIndex(BlockPos blockPos) {
      return getIndex(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
   }

   private static int getIndex(int var0, int var1, int var2) {
      return var0 << 0 | var1 << 8 | var2 << 4;
   }

   public VisibilitySet resolve() {
      VisibilitySet visibilitySet = new VisibilitySet();
      if(4096 - this.empty < 256) {
         visibilitySet.setAll(true);
      } else if(this.empty == 0) {
         visibilitySet.setAll(false);
      } else {
         for(int var5 : INDEX_OF_EDGES) {
            if(!this.bitSet.get(var5)) {
               visibilitySet.add(this.floodFill(var5));
            }
         }
      }

      return visibilitySet;
   }

   public Set floodFill(BlockPos blockPos) {
      return this.floodFill(getIndex(blockPos));
   }

   private Set floodFill(int i) {
      Set<Direction> set = EnumSet.noneOf(Direction.class);
      IntPriorityQueue var3 = new IntArrayFIFOQueue();
      var3.enqueue(i);
      this.bitSet.set(i, true);

      while(!((IntPriorityQueue)var3).isEmpty()) {
         int var4 = var3.dequeueInt();
         this.addEdges(var4, set);

         for(Direction var8 : DIRECTIONS) {
            int var9 = this.getNeighborIndexAtFace(var4, var8);
            if(var9 >= 0 && !this.bitSet.get(var9)) {
               this.bitSet.set(var9, true);
               var3.enqueue(var9);
            }
         }
      }

      return set;
   }

   private void addEdges(int var1, Set set) {
      int var3 = var1 >> 0 & 15;
      if(var3 == 0) {
         set.add(Direction.WEST);
      } else if(var3 == 15) {
         set.add(Direction.EAST);
      }

      int var4 = var1 >> 8 & 15;
      if(var4 == 0) {
         set.add(Direction.DOWN);
      } else if(var4 == 15) {
         set.add(Direction.UP);
      }

      int var5 = var1 >> 4 & 15;
      if(var5 == 0) {
         set.add(Direction.NORTH);
      } else if(var5 == 15) {
         set.add(Direction.SOUTH);
      }

   }

   private int getNeighborIndexAtFace(int var1, Direction direction) {
      switch(direction) {
      case DOWN:
         if((var1 >> 8 & 15) == 0) {
            return -1;
         }

         return var1 - DY;
      case UP:
         if((var1 >> 8 & 15) == 15) {
            return -1;
         }

         return var1 + DY;
      case NORTH:
         if((var1 >> 4 & 15) == 0) {
            return -1;
         }

         return var1 - DZ;
      case SOUTH:
         if((var1 >> 4 & 15) == 15) {
            return -1;
         }

         return var1 + DZ;
      case WEST:
         if((var1 >> 0 & 15) == 0) {
            return -1;
         }

         return var1 - DX;
      case EAST:
         if((var1 >> 0 & 15) == 15) {
            return -1;
         }

         return var1 + DX;
      default:
         return -1;
      }
   }
}
