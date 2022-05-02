package net.minecraft.network.chat;

public enum ChatType {
   CHAT((byte)0, false),
   SYSTEM((byte)1, true),
   GAME_INFO((byte)2, true);

   private final byte index;
   private final boolean interrupt;

   private ChatType(byte index, boolean interrupt) {
      this.index = index;
      this.interrupt = interrupt;
   }

   public byte getIndex() {
      return this.index;
   }

   public static ChatType getForIndex(byte b) {
      for(ChatType var4 : values()) {
         if(b == var4.index) {
            return var4;
         }
      }

      return CHAT;
   }

   public boolean shouldInterrupt() {
      return this.interrupt;
   }
}
