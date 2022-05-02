package net.minecraft.world.entity;

import com.mojang.datafixers.DataFixUtils;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.PigZombie;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.entity.vehicle.MinecartSpawner;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityType {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final EntityType AREA_EFFECT_CLOUD = register("area_effect_cloud", EntityType.Builder.of(AreaEffectCloud::<init>, MobCategory.MISC).fireImmune().sized(6.0F, 0.5F));
   public static final EntityType ARMOR_STAND = register("armor_stand", EntityType.Builder.of(ArmorStand::<init>, MobCategory.MISC).sized(0.5F, 1.975F));
   public static final EntityType ARROW = register("arrow", EntityType.Builder.of(Arrow::<init>, MobCategory.MISC).sized(0.5F, 0.5F));
   public static final EntityType BAT = register("bat", EntityType.Builder.of(Bat::<init>, MobCategory.AMBIENT).sized(0.5F, 0.9F));
   public static final EntityType BLAZE = register("blaze", EntityType.Builder.of(Blaze::<init>, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.8F));
   public static final EntityType BOAT = register("boat", EntityType.Builder.of(Boat::<init>, MobCategory.MISC).sized(1.375F, 0.5625F));
   public static final EntityType CAT = register("cat", EntityType.Builder.of(Cat::<init>, MobCategory.CREATURE).sized(0.6F, 0.7F));
   public static final EntityType CAVE_SPIDER = register("cave_spider", EntityType.Builder.of(CaveSpider::<init>, MobCategory.MONSTER).sized(0.7F, 0.5F));
   public static final EntityType CHICKEN = register("chicken", EntityType.Builder.of(Chicken::<init>, MobCategory.CREATURE).sized(0.4F, 0.7F));
   public static final EntityType COD = register("cod", EntityType.Builder.of(Cod::<init>, MobCategory.WATER_CREATURE).sized(0.5F, 0.3F));
   public static final EntityType COW = register("cow", EntityType.Builder.of(Cow::<init>, MobCategory.CREATURE).sized(0.9F, 1.4F));
   public static final EntityType CREEPER = register("creeper", EntityType.Builder.of(Creeper::<init>, MobCategory.MONSTER).sized(0.6F, 1.7F));
   public static final EntityType DONKEY = register("donkey", EntityType.Builder.of(Donkey::<init>, MobCategory.CREATURE).sized(1.3964844F, 1.5F));
   public static final EntityType DOLPHIN = register("dolphin", EntityType.Builder.of(Dolphin::<init>, MobCategory.WATER_CREATURE).sized(0.9F, 0.6F));
   public static final EntityType DRAGON_FIREBALL = register("dragon_fireball", EntityType.Builder.of(DragonFireball::<init>, MobCategory.MISC).sized(1.0F, 1.0F));
   public static final EntityType DROWNED = register("drowned", EntityType.Builder.of(Drowned::<init>, MobCategory.MONSTER).sized(0.6F, 1.95F));
   public static final EntityType ELDER_GUARDIAN = register("elder_guardian", EntityType.Builder.of(ElderGuardian::<init>, MobCategory.MONSTER).sized(1.9975F, 1.9975F));
   public static final EntityType END_CRYSTAL = register("end_crystal", EntityType.Builder.of(EndCrystal::<init>, MobCategory.MISC).sized(2.0F, 2.0F));
   public static final EntityType ENDER_DRAGON = register("ender_dragon", EntityType.Builder.of(EnderDragon::<init>, MobCategory.MONSTER).fireImmune().sized(16.0F, 8.0F));
   public static final EntityType ENDERMAN = register("enderman", EntityType.Builder.of(EnderMan::<init>, MobCategory.MONSTER).sized(0.6F, 2.9F));
   public static final EntityType ENDERMITE = register("endermite", EntityType.Builder.of(Endermite::<init>, MobCategory.MONSTER).sized(0.4F, 0.3F));
   public static final EntityType EVOKER_FANGS = register("evoker_fangs", EntityType.Builder.of(EvokerFangs::<init>, MobCategory.MISC).sized(0.5F, 0.8F));
   public static final EntityType EVOKER = register("evoker", EntityType.Builder.of(Evoker::<init>, MobCategory.MONSTER).sized(0.6F, 1.95F));
   public static final EntityType EXPERIENCE_ORB = register("experience_orb", EntityType.Builder.of(ExperienceOrb::<init>, MobCategory.MISC).sized(0.5F, 0.5F));
   public static final EntityType EYE_OF_ENDER = register("eye_of_ender", EntityType.Builder.of(EyeOfEnder::<init>, MobCategory.MISC).sized(0.25F, 0.25F));
   public static final EntityType FALLING_BLOCK = register("falling_block", EntityType.Builder.of(FallingBlockEntity::<init>, MobCategory.MISC).sized(0.98F, 0.98F));
   public static final EntityType FIREWORK_ROCKET = register("firework_rocket", EntityType.Builder.of(FireworkRocketEntity::<init>, MobCategory.MISC).sized(0.25F, 0.25F));
   public static final EntityType FOX = register("fox", EntityType.Builder.of(Fox::<init>, MobCategory.CREATURE).sized(0.6F, 0.7F));
   public static final EntityType GHAST = register("ghast", EntityType.Builder.of(Ghast::<init>, MobCategory.MONSTER).fireImmune().sized(4.0F, 4.0F));
   public static final EntityType GIANT = register("giant", EntityType.Builder.of(Giant::<init>, MobCategory.MONSTER).sized(3.6F, 12.0F));
   public static final EntityType GUARDIAN = register("guardian", EntityType.Builder.of(Guardian::<init>, MobCategory.MONSTER).sized(0.85F, 0.85F));
   public static final EntityType HORSE = register("horse", EntityType.Builder.of(Horse::<init>, MobCategory.CREATURE).sized(1.3964844F, 1.6F));
   public static final EntityType HUSK = register("husk", EntityType.Builder.of(Husk::<init>, MobCategory.MONSTER).sized(0.6F, 1.95F));
   public static final EntityType ILLUSIONER = register("illusioner", EntityType.Builder.of(Illusioner::<init>, MobCategory.MONSTER).sized(0.6F, 1.95F));
   public static final EntityType ITEM = register("item", EntityType.Builder.of(ItemEntity::<init>, MobCategory.MISC).sized(0.25F, 0.25F));
   public static final EntityType ITEM_FRAME = register("item_frame", EntityType.Builder.of(ItemFrame::<init>, MobCategory.MISC).sized(0.5F, 0.5F));
   public static final EntityType FIREBALL = register("fireball", EntityType.Builder.of(LargeFireball::<init>, MobCategory.MISC).sized(1.0F, 1.0F));
   public static final EntityType LEASH_KNOT = register("leash_knot", EntityType.Builder.of(LeashFenceKnotEntity::<init>, MobCategory.MISC).noSave().sized(0.5F, 0.5F));
   public static final EntityType LLAMA = register("llama", EntityType.Builder.of(Llama::<init>, MobCategory.CREATURE).sized(0.9F, 1.87F));
   public static final EntityType LLAMA_SPIT = register("llama_spit", EntityType.Builder.of(LlamaSpit::<init>, MobCategory.MISC).sized(0.25F, 0.25F));
   public static final EntityType MAGMA_CUBE = register("magma_cube", EntityType.Builder.of(MagmaCube::<init>, MobCategory.MONSTER).fireImmune().sized(2.04F, 2.04F));
   public static final EntityType MINECART = register("minecart", EntityType.Builder.of(Minecart::<init>, MobCategory.MISC).sized(0.98F, 0.7F));
   public static final EntityType CHEST_MINECART = register("chest_minecart", EntityType.Builder.of(MinecartChest::<init>, MobCategory.MISC).sized(0.98F, 0.7F));
   public static final EntityType COMMAND_BLOCK_MINECART = register("command_block_minecart", EntityType.Builder.of(MinecartCommandBlock::<init>, MobCategory.MISC).sized(0.98F, 0.7F));
   public static final EntityType FURNACE_MINECART = register("furnace_minecart", EntityType.Builder.of(MinecartFurnace::<init>, MobCategory.MISC).sized(0.98F, 0.7F));
   public static final EntityType HOPPER_MINECART = register("hopper_minecart", EntityType.Builder.of(MinecartHopper::<init>, MobCategory.MISC).sized(0.98F, 0.7F));
   public static final EntityType SPAWNER_MINECART = register("spawner_minecart", EntityType.Builder.of(MinecartSpawner::<init>, MobCategory.MISC).sized(0.98F, 0.7F));
   public static final EntityType TNT_MINECART = register("tnt_minecart", EntityType.Builder.of(MinecartTNT::<init>, MobCategory.MISC).sized(0.98F, 0.7F));
   public static final EntityType MULE = register("mule", EntityType.Builder.of(Mule::<init>, MobCategory.CREATURE).sized(1.3964844F, 1.6F));
   public static final EntityType MOOSHROOM = register("mooshroom", EntityType.Builder.of(MushroomCow::<init>, MobCategory.CREATURE).sized(0.9F, 1.4F));
   public static final EntityType OCELOT = register("ocelot", EntityType.Builder.of(Ocelot::<init>, MobCategory.CREATURE).sized(0.6F, 0.7F));
   public static final EntityType PAINTING = register("painting", EntityType.Builder.of(Painting::<init>, MobCategory.MISC).sized(0.5F, 0.5F));
   public static final EntityType PANDA = register("panda", EntityType.Builder.of(Panda::<init>, MobCategory.CREATURE).sized(1.3F, 1.25F));
   public static final EntityType PARROT = register("parrot", EntityType.Builder.of(Parrot::<init>, MobCategory.CREATURE).sized(0.5F, 0.9F));
   public static final EntityType PIG = register("pig", EntityType.Builder.of(Pig::<init>, MobCategory.CREATURE).sized(0.9F, 0.9F));
   public static final EntityType PUFFERFISH = register("pufferfish", EntityType.Builder.of(Pufferfish::<init>, MobCategory.WATER_CREATURE).sized(0.7F, 0.7F));
   public static final EntityType ZOMBIE_PIGMAN = register("zombie_pigman", EntityType.Builder.of(PigZombie::<init>, MobCategory.MONSTER).fireImmune().sized(0.6F, 1.95F));
   public static final EntityType POLAR_BEAR = register("polar_bear", EntityType.Builder.of(PolarBear::<init>, MobCategory.CREATURE).sized(1.4F, 1.4F));
   public static final EntityType TNT = register("tnt", EntityType.Builder.of(PrimedTnt::<init>, MobCategory.MISC).fireImmune().sized(0.98F, 0.98F));
   public static final EntityType RABBIT = register("rabbit", EntityType.Builder.of(Rabbit::<init>, MobCategory.CREATURE).sized(0.4F, 0.5F));
   public static final EntityType SALMON = register("salmon", EntityType.Builder.of(Salmon::<init>, MobCategory.WATER_CREATURE).sized(0.7F, 0.4F));
   public static final EntityType SHEEP = register("sheep", EntityType.Builder.of(Sheep::<init>, MobCategory.CREATURE).sized(0.9F, 1.3F));
   public static final EntityType SHULKER = register("shulker", EntityType.Builder.of(Shulker::<init>, MobCategory.MONSTER).fireImmune().canSpawnFarFromPlayer().sized(1.0F, 1.0F));
   public static final EntityType SHULKER_BULLET = register("shulker_bullet", EntityType.Builder.of(ShulkerBullet::<init>, MobCategory.MISC).sized(0.3125F, 0.3125F));
   public static final EntityType SILVERFISH = register("silverfish", EntityType.Builder.of(Silverfish::<init>, MobCategory.MONSTER).sized(0.4F, 0.3F));
   public static final EntityType SKELETON = register("skeleton", EntityType.Builder.of(Skeleton::<init>, MobCategory.MONSTER).sized(0.6F, 1.99F));
   public static final EntityType SKELETON_HORSE = register("skeleton_horse", EntityType.Builder.of(SkeletonHorse::<init>, MobCategory.CREATURE).sized(1.3964844F, 1.6F));
   public static final EntityType SLIME = register("slime", EntityType.Builder.of(Slime::<init>, MobCategory.MONSTER).sized(2.04F, 2.04F));
   public static final EntityType SMALL_FIREBALL = register("small_fireball", EntityType.Builder.of(SmallFireball::<init>, MobCategory.MISC).sized(0.3125F, 0.3125F));
   public static final EntityType SNOW_GOLEM = register("snow_golem", EntityType.Builder.of(SnowGolem::<init>, MobCategory.MISC).sized(0.7F, 1.9F));
   public static final EntityType SNOWBALL = register("snowball", EntityType.Builder.of(Snowball::<init>, MobCategory.MISC).sized(0.25F, 0.25F));
   public static final EntityType SPECTRAL_ARROW = register("spectral_arrow", EntityType.Builder.of(SpectralArrow::<init>, MobCategory.MISC).sized(0.5F, 0.5F));
   public static final EntityType SPIDER = register("spider", EntityType.Builder.of(Spider::<init>, MobCategory.MONSTER).sized(1.4F, 0.9F));
   public static final EntityType SQUID = register("squid", EntityType.Builder.of(Squid::<init>, MobCategory.WATER_CREATURE).sized(0.8F, 0.8F));
   public static final EntityType STRAY = register("stray", EntityType.Builder.of(Stray::<init>, MobCategory.MONSTER).sized(0.6F, 1.99F));
   public static final EntityType TRADER_LLAMA = register("trader_llama", EntityType.Builder.of(TraderLlama::<init>, MobCategory.CREATURE).sized(0.9F, 1.87F));
   public static final EntityType TROPICAL_FISH = register("tropical_fish", EntityType.Builder.of(TropicalFish::<init>, MobCategory.WATER_CREATURE).sized(0.5F, 0.4F));
   public static final EntityType TURTLE = register("turtle", EntityType.Builder.of(Turtle::<init>, MobCategory.CREATURE).sized(1.2F, 0.4F));
   public static final EntityType EGG = register("egg", EntityType.Builder.of(ThrownEgg::<init>, MobCategory.MISC).sized(0.25F, 0.25F));
   public static final EntityType ENDER_PEARL = register("ender_pearl", EntityType.Builder.of(ThrownEnderpearl::<init>, MobCategory.MISC).sized(0.25F, 0.25F));
   public static final EntityType EXPERIENCE_BOTTLE = register("experience_bottle", EntityType.Builder.of(ThrownExperienceBottle::<init>, MobCategory.MISC).sized(0.25F, 0.25F));
   public static final EntityType POTION = register("potion", EntityType.Builder.of(ThrownPotion::<init>, MobCategory.MISC).sized(0.25F, 0.25F));
   public static final EntityType TRIDENT = register("trident", EntityType.Builder.of(ThrownTrident::<init>, MobCategory.MISC).sized(0.5F, 0.5F));
   public static final EntityType VEX = register("vex", EntityType.Builder.of(Vex::<init>, MobCategory.MONSTER).fireImmune().sized(0.4F, 0.8F));
   public static final EntityType VILLAGER = register("villager", EntityType.Builder.of(Villager::<init>, MobCategory.MISC).sized(0.6F, 1.95F));
   public static final EntityType IRON_GOLEM = register("iron_golem", EntityType.Builder.of(IronGolem::<init>, MobCategory.MISC).sized(1.4F, 2.7F));
   public static final EntityType VINDICATOR = register("vindicator", EntityType.Builder.of(Vindicator::<init>, MobCategory.MONSTER).sized(0.6F, 1.95F));
   public static final EntityType PILLAGER = register("pillager", EntityType.Builder.of(Pillager::<init>, MobCategory.MONSTER).canSpawnFarFromPlayer().sized(0.6F, 1.95F));
   public static final EntityType WANDERING_TRADER = register("wandering_trader", EntityType.Builder.of(WanderingTrader::<init>, MobCategory.CREATURE).sized(0.6F, 1.95F));
   public static final EntityType WITCH = register("witch", EntityType.Builder.of(Witch::<init>, MobCategory.MONSTER).sized(0.6F, 1.95F));
   public static final EntityType WITHER = register("wither", EntityType.Builder.of(WitherBoss::<init>, MobCategory.MONSTER).fireImmune().sized(0.9F, 3.5F));
   public static final EntityType WITHER_SKELETON = register("wither_skeleton", EntityType.Builder.of(WitherSkeleton::<init>, MobCategory.MONSTER).fireImmune().sized(0.7F, 2.4F));
   public static final EntityType WITHER_SKULL = register("wither_skull", EntityType.Builder.of(WitherSkull::<init>, MobCategory.MISC).sized(0.3125F, 0.3125F));
   public static final EntityType WOLF = register("wolf", EntityType.Builder.of(Wolf::<init>, MobCategory.CREATURE).sized(0.6F, 0.85F));
   public static final EntityType ZOMBIE = register("zombie", EntityType.Builder.of(Zombie::<init>, MobCategory.MONSTER).sized(0.6F, 1.95F));
   public static final EntityType ZOMBIE_HORSE = register("zombie_horse", EntityType.Builder.of(ZombieHorse::<init>, MobCategory.CREATURE).sized(1.3964844F, 1.6F));
   public static final EntityType ZOMBIE_VILLAGER = register("zombie_villager", EntityType.Builder.of(ZombieVillager::<init>, MobCategory.MONSTER).sized(0.6F, 1.95F));
   public static final EntityType PHANTOM = register("phantom", EntityType.Builder.of(Phantom::<init>, MobCategory.MONSTER).sized(0.9F, 0.5F));
   public static final EntityType RAVAGER = register("ravager", EntityType.Builder.of(Ravager::<init>, MobCategory.MONSTER).sized(1.95F, 2.2F));
   public static final EntityType LIGHTNING_BOLT = register("lightning_bolt", EntityType.Builder.createNothing(MobCategory.MISC).noSave().sized(0.0F, 0.0F));
   public static final EntityType PLAYER = register("player", EntityType.Builder.createNothing(MobCategory.MISC).noSave().noSummon().sized(0.6F, 1.8F));
   public static final EntityType FISHING_BOBBER = register("fishing_bobber", EntityType.Builder.createNothing(MobCategory.MISC).noSave().noSummon().sized(0.25F, 0.25F));
   private final EntityType.EntityFactory factory;
   private final MobCategory category;
   private final boolean serialize;
   private final boolean summon;
   private final boolean fireImmune;
   private final boolean canSpawnFarFromPlayer;
   @Nullable
   private String descriptionId;
   @Nullable
   private Component description;
   @Nullable
   private ResourceLocation lootTable;
   private final EntityDimensions dimensions;

   private static EntityType register(String string, EntityType.Builder entityType$Builder) {
      return (EntityType)Registry.register(Registry.ENTITY_TYPE, (String)string, entityType$Builder.build(string));
   }

   public static ResourceLocation getKey(EntityType entityType) {
      return Registry.ENTITY_TYPE.getKey(entityType);
   }

   public static Optional byString(String string) {
      return Registry.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(string));
   }

   public EntityType(EntityType.EntityFactory factory, MobCategory category, boolean serialize, boolean summon, boolean fireImmune, boolean canSpawnFarFromPlayer, EntityDimensions dimensions) {
      this.factory = factory;
      this.category = category;
      this.canSpawnFarFromPlayer = canSpawnFarFromPlayer;
      this.serialize = serialize;
      this.summon = summon;
      this.fireImmune = fireImmune;
      this.dimensions = dimensions;
   }

   @Nullable
   public Entity spawn(Level level, @Nullable ItemStack itemStack, @Nullable Player player, BlockPos blockPos, MobSpawnType mobSpawnType, boolean var6, boolean var7) {
      return this.spawn(level, itemStack == null?null:itemStack.getTag(), itemStack != null && itemStack.hasCustomHoverName()?itemStack.getHoverName():null, player, blockPos, mobSpawnType, var6, var7);
   }

   @Nullable
   public Entity spawn(Level level, @Nullable CompoundTag compoundTag, @Nullable Component component, @Nullable Player player, BlockPos blockPos, MobSpawnType mobSpawnType, boolean var7, boolean var8) {
      T entity = this.create(level, compoundTag, component, player, blockPos, mobSpawnType, var7, var8);
      level.addFreshEntity(entity);
      return entity;
   }

   @Nullable
   public Entity create(Level level, @Nullable CompoundTag compoundTag, @Nullable Component component, @Nullable Player player, BlockPos blockPos, MobSpawnType mobSpawnType, boolean var7, boolean var8) {
      T entity = this.create(level);
      if(entity == null) {
         return null;
      } else {
         double var10;
         if(var7) {
            entity.setPos((double)blockPos.getX() + 0.5D, (double)(blockPos.getY() + 1), (double)blockPos.getZ() + 0.5D);
            var10 = getYOffset(level, blockPos, var8, entity.getBoundingBox());
         } else {
            var10 = 0.0D;
         }

         entity.moveTo((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + var10, (double)blockPos.getZ() + 0.5D, Mth.wrapDegrees(level.random.nextFloat() * 360.0F), 0.0F);
         if(entity instanceof Mob) {
            Mob var12 = (Mob)entity;
            var12.yHeadRot = var12.yRot;
            var12.yBodyRot = var12.yRot;
            var12.finalizeSpawn(level, level.getCurrentDifficultyAt(new BlockPos(var12)), mobSpawnType, (SpawnGroupData)null, compoundTag);
            var12.playAmbientSound();
         }

         if(component != null && entity instanceof LivingEntity) {
            entity.setCustomName(component);
         }

         updateCustomEntityTag(level, player, entity, compoundTag);
         return entity;
      }
   }

   protected static double getYOffset(LevelReader levelReader, BlockPos blockPos, boolean var2, AABB aABB) {
      AABB aABB = new AABB(blockPos);
      if(var2) {
         aABB = aABB.expandTowards(0.0D, -1.0D, 0.0D);
      }

      Stream<VoxelShape> var5 = levelReader.getCollisions((Entity)null, aABB, Collections.emptySet());
      return 1.0D + Shapes.collide(Direction.Axis.Y, aABB, var5, var2?-2.0D:-1.0D);
   }

   public static void updateCustomEntityTag(Level level, @Nullable Player player, @Nullable Entity entity, @Nullable CompoundTag compoundTag) {
      if(compoundTag != null && compoundTag.contains("EntityTag", 10)) {
         MinecraftServer var4 = level.getServer();
         if(var4 != null && entity != null) {
            if(level.isClientSide || !entity.onlyOpCanSetNbt() || player != null && var4.getPlayerList().isOp(player.getGameProfile())) {
               CompoundTag var5 = entity.saveWithoutId(new CompoundTag());
               UUID var6 = entity.getUUID();
               var5.merge(compoundTag.getCompound("EntityTag"));
               entity.setUUID(var6);
               entity.load(var5);
            }
         }
      }
   }

   public boolean canSerialize() {
      return this.serialize;
   }

   public boolean canSummon() {
      return this.summon;
   }

   public boolean fireImmune() {
      return this.fireImmune;
   }

   public boolean canSpawnFarFromPlayer() {
      return this.canSpawnFarFromPlayer;
   }

   public MobCategory getCategory() {
      return this.category;
   }

   public String getDescriptionId() {
      if(this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("entity", Registry.ENTITY_TYPE.getKey(this));
      }

      return this.descriptionId;
   }

   public Component getDescription() {
      if(this.description == null) {
         this.description = new TranslatableComponent(this.getDescriptionId(), new Object[0]);
      }

      return this.description;
   }

   public ResourceLocation getDefaultLootTable() {
      if(this.lootTable == null) {
         ResourceLocation resourceLocation = Registry.ENTITY_TYPE.getKey(this);
         this.lootTable = new ResourceLocation(resourceLocation.getNamespace(), "entities/" + resourceLocation.getPath());
      }

      return this.lootTable;
   }

   public float getWidth() {
      return this.dimensions.width;
   }

   public float getHeight() {
      return this.dimensions.height;
   }

   @Nullable
   public Entity create(Level level) {
      return this.factory.create(this, level);
   }

   @Nullable
   public static Entity create(int var0, Level level) {
      return create(level, (EntityType)Registry.ENTITY_TYPE.byId(var0));
   }

   public static Optional create(CompoundTag compoundTag, Level level) {
      return Util.ifElse(by(compoundTag).map((entityType) -> {
         return entityType.create(level);
      }), (entity) -> {
         entity.load(compoundTag);
      }, () -> {
         LOGGER.warn("Skipping Entity with id {}", compoundTag.getString("id"));
      });
   }

   @Nullable
   private static Entity create(Level level, @Nullable EntityType entityType) {
      return entityType == null?null:entityType.create(level);
   }

   public AABB getAABB(double var1, double var3, double var5) {
      float var7 = this.getWidth() / 2.0F;
      return new AABB(var1 - (double)var7, var3, var5 - (double)var7, var1 + (double)var7, var3 + (double)this.getHeight(), var5 + (double)var7);
   }

   public EntityDimensions getDimensions() {
      return this.dimensions;
   }

   public static Optional by(CompoundTag compoundTag) {
      return Registry.ENTITY_TYPE.getOptional(new ResourceLocation(compoundTag.getString("id")));
   }

   @Nullable
   public static Entity loadEntityRecursive(CompoundTag compoundTag, Level level, Function function) {
      return (Entity)loadStaticEntity(compoundTag, level).map(function).map((var3) -> {
         if(compoundTag.contains("Passengers", 9)) {
            ListTag var4 = compoundTag.getList("Passengers", 10);

            for(int var5 = 0; var5 < var4.size(); ++var5) {
               Entity var6 = loadEntityRecursive(var4.getCompound(var5), level, function);
               if(var6 != null) {
                  var6.startRiding(var3, true);
               }
            }
         }

         return var3;
      }).orElse((Object)null);
   }

   private static Optional loadStaticEntity(CompoundTag compoundTag, Level level) {
      try {
         return create(compoundTag, level);
      } catch (RuntimeException var3) {
         LOGGER.warn("Exception loading entity: ", var3);
         return Optional.empty();
      }
   }

   public int chunkRange() {
      return this == PLAYER?32:(this == END_CRYSTAL?16:(this != ENDER_DRAGON && this != TNT && this != FALLING_BLOCK && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != ARMOR_STAND && this != EXPERIENCE_ORB && this != AREA_EFFECT_CLOUD && this != EVOKER_FANGS?(this != FISHING_BOBBER && this != ARROW && this != SPECTRAL_ARROW && this != TRIDENT && this != SMALL_FIREBALL && this != DRAGON_FIREBALL && this != FIREBALL && this != WITHER_SKULL && this != SNOWBALL && this != LLAMA_SPIT && this != ENDER_PEARL && this != EYE_OF_ENDER && this != EGG && this != POTION && this != EXPERIENCE_BOTTLE && this != FIREWORK_ROCKET && this != ITEM?5:4):10));
   }

   public int updateInterval() {
      return this != PLAYER && this != EVOKER_FANGS?(this == EYE_OF_ENDER?4:(this == FISHING_BOBBER?5:(this != SMALL_FIREBALL && this != DRAGON_FIREBALL && this != FIREBALL && this != WITHER_SKULL && this != SNOWBALL && this != LLAMA_SPIT && this != ENDER_PEARL && this != EGG && this != POTION && this != EXPERIENCE_BOTTLE && this != FIREWORK_ROCKET && this != TNT?(this != ARROW && this != SPECTRAL_ARROW && this != TRIDENT && this != ITEM && this != FALLING_BLOCK && this != EXPERIENCE_ORB?(this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != AREA_EFFECT_CLOUD && this != END_CRYSTAL?3:Integer.MAX_VALUE):20):10))):2;
   }

   public boolean trackDeltas() {
      return this != PLAYER && this != LLAMA_SPIT && this != WITHER && this != BAT && this != ITEM_FRAME && this != LEASH_KNOT && this != PAINTING && this != END_CRYSTAL && this != EVOKER_FANGS;
   }

   public boolean is(Tag tag) {
      return tag.contains(this);
   }

   public static class Builder {
      private final EntityType.EntityFactory factory;
      private final MobCategory category;
      private boolean serialize = true;
      private boolean summon = true;
      private boolean fireImmune;
      private boolean canSpawnFarFromPlayer;
      private EntityDimensions dimensions = EntityDimensions.scalable(0.6F, 1.8F);

      private Builder(EntityType.EntityFactory factory, MobCategory category) {
         this.factory = factory;
         this.category = category;
         this.canSpawnFarFromPlayer = category == MobCategory.CREATURE || category == MobCategory.MISC;
      }

      public static EntityType.Builder of(EntityType.EntityFactory entityType$EntityFactory, MobCategory mobCategory) {
         return new EntityType.Builder(entityType$EntityFactory, mobCategory);
      }

      public static EntityType.Builder createNothing(MobCategory mobCategory) {
         return new EntityType.Builder((entityType, level) -> {
            return null;
         }, mobCategory);
      }

      public EntityType.Builder sized(float var1, float var2) {
         this.dimensions = EntityDimensions.scalable(var1, var2);
         return this;
      }

      public EntityType.Builder noSummon() {
         this.summon = false;
         return this;
      }

      public EntityType.Builder noSave() {
         this.serialize = false;
         return this;
      }

      public EntityType.Builder fireImmune() {
         this.fireImmune = true;
         return this;
      }

      public EntityType.Builder canSpawnFarFromPlayer() {
         this.canSpawnFarFromPlayer = true;
         return this;
      }

      public EntityType build(String string) {
         if(this.serialize) {
            try {
               DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).getChoiceType(References.ENTITY_TREE, string);
            } catch (IllegalStateException var3) {
               if(SharedConstants.IS_RUNNING_IN_IDE) {
                  throw var3;
               }

               EntityType.LOGGER.warn("No data fixer registered for entity {}", string);
            }
         }

         return new EntityType(this.factory, this.category, this.serialize, this.summon, this.fireImmune, this.canSpawnFarFromPlayer, this.dimensions);
      }
   }

   public interface EntityFactory {
      Entity create(EntityType var1, Level var2);
   }
}
