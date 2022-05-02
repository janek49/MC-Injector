package net.minecraft.world.entity.decoration;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemFrame extends HangingEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EntityDataAccessor DATA_ITEM = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.ITEM_STACK);
   private static final EntityDataAccessor DATA_ROTATION = SynchedEntityData.defineId(ItemFrame.class, EntityDataSerializers.INT);
   private float dropChance = 1.0F;

   public ItemFrame(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public ItemFrame(Level level, BlockPos blockPos, Direction direction) {
      super(EntityType.ITEM_FRAME, level, blockPos);
      this.setDirection(direction);
   }

   protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 0.0F;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
      this.getEntityData().define(DATA_ROTATION, Integer.valueOf(0));
   }

   protected void setDirection(Direction direction) {
      Validate.notNull(direction);
      this.direction = direction;
      if(direction.getAxis().isHorizontal()) {
         this.xRot = 0.0F;
         this.yRot = (float)(this.direction.get2DDataValue() * 90);
      } else {
         this.xRot = (float)(-90 * direction.getAxisDirection().getStep());
         this.yRot = 0.0F;
      }

      this.xRotO = this.xRot;
      this.yRotO = this.yRot;
      this.recalculateBoundingBox();
   }

   protected void recalculateBoundingBox() {
      if(this.direction != null) {
         double var1 = 0.46875D;
         this.x = (double)this.pos.getX() + 0.5D - (double)this.direction.getStepX() * 0.46875D;
         this.y = (double)this.pos.getY() + 0.5D - (double)this.direction.getStepY() * 0.46875D;
         this.z = (double)this.pos.getZ() + 0.5D - (double)this.direction.getStepZ() * 0.46875D;
         double var3 = (double)this.getWidth();
         double var5 = (double)this.getHeight();
         double var7 = (double)this.getWidth();
         Direction.Axis var9 = this.direction.getAxis();
         switch(var9) {
         case X:
            var3 = 1.0D;
            break;
         case Y:
            var5 = 1.0D;
            break;
         case Z:
            var7 = 1.0D;
         }

         var3 = var3 / 32.0D;
         var5 = var5 / 32.0D;
         var7 = var7 / 32.0D;
         this.setBoundingBox(new AABB(this.x - var3, this.y - var5, this.z - var7, this.x + var3, this.y + var5, this.z + var7));
      }
   }

   public boolean survives() {
      if(!this.level.noCollision(this)) {
         return false;
      } else {
         BlockState var1 = this.level.getBlockState(this.pos.relative(this.direction.getOpposite()));
         return var1.getMaterial().isSolid() || this.direction.getAxis().isHorizontal() && DiodeBlock.isDiode(var1)?this.level.getEntities((Entity)this, this.getBoundingBox(), HANGING_ENTITY).isEmpty():false;
      }
   }

   public float getPickRadius() {
      return 0.0F;
   }

   public void kill() {
      this.removeFramedMap(this.getItem());
      super.kill();
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else if(!damageSource.isExplosion() && !this.getItem().isEmpty()) {
         if(!this.level.isClientSide) {
            this.dropItem(damageSource.getEntity(), false);
            this.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.hurt(damageSource, var2);
      }
   }

   public int getWidth() {
      return 12;
   }

   public int getHeight() {
      return 12;
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      double var3 = 16.0D;
      var3 = var3 * 64.0D * getViewScale();
      return d < var3 * var3;
   }

   public void dropItem(@Nullable Entity entity) {
      this.playSound(SoundEvents.ITEM_FRAME_BREAK, 1.0F, 1.0F);
      this.dropItem(entity, true);
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.ITEM_FRAME_PLACE, 1.0F, 1.0F);
   }

   private void dropItem(@Nullable Entity entity, boolean var2) {
      if(!this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         if(entity == null) {
            this.removeFramedMap(this.getItem());
         }

      } else {
         ItemStack var3 = this.getItem();
         this.setItem(ItemStack.EMPTY);
         if(entity instanceof Player) {
            Player var4 = (Player)entity;
            if(var4.abilities.instabuild) {
               this.removeFramedMap(var3);
               return;
            }
         }

         if(var2) {
            this.spawnAtLocation(Items.ITEM_FRAME);
         }

         if(!var3.isEmpty()) {
            var3 = var3.copy();
            this.removeFramedMap(var3);
            if(this.random.nextFloat() < this.dropChance) {
               this.spawnAtLocation(var3);
            }
         }

      }
   }

   private void removeFramedMap(ItemStack itemStack) {
      if(itemStack.getItem() == Items.FILLED_MAP) {
         MapItemSavedData var2 = MapItem.getOrCreateSavedData(itemStack, this.level);
         var2.removedFromFrame(this.pos, this.getId());
         var2.setDirty(true);
      }

      itemStack.setFramed((ItemFrame)null);
   }

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM);
   }

   public void setItem(ItemStack item) {
      this.setItem(item, true);
   }

   public void setItem(ItemStack itemStack, boolean var2) {
      if(!itemStack.isEmpty()) {
         itemStack = itemStack.copy();
         itemStack.setCount(1);
         itemStack.setFramed(this);
      }

      this.getEntityData().set(DATA_ITEM, itemStack);
      if(!itemStack.isEmpty()) {
         this.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
      }

      if(var2 && this.pos != null) {
         this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   public boolean setSlot(int var1, ItemStack item) {
      if(var1 == 0) {
         this.setItem(item);
         return true;
      } else {
         return false;
      }
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      if(entityDataAccessor.equals(DATA_ITEM)) {
         ItemStack var2 = this.getItem();
         if(!var2.isEmpty() && var2.getFrame() != this) {
            var2.setFramed(this);
         }
      }

   }

   public int getRotation() {
      return ((Integer)this.getEntityData().get(DATA_ROTATION)).intValue();
   }

   public void setRotation(int rotation) {
      this.setRotation(rotation, true);
   }

   private void setRotation(int var1, boolean var2) {
      this.getEntityData().set(DATA_ROTATION, Integer.valueOf(var1 % 8));
      if(var2 && this.pos != null) {
         this.level.updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
      }

   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      if(!this.getItem().isEmpty()) {
         compoundTag.put("Item", this.getItem().save(new CompoundTag()));
         compoundTag.putByte("ItemRotation", (byte)this.getRotation());
         compoundTag.putFloat("ItemDropChance", this.dropChance);
      }

      compoundTag.putByte("Facing", (byte)this.direction.get3DDataValue());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      CompoundTag compoundTag = compoundTag.getCompound("Item");
      if(compoundTag != null && !compoundTag.isEmpty()) {
         ItemStack var3 = ItemStack.of(compoundTag);
         if(var3.isEmpty()) {
            LOGGER.warn("Unable to load item from: {}", compoundTag);
         }

         ItemStack var4 = this.getItem();
         if(!var4.isEmpty() && !ItemStack.matches(var3, var4)) {
            this.removeFramedMap(var4);
         }

         this.setItem(var3, false);
         this.setRotation(compoundTag.getByte("ItemRotation"), false);
         if(compoundTag.contains("ItemDropChance", 99)) {
            this.dropChance = compoundTag.getFloat("ItemDropChance");
         }
      }

      this.setDirection(Direction.from3DDataValue(compoundTag.getByte("Facing")));
   }

   public boolean interact(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      if(!this.level.isClientSide) {
         if(this.getItem().isEmpty()) {
            if(!var3.isEmpty()) {
               this.setItem(var3);
               if(!player.abilities.instabuild) {
                  var3.shrink(1);
               }
            }
         } else {
            this.playSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
            this.setRotation(this.getRotation() + 1);
         }
      }

      return true;
   }

   public int getAnalogOutput() {
      return this.getItem().isEmpty()?0:this.getRotation() % 8 + 1;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, this.getType(), this.direction.get3DDataValue(), this.getPos());
   }
}
