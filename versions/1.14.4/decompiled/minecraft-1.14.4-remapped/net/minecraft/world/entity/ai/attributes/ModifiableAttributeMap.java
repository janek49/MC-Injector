package net.minecraft.world.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.InsensitiveStringMap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.world.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class ModifiableAttributeMap extends BaseAttributeMap {
   private final Set dirtyAttributes = Sets.newHashSet();
   protected final Map attributesByLegacy = new InsensitiveStringMap();

   public ModifiableAttributeInstance getInstance(Attribute attribute) {
      return (ModifiableAttributeInstance)super.getInstance(attribute);
   }

   public ModifiableAttributeInstance getInstance(String string) {
      AttributeInstance var2 = super.getInstance(string);
      if(var2 == null) {
         var2 = (AttributeInstance)this.attributesByLegacy.get(string);
      }

      return (ModifiableAttributeInstance)var2;
   }

   public AttributeInstance registerAttribute(Attribute attribute) {
      AttributeInstance attributeInstance = super.registerAttribute(attribute);
      if(attribute instanceof RangedAttribute && ((RangedAttribute)attribute).getImportLegacyName() != null) {
         this.attributesByLegacy.put(((RangedAttribute)attribute).getImportLegacyName(), attributeInstance);
      }

      return attributeInstance;
   }

   protected AttributeInstance createAttributeInstance(Attribute attribute) {
      return new ModifiableAttributeInstance(this, attribute);
   }

   public void onAttributeModified(AttributeInstance attributeInstance) {
      if(attributeInstance.getAttribute().isClientSyncable()) {
         this.dirtyAttributes.add(attributeInstance);
      }

      for(Attribute var3 : this.descendantsByParent.get(attributeInstance.getAttribute())) {
         ModifiableAttributeInstance var4 = this.getInstance(var3);
         if(var4 != null) {
            var4.setDirty();
         }
      }

   }

   public Set getDirtyAttributes() {
      return this.dirtyAttributes;
   }

   public Collection getSyncableAttributes() {
      Set<AttributeInstance> var1 = Sets.newHashSet();

      for(AttributeInstance var3 : this.getAttributes()) {
         if(var3.getAttribute().isClientSyncable()) {
            var1.add(var3);
         }
      }

      return var1;
   }

   // $FF: synthetic method
   public AttributeInstance getInstance(String var1) {
      return this.getInstance(var1);
   }

   // $FF: synthetic method
   public AttributeInstance getInstance(Attribute var1) {
      return this.getInstance(var1);
   }
}
