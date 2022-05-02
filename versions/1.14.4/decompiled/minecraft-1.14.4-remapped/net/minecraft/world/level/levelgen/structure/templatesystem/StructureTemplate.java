package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

public class StructureTemplate {
   private final List palettes = Lists.newArrayList();
   private final List entityInfoList = Lists.newArrayList();
   private BlockPos size = BlockPos.ZERO;
   private String author = "?";

   public BlockPos getSize() {
      return this.size;
   }

   public void setAuthor(String author) {
      this.author = author;
   }

   public String getAuthor() {
      return this.author;
   }

   public void fillFromWorld(Level level, BlockPos var2, BlockPos size, boolean var4, @Nullable Block block) {
      if(size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1) {
         BlockPos var6 = var2.offset(size).offset(-1, -1, -1);
         List<StructureTemplate.StructureBlockInfo> var7 = Lists.newArrayList();
         List<StructureTemplate.StructureBlockInfo> var8 = Lists.newArrayList();
         List<StructureTemplate.StructureBlockInfo> var9 = Lists.newArrayList();
         BlockPos var10 = new BlockPos(Math.min(var2.getX(), var6.getX()), Math.min(var2.getY(), var6.getY()), Math.min(var2.getZ(), var6.getZ()));
         BlockPos var11 = new BlockPos(Math.max(var2.getX(), var6.getX()), Math.max(var2.getY(), var6.getY()), Math.max(var2.getZ(), var6.getZ()));
         this.size = size;

         for(BlockPos var13 : BlockPos.betweenClosed(var10, var11)) {
            BlockPos var14 = var13.subtract(var10);
            BlockState var15 = level.getBlockState(var13);
            if(block == null || block != var15.getBlock()) {
               BlockEntity var16 = level.getBlockEntity(var13);
               if(var16 != null) {
                  CompoundTag var17 = var16.save(new CompoundTag());
                  var17.remove("x");
                  var17.remove("y");
                  var17.remove("z");
                  var8.add(new StructureTemplate.StructureBlockInfo(var14, var15, var17));
               } else if(!var15.isSolidRender(level, var13) && !var15.isCollisionShapeFullBlock(level, var13)) {
                  var9.add(new StructureTemplate.StructureBlockInfo(var14, var15, (CompoundTag)null));
               } else {
                  var7.add(new StructureTemplate.StructureBlockInfo(var14, var15, (CompoundTag)null));
               }
            }
         }

         List<StructureTemplate.StructureBlockInfo> var12 = Lists.newArrayList();
         var12.addAll(var7);
         var12.addAll(var8);
         var12.addAll(var9);
         this.palettes.clear();
         this.palettes.add(var12);
         if(var4) {
            this.fillEntityList(level, var10, var11.offset(1, 1, 1));
         } else {
            this.entityInfoList.clear();
         }

      }
   }

   private void fillEntityList(Level level, BlockPos var2, BlockPos var3) {
      List<Entity> var4 = level.getEntitiesOfClass(Entity.class, new AABB(var2, var3), (entity) -> {
         return !(entity instanceof Player);
      });
      this.entityInfoList.clear();

      for(Entity var6 : var4) {
         Vec3 var7 = new Vec3(var6.x - (double)var2.getX(), var6.y - (double)var2.getY(), var6.z - (double)var2.getZ());
         CompoundTag var8 = new CompoundTag();
         var6.save(var8);
         BlockPos var9;
         if(var6 instanceof Painting) {
            var9 = ((Painting)var6).getPos().subtract(var2);
         } else {
            var9 = new BlockPos(var7);
         }

         this.entityInfoList.add(new StructureTemplate.StructureEntityInfo(var7, var9, var8));
      }

   }

   public List filterBlocks(BlockPos blockPos, StructurePlaceSettings structurePlaceSettings, Block block) {
      return this.filterBlocks(blockPos, structurePlaceSettings, block, true);
   }

