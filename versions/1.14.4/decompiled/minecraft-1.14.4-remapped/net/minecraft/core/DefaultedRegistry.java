package net.minecraft.core;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;

public class DefaultedRegistry extends MappedRegistry {
   private final ResourceLocation defaultKey;
   private Object defaultValue;

   public DefaultedRegistry(String string) {
      this.defaultKey = new ResourceLocation(string);
   }

   public Object registerMapping(int var1, ResourceLocation resourceLocation, Object defaultValue) {
      if(this.defaultKey.equals(resourceLocation)) {
         this.defaultValue = defaultValue;
      }

      return super.registerMapping(var1, resourceLocation, defaultValue);
   }

   public int getId(@Nullable Object object) {
      int var2 = super.getId(object);
      return var2 == -1?super.getId(this.defaultValue):var2;
   }

   @Nonnull
   public ResourceLocation getKey(Object object) {
      ResourceLocation resourceLocation = super.getKey(object);
      return resourceLocation == null?this.defaultKey:resourceLocation;
   }

   @Nonnull
   public Object get(@Nullable ResourceLocation resourceLocation) {
      T object = super.get(resourceLocation);
      return object == null?this.defaultValue:object;
   }

   @Nonnull
   public Object byId(int id) {
      T object = super.byId(id);
      return object == null?this.defaultValue:object;
   }

   @Nonnull
   public Object getRandom(Random random) {
      T object = super.getRandom(random);
      return object == null?this.defaultValue:object;
   }

   public ResourceLocation getDefaultKey() {
      return this.defaultKey;
   }
}
