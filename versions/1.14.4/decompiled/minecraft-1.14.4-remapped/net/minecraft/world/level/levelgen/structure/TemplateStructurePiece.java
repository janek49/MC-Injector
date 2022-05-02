package net.minecraft.world.level.levelgen.structure;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Random;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class TemplateStructurePiece extends StructurePiece {
   private static final Logger LOGGER = LogManager.getLogger();
   protected StructureTemplate template;
   protected StructurePlaceSettings placeSettings;
   protected BlockPos templatePosition;

   public TemplateStructurePiece(StructurePieceType structurePieceType, int var2) {
      super(structurePieceType, var2);
   }

   public TemplateStructurePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
      super(structurePieceType, compoundTag);
      this.templatePosition = new BlockPos(compoundTag.getInt("TPX"), compoundTag.getInt("TPY"), compoundTag.getInt("TPZ"));
   }

   protected void setup(StructureTemplate template, BlockPos templatePosition, StructurePlaceSettings placeSettings) {
      this.template = template;
      this.setOrientation(Direction.NORTH);
      this.templatePosition = templatePosition;
      this.placeSettings = placeSettings;
      this.boundingBox = template.getBoundingBox(placeSettings, templatePosition);
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putInt("TPX", this.templatePosition.getX());
      compoundTag.putInt("TPY", this.templatePosition.getY());
      compoundTag.putInt("TPZ", this.templatePosition.getZ());
   }

   public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
      this.placeSettings.setBoundingBox(boundingBox);
      this.boundingBox = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
      if(this.template.placeInWorld(levelAccessor, this.templatePosition, this.placeSettings, 2)) {
         for(StructureTemplate.StructureBlockInfo var7 : this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK)) {
            if(var7.nbt != null) {
               StructureMode var8 = StructureMode.valueOf(var7.nbt.getString("mode"));
               if(var8 == StructureMode.DATA) {
                  this.handleDataMarker(var7.nbt.getString("metadata"), var7.pos, levelAccessor, random, boundingBox);
               }
            }
         }

         for(StructureTemplate.StructureBlockInfo var8 : this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.JIGSAW_BLOCK)) {
            if(var8.nbt != null) {
               String var9 = var8.nbt.getString("final_state");
               BlockStateParser var10 = new BlockStateParser(new StringReader(var9), false);
               BlockState var11 = Blocks.AIR.defaultBlockState();

               try {
                  var10.parse(true);
                  BlockState var12 = var10.getState();
                  if(var12 != null) {
                     var11 = var12;
                  } else {
                     LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", var9, var8.pos);
                  }
               } catch (CommandSyntaxException var13) {
                  LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", var9, var8.pos);
               }

               levelAccessor.setBlock(var8.pos, var11, 3);
            }
         }
      }

      return true;
   }

   protected abstract void handleDataMarker(String var1, BlockPos var2, LevelAccessor var3, Random var4, BoundingBox var5);

   public void move(int var1, int var2, int var3) {
      super.move(var1, var2, var3);
      this.templatePosition = this.templatePosition.offset(var1, var2, var3);
   }

   public Rotation getRotation() {
      return this.placeSettings.getRotation();
   }
}
