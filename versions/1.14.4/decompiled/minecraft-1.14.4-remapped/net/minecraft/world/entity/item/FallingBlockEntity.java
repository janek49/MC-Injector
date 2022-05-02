package net.minecraft.world.entity.item;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.DirectionalPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FallingBlockEntity extends Entity {
   private BlockState blockState;
   public int time;
   public boolean dropItem;
   private boolean cancelDrop;
   private boolean hurtEntities;
   private int fallDamageMax;
   private float fallDamageAmount;
   public CompoundTag blockData;
   protected static final EntityDataAccessor DATA_START_POS = SynchedEntityData.defineId(FallingBlockEntity.class, EntityDataSerializers.BLOCK_POS);

   public FallingBlockEntity(EntityType entityType, Level level) {
      super(entityType, level);
      this.blockState = Blocks.SAND.defaultBlockState();
      this.dropItem = true;
      this.fallDamageMax = 40;
      this.fallDamageAmount = 2.0F;
   }

   public FallingBlockEntity(Level level, double xo, double yo, double zo, BlockState blockState) {
      this(EntityType.FALLING_BLOCK, level);
      this.blockState = blockState;
      this.blocksBuilding = true;
      this.setPos(xo, yo + (double)((1.0F - this.getBbHeight()) / 2.0F), zo);
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = xo;
      this.yo = yo;
      this.zo = zo;
      this.setStartPos(new BlockPos(this));
   }

   public boolean isAttackable() {
      return false;
   }

   public void setStartPos(BlockPos startPos) {
      this.entityData.set(DATA_START_POS, startPos);
   }

   public BlockPos getStartPos() {
      return (BlockPos)this.entityData.get(DATA_START_POS);
   }

   protected boolean makeStepSound() {
      return false;
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_START_POS, BlockPos.ZERO);
   }

   public boolean isPickable() {
      return !this.removed;
   }

   public void tick() {
      if(this.blockState.isAir()) {
         this.remove();
      } else {
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         Block var1 = this.blockState.getBlock();
         if(this.time++ == 0) {
            BlockPos var2 = new BlockPos(this);
            if(this.level.getBlockState(var2).getBlock() == var1) {
               this.level.removeBlock(var2, false);
            } else if(!this.level.isClientSide) {
               this.remove();
               return;
            }
         }

         if(!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
         }

         this.move(MoverType.SELF, this.getDeltaMovement());
         if(!this.level.isClientSide) {
            BlockPos var2 = new BlockPos(this);
            boolean var3 = this.blockState.getBlock() instanceof ConcretePowderBlock;
            boolean var4 = var3 && this.level.getFluidState(var2).is(FluidTags.WATER);
            double var5 = this.getDeltaMovement().lengthSqr();
            if(var3 && var5 > 1.0D) {
               BlockHitResult var7 = this.level.clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), new Vec3(this.x, this.y, this.z), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
               if(var7.getType() != HitResult.Type.MISS && this.level.getFluidState(var7.getBlockPos()).is(FluidTags.WATER)) {
                  var2 = var7.getBlockPos();
                  var4 = true;
               }
            }

            if(!this.onGround && !var4) {
               if(!this.level.isClientSide && (this.time > 100 && (var2.getY() < 1 || var2.getY() > 256) || this.time > 600)) {
                  if(this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                     this.spawnAtLocation(var1);
                  }

                  this.remove();
               }
            } else {
               BlockState var7 = this.level.getBlockState(var2);
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
               if(var7.getBlock() != Blocks.MOVING_PISTON) {
                  this.remove();
                  if(!this.cancelDrop) {
                     boolean var8 = var7.canBeReplaced(new DirectionalPlaceContext(this.level, var2, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                     boolean var9 = this.blockState.canSurvive(this.level, var2);
                     if(var8 && var9) {
                        if(this.blockState.hasProperty(BlockStateProperties.WATERLOGGED) && this.level.getFluidState(var2).getType() == Fluids.WATER) {
                           this.blockState = (BlockState)this.blockState.setValue(BlockStateProperties.WATERLOGGED, Boolean.valueOf(true));
                        }

                        if(this.level.setBlock(var2, this.blockState, 3)) {
                           if(var1 instanceof FallingBlock) {
                              ((FallingBlock)var1).onLand(this.level, var2, this.blockState, var7);
                           }

                           if(this.blockData != null && var1 instanceof EntityBlock) {
                              BlockEntity var10 = this.level.getBlockEntity(var2);
                              if(var10 != null) {
                                 CompoundTag var11 = var10.save(new CompoundTag());

                                 for(String var13 : this.blockData.getAllKeys()) {
                                    Tag var14 = this.blockData.get(var13);
                                    if(!"x".equals(var13) && !"y".equals(var13) && !"z".equals(var13)) {
                                       var11.put(var13, var14.copy());
                                    }
                                 }

                                 var10.load(var11);
                                 var10.setChanged();
                              }
                           }
                        } else if(this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                           this.spawnAtLocation(var1);
                        }
                     } else if(this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                        this.spawnAtLocation(var1);
                     }
                  } else if(var1 instanceof FallingBlock) {
                     ((FallingBlock)var1).onBroken(this.level, var2);
                  }
               }
            }
         }

         this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
      }
   }

   public void causeFallDamage(float var1, float var2) {
      if(this.hurtEntities) {
         int var3 = Mth.ceil(var1 - 1.0F);
         if(var3 > 0) {
            List<Entity> var4 = Lists.newArrayList(this.level.getEntities(this, this.getBoundingBox()));
            boolean var5 = this.blockState.is(BlockTags.ANVIL);
            DamageSource var6 = var5?DamageSource.ANVIL:DamageSource.FALLING_BLOCK;

            for(Entity var8 : var4) {
               var8.hurt(var6, (float)Math.min(Mth.floor((float)var3 * this.fallDamageAmount), this.fallDamageMax));
            }

            if(var5 && (double)this.random.nextFloat() < 0.05000000074505806D + (double)var3 * 0.05D) {
               BlockState var7 = AnvilBlock.damage(this.blockState);
               if(var7 == null) {
                  this.cancelDrop = true;
               } else {
                  this.blockState = var7;
               }
            }
         }
      }

   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.put("BlockState", NbtUtils.writeBlockState(this.blockState));
      compoundTag.putInt("Time", this.time);
      compoundTag.putBoolean("DropItem", this.dropItem);
      compoundTag.putBoolean("HurtEntities", this.hurtEntities);
      compoundTag.putFloat("FallHurtAmount", this.fallDamageAmount);
      compoundTag.putInt("FallHurtMax", this.fallDamageMax);
      if(this.blockData != null) {
         compoundTag.put("TileEntityData", this.blockData);
      }

   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      this.blockState = NbtUtils.readBlockState(compoundTag.getCompound("BlockState"));
      this.time = compoundTag.getInt("Time");
      if(compoundTag.contains("HurtEntities", 99)) {
         this.hurtEntities = compoundTag.getBoolean("HurtEntities");
         this.fallDamageAmount = compoundTag.getFloat("FallHurtAmount");
         this.fallDamageMax = compoundTag.getInt("FallHurtMax");
      } else if(this.blockState.is(BlockTags.ANVIL)) {
         this.hurtEntities = true;
      }

      if(compoundTag.contains("DropItem", 99)) {
         this.dropItem = compoundTag.getBoolean("DropItem");
      }

      if(compoundTag.contains("TileEntityData", 10)) {
         this.blockData = compoundTag.getCompound("TileEntityData");
      }

      if(this.blockState.isAir()) {
         this.blockState = Blocks.SAND.defaultBlockState();
      }

   }

   public Level getLevel() {
      return this.level;
   }

   public void setHurtsEntities(boolean hurtsEntities) {
      this.hurtEntities = hurtsEntities;
   }

   public boolean displayFireAnimation() {
      return false;
   }

   public void fillCrashReportCategory(CrashReportCategory crashReportCategory) {
      super.fillCrashReportCategory(crashReportCategory);
      crashReportCategory.setDetail("Immitating BlockState", (Object)this.blockState.toString());
   }

   public BlockState getBlockState() {
      return this.blockState;
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, Block.getId(this.getBlockState()));
   }
}
