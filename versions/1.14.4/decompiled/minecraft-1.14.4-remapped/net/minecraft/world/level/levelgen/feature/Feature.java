package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BambooFeature;
import net.minecraft.world.level.levelgen.feature.BigTreeFeature;
import net.minecraft.world.level.levelgen.feature.BirchFeature;
import net.minecraft.world.level.levelgen.feature.BlockBlobConfiguration;
import net.minecraft.world.level.levelgen.feature.BlockBlobFeature;
import net.minecraft.world.level.levelgen.feature.BlueIceFeature;
import net.minecraft.world.level.levelgen.feature.BonusChestFeature;
import net.minecraft.world.level.levelgen.feature.BuriedTreasureConfiguration;
import net.minecraft.world.level.levelgen.feature.BuriedTreasureFeature;
import net.minecraft.world.level.levelgen.feature.BushConfiguration;
import net.minecraft.world.level.levelgen.feature.BushFeature;
import net.minecraft.world.level.levelgen.feature.CactusFeature;
import net.minecraft.world.level.levelgen.feature.CentralSpikedFeature;
import net.minecraft.world.level.levelgen.feature.ChorusPlantFeature;
import net.minecraft.world.level.levelgen.feature.CoralClawFeature;
import net.minecraft.world.level.levelgen.feature.CoralMushroomFeature;
import net.minecraft.world.level.levelgen.feature.CoralTreeFeature;
import net.minecraft.world.level.levelgen.feature.CountFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.DarkOakFeature;
import net.minecraft.world.level.levelgen.feature.DeadBushFeature;
import net.minecraft.world.level.levelgen.feature.DecoratedFeature;
import net.minecraft.world.level.levelgen.feature.DecoratedFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.DecoratedFlowerFeature;
import net.minecraft.world.level.levelgen.feature.DefaultFlowerFeature;
import net.minecraft.world.level.levelgen.feature.DesertPyramidFeature;
import net.minecraft.world.level.levelgen.feature.DesertWellFeature;
import net.minecraft.world.level.levelgen.feature.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.DiskReplaceFeature;
import net.minecraft.world.level.levelgen.feature.DoublePlantConfiguration;
import net.minecraft.world.level.levelgen.feature.DoublePlantFeature;
import net.minecraft.world.level.levelgen.feature.EndCityFeature;
import net.minecraft.world.level.levelgen.feature.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.EndGatewayFeature;
import net.minecraft.world.level.levelgen.feature.EndIslandFeature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.FeatureRadius;
import net.minecraft.world.level.levelgen.feature.FillLayerFeature;
import net.minecraft.world.level.levelgen.feature.FlowerFeature;
import net.minecraft.world.level.levelgen.feature.ForestFlowerFeature;
import net.minecraft.world.level.levelgen.feature.FossilFeature;
import net.minecraft.world.level.levelgen.feature.GeneralForestFlowerFeature;
import net.minecraft.world.level.levelgen.feature.GlowstoneFeature;
import net.minecraft.world.level.levelgen.feature.GrassConfiguration;
import net.minecraft.world.level.levelgen.feature.GrassFeature;
import net.minecraft.world.level.levelgen.feature.GroundBushFeature;
import net.minecraft.world.level.levelgen.feature.HayBlockPileFeature;
import net.minecraft.world.level.levelgen.feature.HellFireFeature;
import net.minecraft.world.level.levelgen.feature.HellSpringConfiguration;
import net.minecraft.world.level.levelgen.feature.HugeBrownMushroomFeature;
import net.minecraft.world.level.levelgen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.level.levelgen.feature.HugeRedMushroomFeature;
import net.minecraft.world.level.levelgen.feature.IceBlockPileFeature;
import net.minecraft.world.level.levelgen.feature.IcePatchFeature;
import net.minecraft.world.level.levelgen.feature.IceSpikeFeature;
import net.minecraft.world.level.levelgen.feature.IcebergConfiguration;
import net.minecraft.world.level.levelgen.feature.IcebergFeature;
import net.minecraft.world.level.levelgen.feature.IglooFeature;
import net.minecraft.world.level.levelgen.feature.JungleGrassFeature;
import net.minecraft.world.level.levelgen.feature.JunglePyramidFeature;
import net.minecraft.world.level.levelgen.feature.JungleTreeFeature;
import net.minecraft.world.level.levelgen.feature.KelpFeature;
import net.minecraft.world.level.levelgen.feature.LakeConfiguration;
import net.minecraft.world.level.levelgen.feature.LakeFeature;
import net.minecraft.world.level.levelgen.feature.LayerConfiguration;
import net.minecraft.world.level.levelgen.feature.MegaJungleTreeFeature;
import net.minecraft.world.level.levelgen.feature.MegaPineTreeFeature;
import net.minecraft.world.level.levelgen.feature.MelonBlockPileFeature;
import net.minecraft.world.level.levelgen.feature.MelonFeature;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.MonsterRoomFeature;
import net.minecraft.world.level.levelgen.feature.NetherFortressFeature;
import net.minecraft.world.level.levelgen.feature.NetherSpringFeature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.OceanMonumentFeature;
import net.minecraft.world.level.levelgen.feature.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.PillagerOutpostConfiguration;
import net.minecraft.world.level.levelgen.feature.PillagerOutpostFeature;
import net.minecraft.world.level.levelgen.feature.PineFeature;
import net.minecraft.world.level.levelgen.feature.PlainFlowerFeature;
import net.minecraft.world.level.levelgen.feature.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.PumpkinBlockPileFeature;
import net.minecraft.world.level.levelgen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.level.levelgen.feature.RandomBooleanSelectorFeature;
import net.minecraft.world.level.levelgen.feature.RandomFeatureConfig;
import net.minecraft.world.level.levelgen.feature.RandomRandomFeature;
import net.minecraft.world.level.levelgen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.ReedsFeature;
import net.minecraft.world.level.levelgen.feature.ReplaceBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.ReplaceBlockFeature;
import net.minecraft.world.level.levelgen.feature.SavannaTreeFeature;
import net.minecraft.world.level.levelgen.feature.SeaPickleFeature;
import net.minecraft.world.level.levelgen.feature.SeagrassFeature;
import net.minecraft.world.level.levelgen.feature.SeagrassFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.ShipwreckFeature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.SimpleRandomFeatureConfig;
import net.minecraft.world.level.levelgen.feature.SimpleRandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import net.minecraft.world.level.levelgen.feature.SnowBlockPileFeature;
import net.minecraft.world.level.levelgen.feature.SpikeConfiguration;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.SpringConfiguration;
import net.minecraft.world.level.levelgen.feature.SpringFeature;
import net.minecraft.world.level.levelgen.feature.SpruceFeature;
import net.minecraft.world.level.levelgen.feature.StrongholdFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.SwampFlowerFeature;
import net.minecraft.world.level.levelgen.feature.SwampTreeFeature;
import net.minecraft.world.level.levelgen.feature.SwamplandHutFeature;
import net.minecraft.world.level.levelgen.feature.TaigaGrassFeature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.VillageConfiguration;
import net.minecraft.world.level.levelgen.feature.VillageFeature;
import net.minecraft.world.level.levelgen.feature.VinesFeature;
import net.minecraft.world.level.levelgen.feature.VoidStartPlatformFeature;
import net.minecraft.world.level.levelgen.feature.WaterlilyFeature;
import net.minecraft.world.level.levelgen.feature.WoodlandMansionFeature;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;

