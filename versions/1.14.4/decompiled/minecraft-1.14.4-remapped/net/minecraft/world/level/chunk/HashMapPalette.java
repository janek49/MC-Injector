package net.minecraft.world.level.chunk;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;

public class HashMapPalette implements Palette {
   private final IdMapper registry;
   private final CrudeIncrementalIntIdentityHashBiMap values;
   private final PaletteResize resizeHandler;
   private final Function reader;
   private final Function writer;
   private final int bits;

   public HashMapPalette(IdMapper registry, int bits, PaletteResize resizeHandler, Function reader, Function writer) {
      this.registry = registry;
      this.bits = bits;
      this.resizeHandler = resizeHandler;
      this.reader = reader;
      this.writer = writer;
      this.values = new CrudeIncrementalIntIdentityHashBiMap(1 << bits);
   }

   public int idFor(Object object) {
      int var2 = this.values.getId(object);
      if(var2 == -1) {
         var2 = this.values.add(object);
         if(var2 >= 1 << this.bits) {
            var2 = this.resizeHandler.onResize(this.bits + 1, object);
         }
      }

      return var2;
   }

   public boolean maybeHas(Object object) {
      return this.values.getId(object) != -1;
   }

   @Nullable
   public Object valueFor(int i) {
      return this.values.byId(i);
   }

   public void read(FriendlyByteBuf friendlyByteBuf) {
      this.values.clear();
      int var2 = friendlyByteBuf.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.values.add(this.registry.byId(friendlyByteBuf.readVarInt()));
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      int var2 = this.getSize();
      friendlyByteBuf.writeVarInt(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         friendlyByteBuf.writeVarInt(this.registry.getId(this.values.byId(var3)));
      }

   }

   public int getSerializedSize() {
      int var1 = FriendlyByteBuf.getVarIntSize(this.getSize());

      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         var1 += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values.byId(var2)));
      }

      return var1;
   }

   public int getSize() {
      return this.values.size();
   }

   public void read(ListTag listTag) {
      this.values.clear();

      for(int var2 = 0; var2 < listTag.size(); ++var2) {
         this.values.add(this.reader.apply(listTag.getCompound(var2)));
      }

   }

   public void write(ListTag listTag) {
      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         listTag.add(this.writer.apply(this.values.byId(var2)));
      }

   }
}
