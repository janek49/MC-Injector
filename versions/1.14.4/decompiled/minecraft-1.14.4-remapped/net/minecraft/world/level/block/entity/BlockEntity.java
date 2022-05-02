package net.minecraft.world.level.block.entity;

import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private final BlockEntityType type;
   @Nullable
   protected Level level;
   protected BlockPos worldPosition = BlockPos.ZERO;
   protected boolean remove;
   @Nullable
   private BlockState blockState;
   private boolean hasLoggedInvalidStateBefore;

   public BlockEntity(BlockEntityType type) {
      this.type = type;
   }

   @Nullable
   public Level getLevel() {
      return this.level;
   }

   public void setLevel(Level level) {
      this.level = level;
   }

   public boolean hasLevel() {
      return this.level != null;
   }

   public void load(CompoundTag compoundTag) {
      this.worldPosition = new BlockPos(compoundTag.getInt("x"), compoundTag.getInt("y"), compoundTag.getInt("z"));
   }

   public CompoundTag save(CompoundTag compoundTag) {
      return this.saveMetadata(compoundTag);
   }

   private CompoundTag saveMetadata(CompoundTag compoundTag) {
      ResourceLocation var2 = BlockEntityType.getKey(this.getType());
      if(var2 == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         compoundTag.putString("id", var2.toString());
         compoundTag.putInt("x", this.worldPosition.getX());
         compoundTag.putInt("y", this.worldPosition.getY());
         compoundTag.putInt("z", this.worldPosition.getZ());
         return compoundTag;
      }
   }

   @Nullable
   public static BlockEntity loadStatic(CompoundTag compoundTag) {
      String var1 = compoundTag.getString("id");
      return (BlockEntity)Registry.BLOCK_ENTITY_TYPE.getOptional(new ResourceLocation(var1)).map((blockEntityType) -> {
         try {
            return blockEntityType.create();
         } catch (Throwable var3) {
            LOGGER.error("Failed to create block entity {}", var1, var3);
            return null;
         }
      }).map((var2) -> {
         try {
            var2.load(compoundTag);
            return var2;
         } catch (Throwable var4) {
            LOGGER.error("Failed to load data for block entity {}", var1, var4);
            return null;
         }
      }).orElseGet(() -> {
         LOGGER.warn("Skipping BlockEntity with id {}", var1);
         return null;
      });
   }

   public void setChanged() {
      if(this.level != null) {
         this.blockState = this.level.getBlockState(this.worldPosition);
         this.level.blockEntityChanged(this.worldPosition, this);
         if(!this.blockState.isAir()) {
            this.level.updateNeighbourForOutputSignal(this.worldPosition, this.blockState.getBlock());
         }
      }

   }

   public double distanceToSqr(double var1, double var3, double var5) {
      double var7 = (double)this.worldPosition.getX() + 0.5D - var1;
      double var9 = (double)this.worldPosition.getY() + 0.5D - var3;
      double var11 = (double)this.worldPosition.getZ() + 0.5D - var5;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public double getViewDistance() {
      return 4096.0D;
   }

   public BlockPos getBlockPos() {
      return this.worldPosition;
   }

   public BlockState getBlockState() {
      if(this.blockState == null) {
         this.blockState = this.level.getBlockState(this.worldPosition);
      }

      return this.blockState;
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return null;
   }

   public CompoundTag getUpdateTag() {
      return this.saveMetadata(new CompoundTag());
   }

   public boolean isRemoved() {
      return this.remove;
   }

   public void setRemoved() {
      this.remove = true;
   }

   public void clearRemoved() {
      this.remove = false;
   }

   public boolean triggerEvent(int var1, int var2) {
      return false;
   }

   public void clearCache() {
      this.blockState = null;
   }

   public void fillCrashReportCategory(CrashReportCategory crashReportCategory) {
      crashReportCategory.setDetail("Name", () -> {
         return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName();
      });
      if(this.level != null) {
         CrashReportCategory.populateBlockDetails(crashReportCategory, this.worldPosition, this.getBlockState());
         CrashReportCategory.populateBlockDetails(crashReportCategory, this.worldPosition, this.level.getBlockState(this.worldPosition));
      }
   }

   public void setPosition(BlockPos position) {
      this.worldPosition = position.immutable();
   }

   public boolean onlyOpCanSetNbt() {
      return false;
   }

   public void rotate(Rotation rotation) {
   }

   public void mirror(Mirror mirror) {
   }

   public BlockEntityType getType() {
      return this.type;
   }

   public void logInvalidState() {
      if(!this.hasLoggedInvalidStateBefore) {
         this.hasLoggedInvalidStateBefore = true;
         LOGGER.warn("Block entity invalid: {} @ {}", new org.apache.logging.log4j.util.Supplier[]{() -> {
            return Registry.BLOCK_ENTITY_TYPE.getKey(this.getType());
         }, this::getBlockPos});
      }
   }
}
