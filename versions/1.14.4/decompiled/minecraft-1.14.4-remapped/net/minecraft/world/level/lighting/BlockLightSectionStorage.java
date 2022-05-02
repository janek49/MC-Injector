package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;

public class BlockLightSectionStorage extends LayerLightSectionStorage {
   protected BlockLightSectionStorage(LightChunkGetter lightChunkGetter) {
      super(LightLayer.BLOCK, lightChunkGetter, new BlockLightSectionStorage.BlockDataLayerStorageMap(new Long2ObjectOpenHashMap()));
   }

   protected int getLightValue(long l) {
      long var3 = SectionPos.blockToSection(l);
      DataLayer var5 = this.getDataLayer(var3, false);
      return var5 == null?0:var5.get(SectionPos.sectionRelative(BlockPos.getX(l)), SectionPos.sectionRelative(BlockPos.getY(l)), SectionPos.sectionRelative(BlockPos.getZ(l)));
   }

   public static final class BlockDataLayerStorageMap extends DataLayerStorageMap {
      public BlockDataLayerStorageMap(Long2ObjectOpenHashMap long2ObjectOpenHashMap) {
         super(long2ObjectOpenHashMap);
      }

      public BlockLightSectionStorage.BlockDataLayerStorageMap copy() {
         return new BlockLightSectionStorage.BlockDataLayerStorageMap(this.map.clone());
      }

      // $FF: synthetic method
      public DataLayerStorageMap copy() {
         return this.copy();
      }
   }
}
