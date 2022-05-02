package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.Objects;
import net.minecraft.util.datafix.fixes.SimplestEntityRenameFix;

public class EntityRavagerRenameFix extends SimplestEntityRenameFix {
   public static final Map RENAMED_IDS = ImmutableMap.builder().put("minecraft:illager_beast_spawn_egg", "minecraft:ravager_spawn_egg").build();

   public EntityRavagerRenameFix(Schema schema, boolean var2) {
      super("EntityRavagerRenameFix", schema, var2);
   }

   protected String rename(String string) {
      return Objects.equals("minecraft:illager_beast", string)?"minecraft:ravager":string;
   }
}
