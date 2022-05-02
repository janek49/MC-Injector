package net.minecraft.client.multiplayer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.EmptyTickList;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;

@ClientJarOnly
public class MultiPlayerLevel extends Level {
   private final List globalEntities = Lists.newArrayList();
   private final Int2ObjectMap entitiesById = new Int2ObjectOpenHashMap();
   private final ClientPacketListener connection;
   private final LevelRenderer levelRenderer;
   private final Minecraft minecraft = Minecraft.getInstance();
   private final List players = Lists.newArrayList();
   private int delayUntilNextMoodSound;
   private Scoreboard scoreboard;
   private final Map mapData;

   public MultiPlayerLevel(ClientPacketListener connection, LevelSettings levelSettings, DimensionType dimensionType, int var4, ProfilerFiller profilerFiller, LevelRenderer levelRenderer) {
      super(new LevelData(levelSettings, "MpServer"), dimensionType, (level, dimension) -> {
         return new ClientChunkCache((MultiPlayerLevel)level, var4);
      }, profilerFiller, true);
      this.delayUntilNextMoodSound = this.random.nextInt(12000);
      this.scoreboard = new Scoreboard();
      this.mapData = Maps.newHashMap();
      this.connection = connection;
      this.levelRenderer = levelRenderer;
      this.setSpawnPos(new BlockPos(8, 64, 8));
      this.updateSkyBrightness();
      this.prepareWeather();
   }

   public void tick(BooleanSupplier booleanSupplier) {
      this.getWorldBorder().tick();
      this.tickTime();
      this.getProfiler().push("blocks");
      this.chunkSource.tick(booleanSupplier);
      this.playMoodSounds();
      this.getProfiler().pop();
   }

   public Iterable entitiesForRendering() {
      return Iterables.concat(this.entitiesById.values(), this.globalEntities);
   }

   public void tickEntities() {
      ProfilerFiller var1 = this.getProfiler();
      var1.push("entities");
      var1.push("global");

      for(int var2 = 0; var2 < this.globalEntities.size(); ++var2) {
         Entity var3 = (Entity)this.globalEntities.get(var2);
         this.guardEntityTick((entity) -> {
            ++entity.tickCount;
            entity.tick();
         }, var3);
         if(var3.removed) {
            this.globalEntities.remove(var2--);
         }
      }

      var1.popPush("regular");
      ObjectIterator<Entry<Entity>> var2 = this.entitiesById.int2ObjectEntrySet().iterator();

      while(var2.hasNext()) {
         Entry<Entity> var3 = (Entry)var2.next();
         Entity var4 = (Entity)var3.getValue();
         if(!var4.isPassenger()) {
            var1.push("tick");
            if(!var4.removed) {
               this.guardEntityTick(this::tickNonPassenger, var4);
            }

            var1.pop();
            var1.push("remove");
            if(var4.removed) {
               var2.remove();
               this.onEntityRemoved(var4);
            }

            var1.pop();
         }
      }

      var1.pop();
      this.tickBlockEntities();
      var1.pop();
   }

