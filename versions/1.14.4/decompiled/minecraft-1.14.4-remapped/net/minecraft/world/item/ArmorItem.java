package net.minecraft.world.item;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class ArmorItem extends Item {
   private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
   public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
      protected ItemStack execute(BlockSource blockSource, ItemStack var2) {
         ItemStack var3 = ArmorItem.dispenseArmor(blockSource, var2);
         return var3.isEmpty()?super.execute(blockSource, var2):var3;
      }
   };
   protected final EquipmentSlot slot;
   protected final int defense;
   protected final float toughness;
   protected final ArmorMaterial material;

   public static ItemStack dispenseArmor(BlockSource blockSource, ItemStack var1) {
      BlockPos var2 = blockSource.getPos().relative((Direction)blockSource.getBlockState().getValue(DispenserBlock.FACING));
      List<LivingEntity> var3 = blockSource.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(var2), EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmourEntitySelector(var1)));
      if(var3.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         LivingEntity var4 = (LivingEntity)var3.get(0);
         EquipmentSlot var5 = Mob.getEquipmentSlotForItem(var1);
         ItemStack var6 = var1.split(1);
         var4.setItemSlot(var5, var6);
         if(var4 instanceof Mob) {
            ((Mob)var4).setDropChance(var5, 2.0F);
            ((Mob)var4).setPersistenceRequired();
         }

         return var1;
      }
   }

   public ArmorItem(ArmorMaterial material, EquipmentSlot slot, Item.Properties item$Properties) {
      super(item$Properties.defaultDurability(material.getDurabilityForSlot(slot)));
      this.material = material;
      this.slot = slot;
      this.defense = material.getDefenseForSlot(slot);
      this.toughness = material.getToughness();
      DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
   }

   public EquipmentSlot getSlot() {
      return this.slot;
   }

   public int getEnchantmentValue() {
      return this.material.getEnchantmentValue();
   }

   public ArmorMaterial getMaterial() {
      return this.material;
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return this.material.getRepairIngredient().test(var2) || super.isValidRepairItem(var1, var2);
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      EquipmentSlot var5 = Mob.getEquipmentSlotForItem(var4);
      ItemStack var6 = player.getItemBySlot(var5);
      if(var6.isEmpty()) {
         player.setItemSlot(var5, var4.copy());
         var4.setCount(0);
         return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
      } else {
         return new InteractionResultHolder(InteractionResult.FAIL, var4);
      }
   }

   public Multimap getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap = super.getDefaultAttributeModifiers(equipmentSlot);
      if(equipmentSlot == this.slot) {
         multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[equipmentSlot.getIndex()], "Armor modifier", (double)this.defense, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIER_UUID_PER_SLOT[equipmentSlot.getIndex()], "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }

   public int getDefense() {
      return this.defense;
   }
}
