package net.minecraft.world.entity.schedule;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;

public class ScheduleBuilder {
   private final Schedule schedule;
   private final List transitions = Lists.newArrayList();

   public ScheduleBuilder(Schedule schedule) {
      this.schedule = schedule;
   }

   public ScheduleBuilder changeActivityAt(int var1, Activity activity) {
      this.transitions.add(new ScheduleBuilder.ActivityTransition(var1, activity));
      return this;
   }

   public Schedule build() {
      Set var10000 = (Set)this.transitions.stream().map(ScheduleBuilder.ActivityTransition::getActivity).collect(Collectors.toSet());
      Schedule var10001 = this.schedule;
      this.schedule.getClass();
      var10000.forEach(var10001::ensureTimelineExistsFor);
      this.transitions.forEach((scheduleBuilder$ActivityTransition) -> {
         Activity var2 = scheduleBuilder$ActivityTransition.getActivity();
         this.schedule.getAllTimelinesExceptFor(var2).forEach((timeline) -> {
            timeline.addKeyframe(scheduleBuilder$ActivityTransition.getTime(), 0.0F);
         });
         this.schedule.getTimelineFor(var2).addKeyframe(scheduleBuilder$ActivityTransition.getTime(), 1.0F);
      });
      return this.schedule;
   }

   static class ActivityTransition {
      private final int time;
      private final Activity activity;

      public ActivityTransition(int time, Activity activity) {
         this.time = time;
         this.activity = activity;
      }

      public int getTime() {
         return this.time;
      }

      public Activity getActivity() {
         return this.activity;
      }
   }
}
