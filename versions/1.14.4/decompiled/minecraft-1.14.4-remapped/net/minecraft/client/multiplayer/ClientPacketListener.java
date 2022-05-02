package net.minecraft.client.multiplayer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.DebugQueryHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.DemoIntroScreen;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.achievement.StatsUpdateListener;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.HorseInventoryScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.particle.ItemPickupParticle;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.debug.GoalSelectorDebugRenderer;
import net.minecraft.client.renderer.debug.NeighborsUpdateRenderer;
import net.minecraft.client.renderer.debug.VillageDebugRenderer;
import net.minecraft.client.renderer.debug.WorldGenAttemptRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.GuardianAttackSoundInstance;
import net.minecraft.client.resources.sounds.MinecartSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.PositionImpl;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddExperienceOrbPacket;
import net.minecraft.network.protocol.game.ClientboundAddGlobalEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundAddPaintingPacket;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBlocksUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundContainerAckPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundCooldownPacket;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRecipePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquippedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.network.protocol.game.ClientboundSetSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.network.protocol.game.ClientboundTagQueryPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundContainerAckPacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Mth;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.fishing.FishingHook;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class ClientPacketListener implements ClientGamePacketListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Connection connection;
   private final GameProfile localGameProfile;
   private final Screen callbackScreen;
   private Minecraft minecraft;
   private MultiPlayerLevel level;
   private boolean started;
   private final Map playerInfoMap = Maps.newHashMap();
   private final ClientAdvancements advancements;
   private final ClientSuggestionProvider suggestionsProvider;
   private TagManager tags = new TagManager();
   private final DebugQueryHandler debugQueryHandler = new DebugQueryHandler(this);
   private int serverChunkRadius = 3;
   private final Random random = new Random();
   private CommandDispatcher commands = new CommandDispatcher();
   private final RecipeManager recipeManager = new RecipeManager();
   private final UUID id = UUID.randomUUID();

   public ClientPacketListener(Minecraft minecraft, Screen callbackScreen, Connection connection, GameProfile localGameProfile) {
      this.minecraft = minecraft;
      this.callbackScreen = callbackScreen;
      this.connection = connection;
      this.localGameProfile = localGameProfile;
      this.advancements = new ClientAdvancements(minecraft);
      this.suggestionsProvider = new ClientSuggestionProvider(this, minecraft);
   }

   public ClientSuggestionProvider getSuggestionsProvider() {
      return this.suggestionsProvider;
   }

   public void cleanup() {
      this.level = null;
   }

   public RecipeManager getRecipeManager() {
      return this.recipeManager;
   }

   public void handleLogin(ClientboundLoginPacket clientboundLoginPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundLoginPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.gameMode = new MultiPlayerGameMode(this.minecraft, this);
      this.serverChunkRadius = clientboundLoginPacket.getChunkRadius();
      this.level = new MultiPlayerLevel(this, new LevelSettings(0L, clientboundLoginPacket.getGameType(), false, clientboundLoginPacket.isHardcore(), clientboundLoginPacket.getLevelType()), clientboundLoginPacket.getDimension(), this.serverChunkRadius, this.minecraft.getProfiler(), this.minecraft.levelRenderer);
      this.minecraft.setLevel(this.level);
      if(this.minecraft.player == null) {
         this.minecraft.player = this.minecraft.gameMode.createPlayer(this.level, new StatsCounter(), new ClientRecipeBook(this.level.getRecipeManager()));
         this.minecraft.player.yRot = -180.0F;
         if(this.minecraft.getSingleplayerServer() != null) {
            this.minecraft.getSingleplayerServer().setUUID(this.minecraft.player.getUUID());
         }
      }

      this.minecraft.debugRenderer.clear();
      this.minecraft.player.resetPos();
      int var2 = clientboundLoginPacket.getPlayerId();
      this.level.addPlayer(var2, this.minecraft.player);
      this.minecraft.player.input = new KeyboardInput(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(this.minecraft.player);
      this.minecraft.cameraEntity = this.minecraft.player;
      this.minecraft.player.dimension = clientboundLoginPacket.getDimension();
      this.minecraft.setScreen(new ReceivingLevelScreen());
      this.minecraft.player.setId(var2);
      this.minecraft.player.setReducedDebugInfo(clientboundLoginPacket.isReducedDebugInfo());
      this.minecraft.gameMode.setLocalMode(clientboundLoginPacket.getGameType());
      this.minecraft.options.broadcastOptions();
      this.connection.send(new ServerboundCustomPayloadPacket(ServerboundCustomPayloadPacket.BRAND, (new FriendlyByteBuf(Unpooled.buffer())).writeUtf(ClientBrandRetriever.getClientModName())));
      this.minecraft.getGame().onStartGameSession();
   }

   public void handleAddEntity(ClientboundAddEntityPacket clientboundAddEntityPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundAddEntityPacket, this, (BlockableEventLoop)this.minecraft);
      double var2 = clientboundAddEntityPacket.getX();
      double var4 = clientboundAddEntityPacket.getY();
      double var6 = clientboundAddEntityPacket.getZ();
      EntityType<?> var9 = clientboundAddEntityPacket.getType();
      Entity var8;
      if(var9 == EntityType.CHEST_MINECART) {
         var8 = new MinecartChest(this.level, var2, var4, var6);
      } else if(var9 == EntityType.FURNACE_MINECART) {
         var8 = new MinecartFurnace(this.level, var2, var4, var6);
      } else if(var9 == EntityType.TNT_MINECART) {
         var8 = new MinecartTNT(this.level, var2, var4, var6);
      } else if(var9 == EntityType.SPAWNER_MINECART) {
         var8 = new MinecartSpawner(this.level, var2, var4, var6);
      } else if(var9 == EntityType.HOPPER_MINECART) {
         var8 = new MinecartHopper(this.level, var2, var4, var6);
      } else if(var9 == EntityType.COMMAND_BLOCK_MINECART) {
         var8 = new MinecartCommandBlock(this.level, var2, var4, var6);
      } else if(var9 == EntityType.MINECART) {
         var8 = new Minecart(this.level, var2, var4, var6);
      } else if(var9 == EntityType.FISHING_BOBBER) {
         Entity var10 = this.level.getEntity(clientboundAddEntityPacket.getData());
         if(var10 instanceof Player) {
            var8 = new FishingHook(this.level, (Player)var10, var2, var4, var6);
         } else {
            var8 = null;
         }
      } else if(var9 == EntityType.ARROW) {
         var8 = new Arrow(this.level, var2, var4, var6);
         Entity var10 = this.level.getEntity(clientboundAddEntityPacket.getData());
         if(var10 != null) {
            ((AbstractArrow)var8).setOwner(var10);
         }
      } else if(var9 == EntityType.SPECTRAL_ARROW) {
         var8 = new SpectralArrow(this.level, var2, var4, var6);
         Entity var10 = this.level.getEntity(clientboundAddEntityPacket.getData());
         if(var10 != null) {
            ((AbstractArrow)var8).setOwner(var10);
         }
      } else if(var9 == EntityType.TRIDENT) {
         var8 = new ThrownTrident(this.level, var2, var4, var6);
         Entity var10 = this.level.getEntity(clientboundAddEntityPacket.getData());
         if(var10 != null) {
            ((AbstractArrow)var8).setOwner(var10);
         }
      } else if(var9 == EntityType.SNOWBALL) {
         var8 = new Snowball(this.level, var2, var4, var6);
      } else if(var9 == EntityType.LLAMA_SPIT) {
         var8 = new LlamaSpit(this.level, var2, var4, var6, clientboundAddEntityPacket.getXa(), clientboundAddEntityPacket.getYa(), clientboundAddEntityPacket.getZa());
      } else if(var9 == EntityType.ITEM_FRAME) {
         var8 = new ItemFrame(this.level, new BlockPos(var2, var4, var6), Direction.from3DDataValue(clientboundAddEntityPacket.getData()));
      } else if(var9 == EntityType.LEASH_KNOT) {
         var8 = new LeashFenceKnotEntity(this.level, new BlockPos(var2, var4, var6));
      } else if(var9 == EntityType.ENDER_PEARL) {
         var8 = new ThrownEnderpearl(this.level, var2, var4, var6);
      } else if(var9 == EntityType.EYE_OF_ENDER) {
         var8 = new EyeOfEnder(this.level, var2, var4, var6);
      } else if(var9 == EntityType.FIREWORK_ROCKET) {
         var8 = new FireworkRocketEntity(this.level, var2, var4, var6, ItemStack.EMPTY);
      } else if(var9 == EntityType.FIREBALL) {
         var8 = new LargeFireball(this.level, var2, var4, var6, clientboundAddEntityPacket.getXa(), clientboundAddEntityPacket.getYa(), clientboundAddEntityPacket.getZa());
      } else if(var9 == EntityType.DRAGON_FIREBALL) {
         var8 = new DragonFireball(this.level, var2, var4, var6, clientboundAddEntityPacket.getXa(), clientboundAddEntityPacket.getYa(), clientboundAddEntityPacket.getZa());
      } else if(var9 == EntityType.SMALL_FIREBALL) {
         var8 = new SmallFireball(this.level, var2, var4, var6, clientboundAddEntityPacket.getXa(), clientboundAddEntityPacket.getYa(), clientboundAddEntityPacket.getZa());
      } else if(var9 == EntityType.WITHER_SKULL) {
         var8 = new WitherSkull(this.level, var2, var4, var6, clientboundAddEntityPacket.getXa(), clientboundAddEntityPacket.getYa(), clientboundAddEntityPacket.getZa());
      } else if(var9 == EntityType.SHULKER_BULLET) {
         var8 = new ShulkerBullet(this.level, var2, var4, var6, clientboundAddEntityPacket.getXa(), clientboundAddEntityPacket.getYa(), clientboundAddEntityPacket.getZa());
      } else if(var9 == EntityType.EGG) {
         var8 = new ThrownEgg(this.level, var2, var4, var6);
      } else if(var9 == EntityType.EVOKER_FANGS) {
         var8 = new EvokerFangs(this.level, var2, var4, var6, 0.0F, 0, (LivingEntity)null);
      } else if(var9 == EntityType.POTION) {
         var8 = new ThrownPotion(this.level, var2, var4, var6);
      } else if(var9 == EntityType.EXPERIENCE_BOTTLE) {
         var8 = new ThrownExperienceBottle(this.level, var2, var4, var6);
      } else if(var9 == EntityType.BOAT) {
         var8 = new Boat(this.level, var2, var4, var6);
      } else if(var9 == EntityType.TNT) {
         var8 = new PrimedTnt(this.level, var2, var4, var6, (LivingEntity)null);
      } else if(var9 == EntityType.ARMOR_STAND) {
         var8 = new ArmorStand(this.level, var2, var4, var6);
      } else if(var9 == EntityType.END_CRYSTAL) {
         var8 = new EndCrystal(this.level, var2, var4, var6);
      } else if(var9 == EntityType.ITEM) {
         var8 = new ItemEntity(this.level, var2, var4, var6);
      } else if(var9 == EntityType.FALLING_BLOCK) {
         var8 = new FallingBlockEntity(this.level, var2, var4, var6, Block.stateById(clientboundAddEntityPacket.getData()));
      } else if(var9 == EntityType.AREA_EFFECT_CLOUD) {
         var8 = new AreaEffectCloud(this.level, var2, var4, var6);
      } else {
         var8 = null;
      }

      if(var8 != null) {
         int var10 = clientboundAddEntityPacket.getId();
         var8.setPacketCoordinates(var2, var4, var6);
         var8.xRot = (float)(clientboundAddEntityPacket.getxRot() * 360) / 256.0F;
         var8.yRot = (float)(clientboundAddEntityPacket.getyRot() * 360) / 256.0F;
         var8.setId(var10);
         var8.setUUID(clientboundAddEntityPacket.getUUID());
         this.level.putNonPlayerEntity(var10, var8);
         if(var8 instanceof AbstractMinecart) {
            this.minecraft.getSoundManager().play(new MinecartSoundInstance((AbstractMinecart)var8));
         }
      }

   }

   public void handleAddExperienceOrb(ClientboundAddExperienceOrbPacket clientboundAddExperienceOrbPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundAddExperienceOrbPacket, this, (BlockableEventLoop)this.minecraft);
      double var2 = clientboundAddExperienceOrbPacket.getX();
      double var4 = clientboundAddExperienceOrbPacket.getY();
      double var6 = clientboundAddExperienceOrbPacket.getZ();
      Entity var8 = new ExperienceOrb(this.level, var2, var4, var6, clientboundAddExperienceOrbPacket.getValue());
      var8.setPacketCoordinates(var2, var4, var6);
      var8.yRot = 0.0F;
      var8.xRot = 0.0F;
      var8.setId(clientboundAddExperienceOrbPacket.getId());
      this.level.putNonPlayerEntity(clientboundAddExperienceOrbPacket.getId(), var8);
   }

   public void handleAddGlobalEntity(ClientboundAddGlobalEntityPacket clientboundAddGlobalEntityPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundAddGlobalEntityPacket, this, (BlockableEventLoop)this.minecraft);
      double var2 = clientboundAddGlobalEntityPacket.getX();
      double var4 = clientboundAddGlobalEntityPacket.getY();
      double var6 = clientboundAddGlobalEntityPacket.getZ();
      if(clientboundAddGlobalEntityPacket.getType() == 1) {
         LightningBolt var8 = new LightningBolt(this.level, var2, var4, var6, false);
         var8.setPacketCoordinates(var2, var4, var6);
         var8.yRot = 0.0F;
         var8.xRot = 0.0F;
         var8.setId(clientboundAddGlobalEntityPacket.getId());
         this.level.addLightning(var8);
      }

   }

   public void handleAddPainting(ClientboundAddPaintingPacket clientboundAddPaintingPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundAddPaintingPacket, this, (BlockableEventLoop)this.minecraft);
      Painting var2 = new Painting(this.level, clientboundAddPaintingPacket.getPos(), clientboundAddPaintingPacket.getDirection(), clientboundAddPaintingPacket.getMotive());
      var2.setId(clientboundAddPaintingPacket.getId());
      var2.setUUID(clientboundAddPaintingPacket.getUUID());
      this.level.putNonPlayerEntity(clientboundAddPaintingPacket.getId(), var2);
   }

   public void handleSetEntityMotion(ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetEntityMotionPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundSetEntityMotionPacket.getId());
      if(var2 != null) {
         var2.lerpMotion((double)clientboundSetEntityMotionPacket.getXa() / 8000.0D, (double)clientboundSetEntityMotionPacket.getYa() / 8000.0D, (double)clientboundSetEntityMotionPacket.getZa() / 8000.0D);
      }
   }

   public void handleSetEntityData(ClientboundSetEntityDataPacket clientboundSetEntityDataPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetEntityDataPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundSetEntityDataPacket.getId());
      if(var2 != null && clientboundSetEntityDataPacket.getUnpackedData() != null) {
         var2.getEntityData().assignValues(clientboundSetEntityDataPacket.getUnpackedData());
      }

   }

   public void handleAddPlayer(ClientboundAddPlayerPacket clientboundAddPlayerPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundAddPlayerPacket, this, (BlockableEventLoop)this.minecraft);
      double var2 = clientboundAddPlayerPacket.getX();
      double var4 = clientboundAddPlayerPacket.getY();
      double var6 = clientboundAddPlayerPacket.getZ();
      float var8 = (float)(clientboundAddPlayerPacket.getyRot() * 360) / 256.0F;
      float var9 = (float)(clientboundAddPlayerPacket.getxRot() * 360) / 256.0F;
      int var10 = clientboundAddPlayerPacket.getEntityId();
      RemotePlayer var11 = new RemotePlayer(this.minecraft.level, this.getPlayerInfo(clientboundAddPlayerPacket.getPlayerId()).getProfile());
      var11.setId(var10);
      var11.xo = var2;
      var11.xOld = var2;
      var11.yo = var4;
      var11.yOld = var4;
      var11.zo = var6;
      var11.zOld = var6;
      var11.setPacketCoordinates(var2, var4, var6);
      var11.absMoveTo(var2, var4, var6, var8, var9);
      this.level.addPlayer(var10, var11);
      List<SynchedEntityData.DataItem<?>> var12 = clientboundAddPlayerPacket.getUnpackedData();
      if(var12 != null) {
         var11.getEntityData().assignValues(var12);
      }

   }

   public void handleTeleportEntity(ClientboundTeleportEntityPacket clientboundTeleportEntityPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundTeleportEntityPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundTeleportEntityPacket.getId());
      if(var2 != null) {
         double var3 = clientboundTeleportEntityPacket.getX();
         double var5 = clientboundTeleportEntityPacket.getY();
         double var7 = clientboundTeleportEntityPacket.getZ();
         var2.setPacketCoordinates(var3, var5, var7);
         if(!var2.isControlledByLocalInstance()) {
            float var9 = (float)(clientboundTeleportEntityPacket.getyRot() * 360) / 256.0F;
            float var10 = (float)(clientboundTeleportEntityPacket.getxRot() * 360) / 256.0F;
            if(Math.abs(var2.x - var3) < 0.03125D && Math.abs(var2.y - var5) < 0.015625D && Math.abs(var2.z - var7) < 0.03125D) {
               var2.lerpTo(var2.x, var2.y, var2.z, var9, var10, 0, true);
            } else {
               var2.lerpTo(var3, var5, var7, var9, var10, 3, true);
            }

            var2.onGround = clientboundTeleportEntityPacket.isOnGround();
         }

      }
   }

   public void handleSetCarriedItem(ClientboundSetCarriedItemPacket clientboundSetCarriedItemPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetCarriedItemPacket, this, (BlockableEventLoop)this.minecraft);
      if(Inventory.isHotbarSlot(clientboundSetCarriedItemPacket.getSlot())) {
         this.minecraft.player.inventory.selected = clientboundSetCarriedItemPacket.getSlot();
      }

   }

   public void handleMoveEntity(ClientboundMoveEntityPacket clientboundMoveEntityPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundMoveEntityPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = clientboundMoveEntityPacket.getEntity(this.level);
      if(var2 != null) {
         var2.xp += (long)clientboundMoveEntityPacket.getXa();
         var2.yp += (long)clientboundMoveEntityPacket.getYa();
         var2.zp += (long)clientboundMoveEntityPacket.getZa();
         Vec3 var3 = ClientboundMoveEntityPacket.packetToEntity(var2.xp, var2.yp, var2.zp);
         if(!var2.isControlledByLocalInstance()) {
            float var4 = clientboundMoveEntityPacket.hasRotation()?(float)(clientboundMoveEntityPacket.getyRot() * 360) / 256.0F:var2.yRot;
            float var5 = clientboundMoveEntityPacket.hasRotation()?(float)(clientboundMoveEntityPacket.getxRot() * 360) / 256.0F:var2.xRot;
            var2.lerpTo(var3.x, var3.y, var3.z, var4, var5, 3, false);
            var2.onGround = clientboundMoveEntityPacket.isOnGround();
         }

      }
   }

   public void handleRotateMob(ClientboundRotateHeadPacket clientboundRotateHeadPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundRotateHeadPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = clientboundRotateHeadPacket.getEntity(this.level);
      if(var2 != null) {
         float var3 = (float)(clientboundRotateHeadPacket.getYHeadRot() * 360) / 256.0F;
         var2.lerpHeadTo(var3, 3);
      }
   }

   public void handleRemoveEntity(ClientboundRemoveEntitiesPacket clientboundRemoveEntitiesPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundRemoveEntitiesPacket, this, (BlockableEventLoop)this.minecraft);

      for(int var2 = 0; var2 < clientboundRemoveEntitiesPacket.getEntityIds().length; ++var2) {
         int var3 = clientboundRemoveEntitiesPacket.getEntityIds()[var2];
         this.level.removeEntity(var3);
      }

   }

   public void handleMovePlayer(ClientboundPlayerPositionPacket clientboundPlayerPositionPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundPlayerPositionPacket, this, (BlockableEventLoop)this.minecraft);
      Player var2 = this.minecraft.player;
      double var3 = clientboundPlayerPositionPacket.getX();
      double var5 = clientboundPlayerPositionPacket.getY();
      double var7 = clientboundPlayerPositionPacket.getZ();
      float var9 = clientboundPlayerPositionPacket.getYRot();
      float var10 = clientboundPlayerPositionPacket.getXRot();
      Vec3 var11 = var2.getDeltaMovement();
      double var12 = var11.x;
      double var14 = var11.y;
      double var16 = var11.z;
      if(clientboundPlayerPositionPacket.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.X)) {
         var2.xOld += var3;
         var3 += var2.x;
      } else {
         var2.xOld = var3;
         var12 = 0.0D;
      }

      if(clientboundPlayerPositionPacket.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.Y)) {
         var2.yOld += var5;
         var5 += var2.y;
      } else {
         var2.yOld = var5;
         var14 = 0.0D;
      }

      if(clientboundPlayerPositionPacket.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.Z)) {
         var2.zOld += var7;
         var7 += var2.z;
      } else {
         var2.zOld = var7;
         var16 = 0.0D;
      }

      var2.setDeltaMovement(var12, var14, var16);
      if(clientboundPlayerPositionPacket.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT)) {
         var10 += var2.xRot;
      }

      if(clientboundPlayerPositionPacket.getRelativeArguments().contains(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT)) {
         var9 += var2.yRot;
      }

      var2.absMoveTo(var3, var5, var7, var9, var10);
      this.connection.send(new ServerboundAcceptTeleportationPacket(clientboundPlayerPositionPacket.getId()));
      this.connection.send(new ServerboundMovePlayerPacket.PosRot(var2.x, var2.getBoundingBox().minY, var2.z, var2.yRot, var2.xRot, false));
      if(!this.started) {
         this.minecraft.player.xo = this.minecraft.player.x;
         this.minecraft.player.yo = this.minecraft.player.y;
         this.minecraft.player.zo = this.minecraft.player.z;
         this.started = true;
         this.minecraft.setScreen((Screen)null);
      }

   }

   public void handleChunkBlocksUpdate(ClientboundChunkBlocksUpdatePacket clientboundChunkBlocksUpdatePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundChunkBlocksUpdatePacket, this, (BlockableEventLoop)this.minecraft);

      for(ClientboundChunkBlocksUpdatePacket.BlockUpdate var5 : clientboundChunkBlocksUpdatePacket.getUpdates()) {
         this.level.setKnownState(var5.getPos(), var5.getBlock());
      }

   }

   public void handleLevelChunk(ClientboundLevelChunkPacket clientboundLevelChunkPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundLevelChunkPacket, this, (BlockableEventLoop)this.minecraft);
      int var2 = clientboundLevelChunkPacket.getX();
      int var3 = clientboundLevelChunkPacket.getZ();
      LevelChunk var4 = this.level.getChunkSource().replaceWithPacketData(this.level, var2, var3, clientboundLevelChunkPacket.getReadBuffer(), clientboundLevelChunkPacket.getHeightmaps(), clientboundLevelChunkPacket.getAvailableSections(), clientboundLevelChunkPacket.isFullChunk());
      if(var4 != null && clientboundLevelChunkPacket.isFullChunk()) {
         this.level.reAddEntitiesToChunk(var4);
      }

      for(int var5 = 0; var5 < 16; ++var5) {
         this.level.setSectionDirtyWithNeighbors(var2, var5, var3);
      }

      for(CompoundTag var6 : clientboundLevelChunkPacket.getBlockEntitiesTags()) {
         BlockPos var7 = new BlockPos(var6.getInt("x"), var6.getInt("y"), var6.getInt("z"));
         BlockEntity var8 = this.level.getBlockEntity(var7);
         if(var8 != null) {
            var8.load(var6);
         }
      }

   }

   public void handleForgetLevelChunk(ClientboundForgetLevelChunkPacket clientboundForgetLevelChunkPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundForgetLevelChunkPacket, this, (BlockableEventLoop)this.minecraft);
      int var2 = clientboundForgetLevelChunkPacket.getX();
      int var3 = clientboundForgetLevelChunkPacket.getZ();
      ClientChunkCache var4 = this.level.getChunkSource();
      var4.drop(var2, var3);
      LevelLightEngine var5 = var4.getLightEngine();

      for(int var6 = 0; var6 < 16; ++var6) {
         this.level.setSectionDirtyWithNeighbors(var2, var6, var3);
         var5.updateSectionStatus(SectionPos.of(var2, var6, var3), true);
      }

      var5.enableLightSources(new ChunkPos(var2, var3), false);
   }

   public void handleBlockUpdate(ClientboundBlockUpdatePacket clientboundBlockUpdatePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundBlockUpdatePacket, this, (BlockableEventLoop)this.minecraft);
      this.level.setKnownState(clientboundBlockUpdatePacket.getPos(), clientboundBlockUpdatePacket.getBlockState());
   }

   public void handleDisconnect(ClientboundDisconnectPacket clientboundDisconnectPacket) {
      this.connection.disconnect(clientboundDisconnectPacket.getReason());
   }

   public void onDisconnect(Component component) {
      this.minecraft.clearLevel();
      if(this.callbackScreen != null) {
         if(this.callbackScreen instanceof RealmsScreenProxy) {
            this.minecraft.setScreen((new DisconnectedRealmsScreen(((RealmsScreenProxy)this.callbackScreen).getScreen(), "disconnect.lost", component)).getProxy());
         } else {
            this.minecraft.setScreen(new DisconnectedScreen(this.callbackScreen, "disconnect.lost", component));
         }
      } else {
         this.minecraft.setScreen(new DisconnectedScreen(new JoinMultiplayerScreen(new TitleScreen()), "disconnect.lost", component));
      }

   }

   public void send(Packet packet) {
      this.connection.send(packet);
   }

   public void handleTakeItemEntity(ClientboundTakeItemEntityPacket clientboundTakeItemEntityPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundTakeItemEntityPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundTakeItemEntityPacket.getItemId());
      LivingEntity var3 = (LivingEntity)this.level.getEntity(clientboundTakeItemEntityPacket.getPlayerId());
      if(var3 == null) {
         var3 = this.minecraft.player;
      }

      if(var2 != null) {
         if(var2 instanceof ExperienceOrb) {
            this.level.playLocalSound(var2.x, var2.y, var2.z, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.1F, (this.random.nextFloat() - this.random.nextFloat()) * 0.35F + 0.9F, false);
         } else {
            this.level.playLocalSound(var2.x, var2.y, var2.z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 1.4F + 2.0F, false);
         }

         if(var2 instanceof ItemEntity) {
            ((ItemEntity)var2).getItem().setCount(clientboundTakeItemEntityPacket.getAmount());
         }

         this.minecraft.particleEngine.add(new ItemPickupParticle(this.level, var2, var3, 0.5F));
         this.level.removeEntity(clientboundTakeItemEntityPacket.getItemId());
      }

   }

   public void handleChat(ClientboundChatPacket clientboundChatPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundChatPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.gui.handleChat(clientboundChatPacket.getType(), clientboundChatPacket.getMessage());
   }

   public void handleAnimate(ClientboundAnimatePacket clientboundAnimatePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundAnimatePacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundAnimatePacket.getId());
      if(var2 != null) {
         if(clientboundAnimatePacket.getAction() == 0) {
            LivingEntity var3 = (LivingEntity)var2;
            var3.swing(InteractionHand.MAIN_HAND);
         } else if(clientboundAnimatePacket.getAction() == 3) {
            LivingEntity var3 = (LivingEntity)var2;
            var3.swing(InteractionHand.OFF_HAND);
         } else if(clientboundAnimatePacket.getAction() == 1) {
            var2.animateHurt();
         } else if(clientboundAnimatePacket.getAction() == 2) {
            Player var3 = (Player)var2;
            var3.stopSleepInBed(false, false, false);
         } else if(clientboundAnimatePacket.getAction() == 4) {
            this.minecraft.particleEngine.createTrackingEmitter(var2, ParticleTypes.CRIT);
         } else if(clientboundAnimatePacket.getAction() == 5) {
            this.minecraft.particleEngine.createTrackingEmitter(var2, ParticleTypes.ENCHANTED_HIT);
         }

      }
   }

   public void handleAddMob(ClientboundAddMobPacket clientboundAddMobPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundAddMobPacket, this, (BlockableEventLoop)this.minecraft);
      double var2 = clientboundAddMobPacket.getX();
      double var4 = clientboundAddMobPacket.getY();
      double var6 = clientboundAddMobPacket.getZ();
      float var8 = (float)(clientboundAddMobPacket.getyRot() * 360) / 256.0F;
      float var9 = (float)(clientboundAddMobPacket.getxRot() * 360) / 256.0F;
      LivingEntity var10 = (LivingEntity)EntityType.create(clientboundAddMobPacket.getType(), this.minecraft.level);
      if(var10 != null) {
         var10.setPacketCoordinates(var2, var4, var6);
         var10.yBodyRot = (float)(clientboundAddMobPacket.getyHeadRot() * 360) / 256.0F;
         var10.yHeadRot = (float)(clientboundAddMobPacket.getyHeadRot() * 360) / 256.0F;
         if(var10 instanceof EnderDragon) {
            EnderDragonPart[] vars11 = ((EnderDragon)var10).getSubEntities();

            for(int var12 = 0; var12 < vars11.length; ++var12) {
               vars11[var12].setId(var12 + clientboundAddMobPacket.getId());
            }
         }

         var10.setId(clientboundAddMobPacket.getId());
         var10.setUUID(clientboundAddMobPacket.getUUID());
         var10.absMoveTo(var2, var4, var6, var8, var9);
         var10.setDeltaMovement((double)((float)clientboundAddMobPacket.getXd() / 8000.0F), (double)((float)clientboundAddMobPacket.getYd() / 8000.0F), (double)((float)clientboundAddMobPacket.getZd() / 8000.0F));
         this.level.putNonPlayerEntity(clientboundAddMobPacket.getId(), var10);
         List<SynchedEntityData.DataItem<?>> var11 = clientboundAddMobPacket.getUnpackedData();
         if(var11 != null) {
            var10.getEntityData().assignValues(var11);
         }
      } else {
         LOGGER.warn("Skipping Entity with id {}", Integer.valueOf(clientboundAddMobPacket.getType()));
      }

   }

   public void handleSetTime(ClientboundSetTimePacket clientboundSetTimePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetTimePacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.level.setGameTime(clientboundSetTimePacket.getGameTime());
      this.minecraft.level.setDayTime(clientboundSetTimePacket.getDayTime());
   }

   public void handleSetSpawn(ClientboundSetSpawnPositionPacket clientboundSetSpawnPositionPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetSpawnPositionPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.player.setRespawnPosition(clientboundSetSpawnPositionPacket.getPos(), true);
      this.minecraft.level.getLevelData().setSpawn(clientboundSetSpawnPositionPacket.getPos());
   }

   public void handleSetEntityPassengersPacket(ClientboundSetPassengersPacket clientboundSetPassengersPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetPassengersPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundSetPassengersPacket.getVehicle());
      if(var2 == null) {
         LOGGER.warn("Received passengers for unknown entity");
      } else {
         boolean var3 = var2.hasIndirectPassenger(this.minecraft.player);
         var2.ejectPassengers();

         for(int var7 : clientboundSetPassengersPacket.getPassengers()) {
            Entity var8 = this.level.getEntity(var7);
            if(var8 != null) {
               var8.startRiding(var2, true);
               if(var8 == this.minecraft.player && !var3) {
                  this.minecraft.gui.setOverlayMessage(I18n.get("mount.onboard", new Object[]{this.minecraft.options.keySneak.getTranslatedKeyMessage()}), false);
               }
            }
         }

      }
   }

   public void handleEntityLinkPacket(ClientboundSetEntityLinkPacket clientboundSetEntityLinkPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetEntityLinkPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundSetEntityLinkPacket.getSourceId());
      if(var2 instanceof Mob) {
         ((Mob)var2).setDelayedLeashHolderId(clientboundSetEntityLinkPacket.getDestId());
      }

   }

   private static ItemStack findTotem(Player player) {
      for(InteractionHand var4 : InteractionHand.values()) {
         ItemStack var5 = player.getItemInHand(var4);
         if(var5.getItem() == Items.TOTEM_OF_UNDYING) {
            return var5;
         }
      }

      return new ItemStack(Items.TOTEM_OF_UNDYING);
   }

   public void handleEntityEvent(ClientboundEntityEventPacket clientboundEntityEventPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundEntityEventPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = clientboundEntityEventPacket.getEntity(this.level);
      if(var2 != null) {
         if(clientboundEntityEventPacket.getEventId() == 21) {
            this.minecraft.getSoundManager().play(new GuardianAttackSoundInstance((Guardian)var2));
         } else if(clientboundEntityEventPacket.getEventId() == 35) {
            int var3 = 40;
            this.minecraft.particleEngine.createTrackingEmitter(var2, ParticleTypes.TOTEM_OF_UNDYING, 30);
            this.level.playLocalSound(var2.x, var2.y, var2.z, SoundEvents.TOTEM_USE, var2.getSoundSource(), 1.0F, 1.0F, false);
            if(var2 == this.minecraft.player) {
               this.minecraft.gameRenderer.displayItemActivation(findTotem(this.minecraft.player));
            }
         } else {
            var2.handleEntityEvent(clientboundEntityEventPacket.getEventId());
         }
      }

   }

   public void handleSetHealth(ClientboundSetHealthPacket clientboundSetHealthPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetHealthPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.player.hurtTo(clientboundSetHealthPacket.getHealth());
      this.minecraft.player.getFoodData().setFoodLevel(clientboundSetHealthPacket.getFood());
      this.minecraft.player.getFoodData().setSaturation(clientboundSetHealthPacket.getSaturation());
   }

   public void handleSetExperience(ClientboundSetExperiencePacket clientboundSetExperiencePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetExperiencePacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.player.setExperienceValues(clientboundSetExperiencePacket.getExperienceProgress(), clientboundSetExperiencePacket.getTotalExperience(), clientboundSetExperiencePacket.getExperienceLevel());
   }

   public void handleRespawn(ClientboundRespawnPacket clientboundRespawnPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundRespawnPacket, this, (BlockableEventLoop)this.minecraft);
      DimensionType var2 = clientboundRespawnPacket.getDimension();
      LocalPlayer var3 = this.minecraft.player;
      int var4 = var3.getId();
      if(var2 != var3.dimension) {
         this.started = false;
         Scoreboard var5 = this.level.getScoreboard();
         this.level = new MultiPlayerLevel(this, new LevelSettings(0L, clientboundRespawnPacket.getPlayerGameType(), false, this.minecraft.level.getLevelData().isHardcore(), clientboundRespawnPacket.getLevelType()), clientboundRespawnPacket.getDimension(), this.serverChunkRadius, this.minecraft.getProfiler(), this.minecraft.levelRenderer);
         this.level.setScoreboard(var5);
         this.minecraft.setLevel(this.level);
         this.minecraft.setScreen(new ReceivingLevelScreen());
      }

      this.level.validateSpawn();
      this.level.removeAllPendingEntityRemovals();
      String var5 = var3.getServerBrand();
      this.minecraft.cameraEntity = null;
      LocalPlayer var6 = this.minecraft.gameMode.createPlayer(this.level, var3.getStats(), var3.getRecipeBook());
      var6.setId(var4);
      var6.dimension = var2;
      this.minecraft.player = var6;
      this.minecraft.cameraEntity = var6;
      var6.getEntityData().assignValues(var3.getEntityData().getAll());
      var6.resetPos();
      var6.setServerBrand(var5);
      this.level.addPlayer(var4, var6);
      var6.yRot = -180.0F;
      var6.input = new KeyboardInput(this.minecraft.options);
      this.minecraft.gameMode.adjustPlayer(var6);
      var6.setReducedDebugInfo(var3.isReducedDebugInfo());
      if(this.minecraft.screen instanceof DeathScreen) {
         this.minecraft.setScreen((Screen)null);
      }

      this.minecraft.gameMode.setLocalMode(clientboundRespawnPacket.getPlayerGameType());
   }

   public void handleExplosion(ClientboundExplodePacket clientboundExplodePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundExplodePacket, this, (BlockableEventLoop)this.minecraft);
      Explosion var2 = new Explosion(this.minecraft.level, (Entity)null, clientboundExplodePacket.getX(), clientboundExplodePacket.getY(), clientboundExplodePacket.getZ(), clientboundExplodePacket.getPower(), clientboundExplodePacket.getToBlow());
      var2.finalizeExplosion(true);
      this.minecraft.player.setDeltaMovement(this.minecraft.player.getDeltaMovement().add((double)clientboundExplodePacket.getKnockbackX(), (double)clientboundExplodePacket.getKnockbackY(), (double)clientboundExplodePacket.getKnockbackZ()));
   }

   public void handleHorseScreenOpen(ClientboundHorseScreenOpenPacket clientboundHorseScreenOpenPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundHorseScreenOpenPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundHorseScreenOpenPacket.getEntityId());
      if(var2 instanceof AbstractHorse) {
         LocalPlayer var3 = this.minecraft.player;
         AbstractHorse var4 = (AbstractHorse)var2;
         SimpleContainer var5 = new SimpleContainer(clientboundHorseScreenOpenPacket.getSize());
         HorseInventoryMenu var6 = new HorseInventoryMenu(clientboundHorseScreenOpenPacket.getContainerId(), var3.inventory, var5, var4);
         var3.containerMenu = var6;
         this.minecraft.setScreen(new HorseInventoryScreen(var6, var3.inventory, var4));
      }

   }

   public void handleOpenScreen(ClientboundOpenScreenPacket clientboundOpenScreenPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundOpenScreenPacket, this, (BlockableEventLoop)this.minecraft);
      MenuScreens.create(clientboundOpenScreenPacket.getType(), this.minecraft, clientboundOpenScreenPacket.getContainerId(), clientboundOpenScreenPacket.getTitle());
   }

   public void handleContainerSetSlot(ClientboundContainerSetSlotPacket clientboundContainerSetSlotPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundContainerSetSlotPacket, this, (BlockableEventLoop)this.minecraft);
      Player var2 = this.minecraft.player;
      ItemStack var3 = clientboundContainerSetSlotPacket.getItem();
      int var4 = clientboundContainerSetSlotPacket.getSlot();
      this.minecraft.getTutorial().onGetItem(var3);
      if(clientboundContainerSetSlotPacket.getContainerId() == -1) {
         if(!(this.minecraft.screen instanceof CreativeModeInventoryScreen)) {
            var2.inventory.setCarried(var3);
         }
      } else if(clientboundContainerSetSlotPacket.getContainerId() == -2) {
         var2.inventory.setItem(var4, var3);
      } else {
         boolean var5 = false;
         if(this.minecraft.screen instanceof CreativeModeInventoryScreen) {
            CreativeModeInventoryScreen var6 = (CreativeModeInventoryScreen)this.minecraft.screen;
            var5 = var6.getSelectedTab() != CreativeModeTab.TAB_INVENTORY.getId();
         }

         if(clientboundContainerSetSlotPacket.getContainerId() == 0 && clientboundContainerSetSlotPacket.getSlot() >= 36 && var4 < 45) {
            if(!var3.isEmpty()) {
               ItemStack var6 = var2.inventoryMenu.getSlot(var4).getItem();
               if(var6.isEmpty() || var6.getCount() < var3.getCount()) {
                  var3.setPopTime(5);
               }
            }

            var2.inventoryMenu.setItem(var4, var3);
         } else if(clientboundContainerSetSlotPacket.getContainerId() == var2.containerMenu.containerId && (clientboundContainerSetSlotPacket.getContainerId() != 0 || !var5)) {
            var2.containerMenu.setItem(var4, var3);
         }
      }

   }

   public void handleContainerAck(ClientboundContainerAckPacket clientboundContainerAckPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundContainerAckPacket, this, (BlockableEventLoop)this.minecraft);
      AbstractContainerMenu var2 = null;
      Player var3 = this.minecraft.player;
      if(clientboundContainerAckPacket.getContainerId() == 0) {
         var2 = var3.inventoryMenu;
      } else if(clientboundContainerAckPacket.getContainerId() == var3.containerMenu.containerId) {
         var2 = var3.containerMenu;
      }

      if(var2 != null && !clientboundContainerAckPacket.isAccepted()) {
         this.send((Packet)(new ServerboundContainerAckPacket(clientboundContainerAckPacket.getContainerId(), clientboundContainerAckPacket.getUid(), true)));
      }

   }

   public void handleContainerContent(ClientboundContainerSetContentPacket clientboundContainerSetContentPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundContainerSetContentPacket, this, (BlockableEventLoop)this.minecraft);
      Player var2 = this.minecraft.player;
      if(clientboundContainerSetContentPacket.getContainerId() == 0) {
         var2.inventoryMenu.setAll(clientboundContainerSetContentPacket.getItems());
      } else if(clientboundContainerSetContentPacket.getContainerId() == var2.containerMenu.containerId) {
         var2.containerMenu.setAll(clientboundContainerSetContentPacket.getItems());
      }

   }

   public void handleOpenSignEditor(ClientboundOpenSignEditorPacket clientboundOpenSignEditorPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundOpenSignEditorPacket, this, (BlockableEventLoop)this.minecraft);
      BlockEntity var2 = this.level.getBlockEntity(clientboundOpenSignEditorPacket.getPos());
      if(!(var2 instanceof SignBlockEntity)) {
         var2 = new SignBlockEntity();
         var2.setLevel(this.level);
         var2.setPosition(clientboundOpenSignEditorPacket.getPos());
      }

      this.minecraft.player.openTextEdit((SignBlockEntity)var2);
   }

   public void handleBlockEntityData(ClientboundBlockEntityDataPacket clientboundBlockEntityDataPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundBlockEntityDataPacket, this, (BlockableEventLoop)this.minecraft);
      if(this.minecraft.level.hasChunkAt(clientboundBlockEntityDataPacket.getPos())) {
         BlockEntity var2 = this.minecraft.level.getBlockEntity(clientboundBlockEntityDataPacket.getPos());
         int var3 = clientboundBlockEntityDataPacket.getType();
         boolean var4 = var3 == 2 && var2 instanceof CommandBlockEntity;
         if(var3 == 1 && var2 instanceof SpawnerBlockEntity || var4 || var3 == 3 && var2 instanceof BeaconBlockEntity || var3 == 4 && var2 instanceof SkullBlockEntity || var3 == 6 && var2 instanceof BannerBlockEntity || var3 == 7 && var2 instanceof StructureBlockEntity || var3 == 8 && var2 instanceof TheEndGatewayBlockEntity || var3 == 9 && var2 instanceof SignBlockEntity || var3 == 11 && var2 instanceof BedBlockEntity || var3 == 5 && var2 instanceof ConduitBlockEntity || var3 == 12 && var2 instanceof JigsawBlockEntity || var3 == 13 && var2 instanceof CampfireBlockEntity) {
            var2.load(clientboundBlockEntityDataPacket.getTag());
         }

         if(var4 && this.minecraft.screen instanceof CommandBlockEditScreen) {
            ((CommandBlockEditScreen)this.minecraft.screen).updateGui();
         }
      }

   }

   public void handleContainerSetData(ClientboundContainerSetDataPacket clientboundContainerSetDataPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundContainerSetDataPacket, this, (BlockableEventLoop)this.minecraft);
      Player var2 = this.minecraft.player;
      if(var2.containerMenu != null && var2.containerMenu.containerId == clientboundContainerSetDataPacket.getContainerId()) {
         var2.containerMenu.setData(clientboundContainerSetDataPacket.getId(), clientboundContainerSetDataPacket.getValue());
      }

   }

   public void handleSetEquippedItem(ClientboundSetEquippedItemPacket clientboundSetEquippedItemPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetEquippedItemPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundSetEquippedItemPacket.getEntity());
      if(var2 != null) {
         var2.setItemSlot(clientboundSetEquippedItemPacket.getSlot(), clientboundSetEquippedItemPacket.getItem());
      }

   }

   public void handleContainerClose(ClientboundContainerClosePacket clientboundContainerClosePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundContainerClosePacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.player.clientSideCloseContainer();
   }

   public void handleBlockEvent(ClientboundBlockEventPacket clientboundBlockEventPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundBlockEventPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.level.blockEvent(clientboundBlockEventPacket.getPos(), clientboundBlockEventPacket.getBlock(), clientboundBlockEventPacket.getB0(), clientboundBlockEventPacket.getB1());
   }

   public void handleBlockDestruction(ClientboundBlockDestructionPacket clientboundBlockDestructionPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundBlockDestructionPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.level.destroyBlockProgress(clientboundBlockDestructionPacket.getId(), clientboundBlockDestructionPacket.getPos(), clientboundBlockDestructionPacket.getProgress());
   }

   public void handleGameEvent(ClientboundGameEventPacket clientboundGameEventPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundGameEventPacket, this, (BlockableEventLoop)this.minecraft);
      Player var2 = this.minecraft.player;
      int var3 = clientboundGameEventPacket.getEvent();
      float var4 = clientboundGameEventPacket.getParam();
      int var5 = Mth.floor(var4 + 0.5F);
      if(var3 >= 0 && var3 < ClientboundGameEventPacket.EVENT_LANGUAGE_ID.length && ClientboundGameEventPacket.EVENT_LANGUAGE_ID[var3] != null) {
         var2.displayClientMessage(new TranslatableComponent(ClientboundGameEventPacket.EVENT_LANGUAGE_ID[var3], new Object[0]), false);
      }

      if(var3 == 1) {
         this.level.getLevelData().setRaining(true);
         this.level.setRainLevel(0.0F);
      } else if(var3 == 2) {
         this.level.getLevelData().setRaining(false);
         this.level.setRainLevel(1.0F);
      } else if(var3 == 3) {
         this.minecraft.gameMode.setLocalMode(GameType.byId(var5));
      } else if(var3 == 4) {
         if(var5 == 0) {
            this.minecraft.player.connection.send((Packet)(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN)));
            this.minecraft.setScreen(new ReceivingLevelScreen());
         } else if(var5 == 1) {
            this.minecraft.setScreen(new WinScreen(true, () -> {
               this.minecraft.player.connection.send((Packet)(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN)));
            }));
         }
      } else if(var3 == 5) {
         Options var6 = this.minecraft.options;
         if(var4 == 0.0F) {
            this.minecraft.setScreen(new DemoIntroScreen());
         } else if(var4 == 101.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.help.movement", new Object[]{var6.keyUp.getTranslatedKeyMessage(), var6.keyLeft.getTranslatedKeyMessage(), var6.keyDown.getTranslatedKeyMessage(), var6.keyRight.getTranslatedKeyMessage()}));
         } else if(var4 == 102.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.help.jump", new Object[]{var6.keyJump.getTranslatedKeyMessage()}));
         } else if(var4 == 103.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.help.inventory", new Object[]{var6.keyInventory.getTranslatedKeyMessage()}));
         } else if(var4 == 104.0F) {
            this.minecraft.gui.getChat().addMessage(new TranslatableComponent("demo.day.6", new Object[]{var6.keyScreenshot.getTranslatedKeyMessage()}));
         }
      } else if(var3 == 6) {
         this.level.playSound(var2, var2.x, var2.y + (double)var2.getEyeHeight(), var2.z, SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 0.18F, 0.45F);
      } else if(var3 == 7) {
         this.level.setRainLevel(var4);
      } else if(var3 == 8) {
         this.level.setThunderLevel(var4);
      } else if(var3 == 9) {
         this.level.playSound(var2, var2.x, var2.y, var2.z, SoundEvents.PUFFER_FISH_STING, SoundSource.NEUTRAL, 1.0F, 1.0F);
      } else if(var3 == 10) {
         this.level.addParticle(ParticleTypes.ELDER_GUARDIAN, var2.x, var2.y, var2.z, 0.0D, 0.0D, 0.0D);
         this.level.playSound(var2, var2.x, var2.y, var2.z, SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.HOSTILE, 1.0F, 1.0F);
      }

   }

   public void handleMapItemData(ClientboundMapItemDataPacket clientboundMapItemDataPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundMapItemDataPacket, this, (BlockableEventLoop)this.minecraft);
      MapRenderer var2 = this.minecraft.gameRenderer.getMapRenderer();
      String var3 = MapItem.makeKey(clientboundMapItemDataPacket.getMapId());
      MapItemSavedData var4 = this.minecraft.level.getMapData(var3);
      if(var4 == null) {
         var4 = new MapItemSavedData(var3);
         if(var2.getMapInstanceIfExists(var3) != null) {
            MapItemSavedData var5 = var2.getData(var2.getMapInstanceIfExists(var3));
            if(var5 != null) {
               var4 = var5;
            }
         }

         this.minecraft.level.setMapData(var4);
      }

      clientboundMapItemDataPacket.applyToMap(var4);
      var2.update(var4);
   }

   public void handleLevelEvent(ClientboundLevelEventPacket clientboundLevelEventPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundLevelEventPacket, this, (BlockableEventLoop)this.minecraft);
      if(clientboundLevelEventPacket.isGlobalEvent()) {
         this.minecraft.level.globalLevelEvent(clientboundLevelEventPacket.getType(), clientboundLevelEventPacket.getPos(), clientboundLevelEventPacket.getData());
      } else {
         this.minecraft.level.levelEvent(clientboundLevelEventPacket.getType(), clientboundLevelEventPacket.getPos(), clientboundLevelEventPacket.getData());
      }

   }

   public void handleUpdateAdvancementsPacket(ClientboundUpdateAdvancementsPacket clientboundUpdateAdvancementsPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundUpdateAdvancementsPacket, this, (BlockableEventLoop)this.minecraft);
      this.advancements.update(clientboundUpdateAdvancementsPacket);
   }

   public void handleSelectAdvancementsTab(ClientboundSelectAdvancementsTabPacket clientboundSelectAdvancementsTabPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSelectAdvancementsTabPacket, this, (BlockableEventLoop)this.minecraft);
      ResourceLocation var2 = clientboundSelectAdvancementsTabPacket.getTab();
      if(var2 == null) {
         this.advancements.setSelectedTab((Advancement)null, false);
      } else {
         Advancement var3 = this.advancements.getAdvancements().get(var2);
         this.advancements.setSelectedTab(var3, false);
      }

   }

   public void handleCommands(ClientboundCommandsPacket clientboundCommandsPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundCommandsPacket, this, (BlockableEventLoop)this.minecraft);
      this.commands = new CommandDispatcher(clientboundCommandsPacket.getRoot());
   }

   public void handleStopSoundEvent(ClientboundStopSoundPacket clientboundStopSoundPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundStopSoundPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.getSoundManager().stop(clientboundStopSoundPacket.getName(), clientboundStopSoundPacket.getSource());
   }

   public void handleCommandSuggestions(ClientboundCommandSuggestionsPacket clientboundCommandSuggestionsPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundCommandSuggestionsPacket, this, (BlockableEventLoop)this.minecraft);
      this.suggestionsProvider.completeCustomSuggestions(clientboundCommandSuggestionsPacket.getId(), clientboundCommandSuggestionsPacket.getSuggestions());
   }

   public void handleUpdateRecipes(ClientboundUpdateRecipesPacket clientboundUpdateRecipesPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundUpdateRecipesPacket, this, (BlockableEventLoop)this.minecraft);
      this.recipeManager.replaceRecipes(clientboundUpdateRecipesPacket.getRecipes());
      MutableSearchTree<RecipeCollection> var2 = this.minecraft.getSearchTree(SearchRegistry.RECIPE_COLLECTIONS);
      var2.clear();
      ClientRecipeBook var3 = this.minecraft.player.getRecipeBook();
      var3.setupCollections();
      var3.getCollections().forEach(var2::add);
      var2.refresh();
   }

   public void handleLookAt(ClientboundPlayerLookAtPacket clientboundPlayerLookAtPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundPlayerLookAtPacket, this, (BlockableEventLoop)this.minecraft);
      Vec3 var2 = clientboundPlayerLookAtPacket.getPosition(this.level);
      if(var2 != null) {
         this.minecraft.player.lookAt(clientboundPlayerLookAtPacket.getFromAnchor(), var2);
      }

   }

   public void handleTagQueryPacket(ClientboundTagQueryPacket clientboundTagQueryPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundTagQueryPacket, this, (BlockableEventLoop)this.minecraft);
      if(!this.debugQueryHandler.handleResponse(clientboundTagQueryPacket.getTransactionId(), clientboundTagQueryPacket.getTag())) {
         LOGGER.debug("Got unhandled response to tag query {}", Integer.valueOf(clientboundTagQueryPacket.getTransactionId()));
      }

   }

   public void handleAwardStats(ClientboundAwardStatsPacket clientboundAwardStatsPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundAwardStatsPacket, this, (BlockableEventLoop)this.minecraft);

      for(Entry<Stat<?>, Integer> var3 : clientboundAwardStatsPacket.getStats().entrySet()) {
         Stat<?> var4 = (Stat)var3.getKey();
         int var5 = ((Integer)var3.getValue()).intValue();
         this.minecraft.player.getStats().setValue(this.minecraft.player, var4, var5);
      }

      if(this.minecraft.screen instanceof StatsUpdateListener) {
         ((StatsUpdateListener)this.minecraft.screen).onStatsUpdated();
      }

   }

   public void handleAddOrRemoveRecipes(ClientboundRecipePacket clientboundRecipePacket) {
      ClientRecipeBook var2;
      PacketUtils.ensureRunningOnSameThread(clientboundRecipePacket, this, (BlockableEventLoop)this.minecraft);
      var2 = this.minecraft.player.getRecipeBook();
      var2.setGuiOpen(clientboundRecipePacket.isGuiOpen());
      var2.setFilteringCraftable(clientboundRecipePacket.isFilteringCraftable());
      var2.setFurnaceGuiOpen(clientboundRecipePacket.isFurnaceGuiOpen());
      var2.setFurnaceFilteringCraftable(clientboundRecipePacket.isFurnaceFilteringCraftable());
      ClientboundRecipePacket.State var3 = clientboundRecipePacket.getState();
      label0:
      switch(var3) {
      case REMOVE:
         Iterator var8 = clientboundRecipePacket.getRecipes().iterator();

         while(true) {
            if(!var8.hasNext()) {
               break label0;
            }

            ResourceLocation var5 = (ResourceLocation)var8.next();
            this.recipeManager.byKey(var5).ifPresent(var2::remove);
         }
      case INIT:
         for(ResourceLocation var5 : clientboundRecipePacket.getRecipes()) {
            this.recipeManager.byKey(var5).ifPresent(var2::add);
         }

         Iterator var7 = clientboundRecipePacket.getHighlights().iterator();

         while(true) {
            if(!var7.hasNext()) {
               break label0;
            }

            ResourceLocation var5 = (ResourceLocation)var7.next();
            this.recipeManager.byKey(var5).ifPresent(var2::addHighlight);
         }
      case ADD:
         for(ResourceLocation var5 : clientboundRecipePacket.getRecipes()) {
            this.recipeManager.byKey(var5).ifPresent((recipe) -> {
               var2.add(recipe);
               var2.addHighlight(recipe);
               RecipeToast.addOrUpdate(this.minecraft.getToasts(), recipe);
            });
         }
      }

      var2.getCollections().forEach((recipeCollection) -> {
         recipeCollection.updateKnownRecipes(var2);
      });
      if(this.minecraft.screen instanceof RecipeUpdateListener) {
         ((RecipeUpdateListener)this.minecraft.screen).recipesUpdated();
      }

   }

   public void handleUpdateMobEffect(ClientboundUpdateMobEffectPacket clientboundUpdateMobEffectPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundUpdateMobEffectPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundUpdateMobEffectPacket.getEntityId());
      if(var2 instanceof LivingEntity) {
         MobEffect var3 = MobEffect.byId(clientboundUpdateMobEffectPacket.getEffectId());
         if(var3 != null) {
            MobEffectInstance var4 = new MobEffectInstance(var3, clientboundUpdateMobEffectPacket.getEffectDurationTicks(), clientboundUpdateMobEffectPacket.getEffectAmplifier(), clientboundUpdateMobEffectPacket.isEffectAmbient(), clientboundUpdateMobEffectPacket.isEffectVisible(), clientboundUpdateMobEffectPacket.effectShowsIcon());
            var4.setNoCounter(clientboundUpdateMobEffectPacket.isSuperLongDuration());
            ((LivingEntity)var2).addEffect(var4);
         }
      }
   }

   public void handleUpdateTags(ClientboundUpdateTagsPacket clientboundUpdateTagsPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundUpdateTagsPacket, this, (BlockableEventLoop)this.minecraft);
      this.tags = clientboundUpdateTagsPacket.getTags();
      if(!this.connection.isMemoryConnection()) {
         BlockTags.reset(this.tags.getBlocks());
         ItemTags.reset(this.tags.getItems());
         FluidTags.reset(this.tags.getFluids());
         EntityTypeTags.reset(this.tags.getEntityTypes());
      }

      this.minecraft.getSearchTree(SearchRegistry.CREATIVE_TAGS).refresh();
   }

   public void handlePlayerCombat(ClientboundPlayerCombatPacket clientboundPlayerCombatPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundPlayerCombatPacket, this, (BlockableEventLoop)this.minecraft);
      if(clientboundPlayerCombatPacket.event == ClientboundPlayerCombatPacket.Event.ENTITY_DIED) {
         Entity var2 = this.level.getEntity(clientboundPlayerCombatPacket.playerId);
         if(var2 == this.minecraft.player) {
            this.minecraft.setScreen(new DeathScreen(clientboundPlayerCombatPacket.message, this.level.getLevelData().isHardcore()));
         }
      }

   }

   public void handleChangeDifficulty(ClientboundChangeDifficultyPacket clientboundChangeDifficultyPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundChangeDifficultyPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.level.getLevelData().setDifficulty(clientboundChangeDifficultyPacket.getDifficulty());
      this.minecraft.level.getLevelData().setDifficultyLocked(clientboundChangeDifficultyPacket.isLocked());
   }

   public void handleSetCamera(ClientboundSetCameraPacket clientboundSetCameraPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetCameraPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = clientboundSetCameraPacket.getEntity(this.level);
      if(var2 != null) {
         this.minecraft.setCameraEntity(var2);
      }

   }

   public void handleSetBorder(ClientboundSetBorderPacket clientboundSetBorderPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetBorderPacket, this, (BlockableEventLoop)this.minecraft);
      clientboundSetBorderPacket.applyChanges(this.level.getWorldBorder());
   }

   public void handleSetTitles(ClientboundSetTitlesPacket clientboundSetTitlesPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetTitlesPacket, this, (BlockableEventLoop)this.minecraft);
      ClientboundSetTitlesPacket.Type var2 = clientboundSetTitlesPacket.getType();
      String var3 = null;
      String var4 = null;
      String var5 = clientboundSetTitlesPacket.getText() != null?clientboundSetTitlesPacket.getText().getColoredString():"";
      switch(var2) {
      case TITLE:
         var3 = var5;
         break;
      case SUBTITLE:
         var4 = var5;
         break;
      case ACTIONBAR:
         this.minecraft.gui.setOverlayMessage(var5, false);
         return;
      case RESET:
         this.minecraft.gui.setTitles("", "", -1, -1, -1);
         this.minecraft.gui.resetTitleTimes();
         return;
      }

      this.minecraft.gui.setTitles(var3, var4, clientboundSetTitlesPacket.getFadeInTime(), clientboundSetTitlesPacket.getStayTime(), clientboundSetTitlesPacket.getFadeOutTime());
   }

   public void handleTabListCustomisation(ClientboundTabListPacket clientboundTabListPacket) {
      this.minecraft.gui.getTabList().setHeader(clientboundTabListPacket.getHeader().getColoredString().isEmpty()?null:clientboundTabListPacket.getHeader());
      this.minecraft.gui.getTabList().setFooter(clientboundTabListPacket.getFooter().getColoredString().isEmpty()?null:clientboundTabListPacket.getFooter());
   }

   public void handleRemoveMobEffect(ClientboundRemoveMobEffectPacket clientboundRemoveMobEffectPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundRemoveMobEffectPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = clientboundRemoveMobEffectPacket.getEntity(this.level);
      if(var2 instanceof LivingEntity) {
         ((LivingEntity)var2).removeEffectNoUpdate(clientboundRemoveMobEffectPacket.getEffect());
      }

   }

   public void handlePlayerInfo(ClientboundPlayerInfoPacket clientboundPlayerInfoPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundPlayerInfoPacket, this, (BlockableEventLoop)this.minecraft);

      for(ClientboundPlayerInfoPacket.PlayerUpdate var3 : clientboundPlayerInfoPacket.getEntries()) {
         if(clientboundPlayerInfoPacket.getAction() == ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER) {
            this.playerInfoMap.remove(var3.getProfile().getId());
         } else {
            PlayerInfo var4 = (PlayerInfo)this.playerInfoMap.get(var3.getProfile().getId());
            if(clientboundPlayerInfoPacket.getAction() == ClientboundPlayerInfoPacket.Action.ADD_PLAYER) {
               var4 = new PlayerInfo(var3);
               this.playerInfoMap.put(var4.getProfile().getId(), var4);
            }

            if(var4 != null) {
               switch(clientboundPlayerInfoPacket.getAction()) {
               case ADD_PLAYER:
                  var4.setGameMode(var3.getGameMode());
                  var4.setLatency(var3.getLatency());
                  var4.setTabListDisplayName(var3.getDisplayName());
                  break;
               case UPDATE_GAME_MODE:
                  var4.setGameMode(var3.getGameMode());
                  break;
               case UPDATE_LATENCY:
                  var4.setLatency(var3.getLatency());
                  break;
               case UPDATE_DISPLAY_NAME:
                  var4.setTabListDisplayName(var3.getDisplayName());
               }
            }
         }
      }

   }

   public void handleKeepAlive(ClientboundKeepAlivePacket clientboundKeepAlivePacket) {
      this.send((Packet)(new ServerboundKeepAlivePacket(clientboundKeepAlivePacket.getId())));
   }

   public void handlePlayerAbilities(ClientboundPlayerAbilitiesPacket clientboundPlayerAbilitiesPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundPlayerAbilitiesPacket, this, (BlockableEventLoop)this.minecraft);
      Player var2 = this.minecraft.player;
      var2.abilities.flying = clientboundPlayerAbilitiesPacket.isFlying();
      var2.abilities.instabuild = clientboundPlayerAbilitiesPacket.canInstabuild();
      var2.abilities.invulnerable = clientboundPlayerAbilitiesPacket.isInvulnerable();
      var2.abilities.mayfly = clientboundPlayerAbilitiesPacket.canFly();
      var2.abilities.setFlyingSpeed(clientboundPlayerAbilitiesPacket.getFlyingSpeed());
      var2.abilities.setWalkingSpeed(clientboundPlayerAbilitiesPacket.getWalkingSpeed());
   }

   public void handleSoundEvent(ClientboundSoundPacket clientboundSoundPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSoundPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.level.playSound(this.minecraft.player, clientboundSoundPacket.getX(), clientboundSoundPacket.getY(), clientboundSoundPacket.getZ(), clientboundSoundPacket.getSound(), clientboundSoundPacket.getSource(), clientboundSoundPacket.getVolume(), clientboundSoundPacket.getPitch());
   }

   public void handleSoundEntityEvent(ClientboundSoundEntityPacket clientboundSoundEntityPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSoundEntityPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundSoundEntityPacket.getId());
      if(var2 != null) {
         this.minecraft.level.playSound(this.minecraft.player, var2, clientboundSoundEntityPacket.getSound(), clientboundSoundEntityPacket.getSource(), clientboundSoundEntityPacket.getVolume(), clientboundSoundEntityPacket.getPitch());
      }
   }

   public void handleCustomSoundEvent(ClientboundCustomSoundPacket clientboundCustomSoundPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundCustomSoundPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.getSoundManager().play(new SimpleSoundInstance(clientboundCustomSoundPacket.getName(), clientboundCustomSoundPacket.getSource(), clientboundCustomSoundPacket.getVolume(), clientboundCustomSoundPacket.getPitch(), false, 0, SoundInstance.Attenuation.LINEAR, (float)clientboundCustomSoundPacket.getX(), (float)clientboundCustomSoundPacket.getY(), (float)clientboundCustomSoundPacket.getZ(), false));
   }

   public void handleResourcePack(ClientboundResourcePackPacket clientboundResourcePackPacket) {
      String var2 = clientboundResourcePackPacket.getUrl();
      String var3 = clientboundResourcePackPacket.getHash();
      if(this.validateResourcePackUrl(var2)) {
         if(var2.startsWith("level://")) {
            try {
               String var4 = URLDecoder.decode(var2.substring("level://".length()), StandardCharsets.UTF_8.toString());
               File var5 = new File(this.minecraft.gameDirectory, "saves");
               File var6 = new File(var5, var4);
               if(var6.isFile()) {
                  this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
                  CompletableFuture<?> var7 = this.minecraft.getClientPackSource().setServerPack(var6);
                  this.downloadCallback(var7);
                  return;
               }
            } catch (UnsupportedEncodingException var8) {
               ;
            }

            this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
         } else {
            ServerData var4 = this.minecraft.getCurrentServer();
            if(var4 != null && var4.getResourcePackStatus() == ServerData.ServerPackStatus.ENABLED) {
               this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
               this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(var2, var3));
            } else if(var4 != null && var4.getResourcePackStatus() != ServerData.ServerPackStatus.PROMPT) {
               this.send(ServerboundResourcePackPacket.Action.DECLINED);
            } else {
               this.minecraft.execute(() -> {
                  this.minecraft.setScreen(new ConfirmScreen((var3x) -> {
                     this.minecraft = Minecraft.getInstance();
                     ServerData var4 = this.minecraft.getCurrentServer();
                     if(var3x) {
                        if(var4 != null) {
                           var4.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                        }

                        this.send(ServerboundResourcePackPacket.Action.ACCEPTED);
                        this.downloadCallback(this.minecraft.getClientPackSource().downloadAndSelectResourcePack(var2, var3));
                     } else {
                        if(var4 != null) {
                           var4.setResourcePackStatus(ServerData.ServerPackStatus.DISABLED);
                        }

                        this.send(ServerboundResourcePackPacket.Action.DECLINED);
                     }

                     ServerList.saveSingleServer(var4);
                     this.minecraft.setScreen((Screen)null);
                  }, new TranslatableComponent("multiplayer.texturePrompt.line1", new Object[0]), new TranslatableComponent("multiplayer.texturePrompt.line2", new Object[0])));
               });
            }

         }
      }
   }

   private boolean validateResourcePackUrl(String string) {
      try {
         URI var2 = new URI(string);
         String var3 = var2.getScheme();
         boolean var4 = "level".equals(var3);
         if(!"http".equals(var3) && !"https".equals(var3) && !var4) {
            throw new URISyntaxException(string, "Wrong protocol");
         } else if(!var4 || !string.contains("..") && string.endsWith("/resources.zip")) {
            return true;
         } else {
            throw new URISyntaxException(string, "Invalid levelstorage resourcepack path");
         }
      } catch (URISyntaxException var5) {
         this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
         return false;
      }
   }

   private void downloadCallback(CompletableFuture completableFuture) {
      completableFuture.thenRun(() -> {
         this.send(ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED);
      }).exceptionally((throwable) -> {
         this.send(ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD);
         return null;
      });
   }

   private void send(ServerboundResourcePackPacket.Action serverboundResourcePackPacket$Action) {
      this.connection.send(new ServerboundResourcePackPacket(serverboundResourcePackPacket$Action));
   }

   public void handleBossUpdate(ClientboundBossEventPacket clientboundBossEventPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundBossEventPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.gui.getBossOverlay().update(clientboundBossEventPacket);
   }

   public void handleItemCooldown(ClientboundCooldownPacket clientboundCooldownPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundCooldownPacket, this, (BlockableEventLoop)this.minecraft);
      if(clientboundCooldownPacket.getDuration() == 0) {
         this.minecraft.player.getCooldowns().removeCooldown(clientboundCooldownPacket.getItem());
      } else {
         this.minecraft.player.getCooldowns().addCooldown(clientboundCooldownPacket.getItem(), clientboundCooldownPacket.getDuration());
      }

   }

   public void handleMoveVehicle(ClientboundMoveVehiclePacket clientboundMoveVehiclePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundMoveVehiclePacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.minecraft.player.getRootVehicle();
      if(var2 != this.minecraft.player && var2.isControlledByLocalInstance()) {
         var2.absMoveTo(clientboundMoveVehiclePacket.getX(), clientboundMoveVehiclePacket.getY(), clientboundMoveVehiclePacket.getZ(), clientboundMoveVehiclePacket.getYRot(), clientboundMoveVehiclePacket.getXRot());
         this.connection.send(new ServerboundMoveVehiclePacket(var2));
      }

   }

   public void handleOpenBook(ClientboundOpenBookPacket clientboundOpenBookPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundOpenBookPacket, this, (BlockableEventLoop)this.minecraft);
      ItemStack var2 = this.minecraft.player.getItemInHand(clientboundOpenBookPacket.getHand());
      if(var2.getItem() == Items.WRITTEN_BOOK) {
         this.minecraft.setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(var2)));
      }

   }

   public void handleCustomPayload(ClientboundCustomPayloadPacket clientboundCustomPayloadPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundCustomPayloadPacket, this, (BlockableEventLoop)this.minecraft);
      ResourceLocation var2 = clientboundCustomPayloadPacket.getIdentifier();
      FriendlyByteBuf var3 = null;

      try {
         var3 = clientboundCustomPayloadPacket.getData();
         if(ClientboundCustomPayloadPacket.BRAND.equals(var2)) {
            this.minecraft.player.setServerBrand(var3.readUtf(32767));
         } else if(ClientboundCustomPayloadPacket.DEBUG_PATHFINDING_PACKET.equals(var2)) {
            int var4 = var3.readInt();
            float var5 = var3.readFloat();
            Path var6 = Path.createFromStream(var3);
            this.minecraft.debugRenderer.pathfindingRenderer.addPath(var4, var6, var5);
         } else if(ClientboundCustomPayloadPacket.DEBUG_NEIGHBORSUPDATE_PACKET.equals(var2)) {
            long var4 = var3.readVarLong();
            BlockPos var6 = var3.readBlockPos();
            ((NeighborsUpdateRenderer)this.minecraft.debugRenderer.neighborsUpdateRenderer).addUpdate(var4, var6);
         } else if(ClientboundCustomPayloadPacket.DEBUG_CAVES_PACKET.equals(var2)) {
            BlockPos var4 = var3.readBlockPos();
            int var5 = var3.readInt();
            List<BlockPos> var6 = Lists.newArrayList();
            List<Float> var7 = Lists.newArrayList();

            for(int var8 = 0; var8 < var5; ++var8) {
               var6.add(var3.readBlockPos());
               var7.add(Float.valueOf(var3.readFloat()));
            }

            this.minecraft.debugRenderer.caveRenderer.addTunnel(var4, var6, var7);
         } else if(ClientboundCustomPayloadPacket.DEBUG_STRUCTURES_PACKET.equals(var2)) {
            DimensionType var4 = DimensionType.getById(var3.readInt());
            BoundingBox var5 = new BoundingBox(var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt());
            int var6 = var3.readInt();
            List<BoundingBox> var7 = Lists.newArrayList();
            List<Boolean> var8 = Lists.newArrayList();

            for(int var9 = 0; var9 < var6; ++var9) {
               var7.add(new BoundingBox(var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt()));
               var8.add(Boolean.valueOf(var3.readBoolean()));
            }

            this.minecraft.debugRenderer.structureRenderer.addBoundingBox(var5, var7, var8, var4);
         } else if(ClientboundCustomPayloadPacket.DEBUG_WORLDGENATTEMPT_PACKET.equals(var2)) {
            ((WorldGenAttemptRenderer)this.minecraft.debugRenderer.worldGenAttemptRenderer).addPos(var3.readBlockPos(), var3.readFloat(), var3.readFloat(), var3.readFloat(), var3.readFloat(), var3.readFloat());
         } else if(ClientboundCustomPayloadPacket.DEBUG_VILLAGE_SECTIONS.equals(var2)) {
            int var4 = var3.readInt();

            for(int var5 = 0; var5 < var4; ++var5) {
               this.minecraft.debugRenderer.villageDebugRenderer.setVillageSection(var3.readSectionPos());
            }

            int var5 = var3.readInt();

            for(int var6 = 0; var6 < var5; ++var6) {
               this.minecraft.debugRenderer.villageDebugRenderer.setNotVillageSection(var3.readSectionPos());
            }
         } else if(ClientboundCustomPayloadPacket.DEBUG_POI_ADDED_PACKET.equals(var2)) {
            BlockPos var4 = var3.readBlockPos();
            String var5 = var3.readUtf();
            int var6 = var3.readInt();
            VillageDebugRenderer.PoiInfo var7 = new VillageDebugRenderer.PoiInfo(var4, var5, var6);
            this.minecraft.debugRenderer.villageDebugRenderer.addPoi(var7);
         } else if(ClientboundCustomPayloadPacket.DEBUG_POI_REMOVED_PACKET.equals(var2)) {
            BlockPos var4 = var3.readBlockPos();
            this.minecraft.debugRenderer.villageDebugRenderer.removePoi(var4);
         } else if(ClientboundCustomPayloadPacket.DEBUG_POI_TICKET_COUNT_PACKET.equals(var2)) {
            BlockPos var4 = var3.readBlockPos();
            int var5 = var3.readInt();
            this.minecraft.debugRenderer.villageDebugRenderer.setFreeTicketCount(var4, var5);
         } else if(ClientboundCustomPayloadPacket.DEBUG_GOAL_SELECTOR.equals(var2)) {
            BlockPos var4 = var3.readBlockPos();
            int var5 = var3.readInt();
            int var6 = var3.readInt();
            List<GoalSelectorDebugRenderer.DebugGoal> var7 = Lists.newArrayList();

            for(int var8 = 0; var8 < var6; ++var8) {
               int var9 = var3.readInt();
               boolean var10 = var3.readBoolean();
               String var11 = var3.readUtf(255);
               var7.add(new GoalSelectorDebugRenderer.DebugGoal(var4, var9, var11, var10));
            }

            this.minecraft.debugRenderer.goalSelectorRenderer.addGoalSelector(var5, var7);
         } else if(ClientboundCustomPayloadPacket.DEBUG_RAIDS.equals(var2)) {
            int var4 = var3.readInt();
            Collection<BlockPos> var5 = Lists.newArrayList();

            for(int var6 = 0; var6 < var4; ++var6) {
               var5.add(var3.readBlockPos());
            }

            this.minecraft.debugRenderer.raidDebugRenderer.setRaidCenters(var5);
         } else if(ClientboundCustomPayloadPacket.DEBUG_BRAIN.equals(var2)) {
            double var4 = var3.readDouble();
            double var6 = var3.readDouble();
            double var8 = var3.readDouble();
            Position var10 = new PositionImpl(var4, var6, var8);
            UUID var11 = var3.readUUID();
            int var12 = var3.readInt();
            String var13 = var3.readUtf();
            String var14 = var3.readUtf();
            int var15 = var3.readInt();
            String var16 = var3.readUtf();
            boolean var17 = var3.readBoolean();
            Path var18;
            if(var17) {
               var18 = Path.createFromStream(var3);
            } else {
               var18 = null;
            }

            boolean var19 = var3.readBoolean();
            VillageDebugRenderer.BrainDump var20 = new VillageDebugRenderer.BrainDump(var11, var12, var13, var14, var15, var10, var16, var18, var19);
            int var21 = var3.readInt();

            for(int var22 = 0; var22 < var21; ++var22) {
               String var23 = var3.readUtf();
               var20.activities.add(var23);
            }

            int var22 = var3.readInt();

            for(int var23 = 0; var23 < var22; ++var23) {
               String var24 = var3.readUtf();
               var20.behaviors.add(var24);
            }

            int var23 = var3.readInt();

            for(int var24 = 0; var24 < var23; ++var24) {
               String var25 = var3.readUtf();
               var20.memories.add(var25);
            }

            int var24 = var3.readInt();

            for(int var25 = 0; var25 < var24; ++var25) {
               BlockPos var26 = var3.readBlockPos();
               var20.pois.add(var26);
            }

            int var25 = var3.readInt();

            for(int var26 = 0; var26 < var25; ++var26) {
               String var27 = var3.readUtf();
               var20.gossips.add(var27);
            }

            this.minecraft.debugRenderer.villageDebugRenderer.addOrUpdateBrainDump(var20);
         } else {
            LOGGER.warn("Unknown custom packed identifier: {}", var2);
         }
      } finally {
         if(var3 != null) {
            var3.release();
         }

      }

   }

   public void handleAddObjective(ClientboundSetObjectivePacket clientboundSetObjectivePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetObjectivePacket, this, (BlockableEventLoop)this.minecraft);
      Scoreboard var2 = this.level.getScoreboard();
      String var3 = clientboundSetObjectivePacket.getObjectiveName();
      if(clientboundSetObjectivePacket.getMethod() == 0) {
         var2.addObjective(var3, ObjectiveCriteria.DUMMY, clientboundSetObjectivePacket.getDisplayName(), clientboundSetObjectivePacket.getRenderType());
      } else if(var2.hasObjective(var3)) {
         Objective var4 = var2.getObjective(var3);
         if(clientboundSetObjectivePacket.getMethod() == 1) {
            var2.removeObjective(var4);
         } else if(clientboundSetObjectivePacket.getMethod() == 2) {
            var4.setRenderType(clientboundSetObjectivePacket.getRenderType());
            var4.setDisplayName(clientboundSetObjectivePacket.getDisplayName());
         }
      }

   }

   public void handleSetScore(ClientboundSetScorePacket clientboundSetScorePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetScorePacket, this, (BlockableEventLoop)this.minecraft);
      Scoreboard var2 = this.level.getScoreboard();
      String var3 = clientboundSetScorePacket.getObjectiveName();
      switch(clientboundSetScorePacket.getMethod()) {
      case CHANGE:
         Objective var4 = var2.getOrCreateObjective(var3);
         Score var5 = var2.getOrCreatePlayerScore(clientboundSetScorePacket.getOwner(), var4);
         var5.setScore(clientboundSetScorePacket.getScore());
         break;
      case REMOVE:
         var2.resetPlayerScore(clientboundSetScorePacket.getOwner(), var2.getObjective(var3));
      }

   }

   public void handleSetDisplayObjective(ClientboundSetDisplayObjectivePacket clientboundSetDisplayObjectivePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetDisplayObjectivePacket, this, (BlockableEventLoop)this.minecraft);
      Scoreboard var2 = this.level.getScoreboard();
      String var3 = clientboundSetDisplayObjectivePacket.getObjectiveName();
      Objective var4 = var3 == null?null:var2.getOrCreateObjective(var3);
      var2.setDisplayObjective(clientboundSetDisplayObjectivePacket.getSlot(), var4);
   }

   public void handleSetPlayerTeamPacket(ClientboundSetPlayerTeamPacket clientboundSetPlayerTeamPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetPlayerTeamPacket, this, (BlockableEventLoop)this.minecraft);
      Scoreboard var2 = this.level.getScoreboard();
      PlayerTeam var3;
      if(clientboundSetPlayerTeamPacket.getMethod() == 0) {
         var3 = var2.addPlayerTeam(clientboundSetPlayerTeamPacket.getName());
      } else {
         var3 = var2.getPlayerTeam(clientboundSetPlayerTeamPacket.getName());
      }

      if(clientboundSetPlayerTeamPacket.getMethod() == 0 || clientboundSetPlayerTeamPacket.getMethod() == 2) {
         var3.setDisplayName(clientboundSetPlayerTeamPacket.getDisplayName());
         var3.setColor(clientboundSetPlayerTeamPacket.getColor());
         var3.unpackOptions(clientboundSetPlayerTeamPacket.getOptions());
         Team.Visibility var4 = Team.Visibility.byName(clientboundSetPlayerTeamPacket.getNametagVisibility());
         if(var4 != null) {
            var3.setNameTagVisibility(var4);
         }

         Team.CollisionRule var5 = Team.CollisionRule.byName(clientboundSetPlayerTeamPacket.getCollisionRule());
         if(var5 != null) {
            var3.setCollisionRule(var5);
         }

         var3.setPlayerPrefix(clientboundSetPlayerTeamPacket.getPlayerPrefix());
         var3.setPlayerSuffix(clientboundSetPlayerTeamPacket.getPlayerSuffix());
      }

      if(clientboundSetPlayerTeamPacket.getMethod() == 0 || clientboundSetPlayerTeamPacket.getMethod() == 3) {
         for(String var5 : clientboundSetPlayerTeamPacket.getPlayers()) {
            var2.addPlayerToTeam(var5, var3);
         }
      }

      if(clientboundSetPlayerTeamPacket.getMethod() == 4) {
         for(String var5 : clientboundSetPlayerTeamPacket.getPlayers()) {
            var2.removePlayerFromTeam(var5, var3);
         }
      }

      if(clientboundSetPlayerTeamPacket.getMethod() == 1) {
         var2.removePlayerTeam(var3);
      }

   }

   public void handleParticleEvent(ClientboundLevelParticlesPacket clientboundLevelParticlesPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundLevelParticlesPacket, this, (BlockableEventLoop)this.minecraft);
      if(clientboundLevelParticlesPacket.getCount() == 0) {
         double var2 = (double)(clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getXDist());
         double var4 = (double)(clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getYDist());
         double var6 = (double)(clientboundLevelParticlesPacket.getMaxSpeed() * clientboundLevelParticlesPacket.getZDist());

         try {
            this.level.addParticle(clientboundLevelParticlesPacket.getParticle(), clientboundLevelParticlesPacket.isOverrideLimiter(), clientboundLevelParticlesPacket.getX(), clientboundLevelParticlesPacket.getY(), clientboundLevelParticlesPacket.getZ(), var2, var4, var6);
         } catch (Throwable var17) {
            LOGGER.warn("Could not spawn particle effect {}", clientboundLevelParticlesPacket.getParticle());
         }
      } else {
         for(int var2 = 0; var2 < clientboundLevelParticlesPacket.getCount(); ++var2) {
            double var3 = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getXDist();
            double var5 = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getYDist();
            double var7 = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getZDist();
            double var9 = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getMaxSpeed();
            double var11 = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getMaxSpeed();
            double var13 = this.random.nextGaussian() * (double)clientboundLevelParticlesPacket.getMaxSpeed();

            try {
               this.level.addParticle(clientboundLevelParticlesPacket.getParticle(), clientboundLevelParticlesPacket.isOverrideLimiter(), clientboundLevelParticlesPacket.getX() + var3, clientboundLevelParticlesPacket.getY() + var5, clientboundLevelParticlesPacket.getZ() + var7, var9, var11, var13);
            } catch (Throwable var16) {
               LOGGER.warn("Could not spawn particle effect {}", clientboundLevelParticlesPacket.getParticle());
               return;
            }
         }
      }

   }

   public void handleUpdateAttributes(ClientboundUpdateAttributesPacket clientboundUpdateAttributesPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundUpdateAttributesPacket, this, (BlockableEventLoop)this.minecraft);
      Entity var2 = this.level.getEntity(clientboundUpdateAttributesPacket.getEntityId());
      if(var2 != null) {
         if(!(var2 instanceof LivingEntity)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
         } else {
            BaseAttributeMap var3 = ((LivingEntity)var2).getAttributes();

            for(ClientboundUpdateAttributesPacket.AttributeSnapshot var5 : clientboundUpdateAttributesPacket.getValues()) {
               AttributeInstance var6 = var3.getInstance(var5.getName());
               if(var6 == null) {
                  var6 = var3.registerAttribute(new RangedAttribute((Attribute)null, var5.getName(), 0.0D, 2.2250738585072014E-308D, Double.MAX_VALUE));
               }

               var6.setBaseValue(var5.getBase());
               var6.removeModifiers();

               for(AttributeModifier var8 : var5.getModifiers()) {
                  var6.addModifier(var8);
               }
            }

         }
      }
   }

   public void handlePlaceRecipe(ClientboundPlaceGhostRecipePacket clientboundPlaceGhostRecipePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundPlaceGhostRecipePacket, this, (BlockableEventLoop)this.minecraft);
      AbstractContainerMenu var2 = this.minecraft.player.containerMenu;
      if(var2.containerId == clientboundPlaceGhostRecipePacket.getContainerId() && var2.isSynched(this.minecraft.player)) {
         this.recipeManager.byKey(clientboundPlaceGhostRecipePacket.getRecipe()).ifPresent((recipe) -> {
            if(this.minecraft.screen instanceof RecipeUpdateListener) {
               RecipeBookComponent var3 = ((RecipeUpdateListener)this.minecraft.screen).getRecipeBookComponent();
               var3.setupGhostRecipe(recipe, var2.slots);
            }

         });
      }
   }

   public void handleLightUpdatePacked(ClientboundLightUpdatePacket clientboundLightUpdatePacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundLightUpdatePacket, this, (BlockableEventLoop)this.minecraft);
      int var2 = clientboundLightUpdatePacket.getX();
      int var3 = clientboundLightUpdatePacket.getZ();
      LevelLightEngine var4 = this.level.getChunkSource().getLightEngine();
      int var5 = clientboundLightUpdatePacket.getSkyYMask();
      int var6 = clientboundLightUpdatePacket.getEmptySkyYMask();
      Iterator<byte[]> var7 = clientboundLightUpdatePacket.getSkyUpdates().iterator();
      this.readSectionList(var2, var3, var4, LightLayer.SKY, var5, var6, var7);
      int var8 = clientboundLightUpdatePacket.getBlockYMask();
      int var9 = clientboundLightUpdatePacket.getEmptyBlockYMask();
      Iterator<byte[]> var10 = clientboundLightUpdatePacket.getBlockUpdates().iterator();
      this.readSectionList(var2, var3, var4, LightLayer.BLOCK, var8, var9, var10);
   }

   public void handleMerchantOffers(ClientboundMerchantOffersPacket clientboundMerchantOffersPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundMerchantOffersPacket, this, (BlockableEventLoop)this.minecraft);
      AbstractContainerMenu var2 = this.minecraft.player.containerMenu;
      if(clientboundMerchantOffersPacket.getContainerId() == var2.containerId && var2 instanceof MerchantMenu) {
         ((MerchantMenu)var2).setOffers(new MerchantOffers(clientboundMerchantOffersPacket.getOffers().createTag()));
         ((MerchantMenu)var2).setXp(clientboundMerchantOffersPacket.getVillagerXp());
         ((MerchantMenu)var2).setMerchantLevel(clientboundMerchantOffersPacket.getVillagerLevel());
         ((MerchantMenu)var2).setShowProgressBar(clientboundMerchantOffersPacket.showProgress());
         ((MerchantMenu)var2).setCanRestock(clientboundMerchantOffersPacket.canRestock());
      }

   }

   public void handleSetChunkCacheRadius(ClientboundSetChunkCacheRadiusPacket clientboundSetChunkCacheRadiusPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetChunkCacheRadiusPacket, this, (BlockableEventLoop)this.minecraft);
      this.serverChunkRadius = clientboundSetChunkCacheRadiusPacket.getRadius();
      this.level.getChunkSource().updateViewRadius(clientboundSetChunkCacheRadiusPacket.getRadius());
   }

   public void handleSetChunkCacheCenter(ClientboundSetChunkCacheCenterPacket clientboundSetChunkCacheCenterPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundSetChunkCacheCenterPacket, this, (BlockableEventLoop)this.minecraft);
      this.level.getChunkSource().updateViewCenter(clientboundSetChunkCacheCenterPacket.getX(), clientboundSetChunkCacheCenterPacket.getZ());
   }

   public void handleBlockBreakAck(ClientboundBlockBreakAckPacket clientboundBlockBreakAckPacket) {
      PacketUtils.ensureRunningOnSameThread(clientboundBlockBreakAckPacket, this, (BlockableEventLoop)this.minecraft);
      this.minecraft.gameMode.handleBlockBreakAck(this.level, clientboundBlockBreakAckPacket.getPos(), clientboundBlockBreakAckPacket.getState(), clientboundBlockBreakAckPacket.action(), clientboundBlockBreakAckPacket.allGood());
   }

   private void readSectionList(int var1, int var2, LevelLightEngine levelLightEngine, LightLayer lightLayer, int var5, int var6, Iterator iterator) {
      for(int var8 = 0; var8 < 18; ++var8) {
         int var9 = -1 + var8;
         boolean var10 = (var5 & 1 << var8) != 0;
         boolean var11 = (var6 & 1 << var8) != 0;
         if(var10 || var11) {
            levelLightEngine.queueSectionData(lightLayer, SectionPos.of(var1, var9, var2), var10?new DataLayer((byte[])((byte[])iterator.next()).clone()):new DataLayer());
            this.level.setSectionDirtyWithNeighbors(var1, var9, var2);
         }
      }

   }

   public Connection getConnection() {
      return this.connection;
   }

   public Collection getOnlinePlayers() {
      return this.playerInfoMap.values();
   }

   @Nullable
   public PlayerInfo getPlayerInfo(UUID uUID) {
      return (PlayerInfo)this.playerInfoMap.get(uUID);
   }

   @Nullable
   public PlayerInfo getPlayerInfo(String string) {
      for(PlayerInfo var3 : this.playerInfoMap.values()) {
         if(var3.getProfile().getName().equals(string)) {
            return var3;
         }
      }

      return null;
   }

   public GameProfile getLocalGameProfile() {
      return this.localGameProfile;
   }

   public ClientAdvancements getAdvancements() {
      return this.advancements;
   }

   public CommandDispatcher getCommands() {
      return this.commands;
   }

   public MultiPlayerLevel getLevel() {
      return this.level;
   }

   public TagManager getTags() {
      return this.tags;
   }

   public DebugQueryHandler getDebugQueryHandler() {
      return this.debugQueryHandler;
   }

   public UUID getId() {
      return this.id;
   }
}
