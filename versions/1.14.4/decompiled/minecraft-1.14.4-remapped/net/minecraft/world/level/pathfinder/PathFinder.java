package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BinaryHeap;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.NodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.Target;

public class PathFinder {
   private final BinaryHeap openSet = new BinaryHeap();
   private final Set closedSet = Sets.newHashSet();
   private final Node[] neighbors = new Node[32];
   private final int maxVisitedNodes;
   private NodeEvaluator nodeEvaluator;

   public PathFinder(NodeEvaluator nodeEvaluator, int maxVisitedNodes) {
      this.nodeEvaluator = nodeEvaluator;
      this.maxVisitedNodes = maxVisitedNodes;
   }

   @Nullable
   public Path findPath(LevelReader levelReader, Mob mob, Set set, float var4, int var5) {
      this.openSet.clear();
      this.nodeEvaluator.prepare(levelReader, mob);
      Node var6 = this.nodeEvaluator.getStart();
      Map<Target, BlockPos> var7 = (Map)set.stream().collect(Collectors.toMap((blockPos) -> {
         return this.nodeEvaluator.getGoal((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
      }, Function.identity()));
      Path var8 = this.findPath(var6, var7, var4, var5);
      this.nodeEvaluator.done();
      return var8;
   }

   @Nullable
   private Path findPath(Node node, Map map, float var3, int var4) {
      Set<Target> var5 = map.keySet();
      node.g = 0.0F;
      node.h = this.getBestH(node, var5);
      node.f = node.h;
      this.openSet.clear();
      this.closedSet.clear();
      this.openSet.insert(node);
      int var6 = 0;

      while(!this.openSet.isEmpty()) {
         ++var6;
         if(var6 >= this.maxVisitedNodes) {
            break;
         }

         Node var7 = this.openSet.pop();
         var7.closed = true;
         var5.stream().filter((target) -> {
            return var7.distanceManhattan((Node)target) <= (float)var4;
         }).forEach(Target::setReached);
         if(var5.stream().anyMatch(Target::isReached)) {
            break;
         }

         if(var7.distanceTo(node) < var3) {
            int var8 = this.nodeEvaluator.getNeighbors(this.neighbors, var7);

            for(int var9 = 0; var9 < var8; ++var9) {
               Node var10 = this.neighbors[var9];
               float var11 = var7.distanceTo(var10);
               var10.walkedDistance = var7.walkedDistance + var11;
               float var12 = var7.g + var11 + var10.costMalus;
               if(var10.walkedDistance < var3 && (!var10.inOpenSet() || var12 < var10.g)) {
                  var10.cameFrom = var7;
                  var10.g = var12;
                  var10.h = this.getBestH(var10, var5) * 1.5F;
                  if(var10.inOpenSet()) {
                     this.openSet.changeCost(var10, var10.g + var10.h);
                  } else {
                     var10.f = var10.g + var10.h;
                     this.openSet.insert(var10);
                  }
               }
            }
         }
      }

      Stream<Path> var7;
      if(var5.stream().anyMatch(Target::isReached)) {
         var7 = var5.stream().filter(Target::isReached).map((target) -> {
            return this.reconstructPath(target.getBestNode(), (BlockPos)map.get(target), true);
         }).sorted(Comparator.comparingInt(Path::getSize));
      } else {
         var7 = var5.stream().map((target) -> {
            return this.reconstructPath(target.getBestNode(), (BlockPos)map.get(target), false);
         }).sorted(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getSize));
      }

      Optional<Path> var8 = var7.findFirst();
      if(!var8.isPresent()) {
         return null;
      } else {
         Path var9 = (Path)var8.get();
         return var9;
      }
   }

   private float getBestH(Node node, Set set) {
      float var3 = Float.MAX_VALUE;

      for(Target var5 : set) {
         float var6 = node.distanceTo(var5);
         var5.updateBest(var6, node);
         var3 = Math.min(var6, var3);
      }

      return var3;
   }

   private Path reconstructPath(Node node, BlockPos blockPos, boolean var3) {
      List<Node> var4 = Lists.newArrayList();
      Node var5 = node;
      var4.add(0, node);

      while(var5.cameFrom != null) {
         var5 = var5.cameFrom;
         var4.add(0, var5);
      }

      return new Path(var4, blockPos, var3);
   }
}
