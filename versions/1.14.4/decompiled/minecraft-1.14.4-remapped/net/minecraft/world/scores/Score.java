package net.minecraft.world.scores;

import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class Score {
   public static final Comparator SCORE_COMPARATOR = (var0, var1) -> {
      return var0.getScore() > var1.getScore()?1:(var0.getScore() < var1.getScore()?-1:var1.getOwner().compareToIgnoreCase(var0.getOwner()));
   };
   private final Scoreboard scoreboard;
   @Nullable
   private final Objective objective;
   private final String owner;
   private int count;
   private boolean locked;
   private boolean forceUpdate;

   public Score(Scoreboard scoreboard, Objective objective, String owner) {
      this.scoreboard = scoreboard;
      this.objective = objective;
      this.owner = owner;
      this.locked = true;
      this.forceUpdate = true;
   }

   public void add(int i) {
      if(this.objective.getCriteria().isReadOnly()) {
         throw new IllegalStateException("Cannot modify read-only score");
      } else {
         this.setScore(this.getScore() + i);
      }
   }

   public void increment() {
      this.add(1);
   }

   public int getScore() {
      return this.count;
   }

   public void reset() {
      this.setScore(0);
   }

   public void setScore(int score) {
      int var2 = this.count;
      this.count = score;
      if(var2 != score || this.forceUpdate) {
         this.forceUpdate = false;
         this.getScoreboard().onScoreChanged(this);
      }

   }

   @Nullable
   public Objective getObjective() {
      return this.objective;
   }

   public String getOwner() {
      return this.owner;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }
}
