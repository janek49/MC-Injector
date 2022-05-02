package net.minecraft.client.multiplayer;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.PosAndRot;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class MultiPlayerGameMode {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final ClientPacketListener connection;
   private BlockPos destroyBlockPos = new BlockPos(-1, -1, -1);
   private ItemStack destroyingItem = ItemStack.EMPTY;
   private float destroyProgress;
   private float destroyTicks;
   private int destroyDelay;
   private boolean isDestroying;
   private GameType localPlayerMode = GameType.SURVIVAL;
   private final Object2ObjectLinkedOpenHashMap unAckedActions = new Object2ObjectLinkedOpenHashMap();
   private int carriedIndex;

   public MultiPlayerGameMode(Minecraft minecraft, ClientPacketListener connection) {
      this.minecraft = minecraft;
      this.connection = connection;
   }

   public static void creativeDestroyBlock(Minecraft minecraft, MultiPlayerGameMode multiPlayerGameMode, BlockPos blockPos, Direction direction) {
      if(!minecraft.level.extinguishFire(minecraft.player, blockPos, direction)) {
         multiPlayerGameMode.destroyBlock(blockPos);
      }

   }

   public void adjustPlayer(Player player) {
      this.localPlayerMode.updatePlayerAbilities(player.abilities);
   }

   public void setLocalMode(GameType localMode) {
      this.localPlayerMode = localMode;
      this.localPlayerMode.updatePlayerAbilities(this.minecraft.player.abilities);
   }

   public boolean canHurtPlayer() {
      return this.localPlayerMode.isSurvival();
   }

   public boolean destroyBlock(BlockPos blockPos) {
      if(this.minecraft.player.blockActionRestricted(this.minecraft.level, blockPos, this.localPlayerMode)) {
         return false;
      } else {
         Level var2 = this.minecraft.level;
         BlockState var3 = var2.getBlockState(blockPos);
         if(!this.minecraft.player.getMainHandItem().getItem().canAttackBlock(var3, var2, blockPos, this.minecraft.player)) {
            return false;
         } else {
            Block var4 = var3.getBlock();
            if((var4 instanceof CommandBlock || var4 instanceof StructureBlock || var4 instanceof JigsawBlock) && !this.minecraft.player.canUseGameMasterBlocks()) {
               return false;
            } else if(var3.isAir()) {
               return false;
            } else {
               var4.playerWillDestroy(var2, blockPos, var3, this.minecraft.player);
               FluidState var5 = var2.getFluidState(blockPos);
               boolean var6 = var2.setBlock(blockPos, var5.createLegacyBlock(), 11);
               if(var6) {
                  var4.destroy(var2, blockPos, var3);
               }

               return var6;
            }
         }
      }
   }

   public boolean startDestroyBlock(BlockPos destroyBlockPos, Direction direction) {
      if(this.minecraft.player.blockActionRestricted(this.minecraft.level, destroyBlockPos, this.localPlayerMode)) {
         return false;
      } else if(!this.minecraft.level.getWorldBorder().isWithinBounds(destroyBlockPos)) {
         return false;
      } else {
         if(this.localPlayerMode.isCreative()) {
            BlockState var3 = this.minecraft.level.getBlockState(destroyBlockPos);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, destroyBlockPos, var3, 1.0F);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, destroyBlockPos, direction);
            creativeDestroyBlock(this.minecraft, this, destroyBlockPos, direction);
            this.destroyDelay = 5;
         } else if(!this.isDestroying || !this.sameDestroyTarget(destroyBlockPos)) {
            if(this.isDestroying) {
               this.sendBlockAction(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, direction);
            }

            BlockState var3 = this.minecraft.level.getBlockState(destroyBlockPos);
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, destroyBlockPos, var3, 0.0F);
            this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, destroyBlockPos, direction);
            boolean var4 = !var3.isAir();
            if(var4 && this.destroyProgress == 0.0F) {
               var3.attack(this.minecraft.level, destroyBlockPos, this.minecraft.player);
            }

            if(var4 && var3.getDestroyProgress(this.minecraft.player, this.minecraft.player.level, destroyBlockPos) >= 1.0F) {
               this.destroyBlock(destroyBlockPos);
            } else {
               this.isDestroying = true;
               this.destroyBlockPos = destroyBlockPos;
               this.destroyingItem = this.minecraft.player.getMainHandItem();
               this.destroyProgress = 0.0F;
               this.destroyTicks = 0.0F;
               this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, (int)(this.destroyProgress * 10.0F) - 1);
            }
         }

         return true;
      }
   }

   public void stopDestroyBlock() {
      if(this.isDestroying) {
         BlockState var1 = this.minecraft.level.getBlockState(this.destroyBlockPos);
         this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, this.destroyBlockPos, var1, -1.0F);
         this.sendBlockAction(ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK, this.destroyBlockPos, Direction.DOWN);
         this.isDestroying = false;
         this.destroyProgress = 0.0F;
         this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, -1);
         this.minecraft.player.resetAttackStrengthTicker();
      }

   }

   public boolean continueDestroyBlock(BlockPos blockPos, Direction direction) {
      this.ensureHasSentCarriedItem();
      if(this.destroyDelay > 0) {
         --this.destroyDelay;
         return true;
      } else if(this.localPlayerMode.isCreative() && this.minecraft.level.getWorldBorder().isWithinBounds(blockPos)) {
         this.destroyDelay = 5;
         BlockState var3 = this.minecraft.level.getBlockState(blockPos);
         this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, blockPos, var3, 1.0F);
         this.sendBlockAction(ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK, blockPos, direction);
         creativeDestroyBlock(this.minecraft, this, blockPos, direction);
         return true;
      } else if(this.sameDestroyTarget(blockPos)) {
         BlockState var3 = this.minecraft.level.getBlockState(blockPos);
         if(var3.isAir()) {
            this.isDestroying = false;
            return false;
         } else {
            this.destroyProgress += var3.getDestroyProgress(this.minecraft.player, this.minecraft.player.level, blockPos);
            if(this.destroyTicks % 4.0F == 0.0F) {
               SoundType var4 = var3.getSoundType();
               this.minecraft.getSoundManager().play(new SimpleSoundInstance(var4.getHitSound(), SoundSource.NEUTRAL, (var4.getVolume() + 1.0F) / 8.0F, var4.getPitch() * 0.5F, blockPos));
            }

            ++this.destroyTicks;
            this.minecraft.getTutorial().onDestroyBlock(this.minecraft.level, blockPos, var3, Mth.clamp(this.destroyProgress, 0.0F, 1.0F));
            if(this.destroyProgress >= 1.0F) {
               this.isDestroying = false;
               this.sendBlockAction(ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction);
               this.destroyBlock(blockPos);
               this.destroyProgress = 0.0F;
               this.destroyTicks = 0.0F;
               this.destroyDelay = 5;
            }

            this.minecraft.level.destroyBlockProgress(this.minecraft.player.getId(), this.destroyBlockPos, (int)(this.destroyProgress * 10.0F) - 1);
            return true;
         }
      } else {
         return this.startDestroyBlock(blockPos, direction);
      }
   }

   public float getPickRange() {
      return this.localPlayerMode.isCreative()?5.0F:4.5F;
   }

   public void tick() {
      this.ensureHasSentCarriedItem();
      if(this.connection.getConnection().isConnected()) {
         this.connection.getConnection().tick();
      } else {
         this.connection.getConnection().handleDisconnection();
      }

   }

   private boolean sameDestroyTarget(BlockPos blockPos) {
      ItemStack var2 = this.minecraft.player.getMainHandItem();
      boolean var3 = this.destroyingItem.isEmpty() && var2.isEmpty();
      if(!this.destroyingItem.isEmpty() && !var2.isEmpty()) {
         var3 = var2.getItem() == this.destroyingItem.getItem() && ItemStack.tagMatches(var2, this.destroyingItem) && (var2.isDamageableItem() || var2.getDamageValue() == this.destroyingItem.getDamageValue());
      }

      return blockPos.equals(this.destroyBlockPos) && var3;
   }

   private void ensureHasSentCarriedItem() {
      int var1 = this.minecraft.player.inventory.selected;
      if(var1 != this.carriedIndex) {
         this.carriedIndex = var1;
         this.connection.send((Packet)(new ServerboundSetCarriedItemPacket(this.carriedIndex)));
      }

   }

   public InteractionResult useItemOn(LocalPlayer localPlayer, MultiPlayerLevel multiPlayerLevel, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      this.ensureHasSentCarriedItem();
      BlockPos var5 = blockHitResult.getBlockPos();
      Vec3 var6 = blockHitResult.getLocation();
      if(!this.minecraft.level.getWorldBorder().isWithinBounds(var5)) {
         return InteractionResult.FAIL;
      } else {
         ItemStack var7 = localPlayer.getItemInHand(interactionHand);
         if(this.localPlayerMode == GameType.SPECTATOR) {
            this.connection.send((Packet)(new ServerboundUseItemOnPacket(interactionHand, blockHitResult)));
            return InteractionResult.SUCCESS;
         } else {
            boolean var8 = !localPlayer.getMainHandItem().isEmpty() || !localPlayer.getOffhandItem().isEmpty();
            boolean var9 = localPlayer.isSneaking() && var8;
            if(!var9 && multiPlayerLevel.getBlockState(var5).use(multiPlayerLevel, localPlayer, interactionHand, blockHitResult)) {
               this.connection.send((Packet)(new ServerboundUseItemOnPacket(interactionHand, blockHitResult)));
               return InteractionResult.SUCCESS;
            } else {
               this.connection.send((Packet)(new ServerboundUseItemOnPacket(interactionHand, blockHitResult)));
               if(!var7.isEmpty() && !localPlayer.getCooldowns().isOnCooldown(var7.getItem())) {
                  UseOnContext var11 = new UseOnContext(localPlayer, interactionHand, blockHitResult);
                  InteractionResult var10;
                  if(this.localPlayerMode.isCreative()) {
                     int var12 = var7.getCount();
                     var10 = var7.useOn(var11);
                     var7.setCount(var12);
                  } else {
                     var10 = var7.useOn(var11);
                  }

                  return var10;
               } else {
                  return InteractionResult.PASS;
               }
            }
         }
      }
   }

   public InteractionResult useItem(Player player, Level level, InteractionHand interactionHand) {
      if(this.localPlayerMode == GameType.SPECTATOR) {
         return InteractionResult.PASS;
      } else {
         this.ensureHasSentCarriedItem();
         this.connection.send((Packet)(new ServerboundUseItemPacket(interactionHand)));
         ItemStack var4 = player.getItemInHand(interactionHand);
         if(player.getCooldowns().isOnCooldown(var4.getItem())) {
            return InteractionResult.PASS;
         } else {
            int var5 = var4.getCount();
            InteractionResultHolder<ItemStack> var6 = var4.use(level, player, interactionHand);
            ItemStack var7 = (ItemStack)var6.getObject();
            if(var7 != var4 || var7.getCount() != var5) {
               player.setItemInHand(interactionHand, var7);
            }

            return var6.getResult();
         }
      }
   }

   public LocalPlayer createPlayer(MultiPlayerLevel multiPlayerLevel, StatsCounter statsCounter, ClientRecipeBook clientRecipeBook) {
      return new LocalPlayer(this.minecraft, multiPlayerLevel, this.connection, statsCounter, clientRecipeBook);
   }

   public void attack(Player player, Entity entity) {
      this.ensureHasSentCarriedItem();
      this.connection.send((Packet)(new ServerboundInteractPacket(entity)));
      if(this.localPlayerMode != GameType.SPECTATOR) {
         player.attack(entity);
         player.resetAttackStrengthTicker();
      }

   }

   public InteractionResult interact(Player player, Entity entity, InteractionHand interactionHand) {
      this.ensureHasSentCarriedItem();
      this.connection.send((Packet)(new ServerboundInteractPacket(entity, interactionHand)));
      return this.localPlayerMode == GameType.SPECTATOR?InteractionResult.PASS:player.interactOn(entity, interactionHand);
   }

   public InteractionResult interactAt(Player player, Entity entity, EntityHitResult entityHitResult, InteractionHand interactionHand) {
      this.ensureHasSentCarriedItem();
      Vec3 var5 = entityHitResult.getLocation().subtract(entity.x, entity.y, entity.z);
      this.connection.send((Packet)(new ServerboundInteractPacket(entity, interactionHand, var5)));
      return this.localPlayerMode == GameType.SPECTATOR?InteractionResult.PASS:entity.interactAt(player, var5, interactionHand);
   }

   public ItemStack handleInventoryMouseClick(int var1, int var2, int var3, ClickType clickType, Player player) {
      short var6 = player.containerMenu.backup(player.inventory);
      ItemStack var7 = player.containerMenu.clicked(var2, var3, clickType, player);
      this.connection.send((Packet)(new ServerboundContainerClickPacket(var1, var2, var3, clickType, var7, var6)));
      return var7;
   }

   public void handlePlaceRecipe(int var1, Recipe recipe, boolean var3) {
      this.connection.send((Packet)(new ServerboundPlaceRecipePacket(var1, recipe, var3)));
   }

   public void handleInventoryButtonClick(int var1, int var2) {
      this.connection.send((Packet)(new ServerboundContainerButtonClickPacket(var1, var2)));
   }

   public void handleCreativeModeItemAdd(ItemStack itemStack, int var2) {
      if(this.localPlayerMode.isCreative()) {
         this.connection.send((Packet)(new ServerboundSetCreativeModeSlotPacket(var2, itemStack)));
      }

   }

   public void handleCreativeModeItemDrop(ItemStack itemStack) {
      if(this.localPlayerMode.isCreative() && !itemStack.isEmpty()) {
         this.connection.send((Packet)(new ServerboundSetCreativeModeSlotPacket(-1, itemStack)));
      }

   }

   public void releaseUsingItem(Player player) {
      this.ensureHasSentCarriedItem();
      this.connection.send((Packet)(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM, BlockPos.ZERO, Direction.DOWN)));
      player.releaseUsingItem();
   }

   public boolean hasExperience() {
      return this.localPlayerMode.isSurvival();
   }

   public boolean hasMissTime() {
      return !this.localPlayerMode.isCreative();
   }

   public boolean hasInfiniteItems() {
      return this.localPlayerMode.isCreative();
   }

   public boolean hasFarPickRange() {
      return this.localPlayerMode.isCreative();
   }

   public boolean isServerControlledInventory() {
      return this.minecraft.player.isPassenger() && this.minecraft.player.getVehicle() instanceof AbstractHorse;
   }

   public boolean isAlwaysFlying() {
      return this.localPlayerMode == GameType.SPECTATOR;
   }

   public GameType getPlayerMode() {
      return this.localPlayerMode;
   }

   public boolean isDestroying() {
      return this.isDestroying;
   }

   public void handlePickItem(int i) {
      this.connection.send((Packet)(new ServerboundPickItemPacket(i)));
   }

   private void sendBlockAction(ServerboundPlayerActionPacket.Action serverboundPlayerActionPacket$Action, BlockPos blockPos, Direction direction) {
      LocalPlayer var4 = this.minecraft.player;
      this.unAckedActions.put(Pair.of(blockPos, serverboundPlayerActionPacket$Action), new PosAndRot(var4.position(), var4.xRot, var4.yRot));
      this.connection.send((Packet)(new ServerboundPlayerActionPacket(serverboundPlayerActionPacket$Action, blockPos, direction)));
   }

   public void handleBlockBreakAck(MultiPlayerLevel multiPlayerLevel, BlockPos blockPos, BlockState blockState, ServerboundPlayerActionPacket.Action serverboundPlayerActionPacket$Action, boolean var5) {
      PosAndRot var6 = (PosAndRot)this.unAckedActions.remove(Pair.of(blockPos, serverboundPlayerActionPacket$Action));
      if(var6 == null || !var5 || serverboundPlayerActionPacket$Action != ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK && multiPlayerLevel.getBlockState(blockPos) != blockState) {
         multiPlayerLevel.setKnownState(blockPos, blockState);
         if(var6 != null) {
            Vec3 var7 = var6.pos();
            this.minecraft.player.absMoveTo(var7.x, var7.y, var7.z, var6.yRot(), var6.xRot());
         }
      }

      while(this.unAckedActions.size() >= 50) {
         Pair<BlockPos, ServerboundPlayerActionPacket.Action> var7 = (Pair)this.unAckedActions.firstKey();
         this.unAckedActions.removeFirst();
         LOGGER.error("Too many unacked block actions, dropping " + var7);
      }

   }
}
