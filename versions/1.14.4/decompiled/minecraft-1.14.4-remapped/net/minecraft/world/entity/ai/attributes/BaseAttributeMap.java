package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.InsensitiveStringMap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public abstract class BaseAttributeMap {
   protected final Map attributesByObject = Maps.newHashMap();
   protected final Map attributesByName = new InsensitiveStringMap();
   protected final Multimap descendantsByParent = HashMultimap.create();

   @Nullable
   public AttributeInstance getInstance(Attribute attribute) {
      return (AttributeInstance)this.attributesByObject.get(attribute);
   }

   @Nullable
   public AttributeInstance getInstance(String string) {
      return (AttributeInstance)this.attributesByName.get(string);
   }

   public AttributeInstance registerAttribute(Attribute attribute) {
      if(this.attributesByName.containsKey(attribute.getName())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      } else {
         AttributeInstance attributeInstance = this.createAttributeInstance(attribute);
         this.attributesByName.put(attribute.getName(), attributeInstance);
         this.attributesByObject.put(attribute, attributeInstance);

         for(Attribute var3 = attribute.getParentAttribute(); var3 != null; var3 = var3.getParentAttribute()) {
            this.descendantsByParent.put(var3, attribute);
         }

         return attributeInstance;
      }
   }

   protected abstract AttributeInstance createAttributeInstance(Attribute var1);

   public Collection getAttributes() {
      return this.attributesByName.values();
   }

   public void onAttributeModified(AttributeInstance attributeInstance) {
   }

   public void removeAttributeModifiers(Multimap multimap) {
      for(Entry<String, AttributeModifier> var3 : multimap.entries()) {
         AttributeInstance var4 = this.getInstance((String)var3.getKey());
         if(var4 != null) {
            var4.removeModifier((AttributeModifier)var3.getValue());
         }
      }

   }

   public void addAttributeModifiers(Multimap multimap) {
      for(Entry<String, AttributeModifier> var3 : multimap.entries()) {
         AttributeInstance var4 = this.getInstance((String)var3.getKey());
         if(var4 != null) {
            var4.removeModifier((AttributeModifier)var3.getValue());
            var4.addModifier((AttributeModifier)var3.getValue());
         }
      }

   }
}
