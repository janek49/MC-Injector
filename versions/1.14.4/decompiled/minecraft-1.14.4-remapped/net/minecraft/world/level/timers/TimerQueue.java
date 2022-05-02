package net.minecraft.world.level.timers;

import com.google.common.collect.Maps;
import com.google.common.primitives.UnsignedLong;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerCallbacks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerQueue {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TimerCallbacks callbacksRegistry;
   private final Queue queue = new PriorityQueue(createComparator());
   private UnsignedLong sequentialId = UnsignedLong.ZERO;
   private final Map events = Maps.newHashMap();

   private static Comparator createComparator() {
      return (var0, var1) -> {
         int var2 = Long.compare(var0.triggerTime, var1.triggerTime);
         return var2 != 0?var2:var0.sequentialId.compareTo(var1.sequentialId);
      };
   }

   public TimerQueue(TimerCallbacks callbacksRegistry) {
      this.callbacksRegistry = callbacksRegistry;
   }

   public void tick(Object object, long var2) {
      while(true) {
         TimerQueue.Event<T> var4 = (TimerQueue.Event)this.queue.peek();
         if(var4 == null || var4.triggerTime > var2) {
            return;
         }

         this.queue.remove();
         this.events.remove(var4.id);
         var4.callback.handle(object, this, var2);
      }
   }

   private void addEvent(String string, long var2, TimerCallback timerCallback) {
      this.sequentialId = this.sequentialId.plus(UnsignedLong.ONE);
      TimerQueue.Event<T> var5 = new TimerQueue.Event(var2, this.sequentialId, string, timerCallback);
      this.events.put(string, var5);
      this.queue.add(var5);
   }

   public boolean schedule(String string, long var2, TimerCallback timerCallback) {
      if(this.events.containsKey(string)) {
         return false;
      } else {
         this.addEvent(string, var2, timerCallback);
         return true;
      }
   }

   public void reschedule(String string, long var2, TimerCallback timerCallback) {
      TimerQueue.Event<T> var5 = (TimerQueue.Event)this.events.remove(string);
      if(var5 != null) {
         this.queue.remove(var5);
      }

      this.addEvent(string, var2, timerCallback);
   }

   private void loadEvent(CompoundTag compoundTag) {
      CompoundTag compoundTag = compoundTag.getCompound("Callback");
      TimerCallback<T> var3 = this.callbacksRegistry.deserialize(compoundTag);
      if(var3 != null) {
         String var4 = compoundTag.getString("Name");
         long var5 = compoundTag.getLong("TriggerTime");
         this.schedule(var4, var5, var3);
      }

   }

   public void load(ListTag listTag) {
      this.queue.clear();
      this.events.clear();
      this.sequentialId = UnsignedLong.ZERO;
      if(!listTag.isEmpty()) {
         if(listTag.getElementType() != 10) {
            LOGGER.warn("Invalid format of events: " + listTag);
         } else {
            for(Tag var3 : listTag) {
               this.loadEvent((CompoundTag)var3);
            }

         }
      }
   }

   private CompoundTag storeEvent(TimerQueue.Event timerQueue$Event) {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.putString("Name", timerQueue$Event.id);
      compoundTag.putLong("TriggerTime", timerQueue$Event.triggerTime);
      compoundTag.put("Callback", this.callbacksRegistry.serialize(timerQueue$Event.callback));
      return compoundTag;
   }

   public ListTag store() {
      ListTag listTag = new ListTag();
      this.queue.stream().sorted(createComparator()).map(this::storeEvent).forEach(listTag::add);
      return listTag;
   }

   public static class Event {
      public final long triggerTime;
      public final UnsignedLong sequentialId;
      public final String id;
      public final TimerCallback callback;

      private Event(long triggerTime, UnsignedLong sequentialId, String id, TimerCallback callback) {
         this.triggerTime = triggerTime;
         this.sequentialId = sequentialId;
         this.id = id;
         this.callback = callback;
      }
   }
}
