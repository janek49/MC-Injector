package net.minecraft.client.resources;

import com.fox2code.repacker.ClientJarOnly;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.resources.AssetIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPack;

@ClientJarOnly
public class DefaultClientResourcePack extends VanillaPack {
   private final AssetIndex assetIndex;

   public DefaultClientResourcePack(AssetIndex assetIndex) {
      super(new String[]{"minecraft", "realms"});
      this.assetIndex = assetIndex;
   }

   @Nullable
   protected InputStream getResourceAsStream(PackType packType, ResourceLocation resourceLocation) {
      if(packType == PackType.CLIENT_RESOURCES) {
         File var3 = this.assetIndex.getFile(resourceLocation);
         if(var3 != null && var3.exists()) {
            try {
               return new FileInputStream(var3);
            } catch (FileNotFoundException var5) {
               ;
            }
         }
      }

      return super.getResourceAsStream(packType, resourceLocation);
   }

   public boolean hasResource(PackType packType, ResourceLocation resourceLocation) {
      if(packType == PackType.CLIENT_RESOURCES) {
         File var3 = this.assetIndex.getFile(resourceLocation);
         if(var3 != null && var3.exists()) {
            return true;
         }
      }

      return super.hasResource(packType, resourceLocation);
   }

   @Nullable
   protected InputStream getResourceAsStream(String string) {
      File var2 = this.assetIndex.getFile(string);
      if(var2 != null && var2.exists()) {
         try {
            return new FileInputStream(var2);
         } catch (FileNotFoundException var4) {
            ;
         }
      }

      return super.getResourceAsStream(string);
   }

   public Collection getResources(PackType packType, String string, int var3, Predicate predicate) {
      Collection<ResourceLocation> collection = super.getResources(packType, string, var3, predicate);
      collection.addAll((Collection)this.assetIndex.getFiles(string, var3, predicate).stream().map(ResourceLocation::<init>).collect(Collectors.toList()));
      return collection;
   }
}
