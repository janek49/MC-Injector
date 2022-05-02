package net.minecraft.network.syncher;

import net.minecraft.network.syncher.EntityDataSerializer;

public class EntityDataAccessor {
   private final int id;
   private final EntityDataSerializer serializer;

   public EntityDataAccessor(int id, EntityDataSerializer serializer) {
      this.id = id;
      this.serializer = serializer;
   }

   public int getId() {
      return this.id;
   }

   public EntityDataSerializer getSerializer() {
      return this.serializer;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(object != null && this.getClass() == object.getClass()) {
         EntityDataAccessor<?> var2 = (EntityDataAccessor)object;
         return this.id == var2.id;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id;
   }

   public String toString() {
      return "<entity data: " + this.id + ">";
   }
}
