package net.minecraft;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ReportedException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
   private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
   private static final ExecutorService BACKGROUND_EXECUTOR = makeBackgroundExecutor();
   public static LongSupplier timeSource = System::nanoTime;
   private static final Logger LOGGER = LogManager.getLogger();

   public static Collector toMap() {
      return Collectors.toMap(Entry::getKey, Entry::getValue);
   }

   public static String getPropertyName(Property property, Object object) {
      return property.getName((Comparable)object);
   }

   public static String makeDescriptionId(String var0, @Nullable ResourceLocation resourceLocation) {
      return resourceLocation == null?var0 + ".unregistered_sadface":var0 + '.' + resourceLocation.getNamespace() + '.' + resourceLocation.getPath().replace('/', '.');
   }

   public static long getMillis() {
      return getNanos() / 1000000L;
   }

   public static long getNanos() {
      return timeSource.getAsLong();
   }

   public static long getEpochMillis() {
      return Instant.now().toEpochMilli();
   }

   private static ExecutorService makeBackgroundExecutor() {
      int var0 = Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, 7);
      ExecutorService var1;
      if(var0 <= 0) {
         var1 = MoreExecutors.newDirectExecutorService();
      } else {
         var1 = new ForkJoinPool(var0, (forkJoinPool) -> {
            ForkJoinWorkerThread forkJoinWorkerThread = new ForkJoinWorkerThread(forkJoinPool) {
            };
            forkJoinWorkerThread.setName("Server-Worker-" + WORKER_COUNT.getAndIncrement());
            return forkJoinWorkerThread;
         }, (thread, throwable) -> {
            if(throwable instanceof CompletionException) {
               throwable = throwable.getCause();
            }

            if(throwable instanceof ReportedException) {
               Bootstrap.realStdoutPrintln(((ReportedException)throwable).getReport().getFriendlyReport());
               System.exit(-1);
            }

            LOGGER.error(String.format("Caught exception in thread %s", new Object[]{thread}), throwable);
         }, true);
      }

      return var1;
   }

   public static Executor backgroundExecutor() {
      return BACKGROUND_EXECUTOR;
   }

   public static void shutdownBackgroundExecutor() {
      BACKGROUND_EXECUTOR.shutdown();

      boolean var0;
      try {
         var0 = BACKGROUND_EXECUTOR.awaitTermination(3L, TimeUnit.SECONDS);
      } catch (InterruptedException var2) {
         var0 = false;
      }

      if(!var0) {
         BACKGROUND_EXECUTOR.shutdownNow();
      }

   }

   public static CompletableFuture failedFuture(Throwable throwable) {
      CompletableFuture<T> completableFuture = new CompletableFuture();
      completableFuture.completeExceptionally(throwable);
      return completableFuture;
   }

   public static Util.OS getPlatform() {
      String var0 = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      return var0.contains("win")?Util.OS.WINDOWS:(var0.contains("mac")?Util.OS.OSX:(var0.contains("solaris")?Util.OS.SOLARIS:(var0.contains("sunos")?Util.OS.SOLARIS:(var0.contains("linux")?Util.OS.LINUX:(var0.contains("unix")?Util.OS.LINUX:Util.OS.UNKNOWN)))));
   }

   public static Stream getVmArguments() {
      RuntimeMXBean var0 = ManagementFactory.getRuntimeMXBean();
      return var0.getInputArguments().stream().filter((string) -> {
         return string.startsWith("-X");
      });
   }

   public static Object lastOf(List list) {
      return list.get(list.size() - 1);
   }

   public static Object findNextInIterable(Iterable iterable, @Nullable Object var1) {
      Iterator<T> var2 = iterable.iterator();
      T var3 = var2.next();
      if(var1 != null) {
         T var4 = var3;

         while(var4 != var1) {
            if(var2.hasNext()) {
               var4 = var2.next();
            }
         }

         if(var2.hasNext()) {
            return var2.next();
         }
      }

      return var3;
   }

   public static Object findPreviousInIterable(Iterable iterable, @Nullable Object var1) {
      Iterator<T> var2 = iterable.iterator();

      T var3;
      T var4;
      for(var3 = null; var2.hasNext(); var3 = var4) {
         var4 = var2.next();
         if(var4 == var1) {
            if(var3 == null) {
               var3 = var2.hasNext()?Iterators.getLast(var2):var1;
            }
            break;
         }
      }

      return var3;
   }

   public static Object make(Supplier supplier) {
      return supplier.get();
   }

   public static Object make(Object var0, Consumer consumer) {
      consumer.accept(var0);
      return var0;
   }

   public static Strategy identityStrategy() {
      return Util.IdentityStrategy.INSTANCE;
   }

   public static CompletableFuture sequence(List list) {
      List<V> list = Lists.newArrayListWithCapacity(list.size());
      CompletableFuture<?>[] vars2 = new CompletableFuture[list.size()];
      CompletableFuture<Void> var3 = new CompletableFuture();
      list.forEach((var3x) -> {
         int var4 = list.size();
         list.add((Object)null);
         vars2[var4] = var3x.whenComplete((object, throwable) -> {
            if(throwable != null) {
               var3.completeExceptionally(throwable);
            } else {
               list.set(var4, object);
            }

         });
      });
      return CompletableFuture.allOf(vars2).applyToEither(var3, (void) -> {
         return list;
      });
   }

   public static Stream toStream(Optional optional) {
      return (Stream)DataFixUtils.orElseGet(optional.map(Stream::of), Stream::empty);
   }

   public static Optional ifElse(Optional var0, Consumer consumer, Runnable runnable) {
      if(var0.isPresent()) {
         consumer.accept(var0.get());
      } else {
         runnable.run();
      }

      return var0;
   }

   public static Runnable name(Runnable var0, Supplier supplier) {
      return var0;
   }

   public static Optional readUUID(String string, Dynamic dynamic) {
      return dynamic.get(string + "Most").asNumber().flatMap((number) -> {
         return dynamic.get(string + "Least").asNumber().map((var1) -> {
            return new UUID(number.longValue(), var1.longValue());
         });
      });
   }

   public static Dynamic writeUUID(String string, UUID uUID, Dynamic var2) {
      return var2.set(string + "Most", var2.createLong(uUID.getMostSignificantBits())).set(string + "Least", var2.createLong(uUID.getLeastSignificantBits()));
   }

   static enum IdentityStrategy implements Strategy {
      INSTANCE;

      public int hashCode(Object object) {
         return System.identityHashCode(object);
      }

      public boolean equals(Object var1, Object var2) {
         return var1 == var2;
      }
   }

   public static enum OS {
      LINUX,
      SOLARIS,
      WINDOWS {
         protected String[] getOpenUrlArguments(URL uRL) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", uRL.toString()};
         }
      },
      OSX {
         protected String[] getOpenUrlArguments(URL uRL) {
            return new String[]{"open", uRL.toString()};
         }
      },
      UNKNOWN;

      private OS() {
      }

      public void openUrl(URL uRL) {
         try {
            Process var2 = (Process)AccessController.doPrivileged(() -> {
               return Runtime.getRuntime().exec(this.getOpenUrlArguments(uRL));
            });

            for(String var4 : IOUtils.readLines(var2.getErrorStream())) {
               Util.LOGGER.error(var4);
            }

            var2.getInputStream().close();
            var2.getErrorStream().close();
            var2.getOutputStream().close();
         } catch (IOException | PrivilegedActionException var5) {
            Util.LOGGER.error("Couldn\'t open url \'{}\'", uRL, var5);
         }

      }

      public void openUri(URI uRI) {
         try {
            this.openUrl(uRI.toURL());
         } catch (MalformedURLException var3) {
            Util.LOGGER.error("Couldn\'t open uri \'{}\'", uRI, var3);
         }

      }

      public void openFile(File file) {
         try {
            this.openUrl(file.toURI().toURL());
         } catch (MalformedURLException var3) {
            Util.LOGGER.error("Couldn\'t open file \'{}\'", file, var3);
         }

      }

      protected String[] getOpenUrlArguments(URL uRL) {
         String string = uRL.toString();
         if("file".equals(uRL.getProtocol())) {
            string = string.replace("file:", "file://");
         }

         return new String[]{"xdg-open", string};
      }

      public void openUri(String string) {
         try {
            this.openUrl((new URI(string)).toURL());
         } catch (MalformedURLException | IllegalArgumentException | URISyntaxException var3) {
            Util.LOGGER.error("Couldn\'t open uri \'{}\'", string, var3);
         }

      }
   }
}
