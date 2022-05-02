package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class FossilFeature extends Feature {
   private static final ResourceLocation SPINE_1 = new ResourceLocation("fossil/spine_1");
   private static final ResourceLocation SPINE_2 = new ResourceLocation("fossil/spine_2");
   private static final ResourceLocation SPINE_3 = new ResourceLocation("fossil/spine_3");
   private static final ResourceLocation SPINE_4 = new ResourceLocation("fossil/spine_4");
   private static final ResourceLocation SPINE_1_COAL = new ResourceLocation("fossil/spine_1_coal");
   private static final ResourceLocation SPINE_2_COAL = new ResourceLocation("fossil/spine_2_coal");
   private static final ResourceLocation SPINE_3_COAL = new ResourceLocation("fossil/spine_3_coal");
   private static final ResourceLocation SPINE_4_COAL = new ResourceLocation("fossil/spine_4_coal");
   private static final ResourceLocation SKULL_1 = new ResourceLocation("fossil/skull_1");
   private static final ResourceLocation SKULL_2 = new ResourceLocation("fossil/skull_2");
   private static final ResourceLocation SKULL_3 = new ResourceLocation("fossil/skull_3");
   private static final ResourceLocation SKULL_4 = new ResourceLocation("fossil/skull_4");
   private static final ResourceLocation SKULL_1_COAL = new ResourceLocation("fossil/skull_1_coal");
   private static final ResourceLocation SKULL_2_COAL = new ResourceLocation("fossil/skull_2_coal");
   private static final ResourceLocation SKULL_3_COAL = new ResourceLocation("fossil/skull_3_coal");
   private static final ResourceLocation SKULL_4_COAL = new ResourceLocation("fossil/skull_4_coal");
   private static final ResourceLocation[] fossils = new ResourceLocation[]{SPINE_1, SPINE_2, SPINE_3, SPINE_4, SKULL_1, SKULL_2, SKULL_3, SKULL_4};
   private static final ResourceLocation[] fossilsCoal = new ResourceLocation[]{SPINE_1_COAL, SPINE_2_COAL, SPINE_3_COAL, SPINE_4_COAL, SKULL_1_COAL, SKULL_2_COAL, SKULL_3_COAL, SKULL_4_COAL};

   public FossilFeature(Function function) {
      super(function);
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, NoneFeatureConfiguration noneFeatureConfiguration) {
      Random random = levelAccessor.getRandom();
      Rotation[] vars7 = Rotation.values();
      Rotation var8 = vars7[random.nextInt(vars7.length)];
      int var9 = random.nextInt(fossils.length);
      StructureManager var10 = ((ServerLevel)levelAccessor.getLevel()).getLevelStorage().getStructureManager();
      StructureTemplate var11 = var10.getOrCreate(fossils[var9]);
      StructureTemplate var12 = var10.getOrCreate(fossilsCoal[var9]);
      ChunkPos var13 = new ChunkPos(blockPos);
      BoundingBox var14 = new BoundingBox(var13.getMinBlockX(), 0, var13.getMinBlockZ(), var13.getMaxBlockX(), 256, var13.getMaxBlockZ());
      StructurePlaceSettings var15 = (new StructurePlaceSettings()).setRotation(var8).setBoundingBox(var14).setRandom(random).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
      BlockPos var16 = var11.getSize(var8);
      int var17 = random.nextInt(16 - var16.getX());
      int var18 = random.nextInt(16 - var16.getZ());
      int var19 = 256;

      for(int var20 = 0; var20 < var16.getX(); ++var20) {
         for(int var21 = 0; var21 < var16.getZ(); ++var21) {
            var19 = Math.min(var19, levelAccessor.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, blockPos.getX() + var20 + var17, blockPos.getZ() + var21 + var18));
         }
      }

      int var20 = Math.max(var19 - 15 - random.nextInt(10), 10);
      BlockPos var21 = var11.getZeroPositionWithTransform(blockPos.offset(var17, var20, var18), Mirror.NONE, var8);
      BlockRotProcessor var22 = new BlockRotProcessor(0.9F);
      var15.clearProcessors().addProcessor(var22);
      var11.placeInWorld(levelAccessor, var21, var15, 4);
      var15.popProcessor(var22);
      BlockRotProcessor var23 = new BlockRotProcessor(0.1F);
      var15.clearProcessors().addProcessor(var23);
      var12.placeInWorld(levelAccessor, var21, var15, 4);
      return true;
   }
}
