package net.minecraft.world.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Item implements ItemLike {
   public static final Map BY_BLOCK = Maps.newHashMap();
   private static final ItemPropertyFunction PROPERTY_DAMAGED = (itemStack, level, livingEntity) -> {
      return itemStack.isDamaged()?1.0F:0.0F;
   };
   private static final ItemPropertyFunction PROPERTY_DAMAGE = (itemStack, level, livingEntity) -> {
      return Mth.clamp((float)itemStack.getDamageValue() / (float)itemStack.getMaxDamage(), 0.0F, 1.0F);
   };
   private static final ItemPropertyFunction PROPERTY_LEFTHANDED = (itemStack, level, livingEntity) -> {
      return livingEntity != null && livingEntity.getMainArm() != HumanoidArm.RIGHT?1.0F:0.0F;
   };
   private static final ItemPropertyFunction PROPERTY_COOLDOWN = (itemStack, level, livingEntity) -> {
      return livingEntity instanceof Player?((Player)livingEntity).getCooldowns().getCooldownPercent(itemStack.getItem(), 0.0F):0.0F;
   };
   private static final ItemPropertyFunction PROPERTY_CUSTOM_MODEL_DATA = (itemStack, level, livingEntity) -> {
      return itemStack.hasTag()?(float)itemStack.getTag().getInt("CustomModelData"):0.0F;
   };
   protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
   protected static final Random random = new Random();
   private final Map properties = Maps.newHashMap();
   protected final CreativeModeTab category;
   private final Rarity rarity;
   private final int maxStackSize;
   private final int maxDamage;
   private final Item craftingRemainingItem;
   @Nullable
   private String descriptionId;
   @Nullable
   private final FoodProperties foodProperties;

   public static int getId(Item item) {
      return item == null?0:Registry.ITEM.getId(item);
   }

   public static Item byId(int id) {
      return (Item)Registry.ITEM.byId(id);
   }

   @Deprecated
   public static Item byBlock(Block block) {
      return (Item)BY_BLOCK.getOrDefault(block, Items.AIR);
   }

   public Item(Item.Properties item$Properties) {
      this.addProperty(new ResourceLocation("lefthanded"), PROPERTY_LEFTHANDED);
      this.addProperty(new ResourceLocation("cooldown"), PROPERTY_COOLDOWN);
      this.addProperty(new ResourceLocation("custom_model_data"), PROPERTY_CUSTOM_MODEL_DATA);
      this.category = item$Properties.category;
      this.rarity = item$Properties.rarity;
      this.craftingRemainingItem = item$Properties.craftingRemainingItem;
      this.maxDamage = item$Properties.maxDamage;
      this.maxStackSize = item$Properties.maxStackSize;
      this.foodProperties = item$Properties.foodProperties;
      if(this.maxDamage > 0) {
         this.addProperty(new ResourceLocation("damaged"), PROPERTY_DAMAGED);
         this.addProperty(new ResourceLocation("damage"), PROPERTY_DAMAGE);
      }

   }

   public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int var4) {
   }

   @Nullable
   public ItemPropertyFunction getProperty(ResourceLocation resourceLocation) {
      return (ItemPropertyFunction)this.properties.get(resourceLocation);
   }

   public boolean hasProperties() {
      return !this.properties.isEmpty();
   }

   public boolean verifyTagAfterLoad(CompoundTag compoundTag) {
      return false;
   }

   public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
      return true;
   }

   public Item asItem() {
      return this;
   }

   public final void addProperty(ResourceLocation resourceLocation, ItemPropertyFunction itemPropertyFunction) {
      this.properties.put(resourceLocation, itemPropertyFunction);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      return InteractionResult.PASS;
   }

   public float getDestroySpeed(ItemStack itemStack, BlockState blockState) {
      return 1.0F;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      if(this.isEdible()) {
         ItemStack var4 = player.getItemInHand(interactionHand);
         if(player.canEat(this.getFoodProperties().canAlwaysEat())) {
            player.startUsingItem(interactionHand);
            return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
         } else {
            return new InteractionResultHolder(InteractionResult.FAIL, var4);
         }
      } else {
         return new InteractionResultHolder(InteractionResult.PASS, player.getItemInHand(interactionHand));
      }
   }

   public ItemStack finishUsingItem(ItemStack var1, Level level, LivingEntity livingEntity) {
      return this.isEdible()?livingEntity.eat(level, var1):var1;
   }

   public final int getMaxStackSize() {
      return this.maxStackSize;
   }

   public final int getMaxDamage() {
      return this.maxDamage;
   }

   public boolean canBeDepleted() {
      return this.maxDamage > 0;
   }

   public boolean hurtEnemy(ItemStack itemStack, LivingEntity var2, LivingEntity var3) {
      return false;
   }

   public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity) {
      return false;
   }

   public boolean canDestroySpecial(BlockState blockState) {
      return false;
   }

   public boolean interactEnemy(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
      return false;
   }

   public Component getDescription() {
      return new TranslatableComponent(this.getDescriptionId(), new Object[0]);
   }

   public String toString() {
      return Registry.ITEM.getKey(this).getPath();
   }

   protected String getOrCreateDescriptionId() {
      if(this.descriptionId == null) {
         this.descriptionId = Util.makeDescriptionId("item", Registry.ITEM.getKey(this));
      }

      return this.descriptionId;
   }

   public String getDescriptionId() {
      return this.getOrCreateDescriptionId();
   }

   public String getDescriptionId(ItemStack itemStack) {
      return this.getDescriptionId();
   }

   public boolean shouldOverrideMultiplayerNbt() {
      return true;
   }

   @Nullable
   public final Item getCraftingRemainingItem() {
      return this.craftingRemainingItem;
   }

   public boolean hasCraftingRemainingItem() {
      return this.craftingRemainingItem != null;
   }

   public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int var4, boolean var5) {
   }

   public void onCraftedBy(ItemStack itemStack, Level level, Player player) {
   }

   public boolean isComplex() {
      return false;
   }

   public UseAnim getUseAnimation(ItemStack itemStack) {
      return itemStack.getItem().isEdible()?UseAnim.EAT:UseAnim.NONE;
   }

   public int getUseDuration(ItemStack itemStack) {
      return itemStack.getItem().isEdible()?(this.getFoodProperties().isFastFood()?16:32):0;
   }

   public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int var4) {
   }

   public void appendHoverText(ItemStack itemStack, @Nullable Level level, List list, TooltipFlag tooltipFlag) {
   }

   public Component getName(ItemStack itemStack) {
      return new TranslatableComponent(this.getDescriptionId(itemStack), new Object[0]);
   }

   public boolean isFoil(ItemStack itemStack) {
      return itemStack.isEnchanted();
   }

   public Rarity getRarity(ItemStack itemStack) {
      if(!itemStack.isEnchanted()) {
         return this.rarity;
      } else {
         switch(this.rarity) {
         case COMMON:
         case UNCOMMON:
            return Rarity.RARE;
         case RARE:
            return Rarity.EPIC;
         case EPIC:
         default:
            return this.rarity;
         }
      }
   }

   public boolean isEnchantable(ItemStack itemStack) {
      return this.getMaxStackSize() == 1 && this.canBeDepleted();
   }

   protected static HitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid clipContext$Fluid) {
      float var3 = player.xRot;
      float var4 = player.yRot;
      Vec3 var5 = player.getEyePosition(1.0F);
      float var6 = Mth.cos(-var4 * 0.017453292F - 3.1415927F);
      float var7 = Mth.sin(-var4 * 0.017453292F - 3.1415927F);
      float var8 = -Mth.cos(-var3 * 0.017453292F);
      float var9 = Mth.sin(-var3 * 0.017453292F);
      float var10 = var7 * var8;
      float var12 = var6 * var8;
      double var13 = 5.0D;
      Vec3 var15 = var5.add((double)var10 * 5.0D, (double)var9 * 5.0D, (double)var12 * 5.0D);
      return level.clip(new ClipContext(var5, var15, ClipContext.Block.OUTLINE, clipContext$Fluid, player));
   }

   public int getEnchantmentValue() {
      return 0;
   }

   public void fillItemCategory(CreativeModeTab creativeModeTab, NonNullList nonNullList) {
      if(this.allowdedIn(creativeModeTab)) {
         nonNullList.add(new ItemStack(this));
      }

   }

   protected boolean allowdedIn(CreativeModeTab creativeModeTab) {
      CreativeModeTab creativeModeTab = this.getItemCategory();
      return creativeModeTab != null && (creativeModeTab == CreativeModeTab.TAB_SEARCH || creativeModeTab == creativeModeTab);
   }

   @Nullable
   public final CreativeModeTab getItemCategory() {
      return this.category;
   }

   public boolean isValidRepairItem(ItemStack var1, ItemStack var2) {
      return false;
   }

   public Multimap getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
      return HashMultimap.create();
   }

   public boolean useOnRelease(ItemStack itemStack) {
      return itemStack.getItem() == Items.CROSSBOW;
   }

   public ItemStack getDefaultInstance() {
      return new ItemStack(this);
   }

   public boolean is(Tag tag) {
      return tag.contains(this);
   }

   public boolean isEdible() {
      return this.foodProperties != null;
   }

   @Nullable
   public FoodProperties getFoodProperties() {
      return this.foodProperties;
   }

   public static class Properties {
      private int maxStackSize = 64;
      private int maxDamage;
      private Item craftingRemainingItem;
      private CreativeModeTab category;
      private Rarity rarity = Rarity.COMMON;
      private FoodProperties foodProperties;

      public Item.Properties food(FoodProperties foodProperties) {
         this.foodProperties = foodProperties;
         return this;
      }

      public Item.Properties stacksTo(int maxStackSize) {
         if(this.maxDamage > 0) {
            throw new RuntimeException("Unable to have damage AND stack.");
         } else {
            this.maxStackSize = maxStackSize;
            return this;
         }
      }

      public Item.Properties defaultDurability(int i) {
         return this.maxDamage == 0?this.durability(i):this;
      }

      public Item.Properties durability(int maxDamage) {
         this.maxDamage = maxDamage;
         this.maxStackSize = 1;
         return this;
      }

      public Item.Properties craftRemainder(Item craftingRemainingItem) {
         this.craftingRemainingItem = craftingRemainingItem;
         return this;
      }

      public Item.Properties tab(CreativeModeTab category) {
         this.category = category;
         return this;
      }

      public Item.Properties rarity(Rarity rarity) {
         this.rarity = rarity;
         return this;
      }
   }
}
