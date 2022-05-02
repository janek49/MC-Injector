package net.minecraft.world.level.newbiome.layer.traits;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.context.Context;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;
import net.minecraft.world.level.newbiome.layer.traits.DimensionOffset1Transformer;

public interface BishopTransformer extends AreaTransformer1, DimensionOffset1Transformer {
   int apply(Context var1, int var2, int var3, int var4, int var5, int var6);

   default int applyPixel(BigContext bigContext, Area area, int var3, int var4) {
      return this.apply(bigContext, area.get(this.getParentX(var3 + 0), this.getParentY(var4 + 2)), area.get(this.getParentX(var3 + 2), this.getParentY(var4 + 2)), area.get(this.getParentX(var3 + 2), this.getParentY(var4 + 0)), area.get(this.getParentX(var3 + 0), this.getParentY(var4 + 0)), area.get(this.getParentX(var3 + 1), this.getParentY(var4 + 1)));
   }
}
