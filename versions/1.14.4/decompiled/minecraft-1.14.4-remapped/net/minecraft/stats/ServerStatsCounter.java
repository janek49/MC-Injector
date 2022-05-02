package net.minecraft.stats;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundAwardStatsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerStatsCounter extends StatsCounter {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   private final File file;
   private final Set dirty = Sets.newHashSet();
   private int lastStatRequest = -300;

   public ServerStatsCounter(MinecraftServer server, File file) {
      this.server = server;
      this.file = file;
      if(file.isFile()) {
         try {
            this.parseLocal(server.getFixerUpper(), FileUtils.readFileToString(file));
         } catch (IOException var4) {
            LOGGER.error("Couldn\'t read statistics file {}", file, var4);
         } catch (JsonParseException var5) {
            LOGGER.error("Couldn\'t parse statistics file {}", file, var5);
         }
      }

   }

   public void save() {
      try {
         FileUtils.writeStringToFile(this.file, this.toJson());
      } catch (IOException var2) {
         LOGGER.error("Couldn\'t save stats", var2);
      }

   }

   public void setValue(Player player, Stat stat, int var3) {
      super.setValue(player, stat, var3);
      this.dirty.add(stat);
   }

   private Set getDirty() {
      Set<Stat<?>> set = Sets.newHashSet(this.dirty);
      this.dirty.clear();
      return set;
   }

   public void parseLocal(DataFixer dataFixer, String string) {
      try {
         JsonReader var3 = new JsonReader(new StringReader(string));
         Throwable var4 = null;

         try {
            var3.setLenient(false);
            JsonElement var5 = Streams.parse(var3);
            if(!var5.isJsonNull()) {
               CompoundTag var6 = fromJson(var5.getAsJsonObject());
               if(!var6.contains("DataVersion", 99)) {
                  var6.putInt("DataVersion", 1343);
               }

               var6 = NbtUtils.update(dataFixer, DataFixTypes.STATS, var6, var6.getInt("DataVersion"));
               if(var6.contains("stats", 10)) {
                  CompoundTag var7 = var6.getCompound("stats");

                  for(String var9 : var7.getAllKeys()) {
                     if(var7.contains(var9, 10)) {
                        Util.ifElse(Registry.STAT_TYPE.getOptional(new ResourceLocation(var9)), (statType) -> {
                           CompoundTag compoundTag = var7.getCompound(var9);

                           for(String var6 : compoundTag.getAllKeys()) {
                              if(compoundTag.contains(var6, 99)) {
                                 Util.ifElse(this.getStat(statType, var6), (stat) -> {
                                    this.stats.put(stat, compoundTagx.getInt(var6));
                                 }, () -> {
                                    LOGGER.warn("Invalid statistic in {}: Don\'t know what {} is", this.file, var6);
                                 });
                              } else {
                                 LOGGER.warn("Invalid statistic value in {}: Don\'t know what {} is for key {}", this.file, compoundTag.get(var6), var6);
                              }
                           }

                        }, () -> {
                           LOGGER.warn("Invalid statistic type in {}: Don\'t know what {} is", this.file, var9);
                        });
                     }
                  }
               }

               return;
            }

            LOGGER.error("Unable to parse Stat data from {}", this.file);
         } catch (Throwable var19) {
            var4 = var19;
            throw var19;
         } finally {
            if(var3 != null) {
               if(var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var18) {
                     var4.addSuppressed(var18);
                  }
               } else {
                  var3.close();
               }
            }

         }

      } catch (IOException | JsonParseException var21) {
         LOGGER.error("Unable to parse Stat data from {}", this.file, var21);
      }
   }

   private Optional getStat(StatType statType, String string) {
      Optional var10000 = Optional.ofNullable(ResourceLocation.tryParse(string));
      Registry var10001 = statType.getRegistry();
      var10001.getClass();
      var10000 = var10000.flatMap(var10001::getOptional);
      statType.getClass();
      return var10000.map(statType::get);
   }

   private static CompoundTag fromJson(JsonObject json) {
      CompoundTag compoundTag = new CompoundTag();

      for(Entry<String, JsonElement> var3 : json.entrySet()) {
         JsonElement var4 = (JsonElement)var3.getValue();
         if(var4.isJsonObject()) {
            compoundTag.put((String)var3.getKey(), fromJson(var4.getAsJsonObject()));
         } else if(var4.isJsonPrimitive()) {
            JsonPrimitive var5 = var4.getAsJsonPrimitive();
            if(var5.isNumber()) {
               compoundTag.putInt((String)var3.getKey(), var5.getAsInt());
            }
         }
      }

      return compoundTag;
   }

   protected String toJson() {
      Map<StatType<?>, JsonObject> var1 = Maps.newHashMap();
      ObjectIterator var2 = this.stats.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<Stat<?>> var3 = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry)var2.next();
         Stat<?> var4 = (Stat)var3.getKey();
         ((JsonObject)var1.computeIfAbsent(var4.getType(), (statType) -> {
            return new JsonObject();
         })).addProperty(getKey(var4).toString(), Integer.valueOf(var3.getIntValue()));
      }

      JsonObject var2 = new JsonObject();

      for(Entry<StatType<?>, JsonObject> var4 : var1.entrySet()) {
         var2.add(Registry.STAT_TYPE.getKey(var4.getKey()).toString(), (JsonElement)var4.getValue());
      }

      JsonObject var3 = new JsonObject();
      var3.add("stats", var2);
      var3.addProperty("DataVersion", Integer.valueOf(SharedConstants.getCurrentVersion().getWorldVersion()));
      return var3.toString();
   }

   private static ResourceLocation getKey(Stat stat) {
      return stat.getType().getRegistry().getKey(stat.getValue());
   }

   public void markAllDirty() {
      this.dirty.addAll(this.stats.keySet());
   }

   public void sendStats(ServerPlayer serverPlayer) {
      int var2 = this.server.getTickCount();
      Object2IntMap<Stat<?>> var3 = new Object2IntOpenHashMap();
      if(var2 - this.lastStatRequest > 300) {
         this.lastStatRequest = var2;

         for(Stat<?> var5 : this.getDirty()) {
            var3.put(var5, this.getValue(var5));
         }
      }

      serverPlayer.connection.send(new ClientboundAwardStatsPacket(var3));
   }
}
