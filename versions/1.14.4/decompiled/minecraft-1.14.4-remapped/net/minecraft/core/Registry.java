package net.minecraft.core;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.IdMap;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIO;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Registry implements IdMap {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Map LOADERS = Maps.newLinkedHashMap();
   public static final WritableRegistry REGISTRY = new MappedRegistry();
   public static final Registry SOUND_EVENT = registerSimple("sound_event", () -> {
      return SoundEvents.ITEM_PICKUP;
   });
   public static final DefaultedRegistry FLUID = registerDefaulted("fluid", "empty", () -> {
      return Fluids.EMPTY;
   });
   public static final Registry MOB_EFFECT = registerSimple("mob_effect", () -> {
      return MobEffects.LUCK;
   });
   public static final DefaultedRegistry BLOCK = registerDefaulted("block", "air", () -> {
      return Blocks.AIR;
   });
   public static final Registry ENCHANTMENT = registerSimple("enchantment", () -> {
      return Enchantments.BLOCK_FORTUNE;
   });
   public static final DefaultedRegistry ENTITY_TYPE = registerDefaulted("entity_type", "pig", () -> {
      return EntityType.PIG;
   });
   public static final DefaultedRegistry ITEM = registerDefaulted("item", "air", () -> {
      return Items.AIR;
   });
   public static final DefaultedRegistry POTION = registerDefaulted("potion", "empty", () -> {
      return Potions.EMPTY;
   });
   public static final Registry CARVER = registerSimple("carver", () -> {
      return WorldCarver.CAVE;
   });
   public static final Registry SURFACE_BUILDER = registerSimple("surface_builder", () -> {
      return SurfaceBuilder.DEFAULT;
   });
   public static final Registry FEATURE = registerSimple("feature", () -> {
      return Feature.ORE;
   });
   public static final Registry DECORATOR = registerSimple("decorator", () -> {
      return FeatureDecorator.NOPE;
   });
   public static final Registry BIOME = registerSimple("biome", () -> {
      return Biomes.DEFAULT;
   });
   public static final Registry PARTICLE_TYPE = registerSimple("particle_type", () -> {
      return ParticleTypes.BLOCK;
   });
   public static final Registry BIOME_SOURCE_TYPE = registerSimple("biome_source_type", () -> {
      return BiomeSourceType.VANILLA_LAYERED;
   });
   public static final Registry BLOCK_ENTITY_TYPE = registerSimple("block_entity_type", () -> {
      return BlockEntityType.FURNACE;
   });
   public static final Registry CHUNK_GENERATOR_TYPE = registerSimple("chunk_generator_type", () -> {
      return ChunkGeneratorType.FLAT;
   });
   public static final Registry DIMENSION_TYPE = registerSimple("dimension_type", () -> {
      return DimensionType.OVERWORLD;
   });
   public static final DefaultedRegistry MOTIVE = registerDefaulted("motive", "kebab", () -> {
      return Motive.KEBAB;
   });
   public static final Registry CUSTOM_STAT = registerSimple("custom_stat", () -> {
      return Stats.JUMP;
   });
   public static final DefaultedRegistry CHUNK_STATUS = registerDefaulted("chunk_status", "empty", () -> {
      return ChunkStatus.EMPTY;
   });
   public static final Registry STRUCTURE_FEATURE = registerSimple("structure_feature", () -> {
      return StructureFeatureIO.MINESHAFT;
   });
   public static final Registry STRUCTURE_PIECE = registerSimple("structure_piece", () -> {
      return StructurePieceType.MINE_SHAFT_ROOM;
   });
   public static final Registry RULE_TEST = registerSimple("rule_test", () -> {
      return RuleTestType.ALWAYS_TRUE_TEST;
   });
   public static final Registry STRUCTURE_PROCESSOR = registerSimple("structure_processor", () -> {
      return StructureProcessorType.BLOCK_IGNORE;
   });
   public static final Registry STRUCTURE_POOL_ELEMENT = registerSimple("structure_pool_element", () -> {
      return StructurePoolElementType.EMPTY;
   });
   public static final Registry MENU = registerSimple("menu", () -> {
      return MenuType.ANVIL;
   });
   public static final Registry RECIPE_TYPE = registerSimple("recipe_type", () -> {
      return RecipeType.CRAFTING;
   });
   public static final Registry RECIPE_SERIALIZER = registerSimple("recipe_serializer", () -> {
      return RecipeSerializer.SHAPELESS_RECIPE;
   });
   public static final Registry STAT_TYPE = registerSimple("stat_type", () -> {
      return Stats.ITEM_USED;
   });
   public static final DefaultedRegistry VILLAGER_TYPE = registerDefaulted("villager_type", "plains", () -> {
      return VillagerType.PLAINS;
   });
   public static final DefaultedRegistry VILLAGER_PROFESSION = registerDefaulted("villager_profession", "none", () -> {
      return VillagerProfession.NONE;
   });
   public static final DefaultedRegistry POINT_OF_INTEREST_TYPE = registerDefaulted("point_of_interest_type", "unemployed", () -> {
      return PoiType.UNEMPLOYED;
   });
   public static final DefaultedRegistry MEMORY_MODULE_TYPE = registerDefaulted("memory_module_type", "dummy", () -> {
      return MemoryModuleType.DUMMY;
   });
   public static final DefaultedRegistry SENSOR_TYPE = registerDefaulted("sensor_type", "dummy", () -> {
      return SensorType.DUMMY;
   });
   public static final Registry SCHEDULE = registerSimple("schedule", () -> {
      return Schedule.EMPTY;
   });
   public static final Registry ACTIVITY = registerSimple("activity", () -> {
      return Activity.IDLE;
   });

   private static Registry registerSimple(String string, Supplier supplier) {
      return internalRegister(string, new MappedRegistry(), supplier);
   }

   private static DefaultedRegistry registerDefaulted(String var0, String var1, Supplier supplier) {
      return (DefaultedRegistry)internalRegister(var0, new DefaultedRegistry(var1), supplier);
   }

   private static WritableRegistry internalRegister(String string, WritableRegistry var1, Supplier supplier) {
      ResourceLocation var3 = new ResourceLocation(string);
      LOADERS.put(var3, supplier);
      return (WritableRegistry)REGISTRY.register(var3, var1);
   }

   @Nullable
   public abstract ResourceLocation getKey(Object var1);

   public abstract int getId(@Nullable Object var1);

   @Nullable
   public abstract Object get(@Nullable ResourceLocation var1);

   public abstract Optional getOptional(@Nullable ResourceLocation var1);

   public abstract Set keySet();

   @Nullable
   public abstract Object getRandom(Random var1);

   public Stream stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   public abstract boolean containsKey(ResourceLocation var1);

   public static Object register(Registry registry, String string, Object var2) {
      return register(registry, new ResourceLocation(string), var2);
   }

   public static Object register(Registry registry, ResourceLocation resourceLocation, Object var2) {
      return ((WritableRegistry)registry).register(resourceLocation, var2);
   }

   public static Object registerMapping(Registry registry, int var1, String string, Object var3) {
      return ((WritableRegistry)registry).registerMapping(var1, new ResourceLocation(string), var3);
   }

   static {
      LOADERS.entrySet().forEach((map$Entry) -> {
         if(((Supplier)map$Entry.getValue()).get() == null) {
            LOGGER.error("Unable to bootstrap registry \'{}\'", map$Entry.getKey());
         }

      });
      REGISTRY.forEach((writableRegistry) -> {
         if(writableRegistry.isEmpty()) {
            LOGGER.error("Registry \'{}\' was empty after loading", REGISTRY.getKey(writableRegistry));
            if(SharedConstants.IS_RUNNING_IN_IDE) {
               throw new IllegalStateException("Registry: \'" + REGISTRY.getKey(writableRegistry) + "\' is empty, not allowed, fix me!");
            }
         }

         if(writableRegistry instanceof DefaultedRegistry) {
            ResourceLocation var1 = ((DefaultedRegistry)writableRegistry).getDefaultKey();
            Validate.notNull(writableRegistry.get(var1), "Missing default of DefaultedMappedRegistry: " + var1, new Object[0]);
         }

      });
   }
}
