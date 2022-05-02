package net.minecraft.world.entity.vehicle;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractMinecart extends Entity {
   private static final EntityDataAccessor DATA_ID_HURT = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor DATA_ID_HURTDIR = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor DATA_ID_DAMAGE = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor DATA_ID_DISPLAY_BLOCK = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor DATA_ID_DISPLAY_OFFSET = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor DATA_ID_CUSTOM_DISPLAY = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.BOOLEAN);
   private boolean flipped;
   private static final int[][][] EXITS = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
   private int lSteps;
   private double lx;
   private double ly;
   private double lz;
   private double lyr;
   private double lxr;
   private double lxd;
   private double lyd;
   private double lzd;

   protected AbstractMinecart(EntityType entityType, Level level) {
      super(entityType, level);
      this.blocksBuilding = true;
   }

   protected AbstractMinecart(EntityType entityType, Level level, double xo, double yo, double zo) {
      this(entityType, level);
      this.setPos(xo, yo, zo);
      this.setDeltaMovement(Vec3.ZERO);
      this.xo = xo;
      this.yo = yo;
      this.zo = zo;
   }

   public static AbstractMinecart createMinecart(Level level, double var1, double var3, double var5, AbstractMinecart.Type abstractMinecart$Type) {
      return (AbstractMinecart)(abstractMinecart$Type == AbstractMinecart.Type.CHEST?new MinecartChest(level, var1, var3, var5):(abstractMinecart$Type == AbstractMinecart.Type.FURNACE?new MinecartFurnace(level, var1, var3, var5):(abstractMinecart$Type == AbstractMinecart.Type.TNT?new MinecartTNT(level, var1, var3, var5):(abstractMinecart$Type == AbstractMinecart.Type.SPAWNER?new MinecartSpawner(level, var1, var3, var5):(abstractMinecart$Type == AbstractMinecart.Type.HOPPER?new MinecartHopper(level, var1, var3, var5):(abstractMinecart$Type == AbstractMinecart.Type.COMMAND_BLOCK?new MinecartCommandBlock(level, var1, var3, var5):new Minecart(level, var1, var3, var5)))))));
   }

   protected boolean makeStepSound() {
      return false;
   }

   protected void defineSynchedData() {
      this.entityData.define(DATA_ID_HURT, Integer.valueOf(0));
      this.entityData.define(DATA_ID_HURTDIR, Integer.valueOf(1));
      this.entityData.define(DATA_ID_DAMAGE, Float.valueOf(0.0F));
      this.entityData.define(DATA_ID_DISPLAY_BLOCK, Integer.valueOf(Block.getId(Blocks.AIR.defaultBlockState())));
      this.entityData.define(DATA_ID_DISPLAY_OFFSET, Integer.valueOf(6));
      this.entityData.define(DATA_ID_CUSTOM_DISPLAY, Boolean.valueOf(false));
   }

   @Nullable
   public AABB getCollideAgainstBox(Entity entity) {
      return entity.isPushable()?entity.getBoundingBox():null;
   }

   public boolean isPushable() {
      return true;
   }

   public double getRideHeight() {
      return 0.0D;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(!this.level.isClientSide && !this.removed) {
         if(this.isInvulnerableTo(damageSource)) {
            return false;
         } else {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.markHurt();
            this.setDamage(this.getDamage() + var2 * 10.0F);
            boolean var3 = damageSource.getEntity() instanceof Player && ((Player)damageSource.getEntity()).abilities.instabuild;
            if(var3 || this.getDamage() > 40.0F) {
               this.ejectPassengers();
               if(var3 && !this.hasCustomName()) {
                  this.remove();
               } else {
                  this.destroy(damageSource);
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public void destroy(DamageSource damageSource) {
      this.remove();
      if(this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         ItemStack var2 = new ItemStack(Items.MINECART);
         if(this.hasCustomName()) {
            var2.setHoverName(this.getCustomName());
         }

         this.spawnAtLocation(var2);
      }

   }

   public void animateHurt() {
      this.setHurtDir(-this.getHurtDir());
      this.setHurtTime(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   public boolean isPickable() {
      return !this.removed;
   }

   public Direction getMotionDirection() {
      return this.flipped?this.getDirection().getOpposite().getClockWise():this.getDirection().getClockWise();
   }

   public void tick() {
      if(this.getHurtTime() > 0) {
         this.setHurtTime(this.getHurtTime() - 1);
      }

      if(this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if(this.y < -64.0D) {
         this.outOfWorld();
      }

      this.handleNetherPortal();
      if(this.level.isClientSide) {
         if(this.lSteps > 0) {
            double var1 = this.x + (this.lx - this.x) / (double)this.lSteps;
            double var3 = this.y + (this.ly - this.y) / (double)this.lSteps;
            double var5 = this.z + (this.lz - this.z) / (double)this.lSteps;
            double var7 = Mth.wrapDegrees(this.lyr - (double)this.yRot);
            this.yRot = (float)((double)this.yRot + var7 / (double)this.lSteps);
            this.xRot = (float)((double)this.xRot + (this.lxr - (double)this.xRot) / (double)this.lSteps);
            --this.lSteps;
            this.setPos(var1, var3, var5);
            this.setRot(this.yRot, this.xRot);
         } else {
            this.setPos(this.x, this.y, this.z);
            this.setRot(this.yRot, this.xRot);
         }

      } else {
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         if(!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
         }

         int var1 = Mth.floor(this.x);
         int var2 = Mth.floor(this.y);
         int var3 = Mth.floor(this.z);
         if(this.level.getBlockState(new BlockPos(var1, var2 - 1, var3)).is(BlockTags.RAILS)) {
            --var2;
         }

         BlockPos var4 = new BlockPos(var1, var2, var3);
         BlockState var5 = this.level.getBlockState(var4);
         if(var5.is(BlockTags.RAILS)) {
            this.moveAlongTrack(var4, var5);
            if(var5.getBlock() == Blocks.ACTIVATOR_RAIL) {
               this.activateMinecart(var1, var2, var3, ((Boolean)var5.getValue(PoweredRailBlock.POWERED)).booleanValue());
            }
         } else {
            this.comeOffTrack();
         }

         this.checkInsideBlocks();
         this.xRot = 0.0F;
         double var6 = this.xo - this.x;
         double var8 = this.zo - this.z;
         if(var6 * var6 + var8 * var8 > 0.001D) {
            this.yRot = (float)(Mth.atan2(var8, var6) * 180.0D / 3.141592653589793D);
            if(this.flipped) {
               this.yRot += 180.0F;
            }
         }

         double var10 = (double)Mth.wrapDegrees(this.yRot - this.yRotO);
         if(var10 < -170.0D || var10 >= 170.0D) {
            this.yRot += 180.0F;
            this.flipped = !this.flipped;
         }

         this.setRot(this.yRot, this.xRot);
         if(this.getMinecartType() == AbstractMinecart.Type.RIDEABLE && getHorizontalDistanceSqr(this.getDeltaMovement()) > 0.01D) {
            List<Entity> var12 = this.level.getEntities((Entity)this, this.getBoundingBox().inflate(0.20000000298023224D, 0.0D, 0.20000000298023224D), EntitySelector.pushableBy(this));
            if(!var12.isEmpty()) {
               for(int var13 = 0; var13 < var12.size(); ++var13) {
                  Entity var14 = (Entity)var12.get(var13);
                  if(!(var14 instanceof Player) && !(var14 instanceof IronGolem) && !(var14 instanceof AbstractMinecart) && !this.isVehicle() && !var14.isPassenger()) {
                     var14.startRiding(this);
                  } else {
                     var14.push(this);
                  }
               }
            }
         } else {
            for(Entity var13 : this.level.getEntities(this, this.getBoundingBox().inflate(0.20000000298023224D, 0.0D, 0.20000000298023224D))) {
               if(!this.hasPassenger(var13) && var13.isPushable() && var13 instanceof AbstractMinecart) {
                  var13.push(this);
               }
            }
         }

         this.updateInWaterState();
      }
   }

   protected double getMaxSpeed() {
      return 0.4D;
   }

   public void activateMinecart(int var1, int var2, int var3, boolean var4) {
   }

   protected void comeOffTrack() {
      double var1 = this.getMaxSpeed();
      Vec3 var3 = this.getDeltaMovement();
      this.setDeltaMovement(Mth.clamp(var3.x, -var1, var1), var3.y, Mth.clamp(var3.z, -var1, var1));
      if(this.onGround) {
         this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
      }

      this.move(MoverType.SELF, this.getDeltaMovement());
      if(!this.onGround) {
         this.setDeltaMovement(this.getDeltaMovement().scale(0.95D));
      }

   }

   protected void moveAlongTrack(BlockPos blockPos, BlockState blockState) {
      this.fallDistance = 0.0F;
      Vec3 var3 = this.getPos(this.x, this.y, this.z);
      this.y = (double)blockPos.getY();
      boolean var4 = false;
      boolean var5 = false;
      BaseRailBlock var6 = (BaseRailBlock)blockState.getBlock();
      if(var6 == Blocks.POWERED_RAIL) {
         var4 = ((Boolean)blockState.getValue(PoweredRailBlock.POWERED)).booleanValue();
         var5 = !var4;
      }

      double var7 = 0.0078125D;
      Vec3 var9 = this.getDeltaMovement();
      RailShape var10 = (RailShape)blockState.getValue(var6.getShapeProperty());
      switch(var10) {
      case ASCENDING_EAST:
         this.setDeltaMovement(var9.add(-0.0078125D, 0.0D, 0.0D));
         ++this.y;
         break;
      case ASCENDING_WEST:
         this.setDeltaMovement(var9.add(0.0078125D, 0.0D, 0.0D));
         ++this.y;
         break;
      case ASCENDING_NORTH:
         this.setDeltaMovement(var9.add(0.0D, 0.0D, 0.0078125D));
         ++this.y;
         break;
      case ASCENDING_SOUTH:
         this.setDeltaMovement(var9.add(0.0D, 0.0D, -0.0078125D));
         ++this.y;
      }

      var9 = this.getDeltaMovement();
      int[][] vars11 = EXITS[var10.getData()];
      double var12 = (double)(vars11[1][0] - vars11[0][0]);
      double var14 = (double)(vars11[1][2] - vars11[0][2]);
      double var16 = Math.sqrt(var12 * var12 + var14 * var14);
      double var18 = var9.x * var12 + var9.z * var14;
      if(var18 < 0.0D) {
         var12 = -var12;
         var14 = -var14;
      }

      double var20 = Math.min(2.0D, Math.sqrt(getHorizontalDistanceSqr(var9)));
      var9 = new Vec3(var20 * var12 / var16, var9.y, var20 * var14 / var16);
      this.setDeltaMovement(var9);
      Entity var22 = this.getPassengers().isEmpty()?null:(Entity)this.getPassengers().get(0);
      if(var22 instanceof Player) {
         Vec3 var23 = var22.getDeltaMovement();
         double var24 = getHorizontalDistanceSqr(var23);
         double var26 = getHorizontalDistanceSqr(this.getDeltaMovement());
         if(var24 > 1.0E-4D && var26 < 0.01D) {
            this.setDeltaMovement(this.getDeltaMovement().add(var23.x * 0.1D, 0.0D, var23.z * 0.1D));
            var5 = false;
         }
      }

      if(var5) {
         double var23 = Math.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement()));
         if(var23 < 0.03D) {
            this.setDeltaMovement(Vec3.ZERO);
         } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 0.0D, 0.5D));
         }
      }

      double var23 = (double)blockPos.getX() + 0.5D + (double)vars11[0][0] * 0.5D;
      double var25 = (double)blockPos.getZ() + 0.5D + (double)vars11[0][2] * 0.5D;
      double var27 = (double)blockPos.getX() + 0.5D + (double)vars11[1][0] * 0.5D;
      double var29 = (double)blockPos.getZ() + 0.5D + (double)vars11[1][2] * 0.5D;
      var12 = var27 - var23;
      var14 = var29 - var25;
      double var31;
      if(var12 == 0.0D) {
         this.x = (double)blockPos.getX() + 0.5D;
         var31 = this.z - (double)blockPos.getZ();
      } else if(var14 == 0.0D) {
         this.z = (double)blockPos.getZ() + 0.5D;
         var31 = this.x - (double)blockPos.getX();
      } else {
         double var33 = this.x - var23;
         double var35 = this.z - var25;
         var31 = (var33 * var12 + var35 * var14) * 2.0D;
      }

      this.x = var23 + var12 * var31;
      this.z = var25 + var14 * var31;
      this.setPos(this.x, this.y, this.z);
      double var33 = this.isVehicle()?0.75D:1.0D;
      double var35 = this.getMaxSpeed();
      var9 = this.getDeltaMovement();
      this.move(MoverType.SELF, new Vec3(Mth.clamp(var33 * var9.x, -var35, var35), 0.0D, Mth.clamp(var33 * var9.z, -var35, var35)));
      if(vars11[0][1] != 0 && Mth.floor(this.x) - blockPos.getX() == vars11[0][0] && Mth.floor(this.z) - blockPos.getZ() == vars11[0][2]) {
         this.setPos(this.x, this.y + (double)vars11[0][1], this.z);
      } else if(vars11[1][1] != 0 && Mth.floor(this.x) - blockPos.getX() == vars11[1][0] && Mth.floor(this.z) - blockPos.getZ() == vars11[1][2]) {
         this.setPos(this.x, this.y + (double)vars11[1][1], this.z);
      }

      this.applyNaturalSlowdown();
      Vec3 var37 = this.getPos(this.x, this.y, this.z);
      if(var37 != null && var3 != null) {
         double var38 = (var3.y - var37.y) * 0.05D;
         Vec3 var40 = this.getDeltaMovement();
         double var41 = Math.sqrt(getHorizontalDistanceSqr(var40));
         if(var41 > 0.0D) {
            this.setDeltaMovement(var40.multiply((var41 + var38) / var41, 1.0D, (var41 + var38) / var41));
         }

         this.setPos(this.x, var37.y, this.z);
      }

      int var38 = Mth.floor(this.x);
      int var39 = Mth.floor(this.z);
      if(var38 != blockPos.getX() || var39 != blockPos.getZ()) {
         Vec3 var40 = this.getDeltaMovement();
         double var41 = Math.sqrt(getHorizontalDistanceSqr(var40));
         this.setDeltaMovement(var41 * (double)(var38 - blockPos.getX()), var40.y, var41 * (double)(var39 - blockPos.getZ()));
      }

      if(var4) {
         Vec3 var40 = this.getDeltaMovement();
         double var41 = Math.sqrt(getHorizontalDistanceSqr(var40));
         if(var41 > 0.01D) {
            double var43 = 0.06D;
            this.setDeltaMovement(var40.add(var40.x / var41 * 0.06D, 0.0D, var40.z / var41 * 0.06D));
         } else {
            Vec3 var43 = this.getDeltaMovement();
            double var44 = var43.x;
            double var46 = var43.z;
            if(var10 == RailShape.EAST_WEST) {
               if(this.isRedstoneConductor(blockPos.west())) {
                  var44 = 0.02D;
               } else if(this.isRedstoneConductor(blockPos.east())) {
                  var44 = -0.02D;
               }
            } else {
               if(var10 != RailShape.NORTH_SOUTH) {
                  return;
               }

               if(this.isRedstoneConductor(blockPos.north())) {
                  var46 = 0.02D;
               } else if(this.isRedstoneConductor(blockPos.south())) {
                  var46 = -0.02D;
               }
            }

            this.setDeltaMovement(var44, var43.y, var46);
         }
      }

   }

   private boolean isRedstoneConductor(BlockPos blockPos) {
      return this.level.getBlockState(blockPos).isRedstoneConductor(this.level, blockPos);
   }

   protected void applyNaturalSlowdown() {
      double var1 = this.isVehicle()?0.997D:0.96D;
      this.setDeltaMovement(this.getDeltaMovement().multiply(var1, 0.0D, var1));
   }

   @Nullable
   public Vec3 getPosOffs(double var1, double var3, double var5, double var7) {
      int var9 = Mth.floor(var1);
      int var10 = Mth.floor(var3);
      int var11 = Mth.floor(var5);
      if(this.level.getBlockState(new BlockPos(var9, var10 - 1, var11)).is(BlockTags.RAILS)) {
         --var10;
      }

      BlockState var12 = this.level.getBlockState(new BlockPos(var9, var10, var11));
      if(var12.is(BlockTags.RAILS)) {
         RailShape var13 = (RailShape)var12.getValue(((BaseRailBlock)var12.getBlock()).getShapeProperty());
         var3 = (double)var10;
         if(var13.isAscending()) {
            var3 = (double)(var10 + 1);
         }

         int[][] vars14 = EXITS[var13.getData()];
         double var15 = (double)(vars14[1][0] - vars14[0][0]);
         double var17 = (double)(vars14[1][2] - vars14[0][2]);
         double var19 = Math.sqrt(var15 * var15 + var17 * var17);
         var15 = var15 / var19;
         var17 = var17 / var19;
         var1 = var1 + var15 * var7;
         var5 = var5 + var17 * var7;
         if(vars14[0][1] != 0 && Mth.floor(var1) - var9 == vars14[0][0] && Mth.floor(var5) - var11 == vars14[0][2]) {
            var3 += (double)vars14[0][1];
         } else if(vars14[1][1] != 0 && Mth.floor(var1) - var9 == vars14[1][0] && Mth.floor(var5) - var11 == vars14[1][2]) {
            var3 += (double)vars14[1][1];
         }

         return this.getPos(var1, var3, var5);
      } else {
         return null;
      }
   }

   @Nullable
   public Vec3 getPos(double var1, double var3, double var5) {
      int var7 = Mth.floor(var1);
      int var8 = Mth.floor(var3);
      int var9 = Mth.floor(var5);
      if(this.level.getBlockState(new BlockPos(var7, var8 - 1, var9)).is(BlockTags.RAILS)) {
         --var8;
      }

      BlockState var10 = this.level.getBlockState(new BlockPos(var7, var8, var9));
      if(var10.is(BlockTags.RAILS)) {
         RailShape var11 = (RailShape)var10.getValue(((BaseRailBlock)var10.getBlock()).getShapeProperty());
         int[][] vars12 = EXITS[var11.getData()];
         double var13 = (double)var7 + 0.5D + (double)vars12[0][0] * 0.5D;
         double var15 = (double)var8 + 0.0625D + (double)vars12[0][1] * 0.5D;
         double var17 = (double)var9 + 0.5D + (double)vars12[0][2] * 0.5D;
         double var19 = (double)var7 + 0.5D + (double)vars12[1][0] * 0.5D;
         double var21 = (double)var8 + 0.0625D + (double)vars12[1][1] * 0.5D;
         double var23 = (double)var9 + 0.5D + (double)vars12[1][2] * 0.5D;
         double var25 = var19 - var13;
         double var27 = (var21 - var15) * 2.0D;
         double var29 = var23 - var17;
         double var31;
         if(var25 == 0.0D) {
            var31 = var5 - (double)var9;
         } else if(var29 == 0.0D) {
            var31 = var1 - (double)var7;
         } else {
            double var33 = var1 - var13;
            double var35 = var5 - var17;
            var31 = (var33 * var25 + var35 * var29) * 2.0D;
         }

         var1 = var13 + var25 * var31;
         var3 = var15 + var27 * var31;
         var5 = var17 + var29 * var31;
         if(var27 < 0.0D) {
            ++var3;
         }

         if(var27 > 0.0D) {
            var3 += 0.5D;
         }

         return new Vec3(var1, var3, var5);
      } else {
         return null;
      }
   }

   public AABB getBoundingBoxForCulling() {
      AABB aABB = this.getBoundingBox();
      return this.hasCustomDisplay()?aABB.inflate((double)Math.abs(this.getDisplayOffset()) / 16.0D):aABB;
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      if(compoundTag.getBoolean("CustomDisplayTile")) {
         this.setDisplayBlockState(NbtUtils.readBlockState(compoundTag.getCompound("DisplayState")));
         this.setDisplayOffset(compoundTag.getInt("DisplayOffset"));
      }

   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      if(this.hasCustomDisplay()) {
         compoundTag.putBoolean("CustomDisplayTile", true);
         compoundTag.put("DisplayState", NbtUtils.writeBlockState(this.getDisplayBlockState()));
         compoundTag.putInt("DisplayOffset", this.getDisplayOffset());
      }

   }

   public void push(Entity entity) {
      if(!this.level.isClientSide) {
         if(!entity.noPhysics && !this.noPhysics) {
            if(!this.hasPassenger(entity)) {
               double var2 = entity.x - this.x;
               double var4 = entity.z - this.z;
               double var6 = var2 * var2 + var4 * var4;
               if(var6 >= 9.999999747378752E-5D) {
                  var6 = (double)Mth.sqrt(var6);
                  var2 = var2 / var6;
                  var4 = var4 / var6;
                  double var8 = 1.0D / var6;
                  if(var8 > 1.0D) {
                     var8 = 1.0D;
                  }

                  var2 = var2 * var8;
                  var4 = var4 * var8;
                  var2 = var2 * 0.10000000149011612D;
                  var4 = var4 * 0.10000000149011612D;
                  var2 = var2 * (double)(1.0F - this.pushthrough);
                  var4 = var4 * (double)(1.0F - this.pushthrough);
                  var2 = var2 * 0.5D;
                  var4 = var4 * 0.5D;
                  if(entity instanceof AbstractMinecart) {
                     double var10 = entity.x - this.x;
                     double var12 = entity.z - this.z;
                     Vec3 var14 = (new Vec3(var10, 0.0D, var12)).normalize();
                     Vec3 var15 = (new Vec3((double)Mth.cos(this.yRot * 0.017453292F), 0.0D, (double)Mth.sin(this.yRot * 0.017453292F))).normalize();
                     double var16 = Math.abs(var14.dot(var15));
                     if(var16 < 0.800000011920929D) {
                        return;
                     }

                     Vec3 var18 = this.getDeltaMovement();
                     Vec3 var19 = entity.getDeltaMovement();
                     if(((AbstractMinecart)entity).getMinecartType() == AbstractMinecart.Type.FURNACE && this.getMinecartType() != AbstractMinecart.Type.FURNACE) {
                        this.setDeltaMovement(var18.multiply(0.2D, 1.0D, 0.2D));
                        this.push(var19.x - var2, 0.0D, var19.z - var4);
                        entity.setDeltaMovement(var19.multiply(0.95D, 1.0D, 0.95D));
                     } else if(((AbstractMinecart)entity).getMinecartType() != AbstractMinecart.Type.FURNACE && this.getMinecartType() == AbstractMinecart.Type.FURNACE) {
                        entity.setDeltaMovement(var19.multiply(0.2D, 1.0D, 0.2D));
                        entity.push(var18.x + var2, 0.0D, var18.z + var4);
                        this.setDeltaMovement(var18.multiply(0.95D, 1.0D, 0.95D));
                     } else {
                        double var20 = (var19.x + var18.x) / 2.0D;
                        double var22 = (var19.z + var18.z) / 2.0D;
                        this.setDeltaMovement(var18.multiply(0.2D, 1.0D, 0.2D));
                        this.push(var20 - var2, 0.0D, var22 - var4);
                        entity.setDeltaMovement(var19.multiply(0.2D, 1.0D, 0.2D));
                        entity.push(var20 + var2, 0.0D, var22 + var4);
                     }
                  } else {
                     this.push(-var2, 0.0D, -var4);
                     entity.push(var2 / 4.0D, 0.0D, var4 / 4.0D);
                  }
               }

            }
         }
      }
   }

   public void lerpTo(double lx, double ly, double lz, float var7, float var8, int var9, boolean var10) {
      this.lx = lx;
      this.ly = ly;
      this.lz = lz;
      this.lyr = (double)var7;
      this.lxr = (double)var8;
      this.lSteps = var9 + 2;
      this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
   }

   public void lerpMotion(double lxd, double lyd, double lzd) {
      this.lxd = lxd;
      this.lyd = lyd;
      this.lzd = lzd;
      this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
   }

   public void setDamage(float damage) {
      this.entityData.set(DATA_ID_DAMAGE, Float.valueOf(damage));
   }

   public float getDamage() {
      return ((Float)this.entityData.get(DATA_ID_DAMAGE)).floatValue();
   }

   public void setHurtTime(int hurtTime) {
      this.entityData.set(DATA_ID_HURT, Integer.valueOf(hurtTime));
   }

   public int getHurtTime() {
      return ((Integer)this.entityData.get(DATA_ID_HURT)).intValue();
   }

   public void setHurtDir(int hurtDir) {
      this.entityData.set(DATA_ID_HURTDIR, Integer.valueOf(hurtDir));
   }

   public int getHurtDir() {
      return ((Integer)this.entityData.get(DATA_ID_HURTDIR)).intValue();
   }

   public abstract AbstractMinecart.Type getMinecartType();

   public BlockState getDisplayBlockState() {
      return !this.hasCustomDisplay()?this.getDefaultDisplayBlockState():Block.stateById(((Integer)this.getEntityData().get(DATA_ID_DISPLAY_BLOCK)).intValue());
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.AIR.defaultBlockState();
   }

   public int getDisplayOffset() {
      return !this.hasCustomDisplay()?this.getDefaultDisplayOffset():((Integer)this.getEntityData().get(DATA_ID_DISPLAY_OFFSET)).intValue();
   }

   public int getDefaultDisplayOffset() {
      return 6;
   }

   public void setDisplayBlockState(BlockState displayBlockState) {
      this.getEntityData().set(DATA_ID_DISPLAY_BLOCK, Integer.valueOf(Block.getId(displayBlockState)));
      this.setCustomDisplay(true);
   }

   public void setDisplayOffset(int displayOffset) {
      this.getEntityData().set(DATA_ID_DISPLAY_OFFSET, Integer.valueOf(displayOffset));
      this.setCustomDisplay(true);
   }

   public boolean hasCustomDisplay() {
      return ((Boolean)this.getEntityData().get(DATA_ID_CUSTOM_DISPLAY)).booleanValue();
   }

   public void setCustomDisplay(boolean customDisplay) {
      this.getEntityData().set(DATA_ID_CUSTOM_DISPLAY, Boolean.valueOf(customDisplay));
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   public static enum Type {
      RIDEABLE,
      CHEST,
      FURNACE,
      TNT,
      SPAWNER,
      HOPPER,
      COMMAND_BLOCK;
   }
}
