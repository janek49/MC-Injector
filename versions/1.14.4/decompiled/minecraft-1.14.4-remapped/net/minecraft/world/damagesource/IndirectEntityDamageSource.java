package net.minecraft.world.damagesource;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class IndirectEntityDamageSource extends EntityDamageSource {
   private final Entity owner;

   public IndirectEntityDamageSource(String string, Entity var2, @Nullable Entity owner) {
      super(string, var2);
      this.owner = owner;
   }

   @Nullable
   public Entity getDirectEntity() {
      return this.entity;
   }

   @Nullable
   public Entity getEntity() {
      return this.owner;
   }

   public Component getLocalizedDeathMessage(LivingEntity livingEntity) {
      Component component = this.owner == null?this.entity.getDisplayName():this.owner.getDisplayName();
      ItemStack var3 = this.owner instanceof LivingEntity?((LivingEntity)this.owner).getMainHandItem():ItemStack.EMPTY;
      String var4 = "death.attack." + this.msgId;
      String var5 = var4 + ".item";
      return !var3.isEmpty() && var3.hasCustomHoverName()?new TranslatableComponent(var5, new Object[]{livingEntity.getDisplayName(), component, var3.getDisplayName()}):new TranslatableComponent(var4, new Object[]{livingEntity.getDisplayName(), component});
   }
}
