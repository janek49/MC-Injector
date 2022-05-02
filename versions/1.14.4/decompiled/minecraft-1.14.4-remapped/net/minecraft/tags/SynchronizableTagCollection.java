package net.minecraft.tags;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;

public class SynchronizableTagCollection extends TagCollection {
   private final Registry registry;

   public SynchronizableTagCollection(Registry registry, String var2, String var3) {
      super(registry::getOptional, var2, false, var3);
      this.registry = registry;
   }

   public void serializeToNetwork(FriendlyByteBuf friendlyByteBuf) {
      Map<ResourceLocation, Tag<T>> var2 = this.getAllTags();
      friendlyByteBuf.writeVarInt(var2.size());

      for(Entry<ResourceLocation, Tag<T>> var4 : var2.entrySet()) {
         friendlyByteBuf.writeResourceLocation((ResourceLocation)var4.getKey());
         friendlyByteBuf.writeVarInt(((Tag)var4.getValue()).getValues().size());

         for(T var6 : ((Tag)var4.getValue()).getValues()) {
            friendlyByteBuf.writeVarInt(this.registry.getId(var6));
         }
      }

   }

   public void loadFromNetwork(FriendlyByteBuf friendlyByteBuf) {
      Map<ResourceLocation, Tag<T>> var2 = Maps.newHashMap();
      int var3 = friendlyByteBuf.readVarInt();

      for(int var4 = 0; var4 < var3; ++var4) {
         ResourceLocation var5 = friendlyByteBuf.readResourceLocation();
         int var6 = friendlyByteBuf.readVarInt();
         Tag.Builder<T> var7 = Tag.Builder.tag();

         for(int var8 = 0; var8 < var6; ++var8) {
            var7.add(this.registry.byId(friendlyByteBuf.readVarInt()));
         }

         var2.put(var5, var7.build(var5));
      }

      this.replace(var2);
   }
}
