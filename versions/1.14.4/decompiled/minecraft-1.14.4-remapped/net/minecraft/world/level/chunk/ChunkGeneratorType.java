package net.minecraft.world.level.chunk;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorFactory;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.DebugGeneratorSettings;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.NetherGeneratorSettings;
import net.minecraft.world.level.levelgen.NetherLevelSource;
import net.minecraft.world.level.levelgen.OverworldGeneratorSettings;
import net.minecraft.world.level.levelgen.OverworldLevelSource;
import net.minecraft.world.level.levelgen.TheEndGeneratorSettings;
import net.minecraft.world.level.levelgen.TheEndLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

public class ChunkGeneratorType implements ChunkGeneratorFactory {
   public static final ChunkGeneratorType SURFACE = register("surface", OverworldLevelSource::<init>, OverworldGeneratorSettings::<init>, true);
   public static final ChunkGeneratorType CAVES = register("caves", NetherLevelSource::<init>, NetherGeneratorSettings::<init>, true);
   public static final ChunkGeneratorType FLOATING_ISLANDS = register("floating_islands", TheEndLevelSource::<init>, TheEndGeneratorSettings::<init>, true);
   public static final ChunkGeneratorType DEBUG = register("debug", DebugLevelSource::<init>, DebugGeneratorSettings::<init>, false);
   public static final ChunkGeneratorType FLAT = register("flat", FlatLevelSource::<init>, FlatLevelGeneratorSettings::<init>, false);
   private final ChunkGeneratorFactory factory;
   private final boolean isPublic;
   private final Supplier settingsFactory;

   private static ChunkGeneratorType register(String string, ChunkGeneratorFactory chunkGeneratorFactory, Supplier supplier, boolean var3) {
      return (ChunkGeneratorType)Registry.register(Registry.CHUNK_GENERATOR_TYPE, (String)string, new ChunkGeneratorType(chunkGeneratorFactory, var3, supplier));
   }

   public ChunkGeneratorType(ChunkGeneratorFactory factory, boolean isPublic, Supplier settingsFactory) {
      this.factory = factory;
      this.isPublic = isPublic;
      this.settingsFactory = settingsFactory;
   }

   public ChunkGenerator create(Level level, BiomeSource biomeSource, ChunkGeneratorSettings chunkGeneratorSettings) {
      return this.factory.create(level, biomeSource, chunkGeneratorSettings);
   }

   public ChunkGeneratorSettings createSettings() {
      return (ChunkGeneratorSettings)this.settingsFactory.get();
   }

   public boolean isPublic() {
      return this.isPublic;
   }
}
