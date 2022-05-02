package net.minecraft.world.entity.npc;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.UseItemGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class WanderingTrader extends AbstractVillager {
   @Nullable
   private BlockPos wanderTarget;
   private int despawnDelay;

   public WanderingTrader(EntityType entityType, Level level) {
      super(entityType, level);
      this.forcedLoading = true;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(0, new UseItemGoal(this, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.INVISIBILITY), SoundEvents.WANDERING_TRADER_DISAPPEARED, (wanderingTrader) -> {
         return !this.level.isDay() && !wanderingTrader.isInvisible();
      }));
      this.goalSelector.addGoal(0, new UseItemGoal(this, new ItemStack(Items.MILK_BUCKET), SoundEvents.WANDERING_TRADER_REAPPEARED, (wanderingTrader) -> {
         return this.level.isDay() && wanderingTrader.isInvisible();
      }));
      this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Zombie.class, 8.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Evoker.class, 12.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Vindicator.class, 8.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Vex.class, 8.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Pillager.class, 15.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new AvoidEntityGoal(this, Illusioner.class, 12.0F, 0.5D, 0.5D));
      this.goalSelector.addGoal(1, new PanicGoal(this, 0.5D));
      this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
      this.goalSelector.addGoal(2, new WanderingTrader.WanderToPositionGoal(this, 2.0D, 0.35D));
      this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 1.0D));
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.35D));
      this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
   }

   @Nullable
   public AgableMob getBreedOffspring(AgableMob agableMob) {
      return null;
   }

   public boolean showProgressBar() {
      return false;
   }

   public boolean mobInteract(Player tradingPlayer, InteractionHand interactionHand) {
      ItemStack var3 = tradingPlayer.getItemInHand(interactionHand);
      boolean var4 = var3.getItem() == Items.NAME_TAG;
      if(var4) {
         var3.interactEnemy(tradingPlayer, this, interactionHand);
         return true;
      } else if(var3.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.isTrading() && !this.isBaby()) {
         if(interactionHand == InteractionHand.MAIN_HAND) {
            tradingPlayer.awardStat(Stats.TALKED_TO_VILLAGER);
         }

         if(this.getOffers().isEmpty()) {
            return super.mobInteract(tradingPlayer, interactionHand);
         } else {
            if(!this.level.isClientSide) {
               this.setTradingPlayer(tradingPlayer);
               this.openTradingScreen(tradingPlayer, this.getDisplayName(), 1);
            }

            return true;
         }
      } else {
         return super.mobInteract(tradingPlayer, interactionHand);
      }
   }

   protected void updateTrades() {
      VillagerTrades.ItemListing[] vars1 = (VillagerTrades.ItemListing[])VillagerTrades.WANDERING_TRADER_TRADES.get(1);
      VillagerTrades.ItemListing[] vars2 = (VillagerTrades.ItemListing[])VillagerTrades.WANDERING_TRADER_TRADES.get(2);
      if(vars1 != null && vars2 != null) {
         MerchantOffers var3 = this.getOffers();
         this.addOffersFromItemListings(var3, vars1, 5);
         int var4 = this.random.nextInt(vars2.length);
         VillagerTrades.ItemListing var5 = vars2[var4];
         MerchantOffer var6 = var5.getOffer(this, this.random);
         if(var6 != null) {
            var3.add(var6);
         }

      }
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("DespawnDelay", this.despawnDelay);
      if(this.wanderTarget != null) {
         compoundTag.put("WanderTarget", NbtUtils.writeBlockPos(this.wanderTarget));
      }

   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      if(compoundTag.contains("DespawnDelay", 99)) {
         this.despawnDelay = compoundTag.getInt("DespawnDelay");
      }

      if(compoundTag.contains("WanderTarget")) {
         this.wanderTarget = NbtUtils.readBlockPos(compoundTag.getCompound("WanderTarget"));
      }

      this.setAge(Math.max(0, this.getAge()));
   }

   public boolean removeWhenFarAway(double d) {
      return false;
   }

   protected void rewardTradeXp(MerchantOffer merchantOffer) {
      if(merchantOffer.shouldRewardExp()) {
         int var2 = 3 + this.random.nextInt(4);
         this.level.addFreshEntity(new ExperienceOrb(this.level, this.x, this.y + 0.5D, this.z, var2));
      }

   }

   protected SoundEvent getAmbientSound() {
      return this.isTrading()?SoundEvents.WANDERING_TRADER_TRADE:SoundEvents.WANDERING_TRADER_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.WANDERING_TRADER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WANDERING_TRADER_DEATH;
   }

   protected SoundEvent getDrinkingSound(ItemStack itemStack) {
      Item var2 = itemStack.getItem();
      return var2 == Items.MILK_BUCKET?SoundEvents.WANDERING_TRADER_DRINK_MILK:SoundEvents.WANDERING_TRADER_DRINK_POTION;
   }

   protected SoundEvent getTradeUpdatedSound(boolean b) {
      return b?SoundEvents.WANDERING_TRADER_YES:SoundEvents.WANDERING_TRADER_NO;
   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.WANDERING_TRADER_YES;
   }

   public void setDespawnDelay(int despawnDelay) {
      this.despawnDelay = despawnDelay;
   }

   public int getDespawnDelay() {
      return this.despawnDelay;
   }

   public void aiStep() {
      super.aiStep();
      if(!this.level.isClientSide) {
         this.maybeDespawn();
      }

   }

   private void maybeDespawn() {
      if(this.despawnDelay > 0 && !this.isTrading() && --this.despawnDelay == 0) {
         this.remove();
      }

   }

   public void setWanderTarget(@Nullable BlockPos wanderTarget) {
      this.wanderTarget = wanderTarget;
   }

   @Nullable
   private BlockPos getWanderTarget() {
      return this.wanderTarget;
   }

   class WanderToPositionGoal extends Goal {
      final WanderingTrader trader;
      final double stopDistance;
      final double speedModifier;

      WanderToPositionGoal(WanderingTrader trader, double stopDistance, double speedModifier) {
         this.trader = trader;
         this.stopDistance = stopDistance;
         this.speedModifier = speedModifier;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public void stop() {
         this.trader.setWanderTarget((BlockPos)null);
         WanderingTrader.this.navigation.stop();
      }

      public boolean canUse() {
         BlockPos var1 = this.trader.getWanderTarget();
         return var1 != null && this.isTooFarAway(var1, this.stopDistance);
      }

      public void tick() {
         BlockPos var1 = this.trader.getWanderTarget();
         if(var1 != null && WanderingTrader.this.navigation.isDone()) {
            if(this.isTooFarAway(var1, 10.0D)) {
               Vec3 var2 = (new Vec3((double)var1.getX() - this.trader.x, (double)var1.getY() - this.trader.y, (double)var1.getZ() - this.trader.z)).normalize();
               Vec3 var3 = var2.scale(10.0D).add(this.trader.x, this.trader.y, this.trader.z);
               WanderingTrader.this.navigation.moveTo(var3.x, var3.y, var3.z, this.speedModifier);
            } else {
               WanderingTrader.this.navigation.moveTo((double)var1.getX(), (double)var1.getY(), (double)var1.getZ(), this.speedModifier);
            }
         }

      }

      private boolean isTooFarAway(BlockPos blockPos, double var2) {
         return !blockPos.closerThan(this.trader.position(), var2);
      }
   }
}
