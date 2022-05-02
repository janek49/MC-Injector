package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class IglooPieces {
   private static final ResourceLocation STRUCTURE_LOCATION_IGLOO = new ResourceLocation("igloo/top");
   private static final ResourceLocation STRUCTURE_LOCATION_LADDER = new ResourceLocation("igloo/middle");
   private static final ResourceLocation STRUCTURE_LOCATION_LABORATORY = new ResourceLocation("igloo/bottom");
   private static final Map PIVOTS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, new BlockPos(3, 5, 5), STRUCTURE_LOCATION_LADDER, new BlockPos(1, 3, 1), STRUCTURE_LOCATION_LABORATORY, new BlockPos(3, 6, 7));
   private static final Map OFFSETS = ImmutableMap.of(STRUCTURE_LOCATION_IGLOO, BlockPos.ZERO, STRUCTURE_LOCATION_LADDER, new BlockPos(2, -3, 4), STRUCTURE_LOCATION_LABORATORY, new BlockPos(0, -3, -2));

   public static void addPieces(StructureManager structureManager, BlockPos blockPos, Rotation rotation, List list, Random random, NoneFeatureConfiguration noneFeatureConfiguration) {
      if(random.nextDouble() < 0.5D) {
         int var6 = random.nextInt(8) + 4;
         list.add(new IglooPieces.IglooPiece(structureManager, STRUCTURE_LOCATION_LABORATORY, blockPos, rotation, var6 * 3));

         for(int var7 = 0; var7 < var6 - 1; ++var7) {
            list.add(new IglooPieces.IglooPiece(structureManager, STRUCTURE_LOCATION_LADDER, blockPos, rotation, var7 * 3));
         }
      }

      list.add(new IglooPieces.IglooPiece(structureManager, STRUCTURE_LOCATION_IGLOO, blockPos, rotation, 0));
   }

   public static class IglooPiece extends TemplateStructurePiece {
      private final ResourceLocation templateLocation;
      private final Rotation rotation;

      public IglooPiece(StructureManager structureManager, ResourceLocation templateLocation, BlockPos blockPos, Rotation rotation, int var5) {
         super(StructurePieceType.IGLOO, 0);
         this.templateLocation = templateLocation;
         BlockPos blockPos = (BlockPos)IglooPieces.OFFSETS.get(templateLocation);
         this.templatePosition = blockPos.offset(blockPos.getX(), blockPos.getY() - var5, blockPos.getZ());
         this.rotation = rotation;
         this.loadTemplate(structureManager);
      }

      public IglooPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.IGLOO, compoundTag);
         this.templateLocation = new ResourceLocation(compoundTag.getString("Template"));
         this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
         this.loadTemplate(structureManager);
      }

      private void loadTemplate(StructureManager structureManager) {
         StructureTemplate var2 = structureManager.getOrCreate(this.templateLocation);
         StructurePlaceSettings var3 = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot((BlockPos)IglooPieces.PIVOTS.get(this.templateLocation)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
         this.setup(var2, this.templatePosition, var3);
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putString("Template", this.templateLocation.toString());
         compoundTag.putString("Rot", this.rotation.name());
      }

      protected void handleDataMarker(String string, BlockPos blockPos, LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         if("chest".equals(string)) {
            levelAccessor.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
            BlockEntity var6 = levelAccessor.getBlockEntity(blockPos.below());
            if(var6 instanceof ChestBlockEntity) {
               ((ChestBlockEntity)var6).setLootTable(BuiltInLootTables.IGLOO_CHEST, random.nextLong());
            }

         }
      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         StructurePlaceSettings var5 = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot((BlockPos)IglooPieces.PIVOTS.get(this.templateLocation)).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
         BlockPos var6 = (BlockPos)IglooPieces.OFFSETS.get(this.templateLocation);
         BlockPos var7 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(var5, new BlockPos(3 - var6.getX(), 0, 0 - var6.getZ())));
         int var8 = levelAccessor.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var7.getX(), var7.getZ());
         BlockPos var9 = this.templatePosition;
         this.templatePosition = this.templatePosition.offset(0, var8 - 90 - 1, 0);
         boolean var10 = super.postProcess(levelAccessor, random, boundingBox, chunkPos);
         if(this.templateLocation.equals(IglooPieces.STRUCTURE_LOCATION_IGLOO)) {
            BlockPos var11 = this.templatePosition.offset(StructureTemplate.calculateRelativePosition(var5, new BlockPos(3, 0, 5)));
            BlockState var12 = levelAccessor.getBlockState(var11.below());
            if(!var12.isAir() && var12.getBlock() != Blocks.LADDER) {
               levelAccessor.setBlock(var11, Blocks.SNOW_BLOCK.defaultBlockState(), 3);
            }
         }

         this.templatePosition = var9;
         return var10;
      }
   }
}
