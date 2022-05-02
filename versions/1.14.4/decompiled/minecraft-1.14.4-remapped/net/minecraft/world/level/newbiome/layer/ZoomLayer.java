package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public enum ZoomLayer implements AreaTransformer1 {
   NORMAL,
   FUZZY {
      protected int modeOrRandom(BigContext bigContext, int var2, int var3, int var4, int var5) {
         return bigContext.random(var2, var3, var4, var5);
      }
   };

   private ZoomLayer() {
   }

   public int getParentX(int i) {
      return i >> 1;
   }

   public int getParentY(int i) {
      return i >> 1;
   }

   public int applyPixel(BigContext bigContext, Area area, int var3, int var4) {
      int var5 = area.get(this.getParentX(var3), this.getParentY(var4));
      bigContext.initRandom((long)(var3 >> 1 << 1), (long)(var4 >> 1 << 1));
      int var6 = var3 & 1;
      int var7 = var4 & 1;
      if(var6 == 0 && var7 == 0) {
         return var5;
      } else {
         int var8 = area.get(this.getParentX(var3), this.getParentY(var4 + 1));
         int var9 = bigContext.random(var5, var8);
         if(var6 == 0 && var7 == 1) {
            return var9;
         } else {
            int var10 = area.get(this.getParentX(var3 + 1), this.getParentY(var4));
            int var11 = bigContext.random(var5, var10);
            if(var6 == 1 && var7 == 0) {
               return var11;
            } else {
               int var12 = area.get(this.getParentX(var3 + 1), this.getParentY(var4 + 1));
               return this.modeOrRandom(bigContext, var5, var10, var8, var12);
            }
         }
      }
   }

   protected int modeOrRandom(BigContext bigContext, int var2, int var3, int var4, int var5) {
      return var3 == var4 && var4 == var5?var3:(var2 == var3 && var2 == var4?var2:(var2 == var3 && var2 == var5?var2:(var2 == var4 && var2 == var5?var2:(var2 == var3 && var4 != var5?var2:(var2 == var4 && var3 != var5?var2:(var2 == var5 && var3 != var4?var2:(var3 == var4 && var2 != var5?var3:(var3 == var5 && var2 != var4?var3:(var4 == var5 && var2 != var3?var4:bigContext.random(var2, var3, var4, var5))))))))));
   }
}
