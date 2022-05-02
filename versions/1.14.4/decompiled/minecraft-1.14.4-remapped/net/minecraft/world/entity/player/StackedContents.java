package net.minecraft.world.entity.player;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.BitSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class StackedContents {
   public final Int2IntMap contents = new Int2IntOpenHashMap();

   public void accountSimpleStack(ItemStack itemStack) {
      if(!itemStack.isDamaged() && !itemStack.isEnchanted() && !itemStack.hasCustomHoverName()) {
         this.accountStack(itemStack);
      }

   }

   public void accountStack(ItemStack itemStack) {
      this.accountStack(itemStack, 64);
   }

   public void accountStack(ItemStack itemStack, int var2) {
      if(!itemStack.isEmpty()) {
         int var3 = getStackingIndex(itemStack);
         int var4 = Math.min(var2, itemStack.getCount());
         this.put(var3, var4);
      }

   }

   public static int getStackingIndex(ItemStack itemStack) {
      return Registry.ITEM.getId(itemStack.getItem());
   }

   private boolean has(int i) {
      return this.contents.get(i) > 0;
   }

   private int take(int var1, int var2) {
      int var3 = this.contents.get(var1);
      if(var3 >= var2) {
         this.contents.put(var1, var3 - var2);
         return var1;
      } else {
         return 0;
      }
   }

   private void put(int var1, int var2) {
      this.contents.put(var1, this.contents.get(var1) + var2);
   }

   public boolean canCraft(Recipe recipe, @Nullable IntList intList) {
      return this.canCraft(recipe, intList, 1);
   }

   public boolean canCraft(Recipe recipe, @Nullable IntList intList, int var3) {
      return (new StackedContents.RecipePicker(recipe)).tryPick(var3, intList);
   }

   public int getBiggestCraftableStack(Recipe recipe, @Nullable IntList intList) {
      return this.getBiggestCraftableStack(recipe, Integer.MAX_VALUE, intList);
   }

   public int getBiggestCraftableStack(Recipe recipe, int var2, @Nullable IntList intList) {
      return (new StackedContents.RecipePicker(recipe)).tryPickAll(var2, intList);
   }

   public static ItemStack fromStackingIndex(int stackingIndex) {
      return stackingIndex == 0?ItemStack.EMPTY:new ItemStack(Item.byId(stackingIndex));
   }

   public void clear() {
      this.contents.clear();
   }

   class RecipePicker {
      private final Recipe recipe;
      private final List ingredients = Lists.newArrayList();
      private final int ingredientCount;
      private final int[] items;
      private final int itemCount;
      private final BitSet data;
      private final IntList path = new IntArrayList();

      public RecipePicker(Recipe recipe) {
         this.recipe = recipe;
         this.ingredients.addAll(recipe.getIngredients());
         this.ingredients.removeIf(Ingredient::isEmpty);
         this.ingredientCount = this.ingredients.size();
         this.items = this.getUniqueAvailableIngredientItems();
         this.itemCount = this.items.length;
         this.data = new BitSet(this.ingredientCount + this.itemCount + this.ingredientCount + this.ingredientCount * this.itemCount);

         for(int var3 = 0; var3 < this.ingredients.size(); ++var3) {
            IntList var4 = ((Ingredient)this.ingredients.get(var3)).getStackingIds();

            for(int var5 = 0; var5 < this.itemCount; ++var5) {
               if(var4.contains(this.items[var5])) {
                  this.data.set(this.getIndex(true, var5, var3));
               }
            }
         }

      }

      public boolean tryPick(int var1, @Nullable IntList intList) {
         if(var1 <= 0) {
            return true;
         } else {
            int var3;
            for(var3 = 0; this.dfs(var1); ++var3) {
               StackedContents.this.take(this.items[this.path.getInt(0)], var1);
               int var4 = this.path.size() - 1;
               this.setSatisfied(this.path.getInt(var4));

               for(int var5 = 0; var5 < var4; ++var5) {
                  this.toggleResidual((var5 & 1) == 0, this.path.get(var5).intValue(), this.path.get(var5 + 1).intValue());
               }

               this.path.clear();
               this.data.clear(0, this.ingredientCount + this.itemCount);
            }

            boolean var4 = var3 == this.ingredientCount;
            boolean var5 = var4 && intList != null;
            if(var5) {
               intList.clear();
            }

            this.data.clear(0, this.ingredientCount + this.itemCount + this.ingredientCount);
            int var6 = 0;
            List<Ingredient> var7 = this.recipe.getIngredients();

            for(int var8 = 0; var8 < ((List)var7).size(); ++var8) {
               if(var5 && ((Ingredient)var7.get(var8)).isEmpty()) {
                  intList.add(0);
               } else {
                  for(int var9 = 0; var9 < this.itemCount; ++var9) {
                     if(this.hasResidual(false, var6, var9)) {
                        this.toggleResidual(true, var9, var6);
                        StackedContents.this.put(this.items[var9], var1);
                        if(var5) {
                           intList.add(this.items[var9]);
                        }
                     }
                  }

                  ++var6;
               }
            }

            return var4;
         }
      }

      private int[] getUniqueAvailableIngredientItems() {
         IntCollection var1 = new IntAVLTreeSet();

         for(Ingredient var3 : this.ingredients) {
            var1.addAll(var3.getStackingIds());
         }

         IntIterator var2 = var1.iterator();

         while(var2.hasNext()) {
            if(!StackedContents.this.has(var2.nextInt())) {
               var2.remove();
            }
         }

         return var1.toIntArray();
      }

      private boolean dfs(int i) {
         int var2 = this.itemCount;

         for(int var3 = 0; var3 < var2; ++var3) {
            if(StackedContents.this.contents.get(this.items[var3]) >= i) {
               this.visit(false, var3);

               while(!this.path.isEmpty()) {
                  int var4 = this.path.size();
                  boolean var5 = (var4 & 1) == 1;
                  int var6 = this.path.getInt(var4 - 1);
                  if(!var5 && !this.isSatisfied(var6)) {
                     break;
                  }

                  int var7 = var5?this.ingredientCount:var2;

                  for(int var8 = 0; var8 < var7; ++var8) {
                     if(!this.hasVisited(var5, var8) && this.hasConnection(var5, var6, var8) && this.hasResidual(var5, var6, var8)) {
                        this.visit(var5, var8);
                        break;
                     }
                  }

                  int var8 = this.path.size();
                  if(var8 == var4) {
                     this.path.removeInt(var8 - 1);
                  }
               }

               if(!this.path.isEmpty()) {
                  return true;
               }
            }
         }

         return false;
      }

      private boolean isSatisfied(int i) {
         return this.data.get(this.getSatisfiedIndex(i));
      }

      private void setSatisfied(int satisfied) {
         this.data.set(this.getSatisfiedIndex(satisfied));
      }

      private int getSatisfiedIndex(int i) {
         return this.ingredientCount + this.itemCount + i;
      }

      private boolean hasConnection(boolean var1, int var2, int var3) {
         return this.data.get(this.getIndex(var1, var2, var3));
      }

      private boolean hasResidual(boolean var1, int var2, int var3) {
         return var1 != this.data.get(1 + this.getIndex(var1, var2, var3));
      }

      private void toggleResidual(boolean var1, int var2, int var3) {
         this.data.flip(1 + this.getIndex(var1, var2, var3));
      }

      private int getIndex(boolean var1, int var2, int var3) {
         int var4 = var1?var2 * this.ingredientCount + var3:var3 * this.ingredientCount + var2;
         return this.ingredientCount + this.itemCount + this.ingredientCount + 2 * var4;
      }

      private void visit(boolean var1, int var2) {
         this.data.set(this.getVisitedIndex(var1, var2));
         this.path.add(var2);
      }

      private boolean hasVisited(boolean var1, int var2) {
         return this.data.get(this.getVisitedIndex(var1, var2));
      }

      private int getVisitedIndex(boolean var1, int var2) {
         return (var1?0:this.ingredientCount) + var2;
      }

      public int tryPickAll(int var1, @Nullable IntList intList) {
         int var3 = 0;
         int var4 = Math.min(var1, this.getMinIngredientCount()) + 1;

         while(true) {
            int var5 = (var3 + var4) / 2;
            if(this.tryPick(var5, (IntList)null)) {
               if(var4 - var3 <= 1) {
                  if(var5 > 0) {
                     this.tryPick(var5, intList);
                  }

                  return var5;
               }

               var3 = var5;
            } else {
               var4 = var5;
            }
         }
      }

      private int getMinIngredientCount() {
         int var1 = Integer.MAX_VALUE;

         for(Ingredient var3 : this.ingredients) {
            int var4 = 0;

            int var6;
            for(IntListIterator var5 = var3.getStackingIds().iterator(); var5.hasNext(); var4 = Math.max(var4, StackedContents.this.contents.get(var6))) {
               var6 = ((Integer)var5.next()).intValue();
            }

            if(var1 > 0) {
               var1 = Math.min(var1, var4);
            }
         }

         return var1;
      }
   }
}
