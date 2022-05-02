package net.minecraft.world.level.newbiome.layer;

import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.newbiome.area.AreaFactory;
import net.minecraft.world.level.newbiome.area.LazyArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Layer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LazyArea area;

   public Layer(AreaFactory areaFactory) {
      this.area = (LazyArea)areaFactory.make();
   }

   public Biome[] getArea(int var1, int var2, int var3, int var4) {
      Biome[] biomes = new Biome[var3 * var4];

      for(int var6 = 0; var6 < var4; ++var6) {
         for(int var7 = 0; var7 < var3; ++var7) {
            int var8 = this.area.get(var1 + var7, var2 + var6);
            Biome var9 = this.getBiome(var8);
            biomes[var7 + var6 * var3] = var9;
         }
      }

      return biomes;
   }

   private Biome getBiome(int i) {
      Biome biome = (Biome)Registry.BIOME.byId(i);
      if(biome == null) {
         if(SharedConstants.IS_RUNNING_IN_IDE) {
            throw new IllegalStateException("Unknown biome id: " + i);
         } else {
            LOGGER.warn("Unknown biome id: ", Integer.valueOf(i));
            return Biomes.DEFAULT;
         }
      } else {
         return biome;
      }
   }

   public Biome get(int var1, int var2) {
      return this.getBiome(this.area.get(var1, var2));
   }
}
