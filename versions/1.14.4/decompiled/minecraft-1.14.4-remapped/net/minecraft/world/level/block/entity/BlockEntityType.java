package net.minecraft.world.level.block.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.ComparatorBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockEntityType {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final BlockEntityType FURNACE = register("furnace", BlockEntityType.Builder.of(FurnaceBlockEntity::<init>, new Block[]{Blocks.FURNACE}));
   public static final BlockEntityType CHEST = register("chest", BlockEntityType.Builder.of(ChestBlockEntity::<init>, new Block[]{Blocks.CHEST}));
   public static final BlockEntityType TRAPPED_CHEST = register("trapped_chest", BlockEntityType.Builder.of(TrappedChestBlockEntity::<init>, new Block[]{Blocks.TRAPPED_CHEST}));
   public static final BlockEntityType ENDER_CHEST = register("ender_chest", BlockEntityType.Builder.of(EnderChestBlockEntity::<init>, new Block[]{Blocks.ENDER_CHEST}));
   public static final BlockEntityType JUKEBOX = register("jukebox", BlockEntityType.Builder.of(JukeboxBlockEntity::<init>, new Block[]{Blocks.JUKEBOX}));
   public static final BlockEntityType DISPENSER = register("dispenser", BlockEntityType.Builder.of(DispenserBlockEntity::<init>, new Block[]{Blocks.DISPENSER}));
   public static final BlockEntityType DROPPER = register("dropper", BlockEntityType.Builder.of(DropperBlockEntity::<init>, new Block[]{Blocks.DROPPER}));
   public static final BlockEntityType SIGN = register("sign", BlockEntityType.Builder.of(SignBlockEntity::<init>, new Block[]{Blocks.OAK_SIGN, Blocks.SPRUCE_SIGN, Blocks.BIRCH_SIGN, Blocks.ACACIA_SIGN, Blocks.JUNGLE_SIGN, Blocks.DARK_OAK_SIGN, Blocks.OAK_WALL_SIGN, Blocks.SPRUCE_WALL_SIGN, Blocks.BIRCH_WALL_SIGN, Blocks.ACACIA_WALL_SIGN, Blocks.JUNGLE_WALL_SIGN, Blocks.DARK_OAK_WALL_SIGN}));
   public static final BlockEntityType MOB_SPAWNER = register("mob_spawner", BlockEntityType.Builder.of(SpawnerBlockEntity::<init>, new Block[]{Blocks.SPAWNER}));
   public static final BlockEntityType PISTON = register("piston", BlockEntityType.Builder.of(PistonMovingBlockEntity::<init>, new Block[]{Blocks.MOVING_PISTON}));
   public static final BlockEntityType BREWING_STAND = register("brewing_stand", BlockEntityType.Builder.of(BrewingStandBlockEntity::<init>, new Block[]{Blocks.BREWING_STAND}));
   public static final BlockEntityType ENCHANTING_TABLE = register("enchanting_table", BlockEntityType.Builder.of(EnchantmentTableBlockEntity::<init>, new Block[]{Blocks.ENCHANTING_TABLE}));
   public static final BlockEntityType END_PORTAL = register("end_portal", BlockEntityType.Builder.of(TheEndPortalBlockEntity::<init>, new Block[]{Blocks.END_PORTAL}));
   public static final BlockEntityType BEACON = register("beacon", BlockEntityType.Builder.of(BeaconBlockEntity::<init>, new Block[]{Blocks.BEACON}));
   public static final BlockEntityType SKULL = register("skull", BlockEntityType.Builder.of(SkullBlockEntity::<init>, new Block[]{Blocks.SKELETON_SKULL, Blocks.SKELETON_WALL_SKULL, Blocks.CREEPER_HEAD, Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.ZOMBIE_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.WITHER_SKELETON_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PLAYER_HEAD, Blocks.PLAYER_WALL_HEAD}));
   public static final BlockEntityType DAYLIGHT_DETECTOR = register("daylight_detector", BlockEntityType.Builder.of(DaylightDetectorBlockEntity::<init>, new Block[]{Blocks.DAYLIGHT_DETECTOR}));
   public static final BlockEntityType HOPPER = register("hopper", BlockEntityType.Builder.of(HopperBlockEntity::<init>, new Block[]{Blocks.HOPPER}));
   public static final BlockEntityType COMPARATOR = register("comparator", BlockEntityType.Builder.of(ComparatorBlockEntity::<init>, new Block[]{Blocks.COMPARATOR}));
   public static final BlockEntityType BANNER = register("banner", BlockEntityType.Builder.of(BannerBlockEntity::<init>, new Block[]{Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER, Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER}));
   public static final BlockEntityType STRUCTURE_BLOCK = register("structure_block", BlockEntityType.Builder.of(StructureBlockEntity::<init>, new Block[]{Blocks.STRUCTURE_BLOCK}));
   public static final BlockEntityType END_GATEWAY = register("end_gateway", BlockEntityType.Builder.of(TheEndGatewayBlockEntity::<init>, new Block[]{Blocks.END_GATEWAY}));
   public static final BlockEntityType COMMAND_BLOCK = register("command_block", BlockEntityType.Builder.of(CommandBlockEntity::<init>, new Block[]{Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK}));
   public static final BlockEntityType SHULKER_BOX = register("shulker_box", BlockEntityType.Builder.of(ShulkerBoxBlockEntity::<init>, new Block[]{Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX}));
   public static final BlockEntityType BED = register("bed", BlockEntityType.Builder.of(BedBlockEntity::<init>, new Block[]{Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}));
   public static final BlockEntityType CONDUIT = register("conduit", BlockEntityType.Builder.of(ConduitBlockEntity::<init>, new Block[]{Blocks.CONDUIT}));
   public static final BlockEntityType BARREL = register("barrel", BlockEntityType.Builder.of(BarrelBlockEntity::<init>, new Block[]{Blocks.BARREL}));
   public static final BlockEntityType SMOKER = register("smoker", BlockEntityType.Builder.of(SmokerBlockEntity::<init>, new Block[]{Blocks.SMOKER}));
   public static final BlockEntityType BLAST_FURNACE = register("blast_furnace", BlockEntityType.Builder.of(BlastFurnaceBlockEntity::<init>, new Block[]{Blocks.BLAST_FURNACE}));
   public static final BlockEntityType LECTERN = register("lectern", BlockEntityType.Builder.of(LecternBlockEntity::<init>, new Block[]{Blocks.LECTERN}));
   public static final BlockEntityType BELL = register("bell", BlockEntityType.Builder.of(BellBlockEntity::<init>, new Block[]{Blocks.BELL}));
   public static final BlockEntityType JIGSAW = register("jigsaw", BlockEntityType.Builder.of(JigsawBlockEntity::<init>, new Block[]{Blocks.JIGSAW_BLOCK}));
   public static final BlockEntityType CAMPFIRE = register("campfire", BlockEntityType.Builder.of(CampfireBlockEntity::<init>, new Block[]{Blocks.CAMPFIRE}));
   private final Supplier factory;
   private final Set validBlocks;
   private final Type dataType;

   @Nullable
   public static ResourceLocation getKey(BlockEntityType blockEntityType) {
      return Registry.BLOCK_ENTITY_TYPE.getKey(blockEntityType);
   }

   private static BlockEntityType register(String string, BlockEntityType.Builder blockEntityType$Builder) {
      Type<?> var2 = null;

      try {
         var2 = DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey(SharedConstants.getCurrentVersion().getWorldVersion())).getChoiceType(References.BLOCK_ENTITY, string);
      } catch (IllegalStateException var4) {
         if(SharedConstants.IS_RUNNING_IN_IDE) {
            throw var4;
         }

         LOGGER.warn("No data fixer registered for block entity {}", string);
      }

      if(blockEntityType$Builder.validBlocks.isEmpty()) {
         LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", string);
      }

      return (BlockEntityType)Registry.register(Registry.BLOCK_ENTITY_TYPE, (String)string, blockEntityType$Builder.build(var2));
   }

   public BlockEntityType(Supplier factory, Set validBlocks, Type dataType) {
      this.factory = factory;
      this.validBlocks = validBlocks;
      this.dataType = dataType;
   }

   @Nullable
   public BlockEntity create() {
      return (BlockEntity)this.factory.get();
   }

   public boolean isValid(Block block) {
      return this.validBlocks.contains(block);
   }

   public static final class Builder {
      private final Supplier factory;
      private final Set validBlocks;

      private Builder(Supplier factory, Set validBlocks) {
         this.factory = factory;
         this.validBlocks = validBlocks;
      }

      public static BlockEntityType.Builder of(Supplier supplier, Block... blocks) {
         return new BlockEntityType.Builder(supplier, ImmutableSet.copyOf(blocks));
      }

      public BlockEntityType build(Type type) {
         return new BlockEntityType(this.factory, this.validBlocks, type);
      }
   }
}
