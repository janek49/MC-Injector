package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractFurnaceBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer, RecipeHolder, StackedContentsCompatible, TickableBlockEntity {
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
   private static final int[] SLOTS_FOR_SIDES = new int[]{1};
   protected NonNullList items = NonNullList.withSize(3, ItemStack.EMPTY);
   private int litTime;
   private int litDuration;
   private int cookingProgress;
   private int cookingTotalTime;
   protected final ContainerData dataAccess = new ContainerData() {
      public int get(int i) {
         switch(i) {
         case 0:
            return AbstractFurnaceBlockEntity.this.litTime;
         case 1:
            return AbstractFurnaceBlockEntity.this.litDuration;
         case 2:
            return AbstractFurnaceBlockEntity.this.cookingProgress;
         case 3:
            return AbstractFurnaceBlockEntity.this.cookingTotalTime;
         default:
            return 0;
         }
      }

      public void set(int var1, int var2) {
         switch(var1) {
         case 0:
            AbstractFurnaceBlockEntity.this.litTime = var2;
            break;
         case 1:
            AbstractFurnaceBlockEntity.this.litDuration = var2;
            break;
         case 2:
            AbstractFurnaceBlockEntity.this.cookingProgress = var2;
            break;
         case 3:
            AbstractFurnaceBlockEntity.this.cookingTotalTime = var2;
         }

      }

      public int getCount() {
         return 4;
      }
   };
   private final Map recipesUsed = Maps.newHashMap();
   protected final RecipeType recipeType;

   protected AbstractFurnaceBlockEntity(BlockEntityType blockEntityType, RecipeType recipeType) {
      super(blockEntityType);
      this.recipeType = recipeType;
   }

   public static Map getFuel() {
      Map<Item, Integer> map = Maps.newLinkedHashMap();
      add(map, (ItemLike)Items.LAVA_BUCKET, 20000);
      add(map, (ItemLike)Blocks.COAL_BLOCK, 16000);
      add(map, (ItemLike)Items.BLAZE_ROD, 2400);
      add(map, (ItemLike)Items.COAL, 1600);
      add(map, (ItemLike)Items.CHARCOAL, 1600);
      add(map, (Tag)ItemTags.LOGS, 300);
      add(map, (Tag)ItemTags.PLANKS, 300);
      add(map, (Tag)ItemTags.WOODEN_STAIRS, 300);
      add(map, (Tag)ItemTags.WOODEN_SLABS, 150);
      add(map, (Tag)ItemTags.WOODEN_TRAPDOORS, 300);
      add(map, (Tag)ItemTags.WOODEN_PRESSURE_PLATES, 300);
      add(map, (ItemLike)Blocks.OAK_FENCE, 300);
      add(map, (ItemLike)Blocks.BIRCH_FENCE, 300);
      add(map, (ItemLike)Blocks.SPRUCE_FENCE, 300);
      add(map, (ItemLike)Blocks.JUNGLE_FENCE, 300);
      add(map, (ItemLike)Blocks.DARK_OAK_FENCE, 300);
      add(map, (ItemLike)Blocks.ACACIA_FENCE, 300);
      add(map, (ItemLike)Blocks.OAK_FENCE_GATE, 300);
      add(map, (ItemLike)Blocks.BIRCH_FENCE_GATE, 300);
      add(map, (ItemLike)Blocks.SPRUCE_FENCE_GATE, 300);
      add(map, (ItemLike)Blocks.JUNGLE_FENCE_GATE, 300);
      add(map, (ItemLike)Blocks.DARK_OAK_FENCE_GATE, 300);
      add(map, (ItemLike)Blocks.ACACIA_FENCE_GATE, 300);
      add(map, (ItemLike)Blocks.NOTE_BLOCK, 300);
      add(map, (ItemLike)Blocks.BOOKSHELF, 300);
      add(map, (ItemLike)Blocks.LECTERN, 300);
      add(map, (ItemLike)Blocks.JUKEBOX, 300);
      add(map, (ItemLike)Blocks.CHEST, 300);
      add(map, (ItemLike)Blocks.TRAPPED_CHEST, 300);
      add(map, (ItemLike)Blocks.CRAFTING_TABLE, 300);
      add(map, (ItemLike)Blocks.DAYLIGHT_DETECTOR, 300);
      add(map, (Tag)ItemTags.BANNERS, 300);
      add(map, (ItemLike)Items.BOW, 300);
      add(map, (ItemLike)Items.FISHING_ROD, 300);
      add(map, (ItemLike)Blocks.LADDER, 300);
      add(map, (Tag)ItemTags.SIGNS, 200);
      add(map, (ItemLike)Items.WOODEN_SHOVEL, 200);
      add(map, (ItemLike)Items.WOODEN_SWORD, 200);
      add(map, (ItemLike)Items.WOODEN_HOE, 200);
      add(map, (ItemLike)Items.WOODEN_AXE, 200);
      add(map, (ItemLike)Items.WOODEN_PICKAXE, 200);
      add(map, (Tag)ItemTags.WOODEN_DOORS, 200);
      add(map, (Tag)ItemTags.BOATS, 200);
      add(map, (Tag)ItemTags.WOOL, 100);
      add(map, (Tag)ItemTags.WOODEN_BUTTONS, 100);
      add(map, (ItemLike)Items.STICK, 100);
      add(map, (Tag)ItemTags.SAPLINGS, 100);
      add(map, (ItemLike)Items.BOWL, 100);
      add(map, (Tag)ItemTags.CARPETS, 67);
      add(map, (ItemLike)Blocks.DRIED_KELP_BLOCK, 4001);
      add(map, (ItemLike)Items.CROSSBOW, 300);
      add(map, (ItemLike)Blocks.BAMBOO, 50);
      add(map, (ItemLike)Blocks.DEAD_BUSH, 100);
      add(map, (ItemLike)Blocks.SCAFFOLDING, 50);
      add(map, (ItemLike)Blocks.LOOM, 300);
      add(map, (ItemLike)Blocks.BARREL, 300);
      add(map, (ItemLike)Blocks.CARTOGRAPHY_TABLE, 300);
      add(map, (ItemLike)Blocks.FLETCHING_TABLE, 300);
      add(map, (ItemLike)Blocks.SMITHING_TABLE, 300);
      add(map, (ItemLike)Blocks.COMPOSTER, 300);
      return map;
   }

   private static void add(Map map, Tag tag, int var2) {
      for(Item var4 : tag.getValues()) {
         map.put(var4, Integer.valueOf(var2));
      }

   }

   private static void add(Map map, ItemLike itemLike, int var2) {
      map.put(itemLike.asItem(), Integer.valueOf(var2));
   }

   private boolean isLit() {
      return this.litTime > 0;
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ContainerHelper.loadAllItems(compoundTag, this.items);
      this.litTime = compoundTag.getShort("BurnTime");
      this.cookingProgress = compoundTag.getShort("CookTime");
      this.cookingTotalTime = compoundTag.getShort("CookTimeTotal");
      this.litDuration = this.getBurnDuration((ItemStack)this.items.get(1));
      int var2 = compoundTag.getShort("RecipesUsedSize");

      for(int var3 = 0; var3 < var2; ++var3) {
         ResourceLocation var4 = new ResourceLocation(compoundTag.getString("RecipeLocation" + var3));
         int var5 = compoundTag.getInt("RecipeAmount" + var3);
         this.recipesUsed.put(var4, Integer.valueOf(var5));
      }

   }

   public CompoundTag save(CompoundTag compoundTag) {
      super.save(compoundTag);
      compoundTag.putShort("BurnTime", (short)this.litTime);
      compoundTag.putShort("CookTime", (short)this.cookingProgress);
      compoundTag.putShort("CookTimeTotal", (short)this.cookingTotalTime);
      ContainerHelper.saveAllItems(compoundTag, this.items);
      compoundTag.putShort("RecipesUsedSize", (short)this.recipesUsed.size());
      int var2 = 0;

      for(Entry<ResourceLocation, Integer> var4 : this.recipesUsed.entrySet()) {
         compoundTag.putString("RecipeLocation" + var2, ((ResourceLocation)var4.getKey()).toString());
         compoundTag.putInt("RecipeAmount" + var2, ((Integer)var4.getValue()).intValue());
         ++var2;
      }

      return compoundTag;
   }

   public void tick() {
      boolean var1 = this.isLit();
      boolean var2 = false;
      if(this.isLit()) {
         --this.litTime;
      }

      if(!this.level.isClientSide) {
         ItemStack var3 = (ItemStack)this.items.get(1);
         if(this.isLit() || !var3.isEmpty() && !((ItemStack)this.items.get(0)).isEmpty()) {
            Recipe<?> var4 = (Recipe)this.level.getRecipeManager().getRecipeFor(this.recipeType, this, this.level).orElse((Object)null);
            if(!this.isLit() && this.canBurn(var4)) {
               this.litTime = this.getBurnDuration(var3);
               this.litDuration = this.litTime;
               if(this.isLit()) {
                  var2 = true;
                  if(!var3.isEmpty()) {
                     Item var5 = var3.getItem();
                     var3.shrink(1);
                     if(var3.isEmpty()) {
                        Item var6 = var5.getCraftingRemainingItem();
                        this.items.set(1, var6 == null?ItemStack.EMPTY:new ItemStack(var6));
                     }
                  }
               }
            }

            if(this.isLit() && this.canBurn(var4)) {
               ++this.cookingProgress;
               if(this.cookingProgress == this.cookingTotalTime) {
                  this.cookingProgress = 0;
                  this.cookingTotalTime = this.getTotalCookTime();
                  this.burn(var4);
                  var2 = true;
               }
            } else {
               this.cookingProgress = 0;
            }
         } else if(!this.isLit() && this.cookingProgress > 0) {
            this.cookingProgress = Mth.clamp(this.cookingProgress - 2, 0, this.cookingTotalTime);
         }

         if(var1 != this.isLit()) {
            var2 = true;
            this.level.setBlock(this.worldPosition, (BlockState)this.level.getBlockState(this.worldPosition).setValue(AbstractFurnaceBlock.LIT, Boolean.valueOf(this.isLit())), 3);
         }
      }

      if(var2) {
         this.setChanged();
      }

   }

   protected boolean canBurn(@Nullable Recipe recipe) {
      if(!((ItemStack)this.items.get(0)).isEmpty() && recipe != null) {
         ItemStack var2 = recipe.getResultItem();
         if(var2.isEmpty()) {
            return false;
         } else {
            ItemStack var3 = (ItemStack)this.items.get(2);
            return var3.isEmpty()?true:(!var3.sameItem(var2)?false:(var3.getCount() < this.getMaxStackSize() && var3.getCount() < var3.getMaxStackSize()?true:var3.getCount() < var2.getMaxStackSize()));
         }
      } else {
         return false;
      }
   }

   private void burn(@Nullable Recipe recipeUsed) {
      if(recipeUsed != null && this.canBurn(recipeUsed)) {
         ItemStack var2 = (ItemStack)this.items.get(0);
         ItemStack var3 = recipeUsed.getResultItem();
         ItemStack var4 = (ItemStack)this.items.get(2);
         if(var4.isEmpty()) {
            this.items.set(2, var3.copy());
         } else if(var4.getItem() == var3.getItem()) {
            var4.grow(1);
         }

         if(!this.level.isClientSide) {
            this.setRecipeUsed(recipeUsed);
         }

         if(var2.getItem() == Blocks.WET_SPONGE.asItem() && !((ItemStack)this.items.get(1)).isEmpty() && ((ItemStack)this.items.get(1)).getItem() == Items.BUCKET) {
            this.items.set(1, new ItemStack(Items.WATER_BUCKET));
         }

         var2.shrink(1);
      }
   }

   protected int getBurnDuration(ItemStack itemStack) {
      if(itemStack.isEmpty()) {
         return 0;
      } else {
         Item var2 = itemStack.getItem();
         return ((Integer)getFuel().getOrDefault(var2, Integer.valueOf(0))).intValue();
      }
   }

   protected int getTotalCookTime() {
      return ((Integer)this.level.getRecipeManager().getRecipeFor(this.recipeType, this, this.level).map(AbstractCookingRecipe::getCookingTime).orElse(Integer.valueOf(200))).intValue();
   }

   public static boolean isFuel(ItemStack itemStack) {
      return getFuel().containsKey(itemStack.getItem());
   }

   public int[] getSlotsForFace(Direction direction) {
      return direction == Direction.DOWN?SLOTS_FOR_DOWN:(direction == Direction.UP?SLOTS_FOR_UP:SLOTS_FOR_SIDES);
   }

   public boolean canPlaceItemThroughFace(int var1, ItemStack itemStack, @Nullable Direction direction) {
      return this.canPlaceItem(var1, itemStack);
   }

   public boolean canTakeItemThroughFace(int var1, ItemStack itemStack, Direction direction) {
      if(direction == Direction.DOWN && var1 == 1) {
         Item var4 = itemStack.getItem();
         if(var4 != Items.WATER_BUCKET && var4 != Items.BUCKET) {
            return false;
         }
      }

      return true;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      for(ItemStack var2 : this.items) {
         if(!var2.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int i) {
      return (ItemStack)this.items.get(i);
   }

   public ItemStack removeItem(int var1, int var2) {
      return ContainerHelper.removeItem(this.items, var1, var2);
   }

   public ItemStack removeItemNoUpdate(int i) {
      return ContainerHelper.takeItem(this.items, i);
   }

   public void setItem(int var1, ItemStack itemStack) {
      ItemStack itemStack = (ItemStack)this.items.get(var1);
      boolean var4 = !itemStack.isEmpty() && itemStack.sameItem(itemStack) && ItemStack.tagMatches(itemStack, itemStack);
      this.items.set(var1, itemStack);
      if(itemStack.getCount() > this.getMaxStackSize()) {
         itemStack.setCount(this.getMaxStackSize());
      }

      if(var1 == 0 && !var4) {
         this.cookingTotalTime = this.getTotalCookTime();
         this.cookingProgress = 0;
         this.setChanged();
      }

   }

   public boolean stillValid(Player player) {
      return this.level.getBlockEntity(this.worldPosition) != this?false:player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
   }

   public boolean canPlaceItem(int var1, ItemStack itemStack) {
      if(var1 == 2) {
         return false;
      } else if(var1 != 1) {
         return true;
      } else {
         ItemStack itemStack = (ItemStack)this.items.get(1);
         return isFuel(itemStack) || itemStack.getItem() == Items.BUCKET && itemStack.getItem() != Items.BUCKET;
      }
   }

   public void clearContent() {
      this.items.clear();
   }

   public void setRecipeUsed(@Nullable Recipe recipeUsed) {
      if(recipeUsed != null) {
         this.recipesUsed.compute(recipeUsed.getId(), (resourceLocation, var1) -> {
            return Integer.valueOf(1 + (var1 == null?0:var1.intValue()));
         });
      }

   }

   @Nullable
   public Recipe getRecipeUsed() {
      return null;
   }

   public void awardAndReset(Player player) {
   }

   public void awardResetAndExperience(Player player) {
      List<Recipe<?>> var2 = Lists.newArrayList();

      for(Entry<ResourceLocation, Integer> var4 : this.recipesUsed.entrySet()) {
         player.level.getRecipeManager().byKey((ResourceLocation)var4.getKey()).ifPresent((recipe) -> {
            var2.add(recipe);
            createExperience(player, ((Integer)var4.getValue()).intValue(), ((AbstractCookingRecipe)recipe).getExperience());
         });
      }

      player.awardRecipes(var2);
      this.recipesUsed.clear();
   }

   private static void createExperience(Player player, int var1, float var2) {
      if(var2 == 0.0F) {
         var1 = 0;
      } else if(var2 < 1.0F) {
         int var3 = Mth.floor((float)var1 * var2);
         if(var3 < Mth.ceil((float)var1 * var2) && Math.random() < (double)((float)var1 * var2 - (float)var3)) {
            ++var3;
         }

         var1 = var3;
      }

      while(var1 > 0) {
         int var3 = ExperienceOrb.getExperienceValue(var1);
         var1 -= var3;
         player.level.addFreshEntity(new ExperienceOrb(player.level, player.x, player.y + 0.5D, player.z + 0.5D, var3));
      }

   }

   public void fillStackedContents(StackedContents stackedContents) {
      for(ItemStack var3 : this.items) {
         stackedContents.accountStack(var3);
      }

   }
}
