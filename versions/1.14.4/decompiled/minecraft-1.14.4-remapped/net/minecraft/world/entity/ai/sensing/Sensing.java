package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class Sensing {
   private final Mob mob;
   private final List seen = Lists.newArrayList();
   private final List unseen = Lists.newArrayList();

   public Sensing(Mob mob) {
      this.mob = mob;
   }

   public void tick() {
      this.seen.clear();
      this.unseen.clear();
   }

   public boolean canSee(Entity entity) {
      if(this.seen.contains(entity)) {
         return true;
      } else if(this.unseen.contains(entity)) {
         return false;
      } else {
         this.mob.level.getProfiler().push("canSee");
         boolean var2 = this.mob.canSee(entity);
         this.mob.level.getProfiler().pop();
         if(var2) {
            this.seen.add(entity);
         } else {
            this.unseen.add(entity);
         }

         return var2;
      }
   }
}
