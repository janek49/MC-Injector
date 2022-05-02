package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class DefaultPlayerSkin {
   private static final ResourceLocation STEVE_SKIN_LOCATION = new ResourceLocation("textures/entity/steve.png");
   private static final ResourceLocation ALEX_SKIN_LOCATION = new ResourceLocation("textures/entity/alex.png");

   public static ResourceLocation getDefaultSkin() {
      return STEVE_SKIN_LOCATION;
   }

   public static ResourceLocation getDefaultSkin(UUID uUID) {
      return isAlexDefault(uUID)?ALEX_SKIN_LOCATION:STEVE_SKIN_LOCATION;
   }

   public static String getSkinModelName(UUID uUID) {
      return isAlexDefault(uUID)?"slim":"default";
   }

   private static boolean isAlexDefault(UUID uUID) {
      return (uUID.hashCode() & 1) == 1;
   }
}
