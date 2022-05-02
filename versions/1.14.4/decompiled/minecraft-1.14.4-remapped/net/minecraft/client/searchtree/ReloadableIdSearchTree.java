package net.minecraft.client.searchtree;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.SuffixArray;

@ClientJarOnly
public class ReloadableIdSearchTree implements MutableSearchTree {
   protected SuffixArray namespaceTree = new SuffixArray();
   protected SuffixArray pathTree = new SuffixArray();
   private final Function idGetter;
   private final List contents = Lists.newArrayList();
   private final Object2IntMap orderT = new Object2IntOpenHashMap();

   public ReloadableIdSearchTree(Function idGetter) {
      this.idGetter = idGetter;
   }

   public void refresh() {
      this.namespaceTree = new SuffixArray();
      this.pathTree = new SuffixArray();

      for(T var2 : this.contents) {
         this.index(var2);
      }

      this.namespaceTree.generate();
      this.pathTree.generate();
   }

   public void add(Object object) {
      this.orderT.put(object, this.contents.size());
      this.contents.add(object);
      this.index(object);
   }

   public void clear() {
      this.contents.clear();
      this.orderT.clear();
   }

   protected void index(Object object) {
      ((Stream)this.idGetter.apply(object)).forEach((resourceLocation) -> {
         this.namespaceTree.add(object, resourceLocation.getNamespace().toLowerCase(Locale.ROOT));
         this.pathTree.add(object, resourceLocation.getPath().toLowerCase(Locale.ROOT));
      });
   }

   protected int comparePosition(Object var1, Object var2) {
      return Integer.compare(this.orderT.getInt(var1), this.orderT.getInt(var2));
   }

   public List search(String string) {
      int var2 = string.indexOf(58);
      if(var2 == -1) {
         return this.pathTree.search(string);
      } else {
         List<T> var3 = this.namespaceTree.search(string.substring(0, var2).trim());
         String var4 = string.substring(var2 + 1).trim();
         List<T> var5 = this.pathTree.search(var4);
         return Lists.newArrayList(new ReloadableIdSearchTree.IntersectionIterator(var3.iterator(), var5.iterator(), this::comparePosition));
      }
   }

   @ClientJarOnly
   public static class IntersectionIterator extends AbstractIterator {
      private final PeekingIterator firstIterator;
      private final PeekingIterator secondIterator;
      private final Comparator orderT;

      public IntersectionIterator(Iterator var1, Iterator var2, Comparator orderT) {
         this.firstIterator = Iterators.peekingIterator(var1);
         this.secondIterator = Iterators.peekingIterator(var2);
         this.orderT = orderT;
      }

      protected Object computeNext() {
         while(this.firstIterator.hasNext() && this.secondIterator.hasNext()) {
            int var1 = this.orderT.compare(this.firstIterator.peek(), this.secondIterator.peek());
            if(var1 == 0) {
               this.secondIterator.next();
               return this.firstIterator.next();
            }

            if(var1 < 0) {
               this.firstIterator.next();
            } else {
               this.secondIterator.next();
            }
         }

         return this.endOfData();
      }
   }
}
