package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PumpkinBlock;

public enum EnchantmentCategory {
   ALL {
      public boolean canEnchant(Item item) {
         for(EnchantmentCategory var5 : EnchantmentCategory.values()) {
            if(var5 != EnchantmentCategory.ALL && var5.canEnchant(item)) {
               return true;
            }
         }

         return false;
      }
   },
   ARMOR {
      public boolean canEnchant(Item item) {
         return item instanceof ArmorItem;
      }
   },
   ARMOR_FEET {
      public boolean canEnchant(Item item) {
         return item instanceof ArmorItem && ((ArmorItem)item).getSlot() == EquipmentSlot.FEET;
      }
   },
   ARMOR_LEGS {
      public boolean canEnchant(Item item) {
         return item instanceof ArmorItem && ((ArmorItem)item).getSlot() == EquipmentSlot.LEGS;
      }
   },
   ARMOR_CHEST {
      public boolean canEnchant(Item item) {
         return item instanceof ArmorItem && ((ArmorItem)item).getSlot() == EquipmentSlot.CHEST;
      }
   },
   ARMOR_HEAD {
      public boolean canEnchant(Item item) {
         return item instanceof ArmorItem && ((ArmorItem)item).getSlot() == EquipmentSlot.HEAD;
      }
   },
   WEAPON {
      public boolean canEnchant(Item item) {
         return item instanceof SwordItem;
      }
   },
   DIGGER {
      public boolean canEnchant(Item item) {
         return item instanceof DiggerItem;
      }
   },
   FISHING_ROD {
      public boolean canEnchant(Item item) {
         return item instanceof FishingRodItem;
      }
   },
   TRIDENT {
      public boolean canEnchant(Item item) {
         return item instanceof TridentItem;
      }
   },
   BREAKABLE {
      public boolean canEnchant(Item item) {
         return item.canBeDepleted();
      }
   },
   BOW {
      public boolean canEnchant(Item item) {
         return item instanceof BowItem;
      }
   },
   WEARABLE {
      public boolean canEnchant(Item item) {
         Block var2 = Block.byItem(item);
         return item instanceof ArmorItem || item instanceof ElytraItem || var2 instanceof AbstractSkullBlock || var2 instanceof PumpkinBlock;
      }
   },
   CROSSBOW {
      public boolean canEnchant(Item item) {
         return item instanceof CrossbowItem;
      }
   };

   private EnchantmentCategory() {
   }

   public abstract boolean canEnchant(Item var1);
}
