package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class LootContext {
   private final Random random;
   private final float luck;
   private final ServerLevel level;
   private final LootTables lootTables;
   private final Set visitedTables;
   private final Map params;
   private final Map dynamicDrops;

   private LootContext(Random random, float luck, ServerLevel level, LootTables lootTables, Map var5, Map var6) {
      this.visitedTables = Sets.newLinkedHashSet();
      this.random = random;
      this.luck = luck;
      this.level = level;
      this.lootTables = lootTables;
      this.params = ImmutableMap.copyOf(var5);
      this.dynamicDrops = ImmutableMap.copyOf(var6);
   }

   public boolean hasParam(LootContextParam lootContextParam) {
      return this.params.containsKey(lootContextParam);
   }

   public void addDynamicDrops(ResourceLocation resourceLocation, Consumer consumer) {
      LootContext.DynamicDrop var3 = (LootContext.DynamicDrop)this.dynamicDrops.get(resourceLocation);
      if(var3 != null) {
         var3.add(this, consumer);
      }

   }

   @Nullable
   public Object getParamOrNull(LootContextParam lootContextParam) {
      return this.params.get(lootContextParam);
   }

   public boolean addVisitedTable(LootTable lootTable) {
      return this.visitedTables.add(lootTable);
   }

   public void removeVisitedTable(LootTable lootTable) {
      this.visitedTables.remove(lootTable);
   }

   public LootTables getLootTables() {
      return this.lootTables;
   }

   public Random getRandom() {
      return this.random;
   }

   public float getLuck() {
      return this.luck;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   public static class Builder {
      private final ServerLevel level;
      private final Map params = Maps.newIdentityHashMap();
      private final Map dynamicDrops = Maps.newHashMap();
      private Random random;
      private float luck;

      public Builder(ServerLevel level) {
         this.level = level;
      }

      public LootContext.Builder withRandom(Random random) {
         this.random = random;
         return this;
      }

      public LootContext.Builder withOptionalRandomSeed(long l) {
         if(l != 0L) {
            this.random = new Random(l);
         }

         return this;
      }

      public LootContext.Builder withOptionalRandomSeed(long var1, Random random) {
         if(var1 == 0L) {
            this.random = random;
         } else {
            this.random = new Random(var1);
         }

         return this;
      }

      public LootContext.Builder withLuck(float luck) {
         this.luck = luck;
         return this;
      }

      public LootContext.Builder withParameter(LootContextParam lootContextParam, Object object) {
         this.params.put(lootContextParam, object);
         return this;
      }

      public LootContext.Builder withOptionalParameter(LootContextParam lootContextParam, @Nullable Object object) {
         if(object == null) {
            this.params.remove(lootContextParam);
         } else {
            this.params.put(lootContextParam, object);
         }

         return this;
      }

      public LootContext.Builder withDynamicDrop(ResourceLocation resourceLocation, LootContext.DynamicDrop lootContext$DynamicDrop) {
         LootContext.DynamicDrop lootContext$DynamicDrop = (LootContext.DynamicDrop)this.dynamicDrops.put(resourceLocation, lootContext$DynamicDrop);
         if(lootContext$DynamicDrop != null) {
            throw new IllegalStateException("Duplicated dynamic drop \'" + this.dynamicDrops + "\'");
         } else {
            return this;
         }
      }

      public ServerLevel getLevel() {
         return this.level;
      }

      public Object getParameter(LootContextParam lootContextParam) {
         T object = this.params.get(lootContextParam);
         if(object == null) {
            throw new IllegalArgumentException("No parameter " + lootContextParam);
         } else {
            return object;
         }
      }

      @Nullable
      public Object getOptionalParameter(LootContextParam lootContextParam) {
         return this.params.get(lootContextParam);
      }

      public LootContext create(LootContextParamSet lootContextParamSet) {
         Set<LootContextParam<?>> var2 = Sets.difference(this.params.keySet(), lootContextParamSet.getAllowed());
         if(!var2.isEmpty()) {
            throw new IllegalArgumentException("Parameters not allowed in this parameter set: " + var2);
         } else {
            Set<LootContextParam<?>> var3 = Sets.difference(lootContextParamSet.getRequired(), this.params.keySet());
            if(!var3.isEmpty()) {
               throw new IllegalArgumentException("Missing required parameters: " + var3);
            } else {
               Random var4 = this.random;
               if(var4 == null) {
                  var4 = new Random();
               }

               return new LootContext(var4, this.luck, this.level, this.level.getServer().getLootTables(), this.params, this.dynamicDrops);
            }
         }
      }
   }

   @FunctionalInterface
   public interface DynamicDrop {
      void add(LootContext var1, Consumer var2);
   }

   public static enum EntityTarget {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      DIRECT_KILLER("direct_killer", LootContextParams.DIRECT_KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER);

      private final String name;
      private final LootContextParam param;

      private EntityTarget(String name, LootContextParam param) {
         this.name = name;
         this.param = param;
      }

      public LootContextParam getParam() {
         return this.param;
      }

      public static LootContext.EntityTarget getByName(String name) {
         for(LootContext.EntityTarget var4 : values()) {
            if(var4.name.equals(name)) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Invalid entity target " + name);
      }

      public static class Serializer extends TypeAdapter {
         public void write(JsonWriter jsonWriter, LootContext.EntityTarget lootContext$EntityTarget) throws IOException {
            jsonWriter.value(lootContext$EntityTarget.name);
         }

         public LootContext.EntityTarget read(JsonReader jsonReader) throws IOException {
            return LootContext.EntityTarget.getByName(jsonReader.nextString());
         }

         // $FF: synthetic method
         public Object read(JsonReader var1) throws IOException {
            return this.read(var1);
         }

         // $FF: synthetic method
         public void write(JsonWriter var1, Object var2) throws IOException {
            this.write(var1, (LootContext.EntityTarget)var2);
         }
      }
   }
}
