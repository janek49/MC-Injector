package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

public class EndGatewayFeature extends Feature {
   public EndGatewayFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, EndGatewayConfiguration endGatewayConfiguration) {
      for(BlockPos var7 : BlockPos.betweenClosed(blockPos.offset(-1, -2, -1), blockPos.offset(1, 2, 1))) {
         boolean var8 = var7.getX() == blockPos.getX();
         boolean var9 = var7.getY() == blockPos.getY();
         boolean var10 = var7.getZ() == blockPos.getZ();
         boolean var11 = Math.abs(var7.getY() - blockPos.getY()) == 2;
         if(var8 && var9 && var10) {
            BlockPos var12 = var7.immutable();
            this.setBlock(levelAccessor, var12, Blocks.END_GATEWAY.defaultBlockState());
            endGatewayConfiguration.getExit().ifPresent((var3) -> {
               BlockEntity var4 = levelAccessor.getBlockEntity(var12);
               if(var4 instanceof TheEndGatewayBlockEntity) {
                  TheEndGatewayBlockEntity var5 = (TheEndGatewayBlockEntity)var4;
                  var5.setExitPosition(var3, endGatewayConfiguration.isExitExact());
                  var4.setChanged();
               }

            });
         } else if(var9) {
            this.setBlock(levelAccessor, var7, Blocks.AIR.defaultBlockState());
         } else if(var11 && var8 && var10) {
            this.setBlock(levelAccessor, var7, Blocks.BEDROCK.defaultBlockState());
         } else if((var8 || var10) && !var11) {
            this.setBlock(levelAccessor, var7, Blocks.BEDROCK.defaultBlockState());
         } else {
            this.setBlock(levelAccessor, var7, Blocks.AIR.defaultBlockState());
         }
      }

      return true;
   }
}
