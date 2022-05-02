package net.minecraft.world.level.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.IdMapper;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block implements ItemLike {
   protected static final Logger LOGGER = LogManager.getLogger();
   public static final IdMapper BLOCK_STATE_REGISTRY = new IdMapper();
   private static final Direction[] UPDATE_SHAPE_ORDER = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};
   private static final LoadingCache SHAPE_FULL_BLOCK_CACHE = CacheBuilder.newBuilder().maximumSize(512L).weakKeys().build(new CacheLoader() {
      public Boolean load(VoxelShape voxelShape) {
         return Boolean.valueOf(!Shapes.joinIsNotEmpty(Shapes.block(), voxelShape, BooleanOp.NOT_SAME));
      }

      // $FF: synthetic method
      public Object load(Object var1) throws Exception {
         return this.load((VoxelShape)var1);
      }
   });
   private static final VoxelShape RIGID_SUPPORT_SHAPE = Shapes.join(Shapes.block(), box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D), BooleanOp.ONLY_FIRST);
   private static final VoxelShape CENTER_SUPPORT_SHAPE = box(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D);
   protected final int lightEmission;
   protected final float destroySpeed;
   protected final float explosionResistance;
   protected final boolean isTicking;
   protected final SoundType soundType;
   protected final Material material;
   protected final MaterialColor materialColor;
   private final float friction;
   protected final StateDefinition stateDefinition;
   private BlockState defaultBlockState;
   protected final boolean hasCollision;
   private final boolean dynamicShape;
   @Nullable
   private ResourceLocation drops;
   @Nullable
   private String descriptionId;
   @Nullable
   private Item item;
   private static final ThreadLocal OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
      Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2ByteLinkedOpenHashMap = new Object2ByteLinkedOpenHashMap(200) {
         protected void rehash(int i) {
         }
      };
      object2ByteLinkedOpenHashMap.defaultReturnValue((byte)127);
      return object2ByteLinkedOpenHashMap;
   });

   public static int getId(@Nullable BlockState blockState) {
      if(blockState == null) {
         return 0;
      } else {
         int var1 = BLOCK_STATE_REGISTRY.getId(blockState);
         return var1 == -1?0:var1;
      }
   }

   public static BlockState stateById(int i) {
      BlockState blockState = (BlockState)BLOCK_STATE_REGISTRY.byId(i);
      return blockState == null?Blocks.AIR.defaultBlockState():blockState;
   }

   public static Block byItem(@Nullable Item item) {
      return item instanceof BlockItem?((BlockItem)item).getBlock():Blocks.AIR;
   }

   public static BlockState pushEntitiesUp(BlockState var0, BlockState var1, Level level, BlockPos blockPos) {
      VoxelShape var4 = Shapes.joinUnoptimized(var0.getCollisionShape(level, blockPos), var1.getCollisionShape(level, blockPos), BooleanOp.ONLY_SECOND).move((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());

      for(Entity var7 : level.getEntities((Entity)null, var4.bounds())) {
         double var8 = Shapes.collide(Direction.Axis.Y, var7.getBoundingBox().move(0.0D, 1.0D, 0.0D), Stream.of(var4), -1.0D);
         var7.teleportTo(var7.x, var7.y + 1.0D + var8, var7.z);
      }

      return var1;
   }

   public static VoxelShape box(double var0, double var2, double var4, double var6, double var8, double var10) {
      return Shapes.box(var0 / 16.0D, var2 / 16.0D, var4 / 16.0D, var6 / 16.0D, var8 / 16.0D, var10 / 16.0D);
   }

   @Deprecated
   public boolean isValidSpawn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, EntityType entityType) {
      return blockState.isFaceSturdy(blockGetter, blockPos, Direction.UP) && this.lightEmission < 14;
   }

   @Deprecated
   public boolean isAir(BlockState blockState) {
      return false;
   }

   @Deprecated
   public int getLightEmission(BlockState blockState) {
      return this.lightEmission;
   }

   @Deprecated
   public Material getMaterial(BlockState blockState) {
      return this.material;
   }

   @Deprecated
   public MaterialColor getMapColor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return this.materialColor;
   }

   @Deprecated
   public void updateNeighbourShapes(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, int var4) {
      BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var6 = null;

      try {
         for(Direction var10 : UPDATE_SHAPE_ORDER) {
            var5.set((Vec3i)blockPos).move(var10);
            BlockState var11 = levelAccessor.getBlockState(var5);
            BlockState var12 = var11.updateShape(var10.getOpposite(), blockState, levelAccessor, var5, blockPos);
            updateOrDestroy(var11, var12, levelAccessor, var5, var4);
         }
      } catch (Throwable var20) {
         var6 = var20;
         throw var20;
      } finally {
         if(var5 != null) {
            if(var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var19) {
                  var6.addSuppressed(var19);
               }
            } else {
               var5.close();
            }
         }

      }

   }

   public boolean is(Tag tag) {
      return tag.contains(this);
   }

   public static BlockState updateFromNeighbourShapes(BlockState var0, LevelAccessor levelAccessor, BlockPos blockPos) {
      BlockState var3 = var0;
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();

      for(Direction var8 : UPDATE_SHAPE_ORDER) {
         var4.set((Vec3i)blockPos).move(var8);
         var3 = var3.updateShape(var8, levelAccessor.getBlockState(var4), levelAccessor, blockPos, var4);
      }

      return var3;
   }

   public static void updateOrDestroy(BlockState var0, BlockState var1, LevelAccessor levelAccessor, BlockPos blockPos, int var4) {
      if(var1 != var0) {
         if(var1.isAir()) {
            if(!levelAccessor.isClientSide()) {
               levelAccessor.destroyBlock(blockPos, (var4 & 32) == 0);
            }
         } else {
            levelAccessor.setBlock(blockPos, var1, var4 & -33);
         }
      }

   }

   @Deprecated
   public void updateIndirectNeighbourShapes(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, int var4) {
   }

   @Deprecated
   public BlockState updateShape(BlockState var1, Direction direction, BlockState var3, LevelAccessor levelAccessor, BlockPos var5, BlockPos var6) {
      return var1;
   }

   @Deprecated
   public BlockState rotate(BlockState var1, Rotation rotation) {
      return var1;
   }

   @Deprecated
   public BlockState mirror(BlockState var1, Mirror mirror) {
      return var1;
   }

   public Block(Block.Properties block$Properties) {
      StateDefinition.Builder<Block, BlockState> var2 = new StateDefinition.Builder(this);
      this.createBlockStateDefinition(var2);
      this.material = block$Properties.material;
      this.materialColor = block$Properties.materialColor;
      this.hasCollision = block$Properties.hasCollision;
      this.soundType = block$Properties.soundType;
      this.lightEmission = block$Properties.lightEmission;
      this.explosionResistance = block$Properties.explosionResistance;
      this.destroySpeed = block$Properties.destroyTime;
      this.isTicking = block$Properties.isTicking;
      this.friction = block$Properties.friction;
      this.dynamicShape = block$Properties.dynamicShape;
      this.drops = block$Properties.drops;
      this.stateDefinition = var2.create(BlockState::<init>);
      this.registerDefaultState((BlockState)this.stateDefinition.any());
   }

   public static boolean isExceptionForConnection(Block block) {
      return block instanceof LeavesBlock || block == Blocks.BARRIER || block == Blocks.CARVED_PUMPKIN || block == Blocks.JACK_O_LANTERN || block == Blocks.MELON || block == Blocks.PUMPKIN;
   }

   @Deprecated
   public boolean isRedstoneConductor(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.getMaterial().isSolidBlocking() && blockState.isCollisionShapeFullBlock(blockGetter, blockPos) && !blockState.isSignalSource();
   }

   @Deprecated
   public boolean isViewBlocking(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return this.material.blocksMotion() && blockState.isCollisionShapeFullBlock(blockGetter, blockPos);
   }

   @Deprecated
   public boolean hasCustomBreakingProgress(BlockState blockState) {
      return false;
   }

   @Deprecated
   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      switch(pathComputationType) {
      case LAND:
         return !blockState.isCollisionShapeFullBlock(blockGetter, blockPos);
      case WATER:
         return blockGetter.getFluidState(blockPos).is(FluidTags.WATER);
      case AIR:
         return !blockState.isCollisionShapeFullBlock(blockGetter, blockPos);
      default:
         return false;
      }
   }

   @Deprecated
   public RenderShape getRenderShape(BlockState blockState) {
      return RenderShape.MODEL;
   }

   @Deprecated
   public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
      return this.material.isReplaceable() && (blockPlaceContext.getItemInHand().isEmpty() || blockPlaceContext.getItemInHand().getItem() != this.asItem());
   }

   @Deprecated
   public float getDestroySpeed(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return this.destroySpeed;
   }

   public boolean isRandomlyTicking(BlockState blockState) {
      return this.isTicking;
   }

   public boolean isEntityBlock() {
      return this instanceof EntityBlock;
   }

   @Deprecated
   public boolean hasPostProcess(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return false;
   }

   @Deprecated
   public int getLightColor(BlockState blockState, BlockAndBiomeGetter blockAndBiomeGetter, BlockPos blockPos) {
      return blockAndBiomeGetter.getLightColor(blockPos, blockState.getLightEmission());
   }

   public static boolean shouldRenderFace(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      BlockPos blockPos = blockPos.relative(direction);
      BlockState var5 = blockGetter.getBlockState(blockPos);
      if(blockState.skipRendering(var5, direction)) {
         return false;
      } else if(var5.canOcclude()) {
         Block.BlockStatePairKey var6 = new Block.BlockStatePairKey(blockState, var5, direction);
         Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> var7 = (Object2ByteLinkedOpenHashMap)OCCLUSION_CACHE.get();
         byte var8 = var7.getAndMoveToFirst(var6);
         if(var8 != 127) {
            return var8 != 0;
         } else {
            VoxelShape var9 = blockState.getFaceOcclusionShape(blockGetter, blockPos, direction);
            VoxelShape var10 = var5.getFaceOcclusionShape(blockGetter, blockPos, direction.getOpposite());
            boolean var11 = Shapes.joinIsNotEmpty(var9, var10, BooleanOp.ONLY_FIRST);
            if(var7.size() == 200) {
               var7.removeLastByte();
            }

            var7.putAndMoveToFirst(var6, (byte)(var11?1:0));
            return var11;
         }
      } else {
         return true;
      }
   }

   @Deprecated
   public boolean canOcclude(BlockState blockState) {
      return this.hasCollision && this.getRenderLayer() == BlockLayer.SOLID;
   }

   @Deprecated
   public boolean skipRendering(BlockState var1, BlockState var2, Direction direction) {
      return false;
   }

   @Deprecated
   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return Shapes.block();
   }

   @Deprecated
   public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return this.hasCollision?blockState.getShape(blockGetter, blockPos):Shapes.empty();
   }

   @Deprecated
   public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.getShape(blockGetter, blockPos);
   }

   @Deprecated
   public VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return Shapes.empty();
   }

   public static boolean canSupportRigidBlock(BlockGetter blockGetter, BlockPos blockPos) {
      BlockState var2 = blockGetter.getBlockState(blockPos);
      return !var2.is(BlockTags.LEAVES) && !Shapes.joinIsNotEmpty(var2.getCollisionShape(blockGetter, blockPos).getFaceShape(Direction.UP), RIGID_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
   }

   public static boolean canSupportCenter(LevelReader levelReader, BlockPos blockPos, Direction direction) {
      BlockState var3 = levelReader.getBlockState(blockPos);
      return !var3.is(BlockTags.LEAVES) && !Shapes.joinIsNotEmpty(var3.getCollisionShape(levelReader, blockPos).getFaceShape(direction), CENTER_SUPPORT_SHAPE, BooleanOp.ONLY_SECOND);
   }

   public static boolean isFaceSturdy(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return !blockState.is(BlockTags.LEAVES) && isFaceFull(blockState.getCollisionShape(blockGetter, blockPos), direction);
   }

   public static boolean isFaceFull(VoxelShape voxelShape, Direction direction) {
      VoxelShape voxelShape = voxelShape.getFaceShape(direction);
      return isShapeFullBlock(voxelShape);
   }

   public static boolean isShapeFullBlock(VoxelShape voxelShape) {
      return ((Boolean)SHAPE_FULL_BLOCK_CACHE.getUnchecked(voxelShape)).booleanValue();
   }

   @Deprecated
   public final boolean isSolidRender(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.canOcclude()?isShapeFullBlock(blockState.getOcclusionShape(blockGetter, blockPos)):false;
   }

   public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return !isShapeFullBlock(blockState.getShape(blockGetter, blockPos)) && blockState.getFluidState().isEmpty();
   }

   @Deprecated
   public int getLightBlock(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.isSolidRender(blockGetter, blockPos)?blockGetter.getMaxLightLevel():(blockState.propagatesSkylightDown(blockGetter, blockPos)?0:1);
   }

   @Deprecated
   public boolean useShapeForLightOcclusion(BlockState blockState) {
      return false;
   }

   @Deprecated
   public void randomTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
      this.tick(blockState, level, blockPos, random);
   }

   @Deprecated
   public void tick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, Random random) {
   }

   public void destroy(LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState) {
   }

   @Deprecated
   public void neighborChanged(BlockState blockState, Level level, BlockPos var3, Block block, BlockPos var5, boolean var6) {
      DebugPackets.sendNeighborsUpdatePacket(level, var3);
   }

   public int getTickDelay(LevelReader levelReader) {
      return 10;
   }

   @Nullable
   @Deprecated
   public MenuProvider getMenuProvider(BlockState blockState, Level level, BlockPos blockPos) {
      return null;
   }

   @Deprecated
   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
   }

   @Deprecated
   public void onRemove(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(this.isEntityBlock() && var1.getBlock() != var4.getBlock()) {
         level.removeBlockEntity(blockPos);
      }

   }

   @Deprecated
   public float getDestroyProgress(BlockState blockState, Player player, BlockGetter blockGetter, BlockPos blockPos) {
      float var5 = blockState.getDestroySpeed(blockGetter, blockPos);
      if(var5 == -1.0F) {
         return 0.0F;
      } else {
         int var6 = player.canDestroy(blockState)?30:100;
         return player.getDestroySpeed(blockState) / var5 / (float)var6;
      }
   }

   @Deprecated
   public void spawnAfterBreak(BlockState blockState, Level level, BlockPos blockPos, ItemStack itemStack) {
   }

   public ResourceLocation getLootTable() {
      if(this.drops == null) {
         ResourceLocation resourceLocation = Registry.BLOCK.getKey(this);
         this.drops = new ResourceLocation(resourceLocation.getNamespace(), "blocks/" + resourceLocation.getPath());
      }

      return this.drops;
   }

   @Deprecated
   public List getDrops(BlockState blockState, LootContext.Builder lootContext$Builder) {
      ResourceLocation var3 = this.getLootTable();
      if(var3 == BuiltInLootTables.EMPTY) {
         return Collections.emptyList();
      } else {
         LootContext var4 = lootContext$Builder.withParameter(LootContextParams.BLOCK_STATE, blockState).create(LootContextParamSets.BLOCK);
         ServerLevel var5 = var4.getLevel();
         LootTable var6 = var5.getServer().getLootTables().get(var3);
         return var6.getRandomItems(var4);
      }
   }

   public static List getDrops(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
      LootContext.Builder var4 = (new LootContext.Builder(serverLevel)).withRandom(serverLevel.random).withParameter(LootContextParams.BLOCK_POS, blockPos).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
      return blockState.getDrops(var4);
   }

   public static List getDrops(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack itemStack) {
      LootContext.Builder var6 = (new LootContext.Builder(serverLevel)).withRandom(serverLevel.random).withParameter(LootContextParams.BLOCK_POS, blockPos).withParameter(LootContextParams.TOOL, itemStack).withParameter(LootContextParams.THIS_ENTITY, entity).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
      return blockState.getDrops(var6);
   }

   public static void dropResources(BlockState blockState, LootContext.Builder lootContext$Builder) {
      ServerLevel var2 = lootContext$Builder.getLevel();
      BlockPos var3 = (BlockPos)lootContext$Builder.getParameter(LootContextParams.BLOCK_POS);
      blockState.getDrops(lootContext$Builder).forEach((itemStack) -> {
         popResource(var2, var3, itemStack);
      });
      blockState.spawnAfterBreak(var2, var3, ItemStack.EMPTY);
   }

   public static void dropResources(BlockState blockState, Level level, BlockPos blockPos) {
      if(level instanceof ServerLevel) {
         getDrops(blockState, (ServerLevel)level, blockPos, (BlockEntity)null).forEach((itemStack) -> {
            popResource(level, blockPos, itemStack);
         });
      }

      blockState.spawnAfterBreak(level, blockPos, ItemStack.EMPTY);
   }

   public static void dropResources(BlockState blockState, Level level, BlockPos blockPos, @Nullable BlockEntity blockEntity) {
      if(level instanceof ServerLevel) {
         getDrops(blockState, (ServerLevel)level, blockPos, blockEntity).forEach((itemStack) -> {
            popResource(level, blockPos, itemStack);
         });
      }

      blockState.spawnAfterBreak(level, blockPos, ItemStack.EMPTY);
   }

   public static void dropResources(BlockState blockState, Level level, BlockPos blockPos, @Nullable BlockEntity blockEntity, Entity entity, ItemStack itemStack) {
      if(level instanceof ServerLevel) {
         getDrops(blockState, (ServerLevel)level, blockPos, blockEntity, entity, itemStack).forEach((itemStack) -> {
            popResource(level, blockPos, itemStack);
         });
      }

      blockState.spawnAfterBreak(level, blockPos, itemStack);
   }

   public static void popResource(Level level, BlockPos blockPos, ItemStack itemStack) {
      if(!level.isClientSide && !itemStack.isEmpty() && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
         float var3 = 0.5F;
         double var4 = (double)(level.random.nextFloat() * 0.5F) + 0.25D;
         double var6 = (double)(level.random.nextFloat() * 0.5F) + 0.25D;
         double var8 = (double)(level.random.nextFloat() * 0.5F) + 0.25D;
         ItemEntity var10 = new ItemEntity(level, (double)blockPos.getX() + var4, (double)blockPos.getY() + var6, (double)blockPos.getZ() + var8, itemStack);
         var10.setDefaultPickUpDelay();
         level.addFreshEntity(var10);
      }
   }

   protected void popExperience(Level level, BlockPos blockPos, int var3) {
      if(!level.isClientSide && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
         while(var3 > 0) {
            int var4 = ExperienceOrb.getExperienceValue(var3);
            var3 -= var4;
            level.addFreshEntity(new ExperienceOrb(level, (double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D, var4));
         }
      }

   }

   public float getExplosionResistance() {
      return this.explosionResistance;
   }

   public void wasExploded(Level level, BlockPos blockPos, Explosion explosion) {
   }

   public BlockLayer getRenderLayer() {
      return BlockLayer.SOLID;
   }

   @Deprecated
   public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return true;
   }

   @Deprecated
   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      return false;
   }

   public void stepOn(Level level, BlockPos blockPos, Entity entity) {
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return this.defaultBlockState();
   }

   @Deprecated
   public void attack(BlockState blockState, Level level, BlockPos blockPos, Player player) {
   }

   @Deprecated
   public int getSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return 0;
   }

   @Deprecated
   public boolean isSignalSource(BlockState blockState) {
      return false;
   }

   @Deprecated
   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
   }

   @Deprecated
   public int getDirectSignal(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, Direction direction) {
      return 0;
   }

   public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
      player.awardStat(Stats.BLOCK_MINED.get(this));
      player.causeFoodExhaustion(0.005F);
      dropResources(blockState, level, blockPos, blockEntity, player, itemStack);
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
   }

   public boolean isPossibleToRespawnInThis() {
      return !this.material.isSolid() && !this.material.isLiquid();
   }

   public Component getName() {
      return new TranslatableComponent(this.getDescriptionId(), new Object[0]);
   }

   public String getDescriptionId() {
      if(this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("block", Registry.BLOCK.getKey(this));
      }

      return this.descriptionId;
   }

   @Deprecated
   public boolean triggerEvent(BlockState blockState, Level level, BlockPos blockPos, int var4, int var5) {
      return false;
   }

   @Deprecated
   public PushReaction getPistonPushReaction(BlockState blockState) {
      return this.material.getPushReaction();
   }

   @Deprecated
   public float getShadeBrightness(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return blockState.isCollisionShapeFullBlock(blockGetter, blockPos)?0.2F:1.0F;
   }

   public void fallOn(Level level, BlockPos blockPos, Entity entity, float var4) {
      entity.causeFallDamage(var4, 1.0F);
   }

   public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
      entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D));
   }

   public ItemStack getCloneItemStack(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState) {
      return new ItemStack(this);
   }

   public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList nonNullList) {
      nonNullList.add(new ItemStack(this));
   }

   @Deprecated
   public FluidState getFluidState(BlockState blockState) {
      return Fluids.EMPTY.defaultFluidState();
   }

   public float getFriction() {
      return this.friction;
   }

   @Deprecated
   public long getSeed(BlockState blockState, BlockPos blockPos) {
      return Mth.getSeed(blockPos);
   }

   public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Entity entity) {
   }

   public void playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      level.levelEvent(player, 2001, blockPos, getId(blockState));
   }

   public void handleRain(Level level, BlockPos blockPos) {
   }

   public boolean dropFromExplosion(Explosion explosion) {
      return true;
   }

   @Deprecated
   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return false;
   }

   @Deprecated
   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      return 0;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
   }

   public StateDefinition getStateDefinition() {
      return this.stateDefinition;
   }

   protected final void registerDefaultState(BlockState defaultBlockState) {
      this.defaultBlockState = defaultBlockState;
   }

   public final BlockState defaultBlockState() {
      return this.defaultBlockState;
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.NONE;
   }

   @Deprecated
   public Vec3 getOffset(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      Block.OffsetType var4 = this.getOffsetType();
      if(var4 == Block.OffsetType.NONE) {
         return Vec3.ZERO;
      } else {
         long var5 = Mth.getSeed(blockPos.getX(), 0, blockPos.getZ());
         return new Vec3(((double)((float)(var5 & 15L) / 15.0F) - 0.5D) * 0.5D, var4 == Block.OffsetType.XYZ?((double)((float)(var5 >> 4 & 15L) / 15.0F) - 1.0D) * 0.2D:0.0D, ((double)((float)(var5 >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
      }
   }

   public SoundType getSoundType(BlockState blockState) {
      return this.soundType;
   }

   public Item asItem() {
      if(this.item == null) {
         this.item = Item.byBlock(this);
      }

      return this.item;
   }

   public boolean hasDynamicShape() {
      return this.dynamicShape;
   }

   public String toString() {
      return "Block{" + Registry.BLOCK.getKey(this) + "}";
   }

   public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter blockGetter, List list, TooltipFlag tooltipFlag) {
   }

   public static boolean equalsStone(Block block) {
      return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
   }

   public static boolean equalsDirt(Block block) {
      return block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL;
   }

   public static final class BlockStatePairKey {
      private final BlockState first;
      private final BlockState second;
      private final Direction direction;

      public BlockStatePairKey(BlockState first, BlockState second, Direction direction) {
         this.first = first;
         this.second = second;
         this.direction = direction;
      }

      public boolean equals(Object object) {
         if(this == object) {
            return true;
         } else if(!(object instanceof Block.BlockStatePairKey)) {
            return false;
         } else {
            Block.BlockStatePairKey var2 = (Block.BlockStatePairKey)object;
            return this.first == var2.first && this.second == var2.second && this.direction == var2.direction;
         }
      }

      public int hashCode() {
         int var1 = this.first.hashCode();
         var1 = 31 * var1 + this.second.hashCode();
         var1 = 31 * var1 + this.direction.hashCode();
         return var1;
      }
   }

   public static enum OffsetType {
      NONE,
      XZ,
      XYZ;
   }

   public static class Properties {
      private Material material;
      private MaterialColor materialColor;
      private boolean hasCollision = true;
      private SoundType soundType = SoundType.STONE;
      private int lightEmission;
      private float explosionResistance;
      private float destroyTime;
      private boolean isTicking;
      private float friction = 0.6F;
      private ResourceLocation drops;
      private boolean dynamicShape;

      private Properties(Material material, MaterialColor materialColor) {
         this.material = material;
         this.materialColor = materialColor;
      }

      public static Block.Properties of(Material material) {
         return of(material, material.getColor());
      }

      public static Block.Properties of(Material material, DyeColor dyeColor) {
         return of(material, dyeColor.getMaterialColor());
      }

      public static Block.Properties of(Material material, MaterialColor materialColor) {
         return new Block.Properties(material, materialColor);
      }

      public static Block.Properties copy(Block block) {
         Block.Properties block$Properties = new Block.Properties(block.material, block.materialColor);
         block$Properties.material = block.material;
         block$Properties.destroyTime = block.destroySpeed;
         block$Properties.explosionResistance = block.explosionResistance;
         block$Properties.hasCollision = block.hasCollision;
         block$Properties.isTicking = block.isTicking;
         block$Properties.lightEmission = block.lightEmission;
         block$Properties.materialColor = block.materialColor;
         block$Properties.soundType = block.soundType;
         block$Properties.friction = block.getFriction();
         block$Properties.dynamicShape = block.dynamicShape;
         return block$Properties;
      }

      public Block.Properties noCollission() {
         this.hasCollision = false;
         return this;
      }

      public Block.Properties friction(float friction) {
         this.friction = friction;
         return this;
      }

      protected Block.Properties sound(SoundType soundType) {
         this.soundType = soundType;
         return this;
      }

      protected Block.Properties lightLevel(int lightEmission) {
         this.lightEmission = lightEmission;
         return this;
      }

      public Block.Properties strength(float destroyTime, float var2) {
         this.destroyTime = destroyTime;
         this.explosionResistance = Math.max(0.0F, var2);
         return this;
      }

      protected Block.Properties instabreak() {
         return this.strength(0.0F);
      }

      protected Block.Properties strength(float f) {
         this.strength(f, f);
         return this;
      }

      protected Block.Properties randomTicks() {
         this.isTicking = true;
         return this;
      }

      protected Block.Properties dynamicShape() {
         this.dynamicShape = true;
         return this;
      }

      protected Block.Properties noDrops() {
         this.drops = BuiltInLootTables.EMPTY;
         return this;
      }

      public Block.Properties dropsLike(Block block) {
         this.drops = block.getLootTable();
         return this;
      }
   }
}
