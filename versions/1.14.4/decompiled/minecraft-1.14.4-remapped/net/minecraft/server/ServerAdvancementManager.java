package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAdvancementManager extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Advancement.Builder.class, (jsonElement, type, jsonDeserializationContext) -> {
      JsonObject var3 = GsonHelper.convertToJsonObject(jsonElement, "advancement");
      return Advancement.Builder.fromJson(var3, jsonDeserializationContext);
   }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter(Component.class, new Component.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory()).create();
   private AdvancementList advancements = new AdvancementList();

   public ServerAdvancementManager() {
      super(GSON, "advancements");
   }

   protected void apply(Map map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
      Map<ResourceLocation, Advancement.Builder> map = Maps.newHashMap();
      map.forEach((resourceLocation, jsonObject) -> {
         try {
            Advancement.Builder var3 = (Advancement.Builder)GSON.fromJson(jsonObject, Advancement.Builder.class);
            map.put(resourceLocation, var3);
         } catch (IllegalArgumentException | JsonParseException var4) {
            LOGGER.error("Parsing error loading custom advancement {}: {}", resourceLocation, var4.getMessage());
         }

      });
      AdvancementList var5 = new AdvancementList();
      var5.add(map);

      for(Advancement var7 : var5.getRoots()) {
         if(var7.getDisplay() != null) {
            TreeNodePosition.run(var7);
         }
      }

      this.advancements = var5;
   }

   @Nullable
   public Advancement getAdvancement(ResourceLocation resourceLocation) {
      return this.advancements.get(resourceLocation);
   }

   public Collection getAllAdvancements() {
      return this.advancements.getAllAdvancements();
   }
}
