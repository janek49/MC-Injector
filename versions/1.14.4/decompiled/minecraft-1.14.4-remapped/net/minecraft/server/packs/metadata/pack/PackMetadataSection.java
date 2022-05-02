package net.minecraft.server.packs.metadata.pack;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.metadata.pack.PackMetadataSectionSerializer;

public class PackMetadataSection {
   public static final PackMetadataSectionSerializer SERIALIZER = new PackMetadataSectionSerializer();
   private final Component description;
   private final int packFormat;

   public PackMetadataSection(Component description, int packFormat) {
      this.description = description;
      this.packFormat = packFormat;
   }

   public Component getDescription() {
      return this.description;
   }

   public int getPackFormat() {
      return this.packFormat;
   }
}