   public List filterBlocks(BlockPos blockPos, StructurePlaceSettings structurePlaceSettings, Block block, boolean var4) {
      List<StructureTemplate.StructureBlockInfo> list = Lists.newArrayList();
      BoundingBox var6 = structurePlaceSettings.getBoundingBox();

      for(StructureTemplate.StructureBlockInfo var8 : structurePlaceSettings.getPalette(this.palettes, blockPos)) {
         BlockPos var9 = var4?calculateRelativePosition(structurePlaceSettings, var8.pos).offset(blockPos):var8.pos;
         if(var6 == null || var6.isInside(var9)) {
            BlockState var10 = var8.state;
            if(var10.getBlock() == block) {
               list.add(new StructureTemplate.StructureBlockInfo(var9, var10.rotate(structurePlaceSettings.getRotation()), var8.nbt));
            }
         }
      }

      return list;
   }

   public BlockPos calculateConnectedPosition(StructurePlaceSettings var1, BlockPos var2, StructurePlaceSettings var3, BlockPos var4) {
      BlockPos var5 = calculateRelativePosition(var1, var2);
      BlockPos var6 = calculateRelativePosition(var3, var4);
      return var5.subtract(var6);
   }

   public static BlockPos calculateRelativePosition(StructurePlaceSettings structurePlaceSettings, BlockPos var1) {
      return transform(var1, structurePlaceSettings.getMirror(), structurePlaceSettings.getRotation(), structurePlaceSettings.getRotationPivot());
   }

   public void placeInWorldChunk(LevelAccessor levelAccessor, BlockPos blockPos, StructurePlaceSettings structurePlaceSettings) {
      structurePlaceSettings.updateBoundingBoxFromChunkPos();
      this.placeInWorld(levelAccessor, blockPos, structurePlaceSettings);
   }

   public void placeInWorld(LevelAccessor levelAccessor, BlockPos blockPos, StructurePlaceSettings structurePlaceSettings) {
      this.placeInWorld(levelAccessor, blockPos, structurePlaceSettings, 2);
   }

