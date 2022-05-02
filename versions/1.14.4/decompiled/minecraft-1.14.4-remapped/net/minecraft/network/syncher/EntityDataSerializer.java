package net.minecraft.network.syncher;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;

public interface EntityDataSerializer {
   void write(FriendlyByteBuf var1, Object var2);

   Object read(FriendlyByteBuf var1);

   default EntityDataAccessor createAccessor(int i) {
      return new EntityDataAccessor(i, this);
   }

   Object copy(Object var1);
}
