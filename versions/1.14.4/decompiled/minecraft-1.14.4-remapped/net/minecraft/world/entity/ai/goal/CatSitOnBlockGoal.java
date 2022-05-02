package net.minecraft.world.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

public class CatSitOnBlockGoal extends MoveToBlockGoal {
   private final Cat cat;

   public CatSitOnBlockGoal(Cat cat, double var2) {
      super(cat, var2, 8);
      this.cat = cat;
   }

   public boolean canUse() {
      return this.cat.isTame() && !this.cat.isSitting() && super.canUse();
   }

   public void start() {
      super.start();
      this.cat.getSitGoal().wantToSit(false);
   }

   public void stop() {
      super.stop();
      this.cat.setSitting(false);
   }

   public void tick() {
      super.tick();
      this.cat.getSitGoal().wantToSit(false);
      if(!this.isReachedTarget()) {
         this.cat.setSitting(false);
      } else if(!this.cat.isSitting()) {
         this.cat.setSitting(true);
      }

   }

   protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
      if(!levelReader.isEmptyBlock(blockPos.above())) {
         return false;
      } else {
         BlockState var3 = levelReader.getBlockState(blockPos);
         Block var4 = var3.getBlock();
         return var4 == Blocks.CHEST?ChestBlockEntity.getOpenCount(levelReader, blockPos) < 1:(var4 == Blocks.FURNACE && ((Boolean)var3.getValue(FurnaceBlock.LIT)).booleanValue()?true:var4.is(BlockTags.BEDS) && var3.getValue(BedBlock.PART) != BedPart.HEAD);
      }
   }
}
