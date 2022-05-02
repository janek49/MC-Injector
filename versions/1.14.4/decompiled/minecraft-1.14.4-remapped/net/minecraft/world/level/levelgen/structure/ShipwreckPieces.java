package net.minecraft.world.level.levelgen.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class ShipwreckPieces {
   private static final BlockPos PIVOT = new BlockPos(4, 0, 15);
   private static final ResourceLocation[] STRUCTURE_LOCATION_BEACHED = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};
   private static final ResourceLocation[] STRUCTURE_LOCATION_OCEAN = new ResourceLocation[]{new ResourceLocation("shipwreck/with_mast"), new ResourceLocation("shipwreck/upsidedown_full"), new ResourceLocation("shipwreck/upsidedown_fronthalf"), new ResourceLocation("shipwreck/upsidedown_backhalf"), new ResourceLocation("shipwreck/sideways_full"), new ResourceLocation("shipwreck/sideways_fronthalf"), new ResourceLocation("shipwreck/sideways_backhalf"), new ResourceLocation("shipwreck/rightsideup_full"), new ResourceLocation("shipwreck/rightsideup_fronthalf"), new ResourceLocation("shipwreck/rightsideup_backhalf"), new ResourceLocation("shipwreck/with_mast_degraded"), new ResourceLocation("shipwreck/upsidedown_full_degraded"), new ResourceLocation("shipwreck/upsidedown_fronthalf_degraded"), new ResourceLocation("shipwreck/upsidedown_backhalf_degraded"), new ResourceLocation("shipwreck/sideways_full_degraded"), new ResourceLocation("shipwreck/sideways_fronthalf_degraded"), new ResourceLocation("shipwreck/sideways_backhalf_degraded"), new ResourceLocation("shipwreck/rightsideup_full_degraded"), new ResourceLocation("shipwreck/rightsideup_fronthalf_degraded"), new ResourceLocation("shipwreck/rightsideup_backhalf_degraded")};

   public static void addPieces(StructureManager structureManager, BlockPos blockPos, Rotation rotation, List list, Random random, ShipwreckConfiguration shipwreckConfiguration) {
      ResourceLocation var6 = shipwreckConfiguration.isBeached?STRUCTURE_LOCATION_BEACHED[random.nextInt(STRUCTURE_LOCATION_BEACHED.length)]:STRUCTURE_LOCATION_OCEAN[random.nextInt(STRUCTURE_LOCATION_OCEAN.length)];
      list.add(new ShipwreckPieces.ShipwreckPiece(structureManager, var6, blockPos, rotation, shipwreckConfiguration.isBeached));
   }

   public static class ShipwreckPiece extends TemplateStructurePiece {
      private final Rotation rotation;
      private final ResourceLocation templateLocation;
      private final boolean isBeached;

      public ShipwreckPiece(StructureManager structureManager, ResourceLocation templateLocation, BlockPos templatePosition, Rotation rotation, boolean isBeached) {
         super(StructurePieceType.SHIPWRECK_PIECE, 0);
         this.templatePosition = templatePosition;
         this.rotation = rotation;
         this.templateLocation = templateLocation;
         this.isBeached = isBeached;
         this.loadTemplate(structureManager);
      }

      public ShipwreckPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.SHIPWRECK_PIECE, compoundTag);
         this.templateLocation = new ResourceLocation(compoundTag.getString("Template"));
         this.isBeached = compoundTag.getBoolean("isBeached");
         this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
         this.loadTemplate(structureManager);
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putString("Template", this.templateLocation.toString());
         compoundTag.putBoolean("isBeached", this.isBeached);
         compoundTag.putString("Rot", this.rotation.name());
      }

      private void loadTemplate(StructureManager structureManager) {
         StructureTemplate var2 = structureManager.getOrCreate(this.templateLocation);
         StructurePlaceSettings var3 = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setRotationPivot(ShipwreckPieces.PIVOT).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
         this.setup(var2, this.templatePosition, var3);
      }

      protected void handleDataMarker(String string, BlockPos blockPos, LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         if("map_chest".equals(string)) {
            RandomizableContainerBlockEntity.setLootTable(levelAccessor, random, blockPos.below(), BuiltInLootTables.SHIPWRECK_MAP);
         } else if("treasure_chest".equals(string)) {
            RandomizableContainerBlockEntity.setLootTable(levelAccessor, random, blockPos.below(), BuiltInLootTables.SHIPWRECK_TREASURE);
         } else if("supply_chest".equals(string)) {
            RandomizableContainerBlockEntity.setLootTable(levelAccessor, random, blockPos.below(), BuiltInLootTables.SHIPWRECK_SUPPLY);
         }

      }

      public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
         int var5 = 256;
         int var6 = 0;
         BlockPos var7 = this.templatePosition.offset(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1);

         for(BlockPos var9 : BlockPos.betweenClosed(this.templatePosition, var7)) {
            int var10 = levelAccessor.getHeight(this.isBeached?Heightmap.Types.WORLD_SURFACE_WG:Heightmap.Types.OCEAN_FLOOR_WG, var9.getX(), var9.getZ());
            var6 += var10;
            var5 = Math.min(var5, var10);
         }

         var6 = var6 / (this.template.getSize().getX() * this.template.getSize().getZ());
         int var8 = this.isBeached?var5 - this.template.getSize().getY() / 2 - random.nextInt(3):var6;
         this.templatePosition = new BlockPos(this.templatePosition.getX(), var8, this.templatePosition.getZ());
         return super.postProcess(levelAccessor, random, boundingBox, chunkPos);
      }
   }
}
