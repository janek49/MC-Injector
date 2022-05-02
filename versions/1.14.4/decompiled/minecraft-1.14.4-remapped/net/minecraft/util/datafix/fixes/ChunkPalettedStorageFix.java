package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.BitStorage;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.References;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPalettedStorageFix extends DataFix {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final BitSet VIRTUAL = new BitSet(256);
   private static final BitSet FIX = new BitSet(256);
   private static final Dynamic PUMPKIN = BlockStateData.parse("{Name:\'minecraft:pumpkin\'}");
   private static final Dynamic SNOWY_PODZOL = BlockStateData.parse("{Name:\'minecraft:podzol\',Properties:{snowy:\'true\'}}");
   private static final Dynamic SNOWY_GRASS = BlockStateData.parse("{Name:\'minecraft:grass_block\',Properties:{snowy:\'true\'}}");
   private static final Dynamic SNOWY_MYCELIUM = BlockStateData.parse("{Name:\'minecraft:mycelium\',Properties:{snowy:\'true\'}}");
   private static final Dynamic UPPER_SUNFLOWER = BlockStateData.parse("{Name:\'minecraft:sunflower\',Properties:{half:\'upper\'}}");
   private static final Dynamic UPPER_LILAC = BlockStateData.parse("{Name:\'minecraft:lilac\',Properties:{half:\'upper\'}}");
   private static final Dynamic UPPER_TALL_GRASS = BlockStateData.parse("{Name:\'minecraft:tall_grass\',Properties:{half:\'upper\'}}");
   private static final Dynamic UPPER_LARGE_FERN = BlockStateData.parse("{Name:\'minecraft:large_fern\',Properties:{half:\'upper\'}}");
   private static final Dynamic UPPER_ROSE_BUSH = BlockStateData.parse("{Name:\'minecraft:rose_bush\',Properties:{half:\'upper\'}}");
   private static final Dynamic UPPER_PEONY = BlockStateData.parse("{Name:\'minecraft:peony\',Properties:{half:\'upper\'}}");
   private static final Map FLOWER_POT_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (hashMap) -> {
      hashMap.put("minecraft:air0", BlockStateData.parse("{Name:\'minecraft:flower_pot\'}"));
      hashMap.put("minecraft:red_flower0", BlockStateData.parse("{Name:\'minecraft:potted_poppy\'}"));
      hashMap.put("minecraft:red_flower1", BlockStateData.parse("{Name:\'minecraft:potted_blue_orchid\'}"));
      hashMap.put("minecraft:red_flower2", BlockStateData.parse("{Name:\'minecraft:potted_allium\'}"));
      hashMap.put("minecraft:red_flower3", BlockStateData.parse("{Name:\'minecraft:potted_azure_bluet\'}"));
      hashMap.put("minecraft:red_flower4", BlockStateData.parse("{Name:\'minecraft:potted_red_tulip\'}"));
      hashMap.put("minecraft:red_flower5", BlockStateData.parse("{Name:\'minecraft:potted_orange_tulip\'}"));
      hashMap.put("minecraft:red_flower6", BlockStateData.parse("{Name:\'minecraft:potted_white_tulip\'}"));
      hashMap.put("minecraft:red_flower7", BlockStateData.parse("{Name:\'minecraft:potted_pink_tulip\'}"));
      hashMap.put("minecraft:red_flower8", BlockStateData.parse("{Name:\'minecraft:potted_oxeye_daisy\'}"));
      hashMap.put("minecraft:yellow_flower0", BlockStateData.parse("{Name:\'minecraft:potted_dandelion\'}"));
      hashMap.put("minecraft:sapling0", BlockStateData.parse("{Name:\'minecraft:potted_oak_sapling\'}"));
      hashMap.put("minecraft:sapling1", BlockStateData.parse("{Name:\'minecraft:potted_spruce_sapling\'}"));
      hashMap.put("minecraft:sapling2", BlockStateData.parse("{Name:\'minecraft:potted_birch_sapling\'}"));
      hashMap.put("minecraft:sapling3", BlockStateData.parse("{Name:\'minecraft:potted_jungle_sapling\'}"));
      hashMap.put("minecraft:sapling4", BlockStateData.parse("{Name:\'minecraft:potted_acacia_sapling\'}"));
      hashMap.put("minecraft:sapling5", BlockStateData.parse("{Name:\'minecraft:potted_dark_oak_sapling\'}"));
      hashMap.put("minecraft:red_mushroom0", BlockStateData.parse("{Name:\'minecraft:potted_red_mushroom\'}"));
      hashMap.put("minecraft:brown_mushroom0", BlockStateData.parse("{Name:\'minecraft:potted_brown_mushroom\'}"));
      hashMap.put("minecraft:deadbush0", BlockStateData.parse("{Name:\'minecraft:potted_dead_bush\'}"));
      hashMap.put("minecraft:tallgrass2", BlockStateData.parse("{Name:\'minecraft:potted_fern\'}"));
      hashMap.put("minecraft:cactus0", BlockStateData.getTag(2240));
   });
   private static final Map SKULL_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (hashMap) -> {
      mapSkull(hashMap, 0, "skeleton", "skull");
      mapSkull(hashMap, 1, "wither_skeleton", "skull");
      mapSkull(hashMap, 2, "zombie", "head");
      mapSkull(hashMap, 3, "player", "head");
      mapSkull(hashMap, 4, "creeper", "head");
      mapSkull(hashMap, 5, "dragon", "head");
   });
   private static final Map DOOR_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (hashMap) -> {
      mapDoor(hashMap, "oak_door", 1024);
      mapDoor(hashMap, "iron_door", 1136);
      mapDoor(hashMap, "spruce_door", 3088);
      mapDoor(hashMap, "birch_door", 3104);
      mapDoor(hashMap, "jungle_door", 3120);
      mapDoor(hashMap, "acacia_door", 3136);
      mapDoor(hashMap, "dark_oak_door", 3152);
   });
   private static final Map NOTE_BLOCK_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (hashMap) -> {
      for(int var1 = 0; var1 < 26; ++var1) {
         hashMap.put("true" + var1, BlockStateData.parse("{Name:\'minecraft:note_block\',Properties:{powered:\'true\',note:\'" + var1 + "\'}}"));
         hashMap.put("false" + var1, BlockStateData.parse("{Name:\'minecraft:note_block\',Properties:{powered:\'false\',note:\'" + var1 + "\'}}"));
      }

   });
   private static final Int2ObjectMap DYE_COLOR_MAP = (Int2ObjectMap)DataFixUtils.make(new Int2ObjectOpenHashMap(), (int2ObjectOpenHashMap) -> {
      int2ObjectOpenHashMap.put(0, "white");
      int2ObjectOpenHashMap.put(1, "orange");
      int2ObjectOpenHashMap.put(2, "magenta");
      int2ObjectOpenHashMap.put(3, "light_blue");
      int2ObjectOpenHashMap.put(4, "yellow");
      int2ObjectOpenHashMap.put(5, "lime");
      int2ObjectOpenHashMap.put(6, "pink");
      int2ObjectOpenHashMap.put(7, "gray");
      int2ObjectOpenHashMap.put(8, "light_gray");
      int2ObjectOpenHashMap.put(9, "cyan");
      int2ObjectOpenHashMap.put(10, "purple");
      int2ObjectOpenHashMap.put(11, "blue");
      int2ObjectOpenHashMap.put(12, "brown");
      int2ObjectOpenHashMap.put(13, "green");
      int2ObjectOpenHashMap.put(14, "red");
      int2ObjectOpenHashMap.put(15, "black");
   });
   private static final Map BED_BLOCK_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (hashMap) -> {
      ObjectIterator var1 = DYE_COLOR_MAP.int2ObjectEntrySet().iterator();

      while(var1.hasNext()) {
         Entry<String> var2 = (Entry)var1.next();
         if(!Objects.equals(var2.getValue(), "red")) {
            addBeds(hashMap, var2.getIntKey(), (String)var2.getValue());
         }
      }

   });
   private static final Map BANNER_BLOCK_MAP = (Map)DataFixUtils.make(Maps.newHashMap(), (hashMap) -> {
      ObjectIterator var1 = DYE_COLOR_MAP.int2ObjectEntrySet().iterator();

      while(var1.hasNext()) {
         Entry<String> var2 = (Entry)var1.next();
         if(!Objects.equals(var2.getValue(), "white")) {
            addBanners(hashMap, 15 - var2.getIntKey(), (String)var2.getValue());
         }
      }

   });
   private static final Dynamic AIR = BlockStateData.getTag(0);

   public ChunkPalettedStorageFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   private static void mapSkull(Map map, int var1, String var2, String var3) {
      map.put(var1 + "north", BlockStateData.parse("{Name:\'minecraft:" + var2 + "_wall_" + var3 + "\',Properties:{facing:\'north\'}}"));
      map.put(var1 + "east", BlockStateData.parse("{Name:\'minecraft:" + var2 + "_wall_" + var3 + "\',Properties:{facing:\'east\'}}"));
      map.put(var1 + "south", BlockStateData.parse("{Name:\'minecraft:" + var2 + "_wall_" + var3 + "\',Properties:{facing:\'south\'}}"));
      map.put(var1 + "west", BlockStateData.parse("{Name:\'minecraft:" + var2 + "_wall_" + var3 + "\',Properties:{facing:\'west\'}}"));

      for(int var4 = 0; var4 < 16; ++var4) {
         map.put(var1 + "" + var4, BlockStateData.parse("{Name:\'minecraft:" + var2 + "_" + var3 + "\',Properties:{rotation:\'" + var4 + "\'}}"));
      }

   }

   private static void mapDoor(Map map, String string, int var2) {
      map.put("minecraft:" + string + "eastlowerleftfalsefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'east\',half:\'lower\',hinge:\'left\',open:\'false\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "eastlowerleftfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'east\',half:\'lower\',hinge:\'left\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "eastlowerlefttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'east\',half:\'lower\',hinge:\'left\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "eastlowerlefttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'east\',half:\'lower\',hinge:\'left\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "eastlowerrightfalsefalse", BlockStateData.getTag(var2));
      map.put("minecraft:" + string + "eastlowerrightfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'east\',half:\'lower\',hinge:\'right\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "eastlowerrighttruefalse", BlockStateData.getTag(var2 + 4));
      map.put("minecraft:" + string + "eastlowerrighttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'east\',half:\'lower\',hinge:\'right\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "eastupperleftfalsefalse", BlockStateData.getTag(var2 + 8));
      map.put("minecraft:" + string + "eastupperleftfalsetrue", BlockStateData.getTag(var2 + 10));
      map.put("minecraft:" + string + "eastupperlefttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'east\',half:\'upper\',hinge:\'left\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "eastupperlefttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'east\',half:\'upper\',hinge:\'left\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "eastupperrightfalsefalse", BlockStateData.getTag(var2 + 9));
      map.put("minecraft:" + string + "eastupperrightfalsetrue", BlockStateData.getTag(var2 + 11));
      map.put("minecraft:" + string + "eastupperrighttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'east\',half:\'upper\',hinge:\'right\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "eastupperrighttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'east\',half:\'upper\',hinge:\'right\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "northlowerleftfalsefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'lower\',hinge:\'left\',open:\'false\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "northlowerleftfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'lower\',hinge:\'left\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "northlowerlefttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'lower\',hinge:\'left\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "northlowerlefttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'lower\',hinge:\'left\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "northlowerrightfalsefalse", BlockStateData.getTag(var2 + 3));
      map.put("minecraft:" + string + "northlowerrightfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'lower\',hinge:\'right\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "northlowerrighttruefalse", BlockStateData.getTag(var2 + 7));
      map.put("minecraft:" + string + "northlowerrighttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'lower\',hinge:\'right\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "northupperleftfalsefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'upper\',hinge:\'left\',open:\'false\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "northupperleftfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'upper\',hinge:\'left\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "northupperlefttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'upper\',hinge:\'left\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "northupperlefttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'upper\',hinge:\'left\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "northupperrightfalsefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'upper\',hinge:\'right\',open:\'false\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "northupperrightfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'upper\',hinge:\'right\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "northupperrighttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'upper\',hinge:\'right\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "northupperrighttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'north\',half:\'upper\',hinge:\'right\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "southlowerleftfalsefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'lower\',hinge:\'left\',open:\'false\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "southlowerleftfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'lower\',hinge:\'left\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "southlowerlefttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'lower\',hinge:\'left\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "southlowerlefttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'lower\',hinge:\'left\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "southlowerrightfalsefalse", BlockStateData.getTag(var2 + 1));
      map.put("minecraft:" + string + "southlowerrightfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'lower\',hinge:\'right\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "southlowerrighttruefalse", BlockStateData.getTag(var2 + 5));
      map.put("minecraft:" + string + "southlowerrighttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'lower\',hinge:\'right\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "southupperleftfalsefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'upper\',hinge:\'left\',open:\'false\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "southupperleftfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'upper\',hinge:\'left\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "southupperlefttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'upper\',hinge:\'left\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "southupperlefttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'upper\',hinge:\'left\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "southupperrightfalsefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'upper\',hinge:\'right\',open:\'false\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "southupperrightfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'upper\',hinge:\'right\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "southupperrighttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'upper\',hinge:\'right\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "southupperrighttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'south\',half:\'upper\',hinge:\'right\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "westlowerleftfalsefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'lower\',hinge:\'left\',open:\'false\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "westlowerleftfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'lower\',hinge:\'left\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "westlowerlefttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'lower\',hinge:\'left\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "westlowerlefttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'lower\',hinge:\'left\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "westlowerrightfalsefalse", BlockStateData.getTag(var2 + 2));
      map.put("minecraft:" + string + "westlowerrightfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'lower\',hinge:\'right\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "westlowerrighttruefalse", BlockStateData.getTag(var2 + 6));
      map.put("minecraft:" + string + "westlowerrighttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'lower\',hinge:\'right\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "westupperleftfalsefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'upper\',hinge:\'left\',open:\'false\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "westupperleftfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'upper\',hinge:\'left\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "westupperlefttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'upper\',hinge:\'left\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "westupperlefttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'upper\',hinge:\'left\',open:\'true\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "westupperrightfalsefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'upper\',hinge:\'right\',open:\'false\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "westupperrightfalsetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'upper\',hinge:\'right\',open:\'false\',powered:\'true\'}}"));
      map.put("minecraft:" + string + "westupperrighttruefalse", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'upper\',hinge:\'right\',open:\'true\',powered:\'false\'}}"));
      map.put("minecraft:" + string + "westupperrighttruetrue", BlockStateData.parse("{Name:\'minecraft:" + string + "\',Properties:{facing:\'west\',half:\'upper\',hinge:\'right\',open:\'true\',powered:\'true\'}}"));
   }

   private static void addBeds(Map map, int var1, String string) {
      map.put("southfalsefoot" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'south\',occupied:\'false\',part:\'foot\'}}"));
      map.put("westfalsefoot" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'west\',occupied:\'false\',part:\'foot\'}}"));
      map.put("northfalsefoot" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'north\',occupied:\'false\',part:\'foot\'}}"));
      map.put("eastfalsefoot" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'east\',occupied:\'false\',part:\'foot\'}}"));
      map.put("southfalsehead" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'south\',occupied:\'false\',part:\'head\'}}"));
      map.put("westfalsehead" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'west\',occupied:\'false\',part:\'head\'}}"));
      map.put("northfalsehead" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'north\',occupied:\'false\',part:\'head\'}}"));
      map.put("eastfalsehead" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'east\',occupied:\'false\',part:\'head\'}}"));
      map.put("southtruehead" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'south\',occupied:\'true\',part:\'head\'}}"));
      map.put("westtruehead" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'west\',occupied:\'true\',part:\'head\'}}"));
      map.put("northtruehead" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'north\',occupied:\'true\',part:\'head\'}}"));
      map.put("easttruehead" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_bed\',Properties:{facing:\'east\',occupied:\'true\',part:\'head\'}}"));
   }

   private static void addBanners(Map map, int var1, String string) {
      for(int var3 = 0; var3 < 16; ++var3) {
         map.put("" + var3 + "_" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_banner\',Properties:{rotation:\'" + var3 + "\'}}"));
      }

      map.put("north_" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_wall_banner\',Properties:{facing:\'north\'}}"));
      map.put("south_" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_wall_banner\',Properties:{facing:\'south\'}}"));
      map.put("west_" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_wall_banner\',Properties:{facing:\'west\'}}"));
      map.put("east_" + var1, BlockStateData.parse("{Name:\'minecraft:" + string + "_wall_banner\',Properties:{facing:\'east\'}}"));
   }

   public static String getName(Dynamic dynamic) {
      return dynamic.get("Name").asString("");
   }

   public static String getProperty(Dynamic dynamic, String var1) {
      return dynamic.get("Properties").get(var1).asString("");
   }

   public static int idFor(CrudeIncrementalIntIdentityHashBiMap crudeIncrementalIntIdentityHashBiMap, Dynamic dynamic) {
      int var2 = crudeIncrementalIntIdentityHashBiMap.getId(dynamic);
      if(var2 == -1) {
         var2 = crudeIncrementalIntIdentityHashBiMap.add(dynamic);
      }

      return var2;
   }

   private Dynamic fix(Dynamic dynamic) {
      Optional<? extends Dynamic<?>> var2 = dynamic.get("Level").get();
      return var2.isPresent() && ((Dynamic)var2.get()).get("Sections").asStreamOpt().isPresent()?dynamic.set("Level", (new ChunkPalettedStorageFix.UpgradeChunk((Dynamic)var2.get())).write()):dynamic;
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.CHUNK);
      Type<?> var2 = this.getOutputSchema().getType(References.CHUNK);
      return this.writeFixAndRead("ChunkPalettedStorageFix", var1, var2, this::fix);
   }

   public static int getSideMask(boolean var0, boolean var1, boolean var2, boolean var3) {
      int var4 = 0;
      if(var2) {
         if(var1) {
            var4 |= 2;
         } else if(var0) {
            var4 |= 128;
         } else {
            var4 |= 1;
         }
      } else if(var3) {
         if(var0) {
            var4 |= 32;
         } else if(var1) {
            var4 |= 8;
         } else {
            var4 |= 16;
         }
      } else if(var1) {
         var4 |= 4;
      } else if(var0) {
         var4 |= 64;
      }

      return var4;
   }

   static {
      FIX.set(2);
      FIX.set(3);
      FIX.set(110);
      FIX.set(140);
      FIX.set(144);
      FIX.set(25);
      FIX.set(86);
      FIX.set(26);
      FIX.set(176);
      FIX.set(177);
      FIX.set(175);
      FIX.set(64);
      FIX.set(71);
      FIX.set(193);
      FIX.set(194);
      FIX.set(195);
      FIX.set(196);
      FIX.set(197);
      VIRTUAL.set(54);
      VIRTUAL.set(146);
      VIRTUAL.set(25);
      VIRTUAL.set(26);
      VIRTUAL.set(51);
      VIRTUAL.set(53);
      VIRTUAL.set(67);
      VIRTUAL.set(108);
      VIRTUAL.set(109);
      VIRTUAL.set(114);
      VIRTUAL.set(128);
      VIRTUAL.set(134);
      VIRTUAL.set(135);
      VIRTUAL.set(136);
      VIRTUAL.set(156);
      VIRTUAL.set(163);
      VIRTUAL.set(164);
      VIRTUAL.set(180);
      VIRTUAL.set(203);
      VIRTUAL.set(55);
      VIRTUAL.set(85);
      VIRTUAL.set(113);
      VIRTUAL.set(188);
      VIRTUAL.set(189);
      VIRTUAL.set(190);
      VIRTUAL.set(191);
      VIRTUAL.set(192);
      VIRTUAL.set(93);
      VIRTUAL.set(94);
      VIRTUAL.set(101);
      VIRTUAL.set(102);
      VIRTUAL.set(160);
      VIRTUAL.set(106);
      VIRTUAL.set(107);
      VIRTUAL.set(183);
      VIRTUAL.set(184);
      VIRTUAL.set(185);
      VIRTUAL.set(186);
      VIRTUAL.set(187);
      VIRTUAL.set(132);
      VIRTUAL.set(139);
      VIRTUAL.set(199);
   }

   static class DataLayer {
      private final byte[] data;

      public DataLayer() {
         this.data = new byte[2048];
      }

      public DataLayer(byte[] data) {
         this.data = data;
         if(data.length != 2048) {
            throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + data.length);
         }
      }

      public int get(int var1, int var2, int var3) {
         int var4 = this.getPosition(var2 << 8 | var3 << 4 | var1);
         return this.isFirst(var2 << 8 | var3 << 4 | var1)?this.data[var4] & 15:this.data[var4] >> 4 & 15;
      }

      private boolean isFirst(int i) {
         return (i & 1) == 0;
      }

      private int getPosition(int i) {
         return i >> 1;
      }
   }

   public static enum Direction {
      DOWN(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.Y),
      UP(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.Y),
      NORTH(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.Z),
      SOUTH(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.Z),
      WEST(ChunkPalettedStorageFix.Direction.AxisDirection.NEGATIVE, ChunkPalettedStorageFix.Direction.Axis.X),
      EAST(ChunkPalettedStorageFix.Direction.AxisDirection.POSITIVE, ChunkPalettedStorageFix.Direction.Axis.X);

      private final ChunkPalettedStorageFix.Direction.Axis axis;
      private final ChunkPalettedStorageFix.Direction.AxisDirection axisDirection;

      private Direction(ChunkPalettedStorageFix.Direction.AxisDirection axisDirection, ChunkPalettedStorageFix.Direction.Axis axis) {
         this.axis = axis;
         this.axisDirection = axisDirection;
      }

      public ChunkPalettedStorageFix.Direction.AxisDirection getAxisDirection() {
         return this.axisDirection;
      }

      public ChunkPalettedStorageFix.Direction.Axis getAxis() {
         return this.axis;
      }

      public static enum Axis {
         X,
         Y,
         Z;
      }

      public static enum AxisDirection {
         POSITIVE(1),
         NEGATIVE(-1);

         private final int step;

         private AxisDirection(int step) {
            this.step = step;
         }

         public int getStep() {
            return this.step;
         }
      }
   }

   static class Section {
      private final CrudeIncrementalIntIdentityHashBiMap palette = new CrudeIncrementalIntIdentityHashBiMap(32);
      private Dynamic listTag;
      private final Dynamic section;
      private final boolean hasData;
      private final Int2ObjectMap toFix = new Int2ObjectLinkedOpenHashMap();
      private final IntList update = new IntArrayList();
      public final int y;
      private final Set seen = Sets.newIdentityHashSet();
      private final int[] buffer = new int[4096];

      public Section(Dynamic section) {
         this.listTag = section.emptyList();
         this.section = section;
         this.y = section.get("Y").asInt(0);
         this.hasData = section.get("Blocks").get().isPresent();
      }

      public Dynamic getBlock(int i) {
         if(i >= 0 && i <= 4095) {
            Dynamic<?> dynamic = (Dynamic)this.palette.byId(this.buffer[i]);
            return dynamic == null?ChunkPalettedStorageFix.AIR:dynamic;
         } else {
            return ChunkPalettedStorageFix.AIR;
         }
      }

      public void setBlock(int var1, Dynamic dynamic) {
         if(this.seen.add(dynamic)) {
            this.listTag = this.listTag.merge("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic))?ChunkPalettedStorageFix.AIR:dynamic);
         }

         this.buffer[var1] = ChunkPalettedStorageFix.idFor(this.palette, dynamic);
      }

      public int upgrade(int i) {
         if(!this.hasData) {
            return i;
         } else {
            ByteBuffer var2 = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().get();
            ChunkPalettedStorageFix.DataLayer var3 = (ChunkPalettedStorageFix.DataLayer)this.section.get("Data").asByteBufferOpt().map((byteBuffer) -> {
               return new ChunkPalettedStorageFix.DataLayer(DataFixUtils.toArray(byteBuffer));
            }).orElseGet(ChunkPalettedStorageFix.DataLayer::<init>);
            ChunkPalettedStorageFix.DataLayer var4 = (ChunkPalettedStorageFix.DataLayer)this.section.get("Add").asByteBufferOpt().map((byteBuffer) -> {
               return new ChunkPalettedStorageFix.DataLayer(DataFixUtils.toArray(byteBuffer));
            }).orElseGet(ChunkPalettedStorageFix.DataLayer::<init>);
            this.seen.add(ChunkPalettedStorageFix.AIR);
            ChunkPalettedStorageFix.idFor(this.palette, ChunkPalettedStorageFix.AIR);
            this.listTag = this.listTag.merge(ChunkPalettedStorageFix.AIR);

            for(int var5 = 0; var5 < 4096; ++var5) {
               int var6 = var5 & 15;
               int var7 = var5 >> 8 & 15;
               int var8 = var5 >> 4 & 15;
               int var9 = var4.get(var6, var7, var8) << 12 | (var2.get(var5) & 255) << 4 | var3.get(var6, var7, var8);
               if(ChunkPalettedStorageFix.FIX.get(var9 >> 4)) {
                  this.addFix(var9 >> 4, var5);
               }

               if(ChunkPalettedStorageFix.VIRTUAL.get(var9 >> 4)) {
                  int var10 = ChunkPalettedStorageFix.getSideMask(var6 == 0, var6 == 15, var8 == 0, var8 == 15);
                  if(var10 == 0) {
                     this.update.add(var5);
                  } else {
                     i |= var10;
                  }
               }

               this.setBlock(var5, BlockStateData.getTag(var9));
            }

            return i;
         }
      }

      private void addFix(int var1, int var2) {
         IntList var3 = (IntList)this.toFix.get(var1);
         if(var3 == null) {
            var3 = new IntArrayList();
            this.toFix.put(var1, var3);
         }

         var3.add(var2);
      }

      public Dynamic write() {
         Dynamic<?> dynamic = this.section;
         if(!this.hasData) {
            return dynamic;
         } else {
            dynamic = dynamic.set("Palette", this.listTag);
            int var2 = Math.max(4, DataFixUtils.ceillog2(this.seen.size()));
            BitStorage var3 = new BitStorage(var2, 4096);

            for(int var4 = 0; var4 < this.buffer.length; ++var4) {
               var3.set(var4, this.buffer[var4]);
            }

            dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(var3.getRaw())));
            dynamic = dynamic.remove("Blocks");
            dynamic = dynamic.remove("Data");
            dynamic = dynamic.remove("Add");
            return dynamic;
         }
      }
   }

   static final class UpgradeChunk {
      private int sides;
      private final ChunkPalettedStorageFix.Section[] sections = new ChunkPalettedStorageFix.Section[16];
      private final Dynamic level;
      private final int x;
      private final int z;
      private final Int2ObjectMap blockEntities = new Int2ObjectLinkedOpenHashMap(16);

      public UpgradeChunk(Dynamic level) {
         this.level = level;
         this.x = level.get("xPos").asInt(0) << 4;
         this.z = level.get("zPos").asInt(0) << 4;
         level.get("TileEntities").asStreamOpt().ifPresent((stream) -> {
            stream.forEach((dynamic) -> {
               int var2 = dynamic.get("x").asInt(0) - this.x & 15;
               int var3 = dynamic.get("y").asInt(0);
               int var4 = dynamic.get("z").asInt(0) - this.z & 15;
               int var5 = var3 << 8 | var4 << 4 | var2;
               if(this.blockEntities.put(var5, dynamic) != null) {
                  ChunkPalettedStorageFix.LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", Integer.valueOf(this.x), Integer.valueOf(this.z), Integer.valueOf(var2), Integer.valueOf(var3), Integer.valueOf(var4));
               }

            });
         });
         boolean var2 = level.get("convertedFromAlphaFormat").asBoolean(false);
         level.get("Sections").asStreamOpt().ifPresent((stream) -> {
            stream.forEach((dynamic) -> {
               ChunkPalettedStorageFix.Section var2 = new ChunkPalettedStorageFix.Section(dynamic);
               this.sides = var2.upgrade(this.sides);
               this.sections[var2.y] = var2;
            });
         });

         for(ChunkPalettedStorageFix.Section var6 : this.sections) {
            if(var6 != null) {
               ObjectIterator var7 = var6.toFix.entrySet().iterator();

               label170:
               while(var7.hasNext()) {
                  java.util.Map.Entry<Integer, IntList> var8 = (java.util.Map.Entry)var7.next();
                  int var9 = var6.y << 12;
                  switch(((Integer)var8.getKey()).intValue()) {
                  case 2:
                     IntListIterator var30 = ((IntList)var8.getValue()).iterator();

                     while(true) {
                        if(!var30.hasNext()) {
                           continue label170;
                        }

                        int var11 = ((Integer)var30.next()).intValue();
                        var11 = var11 | var9;
                        Dynamic<?> var12 = this.getBlock(var11);
                        if("minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(var12))) {
                           String var13 = ChunkPalettedStorageFix.getName(this.getBlock(relative(var11, ChunkPalettedStorageFix.Direction.UP)));
                           if("minecraft:snow".equals(var13) || "minecraft:snow_layer".equals(var13)) {
                              this.setBlock(var11, ChunkPalettedStorageFix.SNOWY_GRASS);
                           }
                        }
                     }
                  case 3:
                     IntListIterator var29 = ((IntList)var8.getValue()).iterator();

                     while(true) {
                        if(!var29.hasNext()) {
                           continue label170;
                        }

                        int var11 = ((Integer)var29.next()).intValue();
                        var11 = var11 | var9;
                        Dynamic<?> var12 = this.getBlock(var11);
                        if("minecraft:podzol".equals(ChunkPalettedStorageFix.getName(var12))) {
                           String var13 = ChunkPalettedStorageFix.getName(this.getBlock(relative(var11, ChunkPalettedStorageFix.Direction.UP)));
                           if("minecraft:snow".equals(var13) || "minecraft:snow_layer".equals(var13)) {
                              this.setBlock(var11, ChunkPalettedStorageFix.SNOWY_PODZOL);
                           }
                        }
                     }
                  case 25:
                     IntListIterator var28 = ((IntList)var8.getValue()).iterator();

                     while(true) {
                        if(!var28.hasNext()) {
                           continue label170;
                        }

                        int var11 = ((Integer)var28.next()).intValue();
                        var11 = var11 | var9;
                        Dynamic<?> var12 = this.removeBlockEntity(var11);
                        if(var12 != null) {
                           String var13 = Boolean.toString(var12.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(var12.get("note").asInt(0), 0), 24);
                           this.setBlock(var11, (Dynamic)ChunkPalettedStorageFix.NOTE_BLOCK_MAP.getOrDefault(var13, ChunkPalettedStorageFix.NOTE_BLOCK_MAP.get("false0")));
                        }
                     }
                  case 26:
                     IntListIterator var27 = ((IntList)var8.getValue()).iterator();

                     while(true) {
                        if(!var27.hasNext()) {
                           continue label170;
                        }

                        int var11 = ((Integer)var27.next()).intValue();
                        var11 = var11 | var9;
                        Dynamic<?> var12 = this.getBlockEntity(var11);
                        Dynamic<?> var13 = this.getBlock(var11);
                        if(var12 != null) {
                           int var14 = var12.get("color").asInt(0);
                           if(var14 != 14 && var14 >= 0 && var14 < 16) {
                              String var15 = ChunkPalettedStorageFix.getProperty(var13, "facing") + ChunkPalettedStorageFix.getProperty(var13, "occupied") + ChunkPalettedStorageFix.getProperty(var13, "part") + var14;
                              if(ChunkPalettedStorageFix.BED_BLOCK_MAP.containsKey(var15)) {
                                 this.setBlock(var11, (Dynamic)ChunkPalettedStorageFix.BED_BLOCK_MAP.get(var15));
                              }
                           }
                        }
                     }
                  case 64:
                  case 71:
                  case 193:
                  case 194:
                  case 195:
                  case 196:
                  case 197:
                     IntListIterator var26 = ((IntList)var8.getValue()).iterator();

                     while(true) {
                        if(!var26.hasNext()) {
                           continue label170;
                        }

                        int var11 = ((Integer)var26.next()).intValue();
                        var11 = var11 | var9;
                        Dynamic<?> var12 = this.getBlock(var11);
                        if(ChunkPalettedStorageFix.getName(var12).endsWith("_door")) {
                           Dynamic<?> var13 = this.getBlock(var11);
                           if("lower".equals(ChunkPalettedStorageFix.getProperty(var13, "half"))) {
                              int var14 = relative(var11, ChunkPalettedStorageFix.Direction.UP);
                              Dynamic<?> var15 = this.getBlock(var14);
                              String var16 = ChunkPalettedStorageFix.getName(var13);
                              if(var16.equals(ChunkPalettedStorageFix.getName(var15))) {
                                 String var17 = ChunkPalettedStorageFix.getProperty(var13, "facing");
                                 String var18 = ChunkPalettedStorageFix.getProperty(var13, "open");
                                 String var19 = var2?"left":ChunkPalettedStorageFix.getProperty(var15, "hinge");
                                 String var20 = var2?"false":ChunkPalettedStorageFix.getProperty(var15, "powered");
                                 this.setBlock(var11, (Dynamic)ChunkPalettedStorageFix.DOOR_MAP.get(var16 + var17 + "lower" + var19 + var18 + var20));
                                 this.setBlock(var14, (Dynamic)ChunkPalettedStorageFix.DOOR_MAP.get(var16 + var17 + "upper" + var19 + var18 + var20));
                              }
                           }
                        }
                     }
                  case 86:
                     IntListIterator var25 = ((IntList)var8.getValue()).iterator();

                     while(true) {
                        if(!var25.hasNext()) {
                           continue label170;
                        }

                        int var11 = ((Integer)var25.next()).intValue();
                        var11 = var11 | var9;
                        Dynamic<?> var12 = this.getBlock(var11);
                        if("minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(var12))) {
                           String var13 = ChunkPalettedStorageFix.getName(this.getBlock(relative(var11, ChunkPalettedStorageFix.Direction.DOWN)));
                           if("minecraft:grass_block".equals(var13) || "minecraft:dirt".equals(var13)) {
                              this.setBlock(var11, ChunkPalettedStorageFix.PUMPKIN);
                           }
                        }
                     }
                  case 110:
                     IntListIterator var24 = ((IntList)var8.getValue()).iterator();

                     while(true) {
                        if(!var24.hasNext()) {
                           continue label170;
                        }

                        int var11 = ((Integer)var24.next()).intValue();
                        var11 = var11 | var9;
                        Dynamic<?> var12 = this.getBlock(var11);
                        if("minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(var12))) {
                           String var13 = ChunkPalettedStorageFix.getName(this.getBlock(relative(var11, ChunkPalettedStorageFix.Direction.UP)));
                           if("minecraft:snow".equals(var13) || "minecraft:snow_layer".equals(var13)) {
                              this.setBlock(var11, ChunkPalettedStorageFix.SNOWY_MYCELIUM);
                           }
                        }
                     }
                  case 140:
                     IntListIterator var23 = ((IntList)var8.getValue()).iterator();

                     while(true) {
                        if(!var23.hasNext()) {
                           continue label170;
                        }

                        int var11 = ((Integer)var23.next()).intValue();
                        var11 = var11 | var9;
                        Dynamic<?> var12 = this.removeBlockEntity(var11);
                        if(var12 != null) {
                           String var13 = var12.get("Item").asString("") + var12.get("Data").asInt(0);
                           this.setBlock(var11, (Dynamic)ChunkPalettedStorageFix.FLOWER_POT_MAP.getOrDefault(var13, ChunkPalettedStorageFix.FLOWER_POT_MAP.get("minecraft:air0")));
                        }
                     }
                  case 144:
                     IntListIterator var22 = ((IntList)var8.getValue()).iterator();

                     while(true) {
                        if(!var22.hasNext()) {
                           continue label170;
                        }

                        int var11 = ((Integer)var22.next()).intValue();
                        var11 = var11 | var9;
                        Dynamic<?> var12 = this.getBlockEntity(var11);
                        if(var12 != null) {
                           String var13 = String.valueOf(var12.get("SkullType").asInt(0));
                           String var14 = ChunkPalettedStorageFix.getProperty(this.getBlock(var11), "facing");
                           String var15;
                           if(!"up".equals(var14) && !"down".equals(var14)) {
                              var15 = var13 + var14;
                           } else {
                              var15 = var13 + String.valueOf(var12.get("Rot").asInt(0));
                           }

                           var12.remove("SkullType");
                           var12.remove("facing");
                           var12.remove("Rot");
                           this.setBlock(var11, (Dynamic)ChunkPalettedStorageFix.SKULL_MAP.getOrDefault(var15, ChunkPalettedStorageFix.SKULL_MAP.get("0north")));
                        }
                     }
                  case 175:
                     IntListIterator var21 = ((IntList)var8.getValue()).iterator();

                     while(true) {
                        if(!var21.hasNext()) {
                           continue label170;
                        }

                        int var11 = ((Integer)var21.next()).intValue();
                        var11 = var11 | var9;
                        Dynamic<?> var12 = this.getBlock(var11);
                        if("upper".equals(ChunkPalettedStorageFix.getProperty(var12, "half"))) {
                           Dynamic<?> var13 = this.getBlock(relative(var11, ChunkPalettedStorageFix.Direction.DOWN));
                           String var14 = ChunkPalettedStorageFix.getName(var13);
                           if("minecraft:sunflower".equals(var14)) {
                              this.setBlock(var11, ChunkPalettedStorageFix.UPPER_SUNFLOWER);
                           } else if("minecraft:lilac".equals(var14)) {
                              this.setBlock(var11, ChunkPalettedStorageFix.UPPER_LILAC);
                           } else if("minecraft:tall_grass".equals(var14)) {
                              this.setBlock(var11, ChunkPalettedStorageFix.UPPER_TALL_GRASS);
                           } else if("minecraft:large_fern".equals(var14)) {
                              this.setBlock(var11, ChunkPalettedStorageFix.UPPER_LARGE_FERN);
                           } else if("minecraft:rose_bush".equals(var14)) {
                              this.setBlock(var11, ChunkPalettedStorageFix.UPPER_ROSE_BUSH);
                           } else if("minecraft:peony".equals(var14)) {
                              this.setBlock(var11, ChunkPalettedStorageFix.UPPER_PEONY);
                           }
                        }
                     }
                  case 176:
                  case 177:
                     IntListIterator var10 = ((IntList)var8.getValue()).iterator();

                     while(var10.hasNext()) {
                        int var11 = ((Integer)var10.next()).intValue();
                        var11 = var11 | var9;
                        Dynamic<?> var12 = this.getBlockEntity(var11);
                        Dynamic<?> var13 = this.getBlock(var11);
                        if(var12 != null) {
                           int var14 = var12.get("Base").asInt(0);
                           if(var14 != 15 && var14 >= 0 && var14 < 16) {
                              String var15 = ChunkPalettedStorageFix.getProperty(var13, ((Integer)var8.getKey()).intValue() == 176?"rotation":"facing") + "_" + var14;
                              if(ChunkPalettedStorageFix.BANNER_BLOCK_MAP.containsKey(var15)) {
                                 this.setBlock(var11, (Dynamic)ChunkPalettedStorageFix.BANNER_BLOCK_MAP.get(var15));
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

      }

      @Nullable
      private Dynamic getBlockEntity(int i) {
         return (Dynamic)this.blockEntities.get(i);
      }

      @Nullable
      private Dynamic removeBlockEntity(int i) {
         return (Dynamic)this.blockEntities.remove(i);
      }

      public static int relative(int var0, ChunkPalettedStorageFix.Direction chunkPalettedStorageFix$Direction) {
         switch(chunkPalettedStorageFix$Direction.getAxis()) {
         case X:
            int var2 = (var0 & 15) + chunkPalettedStorageFix$Direction.getAxisDirection().getStep();
            return var2 >= 0 && var2 <= 15?var0 & -16 | var2:-1;
         case Y:
            int var3 = (var0 >> 8) + chunkPalettedStorageFix$Direction.getAxisDirection().getStep();
            return var3 >= 0 && var3 <= 255?var0 & 255 | var3 << 8:-1;
         case Z:
            int var4 = (var0 >> 4 & 15) + chunkPalettedStorageFix$Direction.getAxisDirection().getStep();
            return var4 >= 0 && var4 <= 15?var0 & -241 | var4 << 4:-1;
         default:
            return -1;
         }
      }

      private void setBlock(int var1, Dynamic dynamic) {
         if(var1 >= 0 && var1 <= '\uffff') {
            ChunkPalettedStorageFix.Section var3 = this.getSection(var1);
            if(var3 != null) {
               var3.setBlock(var1 & 4095, dynamic);
            }
         }
      }

      @Nullable
      private ChunkPalettedStorageFix.Section getSection(int i) {
         int var2 = i >> 12;
         return var2 < this.sections.length?this.sections[var2]:null;
      }

      public Dynamic getBlock(int i) {
         if(i >= 0 && i <= '\uffff') {
            ChunkPalettedStorageFix.Section var2 = this.getSection(i);
            return var2 == null?ChunkPalettedStorageFix.AIR:var2.getBlock(i & 4095);
         } else {
            return ChunkPalettedStorageFix.AIR;
         }
      }

      public Dynamic write() {
         Dynamic<?> dynamic = this.level;
         if(this.blockEntities.isEmpty()) {
            dynamic = dynamic.remove("TileEntities");
         } else {
            dynamic = dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
         }

         Dynamic<?> var2 = dynamic.emptyMap();
         Dynamic<?> var3 = dynamic.emptyList();

         for(ChunkPalettedStorageFix.Section var7 : this.sections) {
            if(var7 != null) {
               var3 = var3.merge(var7.write());
               var2 = var2.set(String.valueOf(var7.y), var2.createIntList(Arrays.stream(var7.update.toIntArray())));
            }
         }

         Dynamic<?> var4 = dynamic.emptyMap();
         var4 = var4.set("Sides", var4.createByte((byte)this.sides));
         var4 = var4.set("Indices", var2);
         return dynamic.set("UpgradeData", var4).set("Sections", var3);
      }
   }
}
