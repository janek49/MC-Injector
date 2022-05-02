package net.minecraft.advancements;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;

public class TreeNodePosition {
   private final Advancement advancement;
   private final TreeNodePosition parent;
   private final TreeNodePosition previousSibling;
   private final int childIndex;
   private final List children = Lists.newArrayList();
   private TreeNodePosition ancestor;
   private TreeNodePosition thread;
   private int x;
   private float y;
   private float mod;
   private float change;
   private float shift;

   public TreeNodePosition(Advancement advancement, @Nullable TreeNodePosition parent, @Nullable TreeNodePosition previousSibling, int childIndex, int x) {
      if(advancement.getDisplay() == null) {
         throw new IllegalArgumentException("Can\'t position an invisible advancement!");
      } else {
         this.advancement = advancement;
         this.parent = parent;
         this.previousSibling = previousSibling;
         this.childIndex = childIndex;
         this.ancestor = this;
         this.x = x;
         this.y = -1.0F;
         TreeNodePosition var6 = null;

         for(Advancement var8 : advancement.getChildren()) {
            var6 = this.addChild(var8, var6);
         }

      }
   }

   @Nullable
   private TreeNodePosition addChild(Advancement advancement, @Nullable TreeNodePosition var2) {
      if(advancement.getDisplay() != null) {
         var2 = new TreeNodePosition(advancement, this, var2, this.children.size() + 1, this.x + 1);
         this.children.add(var2);
      } else {
         for(Advancement var4 : advancement.getChildren()) {
            var2 = this.addChild(var4, var2);
         }
      }

      return var2;
   }

   private void firstWalk() {
      if(this.children.isEmpty()) {
         if(this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0F;
         } else {
            this.y = 0.0F;
         }

      } else {
         TreeNodePosition var1 = null;

         for(TreeNodePosition var3 : this.children) {
            var3.firstWalk();
            var1 = var3.apportion(var1 == null?var3:var1);
         }

         this.executeShifts();
         float var2 = (((TreeNodePosition)this.children.get(0)).y + ((TreeNodePosition)this.children.get(this.children.size() - 1)).y) / 2.0F;
         if(this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0F;
            this.mod = this.y - var2;
         } else {
            this.y = var2;
         }

      }
   }

   private float secondWalk(float var1, int x, float var3) {
      this.y += var1;
      this.x = x;
      if(this.y < var3) {
         var3 = this.y;
      }

      for(TreeNodePosition var5 : this.children) {
         var3 = var5.secondWalk(var1 + this.mod, x + 1, var3);
      }

      return var3;
   }

   private void thirdWalk(float f) {
      this.y += f;

      for(TreeNodePosition var3 : this.children) {
         var3.thirdWalk(f);
      }

   }

   private void executeShifts() {
      float var1 = 0.0F;
      float var2 = 0.0F;

      for(int var3 = this.children.size() - 1; var3 >= 0; --var3) {
         TreeNodePosition var4 = (TreeNodePosition)this.children.get(var3);
         var4.y += var1;
         var4.mod += var1;
         var2 += var4.change;
         var1 += var4.shift + var2;
      }

   }

   @Nullable
   private TreeNodePosition previousOrThread() {
      return this.thread != null?this.thread:(!this.children.isEmpty()?(TreeNodePosition)this.children.get(0):null);
   }

   @Nullable
   private TreeNodePosition nextOrThread() {
      return this.thread != null?this.thread:(!this.children.isEmpty()?(TreeNodePosition)this.children.get(this.children.size() - 1):null);
   }

   private TreeNodePosition apportion(TreeNodePosition treeNodePosition) {
      if(this.previousSibling == null) {
         return treeNodePosition;
      } else {
         TreeNodePosition var2 = this;
         TreeNodePosition var3 = this;
         TreeNodePosition var4 = this.previousSibling;
         TreeNodePosition var5 = (TreeNodePosition)this.parent.children.get(0);
         float var6 = this.mod;
         float var7 = this.mod;
         float var8 = var4.mod;

         float var9;
         for(var9 = var5.mod; var4.nextOrThread() != null && var2.previousOrThread() != null; var7 += var3.mod) {
            var4 = var4.nextOrThread();
            var2 = var2.previousOrThread();
            var5 = var5.previousOrThread();
            var3 = var3.nextOrThread();
            var3.ancestor = this;
            float var10 = var4.y + var8 - (var2.y + var6) + 1.0F;
            if(var10 > 0.0F) {
               var4.getAncestor(this, treeNodePosition).moveSubtree(this, var10);
               var6 += var10;
               var7 += var10;
            }

            var8 += var4.mod;
            var6 += var2.mod;
            var9 += var5.mod;
         }

         if(var4.nextOrThread() != null && var3.nextOrThread() == null) {
            var3.thread = var4.nextOrThread();
            var3.mod += var8 - var7;
         } else {
            if(var2.previousOrThread() != null && var5.previousOrThread() == null) {
               var5.thread = var2.previousOrThread();
               var5.mod += var6 - var9;
            }

            treeNodePosition = this;
         }

         return treeNodePosition;
      }
   }

   private void moveSubtree(TreeNodePosition treeNodePosition, float var2) {
      float var3 = (float)(treeNodePosition.childIndex - this.childIndex);
      if(var3 != 0.0F) {
         treeNodePosition.change -= var2 / var3;
         this.change += var2 / var3;
      }

      treeNodePosition.shift += var2;
      treeNodePosition.y += var2;
      treeNodePosition.mod += var2;
   }

   private TreeNodePosition getAncestor(TreeNodePosition var1, TreeNodePosition var2) {
      return this.ancestor != null && var1.parent.children.contains(this.ancestor)?this.ancestor:var2;
   }

   private void finalizePosition() {
      if(this.advancement.getDisplay() != null) {
         this.advancement.getDisplay().setLocation((float)this.x, this.y);
      }

      if(!this.children.isEmpty()) {
         for(TreeNodePosition var2 : this.children) {
            var2.finalizePosition();
         }
      }

   }

   public static void run(Advancement advancement) {
      if(advancement.getDisplay() == null) {
         throw new IllegalArgumentException("Can\'t position children of an invisible root!");
      } else {
         TreeNodePosition var1 = new TreeNodePosition(advancement, (TreeNodePosition)null, (TreeNodePosition)null, 1, 0);
         var1.firstWalk();
         float var2 = var1.secondWalk(0.0F, 0, var1.y);
         if(var2 < 0.0F) {
            var1.thirdWalk(-var2);
         }

         var1.finalizePosition();
      }
   }
}
