package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class OceanRuinPieces {
   private static final ResourceLocation[] WARM_RUINS = new ResourceLocation[]{new ResourceLocation("underwater_ruin/warm_1"), new ResourceLocation("underwater_ruin/warm_2"), new ResourceLocation("underwater_ruin/warm_3"), new ResourceLocation("underwater_ruin/warm_4"), new ResourceLocation("underwater_ruin/warm_5"), new ResourceLocation("underwater_ruin/warm_6"), new ResourceLocation("underwater_ruin/warm_7"), new ResourceLocation("underwater_ruin/warm_8")};
   private static final ResourceLocation[] RUINS_BRICK = new ResourceLocation[]{new ResourceLocation("underwater_ruin/brick_1"), new ResourceLocation("underwater_ruin/brick_2"), new ResourceLocation("underwater_ruin/brick_3"), new ResourceLocation("underwater_ruin/brick_4"), new ResourceLocation("underwater_ruin/brick_5"), new ResourceLocation("underwater_ruin/brick_6"), new ResourceLocation("underwater_ruin/brick_7"), new ResourceLocation("underwater_ruin/brick_8")};
   private static final ResourceLocation[] RUINS_CRACKED = new ResourceLocation[]{new ResourceLocation("underwater_ruin/cracked_1"), new ResourceLocation("underwater_ruin/cracked_2"), new ResourceLocation("underwater_ruin/cracked_3"), new ResourceLocation("underwater_ruin/cracked_4"), new ResourceLocation("underwater_ruin/cracked_5"), new ResourceLocation("underwater_ruin/cracked_6"), new ResourceLocation("underwater_ruin/cracked_7"), new ResourceLocation("underwater_ruin/cracked_8")};
   private static final ResourceLocation[] RUINS_MOSSY = new ResourceLocation[]{new ResourceLocation("underwater_ruin/mossy_1"), new ResourceLocation("underwater_ruin/mossy_2"), new ResourceLocation("underwater_ruin/mossy_3"), new ResourceLocation("underwater_ruin/mossy_4"), new ResourceLocation("underwater_ruin/mossy_5"), new ResourceLocation("underwater_ruin/mossy_6"), new ResourceLocation("underwater_ruin/mossy_7"), new ResourceLocation("underwater_ruin/mossy_8")};
   private static final ResourceLocation[] BIG_RUINS_BRICK = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_brick_1"), new ResourceLocation("underwater_ruin/big_brick_2"), new ResourceLocation("underwater_ruin/big_brick_3"), new ResourceLocation("underwater_ruin/big_brick_8")};
   private static final ResourceLocation[] BIG_RUINS_MOSSY = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_mossy_1"), new ResourceLocation("underwater_ruin/big_mossy_2"), new ResourceLocation("underwater_ruin/big_mossy_3"), new ResourceLocation("underwater_ruin/big_mossy_8")};
   private static final ResourceLocation[] BIG_RUINS_CRACKED = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_cracked_1"), new ResourceLocation("underwater_ruin/big_cracked_2"), new ResourceLocation("underwater_ruin/big_cracked_3"), new ResourceLocation("underwater_ruin/big_cracked_8")};
   private static final ResourceLocation[] BIG_WARM_RUINS = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_warm_4"), new ResourceLocation("underwater_ruin/big_warm_5"), new ResourceLocation("underwater_ruin/big_warm_6"), new ResourceLocation("underwater_ruin/big_warm_7")};

   private static ResourceLocation getSmallWarmRuin(Random random) {
      return WARM_RUINS[random.nextInt(WARM_RUINS.length)];
   }

   private static ResourceLocation getBigWarmRuin(Random random) {
      return BIG_WARM_RUINS[random.nextInt(BIG_WARM_RUINS.length)];
   }

   public static void addPieces(StructureManager structureManager, BlockPos blockPos, Rotation rotation, List list, Random random, OceanRuinConfiguration oceanRuinConfiguration) {
      boolean var6 = random.nextFloat() <= oceanRuinConfiguration.largeProbability;
      float var7 = var6?0.9F:0.8F;
      addPiece(structureManager, blockPos, rotation, list, random, oceanRuinConfiguration, var6, var7);
      if(var6 && random.nextFloat() <= oceanRuinConfiguration.clusterProbability) {
         addClusterRuins(structureManager, random, rotation, blockPos, oceanRuinConfiguration, list);
      }

   }

   private static void addClusterRuins(StructureManager structureManager, Random random, Rotation rotation, BlockPos blockPos, OceanRuinConfiguration oceanRuinConfiguration, List list) {
      int var6 = blockPos.getX();
      int var7 = blockPos.getZ();
      BlockPos var8 = StructureTemplate.transform(new BlockPos(15, 0, 15), Mirror.NONE, rotation, BlockPos.ZERO).offset(var6, 0, var7);
      BoundingBox var9 = BoundingBox.createProper(var6, 0, var7, var8.getX(), 0, var8.getZ());
      BlockPos var10 = new BlockPos(Math.min(var6, var8.getX()), 0, Math.min(var7, var8.getZ()));
      List<BlockPos> var11 = allPositions(random, var10.getX(), var10.getZ());
      int var12 = Mth.nextInt(random, 4, 8);

      for(int var13 = 0; var13 < var12; ++var13) {
         if(!var11.isEmpty()) {
            int var14 = random.nextInt(var11.size());
            BlockPos var15 = (BlockPos)var11.remove(var14);
            int var16 = var15.getX();
            int var17 = var15.getZ();
            Rotation var18 = Rotation.values()[random.nextInt(Rotation.values().length)];
            BlockPos var19 = StructureTemplate.transform(new BlockPos(5, 0, 6), Mirror.NONE, var18, BlockPos.ZERO).offset(var16, 0, var17);
            BoundingBox var20 = BoundingBox.createProper(var16, 0, var17, var19.getX(), 0, var19.getZ());
            if(!var20.intersects(var9)) {
               addPiece(structureManager, var15, var18, list, random, oceanRuinConfiguration, false, 0.8F);
            }
         }
      }

   }

   private static List allPositions(Random random, int var1, int var2) {
      List<BlockPos> list = Lists.newArrayList();
      list.add(new BlockPos(var1 - 16 + Mth.nextInt(random, 1, 8), 90, var2 + 16 + Mth.nextInt(random, 1, 7)));
      list.add(new BlockPos(var1 - 16 + Mth.nextInt(random, 1, 8), 90, var2 + Mth.nextInt(random, 1, 7)));
      list.add(new BlockPos(var1 - 16 + Mth.nextInt(random, 1, 8), 90, var2 - 16 + Mth.nextInt(random, 4, 8)));
      list.add(new BlockPos(var1 + Mth.nextInt(random, 1, 7), 90, var2 + 16 + Mth.nextInt(random, 1, 7)));
      list.add(new BlockPos(var1 + Mth.nextInt(random, 1, 7), 90, var2 - 16 + Mth.nextInt(random, 4, 6)));
      list.add(new BlockPos(var1 + 16 + Mth.nextInt(random, 1, 7), 90, var2 + 16 + Mth.nextInt(random, 3, 8)));
      list.add(new BlockPos(var1 + 16 + Mth.nextInt(random, 1, 7), 90, var2 + Mth.nextInt(random, 1, 7)));
      list.add(new BlockPos(var1 + 16 + Mth.nextInt(random, 1, 7), 90, var2 - 16 + Mth.nextInt(random, 4, 8)));
      return list;
   }

   private static void addPiece(StructureManager structureManager, BlockPos blockPos, Rotation rotation, List list, Random random, OceanRuinConfiguration oceanRuinConfiguration, boolean var6, float var7) {
      if(oceanRuinConfiguration.biomeTemp == OceanRuinFeature.Type.WARM) {
         ResourceLocation var8 = var6?getBigWarmRuin(random):getSmallWarmRuin(random);
         list.add(new OceanRuinPieces.OceanRuinPiece(structureManager, var8, blockPos, rotation, var7, oceanRuinConfiguration.biomeTemp, var6));
      } else if(oceanRuinConfiguration.biomeTemp == OceanRuinFeature.Type.COLD) {
         ResourceLocation[] vars8 = var6?BIG_RUINS_BRICK:RUINS_BRICK;
         ResourceLocation[] vars9 = var6?BIG_RUINS_CRACKED:RUINS_CRACKED;
         ResourceLocation[] vars10 = var6?BIG_RUINS_MOSSY:RUINS_MOSSY;
         int var11 = random.nextInt(vars8.length);
         list.add(new OceanRuinPieces.OceanRuinPiece(structureManager, vars8[var11], blockPos, rotation, var7, oceanRuinConfiguration.biomeTemp, var6));
         list.add(new OceanRuinPieces.OceanRuinPiece(structureManager, vars9[var11], blockPos, rotation, 0.7F, oceanRuinConfiguration.biomeTemp, var6));
         list.add(new OceanRuinPieces.OceanRuinPiece(structureManager, vars10[var11], blockPos, rotation, 0.5F, oceanRuinConfiguration.biomeTemp, var6));
      }

   }

   public static class OceanRuinPiece extends TemplateStructurePiece {
      private final OceanRuinFeature.Type biomeType;
      private final float integrity;
      private final ResourceLocation templateLocation;
      private final Rotation rotation;
      private final boolean isLarge;

      public OceanRuinPiece(StructureManager structureManager, ResourceLocation templateLocation, BlockPos templatePosition, Rotation rotation, float integrity, OceanRuinFeature.Type biomeType, boolean isLarge) {
         super(StructurePieceType.OCEAN_RUIN, 0);
         this.templateLocation = templateLocation;
         this.templatePosition = templatePosition;
         this.rotation = rotation;
         this.integrity = integrity;
         this.biomeType = biomeType;
         this.isLarge = isLarge;
         this.loadTemplate(structureManager);
      }

      public OceanRuinPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.OCEAN_RUIN, compoundTag);
         this.templateLocation = new ResourceLocation(compoundTag.getString("Template"));
         this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
         this.integrity = compoundTag.getFloat("Integrity");
         this.biomeType = OceanRuinFeature.Type.valueOf(compoundTag.getString("BiomeType"));
         this.isLarge = compoundTag.getBoolean("IsLarge");
         this.loadTemplate(structureManager);
      }

      private void loadTemplate(StructureManager structureManager) {
         StructureTemplate var2 = structureManager.getOrCreate(this.templateLocation);
         StructurePlaceSettings var3 = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
         this.setup(var2, this.templatePosition, var3);
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putString("Template", this.templateLocation.toString());
         compoundTag.putString("Rot", this.rotation.name());
         compoundTag.putFloat("Integrity", this.integrity);
         compoundTag.putString("BiomeType", this.biomeType.toString());
         compoundTag.putBoolean("IsLarge", this.isLarge);
      }

      protected void handleDataMarker(String string, BlockPos blockPos, LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         if("chest".equals(string)) {
            levelAccessor.setBlock(blockPos, (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, Boolean.valueOf(levelAccessor.getFluidState(blockPos).is(FluidTags.WATER))), 2);
            BlockEntity var6 = levelAccessor.getBlockEntity(blockPos);
            if(var6 instanceof ChestBlockEntity) {
               ((ChestBlockEntity)var6).setLootTable(this.isLarge?BuiltInLootTables.UNDERWATER_RUIN_BIG:BuiltInLootTables.UNDERWATER_RUIN_SMALL, random.nextLong());
            }
         } else if("drowned".equals(string)) {
            Drowned var6 = (Drowned)EntityType.DROWNED.create(levelAccessor.getLevel());
            var6.setPersistenceRequired();
            var6.moveTo(blockPos, 0.0F, 0.0F);
            var6.finalizeSpawn(levelAccessor, levelAccessor.getCurrentDifficultyAt(blockPos), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            levelAccessor.addFreshEntity(var6);
            if(blockPos.getY() > levelAccessor.getSeaLevel()) {
               levelAccessor.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 2);
            } else {
               levelAccessor.setBlock(blockPos, Blocks.WATER.defaultBlockState(), 2);
            }
         }

      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         this.placeSettings.clearProcessors().addProcessor(new BlockRotProcessor(this.integrity)).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
         int var5 = levelAccessor.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
         this.templatePosition = new BlockPos(this.templatePosition.getX(), var5, this.templatePosition.getZ());
         BlockPos var6 = StructureTemplate.transform(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.rotation, BlockPos.ZERO).offset(this.templatePosition);
         this.templatePosition = new BlockPos(this.templatePosition.getX(), this.getHeight(this.templatePosition, levelAccessor, var6), this.templatePosition.getZ());
         return super.postProcess(levelAccessor, random, boundingBox, chunkPos);
      }

      private int getHeight(BlockPos var1, BlockGetter blockGetter, BlockPos var3) {
         int var4 = var1.getY();
         int var5 = 512;
         int var6 = var4 - 1;
         int var7 = 0;

         for(BlockPos var9 : BlockPos.betweenClosed(var1, var3)) {
            int var10 = var9.getX();
            int var11 = var9.getZ();
            int var12 = var1.getY() - 1;
            BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos(var10, var12, var11);
            BlockState var14 = blockGetter.getBlockState(var13);

            for(FluidState var15 = blockGetter.getFluidState(var13); (var14.isAir() || var15.is(FluidTags.WATER) || var14.getBlock().is(BlockTags.ICE)) && var12 > 1; var15 = blockGetter.getFluidState(var13)) {
               --var12;
               var13.set(var10, var12, var11);
               var14 = blockGetter.getBlockState(var13);
            }

            var5 = Math.min(var5, var12);
            if(var12 < var6 - 2) {
               ++var7;
            }
         }

         int var8 = Math.abs(var1.getX() - var3.getX());
         if(var6 - var5 > 2 && var7 > var8 - 2) {
            var4 = var5 + 1;
         }

         return var4;
      }
   }
}
