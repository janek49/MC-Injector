package net.minecraft.world.level.saveddata.maps;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class MapIndex extends SavedData {
   private final Object2IntMap usedAuxIds = new Object2IntOpenHashMap();

   public MapIndex() {
      super("idcounts");
      this.usedAuxIds.defaultReturnValue(-1);
   }

   public void load(CompoundTag compoundTag) {
      this.usedAuxIds.clear();

      for(String var3 : compoundTag.getAllKeys()) {
         if(compoundTag.contains(var3, 99)) {
            this.usedAuxIds.put(var3, compoundTag.getInt(var3));
         }
      }

   }

   public CompoundTag save(CompoundTag compoundTag) {
      ObjectIterator var2 = this.usedAuxIds.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Entry<String> var3 = (Entry)var2.next();
         compoundTag.putInt((String)var3.getKey(), var3.getIntValue());
      }

      return compoundTag;
   }

   public int getFreeAuxValueForMap() {
      int var1 = this.usedAuxIds.getInt("map") + 1;
      this.usedAuxIds.put("map", var1);
      this.setDirty();
      return var1;
   }
}
