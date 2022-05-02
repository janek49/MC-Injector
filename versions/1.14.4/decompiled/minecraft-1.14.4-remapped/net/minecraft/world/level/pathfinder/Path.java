package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;
import net.minecraft.world.phys.Vec3;

public class Path {
   private final List nodes;
   private Node[] openSet = new Node[0];
   private Node[] closedSet = new Node[0];
   private Set targetNodes;
   private int index;
   private final BlockPos target;
   private final float distToTarget;
   private final boolean reached;

   public Path(List nodes, BlockPos target, boolean reached) {
      this.nodes = nodes;
      this.target = target;
      this.distToTarget = nodes.isEmpty()?Float.MAX_VALUE:((Node)this.nodes.get(this.nodes.size() - 1)).distanceManhattan(this.target);
      this.reached = reached;
   }

   public void next() {
      ++this.index;
   }

   public boolean isDone() {
      return this.index >= this.nodes.size();
   }

   @Nullable
   public Node last() {
      return !this.nodes.isEmpty()?(Node)this.nodes.get(this.nodes.size() - 1):null;
   }

   public Node get(int i) {
      return (Node)this.nodes.get(i);
   }

   public List getNodes() {
      return this.nodes;
   }

   public void truncate(int i) {
      if(this.nodes.size() > i) {
         this.nodes.subList(i, this.nodes.size()).clear();
      }

   }

   public void set(int var1, Node node) {
      this.nodes.set(var1, node);
   }

   public int getSize() {
      return this.nodes.size();
   }

   public int getIndex() {
      return this.index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

   public Vec3 getPos(Entity entity, int var2) {
      Node var3 = (Node)this.nodes.get(var2);
      double var4 = (double)var3.x + (double)((int)(entity.getBbWidth() + 1.0F)) * 0.5D;
      double var6 = (double)var3.y;
      double var8 = (double)var3.z + (double)((int)(entity.getBbWidth() + 1.0F)) * 0.5D;
      return new Vec3(var4, var6, var8);
   }

   public Vec3 currentPos(Entity entity) {
      return this.getPos(entity, this.index);
   }

   public Vec3 currentPos() {
      Node var1 = (Node)this.nodes.get(this.index);
      return new Vec3((double)var1.x, (double)var1.y, (double)var1.z);
   }

   public boolean sameAs(@Nullable Path path) {
      if(path == null) {
         return false;
      } else if(path.nodes.size() != this.nodes.size()) {
         return false;
      } else {
         for(int var2 = 0; var2 < this.nodes.size(); ++var2) {
            Node var3 = (Node)this.nodes.get(var2);
            Node var4 = (Node)path.nodes.get(var2);
            if(var3.x != var4.x || var3.y != var4.y || var3.z != var4.z) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean canReach() {
      return this.reached;
   }

   public Node[] getOpenSet() {
      return this.openSet;
   }

   public Node[] getClosedSet() {
      return this.closedSet;
   }

   public static Path createFromStream(FriendlyByteBuf friendlyByteBuf) {
      boolean var1 = friendlyByteBuf.readBoolean();
      int var2 = friendlyByteBuf.readInt();
      int var3 = friendlyByteBuf.readInt();
      Set<Target> var4 = Sets.newHashSet();

      for(int var5 = 0; var5 < var3; ++var5) {
         var4.add(Target.createFromStream(friendlyByteBuf));
      }

      BlockPos var5 = new BlockPos(friendlyByteBuf.readInt(), friendlyByteBuf.readInt(), friendlyByteBuf.readInt());
      List<Node> var6 = Lists.newArrayList();
      int var7 = friendlyByteBuf.readInt();

      for(int var8 = 0; var8 < var7; ++var8) {
         var6.add(Node.createFromStream(friendlyByteBuf));
      }

      Node[] vars8 = new Node[friendlyByteBuf.readInt()];

      for(int var9 = 0; var9 < vars8.length; ++var9) {
         vars8[var9] = Node.createFromStream(friendlyByteBuf);
      }

      Node[] vars9 = new Node[friendlyByteBuf.readInt()];

      for(int var10 = 0; var10 < vars9.length; ++var10) {
         vars9[var10] = Node.createFromStream(friendlyByteBuf);
      }

      Path var10 = new Path(var6, var5, var1);
      var10.openSet = vars8;
      var10.closedSet = vars9;
      var10.targetNodes = var4;
      var10.index = var2;
      return var10;
   }

   public String toString() {
      return "Path(length=" + this.nodes.size() + ")";
   }

   public BlockPos getTarget() {
      return this.target;
   }

   public float getDistToTarget() {
      return this.distToTarget;
   }
}
