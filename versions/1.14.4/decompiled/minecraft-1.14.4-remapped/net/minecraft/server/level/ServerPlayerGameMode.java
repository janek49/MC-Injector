package net.minecraft.server.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayerGameMode {
   private static final Logger LOGGER = LogManager.getLogger();
   public ServerLevel level;
   public ServerPlayer player;
   private GameType gameModeForPlayer = GameType.NOT_SET;
   private boolean isDestroyingBlock;
   private int destroyProgressStart;
   private BlockPos destroyPos = BlockPos.ZERO;
   private int gameTicks;
   private boolean hasDelayedDestroy;
   private BlockPos delayedDestroyPos = BlockPos.ZERO;
   private int delayedTickStart;
   private int lastSentState = -1;

   public ServerPlayerGameMode(ServerLevel level) {
      this.level = level;
   }

   public void setGameModeForPlayer(GameType gameModeForPlayer) {
      this.gameModeForPlayer = gameModeForPlayer;
      gameModeForPlayer.updatePlayerAbilities(this.player.abilities);
      this.player.onUpdateAbilities();
      this.player.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_GAME_MODE, new ServerPlayer[]{this.player}));
      this.level.updateSleepingPlayerList();
   }

   public GameType getGameModeForPlayer() {
      return this.gameModeForPlayer;
   }

   public boolean isSurvival() {
      return this.gameModeForPlayer.isSurvival();
   }

   public boolean isCreative() {
      return this.gameModeForPlayer.isCreative();
   }

   public void updateGameMode(GameType gameModeForPlayer) {
      if(this.gameModeForPlayer == GameType.NOT_SET) {
         this.gameModeForPlayer = gameModeForPlayer;
      }

      this.setGameModeForPlayer(this.gameModeForPlayer);
   }

   public void tick() {
      ++this.gameTicks;
      if(this.hasDelayedDestroy) {
         BlockState var1 = this.level.getBlockState(this.delayedDestroyPos);
         if(var1.isAir()) {
            this.hasDelayedDestroy = false;
         } else {
            float var2 = this.incrementDestroyProgress(var1, this.delayedDestroyPos);
            if(var2 >= 1.0F) {
               this.hasDelayedDestroy = false;
               this.destroyBlock(this.delayedDestroyPos);
            }
         }
      } else if(this.isDestroyingBlock) {
         BlockState var1 = this.level.getBlockState(this.destroyPos);
         if(var1.isAir()) {
            this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
            this.lastSentState = -1;
            this.isDestroyingBlock = false;
         } else {
            this.incrementDestroyProgress(var1, this.destroyPos);
         }
      }

   }

   private float incrementDestroyProgress(BlockState blockState, BlockPos blockPos) {
      int var3 = this.gameTicks - this.delayedTickStart;
      float var4 = blockState.getDestroyProgress(this.player, this.player.level, blockPos) * (float)(var3 + 1);
      int var5 = (int)(var4 * 10.0F);
      if(var5 != this.lastSentState) {
         this.level.destroyBlockProgress(this.player.getId(), blockPos, var5);
         this.lastSentState = var5;
      }

      return var4;
   }

   public void handleBlockBreakAction(BlockPos destroyPos, ServerboundPlayerActionPacket.Action serverboundPlayerActionPacket$Action, Direction direction, int var4) {
      double var5 = this.player.x - ((double)destroyPos.getX() + 0.5D);
      double var7 = this.player.y - ((double)destroyPos.getY() + 0.5D) + 1.5D;
      double var9 = this.player.z - ((double)destroyPos.getZ() + 0.5D);
      double var11 = var5 * var5 + var7 * var7 + var9 * var9;
      if(var11 > 36.0D) {
         this.player.connection.send(new ClientboundBlockBreakAckPacket(destroyPos, this.level.getBlockState(destroyPos), serverboundPlayerActionPacket$Action, false));
      } else if(destroyPos.getY() >= var4) {
         this.player.connection.send(new ClientboundBlockBreakAckPacket(destroyPos, this.level.getBlockState(destroyPos), serverboundPlayerActionPacket$Action, false));
      } else {
         if(serverboundPlayerActionPacket$Action == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            if(!this.level.mayInteract(this.player, destroyPos)) {
               this.player.connection.send(new ClientboundBlockBreakAckPacket(destroyPos, this.level.getBlockState(destroyPos), serverboundPlayerActionPacket$Action, false));
               return;
            }

            if(this.isCreative()) {
               if(!this.level.extinguishFire((Player)null, destroyPos, direction)) {
                  this.destroyAndAck(destroyPos, serverboundPlayerActionPacket$Action);
               } else {
                  this.player.connection.send(new ClientboundBlockBreakAckPacket(destroyPos, this.level.getBlockState(destroyPos), serverboundPlayerActionPacket$Action, true));
               }

               return;
            }

            if(this.player.blockActionRestricted(this.level, destroyPos, this.gameModeForPlayer)) {
               this.player.connection.send(new ClientboundBlockBreakAckPacket(destroyPos, this.level.getBlockState(destroyPos), serverboundPlayerActionPacket$Action, false));
               return;
            }

            this.level.extinguishFire((Player)null, destroyPos, direction);
            this.destroyProgressStart = this.gameTicks;
            float var13 = 1.0F;
            BlockState var14 = this.level.getBlockState(destroyPos);
            if(!var14.isAir()) {
               var14.attack(this.level, destroyPos, this.player);
               var13 = var14.getDestroyProgress(this.player, this.player.level, destroyPos);
            }

            if(!var14.isAir() && var13 >= 1.0F) {
               this.destroyAndAck(destroyPos, serverboundPlayerActionPacket$Action);
            } else {
               this.isDestroyingBlock = true;
               this.destroyPos = destroyPos;
               int var15 = (int)(var13 * 10.0F);
               this.level.destroyBlockProgress(this.player.getId(), destroyPos, var15);
               this.player.connection.send(new ClientboundBlockBreakAckPacket(destroyPos, this.level.getBlockState(destroyPos), serverboundPlayerActionPacket$Action, true));
               this.lastSentState = var15;
            }
         } else if(serverboundPlayerActionPacket$Action == ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK) {
            if(destroyPos.equals(this.destroyPos)) {
               int var13 = this.gameTicks - this.destroyProgressStart;
               BlockState var14 = this.level.getBlockState(destroyPos);
               if(!var14.isAir()) {
                  float var15 = var14.getDestroyProgress(this.player, this.player.level, destroyPos) * (float)(var13 + 1);
                  if(var15 >= 0.7F) {
                     this.isDestroyingBlock = false;
                     this.level.destroyBlockProgress(this.player.getId(), destroyPos, -1);
                     this.destroyAndAck(destroyPos, serverboundPlayerActionPacket$Action);
                     return;
                  }

                  if(!this.hasDelayedDestroy) {
                     this.isDestroyingBlock = false;
                     this.hasDelayedDestroy = true;
                     this.delayedDestroyPos = destroyPos;
                     this.delayedTickStart = this.destroyProgressStart;
                  }
               }
            }

            this.player.connection.send(new ClientboundBlockBreakAckPacket(destroyPos, this.level.getBlockState(destroyPos), serverboundPlayerActionPacket$Action, true));
         } else if(serverboundPlayerActionPacket$Action == ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK) {
            this.isDestroyingBlock = false;
            this.level.destroyBlockProgress(this.player.getId(), this.destroyPos, -1);
            this.player.connection.send(new ClientboundBlockBreakAckPacket(destroyPos, this.level.getBlockState(destroyPos), serverboundPlayerActionPacket$Action, true));
         }

      }
   }

   public void destroyAndAck(BlockPos blockPos, ServerboundPlayerActionPacket.Action serverboundPlayerActionPacket$Action) {
      if(this.destroyBlock(blockPos)) {
         this.player.connection.send(new ClientboundBlockBreakAckPacket(blockPos, this.level.getBlockState(blockPos), serverboundPlayerActionPacket$Action, true));
      } else {
         this.player.connection.send(new ClientboundBlockBreakAckPacket(blockPos, this.level.getBlockState(blockPos), serverboundPlayerActionPacket$Action, false));
      }

   }

   public boolean destroyBlock(BlockPos blockPos) {
      BlockState var2 = this.level.getBlockState(blockPos);
      if(!this.player.getMainHandItem().getItem().canAttackBlock(var2, this.level, blockPos, this.player)) {
         return false;
      } else {
         BlockEntity var3 = this.level.getBlockEntity(blockPos);
         Block var4 = var2.getBlock();
         if((var4 instanceof CommandBlock || var4 instanceof StructureBlock || var4 instanceof JigsawBlock) && !this.player.canUseGameMasterBlocks()) {
            this.level.sendBlockUpdated(blockPos, var2, var2, 3);
            return false;
         } else if(this.player.blockActionRestricted(this.level, blockPos, this.gameModeForPlayer)) {
            return false;
         } else {
            var4.playerWillDestroy(this.level, blockPos, var2, this.player);
            boolean var5 = this.level.removeBlock(blockPos, false);
            if(var5) {
               var4.destroy(this.level, blockPos, var2);
            }

            if(this.isCreative()) {
               return true;
            } else {
               ItemStack var6 = this.player.getMainHandItem();
               boolean var7 = this.player.canDestroy(var2);
               var6.mineBlock(this.level, var2, blockPos, this.player);
               if(var5 && var7) {
                  ItemStack var8 = var6.isEmpty()?ItemStack.EMPTY:var6.copy();
                  var4.playerDestroy(this.level, this.player, blockPos, var2, var3, var8);
               }

               return true;
            }
         }
      }
   }

   public InteractionResult useItem(Player player, Level level, ItemStack itemStack, InteractionHand interactionHand) {
      if(this.gameModeForPlayer == GameType.SPECTATOR) {
         return InteractionResult.PASS;
      } else if(player.getCooldowns().isOnCooldown(itemStack.getItem())) {
         return InteractionResult.PASS;
      } else {
         int var5 = itemStack.getCount();
         int var6 = itemStack.getDamageValue();
         InteractionResultHolder<ItemStack> var7 = itemStack.use(level, player, interactionHand);
         ItemStack var8 = (ItemStack)var7.getObject();
         if(var8 == itemStack && var8.getCount() == var5 && var8.getUseDuration() <= 0 && var8.getDamageValue() == var6) {
            return var7.getResult();
         } else if(var7.getResult() == InteractionResult.FAIL && var8.getUseDuration() > 0 && !player.isUsingItem()) {
            return var7.getResult();
         } else {
            player.setItemInHand(interactionHand, var8);
            if(this.isCreative()) {
               var8.setCount(var5);
               if(var8.isDamageableItem()) {
                  var8.setDamageValue(var6);
               }
            }

            if(var8.isEmpty()) {
               player.setItemInHand(interactionHand, ItemStack.EMPTY);
            }

            if(!player.isUsingItem()) {
               ((ServerPlayer)player).refreshContainer(player.inventoryMenu);
            }

            return var7.getResult();
         }
      }
   }

   public InteractionResult useItemOn(Player player, Level level, ItemStack itemStack, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      BlockPos var6 = blockHitResult.getBlockPos();
      BlockState var7 = level.getBlockState(var6);
      if(this.gameModeForPlayer == GameType.SPECTATOR) {
         MenuProvider var8 = var7.getMenuProvider(level, var6);
         if(var8 != null) {
            player.openMenu(var8);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.PASS;
         }
      } else {
         boolean var8 = !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty();
         boolean var9 = player.isSneaking() && var8;
         if(!var9 && var7.use(level, player, interactionHand, blockHitResult)) {
            return InteractionResult.SUCCESS;
         } else if(!itemStack.isEmpty() && !player.getCooldowns().isOnCooldown(itemStack.getItem())) {
            UseOnContext var10 = new UseOnContext(player, interactionHand, blockHitResult);
            if(this.isCreative()) {
               int var11 = itemStack.getCount();
               InteractionResult var12 = itemStack.useOn(var10);
               itemStack.setCount(var11);
               return var12;
            } else {
               return itemStack.useOn(var10);
            }
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public void setLevel(ServerLevel level) {
      this.level = level;
   }
}
