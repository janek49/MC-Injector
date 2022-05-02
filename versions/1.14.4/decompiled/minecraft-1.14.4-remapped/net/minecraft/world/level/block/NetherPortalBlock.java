package net.minecraft.world.level.block;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherPortalBlock extends Block {
   public static final EnumProperty AXIS = BlockStateProperties.HORIZONTAL_AXIS;
   protected static final VoxelShape X_AXIS_AABB = Block.box(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
   protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

   public NetherPortalBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Direction.Axis.X));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      switch((Direction.Axis)blockState.getValue(AXIS)) {
      case Z:
         return Z_AXIS_AABB;
      case X:
      default:
         return X_AXIS_AABB;
      }
   }

   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(level.dimension.isNaturalDimension() && level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && random.nextInt(2000) < level.getDifficulty().getId()) {
         while(level.getBlockState(blockPos).getBlock() == this) {
            blockPos = blockPos.below();
         }

         if(level.getBlockState(blockPos).isValidSpawn(level, blockPos, EntityType.ZOMBIE_PIGMAN)) {
            Entity var5 = EntityType.ZOMBIE_PIGMAN.spawn(level, (CompoundTag)null, (Component)null, (Player)null, blockPos.above(), MobSpawnType.STRUCTURE, false, false);
            if(var5 != null) {
               var5.changingDimensionDelay = var5.getDimensionChangingDelay();
            }
         }
      }

   }

   public boolean trySpawnPortal(LevelAccessor levelAccessor, BlockPos blockPos) {
      NetherPortalBlock.PortalShape var3 = this.isPortal(levelAccessor, blockPos);
      if(var3 != null) {
         var3.createPortalBlocks();
         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public NetherPortalBlock.PortalShape isPortal(LevelAccessor levelAccessor, BlockPos blockPos) {
      NetherPortalBlock.PortalShape netherPortalBlock$PortalShape = new NetherPortalBlock.PortalShape(levelAccessor, blockPos, Direction.Axis.X);
      if(netherPortalBlock$PortalShape.isValid() && netherPortalBlock$PortalShape.numPortalBlocks == 0) {
         return netherPortalBlock$PortalShape;
      } else {
         NetherPortalBlock.PortalShape var4 = new NetherPortalBlock.PortalShape(levelAccessor, blockPos, Direction.Axis.Z);
         return var4.isValid() && var4.numPortalBlocks == 0?var4:null;
      }
   }

   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      Direction.Axis var7 = direction.getAxis();
      Direction.Axis var8 = (Direction.Axis)var1.getValue(AXIS);
      boolean var9 = var8 != var7 && var7.isHorizontal();
      return !var9 && var3.getBlock() != this && !(new NetherPortalBlock.PortalShape(levelAccessor, var5, var8)).isComplete()?Blocks.AIR.defaultBlockState():super.updateShape(var1, direction, var3, levelAccessor, var5, var6);
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.TRANSLUCENT;
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      if(!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()) {
         entity.handleInsidePortal(blockPos);
      }

   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      if(random.nextInt(100) == 0) {
         level.playLocalSound((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
      }

      for(int var5 = 0; var5 < 4; ++var5) {
         double var6 = (double)((float)blockPos.getX() + random.nextFloat());
         double var8 = (double)((float)blockPos.getY() + random.nextFloat());
         double var10 = (double)((float)blockPos.getZ() + random.nextFloat());
         double var12 = ((double)random.nextFloat() - 0.5D) * 0.5D;
         double var14 = ((double)random.nextFloat() - 0.5D) * 0.5D;
         double var16 = ((double)random.nextFloat() - 0.5D) * 0.5D;
         int var18 = random.nextInt(2) * 2 - 1;
         if(level.getBlockState(blockPos.west()).getBlock() != this && level.getBlockState(blockPos.east()).getBlock() != this) {
            var6 = (double)blockPos.getX() + 0.5D + 0.25D * (double)var18;
            var12 = (double)(random.nextFloat() * 2.0F * (float)var18);
         } else {
            var10 = (double)blockPos.getZ() + 0.5D + 0.25D * (double)var18;
            var16 = (double)(random.nextFloat() * 2.0F * (float)var18);
         }

         level.addParticle(ParticleTypes.PORTAL, var6, var8, var10, var12, var14, var16);
      }

   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return ItemStack.EMPTY;
   }

   public BlockState rotate(BlockState var1, Rotation rotation) {
      switch(rotation) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         switch((Direction.Axis)var1.getValue(AXIS)) {
         case Z:
            return (BlockState)var1.setValue(AXIS, Direction.Axis.X);
         case X:
            return (BlockState)var1.setValue(AXIS, Direction.Axis.Z);
         default:
            return var1;
         }
      default:
         return var1;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{AXIS});
   }

   public BlockPattern.BlockPatternMatch getPortalShape(LevelAccessor levelAccessor, BlockPos blockPos) {
      Direction.Axis var3 = Direction.Axis.Z;
      NetherPortalBlock.PortalShape var4 = new NetherPortalBlock.PortalShape(levelAccessor, blockPos, Direction.Axis.X);
      LoadingCache<BlockPos, BlockInWorld> var5 = BlockPattern.createLevelCache(levelAccessor, true);
      if(!var4.isValid()) {
         var3 = Direction.Axis.X;
         var4 = new NetherPortalBlock.PortalShape(levelAccessor, blockPos, Direction.Axis.Z);
      }

      if(!var4.isValid()) {
         return new BlockPattern.BlockPatternMatch(blockPos, Direction.NORTH, Direction.UP, var5, 1, 1, 1);
      } else {
         int[] vars6 = new int[Direction.AxisDirection.values().length];
         Direction var7 = var4.rightDir.getCounterClockWise();
         BlockPos var8 = var4.bottomLeft.above(var4.getHeight() - 1);

         for(Direction.AxisDirection var12 : Direction.AxisDirection.values()) {
            BlockPattern.BlockPatternMatch var13 = new BlockPattern.BlockPatternMatch(var7.getAxisDirection() == var12?var8:var8.relative(var4.rightDir, var4.getWidth() - 1), Direction.get(var12, var3), Direction.UP, var5, var4.getWidth(), var4.getHeight(), 1);

            for(int var14 = 0; var14 < var4.getWidth(); ++var14) {
               for(int var15 = 0; var15 < var4.getHeight(); ++var15) {
                  BlockInWorld var16 = var13.getBlock(var14, var15, 1);
                  if(!var16.getState().isAir()) {
                     ++vars6[var12.ordinal()];
                  }
               }
            }
         }

         Direction.AxisDirection var9 = Direction.AxisDirection.POSITIVE;

         for(Direction.AxisDirection var13 : Direction.AxisDirection.values()) {
            if(vars6[var13.ordinal()] < vars6[var9.ordinal()]) {
               var9 = var13;
            }
         }

         return new BlockPattern.BlockPatternMatch(var7.getAxisDirection() == var9?var8:var8.relative(var4.rightDir, var4.getWidth() - 1), Direction.get(var9, var3), Direction.UP, var5, var4.getWidth(), var4.getHeight(), 1);
      }
   }

   public static class PortalShape {
      private final LevelAccessor level;
      private final Direction.Axis axis;
      private final Direction rightDir;
      private final Direction leftDir;
      private int numPortalBlocks;
      @Nullable
      private BlockPos bottomLeft;
      private int height;
      private int width;

      public PortalShape(LevelAccessor level, BlockPos blockPos, Direction.Axis axis) {
         this.level = level;
         this.axis = axis;
         if(axis == Direction.Axis.X) {
            this.leftDir = Direction.EAST;
            this.rightDir = Direction.WEST;
         } else {
            this.leftDir = Direction.NORTH;
            this.rightDir = Direction.SOUTH;
         }

         for(BlockPos blockPos = blockPos; blockPos.getY() > blockPos.getY() - 21 && blockPos.getY() > 0 && this.isEmpty(level.getBlockState(blockPos.below())); blockPos = blockPos.below()) {
            ;
         }

         int var5 = this.getDistanceUntilEdge(blockPos, this.leftDir) - 1;
         if(var5 >= 0) {
            this.bottomLeft = blockPos.relative(this.leftDir, var5);
            this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir);
            if(this.width < 2 || this.width > 21) {
               this.bottomLeft = null;
               this.width = 0;
            }
         }

         if(this.bottomLeft != null) {
            this.height = this.calculatePortalHeight();
         }

      }

      protected int getDistanceUntilEdge(BlockPos blockPos, Direction direction) {
         int var3;
         for(var3 = 0; var3 < 22; ++var3) {
            BlockPos var4 = blockPos.relative(direction, var3);
            if(!this.isEmpty(this.level.getBlockState(var4)) || this.level.getBlockState(var4.below()).getBlock() != Blocks.OBSIDIAN) {
               break;
            }
         }

         Block var4 = this.level.getBlockState(blockPos.relative(direction, var3)).getBlock();
         return var4 == Blocks.OBSIDIAN?var3:0;
      }

      public int getHeight() {
         return this.height;
      }

      public int getWidth() {
         return this.width;
      }

      protected int calculatePortalHeight() {
         label24:
         for(this.height = 0; this.height < 21; ++this.height) {
            for(int var1 = 0; var1 < this.width; ++var1) {
               BlockPos var2 = this.bottomLeft.relative(this.rightDir, var1).above(this.height);
               BlockState var3 = this.level.getBlockState(var2);
               if(!this.isEmpty(var3)) {
                  break label24;
               }

               Block var4 = var3.getBlock();
               if(var4 == Blocks.NETHER_PORTAL) {
                  ++this.numPortalBlocks;
               }

               if(var1 == 0) {
                  var4 = this.level.getBlockState(var2.relative(this.leftDir)).getBlock();
                  if(var4 != Blocks.OBSIDIAN) {
                     break label24;
                  }
               } else if(var1 == this.width - 1) {
                  var4 = this.level.getBlockState(var2.relative(this.rightDir)).getBlock();
                  if(var4 != Blocks.OBSIDIAN) {
                     break label24;
                  }
               }
            }
         }

         for(int var1 = 0; var1 < this.width; ++var1) {
            if(this.level.getBlockState(this.bottomLeft.relative(this.rightDir, var1).above(this.height)).getBlock() != Blocks.OBSIDIAN) {
               this.height = 0;
               break;
            }
         }

         if(this.height <= 21 && this.height >= 3) {
            return this.height;
         } else {
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
            return 0;
         }
      }

      protected boolean isEmpty(BlockState blockState) {
         Block var2 = blockState.getBlock();
         return blockState.isAir() || var2 == Blocks.FIRE || var2 == Blocks.NETHER_PORTAL;
      }

      public boolean isValid() {
         return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
      }

      public void createPortalBlocks() {
         for(int var1 = 0; var1 < this.width; ++var1) {
            BlockPos var2 = this.bottomLeft.relative(this.rightDir, var1);

            for(int var3 = 0; var3 < this.height; ++var3) {
               this.level.setBlock(var2.above(var3), (BlockState)Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, this.axis), 18);
            }
         }

      }

      private boolean hasAllPortalBlocks() {
         return this.numPortalBlocks >= this.width * this.height;
      }

      public boolean isComplete() {
         return this.isValid() && this.hasAllPortalBlocks();
      }
   }
}
