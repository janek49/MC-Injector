package net.minecraft.world.level.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;

public class EnchantmentTableBlockEntity extends BlockEntity implements Nameable, TickableBlockEntity {
   public int time;
   public float flip;
   public float oFlip;
   public float flipT;
   public float flipA;
   public float open;
   public float oOpen;
   public float rot;
   public float oRot;
   public float tRot;
   private static final Random RANDOM = new Random();
   private Component name;

   public EnchantmentTableBlockEntity() {
      super(BlockEntityType.ENCHANTING_TABLE);
   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      if(this.hasCustomName()) {
         compoundTag.putString("CustomName", Component.Serializer.toJson(this.name));
      }

      return compoundTag;
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      if(compoundTag.contains("CustomName", 8)) {
         this.name = Component.Serializer.fromJson(compoundTag.getString("CustomName"));
      }

   }

   public void tick() {
      this.oOpen = this.open;
      this.oRot = this.rot;
      Player var1 = this.level.getNearestPlayer((double)((float)this.worldPosition.getX() + 0.5F), (double)((float)this.worldPosition.getY() + 0.5F), (double)((float)this.worldPosition.getZ() + 0.5F), 3.0D, false);
      if(var1 != null) {
         double var2 = var1.x - (double)((float)this.worldPosition.getX() + 0.5F);
         double var4 = var1.z - (double)((float)this.worldPosition.getZ() + 0.5F);
         this.tRot = (float)Mth.atan2(var4, var2);
         this.open += 0.1F;
         if(this.open < 0.5F || RANDOM.nextInt(40) == 0) {
            float var6 = this.flipT;

            while(true) {
               this.flipT += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
               if(var6 != this.flipT) {
                  break;
               }
            }
         }
      } else {
         this.tRot += 0.02F;
         this.open -= 0.1F;
      }

      while(this.rot >= 3.1415927F) {
         this.rot -= 6.2831855F;
      }

      while(this.rot < -3.1415927F) {
         this.rot += 6.2831855F;
      }

      while(this.tRot >= 3.1415927F) {
         this.tRot -= 6.2831855F;
      }

      while(this.tRot < -3.1415927F) {
         this.tRot += 6.2831855F;
      }

      float var2;
      for(var2 = this.tRot - this.rot; var2 >= 3.1415927F; var2 -= 6.2831855F) {
         ;
      }

      while(var2 < -3.1415927F) {
         var2 += 6.2831855F;
      }

      this.rot += var2 * 0.4F;
      this.open = Mth.clamp(this.open, 0.0F, 1.0F);
      ++this.time;
      this.oFlip = this.flip;
      float var3 = (this.flipT - this.flip) * 0.4F;
      float var4 = 0.2F;
      var3 = Mth.clamp(var3, -0.2F, 0.2F);
      this.flipA += (var3 - this.flipA) * 0.9F;
      this.flip += this.flipA;
   }

   public Component getName() {
      return (Component)(this.name != null?this.name:new TranslatableComponent("container.enchant", new Object[0]));
   }

   public void setCustomName(@Nullable Component customName) {
      this.name = customName;
   }

   @Nullable
   public Component getCustomName() {
      return this.name;
   }
}
