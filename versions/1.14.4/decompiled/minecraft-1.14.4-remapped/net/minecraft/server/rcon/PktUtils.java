package net.minecraft.server.rcon;

import java.nio.charset.StandardCharsets;

public class PktUtils {
   public static final char[] HEX_CHAR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

   public static String stringFromByteArray(byte[] bytes, int var1, int var2) {
      int var3 = var2 - 1;

      int var4;
      for(var4 = var1 > var3?var3:var1; 0 != bytes[var4] && var4 < var3; ++var4) {
         ;
      }

      return new String(bytes, var1, var4 - var1, StandardCharsets.UTF_8);
   }

   public static int intFromByteArray(byte[] bytes, int var1) {
      return intFromByteArray(bytes, var1, bytes.length);
   }

   public static int intFromByteArray(byte[] bytes, int var1, int var2) {
      return 0 > var2 - var1 - 4?0:bytes[var1 + 3] << 24 | (bytes[var1 + 2] & 255) << 16 | (bytes[var1 + 1] & 255) << 8 | bytes[var1] & 255;
   }

   public static int intFromNetworkByteArray(byte[] bytes, int var1, int var2) {
      return 0 > var2 - var1 - 4?0:bytes[var1] << 24 | (bytes[var1 + 1] & 255) << 16 | (bytes[var1 + 2] & 255) << 8 | bytes[var1 + 3] & 255;
   }

   public static String toHexString(byte b) {
      return "" + HEX_CHAR[(b & 240) >>> 4] + HEX_CHAR[b & 15];
   }
}
