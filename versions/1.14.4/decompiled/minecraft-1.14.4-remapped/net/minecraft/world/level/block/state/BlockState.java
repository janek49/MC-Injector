package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.AbstractStateHolder;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockState extends AbstractStateHolder implements StateHolder {
   @Nullable
   private BlockState.Cache cache;
   private final int lightEmission;
   private final boolean useShapeForLightOcclusion;

   public BlockState(Block block, ImmutableMap immutableMap) {
      super(block, immutableMap);
      this.lightEmission = block.getLightEmission(this);
      this.useShapeForLightOcclusion = block.useShapeForLightOcclusion(this);
   }

   public void initCache() {
      if(!this.getBlock().hasDynamicShape()) {
         this.cache = new BlockState.Cache(this);
      }

   }

   public Block getBlock() {
      return (Block)this.owner;
   }

   public Material getMaterial() {
      return this.getBlock().getMaterial(this);
   }

   public boolean isValidSpawn(BlockGetter blockGetter, BlockPos blockPos, EntityType entityType) {
      return this.getBlock().isValidSpawn(this, blockGetter, blockPos, entityType);
   }

   public boolean propagatesSkylightDown(BlockGetter blockGetter, BlockPos blockPos) {
      return this.cache != null?this.cache.propagatesSkylightDown:this.getBlock().propagatesSkylightDown(this, blockGetter, blockPos);
   }

   public int getLightBlock(BlockGetter blockGetter, BlockPos blockPos) {
      return this.cache != null?this.cache.lightBlock:this.getBlock().getLightBlock(this, blockGetter, blockPos);
   }

   public VoxelShape getFaceOcclusionShape(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return this.cache != null && this.cache.occlusionShapes != null?this.cache.occlusionShapes[direction.ordinal()]:Shapes.getFaceShape(this.getOcclusionShape(blockGetter, blockPos), direction);
   }

   public boolean hasLargeCollisionShape() {
      return this.cache == null || this.cache.largeCollisionShape;
   }

   public boolean useShapeForLightOcclusion() {
      return this.useShapeForLightOcclusion;
   }

   public int getLightEmission() {
      return this.lightEmission;
   }

   public boolean isAir() {
      return this.getBlock().isAir(this);
   }

   public MaterialColor getMapColor(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getBlock().getMapColor(this, blockGetter, blockPos);
   }

   public BlockState rotate(Rotation rotation) {
      return this.getBlock().rotate(this, rotation);
   }

   public BlockState mirror(Mirror mirror) {
      return this.getBlock().mirror(this, mirror);
   }

   public boolean hasCustomBreakingProgress() {
      return this.getBlock().hasCustomBreakingProgress(this);
   }

   public RenderShape getRenderShape() {
      return this.getBlock().getRenderShape(this);
   }

   public int getLightColor(BlockAndBiomeGetter blockAndBiomeGetter, BlockPos blockPos) {
      return this.getBlock().getLightColor(this, blockAndBiomeGetter, blockPos);
   }

   public float getShadeBrightness(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getBlock().getShadeBrightness(this, blockGetter, blockPos);
   }

   public boolean isRedstoneConductor(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getBlock().isRedstoneConductor(this, blockGetter, blockPos);
   }

   public boolean isSignalSource() {
      return this.getBlock().isSignalSource(this);
   }

   public int getSignal(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return this.getBlock().getSignal(this, blockGetter, blockPos, direction);
   }

   public boolean hasAnalogOutputSignal() {
      return this.getBlock().hasAnalogOutputSignal(this);
   }

   public int getAnalogOutputSignal(Level level, BlockPos blockPos) {
      return this.getBlock().getAnalogOutputSignal(this, level, blockPos);
   }

   public float getDestroySpeed(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getBlock().getDestroySpeed(this, blockGetter, blockPos);
   }

   public float getDestroyProgress(Player player, BlockGetter blockGetter, BlockPos blockPos) {
      return this.getBlock().getDestroyProgress(this, player, blockGetter, blockPos);
   }

   public int getDirectSignal(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return this.getBlock().getDirectSignal(this, blockGetter, blockPos, direction);
   }

   public PushReaction getPistonPushReaction() {
      return this.getBlock().getPistonPushReaction(this);
   }

   public boolean isSolidRender(BlockGetter blockGetter, BlockPos blockPos) {
      return this.cache != null?this.cache.solidRender:this.getBlock().isSolidRender(this, blockGetter, blockPos);
   }

   public boolean canOcclude() {
      return this.cache != null?this.cache.canOcclude:this.getBlock().canOcclude(this);
   }

   public boolean skipRendering(BlockState blockState, Direction direction) {
      return this.getBlock().skipRendering(this, blockState, direction);
   }

   public VoxelShape getShape(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getShape(blockGetter, blockPos, CollisionContext.empty());
   }

   public VoxelShape getShape(BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return this.getBlock().getShape(this, blockGetter, blockPos, collisionContext);
   }

   public VoxelShape getCollisionShape(BlockGetter blockGetter, BlockPos blockPos) {
      return this.cache != null?this.cache.collisionShape:this.getCollisionShape(blockGetter, blockPos, CollisionContext.empty());
   }

   public VoxelShape getCollisionShape(BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return this.getBlock().getCollisionShape(this, blockGetter, blockPos, collisionContext);
   }

   public VoxelShape getOcclusionShape(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getBlock().getOcclusionShape(this, blockGetter, blockPos);
   }

   public VoxelShape getInteractionShape(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getBlock().getInteractionShape(this, blockGetter, blockPos);
   }

   public final boolean entityCanStandOn(BlockGetter blockGetter, BlockPos blockPos, Entity entity) {
      return Block.isFaceFull(this.getCollisionShape(blockGetter, blockPos, CollisionContext.of(entity)), Direction.UP);
   }

   public Vec3 getOffset(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getBlock().getOffset(this, blockGetter, blockPos);
   }

   public boolean triggerEvent(Level level, BlockPos blockPos, int var3, int var4) {
      return this.getBlock().triggerEvent(this, level, blockPos, var3, var4);
   }

   public void neighborChanged(Level level, BlockPos var2, Block block, BlockPos var4, boolean var5) {
      this.getBlock().neighborChanged(this, level, var2, block, var4, var5);
   }

   public void updateNeighbourShapes(LevelAccessor levelAccessor, BlockPos blockPos, int var3) {
      this.getBlock().updateNeighbourShapes(this, levelAccessor, blockPos, var3);
   }

   public void updateIndirectNeighbourShapes(LevelAccessor levelAccessor, BlockPos blockPos, int var3) {
      this.getBlock().updateIndirectNeighbourShapes(this, levelAccessor, blockPos, var3);
   }

   public void onPlace(Level level, BlockPos blockPos, BlockState blockState, boolean var4) {
      this.getBlock().onPlace(this, level, blockPos, blockState, var4);
   }

   public void onRemove(Level level, BlockPos blockPos, BlockState blockState, boolean var4) {
      this.getBlock().onRemove(this, level, blockPos, blockState, var4);
   }

   public void tick(Level level, BlockPos blockPos, Random random) {
      this.getBlock().tick(this, level, blockPos, random);
   }

   public void randomTick(Level level, BlockPos blockPos, Random random) {
      this.getBlock().randomTick(this, level, blockPos, random);
   }

   public void entityInside(Level level, BlockPos blockPos, Entity entity) {
      this.getBlock().entityInside(this, level, blockPos, entity);
   }

   public void spawnAfterBreak(Level level, BlockPos blockPos, ItemStack itemStack) {
      this.getBlock().spawnAfterBreak(this, level, blockPos, itemStack);
   }

   public List getDrops(LootContext.Builder lootContext$Builder) {
      return this.getBlock().getDrops(this, lootContext$Builder);
   }

   public boolean use(Level level, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      return this.getBlock().use(this, level, blockHitResult.getBlockPos(), player, interactionHand, blockHitResult);
   }

   public void attack(Level level, BlockPos blockPos, Player player) {
      this.getBlock().attack(this, level, blockPos, player);
   }

   public boolean isViewBlocking(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getBlock().isViewBlocking(this, blockGetter, blockPos);
   }

   public BlockState updateShape(Direction direction, BlockState var2, LevelAccessor levelAccessor, BlockPos var4, BlockPos var5) {
      return this.getBlock().updateShape(this, direction, var2, levelAccessor, var4, var5);
   }

   public boolean isPathfindable(BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return this.getBlock().isPathfindable(this, blockGetter, blockPos, pathComputationType);
   }

   public boolean canBeReplaced(BlockPlaceContext blockPlaceContext) {
      return this.getBlock().canBeReplaced(this, blockPlaceContext);
   }

   public boolean canSurvive(LevelReader levelReader, BlockPos blockPos) {
      return this.getBlock().canSurvive(this, levelReader, blockPos);
   }

   public boolean hasPostProcess(BlockGetter blockGetter, BlockPos blockPos) {
      return this.getBlock().hasPostProcess(this, blockGetter, blockPos);
   }

   @Nullable
   public MenuProvider getMenuProvider(Level level, BlockPos blockPos) {
      return this.getBlock().getMenuProvider(this, level, blockPos);
   }

   public boolean is(Tag tag) {
      return this.getBlock().is(tag);
   }

   public FluidState getFluidState() {
      return this.getBlock().getFluidState(this);
   }

   public boolean isRandomlyTicking() {
      return this.getBlock().isRandomlyTicking(this);
   }

   public long getSeed(BlockPos blockPos) {
      return this.getBlock().getSeed(this, blockPos);
   }

   public SoundType getSoundType() {
      return this.getBlock().getSoundType(this);
   }

   public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
      this.getBlock().onProjectileHit(level, blockState, blockHitResult, entity);
   }

   public boolean isFaceSturdy(BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return this.cache != null?this.cache.isFaceSturdy[direction.ordinal()]:Block.isFaceSturdy(this, blockGetter, blockPos, direction);
   }

   public boolean isCollisionShapeFullBlock(BlockGetter blockGetter, BlockPos blockPos) {
      return this.cache != null?this.cache.isCollisionShapeFullBlock:Block.isShapeFullBlock(this.getCollisionShape(blockGetter, blockPos));
   }

   public static Dynamic serialize(DynamicOps dynamicOps, BlockState blockState) {
      ImmutableMap<Property<?>, Comparable<?>> var2 = blockState.getValues();
      T var3;
      if(var2.isEmpty()) {
         var3 = dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.BLOCK.getKey(blockState.getBlock()).toString())));
      } else {
         var3 = dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("Name"), dynamicOps.createString(Registry.BLOCK.getKey(blockState.getBlock()).toString()), dynamicOps.createString("Properties"), dynamicOps.createMap((Map)var2.entrySet().stream().map((map$Entry) -> {
            return Pair.of(dynamicOps.createString(((Property)map$Entry.getKey()).getName()), dynamicOps.createString(StateHolder.getName((Property)map$Entry.getKey(), (Comparable)map$Entry.getValue())));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
      }

      return new Dynamic(dynamicOps, var3);
   }

   public static BlockState deserialize(Dynamic dynamic) {
      DefaultedRegistry var10000 = Registry.BLOCK;
      Optional var10003 = dynamic.getElement("Name");
      DynamicOps var10004 = dynamic.getOps();
      var10004.getClass();
      Block var1 = (Block)var10000.get(new ResourceLocation((String)var10003.flatMap(var10004::getStringValue).orElse("minecraft:air")));
      Map<String, String> var2 = dynamic.get("Properties").asMap((dynamic) -> {
         return dynamic.asString("");
      }, (dynamic) -> {
         return dynamic.asString("");
      });
      BlockState var3 = var1.defaultBlockState();
      StateDefinition<Block, BlockState> var4 = var1.getStateDefinition();

      for(Entry<String, String> var6 : var2.entrySet()) {
         String var7 = (String)var6.getKey();
         Property<?> var8 = var4.getProperty(var7);
         if(var8 != null) {
            var3 = (BlockState)StateHolder.setValueHelper(var3, var8, var7, dynamic.toString(), (String)var6.getValue());
         }
      }

      return var3;
   }

   static final class Cache {
      private static final Direction[] DIRECTIONS = Direction.values();
      private final boolean canOcclude;
      private final boolean solidRender;
      private final boolean propagatesSkylightDown;
      private final int lightBlock;
      private final VoxelShape[] occlusionShapes;
      private final VoxelShape collisionShape;
      private final boolean largeCollisionShape;
      private final boolean[] isFaceSturdy;
      private final boolean isCollisionShapeFullBlock;

      private Cache(BlockState blockState) {
         Block var2 = blockState.getBlock();
         this.canOcclude = var2.canOcclude(blockState);
         this.solidRender = var2.isSolidRender(blockState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
         this.propagatesSkylightDown = var2.propagatesSkylightDown(blockState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
         this.lightBlock = var2.getLightBlock(blockState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
         if(!blockState.canOcclude()) {
            this.occlusionShapes = null;
         } else {
            this.occlusionShapes = new VoxelShape[DIRECTIONS.length];
            VoxelShape var3 = var2.getOcclusionShape(blockState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO);

            for(Direction var7 : DIRECTIONS) {
               this.occlusionShapes[var7.ordinal()] = Shapes.getFaceShape(var3, var7);
            }
         }

         this.collisionShape = var2.getCollisionShape(blockState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());
         this.largeCollisionShape = Arrays.stream(Direction.Axis.values()).anyMatch((direction$Axis) -> {
            return this.collisionShape.min(direction$Axis) < 0.0D || this.collisionShape.max(direction$Axis) > 1.0D;
         });
         this.isFaceSturdy = new boolean[6];

         for(Direction var6 : DIRECTIONS) {
            this.isFaceSturdy[var6.ordinal()] = Block.isFaceSturdy(blockState, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, var6);
         }

         this.isCollisionShapeFullBlock = Block.isShapeFullBlock(blockState.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
      }
   }
}
