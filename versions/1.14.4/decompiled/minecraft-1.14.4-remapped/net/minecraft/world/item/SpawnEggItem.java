package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class SpawnEggItem extends Item {
   private static final Map BY_ID = Maps.newIdentityHashMap();
   private final int color1;
   private final int color2;
   private final EntityType defaultType;

   public SpawnEggItem(EntityType defaultType, int color1, int color2, Item.Properties item$Properties) {
      super(item$Properties);
      this.defaultType = defaultType;
      this.color1 = color1;
      this.color2 = color2;
      BY_ID.put(defaultType, this);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level var2 = useOnContext.getLevel();
      if(var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         ItemStack var3 = useOnContext.getItemInHand();
         BlockPos var4 = useOnContext.getClickedPos();
         Direction var5 = useOnContext.getClickedFace();
         BlockState var6 = var2.getBlockState(var4);
         Block var7 = var6.getBlock();
         if(var7 == Blocks.SPAWNER) {
            BlockEntity var8 = var2.getBlockEntity(var4);
            if(var8 instanceof SpawnerBlockEntity) {
               BaseSpawner var9 = ((SpawnerBlockEntity)var8).getSpawner();
               EntityType<?> var10 = this.getType(var3.getTag());
               var9.setEntityId(var10);
               var8.setChanged();
               var2.sendBlockUpdated(var4, var6, var6, 3);
               var3.shrink(1);
               return InteractionResult.SUCCESS;
            }
         }

         BlockPos var8;
         if(var6.getCollisionShape(var2, var4).isEmpty()) {
            var8 = var4;
         } else {
            var8 = var4.relative(var5);
         }

         EntityType<?> var9 = this.getType(var3.getTag());
         if(var9.spawn(var2, var3, useOnContext.getPlayer(), var8, MobSpawnType.SPAWN_EGG, true, !Objects.equals(var4, var8) && var5 == Direction.UP) != null) {
            var3.shrink(1);
         }

         return InteractionResult.SUCCESS;
      }
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      if(level.isClientSide) {
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      } else {
         HitResult var5 = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
         if(var5.getType() != HitResult.Type.BLOCK) {
            return new InteractionResultHolder(InteractionResult.PASS, var4);
         } else {
            BlockHitResult var6 = (BlockHitResult)var5;
            BlockPos var7 = var6.getBlockPos();
            if(!(level.getBlockState(var7).getBlock() instanceof LiquidBlock)) {
               return new InteractionResultHolder(InteractionResult.PASS, var4);
            } else if(level.mayInteract(player, var7) && player.mayUseItemAt(var7, var6.getDirection(), var4)) {
               EntityType<?> var8 = this.getType(var4.getTag());
               if(var8.spawn(level, var4, player, var7, MobSpawnType.SPAWN_EGG, false, false) == null) {
                  return new InteractionResultHolder(InteractionResult.PASS, var4);
               } else {
                  if(!player.abilities.instabuild) {
                     var4.shrink(1);
                  }

                  player.awardStat(Stats.ITEM_USED.get(this));
                  return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
               }
            } else {
               return new InteractionResultHolder(InteractionResult.FAIL, var4);
            }
         }
      }
   }

   public boolean spawnsEntity(@Nullable CompoundTag compoundTag, EntityType entityType) {
      return Objects.equals(this.getType(compoundTag), entityType);
   }

   public int getColor(int i) {
      return i == 0?this.color1:this.color2;
   }

   public static SpawnEggItem byId(@Nullable EntityType id) {
      return (SpawnEggItem)BY_ID.get(id);
   }

   public static Iterable eggs() {
      return Iterables.unmodifiableIterable(BY_ID.values());
   }

   public EntityType getType(@Nullable CompoundTag compoundTag) {
      if(compoundTag != null && compoundTag.contains("EntityTag", 10)) {
         CompoundTag compoundTag = compoundTag.getCompound("EntityTag");
         if(compoundTag.contains("id", 8)) {
            return (EntityType)EntityType.byString(compoundTag.getString("id")).orElse(this.defaultType);
         }
      }

      return this.defaultType;
   }
}
