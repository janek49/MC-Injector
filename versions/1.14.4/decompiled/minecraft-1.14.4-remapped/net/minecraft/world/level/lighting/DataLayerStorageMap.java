package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.DataLayer;

public abstract class DataLayerStorageMap {
   private final long[] lastSectionKeys = new long[2];
   private final DataLayer[] lastSections = new DataLayer[2];
   private boolean cacheEnabled;
   protected final Long2ObjectOpenHashMap map;

   protected DataLayerStorageMap(Long2ObjectOpenHashMap map) {
      this.map = map;
      this.clearCache();
      this.cacheEnabled = true;
   }

   public abstract DataLayerStorageMap copy();

   public void copyDataLayer(long l) {
      this.map.put(l, ((DataLayer)this.map.get(l)).copy());
      this.clearCache();
   }

   public boolean hasLayer(long l) {
      return this.map.containsKey(l);
   }

   @Nullable
   public DataLayer getLayer(long l) {
      if(this.cacheEnabled) {
         for(int var3 = 0; var3 < 2; ++var3) {
            if(l == this.lastSectionKeys[var3]) {
               return this.lastSections[var3];
            }
         }
      }

      DataLayer dataLayer = (DataLayer)this.map.get(l);
      if(dataLayer == null) {
         return null;
      } else {
         if(this.cacheEnabled) {
            for(int var4 = 1; var4 > 0; --var4) {
               this.lastSectionKeys[var4] = this.lastSectionKeys[var4 - 1];
               this.lastSections[var4] = this.lastSections[var4 - 1];
            }

            this.lastSectionKeys[0] = l;
            this.lastSections[0] = dataLayer;
         }

         return dataLayer;
      }
   }

   @Nullable
   public DataLayer removeLayer(long l) {
      return (DataLayer)this.map.remove(l);
   }

   public void setLayer(long var1, DataLayer dataLayer) {
      this.map.put(var1, dataLayer);
   }

   public void clearCache() {
      for(int var1 = 0; var1 < 2; ++var1) {
         this.lastSectionKeys[var1] = Long.MAX_VALUE;
         this.lastSections[var1] = null;
      }

   }

   public void disableCache() {
      this.cacheEnabled = false;
   }
}
