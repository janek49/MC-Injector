package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class PistonStructureResolver {
   private final Level level;
   private final BlockPos pistonPos;
   private final boolean extending;
   private final BlockPos startPos;
   private final Direction pushDirection;
   private final List toPush = Lists.newArrayList();
   private final List toDestroy = Lists.newArrayList();
   private final Direction pistonDirection;

   public PistonStructureResolver(Level level, BlockPos pistonPos, Direction pistonDirection, boolean extending) {
      this.level = level;
      this.pistonPos = pistonPos;
      this.pistonDirection = pistonDirection;
      this.extending = extending;
      if(extending) {
         this.pushDirection = pistonDirection;
         this.startPos = pistonPos.relative(pistonDirection);
      } else {
         this.pushDirection = pistonDirection.getOpposite();
         this.startPos = pistonPos.relative(pistonDirection, 2);
      }

   }

   public boolean resolve() {
      this.toPush.clear();
      this.toDestroy.clear();
      BlockState var1 = this.level.getBlockState(this.startPos);
      if(!PistonBaseBlock.isPushable(var1, this.level, this.startPos, this.pushDirection, false, this.pistonDirection)) {
         if(this.extending && var1.getPistonPushReaction() == PushReaction.DESTROY) {
            this.toDestroy.add(this.startPos);
            return true;
         } else {
            return false;
         }
      } else if(!this.addBlockLine(this.startPos, this.pushDirection)) {
         return false;
      } else {
         for(int var2 = 0; var2 < this.toPush.size(); ++var2) {
            BlockPos var3 = (BlockPos)this.toPush.get(var2);
            if(this.level.getBlockState(var3).getBlock() == Blocks.SLIME_BLOCK && !this.addBranchingBlocks(var3)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean addBlockLine(BlockPos blockPos, Direction direction) {
      BlockState var3 = this.level.getBlockState(blockPos);
      Block var4 = var3.getBlock();
      if(var3.isAir()) {
         return true;
      } else if(!PistonBaseBlock.isPushable(var3, this.level, blockPos, this.pushDirection, false, direction)) {
         return true;
      } else if(blockPos.equals(this.pistonPos)) {
         return true;
      } else if(this.toPush.contains(blockPos)) {
         return true;
      } else {
         int var5 = 1;
         if(var5 + this.toPush.size() > 12) {
            return false;
         } else {
            while(var4 == Blocks.SLIME_BLOCK) {
               BlockPos var6 = blockPos.relative(this.pushDirection.getOpposite(), var5);
               var3 = this.level.getBlockState(var6);
               var4 = var3.getBlock();
               if(var3.isAir() || !PistonBaseBlock.isPushable(var3, this.level, var6, this.pushDirection, false, this.pushDirection.getOpposite()) || var6.equals(this.pistonPos)) {
                  break;
               }

               ++var5;
               if(var5 + this.toPush.size() > 12) {
                  return false;
               }
            }

            int var6 = 0;

            for(int var7 = var5 - 1; var7 >= 0; --var7) {
               this.toPush.add(blockPos.relative(this.pushDirection.getOpposite(), var7));
               ++var6;
            }

            int var7 = 1;

            while(true) {
               BlockPos var8 = blockPos.relative(this.pushDirection, var7);
               int var9 = this.toPush.indexOf(var8);
               if(var9 > -1) {
                  this.reorderListAtCollision(var6, var9);

                  for(int var10 = 0; var10 <= var9 + var6; ++var10) {
                     BlockPos var11 = (BlockPos)this.toPush.get(var10);
                     if(this.level.getBlockState(var11).getBlock() == Blocks.SLIME_BLOCK && !this.addBranchingBlocks(var11)) {
                        return false;
                     }
                  }

                  return true;
               }

               var3 = this.level.getBlockState(var8);
               if(var3.isAir()) {
                  return true;
               }

               if(!PistonBaseBlock.isPushable(var3, this.level, var8, this.pushDirection, true, this.pushDirection) || var8.equals(this.pistonPos)) {
                  return false;
               }

               if(var3.getPistonPushReaction() == PushReaction.DESTROY) {
                  this.toDestroy.add(var8);
                  return true;
               }

               if(this.toPush.size() >= 12) {
                  return false;
               }

               this.toPush.add(var8);
               ++var6;
               ++var7;
            }
         }
      }
   }

   private void reorderListAtCollision(int var1, int var2) {
      List<BlockPos> var3 = Lists.newArrayList();
      List<BlockPos> var4 = Lists.newArrayList();
      List<BlockPos> var5 = Lists.newArrayList();
      var3.addAll(this.toPush.subList(0, var2));
      var4.addAll(this.toPush.subList(this.toPush.size() - var1, this.toPush.size()));
      var5.addAll(this.toPush.subList(var2, this.toPush.size() - var1));
      this.toPush.clear();
      this.toPush.addAll(var3);
      this.toPush.addAll(var4);
      this.toPush.addAll(var5);
   }

   private boolean addBranchingBlocks(BlockPos blockPos) {
      for(Direction var5 : Direction.values()) {
         if(var5.getAxis() != this.pushDirection.getAxis() && !this.addBlockLine(blockPos.relative(var5), var5)) {
            return false;
         }
      }

      return true;
   }

   public List getToPush() {
      return this.toPush;
   }

   public List getToDestroy() {
      return this.toDestroy;
   }
}
