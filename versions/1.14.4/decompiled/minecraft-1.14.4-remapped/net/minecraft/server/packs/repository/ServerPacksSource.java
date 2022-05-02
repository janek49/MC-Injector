package net.minecraft.server.packs.repository;

import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.server.packs.VanillaPack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.UnopenedPack;

public class ServerPacksSource implements RepositorySource {
   private final VanillaPack vanillaPack = new VanillaPack(new String[]{"minecraft"});

   public void loadPacks(Map map, UnopenedPack.UnopenedPackConstructor unopenedPack$UnopenedPackConstructor) {
      T var3 = UnopenedPack.create("vanilla", false, () -> {
         return this.vanillaPack;
      }, unopenedPack$UnopenedPackConstructor, UnopenedPack.Position.BOTTOM);
      if(var3 != null) {
         map.put("vanilla", var3);
      }

   }
}
