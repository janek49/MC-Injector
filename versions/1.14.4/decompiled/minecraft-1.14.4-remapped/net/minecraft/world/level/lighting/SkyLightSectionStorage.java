package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.FlatDataLayer;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;

public class SkyLightSectionStorage extends LayerLightSectionStorage {
   private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   private final LongSet sectionsWithSources = new LongOpenHashSet();
   private final LongSet sectionsToAddSourcesTo = new LongOpenHashSet();
   private final LongSet sectionsToRemoveSourcesFrom = new LongOpenHashSet();
   private final LongSet columnsWithSkySources = new LongOpenHashSet();
   private volatile boolean hasSourceInconsistencies;

   protected SkyLightSectionStorage(LightChunkGetter lightChunkGetter) {
      super(LightLayer.SKY, lightChunkGetter, new SkyLightSectionStorage.SkyDataLayerStorageMap(new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
   }

   protected int getLightValue(long l) {
      long var3 = SectionPos.blockToSection(l);
      int var5 = SectionPos.y(var3);
      SkyLightSectionStorage.SkyDataLayerStorageMap var6 = (SkyLightSectionStorage.SkyDataLayerStorageMap)this.visibleSectionData;
      int var7 = var6.topSections.get(SectionPos.getZeroNode(var3));
      if(var7 != var6.currentLowestY && var5 < var7) {
         DataLayer var8 = this.getDataLayer(var6, var3);
         if(var8 == null) {
            for(l = BlockPos.getFlatIndex(l); var8 == null; var8 = this.getDataLayer(var6, var3)) {
               var3 = SectionPos.offset(var3, Direction.UP);
               ++var5;
               if(var5 >= var7) {
                  return 15;
               }

               l = BlockPos.offset(l, 0, 16, 0);
            }
         }

         return var8.get(SectionPos.sectionRelative(BlockPos.getX(l)), SectionPos.sectionRelative(BlockPos.getY(l)), SectionPos.sectionRelative(BlockPos.getZ(l)));
      } else {
         return 15;
      }
   }

   protected void onNodeAdded(long l) {
      int var3 = SectionPos.y(l);
      if(((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY > var3) {
         ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY = var3;
         ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.defaultReturnValue(((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY);
      }

      long var4 = SectionPos.getZeroNode(l);
      int var6 = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(var4);
      if(var6 < var3 + 1) {
         ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.put(var4, var3 + 1);
         if(this.columnsWithSkySources.contains(var4)) {
            this.queueAddSource(l);
            if(var6 > ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY) {
               long var7 = SectionPos.asLong(SectionPos.x(l), var6 - 1, SectionPos.z(l));
               this.queueRemoveSource(var7);
            }

            this.recheckInconsistencyFlag();
         }
      }

   }

   private void queueRemoveSource(long l) {
      this.sectionsToRemoveSourcesFrom.add(l);
      this.sectionsToAddSourcesTo.remove(l);
   }

   private void queueAddSource(long l) {
      this.sectionsToAddSourcesTo.add(l);
      this.sectionsToRemoveSourcesFrom.remove(l);
   }

   private void recheckInconsistencyFlag() {
      this.hasSourceInconsistencies = !this.sectionsToAddSourcesTo.isEmpty() || !this.sectionsToRemoveSourcesFrom.isEmpty();
   }

   protected void onNodeRemoved(long l) {
      long var3 = SectionPos.getZeroNode(l);
      boolean var5 = this.columnsWithSkySources.contains(var3);
      if(var5) {
         this.queueRemoveSource(l);
      }

      int var6 = SectionPos.y(l);
      if(((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(var3) == var6 + 1) {
         long var7;
         for(var7 = l; !this.storingLightForSection(var7) && this.hasSectionsBelow(var6); var7 = SectionPos.offset(var7, Direction.DOWN)) {
            --var6;
         }

         if(this.storingLightForSection(var7)) {
            ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.put(var3, var6 + 1);
            if(var5) {
               this.queueAddSource(var7);
            }
         } else {
            ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.remove(var3);
         }
      }

      if(var5) {
         this.recheckInconsistencyFlag();
      }

   }

   protected void enableLightSources(long var1, boolean var3) {
      if(var3 && this.columnsWithSkySources.add(var1)) {
         int var4 = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(var1);
         if(var4 != ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY) {
            long var5 = SectionPos.asLong(SectionPos.x(var1), var4 - 1, SectionPos.z(var1));
            this.queueAddSource(var5);
            this.recheckInconsistencyFlag();
         }
      } else if(!var3) {
         this.columnsWithSkySources.remove(var1);
      }

   }

   protected boolean hasInconsistencies() {
      return super.hasInconsistencies() || this.hasSourceInconsistencies;
   }

   protected DataLayer createDataLayer(long l) {
      DataLayer dataLayer = (DataLayer)this.queuedSections.get(l);
      if(dataLayer != null) {
         return dataLayer;
      } else {
         long var4 = SectionPos.offset(l, Direction.UP);
         int var6 = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(SectionPos.getZeroNode(l));
         if(var6 != ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY && SectionPos.y(var4) < var6) {
            DataLayer var7;
            while((var7 = this.getDataLayer(var4, true)) == null) {
               var4 = SectionPos.offset(var4, Direction.UP);
            }

            return new DataLayer((new FlatDataLayer(var7, 0)).getData());
         } else {
            return new DataLayer();
         }
      }
   }

   protected void markNewInconsistencies(LayerLightEngine layerLightEngine, boolean var2, boolean var3) {
      super.markNewInconsistencies(layerLightEngine, var2, var3);
      if(var2) {
         if(!this.sectionsToAddSourcesTo.isEmpty()) {
            LongIterator var4 = this.sectionsToAddSourcesTo.iterator();

            while(var4.hasNext()) {
               long var5 = ((Long)var4.next()).longValue();
               int var7 = this.getLevel(var5);
               if(var7 != 2 && !this.sectionsToRemoveSourcesFrom.contains(var5) && this.sectionsWithSources.add(var5)) {
                  if(var7 == 1) {
                     this.clearQueuedSectionBlocks(layerLightEngine, var5);
                     if(this.changedSections.add(var5)) {
                        ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).copyDataLayer(var5);
                     }

                     Arrays.fill(this.getDataLayer(var5, true).getData(), (byte)-1);
                     int var8 = SectionPos.sectionToBlockCoord(SectionPos.x(var5));
                     int var9 = SectionPos.sectionToBlockCoord(SectionPos.y(var5));
                     int var10 = SectionPos.sectionToBlockCoord(SectionPos.z(var5));

                     for(Direction var14 : HORIZONTALS) {
                        long var15 = SectionPos.offset(var5, var14);
                        if((this.sectionsToRemoveSourcesFrom.contains(var15) || !this.sectionsWithSources.contains(var15) && !this.sectionsToAddSourcesTo.contains(var15)) && this.storingLightForSection(var15)) {
                           for(int var17 = 0; var17 < 16; ++var17) {
                              for(int var18 = 0; var18 < 16; ++var18) {
                                 long var19;
                                 long var21;
                                 switch(var14) {
                                 case NORTH:
                                    var19 = BlockPos.asLong(var8 + var17, var9 + var18, var10);
                                    var21 = BlockPos.asLong(var8 + var17, var9 + var18, var10 - 1);
                                    break;
                                 case SOUTH:
                                    var19 = BlockPos.asLong(var8 + var17, var9 + var18, var10 + 16 - 1);
                                    var21 = BlockPos.asLong(var8 + var17, var9 + var18, var10 + 16);
                                    break;
                                 case WEST:
                                    var19 = BlockPos.asLong(var8, var9 + var17, var10 + var18);
                                    var21 = BlockPos.asLong(var8 - 1, var9 + var17, var10 + var18);
                                    break;
                                 default:
                                    var19 = BlockPos.asLong(var8 + 16 - 1, var9 + var17, var10 + var18);
                                    var21 = BlockPos.asLong(var8 + 16, var9 + var17, var10 + var18);
                                 }

                                 layerLightEngine.checkEdge(var19, var21, layerLightEngine.computeLevelFromNeighbor(var19, var21, 0), true);
                              }
                           }
                        }
                     }

                     for(int var11 = 0; var11 < 16; ++var11) {
                        for(int var12 = 0; var12 < 16; ++var12) {
                           long var13 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(var5)) + var11, SectionPos.sectionToBlockCoord(SectionPos.y(var5)), SectionPos.sectionToBlockCoord(SectionPos.z(var5)) + var12);
                           long var15 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(var5)) + var11, SectionPos.sectionToBlockCoord(SectionPos.y(var5)) - 1, SectionPos.sectionToBlockCoord(SectionPos.z(var5)) + var12);
                           layerLightEngine.checkEdge(var13, var15, layerLightEngine.computeLevelFromNeighbor(var13, var15, 0), true);
                        }
                     }
                  } else {
                     for(int var8 = 0; var8 < 16; ++var8) {
                        for(int var9 = 0; var9 < 16; ++var9) {
                           long var10 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(var5)) + var8, SectionPos.sectionToBlockCoord(SectionPos.y(var5)) + 16 - 1, SectionPos.sectionToBlockCoord(SectionPos.z(var5)) + var9);
                           layerLightEngine.checkEdge(Long.MAX_VALUE, var10, 0, true);
                        }
                     }
                  }
               }
            }
         }

         this.sectionsToAddSourcesTo.clear();
         if(!this.sectionsToRemoveSourcesFrom.isEmpty()) {
            LongIterator var23 = this.sectionsToRemoveSourcesFrom.iterator();

            while(var23.hasNext()) {
               long var5 = ((Long)var23.next()).longValue();
               if(this.sectionsWithSources.remove(var5) && this.storingLightForSection(var5)) {
                  for(int var7 = 0; var7 < 16; ++var7) {
                     for(int var8 = 0; var8 < 16; ++var8) {
                        long var9 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x(var5)) + var7, SectionPos.sectionToBlockCoord(SectionPos.y(var5)) + 16 - 1, SectionPos.sectionToBlockCoord(SectionPos.z(var5)) + var8);
                        layerLightEngine.checkEdge(Long.MAX_VALUE, var9, 15, false);
                     }
                  }
               }
            }
         }

         this.sectionsToRemoveSourcesFrom.clear();
         this.hasSourceInconsistencies = false;
      }
   }

   protected boolean hasSectionsBelow(int i) {
      return i >= ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY;
   }

   protected boolean hasLightSource(long l) {
      int var3 = BlockPos.getY(l);
      if((var3 & 15) != 15) {
         return false;
      } else {
         long var4 = SectionPos.blockToSection(l);
         long var6 = SectionPos.getZeroNode(var4);
         if(!this.columnsWithSkySources.contains(var6)) {
            return false;
         } else {
            int var8 = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(var6);
            return SectionPos.sectionToBlockCoord(var8) == var3 + 16;
         }
      }
   }

   protected boolean isAboveData(long l) {
      long var3 = SectionPos.getZeroNode(l);
      int var5 = ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(var3);
      return var5 == ((SkyLightSectionStorage.SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY || SectionPos.y(l) >= var5;
   }

   protected boolean lightOnInSection(long l) {
      long var3 = SectionPos.getZeroNode(l);
      return this.columnsWithSkySources.contains(var3);
   }

   public static final class SkyDataLayerStorageMap extends DataLayerStorageMap {
      private int currentLowestY;
      private final Long2IntOpenHashMap topSections;

      public SkyDataLayerStorageMap(Long2ObjectOpenHashMap long2ObjectOpenHashMap, Long2IntOpenHashMap topSections, int currentLowestY) {
         super(long2ObjectOpenHashMap);
         this.topSections = topSections;
         topSections.defaultReturnValue(currentLowestY);
         this.currentLowestY = currentLowestY;
      }

      public SkyLightSectionStorage.SkyDataLayerStorageMap copy() {
         return new SkyLightSectionStorage.SkyDataLayerStorageMap(this.map.clone(), this.topSections.clone(), this.currentLowestY);
      }

      // $FF: synthetic method
      public DataLayerStorageMap copy() {
         return this.copy();
      }
   }
}
