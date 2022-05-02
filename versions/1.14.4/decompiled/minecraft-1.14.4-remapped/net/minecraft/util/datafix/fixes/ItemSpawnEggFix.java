package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class ItemSpawnEggFix extends DataFix {
   private static final String[] ID_TO_ENTITY = (String[])DataFixUtils.make(new String[256], (strings) -> {
      strings[1] = "Item";
      strings[2] = "XPOrb";
      strings[7] = "ThrownEgg";
      strings[8] = "LeashKnot";
      strings[9] = "Painting";
      strings[10] = "Arrow";
      strings[11] = "Snowball";
      strings[12] = "Fireball";
      strings[13] = "SmallFireball";
      strings[14] = "ThrownEnderpearl";
      strings[15] = "EyeOfEnderSignal";
      strings[16] = "ThrownPotion";
      strings[17] = "ThrownExpBottle";
      strings[18] = "ItemFrame";
      strings[19] = "WitherSkull";
      strings[20] = "PrimedTnt";
      strings[21] = "FallingSand";
      strings[22] = "FireworksRocketEntity";
      strings[23] = "TippedArrow";
      strings[24] = "SpectralArrow";
      strings[25] = "ShulkerBullet";
      strings[26] = "DragonFireball";
      strings[30] = "ArmorStand";
      strings[41] = "Boat";
      strings[42] = "MinecartRideable";
      strings[43] = "MinecartChest";
      strings[44] = "MinecartFurnace";
      strings[45] = "MinecartTNT";
      strings[46] = "MinecartHopper";
      strings[47] = "MinecartSpawner";
      strings[40] = "MinecartCommandBlock";
      strings[48] = "Mob";
      strings[49] = "Monster";
      strings[50] = "Creeper";
      strings[51] = "Skeleton";
      strings[52] = "Spider";
      strings[53] = "Giant";
      strings[54] = "Zombie";
      strings[55] = "Slime";
      strings[56] = "Ghast";
      strings[57] = "PigZombie";
      strings[58] = "Enderman";
      strings[59] = "CaveSpider";
      strings[60] = "Silverfish";
      strings[61] = "Blaze";
      strings[62] = "LavaSlime";
      strings[63] = "EnderDragon";
      strings[64] = "WitherBoss";
      strings[65] = "Bat";
      strings[66] = "Witch";
      strings[67] = "Endermite";
      strings[68] = "Guardian";
      strings[69] = "Shulker";
      strings[90] = "Pig";
      strings[91] = "Sheep";
      strings[92] = "Cow";
      strings[93] = "Chicken";
      strings[94] = "Squid";
      strings[95] = "Wolf";
      strings[96] = "MushroomCow";
      strings[97] = "SnowMan";
      strings[98] = "Ozelot";
      strings[99] = "VillagerGolem";
      strings[100] = "EntityHorse";
      strings[101] = "Rabbit";
      strings[120] = "Villager";
      strings[200] = "EnderCrystal";
   });

   public ItemSpawnEggFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      Schema var1 = this.getInputSchema();
      Type<?> var2 = var1.getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> var3 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<String> var4 = DSL.fieldFinder("id", DSL.string());
      OpticFinder<?> var5 = var2.findField("tag");
      OpticFinder<?> var6 = var5.type().findField("EntityTag");
      OpticFinder<?> var7 = DSL.typeFinder(var1.getTypeRaw(References.ENTITY));
      return this.fixTypeEverywhereTyped("ItemSpawnEggFix", var2, (var6x) -> {
         Optional<Pair<String, String>> var7 = var6x.getOptional(var3);
         if(var7.isPresent() && Objects.equals(((Pair)var7.get()).getSecond(), "minecraft:spawn_egg")) {
            Dynamic<?> var8 = (Dynamic)var6x.get(DSL.remainderFinder());
            short var9 = var8.get("Damage").asShort((short)0);
            Optional<? extends Typed<?>> var10 = var6x.getOptionalTyped(var5);
            Optional<? extends Typed<?>> var11 = var10.flatMap((typed) -> {
               return typed.getOptionalTyped(var6);
            });
            Optional<? extends Typed<?>> var12 = var11.flatMap((typed) -> {
               return typed.getOptionalTyped(var7);
            });
            Optional<String> var13 = var12.flatMap((typed) -> {
               return typed.getOptional(var4);
            });
            Typed<?> var14 = var6x;
            String var15 = ID_TO_ENTITY[var9 & 255];
            if(var15 != null && (!var13.isPresent() || !Objects.equals(var13.get(), var15))) {
               Typed<?> var16 = var6x.getOrCreateTyped(var5);
               Typed<?> var17 = var16.getOrCreateTyped(var6);
               Typed<?> var18 = var17.getOrCreateTyped(var7);
               Dynamic<?> var19 = var18.write().set("id", var8.createString(var15));
               Typed<?> var20 = (Typed)((Optional)this.getOutputSchema().getTypeRaw(References.ENTITY).readTyped(var19).getSecond()).orElseThrow(() -> {
                  return new IllegalStateException("Could not parse new entity");
               });
               var14 = var6x.set(var5, var16.set(var6, var17.set(var7, var20)));
            }

            if(var9 != 0) {
               var8 = var8.set("Damage", var8.createShort((short)0));
               var14 = var14.set(DSL.remainderFinder(), var8);
            }

            return var14;
         } else {
            return var6x;
         }
      });
   }
}
