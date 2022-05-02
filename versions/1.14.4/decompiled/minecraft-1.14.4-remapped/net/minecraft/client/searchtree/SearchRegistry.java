package net.minecraft.client.searchtree;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

@ClientJarOnly
public class SearchRegistry implements ResourceManagerReloadListener {
   public static final SearchRegistry.Key CREATIVE_NAMES = new SearchRegistry.Key();
   public static final SearchRegistry.Key CREATIVE_TAGS = new SearchRegistry.Key();
   public static final SearchRegistry.Key RECIPE_COLLECTIONS = new SearchRegistry.Key();
   private final Map searchTrees = Maps.newHashMap();

   public void onResourceManagerReload(ResourceManager resourceManager) {
      for(MutableSearchTree<?> var3 : this.searchTrees.values()) {
         var3.refresh();
      }

   }

   public void register(SearchRegistry.Key searchRegistry$Key, MutableSearchTree mutableSearchTree) {
      this.searchTrees.put(searchRegistry$Key, mutableSearchTree);
   }

   public MutableSearchTree getTree(SearchRegistry.Key searchRegistry$Key) {
      return (MutableSearchTree)this.searchTrees.get(searchRegistry$Key);
   }

   @ClientJarOnly
   public static class Key {
   }
}
