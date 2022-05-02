package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class HoeItem extends TieredItem {
   private final float attackSpeed;
   protected static final Map TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.defaultBlockState(), Blocks.GRASS_PATH, Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT, Blocks.FARMLAND.defaultBlockState(), Blocks.COARSE_DIRT, Blocks.DIRT.defaultBlockState()));

   public HoeItem(Tier tier, float attackSpeed, Item.Properties item$Properties) {
      super(tier, item$Properties);
      this.attackSpeed = attackSpeed;
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level var2 = useOnContext.getLevel();
      BlockPos var3 = useOnContext.getClickedPos();
      if(useOnContext.getClickedFace() != Direction.DOWN && var2.getBlockState(var3.above()).isAir()) {
         BlockState var4 = (BlockState)TILLABLES.get(var2.getBlockState(var3).getBlock());
         if(var4 != null) {
            Player var5 = useOnContext.getPlayer();
            var2.playSound(var5, var3, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if(!var2.isClientSide) {
               var2.setBlock(var3, var4, 11);
               if(var5 != null) {
                  useOnContext.getItemInHand().hurtAndBreak(1, var5, (player) -> {
                     player.broadcastBreakEvent(useOnContext.getHand());
                  });
               }
            }

            return InteractionResult.SUCCESS;
         }
      }

      return InteractionResult.PASS;
   }

   public boolean hurtEnemy(ItemStack itemStack, LivingEntity var2, LivingEntity var3) {
      itemStack.hurtAndBreak(1, var3, (livingEntity) -> {
         livingEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
      });
      return true;
   }

   public Multimap getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getDefaultAttributeModifiers(equipmentSlot);
      if(equipmentSlot == EquipmentSlot.MAINHAND) {
         multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 0.0D, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.attackSpeed, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }
}
