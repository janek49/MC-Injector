package net.minecraft.world.level.timers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.timers.FunctionCallback;
import net.minecraft.world.level.timers.FunctionTagCallback;
import net.minecraft.world.level.timers.TimerCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerCallbacks {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final TimerCallbacks SERVER_CALLBACKS = (new TimerCallbacks()).register(new FunctionCallback.Serializer()).register(new FunctionTagCallback.Serializer());
   private final Map idToSerializer = Maps.newHashMap();
   private final Map classToSerializer = Maps.newHashMap();

   public TimerCallbacks register(TimerCallback.Serializer timerCallback$Serializer) {
      this.idToSerializer.put(timerCallback$Serializer.getId(), timerCallback$Serializer);
      this.classToSerializer.put(timerCallback$Serializer.getCls(), timerCallback$Serializer);
      return this;
   }

   private TimerCallback.Serializer getSerializer(Class class) {
      return (TimerCallback.Serializer)this.classToSerializer.get(class);
   }

   public CompoundTag serialize(TimerCallback timerCallback) {
      TimerCallback.Serializer<C, T> var2 = this.getSerializer(timerCallback.getClass());
      CompoundTag var3 = new CompoundTag();
      var2.serialize(var3, timerCallback);
      var3.putString("Type", var2.getId().toString());
      return var3;
   }

   @Nullable
   public TimerCallback deserialize(CompoundTag compoundTag) {
      ResourceLocation var2 = ResourceLocation.tryParse(compoundTag.getString("Type"));
      TimerCallback.Serializer<C, ?> var3 = (TimerCallback.Serializer)this.idToSerializer.get(var2);
      if(var3 == null) {
         LOGGER.error("Failed to deserialize timer callback: " + compoundTag);
         return null;
      } else {
         try {
            return var3.deserialize(compoundTag);
         } catch (Exception var5) {
            LOGGER.error("Failed to deserialize timer callback: " + compoundTag, var5);
            return null;
         }
      }
   }
}
