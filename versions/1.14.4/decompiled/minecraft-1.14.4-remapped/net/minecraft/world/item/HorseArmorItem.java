package net.minecraft.world.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class HorseArmorItem extends Item {
   private final int protection;
   private final String texture;

   public HorseArmorItem(int protection, String string, Item.Properties item$Properties) {
      super(item$Properties);
      this.protection = protection;
      this.texture = "textures/entity/horse/armor/horse_armor_" + string + ".png";
   }

   public ResourceLocation getTexture() {
      return new ResourceLocation(this.texture);
   }

   public int getProtection() {
      return this.protection;
   }
}
