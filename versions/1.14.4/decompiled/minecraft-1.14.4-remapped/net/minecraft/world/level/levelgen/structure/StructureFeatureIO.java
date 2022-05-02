package net.minecraft.world.level.levelgen.structure;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureFeatureIO {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final StructureFeature MINESHAFT = register("Mineshaft", Feature.MINESHAFT);
   public static final StructureFeature PILLAGER_OUTPOST = register("Pillager_Outpost", Feature.PILLAGER_OUTPOST);
   public static final StructureFeature NETHER_FORTRESS = register("Fortress", Feature.NETHER_BRIDGE);
   public static final StructureFeature STRONGHOLD = register("Stronghold", Feature.STRONGHOLD);
   public static final StructureFeature JUNGLE_PYRAMID = register("Jungle_Pyramid", Feature.JUNGLE_TEMPLE);
   public static final StructureFeature OCEAN_RUIN = register("Ocean_Ruin", Feature.OCEAN_RUIN);
   public static final StructureFeature DESERT_PYRAMID = register("Desert_Pyramid", Feature.DESERT_PYRAMID);
   public static final StructureFeature IGLOO = register("Igloo", Feature.IGLOO);
   public static final StructureFeature SWAMP_HUT = register("Swamp_Hut", Feature.SWAMP_HUT);
   public static final StructureFeature OCEAN_MONUMENT = register("Monument", Feature.OCEAN_MONUMENT);
   public static final StructureFeature END_CITY = register("EndCity", Feature.END_CITY);
   public static final StructureFeature WOODLAND_MANSION = register("Mansion", Feature.WOODLAND_MANSION);
   public static final StructureFeature BURIED_TREASURE = register("Buried_Treasure", Feature.BURIED_TREASURE);
   public static final StructureFeature SHIPWRECK = register("Shipwreck", Feature.SHIPWRECK);
   public static final StructureFeature VILLAGE = register("Village", Feature.VILLAGE);

   private static StructureFeature register(String string, StructureFeature var1) {
      return (StructureFeature)Registry.register(Registry.STRUCTURE_FEATURE, (String)string.toLowerCase(Locale.ROOT), var1);
   }

   public static void bootstrap() {
   }

   @Nullable
   public static StructureStart loadStaticStart(ChunkGenerator chunkGenerator, StructureManager structureManager, BiomeSource biomeSource, CompoundTag compoundTag) {
      String var4 = compoundTag.getString("id");
      if("INVALID".equals(var4)) {
         return StructureStart.INVALID_START;
      } else {
         StructureFeature<?> var5 = (StructureFeature)Registry.STRUCTURE_FEATURE.get(new ResourceLocation(var4.toLowerCase(Locale.ROOT)));
         if(var5 == null) {
            LOGGER.error("Unknown feature id: {}", var4);
            return null;
         } else {
            int var6 = compoundTag.getInt("ChunkX");
            int var7 = compoundTag.getInt("ChunkZ");
            Biome var8 = compoundTag.contains("biome")?(Biome)Registry.BIOME.get(new ResourceLocation(compoundTag.getString("biome"))):biomeSource.getBiome(new BlockPos((var6 << 4) + 9, 0, (var7 << 4) + 9));
            BoundingBox var9 = compoundTag.contains("BB")?new BoundingBox(compoundTag.getIntArray("BB")):BoundingBox.getUnknownBox();
            ListTag var10 = compoundTag.getList("Children", 10);

            try {
               StructureStart var11 = var5.getStartFactory().create(var5, var6, var7, var8, var9, 0, chunkGenerator.getSeed());

               for(int var12 = 0; var12 < var10.size(); ++var12) {
                  CompoundTag var13 = var10.getCompound(var12);
                  String var14 = var13.getString("id");
                  StructurePieceType var15 = (StructurePieceType)Registry.STRUCTURE_PIECE.get(new ResourceLocation(var14.toLowerCase(Locale.ROOT)));
                  if(var15 == null) {
                     LOGGER.error("Unknown structure piece id: {}", var14);
                  } else {
                     try {
                        StructurePiece var16 = var15.load(structureManager, var13);
                        var11.pieces.add(var16);
                     } catch (Exception var17) {
                        LOGGER.error("Exception loading structure piece with id {}", var14, var17);
                     }
                  }
               }

               return var11;
            } catch (Exception var18) {
               LOGGER.error("Failed Start with id {}", var4, var18);
               return null;
            }
         }
      }
   }
}
