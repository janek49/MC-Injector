package net.minecraft.client.sounds;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@ClientJarOnly
public class ChannelAccess {
   private final Set channels = Sets.newIdentityHashSet();
   private final Library library;
   private final Executor executor;

   public ChannelAccess(Library library, Executor executor) {
      this.library = library;
      this.executor = executor;
   }

   public ChannelAccess.ChannelHandle createHandle(Library.Pool library$Pool) {
      ChannelAccess.ChannelHandle channelAccess$ChannelHandle = new ChannelAccess.ChannelHandle();
      this.executor.execute(() -> {
         Channel var3 = this.library.acquireChannel(library$Pool);
         if(var3 != null) {
            channelAccess$ChannelHandle.channel = var3;
            this.channels.add(channelAccess$ChannelHandle);
         }

      });
      return channelAccess$ChannelHandle;
   }

   public void executeOnChannels(Consumer consumer) {
      this.executor.execute(() -> {
         consumer.accept(this.channels.stream().map((channelAccess$ChannelHandle) -> {
            return channelAccess$ChannelHandle.channel;
         }).filter(Objects::nonNull));
      });
   }

   public void scheduleTick() {
      this.executor.execute(() -> {
         Iterator<ChannelAccess.ChannelHandle> var1 = this.channels.iterator();

         while(var1.hasNext()) {
            ChannelAccess.ChannelHandle var2 = (ChannelAccess.ChannelHandle)var1.next();
            var2.channel.updateStream();
            if(var2.channel.stopped()) {
               var2.release();
               var1.remove();
            }
         }

      });
   }

   public void clear() {
      this.channels.forEach(ChannelAccess.ChannelHandle::release);
      this.channels.clear();
   }

   @ClientJarOnly
   public class ChannelHandle {
      private Channel channel;
      private boolean stopped;

      public boolean isStopped() {
         return this.stopped;
      }

      public void execute(Consumer consumer) {
         ChannelAccess.this.executor.execute(() -> {
            if(this.channel != null) {
               consumer.accept(this.channel);
            }

         });
      }

      public void release() {
         this.stopped = true;
         ChannelAccess.this.library.releaseChannel(this.channel);
         this.channel = null;
      }
   }
}
