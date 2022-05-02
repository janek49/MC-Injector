package net.minecraft.client.resources.model;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class ModelResourceLocation extends ResourceLocation {
   private final String variant;

   protected ModelResourceLocation(String[] strings) {
      super(strings);
      this.variant = strings[2].toLowerCase(Locale.ROOT);
   }

   public ModelResourceLocation(String string) {
      this(decompose(string));
   }

   public ModelResourceLocation(ResourceLocation resourceLocation, String string) {
      this(resourceLocation.toString(), string);
   }

   public ModelResourceLocation(String var1, String var2) {
      this(decompose(var1 + '#' + var2));
   }

   protected static String[] decompose(String string) {
      String[] strings = new String[]{null, string, ""};
      int var2 = string.indexOf(35);
      String var3 = string;
      if(var2 >= 0) {
         strings[2] = string.substring(var2 + 1, string.length());
         if(var2 > 1) {
            var3 = string.substring(0, var2);
         }
      }

      System.arraycopy(ResourceLocation.decompose(var3, ':'), 0, strings, 0, 2);
      return strings;
   }

   public String getVariant() {
      return this.variant;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object instanceof ModelResourceLocation && super.equals(object)) {
         ModelResourceLocation var2 = (ModelResourceLocation)object;
         return this.variant.equals(var2.variant);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 31 * super.hashCode() + this.variant.hashCode();
   }

   public String toString() {
      return super.toString() + '#' + this.variant;
   }
}
