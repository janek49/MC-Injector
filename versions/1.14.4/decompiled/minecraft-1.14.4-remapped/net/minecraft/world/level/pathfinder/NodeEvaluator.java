package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.function.IntFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Target;

public abstract class NodeEvaluator {
   protected LevelReader level;
   protected Mob mob;
   protected final Int2ObjectMap nodes = new Int2ObjectOpenHashMap();
   protected int entityWidth;
   protected int entityHeight;
   protected int entityDepth;
   protected boolean canPassDoors;
   protected boolean canOpenDoors;
   protected boolean canFloat;

   public void prepare(LevelReader level, Mob mob) {
      this.level = level;
      this.mob = mob;
      this.nodes.clear();
      this.entityWidth = Mth.floor(mob.getBbWidth() + 1.0F);
      this.entityHeight = Mth.floor(mob.getBbHeight() + 1.0F);
      this.entityDepth = Mth.floor(mob.getBbWidth() + 1.0F);
   }

   public void done() {
      this.level = null;
      this.mob = null;
   }

   protected Node getNode(int var1, int var2, int var3) {
      return (Node)this.nodes.computeIfAbsent(Node.createHash(var1, var2, var3), (var3x) -> {
         return new Node(var1, var2, var3);
      });
   }

   public abstract Node getStart();

   public abstract Target getGoal(double var1, double var3, double var5);

   public abstract int getNeighbors(Node[] var1, Node var2);

   public abstract BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4, Mob var5, int var6, int var7, int var8, boolean var9, boolean var10);

   public abstract BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4);

   public void setCanPassDoors(boolean canPassDoors) {
      this.canPassDoors = canPassDoors;
   }

   public void setCanOpenDoors(boolean canOpenDoors) {
      this.canOpenDoors = canOpenDoors;
   }

   public void setCanFloat(boolean canFloat) {
      this.canFloat = canFloat;
   }

   public boolean canPassDoors() {
      return this.canPassDoors;
   }

   public boolean canOpenDoors() {
      return this.canOpenDoors;
   }

   public boolean canFloat() {
      return this.canFloat;
   }
}
