package net.minecraft.world.entity.ai.attributes;

import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.attributes.Attribute;

public abstract class BaseAttribute implements Attribute {
   private final Attribute parent;
   private final String name;
   private final double defaultValue;
   private boolean syncable;

   protected BaseAttribute(@Nullable Attribute parent, String name, double defaultValue) {
      this.parent = parent;
      this.name = name;
      this.defaultValue = defaultValue;
      if(name == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }
   }

   public String getName() {
      return this.name;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public boolean isClientSyncable() {
      return this.syncable;
   }

   public BaseAttribute setSyncable(boolean syncable) {
      this.syncable = syncable;
      return this;
   }

   @Nullable
   public Attribute getParentAttribute() {
      return this.parent;
   }

   public int hashCode() {
      return this.name.hashCode();
   }

   public boolean equals(Object object) {
      return object instanceof Attribute && this.name.equals(((Attribute)object).getName());
   }
}
