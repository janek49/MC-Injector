package net.minecraft.server.packs.repository;

import java.util.Map;
import net.minecraft.server.packs.repository.UnopenedPack;

public interface RepositorySource {
   void loadPacks(Map var1, UnopenedPack.UnopenedPackConstructor var2);
}
