package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.SectionTracker;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightEngine;

public abstract class LayerLightSectionStorage extends SectionTracker {
   protected static final DataLayer EMPTY_DATA = new DataLayer();
   private static final Direction[] DIRECTIONS = Direction.values();
   private final LightLayer layer;
   private final LightChunkGetter chunkSource;
   protected final LongSet dataSectionSet = new LongOpenHashSet();
   protected final LongSet toMarkNoData = new LongOpenHashSet();
   protected final LongSet toMarkData = new LongOpenHashSet();
   protected volatile DataLayerStorageMap visibleSectionData;
   protected final DataLayerStorageMap updatingSectionData;
   protected final LongSet changedSections = new LongOpenHashSet();
   protected final LongSet sectionsAffectedByLightUpdates = new LongOpenHashSet();
   protected final Long2ObjectMap queuedSections = new Long2ObjectOpenHashMap();
   private final LongSet columnsToRetainQueuedDataFor = new LongOpenHashSet();
   private final LongSet toRemove = new LongOpenHashSet();
   protected volatile boolean hasToRemove;

   protected LayerLightSectionStorage(LightLayer layer, LightChunkGetter chunkSource, DataLayerStorageMap updatingSectionData) {
      super(3, 16, 256);
      this.layer = layer;
      this.chunkSource = chunkSource;
      this.updatingSectionData = updatingSectionData;
      this.visibleSectionData = updatingSectionData.copy();
      this.visibleSectionData.disableCache();
   }

   protected boolean storingLightForSection(long l) {
      return this.getDataLayer(l, true) != null;
   }

   @Nullable
   protected DataLayer getDataLayer(long var1, boolean var3) {
      return this.getDataLayer(var3?this.updatingSectionData:this.visibleSectionData, var1);
   }

   @Nullable
   protected DataLayer getDataLayer(DataLayerStorageMap dataLayerStorageMap, long var2) {
      return dataLayerStorageMap.getLayer(var2);
   }

   @Nullable
   public DataLayer getDataLayerData(long l) {
      DataLayer dataLayer = (DataLayer)this.queuedSections.get(l);
      return dataLayer != null?dataLayer:this.getDataLayer(l, false);
   }

   protected abstract int getLightValue(long var1);

   protected int getStoredLevel(long l) {
      long var3 = SectionPos.blockToSection(l);
      DataLayer var5 = this.getDataLayer(var3, true);
      return var5.get(SectionPos.sectionRelative(BlockPos.getX(l)), SectionPos.sectionRelative(BlockPos.getY(l)), SectionPos.sectionRelative(BlockPos.getZ(l)));
   }

   protected void setStoredLevel(long var1, int var3) {
      long var4 = SectionPos.blockToSection(var1);
      if(this.changedSections.add(var4)) {
         this.updatingSectionData.copyDataLayer(var4);
      }

      DataLayer var6 = this.getDataLayer(var4, true);
      var6.set(SectionPos.sectionRelative(BlockPos.getX(var1)), SectionPos.sectionRelative(BlockPos.getY(var1)), SectionPos.sectionRelative(BlockPos.getZ(var1)), var3);

      for(int var7 = -1; var7 <= 1; ++var7) {
         for(int var8 = -1; var8 <= 1; ++var8) {
            for(int var9 = -1; var9 <= 1; ++var9) {
               this.sectionsAffectedByLightUpdates.add(SectionPos.blockToSection(BlockPos.offset(var1, var8, var9, var7)));
            }
         }
      }

   }

   protected int getLevel(long l) {
      return l == Long.MAX_VALUE?2:(this.dataSectionSet.contains(l)?0:(!this.toRemove.contains(l) && this.updatingSectionData.hasLayer(l)?1:2));
   }

   protected int getLevelFromSource(long l) {
      return this.toMarkNoData.contains(l)?2:(!this.dataSectionSet.contains(l) && !this.toMarkData.contains(l)?2:0);
   }

