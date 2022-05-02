package net.minecraft.world.level.levelgen.feature.structures;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePools;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIO;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JigsawPlacement {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final StructureTemplatePools POOLS = new StructureTemplatePools();

   public static void addPieces(ResourceLocation resourceLocation, int var1, JigsawPlacement.PieceFactory jigsawPlacement$PieceFactory, ChunkGenerator chunkGenerator, StructureManager structureManager, BlockPos blockPos, List list, Random random) {
      StructureFeatureIO.bootstrap();
      new JigsawPlacement.Placer(resourceLocation, var1, jigsawPlacement$PieceFactory, chunkGenerator, structureManager, blockPos, list, random);
   }

   static {
      POOLS.register(StructureTemplatePool.EMPTY);
   }

   public interface PieceFactory {
      PoolElementStructurePiece create(StructureManager var1, StructurePoolElement var2, BlockPos var3, int var4, Rotation var5, BoundingBox var6);
   }

   static final class PieceState {
      private final PoolElementStructurePiece piece;
      private final AtomicReference free;
      private final int boundsTop;
      private final int depth;

      private PieceState(PoolElementStructurePiece piece, AtomicReference free, int boundsTop, int depth) {
         this.piece = piece;
         this.free = free;
         this.boundsTop = boundsTop;
         this.depth = depth;
      }
   }

   static final class Placer {
      private final int maxDepth;
      private final JigsawPlacement.PieceFactory factory;
      private final ChunkGenerator chunkGenerator;
      private final StructureManager structureManager;
      private final List pieces;
      private final Random random;
      private final Deque placing = Queues.newArrayDeque();

      public Placer(ResourceLocation resourceLocation, int maxDepth, JigsawPlacement.PieceFactory factory, ChunkGenerator chunkGenerator, StructureManager structureManager, BlockPos blockPos, List pieces, Random random) {
         this.maxDepth = maxDepth;
         this.factory = factory;
         this.chunkGenerator = chunkGenerator;
         this.structureManager = structureManager;
         this.pieces = pieces;
         this.random = random;
         Rotation var9 = Rotation.getRandom(random);
         StructureTemplatePool var10 = JigsawPlacement.POOLS.getPool(resourceLocation);
         StructurePoolElement var11 = var10.getRandomTemplate(random);
         PoolElementStructurePiece var12 = factory.create(structureManager, var11, blockPos, var11.getGroundLevelDelta(), var9, var11.getBoundingBox(structureManager, blockPos, var9));
         BoundingBox var13 = var12.getBoundingBox();
         int var14 = (var13.x1 + var13.x0) / 2;
         int var15 = (var13.z1 + var13.z0) / 2;
         int var16 = chunkGenerator.getFirstFreeHeight(var14, var15, Heightmap.Types.WORLD_SURFACE_WG);
         var12.move(0, var16 - (var13.y0 + var12.getGroundLevelDelta()), 0);
         pieces.add(var12);
         if(maxDepth > 0) {
            int var17 = 80;
            AABB var18 = new AABB((double)(var14 - 80), (double)(var16 - 80), (double)(var15 - 80), (double)(var14 + 80 + 1), (double)(var16 + 80 + 1), (double)(var15 + 80 + 1));
            this.placing.addLast(new JigsawPlacement.PieceState(var12, new AtomicReference(Shapes.join(Shapes.create(var18), Shapes.create(AABB.of(var13)), BooleanOp.ONLY_FIRST)), var16 + 80, 0));

            while(!this.placing.isEmpty()) {
               JigsawPlacement.PieceState var19 = (JigsawPlacement.PieceState)this.placing.removeFirst();
               this.tryPlacingChildren(var19.piece, var19.free, var19.boundsTop, var19.depth);
            }

         }
      }

      private void tryPlacingChildren(PoolElementStructurePiece poolElementStructurePiece, AtomicReference atomicReference, int var3, int var4) {
         StructurePoolElement var5 = poolElementStructurePiece.getElement();
         BlockPos var6 = poolElementStructurePiece.getPosition();
         Rotation var7 = poolElementStructurePiece.getRotation();
         StructureTemplatePool.Projection var8 = var5.getProjection();
         boolean var9 = var8 == StructureTemplatePool.Projection.RIGID;
         AtomicReference<VoxelShape> var10 = new AtomicReference();
         BoundingBox var11 = poolElementStructurePiece.getBoundingBox();
         int var12 = var11.y0;

         label96:
         for(StructureTemplate.StructureBlockInfo var14 : var5.getShuffledJigsawBlocks(this.structureManager, var6, var7, this.random)) {
            Direction var15 = (Direction)var14.state.getValue(JigsawBlock.FACING);
            BlockPos var16 = var14.pos;
            BlockPos var17 = var16.relative(var15);
            int var18 = var16.getY() - var12;
            int var19 = -1;
            StructureTemplatePool var20 = JigsawPlacement.POOLS.getPool(new ResourceLocation(var14.nbt.getString("target_pool")));
            StructureTemplatePool var21 = JigsawPlacement.POOLS.getPool(var20.getFallback());
            if(var20 != StructureTemplatePool.INVALID && (var20.size() != 0 || var20 == StructureTemplatePool.EMPTY)) {
               boolean var24 = var11.isInside(var17);
               AtomicReference<VoxelShape> var22;
               int var23;
               if(var24) {
                  var22 = var10;
                  var23 = var12;
                  if(var10.get() == null) {
                     var10.set(Shapes.create(AABB.of(var11)));
                  }
               } else {
                  var22 = atomicReference;
                  var23 = var3;
               }

               List<StructurePoolElement> var25 = Lists.newArrayList();
               if(var4 != this.maxDepth) {
                  var25.addAll(var20.getShuffledTemplates(this.random));
               }

               var25.addAll(var21.getShuffledTemplates(this.random));

               for(StructurePoolElement var27 : var25) {
                  if(var27 == EmptyPoolElement.INSTANCE) {
                     break;
                  }

                  for(Rotation var29 : Rotation.getShuffled(this.random)) {
                     List<StructureTemplate.StructureBlockInfo> var30 = var27.getShuffledJigsawBlocks(this.structureManager, BlockPos.ZERO, var29, this.random);
                     BoundingBox var31 = var27.getBoundingBox(this.structureManager, BlockPos.ZERO, var29);
                     int var32;
                     if(var31.getYSpan() > 16) {
                        var32 = 0;
                     } else {
                        var32 = var30.stream().mapToInt((structureTemplate$StructureBlockInfo) -> {
                           if(!var31.isInside(structureTemplate$StructureBlockInfo.pos.relative((Direction)structureTemplate$StructureBlockInfo.state.getValue(JigsawBlock.FACING)))) {
                              return 0;
                           } else {
                              ResourceLocation var3 = new ResourceLocation(structureTemplate$StructureBlockInfo.nbt.getString("target_pool"));
                              StructureTemplatePool var4 = JigsawPlacement.POOLS.getPool(var3);
                              StructureTemplatePool var5 = JigsawPlacement.POOLS.getPool(var4.getFallback());
                              return Math.max(var4.getMaxSize(this.structureManager), var5.getMaxSize(this.structureManager));
                           }
                        }).max().orElse(0);
                     }

                     for(StructureTemplate.StructureBlockInfo var34 : var30) {
                        if(JigsawBlock.canAttach(var14, var34)) {
                           BlockPos var35 = var34.pos;
                           BlockPos var36 = new BlockPos(var17.getX() - var35.getX(), var17.getY() - var35.getY(), var17.getZ() - var35.getZ());
                           BoundingBox var37 = var27.getBoundingBox(this.structureManager, var36, var29);
                           int var38 = var37.y0;
                           StructureTemplatePool.Projection var39 = var27.getProjection();
                           boolean var40 = var39 == StructureTemplatePool.Projection.RIGID;
                           int var41 = var35.getY();
                           int var42 = var18 - var41 + ((Direction)var14.state.getValue(JigsawBlock.FACING)).getStepY();
                           int var43;
                           if(var9 && var40) {
                              var43 = var12 + var42;
                           } else {
                              if(var19 == -1) {
                                 var19 = this.chunkGenerator.getFirstFreeHeight(var16.getX(), var16.getZ(), Heightmap.Types.WORLD_SURFACE_WG);
                              }

                              var43 = var19 - var41;
                           }

                           int var44 = var43 - var38;
                           BoundingBox var45 = var37.moved(0, var44, 0);
                           BlockPos var46 = var36.offset(0, var44, 0);
                           if(var32 > 0) {
                              int var47 = Math.max(var32 + 1, var45.y1 - var45.y0);
                              var45.y1 = var45.y0 + var47;
                           }

                           if(!Shapes.joinIsNotEmpty((VoxelShape)var22.get(), Shapes.create(AABB.of(var45).deflate(0.25D)), BooleanOp.ONLY_SECOND)) {
                              var22.set(Shapes.joinUnoptimized((VoxelShape)var22.get(), Shapes.create(AABB.of(var45)), BooleanOp.ONLY_FIRST));
                              int var47 = poolElementStructurePiece.getGroundLevelDelta();
                              int var48;
                              if(var40) {
                                 var48 = var47 - var42;
                              } else {
                                 var48 = var27.getGroundLevelDelta();
                              }

                              PoolElementStructurePiece var49 = this.factory.create(this.structureManager, var27, var46, var48, var29, var45);
                              int var50;
                              if(var9) {
                                 var50 = var12 + var18;
                              } else if(var40) {
                                 var50 = var43 + var41;
                              } else {
                                 if(var19 == -1) {
                                    var19 = this.chunkGenerator.getFirstFreeHeight(var16.getX(), var16.getZ(), Heightmap.Types.WORLD_SURFACE_WG);
                                 }

                                 var50 = var19 + var42 / 2;
                              }

                              poolElementStructurePiece.addJunction(new JigsawJunction(var17.getX(), var50 - var18 + var47, var17.getZ(), var42, var39));
                              var49.addJunction(new JigsawJunction(var16.getX(), var50 - var41 + var48, var16.getZ(), -var42, var8));
                              this.pieces.add(var49);
                              if(var4 + 1 <= this.maxDepth) {
                                 this.placing.addLast(new JigsawPlacement.PieceState(var49, var22, var23, var4 + 1));
                              }
                              continue label96;
                           }
                        }
                     }
                  }
               }
            } else {
               JigsawPlacement.LOGGER.warn("Empty or none existent pool: {}", var14.nbt.getString("target_pool"));
            }
         }

      }
   }
}
