package net.minecraft.util.datafix.schemas;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class V99 extends Schema {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map ITEM_TO_BLOCKENTITY = (Map)DataFixUtils.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put("minecraft:furnace", "Furnace");
      hashMap.put("minecraft:lit_furnace", "Furnace");
      hashMap.put("minecraft:chest", "Chest");
      hashMap.put("minecraft:trapped_chest", "Chest");
      hashMap.put("minecraft:ender_chest", "EnderChest");
      hashMap.put("minecraft:jukebox", "RecordPlayer");
      hashMap.put("minecraft:dispenser", "Trap");
      hashMap.put("minecraft:dropper", "Dropper");
      hashMap.put("minecraft:sign", "Sign");
      hashMap.put("minecraft:mob_spawner", "MobSpawner");
      hashMap.put("minecraft:noteblock", "Music");
      hashMap.put("minecraft:brewing_stand", "Cauldron");
      hashMap.put("minecraft:enhanting_table", "EnchantTable");
      hashMap.put("minecraft:command_block", "CommandBlock");
      hashMap.put("minecraft:beacon", "Beacon");
      hashMap.put("minecraft:skull", "Skull");
      hashMap.put("minecraft:daylight_detector", "DLDetector");
      hashMap.put("minecraft:hopper", "Hopper");
      hashMap.put("minecraft:banner", "Banner");
      hashMap.put("minecraft:flower_pot", "FlowerPot");
      hashMap.put("minecraft:repeating_command_block", "CommandBlock");
      hashMap.put("minecraft:chain_command_block", "CommandBlock");
      hashMap.put("minecraft:standing_sign", "Sign");
      hashMap.put("minecraft:wall_sign", "Sign");
      hashMap.put("minecraft:piston_head", "Piston");
      hashMap.put("minecraft:daylight_detector_inverted", "DLDetector");
      hashMap.put("minecraft:unpowered_comparator", "Comparator");
      hashMap.put("minecraft:powered_comparator", "Comparator");
      hashMap.put("minecraft:wall_banner", "Banner");
      hashMap.put("minecraft:standing_banner", "Banner");
      hashMap.put("minecraft:structure_block", "Structure");
      hashMap.put("minecraft:end_portal", "Airportal");
      hashMap.put("minecraft:end_gateway", "EndGateway");
      hashMap.put("minecraft:shield", "Banner");
   });
   protected static final HookFunction ADD_NAMES = new HookFunction() {
      public Object apply(DynamicOps dynamicOps, Object var2) {
         return V99.addNames(new Dynamic(dynamicOps, var2), V99.ITEM_TO_BLOCKENTITY, "ArmorStand");
      }
   };

   public V99(int var1, Schema schema) {
      super(var1, schema);
   }

   protected static TypeTemplate equipment(Schema schema) {
      return DSL.optionalFields("Equipment", DSL.list(References.ITEM_STACK.in(schema)));
   }

   protected static void registerMob(Schema schema, Map map, String string) {
      schema.register(map, string, () -> {
         return equipment(schema);
      });
   }

   protected static void registerThrowableProjectile(Schema schema, Map map, String string) {
      schema.register(map, string, () -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(schema));
      });
   }

   protected static void registerMinecart(Schema schema, Map map, String string) {
      schema.register(map, string, () -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(schema));
      });
   }

   protected static void registerInventory(Schema schema, Map map, String string) {
      schema.register(map, string, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(schema)));
      });
   }

   public Map registerEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      schema.register(map, "Item", (string) -> {
         return DSL.optionalFields("Item", References.ITEM_STACK.in(schema));
      });
      schema.registerSimple(map, "XPOrb");
      registerThrowableProjectile(schema, map, "ThrownEgg");
      schema.registerSimple(map, "LeashKnot");
      schema.registerSimple(map, "Painting");
      schema.register(map, "Arrow", (string) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(schema));
      });
      schema.register(map, "TippedArrow", (string) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(schema));
      });
      schema.register(map, "SpectralArrow", (string) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(schema));
      });
      registerThrowableProjectile(schema, map, "Snowball");
      registerThrowableProjectile(schema, map, "Fireball");
      registerThrowableProjectile(schema, map, "SmallFireball");
      registerThrowableProjectile(schema, map, "ThrownEnderpearl");
      schema.registerSimple(map, "EyeOfEnderSignal");
      schema.register(map, "ThrownPotion", (string) -> {
         return DSL.optionalFields("inTile", References.BLOCK_NAME.in(schema), "Potion", References.ITEM_STACK.in(schema));
      });
      registerThrowableProjectile(schema, map, "ThrownExpBottle");
      schema.register(map, "ItemFrame", (string) -> {
         return DSL.optionalFields("Item", References.ITEM_STACK.in(schema));
      });
      registerThrowableProjectile(schema, map, "WitherSkull");
      schema.registerSimple(map, "PrimedTnt");
      schema.register(map, "FallingSand", (string) -> {
         return DSL.optionalFields("Block", References.BLOCK_NAME.in(schema), "TileEntityData", References.BLOCK_ENTITY.in(schema));
      });
      schema.register(map, "FireworksRocketEntity", (string) -> {
         return DSL.optionalFields("FireworksItem", References.ITEM_STACK.in(schema));
      });
      schema.registerSimple(map, "Boat");
      schema.register(map, "Minecart", () -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(schema), "Items", DSL.list(References.ITEM_STACK.in(schema)));
      });
      registerMinecart(schema, map, "MinecartRideable");
      schema.register(map, "MinecartChest", (string) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(schema), "Items", DSL.list(References.ITEM_STACK.in(schema)));
      });
      registerMinecart(schema, map, "MinecartFurnace");
      registerMinecart(schema, map, "MinecartTNT");
      schema.register(map, "MinecartSpawner", () -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(schema), References.UNTAGGED_SPAWNER.in(schema));
      });
      schema.register(map, "MinecartHopper", (string) -> {
         return DSL.optionalFields("DisplayTile", References.BLOCK_NAME.in(schema), "Items", DSL.list(References.ITEM_STACK.in(schema)));
      });
      registerMinecart(schema, map, "MinecartCommandBlock");
      registerMob(schema, map, "ArmorStand");
      registerMob(schema, map, "Creeper");
      registerMob(schema, map, "Skeleton");
      registerMob(schema, map, "Spider");
      registerMob(schema, map, "Giant");
      registerMob(schema, map, "Zombie");
      registerMob(schema, map, "Slime");
      registerMob(schema, map, "Ghast");
      registerMob(schema, map, "PigZombie");
      schema.register(map, "Enderman", (string) -> {
         return DSL.optionalFields("carried", References.BLOCK_NAME.in(schema), equipment(schema));
      });
      registerMob(schema, map, "CaveSpider");
      registerMob(schema, map, "Silverfish");
      registerMob(schema, map, "Blaze");
      registerMob(schema, map, "LavaSlime");
      registerMob(schema, map, "EnderDragon");
      registerMob(schema, map, "WitherBoss");
      registerMob(schema, map, "Bat");
      registerMob(schema, map, "Witch");
      registerMob(schema, map, "Endermite");
      registerMob(schema, map, "Guardian");
      registerMob(schema, map, "Pig");
      registerMob(schema, map, "Sheep");
      registerMob(schema, map, "Cow");
      registerMob(schema, map, "Chicken");
      registerMob(schema, map, "Squid");
      registerMob(schema, map, "Wolf");
      registerMob(schema, map, "MushroomCow");
      registerMob(schema, map, "SnowMan");
      registerMob(schema, map, "Ozelot");
      registerMob(schema, map, "VillagerGolem");
      schema.register(map, "EntityHorse", (string) -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(schema)), "ArmorItem", References.ITEM_STACK.in(schema), "SaddleItem", References.ITEM_STACK.in(schema), equipment(schema));
      });
      registerMob(schema, map, "Rabbit");
      schema.register(map, "Villager", (string) -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(schema)), "Offers", DSL.optionalFields("Recipes", DSL.list(DSL.optionalFields("buy", References.ITEM_STACK.in(schema), "buyB", References.ITEM_STACK.in(schema), "sell", References.ITEM_STACK.in(schema)))), equipment(schema));
      });
      schema.registerSimple(map, "EnderCrystal");
      schema.registerSimple(map, "AreaEffectCloud");
      schema.registerSimple(map, "ShulkerBullet");
      registerMob(schema, map, "Shulker");
      return map;
   }

   public Map registerBlockEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();
      registerInventory(schema, map, "Furnace");
      registerInventory(schema, map, "Chest");
      schema.registerSimple(map, "EnderChest");
      schema.register(map, "RecordPlayer", (string) -> {
         return DSL.optionalFields("RecordItem", References.ITEM_STACK.in(schema));
      });
      registerInventory(schema, map, "Trap");
      registerInventory(schema, map, "Dropper");
      schema.registerSimple(map, "Sign");
      schema.register(map, "MobSpawner", (string) -> {
         return References.UNTAGGED_SPAWNER.in(schema);
      });
      schema.registerSimple(map, "Music");
      schema.registerSimple(map, "Piston");
      registerInventory(schema, map, "Cauldron");
      schema.registerSimple(map, "EnchantTable");
      schema.registerSimple(map, "Airportal");
      schema.registerSimple(map, "Control");
      schema.registerSimple(map, "Beacon");
      schema.registerSimple(map, "Skull");
      schema.registerSimple(map, "DLDetector");
      registerInventory(schema, map, "Hopper");
      schema.registerSimple(map, "Comparator");
      schema.register(map, "FlowerPot", (string) -> {
         return DSL.optionalFields("Item", DSL.or(DSL.constType(DSL.intType()), References.ITEM_NAME.in(schema)));
      });
      schema.registerSimple(map, "Banner");
      schema.registerSimple(map, "Structure");
      schema.registerSimple(map, "EndGateway");
      return map;
   }

   public void registerTypes(Schema schema, Map var2, Map var3) {
      schema.registerType(false, References.LEVEL, DSL::remainder);
      schema.registerType(false, References.PLAYER, () -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(schema)), "EnderItems", DSL.list(References.ITEM_STACK.in(schema)));
      });
      schema.registerType(false, References.CHUNK, () -> {
         return DSL.fields("Level", DSL.optionalFields("Entities", DSL.list(References.ENTITY_TREE.in(schema)), "TileEntities", DSL.list(References.BLOCK_ENTITY.in(schema)), "TileTicks", DSL.list(DSL.fields("i", References.BLOCK_NAME.in(schema)))));
      });
      schema.registerType(true, References.BLOCK_ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), var3);
      });
      schema.registerType(true, References.ENTITY_TREE, () -> {
         return DSL.optionalFields("Riding", References.ENTITY_TREE.in(schema), References.ENTITY.in(schema));
      });
      schema.registerType(false, References.ENTITY_NAME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      schema.registerType(true, References.ENTITY, () -> {
         return DSL.taggedChoiceLazy("id", DSL.string(), var2);
      });
      schema.registerType(true, References.ITEM_STACK, () -> {
         return DSL.hook(DSL.optionalFields("id", DSL.or(DSL.constType(DSL.intType()), References.ITEM_NAME.in(schema)), "tag", DSL.optionalFields("EntityTag", References.ENTITY_TREE.in(schema), "BlockEntityTag", References.BLOCK_ENTITY.in(schema), "CanDestroy", DSL.list(References.BLOCK_NAME.in(schema)), "CanPlaceOn", DSL.list(References.BLOCK_NAME.in(schema)))), ADD_NAMES, HookFunction.IDENTITY);
      });
      schema.registerType(false, References.OPTIONS, DSL::remainder);
      schema.registerType(false, References.BLOCK_NAME, () -> {
         return DSL.or(DSL.constType(DSL.intType()), DSL.constType(DSL.namespacedString()));
      });
      schema.registerType(false, References.ITEM_NAME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      schema.registerType(false, References.STATS, DSL::remainder);
      schema.registerType(false, References.SAVED_DATA, () -> {
         return DSL.optionalFields("data", DSL.optionalFields("Features", DSL.compoundList(References.STRUCTURE_FEATURE.in(schema)), "Objectives", DSL.list(References.OBJECTIVE.in(schema)), "Teams", DSL.list(References.TEAM.in(schema))));
      });
      schema.registerType(false, References.STRUCTURE_FEATURE, DSL::remainder);
      schema.registerType(false, References.OBJECTIVE, DSL::remainder);
      schema.registerType(false, References.TEAM, DSL::remainder);
      schema.registerType(true, References.UNTAGGED_SPAWNER, DSL::remainder);
      schema.registerType(false, References.POI_CHUNK, DSL::remainder);
   }

   protected static Object addNames(Dynamic dynamic, Map map, String string) {
      return dynamic.update("tag", (var3) -> {
         return var3.update("BlockEntityTag", (var2) -> {
            String var3 = dynamic.get("id").asString("");
            String var4 = (String)map.get(NamespacedSchema.ensureNamespaced(var3));
            if(var4 == null) {
               LOGGER.warn("Unable to resolve BlockEntity for ItemStack: {}", var3);
               return var2;
            } else {
               return var2.set("id", dynamic.createString(var4));
            }
         }).update("EntityTag", (var2) -> {
            String string = dynamic.get("id").asString("");
            return Objects.equals(NamespacedSchema.ensureNamespaced(string), "minecraft:armor_stand")?var2.set("id", dynamic.createString(string)):var2;
         });
      }).getValue();
   }
}