   protected void setLevel(long var1, int var3) {
      int var4 = this.getLevel(var1);
      if(var4 != 0 && var3 == 0) {
         this.dataSectionSet.add(var1);
         this.toMarkData.remove(var1);
      }

      if(var4 == 0 && var3 != 0) {
         this.dataSectionSet.remove(var1);
         this.toMarkNoData.remove(var1);
      }

      if(var4 >= 2 && var3 != 2) {
         if(this.toRemove.contains(var1)) {
            this.toRemove.remove(var1);
         } else {
            this.updatingSectionData.setLayer(var1, this.createDataLayer(var1));
            this.changedSections.add(var1);
            this.onNodeAdded(var1);

            for(int var5 = -1; var5 <= 1; ++var5) {
               for(int var6 = -1; var6 <= 1; ++var6) {
                  for(int var7 = -1; var7 <= 1; ++var7) {
                     this.sectionsAffectedByLightUpdates.add(SectionPos.blockToSection(BlockPos.offset(var1, var6, var7, var5)));
                  }
               }
            }
         }
      }

      if(var4 != 2 && var3 >= 2) {
         this.toRemove.add(var1);
      }

      this.hasToRemove = !this.toRemove.isEmpty();
   }

   protected DataLayer createDataLayer(long l) {
      DataLayer dataLayer = (DataLayer)this.queuedSections.get(l);
      return dataLayer != null?dataLayer:new DataLayer();
   }

   protected void clearQueuedSectionBlocks(LayerLightEngine layerLightEngine, long var2) {
      int var4 = SectionPos.sectionToBlockCoord(SectionPos.x(var2));
      int var5 = SectionPos.sectionToBlockCoord(SectionPos.y(var2));
      int var6 = SectionPos.sectionToBlockCoord(SectionPos.z(var2));

      for(int var7 = 0; var7 < 16; ++var7) {
         for(int var8 = 0; var8 < 16; ++var8) {
            for(int var9 = 0; var9 < 16; ++var9) {
               long var10 = BlockPos.asLong(var4 + var7, var5 + var8, var6 + var9);
               layerLightEngine.removeFromQueue(var10);
            }
         }
      }

   }

   protected boolean hasInconsistencies() {
      return this.hasToRemove;
   }

