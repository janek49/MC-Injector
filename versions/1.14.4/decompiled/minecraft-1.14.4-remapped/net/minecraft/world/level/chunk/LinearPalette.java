package net.minecraft.world.level.chunk;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.IdMapper;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PaletteResize;
import org.apache.commons.lang3.ArrayUtils;

public class LinearPalette implements Palette {
   private final IdMapper registry;
   private final Object[] values;
   private final PaletteResize resizeHandler;
   private final Function reader;
   private final int bits;
   private int size;

   public LinearPalette(IdMapper registry, int bits, PaletteResize resizeHandler, Function reader) {
      this.registry = registry;
      this.values = (Object[])(new Object[1 << bits]);
      this.bits = bits;
      this.resizeHandler = resizeHandler;
      this.reader = reader;
   }

   public int idFor(Object object) {
      for(int var2 = 0; var2 < this.size; ++var2) {
         if(this.values[var2] == object) {
            return var2;
         }
      }

      int var2 = this.size;
      if(var2 < this.values.length) {
         this.values[var2] = object;
         ++this.size;
         return var2;
      } else {
         return this.resizeHandler.onResize(this.bits + 1, object);
      }
   }

   public boolean maybeHas(Object object) {
      return ArrayUtils.contains(this.values, object);
   }

   @Nullable
   public Object valueFor(int i) {
      return i >= 0 && i < this.size?this.values[i]:null;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) {
      this.size = friendlyByteBuf.readVarInt();

      for(int var2 = 0; var2 < this.size; ++var2) {
         this.values[var2] = this.registry.byId(friendlyByteBuf.readVarInt());
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.writeVarInt(this.size);

      for(int var2 = 0; var2 < this.size; ++var2) {
         friendlyByteBuf.writeVarInt(this.registry.getId(this.values[var2]));
      }

   }

   public int getSerializedSize() {
      int var1 = FriendlyByteBuf.getVarIntSize(this.getSize());

      for(int var2 = 0; var2 < this.getSize(); ++var2) {
         var1 += FriendlyByteBuf.getVarIntSize(this.registry.getId(this.values[var2]));
      }

      return var1;
   }

   public int getSize() {
      return this.size;
   }

   public void read(ListTag listTag) {
      for(int var2 = 0; var2 < listTag.size(); ++var2) {
         this.values[var2] = this.reader.apply(listTag.getCompound(var2));
      }

      this.size = listTag.size();
   }
}
