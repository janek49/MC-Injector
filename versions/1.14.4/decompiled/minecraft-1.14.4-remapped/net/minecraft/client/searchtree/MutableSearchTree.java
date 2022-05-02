package net.minecraft.client.searchtree;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.searchtree.SearchTree;

@ClientJarOnly
public interface MutableSearchTree extends SearchTree {
   void add(Object var1);

   void clear();

   void refresh();
}
