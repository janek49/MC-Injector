package net.minecraft.world.level.chunk;

import net.minecraft.core.IdMapper;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.Palette;

public class GlobalPalette implements Palette {
   private final IdMapper registry;
   private final Object defaultValue;

   public GlobalPalette(IdMapper registry, Object defaultValue) {
      this.registry = registry;
      this.defaultValue = defaultValue;
   }

   public int idFor(Object object) {
      int var2 = this.registry.getId(object);
      return var2 == -1?0:var2;
   }

   public boolean maybeHas(Object object) {
      return true;
   }

   public Object valueFor(int i) {
      T object = this.registry.byId(i);
      return object == null?this.defaultValue:object;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) {
   }

   public void write(FriendlyByteBuf friendlyByteBuf) {
   }

   public int getSerializedSize() {
      return FriendlyByteBuf.getVarIntSize(0);
   }

   public void read(ListTag listTag) {
   }
}
