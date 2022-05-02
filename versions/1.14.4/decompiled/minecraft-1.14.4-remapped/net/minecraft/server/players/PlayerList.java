package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.DemoMode;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.PlayerIO;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerList {
   public static final File USERBANLIST_FILE = new File("banned-players.json");
   public static final File IPBANLIST_FILE = new File("banned-ips.json");
   public static final File OPLIST_FILE = new File("ops.json");
   public static final File WHITELIST_FILE = new File("whitelist.json");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleDateFormat BAN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");
   private final MinecraftServer server;
   private final List players = Lists.newArrayList();
   private final Map playersByUUID = Maps.newHashMap();
   private final UserBanList bans;
   private final IpBanList ipBans;
   private final ServerOpList ops;
   private final UserWhiteList whitelist;
   private final Map stats;
   private final Map advancements;
   private PlayerIO playerIo;
   private boolean doWhiteList;
   protected final int maxPlayers;
   private int viewDistance;
   private GameType overrideGameMode;
   private boolean allowCheatsForAllPlayers;
   private int sendAllPlayerInfoIn;

   public PlayerList(MinecraftServer server, int maxPlayers) {
      this.bans = new UserBanList(USERBANLIST_FILE);
      this.ipBans = new IpBanList(IPBANLIST_FILE);
      this.ops = new ServerOpList(OPLIST_FILE);
      this.whitelist = new UserWhiteList(WHITELIST_FILE);
      this.stats = Maps.newHashMap();
      this.advancements = Maps.newHashMap();
      this.server = server;
      this.maxPlayers = maxPlayers;
      this.getBans().setEnabled(true);
      this.getIpBans().setEnabled(true);
   }

   public void placeNewPlayer(Connection connection, ServerPlayer serverPlayer) {
      GameProfile var3 = serverPlayer.getGameProfile();
      GameProfileCache var4 = this.server.getProfileCache();
      GameProfile var5 = var4.get(var3.getId());
      String var6 = var5 == null?var3.getName():var5.getName();
      var4.add(var3);
      CompoundTag var7 = this.load(serverPlayer);
      ServerLevel var8 = this.server.getLevel(serverPlayer.dimension);
      serverPlayer.setLevel(var8);
      serverPlayer.gameMode.setLevel((ServerLevel)serverPlayer.level);
      String var9 = "local";
      if(connection.getRemoteAddress() != null) {
         var9 = connection.getRemoteAddress().toString();
      }

      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", serverPlayer.getName().getString(), var9, Integer.valueOf(serverPlayer.getId()), Double.valueOf(serverPlayer.x), Double.valueOf(serverPlayer.y), Double.valueOf(serverPlayer.z));
      LevelData var10 = var8.getLevelData();
      this.updatePlayerGameMode(serverPlayer, (ServerPlayer)null, var8);
      ServerGamePacketListenerImpl var11 = new ServerGamePacketListenerImpl(this.server, connection, serverPlayer);
      var11.send(new ClientboundLoginPacket(serverPlayer.getId(), serverPlayer.gameMode.getGameModeForPlayer(), var10.isHardcore(), var8.dimension.getType(), this.getMaxPlayers(), var10.getGeneratorType(), this.viewDistance, var8.getGameRules().getBoolean(GameRules.RULE_REDUCEDDEBUGINFO)));
      var11.send(new ClientboundCustomPayloadPacket(ClientboundCustomPayloadPacket.BRAND, (new FriendlyByteBuf(Unpooled.buffer())).writeUtf(this.getServer().getServerModName())));
      var11.send(new ClientboundChangeDifficultyPacket(var10.getDifficulty(), var10.isDifficultyLocked()));
      var11.send(new ClientboundPlayerAbilitiesPacket(serverPlayer.abilities));
      var11.send(new ClientboundSetCarriedItemPacket(serverPlayer.inventory.selected));
      var11.send(new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes()));
      var11.send(new ClientboundUpdateTagsPacket(this.server.getTags()));
      this.sendPlayerPermissionLevel(serverPlayer);
      serverPlayer.getStats().markAllDirty();
      serverPlayer.getRecipeBook().sendInitialRecipeBook(serverPlayer);
      this.updateEntireScoreboard(var8.getScoreboard(), serverPlayer);
      this.server.invalidateStatus();
      Component var12;
      if(serverPlayer.getGameProfile().getName().equalsIgnoreCase(var6)) {
         var12 = new TranslatableComponent("multiplayer.player.joined", new Object[]{serverPlayer.getDisplayName()});
      } else {
         var12 = new TranslatableComponent("multiplayer.player.joined.renamed", new Object[]{serverPlayer.getDisplayName(), var6});
      }

      this.broadcastMessage(var12.withStyle(ChatFormatting.YELLOW));
      var11.teleport(serverPlayer.x, serverPlayer.y, serverPlayer.z, serverPlayer.yRot, serverPlayer.xRot);
      this.players.add(serverPlayer);
      this.playersByUUID.put(serverPlayer.getUUID(), serverPlayer);
      this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, new ServerPlayer[]{serverPlayer}));

      for(int var13 = 0; var13 < this.players.size(); ++var13) {
         serverPlayer.connection.send(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, new ServerPlayer[]{(ServerPlayer)this.players.get(var13)}));
      }

      var8.addNewPlayer(serverPlayer);
      this.server.getCustomBossEvents().onPlayerConnect(serverPlayer);
      this.sendLevelInfo(serverPlayer, var8);
      if(!this.server.getResourcePack().isEmpty()) {
         serverPlayer.sendTexturePack(this.server.getResourcePack(), this.server.getResourcePackHash());
      }

      for(MobEffectInstance var14 : serverPlayer.getActiveEffects()) {
         var11.send(new ClientboundUpdateMobEffectPacket(serverPlayer.getId(), var14));
      }

      if(var7 != null && var7.contains("RootVehicle", 10)) {
         CompoundTag var13 = var7.getCompound("RootVehicle");
         Entity var14 = EntityType.loadEntityRecursive(var13.getCompound("Entity"), var8, (var1) -> {
            return !var8.addWithUUID(var1)?null:var1;
         });
         if(var14 != null) {
            UUID var15 = var13.getUUID("Attach");
            if(var14.getUUID().equals(var15)) {
               serverPlayer.startRiding(var14, true);
            } else {
               for(Entity var17 : var14.getIndirectPassengers()) {
                  if(var17.getUUID().equals(var15)) {
                     serverPlayer.startRiding(var17, true);
                     break;
                  }
               }
            }

            if(!serverPlayer.isPassenger()) {
               LOGGER.warn("Couldn\'t reattach entity to player");
               var8.despawn(var14);

               for(Entity var17 : var14.getIndirectPassengers()) {
                  var8.despawn(var17);
               }
            }
         }
      }

      serverPlayer.initMenu();
   }

   protected void updateEntireScoreboard(ServerScoreboard serverScoreboard, ServerPlayer serverPlayer) {
      Set<Objective> var3 = Sets.newHashSet();

      for(PlayerTeam var5 : serverScoreboard.getPlayerTeams()) {
         serverPlayer.connection.send(new ClientboundSetPlayerTeamPacket(var5, 0));
      }

      for(int var4 = 0; var4 < 19; ++var4) {
         Objective var5 = serverScoreboard.getDisplayObjective(var4);
         if(var5 != null && !var3.contains(var5)) {
            for(Packet<?> var8 : serverScoreboard.getStartTrackingPackets(var5)) {
               serverPlayer.connection.send(var8);
            }

            var3.add(var5);
         }
      }

   }

   public void setLevel(ServerLevel level) {
      this.playerIo = level.getLevelStorage();
      level.getWorldBorder().addListener(new BorderChangeListener() {
         public void onBorderSizeSet(WorldBorder worldBorder, double var2) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(worldBorder, ClientboundSetBorderPacket.Type.SET_SIZE));
         }

         public void onBorderSizeLerping(WorldBorder worldBorder, double var2, double var4, long var6) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(worldBorder, ClientboundSetBorderPacket.Type.LERP_SIZE));
         }

         public void onBorderCenterSet(WorldBorder worldBorder, double var2, double var4) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(worldBorder, ClientboundSetBorderPacket.Type.SET_CENTER));
         }

         public void onBorderSetWarningTime(WorldBorder worldBorder, int var2) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(worldBorder, ClientboundSetBorderPacket.Type.SET_WARNING_TIME));
         }

         public void onBorderSetWarningBlocks(WorldBorder worldBorder, int var2) {
            PlayerList.this.broadcastAll(new ClientboundSetBorderPacket(worldBorder, ClientboundSetBorderPacket.Type.SET_WARNING_BLOCKS));
         }

         public void onBorderSetDamagePerBlock(WorldBorder worldBorder, double var2) {
         }

         public void onBorderSetDamageSafeZOne(WorldBorder worldBorder, double var2) {
         }
      });
   }

   @Nullable
   public CompoundTag load(ServerPlayer serverPlayer) {
      CompoundTag compoundTag = this.server.getLevel(DimensionType.OVERWORLD).getLevelData().getLoadedPlayerTag();
      CompoundTag var3;
      if(serverPlayer.getName().getString().equals(this.server.getSingleplayerName()) && compoundTag != null) {
         var3 = compoundTag;
         serverPlayer.load(compoundTag);
         LOGGER.debug("loading single player");
      } else {
         var3 = this.playerIo.load(serverPlayer);
      }

      return var3;
   }

   protected void save(ServerPlayer serverPlayer) {
      this.playerIo.save(serverPlayer);
      ServerStatsCounter var2 = (ServerStatsCounter)this.stats.get(serverPlayer.getUUID());
      if(var2 != null) {
         var2.save();
      }

      PlayerAdvancements var3 = (PlayerAdvancements)this.advancements.get(serverPlayer.getUUID());
      if(var3 != null) {
         var3.save();
      }

   }

   public void remove(ServerPlayer serverPlayer) {
      ServerLevel var2 = serverPlayer.getLevel();
      serverPlayer.awardStat(Stats.LEAVE_GAME);
      this.save(serverPlayer);
      if(serverPlayer.isPassenger()) {
         Entity var3 = serverPlayer.getRootVehicle();
         if(var3.hasOnePlayerPassenger()) {
            LOGGER.debug("Removing player mount");
            serverPlayer.stopRiding();
            var2.despawn(var3);

            for(Entity var5 : var3.getIndirectPassengers()) {
               var2.despawn(var5);
            }

            var2.getChunk(serverPlayer.xChunk, serverPlayer.zChunk).markUnsaved();
         }
      }

      serverPlayer.unRide();
      var2.removePlayerImmediately(serverPlayer);
      serverPlayer.getAdvancements().stopListening();
      this.players.remove(serverPlayer);
      this.server.getCustomBossEvents().onPlayerDisconnect(serverPlayer);
      UUID var3 = serverPlayer.getUUID();
      ServerPlayer var4 = (ServerPlayer)this.playersByUUID.get(var3);
      if(var4 == serverPlayer) {
         this.playersByUUID.remove(var3);
         this.stats.remove(var3);
         this.advancements.remove(var3);
      }

      this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, new ServerPlayer[]{serverPlayer}));
   }

   @Nullable
   public Component canPlayerLogin(SocketAddress socketAddress, GameProfile gameProfile) {
      if(this.bans.isBanned(gameProfile)) {
         UserBanListEntry var3 = (UserBanListEntry)this.bans.get(gameProfile);
         Component var4 = new TranslatableComponent("multiplayer.disconnect.banned.reason", new Object[]{var3.getReason()});
         if(var3.getExpires() != null) {
            var4.append((Component)(new TranslatableComponent("multiplayer.disconnect.banned.expiration", new Object[]{BAN_DATE_FORMAT.format(var3.getExpires())})));
         }

         return var4;
      } else if(!this.isWhiteListed(gameProfile)) {
         return new TranslatableComponent("multiplayer.disconnect.not_whitelisted", new Object[0]);
      } else if(this.ipBans.isBanned(socketAddress)) {
         IpBanListEntry var3 = this.ipBans.get(socketAddress);
         Component var4 = new TranslatableComponent("multiplayer.disconnect.banned_ip.reason", new Object[]{var3.getReason()});
         if(var3.getExpires() != null) {
            var4.append((Component)(new TranslatableComponent("multiplayer.disconnect.banned_ip.expiration", new Object[]{BAN_DATE_FORMAT.format(var3.getExpires())})));
         }

         return var4;
      } else {
         return this.players.size() >= this.maxPlayers && !this.canBypassPlayerLimit(gameProfile)?new TranslatableComponent("multiplayer.disconnect.server_full", new Object[0]):null;
      }
   }

   public ServerPlayer getPlayerForLogin(GameProfile gameProfile) {
      UUID var2 = Player.createPlayerUUID(gameProfile);
      List<ServerPlayer> var3 = Lists.newArrayList();

      for(int var4 = 0; var4 < this.players.size(); ++var4) {
         ServerPlayer var5 = (ServerPlayer)this.players.get(var4);
         if(var5.getUUID().equals(var2)) {
            var3.add(var5);
         }
      }

      ServerPlayer var4 = (ServerPlayer)this.playersByUUID.get(gameProfile.getId());
      if(var4 != null && !var3.contains(var4)) {
         var3.add(var4);
      }

      for(ServerPlayer var6 : var3) {
         var6.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.duplicate_login", new Object[0]));
      }

      ServerPlayerGameMode var5;
      if(this.server.isDemo()) {
         var5 = new DemoMode(this.server.getLevel(DimensionType.OVERWORLD));
      } else {
         var5 = new ServerPlayerGameMode(this.server.getLevel(DimensionType.OVERWORLD));
      }

      return new ServerPlayer(this.server, this.server.getLevel(DimensionType.OVERWORLD), gameProfile, var5);
   }

   public ServerPlayer respawn(ServerPlayer var1, DimensionType dimensionType, boolean var3) {
      this.players.remove(var1);
      var1.getLevel().removePlayerImmediately(var1);
      BlockPos var4 = var1.getRespawnPosition();
      boolean var5 = var1.isRespawnForced();
      var1.dimension = dimensionType;
      ServerPlayerGameMode var6;
      if(this.server.isDemo()) {
         var6 = new DemoMode(this.server.getLevel(var1.dimension));
      } else {
         var6 = new ServerPlayerGameMode(this.server.getLevel(var1.dimension));
      }

      ServerPlayer var7 = new ServerPlayer(this.server, this.server.getLevel(var1.dimension), var1.getGameProfile(), var6);
      var7.connection = var1.connection;
      var7.restoreFrom(var1, var3);
      var7.setId(var1.getId());
      var7.setMainArm(var1.getMainArm());

      for(String var9 : var1.getTags()) {
         var7.addTag(var9);
      }

      ServerLevel var8 = this.server.getLevel(var1.dimension);
      this.updatePlayerGameMode(var7, var1, var8);
      if(var4 != null) {
         Optional<Vec3> var9 = Player.checkBedValidRespawnPosition(this.server.getLevel(var1.dimension), var4, var5);
         if(var9.isPresent()) {
            Vec3 var10 = (Vec3)var9.get();
            var7.moveTo(var10.x, var10.y, var10.z, 0.0F, 0.0F);
            var7.setRespawnPosition(var4, var5);
         } else {
            var7.connection.send(new ClientboundGameEventPacket(0, 0.0F));
         }
      }

      while(!var8.noCollision(var7) && var7.y < 256.0D) {
         var7.setPos(var7.x, var7.y + 1.0D, var7.z);
      }

      LevelData var9 = var7.level.getLevelData();
      var7.connection.send(new ClientboundRespawnPacket(var7.dimension, var9.getGeneratorType(), var7.gameMode.getGameModeForPlayer()));
      BlockPos var10 = var8.getSharedSpawnPos();
      var7.connection.teleport(var7.x, var7.y, var7.z, var7.yRot, var7.xRot);
      var7.connection.send(new ClientboundSetSpawnPositionPacket(var10));
      var7.connection.send(new ClientboundChangeDifficultyPacket(var9.getDifficulty(), var9.isDifficultyLocked()));
      var7.connection.send(new ClientboundSetExperiencePacket(var7.experienceProgress, var7.totalExperience, var7.experienceLevel));
      this.sendLevelInfo(var7, var8);
      this.sendPlayerPermissionLevel(var7);
      var8.addRespawnedPlayer(var7);
      this.players.add(var7);
      this.playersByUUID.put(var7.getUUID(), var7);
      var7.initMenu();
      var7.setHealth(var7.getHealth());
      return var7;
   }

   public void sendPlayerPermissionLevel(ServerPlayer serverPlayer) {
      GameProfile var2 = serverPlayer.getGameProfile();
      int var3 = this.server.getProfilePermissions(var2);
      this.sendPlayerPermissionLevel(serverPlayer, var3);
   }

   public void tick() {
      if(++this.sendAllPlayerInfoIn > 600) {
         this.broadcastAll(new ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.UPDATE_LATENCY, this.players));
         this.sendAllPlayerInfoIn = 0;
      }

   }

   public void broadcastAll(Packet packet) {
      for(int var2 = 0; var2 < this.players.size(); ++var2) {
         ((ServerPlayer)this.players.get(var2)).connection.send(packet);
      }

   }

   public void broadcastAll(Packet packet, DimensionType dimensionType) {
      for(int var3 = 0; var3 < this.players.size(); ++var3) {
         ServerPlayer var4 = (ServerPlayer)this.players.get(var3);
         if(var4.dimension == dimensionType) {
            var4.connection.send(packet);
         }
      }

   }

   public void broadcastToTeam(Player player, Component component) {
      Team var3 = player.getTeam();
      if(var3 != null) {
         for(String var6 : var3.getPlayers()) {
            ServerPlayer var7 = this.getPlayerByName(var6);
            if(var7 != null && var7 != player) {
               var7.sendMessage(component);
            }
         }

      }
   }

   public void broadcastToAllExceptTeam(Player player, Component component) {
      Team var3 = player.getTeam();
      if(var3 == null) {
         this.broadcastMessage(component);
      } else {
         for(int var4 = 0; var4 < this.players.size(); ++var4) {
            ServerPlayer var5 = (ServerPlayer)this.players.get(var4);
            if(var5.getTeam() != var3) {
               var5.sendMessage(component);
            }
         }

      }
   }

   public String[] getPlayerNamesArray() {
      String[] strings = new String[this.players.size()];

      for(int var2 = 0; var2 < this.players.size(); ++var2) {
         strings[var2] = ((ServerPlayer)this.players.get(var2)).getGameProfile().getName();
      }

      return strings;
   }

   public UserBanList getBans() {
      return this.bans;
   }

   public IpBanList getIpBans() {
      return this.ipBans;
   }

   public void op(GameProfile gameProfile) {
      this.ops.add(new ServerOpListEntry(gameProfile, this.server.getOperatorUserPermissionLevel(), this.ops.canBypassPlayerLimit(gameProfile)));
      ServerPlayer var2 = this.getPlayer(gameProfile.getId());
      if(var2 != null) {
         this.sendPlayerPermissionLevel(var2);
      }

   }

   public void deop(GameProfile gameProfile) {
      this.ops.remove(gameProfile);
      ServerPlayer var2 = this.getPlayer(gameProfile.getId());
      if(var2 != null) {
         this.sendPlayerPermissionLevel(var2);
      }

   }

   private void sendPlayerPermissionLevel(ServerPlayer serverPlayer, int var2) {
      if(serverPlayer.connection != null) {
         byte var3;
         if(var2 <= 0) {
            var3 = 24;
         } else if(var2 >= 4) {
            var3 = 28;
         } else {
            var3 = (byte)(24 + var2);
         }

         serverPlayer.connection.send(new ClientboundEntityEventPacket(serverPlayer, var3));
      }

      this.server.getCommands().sendCommands(serverPlayer);
   }

   public boolean isWhiteListed(GameProfile gameProfile) {
      return !this.doWhiteList || this.ops.contains(gameProfile) || this.whitelist.contains(gameProfile);
   }

   public boolean isOp(GameProfile gameProfile) {
      return this.ops.contains(gameProfile) || this.server.isSingleplayerOwner(gameProfile) && this.server.getLevel(DimensionType.OVERWORLD).getLevelData().getAllowCommands() || this.allowCheatsForAllPlayers;
   }

   @Nullable
   public ServerPlayer getPlayerByName(String string) {
      for(ServerPlayer var3 : this.players) {
         if(var3.getGameProfile().getName().equalsIgnoreCase(string)) {
            return var3;
         }
      }

      return null;
   }

   public void broadcast(@Nullable Player player, double var2, double var4, double var6, double var8, DimensionType dimensionType, Packet packet) {
      for(int var12 = 0; var12 < this.players.size(); ++var12) {
         ServerPlayer var13 = (ServerPlayer)this.players.get(var12);
         if(var13 != player && var13.dimension == dimensionType) {
            double var14 = var2 - var13.x;
            double var16 = var4 - var13.y;
            double var18 = var6 - var13.z;
            if(var14 * var14 + var16 * var16 + var18 * var18 < var8 * var8) {
               var13.connection.send(packet);
            }
         }
      }

   }

   public void saveAll() {
      for(int var1 = 0; var1 < this.players.size(); ++var1) {
         this.save((ServerPlayer)this.players.get(var1));
      }

   }

   public UserWhiteList getWhiteList() {
      return this.whitelist;
   }

   public String[] getWhiteListNames() {
      return this.whitelist.getUserList();
   }

   public ServerOpList getOps() {
      return this.ops;
   }

   public String[] getOpNames() {
      return this.ops.getUserList();
   }

   public void reloadWhiteList() {
   }

   public void sendLevelInfo(ServerPlayer serverPlayer, ServerLevel serverLevel) {
      WorldBorder var3 = this.server.getLevel(DimensionType.OVERWORLD).getWorldBorder();
      serverPlayer.connection.send(new ClientboundSetBorderPacket(var3, ClientboundSetBorderPacket.Type.INITIALIZE));
      serverPlayer.connection.send(new ClientboundSetTimePacket(serverLevel.getGameTime(), serverLevel.getDayTime(), serverLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)));
      BlockPos var4 = serverLevel.getSharedSpawnPos();
      serverPlayer.connection.send(new ClientboundSetSpawnPositionPacket(var4));
      if(serverLevel.isRaining()) {
         serverPlayer.connection.send(new ClientboundGameEventPacket(1, 0.0F));
         serverPlayer.connection.send(new ClientboundGameEventPacket(7, serverLevel.getRainLevel(1.0F)));
         serverPlayer.connection.send(new ClientboundGameEventPacket(8, serverLevel.getThunderLevel(1.0F)));
      }

   }

   public void sendAllPlayerInfo(ServerPlayer serverPlayer) {
      serverPlayer.refreshContainer(serverPlayer.inventoryMenu);
      serverPlayer.resetSentInfo();
      serverPlayer.connection.send(new ClientboundSetCarriedItemPacket(serverPlayer.inventory.selected));
   }

   public int getPlayerCount() {
      return this.players.size();
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public boolean isUsingWhitelist() {
      return this.doWhiteList;
   }

   public void setUsingWhiteList(boolean usingWhiteList) {
      this.doWhiteList = usingWhiteList;
   }

   public List getPlayersWithAddress(String string) {
      List<ServerPlayer> list = Lists.newArrayList();

      for(ServerPlayer var4 : this.players) {
         if(var4.getIpAddress().equals(string)) {
            list.add(var4);
         }
      }

      return list;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public CompoundTag getSingleplayerData() {
      return null;
   }

   public void setOverrideGameMode(GameType overrideGameMode) {
      this.overrideGameMode = overrideGameMode;
   }

   private void updatePlayerGameMode(ServerPlayer var1, ServerPlayer var2, LevelAccessor levelAccessor) {
      if(var2 != null) {
         var1.gameMode.setGameModeForPlayer(var2.gameMode.getGameModeForPlayer());
      } else if(this.overrideGameMode != null) {
         var1.gameMode.setGameModeForPlayer(this.overrideGameMode);
      }

      var1.gameMode.updateGameMode(levelAccessor.getLevelData().getGameType());
   }

   public void setAllowCheatsForAllPlayers(boolean allowCheatsForAllPlayers) {
      this.allowCheatsForAllPlayers = allowCheatsForAllPlayers;
   }

   public void removeAll() {
      for(int var1 = 0; var1 < this.players.size(); ++var1) {
         ((ServerPlayer)this.players.get(var1)).connection.disconnect(new TranslatableComponent("multiplayer.disconnect.server_shutdown", new Object[0]));
      }

   }

   public void broadcastMessage(Component component, boolean var2) {
      this.server.sendMessage(component);
      ChatType var3 = var2?ChatType.SYSTEM:ChatType.CHAT;
      this.broadcastAll(new ClientboundChatPacket(component, var3));
   }

   public void broadcastMessage(Component component) {
      this.broadcastMessage(component, true);
   }

   public ServerStatsCounter getPlayerStats(Player player) {
      UUID var2 = player.getUUID();
      ServerStatsCounter var3 = var2 == null?null:(ServerStatsCounter)this.stats.get(var2);
      if(var3 == null) {
         File var4 = new File(this.server.getLevel(DimensionType.OVERWORLD).getLevelStorage().getFolder(), "stats");
         File var5 = new File(var4, var2 + ".json");
         if(!var5.exists()) {
            File var6 = new File(var4, player.getName().getString() + ".json");
            if(var6.exists() && var6.isFile()) {
               var6.renameTo(var5);
            }
         }

         var3 = new ServerStatsCounter(this.server, var5);
         this.stats.put(var2, var3);
      }

      return var3;
   }

   public PlayerAdvancements getPlayerAdvancements(ServerPlayer serverPlayer) {
      UUID var2 = serverPlayer.getUUID();
      PlayerAdvancements var3 = (PlayerAdvancements)this.advancements.get(var2);
      if(var3 == null) {
         File var4 = new File(this.server.getLevel(DimensionType.OVERWORLD).getLevelStorage().getFolder(), "advancements");
         File var5 = new File(var4, var2 + ".json");
         var3 = new PlayerAdvancements(this.server, var5, serverPlayer);
         this.advancements.put(var2, var3);
      }

      var3.setPlayer(serverPlayer);
      return var3;
   }

   public void setViewDistance(int viewDistance) {
      this.viewDistance = viewDistance;
      this.broadcastAll(new ClientboundSetChunkCacheRadiusPacket(viewDistance));

      for(ServerLevel var3 : this.server.getAllLevels()) {
         if(var3 != null) {
            var3.getChunkSource().setViewDistance(viewDistance);
         }
      }

   }

   public List getPlayers() {
      return this.players;
   }

   @Nullable
   public ServerPlayer getPlayer(UUID uUID) {
      return (ServerPlayer)this.playersByUUID.get(uUID);
   }

   public boolean canBypassPlayerLimit(GameProfile gameProfile) {
      return false;
   }

   public void reloadResources() {
      for(PlayerAdvancements var2 : this.advancements.values()) {
         var2.reload();
      }

      this.broadcastAll(new ClientboundUpdateTagsPacket(this.server.getTags()));
      ClientboundUpdateRecipesPacket var1 = new ClientboundUpdateRecipesPacket(this.server.getRecipeManager().getRecipes());

      for(ServerPlayer var3 : this.players) {
         var3.connection.send(var1);
         var3.getRecipeBook().sendInitialRecipeBook(var3);
      }

   }

   public boolean isAllowCheatsForAllPlayers() {
      return this.allowCheatsForAllPlayers;
   }
}
