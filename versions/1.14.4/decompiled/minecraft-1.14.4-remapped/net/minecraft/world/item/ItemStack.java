package net.minecraft.world.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ItemStack EMPTY = new ItemStack((Item)null);
   public static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = getAttributeDecimalFormat();
   private int count;
   private int popTime;
   @Deprecated
   private final Item item;
   private CompoundTag tag;
   private boolean emptyCacheFlag;
   private ItemFrame frame;
   private BlockInWorld cachedBreakBlock;
   private boolean cachedBreakBlockResult;
   private BlockInWorld cachedPlaceBlock;
   private boolean cachedPlaceBlockResult;

   private static DecimalFormat getAttributeDecimalFormat() {
      DecimalFormat decimalFormat = new DecimalFormat("#.##");
      decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
      return decimalFormat;
   }

   public ItemStack(ItemLike itemLike) {
      this(itemLike, 1);
   }

   public ItemStack(ItemLike itemLike, int count) {
      this.item = itemLike == null?null:itemLike.asItem();
      this.count = count;
      this.updateEmptyCacheFlag();
   }

   private void updateEmptyCacheFlag() {
      this.emptyCacheFlag = false;
      this.emptyCacheFlag = this.isEmpty();
   }

   private ItemStack(CompoundTag compoundTag) {
      this.item = (Item)Registry.ITEM.get(new ResourceLocation(compoundTag.getString("id")));
      this.count = compoundTag.getByte("Count");
      if(compoundTag.contains("tag", 10)) {
         this.tag = compoundTag.getCompound("tag");
         this.getItem().verifyTagAfterLoad(compoundTag);
      }

      if(this.getItem().canBeDepleted()) {
         this.setDamageValue(this.getDamageValue());
      }

      this.updateEmptyCacheFlag();
   }

   public static ItemStack of(CompoundTag compoundTag) {
      try {
         return new ItemStack(compoundTag);
      } catch (RuntimeException var2) {
         LOGGER.debug("Tried to load invalid item: {}", compoundTag, var2);
         return EMPTY;
      }
   }

   public boolean isEmpty() {
      return this == EMPTY?true:(this.getItem() != null && this.getItem() != Items.AIR?this.count <= 0:true);
   }

   public ItemStack split(int i) {
      int var2 = Math.min(i, this.count);
      ItemStack var3 = this.copy();
      var3.setCount(var2);
      this.shrink(var2);
      return var3;
   }

   public Item getItem() {
      return this.emptyCacheFlag?Items.AIR:this.item;
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Player var2 = useOnContext.getPlayer();
      BlockPos var3 = useOnContext.getClickedPos();
      BlockInWorld var4 = new BlockInWorld(useOnContext.getLevel(), var3, false);
      if(var2 != null && !var2.abilities.mayBuild && !this.hasAdventureModePlaceTagForBlock(useOnContext.getLevel().getTagManager(), var4)) {
         return InteractionResult.PASS;
      } else {
         Item var5 = this.getItem();
         InteractionResult var6 = var5.useOn(useOnContext);
         if(var2 != null && var6 == InteractionResult.SUCCESS) {
            var2.awardStat(Stats.ITEM_USED.get(var5));
         }

         return var6;
      }
   }

   public float getDestroySpeed(BlockState blockState) {
      return this.getItem().getDestroySpeed(this, blockState);
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      return this.getItem().use(level, player, interactionHand);
   }

   public ItemStack finishUsingItem(Level level, LivingEntity livingEntity) {
      return this.getItem().finishUsingItem(this, level, livingEntity);
   }

   public CompoundTag save(CompoundTag compoundTag) {
      ResourceLocation var2 = Registry.ITEM.getKey(this.getItem());
      compoundTag.putString("id", var2 == null?"minecraft:air":var2.toString());
      compoundTag.putByte("Count", (byte)this.count);
      if(this.tag != null) {
         compoundTag.put("tag", this.tag);
      }

      return compoundTag;
   }

   public int getMaxStackSize() {
      return this.getItem().getMaxStackSize();
   }

   public boolean isStackable() {
      return this.getMaxStackSize() > 1 && (!this.isDamageableItem() || !this.isDamaged());
   }

   public boolean isDamageableItem() {
      if(!this.emptyCacheFlag && this.getItem().getMaxDamage() > 0) {
         CompoundTag var1 = this.getTag();
         return var1 == null || !var1.getBoolean("Unbreakable");
      } else {
         return false;
      }
   }

   public boolean isDamaged() {
      return this.isDamageableItem() && this.getDamageValue() > 0;
   }

   public int getDamageValue() {
      return this.tag == null?0:this.tag.getInt("Damage");
   }

   public void setDamageValue(int damageValue) {
      this.getOrCreateTag().putInt("Damage", Math.max(0, damageValue));
   }

   public int getMaxDamage() {
      return this.getItem().getMaxDamage();
   }

   public boolean hurt(int var1, Random random, @Nullable ServerPlayer serverPlayer) {
      if(!this.isDamageableItem()) {
         return false;
      } else {
         if(var1 > 0) {
            int var4 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, this);
            int var5 = 0;

            for(int var6 = 0; var4 > 0 && var6 < var1; ++var6) {
               if(DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(this, var4, random)) {
                  ++var5;
               }
            }

            var1 -= var5;
            if(var1 <= 0) {
               return false;
            }
         }

         if(serverPlayer != null && var1 != 0) {
            CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(serverPlayer, this, this.getDamageValue() + var1);
         }

         int var4 = this.getDamageValue() + var1;
         this.setDamageValue(var4);
         return var4 >= this.getMaxDamage();
      }
   }

   public void hurtAndBreak(int var1, LivingEntity livingEntity, Consumer consumer) {
      if(!livingEntity.level.isClientSide && (!(livingEntity instanceof Player) || !((Player)livingEntity).abilities.instabuild)) {
         if(this.isDamageableItem()) {
            if(this.hurt(var1, livingEntity.getRandom(), livingEntity instanceof ServerPlayer?(ServerPlayer)livingEntity:null)) {
               consumer.accept(livingEntity);
               Item var4 = this.getItem();
               this.shrink(1);
               if(livingEntity instanceof Player) {
                  ((Player)livingEntity).awardStat(Stats.ITEM_BROKEN.get(var4));
               }

               this.setDamageValue(0);
            }

         }
      }
   }

   public void hurtEnemy(LivingEntity livingEntity, Player player) {
      Item var3 = this.getItem();
      if(var3.hurtEnemy(this, livingEntity, player)) {
         player.awardStat(Stats.ITEM_USED.get(var3));
      }

   }

   public void mineBlock(Level level, BlockState blockState, BlockPos blockPos, Player player) {
      Item var5 = this.getItem();
      if(var5.mineBlock(this, level, blockState, blockPos, player)) {
         player.awardStat(Stats.ITEM_USED.get(var5));
      }

   }

   public boolean canDestroySpecial(BlockState blockState) {
      return this.getItem().canDestroySpecial(blockState);
   }

   public boolean interactEnemy(Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
      return this.getItem().interactEnemy(this, player, livingEntity, interactionHand);
   }

   public ItemStack copy() {
      ItemStack itemStack = new ItemStack(this.getItem(), this.count);
      itemStack.setPopTime(this.getPopTime());
      if(this.tag != null) {
         itemStack.tag = this.tag.copy();
      }

      return itemStack;
   }

   public static boolean tagMatches(ItemStack var0, ItemStack var1) {
      return var0.isEmpty() && var1.isEmpty()?true:(!var0.isEmpty() && !var1.isEmpty()?(var0.tag == null && var1.tag != null?false:var0.tag == null || var0.tag.equals(var1.tag)):false);
   }

   public static boolean matches(ItemStack var0, ItemStack var1) {
      return var0.isEmpty() && var1.isEmpty()?true:(!var0.isEmpty() && !var1.isEmpty()?var0.matches(var1):false);
   }

   private boolean matches(ItemStack itemStack) {
      return this.count != itemStack.count?false:(this.getItem() != itemStack.getItem()?false:(this.tag == null && itemStack.tag != null?false:this.tag == null || this.tag.equals(itemStack.tag)));
   }

   public static boolean isSame(ItemStack var0, ItemStack var1) {
      return var0 == var1?true:(!var0.isEmpty() && !var1.isEmpty()?var0.sameItem(var1):false);
   }

   public static boolean isSameIgnoreDurability(ItemStack var0, ItemStack var1) {
      return var0 == var1?true:(!var0.isEmpty() && !var1.isEmpty()?var0.sameItemStackIgnoreDurability(var1):false);
   }

   public boolean sameItem(ItemStack itemStack) {
      return !itemStack.isEmpty() && this.getItem() == itemStack.getItem();
   }

   public boolean sameItemStackIgnoreDurability(ItemStack itemStack) {
      return !this.isDamageableItem()?this.sameItem(itemStack):!itemStack.isEmpty() && this.getItem() == itemStack.getItem();
   }

   public String getDescriptionId() {
      return this.getItem().getDescriptionId(this);
   }

   public String toString() {
      return this.count + " " + this.getItem();
   }

   public void inventoryTick(Level level, Entity entity, int var3, boolean var4) {
      if(this.popTime > 0) {
         --this.popTime;
      }

      if(this.getItem() != null) {
         this.getItem().inventoryTick(this, level, entity, var3, var4);
      }

   }

   public void onCraftedBy(Level level, Player player, int var3) {
      player.awardStat(Stats.ITEM_CRAFTED.get(this.getItem()), var3);
      this.getItem().onCraftedBy(this, level, player);
   }

   public int getUseDuration() {
      return this.getItem().getUseDuration(this);
   }

   public UseAnim getUseAnimation() {
      return this.getItem().getUseAnimation(this);
   }

   public void releaseUsing(Level level, LivingEntity livingEntity, int var3) {
      this.getItem().releaseUsing(this, level, livingEntity, var3);
   }

   public boolean useOnRelease() {
      return this.getItem().useOnRelease(this);
   }

   public boolean hasTag() {
      return !this.emptyCacheFlag && this.tag != null && !this.tag.isEmpty();
   }

   @Nullable
   public CompoundTag getTag() {
      return this.tag;
   }

   public CompoundTag getOrCreateTag() {
      if(this.tag == null) {
         this.setTag(new CompoundTag());
      }

      return this.tag;
   }

   public CompoundTag getOrCreateTagElement(String string) {
      if(this.tag != null && this.tag.contains(string, 10)) {
         return this.tag.getCompound(string);
      } else {
         CompoundTag compoundTag = new CompoundTag();
         this.addTagElement(string, compoundTag);
         return compoundTag;
      }
   }

   @Nullable
   public CompoundTag getTagElement(String string) {
      return this.tag != null && this.tag.contains(string, 10)?this.tag.getCompound(string):null;
   }

   public void removeTagKey(String string) {
      if(this.tag != null && this.tag.contains(string)) {
         this.tag.remove(string);
         if(this.tag.isEmpty()) {
            this.tag = null;
         }
      }

   }

   public ListTag getEnchantmentTags() {
      return this.tag != null?this.tag.getList("Enchantments", 10):new ListTag();
   }

   public void setTag(@Nullable CompoundTag tag) {
      this.tag = tag;
   }

   public Component getHoverName() {
      CompoundTag var1 = this.getTagElement("display");
      if(var1 != null && var1.contains("Name", 8)) {
         try {
            Component var2 = Component.Serializer.fromJson(var1.getString("Name"));
            if(var2 != null) {
               return var2;
            }

            var1.remove("Name");
         } catch (JsonParseException var3) {
            var1.remove("Name");
         }
      }

      return this.getItem().getName(this);
   }

   public ItemStack setHoverName(@Nullable Component hoverName) {
      CompoundTag var2 = this.getOrCreateTagElement("display");
      if(hoverName != null) {
         var2.putString("Name", Component.Serializer.toJson(hoverName));
      } else {
         var2.remove("Name");
      }

      return this;
   }

   public void resetHoverName() {
      CompoundTag var1 = this.getTagElement("display");
      if(var1 != null) {
         var1.remove("Name");
         if(var1.isEmpty()) {
            this.removeTagKey("display");
         }
      }

      if(this.tag != null && this.tag.isEmpty()) {
         this.tag = null;
      }

   }

   public boolean hasCustomHoverName() {
      CompoundTag var1 = this.getTagElement("display");
      return var1 != null && var1.contains("Name", 8);
   }

   public List getTooltipLines(@Nullable Player player, TooltipFlag tooltipFlag) {
      List<Component> list = Lists.newArrayList();
      Component var4 = (new TextComponent("")).append(this.getHoverName()).withStyle(this.getRarity().color);
      if(this.hasCustomHoverName()) {
         var4.withStyle(ChatFormatting.ITALIC);
      }

      list.add(var4);
      if(!tooltipFlag.isAdvanced() && !this.hasCustomHoverName() && this.getItem() == Items.FILLED_MAP) {
         list.add((new TextComponent("#" + MapItem.getMapId(this))).withStyle(ChatFormatting.GRAY));
      }

      int var5 = 0;
      if(this.hasTag() && this.tag.contains("HideFlags", 99)) {
         var5 = this.tag.getInt("HideFlags");
      }

      if((var5 & 32) == 0) {
         this.getItem().appendHoverText(this, player == null?null:player.level, list, tooltipFlag);
      }

      if(this.hasTag()) {
         if((var5 & 1) == 0) {
            appendEnchantmentNames(list, this.getEnchantmentTags());
         }

         if(this.tag.contains("display", 10)) {
            CompoundTag var6 = this.tag.getCompound("display");
            if(var6.contains("color", 3)) {
               if(tooltipFlag.isAdvanced()) {
                  list.add((new TranslatableComponent("item.color", new Object[]{String.format("#%06X", new Object[]{Integer.valueOf(var6.getInt("color"))})})).withStyle(ChatFormatting.GRAY));
               } else {
                  list.add((new TranslatableComponent("item.dyed", new Object[0])).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}));
               }
            }

            if(var6.getTagType("Lore") == 9) {
               ListTag var7 = var6.getList("Lore", 8);

               for(int var8 = 0; var8 < var7.size(); ++var8) {
                  String var9 = var7.getString(var8);

                  try {
                     Component var10 = Component.Serializer.fromJson(var9);
                     if(var10 != null) {
                        list.add(ComponentUtils.mergeStyles(var10, (new Style()).setColor(ChatFormatting.DARK_PURPLE).setItalic(Boolean.valueOf(true))));
                     }
                  } catch (JsonParseException var19) {
                     var6.remove("Lore");
                  }
               }
            }
         }
      }

      for(EquipmentSlot var9 : EquipmentSlot.values()) {
         Multimap<String, AttributeModifier> var10 = this.getAttributeModifiers(var9);
         if(!var10.isEmpty() && (var5 & 2) == 0) {
            list.add(new TextComponent(""));
            list.add((new TranslatableComponent("item.modifiers." + var9.getName(), new Object[0])).withStyle(ChatFormatting.GRAY));

            for(Entry<String, AttributeModifier> var12 : var10.entries()) {
               AttributeModifier var13 = (AttributeModifier)var12.getValue();
               double var14 = var13.getAmount();
               boolean var18 = false;
               if(player != null) {
                  if(var13.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
                     var14 = var14 + player.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
                     var14 = var14 + (double)EnchantmentHelper.getDamageBonus(this, MobType.UNDEFINED);
                     var18 = true;
                  } else if(var13.getId() == Item.BASE_ATTACK_SPEED_UUID) {
                     var14 += player.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
                     var18 = true;
                  }
               }

               double var16;
               if(var13.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && var13.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                  var16 = var14;
               } else {
                  var16 = var14 * 100.0D;
               }

               if(var18) {
                  list.add((new TextComponent(" ")).append(new TranslatableComponent("attribute.modifier.equals." + var13.getOperation().toValue(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format(var16), new TranslatableComponent("attribute.name." + (String)var12.getKey(), new Object[0])})).withStyle(ChatFormatting.DARK_GREEN));
               } else if(var14 > 0.0D) {
                  list.add((new TranslatableComponent("attribute.modifier.plus." + var13.getOperation().toValue(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format(var16), new TranslatableComponent("attribute.name." + (String)var12.getKey(), new Object[0])})).withStyle(ChatFormatting.BLUE));
               } else if(var14 < 0.0D) {
                  var16 = var16 * -1.0D;
                  list.add((new TranslatableComponent("attribute.modifier.take." + var13.getOperation().toValue(), new Object[]{ATTRIBUTE_MODIFIER_FORMAT.format(var16), new TranslatableComponent("attribute.name." + (String)var12.getKey(), new Object[0])})).withStyle(ChatFormatting.RED));
               }
            }
         }
      }

      if(this.hasTag() && this.getTag().getBoolean("Unbreakable") && (var5 & 4) == 0) {
         list.add((new TranslatableComponent("item.unbreakable", new Object[0])).withStyle(ChatFormatting.BLUE));
      }

      if(this.hasTag() && this.tag.contains("CanDestroy", 9) && (var5 & 8) == 0) {
         ListTag var6 = this.tag.getList("CanDestroy", 8);
         if(!var6.isEmpty()) {
            list.add(new TextComponent(""));
            list.add((new TranslatableComponent("item.canBreak", new Object[0])).withStyle(ChatFormatting.GRAY));

            for(int var7 = 0; var7 < var6.size(); ++var7) {
               list.addAll(expandBlockState(var6.getString(var7)));
            }
         }
      }

      if(this.hasTag() && this.tag.contains("CanPlaceOn", 9) && (var5 & 16) == 0) {
         ListTag var6 = this.tag.getList("CanPlaceOn", 8);
         if(!var6.isEmpty()) {
            list.add(new TextComponent(""));
            list.add((new TranslatableComponent("item.canPlace", new Object[0])).withStyle(ChatFormatting.GRAY));

            for(int var7 = 0; var7 < var6.size(); ++var7) {
               list.addAll(expandBlockState(var6.getString(var7)));
            }
         }
      }

      if(tooltipFlag.isAdvanced()) {
         if(this.isDamaged()) {
            list.add(new TranslatableComponent("item.durability", new Object[]{Integer.valueOf(this.getMaxDamage() - this.getDamageValue()), Integer.valueOf(this.getMaxDamage())}));
         }

         list.add((new TextComponent(Registry.ITEM.getKey(this.getItem()).toString())).withStyle(ChatFormatting.DARK_GRAY));
         if(this.hasTag()) {
            list.add((new TranslatableComponent("item.nbt_tags", new Object[]{Integer.valueOf(this.getTag().getAllKeys().size())})).withStyle(ChatFormatting.DARK_GRAY));
         }
      }

      return list;
   }

   public static void appendEnchantmentNames(List list, ListTag listTag) {
      for(int var2 = 0; var2 < listTag.size(); ++var2) {
         CompoundTag var3 = listTag.getCompound(var2);
         Registry.ENCHANTMENT.getOptional(ResourceLocation.tryParse(var3.getString("id"))).ifPresent((enchantment) -> {
            list.add(enchantment.getFullname(var3.getInt("lvl")));
         });
      }

   }

   private static Collection expandBlockState(String string) {
      try {
         BlockStateParser var1 = (new BlockStateParser(new StringReader(string), true)).parse(true);
         BlockState var2 = var1.getState();
         ResourceLocation var3 = var1.getTag();
         boolean var4 = var2 != null;
         boolean var5 = var3 != null;
         if(var4 || var5) {
            if(var4) {
               return Lists.newArrayList(var2.getBlock().getName().withStyle(ChatFormatting.DARK_GRAY));
            }

            Tag<Block> var6 = BlockTags.getAllTags().getTag(var3);
            if(var6 != null) {
               Collection<Block> var7 = var6.getValues();
               if(!var7.isEmpty()) {
                  return (Collection)var7.stream().map(Block::getName).map((component) -> {
                     return component.withStyle(ChatFormatting.DARK_GRAY);
                  }).collect(Collectors.toList());
               }
            }
         }
      } catch (CommandSyntaxException var8) {
         ;
      }

      return Lists.newArrayList((new TextComponent("missingno")).withStyle(ChatFormatting.DARK_GRAY));
   }

   public boolean hasFoil() {
      return this.getItem().isFoil(this);
   }

   public Rarity getRarity() {
      return this.getItem().getRarity(this);
   }

   public boolean isEnchantable() {
      return !this.getItem().isEnchantable(this)?false:!this.isEnchanted();
   }

   public void enchant(Enchantment enchantment, int var2) {
      this.getOrCreateTag();
      if(!this.tag.contains("Enchantments", 9)) {
         this.tag.put("Enchantments", new ListTag());
      }

      ListTag var3 = this.tag.getList("Enchantments", 10);
      CompoundTag var4 = new CompoundTag();
      var4.putString("id", String.valueOf(Registry.ENCHANTMENT.getKey(enchantment)));
      var4.putShort("lvl", (short)((byte)var2));
      var3.add(var4);
   }

   public boolean isEnchanted() {
      return this.tag != null && this.tag.contains("Enchantments", 9)?!this.tag.getList("Enchantments", 10).isEmpty():false;
   }

   public void addTagElement(String string, net.minecraft.nbt.Tag tag) {
      this.getOrCreateTag().put(string, tag);
   }

   public boolean isFramed() {
      return this.frame != null;
   }

   public void setFramed(@Nullable ItemFrame framed) {
      this.frame = framed;
   }

   @Nullable
   public ItemFrame getFrame() {
      return this.emptyCacheFlag?null:this.frame;
   }

   public int getBaseRepairCost() {
      return this.hasTag() && this.tag.contains("RepairCost", 3)?this.tag.getInt("RepairCost"):0;
   }

   public void setRepairCost(int repairCost) {
      this.getOrCreateTag().putInt("RepairCost", repairCost);
   }

   public Multimap getAttributeModifiers(EquipmentSlot equipmentSlot) {
      Multimap<String, AttributeModifier> multimap;
      if(this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
         multimap = HashMultimap.create();
         ListTag var3 = this.tag.getList("AttributeModifiers", 10);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            CompoundTag var5 = var3.getCompound(var4);
            AttributeModifier var6 = SharedMonsterAttributes.loadAttributeModifier(var5);
            if(var6 != null && (!var5.contains("Slot", 8) || var5.getString("Slot").equals(equipmentSlot.getName())) && var6.getId().getLeastSignificantBits() != 0L && var6.getId().getMostSignificantBits() != 0L) {
               multimap.put(var5.getString("AttributeName"), var6);
            }
         }
      } else {
         multimap = this.getItem().getDefaultAttributeModifiers(equipmentSlot);
      }

      return multimap;
   }

   public void addAttributeModifier(String string, AttributeModifier attributeModifier, @Nullable EquipmentSlot equipmentSlot) {
      this.getOrCreateTag();
      if(!this.tag.contains("AttributeModifiers", 9)) {
         this.tag.put("AttributeModifiers", new ListTag());
      }

      ListTag var4 = this.tag.getList("AttributeModifiers", 10);
      CompoundTag var5 = SharedMonsterAttributes.saveAttributeModifier(attributeModifier);
      var5.putString("AttributeName", string);
      if(equipmentSlot != null) {
         var5.putString("Slot", equipmentSlot.getName());
      }

      var4.add(var5);
   }

   public Component getDisplayName() {
      Component component = (new TextComponent("")).append(this.getHoverName());
      if(this.hasCustomHoverName()) {
         component.withStyle(ChatFormatting.ITALIC);
      }

      Component var2 = ComponentUtils.wrapInSquareBrackets(component);
      if(!this.emptyCacheFlag) {
         CompoundTag var3 = this.save(new CompoundTag());
         var2.withStyle(this.getRarity().color).withStyle((style) -> {
            style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponent(var3.toString())));
         });
      }

      return var2;
   }

   private static boolean areSameBlocks(BlockInWorld var0, @Nullable BlockInWorld var1) {
      return var1 != null && var0.getState() == var1.getState()?(var0.getEntity() == null && var1.getEntity() == null?true:(var0.getEntity() != null && var1.getEntity() != null?Objects.equals(var0.getEntity().save(new CompoundTag()), var1.getEntity().save(new CompoundTag())):false)):false;
   }

   public boolean hasAdventureModeBreakTagForBlock(TagManager tagManager, BlockInWorld cachedBreakBlock) {
      if(areSameBlocks(cachedBreakBlock, this.cachedBreakBlock)) {
         return this.cachedBreakBlockResult;
      } else {
         this.cachedBreakBlock = cachedBreakBlock;
         if(this.hasTag() && this.tag.contains("CanDestroy", 9)) {
            ListTag var3 = this.tag.getList("CanDestroy", 8);

            for(int var4 = 0; var4 < var3.size(); ++var4) {
               String var5 = var3.getString(var4);

               try {
                  Predicate<BlockInWorld> var6 = BlockPredicateArgument.blockPredicate().parse(new StringReader(var5)).create(tagManager);
                  if(var6.test(cachedBreakBlock)) {
                     this.cachedBreakBlockResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
                  ;
               }
            }
         }

         this.cachedBreakBlockResult = false;
         return false;
      }
   }

   public boolean hasAdventureModePlaceTagForBlock(TagManager tagManager, BlockInWorld cachedPlaceBlock) {
      if(areSameBlocks(cachedPlaceBlock, this.cachedPlaceBlock)) {
         return this.cachedPlaceBlockResult;
      } else {
         this.cachedPlaceBlock = cachedPlaceBlock;
         if(this.hasTag() && this.tag.contains("CanPlaceOn", 9)) {
            ListTag var3 = this.tag.getList("CanPlaceOn", 8);

            for(int var4 = 0; var4 < var3.size(); ++var4) {
               String var5 = var3.getString(var4);

               try {
                  Predicate<BlockInWorld> var6 = BlockPredicateArgument.blockPredicate().parse(new StringReader(var5)).create(tagManager);
                  if(var6.test(cachedPlaceBlock)) {
                     this.cachedPlaceBlockResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException var7) {
                  ;
               }
            }
         }

         this.cachedPlaceBlockResult = false;
         return false;
      }
   }

   public int getPopTime() {
      return this.popTime;
   }

   public void setPopTime(int popTime) {
      this.popTime = popTime;
   }

   public int getCount() {
      return this.emptyCacheFlag?0:this.count;
   }

   public void setCount(int count) {
      this.count = count;
      this.updateEmptyCacheFlag();
   }

   public void grow(int i) {
      this.setCount(this.count + i);
   }

   public void shrink(int i) {
      this.grow(-i);
   }

   public void onUseTick(Level level, LivingEntity livingEntity, int var3) {
      this.getItem().onUseTick(level, livingEntity, this, var3);
   }

   public boolean isEdible() {
      return this.getItem().isEdible();
   }
}
