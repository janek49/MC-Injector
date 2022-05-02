package net.minecraft.world.level.newbiome.layer;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.Layers;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer2;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset0Transformer;

public enum RiverMixerLayer implements AreaTransformer2, DimensionOffset0Transformer {
   INSTANCE;

   private static final int FROZEN_RIVER = Registry.BIOME.getId(Biomes.FROZEN_RIVER);
   private static final int SNOWY_TUNDRA = Registry.BIOME.getId(Biomes.SNOWY_TUNDRA);
   private static final int MUSHROOM_FIELDS = Registry.BIOME.getId(Biomes.MUSHROOM_FIELDS);
   private static final int MUSHROOM_FIELD_SHORE = Registry.BIOME.getId(Biomes.MUSHROOM_FIELD_SHORE);
   private static final int RIVER = Registry.BIOME.getId(Biomes.RIVER);

   public int applyPixel(Context context, Area var2, Area var3, int var4, int var5) {
      int var6 = var2.get(this.getParentX(var4), this.getParentY(var5));
      int var7 = var3.get(this.getParentX(var4), this.getParentY(var5));
      return Layers.isOcean(var6)?var6:(var7 == RIVER?(var6 == SNOWY_TUNDRA?FROZEN_RIVER:(var6 != MUSHROOM_FIELDS && var6 != MUSHROOM_FIELD_SHORE?var7 & 255:MUSHROOM_FIELD_SHORE)):var6);
   }
}
