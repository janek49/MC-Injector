package net.minecraft.world.entity.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class Raid {
   private static final TranslatableComponent RAID_NAME_COMPONENT = new TranslatableComponent("event.minecraft.raid", new Object[0]);
   private static final TranslatableComponent VICTORY = new TranslatableComponent("event.minecraft.raid.victory", new Object[0]);
   private static final TranslatableComponent DEFEAT = new TranslatableComponent("event.minecraft.raid.defeat", new Object[0]);
   private static final Component RAID_BAR_VICTORY_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append((Component)VICTORY);
   private static final Component RAID_BAR_DEFEAT_COMPONENT = RAID_NAME_COMPONENT.copy().append(" - ").append((Component)DEFEAT);
   private final Map groupToLeaderMap = Maps.newHashMap();
   private final Map groupRaiderMap = Maps.newHashMap();
   private final Set heroesOfTheVillage = Sets.newHashSet();
   private long ticksActive;
   private BlockPos center;
   private final ServerLevel level;
   private boolean started;
   private final int id;
   private float totalHealth;
   private int badOmenLevel;
   private boolean active;
   private int groupsSpawned;
   private final ServerBossEvent raidEvent;
   private int postRaidTicks;
   private int raidCooldownTicks;
   private final Random random;
   private final int numGroups;
   private Raid.RaidStatus status;
   private int celebrationTicks;
   private Optional waveSpawnPos;

   public Raid(int id, ServerLevel level, BlockPos center) {
      this.raidEvent = new ServerBossEvent(RAID_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
      this.random = new Random();
      this.waveSpawnPos = Optional.empty();
      this.id = id;
      this.level = level;
      this.active = true;
      this.raidCooldownTicks = 300;
      this.raidEvent.setPercent(0.0F);
      this.center = center;
      this.numGroups = this.getNumGroups(level.getDifficulty());
      this.status = Raid.RaidStatus.ONGOING;
   }

   public Raid(ServerLevel level, CompoundTag compoundTag) {
      this.raidEvent = new ServerBossEvent(RAID_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
      this.random = new Random();
      this.waveSpawnPos = Optional.empty();
      this.level = level;
      this.id = compoundTag.getInt("Id");
      this.started = compoundTag.getBoolean("Started");
      this.active = compoundTag.getBoolean("Active");
      this.ticksActive = compoundTag.getLong("TicksActive");
      this.badOmenLevel = compoundTag.getInt("BadOmenLevel");
      this.groupsSpawned = compoundTag.getInt("GroupsSpawned");
      this.raidCooldownTicks = compoundTag.getInt("PreRaidTicks");
      this.postRaidTicks = compoundTag.getInt("PostRaidTicks");
      this.totalHealth = compoundTag.getFloat("TotalHealth");
      this.center = new BlockPos(compoundTag.getInt("CX"), compoundTag.getInt("CY"), compoundTag.getInt("CZ"));
      this.numGroups = compoundTag.getInt("NumGroups");
      this.status = Raid.RaidStatus.getByName(compoundTag.getString("Status"));
      this.heroesOfTheVillage.clear();
      if(compoundTag.contains("HeroesOfTheVillage", 9)) {
         ListTag var3 = compoundTag.getList("HeroesOfTheVillage", 10);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            CompoundTag var5 = var3.getCompound(var4);
            UUID var6 = var5.getUUID("UUID");
            this.heroesOfTheVillage.add(var6);
         }
      }

   }

   public boolean isOver() {
      return this.isVictory() || this.isLoss();
   }

   public boolean isBetweenWaves() {
      return this.hasFirstWaveSpawned() && this.getTotalRaidersAlive() == 0 && this.raidCooldownTicks > 0;
   }

   public boolean hasFirstWaveSpawned() {
      return this.groupsSpawned > 0;
   }

   public boolean isStopped() {
      return this.status == Raid.RaidStatus.STOPPED;
   }

   public boolean isVictory() {
      return this.status == Raid.RaidStatus.VICTORY;
   }

   public boolean isLoss() {
      return this.status == Raid.RaidStatus.LOSS;
   }

   public Level getLevel() {
      return this.level;
   }

   public boolean isStarted() {
      return this.started;
   }

   public int getGroupsSpawned() {
      return this.groupsSpawned;
   }

   private Predicate validPlayer() {
      return (serverPlayer) -> {
         BlockPos var2 = new BlockPos(serverPlayer);
         return serverPlayer.isAlive() && this.level.getRaidAt(var2) == this;
      };
   }

   private void updatePlayers() {
      Set<ServerPlayer> var1 = Sets.newHashSet(this.raidEvent.getPlayers());
      List<ServerPlayer> var2 = this.level.getPlayers(this.validPlayer());

      for(ServerPlayer var4 : var2) {
         if(!var1.contains(var4)) {
            this.raidEvent.addPlayer(var4);
         }
      }

      for(ServerPlayer var4 : var1) {
         if(!var2.contains(var4)) {
            this.raidEvent.removePlayer(var4);
         }
      }

   }

   public int getMaxBadOmenLevel() {
      return 5;
   }

   public int getBadOmenLevel() {
      return this.badOmenLevel;
   }

   public void absorbBadOmen(Player player) {
      if(player.hasEffect(MobEffects.BAD_OMEN)) {
         this.badOmenLevel += player.getEffect(MobEffects.BAD_OMEN).getAmplifier() + 1;
         this.badOmenLevel = Mth.clamp(this.badOmenLevel, 0, this.getMaxBadOmenLevel());
      }

      player.removeEffect(MobEffects.BAD_OMEN);
   }

   public void stop() {
      this.active = false;
      this.raidEvent.removeAllPlayers();
      this.status = Raid.RaidStatus.STOPPED;
   }

   public void tick() {
      if(!this.isStopped()) {
         if(this.status == Raid.RaidStatus.ONGOING) {
            boolean var1 = this.active;
            this.active = this.level.hasChunkAt(this.center);
            if(this.level.getDifficulty() == Difficulty.PEACEFUL) {
               this.stop();
               return;
            }

            if(var1 != this.active) {
               this.raidEvent.setVisible(this.active);
            }

            if(!this.active) {
               return;
            }

            if(!this.level.isVillage(this.center)) {
               this.moveRaidCenterToNearbyVillageSection();
            }

            if(!this.level.isVillage(this.center)) {
               if(this.groupsSpawned > 0) {
                  this.status = Raid.RaidStatus.LOSS;
               } else {
                  this.stop();
               }
            }

            ++this.ticksActive;
            if(this.ticksActive >= 48000L) {
               this.stop();
               return;
            }

            int var2 = this.getTotalRaidersAlive();
            if(var2 == 0 && this.hasMoreWaves()) {
               if(this.raidCooldownTicks <= 0) {
                  if(this.raidCooldownTicks == 0 && this.groupsSpawned > 0) {
                     this.raidCooldownTicks = 300;
                     this.raidEvent.setName(RAID_NAME_COMPONENT);
                     return;
                  }
               } else {
                  boolean var3 = this.waveSpawnPos.isPresent();
                  boolean var4 = !var3 && this.raidCooldownTicks % 5 == 0;
                  if(var3 && !this.level.getChunkSource().isEntityTickingChunk(new ChunkPos((BlockPos)this.waveSpawnPos.get()))) {
                     var4 = true;
                  }

                  if(var4) {
                     int var5 = 0;
                     if(this.raidCooldownTicks < 100) {
                        var5 = 1;
                     } else if(this.raidCooldownTicks < 40) {
                        var5 = 2;
                     }

                     this.waveSpawnPos = this.getValidSpawnPos(var5);
                  }

                  if(this.raidCooldownTicks == 300 || this.raidCooldownTicks % 20 == 0) {
                     this.updatePlayers();
                  }

                  --this.raidCooldownTicks;
                  this.raidEvent.setPercent(Mth.clamp((float)(300 - this.raidCooldownTicks) / 300.0F, 0.0F, 1.0F));
               }
            }

            if(this.ticksActive % 20L == 0L) {
               this.updatePlayers();
               this.updateRaiders();
               if(var2 > 0) {
                  if(var2 <= 2) {
                     this.raidEvent.setName(RAID_NAME_COMPONENT.copy().append(" - ").append((Component)(new TranslatableComponent("event.minecraft.raid.raiders_remaining", new Object[]{Integer.valueOf(var2)}))));
                  } else {
                     this.raidEvent.setName(RAID_NAME_COMPONENT);
                  }
               } else {
                  this.raidEvent.setName(RAID_NAME_COMPONENT);
               }
            }

            boolean var3 = false;
            int var4 = 0;

            while(this.shouldSpawnGroup()) {
               BlockPos var5 = this.waveSpawnPos.isPresent()?(BlockPos)this.waveSpawnPos.get():this.findRandomSpawnPos(var4, 20);
               if(var5 != null) {
                  this.started = true;
                  this.spawnGroup(var5);
                  if(!var3) {
                     this.playSound(var5);
                     var3 = true;
                  }
               } else {
                  ++var4;
               }

               if(var4 > 3) {
                  this.stop();
                  break;
               }
            }

            if(this.isStarted() && !this.hasMoreWaves() && var2 == 0) {
               if(this.postRaidTicks < 40) {
                  ++this.postRaidTicks;
               } else {
                  this.status = Raid.RaidStatus.VICTORY;

                  for(UUID var6 : this.heroesOfTheVillage) {
                     Entity var7 = this.level.getEntity(var6);
                     if(var7 instanceof LivingEntity && !var7.isSpectator()) {
                        LivingEntity var8 = (LivingEntity)var7;
                        var8.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, '뮀', this.badOmenLevel - 1, false, false, true));
                        if(var8 instanceof ServerPlayer) {
                           ServerPlayer var9 = (ServerPlayer)var8;
                           var9.awardStat(Stats.RAID_WIN);
                           CriteriaTriggers.RAID_WIN.trigger(var9);
                        }
                     }
                  }
               }
            }

            this.setDirty();
         } else if(this.isOver()) {
            ++this.celebrationTicks;
            if(this.celebrationTicks >= 600) {
               this.stop();
               return;
            }

            if(this.celebrationTicks % 20 == 0) {
               this.updatePlayers();
               this.raidEvent.setVisible(true);
               if(this.isVictory()) {
                  this.raidEvent.setPercent(0.0F);
                  this.raidEvent.setName(RAID_BAR_VICTORY_COMPONENT);
               } else {
                  this.raidEvent.setName(RAID_BAR_DEFEAT_COMPONENT);
               }
            }
         }

      }
   }

   private void moveRaidCenterToNearbyVillageSection() {
      Stream<SectionPos> var1 = SectionPos.cube(SectionPos.of(this.center), 2);
      ServerLevel var10001 = this.level;
      this.level.getClass();
      var1.filter(var10001::isVillage).map(SectionPos::center).min(Comparator.comparingDouble((blockPos) -> {
         return blockPos.distSqr(this.center);
      })).ifPresent(this::setCenter);
   }

   private Optional getValidSpawnPos(int i) {
      for(int var2 = 0; var2 < 3; ++var2) {
         BlockPos var3 = this.findRandomSpawnPos(i, 1);
         if(var3 != null) {
            return Optional.of(var3);
         }
      }

      return Optional.empty();
   }

   private boolean hasMoreWaves() {
      return this.hasBonusWave()?!this.hasSpawnedBonusWave():!this.isFinalWave();
   }

   private boolean isFinalWave() {
      return this.getGroupsSpawned() == this.numGroups;
   }

   private boolean hasBonusWave() {
      return this.badOmenLevel > 1;
   }

   private boolean hasSpawnedBonusWave() {
      return this.getGroupsSpawned() > this.numGroups;
   }

   private boolean shouldSpawnBonusGroup() {
      return this.isFinalWave() && this.getTotalRaidersAlive() == 0 && this.hasBonusWave();
   }

   private void updateRaiders() {
      Iterator<Set<Raider>> var1 = this.groupRaiderMap.values().iterator();
      Set<Raider> var2 = Sets.newHashSet();

      while(var1.hasNext()) {
         for(Raider var5 : (Set)var1.next()) {
            BlockPos var6 = new BlockPos(var5);
            if(!var5.removed && var5.dimension == this.level.getDimension().getType() && this.center.distSqr(var6) < 12544.0D) {
               if(var5.tickCount > 600) {
                  if(this.level.getEntity(var5.getUUID()) == null) {
                     var2.add(var5);
                  }

                  if(!this.level.isVillage(var6) && var5.getNoActionTime() > 2400) {
                     var5.setTicksOutsideRaid(var5.getTicksOutsideRaid() + 1);
                  }

                  if(var5.getTicksOutsideRaid() >= 30) {
                     var2.add(var5);
                  }
               }
            } else {
               var2.add(var5);
            }
         }
      }

      for(Raider var4 : var2) {
         this.removeFromRaid(var4, true);
      }

   }

   private void playSound(BlockPos blockPos) {
      float var2 = 13.0F;
      int var3 = 64;

      for(Player var5 : this.level.players()) {
         Vec3 var6 = new Vec3(var5.x, var5.y, var5.z);
         Vec3 var7 = new Vec3((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
         float var8 = Mth.sqrt((var7.x - var6.x) * (var7.x - var6.x) + (var7.z - var6.z) * (var7.z - var6.z));
         double var9 = var6.x + (double)(13.0F / var8) * (var7.x - var6.x);
         double var11 = var6.z + (double)(13.0F / var8) * (var7.z - var6.z);
         if(var8 <= 64.0F || this.level.isVillage(new BlockPos(var5))) {
            ((ServerPlayer)var5).connection.send(new ClientboundSoundPacket(SoundEvents.RAID_HORN, SoundSource.NEUTRAL, var9, var5.y, var11, 64.0F, 1.0F));
         }
      }

   }

   private void spawnGroup(BlockPos blockPos) {
      boolean var2 = false;
      int var3 = this.groupsSpawned + 1;
      this.totalHealth = 0.0F;
      DifficultyInstance var4 = this.level.getCurrentDifficultyAt(blockPos);
      boolean var5 = this.shouldSpawnBonusGroup();

      for(Raid.RaiderType var9 : Raid.RaiderType.VALUES) {
         int var10 = this.getDefaultNumSpawns(var9, var3, var5) + this.getPotentialBonusSpawns(var9, this.random, var3, var4, var5);
         int var11 = 0;

         for(int var12 = 0; var12 < var10; ++var12) {
            Raider var13 = (Raider)var9.entityType.create(this.level);
            if(!var2 && var13.canBeLeader()) {
               var13.setPatrolLeader(true);
               this.setLeader(var3, var13);
               var2 = true;
            }

            this.joinRaid(var3, var13, blockPos, false);
            if(var9.entityType == EntityType.RAVAGER) {
               Raider var14 = null;
               if(var3 == this.getNumGroups(Difficulty.NORMAL)) {
                  var14 = (Raider)EntityType.PILLAGER.create(this.level);
               } else if(var3 >= this.getNumGroups(Difficulty.HARD)) {
                  if(var11 == 0) {
                     var14 = (Raider)EntityType.EVOKER.create(this.level);
                  } else {
                     var14 = (Raider)EntityType.VINDICATOR.create(this.level);
                  }
               }

               ++var11;
               if(var14 != null) {
                  this.joinRaid(var3, var14, blockPos, false);
                  var14.moveTo(blockPos, 0.0F, 0.0F);
                  var14.startRiding(var13);
               }
            }
         }
      }

      this.waveSpawnPos = Optional.empty();
      ++this.groupsSpawned;
      this.updateBossbar();
      this.setDirty();
   }

   public void joinRaid(int var1, Raider raider, @Nullable BlockPos blockPos, boolean var4) {
      boolean var5 = this.addWaveMob(var1, raider);
      if(var5) {
         raider.setCurrentRaid(this);
         raider.setWave(var1);
         raider.setCanJoinRaid(true);
         raider.setTicksOutsideRaid(0);
         if(!var4 && blockPos != null) {
            raider.setPos((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 1.0D, (double)blockPos.getZ() + 0.5D);
            raider.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(blockPos), MobSpawnType.EVENT, (SpawnGroupData)null, (CompoundTag)null);
            raider.applyRaidBuffs(var1, false);
            raider.onGround = true;
            this.level.addFreshEntity(raider);
         }
      }

   }

   public void updateBossbar() {
      this.raidEvent.setPercent(Mth.clamp(this.getHealthOfLivingRaiders() / this.totalHealth, 0.0F, 1.0F));
   }

   public float getHealthOfLivingRaiders() {
      float var1 = 0.0F;
      Iterator var2 = this.groupRaiderMap.values().iterator();

      while(var2.hasNext()) {
         for(Raider var5 : (Set)var2.next()) {
            var1 += var5.getHealth();
         }
      }

      return var1;
   }

   private boolean shouldSpawnGroup() {
      return this.raidCooldownTicks == 0 && (this.groupsSpawned < this.numGroups || this.shouldSpawnBonusGroup()) && this.getTotalRaidersAlive() == 0;
   }

   public int getTotalRaidersAlive() {
      return this.groupRaiderMap.values().stream().mapToInt(Set::size).sum();
   }

   public void removeFromRaid(@Nonnull Raider raider, boolean var2) {
      Set<Raider> var3 = (Set)this.groupRaiderMap.get(Integer.valueOf(raider.getWave()));
      if(var3 != null) {
         boolean var4 = var3.remove(raider);
         if(var4) {
            if(var2) {
               this.totalHealth -= raider.getHealth();
            }

            raider.setCurrentRaid((Raid)null);
            this.updateBossbar();
            this.setDirty();
         }
      }

   }

   private void setDirty() {
      this.level.getRaids().setDirty();
   }

   public static ItemStack getLeaderBannerInstance() {
      ItemStack itemStack = new ItemStack(Items.WHITE_BANNER);
      CompoundTag var1 = itemStack.getOrCreateTagElement("BlockEntityTag");
      ListTag var2 = (new BannerPattern.Builder()).addPattern(BannerPattern.RHOMBUS_MIDDLE, DyeColor.CYAN).addPattern(BannerPattern.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.STRIPE_CENTER, DyeColor.GRAY).addPattern(BannerPattern.BORDER, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK).addPattern(BannerPattern.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY).addPattern(BannerPattern.BORDER, DyeColor.BLACK).toListTag();
      var1.put("Patterns", var2);
      itemStack.setHoverName((new TranslatableComponent("block.minecraft.ominous_banner", new Object[0])).withStyle(ChatFormatting.GOLD));
      return itemStack;
   }

   @Nullable
   public Raider getLeader(int i) {
      return (Raider)this.groupToLeaderMap.get(Integer.valueOf(i));
   }

   @Nullable
   private BlockPos findRandomSpawnPos(int var1, int var2) {
      int var3 = var1 == 0?2:2 - var1;
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      for(int var8 = 0; var8 < var2; ++var8) {
         float var9 = this.level.random.nextFloat() * 6.2831855F;
         int var4 = this.center.getX() + Mth.floor(Mth.cos(var9) * 32.0F * (float)var3) + this.level.random.nextInt(5);
         int var6 = this.center.getZ() + Mth.floor(Mth.sin(var9) * 32.0F * (float)var3) + this.level.random.nextInt(5);
         int var5 = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, var4, var6);
         var7.set(var4, var5, var6);
         if((!this.level.isVillage((BlockPos)var7) || var1 >= 2) && this.level.hasChunksAt(var7.getX() - 10, var7.getY() - 10, var7.getZ() - 10, var7.getX() + 10, var7.getY() + 10, var7.getZ() + 10) && this.level.getChunkSource().isEntityTickingChunk(new ChunkPos(var7)) && (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, this.level, var7, EntityType.RAVAGER) || this.level.getBlockState(var7.below()).getBlock() == Blocks.SNOW && this.level.getBlockState(var7).isAir())) {
            return var7;
         }
      }

      return null;
   }

   private boolean addWaveMob(int var1, Raider raider) {
      return this.addWaveMob(var1, raider, true);
   }

   public boolean addWaveMob(int var1, Raider raider, boolean var3) {
      this.groupRaiderMap.computeIfAbsent(Integer.valueOf(var1), (integer) -> {
         return Sets.newHashSet();
      });
      Set<Raider> var4 = (Set)this.groupRaiderMap.get(Integer.valueOf(var1));
      Raider var5 = null;

      for(Raider var7 : var4) {
         if(var7.getUUID().equals(raider.getUUID())) {
            var5 = var7;
            break;
         }
      }

      if(var5 != null) {
         var4.remove(var5);
         var4.add(raider);
      }

      var4.add(raider);
      if(var3) {
         this.totalHealth += raider.getHealth();
      }

      this.updateBossbar();
      this.setDirty();
      return true;
   }

   public void setLeader(int var1, Raider raider) {
      this.groupToLeaderMap.put(Integer.valueOf(var1), raider);
      raider.setItemSlot(EquipmentSlot.HEAD, getLeaderBannerInstance());
      raider.setDropChance(EquipmentSlot.HEAD, 2.0F);
   }

   public void removeLeader(int i) {
      this.groupToLeaderMap.remove(Integer.valueOf(i));
   }

   public BlockPos getCenter() {
      return this.center;
   }

   private void setCenter(BlockPos center) {
      this.center = center;
   }

   public int getId() {
      return this.id;
   }

   private int getDefaultNumSpawns(Raid.RaiderType raid$RaiderType, int var2, boolean var3) {
      return var3?raid$RaiderType.spawnsPerWaveBeforeBonus[this.numGroups]:raid$RaiderType.spawnsPerWaveBeforeBonus[var2];
   }

   private int getPotentialBonusSpawns(Raid.RaiderType raid$RaiderType, Random random, int var3, DifficultyInstance difficultyInstance, boolean var5) {
      Difficulty var6 = difficultyInstance.getDifficulty();
      boolean var7 = var6 == Difficulty.EASY;
      boolean var8 = var6 == Difficulty.NORMAL;
      int var9;
      switch(raid$RaiderType) {
      case WITCH:
         if(var7 || var3 <= 2 || var3 == 4) {
            return 0;
         }

         var9 = 1;
         break;
      case PILLAGER:
      case VINDICATOR:
         if(var7) {
            var9 = random.nextInt(2);
         } else if(var8) {
            var9 = 1;
         } else {
            var9 = 2;
         }
         break;
      case RAVAGER:
         var9 = !var7 && var5?1:0;
         break;
      default:
         return 0;
      }

      return var9 > 0?random.nextInt(var9 + 1):0;
   }

   public boolean isActive() {
      return this.active;
   }

   public CompoundTag save(CompoundTag compoundTag) {
      compoundTag.putInt("Id", this.id);
      compoundTag.putBoolean("Started", this.started);
      compoundTag.putBoolean("Active", this.active);
      compoundTag.putLong("TicksActive", this.ticksActive);
      compoundTag.putInt("BadOmenLevel", this.badOmenLevel);
      compoundTag.putInt("GroupsSpawned", this.groupsSpawned);
      compoundTag.putInt("PreRaidTicks", this.raidCooldownTicks);
      compoundTag.putInt("PostRaidTicks", this.postRaidTicks);
      compoundTag.putFloat("TotalHealth", this.totalHealth);
      compoundTag.putInt("NumGroups", this.numGroups);
      compoundTag.putString("Status", this.status.getName());
      compoundTag.putInt("CX", this.center.getX());
      compoundTag.putInt("CY", this.center.getY());
      compoundTag.putInt("CZ", this.center.getZ());
      ListTag var2 = new ListTag();

      for(UUID var4 : this.heroesOfTheVillage) {
         CompoundTag var5 = new CompoundTag();
         var5.putUUID("UUID", var4);
         var2.add(var5);
      }

      compoundTag.put("HeroesOfTheVillage", var2);
      return compoundTag;
   }

   public int getNumGroups(Difficulty difficulty) {
      switch(difficulty) {
      case EASY:
         return 3;
      case NORMAL:
         return 5;
      case HARD:
         return 7;
      default:
         return 0;
      }
   }

   public float getEnchantOdds() {
      int var1 = this.getBadOmenLevel();
      return var1 == 2?0.1F:(var1 == 3?0.25F:(var1 == 4?0.5F:(var1 == 5?0.75F:0.0F)));
   }

   public void addHeroOfTheVillage(Entity entity) {
      this.heroesOfTheVillage.add(entity.getUUID());
   }

   static enum RaidStatus {
      ONGOING,
      VICTORY,
      LOSS,
      STOPPED;

      private static final Raid.RaidStatus[] VALUES = values();

      private static Raid.RaidStatus getByName(String name) {
         for(Raid.RaidStatus var4 : VALUES) {
            if(name.equalsIgnoreCase(var4.name())) {
               return var4;
            }
         }

         return ONGOING;
      }

      public String getName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }

   static enum RaiderType {
      VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
      EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
      PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
      WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
      RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

      private static final Raid.RaiderType[] VALUES = values();
      private final EntityType entityType;
      private final int[] spawnsPerWaveBeforeBonus;

      private RaiderType(EntityType entityType, int[] spawnsPerWaveBeforeBonus) {
         this.entityType = entityType;
         this.spawnsPerWaveBeforeBonus = spawnsPerWaveBeforeBonus;
      }
   }
}
