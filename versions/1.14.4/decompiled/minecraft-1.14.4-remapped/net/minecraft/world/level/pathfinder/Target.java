package net.minecraft.world.level.pathfinder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;

public class Target extends Node {
   private float bestHeuristic = Float.MAX_VALUE;
   private Node bestNode;
   private boolean reached;

   public Target(Node node) {
      super(node.x, node.y, node.z);
   }

   public Target(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public void updateBest(float bestHeuristic, Node bestNode) {
      if(bestHeuristic < this.bestHeuristic) {
         this.bestHeuristic = bestHeuristic;
         this.bestNode = bestNode;
      }

   }

   public Node getBestNode() {
      return this.bestNode;
   }

   public void setReached() {
      this.reached = true;
   }

   public boolean isReached() {
      return this.reached;
   }

   public static Target createFromStream(FriendlyByteBuf friendlyByteBuf) {
      Target target = new Target(friendlyByteBuf.readInt(), friendlyByteBuf.readInt(), friendlyByteBuf.readInt());
      target.walkedDistance = friendlyByteBuf.readFloat();
      target.costMalus = friendlyByteBuf.readFloat();
      target.closed = friendlyByteBuf.readBoolean();
      target.type = BlockPathTypes.values()[friendlyByteBuf.readInt()];
      target.f = friendlyByteBuf.readFloat();
      return target;
   }
}
