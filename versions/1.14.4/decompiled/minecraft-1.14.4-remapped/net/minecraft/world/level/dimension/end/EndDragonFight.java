package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.end.DragonRespawnAnimation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndDragonFight {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Predicate VALID_PLAYER = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.withinDistance(0.0D, 128.0D, 0.0D, 192.0D));
   private final ServerBossEvent dragonEvent = (ServerBossEvent)(new ServerBossEvent(new TranslatableComponent("entity.minecraft.ender_dragon", new Object[0]), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS)).setPlayBossMusic(true).setCreateWorldFog(true);
   private final ServerLevel level;
   private final List gateways = Lists.newArrayList();
   private final BlockPattern exitPortalPattern;
   private int ticksSinceDragonSeen;
   private int crystalsAlive;
   private int ticksSinceCrystalsScanned;
   private int ticksSinceLastPlayerScan;
   private boolean dragonKilled;
   private boolean previouslyKilled;
   private UUID dragonUUID;
   private boolean needsStateScanning = true;
   private BlockPos portalLocation;
   private DragonRespawnAnimation respawnStage;
   private int respawnTime;
   private List respawnCrystals;

   public EndDragonFight(ServerLevel level, CompoundTag compoundTag) {
      this.level = level;
      if(compoundTag.contains("DragonKilled", 99)) {
         if(compoundTag.hasUUID("DragonUUID")) {
            this.dragonUUID = compoundTag.getUUID("DragonUUID");
         }

         this.dragonKilled = compoundTag.getBoolean("DragonKilled");
         this.previouslyKilled = compoundTag.getBoolean("PreviouslyKilled");
         if(compoundTag.getBoolean("IsRespawning")) {
            this.respawnStage = DragonRespawnAnimation.START;
         }

         if(compoundTag.contains("ExitPortalLocation", 10)) {
            this.portalLocation = NbtUtils.readBlockPos(compoundTag.getCompound("ExitPortalLocation"));
         }
      } else {
         this.dragonKilled = true;
         this.previouslyKilled = true;
      }

      if(compoundTag.contains("Gateways", 9)) {
         ListTag var3 = compoundTag.getList("Gateways", 3);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            this.gateways.add(Integer.valueOf(var3.getInt(var4)));
         }
      } else {
         this.gateways.addAll(ContiguousSet.create(Range.closedOpen(Integer.valueOf(0), Integer.valueOf(20)), DiscreteDomain.integers()));
         Collections.shuffle(this.gateways, new Random(level.getSeed()));
      }

      this.exitPortalPattern = BlockPatternBuilder.start().aisle(new String[]{"       ", "       ", "       ", "   #   ", "       ", "       ", "       "}).aisle(new String[]{"       ", "       ", "       ", "   #   ", "       ", "       ", "       "}).aisle(new String[]{"       ", "       ", "       ", "   #   ", "       ", "       ", "       "}).aisle(new String[]{"  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  "}).aisle(new String[]{"       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       "}).where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.BEDROCK))).build();
   }

   public CompoundTag saveData() {
      CompoundTag compoundTag = new CompoundTag();
      if(this.dragonUUID != null) {
         compoundTag.putUUID("DragonUUID", this.dragonUUID);
      }

      compoundTag.putBoolean("DragonKilled", this.dragonKilled);
      compoundTag.putBoolean("PreviouslyKilled", this.previouslyKilled);
      if(this.portalLocation != null) {
         compoundTag.put("ExitPortalLocation", NbtUtils.writeBlockPos(this.portalLocation));
      }

      ListTag var2 = new ListTag();
      Iterator var3 = this.gateways.iterator();

      while(var3.hasNext()) {
         int var4 = ((Integer)var3.next()).intValue();
         var2.add(new IntTag(var4));
      }

      compoundTag.put("Gateways", var2);
      return compoundTag;
   }

   public void tick() {
      this.dragonEvent.setVisible(!this.dragonKilled);
      if(++this.ticksSinceLastPlayerScan >= 20) {
         this.updatePlayers();
         this.ticksSinceLastPlayerScan = 0;
      }

      if(!this.dragonEvent.getPlayers().isEmpty()) {
         this.level.getChunkSource().addRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
         boolean var1 = this.isArenaLoaded();
         if(this.needsStateScanning && var1) {
            this.scanState();
            this.needsStateScanning = false;
         }

         if(this.respawnStage != null) {
            if(this.respawnCrystals == null && var1) {
               this.respawnStage = null;
               this.tryRespawn();
            }

            this.respawnStage.tick(this.level, this, this.respawnCrystals, this.respawnTime++, this.portalLocation);
         }

         if(!this.dragonKilled) {
            if((this.dragonUUID == null || ++this.ticksSinceDragonSeen >= 1200) && var1) {
               this.findOrCreateDragon();
               this.ticksSinceDragonSeen = 0;
            }

            if(++this.ticksSinceCrystalsScanned >= 100 && var1) {
               this.updateCrystalCount();
               this.ticksSinceCrystalsScanned = 0;
            }
         }
      } else {
         this.level.getChunkSource().removeRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
      }

   }

   private void scanState() {
      LOGGER.info("Scanning for legacy world dragon fight...");
      boolean var1 = this.hasExitPortal();
      if(var1) {
         LOGGER.info("Found that the dragon has been killed in this world already.");
         this.previouslyKilled = true;
      } else {
         LOGGER.info("Found that the dragon has not yet been killed in this world.");
         this.previouslyKilled = false;
         this.spawnExitPortal(false);
      }

      List<EnderDragon> var2 = this.level.getDragons();
      if(var2.isEmpty()) {
         this.dragonKilled = true;
      } else {
         EnderDragon var3 = (EnderDragon)var2.get(0);
         this.dragonUUID = var3.getUUID();
         LOGGER.info("Found that there\'s a dragon still alive ({})", var3);
         this.dragonKilled = false;
         if(!var1) {
            LOGGER.info("But we didn\'t have a portal, let\'s remove it.");
            var3.remove();
            this.dragonUUID = null;
         }
      }

      if(!this.previouslyKilled && this.dragonKilled) {
         this.dragonKilled = false;
      }

   }

   private void findOrCreateDragon() {
      List<EnderDragon> var1 = this.level.getDragons();
      if(var1.isEmpty()) {
         LOGGER.debug("Haven\'t seen the dragon, respawning it");
         this.createNewDragon();
      } else {
         LOGGER.debug("Haven\'t seen our dragon, but found another one to use.");
         this.dragonUUID = ((EnderDragon)var1.get(0)).getUUID();
      }

   }

   protected void setRespawnStage(DragonRespawnAnimation respawnStage) {
      if(this.respawnStage == null) {
         throw new IllegalStateException("Dragon respawn isn\'t in progress, can\'t skip ahead in the animation.");
      } else {
         this.respawnTime = 0;
         if(respawnStage == DragonRespawnAnimation.END) {
            this.respawnStage = null;
            this.dragonKilled = false;
            EnderDragon var2 = this.createNewDragon();

            for(ServerPlayer var4 : this.dragonEvent.getPlayers()) {
               CriteriaTriggers.SUMMONED_ENTITY.trigger(var4, var2);
            }
         } else {
            this.respawnStage = respawnStage;
         }

      }
   }

   private boolean hasExitPortal() {
      for(int var1 = -8; var1 <= 8; ++var1) {
         for(int var2 = -8; var2 <= 8; ++var2) {
            LevelChunk var3 = this.level.getChunk(var1, var2);

            for(BlockEntity var5 : var3.getBlockEntities().values()) {
               if(var5 instanceof TheEndPortalBlockEntity) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Nullable
   private BlockPattern.BlockPatternMatch findExitPortal() {
      for(int var1 = -8; var1 <= 8; ++var1) {
         for(int var2 = -8; var2 <= 8; ++var2) {
            LevelChunk var3 = this.level.getChunk(var1, var2);

            for(BlockEntity var5 : var3.getBlockEntities().values()) {
               if(var5 instanceof TheEndPortalBlockEntity) {
                  BlockPattern.BlockPatternMatch var6 = this.exitPortalPattern.find(this.level, var5.getBlockPos());
                  if(var6 != null) {
                     BlockPos var7 = var6.getBlock(3, 3, 3).getPos();
                     if(this.portalLocation == null && var7.getX() == 0 && var7.getZ() == 0) {
                        this.portalLocation = var7;
                     }

                     return var6;
                  }
               }
            }
         }
      }

      int var1 = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION).getY();

      for(int var2 = var1; var2 >= 0; --var2) {
         BlockPattern.BlockPatternMatch var3 = this.exitPortalPattern.find(this.level, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION.getX(), var2, EndPodiumFeature.END_PODIUM_LOCATION.getZ()));
         if(var3 != null) {
            if(this.portalLocation == null) {
               this.portalLocation = var3.getBlock(3, 3, 3).getPos();
            }

            return var3;
         }
      }

      return null;
   }

   private boolean isArenaLoaded() {
      for(int var1 = -8; var1 <= 8; ++var1) {
         for(int var2 = 8; var2 <= 8; ++var2) {
            ChunkAccess var3 = this.level.getChunk(var1, var2, ChunkStatus.FULL, false);
            if(!(var3 instanceof LevelChunk)) {
               return false;
            }

            ChunkHolder.FullChunkStatus var4 = ((LevelChunk)var3).getFullStatus();
            if(!var4.isOrAfter(ChunkHolder.FullChunkStatus.TICKING)) {
               return false;
            }
         }
      }

      return true;
   }

   private void updatePlayers() {
      Set<ServerPlayer> var1 = Sets.newHashSet();

      for(ServerPlayer var3 : this.level.getPlayers(VALID_PLAYER)) {
         this.dragonEvent.addPlayer(var3);
         var1.add(var3);
      }

      Set<ServerPlayer> var2 = Sets.newHashSet(this.dragonEvent.getPlayers());
      var2.removeAll(var1);

      for(ServerPlayer var4 : var2) {
         this.dragonEvent.removePlayer(var4);
      }

   }

   private void updateCrystalCount() {
      this.ticksSinceCrystalsScanned = 0;
      this.crystalsAlive = 0;

      for(SpikeFeature.EndSpike var2 : SpikeFeature.getSpikesForLevel(this.level)) {
         this.crystalsAlive += this.level.getEntitiesOfClass(EndCrystal.class, var2.getTopBoundingBox()).size();
      }

      LOGGER.debug("Found {} end crystals still alive", Integer.valueOf(this.crystalsAlive));
   }

   public void setDragonKilled(EnderDragon dragonKilled) {
      if(dragonKilled.getUUID().equals(this.dragonUUID)) {
         this.dragonEvent.setPercent(0.0F);
         this.dragonEvent.setVisible(false);
         this.spawnExitPortal(true);
         this.spawnNewGateway();
         if(!this.previouslyKilled) {
            this.level.setBlockAndUpdate(this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION), Blocks.DRAGON_EGG.defaultBlockState());
         }

         this.previouslyKilled = true;
         this.dragonKilled = true;
      }

   }

   private void spawnNewGateway() {
      if(!this.gateways.isEmpty()) {
         int var1 = ((Integer)this.gateways.remove(this.gateways.size() - 1)).intValue();
         int var2 = Mth.floor(96.0D * Math.cos(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double)var1)));
         int var3 = Mth.floor(96.0D * Math.sin(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double)var1)));
         this.spawnNewGateway(new BlockPos(var2, 75, var3));
      }
   }

   private void spawnNewGateway(BlockPos blockPos) {
      this.level.levelEvent(3000, blockPos, 0);
      Feature.END_GATEWAY.place(this.level, this.level.getChunkSource().getGenerator(), new Random(), blockPos, EndGatewayConfiguration.delayedExitSearch());
   }

   private void spawnExitPortal(boolean b) {
      EndPodiumFeature var2 = new EndPodiumFeature(b);
      if(this.portalLocation == null) {
         for(this.portalLocation = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION).below(); this.level.getBlockState(this.portalLocation).getBlock() == Blocks.BEDROCK && this.portalLocation.getY() > this.level.getSeaLevel(); this.portalLocation = this.portalLocation.below()) {
            ;
         }
      }

      var2.place(this.level, this.level.getChunkSource().getGenerator(), new Random(), this.portalLocation, (NoneFeatureConfiguration)FeatureConfiguration.NONE);
   }

   private EnderDragon createNewDragon() {
      this.level.getChunkAt(new BlockPos(0, 128, 0));
      EnderDragon enderDragon = (EnderDragon)EntityType.ENDER_DRAGON.create(this.level);
      enderDragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
      enderDragon.moveTo(0.0D, 128.0D, 0.0D, this.level.random.nextFloat() * 360.0F, 0.0F);
      this.level.addFreshEntity(enderDragon);
      this.dragonUUID = enderDragon.getUUID();
      return enderDragon;
   }

   public void updateDragon(EnderDragon enderDragon) {
      if(enderDragon.getUUID().equals(this.dragonUUID)) {
         this.dragonEvent.setPercent(enderDragon.getHealth() / enderDragon.getMaxHealth());
         this.ticksSinceDragonSeen = 0;
         if(enderDragon.hasCustomName()) {
            this.dragonEvent.setName(enderDragon.getDisplayName());
         }
      }

   }

   public int getCrystalsAlive() {
      return this.crystalsAlive;
   }

   public void onCrystalDestroyed(EndCrystal endCrystal, DamageSource damageSource) {
      if(this.respawnStage != null && this.respawnCrystals.contains(endCrystal)) {
         LOGGER.debug("Aborting respawn sequence");
         this.respawnStage = null;
         this.respawnTime = 0;
         this.resetSpikeCrystals();
         this.spawnExitPortal(true);
      } else {
         this.updateCrystalCount();
         Entity var3 = this.level.getEntity(this.dragonUUID);
         if(var3 instanceof EnderDragon) {
            ((EnderDragon)var3).onCrystalDestroyed(endCrystal, new BlockPos(endCrystal), damageSource);
         }
      }

   }

   public boolean hasPreviouslyKilledDragon() {
      return this.previouslyKilled;
   }

   public void tryRespawn() {
      if(this.dragonKilled && this.respawnStage == null) {
         BlockPos var1 = this.portalLocation;
         if(var1 == null) {
            LOGGER.debug("Tried to respawn, but need to find the portal first.");
            BlockPattern.BlockPatternMatch var2 = this.findExitPortal();
            if(var2 == null) {
               LOGGER.debug("Couldn\'t find a portal, so we made one.");
               this.spawnExitPortal(true);
            } else {
               LOGGER.debug("Found the exit portal & temporarily using it.");
            }

            var1 = this.portalLocation;
         }

         List<EndCrystal> var2 = Lists.newArrayList();
         BlockPos var3 = var1.above(1);

         for(Direction var5 : Direction.Plane.HORIZONTAL) {
            List<EndCrystal> var6 = this.level.getEntitiesOfClass(EndCrystal.class, new AABB(var3.relative(var5, 2)));
            if(var6.isEmpty()) {
               return;
            }

            var2.addAll(var6);
         }

         LOGGER.debug("Found all crystals, respawning dragon.");
         this.respawnDragon(var2);
      }

   }

   private void respawnDragon(List respawnCrystals) {
      if(this.dragonKilled && this.respawnStage == null) {
         for(BlockPattern.BlockPatternMatch var2 = this.findExitPortal(); var2 != null; var2 = this.findExitPortal()) {
            for(int var3 = 0; var3 < this.exitPortalPattern.getWidth(); ++var3) {
               for(int var4 = 0; var4 < this.exitPortalPattern.getHeight(); ++var4) {
                  for(int var5 = 0; var5 < this.exitPortalPattern.getDepth(); ++var5) {
                     BlockInWorld var6 = var2.getBlock(var3, var4, var5);
                     if(var6.getState().getBlock() == Blocks.BEDROCK || var6.getState().getBlock() == Blocks.END_PORTAL) {
                        this.level.setBlockAndUpdate(var6.getPos(), Blocks.END_STONE.defaultBlockState());
                     }
                  }
               }
            }
         }

         this.respawnStage = DragonRespawnAnimation.START;
         this.respawnTime = 0;
         this.spawnExitPortal(false);
         this.respawnCrystals = respawnCrystals;
      }

   }

   public void resetSpikeCrystals() {
      for(SpikeFeature.EndSpike var2 : SpikeFeature.getSpikesForLevel(this.level)) {
         for(EndCrystal var5 : this.level.getEntitiesOfClass(EndCrystal.class, var2.getTopBoundingBox())) {
            var5.setInvulnerable(false);
            var5.setBeamTarget((BlockPos)null);
         }
      }

   }
}
