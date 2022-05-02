package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public class RailState {
   private final Level level;
   private final BlockPos pos;
   private final BaseRailBlock block;
   private BlockState state;
   private final boolean isStraight;
   private final List connections = Lists.newArrayList();

   public RailState(Level level, BlockPos pos, BlockState state) {
      this.level = level;
      this.pos = pos;
      this.state = state;
      this.block = (BaseRailBlock)state.getBlock();
      RailShape var4 = (RailShape)state.getValue(this.block.getShapeProperty());
      this.isStraight = this.block.isStraight();
      this.updateConnections(var4);
   }

   public List getConnections() {
      return this.connections;
   }

   private void updateConnections(RailShape railShape) {
      this.connections.clear();
      switch(railShape) {
      case NORTH_SOUTH:
         this.connections.add(this.pos.north());
         this.connections.add(this.pos.south());
         break;
      case EAST_WEST:
         this.connections.add(this.pos.west());
         this.connections.add(this.pos.east());
         break;
      case ASCENDING_EAST:
         this.connections.add(this.pos.west());
         this.connections.add(this.pos.east().above());
         break;
      case ASCENDING_WEST:
         this.connections.add(this.pos.west().above());
         this.connections.add(this.pos.east());
         break;
      case ASCENDING_NORTH:
         this.connections.add(this.pos.north().above());
         this.connections.add(this.pos.south());
         break;
      case ASCENDING_SOUTH:
         this.connections.add(this.pos.north());
         this.connections.add(this.pos.south().above());
         break;
      case SOUTH_EAST:
         this.connections.add(this.pos.east());
         this.connections.add(this.pos.south());
         break;
      case SOUTH_WEST:
         this.connections.add(this.pos.west());
         this.connections.add(this.pos.south());
         break;
      case NORTH_WEST:
         this.connections.add(this.pos.west());
         this.connections.add(this.pos.north());
         break;
      case NORTH_EAST:
         this.connections.add(this.pos.east());
         this.connections.add(this.pos.north());
      }

   }

   private void removeSoftConnections() {
      for(int var1 = 0; var1 < this.connections.size(); ++var1) {
         RailState var2 = this.getRail((BlockPos)this.connections.get(var1));
         if(var2 != null && var2.connectsTo(this)) {
            this.connections.set(var1, var2.pos);
         } else {
            this.connections.remove(var1--);
         }
      }

   }

   private boolean hasRail(BlockPos blockPos) {
      return BaseRailBlock.isRail(this.level, blockPos) || BaseRailBlock.isRail(this.level, blockPos.above()) || BaseRailBlock.isRail(this.level, blockPos.below());
   }

   @Nullable
   private RailState getRail(BlockPos blockPos) {
      BlockState var3 = this.level.getBlockState(blockPos);
      if(BaseRailBlock.isRail(var3)) {
         return new RailState(this.level, blockPos, var3);
      } else {
         BlockPos blockPos = blockPos.above();
         var3 = this.level.getBlockState(blockPos);
         if(BaseRailBlock.isRail(var3)) {
            return new RailState(this.level, blockPos, var3);
         } else {
            blockPos = blockPos.below();
            var3 = this.level.getBlockState(blockPos);
            return BaseRailBlock.isRail(var3)?new RailState(this.level, blockPos, var3):null;
         }
      }
   }

   private boolean connectsTo(RailState railState) {
      return this.hasConnection(railState.pos);
   }

   private boolean hasConnection(BlockPos blockPos) {
      for(int var2 = 0; var2 < this.connections.size(); ++var2) {
         BlockPos var3 = (BlockPos)this.connections.get(var2);
         if(var3.getX() == blockPos.getX() && var3.getZ() == blockPos.getZ()) {
            return true;
         }
      }

      return false;
   }

   protected int countPotentialConnections() {
      int var1 = 0;

      for(Direction var3 : Direction.Plane.HORIZONTAL) {
         if(this.hasRail(this.pos.relative(var3))) {
            ++var1;
         }
      }

      return var1;
   }

   private boolean canConnectTo(RailState railState) {
      return this.connectsTo(railState) || this.connections.size() != 2;
   }

   private void connectTo(RailState railState) {
      this.connections.add(railState.pos);
      BlockPos var2 = this.pos.north();
      BlockPos var3 = this.pos.south();
      BlockPos var4 = this.pos.west();
      BlockPos var5 = this.pos.east();
      boolean var6 = this.hasConnection(var2);
      boolean var7 = this.hasConnection(var3);
      boolean var8 = this.hasConnection(var4);
      boolean var9 = this.hasConnection(var5);
      RailShape var10 = null;
      if(var6 || var7) {
         var10 = RailShape.NORTH_SOUTH;
      }

      if(var8 || var9) {
         var10 = RailShape.EAST_WEST;
      }

      if(!this.isStraight) {
         if(var7 && var9 && !var6 && !var8) {
            var10 = RailShape.SOUTH_EAST;
         }

         if(var7 && var8 && !var6 && !var9) {
            var10 = RailShape.SOUTH_WEST;
         }

         if(var6 && var8 && !var7 && !var9) {
            var10 = RailShape.NORTH_WEST;
         }

         if(var6 && var9 && !var7 && !var8) {
            var10 = RailShape.NORTH_EAST;
         }
      }

      if(var10 == RailShape.NORTH_SOUTH) {
         if(BaseRailBlock.isRail(this.level, var2.above())) {
            var10 = RailShape.ASCENDING_NORTH;
         }

         if(BaseRailBlock.isRail(this.level, var3.above())) {
            var10 = RailShape.ASCENDING_SOUTH;
         }
      }

      if(var10 == RailShape.EAST_WEST) {
         if(BaseRailBlock.isRail(this.level, var5.above())) {
            var10 = RailShape.ASCENDING_EAST;
         }

         if(BaseRailBlock.isRail(this.level, var4.above())) {
            var10 = RailShape.ASCENDING_WEST;
         }
      }

      if(var10 == null) {
         var10 = RailShape.NORTH_SOUTH;
      }

      this.state = (BlockState)this.state.setValue(this.block.getShapeProperty(), var10);
      this.level.setBlock(this.pos, this.state, 3);
   }

   private boolean hasNeighborRail(BlockPos blockPos) {
      RailState var2 = this.getRail(blockPos);
      if(var2 == null) {
         return false;
      } else {
         var2.removeSoftConnections();
         return var2.canConnectTo(this);
      }
   }

   public RailState place(boolean var1, boolean var2) {
      BlockPos var3 = this.pos.north();
      BlockPos var4 = this.pos.south();
      BlockPos var5 = this.pos.west();
      BlockPos var6 = this.pos.east();
      boolean var7 = this.hasNeighborRail(var3);
      boolean var8 = this.hasNeighborRail(var4);
      boolean var9 = this.hasNeighborRail(var5);
      boolean var10 = this.hasNeighborRail(var6);
      RailShape var11 = null;
      if((var7 || var8) && !var9 && !var10) {
         var11 = RailShape.NORTH_SOUTH;
      }

      if((var9 || var10) && !var7 && !var8) {
         var11 = RailShape.EAST_WEST;
      }

      if(!this.isStraight) {
         if(var8 && var10 && !var7 && !var9) {
            var11 = RailShape.SOUTH_EAST;
         }

         if(var8 && var9 && !var7 && !var10) {
            var11 = RailShape.SOUTH_WEST;
         }

         if(var7 && var9 && !var8 && !var10) {
            var11 = RailShape.NORTH_WEST;
         }

         if(var7 && var10 && !var8 && !var9) {
            var11 = RailShape.NORTH_EAST;
         }
      }

      if(var11 == null) {
         if(var7 || var8) {
            var11 = RailShape.NORTH_SOUTH;
         }

         if(var9 || var10) {
            var11 = RailShape.EAST_WEST;
         }

         if(!this.isStraight) {
            if(var1) {
               if(var8 && var10) {
                  var11 = RailShape.SOUTH_EAST;
               }

               if(var9 && var8) {
                  var11 = RailShape.SOUTH_WEST;
               }

               if(var10 && var7) {
                  var11 = RailShape.NORTH_EAST;
               }

               if(var7 && var9) {
                  var11 = RailShape.NORTH_WEST;
               }
            } else {
               if(var7 && var9) {
                  var11 = RailShape.NORTH_WEST;
               }

               if(var10 && var7) {
                  var11 = RailShape.NORTH_EAST;
               }

               if(var9 && var8) {
                  var11 = RailShape.SOUTH_WEST;
               }

               if(var8 && var10) {
                  var11 = RailShape.SOUTH_EAST;
               }
            }
         }
      }

      if(var11 == RailShape.NORTH_SOUTH) {
         if(BaseRailBlock.isRail(this.level, var3.above())) {
            var11 = RailShape.ASCENDING_NORTH;
         }

         if(BaseRailBlock.isRail(this.level, var4.above())) {
            var11 = RailShape.ASCENDING_SOUTH;
         }
      }

      if(var11 == RailShape.EAST_WEST) {
         if(BaseRailBlock.isRail(this.level, var6.above())) {
            var11 = RailShape.ASCENDING_EAST;
         }

         if(BaseRailBlock.isRail(this.level, var5.above())) {
            var11 = RailShape.ASCENDING_WEST;
         }
      }

      if(var11 == null) {
         var11 = RailShape.NORTH_SOUTH;
      }

      this.updateConnections(var11);
      this.state = (BlockState)this.state.setValue(this.block.getShapeProperty(), var11);
      if(var2 || this.level.getBlockState(this.pos) != this.state) {
         this.level.setBlock(this.pos, this.state, 3);

         for(int var12 = 0; var12 < this.connections.size(); ++var12) {
            RailState var13 = this.getRail((BlockPos)this.connections.get(var12));
            if(var13 != null) {
               var13.removeSoftConnections();
               if(var13.canConnectTo(this)) {
                  var13.connectTo(this);
               }
            }
         }
      }

      return this;
   }

   public BlockState getState() {
      return this.state;
   }
}
