package net.minecraft.world.level.pathfinder;

import net.minecraft.world.level.pathfinder.Node;

public class BinaryHeap {
   private Node[] heap = new Node[128];
   private int size;

   public Node insert(Node node) {
      if(node.heapIdx >= 0) {
         throw new IllegalStateException("OW KNOWS!");
      } else {
         if(this.size == this.heap.length) {
            Node[] vars2 = new Node[this.size << 1];
            System.arraycopy(this.heap, 0, vars2, 0, this.size);
            this.heap = vars2;
         }

         this.heap[this.size] = node;
         node.heapIdx = this.size;
         this.upHeap(this.size++);
         return node;
      }
   }

   public void clear() {
      this.size = 0;
   }

   public Node pop() {
      Node node = this.heap[0];
      this.heap[0] = this.heap[--this.size];
      this.heap[this.size] = null;
      if(this.size > 0) {
         this.downHeap(0);
      }

      node.heapIdx = -1;
      return node;
   }

   public void changeCost(Node node, float var2) {
      float var3 = node.f;
      node.f = var2;
      if(var2 < var3) {
         this.upHeap(node.heapIdx);
      } else {
         this.downHeap(node.heapIdx);
      }

   }

   private void upHeap(int i) {
      Node var2 = this.heap[i];

      int var4;
      for(float var3 = var2.f; i > 0; i = var4) {
         var4 = i - 1 >> 1;
         Node var5 = this.heap[var4];
         if(var3 >= var5.f) {
            break;
         }

         this.heap[i] = var5;
         var5.heapIdx = i;
      }

      this.heap[i] = var2;
      var2.heapIdx = i;
   }

   private void downHeap(int i) {
      Node var2 = this.heap[i];
      float var3 = var2.f;

      while(true) {
         int var4 = 1 + (i << 1);
         int var5 = var4 + 1;
         if(var4 >= this.size) {
            break;
         }

         Node var6 = this.heap[var4];
         float var7 = var6.f;
         Node var8;
         float var9;
         if(var5 >= this.size) {
            var8 = null;
            var9 = Float.POSITIVE_INFINITY;
         } else {
            var8 = this.heap[var5];
            var9 = var8.f;
         }

         if(var7 < var9) {
            if(var7 >= var3) {
               break;
            }

            this.heap[i] = var6;
            var6.heapIdx = i;
            i = var4;
         } else {
            if(var9 >= var3) {
               break;
            }

            this.heap[i] = var8;
            var8.heapIdx = i;
            i = var5;
         }
      }

      this.heap[i] = var2;
      var2.heapIdx = i;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }
}