   public void tickNonPassenger(Entity entity) {
      if(entity instanceof Player || this.getChunkSource().isEntityTickingChunk(entity)) {
         entity.xOld = entity.x;
         entity.yOld = entity.y;
         entity.zOld = entity.z;
         entity.yRotO = entity.yRot;
         entity.xRotO = entity.xRot;
         if(entity.inChunk || entity.isSpectator()) {
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

   public void unload(LevelChunk levelChunk) {
      this.blockEntitiesToUnload.addAll(levelChunk.getBlockEntities().values());
      this.chunkSource.getLightEngine().enableLightSources(levelChunk.getPos(), false);
   }

   public boolean hasChunk(int var1, int var2) {
      return true;
   }

   private void playMoodSounds() {
      if(this.minecraft.player != null) {
         if(this.delayUntilNextMoodSound > 0) {
            --this.delayUntilNextMoodSound;
         } else {
            BlockPos var1 = new BlockPos(this.minecraft.player);
            BlockPos var2 = var1.offset(4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1));
            double var3 = var1.distSqr(var2);
            if(var3 >= 4.0D && var3 <= 256.0D) {
               BlockState var5 = this.getBlockState(var2);
               if(var5.isAir() && this.getRawBrightness(var2, 0) <= this.random.nextInt(8) && this.getBrightness(LightLayer.SKY, var2) <= 0) {
                  this.playLocalSound((double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D, SoundEvents.AMBIENT_CAVE, SoundSource.AMBIENT, 0.7F, 0.8F + this.random.nextFloat() * 0.2F, false);
                  this.delayUntilNextMoodSound = this.random.nextInt(12000) + 6000;
               }
            }

         }
      }
   }

   public int getEntityCount() {
      return this.entitiesById.size();
   }

   public void addLightning(LightningBolt lightningBolt) {
      this.globalEntities.add(lightningBolt);
   }

   public void addPlayer(int var1, AbstractClientPlayer abstractClientPlayer) {
      this.addEntity(var1, abstractClientPlayer);
      this.players.add(abstractClientPlayer);
   }

   public void putNonPlayerEntity(int var1, Entity entity) {
      this.addEntity(var1, entity);
   }

   private void addEntity(int var1, Entity entity) {
      this.removeEntity(var1);
      this.entitiesById.put(var1, entity);
      this.getChunkSource().getChunk(Mth.floor(entity.x / 16.0D), Mth.floor(entity.z / 16.0D), ChunkStatus.FULL, true).addEntity(entity);
   }

   public void removeEntity(int i) {
      Entity var2 = (Entity)this.entitiesById.remove(i);
      if(var2 != null) {
         var2.remove();
         this.onEntityRemoved(var2);
      }

   }

   private void onEntityRemoved(Entity entity) {
      entity.unRide();
      if(entity.inChunk) {
         this.getChunk(entity.xChunk, entity.zChunk).removeEntity(entity);
      }

      this.players.remove(entity);
   }

   public void reAddEntitiesToChunk(LevelChunk levelChunk) {
      ObjectIterator var2 = this.entitiesById.int2ObjectEntrySet().iterator();

      while(var2.hasNext()) {
         Entry<Entity> var3 = (Entry)var2.next();
         Entity var4 = (Entity)var3.getValue();
         int var5 = Mth.floor(var4.x / 16.0D);
         int var6 = Mth.floor(var4.z / 16.0D);
         if(var5 == levelChunk.getPos().x && var6 == levelChunk.getPos().z) {
            levelChunk.addEntity(var4);
         }
      }

   }

   @Nullable
   public Entity getEntity(int i) {
      return (Entity)this.entitiesById.get(i);
   }

   public void setKnownState(BlockPos blockPos, BlockState blockState) {
      this.setBlock(blockPos, blockState, 19);
   }

   public void disconnect() {
      this.connection.getConnection().disconnect(new TranslatableComponent("multiplayer.status.quitting", new Object[0]));
   }

   public void animateTick(int var1, int var2, int var3) {
      int var4 = 32;
      Random var5 = new Random();
      ItemStack var6 = this.minecraft.player.getMainHandItem();
      boolean var7 = this.minecraft.gameMode.getPlayerMode() == GameType.CREATIVE && !var6.isEmpty() && var6.getItem() == Blocks.BARRIER.asItem();
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();

      for(int var9 = 0; var9 < 667; ++var9) {
         this.doAnimateTick(var1, var2, var3, 16, var5, var7, var8);
         this.doAnimateTick(var1, var2, var3, 32, var5, var7, var8);
      }

   }

   public void doAnimateTick(int var1, int var2, int var3, int var4, Random random, boolean var6, BlockPos.MutableBlockPos blockPos$MutableBlockPos) {
      int var8 = var1 + this.random.nextInt(var4) - this.random.nextInt(var4);
      int var9 = var2 + this.random.nextInt(var4) - this.random.nextInt(var4);
      int var10 = var3 + this.random.nextInt(var4) - this.random.nextInt(var4);
      blockPos$MutableBlockPos.set(var8, var9, var10);
      BlockState var11 = this.getBlockState(blockPos$MutableBlockPos);
      var11.getBlock().animateTick(var11, this, blockPos$MutableBlockPos, random);
      FluidState var12 = this.getFluidState(blockPos$MutableBlockPos);
      if(!var12.isEmpty()) {
         var12.animateTick(this, blockPos$MutableBlockPos, random);
         ParticleOptions var13 = var12.getDripParticle();
         if(var13 != null && this.random.nextInt(10) == 0) {
            boolean var14 = var11.isFaceSturdy(this, blockPos$MutableBlockPos, Direction.DOWN);
            BlockPos var15 = blockPos$MutableBlockPos.below();
            this.trySpawnDripParticles(var15, this.getBlockState(var15), var13, var14);
         }
      }

      if(var6 && var11.getBlock() == Blocks.BARRIER) {
         this.addParticle(ParticleTypes.BARRIER, (double)((float)var8 + 0.5F), (double)((float)var9 + 0.5F), (double)((float)var10 + 0.5F), 0.0D, 0.0D, 0.0D);
      }

   }

   private void trySpawnDripParticles(BlockPos blockPos, BlockState blockState, ParticleOptions particleOptions, boolean var4) {
      if(blockState.getFluidState().isEmpty()) {
         VoxelShape var5 = blockState.getCollisionShape(this, blockPos);
         double var6 = var5.max(Direction.Axis.Y);
         if(var6 < 1.0D) {
            if(var4) {
               this.spawnFluidParticle((double)blockPos.getX(), (double)(blockPos.getX() + 1), (double)blockPos.getZ(), (double)(blockPos.getZ() + 1), (double)(blockPos.getY() + 1) - 0.05D, particleOptions);
            }
         } else if(!blockState.is(BlockTags.IMPERMEABLE)) {
            double var8 = var5.min(Direction.Axis.Y);
            if(var8 > 0.0D) {
               this.spawnParticle(blockPos, particleOptions, var5, (double)blockPos.getY() + var8 - 0.05D);
            } else {
               BlockPos var10 = blockPos.below();
               BlockState var11 = this.getBlockState(var10);
               VoxelShape var12 = var11.getCollisionShape(this, var10);
               double var13 = var12.max(Direction.Axis.Y);
               if(var13 < 1.0D && var11.getFluidState().isEmpty()) {
                  this.spawnParticle(blockPos, particleOptions, var5, (double)blockPos.getY() - 0.05D);
               }
            }
         }

      }
   }

   private void spawnParticle(BlockPos blockPos, ParticleOptions particleOptions, VoxelShape voxelShape, double var4) {
      this.spawnFluidParticle((double)blockPos.getX() + voxelShape.min(Direction.Axis.X), (double)blockPos.getX() + voxelShape.max(Direction.Axis.X), (double)blockPos.getZ() + voxelShape.min(Direction.Axis.Z), (double)blockPos.getZ() + voxelShape.max(Direction.Axis.Z), var4, particleOptions);
   }

   private void spawnFluidParticle(double var1, double var3, double var5, double var7, double var9, ParticleOptions particleOptions) {
      this.addParticle(particleOptions, Mth.lerp(this.random.nextDouble(), var1, var3), var9, Mth.lerp(this.random.nextDouble(), var5, var7), 0.0D, 0.0D, 0.0D);
   }

   public void removeAllPendingEntityRemovals() {
      ObjectIterator<Entry<Entity>> var1 = this.entitiesById.int2ObjectEntrySet().iterator();

      while(var1.hasNext()) {
         Entry<Entity> var2 = (Entry)var1.next();
         Entity var3 = (Entity)var2.getValue();
         if(var3.removed) {
            var1.remove();
            this.onEntityRemoved(var3);
         }
      }

   }

   public CrashReportCategory fillReportDetails(CrashReport crashReport) {
      CrashReportCategory crashReportCategory = super.fillReportDetails(crashReport);
      crashReportCategory.setDetail("Server brand", () -> {
         return this.minecraft.player.getServerBrand();
      });
      crashReportCategory.setDetail("Server type", () -> {
         return this.minecraft.getSingleplayerServer() == null?"Non-integrated multiplayer server":"Integrated singleplayer server";
      });
      return crashReportCategory;
   }

   public void playSound(@Nullable Player player, double var2, double var4, double var6, SoundEvent soundEvent, SoundSource soundSource, float var10, float var11) {
      if(player == this.minecraft.player) {
         this.playLocalSound(var2, var4, var6, soundEvent, soundSource, var10, var11, false);
      }

   }

   public void playSound(@Nullable Player player, Entity entity, SoundEvent soundEvent, SoundSource soundSource, float var5, float var6) {
      if(player == this.minecraft.player) {
         this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(soundEvent, soundSource, entity));
      }

   }

   public void playLocalSound(BlockPos blockPos, SoundEvent soundEvent, SoundSource soundSource, float var4, float var5, boolean var6) {
      this.playLocalSound((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, soundEvent, soundSource, var4, var5, var6);
   }

   public void playLocalSound(double var1, double var3, double var5, SoundEvent soundEvent, SoundSource soundSource, float var9, float var10, boolean var11) {
      double var12 = this.minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(var1, var3, var5);
      SimpleSoundInstance var14 = new SimpleSoundInstance(soundEvent, soundSource, var9, var10, (float)var1, (float)var3, (float)var5);
      if(var11 && var12 > 100.0D) {
         double var15 = Math.sqrt(var12) / 40.0D;
         this.minecraft.getSoundManager().playDelayed(var14, (int)(var15 * 20.0D));
      } else {
         this.minecraft.getSoundManager().play(var14);
      }

   }

   public void createFireworks(double var1, double var3, double var5, double var7, double var9, double var11, @Nullable CompoundTag compoundTag) {
      this.minecraft.particleEngine.add(new FireworkParticles.Starter(this, var1, var3, var5, var7, var9, var11, this.minecraft.particleEngine, compoundTag));
   }

   public void sendPacketToServer(Packet packet) {
      this.connection.send(packet);
   }

   public RecipeManager getRecipeManager() {
      return this.connection.getRecipeManager();
   }

   public void setScoreboard(Scoreboard scoreboard) {
      this.scoreboard = scoreboard;
   }

   public void setDayTime(long dayTime) {
      if(dayTime < 0L) {
         dayTime = -dayTime;
         ((GameRules.BooleanValue)this.getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(false, (MinecraftServer)null);
      } else {
         ((GameRules.BooleanValue)this.getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(true, (MinecraftServer)null);
      }

      super.setDayTime(dayTime);
   }

   public TickList getBlockTicks() {
      return EmptyTickList.empty();
   }

   public TickList getLiquidTicks() {
      return EmptyTickList.empty();
   }

   public ClientChunkCache getChunkSource() {
      return (ClientChunkCache)super.getChunkSource();
   }

   @Nullable
   public MapItemSavedData getMapData(String string) {
      return (MapItemSavedData)this.mapData.get(string);
   }

   public void setMapData(MapItemSavedData mapData) {
      this.mapData.put(mapData.getId(), mapData);
   }

   public int getFreeMapId() {
      return 0;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public TagManager getTagManager() {
      return this.connection.getTags();
   }

   public void sendBlockUpdated(BlockPos blockPos, BlockState var2, BlockState var3, int var4) {
      this.levelRenderer.blockChanged(this, blockPos, var2, var3, var4);
   }

   public void setBlocksDirty(BlockPos blockPos, BlockState var2, BlockState var3) {
      this.levelRenderer.setBlockDirty(blockPos, var2, var3);
   }

   public void setSectionDirtyWithNeighbors(int var1, int var2, int var3) {
      this.levelRenderer.setSectionDirtyWithNeighbors(var1, var2, var3);
   }

   public void destroyBlockProgress(int var1, BlockPos blockPos, int var3) {
      this.levelRenderer.destroyBlockProgress(var1, blockPos, var3);
   }

   public void globalLevelEvent(int var1, BlockPos blockPos, int var3) {
      this.levelRenderer.globalLevelEvent(var1, blockPos, var3);
   }

   public void levelEvent(@Nullable Player player, int var2, BlockPos blockPos, int var4) {
      try {
         this.levelRenderer.levelEvent(player, var2, blockPos, var4);
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.forThrowable(var8, "Playing level event");
         CrashReportCategory var7 = var6.addCategory("Level event being played");
         var7.setDetail("Block coordinates", (Object)CrashReportCategory.formatLocation(blockPos));
         var7.setDetail("Event source", (Object)player);
         var7.setDetail("Event type", (Object)Integer.valueOf(var2));
         var7.setDetail("Event data", (Object)Integer.valueOf(var4));
         throw new ReportedException(var6);
      }
   }

   public void addParticle(ParticleOptions particleOptions, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.levelRenderer.addParticle(particleOptions, particleOptions.getType().getOverrideLimiter(), var2, var4, var6, var8, var10, var12);
   }

   public void addParticle(ParticleOptions particleOptions, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      this.levelRenderer.addParticle(particleOptions, particleOptions.getType().getOverrideLimiter() || var2, var3, var5, var7, var9, var11, var13);
   }

   public void addAlwaysVisibleParticle(ParticleOptions particleOptions, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.levelRenderer.addParticle(particleOptions, false, true, var2, var4, var6, var8, var10, var12);
   }

   public void addAlwaysVisibleParticle(ParticleOptions particleOptions, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      this.levelRenderer.addParticle(particleOptions, particleOptions.getType().getOverrideLimiter() || var2, true, var3, var5, var7, var9, var11, var13);
   }

   public List players() {
      return this.players;
   }

   // $FF: synthetic method
   public ChunkSource getChunkSource() {
      return this.getChunkSource();
   }
}
