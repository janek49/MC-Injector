package net.minecraft.world.entity.raid;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;

public class Raids extends SavedData {
   private final Map raidMap = Maps.newHashMap();
   private final ServerLevel level;
   private int nextAvailableID;
   private int tick;

   public Raids(ServerLevel level) {
      super(getFileId(level.dimension));
      this.level = level;
      this.nextAvailableID = 1;
      this.setDirty();
   }

   public Raid get(int i) {
      return (Raid)this.raidMap.get(Integer.valueOf(i));
   }

   public void tick() {
      ++this.tick;
      Iterator<Raid> var1 = this.raidMap.values().iterator();

      while(var1.hasNext()) {
         Raid var2 = (Raid)var1.next();
         if(this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
            var2.stop();
         }

         if(var2.isStopped()) {
            var1.remove();
            this.setDirty();
         } else {
            var2.tick();
         }
      }

      if(this.tick % 200 == 0) {
         this.setDirty();
      }

      DebugPackets.sendRaids(this.level, this.raidMap.values());
   }

   public static boolean canJoinRaid(Raider raider, Raid raid) {
      return raider != null && raid != null && raid.getLevel() != null?raider.isAlive() && raider.canJoinRaid() && raider.getNoActionTime() <= 2400 && raider.level.getDimension().getType() == raid.getLevel().getDimension().getType():false;
   }

   @Nullable
   public Raid createOrExtendRaid(ServerPlayer serverPlayer) {
      if(serverPlayer.isSpectator()) {
         return null;
      } else if(this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
         return null;
      } else {
         DimensionType var2 = serverPlayer.level.getDimension().getType();
         if(var2 == DimensionType.NETHER) {
            return null;
         } else {
            BlockPos var3 = new BlockPos(serverPlayer);
            List<PoiRecord> var5 = (List)this.level.getPoiManager().getInRange(PoiType.ALL, var3, 64, PoiManager.Occupancy.IS_OCCUPIED).collect(Collectors.toList());
            int var6 = 0;
            Vec3 var7 = new Vec3(0.0D, 0.0D, 0.0D);

            for(PoiRecord var9 : var5) {
               BlockPos var10 = var9.getPos();
               var7 = var7.add((double)var10.getX(), (double)var10.getY(), (double)var10.getZ());
               ++var6;
            }

            BlockPos var4;
            if(var6 > 0) {
               var7 = var7.scale(1.0D / (double)var6);
               var4 = new BlockPos(var7);
            } else {
               var4 = var3;
            }

            Raid var8 = this.getOrCreateRaid(serverPlayer.getLevel(), var4);
            boolean var9 = false;
            if(!var8.isStarted()) {
               if(!this.raidMap.containsKey(Integer.valueOf(var8.getId()))) {
                  this.raidMap.put(Integer.valueOf(var8.getId()), var8);
               }

               var9 = true;
            } else if(var8.getBadOmenLevel() < var8.getMaxBadOmenLevel()) {
               var9 = true;
            } else {
               serverPlayer.removeEffect(MobEffects.BAD_OMEN);
               serverPlayer.connection.send(new ClientboundEntityEventPacket(serverPlayer, (byte)43));
            }

            if(var9) {
               var8.absorbBadOmen(serverPlayer);
               serverPlayer.connection.send(new ClientboundEntityEventPacket(serverPlayer, (byte)43));
               if(!var8.hasFirstWaveSpawned()) {
                  serverPlayer.awardStat(Stats.RAID_TRIGGER);
                  CriteriaTriggers.BAD_OMEN.trigger(serverPlayer);
               }
            }

            this.setDirty();
            return var8;
         }
      }
   }

   private Raid getOrCreateRaid(ServerLevel serverLevel, BlockPos blockPos) {
      Raid raid = serverLevel.getRaidAt(blockPos);
      return raid != null?raid:new Raid(this.getUniqueId(), serverLevel, blockPos);
   }

   public void load(CompoundTag compoundTag) {
      this.nextAvailableID = compoundTag.getInt("NextAvailableID");
      this.tick = compoundTag.getInt("Tick");
      ListTag var2 = compoundTag.getList("Raids", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         CompoundTag var4 = var2.getCompound(var3);
         Raid var5 = new Raid(this.level, var4);
         this.raidMap.put(Integer.valueOf(var5.getId()), var5);
      }

   }

   public CompoundTag save(CompoundTag compoundTag) {
      compoundTag.putInt("NextAvailableID", this.nextAvailableID);
      compoundTag.putInt("Tick", this.tick);
      ListTag var2 = new ListTag();

      for(Raid var4 : this.raidMap.values()) {
         CompoundTag var5 = new CompoundTag();
         var4.save(var5);
         var2.add(var5);
      }

      compoundTag.put("Raids", var2);
      return compoundTag;
   }

   public static String getFileId(Dimension dimension) {
      return "raids" + dimension.getType().getFileSuffix();
   }

   private int getUniqueId() {
      return ++this.nextAvailableID;
   }

   @Nullable
   public Raid getNearbyRaid(BlockPos blockPos, int var2) {
      Raid raid = null;
      double var4 = (double)var2;

      for(Raid var7 : this.raidMap.values()) {
         double var8 = var7.getCenter().distSqr(blockPos);
         if(var7.isActive() && var8 < var4) {
            raid = var7;
            var4 = var8;
         }
      }

      return raid;
   }
}
