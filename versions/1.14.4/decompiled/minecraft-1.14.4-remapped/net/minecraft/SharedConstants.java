package net.minecraft;

import com.google.common.base.Function;
import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.types.constant.NamespacedStringType;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import net.minecraft.DetectedVersion;
import net.minecraft.commands.BrigadierExceptions;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class SharedConstants {
   public static final Level NETTY_LEAK_DETECTION = Level.DISABLED;
   public static boolean IS_RUNNING_IN_IDE;
   public static final char[] ILLEGAL_FILE_CHARACTERS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
   private static GameVersion CURRENT_VERSION;

   public static boolean isAllowedChatCharacter(char c) {
      return c != 167 && c >= 32 && c != 127;
   }

   public static String filterText(String string) {
      StringBuilder var1 = new StringBuilder();

      for(char var5 : string.toCharArray()) {
         if(isAllowedChatCharacter(var5)) {
            var1.append(var5);
         }
      }

      return var1.toString();
   }

   public static String filterUnicodeSupplementary(String string) {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < string.length(); var2 = string.offsetByCodePoints(var2, 1)) {
         int var3 = string.codePointAt(var2);
         if(!Character.isSupplementaryCodePoint(var3)) {
            var1.appendCodePoint(var3);
         } else {
            var1.append('�');
         }
      }

      return var1.toString();
   }

   public static GameVersion getCurrentVersion() {
      if(CURRENT_VERSION == null) {
         CURRENT_VERSION = DetectedVersion.tryDetectVersion();
      }

      return CURRENT_VERSION;
   }

   static {
      ResourceLeakDetector.setLevel(NETTY_LEAK_DETECTION);
      CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
      CommandSyntaxException.BUILT_IN_EXCEPTIONS = new BrigadierExceptions();
      NamespacedStringType.ENSURE_NAMESPACE = NamespacedSchema::ensureNamespaced;
   }
}
