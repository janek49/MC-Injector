package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddGlobalEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagManager;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelConflictException;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.PortalForcer;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.BonusChestFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLevel extends Level {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List globalEntities = Lists.newArrayList();
   private final Int2ObjectMap entitiesById = new Int2ObjectLinkedOpenHashMap();
   private final Map entitiesByUuid = Maps.newHashMap();
   private final Queue toAddAfterTick = Queues.newArrayDeque();
   private final List players = Lists.newArrayList();
   boolean tickingEntities;
   private final MinecraftServer server;
   private final LevelStorage levelStorage;
   public boolean noSave;
   private boolean allPlayersSleeping;
   private int emptyTime;
   private final PortalForcer portalForcer;
   private final ServerTickList blockTicks;
   private final ServerTickList liquidTicks;
   private final Set navigations;
   protected final Raids raids;
   private final ObjectLinkedOpenHashSet blockEvents;
   private boolean handlingTick;
   @Nullable
   private final WanderingTraderSpawner wanderingTraderSpawner;

   public ServerLevel(MinecraftServer server, Executor executor, LevelStorage levelStorage, LevelData levelData, DimensionType dimensionType, ProfilerFiller profilerFiller, ChunkProgressListener chunkProgressListener) {
      super(levelData, dimensionType, (level, dimension) -> {
         return new ServerChunkCache((ServerLevel)level, levelStorage.getFolder(), levelStorage.getFixerUpper(), levelStorage.getStructureManager(), executor, dimension.createRandomLevelGenerator(), server.getPlayerList().getViewDistance(), chunkProgressListener, () -> {
            return server.getLevel(DimensionType.OVERWORLD).getDataStorage();
         });
      }, profilerFiller, false);
      Predicate var10004 = (block) -> {
         return block == null || block.defaultBlockState().isAir();
      };
      DefaultedRegistry var10005 = Registry.BLOCK;
      Registry.BLOCK.getClass();
      Function var9 = var10005::getKey;
      DefaultedRegistry var10006 = Registry.BLOCK;
      Registry.BLOCK.getClass();
      this.blockTicks = new ServerTickList(this, var10004, var9, var10006::get, this::tickBlock);
      var10004 = (fluid) -> {
         return fluid == null || fluid == Fluids.EMPTY;
      };
      DefaultedRegistry var10 = Registry.FLUID;
      Registry.FLUID.getClass();
      Function var11 = var10::getKey;
      var10006 = Registry.FLUID;
      Registry.FLUID.getClass();
      this.liquidTicks = new ServerTickList(this, var10004, var11, var10006::get, this::tickLiquid);
      this.navigations = Sets.newHashSet();
      this.blockEvents = new ObjectLinkedOpenHashSet();
      this.levelStorage = levelStorage;
      this.server = server;
      this.portalForcer = new PortalForcer(this);
      this.updateSkyBrightness();
      this.prepareWeather();
      this.getWorldBorder().setAbsoluteMaxSize(server.getAbsoluteMaxWorldSize());
      this.raids = (Raids)this.getDataStorage().computeIfAbsent(() -> {
         return new Raids(this);
      }, Raids.getFileId(this.dimension));
      if(!server.isSingleplayer()) {
         this.getLevelData().setGameType(server.getDefaultGameType());
      }

      this.wanderingTraderSpawner = this.dimension.getType() == DimensionType.OVERWORLD?new WanderingTraderSpawner(this):null;
   }

   public void tick(BooleanSupplier booleanSupplier) {
      ProfilerFiller var2 = this.getProfiler();
      this.handlingTick = true;
      var2.push("world border");
      this.getWorldBorder().tick();
      var2.popPush("weather");
      boolean var3 = this.isRaining();
      if(this.dimension.isHasSkyLight()) {
         if(this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
            int var4 = this.levelData.getClearWeatherTime();
            int var5 = this.levelData.getThunderTime();
            int var6 = this.levelData.getRainTime();
            boolean var7 = this.levelData.isThundering();
            boolean var8 = this.levelData.isRaining();
            if(var4 > 0) {
               --var4;
               var5 = var7?0:1;
               var6 = var8?0:1;
               var7 = false;
               var8 = false;
            } else {
               if(var5 > 0) {
                  --var5;
                  if(var5 == 0) {
                     var7 = !var7;
                  }
               } else if(var7) {
                  var5 = this.random.nextInt(12000) + 3600;
               } else {
                  var5 = this.random.nextInt(168000) + 12000;
               }

               if(var6 > 0) {
                  --var6;
                  if(var6 == 0) {
                     var8 = !var8;
                  }
               } else if(var8) {
                  var6 = this.random.nextInt(12000) + 12000;
               } else {
                  var6 = this.random.nextInt(168000) + 12000;
               }
            }

            this.levelData.setThunderTime(var5);
            this.levelData.setRainTime(var6);
            this.levelData.setClearWeatherTime(var4);
            this.levelData.setThundering(var7);
            this.levelData.setRaining(var8);
         }

         this.oThunderLevel = this.thunderLevel;
         if(this.levelData.isThundering()) {
            this.thunderLevel = (float)((double)this.thunderLevel + 0.01D);
         } else {
            this.thunderLevel = (float)((double)this.thunderLevel - 0.01D);
         }

         this.thunderLevel = Mth.clamp(this.thunderLevel, 0.0F, 1.0F);
         this.oRainLevel = this.rainLevel;
         if(this.levelData.isRaining()) {
            this.rainLevel = (float)((double)this.rainLevel + 0.01D);
         } else {
            this.rainLevel = (float)((double)this.rainLevel - 0.01D);
         }

         this.rainLevel = Mth.clamp(this.rainLevel, 0.0F, 1.0F);
      }

      if(this.oRainLevel != this.rainLevel) {
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(7, this.rainLevel), this.dimension.getType());
      }

      if(this.oThunderLevel != this.thunderLevel) {
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(8, this.thunderLevel), this.dimension.getType());
      }

      if(var3 != this.isRaining()) {
         if(var3) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(2, 0.0F));
         } else {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(1, 0.0F));
         }

         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(7, this.rainLevel));
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(8, this.thunderLevel));
      }

      if(this.getLevelData().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
         this.getLevelData().setDifficulty(Difficulty.HARD);
      }

      if(this.allPlayersSleeping && this.players.stream().noneMatch((serverPlayer) -> {
         return !serverPlayer.isSpectator() && !serverPlayer.isSleepingLongEnough();
      })) {
         this.allPlayersSleeping = false;
         if(this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            long var4 = this.levelData.getDayTime() + 24000L;
            this.setDayTime(var4 - var4 % 24000L);
         }

         this.players.stream().filter(LivingEntity::isSleeping).forEach((serverPlayer) -> {
            serverPlayer.stopSleepInBed(false, false, true);
         });
         if(this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
            this.stopWeather();
         }
      }

      this.updateSkyBrightness();
      this.tickTime();
      var2.popPush("chunkSource");
      this.getChunkSource().tick(booleanSupplier);
      var2.popPush("tickPending");
      if(this.levelData.getGeneratorType() != LevelType.DEBUG_ALL_BLOCK_STATES) {
         this.blockTicks.tick();
         this.liquidTicks.tick();
      }

      var2.popPush("portalForcer");
      this.portalForcer.tick(this.getGameTime());
      var2.popPush("raid");
      this.raids.tick();
      if(this.wanderingTraderSpawner != null) {
         this.wanderingTraderSpawner.tick();
      }

      var2.popPush("blockEvents");
      this.runBlockEvents();
      this.handlingTick = false;
      var2.popPush("entities");
      boolean var4 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
      if(var4) {
         this.resetEmptyTime();
      }

      if(var4 || this.emptyTime++ < 300) {
         this.dimension.tick();
         var2.push("global");

         for(int var5 = 0; var5 < this.globalEntities.size(); ++var5) {
            Entity var6 = (Entity)this.globalEntities.get(var5);
            this.guardEntityTick((entity) -> {
               ++entity.tickCount;
               entity.tick();
            }, var6);
            if(var6.removed) {
               this.globalEntities.remove(var5--);
            }
         }

         var2.popPush("regular");
         this.tickingEntities = true;
         ObjectIterator<Entry<Entity>> var5 = this.entitiesById.int2ObjectEntrySet().iterator();

         label1105:
         while(true) {
            Entity var7;
            while(true) {
               if(!var5.hasNext()) {
                  this.tickingEntities = false;

                  Entity var6;
                  while((var6 = (Entity)this.toAddAfterTick.poll()) != null) {
                     this.add(var6);
                  }

                  var2.pop();
                  this.tickBlockEntities();
                  break label1105;
               }

               Entry<Entity> var6 = (Entry)var5.next();
               var7 = (Entity)var6.getValue();
               Entity var8 = var7.getVehicle();
               if(!this.server.isAnimals() && (var7 instanceof Animal || var7 instanceof WaterAnimal)) {
                  var7.remove();
               }

               if(!this.server.isNpcsEnabled() && var7 instanceof Npc) {
                  var7.remove();
               }

               if(var8 == null) {
                  break;
               }

               if(var8.removed || !var8.hasPassenger(var7)) {
                  var7.stopRiding();
                  break;
               }
            }

            var2.push("tick");
            if(!var7.removed && !(var7 instanceof EnderDragonPart)) {
               this.guardEntityTick(this::tickNonPassenger, var7);
            }

            var2.pop();
            var2.push("remove");
            if(var7.removed) {
               this.removeFromChunk(var7);
               var5.remove();
               this.onEntityRemoved(var7);
            }

            var2.pop();
         }
      }

      var2.pop();
   }

   public void tickChunk(LevelChunk levelChunk, int var2) {
      ChunkPos var3 = levelChunk.getPos();
      boolean var4 = this.isRaining();
      int var5 = var3.getMinBlockX();
      int var6 = var3.getMinBlockZ();
      ProfilerFiller var7 = this.getProfiler();
      var7.push("thunder");
      if(var4 && this.isThundering() && this.random.nextInt(100000) == 0) {
         BlockPos var8 = this.findLightingTargetAround(this.getBlockRandomPos(var5, 0, var6, 15));
         if(this.isRainingAt(var8)) {
            DifficultyInstance var9 = this.getCurrentDifficultyAt(var8);
            boolean var10 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < (double)var9.getEffectiveDifficulty() * 0.01D;
            if(var10) {
               SkeletonHorse var11 = (SkeletonHorse)EntityType.SKELETON_HORSE.create(this);
               var11.setTrap(true);
               var11.setAge(0);
               var11.setPos((double)var8.getX(), (double)var8.getY(), (double)var8.getZ());
               this.addFreshEntity(var11);
            }

            this.addGlobalEntity(new LightningBolt(this, (double)var8.getX() + 0.5D, (double)var8.getY(), (double)var8.getZ() + 0.5D, var10));
         }
      }

      var7.popPush("iceandsnow");
      if(this.random.nextInt(16) == 0) {
         BlockPos var8 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.getBlockRandomPos(var5, 0, var6, 15));
         BlockPos var9 = var8.below();
         Biome var10 = this.getBiome(var8);
         if(var10.shouldFreeze(this, var9)) {
            this.setBlockAndUpdate(var9, Blocks.ICE.defaultBlockState());
         }

         if(var4 && var10.shouldSnow(this, var8)) {
            this.setBlockAndUpdate(var8, Blocks.SNOW.defaultBlockState());
         }

         if(var4 && this.getBiome(var9).getPrecipitation() == Biome.Precipitation.RAIN) {
            this.getBlockState(var9).getBlock().handleRain(this, var9);
         }
      }

      var7.popPush("tickBlocks");
      if(var2 > 0) {
         for(LevelChunkSection var11 : levelChunk.getSections()) {
            if(var11 != LevelChunk.EMPTY_SECTION && var11.isRandomlyTicking()) {
               int var12 = var11.bottomBlockY();

               for(int var13 = 0; var13 < var2; ++var13) {
                  BlockPos var14 = this.getBlockRandomPos(var5, var12, var6, 15);
                  var7.push("randomTick");
                  BlockState var15 = var11.getBlockState(var14.getX() - var5, var14.getY() - var12, var14.getZ() - var6);
                  if(var15.isRandomlyTicking()) {
                     var15.randomTick(this, var14, this.random);
                  }

                  FluidState var16 = var15.getFluidState();
                  if(var16.isRandomlyTicking()) {
                     var16.randomTick(this, var14, this.random);
                  }

                  var7.pop();
               }
            }
         }
      }

      var7.pop();
   }

   protected BlockPos findLightingTargetAround(BlockPos blockPos) {
      BlockPos var2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos);
      AABB var3 = (new AABB(var2, new BlockPos(var2.getX(), this.getMaxBuildHeight(), var2.getZ()))).inflate(3.0D);
      List<LivingEntity> var4 = this.getEntitiesOfClass(LivingEntity.class, var3, (livingEntity) -> {
         return livingEntity != null && livingEntity.isAlive() && this.canSeeSky(livingEntity.getCommandSenderBlockPosition());
      });
      if(!var4.isEmpty()) {
         return ((LivingEntity)var4.get(this.random.nextInt(var4.size()))).getCommandSenderBlockPosition();
      } else {
         if(var2.getY() == -1) {
            var2 = var2.above(2);
         }

         return var2;
      }
   }

   public boolean isHandlingTick() {
      return this.handlingTick;
   }

   public void updateSleepingPlayerList() {
      this.allPlayersSleeping = false;
      if(!this.players.isEmpty()) {
         int var1 = 0;
         int var2 = 0;

         for(ServerPlayer var4 : this.players) {
            if(var4.isSpectator()) {
               ++var1;
            } else if(var4.isSleeping()) {
               ++var2;
            }
         }

         this.allPlayersSleeping = var2 > 0 && var2 >= this.players.size() - var1;
      }

   }

   public ServerScoreboard getScoreboard() {
      return this.server.getScoreboard();
   }

   private void stopWeather() {
      this.levelData.setRainTime(0);
      this.levelData.setRaining(false);
      this.levelData.setThunderTime(0);
      this.levelData.setThundering(false);
   }

   public void validateSpawn() {
      if(this.levelData.getYSpawn() <= 0) {
         this.levelData.setYSpawn(this.getSeaLevel() + 1);
      }

      int var1 = this.levelData.getXSpawn();
      int var2 = this.levelData.getZSpawn();
      int var3 = 0;

      while(this.getTopBlockState(new BlockPos(var1, 0, var2)).isAir()) {
         var1 += this.random.nextInt(8) - this.random.nextInt(8);
         var2 += this.random.nextInt(8) - this.random.nextInt(8);
         ++var3;
         if(var3 == 10000) {
            break;
         }
      }

      this.levelData.setXSpawn(var1);
      this.levelData.setZSpawn(var2);
   }

   public void resetEmptyTime() {
      this.emptyTime = 0;
   }

   private void tickLiquid(TickNextTickData tickNextTickData) {
      FluidState var2 = this.getFluidState(tickNextTickData.pos);
      if(var2.getType() == tickNextTickData.getType()) {
         var2.tick(this, tickNextTickData.pos);
      }

   }

   private void tickBlock(TickNextTickData tickNextTickData) {
      BlockState var2 = this.getBlockState(tickNextTickData.pos);
      if(var2.getBlock() == tickNextTickData.getType()) {
         var2.tick(this, tickNextTickData.pos, this.random);
      }

   }

   public void tickNonPassenger(Entity entity) {
      if(entity instanceof Player || this.getChunkSource().isEntityTickingChunk(entity)) {
         entity.xOld = entity.x;
         entity.yOld = entity.y;
         entity.zOld = entity.z;
         entity.yRotO = entity.yRot;
         entity.xRotO = entity.xRot;
         if(entity.inChunk) {
            ++entity.tickCount;
            this.getProfiler().push(() -> {
               return Registry.ENTITY_TYPE.getKey(entity.getType()).toString();
            });
            entity.tick();
            this.getProfiler().pop();
         }

         this.updateChunkPos(entity);
         if(entity.inChunk) {
            for(Entity var3 : entity.getPassengers()) {
               this.tickPassenger(entity, var3);
            }
         }

      }
   }

   public void tickPassenger(Entity var1, Entity var2) {
      if(!var2.removed && var2.getVehicle() == var1) {
         if(var2 instanceof Player || this.getChunkSource().isEntityTickingChunk(var2)) {
            var2.xOld = var2.x;
            var2.yOld = var2.y;
            var2.zOld = var2.z;
            var2.yRotO = var2.yRot;
            var2.xRotO = var2.xRot;
            if(var2.inChunk) {
               ++var2.tickCount;
               var2.rideTick();
            }

            this.updateChunkPos(var2);
            if(var2.inChunk) {
               for(Entity var4 : var2.getPassengers()) {
                  this.tickPassenger(var2, var4);
               }
            }

         }
      } else {
         var2.stopRiding();
      }
   }

   public void updateChunkPos(Entity entity) {
      this.getProfiler().push("chunkCheck");
      int var2 = Mth.floor(entity.x / 16.0D);
      int var3 = Mth.floor(entity.y / 16.0D);
      int var4 = Mth.floor(entity.z / 16.0D);
      if(!entity.inChunk || entity.xChunk != var2 || entity.yChunk != var3 || entity.zChunk != var4) {
         if(entity.inChunk && this.hasChunk(entity.xChunk, entity.zChunk)) {
            this.getChunk(entity.xChunk, entity.zChunk).removeEntity(entity, entity.yChunk);
         }

         if(!entity.checkAndResetTeleportedFlag() && !this.hasChunk(var2, var4)) {
            entity.inChunk = false;
         } else {
            this.getChunk(var2, var4).addEntity(entity);
         }
      }

      this.getProfiler().pop();
   }

   public boolean mayInteract(Player player, BlockPos blockPos) {
      return !this.server.isUnderSpawnProtection(this, blockPos, player) && this.getWorldBorder().isWithinBounds(blockPos);
   }

   public void setInitialSpawn(LevelSettings initialSpawn) {
      if(!this.dimension.mayRespawn()) {
         this.levelData.setSpawn(BlockPos.ZERO.above(this.chunkSource.getGenerator().getSpawnHeight()));
      } else if(this.levelData.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES) {
         this.levelData.setSpawn(BlockPos.ZERO.above());
      } else {
         BiomeSource var2 = this.chunkSource.getGenerator().getBiomeSource();
         List<Biome> var3 = var2.getPlayerSpawnBiomes();
         Random var4 = new Random(this.getSeed());
         BlockPos var5 = var2.findBiome(0, 0, 256, var3, var4);
         ChunkPos var6 = var5 == null?new ChunkPos(0, 0):new ChunkPos(var5);
         if(var5 == null) {
            LOGGER.warn("Unable to find spawn biome");
         }

         boolean var7 = false;

         for(Block var9 : BlockTags.VALID_SPAWN.getValues()) {
            if(var2.getSurfaceBlocks().contains(var9.defaultBlockState())) {
               var7 = true;
               break;
            }
         }

         this.levelData.setSpawn(var6.getWorldPosition().offset(8, this.chunkSource.getGenerator().getSpawnHeight(), 8));
         int var8 = 0;
         int var9 = 0;
         int var10 = 0;
         int var11 = -1;
         int var12 = 32;

         for(int var13 = 0; var13 < 1024; ++var13) {
            if(var8 > -16 && var8 <= 16 && var9 > -16 && var9 <= 16) {
               BlockPos var14 = this.dimension.getSpawnPosInChunk(new ChunkPos(var6.x + var8, var6.z + var9), var7);
               if(var14 != null) {
                  this.levelData.setSpawn(var14);
                  break;
               }
            }

            if(var8 == var9 || var8 < 0 && var8 == -var9 || var8 > 0 && var8 == 1 - var9) {
               int var14 = var10;
               var10 = -var11;
               var11 = var14;
            }

            var8 += var10;
            var9 += var11;
         }

         if(initialSpawn.hasStartingBonusItems()) {
            this.generateBonusItemsNearSpawn();
         }

      }
   }

   protected void generateBonusItemsNearSpawn() {
      BonusChestFeature var1 = Feature.BONUS_CHEST;

      for(int var2 = 0; var2 < 10; ++var2) {
         int var3 = this.levelData.getXSpawn() + this.random.nextInt(6) - this.random.nextInt(6);
         int var4 = this.levelData.getZSpawn() + this.random.nextInt(6) - this.random.nextInt(6);
         BlockPos var5 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(var3, 0, var4)).above();
         if(var1.place(this, this.chunkSource.getGenerator(), this.random, var5, (NoneFeatureConfiguration)FeatureConfiguration.NONE)) {
            break;
         }
      }

   }

   @Nullable
   public BlockPos getDimensionSpecificSpawn() {
      return this.dimension.getDimensionSpecificSpawn();
   }

   public void save(@Nullable ProgressListener progressListener, boolean var2, boolean var3) throws LevelConflictException {
      ServerChunkCache var4 = this.getChunkSource();
      if(!var3) {
         if(progressListener != null) {
            progressListener.progressStartNoAbort(new TranslatableComponent("menu.savingLevel", new Object[0]));
         }

         this.saveLevelData();
         if(progressListener != null) {
            progressListener.progressStage(new TranslatableComponent("menu.savingChunks", new Object[0]));
         }

         var4.save(var2);
      }
   }

   protected void saveLevelData() throws LevelConflictException {
      this.checkSession();
      this.dimension.saveData();
      this.getChunkSource().getDataStorage().save();
   }

   public List getEntities(@Nullable EntityType entityType, Predicate predicate) {
      List<Entity> list = Lists.newArrayList();
      ServerChunkCache var4 = this.getChunkSource();
      ObjectIterator var5 = this.entitiesById.values().iterator();

      while(var5.hasNext()) {
         Entity var6 = (Entity)var5.next();
         if((entityType == null || var6.getType() == entityType) && var4.hasChunk(Mth.floor(var6.x) >> 4, Mth.floor(var6.z) >> 4) && predicate.test(var6)) {
            list.add(var6);
         }
      }

      return list;
   }

   public List getDragons() {
      List<EnderDragon> list = Lists.newArrayList();
      ObjectIterator var2 = this.entitiesById.values().iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         if(var3 instanceof EnderDragon && var3.isAlive()) {
            list.add((EnderDragon)var3);
         }
      }

      return list;
   }

   public List getPlayers(Predicate predicate) {
      List<ServerPlayer> list = Lists.newArrayList();

      for(ServerPlayer var4 : this.players) {
         if(predicate.test(var4)) {
            list.add(var4);
         }
      }

      return list;
   }

   @Nullable
   public ServerPlayer getRandomPlayer() {
      List<ServerPlayer> var1 = this.getPlayers(LivingEntity::isAlive);
      return var1.isEmpty()?null:(ServerPlayer)var1.get(this.random.nextInt(var1.size()));
   }

   public Object2IntMap getMobCategoryCounts() {
      Object2IntMap<MobCategory> object2IntMap = new Object2IntOpenHashMap();
      ObjectIterator var2 = this.entitiesById.values().iterator();

      while(true) {
         Entity var3;
         while(true) {
            if(!var2.hasNext()) {
               return object2IntMap;
            }

            var3 = (Entity)var2.next();
            if(!(var3 instanceof Mob)) {
               break;
            }

            Mob var4 = (Mob)var3;
            if(!var4.isPersistenceRequired() && !var4.requiresCustomPersistence()) {
               break;
            }
         }

         MobCategory var4 = var3.getType().getCategory();
         if(var4 != MobCategory.MISC && this.getChunkSource().isInAccessibleChunk(var3)) {
            object2IntMap.mergeInt(var4, 1, Integer::sum);
         }
      }
   }

   public boolean addFreshEntity(Entity entity) {
      return this.addEntity(entity);
   }

   public boolean addWithUUID(Entity entity) {
      return this.addEntity(entity);
   }

   public void addFromAnotherDimension(Entity entity) {
      boolean var2 = entity.forcedLoading;
      entity.forcedLoading = true;
      this.addWithUUID(entity);
      entity.forcedLoading = var2;
      this.updateChunkPos(entity);
   }

   public void addDuringCommandTeleport(ServerPlayer serverPlayer) {
      this.addPlayer(serverPlayer);
      this.updateChunkPos(serverPlayer);
   }

   public void addDuringPortalTeleport(ServerPlayer serverPlayer) {
      this.addPlayer(serverPlayer);
      this.updateChunkPos(serverPlayer);
   }

   public void addNewPlayer(ServerPlayer serverPlayer) {
      this.addPlayer(serverPlayer);
   }

   public void addRespawnedPlayer(ServerPlayer serverPlayer) {
      this.addPlayer(serverPlayer);
   }

   private void addPlayer(ServerPlayer serverPlayer) {
      Entity var2 = (Entity)this.entitiesByUuid.get(serverPlayer.getUUID());
      if(var2 != null) {
         LOGGER.warn("Force-added player with duplicate UUID {}", serverPlayer.getUUID().toString());
         var2.unRide();
         this.removePlayerImmediately((ServerPlayer)var2);
      }

      this.players.add(serverPlayer);
      this.updateSleepingPlayerList();
      ChunkAccess var3 = this.getChunk(Mth.floor(serverPlayer.x / 16.0D), Mth.floor(serverPlayer.z / 16.0D), ChunkStatus.FULL, true);
      if(var3 instanceof LevelChunk) {
         var3.addEntity(serverPlayer);
      }

      this.add(serverPlayer);
   }

   private boolean addEntity(Entity entity) {
      if(entity.removed) {
         LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getKey(entity.getType()));
         return false;
      } else if(this.isUUIDUsed(entity)) {
         return false;
      } else {
         ChunkAccess var2 = this.getChunk(Mth.floor(entity.x / 16.0D), Mth.floor(entity.z / 16.0D), ChunkStatus.FULL, entity.forcedLoading);
         if(!(var2 instanceof LevelChunk)) {
            return false;
         } else {
            var2.addEntity(entity);
            this.add(entity);
            return true;
         }
      }
   }

   public boolean loadFromChunk(Entity entity) {
      if(this.isUUIDUsed(entity)) {
         return false;
      } else {
         this.add(entity);
         return true;
      }
   }

   private boolean isUUIDUsed(Entity entity) {
      Entity entity = (Entity)this.entitiesByUuid.get(entity.getUUID());
      if(entity == null) {
         return false;
      } else {
         LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getKey(entity.getType()), entity.getUUID().toString());
         return true;
      }
   }

   public void unload(LevelChunk levelChunk) {
      this.blockEntitiesToUnload.addAll(levelChunk.getBlockEntities().values());
      ClassInstanceMultiMap[] var2 = levelChunk.getEntitySections();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         for(Entity var7 : var2[var4]) {
            if(!(var7 instanceof ServerPlayer)) {
               if(this.tickingEntities) {
                  throw new IllegalStateException("Removing entity while ticking!");
               }

               this.entitiesById.remove(var7.getId());
               this.onEntityRemoved(var7);
            }
         }
      }

   }

   public void onEntityRemoved(Entity entity) {
      if(entity instanceof EnderDragon) {
         for(EnderDragonPart var5 : ((EnderDragon)entity).getSubEntities()) {
            var5.remove();
         }
      }

      this.entitiesByUuid.remove(entity.getUUID());
      this.getChunkSource().removeEntity(entity);
      if(entity instanceof ServerPlayer) {
         ServerPlayer var2 = (ServerPlayer)entity;
         this.players.remove(var2);
      }

      this.getScoreboard().entityRemoved(entity);
      if(entity instanceof Mob) {
         this.navigations.remove(((Mob)entity).getNavigation());
      }

   }

   private void add(Entity entity) {
      if(this.tickingEntities) {
         this.toAddAfterTick.add(entity);
      } else {
         this.entitiesById.put(entity.getId(), entity);
         if(entity instanceof EnderDragon) {
            for(EnderDragonPart var5 : ((EnderDragon)entity).getSubEntities()) {
               this.entitiesById.put(var5.getId(), var5);
            }
         }

         this.entitiesByUuid.put(entity.getUUID(), entity);
         this.getChunkSource().addEntity(entity);
         if(entity instanceof Mob) {
            this.navigations.add(((Mob)entity).getNavigation());
         }
      }

   }

   public void despawn(Entity entity) {
      if(this.tickingEntities) {
         throw new IllegalStateException("Removing entity while ticking!");
      } else {
         this.removeFromChunk(entity);
         this.entitiesById.remove(entity.getId());
         this.onEntityRemoved(entity);
      }
   }

   private void removeFromChunk(Entity entity) {
      ChunkAccess var2 = this.getChunk(entity.xChunk, entity.zChunk, ChunkStatus.FULL, false);
      if(var2 instanceof LevelChunk) {
         ((LevelChunk)var2).removeEntity(entity);
      }

   }

   public void removePlayerImmediately(ServerPlayer serverPlayer) {
      serverPlayer.remove();
      this.despawn(serverPlayer);
      this.updateSleepingPlayerList();
   }

   public void addGlobalEntity(LightningBolt lightningBolt) {
      this.globalEntities.add(lightningBolt);
      this.server.getPlayerList().broadcast((Player)null, lightningBolt.x, lightningBolt.y, lightningBolt.z, 512.0D, this.dimension.getType(), new ClientboundAddGlobalEntityPacket(lightningBolt));
   }

   public void destroyBlockProgress(int var1, BlockPos blockPos, int var3) {
      for(ServerPlayer var5 : this.server.getPlayerList().getPlayers()) {
         if(var5 != null && var5.level == this && var5.getId() != var1) {
            double var6 = (double)blockPos.getX() - var5.x;
            double var8 = (double)blockPos.getY() - var5.y;
            double var10 = (double)blockPos.getZ() - var5.z;
            if(var6 * var6 + var8 * var8 + var10 * var10 < 1024.0D) {
               var5.connection.send(new ClientboundBlockDestructionPacket(var1, blockPos, var3));
            }
         }
      }

   }

   public void playSound(@Nullable Player player, double var2, double var4, double var6, SoundEvent soundEvent, SoundSource soundSource, float var10, float var11) {
      this.server.getPlayerList().broadcast(player, var2, var4, var6, var10 > 1.0F?(double)(16.0F * var10):16.0D, this.dimension.getType(), new ClientboundSoundPacket(soundEvent, soundSource, var2, var4, var6, var10, var11));
   }

   public void playSound(@Nullable Player player, Entity entity, SoundEvent soundEvent, SoundSource soundSource, float var5, float var6) {
      this.server.getPlayerList().broadcast(player, entity.x, entity.y, entity.z, var5 > 1.0F?(double)(16.0F * var5):16.0D, this.dimension.getType(), new ClientboundSoundEntityPacket(soundEvent, soundSource, entity, var5, var6));
   }

   public void globalLevelEvent(int var1, BlockPos blockPos, int var3) {
      this.server.getPlayerList().broadcastAll(new ClientboundLevelEventPacket(var1, blockPos, var3, true));
   }

   public void levelEvent(@Nullable Player player, int var2, BlockPos blockPos, int var4) {
      this.server.getPlayerList().broadcast(player, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 64.0D, this.dimension.getType(), new ClientboundLevelEventPacket(var2, blockPos, var4, false));
   }

   public void sendBlockUpdated(BlockPos blockPos, BlockState var2, BlockState var3, int var4) {
      this.getChunkSource().blockChanged(blockPos);
      VoxelShape var5 = var2.getCollisionShape(this, blockPos);
      VoxelShape var6 = var3.getCollisionShape(this, blockPos);
      if(Shapes.joinIsNotEmpty(var5, var6, BooleanOp.NOT_SAME)) {
         for(PathNavigation var8 : this.navigations) {
            if(!var8.hasDelayedRecomputation()) {
               var8.recomputePath(blockPos);
            }
         }

      }
   }

   public void broadcastEntityEvent(Entity entity, byte var2) {
      this.getChunkSource().broadcastAndSend(entity, new ClientboundEntityEventPacket(entity, var2));
   }

   public ServerChunkCache getChunkSource() {
      return (ServerChunkCache)super.getChunkSource();
   }

   public Explosion explode(@Nullable Entity entity, DamageSource damageSource, double var3, double var5, double var7, float var9, boolean var10, Explosion.BlockInteraction explosion$BlockInteraction) {
      Explosion explosion = new Explosion(this, entity, var3, var5, var7, var9, var10, explosion$BlockInteraction);
      if(damageSource != null) {
         explosion.setDamageSource(damageSource);
      }

      explosion.explode();
      explosion.finalizeExplosion(false);
      if(explosion$BlockInteraction == Explosion.BlockInteraction.NONE) {
         explosion.clearToBlow();
      }

      for(ServerPlayer var14 : this.players) {
         if(var14.distanceToSqr(var3, var5, var7) < 4096.0D) {
            var14.connection.send(new ClientboundExplodePacket(var3, var5, var7, var9, explosion.getToBlow(), (Vec3)explosion.getHitPlayers().get(var14)));
         }
      }

      return explosion;
   }

   public void blockEvent(BlockPos blockPos, Block block, int var3, int var4) {
      this.blockEvents.add(new BlockEventData(blockPos, block, var3, var4));
   }

   private void runBlockEvents() {
      while(!this.blockEvents.isEmpty()) {
         BlockEventData var1 = (BlockEventData)this.blockEvents.removeFirst();
         if(this.doBlockEvent(var1)) {
            this.server.getPlayerList().broadcast((Player)null, (double)var1.getPos().getX(), (double)var1.getPos().getY(), (double)var1.getPos().getZ(), 64.0D, this.dimension.getType(), new ClientboundBlockEventPacket(var1.getPos(), var1.getBlock(), var1.getParamA(), var1.getParamB()));
         }
      }

   }

   private boolean doBlockEvent(BlockEventData blockEventData) {
      BlockState var2 = this.getBlockState(blockEventData.getPos());
      return var2.getBlock() == blockEventData.getBlock()?var2.triggerEvent(this, blockEventData.getPos(), blockEventData.getParamA(), blockEventData.getParamB()):false;
   }

   public ServerTickList getBlockTicks() {
      return this.blockTicks;
   }

   public ServerTickList getLiquidTicks() {
      return this.liquidTicks;
   }

   @Nonnull
   public MinecraftServer getServer() {
      return this.server;
   }

   public PortalForcer getPortalForcer() {
      return this.portalForcer;
   }

   public StructureManager getStructureManager() {
      return this.levelStorage.getStructureManager();
   }

   public int sendParticles(ParticleOptions particleOptions, double var2, double var4, double var6, int var8, double var9, double var11, double var13, double var15) {
      ClientboundLevelParticlesPacket var17 = new ClientboundLevelParticlesPacket(particleOptions, false, (float)var2, (float)var4, (float)var6, (float)var9, (float)var11, (float)var13, (float)var15, var8);
      int var18 = 0;

      for(int var19 = 0; var19 < this.players.size(); ++var19) {
         ServerPlayer var20 = (ServerPlayer)this.players.get(var19);
         if(this.sendParticles(var20, false, var2, var4, var6, var17)) {
            ++var18;
         }
      }

      return var18;
   }

   public boolean sendParticles(ServerPlayer serverPlayer, ParticleOptions particleOptions, boolean var3, double var4, double var6, double var8, int var10, double var11, double var13, double var15, double var17) {
      Packet<?> var19 = new ClientboundLevelParticlesPacket(particleOptions, var3, (float)var4, (float)var6, (float)var8, (float)var11, (float)var13, (float)var15, (float)var17, var10);
      return this.sendParticles(serverPlayer, var3, var4, var6, var8, var19);
   }

   private boolean sendParticles(ServerPlayer serverPlayer, boolean var2, double var3, double var5, double var7, Packet packet) {
      if(serverPlayer.getLevel() != this) {
         return false;
      } else {
         BlockPos var10 = serverPlayer.getCommandSenderBlockPosition();
         if(var10.closerThan(new Vec3(var3, var5, var7), var2?512.0D:32.0D)) {
            serverPlayer.connection.send(packet);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public Entity getEntity(int i) {
      return (Entity)this.entitiesById.get(i);
   }

   @Nullable
   public Entity getEntity(UUID uUID) {
      return (Entity)this.entitiesByUuid.get(uUID);
   }

   @Nullable
   public BlockPos findNearestMapFeature(String string, BlockPos var2, int var3, boolean var4) {
      return this.getChunkSource().getGenerator().findNearestMapFeature(this, string, var2, var3, var4);
   }

   public RecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   public TagManager getTagManager() {
      return this.server.getTags();
   }

   public void setGameTime(long gameTime) {
      super.setGameTime(gameTime);
      this.levelData.getScheduledEvents().tick(this.server, gameTime);
   }

   public boolean noSave() {
      return this.noSave;
   }

   public void checkSession() throws LevelConflictException {
      this.levelStorage.checkSession();
   }

   public LevelStorage getLevelStorage() {
      return this.levelStorage;
   }

   public DimensionDataStorage getDataStorage() {
      return this.getChunkSource().getDataStorage();
   }

   @Nullable
   public MapItemSavedData getMapData(String string) {
      return (MapItemSavedData)this.getServer().getLevel(DimensionType.OVERWORLD).getDataStorage().get(() -> {
         return new MapItemSavedData(string);
      }, string);
   }

   public void setMapData(MapItemSavedData mapData) {
      this.getServer().getLevel(DimensionType.OVERWORLD).getDataStorage().set(mapData);
   }

   public int getFreeMapId() {
      return ((MapIndex)this.getServer().getLevel(DimensionType.OVERWORLD).getDataStorage().computeIfAbsent(MapIndex::<init>, "idcounts")).getFreeAuxValueForMap();
   }

   public void setSpawnPos(BlockPos spawnPos) {
      ChunkPos var2 = new ChunkPos(new BlockPos(this.levelData.getXSpawn(), 0, this.levelData.getZSpawn()));
      super.setSpawnPos(spawnPos);
      this.getChunkSource().removeRegionTicket(TicketType.START, var2, 11, Unit.INSTANCE);
      this.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(spawnPos), 11, Unit.INSTANCE);
   }

   public LongSet getForcedChunks() {
      ForcedChunksSavedData var1 = (ForcedChunksSavedData)this.getDataStorage().get(ForcedChunksSavedData::<init>, "chunks");
      return (LongSet)(var1 != null?LongSets.unmodifiable(var1.getChunks()):LongSets.EMPTY_SET);
   }

   public boolean setChunkForced(int var1, int var2, boolean var3) {
      ForcedChunksSavedData var4 = (ForcedChunksSavedData)this.getDataStorage().computeIfAbsent(ForcedChunksSavedData::<init>, "chunks");
      ChunkPos var5 = new ChunkPos(var1, var2);
      long var6 = var5.toLong();
      boolean var8;
      if(var3) {
         var8 = var4.getChunks().add(var6);
         if(var8) {
            this.getChunk(var1, var2);
         }
      } else {
         var8 = var4.getChunks().remove(var6);
      }

      var4.setDirty(var8);
      if(var8) {
         this.getChunkSource().updateChunkForced(var5, var3);
      }

      return var8;
   }

   public List players() {
      return this.players;
   }

   public void onBlockStateChange(BlockPos blockPos, BlockState var2, BlockState var3) {
      Optional<PoiType> var4 = PoiType.forState(var2);
      Optional<PoiType> var5 = PoiType.forState(var3);
      if(!Objects.equals(var4, var5)) {
         BlockPos var6 = blockPos.immutable();
         var4.ifPresent((poiType) -> {
            this.getServer().execute(() -> {
               this.getPoiManager().remove(var6);
               DebugPackets.sendPoiRemovedPacket(this, var6);
            });
         });
         var5.ifPresent((poiType) -> {
            this.getServer().execute(() -> {
               this.getPoiManager().add(var6, poiType);
               DebugPackets.sendPoiAddedPacket(this, var6);
            });
         });
      }
   }

   public PoiManager getPoiManager() {
      return this.getChunkSource().getPoiManager();
   }

   public boolean isVillage(BlockPos blockPos) {
      return this.closeToVillage(blockPos, 1);
   }

   public boolean isVillage(SectionPos sectionPos) {
      return this.isVillage(sectionPos.center());
   }

   public boolean closeToVillage(BlockPos blockPos, int var2) {
      return var2 > 6?false:this.sectionsToVillage(SectionPos.of(blockPos)) <= var2;
   }

   public int sectionsToVillage(SectionPos sectionPos) {
      return this.getPoiManager().sectionsToVillage(sectionPos);
   }

   public Raids getRaids() {
      return this.raids;
   }

   @Nullable
   public Raid getRaidAt(BlockPos blockPos) {
      return this.raids.getNearbyRaid(blockPos, 9216);
   }

   public boolean isRaided(BlockPos blockPos) {
      return this.getRaidAt(blockPos) != null;
   }

   public void onReputationEvent(ReputationEventType reputationEventType, Entity entity, ReputationEventHandler reputationEventHandler) {
      reputationEventHandler.onReputationEventFrom(reputationEventType, entity);
   }

   public void saveDebugReport(Path path) throws IOException {
      ChunkMap var2 = this.getChunkSource().chunkMap;
      Writer var3 = Files.newBufferedWriter(path.resolve("stats.txt"), new OpenOption[0]);
      Throwable var4 = null;

      try {
         var3.write(String.format("spawning_chunks: %d\n", new Object[]{Integer.valueOf(var2.getDistanceManager().getNaturalSpawnChunkCount())}));
         ObjectIterator var5 = this.getMobCategoryCounts().object2IntEntrySet().iterator();

         while(var5.hasNext()) {
            it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<MobCategory> var6 = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry)var5.next();
            var3.write(String.format("spawn_count.%s: %d\n", new Object[]{((MobCategory)var6.getKey()).getName(), Integer.valueOf(var6.getIntValue())}));
         }

         var3.write(String.format("entities: %d\n", new Object[]{Integer.valueOf(this.entitiesById.size())}));
         var3.write(String.format("block_entities: %d\n", new Object[]{Integer.valueOf(this.blockEntityList.size())}));
         var3.write(String.format("block_ticks: %d\n", new Object[]{Integer.valueOf(this.getBlockTicks().size())}));
         var3.write(String.format("fluid_ticks: %d\n", new Object[]{Integer.valueOf(this.getLiquidTicks().size())}));
         var3.write("distance_manager: " + var2.getDistanceManager().getDebugStatus() + "\n");
         var3.write(String.format("pending_tasks: %d\n", new Object[]{Integer.valueOf(this.getChunkSource().getPendingTasksCount())}));
      } catch (Throwable var164) {
         var4 = var164;
         throw var164;
      } finally {
         if(var3 != null) {
            if(var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var153) {
                  var4.addSuppressed(var153);
               }
            } else {
               var3.close();
            }
         }

      }

      var3 = new CrashReport("Level dump", new Exception("dummy"));
      this.fillReportDetails(var3);
      var4 = Files.newBufferedWriter(path.resolve("example_crash.txt"), new OpenOption[0]);
      Throwable var5 = null;

      try {
         var4.write(var3.getFriendlyReport());
      } catch (Throwable var158) {
         var5 = var158;
         throw var158;
      } finally {
         if(var4 != null) {
            if(var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var152) {
                  var5.addSuppressed(var152);
               }
            } else {
               var4.close();
            }
         }

      }

      Path var4 = path.resolve("chunks.csv");
      var5 = Files.newBufferedWriter(var4, new OpenOption[0]);
      Throwable var6 = null;

      try {
         var2.dumpChunks(var5);
      } catch (Throwable var157) {
         var6 = var157;
         throw var157;
      } finally {
         if(var5 != null) {
            if(var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var151) {
                  var6.addSuppressed(var151);
               }
            } else {
               var5.close();
            }
         }

      }

      Path var5 = path.resolve("entities.csv");
      var6 = Files.newBufferedWriter(var5, new OpenOption[0]);
      Throwable var7 = null;

      try {
         dumpEntities(var6, this.entitiesById.values());
      } catch (Throwable var156) {
         var7 = var156;
         throw var156;
      } finally {
         if(var6 != null) {
            if(var7 != null) {
               try {
                  var6.close();
               } catch (Throwable var150) {
                  var7.addSuppressed(var150);
               }
            } else {
               var6.close();
            }
         }

      }

      Path var6 = path.resolve("global_entities.csv");
      var7 = Files.newBufferedWriter(var6, new OpenOption[0]);
      Throwable var8 = null;

      try {
         dumpEntities(var7, this.globalEntities);
      } catch (Throwable var155) {
         var8 = var155;
         throw var155;
      } finally {
         if(var7 != null) {
            if(var8 != null) {
               try {
                  var7.close();
               } catch (Throwable var149) {
                  var8.addSuppressed(var149);
               }
            } else {
               var7.close();
            }
         }

      }

      Path var7 = path.resolve("block_entities.csv");
      var8 = Files.newBufferedWriter(var7, new OpenOption[0]);
      Throwable var9 = null;

      try {
         this.dumpBlockEntities(var8);
      } catch (Throwable var154) {
         var9 = var154;
         throw var154;
      } finally {
         if(var8 != null) {
            if(var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var148) {
                  var9.addSuppressed(var148);
               }
            } else {
               var8.close();
            }
         }

      }

   }

   private static void dumpEntities(Writer writer, Iterable iterable) throws IOException {
      CsvOutput var2 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").build(writer);

      for(Entity var4 : iterable) {
         Component var5 = var4.getCustomName();
         Component var6 = var4.getDisplayName();
         var2.writeRow(new Object[]{Double.valueOf(var4.x), Double.valueOf(var4.y), Double.valueOf(var4.z), var4.getUUID(), Registry.ENTITY_TYPE.getKey(var4.getType()), Boolean.valueOf(var4.isAlive()), var6.getString(), var5 != null?var5.getString():null});
      }

   }

   private void dumpBlockEntities(Writer writer) throws IOException {
      CsvOutput var2 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build(writer);

      for(BlockEntity var4 : this.blockEntityList) {
         BlockPos var5 = var4.getBlockPos();
         var2.writeRow(new Object[]{Integer.valueOf(var5.getX()), Integer.valueOf(var5.getY()), Integer.valueOf(var5.getZ()), Registry.BLOCK_ENTITY_TYPE.getKey(var4.getType())});
      }

   }

   // $FF: synthetic method
   public Scoreboard getScoreboard() {
      return this.getScoreboard();
   }

   // $FF: synthetic method
   public ChunkSource getChunkSource() {
      return this.getChunkSource();
   }

   // $FF: synthetic method
   public TickList getLiquidTicks() {
      return this.getLiquidTicks();
   }

   // $FF: synthetic method
   public TickList getBlockTicks() {
      return this.getBlockTicks();
   }
}
