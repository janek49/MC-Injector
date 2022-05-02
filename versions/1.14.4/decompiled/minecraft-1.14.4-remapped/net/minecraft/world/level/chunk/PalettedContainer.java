package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.BitStorage;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.HashMapPalette;
import net.minecraft.world.level.chunk.LinearPalette;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;

public class PalettedContainer implements PaletteResize {
   private final Palette globalPalette;
   private final PaletteResize dummyPaletteResize = (var0, object) -> {
      return 0;
   };
   private final IdMapper registry;
   private final Function reader;
   private final Function writer;
   private final Object defaultValue;
   protected BitStorage storage;
   private Palette palette;
   private int bits;
   private final ReentrantLock lock = new ReentrantLock();

   public void acquire() {
      if(this.lock.isLocked() && !this.lock.isHeldByCurrentThread()) {
         String var1 = (String)Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map((thread) -> {
            return thread.getName() + ": \n\tat " + (String)Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
         }).collect(Collectors.joining("\n"));
         CrashReport var2 = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
         CrashReportCategory var3 = var2.addCategory("Thread dumps");
         var3.setDetail("Thread dumps", (Object)var1);
         throw new ReportedException(var2);
      } else {
         this.lock.lock();
      }
   }

   public void release() {
      this.lock.unlock();
   }

   public PalettedContainer(Palette globalPalette, IdMapper registry, Function reader, Function writer, Object defaultValue) {
      this.globalPalette = globalPalette;
      this.registry = registry;
      this.reader = reader;
      this.writer = writer;
      this.defaultValue = defaultValue;
      this.setBits(4);
   }

   private static int getIndex(int var0, int var1, int var2) {
      return var1 << 8 | var2 << 4 | var0;
   }

   private void setBits(int bits) {
      if(bits != this.bits) {
         this.bits = bits;
         if(this.bits <= 4) {
            this.bits = 4;
            this.palette = new LinearPalette(this.registry, this.bits, this, this.reader);
         } else if(this.bits < 9) {
            this.palette = new HashMapPalette(this.registry, this.bits, this, this.reader, this.writer);
         } else {
            this.palette = this.globalPalette;
            this.bits = Mth.ceillog2(this.registry.size());
         }

         this.palette.idFor(this.defaultValue);
         this.storage = new BitStorage(this.bits, 4096);
      }
   }

   public int onResize(int bits, Object object) {
      this.acquire();
      BitStorage var3 = this.storage;
      Palette<T> var4 = this.palette;
      this.setBits(bits);

      for(int var5 = 0; var5 < var3.getSize(); ++var5) {
         T var6 = var4.valueFor(var3.get(var5));
         if(var6 != null) {
            this.set(var5, var6);
         }
      }

      int var5 = this.palette.idFor(object);
      this.release();
      return var5;
   }

   public Object getAndSet(int var1, int var2, int var3, Object var4) {
      this.acquire();
      T var5 = this.getAndSet(getIndex(var1, var2, var3), var4);
      this.release();
      return var5;
   }

   public Object getAndSetUnchecked(int var1, int var2, int var3, Object var4) {
      return this.getAndSet(getIndex(var1, var2, var3), var4);
   }

   protected Object getAndSet(int var1, Object var2) {
      int var3 = this.palette.idFor(var2);
      int var4 = this.storage.getAndSet(var1, var3);
      T var5 = this.palette.valueFor(var4);
      return var5 == null?this.defaultValue:var5;
   }

   protected void set(int var1, Object object) {
      int var3 = this.palette.idFor(object);
      this.storage.set(var1, var3);
   }

   public Object get(int var1, int var2, int var3) {
      return this.get(getIndex(var1, var2, var3));
   }

   protected Object get(int i) {
      T object = this.palette.valueFor(this.storage.get(i));
      return object == null?this.defaultValue:object;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) {
      this.acquire();
      int var2 = friendlyByteBuf.readByte();
      if(this.bits != var2) {
         this.setBits(var2);
      }

      this.palette.read(friendlyByteBuf);
      friendlyByteBuf.readLongArray(this.storage.getRaw());
      this.release();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      this.acquire();
      friendlyByteBuf.writeByte(this.bits);
      this.palette.write(friendlyByteBuf);
      friendlyByteBuf.writeLongArray(this.storage.getRaw());
      this.release();
   }

   public void read(ListTag listTag, long[] longs) {
      this.acquire();
      int bits = Math.max(4, Mth.ceillog2(listTag.size()));
      if(bits != this.bits) {
         this.setBits(bits);
      }

      this.palette.read(listTag);
      int var4 = longs.length * 64 / 4096;
      if(this.palette == this.globalPalette) {
         Palette<T> var5 = new HashMapPalette(this.registry, bits, this.dummyPaletteResize, this.reader, this.writer);
         var5.read(listTag);
         BitStorage var6 = new BitStorage(bits, 4096, longs);

         for(int var7 = 0; var7 < 4096; ++var7) {
            this.storage.set(var7, this.globalPalette.idFor(var5.valueFor(var6.get(var7))));
         }
      } else if(var4 == this.bits) {
         System.arraycopy(longs, 0, this.storage.getRaw(), 0, longs.length);
      } else {
         BitStorage var5 = new BitStorage(var4, 4096, longs);

         for(int var6 = 0; var6 < 4096; ++var6) {
            this.storage.set(var6, var5.get(var6));
         }
      }

      this.release();
   }

   public void write(CompoundTag compoundTag, String var2, String var3) {
      this.acquire();
      HashMapPalette<T> var4 = new HashMapPalette(this.registry, this.bits, this.dummyPaletteResize, this.reader, this.writer);
      var4.idFor(this.defaultValue);
      int[] vars5 = new int[4096];

      for(int var6 = 0; var6 < 4096; ++var6) {
         vars5[var6] = var4.idFor(this.get(var6));
      }

      ListTag var6 = new ListTag();
      var4.write(var6);
      compoundTag.put(var2, var6);
      int var7 = Math.max(4, Mth.ceillog2(var6.size()));
      BitStorage var8 = new BitStorage(var7, 4096);

      for(int var9 = 0; var9 < vars5.length; ++var9) {
         var8.set(var9, vars5[var9]);
      }

      compoundTag.putLongArray(var3, var8.getRaw());
      this.release();
   }

   public int getSerializedSize() {
      return 1 + this.palette.getSerializedSize() + FriendlyByteBuf.getVarIntSize(this.storage.getSize()) + this.storage.getRaw().length * 8;
   }

   public boolean maybeHas(Object object) {
      return this.palette.maybeHas(object);
   }

   public void count(PalettedContainer.CountConsumer palettedContainer$CountConsumer) {
      Int2IntMap var2 = new Int2IntOpenHashMap();
      this.storage.getAll((var1) -> {
         var2.put(var1, var2.get(var1) + 1);
      });
      var2.int2IntEntrySet().forEach((int2IntMap$Entry) -> {
         palettedContainer$CountConsumer.accept(this.palette.valueFor(int2IntMap$Entry.getIntKey()), int2IntMap$Entry.getIntValue());
      });
   }

   @FunctionalInterface
   public interface CountConsumer {
      void accept(Object var1, int var2);
   }
}
