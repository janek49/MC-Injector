package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Sets;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class EntityHealthFix extends DataFix {
   private static final Set ENTITIES = Sets.newHashSet(new String[]{"ArmorStand", "Bat", "Blaze", "CaveSpider", "Chicken", "Cow", "Creeper", "EnderDragon", "Enderman", "Endermite", "EntityHorse", "Ghast", "Giant", "Guardian", "LavaSlime", "MushroomCow", "Ozelot", "Pig", "PigZombie", "Rabbit", "Sheep", "Shulker", "Silverfish", "Skeleton", "Slime", "SnowMan", "Spider", "Squid", "Villager", "VillagerGolem", "Witch", "WitherBoss", "Wolf", "Zombie"});

   public EntityHealthFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public Dynamic fixTag(Dynamic dynamic) {
      Optional<Number> var3 = dynamic.get("HealF").asNumber();
      Optional<Number> var4 = dynamic.get("Health").asNumber();
      float var2;
      if(var3.isPresent()) {
         var2 = ((Number)var3.get()).floatValue();
         dynamic = dynamic.remove("HealF");
      } else {
         if(!var4.isPresent()) {
            return dynamic;
         }

         var2 = ((Number)var4.get()).floatValue();
      }

      return dynamic.set("Health", dynamic.createFloat(var2));
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityHealthFix", this.getInputSchema().getType(References.ENTITY), (typed) -> {
         return typed.update(DSL.remainderFinder(), this::fixTag);
      });
   }
}
