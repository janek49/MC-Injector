package net.minecraft.world.item;

import com.google.common.collect.Multimap;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class SwordItem extends TieredItem {
   private final float attackDamage;
   private final float attackSpeed;

   public SwordItem(Tier tier, int var2, float attackSpeed, Item.Properties item$Properties) {
      super(tier, item$Properties);
      this.attackSpeed = attackSpeed;
      this.attackDamage = (float)var2 + tier.getAttackDamageBonus();
   }

   public float getDamage() {
      return this.attackDamage;
   }

   public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
      return !player.isCreative();
   }

   public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
      Block var3 = blockState.getBlock();
      if(var3 == Blocks.COBWEB) {
         return 15.0F;
      } else {
         Material var4 = blockState.getMaterial();
         return var4 != Material.PLANT && var4 != Material.REPLACEABLE_PLANT && var4 != Material.CORAL && !blockState.is(BlockTags.LEAVES) && var4 != Material.VEGETABLE?1.0F:1.5F;
      }
   }

   public boolean hurtEnemy(ItemStack itemStack, LivingEntity var2, LivingEntity var3) {
      itemStack.hurtAndBreak(1, var3, (livingEntity) -> {
         livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
      });
      return true;
   }

   public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
      if(blockState.getDestroySpeed(level, blockPos) != 0.0F) {
         itemStack.hurtAndBreak(2, livingEntity, (livingEntity) -> {
            livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
         });
      }

      return true;
   }

   public boolean canDestroySpecial(BlockState blockState) {
      return blockState.getBlock() == Blocks.COBWEB;
   }

   public Multimap getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getDefaultAttributeModifiers(equipmentSlot);
      if(equipmentSlot == EquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }
}
