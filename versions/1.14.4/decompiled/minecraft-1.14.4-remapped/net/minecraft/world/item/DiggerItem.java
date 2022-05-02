package net.minecraft.world.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DiggerItem extends TieredItem {
   private final Set blocks;
   protected final float speed;
   protected final float attackDamage;
   protected final float attackSpeed;

   protected DiggerItem(float var1, float attackSpeed, Tier tier, Set blocks, Item.Properties item$Properties) {
      super(tier, item$Properties);
      this.blocks = blocks;
      this.speed = tier.getSpeed();
      this.attackDamage = var1 + tier.getAttackDamageBonus();
      this.attackSpeed = attackSpeed;
   }

   public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
      return this.blocks.contains(blockState.getBlock())?this.speed:1.0F;
   }

   public boolean hurtEnemy(ItemStack itemStack, LivingEntity var2, LivingEntity var3) {
      itemStack.hurtAndBreak(2, var3, (livingEntity) -> {
         livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
      });
      return true;
   }

   public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
      if(!level.isClientSide && blockState.getDestroySpeed(level, blockPos) != 0.0F) {
         itemStack.hurtAndBreak(1, livingEntity, (livingEntity) -> {
            livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
         });
      }

      return true;
   }

   public Multimap getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getDefaultAttributeModifiers(equipmentSlot);
      if(equipmentSlot == EquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }
}