public abstract class Feature {
   public static final StructureFeature PILLAGER_OUTPOST = (StructureFeature)register("pillager_outpost", new PillagerOutpostFeature(PillagerOutpostConfiguration::deserialize));
   public static final StructureFeature MINESHAFT = (StructureFeature)register("mineshaft", new MineshaftFeature(MineshaftConfiguration::deserialize));
   public static final StructureFeature WOODLAND_MANSION = (StructureFeature)register("woodland_mansion", new WoodlandMansionFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature JUNGLE_TEMPLE = (StructureFeature)register("jungle_temple", new JunglePyramidFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature DESERT_PYRAMID = (StructureFeature)register("desert_pyramid", new DesertPyramidFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature IGLOO = (StructureFeature)register("igloo", new IglooFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature SHIPWRECK = (StructureFeature)register("shipwreck", new ShipwreckFeature(ShipwreckConfiguration::deserialize));
   public static final SwamplandHutFeature SWAMP_HUT = (SwamplandHutFeature)register("swamp_hut", new SwamplandHutFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature STRONGHOLD = (StructureFeature)register("stronghold", new StrongholdFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature OCEAN_MONUMENT = (StructureFeature)register("ocean_monument", new OceanMonumentFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature OCEAN_RUIN = (StructureFeature)register("ocean_ruin", new OceanRuinFeature(OceanRuinConfiguration::deserialize));
   public static final StructureFeature NETHER_BRIDGE = (StructureFeature)register("nether_bridge", new NetherFortressFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature END_CITY = (StructureFeature)register("end_city", new EndCityFeature(NoneFeatureConfiguration::deserialize));
   public static final StructureFeature BURIED_TREASURE = (StructureFeature)register("buried_treasure", new BuriedTreasureFeature(BuriedTreasureConfiguration::deserialize));
   public static final StructureFeature VILLAGE = (StructureFeature)register("village", new VillageFeature(VillageConfiguration::deserialize));
   public static final Feature FANCY_TREE = register("fancy_tree", new BigTreeFeature(NoneFeatureConfiguration::deserialize, false));
   public static final Feature BIRCH_TREE = register("birch_tree", new BirchFeature(NoneFeatureConfiguration::deserialize, false, false));
   public static final Feature SUPER_BIRCH_TREE = register("super_birch_tree", new BirchFeature(NoneFeatureConfiguration::deserialize, false, true));
   public static final Feature JUNGLE_GROUND_BUSH = register("jungle_ground_bush", new GroundBushFeature(NoneFeatureConfiguration::deserialize, Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.OAK_LEAVES.defaultBlockState()));
   public static final Feature JUNGLE_TREE = register("jungle_tree", new JungleTreeFeature(NoneFeatureConfiguration::deserialize, false, 4, Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.JUNGLE_LEAVES.defaultBlockState(), true));
   public static final Feature PINE_TREE = register("pine_tree", new PineFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature DARK_OAK_TREE = register("dark_oak_tree", new DarkOakFeature(NoneFeatureConfiguration::deserialize, false));
   public static final Feature SAVANNA_TREE = register("savanna_tree", new SavannaTreeFeature(NoneFeatureConfiguration::deserialize, false));
   public static final Feature SPRUCE_TREE = register("spruce_tree", new SpruceFeature(NoneFeatureConfiguration::deserialize, false));
   public static final Feature SWAMP_TREE = register("swamp_tree", new SwampTreeFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature NORMAL_TREE = register("normal_tree", new TreeFeature(NoneFeatureConfiguration::deserialize, false));
   public static final Feature MEGA_JUNGLE_TREE = register("mega_jungle_tree", new MegaJungleTreeFeature(NoneFeatureConfiguration::deserialize, false, 10, 20, Blocks.JUNGLE_LOG.defaultBlockState(), Blocks.JUNGLE_LEAVES.defaultBlockState()));
   public static final Feature MEGA_PINE_TREE = register("mega_pine_tree", new MegaPineTreeFeature(NoneFeatureConfiguration::deserialize, false, false));
   public static final Feature MEGA_SPRUCE_TREE = register("mega_spruce_tree", new MegaPineTreeFeature(NoneFeatureConfiguration::deserialize, false, true));
   public static final FlowerFeature DEFAULT_FLOWER = (FlowerFeature)register("default_flower", new DefaultFlowerFeature(NoneFeatureConfiguration::deserialize));
   public static final FlowerFeature FOREST_FLOWER = (FlowerFeature)register("forest_flower", new ForestFlowerFeature(NoneFeatureConfiguration::deserialize));
   public static final FlowerFeature PLAIN_FLOWER = (FlowerFeature)register("plain_flower", new PlainFlowerFeature(NoneFeatureConfiguration::deserialize));
   public static final FlowerFeature SWAMP_FLOWER = (FlowerFeature)register("swamp_flower", new SwampFlowerFeature(NoneFeatureConfiguration::deserialize));
   public static final FlowerFeature GENERAL_FOREST_FLOWER = (FlowerFeature)register("general_forest_flower", new GeneralForestFlowerFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature JUNGLE_GRASS = register("jungle_grass", new JungleGrassFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature TAIGA_GRASS = register("taiga_grass", new TaigaGrassFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature GRASS = register("grass", new GrassFeature(GrassConfiguration::deserialize));
   public static final Feature VOID_START_PLATFORM = register("void_start_platform", new VoidStartPlatformFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature CACTUS = register("cactus", new CactusFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature DEAD_BUSH = register("dead_bush", new DeadBushFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature DESERT_WELL = register("desert_well", new DesertWellFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature FOSSIL = register("fossil", new FossilFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature HELL_FIRE = register("hell_fire", new HellFireFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature HUGE_RED_MUSHROOM = register("huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfig::deserialize));
   public static final Feature HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfig::deserialize));
   public static final Feature ICE_SPIKE = register("ice_spike", new IceSpikeFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature MELON = register("melon", new MelonFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature PUMPKIN = register("pumpkin", new CentralSpikedFeature(NoneFeatureConfiguration::deserialize, Blocks.PUMPKIN.defaultBlockState()));
   public static final Feature REED = register("reed", new ReedsFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature FREEZE_TOP_LAYER = register("freeze_top_layer", new SnowAndFreezeFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature VINES = register("vines", new VinesFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature WATERLILY = register("waterlily", new WaterlilyFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature MONSTER_ROOM = register("monster_room", new MonsterRoomFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature BLUE_ICE = register("blue_ice", new BlueIceFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature ICEBERG = register("iceberg", new IcebergFeature(IcebergConfiguration::deserialize));
   public static final Feature FOREST_ROCK = register("forest_rock", new BlockBlobFeature(BlockBlobConfiguration::deserialize));
   public static final Feature HAY_PILE = register("hay_pile", new HayBlockPileFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature SNOW_PILE = register("snow_pile", new SnowBlockPileFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature ICE_PILE = register("ice_pile", new IceBlockPileFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature MELON_PILE = register("melon_pile", new MelonBlockPileFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature PUMPKIN_PILE = register("pumpkin_pile", new PumpkinBlockPileFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature BUSH = register("bush", new BushFeature(BushConfiguration::deserialize));
   public static final Feature DISK = register("disk", new DiskReplaceFeature(DiskConfiguration::deserialize));
   public static final Feature DOUBLE_PLANT = register("double_plant", new DoublePlantFeature(DoublePlantConfiguration::deserialize));
   public static final Feature NETHER_SPRING = register("nether_spring", new NetherSpringFeature(HellSpringConfiguration::deserialize));
   public static final Feature ICE_PATCH = register("ice_patch", new IcePatchFeature(FeatureRadius::deserialize));
   public static final Feature LAKE = register("lake", new LakeFeature(LakeConfiguration::deserialize));
   public static final Feature ORE = register("ore", new OreFeature(OreConfiguration::deserialize));
   public static final Feature RANDOM_RANDOM_SELECTOR = register("random_random_selector", new RandomRandomFeature(RandomRandomFeatureConfig::deserialize));
   public static final Feature RANDOM_SELECTOR = register("random_selector", new RandomSelectorFeature(RandomFeatureConfig::deserialize));
   public static final Feature SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", new SimpleRandomSelectorFeature(SimpleRandomFeatureConfig::deserialize));
   public static final Feature RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", new RandomBooleanSelectorFeature(RandomBooleanFeatureConfig::deserialize));
   public static final Feature EMERALD_ORE = register("emerald_ore", new ReplaceBlockFeature(ReplaceBlockConfiguration::deserialize));
   public static final Feature SPRING = register("spring_feature", new SpringFeature(SpringConfiguration::deserialize));
   public static final Feature END_SPIKE = register("end_spike", new SpikeFeature(SpikeConfiguration::deserialize));
   public static final Feature END_ISLAND = register("end_island", new EndIslandFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayConfiguration::deserialize));
   public static final Feature SEAGRASS = register("seagrass", new SeagrassFeature(SeagrassFeatureConfiguration::deserialize));
   public static final Feature KELP = register("kelp", new KelpFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature CORAL_TREE = register("coral_tree", new CoralTreeFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature CORAL_CLAW = register("coral_claw", new CoralClawFeature(NoneFeatureConfiguration::deserialize));
   public static final Feature SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(CountFeatureConfiguration::deserialize));
   public static final Feature SIMPLE_BLOCK = register("simple_block", new SimpleBlockFeature(SimpleBlockConfiguration::deserialize));
   public static final Feature BAMBOO = register("bamboo", new BambooFeature(ProbabilityFeatureConfiguration::deserialize));
   public static final Feature DECORATED = register("decorated", new DecoratedFeature(DecoratedFeatureConfiguration::deserialize));
   public static final Feature DECORATED_FLOWER = register("decorated_flower", new DecoratedFlowerFeature(DecoratedFeatureConfiguration::deserialize));
   public static final Feature SWEET_BERRY_BUSH = register("sweet_berry_bush", new CentralSpikedFeature(NoneFeatureConfiguration::deserialize, (BlockState)Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, Integer.valueOf(3))));
   public static final Feature FILL_LAYER = register("fill_layer", new FillLayerFeature(LayerConfiguration::deserialize));
   public static final BonusChestFeature BONUS_CHEST = (BonusChestFeature)register("bonus_chest", new BonusChestFeature(NoneFeatureConfiguration::deserialize));
   public static final BiMap STRUCTURES_REGISTRY = (BiMap)Util.make(HashBiMap.create(), (hashBiMap) -> {
      hashBiMap.put("Pillager_Outpost".toLowerCase(Locale.ROOT), PILLAGER_OUTPOST);
      hashBiMap.put("Mineshaft".toLowerCase(Locale.ROOT), MINESHAFT);
      hashBiMap.put("Mansion".toLowerCase(Locale.ROOT), WOODLAND_MANSION);
      hashBiMap.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), JUNGLE_TEMPLE);
      hashBiMap.put("Desert_Pyramid".toLowerCase(Locale.ROOT), DESERT_PYRAMID);
      hashBiMap.put("Igloo".toLowerCase(Locale.ROOT), IGLOO);
      hashBiMap.put("Shipwreck".toLowerCase(Locale.ROOT), SHIPWRECK);
      hashBiMap.put("Swamp_Hut".toLowerCase(Locale.ROOT), SWAMP_HUT);
      hashBiMap.put("Stronghold".toLowerCase(Locale.ROOT), STRONGHOLD);
      hashBiMap.put("Monument".toLowerCase(Locale.ROOT), OCEAN_MONUMENT);
      hashBiMap.put("Ocean_Ruin".toLowerCase(Locale.ROOT), OCEAN_RUIN);
      hashBiMap.put("Fortress".toLowerCase(Locale.ROOT), NETHER_BRIDGE);
      hashBiMap.put("EndCity".toLowerCase(Locale.ROOT), END_CITY);
      hashBiMap.put("Buried_Treasure".toLowerCase(Locale.ROOT), BURIED_TREASURE);
      hashBiMap.put("Village".toLowerCase(Locale.ROOT), VILLAGE);
   });
   public static final List NOISE_AFFECTING_FEATURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE);
   private final Function configurationFactory;
   protected final boolean doUpdate;

   private static Feature register(String string, Feature var1) {
      return (Feature)Registry.register(Registry.FEATURE, (String)string, var1);
   }

   public Feature(Function configurationFactory) {
      this.configurationFactory = configurationFactory;
      this.doUpdate = false;
   }

   public Feature(Function configurationFactory, boolean doUpdate) {
      this.configurationFactory = configurationFactory;
      this.doUpdate = doUpdate;
   }

   public FeatureConfiguration createSettings(Dynamic dynamic) {
      return (FeatureConfiguration)this.configurationFactory.apply(dynamic);
   }

   protected void setBlock(LevelWriter levelWriter, BlockPos blockPos, BlockState blockState) {
      if(this.doUpdate) {
         levelWriter.setBlock(blockPos, blockState, 3);
      } else {
         levelWriter.setBlock(blockPos, blockState, 2);
      }

   }

   public abstract boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, FeatureConfiguration var5);

   public List getSpecialEnemies() {
      return Collections.emptyList();
   }

   public List getSpecialAnimals() {
      return Collections.emptyList();
   }
}
