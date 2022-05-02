package net.minecraft.server.network;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.ints.Int2ShortMap;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerAckPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerAckPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.util.StringUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerGamePacketListenerImpl implements ServerGamePacketListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public final Connection connection;
   private final MinecraftServer server;
   public ServerPlayer player;
   private int tickCount;
   private long keepAliveTime;
   private boolean keepAlivePending;
   private long keepAliveChallenge;
   private int chatSpamTickCount;
   private int dropSpamTickCount;
   private final Int2ShortMap expectedAcks = new Int2ShortOpenHashMap();
   private double firstGoodX;
   private double firstGoodY;
   private double firstGoodZ;
   private double lastGoodX;
   private double lastGoodY;
   private double lastGoodZ;
   private Entity lastVehicle;
   private double vehicleFirstGoodX;
   private double vehicleFirstGoodY;
   private double vehicleFirstGoodZ;
   private double vehicleLastGoodX;
   private double vehicleLastGoodY;
   private double vehicleLastGoodZ;
   private Vec3 awaitingPositionFromClient;
   private int awaitingTeleport;
   private int awaitingTeleportTime;
   private boolean clientIsFloating;
   private int aboveGroundTickCount;
   private boolean clientVehicleIsFloating;
   private int aboveGroundVehicleTickCount;
   private int receivedMovePacketCount;
   private int knownMovePacketCount;

   public ServerGamePacketListenerImpl(MinecraftServer server, Connection connection, ServerPlayer player) {
      this.server = server;
      this.connection = connection;
      connection.setListener(this);
      this.player = player;
      player.connection = this;
   }

   public void tick() {
      this.resetPosition();
      this.player.doTick();
      this.player.absMoveTo(this.firstGoodX, this.firstGoodY, this.firstGoodZ, this.player.yRot, this.player.xRot);
      ++this.tickCount;
      this.knownMovePacketCount = this.receivedMovePacketCount;
      if(this.clientIsFloating) {
         if(++this.aboveGroundTickCount > 80) {
            LOGGER.warn("{} was kicked for floating too long!", this.player.getName().getString());
            this.disconnect(new TranslatableComponent("multiplayer.disconnect.flying", new Object[0]));
            return;
         }
      } else {
         this.clientIsFloating = false;
         this.aboveGroundTickCount = 0;
      }

      this.lastVehicle = this.player.getRootVehicle();
      if(this.lastVehicle != this.player && this.lastVehicle.getControllingPassenger() == this.player) {
         this.vehicleFirstGoodX = this.lastVehicle.x;
         this.vehicleFirstGoodY = this.lastVehicle.y;
         this.vehicleFirstGoodZ = this.lastVehicle.z;
         this.vehicleLastGoodX = this.lastVehicle.x;
         this.vehicleLastGoodY = this.lastVehicle.y;
         this.vehicleLastGoodZ = this.lastVehicle.z;
         if(this.clientVehicleIsFloating && this.player.getRootVehicle().getControllingPassenger() == this.player) {
            if(++this.aboveGroundVehicleTickCount > 80) {
               LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName().getString());
               this.disconnect(new TranslatableComponent("multiplayer.disconnect.flying", new Object[0]));
               return;
            }
         } else {
            this.clientVehicleIsFloating = false;
            this.aboveGroundVehicleTickCount = 0;
         }
      } else {
         this.lastVehicle = null;
         this.clientVehicleIsFloating = false;
         this.aboveGroundVehicleTickCount = 0;
      }

      this.server.getProfiler().push("keepAlive");
      long var1 = Util.getMillis();
      if(var1 - this.keepAliveTime >= 15000L) {
         if(this.keepAlivePending) {
            this.disconnect(new TranslatableComponent("disconnect.timeout", new Object[0]));
         } else {
            this.keepAlivePending = true;
            this.keepAliveTime = var1;
            this.keepAliveChallenge = var1;
            this.send(new ClientboundKeepAlivePacket(this.keepAliveChallenge));
         }
      }

      this.server.getProfiler().pop();
      if(this.chatSpamTickCount > 0) {
         --this.chatSpamTickCount;
      }

      if(this.dropSpamTickCount > 0) {
         --this.dropSpamTickCount;
      }

      if(this.player.getLastActionTime() > 0L && this.server.getPlayerIdleTimeout() > 0 && Util.getMillis() - this.player.getLastActionTime() > (long)(this.server.getPlayerIdleTimeout() * 1000 * 60)) {
         this.disconnect(new TranslatableComponent("multiplayer.disconnect.idling", new Object[0]));
      }

   }

   public void resetPosition() {
      this.firstGoodX = this.player.x;
      this.firstGoodY = this.player.y;
      this.firstGoodZ = this.player.z;
      this.lastGoodX = this.player.x;
      this.lastGoodY = this.player.y;
      this.lastGoodZ = this.player.z;
   }

   public Connection getConnection() {
      return this.connection;
   }

   private boolean isSingleplayerOwner() {
      return this.server.isSingleplayerOwner(this.player.getGameProfile());
   }

   public void disconnect(Component component) {
      this.connection.send(new ClientboundDisconnectPacket(component), (future) -> {
         this.connection.disconnect(component);
      });
      this.connection.setReadOnly();
      MinecraftServer var10000 = this.server;
      Connection var10001 = this.connection;
      this.connection.getClass();
      var10000.executeBlocking(var10001::handleDisconnection);
   }

   public void handlePlayerInput(ServerboundPlayerInputPacket serverboundPlayerInputPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundPlayerInputPacket, this, (ServerLevel)this.player.getLevel());
      this.player.setPlayerInput(serverboundPlayerInputPacket.getXxa(), serverboundPlayerInputPacket.getZza(), serverboundPlayerInputPacket.isJumping(), serverboundPlayerInputPacket.isSneaking());
   }

   private static boolean containsInvalidValues(ServerboundMovePlayerPacket serverboundMovePlayerPacket) {
      return Doubles.isFinite(serverboundMovePlayerPacket.getX(0.0D)) && Doubles.isFinite(serverboundMovePlayerPacket.getY(0.0D)) && Doubles.isFinite(serverboundMovePlayerPacket.getZ(0.0D)) && Floats.isFinite(serverboundMovePlayerPacket.getXRot(0.0F)) && Floats.isFinite(serverboundMovePlayerPacket.getYRot(0.0F))?Math.abs(serverboundMovePlayerPacket.getX(0.0D)) > 3.0E7D || Math.abs(serverboundMovePlayerPacket.getY(0.0D)) > 3.0E7D || Math.abs(serverboundMovePlayerPacket.getZ(0.0D)) > 3.0E7D:true;
   }

   private static boolean containsInvalidValues(ServerboundMoveVehiclePacket serverboundMoveVehiclePacket) {
      return !Doubles.isFinite(serverboundMoveVehiclePacket.getX()) || !Doubles.isFinite(serverboundMoveVehiclePacket.getY()) || !Doubles.isFinite(serverboundMoveVehiclePacket.getZ()) || !Floats.isFinite(serverboundMoveVehiclePacket.getXRot()) || !Floats.isFinite(serverboundMoveVehiclePacket.getYRot());
   }

   public void handleMoveVehicle(ServerboundMoveVehiclePacket serverboundMoveVehiclePacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundMoveVehiclePacket, this, (ServerLevel)this.player.getLevel());
      if(containsInvalidValues(serverboundMoveVehiclePacket)) {
         this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_vehicle_movement", new Object[0]));
      } else {
         Entity var2 = this.player.getRootVehicle();
         if(var2 != this.player && var2.getControllingPassenger() == this.player && var2 == this.lastVehicle) {
            ServerLevel var3 = this.player.getLevel();
            double var4 = var2.x;
            double var6 = var2.y;
            double var8 = var2.z;
            double var10 = serverboundMoveVehiclePacket.getX();
            double var12 = serverboundMoveVehiclePacket.getY();
            double var14 = serverboundMoveVehiclePacket.getZ();
            float var16 = serverboundMoveVehiclePacket.getYRot();
            float var17 = serverboundMoveVehiclePacket.getXRot();
            double var18 = var10 - this.vehicleFirstGoodX;
            double var20 = var12 - this.vehicleFirstGoodY;
            double var22 = var14 - this.vehicleFirstGoodZ;
            double var24 = var2.getDeltaMovement().lengthSqr();
            double var26 = var18 * var18 + var20 * var20 + var22 * var22;
            if(var26 - var24 > 100.0D && !this.isSingleplayerOwner()) {
               LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", var2.getName().getString(), this.player.getName().getString(), Double.valueOf(var18), Double.valueOf(var20), Double.valueOf(var22));
               this.connection.send(new ClientboundMoveVehiclePacket(var2));
               return;
            }

            boolean var28 = var3.noCollision(var2, var2.getBoundingBox().deflate(0.0625D));
            var18 = var10 - this.vehicleLastGoodX;
            var20 = var12 - this.vehicleLastGoodY - 1.0E-6D;
            var22 = var14 - this.vehicleLastGoodZ;
            var2.move(MoverType.PLAYER, new Vec3(var18, var20, var22));
            double var29 = var20;
            var18 = var10 - var2.x;
            var20 = var12 - var2.y;
            if(var20 > -0.5D || var20 < 0.5D) {
               var20 = 0.0D;
            }

            var22 = var14 - var2.z;
            var26 = var18 * var18 + var20 * var20 + var22 * var22;
            boolean var31 = false;
            if(var26 > 0.0625D) {
               var31 = true;
               LOGGER.warn("{} moved wrongly!", var2.getName().getString());
            }

            var2.absMoveTo(var10, var12, var14, var16, var17);
            boolean var32 = var3.noCollision(var2, var2.getBoundingBox().deflate(0.0625D));
            if(var28 && (var31 || !var32)) {
               var2.absMoveTo(var4, var6, var8, var16, var17);
               this.connection.send(new ClientboundMoveVehiclePacket(var2));
               return;
            }

            this.player.getLevel().getChunkSource().move(this.player);
            this.player.checkMovementStatistics(this.player.x - var4, this.player.y - var6, this.player.z - var8);
            this.clientVehicleIsFloating = var29 >= -0.03125D && !this.server.isFlightAllowed() && !var3.containsAnyBlocks(var2.getBoundingBox().inflate(0.0625D).expandTowards(0.0D, -0.55D, 0.0D));
            this.vehicleLastGoodX = var2.x;
            this.vehicleLastGoodY = var2.y;
            this.vehicleLastGoodZ = var2.z;
         }

      }
   }

   public void handleAcceptTeleportPacket(ServerboundAcceptTeleportationPacket serverboundAcceptTeleportationPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundAcceptTeleportationPacket, this, (ServerLevel)this.player.getLevel());
      if(serverboundAcceptTeleportationPacket.getId() == this.awaitingTeleport) {
         this.player.absMoveTo(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.yRot, this.player.xRot);
         this.lastGoodX = this.awaitingPositionFromClient.x;
         this.lastGoodY = this.awaitingPositionFromClient.y;
         this.lastGoodZ = this.awaitingPositionFromClient.z;
         if(this.player.isChangingDimension()) {
            this.player.hasChangedDimension();
         }

         this.awaitingPositionFromClient = null;
      }

   }

   public void handleRecipeBookUpdatePacket(ServerboundRecipeBookUpdatePacket serverboundRecipeBookUpdatePacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundRecipeBookUpdatePacket, this, (ServerLevel)this.player.getLevel());
      if(serverboundRecipeBookUpdatePacket.getPurpose() == ServerboundRecipeBookUpdatePacket.Purpose.SHOWN) {
         Optional var10000 = this.server.getRecipeManager().byKey(serverboundRecipeBookUpdatePacket.getRecipe());
         ServerRecipeBook var10001 = this.player.getRecipeBook();
         var10000.ifPresent(var10001::removeHighlight);
      } else if(serverboundRecipeBookUpdatePacket.getPurpose() == ServerboundRecipeBookUpdatePacket.Purpose.SETTINGS) {
         this.player.getRecipeBook().setGuiOpen(serverboundRecipeBookUpdatePacket.isGuiOpen());
         this.player.getRecipeBook().setFilteringCraftable(serverboundRecipeBookUpdatePacket.isFilteringCraftable());
         this.player.getRecipeBook().setFurnaceGuiOpen(serverboundRecipeBookUpdatePacket.isFurnaceGuiOpen());
         this.player.getRecipeBook().setFurnaceFilteringCraftable(serverboundRecipeBookUpdatePacket.isFurnaceFilteringCraftable());
         this.player.getRecipeBook().setBlastingFurnaceGuiOpen(serverboundRecipeBookUpdatePacket.isBlastFurnaceGuiOpen());
         this.player.getRecipeBook().setBlastingFurnaceFilteringCraftable(serverboundRecipeBookUpdatePacket.isBlastFurnaceFilteringCraftable());
         this.player.getRecipeBook().setSmokerGuiOpen(serverboundRecipeBookUpdatePacket.isSmokerGuiOpen());
         this.player.getRecipeBook().setSmokerFilteringCraftable(serverboundRecipeBookUpdatePacket.isSmokerFilteringCraftable());
      }

   }

   public void handleSeenAdvancements(ServerboundSeenAdvancementsPacket serverboundSeenAdvancementsPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundSeenAdvancementsPacket, this, (ServerLevel)this.player.getLevel());
      if(serverboundSeenAdvancementsPacket.getAction() == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
         ResourceLocation var2 = serverboundSeenAdvancementsPacket.getTab();
         Advancement var3 = this.server.getAdvancements().getAdvancement(var2);
         if(var3 != null) {
            this.player.getAdvancements().setSelectedTab(var3);
         }
      }

   }

   public void handleCustomCommandSuggestions(ServerboundCommandSuggestionPacket serverboundCommandSuggestionPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundCommandSuggestionPacket, this, (ServerLevel)this.player.getLevel());
      StringReader var2 = new StringReader(serverboundCommandSuggestionPacket.getCommand());
      if(var2.canRead() && var2.peek() == 47) {
         var2.skip();
      }

      ParseResults<CommandSourceStack> var3 = this.server.getCommands().getDispatcher().parse(var2, this.player.createCommandSourceStack());
      this.server.getCommands().getDispatcher().getCompletionSuggestions(var3).thenAccept((suggestions) -> {
         this.connection.send(new ClientboundCommandSuggestionsPacket(serverboundCommandSuggestionPacket.getId(), suggestions));
      });
   }

   public void handleSetCommandBlock(ServerboundSetCommandBlockPacket serverboundSetCommandBlockPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundSetCommandBlockPacket, this, (ServerLevel)this.player.getLevel());
      if(!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TranslatableComponent("advMode.notEnabled", new Object[0]));
      } else if(!this.player.canUseGameMasterBlocks()) {
         this.player.sendMessage(new TranslatableComponent("advMode.notAllowed", new Object[0]));
      } else {
         BaseCommandBlock var2 = null;
         CommandBlockEntity var3 = null;
         BlockPos var4 = serverboundSetCommandBlockPacket.getPos();
         BlockEntity var5 = this.player.level.getBlockEntity(var4);
         if(var5 instanceof CommandBlockEntity) {
            var3 = (CommandBlockEntity)var5;
            var2 = var3.getCommandBlock();
         }

         String var6 = serverboundSetCommandBlockPacket.getCommand();
         boolean var7 = serverboundSetCommandBlockPacket.isTrackOutput();
         if(var2 != null) {
            Direction var8 = (Direction)this.player.level.getBlockState(var4).getValue(CommandBlock.FACING);
            switch(serverboundSetCommandBlockPacket.getMode()) {
            case SEQUENCE:
               BlockState var9 = Blocks.CHAIN_COMMAND_BLOCK.defaultBlockState();
               this.player.level.setBlock(var4, (BlockState)((BlockState)var9.setValue(CommandBlock.FACING, var8)).setValue(CommandBlock.CONDITIONAL, Boolean.valueOf(serverboundSetCommandBlockPacket.isConditional())), 2);
               break;
            case AUTO:
               BlockState var9 = Blocks.REPEATING_COMMAND_BLOCK.defaultBlockState();
               this.player.level.setBlock(var4, (BlockState)((BlockState)var9.setValue(CommandBlock.FACING, var8)).setValue(CommandBlock.CONDITIONAL, Boolean.valueOf(serverboundSetCommandBlockPacket.isConditional())), 2);
               break;
            case REDSTONE:
            default:
               BlockState var9 = Blocks.COMMAND_BLOCK.defaultBlockState();
               this.player.level.setBlock(var4, (BlockState)((BlockState)var9.setValue(CommandBlock.FACING, var8)).setValue(CommandBlock.CONDITIONAL, Boolean.valueOf(serverboundSetCommandBlockPacket.isConditional())), 2);
            }

            var5.clearRemoved();
            this.player.level.setBlockEntity(var4, var5);
            var2.setCommand(var6);
            var2.setTrackOutput(var7);
            if(!var7) {
               var2.setLastOutput((Component)null);
            }

            var3.setAutomatic(serverboundSetCommandBlockPacket.isAutomatic());
            var2.onUpdated();
            if(!StringUtil.isNullOrEmpty(var6)) {
               this.player.sendMessage(new TranslatableComponent("advMode.setCommand.success", new Object[]{var6}));
            }
         }

      }
   }

   public void handleSetCommandMinecart(ServerboundSetCommandMinecartPacket serverboundSetCommandMinecartPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundSetCommandMinecartPacket, this, (ServerLevel)this.player.getLevel());
      if(!this.server.isCommandBlockEnabled()) {
         this.player.sendMessage(new TranslatableComponent("advMode.notEnabled", new Object[0]));
      } else if(!this.player.canUseGameMasterBlocks()) {
         this.player.sendMessage(new TranslatableComponent("advMode.notAllowed", new Object[0]));
      } else {
         BaseCommandBlock var2 = serverboundSetCommandMinecartPacket.getCommandBlock(this.player.level);
         if(var2 != null) {
            var2.setCommand(serverboundSetCommandMinecartPacket.getCommand());
            var2.setTrackOutput(serverboundSetCommandMinecartPacket.isTrackOutput());
            if(!serverboundSetCommandMinecartPacket.isTrackOutput()) {
               var2.setLastOutput((Component)null);
            }

            var2.onUpdated();
            this.player.sendMessage(new TranslatableComponent("advMode.setCommand.success", new Object[]{serverboundSetCommandMinecartPacket.getCommand()}));
         }

      }
   }

   public void handlePickItem(ServerboundPickItemPacket serverboundPickItemPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundPickItemPacket, this, (ServerLevel)this.player.getLevel());
      this.player.inventory.pickSlot(serverboundPickItemPacket.getSlot());
      this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, this.player.inventory.selected, this.player.inventory.getItem(this.player.inventory.selected)));
      this.player.connection.send(new ClientboundContainerSetSlotPacket(-2, serverboundPickItemPacket.getSlot(), this.player.inventory.getItem(serverboundPickItemPacket.getSlot())));
      this.player.connection.send(new ClientboundSetCarriedItemPacket(this.player.inventory.selected));
   }

   public void handleRenameItem(ServerboundRenameItemPacket serverboundRenameItemPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundRenameItemPacket, this, (ServerLevel)this.player.getLevel());
      if(this.player.containerMenu instanceof AnvilMenu) {
         AnvilMenu var2 = (AnvilMenu)this.player.containerMenu;
         String var3 = SharedConstants.filterText(serverboundRenameItemPacket.getName());
         if(var3.length() <= 35) {
            var2.setItemName(var3);
         }
      }

   }

   public void handleSetBeaconPacket(ServerboundSetBeaconPacket serverboundSetBeaconPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundSetBeaconPacket, this, (ServerLevel)this.player.getLevel());
      if(this.player.containerMenu instanceof BeaconMenu) {
         ((BeaconMenu)this.player.containerMenu).updateEffects(serverboundSetBeaconPacket.getPrimary(), serverboundSetBeaconPacket.getSecondary());
      }

   }

   public void handleSetStructureBlock(ServerboundSetStructureBlockPacket serverboundSetStructureBlockPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundSetStructureBlockPacket, this, (ServerLevel)this.player.getLevel());
      if(this.player.canUseGameMasterBlocks()) {
         BlockPos var2 = serverboundSetStructureBlockPacket.getPos();
         BlockState var3 = this.player.level.getBlockState(var2);
         BlockEntity var4 = this.player.level.getBlockEntity(var2);
         if(var4 instanceof StructureBlockEntity) {
            StructureBlockEntity var5 = (StructureBlockEntity)var4;
            var5.setMode(serverboundSetStructureBlockPacket.getMode());
            var5.setStructureName(serverboundSetStructureBlockPacket.getName());
            var5.setStructurePos(serverboundSetStructureBlockPacket.getOffset());
            var5.setStructureSize(serverboundSetStructureBlockPacket.getSize());
            var5.setMirror(serverboundSetStructureBlockPacket.getMirror());
            var5.setRotation(serverboundSetStructureBlockPacket.getRotation());
            var5.setMetaData(serverboundSetStructureBlockPacket.getData());
            var5.setIgnoreEntities(serverboundSetStructureBlockPacket.isIgnoreEntities());
            var5.setShowAir(serverboundSetStructureBlockPacket.isShowAir());
            var5.setShowBoundingBox(serverboundSetStructureBlockPacket.isShowBoundingBox());
            var5.setIntegrity(serverboundSetStructureBlockPacket.getIntegrity());
            var5.setSeed(serverboundSetStructureBlockPacket.getSeed());
            if(var5.hasStructureName()) {
               String var6 = var5.getStructureName();
               if(serverboundSetStructureBlockPacket.getUpdateType() == StructureBlockEntity.UpdateType.SAVE_AREA) {
                  if(var5.saveStructure()) {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.save_success", new Object[]{var6}), false);
                  } else {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.save_failure", new Object[]{var6}), false);
                  }
               } else if(serverboundSetStructureBlockPacket.getUpdateType() == StructureBlockEntity.UpdateType.LOAD_AREA) {
                  if(!var5.isStructureLoadable()) {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.load_not_found", new Object[]{var6}), false);
                  } else if(var5.loadStructure()) {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.load_success", new Object[]{var6}), false);
                  } else {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.load_prepare", new Object[]{var6}), false);
                  }
               } else if(serverboundSetStructureBlockPacket.getUpdateType() == StructureBlockEntity.UpdateType.SCAN_AREA) {
                  if(var5.detectSize()) {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.size_success", new Object[]{var6}), false);
                  } else {
                     this.player.displayClientMessage(new TranslatableComponent("structure_block.size_failure", new Object[0]), false);
                  }
               }
            } else {
               this.player.displayClientMessage(new TranslatableComponent("structure_block.invalid_structure_name", new Object[]{serverboundSetStructureBlockPacket.getName()}), false);
            }

            var5.setChanged();
            this.player.level.sendBlockUpdated(var2, var3, var3, 3);
         }

      }
   }

   public void handleSetJigsawBlock(ServerboundSetJigsawBlockPacket serverboundSetJigsawBlockPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundSetJigsawBlockPacket, this, (ServerLevel)this.player.getLevel());
      if(this.player.canUseGameMasterBlocks()) {
         BlockPos var2 = serverboundSetJigsawBlockPacket.getPos();
         BlockState var3 = this.player.level.getBlockState(var2);
         BlockEntity var4 = this.player.level.getBlockEntity(var2);
         if(var4 instanceof JigsawBlockEntity) {
            JigsawBlockEntity var5 = (JigsawBlockEntity)var4;
            var5.setAttachementType(serverboundSetJigsawBlockPacket.getAttachementType());
            var5.setTargetPool(serverboundSetJigsawBlockPacket.getTargetPool());
            var5.setFinalState(serverboundSetJigsawBlockPacket.getFinalState());
            var5.setChanged();
            this.player.level.sendBlockUpdated(var2, var3, var3, 3);
         }

      }
   }

   public void handleSelectTrade(ServerboundSelectTradePacket serverboundSelectTradePacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundSelectTradePacket, this, (ServerLevel)this.player.getLevel());
      int var2 = serverboundSelectTradePacket.getItem();
      AbstractContainerMenu var3 = this.player.containerMenu;
      if(var3 instanceof MerchantMenu) {
         MerchantMenu var4 = (MerchantMenu)var3;
         var4.setSelectionHint(var2);
         var4.tryMoveItems(var2);
      }

   }

   public void handleEditBook(ServerboundEditBookPacket serverboundEditBookPacket) {
      ItemStack var2 = serverboundEditBookPacket.getBook();
      if(!var2.isEmpty()) {
         if(WritableBookItem.makeSureTagIsValid(var2.getTag())) {
            ItemStack var3 = this.player.getItemInHand(serverboundEditBookPacket.getHand());
            if(var2.getItem() == Items.WRITABLE_BOOK && var3.getItem() == Items.WRITABLE_BOOK) {
               if(serverboundEditBookPacket.isSigning()) {
                  ItemStack var4 = new ItemStack(Items.WRITTEN_BOOK);
                  CompoundTag var5 = var3.getTag();
                  if(var5 != null) {
                     var4.setTag(var5.copy());
                  }

                  var4.addTagElement("author", new StringTag(this.player.getName().getString()));
                  var4.addTagElement("title", new StringTag(var2.getTag().getString("title")));
                  ListTag var6 = var2.getTag().getList("pages", 8);

                  for(int var7 = 0; var7 < var6.size(); ++var7) {
                     String var8 = var6.getString(var7);
                     Component var9 = new TextComponent(var8);
                     var8 = Component.Serializer.toJson(var9);
                     var6.set(var7, (Tag)(new StringTag(var8)));
                  }

                  var4.addTagElement("pages", var6);
                  this.player.setItemInHand(serverboundEditBookPacket.getHand(), var4);
               } else {
                  var3.addTagElement("pages", var2.getTag().getList("pages", 8));
               }
            }

         }
      }
   }

   public void handleEntityTagQuery(ServerboundEntityTagQuery serverboundEntityTagQuery) {
      PacketUtils.ensureRunningOnSameThread(serverboundEntityTagQuery, this, (ServerLevel)this.player.getLevel());
      if(this.player.hasPermissions(2)) {
         Entity var2 = this.player.getLevel().getEntity(serverboundEntityTagQuery.getEntityId());
         if(var2 != null) {
            CompoundTag var3 = var2.saveWithoutId(new CompoundTag());
            this.player.connection.send(new ClientboundTagQueryPacket(serverboundEntityTagQuery.getTransactionId(), var3));
         }

      }
   }

   public void handleBlockEntityTagQuery(ServerboundBlockEntityTagQuery serverboundBlockEntityTagQuery) {
      PacketUtils.ensureRunningOnSameThread(serverboundBlockEntityTagQuery, this, (ServerLevel)this.player.getLevel());
      if(this.player.hasPermissions(2)) {
         BlockEntity var2 = this.player.getLevel().getBlockEntity(serverboundBlockEntityTagQuery.getPos());
         CompoundTag var3 = var2 != null?var2.save(new CompoundTag()):null;
         this.player.connection.send(new ClientboundTagQueryPacket(serverboundBlockEntityTagQuery.getTransactionId(), var3));
      }
   }

   public void handleMovePlayer(ServerboundMovePlayerPacket serverboundMovePlayerPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundMovePlayerPacket, this, (ServerLevel)this.player.getLevel());
      if(containsInvalidValues(serverboundMovePlayerPacket)) {
         this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_player_movement", new Object[0]));
      } else {
         ServerLevel var2 = this.server.getLevel(this.player.dimension);
         if(!this.player.wonGame) {
            if(this.tickCount == 0) {
               this.resetPosition();
            }

            if(this.awaitingPositionFromClient != null) {
               if(this.tickCount - this.awaitingTeleportTime > 20) {
                  this.awaitingTeleportTime = this.tickCount;
                  this.teleport(this.awaitingPositionFromClient.x, this.awaitingPositionFromClient.y, this.awaitingPositionFromClient.z, this.player.yRot, this.player.xRot);
               }

            } else {
               this.awaitingTeleportTime = this.tickCount;
               if(this.player.isPassenger()) {
                  this.player.absMoveTo(this.player.x, this.player.y, this.player.z, serverboundMovePlayerPacket.getYRot(this.player.yRot), serverboundMovePlayerPacket.getXRot(this.player.xRot));
                  this.player.getLevel().getChunkSource().move(this.player);
               } else {
                  double var3 = this.player.x;
                  double var5 = this.player.y;
                  double var7 = this.player.z;
                  double var9 = this.player.y;
                  double var11 = serverboundMovePlayerPacket.getX(this.player.x);
                  double var13 = serverboundMovePlayerPacket.getY(this.player.y);
                  double var15 = serverboundMovePlayerPacket.getZ(this.player.z);
                  float var17 = serverboundMovePlayerPacket.getYRot(this.player.yRot);
                  float var18 = serverboundMovePlayerPacket.getXRot(this.player.xRot);
                  double var19 = var11 - this.firstGoodX;
                  double var21 = var13 - this.firstGoodY;
                  double var23 = var15 - this.firstGoodZ;
                  double var25 = this.player.getDeltaMovement().lengthSqr();
                  double var27 = var19 * var19 + var21 * var21 + var23 * var23;
                  if(this.player.isSleeping()) {
                     if(var27 > 1.0D) {
                        this.teleport(this.player.x, this.player.y, this.player.z, serverboundMovePlayerPacket.getYRot(this.player.yRot), serverboundMovePlayerPacket.getXRot(this.player.xRot));
                     }

                  } else {
                     ++this.receivedMovePacketCount;
                     int var29 = this.receivedMovePacketCount - this.knownMovePacketCount;
                     if(var29 > 5) {
                        LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName().getString(), Integer.valueOf(var29));
                        var29 = 1;
                     }

                     if(!this.player.isChangingDimension() && (!this.player.getLevel().getGameRules().getBoolean(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK) || !this.player.isFallFlying())) {
                        float var30 = this.player.isFallFlying()?300.0F:100.0F;
                        if(var27 - var25 > (double)(var30 * (float)var29) && !this.isSingleplayerOwner()) {
                           LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName().getString(), Double.valueOf(var19), Double.valueOf(var21), Double.valueOf(var23));
                           this.teleport(this.player.x, this.player.y, this.player.z, this.player.yRot, this.player.xRot);
                           return;
                        }
                     }

                     boolean var30 = this.isPlayerCollidingWithAnything(var2);
                     var19 = var11 - this.lastGoodX;
                     var21 = var13 - this.lastGoodY;
                     var23 = var15 - this.lastGoodZ;
                     if(this.player.onGround && !serverboundMovePlayerPacket.isOnGround() && var21 > 0.0D) {
                        this.player.jumpFromGround();
                     }

                     this.player.move(MoverType.PLAYER, new Vec3(var19, var21, var23));
                     this.player.onGround = serverboundMovePlayerPacket.isOnGround();
                     double var31 = var21;
                     var19 = var11 - this.player.x;
                     var21 = var13 - this.player.y;
                     if(var21 > -0.5D || var21 < 0.5D) {
                        var21 = 0.0D;
                     }

                     var23 = var15 - this.player.z;
                     var27 = var19 * var19 + var21 * var21 + var23 * var23;
                     boolean var33 = false;
                     if(!this.player.isChangingDimension() && var27 > 0.0625D && !this.player.isSleeping() && !this.player.gameMode.isCreative() && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                        var33 = true;
                        LOGGER.warn("{} moved wrongly!", this.player.getName().getString());
                     }

                     this.player.absMoveTo(var11, var13, var15, var17, var18);
                     this.player.checkMovementStatistics(this.player.x - var3, this.player.y - var5, this.player.z - var7);
                     if(!this.player.noPhysics && !this.player.isSleeping()) {
                        boolean var34 = this.isPlayerCollidingWithAnything(var2);
                        if(var30 && (var33 || !var34)) {
                           this.teleport(var3, var5, var7, var17, var18);
                           return;
                        }
                     }

                     this.clientIsFloating = var31 >= -0.03125D && this.player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR && !this.server.isFlightAllowed() && !this.player.abilities.mayfly && !this.player.hasEffect(MobEffects.LEVITATION) && !this.player.isFallFlying() && !var2.containsAnyBlocks(this.player.getBoundingBox().inflate(0.0625D).expandTowards(0.0D, -0.55D, 0.0D));
                     this.player.onGround = serverboundMovePlayerPacket.isOnGround();
                     this.player.getLevel().getChunkSource().move(this.player);
                     this.player.doCheckFallDamage(this.player.y - var9, serverboundMovePlayerPacket.isOnGround());
                     this.lastGoodX = this.player.x;
                     this.lastGoodY = this.player.y;
                     this.lastGoodZ = this.player.z;
                  }
               }
            }
         }
      }
   }

   private boolean isPlayerCollidingWithAnything(LevelReader levelReader) {
      return levelReader.noCollision(this.player, this.player.getBoundingBox().deflate(9.999999747378752E-6D));
   }

   public void teleport(double var1, double var3, double var5, float var7, float var8) {
      this.teleport(var1, var3, var5, var7, var8, Collections.emptySet());
   }

   public void teleport(double var1, double var3, double var5, float var7, float var8, Set set) {
      double var10 = set.contains(ClientboundPlayerPositionPacket.RelativeArgument.X)?this.player.x:0.0D;
      double var12 = set.contains(ClientboundPlayerPositionPacket.RelativeArgument.Y)?this.player.y:0.0D;
      double var14 = set.contains(ClientboundPlayerPositionPacket.RelativeArgument.Z)?this.player.z:0.0D;
      float var16 = set.contains(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT)?this.player.yRot:0.0F;
      float var17 = set.contains(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT)?this.player.xRot:0.0F;
      this.awaitingPositionFromClient = new Vec3(var1, var3, var5);
      if(++this.awaitingTeleport == Integer.MAX_VALUE) {
         this.awaitingTeleport = 0;
      }

      this.awaitingTeleportTime = this.tickCount;
      this.player.absMoveTo(var1, var3, var5, var7, var8);
      this.player.connection.send(new ClientboundPlayerPositionPacket(var1 - var10, var3 - var12, var5 - var14, var7 - var16, var8 - var17, set, this.awaitingTeleport));
   }

   public void handlePlayerAction(ServerboundPlayerActionPacket serverboundPlayerActionPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundPlayerActionPacket, this, (ServerLevel)this.player.getLevel());
      BlockPos var2 = serverboundPlayerActionPacket.getPos();
      this.player.resetLastActionTime();
      ServerboundPlayerActionPacket.Action var3 = serverboundPlayerActionPacket.getAction();
      switch(var3) {
      case SWAP_HELD_ITEMS:
         if(!this.player.isSpectator()) {
            ItemStack var4 = this.player.getItemInHand(InteractionHand.OFF_HAND);
            this.player.setItemInHand(InteractionHand.OFF_HAND, this.player.getItemInHand(InteractionHand.MAIN_HAND));
            this.player.setItemInHand(InteractionHand.MAIN_HAND, var4);
         }

         return;
      case DROP_ITEM:
         if(!this.player.isSpectator()) {
            this.player.drop(false);
         }

         return;
      case DROP_ALL_ITEMS:
         if(!this.player.isSpectator()) {
            this.player.drop(true);
         }

         return;
      case RELEASE_USE_ITEM:
         this.player.releaseUsingItem();
         return;
      case START_DESTROY_BLOCK:
      case ABORT_DESTROY_BLOCK:
      case STOP_DESTROY_BLOCK:
         this.player.gameMode.handleBlockBreakAction(var2, var3, serverboundPlayerActionPacket.getDirection(), this.server.getMaxBuildHeight());
         return;
      default:
         throw new IllegalArgumentException("Invalid player action");
      }
   }

   public void handleUseItemOn(ServerboundUseItemOnPacket serverboundUseItemOnPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundUseItemOnPacket, this, (ServerLevel)this.player.getLevel());
      ServerLevel var2 = this.server.getLevel(this.player.dimension);
      InteractionHand var3 = serverboundUseItemOnPacket.getHand();
      ItemStack var4 = this.player.getItemInHand(var3);
      BlockHitResult var5 = serverboundUseItemOnPacket.getHitResult();
      BlockPos var6 = var5.getBlockPos();
      Direction var7 = var5.getDirection();
      this.player.resetLastActionTime();
      if(var6.getY() < this.server.getMaxBuildHeight() - 1 || var7 != Direction.UP && var6.getY() < this.server.getMaxBuildHeight()) {
         if(this.awaitingPositionFromClient == null && this.player.distanceToSqr((double)var6.getX() + 0.5D, (double)var6.getY() + 0.5D, (double)var6.getZ() + 0.5D) < 64.0D && var2.mayInteract(this.player, var6)) {
            this.player.gameMode.useItemOn(this.player, var2, var4, var3, var5);
         }
      } else {
         Component var8 = (new TranslatableComponent("build.tooHigh", new Object[]{Integer.valueOf(this.server.getMaxBuildHeight())})).withStyle(ChatFormatting.RED);
         this.player.connection.send(new ClientboundChatPacket(var8, ChatType.GAME_INFO));
      }

      this.player.connection.send(new ClientboundBlockUpdatePacket(var2, var6));
      this.player.connection.send(new ClientboundBlockUpdatePacket(var2, var6.relative(var7)));
   }

   public void handleUseItem(ServerboundUseItemPacket serverboundUseItemPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundUseItemPacket, this, (ServerLevel)this.player.getLevel());
      ServerLevel var2 = this.server.getLevel(this.player.dimension);
      InteractionHand var3 = serverboundUseItemPacket.getHand();
      ItemStack var4 = this.player.getItemInHand(var3);
      this.player.resetLastActionTime();
      if(!var4.isEmpty()) {
         this.player.gameMode.useItem(this.player, var2, var4, var3);
      }
   }

   public void handleTeleportToEntityPacket(ServerboundTeleportToEntityPacket serverboundTeleportToEntityPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundTeleportToEntityPacket, this, (ServerLevel)this.player.getLevel());
      if(this.player.isSpectator()) {
         for(ServerLevel var3 : this.server.getAllLevels()) {
            Entity var4 = serverboundTeleportToEntityPacket.getEntity(var3);
            if(var4 != null) {
               this.player.teleportTo(var3, var4.x, var4.y, var4.z, var4.yRot, var4.xRot);
               return;
            }
         }
      }

   }

   public void handleResourcePackResponse(ServerboundResourcePackPacket serverboundResourcePackPacket) {
   }

   public void handlePaddleBoat(ServerboundPaddleBoatPacket serverboundPaddleBoatPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundPaddleBoatPacket, this, (ServerLevel)this.player.getLevel());
      Entity var2 = this.player.getVehicle();
      if(var2 instanceof Boat) {
         ((Boat)var2).setPaddleState(serverboundPaddleBoatPacket.getLeft(), serverboundPaddleBoatPacket.getRight());
      }

   }

   public void onDisconnect(Component component) {
      LOGGER.info("{} lost connection: {}", this.player.getName().getString(), component.getString());
      this.server.invalidateStatus();
      this.server.getPlayerList().broadcastMessage((new TranslatableComponent("multiplayer.player.left", new Object[]{this.player.getDisplayName()})).withStyle(ChatFormatting.YELLOW));
      this.player.disconnect();
      this.server.getPlayerList().remove(this.player);
      if(this.isSingleplayerOwner()) {
         LOGGER.info("Stopping singleplayer server as player logged out");
         this.server.halt(false);
      }

   }

   public void send(Packet packet) {
      this.send(packet, (GenericFutureListener)null);
   }

   public void send(Packet packet, @Nullable GenericFutureListener genericFutureListener) {
      if(packet instanceof ClientboundChatPacket) {
         ClientboundChatPacket var3 = (ClientboundChatPacket)packet;
         ChatVisiblity var4 = this.player.getChatVisibility();
         if(var4 == ChatVisiblity.HIDDEN && var3.getType() != ChatType.GAME_INFO) {
            return;
         }

         if(var4 == ChatVisiblity.SYSTEM && !var3.isSystem()) {
            return;
         }
      }

      try {
         this.connection.send(packet, genericFutureListener);
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, "Sending packet");
         CrashReportCategory var5 = var4.addCategory("Packet being sent");
         var5.setDetail("Packet class", () -> {
            return packet.getClass().getCanonicalName();
         });
         throw new ReportedException(var4);
      }
   }

   public void handleSetCarriedItem(ServerboundSetCarriedItemPacket serverboundSetCarriedItemPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundSetCarriedItemPacket, this, (ServerLevel)this.player.getLevel());
      if(serverboundSetCarriedItemPacket.getSlot() >= 0 && serverboundSetCarriedItemPacket.getSlot() < Inventory.getSelectionSize()) {
         this.player.inventory.selected = serverboundSetCarriedItemPacket.getSlot();
         this.player.resetLastActionTime();
      } else {
         LOGGER.warn("{} tried to set an invalid carried item", this.player.getName().getString());
      }
   }

   public void handleChat(ServerboundChatPacket serverboundChatPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundChatPacket, this, (ServerLevel)this.player.getLevel());
      if(this.player.getChatVisibility() == ChatVisiblity.HIDDEN) {
         this.send(new ClientboundChatPacket((new TranslatableComponent("chat.cannotSend", new Object[0])).withStyle(ChatFormatting.RED)));
      } else {
         this.player.resetLastActionTime();
         String var2 = serverboundChatPacket.getMessage();
         var2 = StringUtils.normalizeSpace(var2);

         for(int var3 = 0; var3 < var2.length(); ++var3) {
            if(!SharedConstants.isAllowedChatCharacter(var2.charAt(var3))) {
               this.disconnect(new TranslatableComponent("multiplayer.disconnect.illegal_characters", new Object[0]));
               return;
            }
         }

         if(var2.startsWith("/")) {
            this.handleCommand(var2);
         } else {
            Component var3 = new TranslatableComponent("chat.type.text", new Object[]{this.player.getDisplayName(), var2});
            this.server.getPlayerList().broadcastMessage(var3, false);
         }

         this.chatSpamTickCount += 20;
         if(this.chatSpamTickCount > 200 && !this.server.getPlayerList().isOp(this.player.getGameProfile())) {
            this.disconnect(new TranslatableComponent("disconnect.spam", new Object[0]));
         }

      }
   }

   private void handleCommand(String string) {
      this.server.getCommands().performCommand(this.player.createCommandSourceStack(), string);
   }

   public void handleAnimate(ServerboundSwingPacket serverboundSwingPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundSwingPacket, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      this.player.swing(serverboundSwingPacket.getHand());
   }

   public void handlePlayerCommand(ServerboundPlayerCommandPacket serverboundPlayerCommandPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundPlayerCommandPacket, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      switch(serverboundPlayerCommandPacket.getAction()) {
      case START_SNEAKING:
         this.player.setSneaking(true);
         break;
      case STOP_SNEAKING:
         this.player.setSneaking(false);
         break;
      case START_SPRINTING:
         this.player.setSprinting(true);
         break;
      case STOP_SPRINTING:
         this.player.setSprinting(false);
         break;
      case STOP_SLEEPING:
         if(this.player.isSleeping()) {
            this.player.stopSleepInBed(false, true, true);
            this.awaitingPositionFromClient = new Vec3(this.player.x, this.player.y, this.player.z);
         }
         break;
      case START_RIDING_JUMP:
         if(this.player.getVehicle() instanceof PlayerRideableJumping) {
            PlayerRideableJumping var2 = (PlayerRideableJumping)this.player.getVehicle();
            int var3 = serverboundPlayerCommandPacket.getData();
            if(var2.canJump() && var3 > 0) {
               var2.handleStartJump(var3);
            }
         }
         break;
      case STOP_RIDING_JUMP:
         if(this.player.getVehicle() instanceof PlayerRideableJumping) {
            PlayerRideableJumping var2 = (PlayerRideableJumping)this.player.getVehicle();
            var2.handleStopJump();
         }
         break;
      case OPEN_INVENTORY:
         if(this.player.getVehicle() instanceof AbstractHorse) {
            ((AbstractHorse)this.player.getVehicle()).openInventory(this.player);
         }
         break;
      case START_FALL_FLYING:
         if(!this.player.onGround && this.player.getDeltaMovement().y < 0.0D && !this.player.isFallFlying() && !this.player.isInWater()) {
            ItemStack var2 = this.player.getItemBySlot(EquipmentSlot.CHEST);
            if(var2.getItem() == Items.ELYTRA && ElytraItem.isFlyEnabled(var2)) {
               this.player.startFallFlying();
            }
         } else {
            this.player.stopFallFlying();
         }
         break;
      default:
         throw new IllegalArgumentException("Invalid client command!");
      }

   }

   public void handleInteract(ServerboundInteractPacket serverboundInteractPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundInteractPacket, this, (ServerLevel)this.player.getLevel());
      ServerLevel var2 = this.server.getLevel(this.player.dimension);
      Entity var3 = serverboundInteractPacket.getTarget(var2);
      this.player.resetLastActionTime();
      if(var3 != null) {
         boolean var4 = this.player.canSee(var3);
         double var5 = 36.0D;
         if(!var4) {
            var5 = 9.0D;
         }

         if(this.player.distanceToSqr(var3) < var5) {
            if(serverboundInteractPacket.getAction() == ServerboundInteractPacket.Action.INTERACT) {
               InteractionHand var7 = serverboundInteractPacket.getHand();
               this.player.interactOn(var3, var7);
            } else if(serverboundInteractPacket.getAction() == ServerboundInteractPacket.Action.INTERACT_AT) {
               InteractionHand var7 = serverboundInteractPacket.getHand();
               var3.interactAt(this.player, serverboundInteractPacket.getLocation(), var7);
            } else if(serverboundInteractPacket.getAction() == ServerboundInteractPacket.Action.ATTACK) {
               if(var3 instanceof ItemEntity || var3 instanceof ExperienceOrb || var3 instanceof AbstractArrow || var3 == this.player) {
                  this.disconnect(new TranslatableComponent("multiplayer.disconnect.invalid_entity_attacked", new Object[0]));
                  this.server.warn("Player " + this.player.getName().getString() + " tried to attack an invalid entity");
                  return;
               }

               this.player.attack(var3);
            }
         }
      }

   }

   public void handleClientCommand(ServerboundClientCommandPacket serverboundClientCommandPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundClientCommandPacket, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      ServerboundClientCommandPacket.Action var2 = serverboundClientCommandPacket.getAction();
      switch(var2) {
      case PERFORM_RESPAWN:
         if(this.player.wonGame) {
            this.player.wonGame = false;
            this.player = this.server.getPlayerList().respawn(this.player, DimensionType.OVERWORLD, true);
            CriteriaTriggers.CHANGED_DIMENSION.trigger(this.player, DimensionType.THE_END, DimensionType.OVERWORLD);
         } else {
            if(this.player.getHealth() > 0.0F) {
               return;
            }

            this.player = this.server.getPlayerList().respawn(this.player, DimensionType.OVERWORLD, false);
            if(this.server.isHardcore()) {
               this.player.setGameMode(GameType.SPECTATOR);
               ((GameRules.BooleanValue)this.player.getLevel().getGameRules().getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS)).set(false, this.server);
            }
         }
         break;
      case REQUEST_STATS:
         this.player.getStats().sendStats(this.player);
      }

   }

   public void handleContainerClose(ServerboundContainerClosePacket serverboundContainerClosePacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundContainerClosePacket, this, (ServerLevel)this.player.getLevel());
      this.player.doCloseContainer();
   }

   public void handleContainerClick(ServerboundContainerClickPacket serverboundContainerClickPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundContainerClickPacket, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      if(this.player.containerMenu.containerId == serverboundContainerClickPacket.getContainerId() && this.player.containerMenu.isSynched(this.player)) {
         if(this.player.isSpectator()) {
            NonNullList<ItemStack> var2 = NonNullList.create();

            for(int var3 = 0; var3 < this.player.containerMenu.slots.size(); ++var3) {
               var2.add(((Slot)this.player.containerMenu.slots.get(var3)).getItem());
            }

            this.player.refreshContainer(this.player.containerMenu, var2);
         } else {
            ItemStack var2 = this.player.containerMenu.clicked(serverboundContainerClickPacket.getSlotNum(), serverboundContainerClickPacket.getButtonNum(), serverboundContainerClickPacket.getClickType(), this.player);
            if(ItemStack.matches(serverboundContainerClickPacket.getItem(), var2)) {
               this.player.connection.send(new ClientboundContainerAckPacket(serverboundContainerClickPacket.getContainerId(), serverboundContainerClickPacket.getUid(), true));
               this.player.ignoreSlotUpdateHack = true;
               this.player.containerMenu.broadcastChanges();
               this.player.broadcastCarriedItem();
               this.player.ignoreSlotUpdateHack = false;
            } else {
               this.expectedAcks.put(this.player.containerMenu.containerId, serverboundContainerClickPacket.getUid());
               this.player.connection.send(new ClientboundContainerAckPacket(serverboundContainerClickPacket.getContainerId(), serverboundContainerClickPacket.getUid(), false));
               this.player.containerMenu.setSynched(this.player, false);
               NonNullList<ItemStack> var3 = NonNullList.create();

               for(int var4 = 0; var4 < this.player.containerMenu.slots.size(); ++var4) {
                  ItemStack var5 = ((Slot)this.player.containerMenu.slots.get(var4)).getItem();
                  var3.add(var5.isEmpty()?ItemStack.EMPTY:var5);
               }

               this.player.refreshContainer(this.player.containerMenu, var3);
            }
         }
      }

   }

   public void handlePlaceRecipe(ServerboundPlaceRecipePacket serverboundPlaceRecipePacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundPlaceRecipePacket, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      if(!this.player.isSpectator() && this.player.containerMenu.containerId == serverboundPlaceRecipePacket.getContainerId() && this.player.containerMenu.isSynched(this.player) && this.player.containerMenu instanceof RecipeBookMenu) {
         this.server.getRecipeManager().byKey(serverboundPlaceRecipePacket.getRecipe()).ifPresent((recipe) -> {
            ((RecipeBookMenu)this.player.containerMenu).handlePlacement(serverboundPlaceRecipePacket.isShiftDown(), recipe, this.player);
         });
      }
   }

   public void handleContainerButtonClick(ServerboundContainerButtonClickPacket serverboundContainerButtonClickPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundContainerButtonClickPacket, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      if(this.player.containerMenu.containerId == serverboundContainerButtonClickPacket.getContainerId() && this.player.containerMenu.isSynched(this.player) && !this.player.isSpectator()) {
         this.player.containerMenu.clickMenuButton(this.player, serverboundContainerButtonClickPacket.getButtonId());
         this.player.containerMenu.broadcastChanges();
      }

   }

   public void handleSetCreativeModeSlot(ServerboundSetCreativeModeSlotPacket serverboundSetCreativeModeSlotPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundSetCreativeModeSlotPacket, this, (ServerLevel)this.player.getLevel());
      if(this.player.gameMode.isCreative()) {
         boolean var2 = serverboundSetCreativeModeSlotPacket.getSlotNum() < 0;
         ItemStack var3 = serverboundSetCreativeModeSlotPacket.getItem();
         CompoundTag var4 = var3.getTagElement("BlockEntityTag");
         if(!var3.isEmpty() && var4 != null && var4.contains("x") && var4.contains("y") && var4.contains("z")) {
            BlockPos var5 = new BlockPos(var4.getInt("x"), var4.getInt("y"), var4.getInt("z"));
            BlockEntity var6 = this.player.level.getBlockEntity(var5);
            if(var6 != null) {
               CompoundTag var7 = var6.save(new CompoundTag());
               var7.remove("x");
               var7.remove("y");
               var7.remove("z");
               var3.addTagElement("BlockEntityTag", var7);
            }
         }

         boolean var5 = serverboundSetCreativeModeSlotPacket.getSlotNum() >= 1 && serverboundSetCreativeModeSlotPacket.getSlotNum() <= 45;
         boolean var6 = var3.isEmpty() || var3.getDamageValue() >= 0 && var3.getCount() <= 64 && !var3.isEmpty();
         if(var5 && var6) {
            if(var3.isEmpty()) {
               this.player.inventoryMenu.setItem(serverboundSetCreativeModeSlotPacket.getSlotNum(), ItemStack.EMPTY);
            } else {
               this.player.inventoryMenu.setItem(serverboundSetCreativeModeSlotPacket.getSlotNum(), var3);
            }

            this.player.inventoryMenu.setSynched(this.player, true);
            this.player.inventoryMenu.broadcastChanges();
         } else if(var2 && var6 && this.dropSpamTickCount < 200) {
            this.dropSpamTickCount += 20;
            ItemEntity var7 = this.player.drop(var3, true);
            if(var7 != null) {
               var7.setShortLifeTime();
            }
         }
      }

   }

   public void handleContainerAck(ServerboundContainerAckPacket serverboundContainerAckPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundContainerAckPacket, this, (ServerLevel)this.player.getLevel());
      int var2 = this.player.containerMenu.containerId;
      if(var2 == serverboundContainerAckPacket.getContainerId() && this.expectedAcks.getOrDefault(var2, (short)(serverboundContainerAckPacket.getUid() + 1)) == serverboundContainerAckPacket.getUid() && !this.player.containerMenu.isSynched(this.player) && !this.player.isSpectator()) {
         this.player.containerMenu.setSynched(this.player, true);
      }

   }

   public void handleSignUpdate(ServerboundSignUpdatePacket serverboundSignUpdatePacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundSignUpdatePacket, this, (ServerLevel)this.player.getLevel());
      this.player.resetLastActionTime();
      ServerLevel var2 = this.server.getLevel(this.player.dimension);
      BlockPos var3 = serverboundSignUpdatePacket.getPos();
      if(var2.hasChunkAt(var3)) {
         BlockState var4 = var2.getBlockState(var3);
         BlockEntity var5 = var2.getBlockEntity(var3);
         if(!(var5 instanceof SignBlockEntity)) {
            return;
         }

         SignBlockEntity var6 = (SignBlockEntity)var5;
         if(!var6.isEditable() || var6.getPlayerWhoMayEdit() != this.player) {
            this.server.warn("Player " + this.player.getName().getString() + " just tried to change non-editable sign");
            return;
         }

         String[] vars7 = serverboundSignUpdatePacket.getLines();

         for(int var8 = 0; var8 < vars7.length; ++var8) {
            var6.setMessage(var8, new TextComponent(ChatFormatting.stripFormatting(vars7[var8])));
         }

         var6.setChanged();
         var2.sendBlockUpdated(var3, var4, var4, 3);
      }

   }

   public void handleKeepAlive(ServerboundKeepAlivePacket serverboundKeepAlivePacket) {
      if(this.keepAlivePending && serverboundKeepAlivePacket.getId() == this.keepAliveChallenge) {
         int var2 = (int)(Util.getMillis() - this.keepAliveTime);
         this.player.latency = (this.player.latency * 3 + var2) / 4;
         this.keepAlivePending = false;
      } else if(!this.isSingleplayerOwner()) {
         this.disconnect(new TranslatableComponent("disconnect.timeout", new Object[0]));
      }

   }

   public void handlePlayerAbilities(ServerboundPlayerAbilitiesPacket serverboundPlayerAbilitiesPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundPlayerAbilitiesPacket, this, (ServerLevel)this.player.getLevel());
      this.player.abilities.flying = serverboundPlayerAbilitiesPacket.isFlying() && this.player.abilities.mayfly;
   }

   public void handleClientInformation(ServerboundClientInformationPacket serverboundClientInformationPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundClientInformationPacket, this, (ServerLevel)this.player.getLevel());
      this.player.updateOptions(serverboundClientInformationPacket);
   }

   public void handleCustomPayload(ServerboundCustomPayloadPacket serverboundCustomPayloadPacket) {
   }

   public void handleChangeDifficulty(ServerboundChangeDifficultyPacket serverboundChangeDifficultyPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundChangeDifficultyPacket, this, (ServerLevel)this.player.getLevel());
      if(this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
         this.server.setDifficulty(serverboundChangeDifficultyPacket.getDifficulty(), false);
      }
   }

   public void handleLockDifficulty(ServerboundLockDifficultyPacket serverboundLockDifficultyPacket) {
      PacketUtils.ensureRunningOnSameThread(serverboundLockDifficultyPacket, this, (ServerLevel)this.player.getLevel());
      if(this.player.hasPermissions(2) || this.isSingleplayerOwner()) {
         this.server.setDifficultyLocked(serverboundLockDifficultyPacket.isLocked());
      }
   }
}