   protected void markNewInconsistencies(LayerLightEngine layerLightEngine, boolean var2, boolean var3) {
      if(this.hasInconsistencies() || !this.queuedSections.isEmpty()) {
         LongIterator var4 = this.toRemove.iterator();

         while(var4.hasNext()) {
            long var5 = ((Long)var4.next()).longValue();
            this.clearQueuedSectionBlocks(layerLightEngine, var5);
            DataLayer var7 = (DataLayer)this.queuedSections.remove(var5);
            DataLayer var8 = this.updatingSectionData.removeLayer(var5);
            if(this.columnsToRetainQueuedDataFor.contains(SectionPos.getZeroNode(var5))) {
               if(var7 != null) {
                  this.queuedSections.put(var5, var7);
               } else if(var8 != null) {
                  this.queuedSections.put(var5, var8);
               }
            }
         }

         this.updatingSectionData.clearCache();
         var4 = this.toRemove.iterator();

         while(var4.hasNext()) {
            long var5 = ((Long)var4.next()).longValue();
            this.onNodeRemoved(var5);
         }

         this.toRemove.clear();
         this.hasToRemove = false;
         ObjectIterator var23 = this.queuedSections.long2ObjectEntrySet().iterator();

         while(var23.hasNext()) {
            Entry<DataLayer> var5 = (Entry)var23.next();
            long var6 = var5.getLongKey();
            if(this.storingLightForSection(var6)) {
               DataLayer var8 = (DataLayer)var5.getValue();
               if(this.updatingSectionData.getLayer(var6) != var8) {
                  this.clearQueuedSectionBlocks(layerLightEngine, var6);
                  this.updatingSectionData.setLayer(var6, var8);
                  this.changedSections.add(var6);
               }
            }
         }

         this.updatingSectionData.clearCache();
         if(!var3) {
            LongIterator var24 = this.queuedSections.keySet().iterator();

            while(var24.hasNext()) {
               long var5 = ((Long)var24.next()).longValue();
               if(this.storingLightForSection(var5)) {
                  int var7 = SectionPos.sectionToBlockCoord(SectionPos.x(var5));
                  int var8 = SectionPos.sectionToBlockCoord(SectionPos.y(var5));
                  int var9 = SectionPos.sectionToBlockCoord(SectionPos.z(var5));

                  for(Direction var13 : DIRECTIONS) {
                     long var14 = SectionPos.offset(var5, var13);
                     if(!this.queuedSections.containsKey(var14) && this.storingLightForSection(var14)) {
                        for(int var16 = 0; var16 < 16; ++var16) {
                           for(int var17 = 0; var17 < 16; ++var17) {
                              long var18;
                              long var20;
                              switch(var13) {
                              case DOWN:
                                 var18 = BlockPos.asLong(var7 + var17, var8, var9 + var16);
                                 var20 = BlockPos.asLong(var7 + var17, var8 - 1, var9 + var16);
                                 break;
                              case UP:
                                 var18 = BlockPos.asLong(var7 + var17, var8 + 16 - 1, var9 + var16);
                                 var20 = BlockPos.asLong(var7 + var17, var8 + 16, var9 + var16);
                                 break;
                              case NORTH:
                                 var18 = BlockPos.asLong(var7 + var16, var8 + var17, var9);
                                 var20 = BlockPos.asLong(var7 + var16, var8 + var17, var9 - 1);
                                 break;
                              case SOUTH:
                                 var18 = BlockPos.asLong(var7 + var16, var8 + var17, var9 + 16 - 1);
                                 var20 = BlockPos.asLong(var7 + var16, var8 + var17, var9 + 16);
                                 break;
                              case WEST:
                                 var18 = BlockPos.asLong(var7, var8 + var16, var9 + var17);
                                 var20 = BlockPos.asLong(var7 - 1, var8 + var16, var9 + var17);
                                 break;
                              default:
                                 var18 = BlockPos.asLong(var7 + 16 - 1, var8 + var16, var9 + var17);
                                 var20 = BlockPos.asLong(var7 + 16, var8 + var16, var9 + var17);
                              }

                              layerLightEngine.checkEdge(var18, var20, layerLightEngine.computeLevelFromNeighbor(var18, var20, layerLightEngine.getLevel(var18)), false);
                              layerLightEngine.checkEdge(var20, var18, layerLightEngine.computeLevelFromNeighbor(var20, var18, layerLightEngine.getLevel(var20)), false);
                           }
                        }
                     }
                  }
               }
            }
         }

         var4 = this.queuedSections.long2ObjectEntrySet().iterator();

         while(var23.hasNext()) {
            Entry<DataLayer> var5 = (Entry)var23.next();
            long var6 = var5.getLongKey();
            if(this.storingLightForSection(var6)) {
               var23.remove();
            }
         }

      }
   }

   protected void onNodeAdded(long l) {
   }

   protected void onNodeRemoved(long l) {
   }

   protected void enableLightSources(long var1, boolean var3) {
   }

   public void retainData(long var1, boolean var3) {
      if(var3) {
         this.columnsToRetainQueuedDataFor.add(var1);
      } else {
         this.columnsToRetainQueuedDataFor.remove(var1);
      }

   }

   protected void queueSectionData(long var1, @Nullable DataLayer dataLayer) {
      if(dataLayer != null) {
         this.queuedSections.put(var1, dataLayer);
      } else {
         this.queuedSections.remove(var1);
      }

   }

   protected void updateSectionStatus(long var1, boolean var3) {
      boolean var4 = this.dataSectionSet.contains(var1);
      if(!var4 && !var3) {
         this.toMarkData.add(var1);
         this.checkEdge(Long.MAX_VALUE, var1, 0, true);
      }

      if(var4 && var3) {
         this.toMarkNoData.add(var1);
         this.checkEdge(Long.MAX_VALUE, var1, 2, false);
      }

   }

   protected void runAllUpdates() {
      if(this.hasWork()) {
         this.runUpdates(Integer.MAX_VALUE);
      }

   }

   protected void swapSectionMap() {
      if(!this.changedSections.isEmpty()) {
         M var1 = this.updatingSectionData.copy();
         var1.disableCache();
         this.visibleSectionData = var1;
         this.changedSections.clear();
      }

      if(!this.sectionsAffectedByLightUpdates.isEmpty()) {
         LongIterator var1 = this.sectionsAffectedByLightUpdates.iterator();

         while(var1.hasNext()) {
            long var2 = var1.nextLong();
            this.chunkSource.onLightUpdate(this.layer, SectionPos.of(var2));
         }

         this.sectionsAffectedByLightUpdates.clear();
      }

   }
}
