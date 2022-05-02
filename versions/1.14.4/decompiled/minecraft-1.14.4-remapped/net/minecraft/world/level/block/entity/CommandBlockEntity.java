package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class CommandBlockEntity extends BlockEntity {
   private boolean powered;
   private boolean auto;
   private boolean conditionMet;
   private boolean sendToClient;
   private final BaseCommandBlock commandBlock = new BaseCommandBlock() {
      public void setCommand(String command) {
         super.setCommand(command);
         CommandBlockEntity.this.setChanged();
      }

      public ServerLevel getLevel() {
         return (ServerLevel)CommandBlockEntity.this.level;
      }

      public void onUpdated() {
         BlockState var1 = CommandBlockEntity.this.level.getBlockState(CommandBlockEntity.this.worldPosition);
         this.getLevel().sendBlockUpdated(CommandBlockEntity.this.worldPosition, var1, var1, 3);
      }

      public Vec3 getPosition() {
         return new Vec3((double)CommandBlockEntity.this.worldPosition.getX() + 0.5D, (double)CommandBlockEntity.this.worldPosition.getY() + 0.5D, (double)CommandBlockEntity.this.worldPosition.getZ() + 0.5D);
      }

      public CommandSourceStack createCommandSourceStack() {
         return new CommandSourceStack(this, new Vec3((double)CommandBlockEntity.this.worldPosition.getX() + 0.5D, (double)CommandBlockEntity.this.worldPosition.getY() + 0.5D, (double)CommandBlockEntity.this.worldPosition.getZ() + 0.5D), Vec2.ZERO, this.getLevel(), 2, this.getName().getString(), this.getName(), this.getLevel().getServer(), (Entity)null);
      }
   };

   public CommandBlockEntity() {
      super(BlockEntityType.COMMAND_BLOCK);
   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      this.commandBlock.save(compoundTag);
      compoundTag.putBoolean("powered", this.isPowered());
      compoundTag.putBoolean("conditionMet", this.wasConditionMet());
      compoundTag.putBoolean("auto", this.isAutomatic());
      return compoundTag;
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.commandBlock.load(compoundTag);
      this.powered = compoundTag.getBoolean("powered");
      this.conditionMet = compoundTag.getBoolean("conditionMet");
      this.setAutomatic(compoundTag.getBoolean("auto"));
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      if(this.isSendToClient()) {
         this.setSendToClient(false);
         CompoundTag var1 = this.save(new CompoundTag());
         return new ClientboundBlockEntityDataPacket(this.worldPosition, 2, var1);
      } else {
         return null;
      }
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public BaseCommandBlock getCommandBlock() {
      return this.commandBlock;
   }

   public void setPowered(boolean powered) {
      this.powered = powered;
   }

   public boolean isPowered() {
      return this.powered;
   }

   public boolean isAutomatic() {
      return this.auto;
   }

   public void setAutomatic(boolean automatic) {
      boolean var2 = this.auto;
      this.auto = automatic;
      if(!var2 && automatic && !this.powered && this.level != null && this.getMode() != CommandBlockEntity.Mode.SEQUENCE) {
         Block var3 = this.getBlockState().getBlock();
         if(var3 instanceof CommandBlock) {
            this.markConditionMet();
            this.level.getBlockTicks().scheduleTick(this.worldPosition, var3, var3.getTickDelay(this.level));
         }
      }

   }

   public boolean wasConditionMet() {
      return this.conditionMet;
   }

   public boolean markConditionMet() {
      this.conditionMet = true;
      if(this.isConditional()) {
         BlockPos var1 = this.worldPosition.relative(((Direction)this.level.getBlockState(this.worldPosition).getValue(CommandBlock.FACING)).getOpposite());
         if(this.level.getBlockState(var1).getBlock() instanceof CommandBlock) {
            BlockEntity var2 = this.level.getBlockEntity(var1);
            this.conditionMet = var2 instanceof CommandBlockEntity && ((CommandBlockEntity)var2).getCommandBlock().getSuccessCount() > 0;
         } else {
            this.conditionMet = false;
         }
      }

      return this.conditionMet;
   }

   public boolean isSendToClient() {
      return this.sendToClient;
   }

   public void setSendToClient(boolean sendToClient) {
      this.sendToClient = sendToClient;
   }

   public CommandBlockEntity.Mode getMode() {
      Block var1 = this.getBlockState().getBlock();
      return var1 == Blocks.COMMAND_BLOCK?CommandBlockEntity.Mode.REDSTONE:(var1 == Blocks.REPEATING_COMMAND_BLOCK?CommandBlockEntity.Mode.AUTO:(var1 == Blocks.CHAIN_COMMAND_BLOCK?CommandBlockEntity.Mode.SEQUENCE:CommandBlockEntity.Mode.REDSTONE));
   }

   public boolean isConditional() {
      BlockState var1 = this.level.getBlockState(this.getBlockPos());
      return var1.getBlock() instanceof CommandBlock?((Boolean)var1.getValue(CommandBlock.CONDITIONAL)).booleanValue():false;
   }

   public void clearRemoved() {
      this.clearCache();
      super.clearRemoved();
   }

   public static enum Mode {
      SEQUENCE,
      AUTO,
      REDSTONE;
   }
}
