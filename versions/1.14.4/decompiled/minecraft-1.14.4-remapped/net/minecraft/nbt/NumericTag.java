package net.minecraft.nbt;

import net.minecraft.nbt.Tag;

public abstract class NumericTag implements Tag {
   public abstract long getAsLong();

   public abstract int getAsInt();

   public abstract short getAsShort();

   public abstract byte getAsByte();

   public abstract double getAsDouble();

   public abstract float getAsFloat();

   public abstract Number getAsNumber();
}
