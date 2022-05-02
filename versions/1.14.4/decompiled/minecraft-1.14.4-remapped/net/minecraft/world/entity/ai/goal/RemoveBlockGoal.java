package net.minecraft.world.entity.ai.goal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;

public class RemoveBlockGoal extends MoveToBlockGoal {
   private final Block blockToRemove;
   private final Mob removerMob;
   private int ticksSinceReachedGoal;

   public RemoveBlockGoal(Block blockToRemove, PathfinderMob removerMob, double var3, int var5) {
      super(removerMob, var3, 24, var5);
      this.blockToRemove = blockToRemove;
      this.removerMob = removerMob;
   }

   public boolean canUse() {
      if(!this.removerMob.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
         return false;
      } else if(this.nextStartTick > 0) {
         --this.nextStartTick;
         return false;
      } else if(this.tryFindBlock()) {
         this.nextStartTick = 20;
         return true;
      } else {
         this.nextStartTick = this.nextStartTick(this.mob);
         return false;
      }
   }

   private boolean tryFindBlock() {
      return this.blockPos != null && this.isValidTarget(this.mob.level, this.blockPos)?true:this.findNearestBlock();
   }

   public void stop() {
      super.stop();
      this.removerMob.fallDistance = 1.0F;
   }

   public void start() {
      super.start();
      this.ticksSinceReachedGoal = 0;
   }

   public void playDestroyProgressSound(LevelAccessor levelAccessor, BlockPos blockPos) {
   }

   public void playBreakSound(Level level, BlockPos blockPos) {
   }

   public void tick() {
      super.tick();
      Level var1 = this.removerMob.level;
      BlockPos var2 = new BlockPos(this.removerMob);
      BlockPos var3 = this.getPosWithBlock(var2, var1);
      Random var4 = this.removerMob.getRandom();
      if(this.isReachedTarget() && var3 != null) {
         if(this.ticksSinceReachedGoal > 0) {
            Vec3 var5 = this.removerMob.getDeltaMovement();
            this.removerMob.setDeltaMovement(var5.x, 0.3D, var5.z);
            if(!var1.isClientSide) {
               double var6 = 0.08D;
               ((ServerLevel)var1).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.EGG)), (double)var3.getX() + 0.5D, (double)var3.getY() + 0.7D, (double)var3.getZ() + 0.5D, 3, ((double)var4.nextFloat() - 0.5D) * 0.08D, ((double)var4.nextFloat() - 0.5D) * 0.08D, ((double)var4.nextFloat() - 0.5D) * 0.08D, 0.15000000596046448D);
            }
         }

         if(this.ticksSinceReachedGoal % 2 == 0) {
            Vec3 var5 = this.removerMob.getDeltaMovement();
            this.removerMob.setDeltaMovement(var5.x, -0.3D, var5.z);
            if(this.ticksSinceReachedGoal % 6 == 0) {
               this.playDestroyProgressSound(var1, this.blockPos);
            }
         }

         if(this.ticksSinceReachedGoal > 60) {
            var1.removeBlock(var3, false);
            if(!var1.isClientSide) {
               for(int var5 = 0; var5 < 20; ++var5) {
                  double var6 = var4.nextGaussian() * 0.02D;
                  double var8 = var4.nextGaussian() * 0.02D;
                  double var10 = var4.nextGaussian() * 0.02D;
                  ((ServerLevel)var1).sendParticles(ParticleTypes.POOF, (double)var3.getX() + 0.5D, (double)var3.getY(), (double)var3.getZ() + 0.5D, 1, var6, var8, var10, 0.15000000596046448D);
               }

               this.playBreakSound(var1, var3);
            }
         }

         ++this.ticksSinceReachedGoal;
      }

   }

   @Nullable
   private BlockPos getPosWithBlock(BlockPos var1, BlockGetter blockGetter) {
      if(blockGetter.getBlockState(var1).getBlock() == this.blockToRemove) {
         return var1;
      } else {
         BlockPos[] vars3 = new BlockPos[]{var1.below(), var1.west(), var1.east(), var1.north(), var1.south(), var1.below().below()};

         for(BlockPos var7 : vars3) {
            if(blockGetter.getBlockState(var7).getBlock() == this.blockToRemove) {
               return var7;
            }
         }

         return null;
      }
   }

   protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
      ChunkAccess var3 = levelReader.getChunk(blockPos.getX() >> 4, blockPos.getZ() >> 4, ChunkStatus.FULL, false);
      return var3 == null?false:var3.getBlockState(blockPos).getBlock() == this.blockToRemove && var3.getBlockState(blockPos.above()).isAir() && var3.getBlockState(blockPos.above(2)).isAir();
   }
}
