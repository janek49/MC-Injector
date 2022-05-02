package net.minecraft.nbt;

public class NbtAccounter {
   public static final NbtAccounter UNLIMITED = new NbtAccounter(0L) {
      public void accountBits(long l) {
      }
   };
   private final long quota;
   private long usage;

   public NbtAccounter(long quota) {
      this.quota = quota;
   }

   public void accountBits(long l) {
      this.usage += l / 8L;
      if(this.usage > this.quota) {
         throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.usage + "bytes where max allowed: " + this.quota);
      }
   }
}
