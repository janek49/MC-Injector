package net.minecraft.world.level.block.entity;

import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickableBlockEntity;

public class CampfireBlockEntity extends BlockEntity implements Clearable, TickableBlockEntity {
   private final NonNullList items = NonNullList.withSize(4, ItemStack.EMPTY);
   private final int[] cookingProgress = new int[4];
   private final int[] cookingTime = new int[4];

   public CampfireBlockEntity() {
      super(BlockEntityType.CAMPFIRE);
   }

   public void tick() {
      boolean var1 = ((Boolean)this.getBlockState().getValue(CampfireBlock.LIT)).booleanValue();
      boolean var2 = this.level.isClientSide;
      if(var2) {
         if(var1) {
            this.makeParticles();
         }

      } else {
         if(var1) {
            this.cook();
         } else {
            for(int var3 = 0; var3 < this.items.size(); ++var3) {
               if(this.cookingProgress[var3] > 0) {
                  this.cookingProgress[var3] = Mth.clamp(this.cookingProgress[var3] - 2, 0, this.cookingTime[var3]);
               }
            }
         }

      }
   }

   private void cook() {
      for(int var1 = 0; var1 < this.items.size(); ++var1) {
         ItemStack var2 = (ItemStack)this.items.get(var1);
         if(!var2.isEmpty()) {
            ++this.cookingProgress[var1];
            if(this.cookingProgress[var1] >= this.cookingTime[var1]) {
               Container var3 = new SimpleContainer(new ItemStack[]{var2});
               ItemStack var4 = (ItemStack)this.level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, var3, this.level).map((campfireCookingRecipe) -> {
                  return campfireCookingRecipe.assemble(var3);
               }).orElse(var2);
               BlockPos var5 = this.getBlockPos();
               Containers.dropItemStack(this.level, (double)var5.getX(), (double)var5.getY(), (double)var5.getZ(), var4);
               this.items.set(var1, ItemStack.EMPTY);
               this.markUpdated();
            }
         }
      }

   }

   private void makeParticles() {
      Level var1 = this.getLevel();
      if(var1 != null) {
         BlockPos var2 = this.getBlockPos();
         Random var3 = var1.random;
         if(var3.nextFloat() < 0.11F) {
            for(int var4 = 0; var4 < var3.nextInt(2) + 2; ++var4) {
               CampfireBlock.makeParticles(var1, var2, ((Boolean)this.getBlockState().getValue(CampfireBlock.SIGNAL_FIRE)).booleanValue(), false);
            }
         }

         int var4 = ((Direction)this.getBlockState().getValue(CampfireBlock.FACING)).get2DDataValue();

         for(int var5 = 0; var5 < this.items.size(); ++var5) {
            if(!((ItemStack)this.items.get(var5)).isEmpty() && var3.nextFloat() < 0.2F) {
               Direction var6 = Direction.from2DDataValue(Math.floorMod(var5 + var4, 4));
               float var7 = 0.3125F;
               double var8 = (double)var2.getX() + 0.5D - (double)((float)var6.getStepX() * 0.3125F) + (double)((float)var6.getClockWise().getStepX() * 0.3125F);
               double var10 = (double)var2.getY() + 0.5D;
               double var12 = (double)var2.getZ() + 0.5D - (double)((float)var6.getStepZ() * 0.3125F) + (double)((float)var6.getClockWise().getStepZ() * 0.3125F);

               for(int var14 = 0; var14 < 4; ++var14) {
                  var1.addParticle(ParticleTypes.SMOKE, var8, var10, var12, 0.0D, 5.0E-4D, 0.0D);
               }
            }
         }

      }
   }

   public NonNullList getItems() {
      return this.items;
   }

   public void load(CompoundTag compoundTag) {
      super.load(compoundTag);
      this.items.clear();
      ContainerHelper.loadAllItems(compoundTag, this.items);
      if(compoundTag.contains("CookingTimes", 11)) {
         int[] vars2 = compoundTag.getIntArray("CookingTimes");
         System.arraycopy(vars2, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, vars2.length));
      }

      if(compoundTag.contains("CookingTotalTimes", 11)) {
         int[] vars2 = compoundTag.getIntArray("CookingTotalTimes");
         System.arraycopy(vars2, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, vars2.length));
      }

   }

   public CompoundTag save(CompoundTag compoundTag) {
      this.saveMetadataAndItems(compoundTag);
      compoundTag.putIntArray("CookingTimes", this.cookingProgress);
      compoundTag.putIntArray("CookingTotalTimes", this.cookingTime);
      return compoundTag;
   }

   private CompoundTag saveMetadataAndItems(CompoundTag compoundTag) {
      super.save(compoundTag);
      ContainerHelper.saveAllItems(compoundTag, this.items, true);
      return compoundTag;
   }

   @Nullable
   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return new ClientboundBlockEntityDataPacket(this.worldPosition, 13, this.getUpdateTag());
   }

   public CompoundTag getUpdateTag() {
      return this.saveMetadataAndItems(new CompoundTag());
   }

   public Optional getCookableRecipe(ItemStack itemStack) {
      return this.items.stream().noneMatch(ItemStack::isEmpty)?Optional.empty():this.level.getRecipeManager().getRecipeFor(RecipeType.CAMPFIRE_COOKING, new SimpleContainer(new ItemStack[]{itemStack}), this.level);
   }

   public boolean placeFood(ItemStack itemStack, int var2) {
      for(int var3 = 0; var3 < this.items.size(); ++var3) {
         ItemStack var4 = (ItemStack)this.items.get(var3);
         if(var4.isEmpty()) {
            this.cookingTime[var3] = var2;
            this.cookingProgress[var3] = 0;
            this.items.set(var3, itemStack.split(1));
            this.markUpdated();
            return true;
         }
      }

      return false;
   }

   private void markUpdated() {
      this.setChanged();
      this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
   }

   public void clearContent() {
      this.items.clear();
   }

   public void dowse() {
      if(!this.getLevel().isClientSide) {
         Containers.dropContents(this.getLevel(), this.getBlockPos(), this.getItems());
      }

      this.markUpdated();
   }
}
