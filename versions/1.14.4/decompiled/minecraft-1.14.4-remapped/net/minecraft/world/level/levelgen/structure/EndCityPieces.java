package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class EndCityPieces {
   private static final StructurePlaceSettings OVERWRITE = (new StructurePlaceSettings()).setIgnoreEntities(true).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
   private static final StructurePlaceSettings INSERT = (new StructurePlaceSettings()).setIgnoreEntities(true).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
   private static final EndCityPieces.SectionGenerator HOUSE_TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
      public void init() {
      }

      public boolean generate(StructureManager structureManager, int var2, EndCityPieces.EndCityPiece endCityPieces$EndCityPiece, BlockPos blockPos, List list, Random random) {
         if(var2 > 8) {
            return false;
         } else {
            Rotation var7 = endCityPieces$EndCityPiece.placeSettings.getRotation();
            EndCityPieces.EndCityPiece var8 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPieces$EndCityPiece, blockPos, "base_floor", var7, true));
            int var9 = random.nextInt(3);
            if(var9 == 0) {
               EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var8, new BlockPos(-1, 4, -1), "base_roof", var7, true));
            } else if(var9 == 1) {
               var8 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var8, new BlockPos(-1, 0, -1), "second_floor_2", var7, false));
               var8 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var8, new BlockPos(-1, 8, -1), "second_roof", var7, false));
               EndCityPieces.recursiveChildren(structureManager, EndCityPieces.TOWER_GENERATOR, var2 + 1, var8, (BlockPos)null, list, random);
            } else if(var9 == 2) {
               var8 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var8, new BlockPos(-1, 0, -1), "second_floor_2", var7, false));
               var8 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var8, new BlockPos(-1, 4, -1), "third_floor_2", var7, false));
               var8 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var8, new BlockPos(-1, 8, -1), "third_roof", var7, true));
               EndCityPieces.recursiveChildren(structureManager, EndCityPieces.TOWER_GENERATOR, var2 + 1, var8, (BlockPos)null, list, random);
            }

            return true;
         }
      }
   };
   private static final List TOWER_BRIDGES = Lists.newArrayList(new Tuple[]{new Tuple(Rotation.NONE, new BlockPos(1, -1, 0)), new Tuple(Rotation.CLOCKWISE_90, new BlockPos(6, -1, 1)), new Tuple(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)), new Tuple(Rotation.CLOCKWISE_180, new BlockPos(5, -1, 6))});
   private static final EndCityPieces.SectionGenerator TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
      public void init() {
      }

      public boolean generate(StructureManager structureManager, int var2, EndCityPieces.EndCityPiece endCityPieces$EndCityPiece, BlockPos blockPos, List list, Random random) {
         Rotation var7 = endCityPieces$EndCityPiece.placeSettings.getRotation();
         EndCityPieces.EndCityPiece var8 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPieces$EndCityPiece, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", var7, true));
         var8 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var8, new BlockPos(0, 7, 0), "tower_piece", var7, true));
         EndCityPieces.EndCityPiece var9 = random.nextInt(3) == 0?var8:null;
         int var10 = 1 + random.nextInt(3);

         for(int var11 = 0; var11 < var10; ++var11) {
            var8 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var8, new BlockPos(0, 4, 0), "tower_piece", var7, true));
            if(var11 < var10 - 1 && random.nextBoolean()) {
               var9 = var8;
            }
         }

         if(var9 != null) {
            for(Tuple<Rotation, BlockPos> var12 : EndCityPieces.TOWER_BRIDGES) {
               if(random.nextBoolean()) {
                  EndCityPieces.EndCityPiece var13 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var9, (BlockPos)var12.getB(), "bridge_end", var7.getRotated((Rotation)var12.getA()), true));
                  EndCityPieces.recursiveChildren(structureManager, EndCityPieces.TOWER_BRIDGE_GENERATOR, var2 + 1, var13, (BlockPos)null, list, random);
               }
            }

            EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var8, new BlockPos(-1, 4, -1), "tower_top", var7, true));
         } else {
            if(var2 != 7) {
               return EndCityPieces.recursiveChildren(structureManager, EndCityPieces.FAT_TOWER_GENERATOR, var2 + 1, var8, (BlockPos)null, list, random);
            }

            EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var8, new BlockPos(-1, 4, -1), "tower_top", var7, true));
         }

         return true;
      }
   };
   private static final EndCityPieces.SectionGenerator TOWER_BRIDGE_GENERATOR = new EndCityPieces.SectionGenerator() {
      public boolean shipCreated;

      public void init() {
         this.shipCreated = false;
      }

      public boolean generate(StructureManager structureManager, int var2, EndCityPieces.EndCityPiece endCityPieces$EndCityPiece, BlockPos blockPos, List list, Random random) {
         Rotation var7 = endCityPieces$EndCityPiece.placeSettings.getRotation();
         int var8 = random.nextInt(4) + 1;
         EndCityPieces.EndCityPiece var9 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPieces$EndCityPiece, new BlockPos(0, 0, -4), "bridge_piece", var7, true));
         var9.genDepth = -1;
         int var10 = 0;

         for(int var11 = 0; var11 < var8; ++var11) {
            if(random.nextBoolean()) {
               var9 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var9, new BlockPos(0, var10, -4), "bridge_piece", var7, true));
               var10 = 0;
            } else {
               if(random.nextBoolean()) {
                  var9 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var9, new BlockPos(0, var10, -4), "bridge_steep_stairs", var7, true));
               } else {
                  var9 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var9, new BlockPos(0, var10, -8), "bridge_gentle_stairs", var7, true));
               }

               var10 = 4;
            }
         }

         if(!this.shipCreated && random.nextInt(10 - var2) == 0) {
            EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var9, new BlockPos(-8 + random.nextInt(8), var10, -70 + random.nextInt(10)), "ship", var7, true));
            this.shipCreated = true;
         } else if(!EndCityPieces.recursiveChildren(structureManager, EndCityPieces.HOUSE_TOWER_GENERATOR, var2 + 1, var9, new BlockPos(-3, var10 + 1, -11), list, random)) {
            return false;
         }

         var9 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, var9, new BlockPos(4, var10, 0), "bridge_end", var7.getRotated(Rotation.CLOCKWISE_180), true));
         var9.genDepth = -1;
         return true;
      }
   };
   private static final List FAT_TOWER_BRIDGES = Lists.newArrayList(new Tuple[]{new Tuple(Rotation.NONE, new BlockPos(4, -1, 0)), new Tuple(Rotation.CLOCKWISE_90, new BlockPos(12, -1, 4)), new Tuple(Rotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)), new Tuple(Rotation.CLOCKWISE_180, new BlockPos(8, -1, 12))});
   private static final EndCityPieces.SectionGenerator FAT_TOWER_GENERATOR = new EndCityPieces.SectionGenerator() {
      public void init() {
      }

      public boolean generate(StructureManager structureManager, int var2, EndCityPieces.EndCityPiece endCityPieces$EndCityPiece, BlockPos blockPos, List list, Random random) {
         Rotation var8 = endCityPieces$EndCityPiece.placeSettings.getRotation();
         EndCityPieces.EndCityPiece endCityPieces$EndCityPiece = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPieces$EndCityPiece, new BlockPos(-3, 4, -3), "fat_tower_base", var8, true));
         endCityPieces$EndCityPiece = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPieces$EndCityPiece, new BlockPos(0, 4, 0), "fat_tower_middle", var8, true));

         for(int var9 = 0; var9 < 2 && random.nextInt(3) != 0; ++var9) {
            endCityPieces$EndCityPiece = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPieces$EndCityPiece, new BlockPos(0, 8, 0), "fat_tower_middle", var8, true));

            for(Tuple<Rotation, BlockPos> var11 : EndCityPieces.FAT_TOWER_BRIDGES) {
               if(random.nextBoolean()) {
                  EndCityPieces.EndCityPiece var12 = EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPieces$EndCityPiece, (BlockPos)var11.getB(), "bridge_end", var8.getRotated((Rotation)var11.getA()), true));
                  EndCityPieces.recursiveChildren(structureManager, EndCityPieces.TOWER_BRIDGE_GENERATOR, var2 + 1, var12, (BlockPos)null, list, random);
               }
            }
         }

         EndCityPieces.addHelper(list, EndCityPieces.addPiece(structureManager, endCityPieces$EndCityPiece, new BlockPos(-2, 8, -2), "fat_tower_top", var8, true));
         return true;
      }
   };

   private static EndCityPieces.EndCityPiece addPiece(StructureManager structureManager, EndCityPieces.EndCityPiece var1, BlockPos blockPos, String string, Rotation rotation, boolean var5) {
      EndCityPieces.EndCityPiece var6 = new EndCityPieces.EndCityPiece(structureManager, string, var1.templatePosition, rotation, var5);
      BlockPos var7 = var1.template.calculateConnectedPosition(var1.placeSettings, blockPos, var6.placeSettings, BlockPos.ZERO);
      var6.move(var7.getX(), var7.getY(), var7.getZ());
      return var6;
   }

   public static void startHouseTower(StructureManager structureManager, BlockPos blockPos, Rotation rotation, List list, Random random) {
      FAT_TOWER_GENERATOR.init();
      HOUSE_TOWER_GENERATOR.init();
      TOWER_BRIDGE_GENERATOR.init();
      TOWER_GENERATOR.init();
      EndCityPieces.EndCityPiece var5 = addHelper(list, new EndCityPieces.EndCityPiece(structureManager, "base_floor", blockPos, rotation, true));
      var5 = addHelper(list, addPiece(structureManager, var5, new BlockPos(-1, 0, -1), "second_floor_1", rotation, false));
      var5 = addHelper(list, addPiece(structureManager, var5, new BlockPos(-1, 4, -1), "third_floor_1", rotation, false));
      var5 = addHelper(list, addPiece(structureManager, var5, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
      recursiveChildren(structureManager, TOWER_GENERATOR, 1, var5, (BlockPos)null, list, random);
   }

   private static EndCityPieces.EndCityPiece addHelper(List list, EndCityPieces.EndCityPiece var1) {
      list.add(var1);
      return var1;
   }

   private static boolean recursiveChildren(StructureManager structureManager, EndCityPieces.SectionGenerator endCityPieces$SectionGenerator, int var2, EndCityPieces.EndCityPiece endCityPieces$EndCityPiece, BlockPos blockPos, List list, Random random) {
      if(var2 > 8) {
         return false;
      } else {
         List<StructurePiece> list = Lists.newArrayList();
         if(endCityPieces$SectionGenerator.generate(structureManager, var2, endCityPieces$EndCityPiece, blockPos, list, random)) {
            boolean var8 = false;
            int var9 = random.nextInt();

            for(StructurePiece var11 : list) {
               var11.genDepth = var9;
               StructurePiece var12 = StructurePiece.findCollisionPiece(list, var11.getBoundingBox());
               if(var12 != null && var12.genDepth != endCityPieces$EndCityPiece.genDepth) {
                  var8 = true;
                  break;
               }
            }

            if(!var8) {
               list.addAll(list);
               return true;
            }
         }

         return false;
      }
   }

   public static class EndCityPiece extends TemplateStructurePiece {
      private final String templateName;
      private final Rotation rotation;
      private final boolean overwrite;

      public EndCityPiece(StructureManager structureManager, String templateName, BlockPos templatePosition, Rotation rotation, boolean overwrite) {
         super(StructurePieceType.END_CITY_PIECE, 0);
         this.templateName = templateName;
         this.templatePosition = templatePosition;
         this.rotation = rotation;
         this.overwrite = overwrite;
         this.loadTemplate(structureManager);
      }

      public EndCityPiece(StructureManager structureManager, CompoundTag compoundTag) {
         super(StructurePieceType.END_CITY_PIECE, compoundTag);
         this.templateName = compoundTag.getString("Template");
         this.rotation = Rotation.valueOf(compoundTag.getString("Rot"));
         this.overwrite = compoundTag.getBoolean("OW");
         this.loadTemplate(structureManager);
      }

      private void loadTemplate(StructureManager structureManager) {
         StructureTemplate var2 = structureManager.getOrCreate(new ResourceLocation("end_city/" + this.templateName));
         StructurePlaceSettings var3 = (this.overwrite?EndCityPieces.OVERWRITE:EndCityPieces.INSERT).copy().setRotation(this.rotation);
         this.setup(var2, this.templatePosition, var3);
      }

      protected void addAdditionalSaveData(CompoundTag compoundTag) {
         super.addAdditionalSaveData(compoundTag);
         compoundTag.putString("Template", this.templateName);
         compoundTag.putString("Rot", this.rotation.name());
         compoundTag.putBoolean("OW", this.overwrite);
      }

      protected void handleDataMarker(String string, BlockPos blockPos, LevelAccessor levelAccessor, Random random, BoundingBox boundingBox) {
         if(string.startsWith("Chest")) {
            BlockPos blockPos = blockPos.below();
            if(boundingBox.isInside(blockPos)) {
               RandomizableContainerBlockEntity.setLootTable(levelAccessor, random, blockPos, BuiltInLootTables.END_CITY_TREASURE);
            }
         } else if(string.startsWith("Sentry")) {
            Shulker var6 = (Shulker)EntityType.SHULKER.create(levelAccessor.getLevel());
            var6.setPos((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D);
            var6.setAttachPosition(blockPos);
            levelAccessor.addFreshEntity(var6);
         } else if(string.startsWith("Elytra")) {
            ItemFrame var6 = new ItemFrame(levelAccessor.getLevel(), blockPos, this.rotation.rotate(Direction.SOUTH));
            var6.setItem(new ItemStack(Items.ELYTRA), false);
            levelAccessor.addFreshEntity(var6);
         }

      }
   }

   interface SectionGenerator {
      void init();

      boolean generate(StructureManager var1, int var2, EndCityPieces.EndCityPiece var3, BlockPos var4, List var5, Random var6);
   }
}
