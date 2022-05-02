package net.minecraft.world.level.pathfinder;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class Node {
   public final int x;
   public final int y;
   public final int z;
   private final int hash;
   public int heapIdx = -1;
   public float g;
   public float h;
   public float f;
   public Node cameFrom;
   public boolean closed;
   public float walkedDistance;
   public float costMalus;
   public BlockPathTypes type = BlockPathTypes.BLOCKED;

   public Node(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.hash = createHash(x, y, z);
   }

   public Node cloneMove(int var1, int var2, int var3) {
      Node node = new Node(var1, var2, var3);
      node.heapIdx = this.heapIdx;
      node.g = this.g;
      node.h = this.h;
      node.f = this.f;
      node.cameFrom = this.cameFrom;
      node.closed = this.closed;
      node.walkedDistance = this.walkedDistance;
      node.costMalus = this.costMalus;
      node.type = this.type;
      return node;
   }

   public static int createHash(int var0, int var1, int var2) {
      return var1 & 255 | (var0 & 32767) << 8 | (var2 & 32767) << 24 | (var0 < 0?Integer.MIN_VALUE:0) | (var2 < 0?'耀':0);
   }

   public float distanceTo(Node node) {
      float var2 = (float)(node.x - this.x);
      float var3 = (float)(node.y - this.y);
      float var4 = (float)(node.z - this.z);
      return Mth.sqrt(var2 * var2 + var3 * var3 + var4 * var4);
   }

   public float distanceToSqr(Node node) {
      float var2 = (float)(node.x - this.x);
      float var3 = (float)(node.y - this.y);
      float var4 = (float)(node.z - this.z);
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   public float distanceManhattan(Node node) {
      float var2 = (float)Math.abs(node.x - this.x);
      float var3 = (float)Math.abs(node.y - this.y);
      float var4 = (float)Math.abs(node.z - this.z);
      return var2 + var3 + var4;
   }

   public float distanceManhattan(BlockPos blockPos) {
      float var2 = (float)Math.abs(blockPos.getX() - this.x);
      float var3 = (float)Math.abs(blockPos.getY() - this.y);
      float var4 = (float)Math.abs(blockPos.getZ() - this.z);
      return var2 + var3 + var4;
   }

   public BlockPos asBlockPos() {
      return new BlockPos(this.x, this.y, this.z);
   }

   public boolean equals(Object object) {
      if(!(object instanceof Node)) {
         return false;
      } else {
         Node var2 = (Node)object;
         return this.hash == var2.hash && this.x == var2.x && this.y == var2.y && this.z == var2.z;
      }
   }

   public int hashCode() {
      return this.hash;
   }

   public boolean inOpenSet() {
      return this.heapIdx >= 0;
   }

   public String toString() {
      return "Node{x=" + this.x + ", y=" + this.y + ", z=" + this.z + '}';
   }

   public static Node createFromStream(FriendlyByteBuf friendlyByteBuf) {
      Node node = new Node(friendlyByteBuf.readInt(), friendlyByteBuf.readInt(), friendlyByteBuf.readInt());
      node.walkedDistance = friendlyByteBuf.readFloat();
      node.costMalus = friendlyByteBuf.readFloat();
      node.closed = friendlyByteBuf.readBoolean();
      node.type = BlockPathTypes.values()[friendlyByteBuf.readInt()];
      node.f = friendlyByteBuf.readFloat();
      return node;
   }
}
