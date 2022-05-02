package net.minecraft.world.entity.vehicle;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartCommandBlock extends AbstractMinecart {
   private static final EntityDataAccessor DATA_ID_COMMAND_NAME = SynchedEntityData.defineId(MinecartCommandBlock.class, EntityDataSerializers.STRING);
   private static final EntityDataAccessor DATA_ID_LAST_OUTPUT = SynchedEntityData.defineId(MinecartCommandBlock.class, EntityDataSerializers.COMPONENT);
   private final BaseCommandBlock commandBlock = new MinecartCommandBlock.MinecartCommandBase();
   private int lastActivated;

   public MinecartCommandBlock(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public MinecartCommandBlock(Level level, double var2, double var4, double var6) {
      super(EntityType.COMMAND_BLOCK_MINECART, level, var2, var4, var6);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_ID_COMMAND_NAME, "");
      this.getEntityData().define(DATA_ID_LAST_OUTPUT, new TextComponent(""));
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.commandBlock.load(compoundTag);
      this.getEntityData().set(DATA_ID_COMMAND_NAME, this.getCommandBlock().getCommand());
      this.getEntityData().set(DATA_ID_LAST_OUTPUT, this.getCommandBlock().getLastOutput());
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      this.commandBlock.save(compoundTag);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.COMMAND_BLOCK;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.COMMAND_BLOCK.defaultBlockState();
   }

   public BaseCommandBlock getCommandBlock() {
      return this.commandBlock;
   }

   public void activateMinecart(int var1, int var2, int var3, boolean var4) {
      if(var4 && this.tickCount - this.lastActivated >= 4) {
         this.getCommandBlock().performCommand(this.level);
         this.lastActivated = this.tickCount;
      }

   }

   public boolean interact(Player player, InteractionHand interactionHand) {
      this.commandBlock.usedBy(player);
      return true;
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      super.onSyncedDataUpdated(entityDataAccessor);
      if(DATA_ID_LAST_OUTPUT.equals(entityDataAccessor)) {
         try {
            this.commandBlock.setLastOutput((Component)this.getEntityData().get(DATA_ID_LAST_OUTPUT));
         } catch (Throwable var3) {
            ;
         }
      } else if(DATA_ID_COMMAND_NAME.equals(entityDataAccessor)) {
         this.commandBlock.setCommand((String)this.getEntityData().get(DATA_ID_COMMAND_NAME));
      }

   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public class MinecartCommandBase extends BaseCommandBlock {
      public ServerLevel getLevel() {
         return (ServerLevel)MinecartCommandBlock.this.level;
      }

      public void onUpdated() {
         MinecartCommandBlock.this.getEntityData().set(MinecartCommandBlock.DATA_ID_COMMAND_NAME, this.getCommand());
         MinecartCommandBlock.this.getEntityData().set(MinecartCommandBlock.DATA_ID_LAST_OUTPUT, this.getLastOutput());
      }

      public Vec3 getPosition() {
         return new Vec3(MinecartCommandBlock.this.x, MinecartCommandBlock.this.y, MinecartCommandBlock.this.z);
      }

      public MinecartCommandBlock getMinecart() {
         return MinecartCommandBlock.this;
      }

      public CommandSourceStack createCommandSourceStack() {
         return new CommandSourceStack(this, new Vec3(MinecartCommandBlock.this.x, MinecartCommandBlock.this.y, MinecartCommandBlock.this.z), MinecartCommandBlock.this.getRotationVector(), this.getLevel(), 2, this.getName().getString(), MinecartCommandBlock.this.getDisplayName(), this.getLevel().getServer(), MinecartCommandBlock.this);
      }
   }
}
