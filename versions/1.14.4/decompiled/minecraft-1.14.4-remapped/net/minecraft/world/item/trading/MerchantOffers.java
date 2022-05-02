package net.minecraft.world.item.trading;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;

public class MerchantOffers extends ArrayList {
   public MerchantOffers() {
   }

   public MerchantOffers(CompoundTag compoundTag) {
      ListTag var2 = compoundTag.getList("Recipes", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         this.add(new MerchantOffer(var2.getCompound(var3)));
      }

   }

   @Nullable
   public MerchantOffer getRecipeFor(ItemStack var1, ItemStack var2, int var3) {
      if(var3 > 0 && var3 < this.size()) {
         MerchantOffer merchantOffer = (MerchantOffer)this.get(var3);
         return merchantOffer.satisfiedBy(var1, var2)?merchantOffer:null;
      } else {
         for(int var4 = 0; var4 < this.size(); ++var4) {
            MerchantOffer var5 = (MerchantOffer)this.get(var4);
            if(var5.satisfiedBy(var1, var2)) {
               return var5;
            }
         }

         return null;
      }
   }

   public void writeToStream(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.writeByte((byte)(this.size() & 255));

      for(int var2 = 0; var2 < this.size(); ++var2) {
         MerchantOffer var3 = (MerchantOffer)this.get(var2);
         friendlyByteBuf.writeItem(var3.getBaseCostA());
         friendlyByteBuf.writeItem(var3.getResult());
         ItemStack var4 = var3.getCostB();
         friendlyByteBuf.writeBoolean(!var4.isEmpty());
         if(!var4.isEmpty()) {
            friendlyByteBuf.writeItem(var4);
         }

         friendlyByteBuf.writeBoolean(var3.isOutOfStock());
         friendlyByteBuf.writeInt(var3.getUses());
         friendlyByteBuf.writeInt(var3.getMaxUses());
         friendlyByteBuf.writeInt(var3.getXp());
         friendlyByteBuf.writeInt(var3.getSpecialPriceDiff());
         friendlyByteBuf.writeFloat(var3.getPriceMultiplier());
         friendlyByteBuf.writeInt(var3.getDemand());
      }

   }

   public static MerchantOffers createFromStream(FriendlyByteBuf friendlyByteBuf) {
      MerchantOffers merchantOffers = new MerchantOffers();
      int var2 = friendlyByteBuf.readByte() & 255;

      for(int var3 = 0; var3 < var2; ++var3) {
         ItemStack var4 = friendlyByteBuf.readItem();
         ItemStack var5 = friendlyByteBuf.readItem();
         ItemStack var6 = ItemStack.EMPTY;
         if(friendlyByteBuf.readBoolean()) {
            var6 = friendlyByteBuf.readItem();
         }

         boolean var7 = friendlyByteBuf.readBoolean();
         int var8 = friendlyByteBuf.readInt();
         int var9 = friendlyByteBuf.readInt();
         int var10 = friendlyByteBuf.readInt();
         int var11 = friendlyByteBuf.readInt();
         float var12 = friendlyByteBuf.readFloat();
         int var13 = friendlyByteBuf.readInt();
         MerchantOffer var14 = new MerchantOffer(var4, var6, var5, var8, var9, var10, var12, var13);
         if(var7) {
            var14.setToOutOfStock();
         }

         var14.setSpecialPriceDiff(var11);
         merchantOffers.add(var14);
      }

      return merchantOffers;
   }

   public CompoundTag createTag() {
      CompoundTag compoundTag = new CompoundTag();
      ListTag var2 = new ListTag();

      for(int var3 = 0; var3 < this.size(); ++var3) {
         MerchantOffer var4 = (MerchantOffer)this.get(var3);
         var2.add(var4.createTag());
      }

      compoundTag.put("Recipes", var2);
      return compoundTag;
   }
}
