package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public enum VoronoiZoom implements AreaTransformer1 {
   INSTANCE;

   public int applyPixel(BigContext bigContext, Area area, int var3, int var4) {
      int var5 = var3 - 2;
      int var6 = var4 - 2;
      int var7 = var5 >> 2;
      int var8 = var6 >> 2;
      int var9 = var7 << 2;
      int var10 = var8 << 2;
      bigContext.initRandom((long)var9, (long)var10);
      double var11 = ((double)bigContext.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
      double var13 = ((double)bigContext.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
      bigContext.initRandom((long)(var9 + 4), (long)var10);
      double var15 = ((double)bigContext.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      double var17 = ((double)bigContext.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
      bigContext.initRandom((long)var9, (long)(var10 + 4));
      double var19 = ((double)bigContext.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
      double var21 = ((double)bigContext.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      bigContext.initRandom((long)(var9 + 4), (long)(var10 + 4));
      double var23 = ((double)bigContext.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      double var25 = ((double)bigContext.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      int var27 = var5 & 3;
      int var28 = var6 & 3;
      double var29 = ((double)var28 - var13) * ((double)var28 - var13) + ((double)var27 - var11) * ((double)var27 - var11);
      double var31 = ((double)var28 - var17) * ((double)var28 - var17) + ((double)var27 - var15) * ((double)var27 - var15);
      double var33 = ((double)var28 - var21) * ((double)var28 - var21) + ((double)var27 - var19) * ((double)var27 - var19);
      double var35 = ((double)var28 - var25) * ((double)var28 - var25) + ((double)var27 - var23) * ((double)var27 - var23);
      return var29 < var31 && var29 < var33 && var29 < var35?area.get(this.getParentX(var9), this.getParentY(var10)):(var31 < var29 && var31 < var33 && var31 < var35?area.get(this.getParentX(var9 + 4), this.getParentY(var10)) & 255:(var33 < var29 && var33 < var31 && var33 < var35?area.get(this.getParentX(var9), this.getParentY(var10 + 4)):area.get(this.getParentX(var9 + 4), this.getParentY(var10 + 4)) & 255));
   }

   public int getParentX(int i) {
      return i >> 2;
   }

   public int getParentY(int i) {
      return i >> 2;
   }
}
