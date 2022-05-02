package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class StructureBlockEntity extends BlockEntity {
   private ResourceLocation structureName;
   private String author = "";
   private String metaData = "";
   private BlockPos structurePos = new BlockPos(0, 1, 0);
   private BlockPos structureSize = BlockPos.ZERO;
   private Mirror mirror = Mirror.NONE;
   private Rotation rotation = Rotation.NONE;
   private StructureMode mode = StructureMode.DATA;
   private boolean ignoreEntities = true;
   private boolean powered;
   private boolean showAir;
   private boolean showBoundingBox = true;
   private float integrity = 1.0F;
   private long seed;

   public StructureBlockEntity() {
      super(BlockEntityType.STRUCTURE_BLOCK);
   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      compoundTag.putString("name", this.getStructureName());
      compoundTag.putString("author", this.author);
      compoundTag.putString("metadata", this.metaData);
      compoundTag.putInt("posX", this.structurePos.getX());
      compoundTag.putInt("posY", this.structurePos.getY());
      compoundTag.putInt("posZ", this.structurePos.getZ());
      compoundTag.putInt("sizeX", this.structureSize.getX());
      compoundTag.putInt("sizeY", this.structureSize.getY());
      compoundTag.putInt("sizeZ", this.structureSize.getZ());
      compoundTag.putString("rotation", this.rotation.toString());
      compoundTag.putString("mirror", this.mirror.toString());
      compoundTag.putString("mode", this.mode.toString());
      compoundTag.putBoolean("ignoreEntities", this.ignoreEntities);
      compoundTag.putBoolean("powered", this.powered);
      compoundTag.putBoolean("showair", this.showAir);
      compoundTag.putBoolean("showboundingbox", this.showBoundingBox);
      compoundTag.putFloat("integrity", this.integrity);
      compoundTag.putLong("seed", this.seed);
      return compoundTag;
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.setStructureName(compoundTag.getString("name"));
      this.author = compoundTag.getString("author");
      this.metaData = compoundTag.getString("metadata");
      int var2 = Mth.clamp(compoundTag.getInt("posX"), -32, 32);
      int var3 = Mth.clamp(compoundTag.getInt("posY"), -32, 32);
      int var4 = Mth.clamp(compoundTag.getInt("posZ"), -32, 32);
      this.structurePos = new BlockPos(var2, var3, var4);
      int var5 = Mth.clamp(compoundTag.getInt("sizeX"), 0, 32);
      int var6 = Mth.clamp(compoundTag.getInt("sizeY"), 0, 32);
      int var7 = Mth.clamp(compoundTag.getInt("sizeZ"), 0, 32);
      this.structureSize = new BlockPos(var5, var6, var7);

      try {
         this.rotation = Rotation.valueOf(compoundTag.getString("rotation"));
      } catch (IllegalArgumentException var11) {
         this.rotation = Rotation.NONE;
      }

      try {
         this.mirror = Mirror.valueOf(compoundTag.getString("mirror"));
      } catch (IllegalArgumentException var10) {
         this.mirror = Mirror.NONE;
      }

      try {
         this.mode = StructureMode.valueOf(compoundTag.getString("mode"));
      } catch (IllegalArgumentException var9) {
         this.mode = StructureMode.DATA;
      }

      this.ignoreEntities = compoundTag.getBoolean("ignoreEntities");
      this.powered = compoundTag.getBoolean("powered");
      this.showAir = compoundTag.getBoolean("showair");
      this.showBoundingBox = compoundTag.getBoolean("showboundingbox");
      if(compoundTag.contains("integrity")) {
         this.integrity = compoundTag.getFloat("integrity");
      } else {
         this.integrity = 1.0F;
      }

      this.seed = compoundTag.getLong("seed");
      this.updateBlockState();
   }

   private void updateBlockState() {
      if(this.level != null) {
         BlockPos var1 = this.getBlockPos();
         BlockState var2 = this.level.getBlockState(var1);
         if(var2.getBlock() == Blocks.STRUCTURE_BLOCK) {
            this.level.setBlock(var1, (BlockState)var2.setValue(StructureBlock.MODE, this.mode), 2);
         }

      }
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 7, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public boolean usedBy(Player player) {
      if(!player.canUseGameMasterBlocks()) {
         return false;
      } else {
         if(player.getCommandSenderWorld().isClientSide) {
            player.openStructureBlock(this);
         }

         return true;
      }
   }

   public String getStructureName() {
      return this.structureName == null?"":this.structureName.toString();
   }

   public boolean hasStructureName() {
      return this.structureName != null;
   }

   public void setStructureName(@Nullable String structureName) {
      this.setStructureName(StringUtil.isNullOrEmpty(structureName)?null:ResourceLocation.tryParse(structureName));
   }

   public void setStructureName(@Nullable ResourceLocation structureName) {
      this.structureName = structureName;
   }

   public void createdBy(LivingEntity livingEntity) {
      this.author = livingEntity.getName().getString();
   }

   public BlockPos getStructurePos() {
      return this.structurePos;
   }

   public void setStructurePos(BlockPos structurePos) {
      this.structurePos = structurePos;
   }

   public BlockPos getStructureSize() {
      return this.structureSize;
   }

   public void setStructureSize(BlockPos structureSize) {
      this.structureSize = structureSize;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public void setMirror(Mirror mirror) {
      this.mirror = mirror;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public void setRotation(Rotation rotation) {
      this.rotation = rotation;
   }

   public String getMetaData() {
      return this.metaData;
   }

   public void setMetaData(String metaData) {
      this.metaData = metaData;
   }

   public StructureMode getMode() {
      return this.mode;
   }

   public void setMode(StructureMode mode) {
      this.mode = mode;
      BlockState var2 = this.level.getBlockState(this.getBlockPos());
      if(var2.getBlock() == Blocks.STRUCTURE_BLOCK) {
         this.level.setBlock(this.getBlockPos(), (BlockState)var2.setValue(StructureBlock.MODE, mode), 2);
      }

   }

   public void nextMode() {
      switch(this.getMode()) {
      case SAVE:
         this.setMode(StructureMode.LOAD);
         break;
      case LOAD:
         this.setMode(StructureMode.CORNER);
         break;
      case CORNER:
         this.setMode(StructureMode.DATA);
         break;
      case DATA:
         this.setMode(StructureMode.SAVE);
      }

   }

   public boolean isIgnoreEntities() {
      return this.ignoreEntities;
   }

   public void setIgnoreEntities(boolean ignoreEntities) {
      this.ignoreEntities = ignoreEntities;
   }

   public float getIntegrity() {
      return this.integrity;
   }

   public void setIntegrity(float integrity) {
      this.integrity = integrity;
   }

   public long getSeed() {
      return this.seed;
   }

   public void setSeed(long seed) {
      this.seed = seed;
   }

   public boolean detectSize() {
      if(this.mode != StructureMode.SAVE) {
         return false;
      } else {
         BlockPos var1 = this.getBlockPos();
         int var2 = 80;
         BlockPos var3 = new BlockPos(var1.getX() - 80, 0, var1.getZ() - 80);
         BlockPos var4 = new BlockPos(var1.getX() + 80, 255, var1.getZ() + 80);
         List<StructureBlockEntity> var5 = this.getNearbyCornerBlocks(var3, var4);
         List<StructureBlockEntity> var6 = this.filterRelatedCornerBlocks(var5);
         if(var6.size() < 1) {
            return false;
         } else {
            BoundingBox var7 = this.calculateEnclosingBoundingBox(var1, var6);
            if(var7.x1 - var7.x0 > 1 && var7.y1 - var7.y0 > 1 && var7.z1 - var7.z0 > 1) {
               this.structurePos = new BlockPos(var7.x0 - var1.getX() + 1, var7.y0 - var1.getY() + 1, var7.z0 - var1.getZ() + 1);
               this.structureSize = new BlockPos(var7.x1 - var7.x0 - 1, var7.y1 - var7.y0 - 1, var7.z1 - var7.z0 - 1);
               this.setChanged();
               BlockState var8 = this.level.getBlockState(var1);
               this.level.sendBlockUpdated(var1, var8, var8, 3);
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private List filterRelatedCornerBlocks(List list) {
      Predicate<StructureBlockEntity> var2 = (structureBlockEntity) -> {
         return structureBlockEntity.mode == StructureMode.CORNER && Objects.equals(this.structureName, structureBlockEntity.structureName);
      };
      return (List)list.stream().filter(var2).collect(Collectors.toList());
   }

   private List getNearbyCornerBlocks(BlockPos var1, BlockPos var2) {
      List<StructureBlockEntity> list = Lists.newArrayList();

      for(BlockPos var5 : BlockPos.betweenClosed(var1, var2)) {
         BlockState var6 = this.level.getBlockState(var5);
         if(var6.getBlock() == Blocks.STRUCTURE_BLOCK) {
            BlockEntity var7 = this.level.getBlockEntity(var5);
            if(var7 != null && var7 instanceof StructureBlockEntity) {
               list.add((StructureBlockEntity)var7);
            }
         }
      }

      return list;
   }

   private BoundingBox calculateEnclosingBoundingBox(BlockPos blockPos, List list) {
      BoundingBox boundingBox;
      if(list.size() > 1) {
         BlockPos var4 = ((StructureBlockEntity)list.get(0)).getBlockPos();
         boundingBox = new BoundingBox(var4, var4);
      } else {
         boundingBox = new BoundingBox(blockPos, blockPos);
      }

      for(StructureBlockEntity var5 : list) {
         BlockPos var6 = var5.getBlockPos();
         if(var6.getX() < boundingBox.x0) {
            boundingBox.x0 = var6.getX();
         } else if(var6.getX() > boundingBox.x1) {
            boundingBox.x1 = var6.getX();
         }

         if(var6.getY() < boundingBox.y0) {
            boundingBox.y0 = var6.getY();
         } else if(var6.getY() > boundingBox.y1) {
            boundingBox.y1 = var6.getY();
         }

         if(var6.getZ() < boundingBox.z0) {
            boundingBox.z0 = var6.getZ();
         } else if(var6.getZ() > boundingBox.z1) {
            boundingBox.z1 = var6.getZ();
         }
      }

      return boundingBox;
   }

   public boolean saveStructure() {
      return this.saveStructure(true);
   }

   public boolean saveStructure(boolean b) {
      if(this.mode == StructureMode.SAVE && !this.level.isClientSide && this.structureName != null) {
         BlockPos var2 = this.getBlockPos().offset(this.structurePos);
         ServerLevel var3 = (ServerLevel)this.level;
         StructureManager var4 = var3.getStructureManager();

         StructureTemplate var5;
         try {
            var5 = var4.getOrCreate(this.structureName);
         } catch (ResourceLocationException var8) {
            return false;
         }

         var5.fillFromWorld(this.level, var2, this.structureSize, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
         var5.setAuthor(this.author);
         if(b) {
            try {
               return var4.save(this.structureName);
            } catch (ResourceLocationException var7) {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean loadStructure() {
      return this.loadStructure(true);
   }

   private static Random createRandom(long l) {
      return l == 0L?new Random(Util.getMillis()):new Random(l);
   }

   public boolean loadStructure(boolean b) {
      if(this.mode == StructureMode.LOAD && !this.level.isClientSide && this.structureName != null) {
         BlockPos var2 = this.getBlockPos();
         BlockPos var3 = var2.offset(this.structurePos);
         ServerLevel var4 = (ServerLevel)this.level;
         StructureManager var5 = var4.getStructureManager();

         StructureTemplate var6;
         try {
            var6 = var5.get(this.structureName);
         } catch (ResourceLocationException var10) {
            return false;
         }

         if(var6 == null) {
            return false;
         } else {
            if(!StringUtil.isNullOrEmpty(var6.getAuthor())) {
               this.author = var6.getAuthor();
            }

            BlockPos var7 = var6.getSize();
            boolean var8 = this.structureSize.equals(var7);
            if(!var8) {
               this.structureSize = var7;
               this.setChanged();
               BlockState var9 = this.level.getBlockState(var2);
               this.level.sendBlockUpdated(var2, var9, var9, 3);
            }

            if(b && !var8) {
               return false;
            } else {
               StructurePlaceSettings var9 = (new StructurePlaceSettings()).setMirror(this.mirror).setRotation(this.rotation).setIgnoreEntities(this.ignoreEntities).setChunkPos((ChunkPos)null);
               if(this.integrity < 1.0F) {
                  var9.clearProcessors().addProcessor(new BlockRotProcessor(Mth.clamp(this.integrity, 0.0F, 1.0F))).setRandom(createRandom(this.seed));
               }

               var6.placeInWorldChunk(this.level, var3, var9);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public void unloadStructure() {
      if(this.structureName != null) {
         ServerLevel var1 = (ServerLevel)this.level;
         StructureManager var2 = var1.getStructureManager();
         var2.remove(this.structureName);
      }
   }

   public boolean isStructureLoadable() {
      if(this.mode == StructureMode.LOAD && !this.level.isClientSide && this.structureName != null) {
         ServerLevel var1 = (ServerLevel)this.level;
         StructureManager var2 = var1.getStructureManager();

         try {
            return var2.get(this.structureName) != null;
         } catch (ResourceLocationException var4) {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isPowered() {
      return this.powered;
   }

   public void setPowered(boolean powered) {
      this.powered = powered;
   }

   public boolean getShowAir() {
      return this.showAir;
   }

   public void setShowAir(boolean showAir) {
      this.showAir = showAir;
   }

   public boolean getShowBoundingBox() {
      return this.showBoundingBox;
   }

   public void setShowBoundingBox(boolean showBoundingBox) {
      this.showBoundingBox = showBoundingBox;
   }

   public static enum UpdateType {
      UPDATE_DATA,
      SAVE_AREA,
      LOAD_AREA,
      SCAN_AREA;
   }
}
