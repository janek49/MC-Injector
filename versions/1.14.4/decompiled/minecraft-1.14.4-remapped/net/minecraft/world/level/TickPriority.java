package net.minecraft.world.level;

public enum TickPriority {
   EXTREMELY_HIGH(-3),
   VERY_HIGH(-2),
   HIGH(-1),
   NORMAL(0),
   LOW(1),
   VERY_LOW(2),
   EXTREMELY_LOW(3);

   private final int value;

   private TickPriority(int value) {
      this.value = value;
   }

   public static TickPriority byValue(int value) {
      for(TickPriority var4 : values()) {
         if(var4.value == value) {
            return var4;
         }
      }

      if(value < EXTREMELY_HIGH.value) {
         return EXTREMELY_HIGH;
      } else {
         return EXTREMELY_LOW;
      }
   }

   public int getValue() {
      return this.value;
   }
}
