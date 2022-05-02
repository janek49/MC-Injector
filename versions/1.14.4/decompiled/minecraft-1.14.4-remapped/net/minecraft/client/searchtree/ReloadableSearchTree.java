package net.minecraft.client.searchtree;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.searchtree.ReloadableIdSearchTree;
import net.minecraft.client.searchtree.SuffixArray;

@ClientJarOnly
public class ReloadableSearchTree extends ReloadableIdSearchTree {
   protected SuffixArray tree = new SuffixArray();
   private final Function filler;

   public ReloadableSearchTree(Function filler, Function var2) {
      super(var2);
      this.filler = filler;
   }

   public void refresh() {
      this.tree = new SuffixArray();
      super.refresh();
      this.tree.generate();
   }

   protected void index(Object object) {
      super.index(object);
      ((Stream)this.filler.apply(object)).forEach((string) -> {
         this.tree.add(object, string.toLowerCase(Locale.ROOT));
      });
   }

   public List search(String string) {
      int var2 = string.indexOf(58);
      if(var2 < 0) {
         return this.tree.search(string);
      } else {
         List<T> var3 = this.namespaceTree.search(string.substring(0, var2).trim());
         String var4 = string.substring(var2 + 1).trim();
         List<T> var5 = this.pathTree.search(var4);
         List<T> var6 = this.tree.search(var4);
         return Lists.newArrayList(new ReloadableIdSearchTree.IntersectionIterator(var3.iterator(), new ReloadableSearchTree.MergingUniqueIterator(var5.iterator(), var6.iterator(), this::comparePosition), this::comparePosition));
      }
   }

   @ClientJarOnly
   static class MergingUniqueIterator extends AbstractIterator {
      private final PeekingIterator firstIterator;
      private final PeekingIterator secondIterator;
      private final Comparator orderT;

      public MergingUniqueIterator(Iterator var1, Iterator var2, Comparator orderT) {
         this.firstIterator = Iterators.peekingIterator(var1);
         this.secondIterator = Iterators.peekingIterator(var2);
         this.orderT = orderT;
      }

      protected Object computeNext() {
         boolean var1 = !this.firstIterator.hasNext();
         boolean var2 = !this.secondIterator.hasNext();
         if(var1 && var2) {
            return this.endOfData();
         } else if(var1) {
            return this.secondIterator.next();
         } else if(var2) {
            return this.firstIterator.next();
         } else {
            int var3 = this.orderT.compare(this.firstIterator.peek(), this.secondIterator.peek());
            if(var3 == 0) {
               this.secondIterator.next();
            }

            return var3 <= 0?this.firstIterator.next():this.secondIterator.next();
         }
      }
   }
}
