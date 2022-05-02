package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.Random;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundHorseScreenOpenPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.network.protocol.game.ClientboundOpenBookPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerLookAtPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundResourcePackPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetCameraPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerRecipeBook;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.HorseInventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ServerItemCooldowns;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerPlayer extends Player implements ContainerListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private String language = "en_US";
   public ServerGamePacketListenerImpl connection;
   public final MinecraftServer server;
   public final ServerPlayerGameMode gameMode;
   private final List entitiesToRemove = Lists.newLinkedList();
   private final PlayerAdvancements advancements;
   private final ServerStatsCounter stats;
   private float lastRecordedHealthAndAbsorption = Float.MIN_VALUE;
   private int lastRecordedFoodLevel = Integer.MIN_VALUE;
   private int lastRecordedAirLevel = Integer.MIN_VALUE;
   private int lastRecordedArmor = Integer.MIN_VALUE;
   private int lastRecordedLevel = Integer.MIN_VALUE;
   private int lastRecordedExperience = Integer.MIN_VALUE;
   private float lastSentHealth = -1.0E8F;
   private int lastSentFood = -99999999;
   private boolean lastFoodSaturationZero = true;
   private int lastSentExp = -99999999;
   private int spawnInvulnerableTime = 60;
   private ChatVisiblity chatVisibility;
   private boolean canChatColor = true;
   private long lastActionTime = Util.getMillis();
   private Entity camera;
   private boolean isChangingDimension;
   private boolean seenCredits;
   private final ServerRecipeBook recipeBook;
   private Vec3 levitationStartPos;
   private int levitationStartTime;
   private boolean disconnected;
   @Nullable
   private Vec3 enteredNetherPosition;
   private SectionPos lastSectionPos = SectionPos.of(0, 0, 0);
   private int containerCounter;
   public boolean ignoreSlotUpdateHack;
   public int latency;
   public boolean wonGame;

   public ServerPlayer(MinecraftServer server, ServerLevel serverLevel, GameProfile gameProfile, ServerPlayerGameMode gameMode) {
      super(serverLevel, gameProfile);
      gameMode.player = this;
      this.gameMode = gameMode;
      this.server = server;
      this.recipeBook = new ServerRecipeBook(server.getRecipeManager());
      this.stats = server.getPlayerList().getPlayerStats(this);
      this.advancements = server.getPlayerList().getPlayerAdvancements(this);
      this.maxUpStep = 1.0F;
      this.fudgeSpawnLocation(serverLevel);
   }

   private void fudgeSpawnLocation(ServerLevel serverLevel) {
      BlockPos var2 = serverLevel.getSharedSpawnPos();
      if(serverLevel.dimension.isHasSkyLight() && serverLevel.getLevelData().getGameType() != GameType.ADVENTURE) {
         int var3 = Math.max(0, this.server.getSpawnRadius(serverLevel));
         int var4 = Mth.floor(serverLevel.getWorldBorder().getDistanceToBorder((double)var2.getX(), (double)var2.getZ()));
         if(var4 < var3) {
            var3 = var4;
         }

         if(var4 <= 1) {
            var3 = 1;
         }

         long var5 = (long)(var3 * 2 + 1);
         long var7 = var5 * var5;
         int var9 = var7 > 2147483647L?Integer.MAX_VALUE:(int)var7;
         int var10 = this.getCoprime(var9);
         int var11 = (new Random()).nextInt(var9);

         for(int var12 = 0; var12 < var9; ++var12) {
            int var13 = (var11 + var10 * var12) % var9;
            int var14 = var13 % (var3 * 2 + 1);
            int var15 = var13 / (var3 * 2 + 1);
            BlockPos var16 = serverLevel.getDimension().getValidSpawnPosition(var2.getX() + var14 - var3, var2.getZ() + var15 - var3, false);
            if(var16 != null) {
               this.moveTo(var16, 0.0F, 0.0F);
               if(serverLevel.noCollision(this)) {
                  break;
               }
            }
         }
      } else {
         this.moveTo(var2, 0.0F, 0.0F);

         while(!serverLevel.noCollision(this) && this.y < 255.0D) {
            this.setPos(this.x, this.y + 1.0D, this.z);
         }
      }

   }

   private int getCoprime(int i) {
      return i <= 16?i - 1:17;
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("playerGameType", 99)) {
         if(this.getServer().getForceGameType()) {
            this.gameMode.setGameModeForPlayer(this.getServer().getDefaultGameType());
         } else {
            this.gameMode.setGameModeForPlayer(GameType.byId(compoundTag.getInt("playerGameType")));
         }
      }

      if(compoundTag.contains("enteredNetherPosition", 10)) {
         CompoundTag compoundTag = compoundTag.getCompound("enteredNetherPosition");
         this.enteredNetherPosition = new Vec3(compoundTag.getDouble("x"), compoundTag.getDouble("y"), compoundTag.getDouble("z"));
      }

      this.seenCredits = compoundTag.getBoolean("seenCredits");
      if(compoundTag.contains("recipeBook", 10)) {
         this.recipeBook.fromNbt(compoundTag.getCompound("recipeBook"));
      }

      if(this.isSleeping()) {
         this.stopSleeping();
      }

   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("playerGameType", this.gameMode.getGameModeForPlayer().getId());
      compoundTag.putBoolean("seenCredits", this.seenCredits);
      if(this.enteredNetherPosition != null) {
         CompoundTag compoundTag = new CompoundTag();
         compoundTag.putDouble("x", this.enteredNetherPosition.x);
         compoundTag.putDouble("y", this.enteredNetherPosition.y);
         compoundTag.putDouble("z", this.enteredNetherPosition.z);
         compoundTag.put("enteredNetherPosition", compoundTag);
      }

      Entity var2 = this.getRootVehicle();
      Entity var3 = this.getVehicle();
      if(var3 != null && var2 != this && var2.hasOnePlayerPassenger()) {
         CompoundTag var4 = new CompoundTag();
         CompoundTag var5 = new CompoundTag();
         var2.save(var5);
         var4.putUUID("Attach", var3.getUUID());
         var4.put("Entity", var5);
         compoundTag.put("RootVehicle", var4);
      }

      compoundTag.put("recipeBook", this.recipeBook.toNbt());
   }

   public void setExperiencePoints(int experiencePoints) {
      float var2 = (float)this.getXpNeededForNextLevel();
      float var3 = (var2 - 1.0F) / var2;
      this.experienceProgress = Mth.clamp((float)experiencePoints / var2, 0.0F, var3);
      this.lastSentExp = -1;
   }

   public void setExperienceLevels(int experienceLevels) {
      this.experienceLevel = experienceLevels;
      this.lastSentExp = -1;
   }

   public void giveExperienceLevels(int i) {
      super.giveExperienceLevels(i);
      this.lastSentExp = -1;
   }

   public void onEnchantmentPerformed(ItemStack itemStack, int var2) {
      super.onEnchantmentPerformed(itemStack, var2);
      this.lastSentExp = -1;
   }

   public void initMenu() {
      this.containerMenu.addSlotListener(this);
   }

   public void onEnterCombat() {
      super.onEnterCombat();
      this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTER_COMBAT));
   }

   public void onLeaveCombat() {
      super.onLeaveCombat();
      this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.END_COMBAT));
   }

   protected void onInsideBlock(BlockState blockState) {
      CriteriaTriggers.ENTER_BLOCK.trigger(this, blockState);
   }

   protected ItemCooldowns createItemCooldowns() {
      return new ServerItemCooldowns(this);
   }

   public void tick() {
      this.gameMode.tick();
      --this.spawnInvulnerableTime;
      if(this.invulnerableTime > 0) {
         --this.invulnerableTime;
      }

      this.containerMenu.broadcastChanges();
      if(!this.level.isClientSide && !this.containerMenu.stillValid(this)) {
         this.closeContainer();
         this.containerMenu = this.inventoryMenu;
      }

      while(!this.entitiesToRemove.isEmpty()) {
         int var1 = Math.min(this.entitiesToRemove.size(), Integer.MAX_VALUE);
         int[] vars2 = new int[var1];
         Iterator<Integer> var3 = this.entitiesToRemove.iterator();
         int var4 = 0;

         while(var3.hasNext() && var4 < var1) {
            vars2[var4++] = ((Integer)var3.next()).intValue();
            var3.remove();
         }

         this.connection.send(new ClientboundRemoveEntitiesPacket(vars2));
      }

      Entity var1 = this.getCamera();
      if(var1 != this) {
         if(var1.isAlive()) {
            this.absMoveTo(var1.x, var1.y, var1.z, var1.yRot, var1.xRot);
            this.getLevel().getChunkSource().move(this);
            if(this.isSneaking()) {
               this.setCamera(this);
            }
         } else {
            this.setCamera(this);
         }
      }

      CriteriaTriggers.TICK.trigger(this);
      if(this.levitationStartPos != null) {
         CriteriaTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.tickCount - this.levitationStartTime);
      }

      this.advancements.flushDirty(this);
   }

   public void doTick() {
      try {
         if(!this.isSpectator() || this.level.hasChunkAt(new BlockPos(this))) {
            super.tick();
         }

         for(int var1 = 0; var1 < this.inventory.getContainerSize(); ++var1) {
            ItemStack var2 = this.inventory.getItem(var1);
            if(var2.getItem().isComplex()) {
               Packet<?> var3 = ((ComplexItem)var2.getItem()).getUpdatePacket(var2, this.level, this);
               if(var3 != null) {
                  this.connection.send(var3);
               }
            }
         }

         if(this.getHealth() != this.lastSentHealth || this.lastSentFood != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0F != this.lastFoodSaturationZero) {
            this.connection.send(new ClientboundSetHealthPacket(this.getHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
            this.lastSentHealth = this.getHealth();
            this.lastSentFood = this.foodData.getFoodLevel();
            this.lastFoodSaturationZero = this.foodData.getSaturationLevel() == 0.0F;
         }

         if(this.getHealth() + this.getAbsorptionAmount() != this.lastRecordedHealthAndAbsorption) {
            this.lastRecordedHealthAndAbsorption = this.getHealth() + this.getAbsorptionAmount();
            this.updateScoreForCriteria(ObjectiveCriteria.HEALTH, Mth.ceil(this.lastRecordedHealthAndAbsorption));
         }

         if(this.foodData.getFoodLevel() != this.lastRecordedFoodLevel) {
            this.lastRecordedFoodLevel = this.foodData.getFoodLevel();
            this.updateScoreForCriteria(ObjectiveCriteria.FOOD, Mth.ceil((float)this.lastRecordedFoodLevel));
         }

         if(this.getAirSupply() != this.lastRecordedAirLevel) {
            this.lastRecordedAirLevel = this.getAirSupply();
            this.updateScoreForCriteria(ObjectiveCriteria.AIR, Mth.ceil((float)this.lastRecordedAirLevel));
         }

         if(this.getArmorValue() != this.lastRecordedArmor) {
            this.lastRecordedArmor = this.getArmorValue();
            this.updateScoreForCriteria(ObjectiveCriteria.ARMOR, Mth.ceil((float)this.lastRecordedArmor));
         }

         if(this.totalExperience != this.lastRecordedExperience) {
            this.lastRecordedExperience = this.totalExperience;
            this.updateScoreForCriteria(ObjectiveCriteria.EXPERIENCE, Mth.ceil((float)this.lastRecordedExperience));
         }

         if(this.experienceLevel != this.lastRecordedLevel) {
            this.lastRecordedLevel = this.experienceLevel;
            this.updateScoreForCriteria(ObjectiveCriteria.LEVEL, Mth.ceil((float)this.lastRecordedLevel));
         }

         if(this.totalExperience != this.lastSentExp) {
            this.lastSentExp = this.totalExperience;
            this.connection.send(new ClientboundSetExperiencePacket(this.experienceProgress, this.totalExperience, this.experienceLevel));
         }

         if(this.tickCount % 20 == 0) {
            CriteriaTriggers.LOCATION.trigger(this);
         }

      } catch (Throwable var4) {
         CrashReport var2 = CrashReport.forThrowable(var4, "Ticking player");
         CrashReportCategory var3 = var2.addCategory("Player being ticked");
         this.fillCrashReportCategory(var3);
         throw new ReportedException(var2);
      }
   }

   private void updateScoreForCriteria(ObjectiveCriteria objectiveCriteria, int var2) {
      this.getScoreboard().forAllObjectives(objectiveCriteria, this.getScoreboardName(), (score) -> {
         score.setScore(var2);
      });
   }

   public void die(DamageSource damageSource) {
      boolean var2 = this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
      if(var2) {
         Component var3 = this.getCombatTracker().getDeathMessage();
         this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED, var3), (future) -> {
            if(!future.isSuccess()) {
               int var3 = 256;
               String var4 = var3.getString(256);
               Component var5 = new TranslatableComponent("death.attack.message_too_long", new Object[]{(new TextComponent(var4)).withStyle(ChatFormatting.YELLOW)});
               Component var6 = (new TranslatableComponent("death.attack.even_more_magic", new Object[]{this.getDisplayName()})).withStyle((style) -> {
                  style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var5));
               });
               this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED, var6));
            }

         });
         Team var4 = this.getTeam();
         if(var4 != null && var4.getDeathMessageVisibility() != Team.Visibility.ALWAYS) {
            if(var4.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OTHER_TEAMS) {
               this.server.getPlayerList().broadcastToTeam(this, var3);
            } else if(var4.getDeathMessageVisibility() == Team.Visibility.HIDE_FOR_OWN_TEAM) {
               this.server.getPlayerList().broadcastToAllExceptTeam(this, var3);
            }
         } else {
            this.server.getPlayerList().broadcastMessage(var3);
         }
      } else {
         this.connection.send(new ClientboundPlayerCombatPacket(this.getCombatTracker(), ClientboundPlayerCombatPacket.Event.ENTITY_DIED));
      }

      this.removeEntitiesOnShoulder();
      if(!this.isSpectator()) {
         this.dropAllDeathLoot(damageSource);
      }

      this.getScoreboard().forAllObjectives(ObjectiveCriteria.DEATH_COUNT, this.getScoreboardName(), Score::increment);
      LivingEntity var3 = this.getKillCredit();
      if(var3 != null) {
         this.awardStat(Stats.ENTITY_KILLED_BY.get(var3.getType()));
         var3.awardKillScore(this, this.deathScore, damageSource);
         if(!this.level.isClientSide && var3 instanceof WitherBoss) {
            boolean var4 = false;
            if(this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
               BlockPos var5 = new BlockPos(this.x, this.y, this.z);
               BlockState var6 = Blocks.WITHER_ROSE.defaultBlockState();
               if(this.level.getBlockState(var5).isAir() && var6.canSurvive(this.level, var5)) {
                  this.level.setBlock(var5, var6, 3);
                  var4 = true;
               }
            }

            if(!var4) {
               ItemEntity var5 = new ItemEntity(this.level, this.x, this.y, this.z, new ItemStack(Items.WITHER_ROSE));
               this.level.addFreshEntity(var5);
            }
         }
      }

      this.awardStat(Stats.DEATHS);
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
      this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
      this.clearFire();
      this.setSharedFlag(0, false);
      this.getCombatTracker().recheckStatus();
   }

   public void awardKillScore(Entity entity, int var2, DamageSource damageSource) {
      if(entity != this) {
         super.awardKillScore(entity, var2, damageSource);
         this.increaseScore(var2);
         String var4 = this.getScoreboardName();
         String var5 = entity.getScoreboardName();
         this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_ALL, var4, Score::increment);
         if(entity instanceof Player) {
            this.awardStat(Stats.PLAYER_KILLS);
            this.getScoreboard().forAllObjectives(ObjectiveCriteria.KILL_COUNT_PLAYERS, var4, Score::increment);
         } else {
            this.awardStat(Stats.MOB_KILLS);
         }

         this.handleTeamKill(var4, var5, ObjectiveCriteria.TEAM_KILL);
         this.handleTeamKill(var5, var4, ObjectiveCriteria.KILLED_BY_TEAM);
         CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger(this, entity, damageSource);
      }
   }

   private void handleTeamKill(String var1, String var2, ObjectiveCriteria[] objectiveCriterias) {
      PlayerTeam var4 = this.getScoreboard().getPlayersTeam(var2);
      if(var4 != null) {
         int var5 = var4.getColor().getId();
         if(var5 >= 0 && var5 < objectiveCriterias.length) {
            this.getScoreboard().forAllObjectives(objectiveCriterias[var5], var1, Score::increment);
         }
      }

   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else {
         boolean var3 = this.server.isDedicatedServer() && this.isPvpAllowed() && "fall".equals(damageSource.msgId);
         if(!var3 && this.spawnInvulnerableTime > 0 && damageSource != DamageSource.OUT_OF_WORLD) {
            return false;
         } else {
            if(damageSource instanceof EntityDamageSource) {
               Entity var4 = damageSource.getEntity();
               if(var4 instanceof Player && !this.canHarmPlayer((Player)var4)) {
                  return false;
               }

               if(var4 instanceof AbstractArrow) {
                  AbstractArrow var5 = (AbstractArrow)var4;
                  Entity var6 = var5.getOwner();
                  if(var6 instanceof Player && !this.canHarmPlayer((Player)var6)) {
                     return false;
                  }
               }
            }

            return super.hurt(damageSource, var2);
         }
      }
   }

   public boolean canHarmPlayer(Player player) {
      return !this.isPvpAllowed()?false:super.canHarmPlayer(player);
   }

   private boolean isPvpAllowed() {
      return this.server.isPvpAllowed();
   }

   @Nullable
   public Entity changeDimension(DimensionType dimension) {
      this.isChangingDimension = true;
      DimensionType dimensionType = this.dimension;
      if(dimensionType == DimensionType.THE_END && dimension == DimensionType.OVERWORLD) {
         this.unRide();
         this.getLevel().removePlayerImmediately(this);
         if(!this.wonGame) {
            this.wonGame = true;
            this.connection.send(new ClientboundGameEventPacket(4, this.seenCredits?0.0F:1.0F));
            this.seenCredits = true;
         }

         return this;
      } else {
         ServerLevel var3 = this.server.getLevel(dimensionType);
         this.dimension = dimension;
         ServerLevel var4 = this.server.getLevel(dimension);
         LevelData var5 = this.level.getLevelData();
         this.connection.send(new ClientboundRespawnPacket(dimension, var5.getGeneratorType(), this.gameMode.getGameModeForPlayer()));
         this.connection.send(new ClientboundChangeDifficultyPacket(var5.getDifficulty(), var5.isDifficultyLocked()));
         PlayerList var6 = this.server.getPlayerList();
         var6.sendPlayerPermissionLevel(this);
         var3.removePlayerImmediately(this);
         this.removed = false;
         double var7 = this.x;
         double var9 = this.y;
         double var11 = this.z;
         float var13 = this.xRot;
         float var14 = this.yRot;
         double var15 = 8.0D;
         float var17 = var14;
         var3.getProfiler().push("moving");
         if(dimensionType == DimensionType.OVERWORLD && dimension == DimensionType.NETHER) {
            this.enteredNetherPosition = new Vec3(this.x, this.y, this.z);
            var7 /= 8.0D;
            var11 /= 8.0D;
         } else if(dimensionType == DimensionType.NETHER && dimension == DimensionType.OVERWORLD) {
            var7 *= 8.0D;
            var11 *= 8.0D;
         } else if(dimensionType == DimensionType.OVERWORLD && dimension == DimensionType.THE_END) {
            BlockPos var18 = var4.getDimensionSpecificSpawn();
            var7 = (double)var18.getX();
            var9 = (double)var18.getY();
            var11 = (double)var18.getZ();
            var14 = 90.0F;
            var13 = 0.0F;
         }

         this.moveTo(var7, var9, var11, var14, var13);
         var3.getProfiler().pop();
         var3.getProfiler().push("placing");
         double var18 = Math.min(-2.9999872E7D, var4.getWorldBorder().getMinX() + 16.0D);
         double var20 = Math.min(-2.9999872E7D, var4.getWorldBorder().getMinZ() + 16.0D);
         double var22 = Math.min(2.9999872E7D, var4.getWorldBorder().getMaxX() - 16.0D);
         double var24 = Math.min(2.9999872E7D, var4.getWorldBorder().getMaxZ() - 16.0D);
         var7 = Mth.clamp(var7, var18, var22);
         var11 = Mth.clamp(var11, var20, var24);
         this.moveTo(var7, var9, var11, var14, var13);
         if(dimension == DimensionType.THE_END) {
            int var26 = Mth.floor(this.x);
            int var27 = Mth.floor(this.y) - 1;
            int var28 = Mth.floor(this.z);
            int var29 = 1;
            int var30 = 0;

            for(int var31 = -2; var31 <= 2; ++var31) {
               for(int var32 = -2; var32 <= 2; ++var32) {
                  for(int var33 = -1; var33 < 3; ++var33) {
                     int var34 = var26 + var32 * 1 + var31 * 0;
                     int var35 = var27 + var33;
                     int var36 = var28 + var32 * 0 - var31 * 1;
                     boolean var37 = var33 < 0;
                     var4.setBlockAndUpdate(new BlockPos(var34, var35, var36), var37?Blocks.OBSIDIAN.defaultBlockState():Blocks.AIR.defaultBlockState());
                  }
               }
            }

            this.moveTo((double)var26, (double)var27, (double)var28, var14, 0.0F);
            this.setDeltaMovement(Vec3.ZERO);
         } else if(!var4.getPortalForcer().findAndMoveToPortal(this, var17)) {
            var4.getPortalForcer().createPortal(this);
            var4.getPortalForcer().findAndMoveToPortal(this, var17);
         }

         var3.getProfiler().pop();
         this.setLevel(var4);
         var4.addDuringPortalTeleport(this);
         this.triggerDimensionChangeTriggers(var3);
         this.connection.teleport(this.x, this.y, this.z, var14, var13);
         this.gameMode.setLevel(var4);
         this.connection.send(new ClientboundPlayerAbilitiesPacket(this.abilities));
         var6.sendLevelInfo(this, var4);
         var6.sendAllPlayerInfo(this);

         for(MobEffectInstance var27 : this.getActiveEffects()) {
            this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), var27));
         }

         this.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
         this.lastSentExp = -1;
         this.lastSentHealth = -1.0F;
         this.lastSentFood = -1;
         return this;
      }
   }

   private void triggerDimensionChangeTriggers(ServerLevel serverLevel) {
      DimensionType var2 = serverLevel.dimension.getType();
      DimensionType var3 = this.level.dimension.getType();
      CriteriaTriggers.CHANGED_DIMENSION.trigger(this, var2, var3);
      if(var2 == DimensionType.NETHER && var3 == DimensionType.OVERWORLD && this.enteredNetherPosition != null) {
         CriteriaTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
      }

      if(var3 != DimensionType.NETHER) {
         this.enteredNetherPosition = null;
      }

   }

   public boolean broadcastToPlayer(ServerPlayer serverPlayer) {
      return serverPlayer.isSpectator()?this.getCamera() == this:(this.isSpectator()?false:super.broadcastToPlayer(serverPlayer));
   }

   private void broadcast(BlockEntity blockEntity) {
      if(blockEntity != null) {
         ClientboundBlockEntityDataPacket var2 = blockEntity.getUpdatePacket();
         if(var2 != null) {
            this.connection.send(var2);
         }
      }

   }

   public void take(Entity entity, int var2) {
      super.take(entity, var2);
      this.containerMenu.broadcastChanges();
   }

   public Either startSleepInBed(BlockPos blockPos) {
      return super.startSleepInBed(blockPos).ifRight((unit) -> {
         this.awardStat(Stats.SLEEP_IN_BED);
         CriteriaTriggers.SLEPT_IN_BED.trigger(this);
      });
   }

   public void stopSleepInBed(boolean var1, boolean var2, boolean var3) {
      if(this.isSleeping()) {
         this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(this, 2));
      }

      super.stopSleepInBed(var1, var2, var3);
      if(this.connection != null) {
         this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
      }

   }

   public boolean startRiding(Entity entity, boolean var2) {
      Entity entity = this.getVehicle();
      if(!super.startRiding(entity, var2)) {
         return false;
      } else {
         Entity var4 = this.getVehicle();
         if(var4 != entity && this.connection != null) {
            this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
         }

         return true;
      }
   }

   public void stopRiding() {
      Entity var1 = this.getVehicle();
      super.stopRiding();
      Entity var2 = this.getVehicle();
      if(var2 != var1 && this.connection != null) {
         this.connection.teleport(this.x, this.y, this.z, this.yRot, this.xRot);
      }

   }

   public boolean isInvulnerableTo(DamageSource damageSource) {
      return super.isInvulnerableTo(damageSource) || this.isChangingDimension() || this.abilities.invulnerable && damageSource == DamageSource.WITHER;
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState blockState, BlockPos blockPos) {
   }

   protected void onChangedBlock(BlockPos blockPos) {
      if(!this.isSpectator()) {
         super.onChangedBlock(blockPos);
      }

   }

   public void doCheckFallDamage(double var1, boolean var3) {
      int var4 = Mth.floor(this.x);
      int var5 = Mth.floor(this.y - 0.20000000298023224D);
      int var6 = Mth.floor(this.z);
      BlockPos var7 = new BlockPos(var4, var5, var6);
      if(this.level.hasChunkAt(var7)) {
         BlockState var8 = this.level.getBlockState(var7);
         if(var8.isAir()) {
            BlockPos var9 = var7.below();
            BlockState var10 = this.level.getBlockState(var9);
            Block var11 = var10.getBlock();
            if(var11.is(BlockTags.FENCES) || var11.is(BlockTags.WALLS) || var11 instanceof FenceGateBlock) {
               var7 = var9;
               var8 = var10;
            }
         }

         super.checkFallDamage(var1, var3, var8, var7);
      }
   }

   public void openTextEdit(SignBlockEntity signBlockEntity) {
      signBlockEntity.setAllowedPlayerEditor(this);
      this.connection.send(new ClientboundOpenSignEditorPacket(signBlockEntity.getBlockPos()));
   }

   private void nextContainerCounter() {
      this.containerCounter = this.containerCounter % 100 + 1;
   }

   public OptionalInt openMenu(@Nullable MenuProvider menuProvider) {
      if(menuProvider == null) {
         return OptionalInt.empty();
      } else {
         if(this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
         }

         this.nextContainerCounter();
         AbstractContainerMenu var2 = menuProvider.createMenu(this.containerCounter, this.inventory, this);
         if(var2 == null) {
            if(this.isSpectator()) {
               this.displayClientMessage((new TranslatableComponent("container.spectatorCantOpen", new Object[0])).withStyle(ChatFormatting.RED), true);
            }

            return OptionalInt.empty();
         } else {
            this.connection.send(new ClientboundOpenScreenPacket(var2.containerId, var2.getType(), menuProvider.getDisplayName()));
            var2.addSlotListener(this);
            this.containerMenu = var2;
            return OptionalInt.of(this.containerCounter);
         }
      }
   }

   public void sendMerchantOffers(int var1, MerchantOffers merchantOffers, int var3, int var4, boolean var5, boolean var6) {
      this.connection.send(new ClientboundMerchantOffersPacket(var1, merchantOffers, var3, var4, var5, var6));
   }

   public void openHorseInventory(AbstractHorse abstractHorse, Container container) {
      if(this.containerMenu != this.inventoryMenu) {
         this.closeContainer();
      }

      this.nextContainerCounter();
      this.connection.send(new ClientboundHorseScreenOpenPacket(this.containerCounter, container.getContainerSize(), abstractHorse.getId()));
      this.containerMenu = new HorseInventoryMenu(this.containerCounter, this.inventory, container, abstractHorse);
      this.containerMenu.addSlotListener(this);
   }

   public void openItemGui(ItemStack itemStack, InteractionHand interactionHand) {
      Item var3 = itemStack.getItem();
      if(var3 == Items.WRITTEN_BOOK) {
         if(WrittenBookItem.resolveBookComponents(itemStack, this.createCommandSourceStack(), this)) {
            this.containerMenu.broadcastChanges();
         }

         this.connection.send(new ClientboundOpenBookPacket(interactionHand));
      }

   }

   public void openCommandBlock(CommandBlockEntity commandBlockEntity) {
      commandBlockEntity.setSendToClient(true);
      this.broadcast(commandBlockEntity);
   }

   public void slotChanged(AbstractContainerMenu abstractContainerMenu, int var2, ItemStack itemStack) {
      if(!(abstractContainerMenu.getSlot(var2) instanceof ResultSlot)) {
         if(abstractContainerMenu == this.inventoryMenu) {
            CriteriaTriggers.INVENTORY_CHANGED.trigger(this, this.inventory);
         }

         if(!this.ignoreSlotUpdateHack) {
            this.connection.send(new ClientboundContainerSetSlotPacket(abstractContainerMenu.containerId, var2, itemStack));
         }
      }
   }

   public void refreshContainer(AbstractContainerMenu abstractContainerMenu) {
      this.refreshContainer(abstractContainerMenu, abstractContainerMenu.getItems());
   }

   public void refreshContainer(AbstractContainerMenu abstractContainerMenu, NonNullList nonNullList) {
      this.connection.send(new ClientboundContainerSetContentPacket(abstractContainerMenu.containerId, nonNullList));
      this.connection.send(new ClientboundContainerSetSlotPacket(-1, -1, this.inventory.getCarried()));
   }

   public void setContainerData(AbstractContainerMenu abstractContainerMenu, int var2, int var3) {
      this.connection.send(new ClientboundContainerSetDataPacket(abstractContainerMenu.containerId, var2, var3));
   }

   public void closeContainer() {
      this.connection.send(new ClientboundContainerClosePacket(this.containerMenu.containerId));
      this.doCloseContainer();
   }

   public void broadcastCarriedItem() {
      if(!this.ignoreSlotUpdateHack) {
         this.connection.send(new ClientboundContainerSetSlotPacket(-1, -1, this.inventory.getCarried()));
      }
   }

   public void doCloseContainer() {
      this.containerMenu.removed(this);
      this.containerMenu = this.inventoryMenu;
   }

   public void setPlayerInput(float xxa, float zza, boolean jumping, boolean sneaking) {
      if(this.isPassenger()) {
         if(xxa >= -1.0F && xxa <= 1.0F) {
            this.xxa = xxa;
         }

         if(zza >= -1.0F && zza <= 1.0F) {
            this.zza = zza;
         }

         this.jumping = jumping;
         this.setSneaking(sneaking);
      }

   }

   public void awardStat(Stat stat, int var2) {
      this.stats.increment(this, stat, var2);
      this.getScoreboard().forAllObjectives(stat, this.getScoreboardName(), (score) -> {
         score.add(var2);
      });
   }

   public void resetStat(Stat stat) {
      this.stats.setValue(this, stat, 0);
      this.getScoreboard().forAllObjectives(stat, this.getScoreboardName(), Score::reset);
   }

   public int awardRecipes(Collection collection) {
      return this.recipeBook.addRecipes(collection, this);
   }

   public void awardRecipesByKey(ResourceLocation[] resourceLocations) {
      List<Recipe<?>> var2 = Lists.newArrayList();

      for(ResourceLocation var6 : resourceLocations) {
         this.server.getRecipeManager().byKey(var6).ifPresent(var2::add);
      }

      this.awardRecipes(var2);
   }

   public int resetRecipes(Collection collection) {
      return this.recipeBook.removeRecipes(collection, this);
   }

   public void giveExperiencePoints(int i) {
      super.giveExperiencePoints(i);
      this.lastSentExp = -1;
   }

   public void disconnect() {
      this.disconnected = true;
      this.ejectPassengers();
      if(this.isSleeping()) {
         this.stopSleepInBed(true, false, false);
      }

   }

   public boolean hasDisconnected() {
      return this.disconnected;
   }

   public void resetSentInfo() {
      this.lastSentHealth = -1.0E8F;
   }

   public void displayClientMessage(Component component, boolean var2) {
      this.connection.send(new ClientboundChatPacket(component, var2?ChatType.GAME_INFO:ChatType.CHAT));
   }

   protected void completeUsingItem() {
      if(!this.useItem.isEmpty() && this.isUsingItem()) {
         this.connection.send(new ClientboundEntityEventPacket(this, (byte)9));
         super.completeUsingItem();
      }

   }

   public void lookAt(EntityAnchorArgument.Anchor entityAnchorArgument$Anchor, Vec3 vec3) {
      super.lookAt(entityAnchorArgument$Anchor, vec3);
      this.connection.send(new ClientboundPlayerLookAtPacket(entityAnchorArgument$Anchor, vec3.x, vec3.y, vec3.z));
   }

   public void lookAt(EntityAnchorArgument.Anchor var1, Entity entity, EntityAnchorArgument.Anchor var3) {
      Vec3 var4 = var3.apply(entity);
      super.lookAt(var1, var4);
      this.connection.send(new ClientboundPlayerLookAtPacket(var1, entity, var3));
   }

   public void restoreFrom(ServerPlayer serverPlayer, boolean var2) {
      if(var2) {
         this.inventory.replaceWith(serverPlayer.inventory);
         this.setHealth(serverPlayer.getHealth());
         this.foodData = serverPlayer.foodData;
         this.experienceLevel = serverPlayer.experienceLevel;
         this.totalExperience = serverPlayer.totalExperience;
         this.experienceProgress = serverPlayer.experienceProgress;
         this.setScore(serverPlayer.getScore());
         this.portalEntranceBlock = serverPlayer.portalEntranceBlock;
         this.portalEntranceOffset = serverPlayer.portalEntranceOffset;
         this.portalEntranceForwards = serverPlayer.portalEntranceForwards;
      } else if(this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || serverPlayer.isSpectator()) {
         this.inventory.replaceWith(serverPlayer.inventory);
         this.experienceLevel = serverPlayer.experienceLevel;
         this.totalExperience = serverPlayer.totalExperience;
         this.experienceProgress = serverPlayer.experienceProgress;
         this.setScore(serverPlayer.getScore());
      }

      this.enchantmentSeed = serverPlayer.enchantmentSeed;
      this.enderChestInventory = serverPlayer.enderChestInventory;
      this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, serverPlayer.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION));
      this.lastSentExp = -1;
      this.lastSentHealth = -1.0F;
      this.lastSentFood = -1;
      this.recipeBook.copyOverData(serverPlayer.recipeBook);
      this.entitiesToRemove.addAll(serverPlayer.entitiesToRemove);
      this.seenCredits = serverPlayer.seenCredits;
      this.enteredNetherPosition = serverPlayer.enteredNetherPosition;
      this.setShoulderEntityLeft(serverPlayer.getShoulderEntityLeft());
      this.setShoulderEntityRight(serverPlayer.getShoulderEntityRight());
   }

   protected void onEffectAdded(MobEffectInstance mobEffectInstance) {
      super.onEffectAdded(mobEffectInstance);
      this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), mobEffectInstance));
      if(mobEffectInstance.getEffect() == MobEffects.LEVITATION) {
         this.levitationStartTime = this.tickCount;
         this.levitationStartPos = new Vec3(this.x, this.y, this.z);
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onEffectUpdated(MobEffectInstance mobEffectInstance, boolean var2) {
      super.onEffectUpdated(mobEffectInstance, var2);
      this.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), mobEffectInstance));
      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   protected void onEffectRemoved(MobEffectInstance mobEffectInstance) {
      super.onEffectRemoved(mobEffectInstance);
      this.connection.send(new ClientboundRemoveMobEffectPacket(this.getId(), mobEffectInstance.getEffect()));
      if(mobEffectInstance.getEffect() == MobEffects.LEVITATION) {
         this.levitationStartPos = null;
      }

      CriteriaTriggers.EFFECTS_CHANGED.trigger(this);
   }

   public void teleportTo(double var1, double var3, double var5) {
      this.connection.teleport(var1, var3, var5, this.yRot, this.xRot);
   }

   public void crit(Entity entity) {
      this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(entity, 4));
   }

   public void magicCrit(Entity entity) {
      this.getLevel().getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(entity, 5));
   }

   public void onUpdateAbilities() {
      if(this.connection != null) {
         this.connection.send(new ClientboundPlayerAbilitiesPacket(this.abilities));
         this.updateInvisibilityStatus();
      }
   }

   public ServerLevel getLevel() {
      return (ServerLevel)this.level;
   }

   public void setGameMode(GameType gameMode) {
      this.gameMode.setGameModeForPlayer(gameMode);
      this.connection.send(new ClientboundGameEventPacket(3, (float)gameMode.getId()));
      if(gameMode == GameType.SPECTATOR) {
         this.removeEntitiesOnShoulder();
         this.stopRiding();
      } else {
         this.setCamera(this);
      }

      this.onUpdateAbilities();
      this.updateEffectVisibility();
   }

   public boolean isSpectator() {
      return this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
   }

   public boolean isCreative() {
      return this.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
   }

   public void sendMessage(Component component) {
      this.sendMessage(component, ChatType.SYSTEM);
   }

   public void sendMessage(Component component, ChatType chatType) {
      this.connection.send(new ClientboundChatPacket(component, chatType), (future) -> {
         if(!future.isSuccess() && (chatType == ChatType.GAME_INFO || chatType == ChatType.SYSTEM)) {
            int var4 = 256;
            String var5 = component.getString(256);
            Component var6 = (new TextComponent(var5)).withStyle(ChatFormatting.YELLOW);
            this.connection.send(new ClientboundChatPacket((new TranslatableComponent("multiplayer.message_not_delivered", new Object[]{var6})).withStyle(ChatFormatting.RED), ChatType.SYSTEM));
         }

      });
   }

   public String getIpAddress() {
      String string = this.connection.connection.getRemoteAddress().toString();
      string = string.substring(string.indexOf("/") + 1);
      string = string.substring(0, string.indexOf(":"));
      return string;
   }

   public void updateOptions(ServerboundClientInformationPacket serverboundClientInformationPacket) {
      this.language = serverboundClientInformationPacket.getLanguage();
      this.chatVisibility = serverboundClientInformationPacket.getChatVisibility();
      this.canChatColor = serverboundClientInformationPacket.getChatColors();
      this.getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, Byte.valueOf((byte)serverboundClientInformationPacket.getModelCustomisation()));
      this.getEntityData().set(DATA_PLAYER_MAIN_HAND, Byte.valueOf((byte)(serverboundClientInformationPacket.getMainHand() == HumanoidArm.LEFT?0:1)));
   }

   public ChatVisiblity getChatVisibility() {
      return this.chatVisibility;
   }

   public void sendTexturePack(String var1, String var2) {
      this.connection.send(new ClientboundResourcePackPacket(var1, var2));
   }

   protected int getPermissionLevel() {
      return this.server.getProfilePermissions(this.getGameProfile());
   }

   public void resetLastActionTime() {
      this.lastActionTime = Util.getMillis();
   }

   public ServerStatsCounter getStats() {
      return this.stats;
   }

   public ServerRecipeBook getRecipeBook() {
      return this.recipeBook;
   }

   public void sendRemoveEntity(Entity entity) {
      if(entity instanceof Player) {
         this.connection.send(new ClientboundRemoveEntitiesPacket(new int[]{entity.getId()}));
      } else {
         this.entitiesToRemove.add(Integer.valueOf(entity.getId()));
      }

   }

   public void cancelRemoveEntity(Entity entity) {
      this.entitiesToRemove.remove(Integer.valueOf(entity.getId()));
   }

   protected void updateInvisibilityStatus() {
      if(this.isSpectator()) {
         this.removeEffectParticles();
         this.setInvisible(true);
      } else {
         super.updateInvisibilityStatus();
      }

   }

   public Entity getCamera() {
      return (Entity)(this.camera == null?this:this.camera);
   }

   public void setCamera(Entity camera) {
      Entity entity = this.getCamera();
      this.camera = (Entity)(camera == null?this:camera);
      if(entity != this.camera) {
         this.connection.send(new ClientboundSetCameraPacket(this.camera));
         this.teleportTo(this.camera.x, this.camera.y, this.camera.z);
      }

   }

   protected void processDimensionDelay() {
      if(this.changingDimensionDelay > 0 && !this.isChangingDimension) {
         --this.changingDimensionDelay;
      }

   }

   public void attack(Entity camera) {
      if(this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
         this.setCamera(camera);
      } else {
         super.attack(camera);
      }

   }

   public long getLastActionTime() {
      return this.lastActionTime;
   }

   @Nullable
   public Component getTabListDisplayName() {
      return null;
   }

   public void swing(InteractionHand interactionHand) {
      super.swing(interactionHand);
      this.resetAttackStrengthTicker();
   }

   public boolean isChangingDimension() {
      return this.isChangingDimension;
   }

   public void hasChangedDimension() {
      this.isChangingDimension = false;
   }

   public void startFallFlying() {
      this.setSharedFlag(7, true);
   }

   public void stopFallFlying() {
      this.setSharedFlag(7, true);
      this.setSharedFlag(7, false);
   }

   public PlayerAdvancements getAdvancements() {
      return this.advancements;
   }

   public void teleportTo(ServerLevel level, double var2, double var4, double var6, float var8, float var9) {
      this.setCamera(this);
      this.stopRiding();
      if(level == this.level) {
         this.connection.teleport(var2, var4, var6, var8, var9);
      } else {
         ServerLevel serverLevel = this.getLevel();
         this.dimension = level.dimension.getType();
         LevelData var11 = level.getLevelData();
         this.connection.send(new ClientboundRespawnPacket(this.dimension, var11.getGeneratorType(), this.gameMode.getGameModeForPlayer()));
         this.connection.send(new ClientboundChangeDifficultyPacket(var11.getDifficulty(), var11.isDifficultyLocked()));
         this.server.getPlayerList().sendPlayerPermissionLevel(this);
         serverLevel.removePlayerImmediately(this);
         this.removed = false;
         this.moveTo(var2, var4, var6, var8, var9);
         this.setLevel(level);
         level.addDuringCommandTeleport(this);
         this.triggerDimensionChangeTriggers(serverLevel);
         this.connection.teleport(var2, var4, var6, var8, var9);
         this.gameMode.setLevel(level);
         this.server.getPlayerList().sendLevelInfo(this, level);
         this.server.getPlayerList().sendAllPlayerInfo(this);
      }

   }

   public void trackChunk(ChunkPos chunkPos, Packet var2, Packet var3) {
      this.connection.send(var3);
      this.connection.send(var2);
   }

   public void untrackChunk(ChunkPos chunkPos) {
      this.connection.send(new ClientboundForgetLevelChunkPacket(chunkPos.x, chunkPos.z));
   }

   public SectionPos getLastSectionPos() {
      return this.lastSectionPos;
   }

   public void setLastSectionPos(SectionPos lastSectionPos) {
      this.lastSectionPos = lastSectionPos;
   }

   public void playNotifySound(SoundEvent soundEvent, SoundSource soundSource, float var3, float var4) {
      this.connection.send(new ClientboundSoundPacket(soundEvent, soundSource, this.x, this.y, this.z, var3, var4));
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddPlayerPacket(this);
   }

   public ItemEntity drop(ItemStack itemStack, boolean var2, boolean var3) {
      ItemEntity itemEntity = super.drop(itemStack, var2, var3);
      if(itemEntity == null) {
         return null;
      } else {
         this.level.addFreshEntity(itemEntity);
         ItemStack var5 = itemEntity.getItem();
         if(var3) {
            if(!var5.isEmpty()) {
               this.awardStat(Stats.ITEM_DROPPED.get(var5.getItem()), itemStack.getCount());
            }

            this.awardStat(Stats.DROP);
         }

         return itemEntity;
      }
   }
}
