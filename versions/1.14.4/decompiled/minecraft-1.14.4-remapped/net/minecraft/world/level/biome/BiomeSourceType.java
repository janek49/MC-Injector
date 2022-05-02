package net.minecraft.world.level.biome;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSourceSettings;
import net.minecraft.world.level.biome.CheckerboardBiomeSource;
import net.minecraft.world.level.biome.CheckerboardBiomeSourceSettings;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSourceSettings;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSourceSettings;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSourceSettings;

public class BiomeSourceType {
   public static final BiomeSourceType CHECKERBOARD = register("checkerboard", CheckerboardBiomeSource::<init>, CheckerboardBiomeSourceSettings::<init>);
   public static final BiomeSourceType FIXED = register("fixed", FixedBiomeSource::<init>, FixedBiomeSourceSettings::<init>);
   public static final BiomeSourceType VANILLA_LAYERED = register("vanilla_layered", OverworldBiomeSource::<init>, OverworldBiomeSourceSettings::<init>);
   public static final BiomeSourceType THE_END = register("the_end", TheEndBiomeSource::<init>, TheEndBiomeSourceSettings::<init>);
   private final Function factory;
   private final Supplier settingsFactory;

   private static BiomeSourceType register(String string, Function function, Supplier supplier) {
      return (BiomeSourceType)Registry.register(Registry.BIOME_SOURCE_TYPE, (String)string, new BiomeSourceType(function, supplier));
   }

   public BiomeSourceType(Function factory, Supplier settingsFactory) {
      this.factory = factory;
      this.settingsFactory = settingsFactory;
   }

   public BiomeSource create(BiomeSourceSettings biomeSourceSettings) {
      return (BiomeSource)this.factory.apply(biomeSourceSettings);
   }

   public BiomeSourceSettings createSettings() {
      return (BiomeSourceSettings)this.settingsFactory.get();
   }
}
