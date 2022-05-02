package net.minecraft.client.resources.metadata.animation;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.resources.metadata.animation.VillagerMetadataSectionSerializer;

@ClientJarOnly
public class VillagerMetaDataSection {
   public static final VillagerMetadataSectionSerializer SERIALIZER = new VillagerMetadataSectionSerializer();
   private final VillagerMetaDataSection.Hat hat;

   public VillagerMetaDataSection(VillagerMetaDataSection.Hat hat) {
      this.hat = hat;
   }

   public VillagerMetaDataSection.Hat getHat() {
      return this.hat;
   }

   @ClientJarOnly
   public static enum Hat {
      NONE("none"),
      PARTIAL("partial"),
      FULL("full");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(VillagerMetaDataSection.Hat::getName, (villagerMetaDataSection$Hat) -> {
         return villagerMetaDataSection$Hat;
      }));
      private final String name;

      private Hat(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public static VillagerMetaDataSection.Hat getByName(String name) {
         return (VillagerMetaDataSection.Hat)BY_NAME.getOrDefault(name, NONE);
      }
   }
}
