package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.level.levelgen.feature.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TheEndGatewayBlockEntity extends TheEndPortalBlockEntity implements TickableBlockEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private long age;
   private int teleportCooldown;
   private BlockPos exitPortal;
   private boolean exactTeleport;

   public TheEndGatewayBlockEntity() {
      super(BlockEntityType.END_GATEWAY);
   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      compoundTag.putLong("Age", this.age);
      if(this.exitPortal != null) {
         compoundTag.put("ExitPortal", NbtUtils.writeBlockPos(this.exitPortal));
      }

      if(this.exactTeleport) {
         compoundTag.putBoolean("ExactTeleport", this.exactTeleport);
      }

      return compoundTag;
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.age = compoundTag.getLong("Age");
      if(compoundTag.contains("ExitPortal", 10)) {
         this.exitPortal = NbtUtils.readBlockPos(compoundTag.getCompound("ExitPortal"));
      }

      this.exactTeleport = compoundTag.getBoolean("ExactTeleport");
   }

   public double getViewDistance() {
      return 65536.0D;
   }

   public void tick() {
      boolean var1 = this.isSpawning();
      boolean var2 = this.isCoolingDown();
      ++this.age;
      if(var2) {
         --this.teleportCooldown;
      } else if(!this.level.isClientSide) {
         List<Entity> var3 = this.level.getEntitiesOfClass(Entity.class, new AABB(this.getBlockPos()));
         if(!var3.isEmpty()) {
            this.teleportEntity((Entity)var3.get(0));
         }

         if(this.age % 2400L == 0L) {
            this.triggerCooldown();
         }
      }

      if(var1 != this.isSpawning() || var2 != this.isCoolingDown()) {
         this.setChanged();
      }

   }

   public boolean isSpawning() {
      return this.age < 200L;
   }

   public boolean isCoolingDown() {
      return this.teleportCooldown > 0;
   }

   public float getSpawnPercent(float f) {
      return Mth.clamp(((float)this.age + f) / 200.0F, 0.0F, 1.0F);
   }

   public float getCooldownPercent(float f) {
      return 1.0F - Mth.clamp(((float)this.teleportCooldown - f) / 40.0F, 0.0F, 1.0F);
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 8, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.save(new CompoundTag());
   }

   public void triggerCooldown() {
      if(!this.level.isClientSide) {
         this.teleportCooldown = 40;
         this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 1, 0);
         this.setChanged();
      }

   }

   public boolean triggerEvent(int var1, int var2) {
      if(var1 == 1) {
         this.teleportCooldown = 40;
         return true;
      } else {
         return super.triggerEvent(var1, var2);
      }
   }

   public void teleportEntity(Entity entity) {
      if(!this.level.isClientSide && !this.isCoolingDown()) {
         this.teleportCooldown = 100;
         if(this.exitPortal == null && this.level.dimension instanceof TheEndDimension) {
            this.findExitPortal();
         }

         if(this.exitPortal != null) {
            BlockPos var2 = this.exactTeleport?this.exitPortal:this.findExitPosition();
            entity.teleportToWithTicket((double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D);
         }

         this.triggerCooldown();
      }
   }

   private BlockPos findExitPosition() {
      BlockPos blockPos = findTallestBlock(this.level, this.exitPortal, 5, false);
      LOGGER.debug("Best exit position for portal at {} is {}", this.exitPortal, blockPos);
      return blockPos.above();
   }

   private void findExitPortal() {
      Vec3 var1 = (new Vec3((double)this.getBlockPos().getX(), 0.0D, (double)this.getBlockPos().getZ())).normalize();
      Vec3 var2 = var1.scale(1024.0D);

      for(int var3 = 16; getChunk(this.level, var2).getHighestSectionPosition() > 0 && var3-- > 0; var2 = var2.add(var1.scale(-16.0D))) {
         LOGGER.debug("Skipping backwards past nonempty chunk at {}", var2);
      }

      for(int var5 = 16; getChunk(this.level, var2).getHighestSectionPosition() == 0 && var5-- > 0; var2 = var2.add(var1.scale(16.0D))) {
         LOGGER.debug("Skipping forward past empty chunk at {}", var2);
      }

      LOGGER.debug("Found chunk at {}", var2);
      LevelChunk var4 = getChunk(this.level, var2);
      this.exitPortal = findValidSpawnInChunk(var4);
      if(this.exitPortal == null) {
         this.exitPortal = new BlockPos(var2.x + 0.5D, 75.0D, var2.z + 0.5D);
         LOGGER.debug("Failed to find suitable block, settling on {}", this.exitPortal);
         Feature.END_ISLAND.place(this.level, this.level.getChunkSource().getGenerator(), new Random(this.exitPortal.asLong()), this.exitPortal, FeatureConfiguration.NONE);
      } else {
         LOGGER.debug("Found block at {}", this.exitPortal);
      }

      this.exitPortal = findTallestBlock(this.level, this.exitPortal, 16, true);
      LOGGER.debug("Creating portal at {}", this.exitPortal);
      this.exitPortal = this.exitPortal.above(10);
      this.createExitPortal(this.exitPortal);
      this.setChanged();
   }

   private static BlockPos findTallestBlock(BlockGetter blockGetter, BlockPos var1, int var2, boolean var3) {
      BlockPos var4 = null;

      for(int var5 = -var2; var5 <= var2; ++var5) {
         for(int var6 = -var2; var6 <= var2; ++var6) {
            if(var5 != 0 || var6 != 0 || var3) {
               for(int var7 = 255; var7 > (var4 == null?0:var4.getY()); --var7) {
                  BlockPos var8 = new BlockPos(var1.getX() + var5, var7, var1.getZ() + var6);
                  BlockState var9 = blockGetter.getBlockState(var8);
                  if(var9.isCollisionShapeFullBlock(blockGetter, var8) && (var3 || var9.getBlock() != Blocks.BEDROCK)) {
                     var4 = var8;
                     break;
                  }
               }
            }
         }
      }

      return var4 == null?var1:var4;
   }

   private static LevelChunk getChunk(Level level, Vec3 vec3) {
      return level.getChunk(Mth.floor(vec3.x / 16.0D), Mth.floor(vec3.z / 16.0D));
   }

   @Nullable
   private static BlockPos findValidSpawnInChunk(LevelChunk levelChunk) {
      ChunkPos var1 = levelChunk.getPos();
      BlockPos var2 = new BlockPos(var1.getMinBlockX(), 30, var1.getMinBlockZ());
      int var3 = levelChunk.getHighestSectionPosition() + 16 - 1;
      BlockPos var4 = new BlockPos(var1.getMaxBlockX(), var3, var1.getMaxBlockZ());
      BlockPos var5 = null;
      double var6 = 0.0D;

      for(BlockPos var9 : BlockPos.betweenClosed(var2, var4)) {
         BlockState var10 = levelChunk.getBlockState(var9);
         BlockPos var11 = var9.above();
         BlockPos var12 = var9.above(2);
         if(var10.getBlock() == Blocks.END_STONE && !levelChunk.getBlockState(var11).isCollisionShapeFullBlock(levelChunk, var11) && !levelChunk.getBlockState(var12).isCollisionShapeFullBlock(levelChunk, var12)) {
            double var13 = var9.distSqr(0.0D, 0.0D, 0.0D, true);
            if(var5 == null || var13 < var6) {
               var5 = var9;
               var6 = var13;
            }
         }
      }

      return var5;
   }

   private void createExitPortal(BlockPos blockPos) {
      Feature.END_GATEWAY.place(this.level, this.level.getChunkSource().getGenerator(), new Random(), blockPos, EndGatewayConfiguration.knownExit(this.getBlockPos(), false));
   }

   public boolean shouldRenderFace(Direction direction) {
      return Block.shouldRenderFace(this.getBlockState(), this.level, this.getBlockPos(), direction);
   }

   public int getParticleAmount() {
      int var1 = 0;

      for(Direction var5 : Direction.values()) {
         var1 += this.shouldRenderFace(var5)?1:0;
      }

      return var1;
   }

   public void setExitPosition(BlockPos exitPortal, boolean exactTeleport) {
      this.exactTeleport = exactTeleport;
      this.exitPortal = exitPortal;
   }
}
