package net.minecraft.world.entity.animal;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public abstract class AbstractSchoolingFish extends AbstractFish {
   private AbstractSchoolingFish leader;
   private int schoolSize = 1;

   public AbstractSchoolingFish(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(5, new FollowFlockLeaderGoal(this));
   }

   public int getMaxSpawnClusterSize() {
      return this.getMaxSchoolSize();
   }

   public int getMaxSchoolSize() {
      return super.getMaxSpawnClusterSize();
   }

   protected boolean canRandomSwim() {
      return !this.isFollower();
   }

   public boolean isFollower() {
      return this.leader != null && this.leader.isAlive();
   }

   public AbstractSchoolingFish startFollowing(AbstractSchoolingFish leader) {
      this.leader = leader;
      leader.addFollower();
      return leader;
   }

   public void stopFollowing() {
      this.leader.removeFollower();
      this.leader = null;
   }

   private void addFollower() {
      ++this.schoolSize;
   }

   private void removeFollower() {
      --this.schoolSize;
   }

   public boolean canBeFollowed() {
      return this.hasFollowers() && this.schoolSize < this.getMaxSchoolSize();
   }

   public void tick() {
      super.tick();
      if(this.hasFollowers() && this.level.random.nextInt(200) == 1) {
         List<AbstractFish> var1 = this.level.getEntitiesOfClass(this.getClass(), this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D));
         if(var1.size() <= 1) {
            this.schoolSize = 1;
         }
      }

   }

   public boolean hasFollowers() {
      return this.schoolSize > 1;
   }

   public boolean inRangeOfLeader() {
      return this.distanceToSqr(this.leader) <= 121.0D;
   }

   public void pathToLeader() {
      if(this.isFollower()) {
         this.getNavigation().moveTo((Entity)this.leader, 1.0D);
      }

   }

   public void addFollowers(Stream stream) {
      stream.limit((long)(this.getMaxSchoolSize() - this.schoolSize)).filter((abstractSchoolingFish) -> {
         return abstractSchoolingFish != this;
      }).forEach((abstractSchoolingFish) -> {
         abstractSchoolingFish.startFollowing(this);
      });
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, (SpawnGroupData)var4, compoundTag);
      if(var4 == null) {
         var4 = new AbstractSchoolingFish.SchoolSpawnGroupData(this);
      } else {
         this.startFollowing(((AbstractSchoolingFish.SchoolSpawnGroupData)var4).leader);
      }

      return (SpawnGroupData)var4;
   }

   public static class SchoolSpawnGroupData implements SpawnGroupData {
      public final AbstractSchoolingFish leader;

      public SchoolSpawnGroupData(AbstractSchoolingFish leader) {
         this.leader = leader;
      }
   }
}
