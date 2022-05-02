package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;

@ClientJarOnly
class RepeatedNarrator {
   final Duration repeatDelay;
   private final float permitsPerSecond;
   final AtomicReference params;

   public RepeatedNarrator(Duration repeatDelay) {
      this.repeatDelay = repeatDelay;
      this.params = new AtomicReference();
      float var2 = (float)repeatDelay.toMillis() / 1000.0F;
      this.permitsPerSecond = 1.0F / var2;
   }

   public void narrate(String string) {
      RepeatedNarrator.Params var2 = (RepeatedNarrator.Params)this.params.updateAndGet((var2) -> {
         return var2 != null && string.equals(var2.narration)?var2:new RepeatedNarrator.Params(string, RateLimiter.create((double)this.permitsPerSecond));
      });
      if(var2.rateLimiter.tryAcquire(1)) {
         NarratorChatListener var3 = NarratorChatListener.INSTANCE;
         var3.handle(ChatType.SYSTEM, new TextComponent(string));
      }

   }

   @ClientJarOnly
   static class Params {
      String narration;
      RateLimiter rateLimiter;

      Params(String narration, RateLimiter rateLimiter) {
         this.narration = narration;
         this.rateLimiter = rateLimiter;
      }
   }
}
