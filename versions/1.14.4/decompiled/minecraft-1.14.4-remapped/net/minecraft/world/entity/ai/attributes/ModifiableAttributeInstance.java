package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;

public class ModifiableAttributeInstance implements AttributeInstance {
   private final BaseAttributeMap attributeMap;
   private final Attribute attribute;
   private final Map modifiers = Maps.newEnumMap(AttributeModifier.Operation.class);
   private final Map modifiersByName = Maps.newHashMap();
   private final Map modifierById = Maps.newHashMap();
   private double baseValue;
   private boolean dirty = true;
   private double cachedValue;

   public ModifiableAttributeInstance(BaseAttributeMap attributeMap, Attribute attribute) {
      this.attributeMap = attributeMap;
      this.attribute = attribute;
      this.baseValue = attribute.getDefaultValue();

      for(AttributeModifier.Operation var6 : AttributeModifier.Operation.values()) {
         this.modifiers.put(var6, Sets.newHashSet());
      }

   }

   public Attribute getAttribute() {
      return this.attribute;
   }

   public double getBaseValue() {
      return this.baseValue;
   }

   public void setBaseValue(double baseValue) {
      if(baseValue != this.getBaseValue()) {
         this.baseValue = baseValue;
         this.setDirty();
      }
   }

   public Collection getModifiers(AttributeModifier.Operation attributeModifier$Operation) {
      return (Collection)this.modifiers.get(attributeModifier$Operation);
   }

   public Collection getModifiers() {
      Set<AttributeModifier> var1 = Sets.newHashSet();

      for(AttributeModifier.Operation var5 : AttributeModifier.Operation.values()) {
         var1.addAll(this.getModifiers(var5));
      }

      return var1;
   }

   @Nullable
   public AttributeModifier getModifier(UUID uUID) {
      return (AttributeModifier)this.modifierById.get(uUID);
   }

   public boolean hasModifier(AttributeModifier attributeModifier) {
      return this.modifierById.get(attributeModifier.getId()) != null;
   }

   public void addModifier(AttributeModifier attributeModifier) {
      if(this.getModifier(attributeModifier.getId()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         Set<AttributeModifier> var2 = (Set)this.modifiersByName.computeIfAbsent(attributeModifier.getName(), (string) -> {
            return Sets.newHashSet();
         });
         ((Set)this.modifiers.get(attributeModifier.getOperation())).add(attributeModifier);
         var2.add(attributeModifier);
         this.modifierById.put(attributeModifier.getId(), attributeModifier);
         this.setDirty();
      }
   }

   protected void setDirty() {
      this.dirty = true;
      this.attributeMap.onAttributeModified(this);
   }

   public void removeModifier(AttributeModifier attributeModifier) {
      for(AttributeModifier.Operation var5 : AttributeModifier.Operation.values()) {
         ((Set)this.modifiers.get(var5)).remove(attributeModifier);
      }

      Set<AttributeModifier> var2 = (Set)this.modifiersByName.get(attributeModifier.getName());
      if(var2 != null) {
         var2.remove(attributeModifier);
         if(var2.isEmpty()) {
            this.modifiersByName.remove(attributeModifier.getName());
         }
      }

      this.modifierById.remove(attributeModifier.getId());
      this.setDirty();
   }

   public void removeModifier(UUID uUID) {
      AttributeModifier var2 = this.getModifier(uUID);
      if(var2 != null) {
         this.removeModifier(var2);
      }

   }

   public void removeModifiers() {
      Collection<AttributeModifier> var1 = this.getModifiers();
      if(var1 != null) {
         for(AttributeModifier var3 : Lists.newArrayList(var1)) {
            this.removeModifier(var3);
         }

      }
   }

   public double getValue() {
      if(this.dirty) {
         this.cachedValue = this.calculateValue();
         this.dirty = false;
      }

      return this.cachedValue;
   }

   private double calculateValue() {
      double var1 = this.getBaseValue();

      for(AttributeModifier var4 : this.getAppliedModifiers(AttributeModifier.Operation.ADDITION)) {
         var1 += var4.getAmount();
      }

      double var3 = var1;

      for(AttributeModifier var6 : this.getAppliedModifiers(AttributeModifier.Operation.MULTIPLY_BASE)) {
         var3 += var1 * var6.getAmount();
      }

      for(AttributeModifier var6 : this.getAppliedModifiers(AttributeModifier.Operation.MULTIPLY_TOTAL)) {
         var3 *= 1.0D + var6.getAmount();
      }

      return this.attribute.sanitizeValue(var3);
   }

   private Collection getAppliedModifiers(AttributeModifier.Operation attributeModifier$Operation) {
      Set<AttributeModifier> var2 = Sets.newHashSet(this.getModifiers(attributeModifier$Operation));

      for(Attribute var3 = this.attribute.getParentAttribute(); var3 != null; var3 = var3.getParentAttribute()) {
         AttributeInstance var4 = this.attributeMap.getInstance(var3);
         if(var4 != null) {
            var2.addAll(var4.getModifiers(attributeModifier$Operation));
         }
      }

      return var2;
   }
}