   public boolean placeInWorld(LevelAccessor levelAccessor, BlockPos blockPos, StructurePlaceSettings structurePlaceSettings, int var4) {
      if(this.palettes.isEmpty()) {
         return false;
      } else {
         List<StructureTemplate.StructureBlockInfo> var5 = structurePlaceSettings.getPalette(this.palettes, blockPos);
         if((!var5.isEmpty() || !structurePlaceSettings.isIgnoreEntities() && !this.entityInfoList.isEmpty()) && this.size.getX() >= 1 && this.size.getY() >= 1 && this.size.getZ() >= 1) {
            BoundingBox var6 = structurePlaceSettings.getBoundingBox();
            List<BlockPos> var7 = Lists.newArrayListWithCapacity(structurePlaceSettings.shouldKeepLiquids()?var5.size():0);
            List<Pair<BlockPos, CompoundTag>> var8 = Lists.newArrayListWithCapacity(var5.size());
            int var9 = Integer.MAX_VALUE;
            int var10 = Integer.MAX_VALUE;
            int var11 = Integer.MAX_VALUE;
            int var12 = Integer.MIN_VALUE;
            int var13 = Integer.MIN_VALUE;
            int var14 = Integer.MIN_VALUE;

            for(StructureTemplate.StructureBlockInfo var17 : processBlockInfos(levelAccessor, blockPos, structurePlaceSettings, var5)) {
               BlockPos var18 = var17.pos;
               if(var6 == null || var6.isInside(var18)) {
                  FluidState var19 = structurePlaceSettings.shouldKeepLiquids()?levelAccessor.getFluidState(var18):null;
                  BlockState var20 = var17.state.mirror(structurePlaceSettings.getMirror()).rotate(structurePlaceSettings.getRotation());
                  if(var17.nbt != null) {
                     BlockEntity var21 = levelAccessor.getBlockEntity(var18);
                     Clearable.tryClear(var21);
                     levelAccessor.setBlock(var18, Blocks.BARRIER.defaultBlockState(), 20);
                  }

                  if(levelAccessor.setBlock(var18, var20, var4)) {
                     var9 = Math.min(var9, var18.getX());
                     var10 = Math.min(var10, var18.getY());
                     var11 = Math.min(var11, var18.getZ());
                     var12 = Math.max(var12, var18.getX());
                     var13 = Math.max(var13, var18.getY());
                     var14 = Math.max(var14, var18.getZ());
                     var8.add(Pair.of(var18, var17.nbt));
                     if(var17.nbt != null) {
                        BlockEntity var21 = levelAccessor.getBlockEntity(var18);
                        if(var21 != null) {
                           var17.nbt.putInt("x", var18.getX());
                           var17.nbt.putInt("y", var18.getY());
                           var17.nbt.putInt("z", var18.getZ());
                           var21.load(var17.nbt);
                           var21.mirror(structurePlaceSettings.getMirror());
                           var21.rotate(structurePlaceSettings.getRotation());
                        }
                     }

                     if(var19 != null && var20.getBlock() instanceof LiquidBlockContainer) {
                        ((LiquidBlockContainer)var20.getBlock()).placeLiquid(levelAccessor, var18, var20, var19);
                        if(!var19.isSource()) {
                           var7.add(var18);
                        }
                     }
                  }
               }
            }

            boolean var16 = true;
            Direction[] vars17 = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

            while(var16 && !((List)var7).isEmpty()) {
               var16 = false;
               Iterator<BlockPos> var18 = var7.iterator();

               while(var18.hasNext()) {
                  BlockPos var19 = (BlockPos)var18.next();
                  BlockPos var20 = var19;
                  FluidState var21 = levelAccessor.getFluidState(var19);

                  for(int var22 = 0; var22 < vars17.length && !var21.isSource(); ++var22) {
                     BlockPos var23 = var20.relative(vars17[var22]);
                     FluidState var24 = levelAccessor.getFluidState(var23);
                     if(var24.getHeight(levelAccessor, var23) > var21.getHeight(levelAccessor, var20) || var24.isSource() && !var21.isSource()) {
                        var21 = var24;
                        var20 = var23;
                     }
                  }

                  if(var21.isSource()) {
                     BlockState var22 = levelAccessor.getBlockState(var19);
                     Block var23 = var22.getBlock();
                     if(var23 instanceof LiquidBlockContainer) {
                        ((LiquidBlockContainer)var23).placeLiquid(levelAccessor, var19, var22, var21);
                        var16 = true;
                        var18.remove();
                     }
                  }
               }
            }

            if(var9 <= var12) {
               if(!structurePlaceSettings.getKnownShape()) {
                  DiscreteVoxelShape var18 = new BitSetDiscreteVoxelShape(var12 - var9 + 1, var13 - var10 + 1, var14 - var11 + 1);
                  int var19 = var9;
                  int var20 = var10;
                  int var21 = var11;

                  for(Pair<BlockPos, CompoundTag> var23 : var8) {
                     BlockPos var24 = (BlockPos)var23.getFirst();
                     var18.setFull(var24.getX() - var19, var24.getY() - var20, var24.getZ() - var21, true, true);
                  }

                  updateShapeAtEdge(levelAccessor, var4, var18, var19, var20, var21);
               }

               for(Pair<BlockPos, CompoundTag> var19 : var8) {
                  BlockPos var20 = (BlockPos)var19.getFirst();
                  if(!structurePlaceSettings.getKnownShape()) {
                     BlockState var21 = levelAccessor.getBlockState(var20);
                     BlockState var22 = Block.updateFromNeighbourShapes(var21, levelAccessor, var20);
                     if(var21 != var22) {
                        levelAccessor.setBlock(var20, var22, var4 & -2 | 16);
                     }

                     levelAccessor.blockUpdated(var20, var22.getBlock());
                  }

                  if(var19.getSecond() != null) {
                     BlockEntity var21 = levelAccessor.getBlockEntity(var20);
                     if(var21 != null) {
                        var21.setChanged();
                     }
                  }
               }
            }

            if(!structurePlaceSettings.isIgnoreEntities()) {
               this.placeEntities(levelAccessor, blockPos, structurePlaceSettings.getMirror(), structurePlaceSettings.getRotation(), structurePlaceSettings.getRotationPivot(), var6);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public static void updateShapeAtEdge(LevelAccessor levelAccessor, int var1, DiscreteVoxelShape discreteVoxelShape, int var3, int var4, int var5) {
      discreteVoxelShape.forAllFaces((direction, var6, var7, var8) -> {
         BlockPos var9 = new BlockPos(var3 + var6, var4 + var7, var5 + var8);
         BlockPos var10 = var9.relative(direction);
         BlockState var11 = levelAccessor.getBlockState(var9);
         BlockState var12 = levelAccessor.getBlockState(var10);
         BlockState var13 = var11.updateShape(direction, var12, levelAccessor, var9, var10);
         if(var11 != var13) {
            levelAccessor.setBlock(var9, var13, var1 & -2 | 16);
         }

         BlockState var14 = var12.updateShape(direction.getOpposite(), var13, levelAccessor, var10, var9);
         if(var12 != var14) {
            levelAccessor.setBlock(var10, var14, var1 & -2 | 16);
         }

      });
   }

   public static List processBlockInfos(LevelAccessor param0, BlockPos param1, StructurePlaceSettings param2, List param3) {
      // $FF: Couldn't be decompiled
   }

   private void placeEntities(LevelAccessor levelAccessor, BlockPos var2, Mirror mirror, Rotation rotation, BlockPos var5, @Nullable BoundingBox boundingBox) {
      for(StructureTemplate.StructureEntityInfo var8 : this.entityInfoList) {
         BlockPos var9 = transform(var8.blockPos, mirror, rotation, var5).offset(var2);
         if(boundingBox == null || boundingBox.isInside(var9)) {
            CompoundTag var10 = var8.nbt;
            Vec3 var11 = transform(var8.pos, mirror, rotation, var5);
            Vec3 var12 = var11.add((double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
            ListTag var13 = new ListTag();
            var13.add(new DoubleTag(var12.x));
            var13.add(new DoubleTag(var12.y));
            var13.add(new DoubleTag(var12.z));
            var10.put("Pos", var13);
            var10.remove("UUIDMost");
            var10.remove("UUIDLeast");
            createEntityIgnoreException(levelAccessor, var10).ifPresent((entity) -> {
               float var5 = entity.mirror(mirror);
               var5 = var5 + (entity.yRot - entity.rotate(rotation));
               entity.moveTo(var12.x, var12.y, var12.z, var5, entity.xRot);
               levelAccessor.addFreshEntity(entity);
            });
         }
      }

   }

   private static Optional createEntityIgnoreException(LevelAccessor levelAccessor, CompoundTag compoundTag) {
      try {
         return EntityType.create(compoundTag, levelAccessor.getLevel());
      } catch (Exception var3) {
         return Optional.empty();
      }
   }

   public BlockPos getSize(Rotation rotation) {
      switch(rotation) {
      case COUNTERCLOCKWISE_90:
      case CLOCKWISE_90:
         return new BlockPos(this.size.getZ(), this.size.getY(), this.size.getX());
      default:
         return this.size;
      }
   }

   public static BlockPos transform(BlockPos var0, Mirror mirror, Rotation rotation, BlockPos var3) {
      int var4 = var0.getX();
      int var5 = var0.getY();
      int var6 = var0.getZ();
      boolean var7 = true;
      switch(mirror) {
      case LEFT_RIGHT:
         var6 = -var6;
         break;
      case FRONT_BACK:
         var4 = -var4;
         break;
      default:
         var7 = false;
      }

      int var8 = var3.getX();
      int var9 = var3.getZ();
      switch(rotation) {
      case COUNTERCLOCKWISE_90:
         return new BlockPos(var8 - var9 + var6, var5, var8 + var9 - var4);
      case CLOCKWISE_90:
         return new BlockPos(var8 + var9 - var6, var5, var9 - var8 + var4);
      case CLOCKWISE_180:
         return new BlockPos(var8 + var8 - var4, var5, var9 + var9 - var6);
      default:
         return var7?new BlockPos(var4, var5, var6):var0;
      }
   }

   private static Vec3 transform(Vec3 var0, Mirror mirror, Rotation rotation, BlockPos blockPos) {
      double var4 = var0.x;
      double var6 = var0.y;
      double var8 = var0.z;
      boolean var10 = true;
      switch(mirror) {
      case LEFT_RIGHT:
         var8 = 1.0D - var8;
         break;
      case FRONT_BACK:
         var4 = 1.0D - var4;
         break;
      default:
         var10 = false;
      }

      int var11 = blockPos.getX();
      int var12 = blockPos.getZ();
      switch(rotation) {
      case COUNTERCLOCKWISE_90:
         return new Vec3((double)(var11 - var12) + var8, var6, (double)(var11 + var12 + 1) - var4);
      case CLOCKWISE_90:
         return new Vec3((double)(var11 + var12 + 1) - var8, var6, (double)(var12 - var11) + var4);
      case CLOCKWISE_180:
         return new Vec3((double)(var11 + var11 + 1) - var4, var6, (double)(var12 + var12 + 1) - var8);
      default:
         return var10?new Vec3(var4, var6, var8):var0;
      }
   }

   public BlockPos getZeroPositionWithTransform(BlockPos var1, Mirror mirror, Rotation rotation) {
      return getZeroPositionWithTransform(var1, mirror, rotation, this.getSize().getX(), this.getSize().getZ());
   }

   public static BlockPos getZeroPositionWithTransform(BlockPos var0, Mirror mirror, Rotation rotation, int var3, int var4) {
      --var3;
      --var4;
      int var5 = mirror == Mirror.FRONT_BACK?var3:0;
      int var6 = mirror == Mirror.LEFT_RIGHT?var4:0;
      BlockPos var7 = var0;
      switch(rotation) {
      case COUNTERCLOCKWISE_90:
         var7 = var0.offset(var6, 0, var3 - var5);
         break;
      case CLOCKWISE_90:
         var7 = var0.offset(var4 - var6, 0, var5);
         break;
      case CLOCKWISE_180:
         var7 = var0.offset(var3 - var5, 0, var4 - var6);
         break;
      case NONE:
         var7 = var0.offset(var5, 0, var6);
      }

      return var7;
   }

   public BoundingBox getBoundingBox(StructurePlaceSettings structurePlaceSettings, BlockPos blockPos) {
      Rotation var3 = structurePlaceSettings.getRotation();
      BlockPos var4 = structurePlaceSettings.getRotationPivot();
      BlockPos var5 = this.getSize(var3);
      Mirror var6 = structurePlaceSettings.getMirror();
      int var7 = var4.getX();
      int var8 = var4.getZ();
      int var9 = var5.getX() - 1;
      int var10 = var5.getY() - 1;
      int var11 = var5.getZ() - 1;
      BoundingBox var12 = new BoundingBox(0, 0, 0, 0, 0, 0);
      switch(var3) {
      case COUNTERCLOCKWISE_90:
         var12 = new BoundingBox(var7 - var8, 0, var7 + var8 - var11, var7 - var8 + var9, var10, var7 + var8);
         break;
      case CLOCKWISE_90:
         var12 = new BoundingBox(var7 + var8 - var9, 0, var8 - var7, var7 + var8, var10, var8 - var7 + var11);
         break;
      case CLOCKWISE_180:
         var12 = new BoundingBox(var7 + var7 - var9, 0, var8 + var8 - var11, var7 + var7, var10, var8 + var8);
         break;
      case NONE:
         var12 = new BoundingBox(0, 0, 0, var9, var10, var11);
      }

      switch(var6) {
      case LEFT_RIGHT:
         this.mirrorAABB(var3, var11, var9, var12, Direction.NORTH, Direction.SOUTH);
         break;
      case FRONT_BACK:
         this.mirrorAABB(var3, var9, var11, var12, Direction.WEST, Direction.EAST);
      case NONE:
      }

      var12.move(blockPos.getX(), blockPos.getY(), blockPos.getZ());
      return var12;
   }

   private void mirrorAABB(Rotation rotation, int var2, int var3, BoundingBox boundingBox, Direction var5, Direction var6) {
      BlockPos var7 = BlockPos.ZERO;
      if(rotation != Rotation.CLOCKWISE_90 && rotation != Rotation.COUNTERCLOCKWISE_90) {
         if(rotation == Rotation.CLOCKWISE_180) {
            var7 = var7.relative(var6, var2);
         } else {
            var7 = var7.relative(var5, var2);
         }
      } else {
         var7 = var7.relative(rotation.rotate(var5), var3);
      }

      boundingBox.move(var7.getX(), 0, var7.getZ());
   }

   public CompoundTag save(CompoundTag compoundTag) {
      if(this.palettes.isEmpty()) {
         compoundTag.put("blocks", new ListTag());
         compoundTag.put("palette", new ListTag());
      } else {
         List<StructureTemplate.SimplePalette> var2 = Lists.newArrayList();
         StructureTemplate.SimplePalette var3 = new StructureTemplate.SimplePalette();
         var2.add(var3);

         for(int var4 = 1; var4 < this.palettes.size(); ++var4) {
            var2.add(new StructureTemplate.SimplePalette());
         }

         ListTag var4 = new ListTag();
         List<StructureTemplate.StructureBlockInfo> var5 = (List)this.palettes.get(0);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            StructureTemplate.StructureBlockInfo var7 = (StructureTemplate.StructureBlockInfo)var5.get(var6);
            CompoundTag var8 = new CompoundTag();
            var8.put("pos", this.newIntegerList(new int[]{var7.pos.getX(), var7.pos.getY(), var7.pos.getZ()}));
            int var9 = var3.idFor(var7.state);
            var8.putInt("state", var9);
            if(var7.nbt != null) {
               var8.put("nbt", var7.nbt);
            }

            var4.add(var8);

            for(int var10 = 1; var10 < this.palettes.size(); ++var10) {
               StructureTemplate.SimplePalette var11 = (StructureTemplate.SimplePalette)var2.get(var10);
               var11.addMapping(((StructureTemplate.StructureBlockInfo)((List)this.palettes.get(var10)).get(var6)).state, var9);
            }
         }

         compoundTag.put("blocks", var4);
         if(var2.size() == 1) {
            ListTag var6 = new ListTag();

            for(BlockState var8 : var3) {
               var6.add(NbtUtils.writeBlockState(var8));
            }

            compoundTag.put("palette", var6);
         } else {
            ListTag var6 = new ListTag();

            for(StructureTemplate.SimplePalette var8 : var2) {
               ListTag var9 = new ListTag();

               for(BlockState var11 : var8) {
                  var9.add(NbtUtils.writeBlockState(var11));
               }

               var6.add(var9);
            }

            compoundTag.put("palettes", var6);
         }
      }

      ListTag var2 = new ListTag();

      for(StructureTemplate.StructureEntityInfo var4 : this.entityInfoList) {
         CompoundTag var5 = new CompoundTag();
         var5.put("pos", this.newDoubleList(new double[]{var4.pos.x, var4.pos.y, var4.pos.z}));
         var5.put("blockPos", this.newIntegerList(new int[]{var4.blockPos.getX(), var4.blockPos.getY(), var4.blockPos.getZ()}));
         if(var4.nbt != null) {
            var5.put("nbt", var4.nbt);
         }

         var2.add(var5);
      }

      compoundTag.put("entities", var2);
      compoundTag.put("size", this.newIntegerList(new int[]{this.size.getX(), this.size.getY(), this.size.getZ()}));
      compoundTag.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      return compoundTag;
   }

   public void load(CompoundTag compoundTag) {
      this.palettes.clear();
      this.entityInfoList.clear();
      ListTag var2 = compoundTag.getList("size", 3);
      this.size = new BlockPos(var2.getInt(0), var2.getInt(1), var2.getInt(2));
      ListTag var3 = compoundTag.getList("blocks", 10);
      if(compoundTag.contains("palettes", 9)) {
         ListTag var4 = compoundTag.getList("palettes", 9);

         for(int var5 = 0; var5 < var4.size(); ++var5) {
            this.loadPalette(var4.getList(var5), var3);
         }
      } else {
         this.loadPalette(compoundTag.getList("palette", 10), var3);
      }

      ListTag var4 = compoundTag.getList("entities", 10);

      for(int var5 = 0; var5 < var4.size(); ++var5) {
         CompoundTag var6 = var4.getCompound(var5);
         ListTag var7 = var6.getList("pos", 6);
         Vec3 var8 = new Vec3(var7.getDouble(0), var7.getDouble(1), var7.getDouble(2));
         ListTag var9 = var6.getList("blockPos", 3);
         BlockPos var10 = new BlockPos(var9.getInt(0), var9.getInt(1), var9.getInt(2));
         if(var6.contains("nbt")) {
            CompoundTag var11 = var6.getCompound("nbt");
            this.entityInfoList.add(new StructureTemplate.StructureEntityInfo(var8, var10, var11));
         }
      }

   }

   private void loadPalette(ListTag var1, ListTag var2) {
      StructureTemplate.SimplePalette var3 = new StructureTemplate.SimplePalette();
      List<StructureTemplate.StructureBlockInfo> var4 = Lists.newArrayList();

      for(int var5 = 0; var5 < var1.size(); ++var5) {
         var3.addMapping(NbtUtils.readBlockState(var1.getCompound(var5)), var5);
      }

      for(int var5 = 0; var5 < var2.size(); ++var5) {
         CompoundTag var6 = var2.getCompound(var5);
         ListTag var7 = var6.getList("pos", 3);
         BlockPos var8 = new BlockPos(var7.getInt(0), var7.getInt(1), var7.getInt(2));
         BlockState var9 = var3.stateFor(var6.getInt("state"));
         CompoundTag var10;
         if(var6.contains("nbt")) {
            var10 = var6.getCompound("nbt");
         } else {
            var10 = null;
         }

         var4.add(new StructureTemplate.StructureBlockInfo(var8, var9, var10));
      }

      var4.sort(Comparator.comparingInt((structureTemplate$StructureBlockInfo) -> {
         return structureTemplate$StructureBlockInfo.pos.getY();
      }));
      this.palettes.add(var4);
   }

   private ListTag newIntegerList(int... ints) {
      ListTag listTag = new ListTag();

      for(int var6 : ints) {
         listTag.add(new IntTag(var6));
      }

      return listTag;
   }

   private ListTag newDoubleList(double... doubles) {
      ListTag listTag = new ListTag();

      for(double var6 : doubles) {
         listTag.add(new DoubleTag(var6));
      }

      return listTag;
   }

   static class SimplePalette implements Iterable {
      public static final BlockState DEFAULT_BLOCK_STATE = Blocks.AIR.defaultBlockState();
      private final IdMapper ids;
      private int lastId;

      private SimplePalette() {
         this.ids = new IdMapper(16);
      }

      public int idFor(BlockState blockState) {
         int var2 = this.ids.getId(blockState);
         if(var2 == -1) {
            var2 = this.lastId++;
            this.ids.addMapping(blockState, var2);
         }

         return var2;
      }

      @Nullable
      public BlockState stateFor(int i) {
         BlockState blockState = (BlockState)this.ids.byId(i);
         return blockState == null?DEFAULT_BLOCK_STATE:blockState;
      }

      public Iterator iterator() {
         return this.ids.iterator();
      }

      public void addMapping(BlockState blockState, int var2) {
         this.ids.addMapping(blockState, var2);
      }
   }

   public static class StructureBlockInfo {
      public final BlockPos pos;
      public final BlockState state;
      public final CompoundTag nbt;

      public StructureBlockInfo(BlockPos pos, BlockState state, @Nullable CompoundTag nbt) {
         this.pos = pos;
         this.state = state;
         this.nbt = nbt;
      }

      public String toString() {
         return String.format("<StructureBlockInfo | %s | %s | %s>", new Object[]{this.pos, this.state, this.nbt});
      }
   }

   public static class StructureEntityInfo {
      public final Vec3 pos;
      public final BlockPos blockPos;
      public final CompoundTag nbt;

      public StructureEntityInfo(Vec3 pos, BlockPos blockPos, CompoundTag nbt) {
         this.pos = pos;
         this.blockPos = blockPos;
         this.nbt = nbt;
      }
   }
}
