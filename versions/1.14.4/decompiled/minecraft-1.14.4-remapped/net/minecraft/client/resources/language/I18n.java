package net.minecraft.client.resources.language;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.resources.language.Locale;

@ClientJarOnly
public class I18n {
   private static Locale locale;

   static void setLocale(Locale locale) {
      locale = locale;
   }

   public static String get(String var0, Object... objects) {
      return locale.get(var0, objects);
   }

   public static boolean exists(String string) {
      return locale.has(string);
   }
}
