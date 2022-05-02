package net.minecraft.core.dispenser;

import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.BoatDispenseItemBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ShulkerBoxDispenseBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.WitherSkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public interface DispenseItemBehavior {
   DispenseItemBehavior NOOP = (blockSource, var1) -> {
      return var1;
   };

   ItemStack dispense(BlockSource var1, ItemStack var2);

   static default void bootStrap() {
      DispenserBlock.registerBehavior(Items.ARROW, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
            Arrow var4 = new Arrow(level, position.x(), position.y(), position.z());
            var4.pickup = AbstractArrow.Pickup.ALLOWED;
            return var4;
         }
      });
      DispenserBlock.registerBehavior(Items.TIPPED_ARROW, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
            Arrow var4 = new Arrow(level, position.x(), position.y(), position.z());
            var4.setEffectsFromItem(itemStack);
            var4.pickup = AbstractArrow.Pickup.ALLOWED;
            return var4;
         }
      });
      DispenserBlock.registerBehavior(Items.SPECTRAL_ARROW, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
            AbstractArrow var4 = new SpectralArrow(level, position.x(), position.y(), position.z());
            var4.pickup = AbstractArrow.Pickup.ALLOWED;
            return var4;
         }
      });
      DispenserBlock.registerBehavior(Items.EGG, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
            return (Projectile)Util.make(new ThrownEgg(level, position.x(), position.y(), position.z()), (thrownEgg) -> {
               thrownEgg.setItem(itemStack);
            });
         }
      });
      DispenserBlock.registerBehavior(Items.SNOWBALL, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
            return (Projectile)Util.make(new Snowball(level, position.x(), position.y(), position.z()), (snowball) -> {
               snowball.setItem(itemStack);
            });
         }
      });
      DispenserBlock.registerBehavior(Items.EXPERIENCE_BOTTLE, new AbstractProjectileDispenseBehavior() {
         protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
            return (Projectile)Util.make(new ThrownExperienceBottle(level, position.x(), position.y(), position.z()), (thrownExperienceBottle) -> {
               thrownExperienceBottle.setItem(itemStack);
            });
         }

         protected float getUncertainty() {
            return super.getUncertainty() * 0.5F;
         }

         protected float getPower() {
            return super.getPower() * 1.25F;
         }
      });
      DispenserBlock.registerBehavior(Items.SPLASH_POTION, new DispenseItemBehavior() {
         public ItemStack dispense(BlockSource blockSource, ItemStack var2) {
            return (new AbstractProjectileDispenseBehavior() {
               protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                  return (Projectile)Util.make(new ThrownPotion(level, position.x(), position.y(), position.z()), (thrownPotion) -> {
                     thrownPotion.setItem(itemStack);
                  });
               }

               protected float getUncertainty() {
                  return super.getUncertainty() * 0.5F;
               }

               protected float getPower() {
                  return super.getPower() * 1.25F;
               }
            }).dispense(blockSource, var2);
         }
      });
      DispenserBlock.registerBehavior(Items.LINGERING_POTION, new DispenseItemBehavior() {
         public ItemStack dispense(BlockSource blockSource, ItemStack var2) {
            return (new AbstractProjectileDispenseBehavior() {
               protected Projectile getProjectile(Level level, Position position, ItemStack itemStack) {
                  return (Projectile)Util.make(new ThrownPotion(level, position.x(), position.y(), position.z()), (thrownPotion) -> {
                     thrownPotion.setItem(itemStack);
                  });
               }

               protected float getUncertainty() {
                  return super.getUncertainty() * 0.5F;
               }

               protected float getPower() {
                  return super.getPower() * 1.25F;
               }
            }).dispense(blockSource, var2);
         }
      });
      DefaultDispenseItemBehavior var0 = new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource blockSource, ItemStack var2) {
            Direction var3 = (Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING);
            EntityType<?> var4 = ((SpawnEggItem)var2.getItem()).getType(var2.getTag());
            var4.spawn(blockSource.getLevel(), var2, (Player)null, blockSource.getPos().relative(var3), MobSpawnType.DISPENSER, var3 != Direction.UP, false);
            var2.shrink(1);
            return var2;
         }
      };

      for(SpawnEggItem var2 : SpawnEggItem.eggs()) {
         DispenserBlock.registerBehavior(var2, var0);
      }

      DispenserBlock.registerBehavior(Items.FIREWORK_ROCKET, new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource blockSource, ItemStack var2) {
            Direction var3 = (Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING);
            double var4 = blockSource.x() + (double)var3.getStepX();
            double var6 = (double)((float)blockSource.getPos().getY() + 0.2F);
            double var8 = blockSource.z() + (double)var3.getStepZ();
            blockSource.getLevel().addFreshEntity(new FireworkRocketEntity(blockSource.getLevel(), var4, var6, var8, var2));
            var2.shrink(1);
            return var2;
         }

         protected void playSound(BlockSource blockSource) {
            blockSource.getLevel().levelEvent(1004, blockSource.getPos(), 0);
         }
      });
      DispenserBlock.registerBehavior(Items.FIRE_CHARGE, new DefaultDispenseItemBehavior() {
         public ItemStack execute(BlockSource blockSource, ItemStack var2) {
            Direction var3 = (Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING);
            Position var4 = DispenserBlock.getDispensePosition(blockSource);
            double var5 = var4.x() + (double)((float)var3.getStepX() * 0.3F);
            double var7 = var4.y() + (double)((float)var3.getStepY() * 0.3F);
            double var9 = var4.z() + (double)((float)var3.getStepZ() * 0.3F);
            Level var11 = blockSource.getLevel();
            Random var12 = var11.random;
            double var13 = var12.nextGaussian() * 0.05D + (double)var3.getStepX();
            double var15 = var12.nextGaussian() * 0.05D + (double)var3.getStepY();
            double var17 = var12.nextGaussian() * 0.05D + (double)var3.getStepZ();
            var11.addFreshEntity((Entity)Util.make(new SmallFireball(var11, var5, var7, var9, var13, var15, var17), (smallFireball) -> {
               smallFireball.setItem(var2);
            }));
            var2.shrink(1);
            return var2;
         }

         protected void playSound(BlockSource blockSource) {
            blockSource.getLevel().levelEvent(1018, blockSource.getPos(), 0);
         }
      });
      DispenserBlock.registerBehavior(Items.OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.OAK));
      DispenserBlock.registerBehavior(Items.SPRUCE_BOAT, new BoatDispenseItemBehavior(Boat.Type.SPRUCE));
      DispenserBlock.registerBehavior(Items.BIRCH_BOAT, new BoatDispenseItemBehavior(Boat.Type.BIRCH));
      DispenserBlock.registerBehavior(Items.JUNGLE_BOAT, new BoatDispenseItemBehavior(Boat.Type.JUNGLE));
      DispenserBlock.registerBehavior(Items.DARK_OAK_BOAT, new BoatDispenseItemBehavior(Boat.Type.DARK_OAK));
      DispenserBlock.registerBehavior(Items.ACACIA_BOAT, new BoatDispenseItemBehavior(Boat.Type.ACACIA));
      DispenseItemBehavior var1 = new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         public ItemStack execute(BlockSource blockSource, ItemStack var2) {
            BucketItem var3 = (BucketItem)var2.getItem();
            BlockPos var4 = blockSource.getPos().relative((Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING));
            Level var5 = blockSource.getLevel();
            if(var3.emptyBucket((Player)null, var5, var4, (BlockHitResult)null)) {
               var3.checkExtraContent(var5, var2, var4);
               return new ItemStack(Items.BUCKET);
            } else {
               return this.defaultDispenseItemBehavior.dispense(blockSource, var2);
            }
         }
      };
      DispenserBlock.registerBehavior(Items.LAVA_BUCKET, var1);
      DispenserBlock.registerBehavior(Items.WATER_BUCKET, var1);
      DispenserBlock.registerBehavior(Items.SALMON_BUCKET, var1);
      DispenserBlock.registerBehavior(Items.COD_BUCKET, var1);
      DispenserBlock.registerBehavior(Items.PUFFERFISH_BUCKET, var1);
      DispenserBlock.registerBehavior(Items.TROPICAL_FISH_BUCKET, var1);
      DispenserBlock.registerBehavior(Items.BUCKET, new DefaultDispenseItemBehavior() {
         private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

         public ItemStack execute(BlockSource blockSource, ItemStack var2) {
            LevelAccessor var3 = blockSource.getLevel();
            BlockPos var4 = blockSource.getPos().relative((Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING));
            BlockState var5 = var3.getBlockState(var4);
            Block var6 = var5.getBlock();
            if(var6 instanceof BucketPickup) {
               Fluid var8 = ((BucketPickup)var6).takeLiquid(var3, var4, var5);
               if(!(var8 instanceof FlowingFluid)) {
                  return super.execute(blockSource, var2);
               } else {
                  Item var7 = var8.getBucket();
                  var2.shrink(1);
                  if(var2.isEmpty()) {
                     return new ItemStack(var7);
                  } else {
                     if(((DispenserBlockEntity)blockSource.getEntity()).addItem(new ItemStack(var7)) < 0) {
                        this.defaultDispenseItemBehavior.dispense(blockSource, new ItemStack(var7));
                     }

                     return var2;
                  }
               }
            } else {
               return super.execute(blockSource, var2);
            }
         }
      });
      DispenserBlock.registerBehavior(Items.FLINT_AND_STEEL, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource blockSource, ItemStack var2) {
            Level var3 = blockSource.getLevel();
            this.success = true;
            BlockPos var4 = blockSource.getPos().relative((Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING));
            BlockState var5 = var3.getBlockState(var4);
            if(FlintAndSteelItem.canUse(var5, var3, var4)) {
               var3.setBlockAndUpdate(var4, Blocks.FIRE.defaultBlockState());
            } else if(FlintAndSteelItem.canLightCampFire(var5)) {
               var3.setBlockAndUpdate(var4, (BlockState)var5.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)));
            } else if(var5.getBlock() instanceof TntBlock) {
               TntBlock.explode(var3, var4);
               var3.removeBlock(var4, false);
            } else {
               this.success = false;
            }

            if(this.success && var2.hurt(1, var3.random, (ServerPlayer)null)) {
               var2.setCount(0);
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Items.BONE_MEAL, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource blockSource, ItemStack var2) {
            this.success = true;
            Level var3 = blockSource.getLevel();
            BlockPos var4 = blockSource.getPos().relative((Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING));
            if(!BoneMealItem.growCrop(var2, var3, var4) && !BoneMealItem.growWaterPlant(var2, var3, var4, (Direction)null)) {
               this.success = false;
            } else if(!var3.isClientSide) {
               var3.levelEvent(2005, var4, 0);
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Blocks.TNT, new DefaultDispenseItemBehavior() {
         protected ItemStack execute(BlockSource blockSource, ItemStack var2) {
            Level var3 = blockSource.getLevel();
            BlockPos var4 = blockSource.getPos().relative((Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING));
            PrimedTnt var5 = new PrimedTnt(var3, (double)var4.getX() + 0.5D, (double)var4.getY(), (double)var4.getZ() + 0.5D, (LivingEntity)null);
            var3.addFreshEntity(var5);
            var3.playSound((Player)null, var5.x, var5.y, var5.z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            var2.shrink(1);
            return var2;
         }
      });
      DispenseItemBehavior var2 = new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource blockSource, ItemStack var2) {
            this.success = !ArmorItem.dispenseArmor(blockSource, var2).isEmpty();
            return var2;
         }
      };
      DispenserBlock.registerBehavior(Items.CREEPER_HEAD, var2);
      DispenserBlock.registerBehavior(Items.ZOMBIE_HEAD, var2);
      DispenserBlock.registerBehavior(Items.DRAGON_HEAD, var2);
      DispenserBlock.registerBehavior(Items.SKELETON_SKULL, var2);
      DispenserBlock.registerBehavior(Items.PLAYER_HEAD, var2);
      DispenserBlock.registerBehavior(Items.WITHER_SKELETON_SKULL, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource blockSource, ItemStack var2) {
            Level var3 = blockSource.getLevel();
            Direction var4 = (Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos var5 = blockSource.getPos().relative(var4);
            this.success = true;
            if(var3.isEmptyBlock(var5) && WitherSkullBlock.canSpawnMob(var3, var5, var2)) {
               var3.setBlock(var5, (BlockState)Blocks.WITHER_SKELETON_SKULL.defaultBlockState().setValue(SkullBlock.ROTATION, Integer.valueOf(var4.getAxis() == Direction.Axis.Y?0:var4.getOpposite().get2DDataValue() * 4)), 3);
               BlockEntity var6 = var3.getBlockEntity(var5);
               if(var6 instanceof SkullBlockEntity) {
                  WitherSkullBlock.checkSpawn(var3, var5, (SkullBlockEntity)var6);
               }

               var2.shrink(1);
            } else if(ArmorItem.dispenseArmor(blockSource, var2).isEmpty()) {
               this.success = false;
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Blocks.CARVED_PUMPKIN, new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource blockSource, ItemStack var2) {
            Level var3 = blockSource.getLevel();
            BlockPos var4 = blockSource.getPos().relative((Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING));
            CarvedPumpkinBlock var5 = (CarvedPumpkinBlock)Blocks.CARVED_PUMPKIN;
            this.success = true;
            if(var3.isEmptyBlock(var4) && var5.canSpawnGolem(var3, var4)) {
               if(!var3.isClientSide) {
                  var3.setBlock(var4, var5.defaultBlockState(), 3);
               }

               var2.shrink(1);
            } else {
               ItemStack var6 = ArmorItem.dispenseArmor(blockSource, var2);
               if(var6.isEmpty()) {
                  this.success = false;
               }
            }

            return var2;
         }
      });
      DispenserBlock.registerBehavior(Blocks.SHULKER_BOX.asItem(), new ShulkerBoxDispenseBehavior());

      for(DyeColor var6 : DyeColor.values()) {
         DispenserBlock.registerBehavior(ShulkerBoxBlock.getBlockByColor(var6).asItem(), new ShulkerBoxDispenseBehavior());
      }

      DispenserBlock.registerBehavior(Items.SHEARS.asItem(), new OptionalDispenseItemBehavior() {
         protected ItemStack execute(BlockSource blockSource, ItemStack var2) {
            Level var3 = blockSource.getLevel();
            if(!var3.isClientSide()) {
               this.success = false;
               BlockPos var4 = blockSource.getPos().relative((Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING));

               for(Sheep var7 : var3.getEntitiesOfClass(Sheep.class, new AABB(var4))) {
                  if(var7.isAlive() && !var7.isSheared() && !var7.isBaby()) {
                     var7.shear();
                     if(var2.hurt(1, var3.random, (ServerPlayer)null)) {
                        var2.setCount(0);
                     }

                     this.success = true;
                     break;
                  }
               }
            }

            return var2;
         }
      });
   }
}
