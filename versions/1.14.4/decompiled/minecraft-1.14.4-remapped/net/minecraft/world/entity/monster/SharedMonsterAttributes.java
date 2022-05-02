package net.minecraft.world.entity.monster;

import java.util.Collection;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Attribute MAX_HEALTH = (new RangedAttribute((Attribute)null, "generic.maxHealth", 20.0D, 0.0D, 1024.0D)).importLegacyName("Max Health").setSyncable(true);
   public static final Attribute FOLLOW_RANGE = (new RangedAttribute((Attribute)null, "generic.followRange", 32.0D, 0.0D, 2048.0D)).importLegacyName("Follow Range");
   public static final Attribute KNOCKBACK_RESISTANCE = (new RangedAttribute((Attribute)null, "generic.knockbackResistance", 0.0D, 0.0D, 1.0D)).importLegacyName("Knockback Resistance");
   public static final Attribute MOVEMENT_SPEED = (new RangedAttribute((Attribute)null, "generic.movementSpeed", 0.699999988079071D, 0.0D, 1024.0D)).importLegacyName("Movement Speed").setSyncable(true);
   public static final Attribute FLYING_SPEED = (new RangedAttribute((Attribute)null, "generic.flyingSpeed", 0.4000000059604645D, 0.0D, 1024.0D)).importLegacyName("Flying Speed").setSyncable(true);
   public static final Attribute ATTACK_DAMAGE = new RangedAttribute((Attribute)null, "generic.attackDamage", 2.0D, 0.0D, 2048.0D);
   public static final Attribute ATTACK_KNOCKBACK = new RangedAttribute((Attribute)null, "generic.attackKnockback", 0.0D, 0.0D, 5.0D);
   public static final Attribute ATTACK_SPEED = (new RangedAttribute((Attribute)null, "generic.attackSpeed", 4.0D, 0.0D, 1024.0D)).setSyncable(true);
   public static final Attribute ARMOR = (new RangedAttribute((Attribute)null, "generic.armor", 0.0D, 0.0D, 30.0D)).setSyncable(true);
   public static final Attribute ARMOR_TOUGHNESS = (new RangedAttribute((Attribute)null, "generic.armorToughness", 0.0D, 0.0D, 20.0D)).setSyncable(true);
   public static final Attribute LUCK = (new RangedAttribute((Attribute)null, "generic.luck", 0.0D, -1024.0D, 1024.0D)).setSyncable(true);

   public static ListTag saveAttributes(BaseAttributeMap baseAttributeMap) {
      ListTag listTag = new ListTag();

      for(AttributeInstance var3 : baseAttributeMap.getAttributes()) {
         listTag.add(saveAttribute(var3));
      }

      return listTag;
   }

   private static CompoundTag saveAttribute(AttributeInstance attributeInstance) {
      CompoundTag compoundTag = new CompoundTag();
      Attribute var2 = attributeInstance.getAttribute();
      compoundTag.putString("Name", var2.getName());
      compoundTag.putDouble("Base", attributeInstance.getBaseValue());
      Collection<AttributeModifier> var3 = attributeInstance.getModifiers();
      if(var3 != null && !var3.isEmpty()) {
         ListTag var4 = new ListTag();

         for(AttributeModifier var6 : var3) {
            if(var6.isSerializable()) {
               var4.add(saveAttributeModifier(var6));
            }
         }

         compoundTag.put("Modifiers", var4);
      }

      return compoundTag;
   }

   public static CompoundTag saveAttributeModifier(AttributeModifier attributeModifier) {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.putString("Name", attributeModifier.getName());
      compoundTag.putDouble("Amount", attributeModifier.getAmount());
      compoundTag.putInt("Operation", attributeModifier.getOperation().toValue());
      compoundTag.putUUID("UUID", attributeModifier.getId());
      return compoundTag;
   }

   public static void loadAttributes(BaseAttributeMap baseAttributeMap, ListTag listTag) {
      for(int var2 = 0; var2 < listTag.size(); ++var2) {
         CompoundTag var3 = listTag.getCompound(var2);
         AttributeInstance var4 = baseAttributeMap.getInstance(var3.getString("Name"));
         if(var4 == null) {
            LOGGER.warn("Ignoring unknown attribute \'{}\'", var3.getString("Name"));
         } else {
            loadAttribute(var4, var3);
         }
      }

   }

   private static void loadAttribute(AttributeInstance attributeInstance, CompoundTag compoundTag) {
      attributeInstance.setBaseValue(compoundTag.getDouble("Base"));
      if(compoundTag.contains("Modifiers", 9)) {
         ListTag var2 = compoundTag.getList("Modifiers", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            AttributeModifier var4 = loadAttributeModifier(var2.getCompound(var3));
            if(var4 != null) {
               AttributeModifier var5 = attributeInstance.getModifier(var4.getId());
               if(var5 != null) {
                  attributeInstance.removeModifier(var5);
               }

               attributeInstance.addModifier(var4);
            }
         }
      }

   }

   @Nullable
   public static AttributeModifier loadAttributeModifier(CompoundTag compoundTag) {
      UUID var1 = compoundTag.getUUID("UUID");

      try {
         AttributeModifier.Operation var2 = AttributeModifier.Operation.fromValue(compoundTag.getInt("Operation"));
         return new AttributeModifier(var1, compoundTag.getString("Name"), compoundTag.getDouble("Amount"), var2);
      } catch (Exception var3) {
         LOGGER.warn("Unable to create attribute: {}", var3.getMessage());
         return null;
      }
   }
}
