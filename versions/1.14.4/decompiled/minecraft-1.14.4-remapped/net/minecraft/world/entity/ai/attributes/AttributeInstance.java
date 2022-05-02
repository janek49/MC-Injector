package net.minecraft.world.entity.ai.attributes;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface AttributeInstance {
   Attribute getAttribute();

   double getBaseValue();

   void setBaseValue(double var1);

   Collection getModifiers(AttributeModifier.Operation var1);

   Collection getModifiers();

   boolean hasModifier(AttributeModifier var1);

   @Nullable
   AttributeModifier getModifier(UUID var1);

   void addModifier(AttributeModifier var1);

   void removeModifier(AttributeModifier var1);

   void removeModifier(UUID var1);

   void removeModifiers();

   double getValue();
}
