package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Serializable;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SectionStorage extends RegionFileStorage {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Long2ObjectMap storage = new Long2ObjectOpenHashMap();
   private final LongLinkedOpenHashSet dirty = new LongLinkedOpenHashSet();
   private final BiFunction deserializer;
   private final Function factory;
   private final DataFixer fixerUpper;
   private final DataFixTypes type;

   public SectionStorage(File file, BiFunction deserializer, Function factory, DataFixer fixerUpper, DataFixTypes type) {
      super(file);
      this.deserializer = deserializer;
      this.factory = factory;
      this.fixerUpper = fixerUpper;
      this.type = type;
   }

   protected void tick(BooleanSupplier booleanSupplier) {
      while(!this.dirty.isEmpty() && booleanSupplier.getAsBoolean()) {
         ChunkPos var2 = SectionPos.of(this.dirty.firstLong()).chunk();
         this.writeColumn(var2);
      }

   }

   @Nullable
   protected Optional get(long l) {
      return (Optional)this.storage.get(l);
   }

   protected Optional getOrLoad(long l) {
      SectionPos var3 = SectionPos.of(l);
      if(this.outsideStoredRange(var3)) {
         return Optional.empty();
      } else {
         Optional<R> var4 = this.get(l);
         if(var4 != null) {
            return var4;
         } else {
            this.readColumn(var3.chunk());
            var4 = this.get(l);
            if(var4 == null) {
               throw new IllegalStateException();
            } else {
               return var4;
            }
         }
      }
   }

   protected boolean outsideStoredRange(SectionPos sectionPos) {
      return Level.isOutsideBuildHeight(SectionPos.sectionToBlockCoord(sectionPos.y()));
   }

   protected Serializable getOrCreate(long l) {
      Optional<R> var3 = this.getOrLoad(l);
      if(var3.isPresent()) {
         return (Serializable)var3.get();
      } else {
         R var4 = (Serializable)this.factory.apply(() -> {
            this.setDirty(l);
         });
         this.storage.put(l, Optional.of(var4));
         return var4;
      }
   }

   private void readColumn(ChunkPos chunkPos) {
      this.readColumn(chunkPos, NbtOps.INSTANCE, this.tryRead(chunkPos));
   }

   @Nullable
   private CompoundTag tryRead(ChunkPos chunkPos) {
      try {
         return this.read(chunkPos);
      } catch (IOException var3) {
         LOGGER.error("Error reading chunk {} data from disk", chunkPos, var3);
         return null;
      }
   }

   private void readColumn(ChunkPos chunkPos, DynamicOps dynamicOps, @Nullable Object object) {
      if(object == null) {
         for(int var4 = 0; var4 < 16; ++var4) {
            this.storage.put(SectionPos.of(chunkPos, var4).asLong(), Optional.empty());
         }
      } else {
         Dynamic<T> var4 = new Dynamic(dynamicOps, object);
         int var5 = getVersion(var4);
         int var6 = SharedConstants.getCurrentVersion().getWorldVersion();
         boolean var7 = var5 != var6;
         Dynamic<T> var8 = this.fixerUpper.update(this.type.getType(), var4, var5, var6);
         OptionalDynamic<T> var9 = var8.get("Sections");

         for(int var10 = 0; var10 < 16; ++var10) {
            long var11 = SectionPos.of(chunkPos, var10).asLong();
            Optional<R> var13 = var9.get(Integer.toString(var10)).get().map((dynamic) -> {
               return (Serializable)this.deserializer.apply(() -> {
                  this.setDirty(var11);
               }, dynamic);
            });
            this.storage.put(var11, var13);
            var13.ifPresent((serializable) -> {
               this.onSectionLoad(var11);
               if(var7) {
                  this.setDirty(var11);
               }

            });
         }
      }

   }

   private void writeColumn(ChunkPos chunkPos) {
      Dynamic<Tag> var2 = this.writeColumn(chunkPos, NbtOps.INSTANCE);
      Tag var3 = (Tag)var2.getValue();
      if(var3 instanceof CompoundTag) {
         try {
            this.write(chunkPos, (CompoundTag)var3);
         } catch (IOException var5) {
            LOGGER.error("Error writing data to disk", var5);
         }
      } else {
         LOGGER.error("Expected compound tag, got {}", var3);
      }

   }

   private Dynamic writeColumn(ChunkPos chunkPos, DynamicOps dynamicOps) {
      Map<T, T> var3 = Maps.newHashMap();

      for(int var4 = 0; var4 < 16; ++var4) {
         long var5 = SectionPos.of(chunkPos, var4).asLong();
         this.dirty.remove(var5);
         Optional<R> var7 = (Optional)this.storage.get(var5);
         if(var7 != null && var7.isPresent()) {
            var3.put(dynamicOps.createString(Integer.toString(var4)), ((Serializable)var7.get()).serialize(dynamicOps));
         }
      }

      return new Dynamic(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("Sections"), dynamicOps.createMap(var3), dynamicOps.createString("DataVersion"), dynamicOps.createInt(SharedConstants.getCurrentVersion().getWorldVersion()))));
   }

   protected void onSectionLoad(long l) {
   }

   protected void setDirty(long dirty) {
      Optional<R> var3 = (Optional)this.storage.get(dirty);
      if(var3 != null && var3.isPresent()) {
         this.dirty.add(dirty);
      } else {
         LOGGER.warn("No data for position: {}", SectionPos.of(dirty));
      }
   }

   private static int getVersion(Dynamic dynamic) {
      return ((Number)dynamic.get("DataVersion").asNumber().orElse(Integer.valueOf(1945))).intValue();
   }

   public void flush(ChunkPos chunkPos) {
      if(!this.dirty.isEmpty()) {
         for(int var2 = 0; var2 < 16; ++var2) {
            long var3 = SectionPos.of(chunkPos, var2).asLong();
            if(this.dirty.contains(var3)) {
               this.writeColumn(chunkPos);
               return;
            }
         }
      }

   }
}
